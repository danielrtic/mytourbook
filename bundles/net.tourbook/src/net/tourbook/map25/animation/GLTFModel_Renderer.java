/*******************************************************************************
 * Copyright (C) 2022 Wolfgang Schramm and Contributors
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110, USA
 *******************************************************************************/
package net.tourbook.map25.animation;

import static org.oscim.backend.GLAdapter.gl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Animation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;

import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.attributes.PBRCubemapAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;
import net.mgsx.gltf.scene3d.lights.DirectionalLightEx;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import net.mgsx.gltf.scene3d.scene.SceneSkybox;
import net.mgsx.gltf.scene3d.utils.IBLBuilder;
import net.tourbook.map.player.MapPlayerData;
import net.tourbook.map.player.MapPlayerManager;

import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;
import org.oscim.backend.GL;
import org.oscim.core.GeoPoint;
import org.oscim.core.MapPosition;
import org.oscim.core.MercatorProjection;
import org.oscim.core.Tile;
import org.oscim.event.Event;
import org.oscim.map.Map;
import org.oscim.renderer.GLState;
import org.oscim.renderer.GLViewport;
import org.oscim.renderer.LayerRenderer;
import org.oscim.utils.geom.GeometryUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Renderer/shader for the glTF model
 * <p>
 * Original source {@link org.oscim.gdx.poi3d.GdxRenderer3D2}
 */
public class GLTFModel_Renderer extends LayerRenderer {

   static final Logger        log         = LoggerFactory.getLogger(GLTFModel_Renderer.class);

   private Map                _map;
   private MapCameraMT        _mapCamera;

   private Vector3            _tempVector = new Vector3();

   private MapPosition        _currentMapPosition;
   float[]                    _mapBox     = new float[8];

   private Scene              _scene;

   private SceneAsset         _sceneAsset;
   private SceneManager       _sceneManager;
   private DirectionalLightEx _light;

   private Cubemap            _environmentCubemap;
   private Cubemap            _diffuseCubemap;
   private Cubemap            _specularCubemap;
   private Texture            _brdfLUT;
   private SceneSkybox        _skybox;
   private BoundingBox        _modelBoundingBox;

   private Vector3            _boundingBoxCenter;
   private float              _boundingBoxMinMaxDistance;

   /**
    * Angle that the model is looking forward
    */
   private float              _modelForwardAngle;

   /**
    * The model length needs a factor that the top of the symbol is not before the geo location
    */
   private double             _modelCenterToForwardFactor;

   public GLTFModel_Renderer(final Map map) {

      _map = map;
   }

   private void activateFirstAnimation() {

      final Array<Animation> animations = _sceneAsset.scene.model.animations;

      if (animations != null && animations.size > 0) {

         final String animationID = animations.get(0).id;

         _scene.animationController.setAnimation(animationID, -1);

         System.out.println("Number of animations: " + animations.size + "  using " + animationID);
      }
   }

   public void dispose() {

      if (_sceneManager == null) {
         return;
      }

      _sceneManager.dispose();
      _sceneAsset.dispose();

      _environmentCubemap.dispose();
      _diffuseCubemap.dispose();
      _specularCubemap.dispose();

      _brdfLUT.dispose();
//      skybox.dispose();
   }

   private SceneAsset loadGLTFModel() {

      SceneAsset asset = null;

      /*
       * MT models
       */

      // skateboard
      asset = new GLTFLoader().load(Gdx.files.absolute("C:/DAT/glTF/MT/skateboard/mt-skateboard.gltf"));
      _modelForwardAngle = 90;
      _modelCenterToForwardFactor = 1.4;

      // drawn bicycle
//      asset = new GLTFLoader().load(Gdx.files.absolute("C:/DAT/glTF/MT/simple-bicycle/simple-bicycle.gltf"));
//      _modelForwardAngle = 90;
//      _modelCenterToForwardFactor = 5;

      // hochrad
//      asset = new GLTFLoader().load(Gdx.files.absolute("C:/DAT/glTF/MT/hochrad/hochrad.gltf"));
//      _modelForwardAngle = -90;
//      _modelCenterToForwardFactor = -7;

      // wood truck
//      asset = new GLTFLoader().load(Gdx.files.absolute("C:/DAT/glTF/MT/wood-truck/wood-truck.gltf"));
//      _modelForwardAngle = -90;
//      _modelCenterToForwardFactor = -7;

      // wood plane
//      asset = new GLTFLoader().load(Gdx.files.absolute("C:/DAT/glTF/MT/wood-plane/wood-plane.gltf"));

      /*
       * sketchfab.com models
       */

      // simple bicycle with globe
//      asset = new GLTFLoader().load(Gdx.files.absolute("C:/DAT/glTF/sketchfab.com/2d_bike__downloadable_for_first_10_users/scene.gltf"));
//      _modelForwardAngle = 90;
//      _modelZAdjustment = MODEL_Z_ADJUSTMENT.BoundingBox_Min_Negative;

      // Hochrad - light reflection
//      asset = new GLTFLoader().load(Gdx.files.absolute("C:/DAT/glTF/sketchfab.com/pennyfarthest_bicycle/scene.gltf"));
//      _modelForwardAngle = -90;
//      _modelCenterToForwardFactor = 1;
//      _modelZAdjustment = MODEL_Z_ADJUSTMENT.BoundingBox_Min_Negative;

      // gears - light reflection
//      asset = new GLTFLoader().load(Gdx.files.absolute("C:/DAT/glTF/sketchfab.com/gears/scene.gltf"));
//      _modelForwardAngle = 90;
//      _modelZAdjustment = MODEL_Z_ADJUSTMENT.BoundingBox_Min_Negative;

      // walking roboter
//      asset = new GLTFLoader().load(Gdx.files.absolute("C:/DAT/glTF/sketchfab.com/robot-_walk_animation/scene.gltf"));

      // skateboard
//      asset = new GLTFLoader().load(Gdx.files.absolute("C:/DAT/glTF/sketchfab.com/skateboard_animated_-_blockbench/scene.gltf"));
//      _modelForwardAngle = 90;

      // zeppelin
//      asset = new GLTFLoader().load(Gdx.files.absolute("C:/DAT/glTF/sketchfab.com/zeppelin_aircraft/scene.gltf"));
//      _modelForwardAngle = 180;

      // Locomotive frame
//      asset = new GLTFLoader().load(Gdx.files.absolute("C:/DAT/glTF/sketchfab.com/locomotive/scene.gltf"));
//      _modelForwardAngle = 180;
//      _modelZAdjustment = MODEL_Z_ADJUSTMENT.BoundingBox_Min_Negative;
//      _modelCenterToForwardFactor = 5;

      // Alter Anhänger
//      asset = new GLTFLoader().load(Gdx.files.absolute("C:/DAT/glTF/sketchfab.com/medieval_cart/scene.gltf"));
//      _modelForwardAngle = 180;

      // z-fighting underneath
//      asset = new GLTFLoader().load(Gdx.files.absolute("C:/DAT/glTF/sketchfab.com/lawn_mower_low_poly/scene.gltf"));

      // has z-fighting
//      asset = new GLTFLoader().load(Gdx.files.absolute("C:/DAT/glTF/sketchfab.com/toy_truck/scene.gltf"));

      // Historic baloon with model issue
//      asset = new GLTFLoader().load(Gdx.files.absolute("C:/DAT/glTF/sketchfab.com/steampunk_air_baloon/scene.gltf"));

      // wood truck with position issue
//      asset = new GLTFLoader().load(Gdx.files.absolute("C:/DAT/glTF/sketchfab.com/basic_truck/scene.gltf"));

      // wood plane - light reflection
//      asset = new GLTFLoader().load(Gdx.files.absolute("C:/DAT/glTF/sketchfab.com/basic_plane/scene.gltf"));
//      _modelZAdjustment = MODEL_Z_ADJUSTMENT.BoundingBox_Min_Negative;

//
//    WITH EXCEPTION ISSUES
//

      // Array index out of range: 8
//      asset = new GLTFLoader().load(Gdx.files.absolute("C:/DAT/glTF/sketchfab.com/drunk_monster_truck/scene.gltf"));

      // too many bones: 64, max configured: 24
//      asset = new GLTFLoader().load(Gdx.files.absolute("C:/DAT/glTF/sketchfab.com/jo_on_bike__rigged__animated/scene.gltf"));

      return asset;
   }

   /**
    * Set current map position
    *
    * @param mapEvent
    * @param mapPosition
    */
   void onMapEvent(final Event mapEvent, final MapPosition mapPosition) {

      if (_mapCamera == null) {
         return;
      }

      _currentMapPosition = mapPosition;

      final int mapZoomLevel = mapPosition.zoomLevel;
      final int mapScale = 1 << mapZoomLevel;

      _mapCamera.setMapPosition(mapPosition.x, mapPosition.y, mapScale);
   }

   @Override
   public void render(final GLViewport viewport) {

      if (_scene == null || _currentMapPosition == null) {
         return;
      }

//      final long start = System.nanoTime();

//    // remove if out of visible zoom range
//    _gltfRenderer.allModelInstances.removeAll(_allModelInstances, true);
//    if (mapZoomLevel >= 3 /* MIN_ZOOM */) {
//       _gltfRenderer.allModelInstances.addAll(_allModelInstances);
//    }

      if (MapPlayerManager.isCompileMapScaleModified()) {

         _mapCamera.setMapPosition(

               _currentMapPosition.x,
               _currentMapPosition.y,
               MapPlayerManager.getCompileMapScale());
      }

      _mapCamera.update(viewport);

      updateModelPosition();

      gl.depthMask(true);

//      if (viewportPosition.zoomLevel < 10) {
//         gl.clear(GL.DEPTH_BUFFER_BIT);
//      }

      // Unbind via GLState to ensure no buffer is replaced by accident
      GLState.bindElementBuffer(GLState.UNBIND);
      GLState.bindBuffer(GL.ARRAY_BUFFER, GLState.UNBIND);

      // set state that is expected after modelBatch.end();
      // modelBatch keeps track of its own state
      GLState.enableVertexArrays(GLState.DISABLED, GLState.DISABLED);
      GLState.bindTex2D(GLState.DISABLED);
      GLState.useProgram(GLState.DISABLED);
      GLState.test(false, false);
      GLState.blend(false);

      // gl.cullFace(GL.BACK);
      // flip front face cause of mirror inverted y-axis
      gl.frontFace(GL.CCW);

      final int numModels = 0;
      int numRendered = 0;

      final MapPosition cameraMapPosition = _mapCamera.mMapPosition;
      final MapPosition viewportPosition = viewport.pos;

      _map.viewport().getMapExtents(_mapBox, 10);

      final float dx = (float) (cameraMapPosition.x - viewportPosition.x) * (Tile.SIZE << cameraMapPosition.zoomLevel);
      final float dy = (float) (cameraMapPosition.y - viewportPosition.y) * (Tile.SIZE << cameraMapPosition.zoomLevel);
      final float scale = (float) (cameraMapPosition.scale / viewportPosition.scale);

      for (int i = 0; i < 8; i += 2) {

         _mapBox[i] *= scale;
         _mapBox[i] -= dx;
         _mapBox[i + 1] *= scale;
         _mapBox[i + 1] -= dy;
      }

      synchronized (this) {

//         for (final ModelInstance modelInstance : allModelInstances) {

         final ModelInstance modelInstance = _scene.modelInstance;

         modelInstance.transform.getTranslation(_tempVector);

         _tempVector.scl(0.9f, 0.9f, 1);

         final boolean isInMapBox = GeometryUtils.pointInPoly(_tempVector.x, _tempVector.y, _mapBox, 8, 0);

//       if (isInMapBox == false) {
//          continue;
//       }

         numRendered++;
//         }

         if (numRendered > 0) {

            final float deltaTime = Gdx.graphics.getDeltaTime();

            // render
            _sceneManager.update(deltaTime);
            _sceneManager.render();
         }
      }

      // GLUtils.checkGlError("<" + TAG);

      gl.frontFace(GL.CW);
      gl.depthMask(false);
      GLState.bindElementBuffer(GLState.UNBIND);
      GLState.bindBuffer(GL.ARRAY_BUFFER, GLState.UNBIND);

      // GLState.bindTex2D(-1);
      // GLState.useProgram(-1);

//      final long end = System.nanoTime();
//      final float timeDiff = (float) (end - start) / 1000000;
//      System.out.println(String.format("%6.4f ms   %d / %d", timeDiff, numModels, numRendered));
//      // TODO remove SYSTEM.OUT.PRINTLN
   }

   @Override
   public boolean setup() {

      _sceneAsset = loadGLTFModel();

      _scene = new Scene(_sceneAsset.scene);

      // get bounding box
      _modelBoundingBox = new BoundingBox();
      _scene.modelInstance.calculateBoundingBox(_modelBoundingBox);
      _boundingBoxMinMaxDistance = _modelBoundingBox.max.dst(_modelBoundingBox.min);
      _boundingBoxCenter = _modelBoundingBox.getCenter(new Vector3());

      _sceneManager = new SceneManager();
      _sceneManager.addScene(_scene);

      _mapCamera = new MapCameraMT(_map);
      _sceneManager.setCamera(_mapCamera);

      // setup light
      _light = new DirectionalLightEx();
      _light.direction.set(1, -3, 1).nor();
      _light.color.set(Color.WHITE);
      _sceneManager.environment.add(_light);

      // setup quick IBL (image based lighting)
      final IBLBuilder iblBuilder = IBLBuilder.createOutdoor(_light);
      _environmentCubemap = iblBuilder.buildEnvMap(1024);
      _diffuseCubemap = iblBuilder.buildIrradianceMap(256);
      _specularCubemap = iblBuilder.buildRadianceMap(10);
      iblBuilder.dispose();

      // This texture is provided by the library, no need to have it in your assets.
      _brdfLUT = new Texture(Gdx.files.classpath("net/mgsx/gltf/shaders/brdfLUT.png"));

      _sceneManager.setAmbientLight(1f);
      _sceneManager.environment.set(new PBRTextureAttribute(PBRTextureAttribute.BRDFLUTTexture, _brdfLUT));
      _sceneManager.environment.set(PBRCubemapAttribute.createSpecularEnv(_specularCubemap));
      _sceneManager.environment.set(PBRCubemapAttribute.createDiffuseEnv(_diffuseCubemap));

      // setup skybox
//      skybox = new SceneSkybox(environmentCubemap);
//      sceneManager.setSkyBox(skybox);

      activateFirstAnimation();

      return true;
   }

   @Override
   public synchronized void update(final GLViewport viewport) {

      setReady(true);

      if (isReady() == false) {

         _mapCamera.setPosition(viewport.pos);

         setReady(true);
      }

   }

   /**
    * Update model position, partly copied from {@link org.oscim.gdx.poi3d.GdxModelLayer}
    */
   private void updateModelPosition() {

      final MapPlayerData mapPlayerData = MapPlayerManager.getMapPlayerData();
      if (mapPlayerData == null) {
         return;
      }

      final int currentFrameNumber = MapPlayerManager.getCurrentFrameNumber();
      final GeoPoint[] animatedGeoPoints = mapPlayerData.allAvailableGeoPoints;
      final IntArrayList animatedLocationIndices = mapPlayerData.animatedLocationIndices;

      if (currentFrameNumber >= animatedLocationIndices.size() - 1) {
         return;
      }

      final int geoLocationIndex = animatedLocationIndices.get(currentFrameNumber - 1);
      final GeoPoint geoLocation = animatedGeoPoints[geoLocationIndex];

      // lat/lon -> 0...1
      final double modelProjectedPositionX = MercatorProjection.longitudeToX(geoLocation.getLongitude());
      final double modelProjectedPositionY = MercatorProjection.latitudeToY(geoLocation.getLatitude());

      final ModelInstance modelInstance = _scene.modelInstance;
      final Matrix4 modelTransform = modelInstance.transform;

      final double currentMapScale = _currentMapPosition.scale;
      final int currentMapZoomLevel = _currentMapPosition.zoomLevel;

      final int mapScale = 1 << currentMapZoomLevel;
      final int tileScale = Tile.SIZE << currentMapZoomLevel;

      final double latitude = MercatorProjection.toLatitude(_currentMapPosition.y);
      final float groundScale = (float) MercatorProjection.groundResolutionWithScale(latitude, mapScale);

      float modelScale;

      final boolean isFixedSize = true;

      if (isFixedSize) {

         final int modelSize = 300;

         // viewport scale 2 map scale: 1...2
         final float vp2mp = (float) (currentMapScale / MapPlayerManager.getCompileMapScale());

         final float vp2mpModelSize = modelSize / vp2mp;

         /*
          * This algorithm is not perfect as the model can still be flickering (size is larger or
          * smaller) but it is better than nothing. I spend 2 full days to get the flickering partly
          * fixed
          */
         if (vp2mp < 1.0) {

            modelScale = vp2mpModelSize / 2;

         } else if (vp2mp > 2.0) {

            modelScale = vp2mpModelSize * 2;

         } else {

            modelScale = vp2mpModelSize;
         }

         modelScale /= _boundingBoxMinMaxDistance;

      } else {

         modelScale = 1f / groundScale;

         /*
          * Adjust to a normalized size which depends on the model size because the models can have
          * big size differences
          */
         modelScale /= _boundingBoxMinMaxDistance;

         // increase model size to be more visible
         modelScale *= 100;
      }

      /*
       * Translate glTF model to the map position
       */
      final float dx = (float) ((modelProjectedPositionX - _currentMapPosition.x) * tileScale);
      final float dy = (float) ((modelProjectedPositionY - _currentMapPosition.y) * tileScale);

      final Vector3 bbMin = _modelBoundingBox.min;
      final Vector3 bbMax = _modelBoundingBox.max;
      final Vector3 bboxCenter = _boundingBoxCenter;

      final float zAdjustment = 0;

//      _modelCenterToForwardFactor = 0;

      /**
       * Adjust the center of the symbol to be at the top of the symbol, needs some maths with
       * sinus/cosinus.
       * <p>
       * It took me many hours to get this math fixed because this matrix cannot do it as in the
       * shader code, e.g.
       * - rotate<br>
       * - scale<br>
       * - translate head to center<br>
       * - translate symbol to geo location<br>
       */
      float animationAngle = -MapPlayerManager.getAnimatedAngle();
      animationAngle += _modelForwardAngle;

      final double halfSize = modelScale / 2;
      final double center2BorderSize = halfSize * _modelCenterToForwardFactor;
      final float forwardX = (float) (center2BorderSize * MathUtils.cosDeg(animationAngle));
      final float forwardY = (float) (center2BorderSize * MathUtils.sinDeg(animationAngle));

      // reset matrix to identity matrix
      modelTransform.idt();

      modelTransform.scale(modelScale, modelScale, modelScale);

      modelTransform.rotate(1, 0, 0, 90);
      modelTransform.rotate(0, 1, 0, animationAngle);

      // move to the track position
      modelTransform.trn(
            dx + forwardX,
            dy + forwardY,
            zAdjustment);
   }
}

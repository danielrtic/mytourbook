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
package net.tourbook.map25.renderer;

import static org.oscim.backend.GLAdapter.gl;
import static org.oscim.renderer.MapRenderer.COORD_SCALE;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import net.tourbook.common.UI;
import net.tourbook.map25.Map25ConfigManager;
import net.tourbook.map25.layer.tourtrack.Map25TrackConfig;

import org.eclipse.collections.impl.list.mutable.primitive.ByteArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.ShortArrayList;
import org.oscim.backend.GL;
import org.oscim.core.MapPosition;
import org.oscim.renderer.GLMatrix;
import org.oscim.renderer.GLState;
import org.oscim.renderer.GLUtils;
import org.oscim.renderer.GLViewport;
import org.oscim.renderer.MapRenderer;
import org.oscim.theme.styles.LineStyle;

/**
 * Original code: org.oscim.renderer.bucket.LineBucket.Renderer
 */
public final class TourTrack_Shader {

   private static final char  NL                       = UI.NEW_LINE;

   /**
    * Factor to normalize extrusion vector and scale to coord scale
    */
   private static final float COORD_SCALE_BY_DIR_SCALE = COORD_SCALE / TourTrack_Bucket.DIR_SCALE;

   private static final int   CAP_THIN                 = 0;
   private static final int   CAP_BUTT                 = 1;
   private static final int   CAP_ROUND                = 2;

   private static final int   SHADER_PROJECTED         = 0;
   private static final int   SHADER_FLAT              = 1;

   /**
    * Number of bytes for a <b>short</b> value
    */
   private static final int   SHORT_BYTES              = 2;
// private static final int             FLOAT_BYTES              = 4;

   private static AnimationShader       _animationShader;
   private static DirectionArrowsShader _directionArrowShader;
   private static LineShader[]          _lineShaders         = { null, null };

   private static int                   bufferId_AnimationVertices;
   private static int                   bufferId_TrackVertices;
   private static int                   bufferId_TrackVertices_Colors;
   private static int                   bufferId_DirArrows;
   private static int                   bufferId_DirArrows_ColorCoords;

   static long                          animation_StartTime;
   private static int                   _animation_CurrentPositionIndex;
   private static long                  _animation_LastUpdateTime;

   private static ByteBuffer            _vertexColor_Buffer;
   private static int                   _vertexColor_BufferSize;

   private static short[]               _animationVertices;
   private static GLMatrix              _animationMVP        = new GLMatrix();
   private static GLMatrix              _animationTempMatrix = new GLMatrix();

   private static class AnimationShader extends GLShaderMT {

      /**
       * Location for a shader variable
       */
      int shader_attrib_Pos;
      int shader_uni_MVP;
      int shader_uni_Vp2MpScale;

      AnimationShader(final String shaderFile) {

         if (loadShader(shaderFile, "#version 330" + NL) == false) { //$NON-NLS-1$
            return;
         }

         // SET_FORMATTING_OFF

         shader_attrib_Pos             = getAttrib ("attrib_Pos");         //$NON-NLS-1$
         shader_uni_MVP                = getUniform("uni_MVP");            //$NON-NLS-1$
         shader_uni_Vp2MpScale         = getUniform("uni_Vp2MpScale");     //$NON-NLS-1$

         // SET_FORMATTING_ON
      }
   }

   private static class DirectionArrowsShader extends GLShaderMT {

      /**
       * Location for a shader variable
       */
      int shader_a_pos,
            shader_attrib_ColorCoord,

            shader_u_mvp,

            shader_uni_ArrowColors,
            shader_uni_OutlineWidth,
            shader_uni_Vp2MpScale

      ;

      DirectionArrowsShader(final String shaderFile) {

         if (loadShader(shaderFile, "#version 330" + NL) == false) { //$NON-NLS-1$
            return;
         }

   // SET_FORMATTING_OFF

         shader_a_pos                  = getAttrib("a_pos");                  //$NON-NLS-1$
         shader_u_mvp                  = getUniform("u_mvp");                 //$NON-NLS-1$
         shader_attrib_ColorCoord      = getAttrib("attrib_ColorCoord");      //$NON-NLS-1$

         shader_uni_ArrowColors        = getUniform("uni_ArrowColors");       //$NON-NLS-1$
         shader_uni_OutlineWidth       = getUniform("uni_OutlineWidth");      //$NON-NLS-1$
         shader_uni_Vp2MpScale         = getUniform("uni_Vp2MpScale");        //$NON-NLS-1$

   // SET_FORMATTING_ON
      }
   }

   private static class LineShader extends GLShaderMT {

      int shader_a_pos,
            shader_aVertexColor,

            shader_u_mvp,

            shader_u_fade,
            shader_u_color,
            shader_u_mode,

            shader_u_width,
            shader_u_height,

            shader_uColorMode,
            shader_uOutlineBrightness,
            shader_uVertexColorAlpha

      ;

      LineShader(final String shaderFile) {

         if (loadShader(shaderFile, null) == false) {
            return;
         }

   // SET_FORMATTING_OFF

            shader_a_pos               = getAttrib("a_pos"); //$NON-NLS-1$
            shader_aVertexColor        = getAttrib("aVertexColor"); //$NON-NLS-1$

            shader_u_mvp               = getUniform("u_mvp"); //$NON-NLS-1$

            shader_u_fade              = getUniform("u_fade"); //$NON-NLS-1$
            shader_u_color             = getUniform("u_color"); //$NON-NLS-1$
            shader_u_mode              = getUniform("u_mode"); //$NON-NLS-1$

            shader_u_width             = getUniform("u_width"); //$NON-NLS-1$
            shader_u_height            = getUniform("u_height"); //$NON-NLS-1$

            shader_uColorMode          = getUniform("uColorMode"); //$NON-NLS-1$
            shader_uOutlineBrightness  = getUniform("uOutlineBrightness"); //$NON-NLS-1$
            shader_uVertexColorAlpha   = getUniform("uVertexColorAlpha"); //$NON-NLS-1$

   // SET_FORMATTING_ON
      }
   }

   /**
    * Fill OpenGL buffer with the vertices/color/direction arrow data
    *
    * @return Returns <code>true</code> when data are available
    */
   public static boolean bindBufferData(final TourTrack_Bucket painterBucket) {

      final int numTrackVertices = painterBucket == null
            ? 0
            : painterBucket.numTrackVertices * 4;

      if (numTrackVertices <= 0) {
         return false;
      }

      final Map25TrackConfig trackConfig = Map25ConfigManager.getActiveTourTrackConfig();

      /*
       * Track
       */
      {
         /*
          * Vertices
          */
         final ShortArrayList trackVertices = painterBucket.trackVertexData.tourTrack_Vertices;
         final ShortBuffer buffer1 = MapRenderer.getShortBuffer(numTrackVertices).put(trackVertices.toArray()).flip();
         gl.bindBuffer(GL.ARRAY_BUFFER, bufferId_TrackVertices);
         gl.bufferData(GL.ARRAY_BUFFER, numTrackVertices * SHORT_BYTES, buffer1, GL.STATIC_DRAW);

         /*
          * Color
          */
         final ByteArrayList trackColors = painterBucket.trackVertexData.tourTrack_Colors;
         final ByteBuffer buffer2 = getBuffer_Color(numTrackVertices).put(trackColors.toArray()).flip();
         gl.bindBuffer(GL.ARRAY_BUFFER, bufferId_TrackVertices_Colors);
         gl.bufferData(GL.ARRAY_BUFFER, numTrackVertices, buffer2, GL.STATIC_DRAW);
      }

      if (trackConfig.isShowDirectionArrow) {

         if (trackConfig.arrow_IsAnimate) {

            /*
             * Animation
             */
            {
               /*
                * Vertices
                */

// SET_FORMATTING_OFF

               final short size = 100;

               _animationVertices = new short[] {
                                                   0,     0,  10,
                                                   0,  size,  10,
                                                size,  size,   5,

                                                };
// SET_FORMATTING_ON

               final int numAnimationVertices = _animationVertices.length;
               final ShortBuffer buffer1 = MapRenderer.getShortBuffer(numAnimationVertices).put(_animationVertices).flip();
               gl.bindBuffer(GL.ARRAY_BUFFER, bufferId_AnimationVertices);
               gl.bufferData(GL.ARRAY_BUFFER, numAnimationVertices * SHORT_BYTES, buffer1, GL.STATIC_DRAW);
            }

         } else {

            /*
             * Static direction arrows
             */
            {
               /*
                * Vertices
                */
               final ShortArrayList dirArrowVertices = painterBucket.directionArrow_Vertices;
               final int numVertices = dirArrowVertices.size();
               final ShortBuffer buffer1 = MapRenderer.getShortBuffer(numVertices).put(dirArrowVertices.toArray()).flip();
               gl.bindBuffer(GL.ARRAY_BUFFER, bufferId_DirArrows);
               gl.bufferData(GL.ARRAY_BUFFER, numVertices * SHORT_BYTES, buffer1, GL.STATIC_DRAW);

               /*
                * Color
                */
               final ShortArrayList colorCoords = painterBucket.directionArrow_ColorCoords;
               final int numColorCoords = colorCoords.size();
               final ShortBuffer buffer2 = MapRenderer.getShortBuffer(numColorCoords).put(colorCoords.toArray()).flip();
               gl.bindBuffer(GL.ARRAY_BUFFER, bufferId_DirArrows_ColorCoords);
               gl.bufferData(GL.ARRAY_BUFFER, numColorCoords * SHORT_BYTES, buffer2, GL.STATIC_DRAW);
            }
         }
      }

      return true;
   }

   private static int getAnimatedPositionIndex(final ShortArrayList animatedPositions) {

      final long currentTimeMS = System.currentTimeMillis();

      final Map25TrackConfig trackConfig = Map25ConfigManager.getActiveTourTrackConfig();

      // how many arrows are moved in one second
      final int framesPerSecond = trackConfig.arrow_ArrowsPerSecond;

      // update sequence in one second
      final float frameDurationSec = 1f / framesPerSecond;
      final long frameDurationMS = (long) (frameDurationSec * 1000);

      // ensure there not more frames per second are displayed
      final long nextUpdateTimeMS = _animation_LastUpdateTime + frameDurationMS;
      final long timeDiffMS = nextUpdateTimeMS - currentTimeMS;
      if (timeDiffMS > 0) {
         return _animation_CurrentPositionIndex;
      }

      // number of position items: x,y -> 2
      final int numPosSliceItems = 2;
      final int numPositions = animatedPositions.size();

      // the last frame is not displayed -> -2
      final int maxFrames = Math.max(0, numPositions / numPosSliceItems - numPosSliceItems);
      final float maxLoopTime = maxFrames / framesPerSecond;

      final float timeDiffSinceFirstRun = (float) ((currentTimeMS - animation_StartTime) / 1000.0);
      final float currentTimeIndex = timeDiffSinceFirstRun % maxLoopTime;

      int positionIndex = Math.round(currentTimeIndex * framesPerSecond) * numPosSliceItems;

      // ensure to not jump back to the start when the end is not yet reached
      if (positionIndex <= numPositions - numPosSliceItems) {
         positionIndex = _animation_CurrentPositionIndex + numPosSliceItems;
      }

      // ensure to move not more than one frame
      if (positionIndex > _animation_CurrentPositionIndex + numPosSliceItems) {
         positionIndex = _animation_CurrentPositionIndex + numPosSliceItems;
      }

      // ensure bounds
      if (positionIndex >= numPositions - numPosSliceItems) {
         positionIndex = 0;
      }

      _animation_CurrentPositionIndex = positionIndex;
      _animation_LastUpdateTime = currentTimeMS;

      return positionIndex;
   }

   private static ByteBuffer getBuffer_Color(final int requestedColorSize) {

      final int bufferBlockSize = 2048;
      final int numBufferBlocks = requestedColorSize / bufferBlockSize;
      final int roundedBufferSize = (numBufferBlocks + 1) * bufferBlockSize;

      if (_vertexColor_Buffer == null || _vertexColor_BufferSize < roundedBufferSize) {

         _vertexColor_Buffer = ByteBuffer
               .allocateDirect(roundedBufferSize)
               .order(ByteOrder.nativeOrder());

         _vertexColor_BufferSize = roundedBufferSize;

      } else {

         // IMPORTANT: reset position to 0 to prevent BufferOverflowException

         _vertexColor_Buffer.clear();
      }

      return _vertexColor_Buffer;
   }

   /**
    * Performs OpenGL drawing commands of the renderBucket(s)
    *
    * @param trackBucket
    * @param viewport
    * @param vp2mpScale
    *           Viewport scale 2 map scale: it is between 1...2
    * @param bucketManager
    * @return
    */
   public static void paint(final TourTrack_Bucket trackBucket,
                            final GLViewport viewport,
                            final float vp2mpScale) {

//    _dirArrowFrameBuffer.updateViewport(viewport, 0.5f);

      final Map25TrackConfig trackConfig = Map25ConfigManager.getActiveTourTrackConfig();

      // fix alpha blending
      gl.blendFunc(GL.SRC_ALPHA, GL.ONE_MINUS_SRC_ALPHA);
      {
         paint_10_Track(trackBucket, viewport, vp2mpScale);

         if (trackConfig.isShowDirectionArrow) {

            if (trackConfig.arrow_IsAnimate) {

               paint_30_Animation(viewport, vp2mpScale, trackBucket);

            } else {

               paint_20_DirectionArrows(viewport, vp2mpScale, trackBucket);
            }
         }
      }
      gl.blendFunc(GL.ONE, GL.ONE_MINUS_SRC_ALPHA); // reset to map default
   }

   /**
    * Performs OpenGL drawing commands of the renderBucket(s)
    *
    * @param inoutRenderBucket
    *           In/out render bucked
    * @param viewport
    * @param vp2mpScale
    *           Viewport scale 2 map scale: it is between 1...2
    * @param renderBucketsAll
    * @return
    */
   private static void paint_10_Track(final TourTrack_Bucket trackBucket,
                                      final GLViewport viewport,
                                      final float vp2mpScale) {

      final Map25TrackConfig trackConfig = Map25ConfigManager.getActiveTourTrackConfig();

      final MapPosition mapPosition = viewport.pos;

      /*
       * Simple line shader does not take forward shortening into
       * account. only used when tilt is 0.
       */
      final int shaderMode = mapPosition.tilt < 1

            // 1 == not projected
            ? SHADER_FLAT

            // 0 == projected
            : SHADER_PROJECTED;

//       shaderMode = shaderMode;
//       shaderMode = SHADER_FLAT;

      final LineShader shader = _lineShaders[shaderMode];

      // is calling GL.enableVertexAttribArray() for shader_a_pos
      shader.useProgram();

      GLState.blend(true);

      final int shader_a_pos = shader.shader_a_pos;
      final int shader_aVertexColor = shader.shader_aVertexColor;

      final int shader_u_fade = shader.shader_u_fade;
      final int shader_u_mode = shader.shader_u_mode;
      final int shader_u_color = shader.shader_u_color;
      final int shader_u_width = shader.shader_u_width;
      final int shader_u_height = shader.shader_u_height;

      final int shader_uColorMode = shader.shader_uColorMode;
      final int shader_uOutlineBrightness = shader.shader_uOutlineBrightness;
      final int shader_uVertexColorAlpha = shader.shader_uVertexColorAlpha;

      gl.bindBuffer(GL.ARRAY_BUFFER, bufferId_TrackVertices);
      gl.enableVertexAttribArray(shader_a_pos);
      gl.vertexAttribPointer(

            shader_a_pos, //           index of the vertex attribute that is to be modified
            4, //                      number of components per vertex attribute, must be 1, 2, 3, or 4
            GL.SHORT, //               data type of each component in the array
            false, //                  values should be normalized
            0, //                      offset in bytes between the beginning of consecutive vertex attributes
            0 //                       offset in bytes of the first component in the vertex attribute array
      );

      /*
       * Set vertex color
       */
      gl.bindBuffer(GL.ARRAY_BUFFER, bufferId_TrackVertices_Colors);
      gl.enableVertexAttribArray(shader_aVertexColor);
      gl.vertexAttribPointer(

            shader_aVertexColor, //    index of the vertex attribute that is to be modified
            4, //                      number of components per vertex attribute, must be 1, 2, 3, or 4
            GL.UNSIGNED_BYTE, //       data type of each component in the array
            false, //                  values should be normalized
            0, //                      offset in bytes between the beginning of consecutive vertex attributes
            0 //                       offset in bytes of the first component in the vertex attribute array
      );

      // set matrix
      viewport.mvp.setAsUniform(shader.shader_u_mvp);

//       final double groundResolution = MercatorProjection.groundResolution(mapPosition);

      /**
       * Line scale factor for non fixed lines:
       * <p>
       * Within a zoom-level, lines would be scaled by the factor 2 by view-matrix.
       * Though lines should only scale by sqrt(2). This is achieved
       * by inverting scaling of extrusion vector with: width/sqrt(scale).
       */
      final double variableScale = Math.sqrt(vp2mpScale);

      /*
       * Scale factor to map one pixel on tile to one pixel on screen:
       * used with orthographic projection, (shader mode == 1)
       */
      final double pixel = (shaderMode == SHADER_PROJECTED)
            ? 0.0001
            : 1.5 / vp2mpScale;

      gl.uniform1f(shader_u_fade, (float) pixel);

      int capMode = CAP_THIN;
      gl.uniform1i(shader_u_mode, capMode);

      boolean isBlur = false;
      double width;

      float heightOffset = 0;
      gl.uniform1f(shader_u_height, heightOffset);

      final LineStyle lineStyle = trackBucket.lineStyle.current();

      final boolean isPaintOutline = trackConfig.isShowOutline;
      final float outlineWidth = trackConfig.outlineWidth;
      final float outlineBrightnessRaw = trackConfig.outlineBrighness; // -1.0 ... 1.0
      final float outlineBrightness = outlineBrightnessRaw + 1; // 0...2

      gl.uniform1i(shader_uColorMode, trackBucket.lineColorMode);

      if (lineStyle.heightOffset != trackBucket.heightOffset) {
         trackBucket.heightOffset = lineStyle.heightOffset;
      }

      if (trackBucket.heightOffset != heightOffset) {

         heightOffset = trackBucket.heightOffset;

//          final double lineHeight = (heightOffset / groundResolution) / scale;
         final double lineHeight = heightOffset * vp2mpScale;

         gl.uniform1f(shader_u_height, (float) lineHeight);
      }

      if (lineStyle.fadeScale < mapPosition.zoomLevel) {

         GLUtils.setColor(shader_u_color, lineStyle.color, 1);

      } else if (lineStyle.fadeScale > mapPosition.zoomLevel) {

         return;

      } else {

         final float alpha = (float) (vp2mpScale > 1.2 ? vp2mpScale : 1.2) - 1;
         GLUtils.setColor(shader_u_color, lineStyle.color, alpha);
      }

      // set common alpha for the vertex color
      final float vertexAlpha = ((lineStyle.color >>> 24) & 0xff) / 255f;
      gl.uniform1f(shader_uVertexColorAlpha, vertexAlpha);

      if (shaderMode == SHADER_PROJECTED && isBlur && lineStyle.blur == 0) {
         gl.uniform1f(shader_u_fade, (float) pixel);
         isBlur = false;
      }

      /*
       * First draw the outline which is afterwards overwritten partly by the core line
       */
      if (isPaintOutline) {

         // core width
         if (lineStyle.fixed) {
            width = Math.max(lineStyle.width, 1) / vp2mpScale;
         } else {
            width = lineStyle.width / variableScale;
         }

         // add outline width
         if (lineStyle.fixed) {
            width += outlineWidth / vp2mpScale;
         } else {
            width += outlineWidth / variableScale;
         }

         gl.uniform1f(shader_u_width, (float) (width * COORD_SCALE_BY_DIR_SCALE));

         // outline brighness
         gl.uniform1f(shader_uOutlineBrightness, outlineBrightness);

         // line-edge fade
         if (lineStyle.blur > 0) {
            gl.uniform1f(shader_u_fade, lineStyle.blur);
            isBlur = true;
         } else if (shaderMode == SHADER_FLAT) {
            gl.uniform1f(shader_u_fade, (float) (pixel / width));
         }

         // cap mode
         if (trackBucket.isCapRounded) {
            if (capMode != CAP_ROUND) {
               capMode = CAP_ROUND;
               gl.uniform1i(shader_u_mode, capMode);
            }
         } else if (capMode != CAP_BUTT) {
            capMode = CAP_BUTT;
            gl.uniform1i(shader_u_mode, capMode);
         }

         gl.drawArrays(GL.TRIANGLE_STRIP, 0, trackBucket.numTrackVertices);
      }

      /*
       * Draw core line over the outline
       */

      // invert scaling of extrusion vectors so that line width stays the same.
      if (lineStyle.fixed) {
         width = Math.max(lineStyle.width, 1) / vp2mpScale;
      } else {
         width = lineStyle.width / variableScale;
      }

      // disable outline brighness/darkness, this value is multiplied with the color
      gl.uniform1f(shader_uOutlineBrightness, 1.0f);

      // factor to increase line width relative to scale
      gl.uniform1f(shader_u_width, (float) (width * COORD_SCALE_BY_DIR_SCALE));

      // line-edge fade
      if (lineStyle.blur > 0) {
         gl.uniform1f(shader_u_fade, lineStyle.blur);
         isBlur = true;
      } else if (shaderMode == SHADER_FLAT) {
         gl.uniform1f(shader_u_fade, (float) (pixel / width));
      }

      // cap mode
      if (trackBucket.isCapRounded) {
         if (capMode != CAP_ROUND) {
            capMode = CAP_ROUND;
            gl.uniform1i(shader_u_mode, capMode);
         }
      } else if (capMode != CAP_BUTT) {
         capMode = CAP_BUTT;
         gl.uniform1i(shader_u_mode, capMode);
      }

//       GLState.test(true, false);
//       gl.depthMask(true);
//       {
//          gl.drawArrays(GL.TRIANGLE_STRIP, lineBucket.vertexOffset, lineBucket.numVertices);
//       }
//       gl.depthMask(false);

      gl.drawArrays(GL.TRIANGLE_STRIP, 0, trackBucket.numTrackVertices);
   }

   private static void paint_20_DirectionArrows(final GLViewport viewport,
                                                final float vp2mpScale,
                                                final TourTrack_Bucket trackBucket) {

// SET_FORMATTING_OFF

      final DirectionArrowsShader shader     = _directionArrowShader;

      final int shader_a_pos                 = shader.shader_a_pos;
      final int shader_attrib_ColorCoord     = shader.shader_attrib_ColorCoord;

// SET_FORMATTING_ON

      shader.useProgram();

      final Map25TrackConfig trackConfig = Map25ConfigManager.getActiveTourTrackConfig();

      // set mvp matrix into the shader
      viewport.mvp.setAsUniform(shader.shader_u_mvp);

      // vertices position
      gl.bindBuffer(GL.ARRAY_BUFFER, bufferId_DirArrows);
      gl.enableVertexAttribArray(shader_a_pos);
      gl.vertexAttribPointer(

            shader_a_pos, //                 index of the vertex attribute that is to be modified
            4, //                            number of components per vertex attribute, must be 1, 2, 3, or 4
            GL.SHORT, //                     data type of each component in the array
            false, //                        values should be normalized
            0, //                            offset in bytes between the beginning of consecutive vertex attributes
            0 //                             offset in bytes of the first component in the vertex attribute array
      );

      // direction arrow color
      gl.bindBuffer(GL.ARRAY_BUFFER, bufferId_DirArrows_ColorCoords);
      gl.enableVertexAttribArray(shader_attrib_ColorCoord);
      gl.vertexAttribPointer(

            shader_attrib_ColorCoord, //     index of the vertex attribute that is to be modified
            3, //                            number of components per vertex attribute, must be 1, 2, 3, or 4
            GL.SHORT, //                     data type of each component in the array
            false, //                        values should be normalized
            0, //                            offset in bytes between the beginning of consecutive vertex attributes
            0 //                             offset in bytes of the first component in the vertex attribute array
      );

      // arrow colors
      final float arrowColors[] = trackConfig.getArrowColors();
      gl.uniform4fv(shader.shader_uni_ArrowColors, arrowColors.length / 4, arrowColors, 0);

      // outline width's
      gl.uniform2f(shader.shader_uni_OutlineWidth,
            trackConfig.arrowWing_OutlineWidth / 200f,
            trackConfig.arrowFin_OutlineWidth / 200f);

      // viewport to map scale: 1.0...2.0
      gl.uniform1f(shader.shader_uni_Vp2MpScale, vp2mpScale);

      /*
       * Draw direction arrows
       */
      GLState.test(true, false);
      gl.depthMask(true);
      {
         gl.drawArrays(GL.TRIANGLES, 0, trackBucket.directionArrow_Vertices.size());
      }
      gl.depthMask(false);

//    GLUtils.checkGlError(TourTrack_Shader.class.getName());
   }

   private static void paint_30_Animation(final GLViewport viewport, final float vp2mpScale, final TourTrack_Bucket trackBucket) {

      final ShortArrayList animatedPositions = trackBucket.animatedPositions;

      final int numPositions = animatedPositions.size();
      if (numPositions < 1) {
         return;
      }

      final AnimationShader shader = _animationShader;
      shader.useProgram();

      // get animated position
      final int posIndex = getAnimatedPositionIndex(animatedPositions);

      // ensure bounds -> this case happened during debugging several time
      if (posIndex >= numPositions - 2) {
         return;
      }

      final short posX = animatedPositions.get(posIndex);
      final short posY = animatedPositions.get(posIndex + 1);

      _animationMVP.copy(viewport.mvp);

      viewport.mvp.setTransScale(posX * 0.5f, posY * 0.5f, 1);
      viewport.mvp.multiplyMM(viewport.viewproj, viewport.mvp);

      // set mvp matrix into the shader
      viewport.mvp.setAsUniform(shader.shader_uni_MVP);

      final int shader_Attrib_Pos = shader.shader_attrib_Pos;

      // vertices position
      gl.bindBuffer(GL.ARRAY_BUFFER, bufferId_AnimationVertices);
      gl.enableVertexAttribArray(shader_Attrib_Pos);
      gl.vertexAttribPointer(

            shader_Attrib_Pos, //      index of the vertex attribute that is to be modified
            3, //                      number of components per vertex attribute, must be 1, 2, 3, or 4
            GL.SHORT, //               data type of each component in the array
            false, //                  values should be normalized
            0, //                      offset in bytes between the beginning of consecutive vertex attributes
            0 //                       offset in bytes of the first component in the vertex attribute array
      );

      // viewport to map scale: 1.0...2.0
      gl.uniform1f(shader.shader_uni_Vp2MpScale, vp2mpScale);

      /*
       * Draw animation
       */
      GLState.test(true, false);
      gl.depthMask(true);
      {
         gl.drawArrays(GL.TRIANGLES, 0, _animationVertices.length);
      }
      gl.depthMask(false);

//    GLUtils.checkGlError(TourTrack_Shader.class.getName());

   }

   public static boolean setupShader() {

// SET_FORMATTING_OFF

      _lineShaders[SHADER_PROJECTED]   = new LineShader("line_aa_proj");                  //$NON-NLS-1$
      _lineShaders[SHADER_FLAT]        = new LineShader("line_aa");                       //$NON-NLS-1$

      _animationShader                 = new AnimationShader("animateTrack");             //$NON-NLS-1$
      _directionArrowShader            = new DirectionArrowsShader("directionArrows");    //$NON-NLS-1$

      // create buffer id's
      bufferId_AnimationVertices       = gl.genBuffer();
      bufferId_DirArrows               = gl.genBuffer();
      bufferId_DirArrows_ColorCoords   = gl.genBuffer();
      bufferId_TrackVertices           = gl.genBuffer();
      bufferId_TrackVertices_Colors    = gl.genBuffer();

// SET_FORMATTING_ON

//    _dirArrowFrameBuffer = new FrameBuffer();

      return true;
   }
}

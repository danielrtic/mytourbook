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

import net.tourbook.map25.Map25ConfigManager;
import net.tourbook.map25.layer.tourtrack.Map25TrackConfig;

import org.oscim.backend.GL;
import org.oscim.core.MapPosition;
import org.oscim.renderer.GLState;
import org.oscim.renderer.GLUtils;
import org.oscim.renderer.GLViewport;
import org.oscim.theme.styles.LineStyle;

public final class TourTrack_Shader {

   /**
    * Factor to normalize extrusion vector and scale to coord scale
    */
   private static final float           COORD_SCALE_BY_DIR_SCALE = COORD_SCALE / TourTrack_Bucket.DIR_SCALE;

   private static final int             CAP_THIN                 = 0;
   private static final int             CAP_BUTT                 = 1;
   private static final int             CAP_ROUND                = 2;

   private static final int             SHADER_PROJECTED         = 0;
   private static final int             SHADER_FLAT              = 1;

   private static DirectionArrowsShader _directionArrowShader;
   private static LineShader[]          _lineShaders             = { null, null };

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

//          shader_u_width

      ;

      DirectionArrowsShader(final String shaderFile) {

         if (createMT(shaderFile) == false) {
            return;
         }

   // SET_FORMATTING_OFF

            shader_u_mvp                  = getUniform("u_mvp");                 //$NON-NLS-1$
            shader_a_pos                  = getAttrib("a_pos");                  //$NON-NLS-1$
            shader_attrib_ColorCoord      = getAttrib("attrib_ColorCoord");      //$NON-NLS-1$

//          shader_u_width                = getUniform("u_width");               //$NON-NLS-1$
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

         if (createMT(shaderFile) == false) {
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

      @Override
      public boolean useProgram() {

         if (super.useProgram()) {

            GLState.enableVertexArrays(shader_a_pos, GLState.DISABLED);

            return true;
         }

         return false;
      }
   }

   /**
    * Performs OpenGL drawing commands of the renderBucket(s)
    *
    * @param trackBucket
    * @param viewport
    * @param vp2mpScale
    *           Viewport scale 2 map scale: it is between 1...2
    * @param allRenderBuckets
    * @return
    */
   public static TourTrack_Bucket paint(final TourTrack_Bucket trackBucket,
                                        final GLViewport viewport,
                                        final float vp2mpScale,
                                        final TourTrack_AllBuckets allRenderBuckets) {

//    _dirArrowFrameBuffer.updateViewport(viewport, 0.5f);

      final TourTrack_Bucket[] inoutRenderBucket = new TourTrack_Bucket[] { trackBucket };

      final Map25TrackConfig trackConfig = Map25ConfigManager.getActiveTourTrackConfig();

      // fix alpha blending
      gl.blendFunc(GL.SRC_ALPHA, GL.ONE_MINUS_SRC_ALPHA);
      {
         paint_10_Track(inoutRenderBucket, viewport, vp2mpScale, allRenderBuckets);

         if (trackConfig.isShowDirectionArrow) {
            paint_20_DirectionArrows(viewport, allRenderBuckets, vp2mpScale);
         }
      }
      gl.blendFunc(GL.ONE, GL.ONE_MINUS_SRC_ALPHA); // reset to map default

      return inoutRenderBucket[0];
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
   private static void paint_10_Track(final TourTrack_Bucket[] inoutRenderBucket,
                                      final GLViewport viewport,
                                      final float vp2mpScale,
                                      final TourTrack_AllBuckets renderBucketsAll) {

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
      gl.bindBuffer(GL.ARRAY_BUFFER, renderBucketsAll.vertexColor_BufferId);
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

      for (; inoutRenderBucket[0] != null; inoutRenderBucket[0] = inoutRenderBucket[0].next) {

         final TourTrack_Bucket lineBucket = inoutRenderBucket[0];
         final LineStyle lineStyle = lineBucket.lineStyle.current();

         final float scale = lineBucket.scale;

         final boolean isPaintOutline = trackConfig.isShowOutline;
         final float outlineWidth = trackConfig.outlineWidth;
         final float outlineBrightnessRaw = trackConfig.outlineBrighness; // -1.0 ... 1.0
         final float outlineBrightness = outlineBrightnessRaw + 1; // 0...2

         gl.uniform1i(shader_uColorMode, lineBucket.lineColorMode);

         if (lineStyle.heightOffset != lineBucket._heightOffset) {
            lineBucket._heightOffset = lineStyle.heightOffset;
         }

         if (lineBucket._heightOffset != heightOffset) {

            heightOffset = lineBucket._heightOffset;

//             final double lineHeight = (heightOffset / groundResolution) / scale;
            final double lineHeight = heightOffset * vp2mpScale;

            gl.uniform1f(shader_u_height, (float) lineHeight);
         }

         if (lineStyle.fadeScale < mapPosition.zoomLevel) {

            GLUtils.setColor(shader_u_color, lineStyle.color, 1);

         } else if (lineStyle.fadeScale > mapPosition.zoomLevel) {

            continue;

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
               width = scale * lineStyle.width / variableScale;
            }

            // add outline width
            if (lineStyle.fixed) {
               width += outlineWidth / vp2mpScale;
            } else {
               width += scale * outlineWidth / variableScale;
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
            if (lineBucket._isCapRounded) {
               if (capMode != CAP_ROUND) {
                  capMode = CAP_ROUND;
                  gl.uniform1i(shader_u_mode, capMode);
               }
            } else if (capMode != CAP_BUTT) {
               capMode = CAP_BUTT;
               gl.uniform1i(shader_u_mode, capMode);
            }

            gl.drawArrays(GL.TRIANGLE_STRIP, lineBucket.vertexOffset, lineBucket.numVertices);
         }

         /*
          * Draw core line over the outline
          */

         // invert scaling of extrusion vectors so that line width stays the same.
         if (lineStyle.fixed) {
            width = Math.max(lineStyle.width, 1) / vp2mpScale;
         } else {
            width = scale * lineStyle.width / variableScale;
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
         if (scale < 1.0) {
            if (capMode != CAP_THIN) {
               capMode = CAP_THIN;
               gl.uniform1i(shader_u_mode, capMode);
            }
         } else if (lineBucket._isCapRounded) {
            if (capMode != CAP_ROUND) {
               capMode = CAP_ROUND;
               gl.uniform1i(shader_u_mode, capMode);
            }
         } else if (capMode != CAP_BUTT) {
            capMode = CAP_BUTT;
            gl.uniform1i(shader_u_mode, capMode);
         }

//          GLState.test(true, false);
//          gl.depthMask(true);
//          {
//             gl.drawArrays(GL.TRIANGLE_STRIP, lineBucket.vertexOffset, lineBucket.numVertices);
//          }
//          gl.depthMask(false);

         gl.drawArrays(GL.TRIANGLE_STRIP, lineBucket.vertexOffset, lineBucket.numVertices);
      }
   }

   private static void paint_20_DirectionArrows(final GLViewport viewport,
                                                final TourTrack_AllBuckets allRenderBuckets,
                                                final float vp2mpScale) {

      final Map25TrackConfig trackConfig = Map25ConfigManager.getActiveTourTrackConfig();

// SET_FORMATTING_OFF

         final DirectionArrowsShader shader        = _directionArrowShader;

         final int shader_a_pos                    = shader.shader_a_pos;
         final int shader_attrib_ColorCoord        = shader.shader_attrib_ColorCoord;
         final int shader_u_mvp                    = shader.shader_u_mvp;
//       final int shader_u_width                  = shader.shader_u_width;
         final int shader_uni_ArrowColors          = shader.shader_uni_ArrowColors;
         final int shader_uni_OutlineWidth         = shader.shader_uni_OutlineWidth;
         final int shader_uni_Vp2MpScale           = shader.shader_uni_Vp2MpScale;

// SET_FORMATTING_ON

      shader.useProgram();

      // set mvp matrix into the shader
      viewport.mvp.setAsUniform(shader_u_mvp);

      gl.bindBuffer(GL.ARRAY_BUFFER, allRenderBuckets.dirArrows_BufferId);
      gl.enableVertexAttribArray(shader_a_pos);
      gl.vertexAttribPointer(

            shader_a_pos, //           index of the vertex attribute that is to be modified
            4, //                      number of components per vertex attribute, must be 1, 2, 3, or 4
            GL.SHORT, //               data type of each component in the array
            false, //                  values should be normalized
            0, //                      offset in bytes between the beginning of consecutive vertex attributes
            0 //                       offset in bytes of the first component in the vertex attribute array
      );

      gl.bindBuffer(GL.ARRAY_BUFFER, allRenderBuckets.dirArrows_ColorCoords_BufferId);
      gl.enableVertexAttribArray(shader_attrib_ColorCoord);
      gl.vertexAttribPointer(

            shader_attrib_ColorCoord, //    index of the vertex attribute that is to be modified
            3, //                      number of components per vertex attribute, must be 1, 2, 3, or 4
            GL.SHORT, //               data type of each component in the array
            false, //                  values should be normalized
            0, //                      offset in bytes between the beginning of consecutive vertex attributes
            0 //                       offset in bytes of the first component in the vertex attribute array
      );

      final int numDirArrowShorts = allRenderBuckets.numShortsForDirectionArrows;

//       final float width = 10 / vp2mpScale;
//
//       gl.uniform1f(shader_u_width, width * COORD_SCALE_BY_DIR_SCALE);

      // arrow colors
      final float arrowColors[] = trackConfig.getArrowColors();
      gl.uniform4fv(shader_uni_ArrowColors, arrowColors.length / 4, arrowColors, 0);

      // outline width's
      gl.uniform2f(shader_uni_OutlineWidth,
            trackConfig.arrowWing_OutlineWidth / 200f,
            trackConfig.arrowFin_OutlineWidth / 200f);

      gl.uniform1f(shader_uni_Vp2MpScale, vp2mpScale);

      /*
       * Draw direction arrows
       */

      GLState.test(true, false);
      gl.depthMask(true);
      {
         gl.drawArrays(GL.TRIANGLES, 0, numDirArrowShorts);
      }
      gl.depthMask(false);

      GLUtils.checkGlError(TourTrack_Shader.class.getName());
   }

   public static boolean setupShader() {

// SET_FORMATTING_OFF

      _lineShaders[SHADER_PROJECTED]   = new LineShader("line_aa_proj");       //$NON-NLS-1$
      _lineShaders[SHADER_FLAT]        = new LineShader("line_aa");            //$NON-NLS-1$

      _directionArrowShader            = new DirectionArrowsShader("directionArrows");    //$NON-NLS-1$

// SET_FORMATTING_ON

//    _dirArrowFrameBuffer = new FrameBuffer();

      return true;
   }
}

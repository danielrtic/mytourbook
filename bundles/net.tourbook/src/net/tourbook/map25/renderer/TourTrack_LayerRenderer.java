/*******************************************************************************
 * Copyright (C) 2023 Wolfgang Schramm and Contributors
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

import static org.oscim.renderer.MapRenderer.COORD_SCALE;

import net.tourbook.common.color.ColorUtil;
import net.tourbook.map.player.MapPlayerManager;
import net.tourbook.map25.Map25ConfigManager;
import net.tourbook.map25.layer.tourtrack.Map25TrackConfig;
import net.tourbook.map25.layer.tourtrack.Map25TrackConfig.LineColorMode;
import net.tourbook.map25.layer.tourtrack.TourTrack_Layer;

import org.eclipse.collections.impl.list.mutable.primitive.FloatArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;
import org.oscim.backend.canvas.Paint.Cap;
import org.oscim.core.GeoPoint;
import org.oscim.core.MapPosition;
import org.oscim.core.MercatorProjection;
import org.oscim.core.Point;
import org.oscim.core.Tile;
import org.oscim.map.Map;
import org.oscim.renderer.GLMatrix;
import org.oscim.renderer.GLState;
import org.oscim.renderer.GLViewport;
import org.oscim.renderer.LayerRenderer;
import org.oscim.theme.styles.LineStyle;
import org.oscim.utils.FastMath;
import org.oscim.utils.async.SimpleWorker;
import org.oscim.utils.geom.LineClipper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Original code: org.oscim.renderer.BucketRenderer
 */
public class TourTrack_LayerRenderer extends LayerRenderer {

   public static final Logger            log           = LoggerFactory.getLogger(TourTrack_LayerRenderer.class);

   /**
    * Map position/scale during compile time
    */
   private MapPosition                   _compileMapPosition;
   private Map                           _map;

   /**
    * Buckets for rendering
    */
   private final TourTrack_BucketManager _bucketManager_ForPainting;
   private TourTrack_BucketManager       _bucketManager_ForWorker;

   private boolean                       _isCancelWorkerTask;
   private boolean                       _isUpdateNewPoints;

   /**
    * Contains all available geo locations for all selected tours in lat/lon E6 format.
    */
   private GeoPoint[]                    _anyGeoPoints;
   private IntArrayList                  _allTourStarts;
   private int[]                         _allGeoPointColors;

   private int                           _oldX         = -1;
   private int                           _oldY         = -1;
   private int                           _oldZoomScale = -1;

   /**
    * This is the layer for this renderer
    */
   private TourTrack_Layer               _tourLayer;

   private final TrackCompileWorker      _trackCompileWorker;

   /**
    * Line style
    */
   private LineStyle                     _lineStyle;

   /*
    * Track config values
    */
   private int _config_LineColorMode;

   private final static class TourCompileTask {

      TourTrack_BucketManager __taskBucketManager = new TourTrack_BucketManager();
      MapPosition             __mapPos            = new MapPosition();
   }

   private final class TrackCompileWorker extends SimpleWorker<TourCompileTask> {

      private static final int  MIN_DIST                        = 3;

      /**
       * Visible pixel of a line/tour, all other pixels are clipped with {@link #__lineClipper}
       */
      // limit coords
      private static final int  MAX_VISIBLE_PIXEL               = 2048;

      private static final int  NUM_RETURN_TRACK_POSITIONS      = 30;

      /**
       * Contains all available geo locations for all selected tours in lat/lon E6 format.
       */
      private GeoPoint[]        __anyGeoPoints;

      /**
       * Projected points 0...1 for all geo positions for all selected tours
       * <p>
       * Is projected from -180°...180° ==> 0...1 by using the {@link MercatorProjection}
       */
      private double[]          __allProjectedPoints            = new double[2];

      /**
       * Projected points 0...1 from the geo end point of the tour to the geo start point
       */
      private double[]          __allProjectedPoints_ReturnTrack;

      /**
       * Compiled points which are scaled with the current map position to "screen" pixels.
       * <p>
       * It contains all geo positions for all selected tours within the clipper area
       */
      private float[]           __pixelPoints;

      /**
       * Contains indices for all geo positions for all selected tours which are also outside of
       * the clipper area -2048...2048
       */
      private IntArrayList      __allNotClipped_LocationIndices = new IntArrayList();

      /**
       * One {@link #__pixelPointColorsHalf} has two {@link #__pixelPoints}, half == Half of items
       */
      private int[]             __pixelPointColorsHalf;

      /**
       * Contains the x/y projected pixels where direction arrows are painted
       */
      private FloatArrayList    __pixelDirection_ArrowPositions = new FloatArrayList();
      private IntArrayList      __pixelDirection_LocationIndex  = new IntArrayList();

      private int[]             __allGeoPointColors;
      private IntArrayList      __allTourStarts;

      /**
       * Is clipping line positions between
       * <p>
       * - {@link #MAX_VISIBLE_PIXEL} (-2048) ... <br>
       * + {@link #MAX_VISIBLE_PIXEL} (+2048)
       */
      private final LineClipper __lineClipper;

      private int               __numAllGeoPoints;

      public TrackCompileWorker(final Map map) {

         super(map, 50, new TourCompileTask(), new TourCompileTask());

         __lineClipper = new LineClipper(

               -MAX_VISIBLE_PIXEL,
               -MAX_VISIBLE_PIXEL,
               MAX_VISIBLE_PIXEL,
               MAX_VISIBLE_PIXEL);

         __pixelPoints = new float[0];
         __pixelPointColorsHalf = new int[0];
         __pixelDirection_ArrowPositions.clear();
         __pixelDirection_LocationIndex.clear();
         __allNotClipped_LocationIndices.clear();
      }

      /**
       * Adds a point (2 points: x,y) which are in the range of the {@link #__lineClipper},
       * -2048...+2048
       *
       * @param points
       * @param pointIndex
       * @param x
       * @param y
       * @return
       */
      private int addPoint(final float[] points, int pointIndex, final float x, final float y) {

         points[pointIndex++] = x;
         points[pointIndex++] = y;

         return pointIndex;
      }

      @Override
      public void cleanup(final TourCompileTask task) {

         task.__taskBucketManager.clear();
      }

      /**
       * This method is initiated from {@link TourTrack_LayerRenderer#update}
       */
      @Override
      public boolean doWork(final TourCompileTask task) {

         int numGeoPoints = __numAllGeoPoints;

         if (_isUpdateNewPoints) {

            /*
             * Create projected points (0...1) for all geo points (lat/long)
             */

            __anyGeoPoints = _anyGeoPoints;

            synchronized (__anyGeoPoints) {

               _isUpdateNewPoints = false;
               __numAllGeoPoints = numGeoPoints = __anyGeoPoints.length;

               double[] projectedPoints = __allProjectedPoints;

               if (numGeoPoints * 2 >= projectedPoints.length) {

                  projectedPoints = __allProjectedPoints = new double[numGeoPoints * 2];

                  __pixelPoints = new float[numGeoPoints * 2];
                  __pixelPointColorsHalf = new int[numGeoPoints];
                  __pixelDirection_ArrowPositions.clear();
                  __pixelDirection_LocationIndex.clear();
                  __allNotClipped_LocationIndices.clear();
               }

               // lat/lon -> 0...1
               for (int pointIndex = 0; pointIndex < numGeoPoints; pointIndex++) {
                  MercatorProjection.project(__anyGeoPoints[pointIndex], projectedPoints, pointIndex);
               }

               // fix concurrent issue, it does not need to clone the array
               __allGeoPointColors = _allGeoPointColors;
               __allTourStarts = _allTourStarts;
            }
         }

         _bucketManager_ForWorker = task.__taskBucketManager;

         if (numGeoPoints == 0) {

            if (task.__taskBucketManager.getBucket_Painter() != null) {

               task.__taskBucketManager.clear();

               mMap.render();
            }

         } else {

            doWork_CompileTrack(task, numGeoPoints);

            // trigger redraw to let renderer fetch the result
            mMap.render();
         }

         return true;
      }

      /**
       * Skip all track points which are not visible or are below a minimum distance
       *
       * @param task
       * @param numPoints
       */
      private void doWork_CompileTrack(final TourCompileTask task, final int numPoints) {

         // set current map position into the task map position
         final MapPosition compileMapPos = task.__mapPos;
         mMap.getMapPosition(compileMapPos);

         final int zoomlevel = compileMapPos.zoomLevel;
         final double mapScale = compileMapPos.scale = 1 << zoomlevel;

         final Map25TrackConfig trackConfig = Map25ConfigManager.getActiveTourTrackConfig();
         final boolean isShowDirectionArrows = trackConfig.isShowDirectionArrow;

         int arrow_MinimumDistance = trackConfig.arrow_IsAnimate

               // use a smaller distance when animated, to show the moving figure smoothly
               ? 1

               : trackConfig.arrow_MinimumDistance;

         // this is for debugging
         arrow_MinimumDistance = trackConfig.arrow_MinimumDistance;

         final TourTrack_Bucket workerBucket = getWorkerBucket(task.__taskBucketManager);

         // current map positions 0...1
         final double compileMapPosX = compileMapPos.x; // 0...1, lat == 0 -> 0.5
         final double compileMapPosY = compileMapPos.y; // 0...1, lon == 0 -> 0.5

         // number of x/y pixels for the whole map at the current zoom level
         final double compileMaxMapPixel = Tile.SIZE * mapScale;
         final int maxMapPixel = Tile.SIZE << (zoomlevel - 1);

         // flip around dateline
         int flip = 0;

         float pixelX = (float) ((__allProjectedPoints[0] - compileMapPosX) * compileMaxMapPixel);
         float pixelY = (float) ((__allProjectedPoints[1] - compileMapPosY) * compileMaxMapPixel);

         if (pixelX > maxMapPixel) {

            pixelX -= maxMapPixel * 2;
            flip = -1;

         } else if (pixelX < -maxMapPixel) {

            pixelX += maxMapPixel * 2;
            flip = 1;
         }

         // setup first tour index
         int tourIndex = 0;
         int nextTourStartIndex = getNextTourStartIndex(tourIndex);

         // setup tour clipper
         __lineClipper.clipStart(pixelX, pixelY);

// SET_FORMATTING_OFF

         final float[] pixelPoints                             = __pixelPoints;
         final int[] pixelPointColorsHalf                      = __pixelPointColorsHalf;

         final IntArrayList allNotClipped_LocationIndices      = __allNotClipped_LocationIndices;
         final FloatArrayList allDirectionArrow_PixelPosition  = __pixelDirection_ArrowPositions;
         final IntArrayList allDirectionArrow_LocationIndex    = __pixelDirection_LocationIndex;

// SET_FORMATTING_ON

         allNotClipped_LocationIndices.clear();
         allDirectionArrow_PixelPosition.clear();
         allDirectionArrow_LocationIndex.clear();

         // set first point / color / direction arrow
         int pixelPointIndex = addPoint(pixelPoints, 0, pixelX, pixelY);
         pixelPointColorsHalf[0] = __allGeoPointColors[0];
         allDirectionArrow_PixelPosition.addAll(pixelX, pixelY);
         allDirectionArrow_LocationIndex.add(0);
         allNotClipped_LocationIndices.add(0);

         float prevX = pixelX;
         float prevY = pixelY;
         float prevXNotClipped = pixelX;
         float prevYNotClipped = pixelY;
         float prevXArrow = pixelX;
         float prevYArrow = pixelY;

         float[] segment = null;

         for (int projectedPointIndex = 2; projectedPointIndex < numPoints * 2; projectedPointIndex += 2) {

            final int projectedPointIndexHalf = projectedPointIndex / 2;

            // convert projected points 0...1 into map pixel
            pixelX = (float) ((__allProjectedPoints[projectedPointIndex + 0] - compileMapPosX) * compileMaxMapPixel);
            pixelY = (float) ((__allProjectedPoints[projectedPointIndex + 1] - compileMapPosY) * compileMaxMapPixel);

            int flipDirection = 0;

            if (pixelX > maxMapPixel) {

               pixelX -= maxMapPixel * 2;
               flipDirection = -1;

            } else if (pixelX < -maxMapPixel) {

               pixelX += maxMapPixel * 2;
               flipDirection = 1;
            }

            if (flip != flipDirection) {

               flip = flipDirection;

               if (pixelPointIndex > 2) {
                  workerBucket.addLine(pixelPoints, pixelPointIndex, false, pixelPointColorsHalf);
               }

               __lineClipper.clipStart(pixelX, pixelY);

               pixelPointIndex = addPoint(pixelPoints, 0, pixelX, pixelY);
               pixelPointColorsHalf[0] = __allGeoPointColors[projectedPointIndexHalf];

               continue;
            }

            // ckeck if a new tour starts
            if (projectedPointIndex >= nextTourStartIndex) {

               // finish last tour (copied from flip code)
               if (pixelPointIndex > 2) {
                  workerBucket.addLine(pixelPoints, pixelPointIndex, false, pixelPointColorsHalf);
               }

               // setup next tour
               nextTourStartIndex = getNextTourStartIndex(++tourIndex);

               __lineClipper.clipStart(pixelX, pixelY);
               pixelPointIndex = addPoint(pixelPoints, 0, pixelX, pixelY);
               pixelPointColorsHalf[0] = __allGeoPointColors[projectedPointIndexHalf];

               continue;
            }

            /*
             * Get points which are not clipped
             */
            final float diffXNotClipped = pixelX - prevXNotClipped;
            final float diffYNotClipped = pixelY - prevYNotClipped;

            if (FastMath.absMaxCmp(diffXNotClipped, diffYNotClipped, MIN_DIST)) {

               // point > min distance == 3

               allNotClipped_LocationIndices.add(projectedPointIndexHalf);

               prevXNotClipped = pixelX;
               prevYNotClipped = pixelY;
            }

            final int clipperCode = __lineClipper.clipNext(pixelX, pixelY);

            if (clipperCode != LineClipper.INSIDE) {

               /*
                * Point is outside clipper area +-2048 x +-2048
                */

               if (pixelPointIndex > 2) {
                  workerBucket.addLine(pixelPoints, pixelPointIndex, false, pixelPointColorsHalf);
               }

               if (clipperCode == LineClipper.INTERSECTION) {

                  // add line segment
                  segment = __lineClipper.getLine(segment, 0);
                  workerBucket.addLine(segment, 4, false, pixelPointColorsHalf);

                  // the prev point is the real point not the clipped point
                  // prevX = __lineClipper.outX2;
                  // prevY = __lineClipper.outY2;
                  prevX = pixelX;
                  prevY = pixelY;
               }

               pixelPointIndex = 0;

               // if the end point is inside, add it
               if (__lineClipper.getPrevOutcode() == LineClipper.INSIDE) {

                  pixelPoints[pixelPointIndex++] = prevX;
                  pixelPoints[pixelPointIndex++] = prevY;

                  pixelPointColorsHalf[(pixelPointIndex - 1) / 2] = __allGeoPointColors[projectedPointIndexHalf];
               }

               continue;
            }

            /*
             * Point is inside clipper +-2048 x +-2048
             */

            final float diffX = pixelX - prevX;
            final float diffY = pixelY - prevY;

            if (pixelPointIndex == 0 || FastMath.absMaxCmp(diffX, diffY, MIN_DIST)) {

               // point > min distance == 3

               pixelPoints[pixelPointIndex++] = prevX = pixelX;
               pixelPoints[pixelPointIndex++] = prevY = pixelY;

               pixelPointColorsHalf[(pixelPointIndex - 1) / 2] = __allGeoPointColors[projectedPointIndexHalf];
            }

            final float diffXArrow = pixelX - prevXArrow;
            final float diffYArrow = pixelY - prevYArrow;

            if (projectedPointIndex == 0 || FastMath.absMaxCmp(diffXArrow, diffYArrow, arrow_MinimumDistance)) {

               // point > min distance

               prevXArrow = pixelX;
               prevYArrow = pixelY;

               allDirectionArrow_PixelPosition.addAll(pixelX, pixelY);
               allDirectionArrow_LocationIndex.add(projectedPointIndexHalf);
            }
         }

         if (pixelPointIndex > 2) {
            workerBucket.addLine(pixelPoints, pixelPointIndex, false, pixelPointColorsHalf);
         }

         if (isShowDirectionArrows) {

            // convert arrow positions into arrow vertices
            workerBucket.createArrowVertices(
                  allDirectionArrow_PixelPosition.toArray(),
                  allDirectionArrow_LocationIndex.toArray());
         }

         doWork_CreateReturnTrack(mapScale);

         workerBucket.allProjectedPoints = __allProjectedPoints;
         workerBucket.allProjectedPoints_ReturnTrack = __allProjectedPoints_ReturnTrack;
         workerBucket.allNotClipped_GeoLocationIndices = allNotClipped_LocationIndices.toArray();

      } // doWork_CompileTrack end

      /**
       * Create return track from end...start or start...end
       */
      private void doWork_CreateReturnTrack(final double mapScale) {

         final GeoPoint geoPointStart = _anyGeoPoints[0];
         final GeoPoint geoPointEnd = _anyGeoPoints[__numAllGeoPoints - 1];

// optimize return track by reducing/increasing number of geo positions
         // distance in meters between start and end
         final double sphericalDistance = geoPointStart.sphericalDistance(geoPointEnd);

         final int numReturnTrackPositions = 10;//NUM_RETURN_TRACK_POSITIONS;

         final Point projectedStart = MercatorProjection.project(geoPointStart, null);
         final Point projectedEnd = MercatorProjection.project(geoPointEnd, null);

         final double projectedReturnTrack_DiffX = projectedEnd.x - projectedStart.x;
         final double projectedReturnTrack_DiffY = projectedEnd.y - projectedStart.y;

         final int numReturnTrackPoints = numReturnTrackPositions * 2;

         final double projectedEnd2StartIntervalX = projectedReturnTrack_DiffX / numReturnTrackPositions;
         final double projectedEnd2StartIntervalY = projectedReturnTrack_DiffY / numReturnTrackPositions;

         __allProjectedPoints_ReturnTrack = new double[numReturnTrackPoints];

         for (int pointIndex = 0; pointIndex < numReturnTrackPoints; pointIndex += 2) {

            final double diffX = projectedEnd2StartIntervalX * pointIndex / 2;
            final double diffY = projectedEnd2StartIntervalY * pointIndex / 2;

            __allProjectedPoints_ReturnTrack[pointIndex] = projectedEnd.x - diffX;
            __allProjectedPoints_ReturnTrack[pointIndex + 1] = projectedEnd.y - diffY;
         }
      }

      private int getNextTourStartIndex(final int tourIndex) {

         if (__allTourStarts.size() > tourIndex + 1) {

            return __allTourStarts.get(tourIndex + 1) * 2;

         } else {

            return Integer.MAX_VALUE;
         }
      }

   } // class TrackCompileWorker end

   public TourTrack_LayerRenderer(final TourTrack_Layer tourLayer, final Map map) {

      _tourLayer = tourLayer;
      _map = map;

      _bucketManager_ForPainting = new TourTrack_BucketManager();
      _compileMapPosition = new MapPosition();

      _anyGeoPoints = new GeoPoint[] {};
      _allTourStarts = new IntArrayList();

      _trackCompileWorker = new TrackCompileWorker(map);

      _lineStyle = createLineStyle();
   }

   private LineStyle createLineStyle() {

      final Map25TrackConfig trackConfig = Map25ConfigManager.getActiveTourTrackConfig();

      _config_LineColorMode = trackConfig.lineColorMode == LineColorMode.SOLID

            // solid color
            ? 0

            // gradient color
            : 1;

      final int lineColor = ColorUtil.getARGB(trackConfig.lineColor, trackConfig.lineOpacity);

      final int trackVerticalOffset = trackConfig.isTrackVerticalOffset
            ? trackConfig.trackVerticalOffset
            : 0;

      final LineStyle style = LineStyle.builder()

            .strokeWidth(trackConfig.lineWidth)

            .color(lineColor)

//          .cap(Cap.BUTT)
//          .cap(Cap.SQUARE)
            .cap(Cap.ROUND)

            // I don't know how outline is working
            // .isOutline(true)

            // "u_height" is above the ground -> this is the z axis
            .heightOffset(trackVerticalOffset)

            // VERY IMPORTANT: Set fixed=true, otherwise the line width
            // will jump when the zoom-level is changed !!!
            .fixed(true)

//          .blur(trackConfig.testValue / 100.0f)

            .build();

      return style;
   }

   /**
    * Update linestyle in the bucket
    *
    * @param bucketManager
    * @return
    */
   private TourTrack_Bucket getWorkerBucket(final TourTrack_BucketManager bucketManager) {

      TourTrack_Bucket trackBucket;

      trackBucket = bucketManager.getBucket_Worker();

// SET_FORMATTING_OFF

      trackBucket.lineStyle       = _lineStyle;
      trackBucket.lineColorMode   = _config_LineColorMode;

// SET_FORMATTING_ON

      return trackBucket;
   }

   public void onModifyConfig(final boolean isVerticesModified) {

      _lineStyle = createLineStyle();

      if (isVerticesModified) {

         // vertices structure is modified -> recreate vertices

         _trackCompileWorker.submit(0);

      } else {

         // do a fast update

         getWorkerBucket(_bucketManager_ForWorker);

         _map.render();
      }
   }

   /**
    * Render (do OpenGL drawing) all 'buckets'
    */
   @Override
   public synchronized void render(final GLViewport viewport) {

      final TourTrack_Bucket trackBucket = _bucketManager_ForPainting.getBucket_Painter();
      if (trackBucket == null) {
         return;
      }

      GLState.test(false, false);
      GLState.blend(true);

      setMVPMatrix(viewport);

      TourTrack_Shader.paint(trackBucket, viewport, _compileMapPosition);
   }

   /**
    * Adjust MVP matrix to the difference between the compile time map location and the current map
    * location.
    * <p>
    * <b>Original comment</b>:
    * <p>
    * Utility: Set matrices.mvp matrix relative to the difference of current
    * MapPosition and the last updated Overlay MapPosition.
    * <p>
    * Use this to 'stick' your layer to the map. Note: Vertex coordinates
    * are assumed to be scaled by MapRenderer.COORD_SCALE (== 8).
    *
    * @param viewport
    *           GLViewport
    */
   private void setMVPMatrix(final GLViewport viewport) {

      final MapPosition viewportMapPosition = viewport.pos;

      double diffX = _compileMapPosition.x - viewportMapPosition.x;
      final double diffY = _compileMapPosition.y - viewportMapPosition.y;

      //wrap around date-line
      while (diffX < 0.5) {
         diffX += 1.0;
      }
      while (diffX > 0.5) {
         diffX -= 1.0;
      }

      final double tileScale = Tile.SIZE * viewportMapPosition.scale;

      final double scaledDiffX = diffX * tileScale;
      final double scaledDiffY = diffY * tileScale;

      final double vpScale2mapScale = viewportMapPosition.scale / _compileMapPosition.scale;
      final double diffScale = vpScale2mapScale / COORD_SCALE;

      final GLMatrix mvp = viewport.mvp;

      mvp.setTransScale((float) scaledDiffX, (float) scaledDiffY, (float) diffScale);
      mvp.multiplyLhs(viewport.viewproj);
   }

   public void setupTourPositions(final GeoPoint[] allGeoPoints,
                                  final int[] allGeoPointColors,
                                  final IntArrayList allTourStarts) {

      synchronized (_anyGeoPoints) {

         _anyGeoPoints = allGeoPoints;
         _allGeoPointColors = allGeoPointColors;

         _allTourStarts.clear();
         _allTourStarts.addAll(allTourStarts);
      }

      _trackCompileWorker.cancel(true);

      _isUpdateNewPoints = true;
      _isCancelWorkerTask = true;

      TourTrack_Shader.resetAngle();
   }

   @Override
   public synchronized void update(final GLViewport viewport) {

      if (_tourLayer.isEnabled() == false) {
         return;
      }

      final int zoomLevel = viewport.pos.zoomLevel;

      final int currentZoomScale = 1 << zoomLevel;
      final int currentX = (int) (viewport.pos.x * currentZoomScale);
      final int currentY = (int) (viewport.pos.y * currentZoomScale);

      /*
       * Update layers when map moved by at least one tile or zoomlevel has changed
       */
      final boolean isDiffX = currentX != _oldX;
      final boolean isDiffY = currentY != _oldY;
      final boolean isDiffScale = currentZoomScale != _oldZoomScale;

      if (isDiffX || isDiffY || isDiffScale || _isCancelWorkerTask) {

//         System.out.println(UI.timeStamp()
//
//               + "  isDiffX:" + isDiffX
//               + "  isDiffY:" + isDiffY
//               + "  isDiffScale:" + isDiffScale
//
//         );
//// TODO remove SYSTEM.OUT.PRINTLN

         /*
          * It took me many days to find this solution that a newly selected tour is
          * displayed after the map position was moved/tilt/rotated. It works but I don't
          * know exacly why.
          */
         if (_isCancelWorkerTask) {

            _isCancelWorkerTask = false;

            _trackCompileWorker.cancel(true);
         }

         /*
          * Submit compile task and repeat .poll() in each frame until it returns not null -> then
          * proceed with new data
          */
         _trackCompileWorker.submit(0);

         _oldX = currentX;
         _oldY = currentY;
         _oldZoomScale = currentZoomScale;
      }

      /*
       * workerTask will not be null after the track is compiled
       */
      final TourCompileTask workerTask = _trackCompileWorker.poll();

      if (workerTask == null) {

         // no further tasks -> nothing to do

         return;
      }

      /*
       * Layer is just compiled (above) in
       * TourTrack_LayerRenderer.TrackCompileWorker.doWork(TourCompileTask)
       */

      // get compile map position: workerTask.__mapPos -> _compileMapPosition
      _compileMapPosition.copy(workerTask.__mapPos);

      final TourTrack_Bucket painterBucket = workerTask.__taskBucketManager.getBucket_Painter();
      _bucketManager_ForPainting.setBucket_Painter(painterBucket);

      // set newly compiled data into the shader
      final boolean isDataAvailable = TourTrack_Shader.bindBufferData(painterBucket, viewport);

      setReady(isDataAvailable);

      /*
       * Keep zoomlevel for the animation, otherwise the old zoomlevel would be used which is
       * causing flickering
       */
      MapPlayerManager.setCompileMapScale(_compileMapPosition.scale);
   }
}

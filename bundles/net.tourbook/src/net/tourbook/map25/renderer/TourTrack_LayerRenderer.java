/**
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016 devemux86
 *
 * This file is part of the OpenScienceMap project (http://www.opensciencemap.org).
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.tourbook.map25.renderer;

import static org.oscim.renderer.MapRenderer.COORD_SCALE;

import net.tourbook.common.color.ColorUtil;
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
 * Base class to use the renderer.elements for drawing.
 * <p/>
 * All methods that modify 'buckets' MUST be synchronized!
 */
public class TourTrack_LayerRenderer extends LayerRenderer {

   public static final Logger         log               = LoggerFactory.getLogger(TourTrack_LayerRenderer.class);

   private static final int           RENDERING_DELAY   = 0;

   /**
    * Use _mapPosition.copy(position) to keep the position for which
    * the Overlay is *compiled*. NOTE: required by setMatrix utility
    * functions to draw this layer fixed to the map
    */
   private MapPosition                _mapPosition;
   private Map                        _map;

   /**
    * Wrap around dateline
    */
   private boolean                    _isFlipOnDateLine = true;

   /**
    * Buckets for rendering
    */
   private final TourTrack_AllBuckets _allLayerBuckets;
   private TourTrack_AllBuckets       _currentTaskRenderBuckets;

   private boolean                    _isUpdateLayer;
   private boolean                    _isUpdatePoints;

   /**
    * Stores points, converted to the map projection.
    */
   private GeoPoint[]                 _allGeoPoints;
   private IntArrayList               _allTourStarts;
   private int[]                      _allGeoPointColors;

   private int                        __oldX            = -1;
   private int                        __oldY            = -1;
   private int                        __oldZoomScale    = -1;

   private TourTrack_Layer            _tourLayer;

   private final Worker               _simpleWorker;

   /**
    * Line style
    */
   private LineStyle                  _lineStyle;

   /*
    * Track config values
    */
   private int _config_LineColorMode;

   private final static class TourRenderTask {

      TourTrack_AllBuckets __allWorkerBuckets = new TourTrack_AllBuckets();
      MapPosition          __mapPos           = new MapPosition();
   }

   final class Worker extends SimpleWorker<TourRenderTask> {

      private static final int  MIN_DIST               = 3;

      /**
       * Visible pixel of a line/tour, all other pixels are clipped with {@link #__lineClipper}
       */
      // limit coords
      private static final int  MAX_VISIBLE_PIXEL      = 2048;

      /**
       * Pre-projected points
       * <p>
       * Is projecting -180°...180° => 0...1 by using the {@link MercatorProjection}
       */
      private double[]          __projectedPoints      = new double[2];

      /**
       * Points which are projected (0...1) and then scaled to pixel
       */
      private float[]           __pixelPoints;

      /**
       * One {@link #__pixelPointColors2} has two {@link #__pixelPoints}, 2 == Half of items
       */
      private int[]             __pixelPointColors2;

      /**
       * Contains the x/y projected pixels where direction arrows are painted
       */
      private FloatArrayList    __pixelDirectionArrows = new FloatArrayList();

      /**
       * Is clipping line positions between
       * <p>
       * - {@link #MAX_VISIBLE_PIXEL} (-2048) ... <br>
       * + {@link #MAX_VISIBLE_PIXEL} (+2048)
       */
      private final LineClipper __lineClipper;

      private int               __numGeoPoints;

      public Worker(final Map map) {

         super(map, 50, new TourRenderTask(), new TourRenderTask());

         __lineClipper = new LineClipper(

               -MAX_VISIBLE_PIXEL,
               -MAX_VISIBLE_PIXEL,
               MAX_VISIBLE_PIXEL,
               MAX_VISIBLE_PIXEL);

         __pixelPoints = new float[0];
         __pixelPointColors2 = new int[0];
         __pixelDirectionArrows.clear();
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
      private int addPoint(final float[] points, int pointIndex, final int x, final int y) {

         points[pointIndex++] = x;
         points[pointIndex++] = y;

         return pointIndex;
      }

      @Override
      public void cleanup(final TourRenderTask task) {

         task.__allWorkerBuckets.clear();
      }

      @Override
      public boolean doWork(final TourRenderTask task) {

         int numGeoPoints = __numGeoPoints;

         if (_isUpdatePoints) {

            synchronized (_allGeoPoints) {

               _isUpdatePoints = false;
               __numGeoPoints = numGeoPoints = _allGeoPoints.length;

               double[] projectedPoints = __projectedPoints;

               if (numGeoPoints * 2 >= projectedPoints.length) {

                  projectedPoints = __projectedPoints = new double[numGeoPoints * 2];

                  __pixelPoints = new float[numGeoPoints * 2];
                  __pixelPointColors2 = new int[numGeoPoints];
                  __pixelDirectionArrows.clear();
               }

               for (int pointIndex = 0; pointIndex < numGeoPoints; pointIndex++) {
                  MercatorProjection.project(_allGeoPoints[pointIndex], projectedPoints, pointIndex);
               }
            }
         }

         _currentTaskRenderBuckets = task.__allWorkerBuckets;

         if (numGeoPoints == 0) {

            if (task.__allWorkerBuckets.get() != null) {

               task.__allWorkerBuckets.clear();

               mMap.render();
            }

            return true;
         }

         doWork_Rendering(task, numGeoPoints);

         // trigger redraw to let renderer fetch the result.
         mMap.render();

         return true;
      }

      private void doWork_Rendering(final TourRenderTask task, final int numPoints) {

         final Map25TrackConfig trackConfig = Map25ConfigManager.getActiveTourTrackConfig();

         final TourTrack_Bucket lineBucket = getTrackBucket(task.__allWorkerBuckets);

         final MapPosition mapPos = task.__mapPos;
         mMap.getMapPosition(mapPos);

         final int zoomlevel = mapPos.zoomLevel;
         mapPos.scale = 1 << zoomlevel;

         // current map positions 0...1
         final double currentMapPosX = mapPos.x; // 0...1, lat == 0 -> 0.5
         final double currentMapPosY = mapPos.y; // 0...1, lon == 0 -> 0.5

         // number of x/y pixels for the whole map at the current zoom level
         final double maxMapPixel = Tile.SIZE * mapPos.scale;
         final int maxMapPixel2 = Tile.SIZE << (zoomlevel - 1);

         // flip around dateline
         int flip = 0;

         int pixelX = (int) ((__projectedPoints[0] - currentMapPosX) * maxMapPixel);
         int pixelY = (int) ((__projectedPoints[1] - currentMapPosY) * maxMapPixel);

         if (pixelX > maxMapPixel2) {

            pixelX -= maxMapPixel2 * 2;
            flip = -1;

         } else if (pixelX < -maxMapPixel2) {

            pixelX += maxMapPixel2 * 2;
            flip = 1;
         }

         // setup first tour index
         int tourIndex = 0;
         int nextTourStartIndex = getNextTourStartIndex(tourIndex);

         // setup tour clipper
         __lineClipper.clipStart(pixelX, pixelY);

         final float[] pixelPoints = __pixelPoints;
         final int[] pixelPointColors2 = __pixelPointColors2;

         __pixelDirectionArrows.clear();
         final FloatArrayList allDirectionArrowPixel = __pixelDirectionArrows;

         // set first point / color / direction arrow
         int pixelPointIndex = addPoint(pixelPoints, 0, pixelX, pixelY);
         pixelPointColors2[0] = _allGeoPointColors[0];
         allDirectionArrowPixel.add(pixelX);
         allDirectionArrowPixel.add(pixelY);

         float prevX = pixelX;
         float prevY = pixelY;
         float prevXArrow = pixelX;
         float prevYArrow = pixelY;

         float[] segment = null;

         for (int projectedPointIndex = 2; projectedPointIndex < numPoints * 2; projectedPointIndex += 2) {

            // convert projected points 0...1 into map pixel
            pixelX = (int) ((__projectedPoints[projectedPointIndex + 0] - currentMapPosX) * maxMapPixel);
            pixelY = (int) ((__projectedPoints[projectedPointIndex + 1] - currentMapPosY) * maxMapPixel);

            int flipDirection = 0;

            if (pixelX > maxMapPixel2) {

               pixelX -= maxMapPixel2 * 2;
               flipDirection = -1;

            } else if (pixelX < -maxMapPixel2) {

               pixelX += maxMapPixel2 * 2;
               flipDirection = 1;
            }

            if (flip != flipDirection) {

               flip = flipDirection;

               if (pixelPointIndex > 2) {
                  lineBucket.addLine(pixelPoints, pixelPointIndex, false, pixelPointColors2);
               }

               __lineClipper.clipStart(pixelX, pixelY);

               pixelPointIndex = addPoint(pixelPoints, 0, pixelX, pixelY);
               pixelPointColors2[0] = _allGeoPointColors[projectedPointIndex / 2];

               continue;
            }

            // ckeck if a new tour starts
            if (projectedPointIndex >= nextTourStartIndex) {

               // finish last tour (copied from flip code)
               if (pixelPointIndex > 2) {
                  lineBucket.addLine(pixelPoints, pixelPointIndex, false, pixelPointColors2);
               }

               // setup next tour
               nextTourStartIndex = getNextTourStartIndex(++tourIndex);

               __lineClipper.clipStart(pixelX, pixelY);
               pixelPointIndex = addPoint(pixelPoints, 0, pixelX, pixelY);
               pixelPointColors2[0] = _allGeoPointColors[projectedPointIndex / 2];

               continue;
            }

            final int clipperCode = __lineClipper.clipNext(pixelX, pixelY);

            if (clipperCode != LineClipper.INSIDE) {

               /*
                * Point is outside clipper
                */

               if (pixelPointIndex > 2) {
                  lineBucket.addLine(pixelPoints, pixelPointIndex, false, pixelPointColors2);
               }

               if (clipperCode == LineClipper.INTERSECTION) {

                  // add line segment
                  segment = __lineClipper.getLine(segment, 0);
                  lineBucket.addLine(segment, 4, false, pixelPointColors2);

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

                  pixelPointColors2[(pixelPointIndex - 1) / 2] = _allGeoPointColors[projectedPointIndex / 2];
               }

               continue;
            }

            /*
             * Point is inside clipper
             */

            final float diffX = pixelX - prevX;
            final float diffY = pixelY - prevY;

            if (pixelPointIndex == 0 || FastMath.absMaxCmp(diffX, diffY, MIN_DIST)) {

               // point > min distance == 3

               pixelPoints[pixelPointIndex++] = prevX = pixelX;
               pixelPoints[pixelPointIndex++] = prevY = pixelY;

               pixelPointColors2[(pixelPointIndex - 1) / 2] = _allGeoPointColors[projectedPointIndex / 2];
            }

            final float diffXArrow = pixelX - prevXArrow;
            final float diffYArrow = pixelY - prevYArrow;

            if (projectedPointIndex == 0 || FastMath.absMaxCmp(diffXArrow, diffYArrow, trackConfig.arrow_MinimumDistance)) {

               // point > min distance

               prevXArrow = pixelX;
               prevYArrow = pixelY;

               allDirectionArrowPixel.add(pixelX);
               allDirectionArrowPixel.add(pixelY);
            }
         }

         if (pixelPointIndex > 2) {
            lineBucket.addLine(pixelPoints, pixelPointIndex, false, pixelPointColors2);
         }

         if (trackConfig.isShowDirectionArrow) {
            lineBucket.createDirectionArrowVertices(__pixelDirectionArrows);
         }
      }

      private int getNextTourStartIndex(final int tourIndex) {

         if (_allTourStarts.size() > tourIndex + 1) {
            return _allTourStarts.get(tourIndex + 1) * 2;
         } else {
            return Integer.MAX_VALUE;
         }
      }

   }

   public TourTrack_LayerRenderer(final TourTrack_Layer tourLayer, final Map map) {

      _tourLayer = tourLayer;
      _map = map;

      _allLayerBuckets = new TourTrack_AllBuckets();
      _mapPosition = new MapPosition();

      _allGeoPoints = new GeoPoint[] {};
      _allTourStarts = new IntArrayList();

      _simpleWorker = new Worker(map);

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
    * @param allTrackBuckets
    * @return
    */
   private TourTrack_Bucket getTrackBucket(final TourTrack_AllBuckets allTrackBuckets) {

      TourTrack_Bucket lineBucket;

      lineBucket = allTrackBuckets.getLineBucket();

// SET_FORMATTING_OFF

      lineBucket.lineStyle       = _lineStyle;
      lineBucket.lineColorMode   = _config_LineColorMode;

// SET_FORMATTING_ON

      return lineBucket;
   }

   public void onModifyConfig(final boolean isVerticesModified) {

      _lineStyle = createLineStyle();

      if (isVerticesModified) {

         // vertices structure is modified -> recreate vertices

         _simpleWorker.submit(RENDERING_DELAY);

      } else {

         // do a fast update

         getTrackBucket(_currentTaskRenderBuckets);

         _map.render();
      }
   }

   /**
    * Render (do OpenGL drawing) all 'buckets'
    */
   @Override
   public synchronized void render(final GLViewport viewport) {

      final MapPosition mapPosition = _mapPosition;

      GLState.test(false, false);
      GLState.blend(true);

      // viewport scale 2 map scale: it is between 1...2
      final float viewport2mapscale = (float) (viewport.pos.scale / mapPosition.scale);

      setMatrix(viewport, true);

      for (TourTrack_Bucket bucket = _allLayerBuckets.get(); bucket != null;) {

         bucket = TourTrack_Shader.paint(bucket, viewport, viewport2mapscale, _allLayerBuckets);
      }
   }

   public void setIsUpdateLayer(final boolean isUpdateLayer) {
      _isUpdateLayer = isUpdateLayer;
   }

   /**
    * Utility: Set matrices.mvp matrix relative to the difference of current
    * MapPosition and the last updated Overlay MapPosition.
    * <p>
    * Use this to 'stick' your layer to the map. Note: Vertex coordinates
    * are assumed to be scaled by MapRenderer.COORD_SCALE (== 8).
    *
    * @param viewport
    *           GLViewport
    * @param isProjected
    *           if true apply view- and projection, or just view otherwise.
    */
   private void setMatrix(final GLViewport viewport,
                          final boolean isProjected) {

      final float coordScale = COORD_SCALE;
      final GLMatrix mvp = viewport.mvp;
      final MapPosition mapPosition = _mapPosition;

      final double tileScale = Tile.SIZE * viewport.pos.scale;

      double x = mapPosition.x - viewport.pos.x;
      final double y = mapPosition.y - viewport.pos.y;

      if (_isFlipOnDateLine) {

         //wrap around date-line
         while (x < 0.5) {
            x += 1.0;
         }

         while (x > 0.5) {
            x -= 1.0;
         }
      }

      mvp.setTransScale(
            (float) (x * tileScale),
            (float) (y * tileScale),
            (float) (viewport.pos.scale / mapPosition.scale) / coordScale);

      mvp.multiplyLhs(isProjected

            ? viewport.viewproj
            : viewport.view);
   }

   public void setPoints(final GeoPoint[] allGeoPoints, final int[] allGeoPointColors, final IntArrayList allTourStarts) {

      synchronized (_allGeoPoints) {

         _allGeoPoints = allGeoPoints;
         _allGeoPointColors = allGeoPointColors;

         _allTourStarts.clear();
         _allTourStarts.addAll(allTourStarts);
      }

      _simpleWorker.cancel(true);

      _isUpdatePoints = true;

      setIsUpdateLayer(true);
   }

   @Override
   public synchronized void update(final GLViewport viewport) {

      if (_tourLayer.isEnabled() == false) {
         return;
      }

      final int currentZoomScale = 1 << viewport.pos.zoomLevel;
      final int currentX = (int) (viewport.pos.x * currentZoomScale);
      final int currentY = (int) (viewport.pos.y * currentZoomScale);

      // update layers when map moved by at least one tile
      if (currentX != __oldX || currentY != __oldY || currentZoomScale != __oldZoomScale || _isUpdateLayer) {

         /*
          * It took me many days to find this solution that a newly selected tour is
          * displayed after the map position was moved/tilt/rotated. It works but I don't
          * know exacly why.
          */
         if (_isUpdateLayer) {
            _simpleWorker.cancel(true);
         }

         _isUpdateLayer = false;

         _simpleWorker.submit(RENDERING_DELAY);

         __oldX = currentX;
         __oldY = currentY;
         __oldZoomScale = currentZoomScale;
      }

      final TourRenderTask workerTask = _simpleWorker.poll();

      if (workerTask == null) {

         // task is done -> nothing to do
         return;
      }

      // keep position to render relative to current state
      _mapPosition.copy(workerTask.__mapPos);

      // compile new layers
      final TourTrack_Bucket workerBucket = workerTask.__allWorkerBuckets.get();
      _allLayerBuckets.set(workerBucket);

      final boolean isOK = _allLayerBuckets.setBufferData();

      setReady(isOK);
   }
}

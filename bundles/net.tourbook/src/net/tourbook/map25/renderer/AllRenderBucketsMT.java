/*
 * Copyright 2012-2014 Hannes Janetzek
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
import static org.oscim.renderer.bucket.RenderBucket.LINE;
import static org.oscim.renderer.bucket.RenderBucket.POLYGON;
import static org.oscim.renderer.bucket.RenderBucket.TEXLINE;

import java.nio.ShortBuffer;

import org.oscim.backend.GL;
import org.oscim.core.Tile;
import org.oscim.layers.tile.MapTile.TileData;
import org.oscim.renderer.BufferObject;
import org.oscim.renderer.MapRenderer;
import org.oscim.theme.styles.LineStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is primarily intended for rendering the vector elements of a
 * MapTile. It can be used for other purposes as well but some optimizations
 * (and limitations) probably wont make sense in different contexts.
 */
public class AllRenderBucketsMT extends TileData {

   static final Logger log = LoggerFactory.getLogger(AllRenderBucketsMT.class);

   /* Count of units needed for one vertex */
   public static final int[] VERTEX_CNT  =

         {
               4,                            // LINE_VERTEX
               6,                            // TEXLINE_VERTEX
               2,                            // POLY_VERTEX
               2,                            // MESH_VERTEX
               4,                            // EXTRUSION_VERTEX
               2,                            // HAIRLINE_VERTEX
               6,                            // SYMBOL
               6,                            // BITMAP
               2,                            // CIRCLE
         };

   public static final int   SHORT_BYTES = 2;
   // public static final int INT_BYTES = 4;

   /**
    * Number of vertices to fill a tile (represented by a quad).
    */
   public static final int TILE_FILL_VERTICES = 4;

   private static short[]  fillShortCoords;

   static {

      final short s = (short) (Tile.SIZE * COORD_SCALE);

      fillShortCoords = new short[] { 0, s, s, s, 0, 0, s, 0 };
   }

   private RenderBucketMT _allBuckets;

   /**
    * VBO holds all vertex data to draw lines and polygons after compilation.
    * <p>
    * Layout:
    * - 16 bytes fill coordinates:
    * {@link #TILE_FILL_VERTICES} * {@link #SHORT_BYTES} * coordsPerVertex
    * - n bytes polygon vertices
    * - m bytes lines vertices
    * ...
    */
   public BufferObject    vbo;

   public BufferObject    ibo;

   /**
    * To not need to switch VertexAttribPointer positions all the time:
    * <p>
    * <li>1. polygons are packed in VBO at offset 0</li>
    * <li>2. lines afterwards at lineOffset</li>
    * <li>3. other buckets keep their byte offset in offset</li>
    */
   public int[]           offset = { 0, 0 };

   private RenderBucketMT _currentBucket;

   public AllRenderBucketsMT() {}

   public static void initRenderer() {

      LineBucketMT.Renderer.init();
      LineTexBucketMT.Renderer.init();

//    PolygonBucket.Renderer.init();
//    TextureBucket.Renderer.init();
//    BitmapBucket.Renderer.init();
//    MeshBucket.Renderer.init();
//    HairLineBucket.Renderer.init();
//    CircleBucket.Renderer.init();
   }

//    public CircleBucket addCircleBucket(final int level, final CircleStyle style) {
//        final CircleBucket l = (CircleBucket) getBucket(level, CIRCLE);
//        if (l == null) {
//         return null;
//      }
//        l.circle = style;
//        return l;
//    }
//
//    public HairLineBucket addHairLineBucket(final int level, final LineStyle style) {
//        final HairLineBucket ll = getHairLineBucket(level);
//        if (ll == null) {
//         return null;
//      }
//        ll.line = style;
//
//        return ll;
//    }

   /**
    * add the LineBucket for a level with a given Line style. Levels are
    * ordered from bottom (0) to top
    */
   public LineBucketMT addLineBucket(final int level, final LineStyle style) {

      final LineBucketMT lineBucket = (LineBucketMT) getBucket(level, LINE);

      if (lineBucket == null) {
         return null;
      }

      // FIXME l.scale = style.width;
      lineBucket.scale = 1;
      lineBucket.line = style;

      return lineBucket;
   }

//    public MeshBucket addMeshBucket(final int level, final AreaStyle style) {
//        final MeshBucket l = (MeshBucket) getBucket(level, MESH);
//        if (l == null) {
//         return null;
//      }
//        l.area = style;
//        return l;
//    }
//
//    public PolygonBucket addPolygonBucket(final int level, final AreaStyle style) {
//        final PolygonBucket l = (PolygonBucket) getBucket(level, POLYGON);
//        if (l == null) {
//         return null;
//      }
//        l.area = style;
//        return l;
//    }

   /**
    * Binds vbo and ibo
    */
   public void bind() {

      if (vbo != null) {
         vbo.bind();
      }

      if (ibo != null) {
         ibo.bind();
      }

   }

   /**
    * cleanup only when buckets are not used by tile or bucket anymore!
    */
   public void clear() {

      /* NB: set null calls clear() on each bucket! */
      set(null);
      _currentBucket = null;

      vbo = BufferObject.release(vbo);
      ibo = BufferObject.release(ibo);
   }

   /**
    * cleanup only when buckets are not used by tile or bucket anymore!
    */
   public void clearBuckets() {

      /* NB: set null calls clear() on each bucket! */
      for (RenderBucketMT l = _allBuckets; l != null; l = l.next) {
         l.clear();
      }

      _currentBucket = null;
   }

   /**
    * Compile different types of buckets in one {@link #vbo VBO}.
    *
    * @param addFill
    *           fill tile (add {@link #TILE_FILL_VERTICES 4} vertices).
    * @return true if compilation succeeded.
    */
   public boolean compile(final boolean addFill) {

      int vboSize = countVboSize();

      if (vboSize <= 0) {

         vbo = BufferObject.release(vbo);
         ibo = BufferObject.release(ibo);

         return false;
      }

      if (addFill) {
         vboSize += TILE_FILL_VERTICES * 2;
      }

      final ShortBuffer vboData = MapRenderer.getShortBuffer(vboSize);

      if (addFill) {
         vboData.put(fillShortCoords, 0, TILE_FILL_VERTICES * 2);
      }

      ShortBuffer iboData = null;

      final int iboSize = countIboSize();
      if (iboSize > 0) {
         iboData = MapRenderer.getShortBuffer(iboSize);
      }

      int vertexOffset = addFill ? TILE_FILL_VERTICES : 0;

      /*
       * Compile polygons
       */
      for (RenderBucketMT oneBucket = _allBuckets; oneBucket != null; oneBucket = oneBucket.next) {

         if (oneBucket.type == POLYGON) {

            oneBucket.compile(vboData, iboData);
            oneBucket.vertexOffset = vertexOffset;

            vertexOffset += oneBucket.numVertices;
         }
      }

      /*
       * Compile lines
       */
      offset[LINE] = vboData.position() * SHORT_BYTES;
      vertexOffset = 0;
      for (RenderBucketMT oneBucket = _allBuckets; oneBucket != null; oneBucket = oneBucket.next) {

         if (oneBucket.type == LINE) {

            oneBucket.compile(vboData, iboData);
            oneBucket.vertexOffset = vertexOffset;

            vertexOffset += oneBucket.numVertices;
         }
      }

      /*
       * Compile others
       */
      for (RenderBucketMT oneBucket = _allBuckets; oneBucket != null; oneBucket = oneBucket.next) {

         if (oneBucket.type != LINE && oneBucket.type != POLYGON) {
            oneBucket.compile(vboData, iboData);
         }
      }

      if (vboSize != vboData.position()) {

         log.debug("wrong vertex buffer size: "
               + " new size: " + vboSize
               + " buffer pos: " + vboData.position()
               + " buffer limit: " + vboData.limit()
               + " buffer fill: " + vboData.remaining());

         return false;
      }

      if (iboSize > 0 && iboSize != iboData.position()) {

         log.debug("wrong indice buffer size: "
               + " new size: " + iboSize
               + " buffer pos: " + iboData.position()
               + " buffer limit: " + iboData.limit()
               + " buffer fill: " + iboData.remaining());

         return false;
      }

      if (vbo == null) {
         vbo = BufferObject.get(GL.ARRAY_BUFFER, vboSize);
      }

      // Set VBO data to READ mode
      vbo.loadBufferData(vboData.flip(), vboSize * SHORT_BYTES);

      if (iboSize > 0) {

         if (ibo == null) {
            ibo = BufferObject.get(GL.ELEMENT_ARRAY_BUFFER, iboSize);
         }

         // Set IBO data to READ mode
         ibo.loadBufferData(iboData.flip(), iboSize * SHORT_BYTES);
      }

      return true;
   }

   private int countIboSize() {
      int numIndices = 0;

      for (RenderBucketMT l = _allBuckets; l != null; l = l.next) {
         numIndices += l.numIndices;
      }

      return numIndices;
   }

   private int countVboSize() {
      int vboSize = 0;

      for (RenderBucketMT l = _allBuckets; l != null; l = l.next) {
         vboSize += l.numVertices * VERTEX_CNT[l.type];
      }

      return vboSize;
   }

   @Override
   protected void dispose() {
      clear();
   }

   /**
    * @return internal linked list of RenderBucket items
    */
   public RenderBucketMT get() {
      return _allBuckets;
   }

   private RenderBucketMT getBucket(final int level, final int type) {

      RenderBucketMT bucket = null;

      if (_currentBucket != null && _currentBucket.level == level) {

         bucket = _currentBucket;

         if (bucket.type != type) {
            log.error("BUG wrong bucket {} {} on level {}", bucket.type, type, level);
            throw new IllegalArgumentException();
         }

         return bucket;
      }

      RenderBucketMT b = _allBuckets;
      if (b == null || b.level > level) {
         /* insert new bucket at start */
         b = null;
      } else {
         if (_currentBucket != null && level > _currentBucket.level) {
            b = _currentBucket;
         }

         while (true) {

            /* found bucket */
            if (b.level == level) {
               bucket = b;
               break;
            }

            /* insert bucket between current and next bucket */
            if (b.next == null || b.next.level > level) {
               break;
            }

            b = b.next;
         }
      }

      if (bucket == null) {

         // add a new RenderElement
         if (type == LINE) {

            bucket = new LineBucketMT(level);

//       } else if (type == POLYGON) {
//          bucket = new PolygonBucket(level);

         } else if (type == TEXLINE) {
            bucket = new LineTexBucketMT(level);

//       } else if (type == MESH) {
//          bucket = new MeshBucket(level);
//       } else if (type == HAIRLINE) {
//          bucket = new HairLineBucket(level);
//       } else if (type == CIRCLE) {
//          bucket = new CircleBucket(level);
         }

         if (bucket == null) {
            throw new IllegalArgumentException();
         }

         if (b == null) {

            /** insert at start */
            bucket.next = _allBuckets;
            _allBuckets = bucket;

         } else {

            bucket.next = b.next;
            b.next = bucket;
         }
      }

      /* check if found buckets matches requested type */
      if (bucket.type != type) {
         log.error("BUG wrong bucket {} {} on level {}", bucket.type, type, level);
         throw new IllegalArgumentException();
      }

      _currentBucket = bucket;

      return bucket;
   }

//    /**
//     * Get or add the CircleBucket for a level. Levels are ordered from
//     * bottom (0) to top
//     */
//    public CircleBucket getCircleBucket(final int level) {
//        return (CircleBucket) getBucket(level, CIRCLE);
//    }
//
//    /**
//     * Get or add the TexLineBucket for a level. Levels are ordered from
//     * bottom (0) to top
//     */
//    public HairLineBucket getHairLineBucket(final int level) {
//        return (HairLineBucket) getBucket(level, HAIRLINE);
//    }

   /**
    * Get or add the LineBucket for a level. Levels are ordered from
    * bottom (0) to top
    */
   public LineBucketMT getLineBucket(final int level) {
      return (LineBucketMT) getBucket(level, LINE);
   }

   /**
    * Get or add the TexLineBucket for a level. Levels are ordered from
    * bottom (0) to top
    */
   public LineTexBucketMT getLineTexBucket(final int level) {
      return (LineTexBucketMT) getBucket(level, TEXLINE);
   }
//
//    /**
//     * Get or add the MeshBucket for a level. Levels are ordered from
//     * bottom (0) to top
//     */
//    public MeshBucket getMeshBucket(final int level) {
//        return (MeshBucket) getBucket(level, MESH);
//    }
//
//    /**
//     * Get or add the PolygonBucket for a level. Levels are ordered from
//     * bottom (0) to top
//     */
//    public PolygonBucket getPolygonBucket(final int level) {
//        return (PolygonBucket) getBucket(level, POLYGON);
//    }

   public void prepare() {
      for (RenderBucketMT l = _allBuckets; l != null; l = l.next) {
         l.prepare();
      }
   }

   /**
    * Set new bucket items and clear previous.
    */
   public void set(final RenderBucketMT buckets) {
      for (RenderBucketMT l = this._allBuckets; l != null; l = l.next) {
         l.clear();
      }

      this._allBuckets = buckets;
   }

   public void setFrom(final AllRenderBucketsMT allBuckets) {

      if (allBuckets == this) {
         throw new IllegalArgumentException("Cannot set from oneself!");
      }

      set(allBuckets._allBuckets);

      _currentBucket = null;
      allBuckets._allBuckets = null;
      allBuckets._currentBucket = null;
   }
}

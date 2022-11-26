/*******************************************************************************
 * Copyright (C) 2005, 2022 Wolfgang Schramm and Contributors
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

import org.oscim.core.MapPosition;
import org.oscim.event.Event;
import org.oscim.layers.Layer;
import org.oscim.map.Map;

/**
 * Layer for glTF models, original source {@link org.oscim.gdx.poi3d.GdxModelLayer}
 */
public class GLTFModelLayer extends Layer implements Map.UpdateListener {

   private GLTFModelRenderer _gltfRenderer;

   public GLTFModelLayer(final Map map) {

      super(map);

      mRenderer = _gltfRenderer = new GLTFModelRenderer(mMap);
   }

   public void dispose() {

      _gltfRenderer.dispose();
   }

   @Override
   public void onMapEvent(final Event event, final MapPosition mapPosition) {

      _gltfRenderer.setMapPosition(mapPosition);
   }

}

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
package net.tourbook.map.player;

import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.ShortArrayList;
import org.oscim.core.GeoPoint;

/**
 * Data for the map animation player
 */
public class MapPlayerData {

   public boolean        isPlayerEnabled;

   /**
    * When <code>true</code> then proceed the animation from the current relative position,
    * otherwise start animation from the beginning
    */
   public boolean        isAnimateFromRelativePosition;

   public ShortArrayList animatedPositions;

   public GeoPoint[]     animatedGeoPoints;

   public IntArrayList   animatedLocationIndices;

}

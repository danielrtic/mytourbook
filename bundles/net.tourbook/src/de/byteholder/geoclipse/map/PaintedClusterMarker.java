/*******************************************************************************
 * Copyright (C) 2024 Wolfgang Schramm and Contributors
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
package de.byteholder.geoclipse.map;

import net.tourbook.common.UI;
import net.tourbook.map2.view.Map2Marker;

import org.eclipse.swt.graphics.Rectangle;

import particlelabeling.PointFeature;

public class PaintedClusterMarker {

   private static final char NL = UI.NEW_LINE;

   public PointFeature       distributedLabel;

   /**
    * Rectangle of the painted marker label
    */
   public Rectangle          markerLabelRectangle;

   public Map2Marker         mapMarker;

   public PaintedClusterMarker(final PointFeature pointFeature, final Rectangle markerLabelRectangle) {

      this.distributedLabel = pointFeature;
      this.markerLabelRectangle = markerLabelRectangle;

      if (pointFeature.data instanceof final Map2Marker mapMarker) {

         this.mapMarker = mapMarker;
      }
   }

   @Override
   public String toString() {

      return UI.EMPTY_STRING

            + "PaintedClusterMarker" + NL //                         //$NON-NLS-1$

//            + " clusterLabel     = " + clusterLabel + NL //           //$NON-NLS-1$

      ;
   }
}

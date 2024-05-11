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
import net.tourbook.data.TourMarker;

import org.eclipse.swt.graphics.Rectangle;

public class PaintedMarker {

   private static final char NL = UI.NEW_LINE;

   public TourMarker         tourMarker;
   public Rectangle          markerRectangle;

   public PaintedMarker(final TourMarker marker, final Rectangle rectangle) {

      tourMarker = marker;
      markerRectangle = rectangle;
   }

   @Override
   public String toString() {

      return UI.EMPTY_STRING

            + "PaintedMarker" + NL //                                  //$NON-NLS-1$

            + " markerRectangle = " + markerRectangle + NL //   //$NON-NLS-1$

      ;
   }
}

/*******************************************************************************
 * Copyright (C) 2018, 2023 Wolfgang Schramm and Contributors
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
package net.tourbook.ui.views.geoCompare;

import java.time.ZonedDateTime;

import net.tourbook.common.UI;
import net.tourbook.data.TourType;

/**
 * Contains data for one compared tour
 */
public class GeoComparedTour {

   public long        tourId;

   public GeoPartData geoPartData;

   /*
    * Compare results
    */
   public float[] tourLatLonDiff;

   public int     tourFirstIndex;
   public int     tourLastIndex;

   /**
    * <ul>
    * <li>-2 : Value is not yet set</li>
    * <li>-1 : Value is invalid</li>
    * <li>0...max : A Valid value is set</li>
    * </ul>
    */
   long           minDiffValue = -2;

   float          avgPulse;
   float          avgPace;
   float          avgSpeed;
   double         avgAltimeter;

   ZonedDateTime  tourStartTime;
   long           tourStartTimeMS;

   int            elapsedTime;
   long           recordedTime;
   long           movingTime;

   float          distance;
   float          elevationGain;
   float          elevationLoss;

   /**
    * Ensure title it is set for sorting
    */
   String         tourTitle    = UI.EMPTY_STRING;
   TourType       tourType;

   public GeoComparedTour(final long tourId, final GeoPartData geoPartItem) {

      this.tourId = tourId;
      this.geoPartData = geoPartItem;
   }

   @Override
   public String toString() {
      return "GeoPartComparerItem [" //$NON-NLS-1$
            + "tourId=" + tourId + ", " //$NON-NLS-1$ //$NON-NLS-2$
            + "geoPartItem=" + geoPartData + "]"; //$NON-NLS-1$ //$NON-NLS-2$
   }

}
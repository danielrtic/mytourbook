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
package net.tourbook.map2.view;

import java.util.ArrayList;
import java.util.Set;

import net.tourbook.common.color.IMapColorProvider;
import net.tourbook.common.map.GeoPosition;
import net.tourbook.data.TourData;
import net.tourbook.photo.Photo;
import net.tourbook.tour.filter.TourFilterFieldOperator;

/**
 * Contains data which are needed to paint a tour into the 2D map.
 */
public class TourPainterConfiguration {

   private final static ArrayList<TourData> _allTourData = new ArrayList<>();
   private final static ArrayList<Photo>    _allPhotos   = new ArrayList<>();

   /**
    * contains the upper left and lower right position for a tour
    */
   private static Set<GeoPosition>          _tourBounds;

   private static int                       _zoomLevelAdjustment;

   private static IMapColorProvider         _mapColorProvider;

   static boolean                           isBackgroundDark;

   public static boolean                    isShowLocationsAndMarkers;
   static boolean                           isShowPhotos;
   static boolean                           isShowTours;
   static public boolean                    isShowTourMarker;
   static boolean                           isShowTourPauses;
   static boolean                           isShowTourStartEnd;
   static boolean                           isShowWayPoints;

   /**
    * Is <code>true</code> when a link photo is displayed, otherwise a tour photo (photo which is
    * save in a tour) is displayed.
    */
   static boolean                           isLinkPhotoDisplayed;

   static boolean                           isFilterTourPauses;
   static boolean                           isFilterPauseDuration;
   static boolean                           isShowAutoPauses;
   static boolean                           isShowUserPauses;
   static long                              pauseDuration;
   static Enum<TourFilterFieldOperator>     pauseDurationOperator;

   static boolean                           isShowBreadcrumbs;

   private TourPainterConfiguration() {}

   public static IMapColorProvider getMapColorProvider() {
      return _mapColorProvider;
   }

   public static ArrayList<Photo> getPhotos() {
      return _allPhotos;
   }

   /**
    * @return Returns the tour bounds or <code>null</code> when a tour is not set
    */
   public static Set<GeoPosition> getTourBounds() {
      return _tourBounds;
   }

   /**
    * @return Returns the current {@link TourData} which is selected in a view or editor
    */
   public static ArrayList<TourData> getTourData() {
      return _allTourData;
   }

   public static int getZoomLevelAdjustment() {
      return _zoomLevelAdjustment;
   }

   /**
    * Do not draw a tour
    *
    * @param tourData
    */
   public static void resetTourData() {

      _allTourData.clear();
      _allTourData.add(null);
   }

   public static void setMapColorProvider(final IMapColorProvider mapColorProvider) {
      if (mapColorProvider != null) {
         _mapColorProvider = mapColorProvider;
      }
   }

   /**
    * @param allPhotos
    *           When <code>null</code>, photos are not displayed.
    * @param isShowPhoto
    */
   public static void setPhotos(final ArrayList<Photo> allPhotos, final boolean isShowPhoto, final boolean isLinkPhoto) {

      _allPhotos.clear();

      if (allPhotos != null) {
         _allPhotos.addAll(allPhotos);
      }

      isShowPhotos = isShowPhoto && _allPhotos.size() > 0;

      isLinkPhotoDisplayed = isLinkPhoto;
   }

   public static void setTourBounds(final Set<GeoPosition> mapPositions) {
      _tourBounds = mapPositions;
   }

   /**
    * Sets {@link TourData} for all tours which are displayed
    *
    * @param tourDataList
    * @param isShowTour
    */
   public static void setTourData(final ArrayList<TourData> tourDataList, final boolean isShowTour) {

      _allTourData.clear();

      if (tourDataList != null) {
         _allTourData.addAll(tourDataList);
      }

      isShowTours = isShowTour && _allTourData.size() > 0;
   }

   /**
    * Set {@link TourData} which is used for the next painting or <code>null</code> to not draw the
    * tour
    *
    * @param tourData
    * @param isShowTour
    */
   public static void setTourData(final TourData tourData, final boolean isShowTour) {

      _allTourData.clear();
      _allTourData.add(tourData);

      isShowTours = isShowTour && _allTourData.size() > 0;
   }

   public static void setZoomLevelAdjustment(final int zoomLevel) {
      _zoomLevelAdjustment = zoomLevel;
   }
}

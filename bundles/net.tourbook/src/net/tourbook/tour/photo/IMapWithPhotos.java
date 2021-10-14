/*******************************************************************************
 * Copyright (C) 2021 Wolfgang Schramm and Contributors
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
package net.tourbook.tour.photo;

import java.util.List;

import net.tourbook.photo.Photo;
import net.tourbook.photo.PhotoRatingStarOperator;

/**
 * Map with photos
 */
public interface IMapWithPhotos {

   /**
    * @return Returns a list with all filtered photos
    */
   public List<Photo> getFilteredPhotos();

   /**
    * @return Returns a list with all available photos.
    */
   public List<Photo> getPhotos();

   /**
    * Update filtered photos in the map
    *
    * @param selectedRatingStars
    * @param selectedRatingStarOperator
    */
   public void updatePhotoFilter(int selectedRatingStars, PhotoRatingStarOperator selectedRatingStarOperator);

}
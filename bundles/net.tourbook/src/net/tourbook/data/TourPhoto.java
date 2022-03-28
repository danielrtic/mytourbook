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
package net.tourbook.data;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import net.tourbook.common.UI;
import net.tourbook.database.TourDatabase;
import net.tourbook.photo.Photo;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * Contains a photo for a tour.
 *
 * @since 12.12
 */
@Entity
public class TourPhoto {

   private static final char          NL                  = UI.NEW_LINE;

   public static final int            DB_LENGTH_FILE_PATH = 260;

   /**
    * Manually created marker or imported marker create a unique id to identify them, saved marker
    * are compared with the marker id
    */
   private static final AtomicInteger _createCounter      = new AtomicInteger();

   /**
    * Unique id for the {@link TourPhoto} entity
    */
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private long                       photoId             = TourDatabase.ENTITY_IS_NOT_SAVED;

   /**
    * Image filename WITH extension.
    */
   private String                     imageFileName;

   /**
    * Image file extension.
    */
   private String                     imageFileExt;

   /**
    * Image file path without filename.
    */
   private String                     imageFilePath;

   /**
    * Full image file name with path.
    */
   private String                     imageFilePathName;

   /**
    * Exif time in milliseconds, when not available, the last modified time of the image file is
    * used.
    */
   private long                       imageExifTime;

   /**
    * Last modified in GMT
    */
   private long                       imageFileLastModified;

   /**
    *
    */
   private long                       adjustedTime;

   /**
    * <code>0</code> geo position is from a tour<br>
    * <code>1</code> geo position is from a photo, from the EXIF data within the photo file<br>
    */
   private int                        isGeoFromPhoto;

   /**
    * Rating for a photo, stars can be set from 0 to 5, 0 is no rating.
    */
   private int                        ratingStars;
   /**
    * Double.MIN_VALUE cannot be used, it cannot be saved in the database. 0 is the value when the
    * value is <b>NOT</b> set !!!
    */
   private double                     latitude            = 0;

   private double                     longitude           = 0;

   @ManyToOne(optional = false)
   private TourData                   tourData;

   /**
    * Unique id for manually created markers because the {@link #photoId} is 0 when the marker is
    * not persisted
    */
   @Transient
   private long                       _createId           = 0;

   // constructor is required for hibernate
   public TourPhoto() {}

   /**
    * Used for MT import/export
    *
    * @param tourData
    */
   public TourPhoto(final TourData tourData) {

      _createId = _createCounter.incrementAndGet();

      this.tourData = tourData;
   }

   /**
    * Create a tour photo from a gallery photo
    *
    * @param tourData
    * @param galleryPhoto
    */
   public TourPhoto(final TourData tourData, final Photo galleryPhoto) {

      _createId = _createCounter.incrementAndGet();

      this.tourData = tourData;

      final File imageFile = galleryPhoto.imageFile;

      final String filePathName = imageFile.getAbsolutePath();

      setFilePathName(filePathName);

      imageFileLastModified = imageFile.lastModified();
      imageExifTime = galleryPhoto.imageExifTime;

      if (galleryPhoto.isGeoFromExif) {
         setIsGeoFromPhoto();
      } else {
         setIsGeoFromTour();
      }

//      final long photoAdjustedTimeLink = photo.adjustedTimeLink;
//      adjustedTime = photoAdjustedTimeLink == Long.MIN_VALUE ? imageExifTime : photoAdjustedTimeLink;
//
//      latitude = photo.getLatitude();
//      longitude = photo.getLongitude();
   }

   @Override
   public boolean equals(final Object obj) {

      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (!(obj instanceof TourPhoto)) {
         return false;
      }

      final TourPhoto other = (TourPhoto) obj;

      if (_createId == 0) {

         // photo is from the database
         if (photoId != other.photoId) {
            return false;
         }
      } else {

         // photo was create
         if (_createId != other._createId) {
            return false;
         }
      }

      return true;
   }

   public long getAdjustedTime() {
      return adjustedTime;
   }

   /**
    * @return Returns EXIF time in milliseconds, when not available, the last modified time of the
    *         image file is used.
    */
   public long getImageExifTime() {
      return imageExifTime;
   }

   public String getImageFileExt() {
      return imageFileExt;
   }

   public long getImageFileLastModified() {
      return imageFileLastModified;
   }

   public String getImageFileName() {
      return imageFileName;
   }

   public String getImageFilePath() {
      return imageFilePath;
   }

   /**
    * @return Returns the full filepathname
    */
   public String getImageFilePathName() {
      return imageFilePathName;
   }

   /**
    * Used for MT import/export
    *
    * @return
    */
   public int getIsGeoFromPhoto() {
      return isGeoFromPhoto;
   }

   /**
    * @return Returns 0 when latitude is <b>NOT</b> set.
    */
   public double getLatitude() {
      return latitude;
   }

   public double getLongitude() {
      return longitude;
   }

   public long getPhotoId() {
      return photoId;
   }

   public int getRatingStars() {
      return ratingStars;
   }

   public Long getTourId() {

      return tourData.getTourId();
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (int) (_createId ^ (_createId >>> 32));
      result = prime * result + (int) (photoId ^ (photoId >>> 32));
      return result;
   }

   /**
    * @return Returns <code>true</code> when geo coordinated are save in the photo image.
    */
   public boolean isGeoFromExif() {
      return isGeoFromPhoto == 1;
   }

   public boolean isGeoFromTour() {
      return isGeoFromPhoto == 0;
   }

   public void setAdjustedTime(final long adjustedTime) {
      this.adjustedTime = adjustedTime;
   }

   public void setFilePathName(final String filePathName) {

      final IPath filePath = new Path(filePathName);

      final String fileExtension = filePath.getFileExtension();

      imageFileName = filePath.lastSegment();
      imageFileExt = fileExtension == null ? UI.EMPTY_STRING : fileExtension;
      imageFilePath = filePath.removeLastSegments(1).toOSString();
      imageFilePathName = filePathName;
   }

   public void setGeoLocation(final double latitude, final double longitude) {
      this.latitude = latitude;
      this.longitude = longitude;
   }

   /**
    * Used for MT import/export
    *
    * @param imageExifTime
    */
   public void setImageExifTime(final long imageExifTime) {
      this.imageExifTime = imageExifTime;
   }

   /**
    * Used for MT import/export
    *
    * @param imageFileLastModified
    */
   public void setImageFileLastModified(final long imageFileLastModified) {
      this.imageFileLastModified = imageFileLastModified;
   }

   /**
    * Used for MT import/export
    *
    * @param fromFlag
    */
   public void setIsGeoFrom(final int fromFlag) {
      isGeoFromPhoto = fromFlag;
   }

   public void setIsGeoFromPhoto() {
      isGeoFromPhoto = 1;
   }

   public void setIsGeoFromTour() {
      isGeoFromPhoto = 0;
   }

   /**
    * Used for MT import/export
    *
    * @param latitude
    */
   public void setLatitude(final double latitude) {
      this.latitude = latitude;
   }

   /**
    * Used for MT import/export
    *
    * @param longitude
    */
   public void setLongitude(final double longitude) {
      this.longitude = longitude;
   }

   public void setRatingStars(final int ratingStars) {
      this.ratingStars = ratingStars;
   }

   /**
    * This method is called in the "Tour Data" view !!!
    */
   @Override
   public String toString() {

      return UI.EMPTY_STRING

            + "TourPhoto" + NL //                                             //$NON-NLS-1$

            + "[" + NL //                                                     //$NON-NLS-1$

            + "   photoId                 =" + photoId + NL //                //$NON-NLS-1$
            + "   imageFileName           =" + imageFileName + NL //          //$NON-NLS-1$
            + "   imageFileExt            =" + imageFileExt + NL //           //$NON-NLS-1$
            + "   imageFilePath           =" + imageFilePath + NL //          //$NON-NLS-1$
            + "   imageFilePathName       =" + imageFilePathName + NL //      //$NON-NLS-1$
            + "   imageExifTime           =" + imageExifTime + NL //          //$NON-NLS-1$
            + "   imageFileLastModified   =" + imageFileLastModified + NL //  //$NON-NLS-1$
            + "   adjustedTime            =" + adjustedTime + NL //           //$NON-NLS-1$
            + "   isGeoFromPhoto          =" + isGeoFromPhoto + NL //         //$NON-NLS-1$
            + "   ratingStars             =" + ratingStars + NL //            //$NON-NLS-1$
            + "   latitude                =" + latitude + NL //               //$NON-NLS-1$
            + "   longitude               =" + longitude + NL //              //$NON-NLS-1$

            + "   _createId               =" + _createId + NL //              //$NON-NLS-1$

//          + "   tourData                =" + tourData + NL //               //$NON-NLS-1$

            + "]" + NL //                                                     //$NON-NLS-1$
      ;
   }

}

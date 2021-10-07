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
package net.tourbook.data;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import net.tourbook.Messages;
import net.tourbook.common.UI;
import net.tourbook.database.FIELD_VALIDATION;
import net.tourbook.database.TourDatabase;

/**
 */
@Entity
public class DeviceSensor implements Cloneable {

   private static final char    NL                    = UI.NEW_LINE;

   public static final int      DB_LENGTH_NAME        = 80;
   public static final int      DB_LENGTH_DESCRIPTION = 32000;

   /**
    * Create a unique id to identify imported sensors
    */
   private static AtomicInteger _createCounter        = new AtomicInteger();

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private long                 sensorId              = TourDatabase.ENTITY_IS_NOT_SAVED;

   /**
    * Contains a customer name because the manufacturer and product name are sometimes cryptic
    */
   private String               sensorName;

   private String               manufacturerName;
   private int                  manufacturerNumber;

   private String               productName;
   private int                  productNumber;

   private String               description;
   private String               serialNumber          = UI.EMPTY_STRING;

   /**
    * Time in ms when this sensor was first used
    */
   @Transient
   private long                 usedStartTime;

   /**
    * Time in ms when this sensor was last used
    */
   @Transient
   private long                 usedEndTime;

   @Transient
   private long                 _createId             = 0;

   /**
    * Default constructor used in EJB
    */
   public DeviceSensor() {}

   public DeviceSensor(final int manufacturerNumber,
                       final String manufacturerName,

                       final int productNumber,
                       final String productName,

                       final String serialNumber) {

      _createId = _createCounter.incrementAndGet();

      this.manufacturerNumber = manufacturerNumber;
      this.manufacturerName = manufacturerName;

      this.productNumber = productNumber;
      this.productName = productName;

      this.serialNumber = serialNumber;
   }

   @Override
   public DeviceSensor clone() {

      DeviceSensor newSensor = null;

      try {
         newSensor = (DeviceSensor) super.clone();
      } catch (final CloneNotSupportedException e) {
         e.printStackTrace();
      }

      return newSensor;
   }

   @Override
   public boolean equals(final Object obj) {

      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (!(obj instanceof DeviceSensor)) {
         return false;
      }

      final DeviceSensor other = (DeviceSensor) obj;

      if (_createId == 0) {

         // sensor is from the database
         if (sensorId != other.sensorId) {
            return false;
         }

      } else {

         // sensor is create
         if (_createId != other._createId) {
            return false;
         }
      }

      return true;
   }

   public boolean equals1(final Object obj) {

      if (this == obj) {
         return true;
      }

      if (obj == null) {
         return false;
      }

      if (getClass() != obj.getClass()) {
         return false;
      }

      final DeviceSensor other = (DeviceSensor) obj;

      return sensorId == other.sensorId;
   }

   public String getDescription() {

      if (description == null) {
         return UI.EMPTY_STRING;
      }

      return description;
   }

   public String getManufacturerName() {
      return manufacturerName;
   }

   public int getManufacturerNumber() {
      return manufacturerNumber;
   }

   public String getProductName() {
      return productName;
   }

   public int getProductNumber() {
      return productNumber;
   }

   /**
    * @return Returns the primary key for a {@link DeviceSensor} entity
    */
   public long getSensorId() {
      return sensorId;
   }

   public String getSensorName() {

      if (sensorName == null) {
         return UI.EMPTY_STRING;
      }

      return sensorName;
   }

   public String getSerialNumber() {
      return serialNumber;
   }

   public long getUsedEndTime() {
      return usedEndTime;
   }

   public long getUsedStartTime() {
      return usedStartTime;
   }

   @Override
   public int hashCode() {

      return Objects.hash(sensorId, _createId);
   }

   /**
    * Checks if VARCHAR fields have the correct length
    *
    * @return Returns <code>true</code> when the data are valid and can be saved
    */
   public boolean isValidForSave() {

      FIELD_VALIDATION fieldValidation;

      /*
       * Check: Name
       */
      fieldValidation = TourDatabase.isFieldValidForSave(
            sensorName,
            DB_LENGTH_NAME,
            Messages.Db_Field_SensorName);

      if (fieldValidation == FIELD_VALIDATION.IS_INVALID) {
         return false;
      } else if (fieldValidation == FIELD_VALIDATION.TRUNCATE) {
         sensorName = sensorName.substring(0, DB_LENGTH_NAME);
      }

      /*
       * Check: Description
       */
      fieldValidation = TourDatabase.isFieldValidForSave(
            description,
            DB_LENGTH_DESCRIPTION,
            Messages.Db_Field_SensorDescription);

      if (fieldValidation == FIELD_VALIDATION.IS_INVALID) {
         return false;
      } else if (fieldValidation == FIELD_VALIDATION.TRUNCATE) {
         description = description.substring(0, DB_LENGTH_DESCRIPTION);
      }

      return true;
   }

   public void setDescription(final String description) {
      this.description = description;
   }

   public void setSensorName(final String label) {
      this.sensorName = label;
   }

   public void setSerialNumber(final String serialNumber) {
      this.serialNumber = serialNumber;
   }

   public void setUsedEndTime(final long usedEndTime) {
      this.usedEndTime = usedEndTime;
   }

   public void setUsedStartTime(final long usedStartTime) {
      this.usedStartTime = usedStartTime;
   }

   @Override
   public String toString() {

      return "DeviceSensor" + NL //                                     //$NON-NLS-1$

            + "[" + NL //                                               //$NON-NLS-1$

            + "sensorName           = " + sensorName + NL //            //$NON-NLS-1$
            + "sensorId             = " + sensorId + NL //              //$NON-NLS-1$
            + "manufacturerNumber   = " + manufacturerNumber + NL //    //$NON-NLS-1$
            + "manufacturerName     = " + manufacturerName + NL //      //$NON-NLS-1$
            + "productNumber        = " + productNumber + NL //         //$NON-NLS-1$
            + "productName          = " + productName + NL //           //$NON-NLS-1$
            + "serialNumber         = " + serialNumber + NL //          //$NON-NLS-1$

            + "]" + NL; //                                              //$NON-NLS-1$
   }

   /**
    * Updates values from a modified {@link DeviceSensor}
    *
    * @param modifiedSensor
    */
   public void updateFromModified(final DeviceSensor modifiedSensor) {

      sensorName = modifiedSensor.sensorName;
      description = modifiedSensor.description;
   }
}

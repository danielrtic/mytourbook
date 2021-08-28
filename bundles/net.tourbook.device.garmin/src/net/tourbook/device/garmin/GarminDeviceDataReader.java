/*******************************************************************************
 * Copyright (C) 2005, 2021 Wolfgang Schramm and Contributors
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
package net.tourbook.device.garmin;

import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import net.tourbook.common.util.StatusUtil;
import net.tourbook.data.TourData;
import net.tourbook.importdata.DeviceData;
import net.tourbook.importdata.ImportStates;
import net.tourbook.importdata.SerialParameters;
import net.tourbook.importdata.TourbookDevice;
import net.tourbook.ui.UI;

public class GarminDeviceDataReader extends TourbookDevice {

   private static final String XML_GARMIN_TAG = "<TrainingCenterDatabase"; //$NON-NLS-1$

   public GarminDeviceDataReader() {
      // plugin constructor
   }

   @Override
   public String buildFileNameFromRawData(final String rawDataFileName) {
      // NEXT Auto-generated method stub
      return null;
   }

   @Override
   public boolean checkStartSequence(final int byteIndex, final int newByte) {
      return true;
   }

   @Override
   public String getDeviceModeName(final int profileId) {
      return UI.EMPTY_STRING;
   }

   @Override
   public SerialParameters getPortParameters(final String portName) {
      return null;
   }

   @Override
   public int getStartSequenceSize() {
      return 0;
   }

   @Override
   public int getTransferDataSize() {
      return -1;
   }

   @Override
   public boolean processDeviceData(final String importFilePath,
                                    final DeviceData deviceData,
                                    final Map<Long, TourData> alreadyImportedTours,
                                    final Map<Long, TourData> newlyImportedTours,
                                    final ImportStates importStates) {

      if (isValidXMLFile(importFilePath, XML_GARMIN_TAG) == false) {
         return false;
      }

      final GarminSAXHandler saxHandler = new GarminSAXHandler(
            this,
            importFilePath,
            deviceData,
            alreadyImportedTours,
            newlyImportedTours);

      try {

         final SAXParser parser = SAXParserFactory.newInstance().newSAXParser();

         parser.parse("file:" + importFilePath, saxHandler);//$NON-NLS-1$

      } catch (final Exception e) {

         StatusUtil.log("Error parsing file: " + importFilePath, e); //$NON-NLS-1$
         return false;

      } finally {
         saxHandler.dispose();
      }

      return saxHandler.isImported();
   }

   @Override
   public boolean validateRawData(final String fileName) {
      return isValidXMLFile(fileName, XML_GARMIN_TAG);
   }
}

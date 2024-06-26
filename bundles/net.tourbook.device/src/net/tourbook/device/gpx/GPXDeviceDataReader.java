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
package net.tourbook.device.gpx;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Map;

import javax.xml.parsers.SAXParser;

import net.tourbook.common.UI;
import net.tourbook.common.util.StatusUtil;
import net.tourbook.common.util.Util;
import net.tourbook.common.util.XmlUtils;
import net.tourbook.data.TourData;
import net.tourbook.importdata.DeviceData;
import net.tourbook.importdata.ImportState_File;
import net.tourbook.importdata.ImportState_Process;
import net.tourbook.importdata.SerialParameters;
import net.tourbook.importdata.TourbookDevice;
import net.tourbook.tour.TourLogManager;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.xml.sax.SAXParseException;

public class GPXDeviceDataReader extends TourbookDevice {

   private static final char   NL          = UI.NEW_LINE;

   private static final String XML_GPX_TAG = "<gpx";     //$NON-NLS-1$

   public GPXDeviceDataReader() {
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

   private InputStream convertIntoWellFormedXml(final String importFilePath) {

      StringWriter xmlWriter = null;

      try (FileInputStream inputStream = new FileInputStream(importFilePath);
            BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream, UI.UTF_8))) {

         xmlWriter = new StringWriter();

         // write "<?xml ..." to be well conformed
         xmlWriter.write(XML_HEADER);
         xmlWriter.write(NL);

         // copy all lines
         String line;
         while ((line = fileReader.readLine()) != null) {
            xmlWriter.write(line);
            xmlWriter.write(NL);
         }

      } catch (final Exception e1) {
         StatusUtil.log(e1);
      } finally {
         Util.closeWriter(xmlWriter);
      }

      final String xml = xmlWriter.toString();

      return new ByteArrayInputStream(xml.getBytes());
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

   /**
    * @param importFilePath
    * @return Returns <code>null</code> when file is not a .gpx file.
    */
   private InputStream getWellFormedGPX(final String importFilePath) {

      if (isGPXFile(importFilePath) == false) {
         return null;
      }

      return convertIntoWellFormedXml(importFilePath);
   }

   /**
    * Check if the file is a valid gpx file by checking this tag. This file must not be a well
    * conformed xml file.
    *
    * @param importFilePath
    * @return Returns <code>true</code> when the file contains the gpx tag.
    */
   private boolean isGPXFile(final String importFilePath) {

      try (FileInputStream inputStream = new FileInputStream(importFilePath);
            BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream, UI.UTF_8))) {

         String line;

         while ((line = fileReader.readLine()) != null) {

            if (line.toLowerCase().contains(XML_GPX_TAG)) {
               return true;
            }
         }

      } catch (final Exception e1) {

         StatusUtil.log(e1);

      }

      return false;
   }

   @Override
   public void processDeviceData(final String importFilePath,
                                 final DeviceData deviceData,
                                 final Map<Long, TourData> alreadyImportedTours,
                                 final Map<Long, TourData> newlyImportedTours,
                                 final ImportState_File importState_File,
                                 final ImportState_Process importState_Process) {

      InputStream inputStream = null;

      if (isValidXMLFile(importFilePath, XML_GPX_TAG) == false) {

         inputStream = getWellFormedGPX(importFilePath);

         if (inputStream == null) {
            return;
         }
      }

      try {

         final SAXParser saxParser = XmlUtils.initializeParser();

         final GPX_SAX_Handler handler = new GPX_SAX_Handler(

               importFilePath,
               deviceData,
               alreadyImportedTours,
               newlyImportedTours,

               importState_File,
               importState_Process,

               this);

         if (inputStream == null) {
            saxParser.parse("file:" + importFilePath, handler);//$NON-NLS-1$
         } else {
            saxParser.parse(inputStream, handler);
         }

      } catch (final SAXParseException e) {

         final String errorText = UI.EMPTY_STRING

               + "XML error when parsing file:" + NL //        //$NON-NLS-1$
               + NL
               + importFilePath
               + NL
               + NL
               + e.getLocalizedMessage()
               + NL
               + NL
               + "Line: " + e.getLineNumber() //               //$NON-NLS-1$
               + "   Column: " + e.getColumnNumber() //        //$NON-NLS-1$
         ;

         Display.getDefault().syncExec(() -> MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", errorText)); //$NON-NLS-1$

         TourLogManager.log_EXCEPTION_WithStacktrace(e);

      } catch (final Exception e) {

         TourLogManager.log_ERROR_CannotReadDataFile(importFilePath, e);
      }
   }

   @Override
   public boolean validateRawData(final String importFilePath) {

      if (isValidXMLFile(importFilePath, XML_GPX_TAG)) {
         return true;
      }

      if (isGPXFile(importFilePath)) {
         return true;
      }

      return false;
   }

}

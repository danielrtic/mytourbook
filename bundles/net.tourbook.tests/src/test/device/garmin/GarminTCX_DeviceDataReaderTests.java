/*******************************************************************************
 * Copyright (C) 2020, 2023 Frédéric Bard
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
package device.garmin;

import net.tourbook.device.garmin.GarminTCX_DeviceDataReader;

import org.junit.jupiter.api.Test;

import utils.DeviceDataReaderTester;
import utils.FilesUtils;

public class GarminTCX_DeviceDataReaderTests extends DeviceDataReaderTester {

   public static final String         FILES_PATH       = FilesUtils.rootPath + "device/garmin/tcx/files/"; //$NON-NLS-1$

   private GarminTCX_DeviceDataReader deviceDataReader = new GarminTCX_DeviceDataReader();

   @Test
   void testTcxImportConeyLake() {

      testImportFile(deviceDataReader, FILES_PATH + "Move_2020_05_23_08_55_42_Trail+running", ".tcx"); //$NON-NLS-1$ //$NON-NLS-2$
   }

   /**
    * TCX file with pauses
    */
   @Test
   void testTcxImportLyons() {

      testImportFile(deviceDataReader, FILES_PATH + "2021-01-31", ".tcx"); //$NON-NLS-1$ //$NON-NLS-2$
   }

   /**
    * TCX file with power values
    */
   @Test
   void testTcxPeloton() {

      testImportFile(deviceDataReader, FILES_PATH + "45_min_HIIT_Hills_Ride_with_Robin_Arz_n", ".tcx"); //$NON-NLS-1$ //$NON-NLS-2$
   }
}

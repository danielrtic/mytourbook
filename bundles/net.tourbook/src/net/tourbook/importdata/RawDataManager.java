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
package net.tourbook.importdata;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import net.tourbook.Messages;
import net.tourbook.application.PerspectiveFactoryRawData;
import net.tourbook.application.TourbookPlugin;
import net.tourbook.common.CommonActivator;
import net.tourbook.common.FileSystemManager;
import net.tourbook.common.UI;
import net.tourbook.common.dialog.MessageDialogWithRadioOptions;
import net.tourbook.common.time.TimeTools;
import net.tourbook.common.util.FilesUtils;
import net.tourbook.common.util.ITourViewer3;
import net.tourbook.common.util.StatusUtil;
import net.tourbook.common.util.Util;
import net.tourbook.common.widgets.ComboEnumEntry;
import net.tourbook.data.TourData;
import net.tourbook.data.TourPerson;
import net.tourbook.data.TourTag;
import net.tourbook.data.TourType;
import net.tourbook.database.TourDatabase;
import net.tourbook.preferences.ITourbookPreferences;
import net.tourbook.tour.CadenceMultiplier;
import net.tourbook.tour.TourEventId;
import net.tourbook.tour.TourLogManager;
import net.tourbook.tour.TourLogState;
import net.tourbook.tour.TourLogView;
import net.tourbook.tour.TourManager;
import net.tourbook.ui.views.rawData.RawDataView;
import net.tourbook.ui.views.tourDataEditor.TourDataEditorView;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

public class RawDataManager {

// SET_FORMATTING_OFF

   private static final String   COLUMN_FACTORY_CATEGORY_MARKER              = net.tourbook.ui.Messages.ColumnFactory_Category_Marker;
   private static final String   COLUMN_FACTORY_GEAR_REAR_SHIFT_COUNT_LABEL  = net.tourbook.ui.Messages.ColumnFactory_GearRearShiftCount_Label;
   private static final String   COLUMN_FACTORY_GEAR_FRONT_SHIFT_COUNT_LABEL = net.tourbook.ui.Messages.ColumnFactory_GearFrontShiftCount_Label;
   private static final String   VALUE_UNIT_CADENCE                          = net.tourbook.ui.Messages.Value_Unit_Cadence;
   private static final String   VALUE_UNIT_CADENCE_SPM                      = net.tourbook.ui.Messages.Value_Unit_Cadence_Spm;
   private static final String   VALUE_UNIT_K_CALORIES                       = net.tourbook.ui.Messages.Value_Unit_KCalories;
   private static final String   VALUE_UNIT_PULSE                            = net.tourbook.ui.Messages.Value_Unit_Pulse;

   public static final String    LOG_IMPORT_DELETE_TOUR_FILE                 = Messages.Log_Import_DeleteTourFiles;
   public static final String    LOG_IMPORT_DELETE_TOUR_FILE_END             = Messages.Log_Import_DeleteTourFiles_End;
   private static final String   LOG_IMPORT_TOUR                             = Messages.Log_Import_Tour;
   public static final String    LOG_IMPORT_TOUR_IMPORTED                    = Messages.Log_Import_Tour_Imported;
   private static final String   LOG_IMPORT_TOUR_END                         = Messages.Log_Import_Tour_End;
   public static final String    LOG_IMPORT_TOURS_IMPORTED_FROM_FILE         = Messages.Log_Import_Tours_Imported_From_File;

   public static final String    LOG_DELETE_COMBINED_VALUES                  = NLS.bind(Messages.Log_ModifiedTour_Combined_Values, Messages.Log_Delete_Text);
   public static final String    LOG_DELETE_TOURVALUES_END                   = Messages.Log_Delete_TourValues_End;
   public static final String    LOG_MODIFIEDTOUR_OLD_DATA_VS_NEW_DATA       = Messages.Log_ModifiedTour_Old_Data_Vs_New_Data;

   public static final String    LOG_REIMPORT_PREVIOUS_FILES                 = Messages.Log_Reimport_PreviousFiles;
   public static final String    LOG_REIMPORT_END                            = Messages.Log_Reimport_PreviousFiles_End;
   public static final String    LOG_REIMPORT_COMBINED_VALUES                = NLS.bind(Messages.Log_ModifiedTour_Combined_Values, Messages.Log_Reimport_Text);
   private static final String   LOG_REIMPORT_MANUAL_TOUR                    = Messages.Log_Reimport_ManualTour;
   private static final String   LOG_REIMPORT_TOUR_SKIPPED                   = Messages.Log_Reimport_Tour_Skipped;

// SET_FORMATTING_ON

   private static final String           RAW_DATA_LAST_SELECTED_PATH      = "raw-data-view.last-selected-import-path";             //$NON-NLS-1$
   private static final String           TEMP_IMPORTED_FILE               = "received-device-data.txt";                            //$NON-NLS-1$

   private static final String           FILE_EXTENSION_FIT               = ".fit";                                                //$NON-NLS-1$

   private static final IPreferenceStore _prefStore                       = TourbookPlugin.getPrefStore();
   private static final IDialogSettings  _state_RawDataView               = TourbookPlugin.getState(RawDataView.ID);

   private static final String           INVALIDFILES_TO_IGNORE           = "invalidfiles_to_ignore.txt";                          //$NON-NLS-1$

   public static final int               ADJUST_IMPORT_YEAR_IS_DISABLED   = -1;

   static final ComboEnumEntry<?>[]      ALL_IMPORT_TOUR_TYPE_CONFIG;

   private static boolean                _importState_IsAutoOpenImportLog = RawDataView.STATE_IS_AUTO_OPEN_IMPORT_LOG_VIEW_DEFAULT;
   private static boolean                _importState_IsIgnoreInvalidFile = RawDataView.STATE_IS_IGNORE_INVALID_FILE_DEFAULT;
   private static boolean                _importState_IsSetBodyWeight     = RawDataView.STATE_IS_SET_BODY_WEIGHT_DEFAULT;
   private static CadenceMultiplier      _importState_DefaultCadenceMultiplier;

   static {

      ALL_IMPORT_TOUR_TYPE_CONFIG = new ComboEnumEntry<?>[] {

            new ComboEnumEntry<>(Messages.Import_Data_TourTypeConfig_OneForAll, TourTypeConfig.TOUR_TYPE_CONFIG_ONE_FOR_ALL),
            new ComboEnumEntry<>(Messages.Import_Data_TourTypeConfig_BySpeed, TourTypeConfig.TOUR_TYPE_CONFIG_BY_SPEED)

      };

      _importState_DefaultCadenceMultiplier = (CadenceMultiplier) Util.getStateEnum(_state_RawDataView,
            RawDataView.STATE_DEFAULT_CADENCE_MULTIPLIER,
            RawDataView.STATE_DEFAULT_CADENCE_MULTIPLIER_DEFAULT);

   }

   private static RawDataManager                  _instance;

   /**
    * Is <code>true</code> when currently a re-importing is running
    */
   private static boolean                         _isReimportingActive;

   /**
    * Is <code>true</code> when deleting values from tour(s) is happening
    */
   private static boolean                         _isDeleteValuesActive;

   private static List<TourbookDevice>            _allDevices_BySortPriority;
   private static HashMap<String, TourbookDevice> _allDevices_ByExtension;
   static {

      createDeviceLists();
   }

   /**
    * Contains files which could not be imported, the key is the OS filepath name.
    * <p>
    * Only the KeySet is used
    */
   private static final ConcurrentHashMap<String, Object> _allInvalidFiles              = new ConcurrentHashMap<>();

   /**
    * Contains alternative filepaths from previous re-imported tours, the key is the {@link IPath}.
    * <p>
    * Only the KeySet is used
    */
   private static final ConcurrentHashMap<IPath, Object>  _allPreviousReimportFolders   = new ConcurrentHashMap<>();

   private static volatile IPath                          _previousReimportFolder;

   /**
    * Contains tours which are imported or received and displayed in the import view.
    */
   private static final ConcurrentHashMap<Long, TourData> _allImportedTours             = new ConcurrentHashMap<>();

   /**
    * Contains the filenames for all imported files which are displayed in the import view
    */
   private static final ConcurrentHashMap<String, String> _allImportedFileNames         = new ConcurrentHashMap<>();

   /**
    * Contains filenames which are not directly imported but is imported from other imported files
    */
   private static final ConcurrentHashMap<String, String> _allImportedFileNamesChildren = new ConcurrentHashMap<>();

   //
   /**
    * Contains the device data imported from the device/file
    */
   private static final DeviceData               _deviceData        = new DeviceData();
   //
   private static ThreadPoolExecutor             _importTour_Executor;
   private static ArrayBlockingQueue<ImportFile> _importTour_Queue  = new ArrayBlockingQueue<>(Util.NUMBER_OF_PROCESSORS);
   private static CountDownLatch                 _importTour_CountDownLatch;

   private static ThreadPoolExecutor             _loadingTour_Executor;
   private static ArrayBlockingQueue<TourData>   _loadingTour_Queue = new ArrayBlockingQueue<>(Util.NUMBER_OF_PROCESSORS);
   private static CountDownLatch                 _loadingTour_CountDownLatch;

   static {

      final ThreadFactory importThreadFactory = runnable -> {

         final Thread thread = new Thread(runnable, "Importing tours");//$NON-NLS-1$

         thread.setPriority(Thread.MIN_PRIORITY);
         thread.setDaemon(true);

         return thread;
      };

      _importTour_Executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(Util.NUMBER_OF_PROCESSORS, importThreadFactory);

      final ThreadFactory loadingThreadFactory = runnable -> {

         final Thread thread = new Thread(runnable, "Loading imported tours");//$NON-NLS-1$

         thread.setPriority(Thread.MIN_PRIORITY);
         thread.setDaemon(true);

         return thread;
      };

      _loadingTour_Executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(Util.NUMBER_OF_PROCESSORS, loadingThreadFactory);
   }
   //
   private int     _importState_ImportYear                  = ADJUST_IMPORT_YEAR_IS_DISABLED;
   private boolean _importState_IsConvertWayPoints;
   private boolean _importState_IsCreateTourIdWithTime      = RawDataView.STATE_IS_CREATE_TOUR_ID_WITH_TIME_DEFAULT;
   private boolean _importState_IsHAC4_5_ChecksumValidation = RawDataView.STATE_IS_CHECKSUM_VALIDATION_DEFAULT;
   private boolean _importState_IsMergeTracks               = RawDataView.STATE_IS_MERGE_TRACKS_DEFAULT;
   {
      _importState_IsConvertWayPoints = Util.getStateBoolean(_state_RawDataView,

            RawDataView.STATE_IS_CONVERT_WAYPOINTS,
            RawDataView.STATE_IS_CONVERT_WAYPOINTS_DEFAULT);
   }
   //
   private final ArrayList<TourType>            _tempTourTypes                           = new ArrayList<>();
   private final ArrayList<TourTag>             _tempTourTags                            = new ArrayList<>();

   private volatile ReplaceImportFilenameAction _selectedImportFilenameReplacementOption = ReplaceImportFilenameAction.DO_NOTHING;

   /**
    * This is a wrapper to keep the {@link #isBackupImportFile} state.
    */
   private class ImportFile {

      IPath   filePath;
      boolean isBackupImportFile;
      String  osFilePath;

      public ImportFile(final org.eclipse.core.runtime.Path iPath) {

         filePath = iPath;
         osFilePath = filePath.toOSString();
      }
   }

   /**
    * Actions when a import filename is not the same as in the saved tour
    */
   private enum ReplaceImportFilenameAction {

      DO_NOTHING, //
      DO_NOTHING_AND_DONT_ASK_AGAIN, //
      REPLACE_IMPORT_FILENAME_IN_SAVED_TOUR, //
      REPLACE_IMPORT_FILENAME_IN_ALL_SAVED_TOUR, //
   }

   /**
    * Tour values which should be re-imported/deleted
    */
   public enum TourValueType {

      ENTIRE_TOUR, //
      ALL_TIME_SLICES, //

      TOUR__CALORIES, //
      TOUR__IMPORT_FILE_LOCATION, //
      TOUR__MARKER, //

      TIME_SLICES__BATTERY, //
      TIME_SLICES__CADENCE, //
      TIME_SLICES__ELEVATION, //
      TIME_SLICES__GEAR, //
      TIME_SLICES__POWER_AND_SPEED, //
      TIME_SLICES__POWER_AND_PULSE, //
      TIME_SLICES__RUNNING_DYNAMICS, //
      TIME_SLICES__SWIMMING, //
      TIME_SLICES__TEMPERATURE, //
      TIME_SLICES__TRAINING, //
      TIME_SLICES__TIME, //
      TIME_SLICES__TIMER_PAUSES //
   }

   private RawDataManager() {}

   private static void createDeviceLists() {

      _allDevices_BySortPriority = new ArrayList<>(DeviceManager.getDeviceList());

      // sort device list by sorting priority
      Collections.sort(_allDevices_BySortPriority, (tourbookDevice1, tourbookDevice2) -> {

         // 1. sort by priority
         final int sortByPrio = tourbookDevice1.extensionSortPriority - tourbookDevice2.extensionSortPriority;

         // 2. sort by name
         if (sortByPrio == 0) {
            return tourbookDevice1.deviceId.compareTo(tourbookDevice2.deviceId);
         }

         return sortByPrio;
      });

      _allDevices_ByExtension = new HashMap<>();

      for (final TourbookDevice device : _allDevices_BySortPriority) {
         _allDevices_ByExtension.put(device.fileExtension.toLowerCase(), device);
      }
   }

   /**
    * Displays the differences of data before and after the tour modifications (re-import or
    * deletion)
    *
    * @param tourValueType
    *           A tour value that was modified (re-imported or deleted)
    * @param oldTourData
    *           The Tour before the modifications (re-import or deletion)
    * @param newTourData
    *           The Tour after the modifications (re-import or deletion)
    */
   public static void displayTourModifiedDataDifferences(final TourValueType tourValueType,
                                                         final TourData oldTourData,
                                                         final TourData newTourData) {
      final List<String> previousData = new ArrayList<>();
      final List<String> newData = new ArrayList<>();

      final boolean isEntireTour = tourValueType == TourValueType.ENTIRE_TOUR;
      final boolean isEntireTour_OR_AllTimeSlices = isEntireTour || tourValueType == TourValueType.ALL_TIME_SLICES;

      /**
       * Time slices
       */
      if (isEntireTour_OR_AllTimeSlices || tourValueType == TourValueType.TIME_SLICES__CADENCE) {

         previousData.add(
               Math.round(oldTourData.getAvgCadence()) + (oldTourData.isCadenceSpm()
                     ? VALUE_UNIT_CADENCE_SPM
                     : VALUE_UNIT_CADENCE));
         newData.add(
               Math.round(newTourData.getAvgCadence()) + (newTourData.isCadenceSpm()
                     ? VALUE_UNIT_CADENCE_SPM
                     : VALUE_UNIT_CADENCE));
      }

      if (isEntireTour_OR_AllTimeSlices || tourValueType == TourValueType.TIME_SLICES__ELEVATION) {

         final String heightLabel = UI.UNIT_IS_ELEVATION_METER ? UI.UNIT_METER : UI.UNIT_HEIGHT_FT;

         final int oldAltitudeUp = Math.round(oldTourData.getTourAltUp() / UI.UNIT_VALUE_ELEVATION);
         final int oldAltitudeDown = Math.round(oldTourData.getTourAltDown() / UI.UNIT_VALUE_ELEVATION);
         previousData.add(
               UI.SYMBOL_PLUS + oldAltitudeUp + heightLabel + UI.SLASH_WITH_SPACE
                     + UI.DASH
                     + oldAltitudeDown
                     + heightLabel);

         final int newAltitudeUp = Math.round(newTourData.getTourAltUp() / UI.UNIT_VALUE_ELEVATION);
         final int newAltitudeDown = Math.round(newTourData.getTourAltDown() / UI.UNIT_VALUE_ELEVATION);
         newData.add(
               UI.SYMBOL_PLUS + newAltitudeUp + heightLabel + UI.SLASH_WITH_SPACE
                     + UI.DASH + newAltitudeDown
                     + heightLabel);
      }

      if (isEntireTour_OR_AllTimeSlices || tourValueType == TourValueType.TIME_SLICES__GEAR) {
         previousData.add(
               oldTourData.getFrontShiftCount() + UI.SPACE1 + COLUMN_FACTORY_GEAR_FRONT_SHIFT_COUNT_LABEL
                     + UI.COMMA_SPACE + oldTourData.getRearShiftCount() + UI.SPACE1 + COLUMN_FACTORY_GEAR_REAR_SHIFT_COUNT_LABEL);
         newData.add(
               newTourData.getFrontShiftCount() + UI.SPACE1 + COLUMN_FACTORY_GEAR_FRONT_SHIFT_COUNT_LABEL
                     + UI.COMMA_SPACE + newTourData.getRearShiftCount() + UI.SPACE1 + COLUMN_FACTORY_GEAR_REAR_SHIFT_COUNT_LABEL);
      }

      if (isEntireTour_OR_AllTimeSlices || tourValueType == TourValueType.TIME_SLICES__POWER_AND_PULSE) {

         previousData.add(
               Math.round(oldTourData.getPower_Avg()) + UI.UNIT_POWER_SHORT + UI.COMMA_SPACE
                     + Math.round(oldTourData.getAvgPulse()) + VALUE_UNIT_PULSE);
         newData.add(
               Math.round(newTourData.getPower_Avg()) + UI.UNIT_POWER_SHORT + UI.COMMA_SPACE
                     + Math.round(newTourData.getAvgPulse()) + VALUE_UNIT_PULSE);
      }

      if (isEntireTour_OR_AllTimeSlices || tourValueType == TourValueType.TIME_SLICES__POWER_AND_SPEED) {

         previousData.add(Math.round(oldTourData.getPower_Avg()) + UI.UNIT_POWER_SHORT);
         newData.add(Math.round(newTourData.getPower_Avg()) + UI.UNIT_POWER_SHORT);
      }

      if (isEntireTour_OR_AllTimeSlices || tourValueType == TourValueType.TIME_SLICES__TEMPERATURE) {

         float avgTemperature = oldTourData.getAvgTemperature();
         if (!UI.UNIT_IS_TEMPERATURE_CELSIUS) {
            avgTemperature = avgTemperature
                  * UI.UNIT_FAHRENHEIT_MULTI
                  + UI.UNIT_FAHRENHEIT_ADD;
         }
         previousData.add(
               Math.round(avgTemperature) + (UI.UNIT_IS_TEMPERATURE_CELSIUS
                     ? UI.SYMBOL_TEMPERATURE_CELSIUS
                     : UI.SYMBOL_TEMPERATURE_FAHRENHEIT));

         avgTemperature = newTourData.getAvgTemperature();
         if (!UI.UNIT_IS_TEMPERATURE_CELSIUS) {
            avgTemperature = avgTemperature
                  * UI.UNIT_FAHRENHEIT_MULTI
                  + UI.UNIT_FAHRENHEIT_ADD;
         }
         newData.add(
               Math.round(avgTemperature) + (UI.UNIT_IS_TEMPERATURE_CELSIUS
                     ? UI.SYMBOL_TEMPERATURE_CELSIUS
                     : UI.SYMBOL_TEMPERATURE_FAHRENHEIT));
      }

      if (isEntireTour_OR_AllTimeSlices || tourValueType == TourValueType.TIME_SLICES__TIMER_PAUSES) {

         previousData.add(UI.format_hhh_mm_ss(oldTourData.getTourDeviceTime_Paused()));
         newData.add(UI.format_hhh_mm_ss(newTourData.getTourDeviceTime_Paused()));
      }

      /*
       * Tour
       */
      if (isEntireTour || tourValueType == TourValueType.TOUR__CALORIES) {

         previousData.add(
               oldTourData.getCalories() / 1000f + VALUE_UNIT_K_CALORIES);
         newData.add(
               newTourData.getCalories() / 1000f + VALUE_UNIT_K_CALORIES);
      }

      if (isEntireTour || tourValueType == TourValueType.TOUR__MARKER) {

         previousData.add(
               oldTourData.getTourMarkers().size() + UI.SPACE1 + COLUMN_FACTORY_CATEGORY_MARKER);
         newData.add(
               newTourData.getTourMarkers().size() + UI.SPACE1 + COLUMN_FACTORY_CATEGORY_MARKER);
      }

      if (isEntireTour || tourValueType == TourValueType.TOUR__IMPORT_FILE_LOCATION) {

         previousData.add(oldTourData.getImportFilePathName());
         newData.add(newTourData.getImportFilePathName());
      }

      if (previousData.isEmpty() && newData.isEmpty()) {
         return;
      }

      for (int index = 0; index < previousData.size(); ++index) {

         TourLogManager.subLog_INFO(NLS.bind(
               LOG_MODIFIEDTOUR_OLD_DATA_VS_NEW_DATA,
               previousData.get(index),
               newData.get(index)));
      }
   }

   public static boolean doesInvalidFileExist(final String fileName) {

      final ArrayList<String> invalidFilesList = readInvalidFilesToIgnoreFile();

      return invalidFilesList
            .stream()
            .anyMatch(invalidFilePath -> Paths.get(invalidFilePath).getFileName().toString().equals(fileName));
   }

   /**
    * @return Returns the cadence multiplier default value
    */
   public static CadenceMultiplier getCadenceMultiplierDefaultValue() {
      return _importState_DefaultCadenceMultiplier;
   }

   private static EasyConfig getEasyConfig() {
      return EasyImportManager.getInstance().getEasyConfig();
   }

   public static RawDataManager getInstance() {

      if (_instance == null) {
         _instance = new RawDataManager();
      }

      return _instance;
   }

   private static File getInvalidFilesToIgnoreFile() {
      final IPath stateLocation = Platform.getStateLocation(CommonActivator.getDefault().getBundle());
      return stateLocation.append(INVALIDFILES_TO_IGNORE).toFile();
   }

   /**
    * @return temporary directory where received data are stored temporarily
    */
   public static String getTempDir() {
      return TourbookPlugin.getDefault().getStateLocation().toFile().getAbsolutePath();
   }

   public static boolean isAutoOpenImportLog() {
      return _importState_IsAutoOpenImportLog;
   }

   /**
    * @return Returns <code>true</code> when currently deleting values from tour(s)
    */
   public static boolean isDeleteValuesActive() {
      return _isDeleteValuesActive;
   }

   public static boolean isIgnoreInvalidFile() {
      return _importState_IsIgnoreInvalidFile;
   }

   /**
    * @return Returns <code>true</code> when currently a re-importing is running
    */
   public static boolean isReimportingActive() {
      return _isReimportingActive;
   }

   public static boolean isSetBodyWeight() {
      return _importState_IsSetBodyWeight;
   }

   private static ArrayList<String> readInvalidFilesToIgnoreFile() {
      final ArrayList<String> invalidFilesList = new ArrayList<>();

      final File invalidFilesToIgnoreFile = getInvalidFilesToIgnoreFile();
      if (!invalidFilesToIgnoreFile.exists()) {
         return invalidFilesList;
      }

      try (Scanner s = new Scanner(invalidFilesToIgnoreFile)) {
         while (s.hasNext()) {
            invalidFilesList.add(s.next());
         }
      } catch (final IOException e) {
         e.printStackTrace();
      }

      return invalidFilesList;
   }

   /**
    * Writes the list of files to ignore into a text file.
    */
   private static void save_InvalidFilesToIgnore_InTxt() {

      final File file = getInvalidFilesToIgnoreFile();

      try {
         if (!file.exists()) {
            file.createNewFile();
         }
      } catch (final IOException e) {
         e.printStackTrace();
      }

      try (FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, UI.UTF_8);
            BufferedWriter writer = new BufferedWriter(outputStreamWriter)) {

         final ImportConfig importConfig = getEasyConfig().getActiveImportConfig();

         for (final String invalidFile : _allInvalidFiles.keySet()) {

            Path invalidFilePath = Paths.get(invalidFile);

            //If the invalid files are backed up and deleted from the device folder,
            //then we save their backup path and not their device path.
            if (importConfig.isCreateBackup && importConfig.isDeleteDeviceFiles) {
               invalidFilePath = Paths.get(importConfig.getBackupFolder(), Paths.get(invalidFile).getFileName().toString());
            }

            // We check if the file still exists (it could have been deleted recently)
            // and that it's not already in the text file
            if (Files.exists(invalidFilePath) && !doesInvalidFileExist(invalidFilePath.getFileName().toString())) {
               writer.write(invalidFilePath.toString());
               writer.newLine();
            }
         }

      } catch (final IOException e) {
         e.printStackTrace();
      }
   }

   public static void setIsDeleteValuesActive(final boolean isDeleteValuesActive) {

      _isDeleteValuesActive = isDeleteValuesActive;
   }

   public static void setIsReimportingActive(final boolean isReimportingActive) {

      _isReimportingActive = isReimportingActive;
   }

   public void actionImportFromDevice() {

      final DataTransferWizardDialog dialog = new DataTransferWizardDialog(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            new DataTransferWizard(),
            Messages.Import_Wizard_Dlg_title);

      if (dialog.open() == Window.OK) {
         showRawDataView();
      }
   }

   public void actionImportFromDeviceDirect() {

      final DataTransferWizard transferWizard = new DataTransferWizard();

      final WizardDialog dialog = new DataTransferWizardDialog(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            transferWizard,
            Messages.Import_Wizard_Dlg_title);

      // create the dialog and shell which is required in setAutoDownload()
      dialog.create();

      transferWizard.setAutoDownload();

      if (dialog.open() == Window.OK) {
         showRawDataView();
      }
   }

   /**
    * Import tours from files which are selected in a file selection dialog.
    */
   public void actionImportFromFile() {

      final List<TourbookDevice> allDevices = DeviceManager.getDeviceList();

      // create file filter list
      final int deviceLength = allDevices.size() + 1;
      final String[] filterExtensions = new String[deviceLength];
      final String[] filterNames = new String[deviceLength];

      int deviceIndex = 0;

      // add option to show all files
      filterExtensions[deviceIndex] = "*.*"; //$NON-NLS-1$
      filterNames[deviceIndex] = "*.*"; //$NON-NLS-1$

      deviceIndex++;

      // add option for every file extension
      for (final TourbookDevice device : allDevices) {
         filterExtensions[deviceIndex] = "*." + device.fileExtension; //$NON-NLS-1$
         filterNames[deviceIndex] = device.visibleName + (" (*." + device.fileExtension + UI.SYMBOL_BRACKET_RIGHT); //$NON-NLS-1$
         deviceIndex++;
      }

      final String lastSelectedPath = _prefStore.getString(RAW_DATA_LAST_SELECTED_PATH);

      // setup open dialog
      final FileDialog fileDialog = new FileDialog(Display.getDefault().getActiveShell(), (SWT.OPEN | SWT.MULTI));
      fileDialog.setFilterExtensions(filterExtensions);
      fileDialog.setFilterNames(filterNames);
      fileDialog.setFilterPath(lastSelectedPath);

      // open file dialog
      final String firstFilePathName = fileDialog.open();

      // check if user canceled the dialog
      if (firstFilePathName == null) {
         return;
      }

      final Path firstFilePath = Paths.get(firstFilePathName);
      final String filePathFolder = firstFilePath.getParent().toString();

      // keep last selected path
      _prefStore.putValue(RAW_DATA_LAST_SELECTED_PATH, filePathFolder);

      final String[] selectedFileNames = fileDialog.getFileNames();

      final ArrayList<OSFile> allOSFiles = new ArrayList<>();

      for (final String fileName : selectedFileNames) {

         final Path filePath = Paths.get(filePathFolder, fileName);
         final OSFile osFile = new OSFile(filePath);

         allOSFiles.add(osFile);
      }

      if (_importState_IsAutoOpenImportLog) {
         TourLogManager.showLogView();
      }

      importTours_FromMultipleFiles(
            allOSFiles,
            null,
            new ImportStates().setIsEasyImport(false));
   }

   /**
    * Asks the user if the modification (re-import or deletion) of all the chosen data is desired.
    *
    * @param tourValueTypes
    *           A list of tour values to be modified (re-imported or deleted)
    * @param isReimport
    *           Returns <code>true</code> if data needs to be re-imported, <code>false</code> if the
    *           data needs to be deleted
    * @return
    */
   public boolean actionModifyTourValues_10_Confirm(final List<TourValueType> tourValueTypes, final boolean isReimport) {

      final ArrayList<String> dataToModifyDetails = createTourValuesMessages(tourValueTypes);

      final String toggleState = isReimport
            ? ITourbookPreferences.TOGGLE_STATE_REIMPORT_TOUR_VALUES
            : ITourbookPreferences.TOGGLE_STATE_DELETE_TOUR_VALUES;

      final String dialogTitle = isReimport
            ? Messages.Dialog_ReimportData_Title
            : Messages.Dialog_DeleteData_Title;

      String confirmMessage = isReimport
            ? Messages.Dialog_ReimportTours_Dialog_ConfirmReimportValues_Message
            : Messages.Dialog_DeleteTourValues_Dialog_ConfirmDeleteValues_Message;
      confirmMessage = NLS.bind(confirmMessage, String.join(UI.NEW_LINE1, dataToModifyDetails));

      if (actionModifyTourValues_12_Confirm_Dialog(
            toggleState,
            dialogTitle,
            confirmMessage)//
      ) {

         String logMessage = isReimport ? LOG_REIMPORT_COMBINED_VALUES : LOG_DELETE_COMBINED_VALUES;

         logMessage = logMessage.concat(String.join(UI.SPACE1, dataToModifyDetails));

         TourLogManager.addLog(
               TourLogState.DEFAULT,
               logMessage,
               TourLogView.CSS_LOG_TITLE);

         return true;
      }

      return false;
   }

   private boolean actionModifyTourValues_12_Confirm_Dialog(final String toggleState,
                                                            final String dialogTitle,
                                                            final String confirmMessage) {

      if (_prefStore.getBoolean(toggleState)) {

         return true;

      } else {

         final MessageDialogWithToggle dialog = MessageDialogWithToggle.openOkCancelConfirm(
               Display.getDefault().getActiveShell(),
               dialogTitle,
               confirmMessage,
               Messages.App_ToggleState_DoNotShowAgain,
               false, // toggle default state
               null,
               null);

         if (dialog.getReturnCode() == Window.OK) {
            _prefStore.setValue(toggleState, dialog.getToggleState());
            return true;
         }
      }

      return false;
   }

   public void clearInvalidFilesList() {
      _allInvalidFiles.clear();
   }

   private TourData createTourDataDummyClone(final List<TourValueType> tourValueTypes, final TourData oldTourData) {

      TourData tourDataDummyClone = null;

      try {

         tourDataDummyClone = (TourData) oldTourData.clone();

         /*
          * Loop: For each tour value type, we save the associated data for future display
          * to compare with the new data
          */
         for (final TourValueType tourValueType : tourValueTypes) {

            final boolean isEntireTour = tourValueType == TourValueType.ENTIRE_TOUR;
            final boolean isEntireTour_OR_AllTimeSlices = isEntireTour || tourValueType == TourValueType.ALL_TIME_SLICES;

            /*
             * Tour values
             */
            if (isEntireTour || tourValueType == TourValueType.TOUR__MARKER) {

               tourDataDummyClone.setTourMarkers(new HashSet<>(oldTourData.getTourMarkers()));
            }

            if (isEntireTour || tourValueType == TourValueType.TOUR__CALORIES) {

               tourDataDummyClone.setCalories(oldTourData.getCalories());
            }

            if (isEntireTour || tourValueType == TourValueType.TOUR__IMPORT_FILE_LOCATION) {

               tourDataDummyClone.setImportFilePath(oldTourData.getImportFilePathName());
            }

            /*
             * Time slice values
             */
            if (isEntireTour_OR_AllTimeSlices || tourValueType == TourValueType.TIME_SLICES__CADENCE) {

               tourDataDummyClone.setAvgCadence(oldTourData.getAvgCadence());
               tourDataDummyClone.setCadenceMultiplier(oldTourData.getCadenceMultiplier());

            }

            if (isEntireTour_OR_AllTimeSlices || tourValueType == TourValueType.TIME_SLICES__ELEVATION) {

               tourDataDummyClone.setTourAltDown(oldTourData.getTourAltDown());
               tourDataDummyClone.setTourAltUp(oldTourData.getTourAltUp());

            }

            if (isEntireTour_OR_AllTimeSlices || tourValueType == TourValueType.TIME_SLICES__GEAR) {

               tourDataDummyClone.setFrontShiftCount(oldTourData.getFrontShiftCount());
               tourDataDummyClone.setRearShiftCount(oldTourData.getRearShiftCount());
            }

            if (isEntireTour_OR_AllTimeSlices || tourValueType == TourValueType.TIME_SLICES__POWER_AND_PULSE) {

               tourDataDummyClone.setPower_Avg(oldTourData.getPower_Avg());
               tourDataDummyClone.setAvgPulse(oldTourData.getAvgPulse());
            }

            if (isEntireTour_OR_AllTimeSlices || tourValueType == TourValueType.TIME_SLICES__POWER_AND_SPEED) {

               tourDataDummyClone.setPower_Avg(oldTourData.getPower_Avg());
            }

            if (isEntireTour_OR_AllTimeSlices || tourValueType == TourValueType.TIME_SLICES__TEMPERATURE) {

               tourDataDummyClone.setAvgTemperature(oldTourData.getAvgTemperature());
            }

            if (isEntireTour_OR_AllTimeSlices || tourValueType == TourValueType.TIME_SLICES__TIMER_PAUSES) {

               tourDataDummyClone.setTourDeviceTime_Paused(oldTourData.getTourDeviceTime_Paused());
            }
         }

      } catch (final CloneNotSupportedException e) {
         StatusUtil.log(e);
      }
      return tourDataDummyClone;
   }

   /**
    * Creates a list with messages for all {@link TourValueType}'s
    *
    * @param tourValueTypes
    * @return
    */
   private ArrayList<String> createTourValuesMessages(final List<TourValueType> tourValueTypes) {

      final ArrayList<String> dataToModifyDetails = new ArrayList<>();

      for (final TourValueType tourValueType : tourValueTypes) {

         final boolean isAllTimeSlices = tourValueType == TourValueType.ALL_TIME_SLICES;

         // Battery
         if (isAllTimeSlices || tourValueType == TourValueType.TIME_SLICES__BATTERY) {
            dataToModifyDetails.add(Messages.Tour_Data_Text_BatteryValues);
         }

         // Cadence
         if (isAllTimeSlices || tourValueType == TourValueType.TIME_SLICES__CADENCE) {
            dataToModifyDetails.add(Messages.Tour_Data_Text_CadenceValues);
         }

         // Calories
         if (tourValueType == TourValueType.TOUR__CALORIES) {
            dataToModifyDetails.add(Messages.Tour_Data_Text_Calories);
         }

         // Elevation
         if (isAllTimeSlices || tourValueType == TourValueType.TIME_SLICES__ELEVATION) {
            dataToModifyDetails.add(Messages.Tour_Data_Text_AltitudeValues);
         }

         // Gear
         if (isAllTimeSlices || tourValueType == TourValueType.TIME_SLICES__GEAR) {
            dataToModifyDetails.add(Messages.Tour_Data_Text_GearValues);
         }

         // Power
         if (isAllTimeSlices
               || tourValueType == TourValueType.TIME_SLICES__POWER_AND_PULSE
               || tourValueType == TourValueType.TIME_SLICES__POWER_AND_SPEED) {
            dataToModifyDetails.add(Messages.Tour_Data_Text_PowerValues);
         }

         // Pulse
         if (isAllTimeSlices || tourValueType == TourValueType.TIME_SLICES__POWER_AND_PULSE) {
            dataToModifyDetails.add(Messages.Tour_Data_Text_PulseValues);
         }

         // Speed
         if (isAllTimeSlices || tourValueType == TourValueType.TIME_SLICES__POWER_AND_SPEED) {
            dataToModifyDetails.add(Messages.Tour_Data_Text_SpeedValues);
         }

         // Running Dynamics
         if (isAllTimeSlices || tourValueType == TourValueType.TIME_SLICES__RUNNING_DYNAMICS) {
            dataToModifyDetails.add(Messages.Tour_Data_Text_RunningDynamicsValues);
         }

         // Swimming
         if (isAllTimeSlices || tourValueType == TourValueType.TIME_SLICES__SWIMMING) {
            dataToModifyDetails.add(Messages.Tour_Data_Text_SwimmingValues);
         }

         // Temperature
         if (isAllTimeSlices || tourValueType == TourValueType.TIME_SLICES__TEMPERATURE) {
            dataToModifyDetails.add(Messages.Tour_Data_Text_TemperatureValues);
         }

         // Training
         if (isAllTimeSlices || tourValueType == TourValueType.TIME_SLICES__TRAINING) {
            dataToModifyDetails.add(Messages.Tour_Data_Text_TrainingValues);
         }

         // Timer pauses
         if (isAllTimeSlices || tourValueType == TourValueType.TIME_SLICES__TIMER_PAUSES) {
            dataToModifyDetails.add(Messages.Tour_Data_Text_TourTimerPauses);
         }

         // Tour markers
         if (tourValueType == TourValueType.TOUR__MARKER) {
            dataToModifyDetails.add(Messages.Tour_Data_Text_TourMarkers);
         }

         // Time data
         if (tourValueType == TourValueType.TIME_SLICES__TIME) {
            dataToModifyDetails.add(Messages.Tour_Data_Text_Time);
         }

         // Import file location
         if (tourValueType == TourValueType.TOUR__IMPORT_FILE_LOCATION) {
            dataToModifyDetails.add(Messages.Tour_Data_Text_ImportFileLocation);
         }

         // ALL
         if (isAllTimeSlices) {
            dataToModifyDetails.add(Messages.Tour_Data_Text_TimeSlices);
         }

         // Entire Tour
         if (tourValueType == TourValueType.ENTIRE_TOUR) {
            dataToModifyDetails.add(Messages.Tour_Data_Text_EntireTour);
         }
      }
      return dataToModifyDetails;
   }

   public void deleteTourValues(final List<TourValueType> tourValueTypes, final ITourViewer3 tourViewer) {

      final long start = System.currentTimeMillis();

      if (!actionModifyTourValues_10_Confirm(tourValueTypes, false)) {
         return;
      }

      final Object[] selectedItems = TourManager.getTourViewerSelectedTourIds(tourViewer);

      if (selectedItems == null || selectedItems.length == 0) {

         MessageDialog.openInformation(Display.getDefault().getActiveShell(),
               Messages.Dialog_DeleteTourValues_Dialog_Title,
               Messages.Dialog_ModifyTours_Dialog_ToursAreNotSelected);

         return;
      }

      /*
       * convert selection to array
       */
      final Long[] selectedTourIds = new Long[selectedItems.length];
      for (int i = 0; i < selectedItems.length; i++) {
         selectedTourIds[i] = (Long) selectedItems[i];
      }

      final IRunnableWithProgress importRunnable = new IRunnableWithProgress() {

         Display display = Display.getDefault();

         @Override
         public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

            int imported = 0;
            final int importSize = selectedTourIds.length;

            monitor.beginTask(Messages.Import_Data_Dialog_DeleteTourValues_Task, importSize);

            // loop: all selected tours in the viewer
            for (final Long selectedTourId : selectedTourIds) {

               monitor.worked(1);
               monitor.subTask(NLS.bind(
                     Messages.Import_Data_Dialog_Reimport_SubTask,
                     new Object[] { ++imported, importSize }));

               final TourData tourData = TourManager.getTour(selectedTourId);

               if (tourData == null) {
                  continue;
               }

               deleteTourValuesFromTour(tourValueTypes, tourData);

               if (monitor.isCanceled()) {

                  // user has canceled the deletion -> ask if the whole deletion should be canceled

                  final boolean[] isCancelDeletion = { false };

                  display.syncExec(() -> {

                     if (MessageDialog.openQuestion(display.getActiveShell(),
                           Messages.Import_Data_Dialog_IsCancelTourValuesDeletion_Title,
                           Messages.Import_Data_Dialog_IsCancelTourValuesDeletion_Message)) {

                        isCancelDeletion[0] = true;

                     }
                  });

                  if (isCancelDeletion[0]) {
                     break;
                  }
               }
            }

            updateTourData_InImportView_FromDb(monitor);

            // reselect tours, run in UI thread
            display.asyncExec(tourViewer::reloadViewer);
         }
      };

      try {
         new ProgressMonitorDialog(Display.getDefault().getActiveShell()).run(true, true, importRunnable);
      } catch (final Exception e) {
         TourLogManager.log_EXCEPTION_WithStacktrace(e);
      } finally {

         TourLogManager.log_DEFAULT(String.format(
               RawDataManager.LOG_DELETE_TOURVALUES_END,
               (System.currentTimeMillis() - start) / 1000.0));
      }
   }

   public void deleteTourValuesFromTour(final List<TourValueType> tourValueTypes,
                                        final TourData tourData) {

      final Long tourId = tourData.getTourId();

      /*
       * Tour must be removed otherwise it would be recognized as a duplicate and therefore not
       * imported
       */
      final TourData oldTourDataInImportView = _allImportedTours.remove(tourId);

      TourData clonedTourData = null;
      try {

         clonedTourData = (TourData) tourData.clone();

         // loop: For each tour value type, we save the associated data for future display
         //to compare with the new data
         for (final TourValueType tourValueType : tourValueTypes) {

            switch (tourValueType) {

            case TIME_SLICES__BATTERY:

               tourData.setBattery_Percentage(null);
               tourData.setBattery_Time(null);
               break;

            case TIME_SLICES__CADENCE:

               clonedTourData.setCadenceSerie(tourData.getCadenceSerie());
               clonedTourData.setAvgCadence(tourData.getAvgCadence());
               clonedTourData.setCadenceMultiplier(tourData.getCadenceMultiplier());

               tourData.setCadenceSerie(null);
               tourData.setAvgCadence(0);
               tourData.setCadenceMultiplier(0);
               tourData.setCadenceZone_SlowTime(0);
               tourData.setCadenceZone_FastTime(0);
               break;

            case TIME_SLICES__ELEVATION:

               clonedTourData.setTourAltDown(tourData.getTourAltDown());
               clonedTourData.setTourAltUp(tourData.getTourAltUp());

               tourData.altitudeSerie = null;
               tourData.setTourAltUp(0);
               tourData.setTourAltDown(0);
               break;

            case TIME_SLICES__GEAR:

               clonedTourData.setFrontShiftCount(tourData.getFrontShiftCount());
               clonedTourData.setRearShiftCount(tourData.getRearShiftCount());

               tourData.setFrontShiftCount(0);
               tourData.setRearShiftCount(0);
               break;

            case TIME_SLICES__POWER_AND_PULSE:

               clonedTourData.setPower_Avg(tourData.getPower_Avg());
               clonedTourData.setAvgPulse(tourData.getAvgPulse());

               tourData.setPowerSerie(null);
               tourData.setPower_Avg(0);
               tourData.pulseSerie = null;
               tourData.pulseTime_Milliseconds = null;
               tourData.pulseTime_TimeIndex = null;
               tourData.setAvgPulse(0);
               break;

            case TIME_SLICES__POWER_AND_SPEED:

               clonedTourData.setPower_Avg(tourData.getPower_Avg());

               tourData.setPowerSerie(null);
               tourData.setPower_Avg(0);
               tourData.setSpeedSerie(null);
               break;

            case TIME_SLICES__TEMPERATURE:

               clonedTourData.setAvgTemperature(tourData.getAvgTemperature());

               tourData.temperatureSerie = null;
               tourData.setAvgTemperature(0);
               break;

            case TIME_SLICES__TIME:

               for (int index = 0; index < tourData.timeSerie.length; ++index) {
                  tourData.timeSerie[index] = 0;
               }

               tourData.setTourDeviceTime_Elapsed(0);
               tourData.setPausedTime_Start(null);
               tourData.setPausedTime_End(null);
               tourData.setTourDeviceTime_Paused(0);
               tourData.setTourDeviceTime_Recorded(0);
               tourData.setTourComputedTime_Moving(0);
               tourData.clearComputedSeries();
               break;

            case TIME_SLICES__TIMER_PAUSES:

               clonedTourData.setTourDeviceTime_Paused(tourData.getTourDeviceTime_Paused());

               tourData.setPausedTime_Start(null);
               tourData.setPausedTime_End(null);
               tourData.setTourDeviceTime_Paused(0);
               tourData.setTourDeviceTime_Recorded(tourData.getTourDeviceTime_Elapsed());
               break;

            case TOUR__CALORIES:

               clonedTourData.setCalories(tourData.getCalories());

               tourData.setCalories(0);
               break;

            case TOUR__IMPORT_FILE_LOCATION:

               clonedTourData.setImportFilePath(tourData.getImportFilePathName());
               break;

            case TOUR__MARKER:

               clonedTourData.setTourMarkers(new HashSet<>(tourData.getTourMarkers()));

               tourData.setTourMarkers(new HashSet<>());
               break;

            case ALL_TIME_SLICES:
            case ENTIRE_TOUR:
            default:
               break;
            }
         }
      } catch (final CloneNotSupportedException e) {
         StatusUtil.log(e);
      }

      final TourData saveTourData = TourManager.saveModifiedTour(tourData, false);

      TourLogManager.showLogView();
      TourLogManager.subLog_OK(saveTourData.getTourStartTime().format(TimeTools.Formatter_DateTime_S));

      for (final TourValueType tourValueType : tourValueTypes) {
         displayTourModifiedDataDifferences(tourValueType, clonedTourData, saveTourData);
      }

      // check if tour is displayed in the import view
      if (oldTourDataInImportView != null) {

         // replace tour data in the import view

         _allImportedTours.put(saveTourData.getTourId(), saveTourData);
      }
   }

   public DeviceData getDeviceData() {
      return _deviceData;
   }

   /**
    * @return Returns filenames of all imported tour files which are displayed in the import view
    */
   public ConcurrentHashMap<String, String> getImportedFiles() {
      return _allImportedFileNames;
   }

   /**
    * @return Returns all {@link TourData} which has been imported or received and are displayed in
    *         the import view, tour id is the key.
    */
   public Map<Long, TourData> getImportedTours() {
      return _allImportedTours;
   }

   /**
    * @return Returns an {@link ArrayList} containing the imported tours.
    */
   public ArrayList<TourData> getImportedTours_AsList() {

      final Collection<TourData> importedToursCollection = _allImportedTours.values();
      final ArrayList<TourData> importedTours = new ArrayList<>(importedToursCollection);

      return importedTours;
   }

   /**
    * @return Returns the import year or <code>-1</code> when the year was not set
    */
   public int getImportYear() {
      return _importState_ImportYear;
   }

   public ConcurrentHashMap<String, Object> getInvalidFilesList() {
      return _allInvalidFiles;
   }

   /**
    * Ask user for the replacement options and set the selected replacement options into
    * {@link #_selectedImportFilenameReplacementOption}
    *
    * @param dbTourData
    * @param importedFilePathName
    * @param dbFilePathName
    */
   private synchronized void getSelectedImportFilenameReplacementOption(final TourData dbTourData,
                                                                        final String importedFilePathName) {

      if (_selectedImportFilenameReplacementOption == ReplaceImportFilenameAction.DO_NOTHING
            || _selectedImportFilenameReplacementOption == ReplaceImportFilenameAction.REPLACE_IMPORT_FILENAME_IN_SAVED_TOUR//
      ) {

         Display.getDefault().syncExec(() -> {

            final String dbFilePathName = dbTourData.getImportFilePathName();

            final String message = NLS.bind(Messages.Dialog_ImportData_ReplaceImportFilename_Message,
                  new Object[] {
                        TourManager.getTourDateTimeShort(dbTourData),
                        importedFilePathName,
                        dbFilePathName == null
                              ? Messages.App_Label_NotAvailable
                              : dbFilePathName
                  });

            final String[] allOptions = new String[] {

                  Messages.Dialog_ImportData_ReplaceImportFilename_Radio_DoNothing, //          0
                  Messages.Dialog_ImportData_ReplaceImportFilename_Radio_DoNothingAnymore, //   1
                  Messages.Dialog_ImportData_ReplaceImportFilename_Radio_ReplaceThis, //        2
                  Messages.Dialog_ImportData_ReplaceImportFilename_Radio_ReplaceAll //          3
            };

            final int defaultOption = _selectedImportFilenameReplacementOption == ReplaceImportFilenameAction.REPLACE_IMPORT_FILENAME_IN_SAVED_TOUR

                  ? 2 // ReplaceImportFilenameAction.REPLACE_IMPORT_FILENAME_IN_SAVED_TOUR
                  : 0 // ReplaceImportFilenameAction.DO_NOTHING
            ;

            final MessageDialogWithRadioOptions dialog = new MessageDialogWithRadioOptions(
                  Display.getDefault().getActiveShell(),
                  Messages.Dialog_ImportData_ReplaceImportFilename_Title,
                  null,
                  message,
                  MessageDialog.QUESTION,
                  0, // OK button
                  new String[] {
                        IDialogConstants.OK_LABEL,
                        IDialogConstants.CANCEL_LABEL //
                  });

            dialog.setRadioOptions(allOptions, defaultOption);

            if (dialog.open() == Window.OK) {

               _selectedImportFilenameReplacementOption = getSelectedImportFilenameReplacementOption_10(dialog.getSelectedOption());

            } else {

               // dialog is canceled
               _selectedImportFilenameReplacementOption = ReplaceImportFilenameAction.DO_NOTHING;
            }
         });
      }
   }

   private ReplaceImportFilenameAction getSelectedImportFilenameReplacementOption_10(final int selectedOption) {

      switch (selectedOption) {
      case 1:
         return ReplaceImportFilenameAction.DO_NOTHING_AND_DONT_ASK_AGAIN;

      case 2:
         return ReplaceImportFilenameAction.REPLACE_IMPORT_FILENAME_IN_SAVED_TOUR;

      case 3:
         return ReplaceImportFilenameAction.REPLACE_IMPORT_FILENAME_IN_ALL_SAVED_TOUR;

      case 0:
      default:
         return ReplaceImportFilenameAction.DO_NOTHING;
      }
   }

   public ArrayList<TourTag> getTempTourTags() {
      return _tempTourTags;
   }

   public ArrayList<TourType> getTempTourTypes() {
      return _tempTourTypes;
   }

   /**
    * Import tours from multiple files. The imported tours can be retrieved with
    * {@link #getImportedTours()}
    *
    * @param allImportFiles
    * @param isEasyImport
    * @param fileGlobPattern
    * @param importStates
    * @return
    */
   public void importTours_FromMultipleFiles(final ArrayList<OSFile> allImportFiles,
                                             final String fileGlobPattern,
                                             final ImportStates importStates) {

      if (allImportFiles.isEmpty()) {
         return;
      }

      final long start = System.currentTimeMillis();

      /*
       * Log import
       */
      final String css = importStates.isEasyImport
            ? UI.EMPTY_STRING
            : TourLogView.CSS_LOG_TITLE;

      final String message = importStates.isEasyImport
            ? String.format(EasyImportManager.LOG_EASY_IMPORT_002_TOUR_FILES_START, fileGlobPattern)
            : RawDataManager.LOG_IMPORT_TOUR;

      if (importStates.isLog_DEFAULT) {
         TourLogManager.addLog(TourLogState.DEFAULT, message, css);
      }

      final List<ImportFile> allImportFilePaths = new ArrayList<>();

      /*
       * Convert to IPath because NIO Path DO NOT SUPPORT EXTENSIONS :-(((
       */
      for (final OSFile osFile : allImportFiles) {

         final String absolutePath = osFile.getPath().toString();
         final org.eclipse.core.runtime.Path iPath = new org.eclipse.core.runtime.Path(absolutePath);

         final ImportFile importFile = new ImportFile(iPath);
         importFile.isBackupImportFile = osFile.isBackupImportFile;

         allImportFilePaths.add(importFile);
      }

      /*
       * Resort files by extension priority
       */
      Collections.sort(allImportFilePaths, (importFilePath1, importFilePath2) -> {

         final String file1Extension = importFilePath1.filePath.getFileExtension();
         final String file2Extension = importFilePath2.filePath.getFileExtension();

         if (file1Extension != null
               && file1Extension.length() > 0
               && file2Extension != null
               && file2Extension.length() > 0) {

            final TourbookDevice file1Device = _allDevices_ByExtension.get(file1Extension.toLowerCase());
            final TourbookDevice file2Device = _allDevices_ByExtension.get(file2Extension.toLowerCase());

            if (file1Device != null && file2Device != null) {
               return file1Device.extensionSortPriority - file2Device.extensionSortPriority;
            }
         }

         // sort invalid files to the end
         return Integer.MAX_VALUE;
      });

      importTours_FromMultipleFiles_10(allImportFilePaths, importStates);

      TourLogManager.log_DEFAULT(String.format(

            importStates.isEasyImport
                  ? EasyImportManager.LOG_EASY_IMPORT_002_END
                  : RawDataManager.LOG_IMPORT_TOUR_END,

            (System.currentTimeMillis() - start) / 1000.0));

   }

   private void importTours_FromMultipleFiles_10(final List<ImportFile> allImportFilePaths,
                                                 final ImportStates importStates) {

      final int numAllFiles = allImportFilePaths.size();

      /*
       * Setup concurrency
       */
      _importTour_CountDownLatch = new CountDownLatch(numAllFiles);
      _importTour_Queue.clear();

      final IRunnableWithProgress importRunnable = new IRunnableWithProgress() {

         @Override
         public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

            monitor.beginTask(Messages.import_data_importTours_task, numAllFiles);

            final long startTime = System.currentTimeMillis();
            long lastUpdateTime = startTime;

            final AtomicInteger numImportedFiles = new AtomicInteger();

            int numLastWorked = 0;

            // loop: import all selected files
            for (final ImportFile filePath : allImportFilePaths) {

               if (monitor.isCanceled()) {

                  // stop importing but process imported tours

                  importStates.isImportCanceled_ByMonitor = true;

                  /*
                   * Count down all, that the import task can finish but process imported tours
                   */
                  long numCounts = _importTour_CountDownLatch.getCount();
                  while (numCounts-- > 0) {
                     _importTour_CountDownLatch.countDown();
                  }

                  break;
               }

               final long currentTime = System.currentTimeMillis();
               final long timeDiff = currentTime - lastUpdateTime;

               // reduce logging
               if (timeDiff > 1000) {

                  lastUpdateTime = currentTime;

                  final int numWorked = numImportedFiles.get();

                  // "{0} / {1} - {2} % - {3} Δ"
                  UI.showWorkedInProgressMonitor(monitor, numWorked, numAllFiles, numLastWorked);

                  numLastWorked = numWorked;
               }

               // ignore files which are imported as children from other imported files
               if (_allImportedFileNamesChildren.contains(filePath.osFilePath)) {

                  _importTour_CountDownLatch.countDown();

                  continue;
               }

               importTours_FromMultipleFiles_20_Concurrent(

                     filePath,
                     numImportedFiles,
                     monitor,
                     importStates);
            }

            // wait until all imports are performed
            _importTour_CountDownLatch.await();

            /*
             * Do post import actions
             */
            save_InvalidFilesToIgnore_InTxt();

            if (numImportedFiles.get() > 0) {

               updateTourData_InImportView_FromDb(monitor);

               Display.getDefault().syncExec(() -> {

                  final RawDataView view = showRawDataView();

                  if (view != null) {
                     view.reloadViewer();

                     if (importStates.isEasyImport == false) {

                        // first tour is selected later
                        view.selectFirstTour();
                     }
                  }
               });
            }
         }

      };

      try {

         new ProgressMonitorDialog(Display.getDefault().getActiveShell()).run(true, true, importRunnable);

      } catch (final Exception e) {
         TourLogManager.log_EXCEPTION_WithStacktrace(e);
      }
   }

   private void importTours_FromMultipleFiles_20_Concurrent(final ImportFile filePath,
                                                            final AtomicInteger numImportedFiles,
                                                            final IProgressMonitor monitor,
                                                            final ImportStates importStates) {
      try {

         // put file path into the queue AND wait when it is full

         _importTour_Queue.put(filePath);

      } catch (final InterruptedException e) {

         TourLogManager.log_EXCEPTION_WithStacktrace(e);
         Thread.currentThread().interrupt();
      }

      _importTour_Executor.submit(() -> {

         // get last added item
         final ImportFile queueItem_FilePath = _importTour_Queue.poll();

         try {

            if (queueItem_FilePath != null) {

               importTours_FromMultipleFiles_30_OneFile(

                     filePath,
                     importStates);
            }

         } finally {

            monitor.worked(1);
            numImportedFiles.incrementAndGet();

            _importTour_CountDownLatch.countDown();
         }
      });
   }

   private void importTours_FromMultipleFiles_30_OneFile(final ImportFile filePath,
                                                         final ImportStates importStates) {

      final String osFilePath = filePath.osFilePath;
      File importFile = new File(osFilePath);

      if (FileSystemManager.isFileFromTourBookFileSystem(osFilePath)) {

         importFile = FileSystemManager.CopyLocally(osFilePath);
      }

      final Map<Long, TourData> allImportedToursFromOneFile = new HashMap<>();

//      if ("C:\\Users\\Wolfgang\\Desktop\\ALL my Device Data\\all xml files\\2010-11-14_13-08-51.gpx.sta".equals(osFilePath)) {
//         int a = 0;
//         a++;
//      }

      if ("C:\\Users\\Wolfgang\\Desktop\\ALL my Device Data\\all xml files\\2009-03-27-13-21-38.tcx".equals(osFilePath)) {
         int a = 0;
         a++;
      }

      if (importTours_FromOneFile(

            importFile, //                   importFile
            null, //                         destinationPath
            null, //                         fileCollision
            false, //                        isBuildNewFileNames
            true, //                         isTourDisplayedInImportView
            allImportedToursFromOneFile,
            importStates //
      )) {

         // update state
         for (final TourData importedTourData : allImportedToursFromOneFile.values()) {

            importedTourData.isBackupImportFile = filePath.isBackupImportFile;

            if (importStates.isLog_OK) {

               TourLogManager.subLog_OK(NLS.bind(LOG_IMPORT_TOUR_IMPORTED,
                     importedTourData.getTourStartTime().format(TimeTools.Formatter_DateTime_S),
                     osFilePath));
            }
         }

         final int numImportedToursFromOneFile = allImportedToursFromOneFile.size();

         // reduce log noise: log only when more than ONE tour is imported from ONE file
         if (importStates.isLog_INFO && numImportedToursFromOneFile > 1) {

            TourLogManager.subLog_INFO(NLS.bind(LOG_IMPORT_TOURS_IMPORTED_FROM_FILE,
                  numImportedToursFromOneFile,
                  osFilePath));
         }

      } else {

         _allInvalidFiles.put(osFilePath, new Object());

         TourLogManager.subLog_ERROR(osFilePath);
      }

      if (FileSystemManager.isFileFromTourBookFileSystem(osFilePath)) {

         // Delete the temporary created file
         FilesUtils.deleteIfExists(importFile.toPath());
      }
   }

   /**
    * Import multiple tours from one file, all imported tours can be retrieved with
    * {@link #getImportedTours()}
    *
    * @param importFile
    *           The file to be imported
    * @param destinationPath
    *           If not <code>null</code> copy the file to this path
    * @param fileCollision
    *           Behavior if destination file exists (ask if null)
    * @param isBuildNewFileNames
    *           If <code>true</code> create a new filename depending on the content of the file,
    *           keep old name if <code>false</code>
    * @param isTourDisplayedInImportView
    *           When <code>true</code>, the newly imported tours are displayed in the import view,
    *           otherwise they are imported into {@link #_multipleTours_FromLastImportFile} but
    *           not displayed in the import view.
    * @param allImportedTourDataFromOneFile
    *           Contains all tours which are imported from <code>importFile</code>
    * @param importStates
    * @return Returns <code>true</code> when the import was successfully
    */
   public boolean importTours_FromOneFile(final File importFile,
                                          final String destinationPath,
                                          final FileCollisionBehavior fileCollision,
                                          final boolean isBuildNewFileNames,
                                          final boolean isTourDisplayedInImportView,
                                          final Map<Long, TourData> allImportedTourDataFromOneFile,
                                          final ImportStates importStates) {

      final String importFilePathName = importFile.getAbsolutePath();
      final Display display = Display.getDefault();

      // check if importFile exist
      if (importFile.exists() == false) {

         display.syncExec(() -> {

            final Shell activeShell = display.getActiveShell();

            // during initialization there is no active shell
            if (activeShell != null) {

               MessageDialog.openError(
                     activeShell,
                     Messages.DataImport_Error_file_does_not_exist_title,
                     NLS.bind(Messages.DataImport_Error_file_does_not_exist_msg, importFilePathName));
            }
         });

         return false;
      }

      // find the file extension in the filename
      final int dotPos = importFilePathName.lastIndexOf(UI.SYMBOL_DOT);
      if (dotPos == -1) {
         return false;
      }
      final String fileExtension = importFilePathName.substring(dotPos + 1);

      final boolean[] importReturnValue = { false };
      final String[] lastImportedFileName = { null };

      BusyIndicator.showWhile(null, () -> {

         boolean isDataImported = false;
         final ArrayList<String> additionalImportedFiles = new ArrayList<>();

         if ("C:\\Users\\Wolfgang\\Desktop\\ALL my Device Data\\all xml files\\2010-11-14_13-08-51.gpx.sta".equals(importFilePathName)) {
            int a = 0;
            a++;
         }
         if ("C:\\Users\\Wolfgang\\Desktop\\ALL my Device Data\\all xml files\\2009-03-27-13-21-38.tcx".equals(importFilePathName)) {
            int a = 0;
            a++;
         }

         /*
          * Try to import from all devices which have the defined extension
          */
         for (final TourbookDevice device1 : _allDevices_BySortPriority) {

            final String deviceFileExtension = device1.fileExtension;

            if (deviceFileExtension.equals(UI.SYMBOL_STAR)
                  || deviceFileExtension.equalsIgnoreCase(fileExtension)) {

               // Check if the file we want to import requires confirmation and if yes, ask user
               if (device1.userConfirmationRequired()) {

                  display.syncExec(() -> {

                     final Shell activeShell = display.getActiveShell();

                     if (activeShell != null) {
                        if (MessageDialog.openConfirm(
                              display.getActiveShell(),
                              NLS.bind(Messages.DataImport_ConfirmImport_title, device1.visibleName),
                              device1.userConfirmationMessage())) {
                           importStates.isImportCanceled_ByUserDialog = false;
                        } else {
                           importStates.isImportCanceled_ByUserDialog = true;
                        }
                     }
                  });
               }

               if (importStates.isImportCanceled_ByUserDialog) {
                  importReturnValue[0] = true; // don't display an error to the user
                  return;
               }

               // device file extension was found in the filename extension
               lastImportedFileName[0] = importTours_FromOneFile_10(
                     device1,
                     importFilePathName,
                     destinationPath,
                     fileCollision,
                     isBuildNewFileNames,
                     isTourDisplayedInImportView,
                     importStates,
                     allImportedTourDataFromOneFile);

               if (lastImportedFileName[0] != null) {

                  isDataImported = true;
                  importReturnValue[0] = true;

                  final ArrayList<String> deviceImportedFiles = device1.getAdditionalImportedFiles();
                  if (deviceImportedFiles != null) {
                     additionalImportedFiles.addAll(deviceImportedFiles);
                  }

                  break;
               }

               if (importStates.isImportCanceled_ByUserDialog) {
                  break;
               }
            }
         }

         if (isDataImported == false && !importStates.isImportCanceled_ByUserDialog) {

            /*
             * When data has not imported yet, try all available devices without checking the
             * file extension
             */
            for (final TourbookDevice device2 : _allDevices_BySortPriority) {

               lastImportedFileName[0] = importTours_FromOneFile_10(
                     device2,
                     importFilePathName,
                     destinationPath,
                     fileCollision,
                     isBuildNewFileNames,
                     isTourDisplayedInImportView,
                     importStates,
                     allImportedTourDataFromOneFile);

               if (lastImportedFileName[0] != null) {

                  isDataImported = true;
                  importReturnValue[0] = true;

                  final ArrayList<String> otherImportedFiles = device2.getAdditionalImportedFiles();
                  if (otherImportedFiles != null) {
                     additionalImportedFiles.addAll(otherImportedFiles);
                  }

                  break;
               }
            }
         }

         if (isDataImported) {

            _allImportedFileNames.put(lastImportedFileName[0], lastImportedFileName[0]);

            if (!additionalImportedFiles.isEmpty()) {

               for (final String fileName : additionalImportedFiles) {
                  _allImportedFileNamesChildren.put(fileName, fileName);
               }
            }
         }

         // cleanup
         additionalImportedFiles.clear();
      });

      return importReturnValue[0];
   }

   /**
    * import the raw data of the given file
    *
    * @param device
    *           the device which is able to process the data of the file
    * @param sourceFileName
    *           the file to be imported
    * @param destinationPath
    *           if not null copy the file to this path
    * @param fileCollision
    *           behavior if destination file exists (ask if null)
    * @param isBuildNewFileName
    *           if true create a new filename depending on the content of the file, keep old name if
    *           false
    * @param isTourDisplayedInImportView
    * @param allNewlyImportedToursFromOneFile
    * @return Returns the import filename or <code>null</code> when it was not imported
    */
   private String importTours_FromOneFile_10(final TourbookDevice device,
                                             String sourceFileName,
                                             final String destinationPath,
                                             FileCollisionBehavior fileCollision,
                                             final boolean isBuildNewFileName,
                                             final boolean isTourDisplayedInImportView,
                                             final ImportStates importStates,
                                             final Map<Long, TourData> allNewlyImportedToursFromOneFile) {

      if (fileCollision == null) {
         fileCollision = new FileCollisionBehavior();
      }

      device.setIsChecksumValidation(_importState_IsHAC4_5_ChecksumValidation);

      if (device.validateRawData(sourceFileName)) {

         // file contains valid raw data for the raw data reader

         if (_importState_ImportYear != -1) {
            device.setImportYear(_importState_ImportYear);
         }

         device.setMergeTracks(_importState_IsMergeTracks);
         device.setCreateTourIdWithTime(_importState_IsCreateTourIdWithTime);
         device.setConvertWayPoints(_importState_IsConvertWayPoints);

         // copy file to destinationPath
         if (destinationPath != null) {

            final String newFileName = importTours_FromOneFile_20_CopyFile(
                  device,
                  sourceFileName,
                  destinationPath,
                  isBuildNewFileName,
                  fileCollision,
                  importStates);

            if (newFileName == null) {
               return null;
            }

            sourceFileName = newFileName;
         }

         boolean isImported = false;

         try {

            isImported = device.processDeviceData(
                  sourceFileName,
                  _deviceData,
                  _allImportedTours,
                  allNewlyImportedToursFromOneFile,
                  importStates);

         } catch (final Exception e) {
            TourLogManager.log_EXCEPTION_WithStacktrace(e);
         }

         if (isTourDisplayedInImportView) {
            _allImportedTours.putAll(allNewlyImportedToursFromOneFile);
         }

         // keep tours in _newlyImportedTours because they are used when tours are re-imported

         return isImported
               ? sourceFileName
               : null;
      }

      return null;

   }

   private String importTours_FromOneFile_20_CopyFile(final TourbookDevice device,
                                                      final String sourceFileName,
                                                      final String destinationPath,
                                                      final boolean isBuildNewFileName,
                                                      final FileCollisionBehavior fileCollision,
                                                      final ImportStates importStates) {

      String destFileName = new File(sourceFileName).getName();

      if (isBuildNewFileName) {

         destFileName = null;

         try {
            destFileName = device.buildFileNameFromRawData(sourceFileName);
         } catch (final Exception e) {
            TourLogManager.log_EXCEPTION_WithStacktrace(e);
         } finally {

            if (destFileName == null) {

               final String dialogMessage = NLS.bind(
                     Messages.Import_Data_Error_CreatingFileName_Message,
                     new Object[] {
                           sourceFileName,
                           new org.eclipse.core.runtime.Path(destinationPath)
                                 .addTrailingSeparator()
                                 .toString(), TEMP_IMPORTED_FILE });

               MessageDialog.openError(
                     Display.getDefault().getActiveShell(),
                     Messages.Import_Data_Error_CreatingFileName_Title,
                     dialogMessage);

               destFileName = TEMP_IMPORTED_FILE;
            }
         }
      }
      final File newFile = new File(
            (new org.eclipse.core.runtime.Path(destinationPath)
                  .addTrailingSeparator()
                  .toString() + destFileName));

      // get source file
      final File fileIn = new File(sourceFileName);

      // check if file already exist
      if (newFile.exists()) {

         // TODO allow user to rename the file

         boolean keepFile = false; // for MessageDialog result
         if (fileCollision.value == FileCollisionBehavior.ASK) {

            final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
            final MessageDialog messageDialog = new MessageDialog(
                  shell,
                  Messages.Import_Wizard_Message_Title,
                  null,
                  NLS.bind(Messages.Import_Wizard_Message_replace_existing_file, newFile),
                  MessageDialog.QUESTION,
                  new String[] {
                        IDialogConstants.YES_LABEL,
                        IDialogConstants.YES_TO_ALL_LABEL,
                        IDialogConstants.NO_LABEL,
                        IDialogConstants.NO_TO_ALL_LABEL },
                  0);
            messageDialog.open();
            final int returnCode = messageDialog.getReturnCode();
            switch (returnCode) {

            case 1: // YES_TO_ALL
               fileCollision.value = FileCollisionBehavior.REPLACE;
               break;

            case 3: // NO_TO_ALL
               fileCollision.value = FileCollisionBehavior.KEEP;
            case 2: // NO
               keepFile = true;
               break;

            default:
               break;
            }
         }

         if (fileCollision.value == FileCollisionBehavior.KEEP || keepFile) {
            importStates.isImportCanceled_ByUserDialog = true;
            fileIn.delete();
            return null;
         }
      }

      // copy source file into destination file
      try (FileInputStream inReader = new FileInputStream(fileIn);
            FileOutputStream outReader = new FileOutputStream(newFile)) {

         int c;

         while ((c = inReader.read()) != -1) {
            outReader.write(c);
         }

      } catch (final IOException e) {
         TourLogManager.log_EXCEPTION_WithStacktrace(e);
         return null;
      }

      // delete source file
      fileIn.delete();

      return newFile.getAbsolutePath();
   }

   /**
    * Re-imports a tour and specifically the data specified.
    *
    * @param oldTourData
    *           The tour to re-import
    * @param tourValueTypes
    *           A list of tour values to be re-imported
    * @param reImportStatus
    * @param importStates
    * @return Returns <code>true</code> when <code>oldTourData</code> was reimported, otherwise
    *         <code>false</code>
    */
   public boolean reimportTour(final TourData oldTourData,
                               final List<TourValueType> tourValueTypes,
                               final ReImportStatus reImportStatus,
                               final ImportStates importStates) {

      if (oldTourData.isManualTour()) {

         /**
          * Manually created tours cannot be re-imported, there is no import file path
          * <p>
          * It took a very long time (years) until this case was discovered
          */
         if (importStates.isLog_INFO) {

            TourLogManager.subLog_INFO(NLS.bind(
                  LOG_REIMPORT_MANUAL_TOUR,
                  oldTourData.getTourStartTime().format(TimeTools.Formatter_DateTime_S)));
         }

         return false;
      }

      boolean isReImported = false;

      final File currentTourImportFile = reimportTour_10_GetImportFile(
            oldTourData,
            importStates,
            reImportStatus);

      if (currentTourImportFile == null) {

         /*
          * User canceled file dialog -> continue with next file, it is possible that a
          * tour file could not be reselected because it is not available any more
          */
         String reason;
         if (reImportStatus.isCanceled_Auto_ImportFilePathIsEmpty) {
            reason = Messages.Log_Reimport_Tour_Skipped_FilePathIsEmpty;

         } else if (reImportStatus.isCanceled_Auto_TheFileLocationDialog) {
            reason = Messages.Log_Reimport_Tour_Skipped_FileLocationDialog_Auto;

         } else if (reImportStatus.isCanceled_ByUser_TheFileLocationDialog) {
            reason = Messages.Log_Reimport_Tour_Skipped_FileLocationDialog_ByUser;

         } else {
            reason = Messages.Log_Reimport_Tour_Skipped_OtherReasons;
         }

         TourLogManager.subLog_ERROR(NLS.bind(
               LOG_REIMPORT_TOUR_SKIPPED,
               oldTourData.getTourStartTime().format(TimeTools.Formatter_DateTime_S),
               reason));

      } else {

         // import file is available

         isReImported = reimportTour_20(

               tourValueTypes,
               currentTourImportFile,
               oldTourData,
               importStates

         );
      }

      if (isReImported) {
         reImportStatus.isAnyTourReImported.set(true);
      }

      return isReImported;
   }

   /**
    * @param tourData
    * @param importStates
    *           Indicates whether to re-import or not a tour for which the file is not found
    * @param reImportStatus
    * @return Returns <code>null</code> when the user has canceled the file dialog.
    */
   private File reimportTour_10_GetImportFile(final TourData tourData,
                                              final ImportStates importStates,
                                              final ReImportStatus reImportStatus) {

      final String[] reimportFilePathName = { null };

      boolean isFilePathAvailable = false;

      // get import file name which is kept in the tour
      final String savedImportFilePathName = tourData.getImportFilePathName();

      if (savedImportFilePathName == null) {

         // import filepath is not available

         // The user doesn't want to look for a new file path for the current tour
         if (importStates.isSkipToursWithFileNotFound) {

            reImportStatus.isCanceled_Auto_ImportFilePathIsEmpty = true;

            return null;
         }

      } else {

         final File savedImportFile = new File(savedImportFilePathName);
         if (savedImportFile.exists()) {

            reimportFilePathName[0] = savedImportFilePathName;

            isFilePathAvailable = true;
         }
      }

      if (isFilePathAvailable == false) {

         // request file path only when necessary otherwise it would block concurrency

         reimportTour_12_RequestFileFromUser(
               tourData,
               importStates,
               reImportStatus,
               savedImportFilePathName,
               reimportFilePathName);
      }

      if (reimportFilePathName[0] == null) {

         // user has canceled the file dialog

         return null;

      } else {

         /*
          * Keep selected file path which is used to re-import the following tours from the same
          * folder
          * that the user do not have to reselect again and again.
          */
         final IPath currentReimportFolder = new org.eclipse.core.runtime.Path(reimportFilePathName[0]).removeLastSegments(1);

         _previousReimportFolder = currentReimportFolder;
         _allPreviousReimportFolders.put(currentReimportFolder, new Object());

         return new File(reimportFilePathName[0]);
      }
   }

   private void reimportTour_12_RequestFileFromUser(final TourData tourData,
                                                    final ImportStates importStates,
                                                    final ReImportStatus reImportStatus,
                                                    final String savedImportFilePathName,
                                                    final String[] outReimportFilePathName) {

      synchronized (this) {

         if (reImportStatus.isCanceled_WholeReimport.get()) {
            return;
         }

         Display.getDefault().syncExec(() -> {

            final Shell activeShell = Display.getDefault().getActiveShell();

            try {

               if (savedImportFilePathName == null) {

                  // import filepath is not available, in older versions the file path name is not saved in the tour

                  final String tourDateTimeShort = TourManager.getTourDateTimeShort(tourData);

                  final boolean okPressed = MessageDialog.openConfirm(
                        activeShell,
                        NLS.bind(Messages.Import_Data_Dialog_Reimport_Title, tourDateTimeShort),
                        NLS.bind(Messages.Import_Data_Dialog_GetReimportedFilePath_Message,
                              tourDateTimeShort,
                              tourDateTimeShort));

                  // The user doesn't want to look for a new file path for the current tour
                  if (!okPressed) {

                     reImportStatus.isCanceled_ByUser_TheFileLocationDialog = true;

                     return;
                  }

               } else {

                  // import filepath is available

                  for (final IPath previousReimportFolder : _allPreviousReimportFolders.keySet()) {

                     /*
                      * Try to use a folder from a previously re-imported tour
                      */

                     final String oldImportFileName = new org.eclipse.core.runtime.Path(savedImportFilePathName).lastSegment();
                     final IPath newImportFilePath = previousReimportFolder.append(oldImportFileName);

                     final String newImportFilePathName = newImportFilePath.toOSString();
                     final File newImportFile = new File(newImportFilePathName);
                     if (newImportFile.exists()) {

                        // re-import file exists in the same folder
                        outReimportFilePathName[0] = newImportFilePathName;
                     }
                  }

                  if (outReimportFilePathName[0] == null) {

                     //The user doesn't want to look for a new file path for the current tour.
                     if (importStates.isSkipToursWithFileNotFound) {

                        reImportStatus.isCanceled_Auto_TheFileLocationDialog = true;

                        return;
                     }

                     final boolean okPressed = MessageDialog.openQuestion(
                           activeShell,
                           Messages.Dialog_ReimportData_Title,
                           NLS.bind(
                                 Messages.Import_Data_Dialog_GetAlternativePath_Message,
                                 savedImportFilePathName));

                     // The user doesn't want to look for a new file path for the current tour
                     if (!okPressed) {

                        reImportStatus.isCanceled_ByUser_TheFileLocationDialog = true;

                        return;
                     }
                  }
               }

               if (outReimportFilePathName[0] == null) {

                  // ask user for the import file location

                  // create dialog title
                  final String tourDateTime = tourData.getTourStartTime().format(TimeTools.Formatter_DateTime_ML);
                  final String deviceName = tourData.getDeviceName();
                  final String dataFormat = deviceName == null ? UI.EMPTY_STRING : deviceName;
                  final String fileName = savedImportFilePathName == null ? UI.EMPTY_STRING : savedImportFilePathName;
                  final String dialogTitle = String.format(Messages.Import_Data_Dialog_ReimportFile_Title,
                        tourDateTime,
                        fileName,
                        dataFormat);

                  final FileDialog dialog = new FileDialog(activeShell, SWT.OPEN);
                  dialog.setText(dialogTitle);

                  if (savedImportFilePathName != null) {

                     // select file location from the tour

                     final IPath importFilePath = new org.eclipse.core.runtime.Path(savedImportFilePathName);
                     final String importFileName = importFilePath.lastSegment();

                     dialog.setFileName(importFileName);
                     dialog.setFilterPath(savedImportFilePathName);

                  } else if (_previousReimportFolder != null) {

                     dialog.setFilterPath(_previousReimportFolder.toOSString());
                  }

                  outReimportFilePathName[0] = dialog.open();
               }

            } finally {

               if (reImportStatus.isCanceled_ByUser_TheFileLocationDialog
                     && reImportStatus.isUserAsked_ToCancelWholeReImport == false) {

                  if (MessageDialog.openQuestion(activeShell,
                        Messages.Import_Data_Dialog_IsCancelReImport_Title,
                        Messages.Import_Data_Dialog_IsCancelReImport_Message)) {

                     reImportStatus.isCanceled_WholeReimport.set(true);

                  } else {

                     reImportStatus.isUserAsked_ToCancelWholeReImport = true;
                  }
               }
            }
         });

      } // synchronized
   }

   private boolean reimportTour_20(final List<TourValueType> tourValueTypes,
                                   final File reimportedFile,
                                   final TourData oldTourData,
                                   final ImportStates importStates) {

      boolean isTourReImported = false;

      final Long oldTourId = oldTourData.getTourId();
      final String reimportFileNamePath = reimportedFile.getAbsolutePath();

      /*
       * Tour must be removed otherwise it would be recognized as a duplicate and therefore not
       * imported
       */
      boolean isRevertTour = false;
      final TourData oldTourDataInImportView = _allImportedTours.remove(oldTourId);

      final Map<Long, TourData> allImportedToursFromOneFile = new HashMap<>();

      if (importTours_FromOneFile(

            reimportedFile, //               importFile
            null, //                         destinationPath
            null, //                         fileCollision
            false, //                        isBuildNewFileNames
            false, //                        isTourDisplayedInImportView
            allImportedToursFromOneFile,
            importStates //
      )) {

         /*
          * Tour(s) could be re-imported from the file, check if it contains a valid tour
          */

         TourData updatedTourData = reimportTour_30(

               tourValueTypes,
               reimportedFile,
               oldTourData,
               allImportedToursFromOneFile);

         if (updatedTourData == null) {

            // error is already logged

            isRevertTour = true;

         } else {

            isTourReImported = true;

            // set re-import file path as new location
            updatedTourData.setImportFilePath(reimportFileNamePath);

            // check if tour is saved
            final TourPerson tourPerson = oldTourData.getTourPerson();
            if (tourPerson != null) {

               // re-save tour when the re-imported tour was already saved

               updatedTourData.setTourPerson(tourPerson);

               /*
                * Save tour but don't fire a change event because the tour editor would set the tour
                * to dirty
                */
               final TourData savedTourData = TourDatabase.saveTour_Concurrent(updatedTourData, true);

               updatedTourData = savedTourData;
            }

            if (importStates.isLog_OK) {

               TourLogManager.subLog_OK(NLS.bind(
                     LOG_IMPORT_TOUR_IMPORTED,
                     updatedTourData.getTourStartTime().format(TimeTools.Formatter_DateTime_S),
                     reimportFileNamePath));
            }

            // log the old vs new data comparison
            if (importStates.isLog_INFO) {

               final TourData tourDataDummyClone = createTourDataDummyClone(tourValueTypes, oldTourData);
               for (final TourValueType tourValueType : tourValueTypes) {
                  displayTourModifiedDataDifferences(tourValueType, tourDataDummyClone, updatedTourData);
               }
            }

            // check if tour is displayed in the import view
            if (oldTourDataInImportView != null) {

               // replace tour data in the import view

               _allImportedTours.put(updatedTourData.getTourId(), updatedTourData);
            }
         }

      } else {

         TourLogManager.subLog_ERROR(reimportFileNamePath);

         isRevertTour = true;
      }

      if (isRevertTour && oldTourDataInImportView != null) {

         // re-attach removed tour

         _allImportedTours.put(oldTourId, oldTourDataInImportView);
      }

      return isTourReImported;
   }

   /**
    * @param tourValueTypes
    *           A list of tour values to be re-imported
    * @param reImportedFile
    * @param oldTourData
    * @param allImportedToursFromOneFile
    * @return Returns {@link TourData} with the re-imported time slices or <code>null</code> when an
    *         error occurred.
    */
   private TourData reimportTour_30(final List<TourValueType> tourValueTypes,
                                    final File reImportedFile,
                                    final TourData oldTourData,
                                    final Map<Long, TourData> allImportedToursFromOneFile) {

      TourLogManager.showLogView();

      final String oldTourDateTimeShort = TourManager.getTourDateTimeShort(oldTourData);
      String message = null;

      for (final TourData reimportedTourData : allImportedToursFromOneFile.values()) {

         // skip tours which have a different tour start time
         final long reimportTourStartTime = reimportedTourData.getTourStartTimeMS();
         final long oldTourStartTime = oldTourData.getTourStartTimeMS();
         final long timeDiff = reimportTourStartTime > oldTourStartTime
               ? reimportTourStartTime - oldTourStartTime
               : oldTourStartTime - reimportTourStartTime;

         /**
          * Check time difference, this is VERY important because one .hac file contains multiple
          * tours !!!
          * <p>
          * It must be >60 seconds because db version < 7 (9.01) had no saved seconds !!!
          */
         if (timeDiff > 65_000
               // disabled for .fit files because they can have different tour start times (of some seconds)
               && !reImportedFile.getName().toLowerCase().endsWith(FILE_EXTENSION_FIT)) {

            continue;
         }

         if (oldTourData.timeSerie != null && reimportedTourData.timeSerie != null) {

            /*
             * Data series must have the same number of time slices, otherwise the markers can be
             * off the array bounds, this problem could be solved but takes time to do it.
             */
            final int oldLength = oldTourData.timeSerie.length;
            final int reimportedLength = reimportedTourData.timeSerie.length;

            if (oldLength != reimportedLength) {

               // log error
               message = NLS.bind(
                     Messages.Import_Data_Log_ReimportIsInvalid_WrongSliceNumbers,
                     new Object[] {
                           oldTourDateTimeShort,
                           reImportedFile.toString(),
                           oldLength,
                           reimportedLength });

               break;
            }
         }

         /*
          * Ensure that the re-imported tour has the same tour id
          */
         final long oldTourId = oldTourData.getTourId().longValue();
         final long reimportTourId = reimportedTourData.getTourId().longValue();

         if (oldTourId != reimportTourId) {

            message = NLS.bind(
                  Messages.Import_Data_Log_ReimportIsInvalid_DifferentTourId_Message,
                  new Object[] {
                        oldTourDateTimeShort,
                        reImportedFile.toString(),
                        oldTourId,
                        reimportTourId });

            break;
         }

         TourData newTourData = null;

         if (tourValueTypes.get(0) == TourValueType.ENTIRE_TOUR) {

            // replace complete tour

            TourManager.getInstance().removeTourFromCache(oldTourData.getTourId());

            newTourData = reimportedTourData;

            // keep body weight from old tour
            newTourData.setBodyWeight(oldTourData.getBodyWeight());

         } else {

            if (tourValueTypes.contains(TourValueType.ALL_TIME_SLICES)
                  || tourValueTypes.contains(TourValueType.TIME_SLICES__BATTERY)
                  || tourValueTypes.contains(TourValueType.TIME_SLICES__CADENCE)
                  || tourValueTypes.contains(TourValueType.TIME_SLICES__ELEVATION)
                  || tourValueTypes.contains(TourValueType.TIME_SLICES__GEAR)
                  || tourValueTypes.contains(TourValueType.TIME_SLICES__POWER_AND_PULSE)
                  || tourValueTypes.contains(TourValueType.TIME_SLICES__POWER_AND_SPEED)
                  || tourValueTypes.contains(TourValueType.TIME_SLICES__RUNNING_DYNAMICS)
                  || tourValueTypes.contains(TourValueType.TIME_SLICES__SWIMMING)
                  || tourValueTypes.contains(TourValueType.TIME_SLICES__TEMPERATURE)
                  || tourValueTypes.contains(TourValueType.TIME_SLICES__TIMER_PAUSES)
                  || tourValueTypes.contains(TourValueType.TIME_SLICES__TRAINING)) {

               // replace part of the tour

               reimportTour_40_ReplacesValues(tourValueTypes, oldTourData, reimportedTourData);
            }

            if (tourValueTypes.contains(TourValueType.TOUR__CALORIES)) {

               oldTourData.setCalories(reimportedTourData.getCalories());
            }

            if (tourValueTypes.contains(TourValueType.TOUR__MARKER)) {

               oldTourData.setTourMarkers(reimportedTourData.getTourMarkers());
            }

            if (tourValueTypes.contains(TourValueType.TOUR__IMPORT_FILE_LOCATION)) {

               // update device name which is also not set in older versions
               oldTourData.setDeviceName(reimportedTourData.getDevicePluginName());
               oldTourData.setDeviceFirmwareVersion(reimportedTourData.getDeviceFirmwareVersion());
            }

            newTourData = oldTourData;
         }

         if (newTourData != null) {

            /*
             * Compute computed values
             */
            newTourData.clearComputedSeries();

            newTourData.computeAltitudeUpDown();
            newTourData.computeTourMovingTime();
            newTourData.computeComputedValues();

            // maintain list, that another call of this method do not find this tour again
            allImportedToursFromOneFile.remove(oldTourData.getTourId());

            return newTourData;
         }
      }

      /*
       * A re-import failed, display an error message
       */
      if (message == null) {

         // undefined error
         TourLogManager.subLog_ERROR(NLS.bind(
               Messages.Import_Data_Log_ReimportIsInvalid_TourNotFoundInFile_Message,
               new Object[] {
                     oldTourDateTimeShort,
                     reImportedFile.toString() }));

      } else {
         TourLogManager.subLog_ERROR(message);
      }

      return null;
   }

   /**
    * Replace parts of the tour values.
    *
    * @param allTourValueTypes
    * @param oldTourData
    * @param reimportedTourData
    */
   private void reimportTour_40_ReplacesValues(final List<TourValueType> allTourValueTypes,
                                               final TourData oldTourData,
                                               final TourData reimportedTourData) {

      final boolean isAllTimeSlices = allTourValueTypes.contains(TourValueType.ALL_TIME_SLICES);

      // Battery %
      if (isAllTimeSlices || allTourValueTypes.contains(TourValueType.TIME_SLICES__BATTERY)) {

         // re-import battery values only

         oldTourData.setBattery_Time(reimportedTourData.getBattery_Time());
         oldTourData.setBattery_Percentage(reimportedTourData.getBattery_Percentage());

         oldTourData.setBattery_Percentage_Start(reimportedTourData.getBattery_Percentage_Start());
         oldTourData.setBattery_Percentage_End(reimportedTourData.getBattery_Percentage_End());
      }

      // Cadence
      if (isAllTimeSlices || allTourValueTypes.contains(TourValueType.TIME_SLICES__CADENCE)) {

         // re-import cadence/stride only
         oldTourData.setCadenceSerie(reimportedTourData.getCadenceSerie());
         oldTourData.setCadenceMultiplier(reimportedTourData.getCadenceMultiplier());
         oldTourData.setIsStrideSensorPresent(reimportedTourData.isStrideSensorPresent());
      }

      // Elevation
      if (isAllTimeSlices || allTourValueTypes.contains(TourValueType.TIME_SLICES__ELEVATION)) {

         // re-import altitude only
         oldTourData.altitudeSerie = reimportedTourData.altitudeSerie;
      }

      // Gear
      if (isAllTimeSlices || allTourValueTypes.contains(TourValueType.TIME_SLICES__GEAR)) {

         // re-import gear only
         oldTourData.gearSerie = reimportedTourData.gearSerie;
         oldTourData.setFrontShiftCount(reimportedTourData.getFrontShiftCount());
         oldTourData.setRearShiftCount(reimportedTourData.getRearShiftCount());
      }

      // Power
      if (isAllTimeSlices
            || allTourValueTypes.contains(TourValueType.TIME_SLICES__POWER_AND_PULSE)
            || allTourValueTypes.contains(TourValueType.TIME_SLICES__POWER_AND_SPEED)) {

         // re-import power and speed only when it's from the device
         final boolean isDevicePower = reimportedTourData.isPowerSerieFromDevice();
         if (isDevicePower) {

            final float[] powerSerie = reimportedTourData.getPowerSerie();
            if (powerSerie != null) {
               oldTourData.setPowerSerie(powerSerie);
            }

//SET_FORMATTING_OFF

            oldTourData.setPower_Avg(                          reimportedTourData.getPower_Avg());
            oldTourData.setPower_Max(                          reimportedTourData.getPower_Max());
            oldTourData.setPower_Normalized(                   reimportedTourData.getPower_Normalized());
            oldTourData.setPower_FTP(                          reimportedTourData.getPower_FTP());

            oldTourData.setPower_TotalWork(                    reimportedTourData.getPower_TotalWork());
            oldTourData.setPower_TrainingStressScore(          reimportedTourData.getPower_TrainingStressScore());
            oldTourData.setPower_IntensityFactor(              reimportedTourData.getPower_IntensityFactor());

            oldTourData.setPower_PedalLeftRightBalance(        reimportedTourData.getPower_PedalLeftRightBalance());
            oldTourData.setPower_AvgLeftPedalSmoothness(       reimportedTourData.getPower_AvgLeftPedalSmoothness());
            oldTourData.setPower_AvgLeftTorqueEffectiveness(   reimportedTourData.getPower_AvgLeftTorqueEffectiveness());
            oldTourData.setPower_AvgRightPedalSmoothness(      reimportedTourData.getPower_AvgRightPedalSmoothness());
            oldTourData.setPower_AvgRightTorqueEffectiveness(  reimportedTourData.getPower_AvgRightTorqueEffectiveness());

//SET_FORMATTING_ON
         }
      }

      // Pulse
      if (isAllTimeSlices || allTourValueTypes.contains(TourValueType.TIME_SLICES__POWER_AND_PULSE)) {

         // re-import pulse

         oldTourData.pulseSerie = reimportedTourData.pulseSerie;

         oldTourData.pulseTime_Milliseconds = reimportedTourData.pulseTime_Milliseconds;
         oldTourData.pulseTime_TimeIndex = reimportedTourData.pulseTime_TimeIndex;
      }

      // Speed
      if (isAllTimeSlices || allTourValueTypes.contains(TourValueType.TIME_SLICES__POWER_AND_SPEED)) {

         // re-import speed

         final boolean isDeviceSpeed = reimportedTourData.isSpeedSerieFromDevice();
         if (isDeviceSpeed) {
            final float[] speedSerie = reimportedTourData.getSpeedSerieFromDevice();
            if (speedSerie != null) {
               oldTourData.setSpeedSerie(speedSerie);
            }
         }
      }

      // Running Dynamics
      if (isAllTimeSlices || allTourValueTypes.contains(TourValueType.TIME_SLICES__RUNNING_DYNAMICS)) {

         // re-import only running dynamics

         oldTourData.runDyn_StanceTime = reimportedTourData.runDyn_StanceTime;
         oldTourData.runDyn_StanceTimeBalance = reimportedTourData.runDyn_StanceTimeBalance;
         oldTourData.runDyn_StepLength = reimportedTourData.runDyn_StepLength;
         oldTourData.runDyn_VerticalOscillation = reimportedTourData.runDyn_VerticalOscillation;
         oldTourData.runDyn_VerticalRatio = reimportedTourData.runDyn_VerticalRatio;
      }

      // Swimming
      if (isAllTimeSlices || allTourValueTypes.contains(TourValueType.TIME_SLICES__SWIMMING)) {

         // re-import only swimming

         oldTourData.swim_LengthType = reimportedTourData.swim_LengthType;
         oldTourData.swim_Cadence = reimportedTourData.swim_Cadence;
         oldTourData.swim_Strokes = reimportedTourData.swim_Strokes;
         oldTourData.swim_StrokeStyle = reimportedTourData.swim_StrokeStyle;
         oldTourData.swim_Time = reimportedTourData.swim_Time;
      }

      // Temperature
      if (isAllTimeSlices || allTourValueTypes.contains(TourValueType.TIME_SLICES__TEMPERATURE)) {

         // re-import temperature only

         oldTourData.temperatureSerie = reimportedTourData.temperatureSerie;
      }

      // Training
      if (isAllTimeSlices || allTourValueTypes.contains(TourValueType.TIME_SLICES__TRAINING)) {

         // re-import training only

         oldTourData.setTraining_TrainingEffect_Aerob(reimportedTourData.getTraining_TrainingEffect_Aerob());
         oldTourData.setTraining_TrainingEffect_Anaerob(reimportedTourData.getTraining_TrainingEffect_Anaerob());
         oldTourData.setTraining_TrainingPerformance(reimportedTourData.getTraining_TrainingPerformance());
      }

      // Timer pauses
      if (isAllTimeSlices || allTourValueTypes.contains(TourValueType.TIME_SLICES__TIMER_PAUSES)) {

         // re-import pauses only

         oldTourData.setTourDeviceTime_Recorded(reimportedTourData.getTourDeviceTime_Recorded());

         long totalTourTimerPauses = 0;
         final long[] pausedTime_Start = reimportedTourData.getPausedTime_Start();
         if (pausedTime_Start != null && pausedTime_Start.length > 0) {
            final List<Long> listPausedTime_Start = Arrays.stream(pausedTime_Start).boxed().collect(Collectors.toList());
            final List<Long> listPausedTime_End = Arrays.stream(reimportedTourData.getPausedTime_End()).boxed().collect(Collectors.toList());
            oldTourData.finalizeTour_TimerPauses(listPausedTime_Start, listPausedTime_End);
         } else {
            oldTourData.setPausedTime_Start(reimportedTourData.getPausedTime_Start());
            oldTourData.setPausedTime_End(reimportedTourData.getPausedTime_End());
         }

         totalTourTimerPauses = reimportedTourData.getTourDeviceTime_Paused();

         oldTourData.setTourDeviceTime_Paused(totalTourTimerPauses);
      }

      // ALL
      if (isAllTimeSlices) {

         // re-import all other data series

         // update device data
         oldTourData.setDeviceFirmwareVersion(reimportedTourData.getDeviceFirmwareVersion());
         oldTourData.setDeviceId(reimportedTourData.getDeviceId());
         oldTourData.setDeviceName(reimportedTourData.getDeviceName());

         oldTourData.distanceSerie = reimportedTourData.distanceSerie;
         oldTourData.latitudeSerie = reimportedTourData.latitudeSerie;
         oldTourData.longitudeSerie = reimportedTourData.longitudeSerie;
         oldTourData.timeSerie = reimportedTourData.timeSerie;

         oldTourData.computeGeo_Bounds();
      }
   }

   public void removeAllTours() {

      _allImportedTours.clear();

      _allImportedFileNames.clear();
      _allImportedFileNamesChildren.clear();

      _tempTourTags.clear();
      _tempTourTypes.clear();
   }

   public void removeTours(final TourData[] removedTours) {

      // clone map
      final ConcurrentHashMap<String, String> oldFileNames = new ConcurrentHashMap<>(_allImportedFileNames);

      for (final TourData tourData : removedTours) {

         final Long key = tourData.getTourId();

         if (_allImportedTours.containsKey(key)) {
            _allImportedTours.remove(key);
         }
      }

      /*
       * Check if all tours from a file are removed, when yes, remove file path that the file can
       * not be re-imported. When at least one tour is still used, all tours will be re-imported
       * because it's not yet saved which tours are removed from a file and which are not.
       */

      oldFileNames.forEach((key, value) -> {

         if (key instanceof String) {

            final String oldFilePath = key;
            boolean isNeeded = false;

            for (final TourData tourData : _allImportedTours.values()) {

               final String tourFilePathName = tourData.getImportFilePathName();

               if (tourFilePathName != null && tourFilePathName.equals(oldFilePath)) {
                  isNeeded = true;
                  break;
               }
            }

            if (isNeeded == false) {

               // file path is not needed any more
               _allImportedFileNames.remove(oldFilePath);
            }
         }
      });
   }

   public void setImportYear(final int year) {
      _importState_ImportYear = year;
   }

   public void setIsHAC4_5_ChecksumValidation(final boolean checked) {
      _importState_IsHAC4_5_ChecksumValidation = checked;
   }

   public void setMergeTracks(final boolean checked) {
      _importState_IsMergeTracks = checked;
   }

   public void setState_ConvertWayPoints(final boolean isConvertWayPoints) {
      _importState_IsConvertWayPoints = isConvertWayPoints;
   }

   public void setState_CreateTourIdWithTime(final boolean isActionChecked) {
      _importState_IsCreateTourIdWithTime = isActionChecked;
   }

   public void setState_DefaultCadenceMultiplier(final CadenceMultiplier defaultCadenceMultiplier) {
      _importState_DefaultCadenceMultiplier = defaultCadenceMultiplier;
   }

   public void setState_IsIgnoreInvalidFile(final boolean isIgnoreInvalidFile) {
      _importState_IsIgnoreInvalidFile = isIgnoreInvalidFile;
   }

   public void setState_IsOpenImportLogView(final boolean isOpenImportLog) {
      _importState_IsAutoOpenImportLog = isOpenImportLog;
   }

   public void setState_IsSetBodyWeight(final boolean isSetBodyWeight) {
      _importState_IsSetBodyWeight = isSetBodyWeight;
   }

   private RawDataView showRawDataView() {

      final IWorkbench workbench = PlatformUI.getWorkbench();
      final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();

      try {

         final IViewPart rawDataView = window.getActivePage().findView(RawDataView.ID);

         if (rawDataView == null) {

            // show raw data perspective when raw data view is not visible
            workbench.showPerspective(PerspectiveFactoryRawData.PERSPECTIVE_ID, window);
         }

         // show raw data view
         return (RawDataView) Util.showView(RawDataView.ID, true);

      } catch (final WorkbenchException e) {
         TourLogManager.log_EXCEPTION_WithStacktrace(e);
      }
      return null;
   }

   /**
    * Update {@link TourData} from the database for all imported tours which are contained in
    * {@link #_allImportedTours} and displayed in the import view, a progress dialog is displayed.
    *
    * @param monitor
    *           Progress monitor or <code>null</code>
    */
   public void updateTourData_InImportView_FromDb(final IProgressMonitor monitor) {

      // reset to default
      _selectedImportFilenameReplacementOption = ReplaceImportFilenameAction.DO_NOTHING;

      try {

         final int numImportTours = _allImportedTours.size();

         if (numImportTours == 0) {

            // nothing to do

         } else if (numImportTours < 3) {

            // don't show progress dialog
            updateTourData_InImportView_FromDb_Runnable(null);

         } else {

            if (monitor == null) {

               new ProgressMonitorDialog(Display.getDefault().getActiveShell()).run(
                     true, // fork
                     false, // cancelable
                     newMonitor -> updateTourData_InImportView_FromDb_Runnable(newMonitor));

            } else {

               // a monitor is provided

               updateTourData_InImportView_FromDb_Runnable(monitor);
            }
         }

      } catch (final InvocationTargetException | InterruptedException e) {

         TourLogManager.log_EXCEPTION_WithStacktrace(e);
      }
   }

   private void updateTourData_InImportView_FromDb_Runnable(final IProgressMonitor monitor) throws InterruptedException {

      final int numAllImportedTours = _allImportedTours.size();

      /*
       * Setup concurrency
       */
      _loadingTour_CountDownLatch = new CountDownLatch(numAllImportedTours);
      _loadingTour_Queue.clear();

      final ConcurrentSkipListSet<Long> allSavedTourIds = new ConcurrentSkipListSet<>();

      if (monitor != null) {
         monitor.beginTask(Messages.import_data_updateDataFromDatabase_task, numAllImportedTours);
      }

      final long startTime = System.currentTimeMillis();
      long lastUpdateTime = startTime;

      final AtomicInteger numWorkedTours = new AtomicInteger();
      int numLastWorked = 0;

      for (final TourData importedTourData : _allImportedTours.values()) {

         if (monitor != null) {

            final long currentTime = System.currentTimeMillis();
            final long timeDiff = currentTime - lastUpdateTime;

            // reduce logging
            if (timeDiff > 500) {

               lastUpdateTime = currentTime;

               final int numWorked = numWorkedTours.get();

               // "{0} / {1} - {2} % - {3} Δ"
               UI.showWorkedInProgressMonitor(monitor, numWorked, numAllImportedTours, numLastWorked);

               numLastWorked = numWorked;
            }
         }

         if (importedTourData.isTourDeleted) {
            _loadingTour_CountDownLatch.countDown();
            continue;
         }

         updateTourData_InImportView_FromDb_Runnable_10_Concurrent(importedTourData,
               monitor,
               numWorkedTours,
               allSavedTourIds);
      }

      // wait until all re-imports are performed
      _loadingTour_CountDownLatch.await();

      TourDatabase.saveTour_PostSaveActions_Concurrent_2_ForAllTours(
            allSavedTourIds
                  .stream()
                  .collect(Collectors.toList()));

      // prevent async error
      Display.getDefault().syncExec(() -> TourManager.fireEvent(TourEventId.CLEAR_DISPLAYED_TOUR, null, null));
   }

   private void updateTourData_InImportView_FromDb_Runnable_10_Concurrent(final TourData importedTourData,
                                                                          final IProgressMonitor monitor,
                                                                          final AtomicInteger numWorkedTours,
                                                                          final ConcurrentSkipListSet<Long> allSavedTourIds) {

      try {

         // put tour ID (queue item) into the queue AND wait when it is full

         _loadingTour_Queue.put(importedTourData);

      } catch (final InterruptedException e) {

         StatusUtil.log(e);
         Thread.currentThread().interrupt();
      }

      _loadingTour_Executor.submit(() -> {

         try {

            // get last added item
            final TourData queueItem_ImportedTourData = _loadingTour_Queue.poll();

            if (queueItem_ImportedTourData != null) {

               updateTourData_InImportView_FromDb_Runnable_20_OneTour(
                     queueItem_ImportedTourData,
                     allSavedTourIds);
            }

         } finally {

            if (monitor != null) {
               monitor.worked(1);
            }

            numWorkedTours.incrementAndGet();

            _loadingTour_CountDownLatch.countDown();
         }
      });
   }

   private void updateTourData_InImportView_FromDb_Runnable_20_OneTour(final TourData importedTourData,
                                                                       final ConcurrentSkipListSet<Long> allSavedTourIds) {

      try {

         final Long tourId = importedTourData.getTourId();

         final TourData dbTourData = TourManager.getInstance().getTourDataFromDb(tourId);
         if (dbTourData != null) {

            /*
             * Imported tour is saved in the database, set transient fields.
             */

            // used to delete the device import file
            dbTourData.isTourFileDeleted = importedTourData.isTourFileDeleted;
            dbTourData.isTourFileMoved = importedTourData.isTourFileMoved;
            dbTourData.isBackupImportFile = importedTourData.isBackupImportFile;
            dbTourData.importFilePathOriginal = importedTourData.importFilePathOriginal;

            final Long dbTourId = dbTourData.getTourId();

            // replace existing tours but do not add new tours
            if (_allImportedTours.containsKey(dbTourId)) {

               TourData replacedTourData = dbTourData;

               /*
                * Check if the tour editor contains this tour, this should not be necessary, just
                * make sure the correct tour is used !!!
                */
               final TourDataEditorView tourDataEditor = TourManager.getTourDataEditor();
               if (tourDataEditor != null) {
                  final TourData editorTourData = tourDataEditor.getTourData();
                  if (editorTourData != null) {
                     final long editorTourId = editorTourData.getTourId();
                     if (editorTourId == dbTourId) {
                        replacedTourData = editorTourData;
                     }
                  }
               }

               /**
                * Set newly import filename into the tour which was already saved in the db.
                * <p>
                * This
                */
               final String importedFilePathName = importedTourData.getImportFilePathName();
               final String dbFilePathName = dbTourData.getImportFilePathName();

               if (importedFilePathName != null
                     && importedFilePathName.equalsIgnoreCase(dbFilePathName) == false) {

                  // saved file path name is different when compared with the imported file path name

                  getSelectedImportFilenameReplacementOption(dbTourData, importedFilePathName);

                  switch (_selectedImportFilenameReplacementOption) {

                  case REPLACE_IMPORT_FILENAME_IN_SAVED_TOUR:
                  case REPLACE_IMPORT_FILENAME_IN_ALL_SAVED_TOUR:

                     replacedTourData.setImportFilePath(importedFilePathName);

                     // also update device name/version
                     replacedTourData.setDeviceName(importedTourData.getDeviceName());
                     replacedTourData.setDeviceFirmwareVersion(importedTourData.getDeviceFirmwareVersion());

                     // save tour
                     replacedTourData = TourDatabase.saveTour_Concurrent(dbTourData, true);

                     allSavedTourIds.add(replacedTourData.getTourId());

                     break;

                  case DO_NOTHING:
                  case DO_NOTHING_AND_DONT_ASK_AGAIN:
                  default:
                     break;
                  }
               }

               _allImportedTours.put(dbTourId, replacedTourData);
            }
         }

      } catch (final Exception e) {
         TourLogManager.log_EXCEPTION_WithStacktrace(e);
      }
   }

   /**
    * Updates the model with modified tours
    *
    * @param modifiedTours
    */
   public void updateTourDataModel(final ArrayList<TourData> modifiedTours) {

      for (final TourData tourData : modifiedTours) {
         if (tourData != null) {

            final Long tourId = tourData.getTourId();

            // replace existing tour do not add new tours
            if (_allImportedTours.containsKey(tourId)) {
               _allImportedTours.put(tourId, tourData);
            }
         }
      }
   }
}

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
package net.tourbook;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

   private static final String BUNDLE_NAME = "net.tourbook.messages";                                 //$NON-NLS-1$

   public static String        Action_Cadence_Set;
   public static String        Action_Cadence_Set_None;
   public static String        Action_Cadence_Set_Rpm;
   public static String        Action_Cadence_Set_Spm;

   public static String        Action_Compute_ElevationGain;

   public static String        Action_Map_AddBookmark;
   public static String        Action_Map_RenameBookmark;
   public static String        Action_Map_UpdateBookmark;

   public static String        Action_MarkerFilter_WithGPS_Tooltip;
   public static String        Action_MarkerFilter_WithoutGPS_Tooltip;

   public static String        Action_PhotosAndTours_AddPhoto;
   public static String        Action_PhotosAndTours_FilterNoTours_Tooltip;
   public static String        Action_PhotosAndTours_FilterNotSavedPhotos_Tooltip;
   public static String        Action_PhotosAndTours_FilterPhotos_Tooltip;
   public static String        Action_PhotosAndTours_RemovePhoto;
   public static String        Action_PhotosAndTours_SaveAllPhotos;

   public static String        Action_ToolTip_Hide;
   public static String        Action_ToolTip_SetDefaults;

   public static String        Calendar_Profile_AppDefault_Classic;
   public static String        Calendar_Profile_AppDefault_Compact;
   public static String        Calendar_Profile_AppDefault_Compact_II;
   public static String        Calendar_Profile_AppDefault_Compact_III;
   public static String        Calendar_Profile_AppDefault_Default;
   public static String        Calendar_Profile_AppDefault_UserDefault;
   public static String        Calendar_Profile_AppDefault_Year;
   public static String        Calendar_Profile_AppDefault_Year_II;
   public static String        Calendar_Profile_AppDefault_Year_III;
   public static String        Calendar_Profile_Color_Black;
   public static String        Calendar_Profile_Color_Bright;
   public static String        Calendar_Profile_Color_Contrast;
   public static String        Calendar_Profile_Color_Custom;
   public static String        Calendar_Profile_Color_Dark;
   public static String        Calendar_Profile_Color_Line;
   public static String        Calendar_Profile_Color_Text;
   public static String        Calendar_Profile_Color_White;
   public static String        Calendar_Profile_ColumnLayout_Continuously;
   public static String        Calendar_Profile_DateColumn_Month;
   public static String        Calendar_Profile_DateColumn_WeekNumber;
   public static String        Calendar_Profile_DateColumn_Year;
   public static String        Calendar_Profile_DayHeaderDateFormat_Automatic;
   public static String        Calendar_Profile_DayHeaderDateFormat_Day;
   public static String        Calendar_Profile_Name_Classic;
   public static String        Calendar_Profile_Name_Compact;
   public static String        Calendar_Profile_Name_Compact_II;
   public static String        Calendar_Profile_Name_Compact_III;
   public static String        Calendar_Profile_Name_Default;
   public static String        Calendar_Profile_Name_Year;
   public static String        Calendar_Profile_Name_Year_II;
   public static String        Calendar_Profile_Name_Year_III;
   public static String        Calendar_Profile_TourBackground_Circle;
   public static String        Calendar_Profile_TourBackground_Fill;
   public static String        Calendar_Profile_TourBackground_Fill_Left;
   public static String        Calendar_Profile_TourBackground_Fill_Right;
   public static String        Calendar_Profile_TourBackground_GradientHorizontal;
   public static String        Calendar_Profile_TourBackground_GradientVertical;
   public static String        Calendar_Profile_TourBackground_NoBackground;
   public static String        Calendar_Profile_TourBorder_All;
   public static String        Calendar_Profile_TourBorder_Bottom;
   public static String        Calendar_Profile_TourBorder_Left;
   public static String        Calendar_Profile_TourBorder_LeftRight;
   public static String        Calendar_Profile_TourBorder_NoBorder;
   public static String        Calendar_Profile_TourBorder_Right;
   public static String        Calendar_Profile_TourBorder_Top;
   public static String        Calendar_Profile_TourBorder_TopBottom;
   public static String        Calendar_Profile_Value_Altitude;
   public static String        Calendar_Profile_Value_BreakTime;
   public static String        Calendar_Profile_Value_CadenceZones_TimePercentages;
   public static String        Calendar_Profile_Value_Description;
   public static String        Calendar_Profile_Value_Distance;
   public static String        Calendar_Profile_Value_Elevation_Change;
   public static String        Calendar_Profile_Value_ElapsedTime;
   public static String        Calendar_Profile_Value_Energy_kcal;
   public static String        Calendar_Profile_Value_Energy_MJ;
   public static String        Calendar_Profile_Value_MovingTime;
   public static String        Calendar_Profile_Value_Pace;
   public static String        Calendar_Profile_Value_PausedTime;
   public static String        Calendar_Profile_Value_PowerAvg;
   public static String        Calendar_Profile_Value_PulseAvg;
   public static String        Calendar_Profile_Value_RecordedTime;
   public static String        Calendar_Profile_Value_ShowNothing;
   public static String        Calendar_Profile_Value_Speed;
   public static String        Calendar_Profile_Value_Title;
   public static String        Calendar_View_Action_Back;
   public static String        Calendar_View_Action_Back_Tooltip;
   public static String        Calendar_View_Action_Forward;
   public static String        Calendar_View_Action_Forward_Tooltip;
   public static String        Calendar_View_Action_GotoToday;
   public static String        Calendar_View_Action_LinkWithOtherViews;
   public static String        Calendar_View_Combo_Month_Tooltip;
   public static String        Calendar_View_Combo_Year_Tooltip;

   public static String        dialog_export_btn_export;
   public static String        dialog_export_chk_camouflageSpeed;
   public static String        dialog_export_chk_camouflageSpeed_tooltip;
   public static String        dialog_export_chk_camouflageSpeedInput_tooltip;
   public static String        dialog_export_chk_exportNotes;
   public static String        dialog_export_chk_exportNotes_tooltip;
   public static String        dialog_export_chk_exportMarkers;
   public static String        dialog_export_chk_exportMarkers_tooltip;
   public static String        dialog_export_chk_mergeAllTours;
   public static String        dialog_export_chk_mergeAllTours_tooltip;
   public static String        dialog_export_chk_overwriteFiles;
   public static String        dialog_export_chk_overwriteFiles_tooltip;
   public static String        dialog_export_chk_tourRangeDisabled;
   public static String        dialog_export_chk_tourRangeWithDistance;
   public static String        dialog_export_chk_tourRangeWithoutDistance;
   public static String        dialog_export_dialog_message;
   public static String        dialog_export_dialog_title;
   public static String        dialog_export_dir_dialog_message;
   public static String        dialog_export_dir_dialog_text;
   public static String        dialog_export_file_dialog_text;
   public static String        dialog_export_group_exportFileName;
   public static String        dialog_export_label_DefaultFileName;
   public static String        dialog_export_label_exportFilePath;
   public static String        dialog_export_label_fileName;
   public static String        dialog_export_label_filePath;
   public static String        dialog_export_msg_fileAlreadyExists;
   public static String        dialog_export_msg_fileNameIsInvalid;
   public static String        dialog_export_msg_pathIsNotAvailable;
   public static String        dialog_export_shell_text;
   public static String        dialog_export_txt_filePath_tooltip;

   public static String        Dialog_AdjustAltitude_Label_ElevationGain;
   public static String        Dialog_AdjustAltitude_Label_ElevationGain_After_Tooltip;
   public static String        Dialog_AdjustAltitude_Label_ElevationGain_Before_Tooltip;
   public static String        Dialog_AdjustAltitude_Label_ElevationGain_Diff_Tooltip;
   public static String        Dialog_AdjustAltitude_Label_ElevationLoss;
   public static String        Dialog_AdjustAltitude_Label_ElevationLoss_After_Tooltip;
   public static String        Dialog_AdjustAltitude_Label_ElevationLoss_Before_Tooltip;
   public static String        Dialog_AdjustAltitude_Label_ElevationLoss_Diff_Tooltip;
   public static String        Dialog_AdjustAltitude_Label_SrtmIsInvalid;
   public static String        Dialog_AdjustAltitude_Link_ApproachWholeTour;
   public static String        Dialog_AdjustAltitude_Link_SetLastPointToSRTM;
   public static String        Dialog_AdjustAltitude_Link_SetLastPointToSRTM_Tooltip;

   public static String        Dialog_AdjustTemperature_Button_AdjustTemperature;
   public static String        Dialog_AdjustTemperature_Dialog_Message;
   public static String        Dialog_AdjustTemperature_Dialog_Title;
   public static String        Dialog_AdjustTemperature_Label_AvgTemperature;
   public static String        Dialog_AdjustTemperature_Label_Info;
   public static String        Dialog_AdjustTemperature_Label_InfoHint;
   public static String        Dialog_AdjustTemperature_Label_Progress_SubTask;
   public static String        Dialog_AdjustTemperature_Label_Progress_Task;
   public static String        Dialog_AdjustTemperature_Label_TemperatureAdjustmentDuration;

   public static String        Dialog_DatabaseAction_Confirmation_Message;
   public static String        Dialog_DatabaseAction_Confirmation_Title;

   public static String        Dialog_DeleteTourValues_Action_OpenDialog;
   public static String        Dialog_DeleteTourValues_Button_Delete;
   public static String        Dialog_DeleteTourValues_Checkbox_Time;
   public static String        Dialog_DeleteTourValues_Dialog_ConfirmDeleteValues_Message;
   public static String        Dialog_DeleteTourValues_Dialog_Message;
   public static String        Dialog_DeleteTourValues_Dialog_Title;
   public static String        Dialog_DeleteTourValues_Group_Delete;
   public static String        Dialog_DeleteTourValues_Group_Delete_Label_Info;
   public static String        Dialog_DeleteTourValues_Group_Delete_Tooltip;
   public static String        Dialog_DeleteTourValues_Group_Tours;
   public static String        Dialog_DeleteTourValues_Group_Tours_Tooltip;

   public static String        Dialog_DoubleClickAction_InvalidAction_Message;
   public static String        Dialog_DoubleClickAction_InvalidAction_Title;
   public static String        Dialog_DoubleClickAction_NoAction_Message;
   public static String        Dialog_DoubleClickAction_NoAction_Title;

   public static String        Dialog_EditTimeslicesValues_Title;
   public static String        Dialog_EditTimeslicesValues_Area_Title;
   public static String        Dialog_EditTimeslicesValues_Label_NewValues;
   public static String        Dialog_EditTimeslicesValues_Label_OffsetValues;
   public static String        Dialog_EditTimeslicesValues_Checkbox_Altitude;
   public static String        Dialog_EditTimeslicesValues_Checkbox_Pulse;
   public static String        Dialog_EditTimeslicesValues_Checkbox_Cadence;
   public static String        Dialog_EditTimeslicesValues_Checkbox_Temperature;
   public static String        Dialog_EditTimeslicesValues_Checkbox_Altitude_Tooltip;
   public static String        Dialog_EditTimeslicesValues_Checkbox_Pulse_Tooltip;
   public static String        Dialog_EditTimeslicesValues_Checkbox_Cadence_Tooltip;
   public static String        Dialog_EditTimeslicesValues_Checkbox_Temperature_Tooltip;

   public static String        Dialog_Export_Checkbox_Description;
   public static String        Dialog_Export_Checkbox_SurfingWaves;
   public static String        Dialog_Export_Checkbox_SurfingWaves_Tooltip;
   public static String        Dialog_Export_Checkbox_TourFields;
   public static String        Dialog_Export_Checkbox_TourFields_Tooltip;
   public static String        Dialog_Export_Checkbox_WithBarometer;
   public static String        Dialog_Export_Checkbox_WithBarometer_Tooltip;
   public static String        Dialog_Export_Description_SurfingWaves;
   public static String        Dialog_Export_Error_CourseNameIsInvalid;
   public static String        Dialog_Export_Group_Custom;
   public static String        Dialog_Export_Group_Custom_Tooltip;
   public static String        Dialog_Export_Group_How;
   public static String        Dialog_Export_Group_How_Tooltip;
   public static String        Dialog_Export_Group_What;
   public static String        Dialog_Export_Group_What_Tooltip;
   public static String        Dialog_Export_Label_GPX_DistanceValues;
   public static String        Dialog_Export_Label_TCX_ActivityType;
   public static String        Dialog_Export_Label_TCX_CourseName;
   public static String        Dialog_Export_Label_TCX_NameFrom;
   public static String        Dialog_Export_Label_TCX_NameFrom_Tooltip;
   public static String        Dialog_Export_Label_TCX_Type;
   public static String        Dialog_Export_Radio_GPX_DistanceAbsolute;
   public static String        Dialog_Export_Radio_GPX_DistanceAbsolute_Tooltip;
   public static String        Dialog_Export_Radio_GPX_DistanceRelative;
   public static String        Dialog_Export_Radio_GPX_DistanceRelative_Tooltip;
   public static String        Dialog_Export_Radio_TCX_Aktivities;
   public static String        Dialog_Export_Radio_TCX_Aktivities_Tooltip;
   public static String        Dialog_Export_Radio_TCX_Courses;
   public static String        Dialog_Export_Radio_TCX_Courses_Tooltip;
   public static String        Dialog_Export_Radio_TCX_NameFromField;
   public static String        Dialog_Export_Radio_TCX_NameFromTour;
   public static String        Dialog_Export_SubTask_CreatingExportFile;
   public static String        Dialog_Export_SubTask_Export;

   public static String        Dialog_ExtractTour_DlgArea_Message;
   public static String        Dialog_ExtractTour_DlgArea_Title;
   public static String        Dialog_ExtractTour_Label_DeviceName;
   public static String        Dialog_ExtractTour_Label_SplitMethod;

   public static String        Dialog_HRZone_Button_AddZone;
   public static String        Dialog_HRZone_Button_EditHrZones;
   public static String        Dialog_HRZone_Button_RemoveZone;
   public static String        Dialog_HRZone_Button_SortZone;
   public static String        Dialog_HRZone_Button_SortZone_Tooltip;
   public static String        Dialog_HRZone_DialogMessage;
   public static String        Dialog_HRZone_DialogTitle;
   public static String        Dialog_HRZone_Label_Header_Color;
   public static String        Dialog_HRZone_Label_Header_Pulse;
   public static String        Dialog_HRZone_Label_Header_Zone;
   public static String        Dialog_HRZone_Label_Header_ZoneShortcut;
   public static String        Dialog_HRZone_Label_Trash_Tooltip;

   public static String        Dialog_ImportConfig_Action_AddSpeed_Tooltip;
   public static String        Dialog_ImportConfig_Action_NewOneTourType;
   public static String        Dialog_ImportConfig_Action_NewOneTourType_Tooltip;
   public static String        Dialog_ImportConfig_Action_RemoveSpeed_Tooltip;
   public static String        Dialog_ImportConfig_Action_SortBySpeed_Tooltip;

   public static String        Dialog_ImportConfig_Checkbox_AdjustTemperature;
   public static String        Dialog_ImportConfig_Checkbox_CreateBackup;
   public static String        Dialog_ImportConfig_Checkbox_CreateBackup_Tooltip;
   public static String        Dialog_ImportConfig_Checkbox_DeleteDeviceFiles;
   public static String        Dialog_ImportConfig_Checkbox_DeleteDeviceFiles_Tooltip;
   public static String        Dialog_ImportConfig_Checkbox_DeviceWatching;
   public static String        Dialog_ImportConfig_Checkbox_ImportFiles;
   public static String        Dialog_ImportConfig_Checkbox_LastMarker;
   public static String        Dialog_ImportConfig_Checkbox_LastMarker_Tooltip;
   public static String        Dialog_ImportConfig_Checkbox_LiveUpdate;
   public static String        Dialog_ImportConfig_Checkbox_LiveUpdate_Tooltip;
   public static String        Dialog_ImportConfig_Checkbox_RetrieveWeatherData;
   public static String        Dialog_ImportConfig_Checkbox_RetrieveWeatherData_Tooltip;
   public static String        Dialog_ImportConfig_Checkbox_SaveTour;
   public static String        Dialog_ImportConfig_Checkbox_SaveTour_Tooltip;
   public static String        Dialog_ImportConfig_Checkbox_ShowInDashboard;
   public static String        Dialog_ImportConfig_Checkbox_ShowInDashboard_Tooltip;
   public static String        Dialog_ImportConfig_Checkbox_TourType;
   public static String        Dialog_ImportConfig_Checkbox_TourType_Tooltip;

   public static String        Dialog_ImportConfig_Combo_Device_LocalDevice;
   public static String        Dialog_ImportConfig_Combo_Folder_Tooltip;

   public static String        Dialog_ImportConfig_Column_AdjustTemperature_Header;
   public static String        Dialog_ImportConfig_Column_AdjustTemperature_Label;
   public static String        Dialog_ImportConfig_Column_AdjustTemperature_Tooltip;
   public static String        Dialog_ImportConfig_Column_Backup;
   public static String        Dialog_ImportConfig_Column_DeleteFiles_Header;
   public static String        Dialog_ImportConfig_Column_DeleteFiles_Label;
   public static String        Dialog_ImportConfig_Column_DeleteFiles_Tooltip;
   public static String        Dialog_ImportConfig_Column_Description;
   public static String        Dialog_ImportConfig_Column_Device;
   public static String        Dialog_ImportConfig_Column_DeviceFiles;
   public static String        Dialog_ImportConfig_Column_LastMarker_Header;
   public static String        Dialog_ImportConfig_Column_LastMarker_Label;
   public static String        Dialog_ImportConfig_Column_LastMarker_Tooltip;
   public static String        Dialog_ImportConfig_Column_Name;
   public static String        Dialog_ImportConfig_Column_RetrieveWeatherData_Label;
   public static String        Dialog_ImportConfig_Column_RetrieveWeatherData_Header;
   public static String        Dialog_ImportConfig_Column_Save_Header;
   public static String        Dialog_ImportConfig_Column_Save_Label;
   public static String        Dialog_ImportConfig_Column_ShowInDash_Header;
   public static String        Dialog_ImportConfig_Column_ShowInDash_Label;
   public static String        Dialog_ImportConfig_Column_TourType;
   public static String        Dialog_ImportConfig_Column_TurnOFF_Header;
   public static String        Dialog_ImportConfig_Column_TurnOFF_Label;
   public static String        Dialog_ImportConfig_Column_TurnOFF_Tooltip;

   public static String        Dialog_ImportConfig_Dialog_BackupFolder_Message;
   public static String        Dialog_ImportConfig_Dialog_BackupFolder_Title;
   public static String        Dialog_ImportConfig_Dialog_DeviceFolder_Message;
   public static String        Dialog_ImportConfig_Dialog_DeviceFolder_Title;
   public static String        Dialog_ImportConfig_Dialog_Message;
   public static String        Dialog_ImportConfig_Dialog_Title;

   public static String        Dialog_ImportConfig_Error_FolderIsInvalid;

   public static String        Dialog_ImportConfig_Group_Dashboard;
   public static String        Dialog_ImportConfig_Group_ImportActions;
   public static String        Dialog_ImportConfig_Group_ImportLauncherConfig;
   public static String        Dialog_ImportConfig_Group_StateTooltip;
   public static String        Dialog_ImportConfig_Group_Tiles;

   public static String        Dialog_ImportConfig_Info_ConfigDragDrop;
   public static String        Dialog_ImportConfig_Info_ImportActions;
   public static String        Dialog_ImportConfig_Info_MovedDeviceFiles;
   public static String        Dialog_ImportConfig_Info_NoDeviceName;

   public static String        Dialog_ImportConfig_Label_AnimationCrazyFactor;
   public static String        Dialog_ImportConfig_Label_AnimationCrazyFactor_Tooltip;
   public static String        Dialog_ImportConfig_Label_AnimationDuration;
   public static String        Dialog_ImportConfig_Label_AnimationDuration_Tooltip;
   public static String        Dialog_ImportConfig_Label_BackgroundOpacity;
   public static String        Dialog_ImportConfig_Label_BackgroundOpacity_Tooltip;
   public static String        Dialog_ImportConfig_Label_BackupFolder;
   public static String        Dialog_ImportConfig_Label_BackupFolder_Tooltip;
   public static String        Dialog_ImportConfig_Label_ConfigDescription;
   public static String        Dialog_ImportConfig_Label_ConfigName;
   public static String        Dialog_ImportConfig_Label_ConfigTileSize;
   public static String        Dialog_ImportConfig_Label_ConfigTileSize_Tooltip;
   public static String        Dialog_ImportConfig_Label_DeviceFiles;
   public static String        Dialog_ImportConfig_Label_DeviceFiles_Tooltip;
   public static String        Dialog_ImportConfig_Label_DeviceFolder;
   public static String        Dialog_ImportConfig_Label_DeviceFolder_Tooltip;
   public static String        Dialog_ImportConfig_Label_DeviceType_Tooltip;
   public static String        Dialog_ImportConfig_Label_ImportColumns;
   public static String        Dialog_ImportConfig_Label_ImportColumns_Tooltip;
   public static String        Dialog_ImportConfig_Label_ImportLauncher;
   public static String        Dialog_ImportConfig_Label_LastMarkerDistance;
   public static String        Dialog_ImportConfig_Label_LastMarkerDistance_Tooltip;
   public static String        Dialog_ImportConfig_Label_LastMarkerText;
   public static String        Dialog_ImportConfig_Label_StateTooltip_DisplayAbsoluteFilePath;
   public static String        Dialog_ImportConfig_Label_StateTooltip_DisplayAbsoluteFilePath_Tooltip;
   public static String        Dialog_ImportConfig_Label_StateTooltipWidth;

   public static String        Dialog_ImportConfig_Link_FileSystem_Preferences;
   public static String        Dialog_ImportConfig_Link_OtherActions;
   public static String        Dialog_ImportConfig_Link_TourType;

   public static String        Dialog_ImportConfig_Spinner_Speed_Tooltip;

   public static String        Dialog_ImportConfig_State_OFF;
   public static String        Dialog_ImportConfig_State_ON;

   public static String        Dialog_ImportConfig_Tab_Configuration;
   public static String        Dialog_ImportConfig_Tab_Dashboard;
   public static String        Dialog_ImportConfig_Tab_Launcher;

   public static String        Dialog_ModifyTours_Button_LockMultipleToursSelection_Text;
   public static String        Dialog_ModifyTours_Button_UnlockMultipleToursSelection_Text;
   public static String        Dialog_ModifyTours_Checkbox_BatteryValues;
   public static String        Dialog_ModifyTours_Checkbox_CadenceValues;
   public static String        Dialog_ModifyTours_Checkbox_Calories;
   public static String        Dialog_ModifyTours_Checkbox_ElevationValues;
   public static String        Dialog_ModifyTours_Checkbox_GearValues;
   public static String        Dialog_ModifyTours_Checkbox_PowerAndPulseValues;
   public static String        Dialog_ModifyTours_Checkbox_PowerAndSpeedValues;
   public static String        Dialog_ModifyTours_Checkbox_RunningDynamicsValues;
   public static String        Dialog_ModifyTours_Checkbox_SwimmingValues;
   public static String        Dialog_ModifyTours_Checkbox_TemperatureValues;
   public static String        Dialog_ModifyTours_Checkbox_TourMarkers;
   public static String        Dialog_ModifyTours_Checkbox_TourTimerPauses;
   public static String        Dialog_ModifyTours_Checkbox_TrainingValues;
   public static String        Dialog_ModifyTours_Dialog_ToursAreNotAvailable;
   public static String        Dialog_ModifyTours_Dialog_ToursAreNotSelected;
   public static String        Dialog_ModifyTours_Error_2ndDateMustBeLarger;
   public static String        Dialog_ModifyTours_Radio_AllTours;
   public static String        Dialog_ModifyTours_Radio_BetweenDates;
   public static String        Dialog_ModifyTours_Radio_SelectedTours;

   public static String        Dialog_ReimportTours_Action_OpenDialog;
   public static String        Dialog_ReimportTours_Button_ReImport;
   public static String        Dialog_ReimportTours_Checkbox_EntireTour;
   public static String        Dialog_ReimportTours_Checkbox_ImportFileLocation;
   public static String        Dialog_ReimportTours_Checkbox_ImportFileLocation_Tooltip;
   public static String        Dialog_ReimportTours_Checkbox_SkipToursWithImportFileNotFound;
   public static String        Dialog_ReimportTours_Checkbox_TimeSlices;
   public static String        Dialog_ReimportTours_Dialog_ConfirmReimportValues_Message;
   public static String        Dialog_ReimportTours_Dialog_Message;
   public static String        Dialog_ReimportTours_Dialog_Title;
   public static String        Dialog_ReimportTours_Group_Tours;
   public static String        Dialog_ReimportTours_Group_Tours_Tooltip;
   public static String        Dialog_ReimportTours_Group_Data;
   public static String        Dialog_ReimportTours_Group_Data_Tooltip;
   public static String        Dialog_ReimportTours_Radio_TourPart;

   public static String        Dialog_PersonManager_PersonIsNotAvailable_Message;
   public static String        Dialog_PersonManager_PersonIsNotAvailable_Title;

   public static String        Dialog_DeleteTourValues_Group_Reset;
   public static String        Dialog_DeleteTourValues_Group_Reset_Label_Info;
   public static String        Dialog_DeleteTourValues_Group_Reset_Tooltip;

   public static String        Dialog_RetrieveWeather_Dialog_Title;
   public static String        Dialog_RetrieveWeather_Label_WeatherDataNotRetrieved;
   public static String        Dialog_RetrieveWeather_WeatherDataNotFound;

   public static String        Dialog_SetWeatherDescription_Dialog_Title;

   public static String        dialog_is_tour_editor_modified_message;
   public static String        dialog_is_tour_editor_modified_title;

   public static String        dialog_quick_edit_dialog_area_title;
   public static String        dialog_quick_edit_dialog_title;

   public static String        Action_Tag_Add_AutoOpen;
   public static String        Action_Tag_Add_AutoOpen_ModifiedTags;
   public static String        Action_Tag_Add_AutoOpen_Title;
   public static String        Action_Tag_Add_RecentTags;
   public static String        Action_Tag_AutoOpenCancel;
   public static String        Action_Tag_AutoOpenOK;
   public static String        Action_Tag_Delete;
   public static String        Action_Tag_DeleteCategory;
   public static String        Action_Tag_Edit;
   public static String        Action_Tag_Edit_Tooltip;
   public static String        Action_Tag_Restore_Tooltip;
   public static String        Action_Tag_Save_Tooltip;
   public static String        Action_Tag_SetTags;

   public static String        Action_TagCategory_Edit;
   public static String        Action_TagCategory_Edit_Tooltip;
   public static String        Action_TagCategory_EditCategory;

   public static String        Action_TourType_ModifyTourTypeFilter;

   public static String        Adjust_Altitude_CreateDummyAltitudeData_Message;
   public static String        Adjust_Altitude_CreateDummyAltitudeData_Title;
   public static String        Adjust_Altitude_Group_GeoPosition;
   public static String        Adjust_Altitude_Label_GeoPosition_Slices;
   public static String        Adjust_Altitude_Type_HorizontalGeoPosition;

   public static String        action_export_tour;
   public static String        action_print_tour;
   public static String        action_tag_add;
   public static String        action_tag_open_tagging_structure;
   public static String        action_tag_remove;
   public static String        action_tag_remove_all;
   public static String        action_tag_set_all_confirm_message;
   public static String        action_tag_set_all_confirm_title;
   public static String        action_tag_set_all_tag_structures;
   public static String        action_tag_set_tag_expand_type;
   public static String        action_tagView_flat_hierarchical;
   public static String        action_tagView_flat_layout;
   public static String        action_tour_editor_delete_time_slices_keep_time;
   public static String        action_tour_editor_delete_time_slices_remove_time;
   public static String        action_tourbook_select_year_month_tours;
   public static String        action_tourCatalog_open_compare_wizard;
   public static String        action_tourType_modify_tourTypes;
   public static String        App_Action_Upload_Tour;

   public static String        adjust_altitude_action_create_spline_point;
   public static String        adjust_altitude_btn_reset_altitude;
   public static String        adjust_altitude_btn_reset_altitude_and_points;
   public static String        adjust_altitude_btn_reset_altitude_and_points_tooltip;
   public static String        adjust_altitude_btn_reset_altitude_tooltip;
   public static String        adjust_altitude_btn_save_modified_tour;
   public static String        adjust_altitude_btn_srtm_remove_all_points;
   public static String        adjust_altitude_btn_srtm_remove_all_points_tooltip;
   public static String        adjust_altitude_btn_update_altitude;
   public static String        adjust_altitude_btn_update_altitude_tooltip;
   public static String        adjust_altitude_btn_update_modified_tour;
   public static String        adjust_altitude_dlg_dialog_message;
   public static String        adjust_altitude_dlg_dialog_title;
   public static String        adjust_altitude_dlg_shell_title;
   public static String        adjust_altitude_label_adjustment_type;
   public static String        adjust_altitude_type_adjust_end;
   public static String        adjust_altitude_type_adjust_height;
   public static String        adjust_altitude_type_adjust_whole_tour;
   public static String        adjust_altitude_type_srtm;
   public static String        adjust_altitude_type_srtm_spline;
   public static String        adjust_altitude_type_start_and_end;

   public static String        app_action_button_down;
   public static String        app_action_button_up;
   public static String        app_action_collapse_others_tooltip;
   public static String        app_action_edit_adjust_altitude;
   public static String        app_action_edit_rows_tooltip;
   public static String        app_action_edit_tour_marker;
   public static String        app_action_expand_selection_tooltip;
   public static String        app_action_expand_type_flat;
   public static String        app_action_expand_type_year_day;
   public static String        app_action_expand_type_year_month_day;
   public static String        app_action_merge_tour;
   public static String        app_action_open_tour;
   public static String        app_action_quick_edit;
   public static String        app_action_read_edit_tooltip;
   public static String        app_action_update;

   public static String        app_btn_browse;

   public static String        app_db_consistencyCheck_checkFailed;
   public static String        app_db_consistencyCheck_checkIsOK;
   public static String        app_db_consistencyCheck_dlgTitle;

   public static String        app_dlg_confirmFileOverwrite_message;
   public static String        app_dlg_confirmFileOverwrite_title;

   public static String        app_unit_seconds;

   public static String        App__False;
   public static String        App__True;

   public static String        App_Action_About;
   public static String        App_Action_Add;
   public static String        App_Action_Apply;
   public static String        App_Action_ApplyDefaults;
   public static String        App_Action_ApplyDefaults_Tooltip;
   public static String        App_Action_Cancel;
   public static String        App_Action_Close_ToolTip;
   public static String        App_Action_CollapseAll;
   public static String        App_Action_Columns;
   public static String        App_Action_Copy;
   public static String        App_Action_Delete;
   public static String        App_Action_Delete_WithConfirm;
   public static String        App_Action_DeleteProfile;
   public static String        App_Action_DeleteProperty;
   public static String        App_Action_DeleteTourMarker;
   public static String        App_Action_DeselectAll;
   public static String        App_Action_Dialog_ActionIsInProgress_Message;
   public static String        App_Action_Dialog_ActionIsInProgress_Title;
   public static String        App_Action_Duplicate;
   public static String        App_Action_edit_tour;
   public static String        App_Action_Edit;
   public static String        App_Action_Expand_All_Tooltip;
   public static String        App_Action_ExtractTour;
   public static String        App_Action_JoinTours;
   public static String        App_Action_Menu_Directory;
   public static String        App_Action_Menu_help;
   public static String        App_Action_Menu_Map;
   public static String        App_Action_Menu_New;
   public static String        App_Action_Menu_tools;
   public static String        App_Action_Menu_Tour;
   public static String        App_Action_New;
   public static String        App_Action_open_perspective;
   public static String        App_Action_open_preferences;
   public static String        App_Action_OpenOtherViews;
   public static String        App_Action_RefreshView;
   public static String        App_Action_Remove;
   public static String        App_Action_Remove_Immediate;
   public static String        App_Action_RemoveTourPhotos;
   public static String        App_Action_Rename;
   public static String        App_Action_RestartApp;
   public static String        App_Action_set_tour_type;
   public static String        App_Action_Save;
   public static String        App_Action_SetPerson;
   public static String        App_Action_SplitTour;
   public static String        App_Action_ToolTipLocation_AboveTourChart_Tooltip;
   public static String        App_Action_ToolTipLocation_BelowTourChart_Tooltip;
   public static String        App_Action_UncheckAll;
   public static String        App_Action_UpdateNew;

   public static String        App_measurement_tooltip;

   public static String        App_Cadence_Invalid;
   public static String        App_Cadence_None;
   public static String        App_Cadence_Rpm;
   public static String        App_Cadence_Spm;

   public static String        App_Db_Compress_Button_CompressByCopying;
   public static String        App_Db_Compress_Button_CompressByCopying_Tooltip;
   public static String        App_Db_Compress_Button_CompressDatabase;
   public static String        App_Db_Compress_Button_CompressInplace;
   public static String        App_Db_Compress_Button_CompressInplace_Tooltip;
   public static String        App_Db_Compress_Dialog_ConfirmCompress_Message;
   public static String        App_Db_Compress_DialogTitle;
   public static String        App_Db_Compress_LogLabel_After;
   public static String        App_Db_Compress_LogLabel_Before;
   public static String        App_Db_Compress_LogLabel_Difference;
   public static String        App_Db_Compress_LogLabel_Index;
   public static String        App_Db_Compress_LogLabel_NotUsed;
   public static String        App_Db_Compress_LogLabel_Table;
   public static String        App_Db_Compress_LogLabel_Totals;
   public static String        App_Db_Compress_LogLabel_Used;
   public static String        App_Db_Compress_LogHeader_After;
   public static String        App_Db_Compress_LogHeader_Before;
   public static String        App_Db_Compress_LogHeader_Difference;
   public static String        App_Db_Compress_Monitor_SubTask;
   public static String        App_Db_Compress_Monitor_Task;

   public static String        App_Default_PersonFirstName;

   public static String        App_Dialog_FirstStartup_Message;
   public static String        App_Dialog_FirstStartup_Title;
   public static String        App_Dialog_FirstStartupTip_Message;
   public static String        App_Dialog_FirstStartupTip_Title;
   public static String        App_Dialog_RestartApp_Title;

   public static String        App_Label_BooleanNo;
   public static String        App_Label_BooleanYes;
   public static String        App_Label_H_MM;
   public static String        App_Label_ISO8601;
   public static String        App_Label_max;
   public static String        App_Label_NotAvailable;
   public static String        App_Label_NotAvailable_Shortcut;

   public static String        App_Link_RestoreDefaultValues;

   public static String        App_People_item_all;
   public static String        App_People_tooltip;

   public static String        App_SortDirection_Ascending;
   public static String        App_SortDirection_Descending;
   public static String        App_SortDirection_None;

   public static String        App_Splash_Copyright;

   public static String        App_SplashMessage_Finalize;
   public static String        App_SplashMessage_StartingApplication;
   public static String        App_SplashMessage_StartingDatabase;

   public static String        App_Title;

   public static String        App_ToggleState_DoNotShowAgain;

   public static String        App_Tour_type_item_all_types;
   public static String        App_Tour_type_item_not_defined;

   public static String        App_TourType_ToolTip;
   public static String        App_TourType_ToolTipTitle;

   public static String        App_Unit_HHMMSS;
   public static String        App_Unit_Milliseconds;
   public static String        App_Unit_Minute;
   public static String        App_Unit_Minute_Small;
   public static String        App_Unit_Px;
   public static String        App_Unit_Seconds_Small;

   public static String        App_Window_Title;

   public static String        Collate_Tours_Label_DummyTour_Tooltip;
   public static String        Collate_Tours_Label_TimeScale_BeforePresent;
   public static String        Collate_Tours_Label_TimeScale_Today;
   public static String        Collate_Tours_Label_TooltipHeader_Multiple;
   public static String        Collate_Tours_Label_TooltipHeader_Single;
   public static String        Collate_Tours_Link_SelectTourType;
   public static String        Collate_Tours_Link_SelectTourType_Tooltip;

   public static String        Column_SortInfo_CanNotSort;
   public static String        Column_SortInfo_CanSort;

   public static String        Compare_Result_Action_check_selected_tours;
   public static String        Compare_Result_Action_remove_save_result;
   public static String        Compare_Result_Action_save_checked_tours;
   public static String        Compare_Result_Action_save_checked_tours_tooltip;
   public static String        Compare_Result_Action_uncheck_selected_tours;

   public static String        Compare_Result_Column_diff;
   public static String        Compare_Result_Column_diff_label;
   public static String        Compare_Result_Column_diff_tooltip;
   public static String        Compare_Result_Column_kmh_db_label;
   public static String        Compare_Result_Column_kmh_db_tooltip;
   public static String        Compare_Result_Column_kmh_label;
   public static String        Compare_Result_Column_kmh_moved_label;
   public static String        Compare_Result_Column_kmh_moved_tooltip;
   public static String        Compare_Result_Column_kmh_tooltip;
   public static String        Compare_Result_Column_tour;

   public static String        Compute_BreakTime_Button_ComputeAllTours;
   public static String        Compute_BreakTime_Button_ComputeAllTours_Tooltip;
   public static String        Compute_BreakTime_Button_RestoreDefaultValues;
   public static String        Compute_BreakTime_Button_RestoreDefaultValues_Tooltip;
   public static String        Compute_BreakTime_Button_SetDefaultValues;
   public static String        Compute_BreakTime_Button_SetDefaultValues_Tooltip;
   public static String        Compute_BreakTime_Dialog_ComputeForAllTours_Message;
   public static String        Compute_BreakTime_Dialog_ComputeForAllTours_Title;
   public static String        Compute_BreakTime_ForAllTour_Job_Result;
   public static String        Compute_BreakTime_ForAllTour_Job_SubTask;
   public static String        Compute_BreakTime_Group_BreakTime;
   public static String        Compute_BreakTime_Label_ComputeBreakTimeBy;
   public static String        Compute_BreakTime_Label_Description;
   public static String        Compute_BreakTime_Label_Description_ComputeByAvgSliceSpeed;
   public static String        Compute_BreakTime_Label_Description_ComputeByAvgSpeed;
   public static String        Compute_BreakTime_Label_Description_ComputeBySliceSpeed;
   public static String        Compute_BreakTime_Label_Description_ComputeByTime;
   public static String        Compute_BreakTime_Label_Hints;
   public static String        Compute_BreakTime_Label_MinimumAvgSpeed;
   public static String        Compute_BreakTime_Label_MinimumDistance;
   public static String        Compute_BreakTime_Label_MinimumSliceSpeed;
   public static String        Compute_BreakTime_Label_MinimumSliceTime;
   public static String        Compute_BreakTime_Label_MinimumTime;
   public static String        Compute_BreakTime_Label_SliceDiffBreak;
   public static String        Compute_BreakTime_Label_SliceDiffBreak_Tooltip;
   public static String        Compute_BreakTime_Label_Title;
   public static String        Compute_BreakTime_Label_TourBreakTime;
   public static String        Compute_BreakTime_Method_SpeedByAverage;
   public static String        Compute_BreakTime_Method_SpeedByAverageAndSlice;
   public static String        Compute_BreakTime_Method_SpeedBySlice;
   public static String        Compute_BreakTime_Method_TimeDistance;

   public static String        Compute_CadenceZonesTimes_Group;
   public static String        Compute_CadenceZonesTimes_ComputeForAllTours_Job_Result;
   public static String        Compute_CadenceZonesTimes_Dialog_ComputeForAllTours_Message;
   public static String        Compute_CadenceZonesTimes_Dialog_ComputeForAllTours_Title;

   public static String        Compute_HrZone_Group;
   public static String        Compute_HrZone_Link;
   public static String        Compute_HrZones_Dialog_ComputeAllTours_Title;
   public static String        Compute_HrZones_Dialog_ComputeAllTours_Title_Message;
   public static String        Compute_HrZones_Job_ComputeAllTours_Result;
   public static String        Compute_HrZones_Job_ComputeAllTours_SubTask;

   public static String        Compute_Smoothing_Button_ForAllTours;
   public static String        Compute_Smoothing_Button_ForAllTours_Tooltip;

   public static String        Compute_TourValue_ElevationGain_Button_ComputeValues_Tooltip;
   public static String        Compute_TourValue_ElevationGain_Dlg_ComputeValues_Message;
   public static String        Compute_TourValue_ElevationGain_Label_Description;
   public static String        Compute_TourValue_ElevationGain_Link_DBTolerance;
   public static String        Compute_TourValue_ElevationGain_Message;
   public static String        Compute_TourValue_ElevationGain_ResultText;
   public static String        Compute_TourValue_ElevationGain_Title;

   public static String        Compute_TourValueSpeed_Title;

   public static String        Compute_Values_Group_Smoothing;
   public static String        Compute_Values_Label_Info;

   public static String        Conconi_Chart_Chk_LogScaling;
   public static String        Conconi_Chart_Chk_LogScaling_Tooltip;
   public static String        Conconi_Chart_DeflactionPoint;
   public static String        Conconi_Chart_InvalidData;
   public static String        Conconi_Chart_Label_ScalingFactor;
   public static String        Conconi_Chart_Label_Tour;
   public static String        Conconi_Chart_Label_Tour_Tooltip;

   public static String        compute_tourValueElevation_button_computeValues;
   public static String        compute_tourValueElevation_dlg_computeValues_title;
   public static String        compute_tourValueElevation_group_computeTourAltitude;
   public static String        compute_tourValueElevation_label_description_Hints;
   public static String        compute_tourValueElevation_subTaskText;

   public static String        compute_tourValueSpeed_label_description;
   public static String        compute_tourValueSpeed_label_description_Hints;
   public static String        compute_tourValueSpeed_label_speedTimeSlice;

   public static String        Database_Monitor_CreateDatabase;
   public static String        Database_Monitor_db_service_task;
   public static String        Database_Monitor_persistent_service_task;
   public static String        Database_Monitor_SetupLucene;

   public static String        Database_Monitor_SetupPooledConnection;
   public static String        Database_Monitor_UpgradeDatabase;

   public static String        DataImport_Error_file_does_not_exist_msg;
   public static String        DataImport_Error_file_does_not_exist_title;

   public static String        DataImport_ConfirmImport_title;

   public static String        Db_Field_TourData_Description;
   public static String        Db_Field_TourData_EndPlace;
   public static String        Db_Field_TourData_StartPlace;
   public static String        Db_Field_TourData_Title;
   public static String        Db_Field_TourData_TourImportFilePath;
   public static String        Db_Field_TourData_Weather;
   public static String        Db_Field_TourMarker_UrlAddress;
   public static String        Db_Field_TourMarker_UrlText;
   public static String        Db_Field_TourTag_Name;
   public static String        Db_Field_TourTag_Notes;

   public static String        DeviceManager_Selection_device_is_not_selected;

   public static String        Dialog_DeleteData_Title;
   public static String        Dialog_ReimportData_Title;

   public static String        Dialog_ImportData_ReplaceImportFilename_Message;
   public static String        Dialog_ImportData_ReplaceImportFilename_Radio_DoNothing;
   public static String        Dialog_ImportData_ReplaceImportFilename_Radio_DoNothingAnymore;
   public static String        Dialog_ImportData_ReplaceImportFilename_Radio_ReplaceAll;
   public static String        Dialog_ImportData_ReplaceImportFilename_Radio_ReplaceThis;
   public static String        Dialog_ImportData_ReplaceImportFilename_Title;

   public static String        Dialog_JoinTours_Checkbox_CreateTourMarker;
   public static String        Dialog_JoinTours_Checkbox_IncludeDescription;
   public static String        Dialog_JoinTours_Checkbox_IncludeMarkerWaypoints;
   public static String        Dialog_JoinTours_Checkbox_InsertPauses;
   public static String        Dialog_JoinTours_ComboText_ConcatenateTime;
   public static String        Dialog_JoinTours_ComboText_KeepTime;
   public static String        Dialog_JoinTours_ComboText_MarkerTourTime;
   public static String        Dialog_JoinTours_ComboText_TourTileCustom;
   public static String        Dialog_JoinTours_ComboText_TourTitleFromTour;
   public static String        Dialog_JoinTours_ComboText_TourTypeCustom;
   public static String        Dialog_JoinTours_ComboText_TourTypeFromTour;
   public static String        Dialog_JoinTours_ComboText_TourTypePrevious;
   public static String        Dialog_JoinTours_DlgArea_Message;
   public static String        Dialog_JoinTours_DlgArea_Title;
   public static String        Dialog_JoinTours_InvalidData_Distance;
   public static String        Dialog_JoinTours_InvalidData_DlgMessage;
   public static String        Dialog_JoinTours_InvalidData_DlgTitle;
   public static String        Dialog_JoinTours_InvalidData_Latitude;
   public static String        Dialog_JoinTours_InvalidData_Power;
   public static String        Dialog_JoinTours_InvalidData;
   public static String        Dialog_JoinTours_InvalidData_InvalidTours;
   public static String        Dialog_JoinTours_InvalidData_RequiredDataSeries;
   public static String        Dialog_JoinTours_InvalidData_Speed;
   public static String        Dialog_JoinTours_InvalidData_Time;
   public static String        Dialog_JoinTours_Label_DefaultTitle;
   public static String        Dialog_JoinTours_Label_DeviceName;
   public static String        Dialog_JoinTours_Label_JoinMethod;
   public static String        Dialog_JoinTours_Label_Tour;
   public static String        Dialog_JoinTours_Label_TourDate;
   public static String        Dialog_JoinTours_Label_TourMarkerText;
   public static String        Dialog_JoinTours_Label_TourTime;
   public static String        Dialog_JoinTours_Label_TourType;
   public static String        Dialog_JoinTours_Link_TourType;

   public static String        Dialog_SaveTags_Dialog_Title;
   public static String        Dialog_SaveTags_Label_Info;
   public static String        Dialog_SaveTags_Label_Progress_SubTask;
   public static String        Dialog_SaveTags_Label_Progress_Task;
   public static String        Dialog_SaveTags_Label_SelectedTags;
   public static String        Dialog_SaveTags_Radio_AppendNewTags;
   public static String        Dialog_SaveTags_Radio_RemoveTags_All;
   public static String        Dialog_SaveTags_Radio_RemoveTags_Selected;
   public static String        Dialog_SaveTags_Radio_ReplaceTags;
   public static String        Dialog_SaveTags_Wizard_Title;

   public static String        Dialog_SetTimeZone_Button_AdjustTimeZone;
   public static String        Dialog_SetTimeZone_Dialog_Title;
   public static String        Dialog_SetTimeZone_Label_Info;
   public static String        Dialog_SetTimeZone_Label_Progress_SubTask;
   public static String        Dialog_SetTimeZone_Label_Progress_Task;
   public static String        Dialog_SetTimeZone_Radio_AdjustTourStart_YYMMDD;
   public static String        Dialog_SetTimeZone_Radio_AdjustTourStartYYMMDD_Tooltip;
   public static String        Dialog_SetTimeZone_Radio_RemoveTimeZone;
   public static String        Dialog_SetTimeZone_Radio_SetTimeZone_FromCombo;
   public static String        Dialog_SetTimeZone_Radio_SetTimeZone_FromGeo;

   public static String        Dialog_SplitTour_Checkbox_IncludeDescription;
   public static String        Dialog_SplitTour_Checkbox_KeepTime;
   public static String        Dialog_SplitTour_ComboText_KeepSlices;
   public static String        Dialog_SplitTour_ComboText_RemoveSlices;
   public static String        Dialog_SplitTour_ComboText_TourTileCustom;
   public static String        Dialog_SplitTour_ComboText_TourTitleFromFirstMarker;
   public static String        Dialog_SplitTour_ComboText_TourTitleFromTour;
   public static String        Dialog_SplitTour_ComboText_TourTypeCustom;
   public static String        Dialog_SplitTour_ComboText_TourTypeFromTour;
   public static String        Dialog_SplitTour_ComboText_TourTypePrevious;
   public static String        Dialog_SplitTour_DlgArea_Message;
   public static String        Dialog_SplitTour_DlgArea_Title;
   public static String        Dialog_SplitTour_Label_DefaultTitle;
   public static String        Dialog_SplitTour_Label_DeviceName;
   public static String        Dialog_SplitTour_Label_Person;
   public static String        Dialog_SplitTour_Label_Person_Tooltip;
   public static String        Dialog_SplitTour_Label_SplitMethod;
   public static String        Dialog_SplitTour_Label_TourStartDateTime;
   public static String        Dialog_SplitTour_Label_TourTitle;
   public static String        Dialog_SplitTour_Label_TourTitle_Tooltip;

   public static String        Dialog_TourTag_EditTag_Message;
   public static String        Dialog_TourTag_EditTag_Title;
   public static String        Dialog_TourTag_Label_Notes;
   public static String        Dialog_TourTag_Label_TagName;
   public static String        Dialog_TourTag_Title;

   public static String        Dialog_TourTagCategory_EditCategory_Message;
   public static String        Dialog_TourTagCategory_EditCategory_Title;
   public static String        Dialog_TourTagCategory_Label_CategoryName;
   public static String        Dialog_TourTagCategory_Title;

   public static String        Dlg_AdjustAltitude_Group_options;
   public static String        Dlg_AdjustAltitude_Label_end_altitude;
   public static String        Dlg_AdjustAltitude_Label_end_altitude_tooltip;
   public static String        Dlg_AdjustAltitude_Label_max_altitude;
   public static String        Dlg_AdjustAltitude_Label_max_altitude_tooltip;
   public static String        Dlg_AdjustAltitude_Label_original_values;
   public static String        Dlg_AdjustAltitude_Label_start_altitude;
   public static String        Dlg_AdjustAltitude_Label_start_altitude_tooltip;
   public static String        Dlg_AdjustAltitude_Radio_keep_bottom_altitude;
   public static String        Dlg_AdjustAltitude_Radio_keep_bottom_altitude_tooltip;
   public static String        Dlg_AdjustAltitude_Radio_keep_start_altitude;
   public static String        Dlg_AdjustAltitude_Radio_keep_start_altitude_tooltip;

   public static String        Dlg_TourMarker_Button_delete;
   public static String        Dlg_TourMarker_Button_delete_tooltip;
   public static String        Dlg_TourMarker_Button_HideAllMarker;
   public static String        Dlg_TourMarker_Button_HideAllMarker_Tooltip;
   public static String        Dlg_TourMarker_Button_PasteFromClipboard_Tooltip;
   public static String        Dlg_TourMarker_Button_ShowAllMarker;
   public static String        Dlg_TourMarker_Button_ShowAllMarker_Tooltip;
   public static String        Dlg_TourMarker_Button_undo;
   public static String        Dlg_TourMarker_Button_undo_tooltip;
   public static String        Dlg_TourMarker_Checkbox_MarkerVisibility;
   public static String        Dlg_TourMarker_Dlg_Message;
   public static String        Dlg_TourMarker_Dlg_title;
   public static String        Dlg_TourMarker_Group_Label;
   public static String        Dlg_TourMarker_Group_Url;
   public static String        Dlg_TourMarker_Label_Description;
   public static String        Dlg_TourMarker_Label_Label;
   public static String        Dlg_TourMarker_Label_LinkText;
   public static String        Dlg_TourMarker_Label_LinkText_Tooltip;
   public static String        Dlg_TourMarker_Label_LinkUrl;
   public static String        Dlg_TourMarker_Label_LinkUrl_Tooltip;
   public static String        Dlg_TourMarker_Label_markers;
   public static String        Dlg_TourMarker_Label_OffsetHorizontal;
   public static String        Dlg_TourMarker_Label_OffsetVertical;
   public static String        Dlg_TourMarker_Label_position;
   public static String        Dlg_TourMarker_Label_Position_Tooltip;
   public static String        Dlg_TourMarker_MsgBox_delete_marker_message;
   public static String        Dlg_TourMarker_MsgBox_delete_markers_message;
   public static String        Dlg_TourMarker_MsgBox_delete_marker_title;
   public static String        Dlg_TourMarker_MsgBox_delete_markers_title;
   public static String        Dlg_TourMarker_MsgBox_WrongFormat_Message;
   public static String        Dlg_TourMarker_MsgBox_WrongFormat_Title;

   public static String        Elevation_Compare_Action_AppTourFilter_Tooltip;
   public static String        Elevation_Compare_Action_CompareAllTours;
   public static String        Elevation_Compare_Action_IsNotUsingAppFilter_Tooltip;
   public static String        Elevation_Compare_Action_IsUsingAppFilter_Tooltip;
   public static String        Elevation_Compare_Action_Layout_WithoutYearCategories_Tooltip;
   public static String        Elevation_Compare_Action_Layout_WithYearCategories_Tooltip;
   public static String        Elevation_Compare_Action_RemoveReferenceTours;
   public static String        Elevation_Compare_Action_ReRunComparison_Tooltip;
   public static String        Elevation_Compare_Action_TourCompareFilter_Tooltip;
   public static String        Elevation_Compare_Monitor_SubTask;
   public static String        Elevation_Compare_Monitor_Task;

   public static String        External_Link_MyTourbook;
   public static String        External_Link_MyTourbook_TourChartSmoothing;
   public static String        External_Link_Weather_ApiSignup;

   public static String        Format_hhmm;
   public static String        Format_rawdata_file_yyyy_mm_dd;
   public static String        Format_yyyymmdd_hhmmss;

   public static String        Geo_Compare_Label_ReferenceTour;

   public static String        GeoCompare_View_Action_AppFilter_Tooltip;
   public static String        GeoCompare_View_Action_OnOff_Tooltip;
   public static String        GeoCompare_View_Column_GeoDiff_Header;
   public static String        GeoCompare_View_Column_GeoDiff_Label;
   public static String        GeoCompare_View_Column_GeoDiff_Relative_Header;
   public static String        GeoCompare_View_Column_GeoDiff_Relative_Label;
   public static String        GeoCompare_View_Column_GeoDiff_Relative_Tooltip;
   public static String        GeoCompare_View_Column_SequenceNumber_Header;
   public static String        GeoCompare_View_Column_SequenceNumber_Label;
   public static String        GeoCompare_View_Label_GeoParts;
   public static String        GeoCompare_View_Label_GeoParts_Tooltip;
   public static String        GeoCompare_View_Label_PossibleTours;
   public static String        GeoCompare_View_Label_TimeSlices;
   public static String        GeoCompare_View_PageText_MultipleToursNotSupported;
   public static String        GeoCompare_View_PageText_NoTourWithGeoData;
   public static String        GeoCompare_View_State_CompareResult;
   public static String        GeoCompare_View_State_ComparingIsCanceled;
   public static String        GeoCompare_View_State_StartComparing;

   public static String        Graph_Label_Time_Moving;
   public static String        Graph_Label_Time_Paused;
   public static String        Graph_Label_Time_Elapsed;
   public static String        Graph_Label_Time_Break;
   public static String        Graph_Label_Time_Recorded;

   public static String        HR_Zone_01_060_Moderate;
   public static String        HR_Zone_01_060_Moderate_Shortcut;
   public static String        HR_Zone_01_070_FatBurning;
   public static String        HR_Zone_01_070_FatBurning_Shortcut;
   public static String        HR_Zone_01_080_Aerobic;
   public static String        HR_Zone_01_080_Aerobic_Shortcut;
   public static String        HR_Zone_01_090_Anaerobic;
   public static String        HR_Zone_01_090_Anaerobic_Shortcut;
   public static String        HR_Zone_01_100_Maximum;
   public static String        HR_Zone_01_100_Maximum_Shortcut;
   public static String        HR_Zone_02_065_KB;
   public static String        HR_Zone_02_065_KB_Shortcut;
   public static String        HR_Zone_02_075_GA1;
   public static String        HR_Zone_02_075_GA1_Shortcut;
   public static String        HR_Zone_02_085_GA2;
   public static String        HR_Zone_02_085_GA2_Shortcut;
   public static String        HR_Zone_02_095_EB;
   public static String        HR_Zone_02_095_EB_Shortcut;
   public static String        HR_Zone_02_095_SB;
   public static String        HR_Zone_02_095_SB_Shortcut;
   public static String        HR_Zone_Template_01_Moderate60Max100;
   public static String        HR_Zone_Template_02_GA1GA2;
   public static String        HR_Zone_Template_Select;

   public static String        HRMax_Label;

   public static String        HRMaxFormula_Name_HRmax_191_5;
   public static String        HRMaxFormula_Name_HRmax_205_8;
   public static String        HRMaxFormula_Name_HRmax_206_9;
   public static String        HRMaxFormula_Name_HRmax_220_age;
   public static String        HRMaxFormula_Name_Manual;

   public static String        HRV_View_Action_ShowAllValues;
   public static String        HRV_View_Action_SynchChartScale;
   public static String        HRV_View_Label_InvalidData;
   public static String        HRV_View_Label_LeftChartBorder;
   public static String        HRV_View_Label_LeftChartBorder_Tooltip;
   public static String        HRV_View_Label_RightChartBorder;
   public static String        HRV_View_Label_RightChartBorder_Tooltip;

   public static String        import_data_action_assignment_is_not_available;
   public static String        import_data_action_assignMergedTour;
   public static String        import_data_action_assignMergedTour_default;
   public static String        import_data_action_clear_view;
   public static String        import_data_action_clear_view_tooltip;
   public static String        import_data_action_save_tour_for_person;
   public static String        import_data_action_save_tour_with_person;
   public static String        import_data_action_save_tours_for_person;
   public static String        import_data_dlg_save_tour_msg;
   public static String        import_data_dlg_save_tour_title;
   public static String        import_data_importTours_subTask;
   public static String        import_data_importTours_task;
   public static String        import_data_updateDataFromDatabase_subTask;
   public static String        import_data_updateDataFromDatabase_task;

   public static String        Import_Data_Action_DeleteTourFiles;
   public static String        Import_Data_Action_EditImportPreferences;
   public static String        Import_Data_Action_OpenLogView;
   public static String        Import_Data_Action_OpenLogView_Tooltip;
   public static String        Import_Data_Action_RemoveTour;
   public static String        Import_Data_Action_RemoveToursWhenClosed;
   public static String        Import_Data_Action_SetupEasyImport_Tooltip;
   public static String        Import_Data_Default_FirstEasyImportLauncher_Description;
   public static String        Import_Data_Default_FirstEasyImportLauncher_Name;
   public static String        Import_Data_Default_ImportConfig_Name;

   public static String        Import_Data_Dialog_DeleteTourFiles_LastChance_Message;
   public static String        Import_Data_Dialog_DeleteTourFiles_Message;
   public static String        Import_Data_Dialog_DeleteTourFiles_Title;
   public static String        Import_Data_Dialog_DeleteTourValues_Task;
   public static String        Import_Data_Dialog_EasyImport_InvalidBackupFolder_Message;
   public static String        Import_Data_Dialog_EasyImport_InvalidDeviceFolder_Message;
   public static String        Import_Data_Dialog_EasyImport_NoImportFiles_Message;
   public static String        Import_Data_Dialog_EasyImport_Title;
   public static String        Import_Data_Dialog_GetAlternativePath_Message;
   public static String        Import_Data_Dialog_GetReimportedFilePath_Message;
   public static String        Import_Data_Dialog_IsCancelTourValuesDeletion_Message;
   public static String        Import_Data_Dialog_IsCancelTourValuesDeletion_Title;
   public static String        Import_Data_Dialog_IsCancelReImport_Message;
   public static String        Import_Data_Dialog_IsCancelReImport_Title;
   public static String        Import_Data_Dialog_NoActivePersion_Message;
   public static String        Import_Data_Dialog_Reimport_SubTask;
   public static String        Import_Data_Dialog_Reimport_Task;
   public static String        Import_Data_Dialog_Reimport_Title;
   public static String        Import_Data_Dialog_ReimportFile_Title;

   public static String        Import_Data_Error_CreatingFileName_Message;
   public static String        Import_Data_Error_CreatingFileName_Title;
   public static String        Import_Data_HTML_AcquireDeviceInfo;
   public static String        Import_Data_HTML_Action_OldUI;
   public static String        Import_Data_HTML_Action_OldUI_Tooltip;
   public static String        Import_Data_HTML_AdjustTemperature_No;
   public static String        Import_Data_HTML_AdjustTemperature_Yes;
   public static String        Import_Data_HTML_AllFilesAreBackedUp;
   public static String        Import_Data_HTML_AllFilesAreImported;
   public static String        Import_Data_HTML_DeleteDeviceFiles_No;
   public static String        Import_Data_HTML_DeleteDeviceFiles_Yes;
   public static String        Import_Data_HTML_DeleteFilesNO;
   public static String        Import_Data_HTML_DeleteFilesYES;
   public static String        Import_Data_HTML_DeviceOff_Tooltip;
   public static String        Import_Data_HTML_DeviceOn_Tooltip;
   public static String        Import_Data_HTML_EasyImport;
   public static String        Import_Data_HTML_FolderIsNotAvailable;
   public static String        Import_Data_HTML_GetTours;
   public static String        Import_Data_HTML_ImportFromFiles_Action;
   public static String        Import_Data_HTML_ImportFromFiles_ActionTooltip;
   public static String        Import_Data_HTML_LastMarker_No;
   public static String        Import_Data_HTML_LastMarker_Yes;
   public static String        Import_Data_HTML_MovedFiles;
   public static String        Import_Data_HTML_NotBackedUpFiles;
   public static String        Import_Data_HTML_NothingIsWatched;
   public static String        Import_Data_HTML_NotImportedFiles;
   public static String        Import_Data_HTML_ReceiveFromSerialPort_ConfiguredAction;
   public static String        Import_Data_HTML_ReceiveFromSerialPort_ConfiguredLink;
   public static String        Import_Data_HTML_ReceiveFromSerialPort_DirectlyAction;
   public static String        Import_Data_HTML_ReceiveFromSerialPort_DirectlyLink;
   public static String        Import_Data_HTML_RetrieveWeatherData_Yes;
   public static String        Import_Data_HTML_RetrieveWeatherData_No;
   public static String        Import_Data_HTML_SaveTour_No;
   public static String        Import_Data_HTML_SaveTour_Yes;
   public static String        Import_Data_HTML_Title_Backup;
   public static String        Import_Data_HTML_Title_Delete;
   public static String        Import_Data_HTML_Title_Device;
   public static String        Import_Data_HTML_Title_Files;
   public static String        Import_Data_HTML_Title_Moved;
   public static String        Import_Data_HTML_Title_Moved_State;
   public static String        Import_Data_HTML_WatchingIsOff;
   public static String        Import_Data_HTML_WatchingOff;
   public static String        Import_Data_HTML_WatchingOn;

   public static String        Import_Data_Log_ReimportIsInvalid_DifferentTourId_Message;
   public static String        Import_Data_Log_ReimportIsInvalid_TourNotFoundInFile_Message;
   public static String        Import_Data_Log_ReimportIsInvalid_WrongSliceNumbers;
   public static String        Import_Data_Monitor_Backup;
   public static String        Import_Data_Monitor_Backup_SubTask;
   public static String        Import_Data_Monitor_DeleteTourFiles;
   public static String        Import_Data_Monitor_DeleteTourFiles_Subtask;
   public static String        Import_Data_Task_CloseDeviceInfo;
   public static String        Import_Data_Task_CloseDeviceInfo_CannotClose;

   public static String        Import_Data_TourTypeConfig_BySpeed;
   public static String        Import_Data_TourTypeConfig_OneForAll;
   public static String        Import_Data_OldUI_Label_Hint;
   public static String        Import_Data_OldUI_Label_Info;
   public static String        Import_Data_OldUI_Link_Import;
   public static String        Import_Data_OldUI_Link_ReceiveFromSerialPort_Configured;
   public static String        Import_Data_OldUI_Link_ReceiveFromSerialPort_Directly;
   public static String        Import_Data_OldUI_Link_ShowNewUI;

   public static String        Import_Wizard_Control_combo_person_default_settings;
   public static String        Import_Wizard_Control_combo_ports_not_available;
   public static String        Import_Wizard_Dlg_message;
   public static String        Import_Wizard_Dlg_title;
   public static String        Import_Wizard_Error_com_port_is_required;
   public static String        Import_Wizard_Error_path_is_invalid;
   public static String        Import_Wizard_Error_select_a_device;
   public static String        Import_Wizard_Label_auto_save_path;
   public static String        Import_Wizard_Label_device;
   public static String        Import_Wizard_Label_serial_port;
   public static String        Import_Wizard_Label_use_settings;
   public static String        Import_Wizard_Message_replace_existing_file;
   public static String        Import_Wizard_Message_Title;
   public static String        Import_Wizard_Monitor_stop_port;
   public static String        Import_Wizard_Monitor_task_msg;
   public static String        Import_Wizard_Monitor_task_received_bytes;
   public static String        Import_Wizard_Monitor_wait_for_data;
   public static String        Import_Wizard_Thread_name_read_device_data;

   public static String        Log_App_Canceled;
   public static String        Log_App_LoadedTours;
   public static String        Log_App_LoadingSelectedTours;
   public static String        Log_App_PerformedInNSeconds;

   public static String        Log_ComputeCadenceZonesTimes_001_Start;
   public static String        Log_ComputeCadenceZonesTimes_002_End;
   public static String        Log_ComputeCadenceZonesTimes_010_Success;
   public static String        Log_ComputeCadenceZonesTimes_011_NoSuccess;

   public static String        Log_Delete_Text;
   public static String        Log_Delete_TourValues_End;

   public static String        Log_EasyImport_000_ImportStart;
   public static String        Log_EasyImport_001_BackupTourFiles;
   public static String        Log_EasyImport_001_Copy;
   public static String        Log_EasyImport_002_End;
   public static String        Log_EasyImport_002_TourFilesStart;
   public static String        Log_EasyImport_003_TourType;
   public static String        Log_EasyImport_003_TourType_Item;
   public static String        Log_EasyImport_004_SetLastMarker;
   public static String        Log_EasyImport_005_AdjustTemperatureValues;
   public static String        Log_EasyImport_006_RetrieveWeatherData;
   public static String        Log_EasyImport_099_SaveTour;
   public static String        Log_EasyImport_100_DeleteTourFiles;
   public static String        Log_EasyImport_101_TurnWatchingOff;
   public static String        Log_EasyImport_999_ImportEnd;

   public static String        Log_Import_DeleteTourFiles;
   public static String        Log_Import_DeleteTourFiles_End;
   public static String        Log_Import_Tour;
   public static String        Log_Import_Tour_End;
   public static String        Log_Import_Tour_Imported;
   public static String        Log_Import_Tours_Imported_From_File;

   public static String        Log_ModifiedTour_Old_Data_Vs_New_Data;
   public static String        Log_ModifiedTour_Combined_Values;

   public static String        Log_Reimport_ManualTour;
   public static String        Log_Reimport_PreviousFiles;
   public static String        Log_Reimport_PreviousFiles_End;
   public static String        Log_Reimport_Text;
   public static String        Log_Reimport_Tour_Skipped;
   public static String        Log_Reimport_Tour_Skipped_FileLocationDialog_Auto;
   public static String        Log_Reimport_Tour_Skipped_FileLocationDialog_ByUser;
   public static String        Log_Reimport_Tour_Skipped_FilePathIsEmpty;
   public static String        Log_Reimport_Tour_Skipped_OtherReasons;

   public static String        Log_RetrieveWeatherData_001_Start;
   public static String        Log_RetrieveWeatherData_002_End;
   public static String        Log_RetrieveWeatherData_010_NoGpsDataSeries;

   public static String        Log_SaveTags_End;
   public static String        Log_SaveTags_Progress_AppendTags;
   public static String        Log_SaveTags_Progress_RemoveAllTags;
   public static String        Log_SaveTags_Progress_RemoveSelectedTags;
   public static String        Log_SaveTags_Progress_ReplaceTags;
   public static String        Log_SaveTags_Start_AppendTags;
   public static String        Log_SaveTags_Start_RemoveAllTags;
   public static String        Log_SaveTags_Start_RemoveSelectedTags;
   public static String        Log_SaveTags_Start_ReplaceTags;

   public static String        Log_SetMinMaxTemperature_NoSuccess;
   public static String        Log_SetMinMaxTemperature_Startup;
   public static String        Log_SetMinMaxTemperature_Success;

   public static String        Log_SetTimeZone_001_Start_FromGeo;
   public static String        Log_SetTimeZone_001_Start_FromList;
   public static String        Log_SetTimeZone_001_Start_Remove;
   public static String        Log_SetTimeZone_001_Start_YYMMDD;
   public static String        Log_SetTimeZone_002_End;
   public static String        Log_SetTimeZone_010_SetSelected;
   public static String        Log_SetTimeZone_011_SetFromGeo;
   public static String        Log_SetTimeZone_012_NoGeo;
   public static String        Log_SetTimeZone_013_Removed;
   public static String        Log_SetTimeZone_014_TourStartYYMMDDAdjusted;

   public static String        Log_TemperatureAdjustment_001_Start;
   public static String        Log_TemperatureAdjustment_002_End;
   public static String        Log_TemperatureAdjustment_003_TourChanges;
   public static String        Log_TemperatureAdjustment_005_TourIsTooShort;
   public static String        Log_TemperatureAdjustment_006_IsAboveTemperature;
   public static String        Log_TemperatureAdjustment_010_NoTemperatureDataSeries;
   public static String        Log_TemperatureAdjustment_011_NoTimeDataSeries;

   public static String        Log_Tour_CopyTour;
   public static String        Log_Tour_DeleteTours;
   public static String        Log_Tour_MoveTour;
   public static String        Log_Tour_SaveTours;
   public static String        Log_Tour_SaveTours_File;

   public static String        Map_Action_SynchWithOtherMap;

   public static String        Map_Bookmark_Action_Bookmark_Delete;
   public static String        Map_Bookmark_Action_Bookmark_Rename;
   public static String        Map_Bookmark_Button_Add;
   public static String        Map_Bookmark_Button_Rename;
   public static String        Map_Bookmark_Column_Bearing2;                                          //2, because of refactoring
   public static String        Map_Bookmark_Column_Bearing2_Tooltip;                                  //2, because of refactoring
   public static String        Map_Bookmark_Column_Latitude;
   public static String        Map_Bookmark_Column_Latitude_Tooltip;
   public static String        Map_Bookmark_Column_Longitude;
   public static String        Map_Bookmark_Column_Longitude_Tooltip;
   public static String        Map_Bookmark_Column_PositionMarkerLatitude;
   public static String        Map_Bookmark_Column_PositionMarkerLatitude_Tooltip;
   public static String        Map_Bookmark_Column_PositionMarkerLongitude;
   public static String        Map_Bookmark_Column_PositionMarkerLongitude_Tooltip;
   public static String        Map_Bookmark_Column_Name;
   public static String        Map_Bookmark_Column_Scale;
   public static String        Map_Bookmark_Column_Tilt2;                                             //2, because of refactoring
   public static String        Map_Bookmark_Column_Tilt2_Tooltip;                                     //2, because of refactoring
   public static String        Map_Bookmark_Column_ZoomLevel2;                                        //2, because of refactoring
   public static String        Map_Bookmark_Column_ZoomLevel2_Tooltip;                                //2, because of refactoring
   public static String        Map_Bookmark_Dialog_AddBookmark_Message;
   public static String        Map_Bookmark_Dialog_AddBookmark_Title;
   public static String        Map_Bookmark_Dialog_RenameBookmark_Message;
   public static String        Map_Bookmark_Dialog_RenameBookmark_Title;
   public static String        Map_Bookmark_Dialog_ValidationAddName;
   public static String        Map_Bookmark_Dialog_ValidationRename;

   public static String        Map25_Config_ClusterAlgorithm_FirstMarker_Distance;
   public static String        Map25_Config_ClusterAlgorithm_FirstMarker_Grid;
   public static String        Map25_Config_ClusterAlgorithm_Grid;
   public static String        Map25_Config_SymbolOrientation_Billboard;
   public static String        Map25_Config_SymbolOrientation_Ground;

   public static String        Map25_Provider_Mapilion_Description;
   public static String        Map25_Provider_Mapilion_Name;
   public static String        Map25_Provider_MapzenVectorTiles_Description;
   public static String        Map25_Provider_MapzenVectorTiles_Name;
   public static String        Map25_Provider_MyTileServer_Description;
   public static String        Map25_Provider_MyTileServer_Name;
   public static String        Map25_Provider_OpenAndoMap_Description;

   public static String        merge_tour_dlg_invalid_serie_data_message;
   public static String        merge_tour_dlg_invalid_tour_data_message;
   public static String        merge_tour_dlg_invalid_tour_message;
   public static String        merge_tour_dlg_invalid_tour_title;
   public static String        merge_tour_source_graph_altitude;
   public static String        merge_tour_source_graph_altitude_tooltip;
   public static String        merge_tour_source_graph_cadence;
   public static String        merge_tour_source_graph_cadence_tooltip;
   public static String        merge_tour_source_graph_heartbeat;
   public static String        merge_tour_source_graph_heartbeat_tooltip;
   public static String        merge_tour_source_graph_temperature;
   public static String        merge_tour_source_graph_temperature_tooltip;

   public static String        NT001_DialogExtractTour_InvalidTourData;

   public static String        Photo_Filter_Label_NumberOfAllPhotos_Tooltip;
   public static String        Photo_Filter_Label_NumberOfFilteredPhotos_Tooltip;
   public static String        Photo_Filter_Operator_HasAny;
   public static String        Photo_Filter_Operator_HasAny_Tooltip;
   public static String        Photo_Filter_Operator_IsEqual;
   public static String        Photo_Filter_Operator_IsEqual_Tooltip;
   public static String        Photo_Filter_Operator_IsLess;
   public static String        Photo_Filter_Operator_IsLess_Tooltip;
   public static String        Photo_Filter_Operator_IsMore;
   public static String        Photo_Filter_Operator_IsMore_Tooltip;
   public static String        Photo_Filter_Title_Map2PhotoFilter;
   public static String        Photo_Gallery_Action_ToggleGalleryHorizontal_ToolTip;
   public static String        Photo_Gallery_Action_ToggleGalleryVertical_ToolTip;
   public static String        Photo_Gallery_Label_NoTourWithPhoto;

   public static String        Photo_Properties_Label_Size;
   public static String        Photo_Properties_Label_ThumbnailSize_Tooltip;

   public static String        Photo_Tooltip_Action_MoveToolTip_ToolTip;
   public static String        Photo_Tooltip_Action_PinToolTip_ToolTip;
   public static String        Photo_Tooltip_Label_ShellNoResize;
   public static String        Photo_Tooltip_Label_ShellWithResize;

   public static String        Photo_TourPhotoMgr_Dialog_ReplacePhotoImage_Message;
   public static String        Photo_TourPhotoMgr_Dialog_ReplacePhotoImage_NoImage_Message;
   public static String        Photo_TourPhotoMgr_Dialog_ReplacePhotoImage_NoValidImageNames;
   public static String        Photo_TourPhotoMgr_Dialog_ReplacePhotoImage_NoValidImages_Message;
   public static String        Photo_TourPhotoMgr_Dialog_ReplacePhotoImage_ReplaceAll_Message;
   public static String        Photo_TourPhotoMgr_Dialog_ReplacePhotoImage_ReplacePartly_Message;
   public static String        Photo_TourPhotoMgr_Dialog_ReplacePhotoImage_SelectFolder_Message;
   public static String        Photo_TourPhotoMgr_Dialog_ReplacePhotoImage_Title;
   public static String        Photo_TourPhotoMgr_Dialog_SaveStarRating_Message;
   public static String        Photo_TourPhotoMgr_Dialog_SaveStarRating_Title;

   public static String        Photos_AndTours_Combo_Camera_Tooltip;
   public static String        Photos_AndTours_Dialog_CannotSaveHistoryTour_Message;
   public static String        Photos_AndTours_Dialog_CannotSaveHistoryTour_Title;
   public static String        Photos_AndTours_Dialog_RemovePhotos_Message;
   public static String        Photos_AndTours_Dialog_RemovePhotos_Title;
   public static String        Photos_AndTours_Label_AdjustTime;
   public static String        Photos_AndTours_Label_AdjustTime_Tooltip;
   public static String        Photos_AndTours_Label_DurationLess1Hour;
   public static String        Photos_AndTours_Label_HistoryTour;
   public static String        Photos_AndTours_Label_NoCamera;
   public static String        Photos_AndTours_Label_NoSelectedPhoto;
   public static String        Photos_AndTours_Label_Source_PhotoLink;

   public static String        Photos_AndTours_Label_Source_PhotoLink_Tooltip;
   public static String        Photos_AndTours_Label_Source_Tour;

   public static String        Photos_AndTours_Label_Source_Tour_Tooltip;
   public static String        Photos_AndTours_Link_PhotoDirectory;
   public static String        Photos_AndTours_Spinner_AdjustHours_Tooltip;
   public static String        Photos_AndTours_Spinner_AdjustMinutes_Tooltip;
   public static String        Photos_AndTours_Spinner_AdjustSeconds_Tooltip;

   public static String        Pref_App_Label_TourEditorIsModified;

   public static String        Pref_Appearance_Button_ResetAllToggleDialogs;
   public static String        Pref_Appearance_Button_ResetAllToggleDialogs_Tooltip;
   public static String        Pref_Appearance_Check_AutoOpenTagging;
   public static String        Pref_Appearance_Check_TaggingAnimation;
   public static String        Pref_Appearance_Dialog_ResetAllToggleDialogs_Message;
   public static String        Pref_Appearance_Dialog_ResetAllToggleDialogs_Title;
   public static String        Pref_Appearance_Dialog_RestartAfterThemeChange_Message;
   public static String        Pref_Appearance_Group_PaceAndSpeedDisplay;
   public static String        Pref_Appearance_Group_Tagging;
   public static String        Pref_Appearance_Group_Theme;
   public static String        Pref_Appearance_Label_AutoOpenTagging_Tooltip;
   public static String        Pref_Appearance_Label_AutoOpenTaggingDelay;
   public static String        Pref_Appearance_Label_PaceAndSpeed_ComputationOption;
   public static String        Pref_Appearance_Label_PaceAndSpeed_ComputationOption_Tooltip;
   public static String        Pref_Appearance_Label_Theme;
   public static String        Pref_Appearance_NumberOfRecent_TourTypes;
   public static String        Pref_Appearance_NumberOfRecent_TourTypes_Tooltip;
   public static String        Pref_Appearance_Radio_UseRecordedTime;
   public static String        Pref_Appearance_Radio_UseRecordedTime_Tooltip;
   public static String        Pref_Appearance_Radio_UseMovingTime;
   public static String        Pref_Appearance_Radio_UseMovingTime_Tooltip;
   public static String        Pref_Appearance_ShowTourTypeContextMenu;
   public static String        Pref_Appearance_ShowTourTypeContextMenu_Tooltip;

   public static String        Pref_ChartColors_btn_legend;
   public static String        Pref_ChartColors_unit_high;
   public static String        Pref_ChartColors_unit_low;
   public static String        Pref_ChartColors_unit_max;
   public static String        Pref_ChartColors_unit_mid;
   public static String        Pref_ChartColors_unit_min;

   public static String        Pref_DisplayFormat_Label_Altitude;
   public static String        Pref_DisplayFormat_Label_Cadence;
   public static String        Pref_DisplayFormat_Label_Distance;
   public static String        Pref_DisplayFormat_Label_Info;
   public static String        Pref_DisplayFormat_Label_Power;
   public static String        Pref_DisplayFormat_Label_Pulse;
   public static String        Pref_DisplayFormat_Label_Speed;
   public static String        Pref_DisplayFormat_Tab_MultipleTours;
   public static String        Pref_DisplayFormat_Tab_MultipleTours_Tooltip;
   public static String        Pref_DisplayFormat_Tab_OneTour;
   public static String        Pref_DisplayFormat_Tab_OneTour_Tooltip;

   public static String        Pref_general_show_system_in_ui;
   public static String        Pref_general_system_measurement;

   public static String        Pref_General_Button_ComputeCalendarWeek;
   public static String        Pref_General_CalendarWeek;
   public static String        Pref_General_Checkbox_SetTimeZone;
   public static String        Pref_General_Dialog_CalendarWeekIsModified_Message;
   public static String        Pref_General_Dialog_CalendarWeekIsModified_Title;
   public static String        Pref_General_Group_TimeZone;
   public static String        Pref_General_Label_DefaultLocalTimeZone;
   public static String        Pref_General_Label_FirstDayOfWeek;
   public static String        Pref_General_Label_FirstDayOfWeek_Tooltip;
   public static String        Pref_General_Label_LocalTimeZone_1;
   public static String        Pref_General_Label_LocalTimeZone_2;
   public static String        Pref_General_Label_LocalTimeZone_3;
   public static String        Pref_General_Label_MinimalDaysInFirstWeek;
   public static String        Pref_General_Label_MinimalDaysInFirstWeek_Tooltip;
   public static String        Pref_General_Label_SetAnotherTimeZone;

   public static String        Pref_General_Notes;
   public static String        Pref_General_Notes_Tooltip;

   public static String        Pref_Graphs_Button_down;
   public static String        Pref_Graphs_Button_up;
   public static String        Pref_Graphs_Check_autozoom;
   public static String        Pref_Graphs_Check_show_start_time;
   public static String        Pref_Graphs_Checkbox_EnableMinMaxValues;
   public static String        Pref_Graphs_Checkbox_ForceAltimeterValue;
   public static String        Pref_Graphs_Checkbox_ForceGradientValue;
   public static String        Pref_Graphs_Checkbox_ForcePaceValue;
   public static String        Pref_Graphs_Checkbox_ForcePulseValue;
   public static String        Pref_Graphs_Checkbox_ForceValue_Altitude;
   public static String        Pref_Graphs_Checkbox_ForceValue_Cadence;
   public static String        Pref_Graphs_Checkbox_ForceValue_Power;
   public static String        Pref_Graphs_Checkbox_ForceValue_RunDyn_StanceTime;
   public static String        Pref_Graphs_Checkbox_ForceValue_RunDyn_StanceTimeBalance;
   public static String        Pref_Graphs_Checkbox_ForceValue_RunDyn_StepLength;
   public static String        Pref_Graphs_Checkbox_ForceValue_RunDyn_VerticalOscillation;
   public static String        Pref_Graphs_Checkbox_ForceValue_RunDyn_VerticalRatio;
   public static String        Pref_Graphs_Checkbox_ForceValue_Speed;
   public static String        Pref_Graphs_Checkbox_ForceValue_Swim_Strokes;
   public static String        Pref_Graphs_Checkbox_ForceValue_Swim_Swolf;
   public static String        Pref_Graphs_Checkbox_ForceValue_Temperature;
   public static String        Pref_Graphs_Checkbox_GraphAntialiasing;
   public static String        Pref_Graphs_Checkbox_GraphAntialiasing_Tooltip;
   public static String        Pref_Graphs_Checkbox_SegmentAlternateColor;
   public static String        Pref_Graphs_Checkbox_SegmentAlternateColor_Tooltip;
   public static String        Pref_Graphs_Checkbox_ShowHorizontalGrid;
   public static String        Pref_Graphs_Checkbox_ShowVerticalGrid;
   public static String        Pref_Graphs_Dialog_GridLine_Warning_Message;
   public static String        Pref_Graphs_Error_one_graph_must_be_selected;
   public static String        Pref_Graphs_grid_horizontal_distance;
   public static String        Pref_Graphs_grid_vertical_distance;
   public static String        Pref_Graphs_Group_Graphs;
   public static String        Pref_Graphs_Group_Grid;
   public static String        Pref_Graphs_Group_mouse_mode;
   public static String        Pref_Graphs_Group_units_for_xaxis;
   public static String        Pref_Graphs_Group_zoom_options;
   public static String        Pref_Graphs_Label_GraphTransparency;
   public static String        Pref_Graphs_Label_GraphTransparency_Tooltip;
   public static String        Pref_Graphs_Label_GraphTransparencyLine;
   public static String        Pref_Graphs_Label_GraphTransparencyLine_Tooltip;
   public static String        Pref_Graphs_Label_GridDistance;
   public static String        Pref_Graphs_Label_GridDistance_Tooltip;
   public static String        Pref_Graphs_Label_MaxValue;
   public static String        Pref_Graphs_Label_MinValue;
   public static String        Pref_Graphs_Label_select_graph;
   public static String        Pref_Graphs_Label_select_graph_tooltip;
   public static String        Pref_Graphs_move_sliders_when_zoomed;
   public static String        Pref_Graphs_Radio_mouse_mode_slider;
   public static String        Pref_Graphs_Radio_mouse_mode_zoom;
   public static String        Pref_Graphs_Radio_show_distance;
   public static String        Pref_Graphs_Radio_show_time;
   public static String        Pref_Graphs_Tab_graph_defaults;
   public static String        Pref_Graphs_Tab_Grid;
   public static String        Pref_Graphs_Tab_zoom_options;

   public static String        Pref_LiveUpdate_Checkbox;
   public static String        Pref_LiveUpdate_Checkbox_Tooltip;

   public static String        Pref_Map25_Action_EditMapProviderPreferences_Tooltip;
   public static String        Pref_Map25_Dialog_MapFilename_Title;
   public static String        Pref_Map25_Dialog_MapStyleFilename_Title;
   public static String        Pref_Map25_Encoding_Mapilion;
   public static String        Pref_Map25_Encoding_Mapsforge_Offline;
   public static String        Pref_Map25_Encoding_Mapzen;
   public static String        Pref_Map25_Encoding_OpenScienceMap;
   public static String        Pref_Map25_Offline_Dialog_Restart_Message;
   public static String        Pref_Map25_Offline_Dialog_Restart_Title;
   public static String        Pref_Map25_Offline_Error_Location;
   public static String        Pref_Map25_Offline_Group_OfflineMap;
   public static String        Pref_Map25_Offline_Label_Location;
   public static String        Pref_Map25_Offline_Checkbox_UseDefaultLocation;
   public static String        Pref_Map25_Provider_Checkbox_IsEnabled;
   public static String        Pref_Map25_Provider_Checkbox_IsEnabled_Tooltip;
   public static String        Pref_Map25_Provider_Column_APIKey_ThemeStyle;
   public static String        Pref_Map25_Provider_Column_Enabled;
   public static String        Pref_Map25_Provider_Column_Offline;
   public static String        Pref_Map25_Provider_Column_ProviderName;
   public static String        Pref_Map25_Provider_Column_Theme;
   public static String        Pref_Map25_Provider_Column_TileEncoding;
   public static String        Pref_Map25_Provider_Column_TilePath_ThemeFilename;
   public static String        Pref_Map25_Provider_Column_Url_MapFilename;
   public static String        Pref_Map25_Provider_Dialog_ConfirmDeleteMapProvider_Message;
   public static String        Pref_Map25_Provider_Dialog_ConfirmDeleteMapProvider_Title;
   public static String        Pref_Map25_Provider_Dialog_SaveModifiedProvider_Message;
   public static String        Pref_Map25_Provider_Dialog_SaveModifiedProvider_Title;
   public static String        Pref_Map25_Provider_Error_EnableMapProvider;
   public static String        Pref_Map25_Provider_Error_MapFilename_IsNotValid;
   public static String        Pref_Map25_Provider_Error_MapFilename_IsRequired;
   public static String        Pref_Map25_Provider_Error_ProviderNameIsRequired;
   public static String        Pref_Map25_Provider_Error_ThemeFilename_IsNotValid;
   public static String        Pref_Map25_Provider_Error_ThemeFilename_IsRequired;
   public static String        Pref_Map25_Provider_Error_TilePathIsRequired;
   public static String        Pref_Map25_Provider_Error_UrlIsRequired;
   public static String        Pref_Map25_Provider_Label_APIKey;
   public static String        Pref_Map25_Provider_Label_DefaultTheme;
   public static String        Pref_Map25_Provider_Label_Description;
   public static String        Pref_Map25_Provider_Label_MapFilepath;
   public static String        Pref_Map25_Provider_Label_ProviderName;
   public static String        Pref_Map25_Provider_Label_ThemeFilepath;
   public static String        Pref_Map25_Provider_Label_ThemeStyle;
   public static String        Pref_Map25_Provider_Label_TileEncoding;
   public static String        Pref_Map25_Provider_Label_TilePath;
   public static String        Pref_Map25_Provider_Label_TileUrl;
   public static String        Pref_Map25_Provider_Label_Title;
   public static String        Pref_Map25_Provider_Label_Url;
   public static String        Pref_Map25_Provider_Theme_FromThemeFile;
   public static String        Pref_Map25_Provider_ThemeStyle_Info_All;
   public static String        Pref_Map25_Provider_ThemeStyle_Info_InvalidThemeFilename;
   public static String        Pref_Map25_Provider_ThemeStyle_Info_NoStyles;
   public static String        Pref_Map25_Provider_ThemeStyle_Info_NotAvailable;
   public static String        Pref_Map25_Provider_ThemeStyle_Info_NotSupported;

   public static String        Pref_MapLayout_Checkbox_BorderColor_Color;
   public static String        Pref_MapLayout_Checkbox_BorderColor_Darker;
   public static String        Pref_MapLayout_Dialog_OSX_Warning_Message;
   public static String        Pref_MapLayout_Dialog_OSX_Warning_Title;
   public static String        Pref_MapLayout_Group_TourInMapProperties;
   public static String        Pref_MapLayout_Label_BorderColor;
   public static String        Pref_MapLayout_Label_TourPaintMethod;
   public static String        Pref_MapLayout_Label_TourPaintMethod_Complex;
   public static String        Pref_MapLayout_Label_TourPaintMethod_Complex_Tooltip;
   public static String        Pref_MapLayout_Label_TourPaintMethod_Simple;
   public static String        Pref_MapLayout_Label_TourPaintMethod_Simple_Tooltip;

   public static String        Pref_People_Action_add_person;
   public static String        Compute_CadenceZonesTimes_ComputeAllTours;
   public static String        Pref_People_Button_HrZones_ComputeAllTours;
   public static String        Pref_People_Button_HrZones_ComputeAllTours_Tooltip;
   public static String        Pref_People_Column_Birthday;
   public static String        Pref_People_Column_device;
   public static String        Pref_People_Column_first_name;
   public static String        Pref_People_Column_height;
   public static String        Pref_People_Column_last_name;
   public static String        Pref_People_Dialog_ComputeHrZonesForAllTours_Message;
   public static String        Pref_People_Dialog_ComputeHrZonesForAllToursIsCanceled_Message;
   public static String        Pref_People_Dialog_SaveModifiedPerson_Message;
   public static String        Pref_People_Dialog_SaveModifiedPerson_Title;
   public static String        Pref_People_Error_ComputeHrZonesForAllTours;
   public static String        Pref_People_Error_first_name_is_required;
   public static String        Pref_People_Error_path_is_invalid;
   public static String        Pref_People_Label_Age;
   public static String        Pref_People_Label_Birthday;
   public static String        Compute_CadenceZonesTimes_Label_CadenceZonesDelimiter;
   public static String        Compute_CadenceZonesTimes_Label_Description_CadenceZonesDelimiter;
   public static String        Pref_People_Label_DataTransfer;
   public static String        Pref_People_Label_DefaultDataTransferFilePath;
   public static String        Pref_People_Label_DefaultDataTransferFilePath_Tooltip;
   public static String        Pref_People_Label_device;
   public static String        Pref_People_Label_first_name;
   public static String        Pref_People_Label_Gender;
   public static String        Pref_People_Label_GenderFemale;
   public static String        Pref_People_Label_GenderMale;
   public static String        Pref_People_Label_height;
   public static String        Pref_People_Label_HrZoneInfo;
   public static String        Pref_People_Label_HrZoneTemplate_Tooltip;
   public static String        Pref_People_Label_last_name;
   public static String        Pref_People_Label_MaxHR;
   public static String        Pref_People_Label_RestingHR;
   public static String        Pref_People_Label_weight;
   public static String        Pref_People_Label_Years;
   public static String        Pref_People_Link_BodyWeight;
   public static String        Pref_People_Tab_DataTransfer;
   public static String        Pref_People_Tab_HRZone;
   public static String        Pref_People_Tab_Person;
   public static String        Pref_People_Title;

   public static String        pref_appearance_number_of_recent_tags;
   public static String        pref_appearance_number_of_recent_tags_tooltip;
   public static String        pref_appearance_showMemoryMonitor;
   public static String        pref_appearance_showMemoryMonitor_message;
   public static String        pref_appearance_showMemoryMonitor_title;

   public static String        pref_general_restart_app_message;
   public static String        pref_general_restart_app_title;

   public static String        pref_map_layout_BorderWidth;
   public static String        pref_map_layout_PaintBorder;
   public static String        pref_map_layout_symbol;
   public static String        pref_map_layout_symbol_dot;
   public static String        pref_map_layout_symbol_line;
   public static String        pref_map_layout_symbol_square;
   public static String        pref_map_layout_symbol_width;

   public static String        pref_statistic_lbl_info;

   public static String        pref_tour_editor_description_height;
   public static String        pref_tour_editor_description_height_tooltip;

   public static String        pref_tourtag_btn_new_tag;
   public static String        pref_tourtag_btn_new_tag_category;
   public static String        pref_tourtag_btn_reset;
   public static String        pref_tourtag_dlg_new_tag_category_message;
   public static String        pref_tourtag_dlg_new_tag_category_title;
   public static String        pref_tourtag_dlg_new_tag_message;
   public static String        pref_tourtag_dlg_new_tag_title;
   public static String        pref_tourtag_dlg_reset_message;
   public static String        pref_tourtag_dlg_reset_title;
   public static String        pref_tourtag_hint;
   public static String        pref_tourtag_viewer_title;

   public static String        pref_view_layout_display_lines;
   public static String        pref_view_layout_display_lines_Tooltip;
   public static String        pref_view_layout_label_category;
   public static String        pref_view_layout_label_color_group;
   public static String        pref_view_layout_label_elapsed_time_format;
   public static String        pref_view_layout_label_recorded_time_format;
   public static String        pref_view_layout_label_paused_time_format;
   public static String        pref_view_layout_label_moving_time_format;
   public static String        pref_view_layout_label_break_time_format;
   public static String        pref_view_layout_label_sub;
   public static String        pref_view_layout_label_sub_sub;
   public static String        pref_view_layout_label_title;

   public static String        Pref_Weather_Label_ApiKey;
   public static String        Pref_Weather_Label_ApiKey_Tooltip;
   public static String        Pref_Weather_Link_ApiSignup;
   public static String        Pref_Weather_Button_TestHTTPConnection;
   public static String        Pref_Weather_Checkbox_UseRetrieval;
   public static String        Pref_Weather_Checkbox_UseRetrieval_Tooltip;
   public static String        Pref_Weather_CheckHTTPConnection_Message;
   public static String        Pref_Weather_CheckHTTPConnection_OK_Message;
   public static String        Pref_Weather_CheckHTTPConnection_FAILED_Message;

// public static String    Pref_SignImages_Dialog_ConfirmDelete_Message;
// public static String    Pref_SignImages_Dialog_Delete_Title;
// public static String    Pref_SignImages_Dialog_NoSelectedSignImage_Message;

   public static String Pref_Statistic_Action_SortByData;
   public static String Pref_Statistic_Action_SortByTime;

   public static String Pref_Statistic_Checkbox_Altitude;
   public static String Pref_Statistic_Checkbox_AvgPace;
   public static String Pref_Statistic_Checkbox_AvgSpeed;
   public static String Pref_Statistic_Checkbox_Distance;
   public static String Pref_Statistic_Checkbox_Duration;
   public static String Pref_Statistic_Checkbox_NumberOfTours;
   public static String Pref_Statistic_Checkbox_ShowPercentageValues;
   public static String Pref_Statistic_Checkbox_ShowSummaryValues;
   public static String Pref_Statistic_Checkbox_TrainingEffect_Aerob;
   public static String Pref_Statistic_Checkbox_TrainingEffect_Anaerob;
   public static String Pref_Statistic_Checkbox_TrainingPerformance;
   public static String Pref_Statistic_Checkbox_TrainingPerformance_AvgValue;
   public static String Pref_Statistic_Checkbox_TrainingPerformance_AvgValue_Tooltip;
   public static String Pref_Statistic_Checkbox_YearSeparator;

   public static String Pref_Statistic_Group_ChartType;
   public static String Pref_Statistic_Group_DaySummary;
   public static String Pref_Statistic_Group_DurationTime;
   public static String Pref_Statistic_Group_MonthSummary;
   public static String Pref_Statistic_Group_StatisticTooltip;
   public static String Pref_Statistic_Group_TourFrequency;
   public static String Pref_Statistic_Group_Training;
   public static String Pref_Statistic_Group_WeekSummary;
   public static String Pref_Statistic_Group_YearSummary;

   public static String Pref_Statistic_Label_altitude;
   public static String Pref_Statistic_Label_distance;
   public static String Pref_Statistic_Label_duration;
   public static String Pref_Statistic_Label_Interval;
   public static String Pref_Statistic_Label_Minimum;
   public static String Pref_Statistic_Label_NumberOfBars;

   public static String Pref_Statistic_Radio_BarAdjacent;
   public static String Pref_Statistic_Radio_BarStacked;
   public static String Pref_Statistic_Radio_Duration_MovingTime;
   public static String Pref_Statistic_Radio_Duration_PausedTime;
   public static String Pref_Statistic_Radio_Duration_RecordedTime;
   public static String Pref_Statistic_Radio_Duration_ElapsedTime;
   public static String Pref_Statistic_Radio_Duration_BreakTime;

   public static String Pref_Swimming_Label_Info;

   public static String Pref_Tour_Button_FailedUpdate;
   public static String Pref_Tour_Dialog_ConfirmDatabaseUpdate_Message;
   public static String Pref_Tour_Dialog_ConfirmDatabaseUpdate_Title;
   public static String Pref_Tour_Dialog_TourCacheIsModified_Message;
   public static String Pref_Tour_Dialog_TourCacheIsModified_Title;
   public static String Pref_Tour_Group_FailedUpdates;
   public static String Pref_Tour_Group_TourCache;
   public static String Pref_Tour_Label_FailedUpdateInfo;
   public static String Pref_Tour_Label_FailedUpdateInfo_BOLD;
   public static String Pref_Tour_Label_TourCacheSize;
   public static String Pref_Tour_Label_TourCacheSize_Info;

   public static String Pref_TourDb_Dialog_TourDbSystemIsModified_Message;
   public static String Pref_TourDb_Dialog_TourDbSystemIsModified_Title;
   public static String Pref_TourDb_Group_TourDB;
   public static String Pref_TourDb_Radio_DbSystem_Embedded;
   public static String Pref_TourDb_Radio_DbSystem_Embedded_Tooltip;
   public static String Pref_TourDb_Radio_DbSystem_Server;
   public static String Pref_TourDb_Radio_DbSystem_Server_Tooltip;

   public static String Pref_TourTag_Column_Notes;
   public static String Pref_TourTag_Column_TagsAndCategories;
   public static String Pref_TourTag_Link_AppearanceOptions;

   public static String Pref_TourTypeFilter_button_new;
   public static String Pref_TourTypeFilter_button_remove;
   public static String Pref_TourTypeFilter_button_rename;
   public static String Pref_TourTypeFilter_dlg_new_message;
   public static String Pref_TourTypeFilter_dlg_new_title;
   public static String Pref_TourTypeFilter_dlg_rename_message;
   public static String Pref_TourTypeFilter_dlg_rename_title;

   public static String Pref_TourTypes_Button_add;
   public static String Pref_TourTypes_Button_delete;
   public static String Pref_TourTypes_Button_rename;
   public static String Pref_TourTypes_Dialog_Restart_Message_2;
   public static String Pref_TourTypes_Dialog_Restart_Title;
   public static String Pref_TourTypes_Dlg_delete_tour_type_msg;
   public static String Pref_TourTypes_Dlg_delete_tour_type_title;
   public static String Pref_TourTypes_Dlg_new_tour_type_msg;
   public static String Pref_TourTypes_Dlg_new_tour_type_title;
   public static String Pref_TourTypes_Dlg_rename_tour_type_msg;
   public static String Pref_TourTypes_Dlg_rename_tour_type_title;
   public static String Pref_TourTypes_dnd_hint;
   public static String Pref_TourTypes_Label_BorderLayout;
   public static String Pref_TourTypes_Label_BorderWidth;
   public static String Pref_TourTypes_Label_ImageLayout;
   public static String Pref_TourTypes_Label_TourIsDirty;
   public static String Pref_TourTypes_root_title;
   public static String Pref_TourTypes_Title;

   public static String PrefPage_Import_Checkbox_AutoOpenTourLogView;
   public static String PrefPage_Import_Checkbox_CreateTourIdWithTime;
   public static String PrefPage_Import_Checkbox_CreateTourIdWithTime_Tooltip;
   public static String PrefPage_Import_Label_Info;
   public static String PrefPage_Import_Checkbox_IgnoreInvalidFiles;
   public static String PrefPage_Import_Checkbox_IgnoreInvalidFiles_Tooltip;
   public static String PrefPage_Import_Checkbox_SetBodyWeight;
   public static String PrefPage_Import_Default_Cadence;
   public static String PrefPage_Import_Default_CadenceValue_Tooltip;

   public static String PrefPage_ViewActions_Group;
   public static String PrefPage_ViewActions_Label_DoubleClick;
   public static String PrefPage_ViewActions_Label_DoubleClick_AdjustAltitude;
   public static String PrefPage_ViewActions_Label_DoubleClick_EditMarker;
   public static String PrefPage_ViewActions_Label_DoubleClick_EditTour;
   public static String PrefPage_ViewActions_Label_DoubleClick_None;
   public static String PrefPage_ViewActions_Label_DoubleClick_NoneNoWarning;
   public static String PrefPage_ViewActions_Label_DoubleClick_OpenTour;
   public static String PrefPage_ViewActions_Label_DoubleClick_QuickEdit;
   public static String PrefPage_ViewActions_Label_Info;

   public static String PrefPage_ViewTooltip_Button_DisableAll;
   public static String PrefPage_ViewTooltip_Button_EnableAll;
   public static String PrefPage_ViewTooltip_Chkbox_Collation;
   public static String PrefPage_ViewTooltip_Group;
   public static String PrefPage_ViewTooltip_Label_CollatedTours;
   public static String PrefPage_ViewTooltip_Label_Date;
   public static String PrefPage_ViewTooltip_Label_Day;
   public static String PrefPage_ViewTooltip_Label_Info;
   public static String PrefPage_ViewTooltip_Label_RawData;
   public static String PrefPage_ViewTooltip_Label_ReferenceTour;
   public static String PrefPage_ViewTooltip_Label_ReferenceTours;
   public static String PrefPage_ViewTooltip_Label_TagFirstColumn;
   public static String PrefPage_ViewTooltip_Label_TaggedTour;
   public static String PrefPage_ViewTooltip_Label_Tags;
   public static String PrefPage_ViewTooltip_Label_Time;
   public static String PrefPage_ViewTooltip_Label_Title;
   public static String PrefPage_ViewTooltip_Label_Tour;
   public static String PrefPage_ViewTooltip_Label_TourBook;
   public static String PrefPage_ViewTooltip_Label_CompareResult;
   public static String PrefPage_ViewTooltip_Label_WeekDay;

   public static String PrefPageTourTypeFilterList_Pref_TourTypeFilter_button_down;
   public static String PrefPageTourTypeFilterList_Pref_TourTypeFilter_button_up;

// public static String    SignImage_View_Action_CreateSignCategory;
// public static String    SignImage_View_Action_ImportSignImage;
// public static String    SignImage_Viewer_Column_Dimension_Label;
// public static String    SignImage_Viewer_Column_Dimension_Tooltip;
// public static String    SignImage_Viewer_Column_FilePathName_Label;
// public static String    SignImage_Viewer_Column_FilePathName_Tooltip;
// public static String    SignImage_Viewer_Column_Image_Header;
// public static String    SignImage_Viewer_Column_Image_Label;
// public static String    SignImage_Viewer_Column_Image_Tooltip;
// public static String    SignImage_Viewer_Column_Name_Label;

   public static String Search_Manager_CreateFTIndex;
   public static String Search_Manager_Log_DeletingLuceneRootFolder;
   public static String Search_Manager_Log_LuceneRootFolderIsDeleted;

   public static String Search_View_Action_ExternalSearchUI;
   public static String Search_View_Action_ExternalSearchUI_Tooltip;

   public static String Search_View_Link_ExternalBrowser;
   public static String Search_View_Link_LinuxBrowser;
   public static String Search_View_Link_SetupExternalBrowser;

   public static String Slideout_CalendarOptions_Action_AddProfile_Tooltip;
   public static String Slideout_CalendarOptions_Action_CopyProfile_Tooltip;
   public static String Slideout_CalendarOptions_Action_DeleteProfile_Tooltip;
   public static String Slideout_CalendarOptions_Checkbox_IsHideDayWhenEmpty;
   public static String Slideout_CalendarOptions_Checkbox_IsShowDateColumn;
   public static String Slideout_CalendarOptions_Checkbox_IsShowDateColumn_Tooltip;
   public static String Slideout_CalendarOptions_Checkbox_IsShowDayDate;
   public static String Slideout_CalendarOptions_Checkbox_IsShowDayWeekendColor;
   public static String Slideout_CalendarOptions_Checkbox_IsShowTour_Content;
   public static String Slideout_CalendarOptions_Checkbox_IsShowTour_ValueUnit;
   public static String Slideout_CalendarOptions_Checkbox_IsShowWeek_SummaryColumn;
   public static String Slideout_CalendarOptions_Checkbox_IsShowWeek_ValueUnit;
   public static String Slideout_CalendarOptions_Checkbox_IsShowYearColumn;
   public static String Slideout_CalendarOptions_Checkbox_IsShowYearColumn_Tooltip;
   public static String Slideout_CalendarOptions_Checkbox_IsToggleMonthColor;
   public static String Slideout_CalendarOptions_Checkbox_IsToggleMonthColor_Tooltip;
   public static String Slideout_CalendarOptions_Checkbox_IsTruncateTourText;
   public static String Slideout_CalendarOptions_Checkbox_IsUserDefaultProfile;
   public static String Slideout_CalendarOptions_Checkbox_IsUserDefaultProfile_Tooltip;
   public static String Slideout_CalendarOptions_Checkbox_UseDraggedScrolling;
   public static String Slideout_CalendarOptions_Checkbox_UseDraggedScrolling_Tooltip;
   public static String Slideout_CalendarOptions_ColumnHeader_DefaultId;
   public static String Slideout_CalendarOptions_ColumnHeader_IsAppId;
   public static String Slideout_CalendarOptions_ColumnHeader_IsAppId_Tooltip;
   public static String Slideout_CalendarOptions_ColumnHeader_IsUserId;
   public static String Slideout_CalendarOptions_ColumnHeader_IsUserId_Tooltip;
   public static String Slideout_CalendarOptions_ColumnHeader_Name;
   public static String Slideout_CalendarOptions_Dialog_DeleteProfile_Message;
   public static String Slideout_CalendarOptions_Dialog_DeleteProfile_Title;
   public static String Slideout_CalendarOptions_Group_DateColumn;
   public static String Slideout_CalendarOptions_Group_Day;
   public static String Slideout_CalendarOptions_Group_DayDate;
   public static String Slideout_CalendarOptions_Group_Layout;
   public static String Slideout_CalendarOptions_Group_TourColor;
   public static String Slideout_CalendarOptions_Group_YearColumns;
   public static String Slideout_CalendarOptions_Label_AppPrefix;
   public static String Slideout_CalendarOptions_Label_Calendar_BackgroundColor;
   public static String Slideout_CalendarOptions_Label_Calendar_ForegroundColor;
   public static String Slideout_CalendarOptions_Label_DateColumn_Content;
   public static String Slideout_CalendarOptions_Label_DateColumn_Content_Tooltip;
   public static String Slideout_CalendarOptions_Label_DateColumn_Font;
   public static String Slideout_CalendarOptions_Label_DateColumn_Width;
   public static String Slideout_CalendarOptions_Label_Day_HoveredColor;
   public static String Slideout_CalendarOptions_Label_Day_SelectedColor;
   public static String Slideout_CalendarOptions_Label_Day_TodayColor;
   public static String Slideout_CalendarOptions_Label_DayContent_Direction;
   public static String Slideout_CalendarOptions_Label_DayContent_Direction_Tooltip;
   public static String Slideout_CalendarOptions_Label_DayDate_Format;
   public static String Slideout_CalendarOptions_Label_Line;
   public static String Slideout_CalendarOptions_Label_Margin;
   public static String Slideout_CalendarOptions_Label_Margin_DayDate_Tooltip;
   public static String Slideout_CalendarOptions_Label_Margin_Tooltip;
   public static String Slideout_CalendarOptions_Label_Profile_DefaultId;
   public static String Slideout_CalendarOptions_Label_Profile_DefaultId_Tooltip;
   public static String Slideout_CalendarOptions_Label_Profile_DragDropHint;
   public static String Slideout_CalendarOptions_Label_Profile_Name;
   public static String Slideout_CalendarOptions_Label_Profile_UserParentDefaultID;
   public static String Slideout_CalendarOptions_Label_Profiles;
   public static String Slideout_CalendarOptions_Label_Title;
   public static String Slideout_CalendarOptions_Label_Tour_BackgroundColor;
   public static String Slideout_CalendarOptions_Label_Tour_BorderColor;
   public static String Slideout_CalendarOptions_Label_Tour_ContentFont;
   public static String Slideout_CalendarOptions_Label_DayDate_Font;
   public static String Slideout_CalendarOptions_Label_Tour_DraggedColor;
   public static String Slideout_CalendarOptions_Label_Tour_HoveredColor;
   public static String Slideout_CalendarOptions_Label_Tour_SelectedColor;
   public static String Slideout_CalendarOptions_Label_Tour_TitleFont;
   public static String Slideout_CalendarOptions_Label_Tour_TruncatedLines;
   public static String Slideout_CalendarOptions_Label_Tour_TruncatedLines_Tooltip;
   public static String Slideout_CalendarOptions_Label_Tour_ValueColumns;
   public static String Slideout_CalendarOptions_Label_Tour_ValueColumns_Tooltip;
   public static String Slideout_CalendarOptions_Label_Tour_ValueFont;
   public static String Slideout_CalendarOptions_Label_UserPrefix;
   public static String Slideout_CalendarOptions_Label_Week_ColumnWidth;
   public static String Slideout_CalendarOptions_Label_Week_ValueFont;
   public static String Slideout_CalendarOptions_Label_YearColumn_HeaderFont;
   public static String Slideout_CalendarOptions_Label_YearColumn_Space;
   public static String Slideout_CalendarOptions_Label_YearColumn_Space_Tooltip;
   public static String Slideout_CalendarOptions_Label_YearColumn_Start;
   public static String Slideout_CalendarOptions_Radio_DayContent_Direction_Horizontal;
   public static String Slideout_CalendarOptions_Radio_DayContent_Direction_Vertical;
   public static String Slideout_CalendarOptions_Radio_Weeks_ByHeight;
   public static String Slideout_CalendarOptions_Radio_Weeks_ByNumber;
   public static String Slideout_CalendarOptions_Radio_YearColumn_ByDayWidth;
   public static String Slideout_CalendarOptions_Radio_YearColumn_ByNumber;
   public static String Slideout_CalendarOptions_Tab_CalendarLayout;
   public static String Slideout_CalendarOptions_Tab_Profiles;
   public static String Slideout_CalendarOptions_Tab_TourContent;
   public static String Slideout_CalendarOptions_Tab_TourLayout;
   public static String Slideout_CalendarOptions_Tab_WeekSummary;

   public static String Slideout_ChartMarkerOptions_Checkbox_IsShowAbsoluteValues;
   public static String Slideout_ChartMarkerOptions_Checkbox_IsShowAbsoluteValues_Tooltip;
   public static String Slideout_ChartMarkerOptions_Checkbox_IsShowHiddenMarker;
   public static String Slideout_ChartMarkerOptions_Checkbox_IsShowMarker;
   public static String Slideout_ChartMarkerOptions_Checkbox_IsShowMarkerPoint;
   public static String Slideout_ChartMarkerOptions_Checkbox_IsShowMarkerTooltip;
   public static String Slideout_ChartMarkerOptions_Checkbox_IsShowMarkerWithDefaultColor;
   public static String Slideout_ChartMarkerOptions_Checkbox_IsShowMarkerWithDefaultColor_Tooltip;
   public static String Slideout_ChartMarkerOptions_Checkbox_IsShowOnlyWithDescription;
   public static String Slideout_ChartMarkerOptions_Checkbox_IsShowTempPosition;
   public static String Slideout_ChartMarkerOptions_Checkbox_IsShowTempPosition_Tooltip;
   public static String Slideout_ChartMarkerOptions_Combo_TooltipPosition_Tooltip;
   public static String Slideout_ChartMarkerOptions_Group_TooltipData;
   public static String Slideout_ChartMarkerOptions_Label_DeviceMarkerColor;
   public static String Slideout_ChartMarkerOptions_Label_DeviceMarkerColor_Tooltip;
   public static String Slideout_ChartMarkerOptions_Label_HiddenMarkerColor;
   public static String Slideout_ChartMarkerOptions_Label_HiddenMarkerColor_Tooltip;
   public static String Slideout_ChartMarkerOptions_Label_HoverSize;
   public static String Slideout_ChartMarkerOptions_Label_HoverSize_Tooltip;
   public static String Slideout_ChartMarkerOptions_Label_MarkerColor;
   public static String Slideout_ChartMarkerOptions_Label_MarkerSize;
   public static String Slideout_ChartMarkerOptions_Label_MarkerSize_Tooltip;
   public static String Slideout_ChartMarkerOptions_Label_Offset;
   public static String Slideout_ChartMarkerOptions_Label_Offset_Tooltip;
   public static String Slideout_ChartMarkerOptions_Label_Title;

   public static String Slideout_ConconiOptions_Label_Title;

   public static String Slideout_GeoCompareOptions_Group_MapOptions;
   public static String Slideout_GeoCompareOptions_Label_ComparedTourPart;
   public static String Slideout_GeoCompareOptions_Label_DistanceInterval;
   public static String Slideout_GeoCompareOptions_Label_GeoAccuracy;
   public static String Slideout_GeoCompareOptions_Label_GeoRelativeDifferences_Filter;
   public static String Slideout_GeoCompareOptions_Label_GeoRelativeDifferences_Filter_Tooltip;
   public static String Slideout_GeoCompareOptions_Label_LineWidth;
   public static String Slideout_GeoCompareOptions_Label_NormalizedDistance;
   public static String Slideout_GeoCompareOptions_Label_ReferenceTour;
   public static String Slideout_GeoCompareOptions_Label_Title;

   public static String Slideout_GraphMinMax_Label_Title;

   public static String Slideout_HVROptions_Checkbox_2xValues;
   public static String Slideout_HVROptions_Checkbox_2xValues_Tooltip;
   public static String Slideout_HVROptions_Group;
   public static String Slideout_HVROptions_Label_2xTolerance;
   public static String Slideout_HVROptions_Label_2xToleranceResult;
   public static String Slideout_HVROptions_Label_2xToleranceResult_Tooltip;
   public static String Slideout_HVROptions_Label_Title;

   public static String Slideout_LinkWithOtherViews_Label_Title;
   public static String Slideout_LinkWithOtherViews_Label_Title_Checkbox_IsShowTourTitle;
   public static String Slideout_LinkWithOtherViews_Label_Title_Checkbox_IsShowTourTitle_Tooltip;

   public static String Slideout_Map25TrackOptions_Label_ConfigName_Tooltip;
   public static String Slideout_Map25TrackOptions_Label_Name;
   public static String Slideout_Map25TrackOptions_Label_OutlineColor;
   public static String Slideout_Map25TrackOptions_Label_OutlineColor_Tooltip;
   public static String Slideout_Map25TrackOptions_Label_OutlineWidth;
   public static String Slideout_Map25TrackOptions_Label_OutlineWidth_Tooltip;
   public static String Slideout_Map25TrackOptions_Label_DirectionArrows;
   public static String Slideout_Map25TrackOptions_Label_DirectionArrows_Tooltip;

   public static String Slideout_Map25MapOptions_Checkbox_Layer_3DBuilding;
   public static String Slideout_Map25MapOptions_Checkbox_Layer_Cartography;
   public static String Slideout_Map25MapOptions_Checkbox_Layer_Cartography_Tooltip;
   public static String Slideout_Map25MapOptions_Checkbox_Layer_Hillshading;
   public static String Slideout_Map25MapOptions_Checkbox_Layer_Photo_Size;
   public static String Slideout_Map25MapOptions_Checkbox_Layer_LabelSymbol;
   public static String Slideout_Map25MapOptions_Checkbox_Layer_Satellite;
   public static String Slideout_Map25MapOptions_Checkbox_Layer_ScaleBar;
   public static String Slideout_Map25MapOptions_Checkbox_Layer_TileInfo;
   public static String Slideout_Map25MapOptions_Checkbox_Photo_Title;
   public static String Slideout_Map25MapOptions_Checkbox_UseDraggedKeyNavigation;
   public static String Slideout_Map25MapOptions_Checkbox_UseDraggedKeyNavigation_Tooltip;
   public static String Slideout_Map25MapOptions_Group_MapLayer;
   public static String Slideout_Map25MapOptions_Label_MapOptions;
   public static String Slideout_Map25MapOptions_Spinner_Layer_Hillshading;
   public static String Slideout_Map25MapOptions_Spinner_Layer_Photo_Size;

   public static String Slideout_Map25MarkerOptions_Checkbox_IsMarkerClustering;
   public static String Slideout_Map25MarkerOptions_Checkbox_IsShowBookmarks;
   public static String Slideout_Map25MarkerOptions_Checkbox_IsShowTourMarker;
   public static String Slideout_Map25MarkerOptions_Group_MarkerLayout;
   public static String Slideout_Map25MarkerOptions_Label_ClusterGridSize;
   public static String Slideout_Map25MarkerOptions_Label_ClusterOpacity;
   public static String Slideout_Map25MarkerOptions_Label_ClusterOpacity_Tooltip;
   public static String Slideout_Map25MarkerOptions_Label_ClusterOrientation;
   public static String Slideout_Map25MarkerOptions_Label_ClusterPlacement;
   public static String Slideout_Map25MarkerOptions_Label_ClusterPlacement_Tooltip;
   public static String Slideout_Map25MarkerOptions_Label_ClusterSize;
   public static String Slideout_Map25MarkerOptions_Label_ClusterSize_Tooltip;
   public static String Slideout_Map25MarkerOptions_Label_ClusterSymbolColor;
   public static String Slideout_Map25MarkerOptions_Label_ClusterSymbolColor_Tooltip;
   public static String Slideout_Map25MarkerOptions_Label_MarkerColor;
   public static String Slideout_Map25MarkerOptions_Label_MarkerColor_Tooltip;
   public static String Slideout_Map25MarkerOptions_Label_MarkerOpacity;
   public static String Slideout_Map25MarkerOptions_Label_MarkerOpacity_Tooltip;
   public static String Slideout_Map25MarkerOptions_Label_MarkerOrientation;
   public static String Slideout_Map25MarkerOptions_Label_MarkerSize;
   public static String Slideout_Map25MarkerOptions_Label_MarkerSize_Tooltip;
   public static String Slideout_Map25MarkerOptions_Label_Name;
   public static String Slideout_Map25MarkerOptions_Label_Name_Tooltip;
   public static String Slideout_Map25MarkerOptions_Label_SwapColor_Tooltip;
   public static String Slideout_Map25MarkerOptions_Label_Title;

   public static String Slideout_Map25Provider_Combo_MapProvider_Offline_Tooltip;
   public static String Slideout_Map25Provider_Combo_MapProvider_Online_Tooltip;
   public static String Slideout_Map25Provider_Label_DefaultTheme;
   public static String Slideout_Map25Provider_Label_MapProvider;
   public static String Slideout_Map25Provider_Label_MapProvider_Title;
   public static String Slideout_Map25Provider_Label_ThemeStyle;

   public static String Slideout_Map2MapOptions_Action_SetTourPaintingModeBasic;
   public static String Slideout_Map2MapOptions_Dialog_EnhancePaintingWarning_Message;
   public static String Slideout_Map2MapOptions_Dialog_EnhancePaintingWarning_Title;

   public static String Slideout_Map2Provider_Button_HideMP;
   public static String Slideout_Map2Provider_Button_HideMP_Tooltip;
   public static String Slideout_Map2Provider_Button_UnhideMP;
   public static String Slideout_Map2Provider_Button_UnhideMP_Tooltip;
   public static String Slideout_Map2Provider_Label_Tip;
   public static String Slideout_Map2Provider_Label_Tip_Tooltip;
   public static String Slideout_Map2Provider_Label_Title;
   public static String Slideout_Map2Provider_MapProvider_Next_Tooltip;
   public static String Slideout_Map2Provider_MapProvider_Previous_Tooltip;

   public static String Slideout_Map_Bookmark_Label_NumBookmarkListItems;
   public static String Slideout_Map_Bookmark_Label_NumBookmarkListItems_Tooltip;
   public static String Slideout_Map_Bookmark_Label_NumContextMenuItems;
   public static String Slideout_Map_Bookmark_Label_NumContextMenuItems_Tooltip;
   public static String Slideout_Map_Bookmark_Label_Title;

   public static String Slideout_Map_Options_Checkbox_ChartSlider;
   public static String Slideout_Map_Options_Checkbox_DimMap;
   public static String Slideout_Map_Options_Checkbox_ShowEnhancedWarning;
   public static String Slideout_Map_Options_Checkbox_ShowEnhancedWarning_Tooltip;
   public static String Slideout_Map_Options_Checkbox_ShowHoveredSelectedTour;
   public static String Slideout_Map_Options_Checkbox_ShowHoveredSelectedTour_Tooltip;
   public static String Slideout_Map_Options_Checkbox_ShowTourDirection;
   public static String Slideout_Map_Options_Checkbox_ShowTourDirection_Tooltip;
   public static String Slideout_Map_Options_Checkbox_SliderPath;
   public static String Slideout_Map_Options_Checkbox_SliderPath_Tooltip;
   public static String Slideout_Map_Options_Checkbox_ToggleKeyboardPanning;
   public static String Slideout_Map_Options_Checkbox_ToggleKeyboardPanning_Tooltip;
   public static String Slideout_Map_Options_Checkbox_TrackOpacity;
   public static String Slideout_Map_Options_Checkbox_TrackOpacity_Tooltip;
   public static String Slideout_Map_Options_Checkbox_ZoomWithMousePosition;
   public static String Slideout_Map_Options_Checkbox_ZoomWithMousePosition_Tooltip;
   public static String Slideout_Map_Options_Color_DimColor_Tooltip;
   public static String Slideout_Map_Options_Group_TourTrack;
   public static String Slideout_Map_Options_Label_HoveredAndSelectedColor;
   public static String Slideout_Map_Options_Label_HoveredAndSelectedColor_Tooltip;
   public static String Slideout_Map_Options_Label_HoveredColor;
   public static String Slideout_Map_Options_Label_HoveredColor_Tooltip;
   public static String Slideout_Map_Options_Label_SelectedColor;
   public static String Slideout_Map_Options_Label_SelectedColor_Tooltip;
   public static String Slideout_Map_Options_Label_SliderLocation_Color;
   public static String Slideout_Map_Options_Label_SliderLocation_Color_Tooltip;
   public static String Slideout_Map_Options_Label_SliderLocation_Size;
   public static String Slideout_Map_Options_Label_SliderPath_Color;
   public static String Slideout_Map_Options_Label_SliderPath_Color_Tooltip;
   public static String Slideout_Map_Options_Label_SliderPath_Segements;
   public static String Slideout_Map_Options_Label_SliderPath_Width;
   public static String Slideout_Map_Options_Label_Title;
   public static String Slideout_Map_Options_Label_TourDirection_DistanceBetweenMarkers;
   public static String Slideout_Map_Options_Label_TourDirection_LineWidth;
   public static String Slideout_Map_Options_Label_TourDirection_SymbolColor;
   public static String Slideout_Map_Options_Label_TourDirection_SymbolSize;
   public static String Slideout_Map_Options_Spinner_DimValue_Tooltip;

   public static String Slideout_Map_PhotoOptions_Label_Title;

   public static String Slideout_Map_SyncMap_Label_Title;

   public static String Slideout_Map_TourColors_Checkbox_ShowInChartToolbar_Tooltip;
   public static String Slideout_Map_TourColors_Label_Title;

   public static String Slideout_Map_TrackOptions_Label_Title;
   public static String Slideout_Map_TrackOptions_Label_Title_Tooltip;

   public static String Slideout_RefTour_YearStatisticOptions_Label_Title;

   public static String Slideout_SegmenterChartOptions_Checkbox_HideSmallValues;
   public static String Slideout_SegmenterChartOptions_Checkbox_HideSmallValues_Tooltip;
   public static String Slideout_SegmenterChartOptions_Checkbox_IsShowDecimalPlaces;
   public static String Slideout_SegmenterChartOptions_Checkbox_IsShowSegmentLine;
   public static String Slideout_SegmenterChartOptions_Checkbox_IsShowSegmentMarker;
   public static String Slideout_SegmenterChartOptions_Checkbox_IsShowSegmentTooltip;
   public static String Slideout_SegmenterChartOptions_Checkbox_IsShowSegmentValue;
   public static String Slideout_SegmenterChartOptions_Label_GraphOpacity;
   public static String Slideout_SegmenterChartOptions_Label_LineOpacity;
   public static String Slideout_SegmenterChartOptions_Label_Opacity_Tooltip;
   public static String Slideout_SegmenterChartOptions_Label_StackedValues;
   public static String Slideout_SegmenterChartOptions_Label_StackedValues_Tooltip;
   public static String Slideout_SegmenterChartOptions_Label_Title;
   public static String Slideout_SegmenterChartOptions_Label_ValueFont;
   public static String Slideout_SegmenterChartOptions_Label_ValueFont_Example;

   public static String Slideout_SegmenterOptions_Label_AltitudeDown;
   public static String Slideout_SegmenterOptions_Label_AltitudeUp;
   public static String Slideout_SegmenterOptions_Label_Title;
   public static String Slideout_SegmenterOptions_Label_Totals;

   public static String Slideout_StatisticOptions_Label_Title;

   public static String Slideout_TourBlogOptions_Label_Title;

   public static String Slideout_TourBookOptions_Checkbox_ShowTotalRow;
   public static String Slideout_TourBookOptions_Checkbox_ShowTotalRow_Tooltip;
   public static String Slideout_TourBookOptions_Label_Title;

   public static String Slideout_TourChartGraph_Checkbox_DefaultWhenOpened_Tooltip;
   public static String Slideout_TourChartGraph_Checkbox_ShowInChartToolbar_Tooltip;
   public static String Slideout_TourChartGraph_Label_DefaultWhenOpened;
   public static String Slideout_TourChartGraph_Label_ShowActionInToolbar;
   public static String Slideout_TourChartGraph_Label_ShowGraph;
   public static String Slideout_TourChartGraph_Label_Title;

   public static String Slideout_TourChartGraphBackground_Action_Colors_Tooltip;
   public static String Slideout_TourChartGraphBackground_Combo_BackgroundSource_Tooltip;
   public static String Slideout_TourChartGraphBackground_Label_BackgroundSource;
   public static String Slideout_TourChartGraphBackground_Label_BackgroundStyle;
   public static String Slideout_TourChartGraphBackground_Label_Title;

   public static String Slideout_TourChartOptions_Check_InvertPaceGraph;
   public static String Slideout_TourChartOptions_Check_InvertPaceGraph_Tooltip;
   public static String Slideout_TourChartOptions_Check_NightSectionsOpacity;
   public static String Slideout_TourChartOptions_Check_NightSectionsOpacity_Tooltip;
   public static String Slideout_TourChartOptions_Label_PulseGraph;
   public static String Slideout_TourChartOptions_Label_Title;

   public static String Slideout_TourChartSmoothing_Label_Title;

   public static String Slideout_TourEditor_Label_LatLonDigits;
   public static String Slideout_TourEditor_Label_LatLonDigits_Tooltip;
   public static String Slideout_TourEditor_Label_Title;
   public static String Slideout_TourFilter_Action_ActivateAll;
   public static String Slideout_TourFilter_Action_ActivateAll_Tooltip;
   public static String Slideout_TourFilter_Action_AddProfile;
   public static String Slideout_TourFilter_Action_AddProfile_Tooltip;
   public static String Slideout_TourFilter_Action_AddProperty;
   public static String Slideout_TourFilter_Action_AddProperty_Tooltip;
   public static String Slideout_TourFilter_Action_Apply;
   public static String Slideout_TourFilter_Action_CopyProfile;
   public static String Slideout_TourFilter_Action_CopyProfile_Tooltip;
   public static String Slideout_TourFilter_Action_DeactivateAll;
   public static String Slideout_TourFilter_Action_DeactivateAll_Tooltip;
   public static String Slideout_TourFilter_Action_DeleteProfile;
   public static String Slideout_TourFilter_Action_DeleteProfile_Tooltip;
   public static String Slideout_TourFilter_Action_DeleteProperty_Tooltip;
   public static String Slideout_TourFilter_Action_MovePropertyDown_Tooltip;
   public static String Slideout_TourFilter_Action_MovePropertyUp_Tooltip;
   public static String Slideout_TourFilter_Checkbox_IsLiveUpdate;
   public static String Slideout_TourFilter_Checkbox_IsLiveUpdate_Tooltip;
   public static String Slideout_TourFilter_Checkbox_IsPropertyEnabled_Tooltip;
   public static String Slideout_TourFilter_Column_ProfileName;
   public static String Slideout_TourFilter_Column_Properties;
   public static String Slideout_TourFilter_Column_Properties_Tooltip;
   public static String Slideout_TourFilter_Confirm_DeleteProfile_Message;
   public static String Slideout_TourFilter_Confirm_DeleteProfile_Title;
   public static String Slideout_TourFilter_Confirm_DeleteProperty_Message;
   public static String Slideout_TourFilter_Confirm_DeleteProperty_Title;
   public static String Slideout_TourFilter_Label_ProfileName;
   public static String Slideout_TourFilter_Label_Profiles;
   public static String Slideout_TourFilter_Label_Title;
   public static String Slideout_TourFilter_Link_TextSearchHint;
   public static String Slideout_TourFilter_Link_TextSearchHint_Tooltip;
   public static String Slideout_TourGeoFilter_Action_Delete_AllWithoutName;
   public static String Slideout_TourGeoFilter_Action_Delete_WithoutName;
   public static String Slideout_TourGeoFilter_Action_Delete_WithoutName_Tooltip;
   public static String Slideout_TourGeoFilter_Checkbox_IsAutoOpenSlideout;
   public static String Slideout_TourGeoFilter_Checkbox_IsAutoOpenSlideout_Tooltip;
   public static String Slideout_TourGeoFilter_Checkbox_IsSyncMapPosition;
   public static String Slideout_TourGeoFilter_Checkbox_IsSyncMapPosition_Tooltip;
   public static String Slideout_TourGeoFilter_Checkbox_IsUseFastMapPainting;
   public static String Slideout_TourGeoFilter_Checkbox_IsUseFastMapPainting_Tooltip;
   public static String Slideout_TourGeoFilter_Checkbox_UseAppFilter;
   public static String Slideout_TourGeoFilter_Color_GeoPartHover_Tooltip;
   public static String Slideout_TourGeoFilter_Color_GeoPartSelected_Tooltip;
   public static String Slideout_TourGeoFilter_Column_Created_Label;
   public static String Slideout_TourGeoFilter_Column_FilterName_Label;
   public static String Slideout_TourGeoFilter_Column_Latitude1_Header;
   public static String Slideout_TourGeoFilter_Column_Latitude1_Label;
   public static String Slideout_TourGeoFilter_Column_Latitude2_Header;
   public static String Slideout_TourGeoFilter_Column_Latitude2_Label;
   public static String Slideout_TourGeoFilter_Column_Longitude1_Header;
   public static String Slideout_TourGeoFilter_Column_Longitude1_Label;
   public static String Slideout_TourGeoFilter_Column_Longitude2_Header;
   public static String Slideout_TourGeoFilter_Column_Longitude2_Label;
   public static String Slideout_TourGeoFilter_Column_NumGeoParts_Header;
   public static String Slideout_TourGeoFilter_Column_NumGeoParts_Label;
   public static String Slideout_TourGeoFilter_Dialog_DeleteAllFilter_Message;
   public static String Slideout_TourGeoFilter_Dialog_DeleteAllFilter_Title;
   public static String Slideout_TourGeoFilter_Label_FilterIncludeExclude;
   public static String Slideout_TourGeoFilter_Label_GeoPartColor;
   public static String Slideout_TourGeoFilter_Label_Hint;
   public static String Slideout_TourGeoFilter_Label_History;
   public static String Slideout_TourGeoFilter_Label_Title;
   public static String Slideout_TourGeoFilter_Radio_GeoParts_Exclude;
   public static String Slideout_TourGeoFilter_Radio_GeoParts_Exclude_Tooltip;
   public static String Slideout_TourGeoFilter_Radio_GeoParts_Include;
   public static String Slideout_TourGeoFilter_Radio_GeoParts_Include_Tooltip;
   public static String Slideout_TourGeoFilter_Spinner_FastMapPainting_SkippedValues_Tooltip;

   public static String Slideout_TourInfoOptions_Checkbox_IsShowInfoTooltip;
   public static String Slideout_TourInfoOptions_Checkbox_IsShowTourSeparator;
   public static String Slideout_TourInfoOptions_Checkbox_IsShowTourSeparator_Tooltip;
   public static String Slideout_TourInfoOptions_Checkbox_IsShowTourTitle;
   public static String Slideout_TourInfoOptions_Label_Title;
   public static String Slideout_TourInfoOptions_Label_TooltipDelay;
   public static String Slideout_TourInfoOptions_Label_TooltipDelay_Tooltip;

   public static String Slideout_TourInfoOptions_Label_TooltipDelaySimple_Tooltip;

   public static String Slideout_TourMarkerFilter_Checkbox_IsReduceLatLonDigits;
   public static String Slideout_TourMarkerFilter_Checkbox_IsReduceLatLonDigits_Tooltip;
   public static String Slideout_TourMarkerFilter_Label_GeoFilter;
   public static String Slideout_TourMarkerFilter_Label_GeoFilter_Tooltip;
   public static String Slideout_TourMarkerFilter_Label_GeoFilterArea;
   public static String Slideout_TourMarkerFilter_Label_GeoFilterNotAvailable;
   public static String Slideout_TourMarkerFilter_Label_Title;

   public static String Slideout_TourTagFilter_Action_AddProfile_Tooltip;
   public static String Slideout_TourTagFilter_Action_CheckAllTags_Tooltip;
   public static String Slideout_TourTagFilter_Action_UncheckAllTags_Tooltip;
   public static String Slideout_TourTagFilter_Checkbox_IsLiveUpdate_Tooltip;
   public static String Slideout_TourTagFilter_Column_CombineTags;
   public static String Slideout_TourTagFilter_Column_CombineTags_Tooltip;
   public static String Slideout_TourTagFilter_Column_Tags_Checked;
   public static String Slideout_TourTagFilter_Column_Tags_Checked_Tooltip;
   public static String Slideout_TourTagFilter_Column_Tags_Unchecked;
   public static String Slideout_TourTagFilter_Column_Tags_Unchecked_Tooltip;
   public static String Slideout_TourTagFilter_CombineTags_With_AND;
   public static String Slideout_TourTagFilter_CombineTags_With_OR;
   public static String Slideout_TourTagFilter_Label_AllTags;
   public static String Slideout_TourTagFilter_Label_SelectedTags;
   public static String Slideout_TourTagFilter_Label_TagOperator;
   public static String Slideout_TourTagFilter_Label_Title;
   public static String Slideout_TourTagFilter_Radio_TagOperator_AND;
   public static String Slideout_TourTagFilter_Radio_TagOperator_AND_Tooltip;
   public static String Slideout_TourTagFilter_Radio_TagOperator_OR;
   public static String Slideout_TourTagFilter_Radio_TagOperator_OR_Tooltip;

   public static String Slideout_TrainingOptions_Label_Title;

   public static String SRTM_Download_Dialog_SRTMDownloadValidation_Title;
   public static String SRTM_Download_Info_NoDownloadValidation;
   public static String SRTM_Download_Info_UsernamePasswordIsEmpty;

   public static String Tag_Manager_Action_DeleteCategory;
   public static String Tag_Manager_Action_DeleteTag;
   public static String Tag_Manager_Action_DeleteTags;
   public static String Tag_Manager_Dialog_DeleteCategory_Categories_Message;
   public static String Tag_Manager_Dialog_DeleteCategory_Message;
   public static String Tag_Manager_Dialog_DeleteCategory_Tags_Message;
   public static String Tag_Manager_Dialog_DeleteCategory_Title;
   public static String Tag_Manager_Dialog_DeleteTag_Message;
   public static String Tag_Manager_Dialog_DeleteTag_Multiple_Message;
   public static String Tag_Manager_Dialog_DeleteTag_Title;
   public static String Tag_Manager_LogInfo_DeletedTagCategory;
   public static String Tag_Manager_LogInfo_DeletedTags;

   public static String Tooltip_ValuePoint_Action_CloseContextMenu;
   public static String Tooltip_ValuePoint_Action_OpenToolTipMenu_ToolTip;
   public static String Tooltip_ValuePoint_Action_Orientation_Horizontal;
   public static String Tooltip_ValuePoint_Action_Orientation_Vertical;
   public static String Tooltip_ValuePoint_Action_PinLocation_BottomLeft;
   public static String Tooltip_ValuePoint_Action_PinLocation_BottomRight;
   public static String Tooltip_ValuePoint_Action_PinLocation_Header;
   public static String Tooltip_ValuePoint_Action_PinLocation_MouseXPosition;
   public static String Tooltip_ValuePoint_Action_PinLocation_Screen;
   public static String Tooltip_ValuePoint_Action_PinLocation_TopLeft;
   public static String Tooltip_ValuePoint_Action_PinLocation_TopRight;
   public static String Tooltip_ValuePoint_Action_Value_Altimeter;
   public static String Tooltip_ValuePoint_Action_Value_Altitude;
   public static String Tooltip_ValuePoint_Action_Value_Cadence;
   public static String Tooltip_ValuePoint_Action_Value_ChartZoomFactor;
   public static String Tooltip_ValuePoint_Action_Value_Distance;
   public static String Tooltip_ValuePoint_Action_Value_Gears;
   public static String Tooltip_ValuePoint_Action_Value_Gradient;
   public static String Tooltip_ValuePoint_Action_Value_Header;
   public static String Tooltip_ValuePoint_Action_Value_Pace;
   public static String Tooltip_ValuePoint_Action_Value_Power;
   public static String Tooltip_ValuePoint_Action_Value_Pulse;
   public static String Tooltip_ValuePoint_Action_Value_RunDyn_StanceTime;
   public static String Tooltip_ValuePoint_Action_Value_RunDyn_StanceTimeBalance;
   public static String Tooltip_ValuePoint_Action_Value_RunDyn_StepLength;
   public static String Tooltip_ValuePoint_Action_Value_RunDyn_VerticalOscillation;
   public static String Tooltip_ValuePoint_Action_Value_RunDyn_VerticalRatio;
   public static String Tooltip_ValuePoint_Action_Value_Speed;
   public static String Tooltip_ValuePoint_Action_Value_Temperature;
   public static String Tooltip_ValuePoint_Action_Value_TimeDuration;
   public static String Tooltip_ValuePoint_Action_Value_TimeOfDay;
   public static String Tooltip_ValuePoint_Action_Value_TimeSlices;
   public static String Tooltip_ValuePoint_Action_Value_TourCompareResult;
   public static String Tooltip_ValuePoint_Format_Pace;
   public static String Tooltip_ValuePoint_Label_ChartZoomFactor_Tooltip;
   public static String Tooltip_ValuePoint_Label_NoData;
   public static String Tooltip_ValuePoint_Label_NoData_Tooltip;
   public static String Tooltip_ValuePoint_Label_SlicesCurrent_Tooltip;
   public static String Tooltip_ValuePoint_Label_SlicesMax_Tooltip;

   public static String Tour_Action_AdjustTemperature;
   public static String Tour_Action_AdjustTourValues;
   public static String Tour_Action_RetrieveWeatherData;
   public static String Tour_Action_auto_move_sliders_when_zoomed;
   public static String Tour_Action_auto_zoom_to_slider_position;
   public static String Tour_Action_DuplicateTour;
   public static String Tour_Action_EditChartPreferences;
   public static String Tour_Action_EditSmoothingPreferences;
   public static String Tour_Action_EditStatisticPreferences;
   public static String Tour_Action_graph_altimeter_tooltip;
   public static String Tour_Action_graph_altitude_tooltip;
   public static String Tour_Action_graph_analyzer_tooltip;
   public static String Tour_Action_graph_cadence_tooltip;
   public static String Tour_Action_graph_gradient_tooltip;
   public static String Tour_Action_graph_heartbeat_tooltip;
   public static String Tour_Action_graph_pace_tooltip;
   public static String Tour_Action_graph_power_tooltip;
   public static String Tour_Action_graph_speed_tooltip;
   public static String Tour_Action_graph_temperature_tooltip;
   public static String Tour_Action_graph_tour_compare_tooltip;
   public static String Tour_Action_ComputeCadenceZonesTimes;
   public static String Tour_Action_ComputeCadenceZonesTimes_Message;
   public static String Tour_Action_ComputeCadenceZonesTimes_Title;
   public static String Tour_Action_GeoCompare_Tooltip;
   public static String Tour_Action_GraphGears;
   public static String Tour_Action_GraphOverlapped;
   public static String Tour_Action_MapMarkerOptions_Tooltip;
   public static String Tour_Action_Marker_Delete;
   public static String Tour_Action_Marker_Delete_WithConfirm;
   public static String Tour_Action_Marker_PositionHorizontal;
   public static String Tour_Action_Marker_PositionVertical;
   public static String Tour_Action_Marker_SetHidden;
   public static String Tour_Action_Marker_SetLabelPosition;
   public static String Tour_Action_Marker_SetVisible;
   public static String Tour_Action_MarkerOptions_Tooltip;
   public static String Tour_Action_MultiplyCaloriesBy1000;
   public static String Tour_Action_MultiplyCaloriesBy1000_Apply;
   public static String Tour_Action_MultiplyCaloriesBy1000_Message;
   public static String Tour_Action_MultiplyCaloriesBy1000_Title;
   public static String Tour_Action_RunDyn_StanceTime_Tooltip;
   public static String Tour_Action_RunDyn_StanceTimeBalance_Tooltip;
   public static String Tour_Action_RunDyn_StepLength_Tooltip;
   public static String Tour_Action_RunDyn_VerticalOscillation_Tooltip;
   public static String Tour_Action_RunDyn_VerticalRatio_Tooltip;
   public static String Tour_Action_scroll_zoomed_chart;
   public static String Tour_Action_show_distance_on_x_axis;
   public static String Tour_Action_show_distance_on_x_axis_tooltip;
   public static String Tour_Action_show_start_time_on_x_axis;
   public static String Tour_Action_show_time_on_x_axis;
   public static String Tour_Action_show_time_on_x_axis_tooltip;
   public static String Tour_Action_SetMinMaxTemperature;
   public static String Tour_Action_SetMinMaxTemperature_Apply;
   public static String Tour_Action_SetMinMaxTemperature_Message;
   public static String Tour_Action_SetMinMaxTemperature_Title;
   public static String Tour_Action_SetTimeZone;
   public static String Tour_Action_SetWeatherConditions;
   public static String Tour_Action_ShowBreaktimeValues;
   public static String Tour_Action_ShowTourPauses;
   public static String Tour_Action_ShowTourPauses_Tooltip;
   public static String Tour_Action_ShowValuePointValue;
   public static String Tour_Action_Swim_Strokes_Tooltip;
   public static String Tour_Action_Swim_Swolf_Tooltip;
   public static String Tour_Action_TourInfo_Tooltip;
   public static String Tour_Action_TourPhotos;
   public static String Tour_Action_TourPhotosWithoutTooltip_Tooltip;
   public static String Tour_Action_TourPhotosWithTooltip_Tooltip;
   public static String Tour_Action_ValuePointToolTip_IsVisible;
   public static String Tour_Action_Select_Inbetween_Timeslices;
   public static String Tour_Action_Select_Inbetween_Timeslices_Tooltip;
   public static String Tour_Action_Weather;

   public static String Tour_Blog_Action_EditMarker_Tooltip;
   public static String Tour_Blog_Action_EditTour_Tooltip;
   public static String Tour_Blog_Action_HideMarker_Tooltip;
   public static String Tour_Blog_Action_OpenMarker_Tooltip;
   public static String Tour_Blog_Action_ShowMarker_Tooltip;

   public static String Tour_Book_Action_delete_selected_tours;
   public static String Tour_Book_Action_delete_selected_tours_dlg_message;
   public static String Tour_Book_Action_delete_selected_tours_dlg_message_confirm;
   public static String Tour_Book_Action_delete_selected_tours_dlg_title;
   public static String Tour_Book_Action_delete_selected_tours_dlg_title_confirm;
   public static String Tour_Book_Action_delete_selected_tours_menu;
   public static String Tour_Book_Action_DeleteSelectedTours_Monitor;
   public static String Tour_Book_Action_DeleteSelectedTours_MonitorSubtask;
   public static String Tour_Book_Action_ExportViewCSV;

   public static String Tour_Book_Action_ToggleViewLayout_Tooltip;
   public static String Tour_Book_Combo_statistic_tooltip;
   public static String Tour_Book_Label_chart_title;
   public static String Tour_Book_Label_Total;
   public static String Tour_Book_Monitor_CollateSubtask;
   public static String Tour_Book_Monitor_CollateTask;
   public static String Tour_Book_SortColumnTooltip;

   public static String Tour_Data_LoadTourData_Monitor;
   public static String Tour_Data_LoadTourData_Monitor_SubTask;
   public static String Tour_Data_SaveTour_Monitor;
   public static String Tour_Data_SaveTour_MonitorSubtask;

   public static String Tour_Data_Text_AltitudeValues;
   public static String Tour_Data_Text_BatteryValues;
   public static String Tour_Data_Text_CadenceValues;
   public static String Tour_Data_Text_Calories;
   public static String Tour_Data_Text_GearValues;
   public static String Tour_Data_Text_PowerValues;
   public static String Tour_Data_Text_PulseValues;
   public static String Tour_Data_Text_RunningDynamicsValues;
   public static String Tour_Data_Text_SwimmingValues;
   public static String Tour_Data_Text_SpeedValues;
   public static String Tour_Data_Text_TemperatureValues;
   public static String Tour_Data_Text_TimeSlices;
   public static String Tour_Data_Text_Time;
   public static String Tour_Data_Text_TourMarkers;
   public static String Tour_Data_Text_TourTimerPauses;
   public static String Tour_Data_Text_TrainingValues;
   public static String Tour_Data_Text_EntireTour;
   public static String Tour_Data_Text_ImportFileLocation;

   public static String Tour_Database_Action_CloseApp;
   public static String Tour_Database_Action_UpdateDatabase;
   public static String Tour_Database_CannotConnectToDerbyServer_Message;
   public static String Tour_Database_CannotConnectToDerbyServer_Title;
   public static String Tour_Database_Dialog_ConfirmUpdate_Message;
   public static String Tour_Database_Dialog_ConfirmUpdate_Title;
   public static String Tour_Database_Dialog_ValidateFields_Message;
   public static String Tour_Database_Dialog_ValidateFields_Title;
   public static String Tour_Database_load_all_tours;
   public static String Tour_Database_PostUpdate_028_SetAvgPulse;
   public static String Tour_Database_PostUpdate_029_SetImportFileName;
   public static String Tour_Database_PostUpdate_032_SetTourTimeZone;
   public static String Tour_Database_PostUpdate_034_SetTourGeoParts;
   public static String Tour_Database_PostUpdate_037_SetHasGeoData;
   public static String Tour_Database_PostUpdate_040_SetTourRecordingTime;
   public static String Tour_Database_PostUpdate_043_LatLonE6;
   public static String Tour_Database_PostUpdate011_SetTourCreateTime;
   public static String Tour_Database_PostUpdate020_ConvertIntToFloat;
   public static String Tour_Database_PostUpdate021_SetTourStartEndTime;
   public static String Tour_Database_PostUpdate023_SetTimeSliceNumbers;
   public static String Tour_Database_PostUpdate025_SetMarkerFields;
   public static String Tour_Database_TourSaveError;
   public static String Tour_Database_update_tour;
   public static String Tour_Database_Update;
   public static String Tour_Database_Update_ModifyColumn;
   public static String Tour_Database_Update_Subtask;
   public static String Tour_Database_Update_TourWeek;
   public static String Tour_Database_UpdateDone;
   public static String Tour_Database_UpdateInfo;

   public static String Tour_Editor_Action_DeleteTimeSlices_AdjustTourStartTime;
   public static String Tour_Editor_Action_DeleteTimeSlices_AdjustTourStartTime_Tooltip;
   public static String Tour_Editor_Decorator_TimeZone_Tooltip;
   public static String Tour_Editor_Dialog_DeleteSwimTimeSlices_Message;
   public static String Tour_Editor_Dialog_DeleteSwimTimeSlices_Title;
   public static String Tour_Editor_Label_AirPressure;
   public static String Tour_Editor_Label_AirPressure_Tooltip;
   public static String Tour_Editor_Label_AltitudeDown;
   public static String Tour_Editor_Label_AltitudeUp;
   public static String Tour_Editor_Label_BodyFat;
   public static String Tour_Editor_Label_BodyFat_Tooltip;
   public static String Tour_Editor_Label_BodyWeight;
   public static String Tour_Editor_Label_BodyWeight_Tooltip;
   public static String Tour_Editor_Label_Cadence;
   public static String Tour_Editor_Label_Cadence_Tooltip;
   public static String Tour_Editor_Label_DateTimeCreated;
   public static String Tour_Editor_Label_DateTimeModified;
   public static String Tour_Editor_Label_DeviceFirmwareVersion;
   public static String Tour_Editor_Label_DeviceSensor_Tooltip;
   public static String Tour_Editor_Label_DistanceSensor;
   public static String Tour_Editor_Label_FTP;
   public static String Tour_Editor_Label_FTP_Tooltip;
   public static String Tour_Editor_Label_Hours_Tooltip;
   public static String Tour_Editor_Label_Humidity;
   public static String Tour_Editor_Label_Humidity_Tooltip;
   public static String Tour_Editor_Label_Minutes_Tooltip;
   public static String Tour_Editor_Label_PowerSensor;
   public static String Tour_Editor_Label_Precipitation;
   public static String Tour_Editor_Label_Precipitation_Tooltip;
   public static String Tour_Editor_Label_PulseSensor;
   public static String Tour_Editor_Label_Seconds_Tooltip;
   public static String Tour_Editor_Label_Sensor_No;
   public static String Tour_Editor_Label_Sensor_Yes;
   public static String Tour_Editor_Label_StrideSensor;
   public static String Tour_Editor_Label_Temperature;
   public static String Tour_Editor_Label_Temperature_Avg_Tooltip;
   public static String Tour_Editor_Label_Temperature_Max_Tooltip;
   public static String Tour_Editor_Label_Temperature_Min_Tooltip;
   public static String Tour_Editor_Label_Temperature_Tooltip;
   public static String Tour_Editor_Label_Temperature_WindChill_Tooltip;
   public static String Tour_Editor_Label_TimeZone;
   public static String Tour_Editor_Label_TourStartTime_Tooltip;
   public static String Tour_Editor_Label_Weather;
   public static String Tour_Editor_Label_WindDirection_Unit;
   public static String Tour_Editor_Link_RemoveTimeZone;
   public static String Tour_Editor_Link_RemoveTimeZone_Tooltip;
   public static String Tour_Editor_Link_SetDefautTimeZone;
   public static String Tour_Editor_Link_SetDefautTimeZone_Tooltip;
   public static String Tour_Editor_Link_SetGeoTimeZone;
   public static String Tour_Editor_Link_SetGeoTimeZone_Tooltip;
   public static String Tour_Editor_Link_RetrieveWeather;
   public static String Tour_Editor_Link_RetrieveWeather_Tooltip;
   public static String Tour_Editor_NoSwimData;
   public static String Tour_Editor_TabLabel_SwimSlices;

   public static String Tour_Filter_Action_Tooltip;
   public static String Tour_Filter_Default_ProfileName;
   public static String Tour_Filter_Field_Altitude_Ascent;
   public static String Tour_Filter_Field_Altitude_Descent;
   public static String Tour_Filter_Field_Altitude_Max;
   public static String Tour_Filter_Field_ComputedTime_Break;
   public static String Tour_Filter_Field_Distance;
   public static String Tour_Filter_Field_ComputedTime_Moving;
   public static String Tour_Filter_Field_ManualTour;
   public static String Tour_Filter_Field_Photos;
   public static String Tour_Filter_Field_DeviceTime_Elapsed;
   public static String Tour_Filter_Field_Season;
   public static String Tour_Filter_Field_Temperature;
   public static String Tour_Filter_Field_TourDate;
   public static String Tour_Filter_Field_TourLocation_End;
   public static String Tour_Filter_Field_TourLocation_Start;
   public static String Tour_Filter_Field_TourStartTime;
   public static String Tour_Filter_Field_TourTitle;
   public static String Tour_Filter_Operator_And;
   public static String Tour_Filter_Operator_Between;
   public static String Tour_Filter_Operator_EndsWith;
   public static String Tour_Filter_Operator_Equals;
   public static String Tour_Filter_Operator_GreaterThan;
   public static String Tour_Filter_Operator_GreaterThanOrEqual;
   public static String Tour_Filter_Operator_IsAvailable;
   public static String Tour_Filter_Operator_IsEmpty;
   public static String Tour_Filter_Operator_IsNotAvailable;
   public static String Tour_Filter_Operator_IsNotEmpty;
   public static String Tour_Filter_Operator_LessThan;
   public static String Tour_Filter_Operator_LessThanOrEqual;
   public static String Tour_Filter_Operator_NotBetween;
   public static String Tour_Filter_Operator_NotEquals;
   public static String Tour_Filter_Operator_Season_Current_Day;
   public static String Tour_Filter_Operator_Season_Current_Month;
   public static String Tour_Filter_Operator_Season_Month;
   public static String Tour_Filter_Operator_Season_Today_Until_Date;
   public static String Tour_Filter_Operator_Season_Today_Until_YearEnd;
   public static String Tour_Filter_Operator_Season_UntilToday_From_Date;
   public static String Tour_Filter_Operator_Season_UntilToday_From_YearStart;
   public static String Tour_Filter_Operator_StartsWith;

   public static String Tour_GeoFilter_Action_Tooltip;

   public static String Tour_Info_Flag_Array;
   public static String Tour_Info_Flag_Database;
   public static String Tour_Info_Label_AllFields;
   public static String Tour_Info_Label_AllFields_Tooltip;

   public static String Tour_Log_Action_Clear_Tooltip;

   public static String TourManager_Dialog_OutOfSyncError_Message;
   public static String TourManager_Dialog_OutOfSyncError_Title;

   public static String Tour_Marker_Column_Description_ShortCut;
   public static String Tour_Marker_Column_Description_Tooltip;
   public static String Tour_Marker_Column_horizontal_offset;
   public static String Tour_Marker_Column_horizontal_offset_tooltip;
   public static String Tour_Marker_Column_km_tooltip;
   public static String Tour_Marker_Column_remark;
   public static String Tour_Marker_Column_Url_ShortCut;
   public static String Tour_Marker_Column_Url_Tooltip;
   public static String Tour_Marker_Column_vertical_offset;
   public static String Tour_Marker_Column_vertical_offset_tooltip;
   public static String Tour_Marker_Position_horizontal_above_centered;
   public static String Tour_Marker_Position_horizontal_above_left;
   public static String Tour_Marker_Position_horizontal_above_right;
   public static String Tour_Marker_Position_Horizontal_AboveCentered;
   public static String Tour_Marker_Position_Horizontal_AboveLeft;
   public static String Tour_Marker_Position_Horizontal_AboveRight;
   public static String Tour_Marker_Position_Horizontal_BelowCentered;
   public static String Tour_Marker_Position_Horizontal_BelowLeft;
   public static String Tour_Marker_Position_Horizontal_BelowRight;
   public static String Tour_Marker_Position_horizontal_below_centered;
   public static String Tour_Marker_Position_horizontal_below_left;
   public static String Tour_Marker_Position_horizontal_below_right;
   public static String Tour_Marker_Position_horizontal_left;
   public static String Tour_Marker_Position_horizontal_right;
   public static String Tour_Marker_Position_MarkerPoint_Left;
   public static String Tour_Marker_Position_MarkerPoint_Right;
   public static String Tour_Marker_Position_vertical_above;
   public static String Tour_Marker_Position_vertical_below;
   public static String Tour_Marker_Position_vertical_chart_bottom;
   public static String Tour_Marker_Position_vertical_chart_top;
   public static String Tour_Marker_Position_Vertical_Chart_Bottom;
   public static String Tour_Marker_Position_Vertical_Chart_Top;
   public static String Tour_Marker_Position_Vertical_MarkerPoint_Above;
   public static String Tour_Marker_Position_Vertical_MarkerPoint_Below;
   public static String Tour_Marker_TooltipPosition_Bottom;
   public static String Tour_Marker_TooltipPosition_ChartBottom;
   public static String Tour_Marker_TooltipPosition_ChartTop;
   public static String Tour_Marker_TooltipPosition_Left;
   public static String Tour_Marker_TooltipPosition_Right;
   public static String Tour_Marker_TooltipPosition_Top;

   public static String Tour_Segmenter_Action_ShowHideSegmentsInTourChart_Tooltip;
   public static String Tour_Segmenter_Button_SaveTour_Tooltip;
   public static String Tour_Segmenter_Label_AltitudeUpDown_Tooltip;
   public static String Tour_Segmenter_Label_DPTolerance;
   public static String Tour_Segmenter_Label_DPTolerance_Tooltip;
   public static String Tour_Segmenter_Label_no_chart;
   public static String Tour_Segmenter_Label_NumberOfSegments_Tooltip;
   public static String Tour_Segmenter_Surfing_Button_DeleteWaves;
   public static String Tour_Segmenter_Surfing_Button_DeleteWaves_Tooltip;
   public static String Tour_Segmenter_Surfing_Button_IsNotSaveState_Tooltip;
   public static String Tour_Segmenter_Surfing_Button_IsSaveState_Tooltip;
   public static String Tour_Segmenter_Surfing_Button_SaveWaves;
   public static String Tour_Segmenter_Surfing_Button_SaveWaves_Tooltip;
   public static String Tour_Segmenter_Surfing_Button_RestoreFromDefaults_Tooltip;
   public static String Tour_Segmenter_Surfing_Button_RestoreFromTour;
   public static String Tour_Segmenter_Surfing_Button_RestoreFromTour_Tooltip;
   public static String Tour_Segmenter_Surfing_Button_RestoreFromTourWithData_Tooltip;
   public static String Tour_Segmenter_Surfing_Checkbox_IsMinDistance;
   public static String Tour_Segmenter_Surfing_Checkbox_IsMinDistance_Tooltip;
   public static String Tour_Segmenter_Surfing_Checkbox_IsShowOnlySelectedSegments;
   public static String Tour_Segmenter_Surfing_Label_IsShowOnlySelectedSegments_Tooltip;
   public static String Tour_Segmenter_Surfing_Label_MinSpeed_StartStop;
   public static String Tour_Segmenter_Surfing_Label_MinSpeed_StartStop_Tooltip;
   public static String Tour_Segmenter_Surfing_Label_MinSpeed_Surfing;
   public static String Tour_Segmenter_Surfing_Label_MinSpeed_Surfing_Tooltip;
   public static String Tour_Segmenter_Surfing_Label_MinTimeDuration;
   public static String Tour_Segmenter_Surfing_Label_MinTimeDuration_Tooltip;
   public static String Tour_Segmenter_SurfingFilter_All;
   public static String Tour_Segmenter_SurfingFilter_Paddling;
   public static String Tour_Segmenter_SurfingFilter_Surfing;
   public static String Tour_Segmenter_Type_ByAltitude_Marker;
   public static String Tour_Segmenter_Type_ByAltitude_Merged;
   public static String Tour_Segmenter_Type_ByBreakTime;
   public static String Tour_Segmenter_Type_Surfing;

   public static String Tour_Statistic_Combo_BarVOrder_Tooltip;
   public static String Tour_Statistic_Combo_Year_Tooltip;

   public static String Tour_StatisticValues_Action_CopyIntoClipboard_Tooltip;
   public static String Tour_StatisticValues_Action_CSVFormat_Tooltip;
   public static String Tour_StatisticValues_Action_GroupValues_Tooltip;
   public static String Tour_StatisticValues_Action_OpenPreferences_Tooltip;
   public static String Tour_StatisticValues_Action_ShowSequenceNumbers_Tooltip;
   public static String Tour_StatisticValues_Action_ShowZeroValued_Tooltip;
   public static String Tour_StatisticValues_Info_DataAreCopied;
   public static String Tour_StatisticValues_Label_NoData;
   public static String Tour_StatisticValues_Label_NoStatistic;

   public static String Tour_SubMenu_Cadence;
   public static String Tour_SubMenu_Elevation;

   public static String Tour_Tag_Filter_Action_Tooltip;

   public static String Tour_Tags_Action_Layout_Flat_Tooltip;
   public static String Tour_Tags_Action_Layout_Hierarchical_Tooltip;
   public static String Tour_Tags_Action_OnMouseSelect_ExpandCollapse;

   public static String Tour_Tags_Action_SingleExpand_CollapseOthers;
   public static String Tour_Tags_Action_TagCheckFilter_AllTags_Tooltip;
   public static String Tour_Tags_Action_TagCheckFilter_OnlyTaggedTours_Tooltip;

   public static String Tour_Tags_Title_MultipleTours;
   public static String Tour_Tags_Title_OneTour;

   public static String tag_view_action_refresh_view_tooltip;
   public static String tag_view_title_tag;
   public static String tag_view_title_tag_category;

   public static String tour_action_show_srtm_data;

   public static String tour_data_label_device_marker;
   public static String tour_data_label_feature_since_version_9_01;
   public static String tour_data_label_manually_created_tour;

   public static String tour_database_computeComputedValues_resultMessage;
   public static String tour_database_computeComputedValues_resultTitle;
   public static String tour_database_computeComputeValues_mainTask;
   public static String tour_database_computeComputeValues_subTask;
   public static String tour_database_version_info_message;
   public static String tour_database_version_info_title;

   public static String tour_editor_csvTimeSliceExport;
   public static String tour_editor_dlg_create_tour_message;
   public static String tour_editor_dlg_create_tour_title;
   public static String tour_editor_dlg_delete_marker_message;
   public static String tour_editor_dlg_delete_marker_title;
   public static String tour_editor_dlg_delete_rows_message;
   public static String tour_editor_dlg_delete_rows_mode_message;
   public static String tour_editor_dlg_delete_rows_mode_toggle_message;
   public static String tour_editor_dlg_delete_rows_not_successive;
   public static String tour_editor_dlg_delete_rows_title;
   public static String tour_editor_dlg_discard_tour_message;
   public static String tour_editor_dlg_discard_tour_title;
   public static String tour_editor_dlg_reload_data_message;
   public static String tour_editor_dlg_reload_data_title;
   public static String tour_editor_dlg_revert_tour_message;
   public static String tour_editor_dlg_revert_tour_title;
   public static String tour_editor_dlg_revert_tour_toggle_message;
   public static String tour_editor_dlg_save_invalid_tour;
   public static String tour_editor_dlg_save_tour_message;
   public static String tour_editor_dlg_save_tour_title;
   public static String tour_editor_label_datapoints;
   public static String tour_editor_label_description;
   public static String tour_editor_label_device_name;
   public static String tour_editor_label_distance;
   public static String tour_editor_label_moving_time;
   public static String tour_editor_label_end_location;
   public static String tour_editor_label_import_file_path;
   public static String tour_editor_label_merge_from_tour_id;
   public static String tour_editor_label_merge_from_tour_id_tooltip;
   public static String tour_editor_label_merge_into_tour_id;
   public static String tour_editor_label_merge_into_tour_id_tooltip;
   public static String tour_editor_label_break_time;
   public static String tour_editor_label_paused_time;
   public static String tour_editor_label_recorded_time;
   public static String tour_editor_label_person;
   public static String tour_editor_label_elapsed_time;
   public static String tour_editor_label_ref_tour;
   public static String tour_editor_label_ref_tour_none;
   public static String tour_editor_label_start_location;
   public static String tour_editor_label_start_time;
   public static String tour_editor_label_time;
   public static String tour_editor_label_time_unit;
   public static String tour_editor_label_tour_calories;
   public static String tour_editor_label_tour_date;
   public static String tour_editor_label_tour_distance;
   public static String tour_editor_label_tour_id;
   public static String tour_editor_label_tour_id_tooltip;
   public static String tour_editor_label_tour_tag;
   public static String tour_editor_label_tour_title;
   public static String tour_editor_label_tour_type;
   public static String tour_editor_label_wind_direction;
   public static String tour_editor_label_wind_direction_Tooltip;
   public static String tour_editor_label_wind_speed;
   public static String tour_editor_label_wind_speed_Tooltip;
   public static String tour_editor_label_clouds;
   public static String tour_editor_label_clouds_Tooltip;
   public static String tour_editor_label_rest_pulse;
   public static String tour_editor_label_rest_pulse_Tooltip;
   public static String tour_editor_label_WindDirectionNESW_Tooltip;
   public static String tour_editor_message_person_is_required;
   public static String tour_editor_message_show_another_tour;
   public static String tour_editor_section_characteristics;
   public static String tour_editor_section_date_time;
   public static String tour_editor_section_tour;
   public static String tour_editor_section_personal;
   public static String tour_editor_section_weather;
   public static String tour_editor_tabLabel_tour;
   public static String tour_editor_tabLabel_tour_data;

   public static String tour_merger_btn_reset_adjustment;
   public static String tour_merger_btn_reset_adjustment_tooltip;
   public static String tour_merger_btn_reset_values;
   public static String tour_merger_btn_reset_values_tooltip;
   public static String tour_merger_chk_adjust_altitude_from_source;
   public static String tour_merger_chk_adjust_altitude_from_source_tooltip;
   public static String tour_merger_chk_adjust_altitude_linear_interpolition;
   public static String tour_merger_chk_adjust_altitude_linear_interpolition_tooltip;
   public static String tour_merger_chk_adjust_start_altitude;
   public static String tour_merger_chk_adjust_start_altitude_tooltip;
   public static String tour_merger_chk_alti_diff_scaling;
   public static String tour_merger_chk_alti_diff_scaling_tooltip;
   public static String tour_merger_chk_keep_horiz_vert_adjustments;
   public static String tour_merger_chk_keep_horiz_vert_adjustments_tooltip;
   public static String tour_merger_chk_preview_graphs;
   public static String tour_merger_chk_preview_graphs_tooltip;
   public static String tour_merger_chk_set_tour_type;
   public static String tour_merger_chk_set_tour_type_tooltip;
   public static String tour_merger_chk_use_synced_start_time;
   public static String tour_merger_chk_use_synced_start_time_tooltip;
   public static String tour_merger_dialog_header_message;
   public static String tour_merger_dialog_header_title;
   public static String tour_merger_dialog_title;
   public static String tour_merger_group_adjust_altitude;
   public static String tour_merger_group_adjust_time;
   public static String tour_merger_group_save_actions;
   public static String tour_merger_group_save_actions_tooltip;
   public static String tour_merger_label_adjust_minutes;
   public static String tour_merger_label_adjust_seconds;
   public static String tour_merger_save_target_tour;

   public static String tour_segmenter_button_updateAltitude;
   public static String tour_segmenter_label_createSegmentsWith;
   public static String tour_segmenter_segType_byDistance_defaultDistance;
   public static String tour_segmenter_segType_byDistance_label;
   public static String tour_segmenter_segType_byUpDownAlti_label;
   public static String tour_segmenter_type_byAltitude;
   public static String tour_segmenter_type_byComputedAltiUpDown;
   public static String tour_segmenter_type_byDistance;
   public static String tour_segmenter_type_byMarker;
   public static String tour_segmenter_type_byPower;
   public static String tour_segmenter_type_byPulse;

   public static String tour_statistic_number_of_years;

   public static String TourAnalyzer_Label_average;
   public static String TourAnalyzer_Label_difference;
   public static String TourAnalyzer_Label_left;
   public static String TourAnalyzer_Label_maximum;
   public static String TourAnalyzer_Label_minimum;
   public static String TourAnalyzer_Label_NoTourOrChart;
   public static String TourAnalyzer_Label_right;
   public static String TourAnalyzer_Label_value;

   public static String tourCatalog_view_action_create_left_marker;
   public static String tourCatalog_view_action_create_marker;
   public static String tourCatalog_view_action_create_reference_tour;
   public static String tourCatalog_view_action_create_right_marker;
   public static String tourCatalog_view_action_delete_tours;
   public static String tourCatalog_view_action_link;
   public static String tourCatalog_view_action_rename_reference_tour;
   public static String tourCatalog_view_action_save_marker;
   public static String tourCatalog_view_action_synch_chart_years_tooltip;
   public static String tourCatalog_view_action_synch_charts_byScale_tooltip;
   public static String tourCatalog_view_action_synch_charts_bySize_tooltip;
   public static String tourCatalog_view_action_undo_marker_position;
   public static String tourCatalog_view_compare_job_subtask;
   public static String tourCatalog_view_compare_job_task;
   public static String tourCatalog_view_compare_job_title;
   public static String tourCatalog_view_dlg_add_reference_tour_msg;
   public static String tourCatalog_view_dlg_add_reference_tour_title;
   public static String tourCatalog_view_dlg_delete_comparedTour_msg;
   public static String tourCatalog_view_dlg_delete_comparedTour_title;
   public static String tourCatalog_view_dlg_delete_refTour_msg;
   public static String tourCatalog_view_dlg_delete_refTour_title;
   public static String tourCatalog_view_dlg_rename_reference_tour_msg;
   public static String tourCatalog_view_dlg_rename_reference_tour_title;
   public static String tourCatalog_view_dlg_save_compared_tour_message;
   public static String tourCatalog_view_dlg_save_compared_tour_title;
   public static String tourCatalog_view_label_chart_title_reference_tour;
   public static String tourCatalog_view_label_year_chart_title;
   public static String tourCatalog_view_label_year_not_selected;

   public static String tourCatalog_wizard_Column_altitude_up_tooltip;
   public static String tourCatalog_wizard_Column_distance_tooltip;
   public static String tourCatalog_wizard_Column_h;
   public static String tourCatalog_wizard_Column_h_tooltip;
   public static String tourCatalog_wizard_Column_tour;
   public static String tourCatalog_wizard_Error_tour_must_be_selected;
   public static String tourCatalog_wizard_Group_selected_tour;
   public static String tourCatalog_wizard_Group_selected_tour_2;
   public static String tourCatalog_wizard_Label_a_tour_is_not_selected;
   public static String tourCatalog_wizard_Label_page_message;
   public static String tourCatalog_wizard_Page_compared_tours_title;
   public static String tourCatalog_wizard_Wizard_title;

   public static String TourCatalog_View_Action_NavigateNextTour;
   public static String TourCatalog_View_Action_NavigatePrevTour;

   public static String TourChart_GraphBackgroundSource_Default;
   public static String TourChart_GraphBackgroundSource_HrZone;
   public static String TourChart_GraphBackgroundSource_SwimmingStyle;
   public static String TourChart_GraphBackgroundStyle_GraphColor_Top;
   public static String TourChart_GraphBackgroundStyle_NoGradient;
   public static String TourChart_GraphBackgroundStyle_White_Bottom;
   public static String TourChart_GraphBackgroundStyle_White_Top;

   public static String TourChart_Property_chart_type_bar;
   public static String TourChart_Property_chart_type_line;
   public static String TourChart_Property_ChartType_Dot;

   public static String TourChart_Property_check_customize_pace_clipping;
   public static String TourChart_Property_check_customize_value_clipping;
   public static String TourChart_Property_label_chart_type;
   public static String TourChart_Property_label_pace_speed;
   public static String TourChart_Property_label_time_slices;

   public static String TourChart_PulseGraph_DeviceBpm_Only;
   public static String TourChart_PulseGraph_DeviceBpm_2nd_RRAverage;
   public static String TourChart_PulseGraph_RRAverage_Only;
   public static String TourChart_PulseGraph_RRAverage_2nd_DeviceBpm;
   public static String TourChart_PulseGraph_RRIntervals_Only;
   public static String TourChart_PulseGraph_RRIntervals_2nd_DeviceBpm;
   public static String TourChart_PulseGraph_RRIntervals_2nd_RRAverage;

   public static String TourChart_Smoothing_Algorithm_Initial;
   public static String TourChart_Smoothing_Algorithm_Jamet;
   public static String TourChart_Smoothing_Algorithm_NoSmoothing;
   public static String TourChart_Smoothing_Checkbox_IsAltitudeSmoothing;
   public static String TourChart_Smoothing_Checkbox_IsAltitudeSmoothing_Tooltip;
   public static String TourChart_Smoothing_Checkbox_IsPulseSmoothing;
   public static String TourChart_Smoothing_Checkbox_IsPulseSmoothing_Tooltip;
   public static String TourChart_Smoothing_Checkbox_IsSyncSmoothing;
   public static String TourChart_Smoothing_Checkbox_IsSyncSmoothing_Tooltip;
   public static String TourChart_Smoothing_Dialog_SmoothAllTours_Message;
   public static String TourChart_Smoothing_Dialog_SmoothAllTours_Title;
   public static String TourChart_Smoothing_Label_GradientSmoothing;
   public static String TourChart_Smoothing_Label_GradientSmoothing_Tooltip;
   public static String TourChart_Smoothing_Label_NoSmoothingAlgorithm;
   public static String TourChart_Smoothing_Label_RepeatedSmoothing;
   public static String TourChart_Smoothing_Label_RepeatedSmoothing_Tooltip;
   public static String TourChart_Smoothing_Label_RepeatedTau;
   public static String TourChart_Smoothing_Label_RepeatedTau_Tooltip;
   public static String TourChart_Smoothing_Label_SmoothingAlgorithm;
   public static String TourChart_Smoothing_Label_SpeedSmoothing;
   public static String TourChart_Smoothing_Label_SpeedSmoothing_Tooltip;
   public static String TourChart_Smoothing_Label_TauParameter;
   public static String TourChart_Smoothing_Link_PrefBreakTime;
   public static String TourChart_Smoothing_Link_SmoothingOnlineDocumentation;

   public static String TourData_Label_new_marker;

   public static String TourDataEditorView_tour_editor_status_tour_contains_ref_tour;

   public static String TourEditor_Action_ComputeDistanceValuesFromGeoPosition;
   public static String TourEditor_Action_DeleteDistanceValues;
   public static String TourEditor_Action_EditTimeSlicesValues;
   public static String TourEditor_Action_RemoveSwimStyle;
   public static String TourEditor_Action_SetAltitudeValuesFromSRTM;
   public static String TourEditor_Action_SetStartDistanceTo0;
   public static String TourEditor_Action_SetSwimStyle;

   public static String TourEditor_Dialog_ComputeDistanceValues_Message;
   public static String TourEditor_Dialog_ComputeDistanceValues_Title;
   public static String TourEditor_Dialog_DeleteDistanceValues_Message;
   public static String TourEditor_Dialog_DeleteDistanceValues_Title;
   public static String TourEditor_Dialog_SetAltitudeFromSRTM_Message;
   public static String TourEditor_Dialog_SetAltitudeFromSRTM_Title;

   public static String TourGeoFilter_Loader_Loading;
   public static String TourGeoFilter_Loader_LoadingError;
   public static String TourGeoFilter_Loader_LoadingParts;
   public static String TourGeoFilter_Loader_Tours;

   public static String Training_Action_EditHrZones;
   public static String Training_Action_EditHrZones_Tooltip;

   public static String Training_HRZone_Label_Header_Zone;

   public static String Training_View_Action_ShowAllPulseValues;
   public static String Training_View_Action_SynchChartScale;
   public static String Training_View_Label_LeftChartBorder;
   public static String Training_View_Label_LeftChartBorder_Tooltip;
   public static String Training_View_Label_NoHrZones;
   public static String Training_View_Label_NoPulseData;
   public static String Training_View_Label_RightChartBorder;
   public static String Training_View_Label_RightChartBorder_Tooltip;
   public static String Training_View_Link_NoHrZones;

   public static String UI_Label_BrowserCannotBeCreated;
   public static String UI_Label_BrowserCannotBeCreated_Error;
   public static String UI_Label_no_chart_is_selected;
   public static String UI_Label_PersonIsRequired;
   public static String UI_Label_TourIsNotSelected;

   public static String ui_tour_not_defined;

   public static String Year_Statistic_Combo_LastYears_Tooltip;
   public static String Year_Statistic_Combo_NumberOfYears_Tooltip;
   public static String Year_Statistic_Label_NumberOfYears;

   static {
      // initialize resource bundle
      NLS.initializeMessages(BUNDLE_NAME, Messages.class);
   }

   private Messages() {}
}

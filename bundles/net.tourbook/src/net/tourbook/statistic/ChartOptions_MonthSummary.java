/*******************************************************************************
 * Copyright (C) 2005, 2020 Wolfgang Schramm and Contributors
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
package net.tourbook.statistic;

import de.byteholder.geoclipse.map.UI;

import net.tourbook.Messages;
import net.tourbook.application.TourbookPlugin;
import net.tourbook.chart.ChartDataSerie;
import net.tourbook.common.util.Util;
import net.tourbook.preferences.ITourbookPreferences;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

public class ChartOptions_MonthSummary implements IStatisticOptions {

   private final IPreferenceStore _prefStore = TourbookPlugin.getPrefStore();

   private SelectionAdapter       _defaultSelectionListener;

   /*
    * UI controls
    */
   private Button _chkShowElevationUp;
   private Button _chkShowDistance;
   private Button _chkShowDurationTime;
   private Button _chkShowNumberOfTours;
   private Button _chkShowYearSeparator;
   private Button _chkTooltip_ShowPercentageValues;
   private Button _chkTooltip_ShowSummaryValues;

   private Button _rdoChartType_BarAdjacent;
   private Button _rdoChartType_BarStacked;
   private Button _rdoDuration_ElapsedTime;
   private Button _rdoDuration_RecordedTime;
   private Button _rdoDuration_PausedTime;
   private Button _rdoDuration_MovingTime;
   private Button _rdoDuration_BreakTime;

   @Override
   public void createUI(final Composite parent) {

      initUI(parent);

      createUI_100_Graphs(parent);
      createUI_200_StatisticTooltip(parent);
      createUI_300_ChartType(parent);
   }

   private void createUI_100_Graphs(final Composite parent) {

      final Group group = new Group(parent, SWT.NONE);
      group.setText(Messages.Pref_Statistic_Group_MonthSummary);
      GridDataFactory.fillDefaults()
            .grab(true, false)
            .span(2, 1)
            .applyTo(group);
      GridLayoutFactory.swtDefaults().numColumns(1).applyTo(group);
//      group.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GREEN));
      {
         createUI_110_Left(group);
         createUI_120_Right(group);
      }
   }

   private void createUI_110_Left(final Composite parent) {

      final Composite container = new Composite(parent, SWT.NONE);
      GridDataFactory.fillDefaults().grab(true, false).applyTo(container);
      GridLayoutFactory.fillDefaults().numColumns(1).applyTo(container);
      {
         {
            /*
             * Show distance
             */
            _chkShowDistance = new Button(container, SWT.CHECK);
            _chkShowDistance.setText(Messages.Pref_Statistic_Checkbox_Distance);
            _chkShowDistance.addSelectionListener(_defaultSelectionListener);
         }
         {
            /*
             * Show altitude
             */
            _chkShowElevationUp = new Button(container, SWT.CHECK);
            _chkShowElevationUp.setText(Messages.Pref_Statistic_Checkbox_Altitude);
            _chkShowElevationUp.addSelectionListener(_defaultSelectionListener);
         }
         {
            /*
             * Show time
             */
            _chkShowDurationTime = new Button(container, SWT.CHECK);
            _chkShowDurationTime.setText(Messages.Pref_Statistic_Checkbox_Duration);
            _chkShowDurationTime.addSelectionListener(_defaultSelectionListener);

            /*
             * Elapsed, recorded, paused, moving and break time
             */
            final Composite timeContainer = new Composite(container, SWT.NONE);
            GridDataFactory.fillDefaults()
                  .grab(true, false)
                  .indent(16, 0)
                  .applyTo(timeContainer);
            GridLayoutFactory.fillDefaults().numColumns(2).applyTo(timeContainer);
            {
               // row: 1
               {
                  /*
                   * Elapsed time
                   */
                  _rdoDuration_ElapsedTime = new Button(timeContainer, SWT.RADIO);
                  _rdoDuration_ElapsedTime.setText(Messages.Pref_Statistic_Radio_Duration_ElapsedTime);
                  _rdoDuration_ElapsedTime.addSelectionListener(_defaultSelectionListener);
               }
               {
                  // spacer
                  new Label(timeContainer, SWT.NONE);
               }

               // row: 2
               {
                  /*
                   * Recorded time
                   */
                  _rdoDuration_RecordedTime = new Button(timeContainer, SWT.RADIO);
                  _rdoDuration_RecordedTime.setText(Messages.Pref_Statistic_Radio_Duration_RecordedTime);
                  _rdoDuration_RecordedTime.addSelectionListener(_defaultSelectionListener);
               }

               {
                  /*
                   * Moving time
                   */
                  _rdoDuration_MovingTime = new Button(timeContainer, SWT.RADIO);
                  _rdoDuration_MovingTime.setText(Messages.Pref_Statistic_Radio_Duration_MovingTime);
                  _rdoDuration_MovingTime.addSelectionListener(_defaultSelectionListener);
               }

               // row: 3
               {
                  /*
                   * Paused time
                   */
                  _rdoDuration_PausedTime = new Button(timeContainer, SWT.RADIO);
                  _rdoDuration_PausedTime.setText(Messages.Pref_Statistic_Radio_Duration_PausedTime);
                  _rdoDuration_PausedTime.addSelectionListener(_defaultSelectionListener);
               }
               {
                  /*
                   * Break time
                   */
                  _rdoDuration_BreakTime = new Button(timeContainer, SWT.RADIO);
                  _rdoDuration_BreakTime.setText(Messages.Pref_Statistic_Radio_Duration_BreakTime);
                  _rdoDuration_BreakTime.addSelectionListener(_defaultSelectionListener);
               }
            }
            // set tab order that device and computed times are grouped together, it's the first time to use this feature :-)
            final Control[] tabList = {

                  _rdoDuration_ElapsedTime,
                  _rdoDuration_RecordedTime,
                  _rdoDuration_PausedTime,

                  _rdoDuration_MovingTime,
                  _rdoDuration_BreakTime,
            };
            timeContainer.setTabList(tabList);

         }
         {
            /*
             * Show number of tours
             */
            _chkShowNumberOfTours = new Button(container, SWT.CHECK);
            _chkShowNumberOfTours.setText(Messages.Pref_Statistic_Checkbox_NumberOfTours);
            _chkShowNumberOfTours.addSelectionListener(_defaultSelectionListener);
         }
      }
   }

   private void createUI_120_Right(final Composite parent) {

      final Composite container = new Composite(parent, SWT.NONE);
      GridDataFactory.fillDefaults().grab(true, false).applyTo(container);
      GridLayoutFactory.fillDefaults().numColumns(1).applyTo(container);
      {
         {
            /*
             * Show year separator
             */
            _chkShowYearSeparator = new Button(container, SWT.CHECK);
            _chkShowYearSeparator.setText(Messages.Pref_Statistic_Checkbox_YearSeparator);
            _chkShowYearSeparator.addSelectionListener(_defaultSelectionListener);
         }
      }
   }

   private void createUI_200_StatisticTooltip(final Composite parent) {

      final Group group = new Group(parent, SWT.NONE);
      group.setText(Messages.Pref_Statistic_Group_StatisticTooltip);
      GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(group);
      GridLayoutFactory.swtDefaults().numColumns(2).applyTo(group);
      {
         {
            /*
             * Show total values
             */
            _chkTooltip_ShowSummaryValues = new Button(group, SWT.CHECK);
            _chkTooltip_ShowSummaryValues.setText(Messages.Pref_Statistic_Checkbox_ShowSummaryValues);
            _chkTooltip_ShowSummaryValues.addSelectionListener(_defaultSelectionListener);
         }
         {
            /*
             * Show % values
             */
            _chkTooltip_ShowPercentageValues = new Button(group, SWT.CHECK);
            _chkTooltip_ShowPercentageValues.setText(Messages.Pref_Statistic_Checkbox_ShowPercentageValues);
//          tooltip: Percentage of the bar value to the total value
            _chkTooltip_ShowPercentageValues.addSelectionListener(_defaultSelectionListener);
         }
      }
   }

   private void createUI_300_ChartType(final Composite parent) {

      final Group group = new Group(parent, SWT.NONE);
      group.setText(Messages.Pref_Statistic_Group_ChartType);
      GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(group);
      GridLayoutFactory.swtDefaults().numColumns(2).applyTo(group);
//      group.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GREEN));
      {
         {
            /*
             * Bar adjacent
             */
            _rdoChartType_BarAdjacent = new Button(group, SWT.RADIO);
            _rdoChartType_BarAdjacent.setText(Messages.Pref_Statistic_Radio_BarAdjacent);
            _rdoChartType_BarAdjacent.addSelectionListener(_defaultSelectionListener);
         }
         {
            /*
             * Bar adjacent
             */
            _rdoChartType_BarStacked = new Button(group, SWT.RADIO);
            _rdoChartType_BarStacked.setText(Messages.Pref_Statistic_Radio_BarStacked);
            _rdoChartType_BarStacked.addSelectionListener(_defaultSelectionListener);
         }
      }
   }

   private void enableControls() {

      final boolean isShowDuration = _chkShowDurationTime.getSelection();

      _rdoDuration_MovingTime.setEnabled(isShowDuration);
      _rdoDuration_BreakTime.setEnabled(isShowDuration);
      _rdoDuration_ElapsedTime.setEnabled(isShowDuration);
      _rdoDuration_RecordedTime.setEnabled(isShowDuration);
      _rdoDuration_PausedTime.setEnabled(isShowDuration);
   }

   private void initUI(final Composite parent) {

      _defaultSelectionListener = new SelectionAdapter() {
         @Override
         public void widgetSelected(final SelectionEvent e) {
            onChangeUI();
         }
      };
   }

   private void onChangeUI() {

      // update chart async (which is done when a pref store value is modified) that the UI is updated immediately

      enableControls();

      Display.getCurrent().asyncExec(new Runnable() {
         @Override
         public void run() {

            saveState();
         }
      });
   }

   @Override
   public void resetToDefaults() {

      _chkShowElevationUp.setSelection(_prefStore.getDefaultBoolean(ITourbookPreferences.STAT_MONTH_IS_SHOW_ALTITUDE));
      _chkShowDistance.setSelection(_prefStore.getDefaultBoolean(ITourbookPreferences.STAT_MONTH_IS_SHOW_DISTANCE));
      _chkShowDurationTime.setSelection(_prefStore.getDefaultBoolean(ITourbookPreferences.STAT_MONTH_IS_SHOW_DURATION));
      _chkShowNumberOfTours.setSelection(_prefStore.getDefaultBoolean(ITourbookPreferences.STAT_MONTH_IS_SHOW_NUMBER_OF_TOURS));
      _chkShowYearSeparator.setSelection(_prefStore.getDefaultBoolean(ITourbookPreferences.STAT_MONTH_IS_SHOW_YEAR_SEPARATOR));

      _chkTooltip_ShowPercentageValues.setSelection(_prefStore.getDefaultBoolean(ITourbookPreferences.STAT_MONTH_TOOLTIP_IS_SHOW_PERCENTAGE_VALUES));
      _chkTooltip_ShowSummaryValues.setSelection(_prefStore.getDefaultBoolean(ITourbookPreferences.STAT_MONTH_TOOLTIP_IS_SHOW_SUMMARY_VALUES));

      final String chartType = _prefStore.getDefaultString(ITourbookPreferences.STAT_MONTH_CHART_TYPE);
      _rdoChartType_BarAdjacent.setSelection(chartType.equals(ChartDataSerie.CHART_TYPE_BAR_ADJACENT));
      _rdoChartType_BarStacked.setSelection(chartType.equals(ChartDataSerie.CHART_TYPE_BAR_STACKED));

      final Enum<DurationTime> durationTime = Util.getEnumValue(
            _prefStore.getDefaultString(ITourbookPreferences.STAT_MONTH_DURATION_TIME),
            DurationTime.MOVING);
      _rdoDuration_BreakTime.setSelection(durationTime.equals(DurationTime.BREAK));
      _rdoDuration_MovingTime.setSelection(durationTime.equals(DurationTime.MOVING));
      _rdoDuration_ElapsedTime.setSelection(durationTime.equals(DurationTime.ELAPSED));
      _rdoDuration_RecordedTime.setSelection(durationTime.equals(DurationTime.RECORDED));
      _rdoDuration_PausedTime.setSelection(durationTime.equals(DurationTime.PAUSED));

      enableControls();
   }

   @Override
   public void restoreState() {

      _chkShowElevationUp.setSelection(_prefStore.getBoolean(ITourbookPreferences.STAT_MONTH_IS_SHOW_ALTITUDE));
      _chkShowDistance.setSelection(_prefStore.getBoolean(ITourbookPreferences.STAT_MONTH_IS_SHOW_DISTANCE));
      _chkShowDurationTime.setSelection(_prefStore.getBoolean(ITourbookPreferences.STAT_MONTH_IS_SHOW_DURATION));
      _chkShowNumberOfTours.setSelection(_prefStore.getBoolean(ITourbookPreferences.STAT_MONTH_IS_SHOW_NUMBER_OF_TOURS));

      _chkTooltip_ShowPercentageValues.setSelection(_prefStore.getBoolean(ITourbookPreferences.STAT_MONTH_TOOLTIP_IS_SHOW_PERCENTAGE_VALUES));
      _chkTooltip_ShowSummaryValues.setSelection(_prefStore.getBoolean(ITourbookPreferences.STAT_MONTH_TOOLTIP_IS_SHOW_SUMMARY_VALUES));

      _chkShowYearSeparator.setSelection(_prefStore.getBoolean(ITourbookPreferences.STAT_MONTH_IS_SHOW_YEAR_SEPARATOR));

      final String chartType = _prefStore.getString(ITourbookPreferences.STAT_MONTH_CHART_TYPE);
      _rdoChartType_BarAdjacent.setSelection(chartType.equals(ChartDataSerie.CHART_TYPE_BAR_ADJACENT));
      _rdoChartType_BarStacked.setSelection(chartType.equals(ChartDataSerie.CHART_TYPE_BAR_STACKED));

      final Enum<DurationTime> durationTime = Util.getEnumValue(
            _prefStore.getString(ITourbookPreferences.STAT_MONTH_DURATION_TIME),
            DurationTime.MOVING);
      _rdoDuration_BreakTime.setSelection(durationTime.equals(DurationTime.BREAK));
      _rdoDuration_MovingTime.setSelection(durationTime.equals(DurationTime.MOVING));
      _rdoDuration_ElapsedTime.setSelection(durationTime.equals(DurationTime.ELAPSED));
      _rdoDuration_RecordedTime.setSelection(durationTime.equals(DurationTime.RECORDED));
      _rdoDuration_PausedTime.setSelection(durationTime.equals(DurationTime.PAUSED));

      enableControls();
   }

   @Override
   public void saveState() {

      _prefStore.setValue(ITourbookPreferences.STAT_MONTH_IS_SHOW_ALTITUDE, _chkShowElevationUp.getSelection());
      _prefStore.setValue(ITourbookPreferences.STAT_MONTH_IS_SHOW_DISTANCE, _chkShowDistance.getSelection());
      _prefStore.setValue(ITourbookPreferences.STAT_MONTH_IS_SHOW_DURATION, _chkShowDurationTime.getSelection());
      _prefStore.setValue(ITourbookPreferences.STAT_MONTH_IS_SHOW_NUMBER_OF_TOURS, _chkShowNumberOfTours.getSelection());

      _prefStore.setValue(ITourbookPreferences.STAT_MONTH_TOOLTIP_IS_SHOW_PERCENTAGE_VALUES, _chkTooltip_ShowPercentageValues.getSelection());
      _prefStore.setValue(ITourbookPreferences.STAT_MONTH_TOOLTIP_IS_SHOW_SUMMARY_VALUES, _chkTooltip_ShowSummaryValues.getSelection());

      _prefStore.setValue(ITourbookPreferences.STAT_MONTH_IS_SHOW_YEAR_SEPARATOR, _chkShowYearSeparator.getSelection());

      _prefStore.setValue(ITourbookPreferences.STAT_MONTH_CHART_TYPE,

            _rdoChartType_BarAdjacent.getSelection()
                  ? ChartDataSerie.CHART_TYPE_BAR_ADJACENT
                  : ChartDataSerie.CHART_TYPE_BAR_STACKED);

      String selectedDurationTime = UI.EMPTY_STRING;
      if (_rdoDuration_BreakTime.getSelection()) {
         selectedDurationTime = DurationTime.BREAK.name();
      } else if (_rdoDuration_MovingTime.getSelection()) {
         selectedDurationTime = DurationTime.MOVING.name();
      } else if (_rdoDuration_RecordedTime.getSelection()) {
         selectedDurationTime = DurationTime.RECORDED.name();
      } else if (_rdoDuration_PausedTime.getSelection()) {
         selectedDurationTime = DurationTime.PAUSED.name();
      } else if (_rdoDuration_ElapsedTime.getSelection()) {
         selectedDurationTime = DurationTime.ELAPSED.name();
      }
      _prefStore.setValue(ITourbookPreferences.STAT_MONTH_DURATION_TIME, selectedDurationTime);
   }
}

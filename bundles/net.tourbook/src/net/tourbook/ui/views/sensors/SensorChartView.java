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
package net.tourbook.ui.views.sensors;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;

import net.tourbook.Images;
import net.tourbook.Messages;
import net.tourbook.application.TourbookPlugin;
import net.tourbook.chart.Chart;
import net.tourbook.chart.ChartDataModel;
import net.tourbook.chart.ChartDataSerie;
import net.tourbook.chart.ChartDataXSerie;
import net.tourbook.chart.ChartDataYSerie;
import net.tourbook.chart.ChartType;
import net.tourbook.chart.DelayedBarSelection_TourToolTip;
import net.tourbook.chart.IChartInfoProvider;
import net.tourbook.common.time.TimeTools;
import net.tourbook.common.util.IToolTipProvider;
import net.tourbook.common.util.PostSelectionProvider;
import net.tourbook.common.util.Util;
import net.tourbook.data.TourData;
import net.tourbook.tour.SelectionTourId;
import net.tourbook.tour.TourEventId;
import net.tourbook.tour.TourInfoIconToolTipProvider;
import net.tourbook.tour.TourInfoUI;
import net.tourbook.tour.TourManager;
import net.tourbook.ui.ITourProvider;
import net.tourbook.ui.UI;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.ViewPart;

/**
 * Shows the selected sensor in a chart
 */
public class SensorChartView extends ViewPart implements ITourProvider {

   public static final String              ID                       = "net.tourbook.ui.views.sensors.SensorChartView.ID"; //$NON-NLS-1$

   private final IPreferenceStore          _prefStore               = TourbookPlugin.getPrefStore();
   private final IDialogSettings           _state                   = TourbookPlugin.getState(ID);

   private IPartListener2                  _partListener;
   private PostSelectionProvider           _postSelectionProvider;
   private ISelectionListener              _postSelectionListener;

   private FormToolkit                     _tk;

   private SensorData                      _sensorData;
   private SensorDataProvider              _sensorDataProvider      = new SensorDataProvider();

   private Long                            _selectedTourId;

   private ActionXAxis                     _actionXAxis;

   private DelayedBarSelection_TourToolTip _tourToolTip;
   private TourInfoIconToolTipProvider     _tourInfoToolTipProvider = new TourInfoIconToolTipProvider();
   private TourInfoUI                      _tourInfoUI              = new TourInfoUI();

   /*
    * UI controls
    */
   private PageBook  _pageBook;

   private Composite _pageNoData;
   private Composite _pageNoBatteryData;

   private Chart     _sensorChart;

   private class ActionXAxis extends Action {

      public ActionXAxis() {

         super(Messages.Tour_Action_show_time_on_x_axis, AS_RADIO_BUTTON);

         setToolTipText(Messages.Tour_Action_show_time_on_x_axis_tooltip);
         setImageDescriptor(TourbookPlugin.getThemedImageDescriptor(Images.XAxis_ShowTime));
      }

      @Override
      public void run() {
         onAction_XAxis();
      }
   }

   private void addPartListener() {

      _partListener = new IPartListener2() {

         @Override
         public void partActivated(final IWorkbenchPartReference partRef) {}

         @Override
         public void partBroughtToTop(final IWorkbenchPartReference partRef) {}

         @Override
         public void partClosed(final IWorkbenchPartReference partRef) {}

         @Override
         public void partDeactivated(final IWorkbenchPartReference partRef) {}

         @Override
         public void partHidden(final IWorkbenchPartReference partRef) {}

         @Override
         public void partInputChanged(final IWorkbenchPartReference partRef) {}

         @Override
         public void partOpened(final IWorkbenchPartReference partRef) {}

         @Override
         public void partVisible(final IWorkbenchPartReference partRef) {}
      };

      getViewSite().getPage().addPartListener(_partListener);
   }

   /**
    * listen for events when a tour is selected
    */
   private void addSelectionListener() {

      _postSelectionListener = new ISelectionListener() {
         @Override
         public void selectionChanged(final IWorkbenchPart part, final ISelection selection) {

            if (part == SensorChartView.this) {
               return;
            }

            onSelectionChanged(selection);
         }
      };
      getSite().getPage().addPostSelectionListener(_postSelectionListener);
   }

   private void createActions() {

      _actionXAxis = new ActionXAxis();

      fillToolbar();
   }

   @Override
   public void createPartControl(final Composite parent) {

      createUI(parent);
      createActions();

      restoreState();

      addSelectionListener();
      addPartListener();

      // set this view part as selection provider
      getSite().setSelectionProvider(_postSelectionProvider = new PostSelectionProvider(ID));
   }

   /**
    * @param toolTipProvider
    * @param parent
    * @param hoveredBar_VerticalIndex
    *           serieIndex
    * @param hoveredBar_HorizontalIndex
    *           valueIndex
    */
   private void createToolTipUI(final IToolTipProvider toolTipProvider,
                                final Composite parent,
                                final int serieIndex,
                                final int valueIndex) {

      final long tourId = _sensorData.allTourIds[valueIndex];

      TourData _tourData = null;
      if (tourId != -1) {

         // first get data from the tour id when it is set
         _tourData = TourManager.getInstance().getTourData(tourId);
      }

      if (_tourData == null) {

         // there are no data available

         _tourInfoUI.createUI_NoData(parent);

      } else {

         // tour data is available

         _tourInfoUI.createContentArea(parent, _tourData, toolTipProvider, this);

         _tourInfoUI.setActionsEnabled(true);
      }

      parent.addDisposeListener(new DisposeListener() {
         @Override
         public void widgetDisposed(final DisposeEvent e) {
            _tourInfoUI.dispose();
         }
      });
   }

   private void createUI(final Composite parent) {

      initUI(parent);

      _pageBook = new PageBook(parent, SWT.NONE);

      _pageNoData = UI.createPage(_tk, _pageBook, Messages.Sensor_Chart_Label_SensorIsNotSelected);
      _pageNoBatteryData = UI.createPage(_tk, _pageBook, Messages.Sensor_Chart_Label_SensorWithBatteryValuesIsNotSelected);

      _sensorChart = createUI_10_Chart();

      _pageBook.showPage(_pageNoData);
   }

   private Chart createUI_10_Chart() {

      final Chart sensorChart = new Chart(_pageBook, SWT.FLAT);
      sensorChart.setShowZoomActions(true);
      sensorChart.setMouseMode(true);

      sensorChart.setToolBarManager(getViewSite().getActionBars().getToolBarManager(), true);

      sensorChart.addBarSelectionListener((serieIndex, valueIndex) -> {

         final long[] tourIds = _sensorData.allTourIds;

         if (tourIds != null && tourIds.length > 0) {

            if (valueIndex >= tourIds.length) {
               valueIndex = tourIds.length - 1;
            }

            _selectedTourId = tourIds[valueIndex];
            _tourInfoToolTipProvider.setTourId(_selectedTourId);

            // don't fire an event when preferences are updated
//               if (isInPreferencesUpdate() || _statContext.canFireEvents() == false) {
//                  return;
//               }

            // this view can be inactive -> selection is not fired with the SelectionProvider interface
            TourManager.fireEventWithCustomData(
                  TourEventId.TOUR_SELECTION,
                  new SelectionTourId(_selectedTourId),
                  getViewSite().getPart());
         }
      });

      /*
       * Set tour info icon into the left axis
       */
      _tourToolTip = new DelayedBarSelection_TourToolTip(sensorChart.getToolTipControl());
      _tourToolTip.addToolTipProvider(_tourInfoToolTipProvider);

      // hide hovered image
      _tourToolTip.addHideListener(event -> sensorChart.getToolTipControl().afterHideToolTip());

      sensorChart.setTourInfoIconToolTipProvider(_tourInfoToolTipProvider);
      _tourInfoToolTipProvider.setActionsEnabled(true);

      return sensorChart;
   }

   @Override
   public void dispose() {

      saveState();

      if (_tk != null) {
         _tk.dispose();
      }

      getSite().getPage().removePostSelectionListener(_postSelectionListener);
      getViewSite().getPage().removePartListener(_partListener);

      super.dispose();
   }

   /*
    * Fill view toolbar
    */
   private void fillToolbar() {

      final IToolBarManager tbm = getViewSite().getActionBars().getToolBarManager();

      tbm.add(_actionXAxis);

      // update that actions are fully created otherwise action enable will fail
      tbm.update(true);
   }

   @Override
   public ArrayList<TourData> getSelectedTours() {

      if (_selectedTourId == null) {
         return null;
      }

      final ArrayList<TourData> selectedTours = new ArrayList<>();

      selectedTours.add(TourManager.getInstance().getTourData(_selectedTourId));

      return selectedTours;
   }

   /**
    * @param tourStartTime
    * @return Returns the tour start date time with the tour time zone, when not available with the
    *         default time zone.
    */

   private ZonedDateTime getTourStartTime(final long tourStartTime) {

      final Instant tourStartMills = Instant.ofEpochMilli(tourStartTime);
      final ZoneId tourStartTimeZoneId = TimeTools.getDefaultTimeZone();

      final ZonedDateTime zonedStartTime = ZonedDateTime.ofInstant(tourStartMills, tourStartTimeZoneId);

      return zonedStartTime;
   }

   private void initUI(final Composite parent) {

      _tk = new FormToolkit(parent.getDisplay());
   }

   private void onAction_XAxis() {
      // TODO Auto-generated method stub

   }

   private void onSelectionChanged(final ISelection selection) {

      if (selection instanceof StructuredSelection) {

         final Object firstElement = ((StructuredSelection) selection).getFirstElement();

         if (firstElement instanceof SensorView.SensorItem) {

            // show selected sensor

            final SensorView.SensorItem sensorItem = (SensorView.SensorItem) firstElement;

            final long sensorId = sensorItem.sensor.getSensorId();

            _sensorData = _sensorDataProvider.getTourTimeData(sensorId);

            if (_sensorData.allTourIds.length == 0) {

               _pageBook.showPage(_pageNoBatteryData);

            } else {

               _pageBook.showPage(_sensorChart);
               updateChart(_sensorData);
            }
         }
      }
   }

   private void restoreState() {

   }

   private void saveState() {

   }

   private void setChartProviders(final Chart chartWidget, final ChartDataModel chartModel) {

      final IChartInfoProvider chartInfoProvider = new IChartInfoProvider() {

         @Override
         public void createToolTipUI(final IToolTipProvider toolTipProvider, final Composite parent, final int serieIndex, final int valueIndex) {
            SensorChartView.this.createToolTipUI(toolTipProvider, parent, serieIndex, valueIndex);
         }
      };

      chartModel.setCustomData(ChartDataModel.BAR_TOOLTIP_INFO_PROVIDER, chartInfoProvider);

      // set the menu context provider
//      chartModel.setCustomData(ChartDataModel.BAR_CONTEXT_PROVIDER, new TourChartContextProvider(_sensorChart, this));
   }

   @Override
   public void setFocus() {

      _sensorChart.setFocus();
   }

   private void updateChart(final SensorData sensorData) {

      final ChartDataModel chartModel = new ChartDataModel(ChartType.BAR);

      // set the x-axis
      final ChartDataXSerie xData = new ChartDataXSerie(Util.convertIntToDouble(sensorData.allXValues_ByTime));
      xData.setAxisUnit(ChartDataSerie.X_AXIS_UNIT_HISTORY);
      xData.setStartDateTime(getTourStartTime(sensorData.firstDateTime));
      chartModel.setXData(xData);

      // set  bar low/high data
      final ChartDataYSerie yData = new ChartDataYSerie(
            ChartType.BAR,
            sensorData.allBatteryVoltage_End,
            sensorData.allBatteryVoltage_Start);

      yData.setYTitle("Sensor Battery");
      yData.setUnitLabel("Volt");
      yData.setShowYSlider(true);

      chartModel.addYData(yData);

      // set dummy title that the history labels are not truncated
      chartModel.setTitle(UI.SPACE);

      setChartProviders(_sensorChart, chartModel);

      // show the data in the chart
      _sensorChart.updateChart(chartModel, false, true);
   }
}

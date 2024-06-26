/*******************************************************************************
 * Copyright (C) 2023, 2024 Wolfgang Schramm and Contributors
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
package net.tourbook.ui.views;

import static org.eclipse.swt.events.KeyListener.keyPressedAdapter;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import net.tourbook.Messages;
import net.tourbook.OtherMessages;
import net.tourbook.application.TourbookPlugin;
import net.tourbook.chart.SelectionChartXSliderPosition;
import net.tourbook.common.CommonActivator;
import net.tourbook.common.UI;
import net.tourbook.common.preferences.ICommonPreferences;
import net.tourbook.common.time.TimeTools;
import net.tourbook.common.util.ColumnDefinition;
import net.tourbook.common.util.ColumnDefinitionFor1stVisibleAlignmentColumn;
import net.tourbook.common.util.ColumnManager;
import net.tourbook.common.util.IContextMenuProvider;
import net.tourbook.common.util.ITourViewer;
import net.tourbook.common.util.PostSelectionProvider;
import net.tourbook.common.util.TableColumnDefinition;
import net.tourbook.data.TourData;
import net.tourbook.database.TourDatabase;
import net.tourbook.map2.view.SelectionMapPosition;
import net.tourbook.preferences.ITourbookPreferences;
import net.tourbook.tour.ActionDeletePausesDialog;
import net.tourbook.tour.ITourEventListener;
import net.tourbook.tour.SelectionDeletedTours;
import net.tourbook.tour.SelectionTourData;
import net.tourbook.tour.SelectionTourId;
import net.tourbook.tour.SelectionTourIds;
import net.tourbook.tour.SelectionTourPause;
import net.tourbook.tour.TourEvent;
import net.tourbook.tour.TourEventId;
import net.tourbook.tour.TourManager;
import net.tourbook.ui.ITourProvider;
import net.tourbook.ui.action.SubMenu_SetPausesType;
import net.tourbook.ui.tourChart.TourChart;
import net.tourbook.ui.views.referenceTour.SelectionReferenceTourView;
import net.tourbook.ui.views.referenceTour.TVIElevationCompareResult_ComparedTour;
import net.tourbook.ui.views.referenceTour.TVIRefTour_ComparedTour;
import net.tourbook.ui.views.referenceTour.TVIRefTour_RefTourItem;

import org.apache.commons.collections4.CollectionUtils;
import org.eclipse.e4.ui.di.PersistState;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.ViewPart;

public class TourPausesView extends ViewPart implements ITourProvider, ITourViewer {

   public static final String       ID                              = "net.tourbook.ui.views.TourPausesView"; //$NON-NLS-1$

   private final IPreferenceStore   _prefStore                      = TourbookPlugin.getPrefStore();
   private final IPreferenceStore   _prefStore_Common               = CommonActivator.getPrefStore();
   private final IDialogSettings    _state                          = TourbookPlugin.getState(ID);

   private PostSelectionProvider    _postSelectionProvider;
   private ISelectionListener       _postSelectionListener;
   private IPropertyChangeListener  _prefChangeListener;
   private IPropertyChangeListener  _prefChangeListener_Common;
   private ITourEventListener       _tourEventListener;

   private TourData                 _tourData;

   private MenuManager              _viewerMenuManager;
   private IContextMenuProvider     _tableViewerContextMenuProvider = new TableContextMenuProvider();

   private ColumnManager            _columnManager;

   private ArrayList<DevicePause>   _allDevicePauses;

   private boolean                  _isInUpdate;

   private PixelConverter           _pc;

   private TableViewer              _pausesViewer;

   private ZonedDateTime            _tourStartTime;

   private ActionDeletePausesDialog _actionDeleteTourPauses;
   private SubMenu_SetPausesType    _subMenu_SetPauseType;

   /*
    * UI controls
    */
   private PageBook  _pageBook;

   private Composite _pageNoData;
   private Composite _viewerContainer;

   private Menu      _tableContextMenu;

   private class DevicePause {

      private long type;

      private long relativeStartTime;
      private long relativeEndTime;

      private int  serieIndex_Start;
      private int  serieIndex_End;

      private DevicePause(final long type,
                          final long relativeStartTime,
                          final long relativeEndTime,
                          final int serieIndex_Start,
                          final int serieIndex_End) {

         this.type = type;

         this.relativeStartTime = relativeStartTime;
         this.relativeEndTime = relativeEndTime;

         this.serieIndex_Start = serieIndex_Start;
         this.serieIndex_End = serieIndex_End;
      }
   }

   /**
    * Sort pauses by time
    */
   private class PausesViewer_Comparator extends ViewerComparator {

      @Override
      public int compare(final Viewer viewer, final Object obj1, final Object obj2) {

         return (int) (((DevicePause) (obj1)).relativeStartTime - ((DevicePause) (obj2)).relativeStartTime);
      }
   }

   private class PausesViewer_ContentProvider implements IStructuredContentProvider {

      @Override
      public void dispose() {}

      @Override
      public Object[] getElements(final Object inputElement) {

         return _tourData == null

               ? new Object[0]
               : _allDevicePauses.toArray();
      }

      @Override
      public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {}
   }

   private class TableContextMenuProvider implements IContextMenuProvider {

      @Override
      public void disposeContextMenu() {

         if (_tableContextMenu != null) {
            _tableContextMenu.dispose();
         }
      }

      @Override
      public Menu getContextMenu() {

         return _pausesViewer.getTable().getSelectionCount() > 0
               ? _tableContextMenu : null;
      }

      @Override
      public Menu recreateContextMenu() {

         disposeContextMenu();

         _tableContextMenu = createUI_22_CreateViewerContextMenu();

         return _tableContextMenu;
      }

   }

   public TourPausesView() {
      super();
   }

   private void addPrefListener() {

      _prefChangeListener = propertyChangeEvent -> {

         final String property = propertyChangeEvent.getProperty();

         if (property.equals(ITourbookPreferences.VIEW_LAYOUT_CHANGED)) {

            _pausesViewer.getTable().setLinesVisible(_prefStore.getBoolean(ITourbookPreferences.VIEW_LAYOUT_DISPLAY_LINES));
            _pausesViewer.refresh();

         } else if (property.equals(ITourbookPreferences.TOURMARKERVIEW_USE_ELAPSED_TIME) ||
               property.equals(ITourbookPreferences.TOURMARKERVIEW_USE_MOVING_TIME) ||
               property.equals(ITourbookPreferences.TOURMARKERVIEW_USE_RECORDED_TIME)) {

            refreshView();
         }
      };

      _prefChangeListener_Common = propertyChangeEvent -> {

         final String property = propertyChangeEvent.getProperty();

         if (property.equals(ICommonPreferences.MEASUREMENT_SYSTEM)) {

            // measurement system has changed

            refreshView();
         }
      };

      _prefStore.addPropertyChangeListener(_prefChangeListener);
      _prefStore_Common.addPropertyChangeListener(_prefChangeListener_Common);
   }

   /**
    * listen for events when a tour is selected
    */
   private void addSelectionListener() {

      _postSelectionListener = (workbenchPart, selection) -> {

         if (workbenchPart == TourPausesView.this) {
            return;
         }

         onSelectionChanged(selection);
      };
      getSite().getPage().addPostSelectionListener(_postSelectionListener);
   }

   private void addTourEventListener() {

      _tourEventListener = (workbenchPart, tourEventId, eventData) -> {

         if (_isInUpdate || workbenchPart == TourPausesView.this) {
            return;
         }

         if (tourEventId == TourEventId.TOUR_SELECTION && eventData instanceof final ISelection selection) {

            onSelectionChanged(selection);

         } else {

            if (_tourData == null) {
               return;
            }

            if (tourEventId == TourEventId.TOUR_CHANGED && eventData instanceof final TourEvent tourEvent) {

               final ArrayList<TourData> modifiedTours = tourEvent.getModifiedTours();
               if (modifiedTours != null) {

                  // update modified tour

                  final long viewTourId = _tourData.getTourId();

                  for (final TourData tourData : modifiedTours) {
                     if (tourData.getTourId() == viewTourId) {

                        // get modified tour
                        setupViewerContent(tourData);

                        updateUI_PausesViewer();

                        // removed old tour data from the selection provider
                        _postSelectionProvider.clearSelection();

                        // nothing more to do, the view contains only one tour
                        return;
                     }
                  }
               }

            } else if (tourEventId == TourEventId.PAUSE_SELECTION && eventData instanceof final SelectionTourPause pauseSelection) {

               onTourEvent_TourPause(pauseSelection);

            } else if (tourEventId == TourEventId.CLEAR_DISPLAYED_TOUR) {

               clearView();
            }
         }
      };

      TourManager.getInstance().addTourEventListener(_tourEventListener);
   }

   private void clearView() {

      _tourData = null;

      updateUI_PausesViewer();

      _postSelectionProvider.clearSelection();
   }

   private void createActions() {

      _subMenu_SetPauseType = new SubMenu_SetPausesType(this, false);
      _actionDeleteTourPauses = new ActionDeletePausesDialog(this);
   }

   private void createMenuManager() {

      _viewerMenuManager = new MenuManager("#PopupMenu"); //$NON-NLS-1$
      _viewerMenuManager.setRemoveAllWhenShown(true);
      _viewerMenuManager.addMenuListener(manager -> fillContextMenu(manager));
   }

   @Override
   public void createPartControl(final Composite parent) {

      _pc = new PixelConverter(parent);

      createMenuManager();

      // define all columns for the viewer
      _columnManager = new ColumnManager(this, _state);
      _columnManager.setIsCategoryAvailable(true);
      defineAllColumns();

      createUI(parent);

      addSelectionListener();
      addTourEventListener();
      addPrefListener();

      createActions();

      // this part is a selection provider
      _postSelectionProvider = new PostSelectionProvider(ID);
      getSite().setSelectionProvider(_postSelectionProvider);

      // show default page
      _pageBook.showPage(_pageNoData);

      // show marker from last selection
      onSelectionChanged(getSite().getWorkbenchWindow().getSelectionService().getSelection());

      if (_tourData == null) {
         showTourFromTourProvider();
      }
   }

   private void createUI(final Composite parent) {

      _pageBook = new PageBook(parent, SWT.NONE);

      _pageNoData = UI.createUI_PageNoData(_pageBook, Messages.UI_Label_no_chart_is_selected);

      _viewerContainer = new Composite(_pageBook, SWT.NONE);
      GridLayoutFactory.fillDefaults().applyTo(_viewerContainer);
      {
         createUI_10_TableViewer(_viewerContainer);
      }
   }

   private void createUI_10_TableViewer(final Composite parent) {

      /*
       * create table
       */
      final Table table = new Table(parent, SWT.FULL_SELECTION | SWT.MULTI /* | SWT.BORDER */);
      GridDataFactory.fillDefaults().grab(true, true).applyTo(table);

      table.setHeaderVisible(true);
      table.setLinesVisible(_prefStore.getBoolean(ITourbookPreferences.VIEW_LAYOUT_DISPLAY_LINES));

      table.addKeyListener(keyPressedAdapter(keyEvent -> {

         if (keyEvent.keyCode == SWT.DEL) {

            if (!_actionDeleteTourPauses.isEnabled()) {
               return;
            }

            final int[] selectedIndices = _pausesViewer.getTable().getSelectionIndices();
            if (selectedIndices.length == 0) {
               return;
            }

            setupPausesToDelete(selectedIndices);

            _actionDeleteTourPauses.run();
         }
      }));

      /*
       * create table viewer
       */
      _pausesViewer = new TableViewer(table);

//      // set editing support after the viewer is created but before the columns are created
//      net.tourbook.common.UI.setCellEditSupport(_markerViewer);
//
//      _colDefName.setEditingSupport(new MarkerEditingSupportLabel(_markerViewer));
//      _colDefVisibility.setEditingSupport(new MarkerEditingSupportVisibility(_markerViewer));

      _columnManager.createColumns(_pausesViewer);

      _pausesViewer.setUseHashlookup(true);
      _pausesViewer.setContentProvider(new PausesViewer_ContentProvider());
      _pausesViewer.setComparator(new PausesViewer_Comparator());

      _pausesViewer.addSelectionChangedListener(
            selectionChangedEvent -> onSelect_TourPause((StructuredSelection) selectionChangedEvent.getSelection()));

      createUI_20_ContextMenu();
   }

   /**
    * create the views context menu
    */
   private void createUI_20_ContextMenu() {

      _tableContextMenu = createUI_22_CreateViewerContextMenu();

      final Table table = (Table) _pausesViewer.getControl();

      _columnManager.createHeaderContextMenu(table, _tableViewerContextMenuProvider);
   }

   private Menu createUI_22_CreateViewerContextMenu() {

      final Table table = (Table) _pausesViewer.getControl();
      final Menu tableContextMenu = _viewerMenuManager.createContextMenu(table);

      return tableContextMenu;
   }

   private String createUI_Pause_Absolute_EndTime(final DevicePause pause) {

      return _tourStartTime.plusSeconds(pause.relativeEndTime).format(TimeTools.Formatter_Time_M);
   }

   private String createUI_Pause_Absolute_StartTime(final DevicePause pause) {

      return _tourStartTime.plusSeconds(pause.relativeStartTime).format(TimeTools.Formatter_Time_M);
   }

   private String createUI_Pause_Duration(final DevicePause pause) {

      return UI.format_hh_mm_ss(pause.relativeEndTime - pause.relativeStartTime);
   }

   private String createUI_Pause_Relative_EndTime(final DevicePause pause) {

      return UI.format_hh_mm_ss(pause.relativeEndTime);
   }

   private String createUI_Pause_Relative_StartTime(final DevicePause pause) {

      final long relativeStartTime = pause.relativeStartTime;

      final String timePrefix = relativeStartTime < 0

            // a pause can begin before the tour start time
            ? UI.SYMBOL_MINUS

            : UI.EMPTY_STRING;

      return timePrefix + UI.format_hh_mm_ss(Math.abs(relativeStartTime));
   }

   private void defineAllColumns() {

      defineColumn_PauseDuration();
      defineColumn_PauseType();

      defineColumn_Time_Relative_Start();
      defineColumn_Time_Relative_End();

      defineColumn_Time_Daytime_Start();
      defineColumn_Time_Daytime_End();

      new ColumnDefinitionFor1stVisibleAlignmentColumn(_columnManager);
   }

   /**
    * Column: Pause duration
    */
   private void defineColumn_PauseDuration() {

      final TableColumnDefinition colDef = new TableColumnDefinition(_columnManager, "pauseDuration", SWT.TRAIL); //$NON-NLS-1$

      colDef.setColumnLabel(Messages.Tour_Pauses_Column_Duration_Label);
      colDef.setColumnHeaderText(Messages.Tour_Pauses_Column_Duration_Label);
      colDef.setColumnHeaderToolTipText(Messages.Tour_Pauses_Column_Duration_Tooltip);

      colDef.setColumnCategory(OtherMessages.COLUMN_FACTORY_CATEGORY_TIME);

      colDef.setIsDefaultColumn();
      colDef.setDefaultColumnWidth(_pc.convertWidthInCharsToPixels(10));

      colDef.setLabelProvider(new CellLabelProvider() {
         @Override
         public void update(final ViewerCell cell) {

            final DevicePause pause = (DevicePause) cell.getElement();

            cell.setText(createUI_Pause_Duration(pause));
         }
      });
   }

   /**
    * Column: Pause type: automatic or manual
    */
   private void defineColumn_PauseType() {

      final TableColumnDefinition colDef = new TableColumnDefinition(_columnManager, "pauseType", SWT.TRAIL); //$NON-NLS-1$

      colDef.setColumnLabel(Messages.Tour_Pauses_Column_Type_Label);
      colDef.setColumnHeaderText(Messages.Tour_Pauses_Column_Type_Label);
      colDef.setColumnHeaderToolTipText(Messages.Tour_Pauses_Column_Type_Tooltip);

      colDef.setColumnCategory(OtherMessages.COLUMN_FACTORY_CATEGORY_DATA);

      colDef.setIsDefaultColumn();
      colDef.setDefaultColumnWidth(_pc.convertWidthInCharsToPixels(10));

      colDef.setLabelProvider(new CellLabelProvider() {
         @Override
         public void update(final ViewerCell cell) {

            final DevicePause pause = (DevicePause) cell.getElement();

            cell.setText(pause.type == 0
                  ? Messages.Tour_Pauses_Column_TypeValue_Manual
                  : Messages.Tour_Pauses_Column_TypeValue_Automatic);
         }
      });
   }

   /**
    * Column: Pause end time of day
    */
   private void defineColumn_Time_Daytime_End() {

      final TableColumnDefinition colDef = new TableColumnDefinition(_columnManager, "pauseEndTime_Daytime", SWT.LEAD); //$NON-NLS-1$

      colDef.setColumnLabel(Messages.Tour_Pauses_Column_EndTime_Daytime_Label);
      colDef.setColumnHeaderText(Messages.Tour_Pauses_Column_EndTime_Daytime_Label);
      colDef.setColumnHeaderToolTipText(Messages.Tour_Pauses_Column_EndTime_Daytime_Tooltip);

      colDef.setColumnCategory(OtherMessages.COLUMN_FACTORY_CATEGORY_TIME);

      colDef.setDefaultColumnWidth(_pc.convertWidthInCharsToPixels(10));

      colDef.setLabelProvider(new CellLabelProvider() {
         @Override
         public void update(final ViewerCell cell) {

            final DevicePause pause = (DevicePause) cell.getElement();

            cell.setText(createUI_Pause_Absolute_EndTime(pause));
         }
      });
   }

   /**
    * Column: Pause start time of day
    */
   private void defineColumn_Time_Daytime_Start() {

      final TableColumnDefinition colDef = new TableColumnDefinition(_columnManager, "pauseStartTime_Daytime", SWT.LEAD); //$NON-NLS-1$

      colDef.setColumnLabel(Messages.Tour_Pauses_Column_StartTime_Daytime_Label);
      colDef.setColumnHeaderText(Messages.Tour_Pauses_Column_StartTime_Daytime_Label);
      colDef.setColumnHeaderToolTipText(Messages.Tour_Pauses_Column_StartTime_Daytime_Tooltip);

      colDef.setColumnCategory(OtherMessages.COLUMN_FACTORY_CATEGORY_TIME);

      colDef.setDefaultColumnWidth(_pc.convertWidthInCharsToPixels(10));

      colDef.setLabelProvider(new CellLabelProvider() {
         @Override
         public void update(final ViewerCell cell) {

            final DevicePause pause = (DevicePause) cell.getElement();

            cell.setText(createUI_Pause_Absolute_StartTime(pause));
         }
      });
   }

   /**
    * Column: Pause relative end time
    */
   private void defineColumn_Time_Relative_End() {

      final TableColumnDefinition colDef = new TableColumnDefinition(_columnManager, "pauseEndTime_Relative", SWT.TRAIL); //$NON-NLS-1$

      colDef.setColumnLabel(Messages.Tour_Pauses_Column_EndTime_Relative_Label);
      colDef.setColumnHeaderText(Messages.Tour_Pauses_Column_EndTime_Relative_Header);
      colDef.setColumnHeaderToolTipText(Messages.Tour_Pauses_Column_EndTime_Relative_Label);

      colDef.setColumnCategory(OtherMessages.COLUMN_FACTORY_CATEGORY_TIME);

      colDef.setIsDefaultColumn();
      colDef.setDefaultColumnWidth(_pc.convertWidthInCharsToPixels(10));

      colDef.setLabelProvider(new CellLabelProvider() {
         @Override
         public void update(final ViewerCell cell) {

            final DevicePause pause = (DevicePause) cell.getElement();

            cell.setText(createUI_Pause_Relative_EndTime(pause));
         }

      });
   }

   /**
    * Column: Pause relative start time
    */
   private void defineColumn_Time_Relative_Start() {

      final TableColumnDefinition colDef = new TableColumnDefinition(_columnManager, "pauseStartTime_Relative", SWT.TRAIL); //$NON-NLS-1$

      colDef.setColumnLabel(Messages.Tour_Pauses_Column_StartTime_Relative_Label);
      colDef.setColumnHeaderText(Messages.Tour_Pauses_Column_StartTime_Relative_Header);
      colDef.setColumnHeaderToolTipText(Messages.Tour_Pauses_Column_StartTime_Relative_Tooltip);

      colDef.setColumnCategory(OtherMessages.COLUMN_FACTORY_CATEGORY_TIME);

      colDef.setIsDefaultColumn();
      colDef.setDefaultColumnWidth(_pc.convertWidthInCharsToPixels(10));

      colDef.setLabelProvider(new CellLabelProvider() {
         @Override
         public void update(final ViewerCell cell) {

            final DevicePause pause = (DevicePause) cell.getElement();

            cell.setText(createUI_Pause_Relative_StartTime(pause));
         }

      });
   }

   @Override
   public void dispose() {

      TourManager.getInstance().removeTourEventListener(_tourEventListener);

      getSite().getPage().removePostSelectionListener(_postSelectionListener);

      _prefStore.removePropertyChangeListener(_prefChangeListener);
      _prefStore_Common.removePropertyChangeListener(_prefChangeListener_Common);

      super.dispose();
   }

   private void enableActions() {

      final boolean isTourInDb = _tourData != null && _tourData.getTourPerson() != null;
      final boolean isSingleTour = _tourData != null && _tourData.isMultipleTours() == false;

      _subMenu_SetPauseType.setEnabled(isTourInDb);
      _actionDeleteTourPauses.setEnabled(isTourInDb && isSingleTour);
   }

   private void fillContextMenu(final IMenuManager menuMgr) {

      menuMgr.add(_subMenu_SetPauseType);
      menuMgr.add(_actionDeleteTourPauses);

      final int[] selectedIndices = _pausesViewer.getTable().getSelectionIndices();

      _subMenu_SetPauseType.setTourPauses(selectedIndices);

      setupPausesToDelete(selectedIndices);

      enableActions();
   }

   /**
    * Select the chart/map slider(s) according to the selected slices
    */
   private void fireSliderPosition(final StructuredSelection selection) {

      final Object[] selectedPauses = selection.toArray();
      if ((selectedPauses == null) || (selectedPauses.length == 0)) {
         return;
      }

      final int numSelectedPauses = selectedPauses.length;
      final Object slice1 = selectedPauses[0];

      int serieIndex1 = SelectionChartXSliderPosition.IGNORE_SLIDER_POSITION;
      int serieIndex2 = SelectionChartXSliderPosition.IGNORE_SLIDER_POSITION;

      if (numSelectedPauses == 1) {

         // One slice is selected

         if (slice1 instanceof final DevicePause devicePause) {

            final int serieIndexFirst = devicePause.serieIndex_Start;

            /*
             * Position slider at the beginning of the slice so that each slice borders has an
             * slider
             */

            serieIndex1 = serieIndexFirst == 0 ? 0 : serieIndexFirst - 1;
            serieIndex2 = devicePause.serieIndex_End;
         }

      } else if (numSelectedPauses > 1) {

         // Two or more slices are selected, set the 2 sliders to the first and last selected slices

         if (slice1 instanceof final DevicePause devicePause) {

            final int serieIndexFirst = devicePause.serieIndex_Start;

            /*
             * Position slider at the beginning of the first slice
             */
            serieIndex1 = serieIndexFirst == 0 ? 0 : serieIndexFirst - 1;
            serieIndex2 = ((DevicePause) selectedPauses[numSelectedPauses - 1]).serieIndex_Start;
         }
      }

      if (serieIndex1 == SelectionChartXSliderPosition.IGNORE_SLIDER_POSITION) {
         return;
      }

      ISelection sliderSelection = null;

      TourChart tourChart = null;
      final TourChart activeTourChart = TourManager.getInstance().getActiveTourChart();

      if ((activeTourChart != null) && (activeTourChart.isDisposed() == false)) {
         tourChart = activeTourChart;
      }

      if (tourChart == null) {

         // chart is not available, fire a map position

         final double[] latitudeSerie = _tourData.latitudeSerie;

         if ((latitudeSerie != null) && (latitudeSerie.length > 0)) {

            // map position is available

            sliderSelection = new SelectionMapPosition(_tourData, serieIndex1, serieIndex2, true);
         }

      } else {

         final SelectionChartXSliderPosition xSliderSelection = new SelectionChartXSliderPosition(
               tourChart,
               serieIndex1,
               serieIndex2);

         xSliderSelection.setCenterSliderPosition(true);

         sliderSelection = xSliderSelection;
      }

      if (sliderSelection != null) {
         _postSelectionProvider.setSelection(sliderSelection);
      }
   }

   @Override
   public ColumnManager getColumnManager() {
      return _columnManager;
   }

   @Override
   public ArrayList<TourData> getSelectedTours() {

      final ArrayList<TourData> selectedTours = new ArrayList<>();

      if (_tourData != null) {
         selectedTours.add(_tourData);
      }

      return selectedTours;
   }

   @Override
   public ColumnViewer getViewer() {
      return _pausesViewer;
   }

   private void onSelect_TourPause(final StructuredSelection selection) {

      if (_isInUpdate) {
         return;
      }

      fireSliderPosition(selection);
   }

   private void onSelectionChanged(final ISelection selection) {

      if (_isInUpdate || selection == null) {
         return;
      }

      long tourId = TourDatabase.ENTITY_IS_NOT_SAVED;
      TourData tourData = null;

      if (selection instanceof final SelectionTourData tourDataSelection) {

         // a tour was selected, get the chart and update the marker viewer

         tourData = tourDataSelection.getTourData();

      } else if (selection instanceof final SelectionTourId selectionTourId) {

         tourId = selectionTourId.getTourId();

      } else if (selection instanceof final SelectionTourIds selectionTourIds) {

         final ArrayList<Long> tourIds = selectionTourIds.getTourIds();

         if (CollectionUtils.isNotEmpty(tourIds)) {

            if (tourIds.size() == 1) {
               tourId = tourIds.get(0);
            } else {
               tourData = TourManager.createJoinedTourData(tourIds);
            }
         }

      } else if (selection instanceof final SelectionReferenceTourView tourCatalogSelection) {

         final TVIRefTour_RefTourItem refItem = tourCatalogSelection.getRefItem();
         if (refItem != null) {
            tourId = refItem.getTourId();
         }

      } else if (selection instanceof final StructuredSelection structuredSelection) {

         final Object firstElement = structuredSelection.getFirstElement();
         if (firstElement instanceof final TVIRefTour_ComparedTour tviRefTour_ComparedTour) {

            tourId = tviRefTour_ComparedTour.getTourId();

         } else if (firstElement instanceof final TVIElevationCompareResult_ComparedTour tviElevationCompareResult_ComparedTour) {

            tourId = tviElevationCompareResult_ComparedTour.getTourId();

         }

      } else if (selection instanceof SelectionDeletedTours) {

         clearView();
      }

      if (tourData == null) {

         if (tourId >= TourDatabase.ENTITY_IS_NOT_SAVED) {

            tourData = TourManager.getInstance().getTourData(tourId);
            if (tourData != null) {
               setupViewerContent(tourData);
            }
         }
      } else {

         setupViewerContent(tourData);
      }

      updateUI_PausesViewer();
   }

   private void onTourEvent_TourPause(final SelectionTourPause pauseSelection) {

      final TourData tourData = pauseSelection.getTourData();

      if (tourData != _tourData) {

         setupViewerContent(tourData);

         updateUI_PausesViewer();
      }

      _isInUpdate = true;
      {
         final int pauseIndex = pauseSelection.getPauseIndex();

         // check bounds
         if (pauseIndex < _allDevicePauses.size()) {

            final DevicePause devicePause = _allDevicePauses.get(pauseIndex);

            _pausesViewer.setSelection(new StructuredSelection(devicePause), true);
         }
      }
      _isInUpdate = false;
   }

   @Override
   public ColumnViewer recreateViewer(final ColumnViewer columnViewer) {

      _viewerContainer.setRedraw(false);
      {
         _pausesViewer.getTable().dispose();

         createUI_10_TableViewer(_viewerContainer);
         _viewerContainer.layout();

         // update the viewer
         reloadViewer();
      }
      _viewerContainer.setRedraw(true);

      return _pausesViewer;
   }

   private void refreshView() {

      _columnManager.saveState(_state);
      _columnManager.clearColumns();

      defineAllColumns();

      _pausesViewer = (TableViewer) recreateViewer(_pausesViewer);
   }

   @Override
   public void reloadViewer() {

      updateUI_PausesViewer();
   }

   @PersistState
   private void saveState() {

      _columnManager.saveState(_state);
   }

   @Override
   public void setFocus() {

      _pausesViewer.getTable().setFocus();
   }

   private void setupPausesToDelete(final int[] selectedPausesIndices) {

      _actionDeleteTourPauses.setTourPauses(selectedPausesIndices);

      final List<String> tourPausesViewSelectedPausesStartEndTimes = new ArrayList<>();
      final TableItem[] items = _pausesViewer.getTable().getItems();
      for (final int selectedIndex : selectedPausesIndices) {

         final TableItem item = items[selectedIndex];
         final DevicePause devicePause = (DevicePause) item.getData();

         tourPausesViewSelectedPausesStartEndTimes.add(createUI_Pause_Duration(devicePause));
         tourPausesViewSelectedPausesStartEndTimes.add(createUI_Pause_Relative_StartTime(devicePause));
         tourPausesViewSelectedPausesStartEndTimes.add(createUI_Pause_Relative_EndTime(devicePause));
         tourPausesViewSelectedPausesStartEndTimes.add(createUI_Pause_Absolute_StartTime(devicePause));
         tourPausesViewSelectedPausesStartEndTimes.add(createUI_Pause_Absolute_EndTime(devicePause));
      }
      _actionDeleteTourPauses.setTourPausesStartEndTimes(tourPausesViewSelectedPausesStartEndTimes);
   }

   private void setupViewerContent(final TourData tourData) {

      _tourData = tourData;
      _tourStartTime = tourData.getTourStartTime();

      final long[] allPausedTime_Start = _tourData.getPausedTime_Start();
      final long[] allPausedTime_End = _tourData.getPausedTime_End();
      final long[] allPausedTime_Data = _tourData.getPausedTime_Data();
      final int[] timeSerie = _tourData.timeSerie;

      final long tourStartTimeMS = _tourData.getTourStartTimeMS();

      _allDevicePauses = new ArrayList<>();

      if (allPausedTime_Start == null) {
         return;
      }

      // loop: all pauses
      for (int pausesIndex = 0; pausesIndex < allPausedTime_Start.length; pausesIndex++) {

         final long absolutePausedTimeStartMS = allPausedTime_Start[pausesIndex];
         final long absolutePausedTimeEndMS = allPausedTime_End[pausesIndex];

         final long relativeStartTime = (absolutePausedTimeStartMS - tourStartTimeMS) / 1000;
         final long relativeEndTime = (absolutePausedTimeEndMS - tourStartTimeMS) / 1000;

         if (relativeStartTime < 0) {

            // the pause start is before the tour start -> this occurs very often, so keep this value !

//          continue;
         }

         final int numAllSlices = timeSerie.length;
         int serieIndexStart = 0;
         int serieIndexEnd = 0;

         for (; serieIndexStart < numAllSlices; ++serieIndexStart) {

            final long absoluteSliceTime = timeSerie[serieIndexStart] * 1000L + tourStartTimeMS;

            if (absoluteSliceTime > absolutePausedTimeStartMS) {
               break;
            }
         }

         for (serieIndexEnd = serieIndexStart; serieIndexEnd < numAllSlices; ++serieIndexEnd) {

            final long absoluteSliceTime = timeSerie[serieIndexEnd] * 1000L + tourStartTimeMS;

            if (absoluteSliceTime > absolutePausedTimeEndMS) {

               serieIndexEnd = serieIndexEnd - 1;

               break;
            }
         }

         final boolean isPauseAnAutoPause = allPausedTime_Data == null || allPausedTime_Data[pausesIndex] == 1;

         _allDevicePauses.add(new DevicePause(

               isPauseAnAutoPause ? 1 : 0,

               relativeStartTime,
               relativeEndTime,

               serieIndexStart,
               serieIndexEnd));
      }

   }

   private void showTourFromTourProvider() {

      _pageBook.showPage(_pageNoData);

      // a tour is not displayed, find a tour provider which provides a tour
      Display.getCurrent().asyncExec(() -> {

         // validate widget
         if (_pageBook.isDisposed()) {
            return;
         }

         /*
          * check if tour was set from a selection provider
          */
         if (_tourData != null) {
            return;
         }

         onSelectionChanged(TourManager.getSelectedToursSelection());
      });
   }

   @Override
   public void updateColumnHeader(final ColumnDefinition colDef) {}

   private void updateUI_PausesViewer() {

      if (_tourData == null) {

         _pageBook.showPage(_pageNoData);

      } else {

         _isInUpdate = true;
         {
            _pausesViewer.setInput(new Object[0]);
         }
         _isInUpdate = false;

         _pageBook.showPage(_viewerContainer);
      }

      enableActions();
   }

}

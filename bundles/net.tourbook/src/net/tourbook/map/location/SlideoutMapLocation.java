/*******************************************************************************
 * Copyright (C) 2024 Wolfgang Schramm and Contributors
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
package net.tourbook.map.location;

import java.time.ZonedDateTime;
import java.util.List;

import net.tourbook.Messages;
import net.tourbook.application.TourbookPlugin;
import net.tourbook.common.UI;
import net.tourbook.common.formatter.FormatManager;
import net.tourbook.common.time.TimeTools;
import net.tourbook.common.tooltip.AdvancedSlideout;
import net.tourbook.common.util.ColumnDefinition;
import net.tourbook.common.util.ColumnDefinitionFor1stVisibleAlignmentColumn;
import net.tourbook.common.util.ColumnManager;
import net.tourbook.common.util.ITourViewer2;
import net.tourbook.common.util.TableColumnDefinition;
import net.tourbook.common.util.Util;
import net.tourbook.data.TourLocation;
import net.tourbook.tour.TourEventId;
import net.tourbook.tour.TourManager;
import net.tourbook.tour.location.MapLocationManager;
import net.tourbook.ui.TableColumnFactory;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;

/**
 * Map location slideout
 */
public class SlideoutMapLocation extends AdvancedSlideout implements ITourViewer2 {

   private static final String           COLUMN_CREATED_DATE_TIME    = "createdDateTime";                   //$NON-NLS-1$
   private static final String           COLUMN_LOCATION_NAME        = "LocationName";                      //$NON-NLS-1$
   private static final String           COLUMN_LATITUDE_1           = "latitude1";                         //$NON-NLS-1$
   private static final String           COLUMN_LATITUDE_2           = "latitude2";                         //$NON-NLS-1$
   private static final String           COLUMN_LONGITUDE_1          = "longitude1";                        //$NON-NLS-1$
   private static final String           COLUMN_LONGITUDE_2          = "longitude2";                        //$NON-NLS-1$
   private static final String           COLUMN_SEQUENCE             = "sequence";                          //$NON-NLS-1$
   private static final String           COLUMN_ZOOM_LEVEL           = "zoomLevel";                         //$NON-NLS-1$

   private static final String           STATE_SORT_COLUMN_DIRECTION = "STATE_SORT_COLUMN_DIRECTION";       //$NON-NLS-1$
   private static final String           STATE_SORT_COLUMN_ID        = "STATE_SORT_COLUMN_ID";              //$NON-NLS-1$

   private static final IPreferenceStore _prefStore                  = TourbookPlugin.getPrefStore();
   private IDialogSettings               _state;

   private PixelConverter                _pc;

   private TableViewer                   _mapLocationViewer;
   private MapLocationComparator         _mapLocationComparator      = new MapLocationComparator();
   private ColumnManager                 _columnManager;
   private SelectionAdapter              _columnSortListener;

   private FocusListener                 _keepOpenListener;

   private ToolItem                      _toolItem;

   List<TourLocation>                    _allMapLocations            = MapLocationManager.getMapLocations();

   /*
    * UI controls
    */
   private Composite _viewerContainer;

   private Button    _btnDelete;

   private class MapLocationComparator extends ViewerComparator {

      private static final int ASCENDING       = 0;
      private static final int DESCENDING      = 1;

      private String           __sortColumnId  = COLUMN_LATITUDE_1;
      private int              __sortDirection = ASCENDING;

      @Override
      public int compare(final Viewer viewer, final Object e1, final Object e2) {

         final TourLocation location1 = (TourLocation) e1;
         final TourLocation location2 = (TourLocation) e2;

         boolean _isSortByTime = true;
         double rc = 0;

         // Determine which column and do the appropriate sort
         switch (__sortColumnId) {

//         case COLUMN_LATITUDE_1:
//            rc = location1.geoLocation_TopLeft.latitude - location2.geoLocation_TopLeft.latitude;
//            break;
//
//         case COLUMN_LONGITUDE_1:
//            rc = location1.geoLocation_TopLeft.longitude - location2.geoLocation_TopLeft.longitude;
//            break;
//
//         case COLUMN_LATITUDE_2:
//            rc = location1.geoLocation_BottomRight.latitude - location2.geoLocation_BottomRight.latitude;
//            break;
//
//         case COLUMN_LONGITUDE_2:
//            rc = location1.geoLocation_BottomRight.longitude - location2.geoLocation_BottomRight.longitude;
//            break;
//
         case COLUMN_LOCATION_NAME:
            rc = location1.display_name.compareTo(location2.display_name);
            break;

         case COLUMN_CREATED_DATE_TIME:

            // sorting by date is already set
            break;

         case COLUMN_ZOOM_LEVEL:
            rc = location1.zoomlevel - location2.zoomlevel;
            break;

         default:
            _isSortByTime = true;
         }

         if (rc == 0 && _isSortByTime) {
            rc = location1.getCreatedMS() - location2.getCreatedMS();
         }

         // if descending order, flip the direction
         if (__sortDirection == DESCENDING) {
            rc = -rc;
         }

         /*
          * MUST return 1 or -1 otherwise long values are not sorted correctly
          */
         return rc > 0 //
               ? 1
               : rc < 0 //
                     ? -1
                     : 0;
      }

      @Override
      public boolean isSorterProperty(final Object element, final String property) {

         // force resorting when a name is renamed
         return true;
      }

      public void setSortColumn(final Widget widget) {

         final ColumnDefinition columnDefinition = (ColumnDefinition) widget.getData();
         final String columnId = columnDefinition.getColumnId();

         if (columnId.equals(__sortColumnId)) {

            // Same column as last sort; toggle the direction

            __sortDirection = 1 - __sortDirection;

         } else {

            // New column; do an ascent sorting

            __sortColumnId = columnId;
            __sortDirection = ASCENDING;
         }

         updateUI_SetSortDirection(__sortColumnId, __sortDirection);
      }
   }

   private class MapLocationContentProvider implements IStructuredContentProvider {

      @Override
      public Object[] getElements(final Object inputElement) {
         return _allMapLocations.toArray();
      }
   }

   /**
    * @param ownerControl
    * @param toolBar
    * @param map2View
    * @param map2State
    */
   public SlideoutMapLocation(final ToolItem toolItem,
                              final IDialogSettings state) {

      super(toolItem.getParent(), state, new int[] { 325, 400, 325, 400 });

      _toolItem = toolItem;

      _state = state;

      setTitleText(Messages.Slideout_MapLocation_Label_Title);

      // prevent that the opened slideout is partly hidden
      setIsForceBoundsToBeInsideOfViewport(true);
   }

   @Override
   protected void createSlideoutContent(final Composite parent) {

      initUI(parent);

      restoreState_BeforeUI();

      // define all columns for the viewer
      _columnManager = new ColumnManager(this, _state);
      defineAllColumns();

      createUI(parent);

      fillUI();

      // load viewer
      updateUI_Viewer();

      restoreState();
      enableControls();

      MapLocationManager.setMapLocationSlideout(this);
   }

   /**
    * Create a list with all available map providers, sorted by preference settings
    */

   private Composite createUI(final Composite parent) {

      final Composite shellContainer = new Composite(parent, SWT.NONE);
      GridDataFactory.fillDefaults().grab(true, true).applyTo(shellContainer);
      GridLayoutFactory.fillDefaults().applyTo(shellContainer);
//      shellContainer.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_MAGENTA));
      {
         createUI_10_CustomizeLookup(shellContainer);
         createUI_20_LocationViewer(shellContainer);
         createUI_90_Actions(shellContainer);
      }

      return shellContainer;
   }

   private void createUI_10_CustomizeLookup(final Composite parent) {

      final Composite container = new Composite(parent, SWT.NONE);
      GridDataFactory.fillDefaults().grab(true, false).applyTo(container);
      GridLayoutFactory.fillDefaults().numColumns(2).applyTo(container);
      {}

   }

   private void createUI_20_LocationViewer(final Composite parent) {

      final Composite container = new Composite(parent, SWT.NONE);
      GridDataFactory.fillDefaults().grab(true, true).applyTo(container);
      GridLayoutFactory.fillDefaults().applyTo(container);
      {
         {
            final Label label = new Label(container, SWT.NONE);
            label.setText(Messages.Slideout_TourGeoFilter_Label_History);
         }
         {
            _viewerContainer = new Composite(container, SWT.NONE);
            GridDataFactory.fillDefaults().grab(true, true).applyTo(_viewerContainer);
            GridLayoutFactory.fillDefaults().applyTo(_viewerContainer);
            {
               createUI_22_LocationViewer_Table(_viewerContainer);
            }
         }

//         createUI_680_ViewerActions(container);
      }
   }

   private void createUI_22_LocationViewer_Table(final Composite parent) {

      /*
       * Create table
       */
      final Table table = new Table(parent, SWT.FULL_SELECTION);
      GridDataFactory.fillDefaults().grab(true, true).applyTo(table);

      table.setHeaderVisible(true);
      table.setLinesVisible(false);

      /*
       * It took a while that the correct listener is set and also the checked item is fired and not
       * the wrong selection.
       */
      table.addListener(SWT.Selection, new Listener() {

         @Override
         public void handleEvent(final Event event) {
//            onGeoPart_Select(event);
         }
      });

      table.addKeyListener(new KeyListener() {

         @Override
         public void keyPressed(final KeyEvent e) {

            switch (e.keyCode) {

            case SWT.DEL:
               onLocation_Delete();
               break;

            default:
               break;
            }
         }

         @Override
         public void keyReleased(final KeyEvent e) {}
      });

      /*
       * Create table viewer
       */
      _mapLocationViewer = new TableViewer(table);

      // very important: the editing support must be set BEFORE the columns are created
//      _colDef_FilterName.setEditingSupport(new FilterName_EditingSupport(_geoFilterViewer));

      UI.setCellEditSupport(_mapLocationViewer);

      _columnManager.createColumns(_mapLocationViewer);
      _columnManager.setSlideoutShell(this);

      _mapLocationViewer.setUseHashlookup(true);
      _mapLocationViewer.setContentProvider(new MapLocationContentProvider());
      _mapLocationViewer.setComparator(_mapLocationComparator);

      _mapLocationViewer.addSelectionChangedListener(selectionChangedEvent -> onLocation_Select(selectionChangedEvent));
//    _mapLocationViewer.addDoubleClickListener(doubleClickEvent -> onGeoFilter_ToggleReadEditMode());

      updateUI_SetSortDirection(
            _mapLocationComparator.__sortColumnId,
            _mapLocationComparator.__sortDirection);

      createUI_24_LocationViewer_ContextMenu();
   }

   /**
    * Ceate the view context menus
    */
   private void createUI_24_LocationViewer_ContextMenu() {

      final MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$

      menuMgr.setRemoveAllWhenShown(true);

      menuMgr.addMenuListener(new IMenuListener() {
         @Override
         public void menuAboutToShow(final IMenuManager manager) {
//          fillContextMenu(manager);
         }
      });

      /**
       * ColumnManager context menu is set in {@link #beforeHideToolTip()} but this works only until
       * the column manager is recreating the viewer.
       * <p>
       * The slideout must be reopened that the context menu is working again :-(
       */

      /*
       * Set context menu after a viewer reload, even more complicated
       */
      _columnManager.createHeaderContextMenu(_mapLocationViewer.getTable(), null, getRRShellWithResize());

   }

   private void createUI_90_Actions(final Composite parent) {

      final Composite container = new Composite(parent, SWT.NONE);
      GridDataFactory.fillDefaults()
            .grab(true, false)
            .align(SWT.END, SWT.FILL)
            .applyTo(container);
      GridLayoutFactory.fillDefaults().numColumns(1).applyTo(container);
      {
         {
            /*
             * Button: Delete
             */
            _btnDelete = new Button(container, SWT.PUSH);
            _btnDelete.setText(Messages.App_Action_Delete);
            _btnDelete.addSelectionListener(SelectionListener.widgetSelectedAdapter(selectionEvent -> onLocation_Delete()));

            // set button default width
            UI.setButtonLayoutData(_btnDelete);
         }
      }
   }

   private void defineAllColumns() {

      defineColumn_00_SequenceNumber();
      defineColumn_05_LocationName();
      defineColumn_30_Zoomlevel();
      defineColumn_40_BoundingBox_Width();
      defineColumn_42_BoundingBox_Height();

      defineColumn_99_Created();

      new ColumnDefinitionFor1stVisibleAlignmentColumn(_columnManager);
   }

   private void defineColumn_00_SequenceNumber() {

      final TableColumnDefinition colDef = new TableColumnDefinition(_columnManager, COLUMN_SEQUENCE, SWT.TRAIL);

      colDef.setColumnLabel(Messages.GeoCompare_View_Column_SequenceNumber_Label);
      colDef.setColumnHeaderText(Messages.GeoCompare_View_Column_SequenceNumber_Header);
      colDef.setColumnHeaderToolTipText(Messages.GeoCompare_View_Column_SequenceNumber_Label);

      colDef.setDefaultColumnWidth(_pc.convertWidthInCharsToPixels(8));

      colDef.setIsDefaultColumn();

      colDef.setLabelProvider(new CellLabelProvider() {
         @Override
         public void update(final ViewerCell cell) {

            final int indexOf = _mapLocationViewer.getTable().indexOf((TableItem) cell.getItem());

            cell.setText(Integer.toString(indexOf + 1));
         }
      });

   }

   /**
    * Column: Number of geo parts
    */
   private void defineColumn_05_LocationName() {

      final TableColumnDefinition colDef = new TableColumnDefinition(_columnManager, COLUMN_LOCATION_NAME, SWT.LEAD);

      colDef.setColumnLabel(Messages.Slideout_TourGeoFilter_Column_FilterName_Label);
      colDef.setColumnHeaderText(Messages.Slideout_TourGeoFilter_Column_FilterName_Label);

      colDef.setDefaultColumnWidth(_pc.convertWidthInCharsToPixels(12));

      colDef.setIsDefaultColumn();
      colDef.setCanModifyVisibility(false);
      colDef.setColumnSelectionListener(_columnSortListener);

      colDef.setLabelProvider(new CellLabelProvider() {
         @Override
         public void update(final ViewerCell cell) {

            final TourLocation item = (TourLocation) cell.getElement();

            if (UI.IS_SCRAMBLE_DATA) {

               cell.setText(UI.scrambleText(item.display_name));

            } else {

               cell.setText(item.display_name);
            }
         }
      });
   }

   /**
    * Column: Zoomlevel
    */
   private void defineColumn_30_Zoomlevel() {

      final TableColumnDefinition colDef = new TableColumnDefinition(_columnManager, COLUMN_ZOOM_LEVEL, SWT.TRAIL);

      colDef.setColumnLabel(Messages.Map_Bookmark_Column_ZoomLevel2_Tooltip);
      colDef.setColumnHeaderText(Messages.Map_Bookmark_Column_ZoomLevel2);
      colDef.setColumnHeaderToolTipText(Messages.Map_Bookmark_Column_ZoomLevel2_Tooltip);

      colDef.setDefaultColumnWidth(_pc.convertWidthInCharsToPixels(5));

      colDef.setIsDefaultColumn();
      colDef.setColumnSelectionListener(_columnSortListener);

      colDef.setLabelProvider(new CellLabelProvider() {
         @Override
         public void update(final ViewerCell cell) {

            final TourLocation item = (TourLocation) cell.getElement();

            cell.setText(Integer.toString(item.zoomlevel + 0));
         }
      });
   }

   private void defineColumn_40_BoundingBox_Width() {

      final ColumnDefinition colDef = TableColumnFactory.LOCATION_GEO_BOUNDING_BOX_WIDTH.createColumn(_columnManager, _pc);

      colDef.setIsDefaultColumn();
      colDef.setColumnSelectionListener(_columnSortListener);

      colDef.setLabelProvider(new CellLabelProvider() {
         @Override
         public void update(final ViewerCell cell) {

            final TourLocation tourLocation = (TourLocation) cell.getElement();

            cell.setText(FormatManager.formatNumber_0(tourLocation.boundingBoxWidth));
         }
      });
   }

   private void defineColumn_42_BoundingBox_Height() {

      final ColumnDefinition colDef = TableColumnFactory.LOCATION_GEO_BOUNDING_BOX_HEIGHT.createColumn(_columnManager, _pc);

      colDef.setIsDefaultColumn();
      colDef.setColumnSelectionListener(_columnSortListener);

      colDef.setLabelProvider(new CellLabelProvider() {
         @Override
         public void update(final ViewerCell cell) {

            final TourLocation tourLocation = ((TourLocation) cell.getElement());

            cell.setText(FormatManager.formatNumber_0(tourLocation.boundingBoxHeight));
         }
      });
   }

   /**
    * Column: Created
    */
   private void defineColumn_99_Created() {

      final TableColumnDefinition colDef = new TableColumnDefinition(_columnManager, COLUMN_CREATED_DATE_TIME, SWT.TRAIL);

      colDef.setColumnLabel(Messages.Slideout_TourGeoFilter_Column_Created_Label);
      colDef.setColumnHeaderText(Messages.Slideout_TourGeoFilter_Column_Created_Label);

      colDef.setDefaultColumnWidth(_pc.convertWidthInCharsToPixels(20));

      colDef.setIsDefaultColumn();
      colDef.setColumnSelectionListener(_columnSortListener);

      colDef.setLabelProvider(new CellLabelProvider() {
         @Override
         public void update(final ViewerCell cell) {

            final TourLocation item = (TourLocation) cell.getElement();
            final ZonedDateTime created = item.getCreated();

            if (created != null) {

               cell.setText(created.format(TimeTools.Formatter_DateTime_SM));
            }
         }
      });
   }

   private void enableControls() {

      final List<TourLocation> allSelectedLocations = getSelectedLocations();

      final boolean isLocationSelected = allSelectedLocations.size() > 0;

      _btnDelete.setEnabled(isLocationSelected);
   }

   private void fillUI() {

   }

   @Override
   public ColumnManager getColumnManager() {

      return _columnManager;
   }

   @Override
   protected Rectangle getParentBounds() {

      final Rectangle itemBounds = _toolItem.getBounds();
      final Point itemDisplayPosition = _toolItem.getParent().toDisplay(itemBounds.x, itemBounds.y);

      itemBounds.x = itemDisplayPosition.x;
      itemBounds.y = itemDisplayPosition.y;

      return itemBounds;
   }

   private List<TourLocation> getSelectedLocations() {

      @SuppressWarnings("unchecked")
      final List<TourLocation> allSelectedLocations = _mapLocationViewer.getStructuredSelection().toList();

      return allSelectedLocations;
   }

   /**
    * @param sortColumnId
    *
    * @return Returns the column widget by it's column id, when column id is not found then the
    *         first column is returned.
    */
   private TableColumn getSortColumn(final String sortColumnId) {

      final TableColumn[] allColumns = _mapLocationViewer.getTable().getColumns();

      for (final TableColumn column : allColumns) {

         final String columnId = ((ColumnDefinition) column.getData()).getColumnId();

         if (columnId.equals(sortColumnId)) {
            return column;
         }
      }

      return allColumns[0];
   }

   @Override
   public ColumnViewer getViewer() {
      return _mapLocationViewer;
   }

   private void initUI(final Composite parent) {

      _pc = new PixelConverter(parent);

      _columnSortListener = new SelectionAdapter() {
         @Override
         public void widgetSelected(final SelectionEvent e) {
            onSelectSortColumn(e);
         }
      };

      _keepOpenListener = new FocusListener() {

         @Override
         public void focusGained(final FocusEvent e) {

            /*
             * This will fix the problem that when the list of a combobox is displayed, then the
             * slideout will disappear :-(((
             */
            setIsKeepOpenInternally(true);
         }

         @Override
         public void focusLost(final FocusEvent e) {
            setIsKeepOpenInternally(false);
         }
      };
   }

   @Override
   public boolean isColumn0Visible(final ColumnViewer columnViewer) {

      final TableColumn[] allColumns = _mapLocationViewer.getTable().getColumns();

      if (allColumns.length > 0) {

         final TableColumn column = allColumns[0];
         final String columnId = ((ColumnDefinition) column.getData()).getColumnId();

         if (ColumnDefinitionFor1stVisibleAlignmentColumn.COLUMN_ID.equals(columnId)) {
            return true;
         }
      }

      return false;
   }

   @Override
   protected void onDispose() {

      MapLocationManager.setMapLocationSlideout(null);

      super.onDispose();
   }

   @Override
   protected void onFocus() {

   }

   private void onLocation_Delete() {

      final List<TourLocation> allSelectedLocations = getSelectedLocations();

      // update model
      if (MapLocationManager.deleteLocations(allSelectedLocations) == false) {
         return;
      }

      /*
       * Deletion was performed -> update viewer
       */

      final Table table = _mapLocationViewer.getTable();

      // get index for selected location
      final int lastLocationIndex = table.getSelectionIndex();

      // reload viewer
      reloadViewer();

      // get next location
      TourLocation nextLocationItem = (TourLocation) _mapLocationViewer.getElementAt(lastLocationIndex);

      if (nextLocationItem == null) {
         nextLocationItem = (TourLocation) _mapLocationViewer.getElementAt(lastLocationIndex - 1);
      }

      // select next location
      if (nextLocationItem != null) {
         _mapLocationViewer.setSelection(new StructuredSelection(nextLocationItem), true);
      }

      table.setFocus();

      TourManager.fireEventWithCustomData(
            TourEventId.TOUR_LOCATION_SELECTION,
            null,
            null);
   }

   private void onLocation_Select(final SelectionChangedEvent selectionChangedEvent) {

      final IStructuredSelection selection = _mapLocationViewer.getStructuredSelection();

      if (selection.isEmpty()) {
         return;
      }

      enableControls();

      // fire selection
      TourManager.fireEventWithCustomData(
            TourEventId.TOUR_LOCATION_SELECTION,
            selection.toList(),
            null);
   }

   private void onSelectSortColumn(final SelectionEvent e) {

      _viewerContainer.setRedraw(false);
      {
         // keep selection
//         final ISelection selectionBackup = getViewerSelection();
         final ISelection selectionBackup = _mapLocationViewer.getStructuredSelection();
         {
            // update viewer with new sorting
            _mapLocationComparator.setSortColumn(e.widget);
            _mapLocationViewer.refresh();
         }
         updateUI_SelectMapLocationItem(selectionBackup);
      }
      _viewerContainer.setRedraw(true);
   }

   @Override
   public ColumnViewer recreateViewer(final ColumnViewer columnViewer) {

      _viewerContainer.setRedraw(false);
      {
         _mapLocationViewer.getTable().dispose();

         createUI_22_LocationViewer_Table(_viewerContainer);
         _viewerContainer.layout();

         // update the viewer
         reloadViewer();
      }
      _viewerContainer.setRedraw(true);

      return _mapLocationViewer;
   }

   @Override
   public void reloadViewer() {

      updateUI_Viewer();
   }

   private void restoreState() {

   }

   private void restoreState_BeforeUI() {

      // sorting
      final String sortColumnId = Util.getStateString(_state, STATE_SORT_COLUMN_ID, TableColumnFactory.SENSOR_NAME_ID);
      final int sortDirection = Util.getStateInt(_state, STATE_SORT_COLUMN_DIRECTION, MapLocationComparator.ASCENDING);

      // update comparator
      _mapLocationComparator.__sortColumnId = sortColumnId;
      _mapLocationComparator.__sortDirection = sortDirection;
   }

   @Override
   protected void saveState() {

      _columnManager.saveState(_state);

      _state.put(STATE_SORT_COLUMN_ID, _mapLocationComparator.__sortColumnId);
      _state.put(STATE_SORT_COLUMN_DIRECTION, _mapLocationComparator.__sortDirection);

      super.saveState();
   }

   @Override
   protected void saveState_BeforeDisposed() {

   }

   @Override
   public void updateColumnHeader(final ColumnDefinition colDef) {}

   public void updateUI(final TourLocation tourLocation) {

      _mapLocationViewer.refresh();

      _mapLocationViewer.setSelection(new StructuredSelection(tourLocation), true);
      _mapLocationViewer.getTable().showSelection();
   }

   /**
    * Select and reveal a compare item item.
    *
    * @param selection
    */
   private void updateUI_SelectMapLocationItem(final ISelection selection) {

//      _isInUpdate = true;
      {
         _mapLocationViewer.setSelection(selection, true);
         _mapLocationViewer.getTable().showSelection();

//       // focus can have changed when resorted, set focus to the selected item
//       int selectedIndex = 0;
//       final Table table = _geoFilterViewer.getTable();
//       final TableItem[] items = table.getItems();
//       for (int itemIndex = 0; itemIndex < items.length; itemIndex++) {
//
//          final TableItem tableItem = items[itemIndex];
//
//          if (tableItem.getData() == selectedProfile) {
//             selectedIndex = itemIndex;
//          }
//       }
//       table.setSelection(selectedIndex);
//       table.showSelection();

      }
//      _isInUpdate = false;
   }

   /**
    * Set the sort column direction indicator for a column.
    *
    * @param sortColumnId
    * @param isAscendingSort
    */
   private void updateUI_SetSortDirection(final String sortColumnId, final int sortDirection) {

      final Table table = _mapLocationViewer.getTable();
      final TableColumn tc = getSortColumn(sortColumnId);

      table.setSortColumn(tc);
      table.setSortDirection(sortDirection == MapLocationComparator.ASCENDING ? SWT.UP : SWT.DOWN);
   }

   private void updateUI_Viewer() {

      _mapLocationViewer.setInput(new Object[0]);
   }

}

/*******************************************************************************
 * Copyright (C) 2023 Wolfgang Schramm and Contributors
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
package net.tourbook.tour.location;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import net.tourbook.Messages;
import net.tourbook.common.UI;
import net.tourbook.common.dialog.MessageDialog_OnTop;
import net.tourbook.common.tooltip.AdvancedSlideout;
import net.tourbook.common.util.StatusUtil;
import net.tourbook.common.util.StringUtils;
import net.tourbook.data.TourData;
import net.tourbook.tour.DialogQuickEdit;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.nebula.widgets.opal.duallist.mt.MT_DLItem;
import org.eclipse.nebula.widgets.opal.duallist.mt.MT_DualList;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolItem;

/**
 * Slideout for the start/end location
 */
public class SlideoutLocationOptions extends AdvancedSlideout {

   private static final String LOCATION_KEY = "LOCATION_KEY"; //$NON-NLS-1$

   private ToolItem            _toolItem;

   private PixelConverter      _pc;

   private TourData            _tourData;
   private boolean             _isStartLocation;

//   private DialogQuickEdit                 _dialogQuickEdit;

   private TableViewer                     _profileViewer;

   private ModifyListener                  _defaultModifyListener;

   private final List<TourLocationProfile> _locationProfiles = TourLocationManager.getProfiles();
   private TourLocationProfile             _selectedProfile;

   /*
    * UI controls
    */
   private MT_DualList _listLocationParts;

   private Button      _btnCopyProfile;
   private Button      _btnDeleteProfile;

   private Label       _lblProfileName;

   private Text        _txtDefaultName;
   private Text        _txtProfileName;
   private Text        _txtSelectedLocationNames;

   private class LocationProfileComparator extends ViewerComparator {

      @Override
      public int compare(final Viewer viewer, final Object e1, final Object e2) {

         if (e1 == null || e2 == null) {
            return 0;
         }

         final TourLocationProfile profile1 = (TourLocationProfile) e1;
         final TourLocationProfile profile2 = (TourLocationProfile) e2;

         return profile1.name.compareTo(profile2.name);
      }

      @Override
      public boolean isSorterProperty(final Object element, final String property) {

         // force resorting when a name is renamed
         return true;
      }
   }

   private class LocationProfileProvider implements IStructuredContentProvider {

      @Override
      public void dispose() {}

      @Override
      public Object[] getElements(final Object inputElement) {
         return _locationProfiles.toArray();
      }

      @Override
      public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {}
   }

   public SlideoutLocationOptions(final ToolItem toolItem,
                                  final IDialogSettings state,
                                  final DialogQuickEdit dialogQuickEdit,
                                  final boolean isStartLocation,
                                  final TourData tourData) {

      super(toolItem.getParent(), state, new int[] { 800, 800 });

      _toolItem = toolItem;

//      _dialogQuickEdit = dialogQuickEdit;
      _isStartLocation = isStartLocation;
      _tourData = tourData;

      // prevent that the opened slideout is partly hidden
      setIsForceBoundsToBeInsideOfViewport(true);

      final String title = _isStartLocation
            ? Messages.Slideout_TourLocation_Label_StartLocation_Title
            : Messages.Slideout_TourLocation_Label_EndLocation_Title;

      setTitleText(title);
   }

   private void addAllAddressParts(final OSMAddress address, final List<MT_DLItem> allItems) {

      try {

         final Field[] allAddressFields = address.getClass().getFields();

         for (final Field field : allAddressFields) {

            final String fieldName = field.getName();

            // skip field names which are not needed
            if ("ISO3166_2_lvl4".equals(fieldName)) { //$NON-NLS-1$
               continue;
            }

            final Object fieldValue = field.get(address);

            if (fieldValue instanceof final String stringValue) {

               // log only fields with value
               if (stringValue.length() > 0) {

                  final MT_DLItem dlItem = new MT_DLItem(
                        stringValue,
                        fieldName,
                        LOCATION_KEY,
                        LocationPart.valueOf(fieldName));

                  allItems.add(dlItem);
               }
            }
         }

      } catch (IllegalArgumentException | IllegalAccessException e) {
         StatusUtil.showStatus(e);
      }
   }

   private void addCustomPart(final LocationPart locationPart,
                              final String partValue,
                              final List<MT_DLItem> allParts) {

      if (partValue != null) {

         final String partName = "* " + locationPart.name();

         allParts.add(new MT_DLItem(

               partValue,
               partName,
               LOCATION_KEY,
               locationPart));
      }
   }

   @Override
   protected void createSlideoutContent(final Composite parent) {

      initUI(parent);

      createUI(parent);

      restoreState();

      updateUI_Initial();

      // load viewer
      _profileViewer.setInput(new Object());
   }

   private void createUI(final Composite parent) {

      final Composite container = new Composite(parent, SWT.NONE);
      GridDataFactory.fillDefaults()
            .grab(true, true)
            .applyTo(container);
      GridLayoutFactory.fillDefaults().numColumns(1).applyTo(container);
//      container.setBackground(UI.SYS_COLOR_YELLOW);
      {
         createUI_20_Profiles(container);
         createUI_30_ProfileName(container);
         createUI_50_LocationParts(container);
      }
   }

   private Composite createUI_20_Profiles(final Composite parent) {

      final Composite container = new Composite(parent, SWT.NONE);
      GridDataFactory.fillDefaults()
            .hint(SWT.DEFAULT, _pc.convertHeightInCharsToPixels(10))
            .applyTo(container);
      GridLayoutFactory.fillDefaults()
            .numColumns(2)
            .applyTo(container);
//      container.setBackground(UI.SYS_COLOR_CYAN);
      {
         {
            // label: Profiles

            final Label label = new Label(container, SWT.NONE);
            label.setText(Messages.Slideout_TourFilter_Label_Profiles);
            GridDataFactory.fillDefaults().span(2, 1).applyTo(label);
         }

         createUI_22_ProfileViewer(container);
         createUI_24_ProfileActions(container);
      }

      return container;
   }

   private void createUI_22_ProfileViewer(final Composite parent) {

      final Composite layoutContainer = new Composite(parent, SWT.NONE);
      GridDataFactory.fillDefaults()
            .grab(true, true)
            .applyTo(layoutContainer);

      final TableColumnLayout tableLayout = new TableColumnLayout();
      layoutContainer.setLayout(tableLayout);

      /*
       * Create table
       */
      final Table table = new Table(layoutContainer, SWT.FULL_SELECTION);

      table.setLayout(new TableLayout());

      // !!! this prevents that the horizontal scrollbar is displayed, but is not always working :-(
//      table.setHeaderVisible(false);
      table.setHeaderVisible(true);

      _profileViewer = new TableViewer(table);

      /*
       * Create columns
       */
      TableViewerColumn tvc;
      TableColumn tc;

      {
         // Column: Profile name

         tvc = new TableViewerColumn(_profileViewer, SWT.LEAD);
         tc = tvc.getColumn();
         tc.setText(Messages.Slideout_TourFilter_Column_ProfileName);
         tvc.setLabelProvider(new CellLabelProvider() {
            @Override
            public void update(final ViewerCell cell) {

               final TourLocationProfile profile = (TourLocationProfile) cell.getElement();

               cell.setText(profile.name);
            }
         });
         tableLayout.setColumnData(tc, new ColumnWeightData(1, false));
      }
//      {
//         // Column: Number of properties
//
//         tvc = new TableViewerColumn(_profileViewer, SWT.TRAIL);
//         tc = tvc.getColumn();
//         tc.setText(Messages.Slideout_TourFilter_Column_Properties);
//         tc.setToolTipText(Messages.Slideout_TourFilter_Column_Properties_Tooltip);
//         tvc.setLabelProvider(new CellLabelProvider() {
//            @Override
//            public void update(final ViewerCell cell) {
//
//               final TourLocationProfile profile = (TourLocationProfile) cell.getElement();
//
//               cell.setText(Integer.toString(profile.filterProperties.size()));
//            }
//         });
//         tableLayout.setColumnData(tc, net.tourbook.ui.UI.getColumnPixelWidth(_pc, 6));
//      }

      /*
       * Create table viewer
       */
      _profileViewer.setContentProvider(new LocationProfileProvider());
      _profileViewer.setComparator(new LocationProfileComparator());

      _profileViewer.addSelectionChangedListener(selectionChangedEvent -> onProfile_Select());

      _profileViewer.addDoubleClickListener(doubleClickEvent -> {

         // set focus to  profile name
         _txtProfileName.setFocus();
         _txtProfileName.selectAll();
      });

      _profileViewer.getTable().addKeyListener(KeyListener.keyPressedAdapter(keyEvent -> {
         if (keyEvent.keyCode == SWT.DEL) {
            onProfile_Delete();
         }
      }));
   }

   private void createUI_24_ProfileActions(final Composite parent) {

      final Composite container = new Composite(parent, SWT.NONE);
      GridDataFactory.fillDefaults()
//            .grab(true, false)
            .applyTo(container);
      GridLayoutFactory.fillDefaults().numColumns(1).applyTo(container);
      {
         {
            /*
             * Button: New
             */
            final Button button = new Button(container, SWT.PUSH);
            button.setText(Messages.Slideout_TourFilter_Action_AddProfile);
            button.setToolTipText(Messages.Slideout_TourFilter_Action_AddProfile_Tooltip);
            button.addSelectionListener(SelectionListener.widgetSelectedAdapter(selectionEvent -> onProfile_Add()));

            // set button default width
            UI.setButtonLayoutData(button);
         }
         {
            /*
             * Button: Copy
             */
            _btnCopyProfile = new Button(container, SWT.PUSH);
            _btnCopyProfile.setText(Messages.Slideout_TourFilter_Action_CopyProfile);
            _btnCopyProfile.setToolTipText(Messages.Slideout_TourFilter_Action_CopyProfile_Tooltip);
            _btnCopyProfile.addSelectionListener(SelectionListener.widgetSelectedAdapter(selectionEvent -> onProfile_Copy()));

            // set button default width
            UI.setButtonLayoutData(_btnCopyProfile);
         }
         {
            /*
             * Button: Delete
             */
            _btnDeleteProfile = new Button(container, SWT.PUSH);
            _btnDeleteProfile.setText(Messages.Slideout_TourFilter_Action_DeleteProfile);
            _btnDeleteProfile.setToolTipText(Messages.Slideout_TourFilter_Action_DeleteProfile_Tooltip);
            _btnDeleteProfile.addSelectionListener(SelectionListener.widgetSelectedAdapter(selectionEvent -> onProfile_Delete()));

            // set button default width
            UI.setButtonLayoutData(_btnDeleteProfile);
         }
      }
   }

   private void createUI_30_ProfileName(final Composite parent) {

      final Composite container = new Composite(parent, SWT.NONE);
      GridDataFactory.fillDefaults().grab(true, false).applyTo(container);
      GridLayoutFactory.fillDefaults().numColumns(2).applyTo(container);
      {
         {
            // Label: Profile name

            _lblProfileName = new Label(container, SWT.NONE);
            _lblProfileName.setText(Messages.Slideout_TourFilter_Label_ProfileName);
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).applyTo(_lblProfileName);
         }
         {
            // Text: Profile name

            _txtProfileName = new Text(container, SWT.BORDER);
            _txtProfileName.addModifyListener(_defaultModifyListener);
            GridDataFactory.fillDefaults()
                  .grab(true, false)
                  .hint(_pc.convertWidthInCharsToPixels(30), SWT.DEFAULT)
                  .applyTo(_txtProfileName);
         }
      }
   }

   private void createUI_50_LocationParts(final Composite parent) {

      {
         // default location name

         final Label label = new Label(parent, SWT.NONE);
         GridDataFactory.fillDefaults().applyTo(label);
         label.setText(Messages.Slideout_TourLocation_Label_DefaultLocationName);

         _txtDefaultName = new Text(parent, SWT.READ_ONLY | SWT.WRAP);
         GridDataFactory.fillDefaults()
               .align(SWT.FILL, SWT.CENTER)
               .grab(true, false)
               .applyTo(_txtDefaultName);
      }
      {
         // selected location parts

         final Label label = new Label(parent, SWT.NONE);
         GridDataFactory.fillDefaults().applyTo(label);
         label.setText(Messages.Slideout_TourLocation_Label_SelectedLocationParts);

         _txtSelectedLocationNames = new Text(parent, SWT.READ_ONLY | SWT.WRAP);
         GridDataFactory.fillDefaults()
               .align(SWT.FILL, SWT.CENTER)
               .grab(true, false)
               .applyTo(_txtSelectedLocationNames);
      }
      {
         // dual list with location parts

         final Label label = new Label(parent, SWT.NONE);
         GridDataFactory.fillDefaults().applyTo(label);
         label.setText(Messages.Slideout_TourLocation_Label_LocationParts);

         _listLocationParts = new MT_DualList(parent, SWT.NONE);
         _listLocationParts.addSelectionChangeListener(selectionChangeListener -> onChangeLocationPart());
         GridDataFactory.fillDefaults()
               .grab(true, true)
               .applyTo(_listLocationParts);
      }
   }

   private void enableActions() {

   }

   private void enableControls() {
      // TODO Auto-generated method stub

   }

   private String getFormattedPartName(final LocationPart locationPart) {

      return "* " + locationPart.name();
   }

   private OSMLocation getOsmLocation() {

      return _isStartLocation
            ? _tourData.osmLocation_Start.osmLocation
            : _tourData.osmLocation_End.osmLocation;
   }

   @Override
   protected Rectangle getParentBounds() {

      final Rectangle itemBounds = _toolItem.getBounds();
      final Point itemDisplayPosition = _toolItem.getParent().toDisplay(itemBounds.x, itemBounds.y);

      itemBounds.x = itemDisplayPosition.x;
      itemBounds.y = itemDisplayPosition.y;

      return itemBounds;
   }

   private void initUI(final Composite parent) {

      _pc = new PixelConverter(parent);

      _defaultModifyListener = modifyEvent -> onProfile_Modify();
   }

   private void onChangeLocationPart() {

      // get selected part IDs
      final List<MT_DLItem> allSelectedItems = _listLocationParts.getSelectionAsList();

      final String locationDisplayName = TourLocationManager.createLocationDisplayName(allSelectedItems);

      _txtSelectedLocationNames.setText(locationDisplayName);
   }

   @Override
   protected void onDispose() {

      super.onDispose();
   }

   @Override
   protected void onFocus() {

   }

   private void onProfile_Add() {

      final TourLocationProfile filterProfile = new TourLocationProfile();

      // update model
      _locationProfiles.add(filterProfile);

      // update viewer
      _profileViewer.refresh();

      // select new profile
      selectProfile(filterProfile);

      _txtProfileName.setFocus();
   }

   private void onProfile_Copy() {

      if (_selectedProfile == null) {
         // ignore
         return;
      }

      final TourLocationProfile filterProfile = _selectedProfile.clone();

      // update model
      _locationProfiles.add(filterProfile);

      // update viewer
      _profileViewer.refresh();

      // select new profile
      selectProfile(filterProfile);

      _txtProfileName.setFocus();
   }

   private void onProfile_Delete() {

      if (_selectedProfile == null) {
         // ignore
         return;
      }

      /*
       * Confirm deletion
       */
      boolean isDeleteProfile = false;
      setIsKeepOpenInternally(true);
      {
         MessageDialog_OnTop dialog = new MessageDialog_OnTop(

               getToolTipShell(),

               Messages.Slideout_TourFilter_Confirm_DeleteProfile_Title,
               null, // no title image

               NLS.bind(Messages.Slideout_TourFilter_Confirm_DeleteProfile_Message, _selectedProfile.name),
               MessageDialog.CONFIRM,

               0, // default index

               Messages.App_Action_DeleteProfile,
               Messages.App_Action_Cancel);

         dialog = dialog.withStyleOnTop();

         if (dialog.open() == IDialogConstants.OK_ID) {
            isDeleteProfile = true;
         }
      }
      setIsKeepOpenInternally(false);

      if (isDeleteProfile == false) {
         return;
      }

      // keep currently selected position
      final int lastIndex = _profileViewer.getTable().getSelectionIndex();

      // update model
      _locationProfiles.remove(_selectedProfile);
      TourLocationManager.setSelectedProfile(null);

      // update UI
      _profileViewer.remove(_selectedProfile);

      /*
       * Select another filter at the same position
       */
      final int numFilters = _locationProfiles.size();
      final int nextFilterIndex = Math.min(numFilters - 1, lastIndex);

      final Object nextSelectedProfile = _profileViewer.getElementAt(nextFilterIndex);
      if (nextSelectedProfile == null) {

         _selectedProfile = null;

//         createUI_410_FilterProperties();
//         updateUI_Properties();

         onChangeLocationPart();

      } else {

         selectProfile((TourLocationProfile) nextSelectedProfile);
      }

      enableControls();

      // set focus back to the viewer
      _profileViewer.getTable().setFocus();
   }

   private void onProfile_Modify() {

      if (_selectedProfile == null) {
         return;
      }

      final String profileName = _txtProfileName.getText();

      _selectedProfile.name = profileName;

      _profileViewer.refresh();
   }

   private void onProfile_Select() {

      TourLocationProfile selectedProfile = null;

      // get selected profile from viewer
      final StructuredSelection selection = (StructuredSelection) _profileViewer.getSelection();
      final Object firstElement = selection.getFirstElement();
      if (firstElement != null) {
         selectedProfile = (TourLocationProfile) firstElement;
      }

      if (_selectedProfile != null && _selectedProfile == selectedProfile) {
         // a new profile is not selected
         return;
      }

      _selectedProfile = selectedProfile;

      // update model
      TourLocationManager.setSelectedProfile(_selectedProfile);

      // update UI
      if (_selectedProfile == null) {

         _txtProfileName.setText(UI.EMPTY_STRING);

      } else {

         _txtProfileName.setText(_selectedProfile.name);

         if (_selectedProfile.name.equals(Messages.Tour_Filter_Default_ProfileName)) {

            // a default profile is selected, make is easy to rename it

            _txtProfileName.selectAll();
            _txtProfileName.setFocus();
         }
      }

//      createUI_410_FilterProperties();
//      updateUI_Properties();
//
//      fireModifyEvent();

      onChangeLocationPart();
   }

   private void restoreState() {

      enableActions();
   }

   @Override
   protected void saveState() {

      // save slideout position/size
      super.saveState();
   }

   private void selectProfile(final TourLocationProfile selectedProfile) {

      _profileViewer.setSelection(new StructuredSelection(selectedProfile));

      final Table table = _profileViewer.getTable();
      table.setSelection(table.getSelectionIndices());
   }

   private void updateUI_Initial() {

      final OSMLocation osmLocation = getOsmLocation();
      final OSMAddress address = osmLocation.address;

      // show "display_name" as default name
      _txtDefaultName.setText(osmLocation.display_name);

      /*
       * Fill address part widget
       */
      final List<MT_DLItem> allParts = new ArrayList<>();

// SET_FORMATTING_OFF

      // add customized parts
      final String smallestCity           = TourLocationManager.getCustom_City_Smallest(address);
      final String smallestCityWithZip    = TourLocationManager.getCustom_CityWithZip_Smallest(address);
      final String largestCity            = TourLocationManager.getCustom_City_Largest(address);
      final String largestCityWithZip     = TourLocationManager.getCustom_CityWithZip_Largest(address);
      final String streetWithHouseNumber  = TourLocationManager.getCustom_Street(address);

// SET_FORMATTING_ON

      boolean isShowSmallestCity = false;
      if (largestCity != null && largestCity.equals(smallestCity) == false) {
         isShowSmallestCity = true;
      }

      addCustomPart(LocationPart.CUSTOM_CITY_LARGEST, largestCity, allParts);
      if (isShowSmallestCity) {
         addCustomPart(LocationPart.CUSTOM_CITY_SMALLEST, smallestCity, allParts);
      }

      addCustomPart(LocationPart.CUSTOM_CITY_WITH_ZIP_LARGEST, largestCityWithZip, allParts);
      if (isShowSmallestCity) {
         addCustomPart(LocationPart.CUSTOM_CITY_WITH_ZIP_SMALLEST, smallestCityWithZip, allParts);
      }

      addCustomPart(LocationPart.CUSTOM_STREET_WITH_HOUSE_NUMBER, streetWithHouseNumber, allParts);

      // add "name" when available
      final String locationName = osmLocation.name;
      if (StringUtils.hasContent(locationName)) {

         allParts.add(new MT_DLItem(

               locationName,
               getFormattedPartName(LocationPart.OSM_NAME),
               LOCATION_KEY,
               LocationPart.OSM_NAME));
      }

      // add address parts
      addAllAddressParts(address, allParts);

      _listLocationParts.setItems(allParts);
   }

}

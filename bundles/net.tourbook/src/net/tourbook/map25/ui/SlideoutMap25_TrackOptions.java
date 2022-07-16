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
package net.tourbook.map25.ui;

import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import java.util.ArrayList;

import net.tourbook.Messages;
import net.tourbook.common.UI;
import net.tourbook.common.color.ColorSelectorExtended;
import net.tourbook.common.color.IColorSelectorListener;
import net.tourbook.common.color.MapGraphId;
import net.tourbook.common.font.MTFont;
import net.tourbook.common.tooltip.ToolbarSlideout;
import net.tourbook.common.util.Util;
import net.tourbook.map25.Map25App;
import net.tourbook.map25.Map25ConfigManager;
import net.tourbook.map25.Map25View;
import net.tourbook.map25.layer.tourtrack.Map25TrackConfig;
import net.tourbook.map25.layer.tourtrack.Map25TrackConfig.LineColorMode;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;

/**
 * Slideout for 2.5D tour track configuration
 */
public class SlideoutMap25_TrackOptions extends ToolbarSlideout implements IColorSelectorListener {

   private Map25View               _map25View;

   private MouseWheelListener      _defaultMouseWheelListener;
   private IPropertyChangeListener _defaultPropertyChangeListener;
   private SelectionListener       _defaultSelectionListener;
   private FocusListener           _keepOpenListener;

   private ActionTrackColor        _actionGradientColor_Elevation;
   private ActionTrackColor        _actionGradientColor_Gradient;
   private ActionTrackColor        _actionGradientColor_HrZone;
   private ActionTrackColor        _actionGradientColor_Pace;
   private ActionTrackColor        _actionGradientColor_Pulse;
   private ActionTrackColor        _actionGradientColor_Speed;

   private boolean                 _isUpdateUI;

   private PixelConverter          _pc;
   private int                     _firstColumnIndent;

   /*
    * UI controls
    */
   private Composite             _shellContainer;

   private Button                _chkShowDirectionArrows;
   private Button                _chkShowOutline;
   private Button                _chkShowSliderLocation;
   private Button                _chkShowSliderPath;
   private Button                _chkTrackVerticalOffset;

   private Button                _rdoColorMode_Gradient;
   private Button                _rdoColorMode_Solid;

   private Combo                 _comboName;

   private Label                 _lblConfigName;
   private Label                 _lblSliderLocation_Size;
   private Label                 _lblSliderLocation_Color;
   private Label                 _lblSliderPath_Width;
   private Label                 _lblSliderPath_Color;

   private Spinner               _spinnerLine_Opacity;
   private Spinner               _spinnerLine_Width;
   private Spinner               _spinnerOutline_Width;
   private Spinner               _spinnerOutline_Brighness;
   private Spinner               _spinnerSliderLocation_Size;
   private Spinner               _spinnerSliderLocation_Opacity;
   private Spinner               _spinnerSliderPath_LineWidth;
   private Spinner               _spinnerSliderPath_Opacity;
   private Spinner               _spinnerTrackVerticalOffset;

   private Spinner               _spinnerTESTValue;

   private Text                  _textConfigName;

   private ColorSelectorExtended _colorLine_SolidColor;
   private ColorSelectorExtended _colorSliderLocation_Left;
   private ColorSelectorExtended _colorSliderLocation_Right;
   private ColorSelectorExtended _colorSliderPathColor;

   private boolean               _isLineLayoutModified;

   private class ActionTrackColor extends Action {

      private MapGraphId _graphId;

      ActionTrackColor(final MapGraphId graphId) {

         super(UI.EMPTY_STRING, AS_CHECK_BOX);

         setImageDescriptor(net.tourbook.ui.UI.getGraphImageDescriptor(graphId));
         setDisabledImageDescriptor(net.tourbook.ui.UI.getGraphImageDescriptor_Disabled(graphId));

         _graphId = graphId;
      }

      @Override
      public void run() {
         onAction_GradientColor(_graphId);
      }

   }

   public SlideoutMap25_TrackOptions(final Composite ownerControl,
                                     final ToolBar toolbar,
                                     final Map25View map25View) {

      super(ownerControl, toolbar);

      _map25View = map25View;
   }

   @Override
   public void colorDialogOpened(final boolean isAnotherDialogOpened) {

      setIsAnotherDialogOpened(isAnotherDialogOpened);
   }

   private void createActions() {

      _actionGradientColor_Elevation = new ActionTrackColor(MapGraphId.Altitude);
      _actionGradientColor_Gradient = new ActionTrackColor(MapGraphId.Gradient);
      _actionGradientColor_HrZone = new ActionTrackColor(MapGraphId.HrZone);
      _actionGradientColor_Pace = new ActionTrackColor(MapGraphId.Pace);
      _actionGradientColor_Pulse = new ActionTrackColor(MapGraphId.Pulse);
      _actionGradientColor_Speed = new ActionTrackColor(MapGraphId.Speed);
   }

   @Override
   protected Composite createToolTipContentArea(final Composite parent) {

      initUI(parent);

      createActions();

      final Composite ui = createUI(parent);

      fillUI();
      restoreState();
      enableControls();

      return ui;
   }

   private Composite createUI(final Composite parent) {

      _shellContainer = new Composite(parent, SWT.NONE);
      GridLayoutFactory.fillDefaults().margins(UI.SHELL_MARGIN, UI.SHELL_MARGIN).applyTo(_shellContainer);
//      _shellContainer.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
      {
         final Composite container = new Composite(_shellContainer, SWT.NO_FOCUS);
         GridLayoutFactory.fillDefaults()
               .numColumns(2)
               .applyTo(container);
//         container.setBackground(UI.SYS_COLOR_YELLOW);
         {
            createUI_000_Title(container);

            createUI_100_Track(container);
            createUI_200_SliderLocation(container);
            createUI_210_SliderPath(container);

            createUI_999_ConfigName(container);
         }
      }

      return _shellContainer;
   }

   private void createUI_000_Title(final Composite parent) {

      {
         /*
          * Label: Title
          */
         final Label title = new Label(parent, SWT.LEAD);
         title.setText(Messages.Slideout_Map_TrackOptions_Label_Title);
         title.setToolTipText(Messages.Slideout_Map25TrackOptions_Label_ConfigName_Tooltip);
         MTFont.setBannerFont(title);
         GridDataFactory.fillDefaults()
               .grab(true, false)
               .align(SWT.BEGINNING, SWT.CENTER)
               .applyTo(title);
      }
      {

         /*
          * Combo: Configutation
          */
         _comboName = new Combo(parent, SWT.READ_ONLY | SWT.BORDER);
         _comboName.setVisibleItemCount(20);
         _comboName.addFocusListener(_keepOpenListener);
         _comboName.addSelectionListener(widgetSelectedAdapter(selectionEvent -> onSelectConfig()));
         GridDataFactory.fillDefaults()
               .grab(true, false)
               .align(SWT.BEGINNING, SWT.CENTER)
               // this is too small in linux
               // .hint(_pc.convertHorizontalDLUsToPixels(15 * 4), SWT.DEFAULT)
               .applyTo(_comboName);
      }
   }

   private void createUI_100_Track(final Composite parent) {

      final Group group = new Group(parent, SWT.NONE);
      group.setText(Messages.Slideout_Map_Options_Group_TourTrack);
      GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(group);
      GridLayoutFactory.swtDefaults().numColumns(2).applyTo(group);
//      group.setBackground(UI.SYS_COLOR_GREEN);
      {
         {
            /*
             * Line width
             */
            final String tooltipText = Messages.Slideout_Map25TrackOptions_Label_LineWidth_Tooltip;

            // label
            final Label label = new Label(group, SWT.NONE);
            label.setText(Messages.Slideout_Map25TrackOptions_Label_LineWidth);
            label.setToolTipText(tooltipText);
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).applyTo(label);

            // spinner
            _spinnerLine_Width = new Spinner(group, SWT.BORDER);
            _spinnerLine_Width.setToolTipText(tooltipText);
            _spinnerLine_Width.setMinimum(Map25ConfigManager.LINE_WIDTH_MIN);
            _spinnerLine_Width.setMaximum(Map25ConfigManager.LINE_WIDTH_MAX);
            _spinnerLine_Width.setIncrement(1);
            _spinnerLine_Width.setPageIncrement(10);
            _spinnerLine_Width.addSelectionListener(_defaultSelectionListener);
            _spinnerLine_Width.addMouseWheelListener(_defaultMouseWheelListener);
            GridDataFactory.fillDefaults()
                  .align(SWT.BEGINNING, SWT.FILL)
                  .applyTo(_spinnerLine_Width);
         }
         {
            /*
             * Outline
             */
            final String tooltipText = Messages.Slideout_Map25TrackOptions_Checkbox_Outline_Tooltip;

            _chkShowOutline = new Button(group, SWT.CHECK);
            _chkShowOutline.setText(Messages.Slideout_Map25TrackOptions_Checkbox_Outline);
            _chkShowOutline.setToolTipText(tooltipText);
            _chkShowOutline.addSelectionListener(_defaultSelectionListener);

            final Composite container = new Composite(group, SWT.NONE);
            GridDataFactory.fillDefaults().grab(true, false).applyTo(container);
            GridLayoutFactory.fillDefaults().numColumns(2).applyTo(container);
            {
               {
                  // outline width
                  _spinnerOutline_Width = new Spinner(container, SWT.BORDER);
                  _spinnerOutline_Width.setToolTipText(tooltipText);
                  _spinnerOutline_Width.setMinimum(0);
                  _spinnerOutline_Width.setMaximum(20);
                  _spinnerOutline_Width.setIncrement(1);
                  _spinnerOutline_Width.setPageIncrement(10);
                  _spinnerOutline_Width.addSelectionListener(_defaultSelectionListener);
                  _spinnerOutline_Width.addMouseWheelListener(_defaultMouseWheelListener);
               }
               {
                  // outline brightness/darkness
                  _spinnerOutline_Brighness = new Spinner(container, SWT.BORDER);
                  _spinnerOutline_Brighness.setToolTipText(tooltipText);
                  _spinnerOutline_Brighness.setMinimum(-10);
                  _spinnerOutline_Brighness.setMaximum(10);
                  _spinnerOutline_Brighness.setIncrement(1);
                  _spinnerOutline_Brighness.setPageIncrement(10);
                  _spinnerOutline_Brighness.addSelectionListener(_defaultSelectionListener);
                  _spinnerOutline_Brighness.addMouseWheelListener(_defaultMouseWheelListener);
               }
            }
         }
         {
            /*
             * Track vertical offset
             */
            _chkTrackVerticalOffset = new Button(group, SWT.CHECK);
            _chkTrackVerticalOffset.setText(Messages.Slideout_Map25TrackOptions_Checkbox_TrackVerticalOffset);
            _chkTrackVerticalOffset.addSelectionListener(_defaultSelectionListener);

            // offset value
            _spinnerTrackVerticalOffset = new Spinner(group, SWT.BORDER);
            _spinnerTrackVerticalOffset.setMinimum(-1000);
            _spinnerTrackVerticalOffset.setMaximum(1000);
            _spinnerTrackVerticalOffset.setIncrement(1);
            _spinnerTrackVerticalOffset.setPageIncrement(10);
            _spinnerTrackVerticalOffset.addSelectionListener(_defaultSelectionListener);
            _spinnerTrackVerticalOffset.addMouseWheelListener(_defaultMouseWheelListener);
         }
         {
            /*
             * Direction arrows
             */
            _chkShowDirectionArrows = new Button(group, SWT.CHECK);
            _chkShowDirectionArrows.setText(Messages.Slideout_Map25TrackOptions_Label_DirectionArrows);
            _chkShowDirectionArrows.setToolTipText(Messages.Slideout_Map25TrackOptions_Label_DirectionArrows_Tooltip);
            _chkShowDirectionArrows.addSelectionListener(_defaultSelectionListener);
            GridDataFactory.fillDefaults().span(2, 1).applyTo(_chkShowDirectionArrows);
         }
         {
            /*
             * Color
             */
            // label
            final Label label = new Label(group, SWT.NONE);
            label.setText(Messages.Slideout_Map25TrackOptions_Label_ColorMode);
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING)
                  .indent(0, 3) // align with the gradient label
                  .applyTo(label);

            final Composite containerColorMode = new Composite(group, SWT.NONE);
            GridDataFactory.fillDefaults().grab(true, false).applyTo(containerColorMode);
            GridLayoutFactory.fillDefaults().numColumns(2).applyTo(containerColorMode);
            {
               // radio: gradient
               _rdoColorMode_Gradient = new Button(containerColorMode, SWT.RADIO);
               _rdoColorMode_Gradient.setText(Messages.Slideout_Map25TrackOptions_Radio_ColorMode_Gradient);
               _rdoColorMode_Gradient.addSelectionListener(_defaultSelectionListener);

               {
                  /*
                   * Gradient actions
                   */
                  final ToolBarManager tbm = new ToolBarManager(new ToolBar(containerColorMode, SWT.FLAT));

                  tbm.add(_actionGradientColor_Elevation);
                  tbm.add(_actionGradientColor_Pulse);
                  tbm.add(_actionGradientColor_Speed);
                  tbm.add(_actionGradientColor_Pace);
                  tbm.add(_actionGradientColor_Gradient);
                  tbm.add(_actionGradientColor_HrZone);

                  tbm.update(true);
               }

               // radio: solid
               _rdoColorMode_Solid = new Button(containerColorMode, SWT.RADIO);
               _rdoColorMode_Solid.setText(Messages.Slideout_Map25TrackOptions_Radio_ColorMode_Solid);
               _rdoColorMode_Solid.addSelectionListener(_defaultSelectionListener);

               // color
               _colorLine_SolidColor = new ColorSelectorExtended(containerColorMode);
               _colorLine_SolidColor.addListener(_defaultPropertyChangeListener);
               _colorLine_SolidColor.addOpenListener(this);
            }
         }
         {
            /*
             * Color opacity
             */
            final int opacityMin = (int) ((Map25ConfigManager.LINE_OPACITY_MIN / 255.0f) * UI.TRANSFORM_OPACITY_MAX);
            final String tooltipText = NLS.bind(Messages.Slideout_Map25TrackOptions_Label_LineColorOpacity_Tooltip, UI.TRANSFORM_OPACITY_MAX);

            // label
            final Label label = new Label(group, SWT.NONE);
            label.setText(Messages.Slideout_Map25TrackOptions_Label_LineColorOpacity);
            label.setToolTipText(tooltipText);
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).applyTo(label);

            final Composite container = new Composite(group, SWT.NONE);
            GridDataFactory.fillDefaults().grab(true, false).applyTo(container);
            GridLayoutFactory.fillDefaults().numColumns(2).applyTo(container);
            {
               {
                  // opacity
                  _spinnerLine_Opacity = new Spinner(container, SWT.BORDER);
                  _spinnerLine_Opacity.setMinimum(opacityMin);
                  _spinnerLine_Opacity.setMaximum(UI.TRANSFORM_OPACITY_MAX);
                  _spinnerLine_Opacity.setIncrement(1);
                  _spinnerLine_Opacity.setPageIncrement(10);
                  _spinnerLine_Opacity.addSelectionListener(_defaultSelectionListener);
                  _spinnerLine_Opacity.addMouseWheelListener(_defaultMouseWheelListener);
               }
               {}
            }
         }
         {
            /*
             * TEST value
             */

            // label
            final Label label = new Label(group, SWT.NONE);
            label.setText("TEST value"); //$NON-NLS-1$
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).applyTo(label);

            // spinner
            _spinnerTESTValue = new Spinner(group, SWT.BORDER);
            _spinnerTESTValue.setMinimum(0);
            _spinnerTESTValue.setMaximum(100);
            _spinnerTESTValue.setIncrement(1);
            _spinnerTESTValue.setPageIncrement(10);
            _spinnerTESTValue.addSelectionListener(_defaultSelectionListener);
            _spinnerTESTValue.addMouseWheelListener(_defaultMouseWheelListener);
            GridDataFactory.fillDefaults()
                  .align(SWT.BEGINNING, SWT.FILL)
                  .applyTo(_spinnerTESTValue);
         }
      }
   }

   private void createUI_200_SliderLocation(final Composite parent) {

      final Composite container = new Composite(parent, SWT.NONE);
      GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(container);
      GridLayoutFactory.fillDefaults().numColumns(2).applyTo(container);
      {
         {
            /*
             * Chart slider
             */
            // checkbox
            _chkShowSliderLocation = new Button(container, SWT.CHECK);
            _chkShowSliderLocation.setText(Messages.Slideout_Map_Options_Checkbox_ChartSlider);
            _chkShowSliderLocation.addSelectionListener(_defaultSelectionListener);
            GridDataFactory.fillDefaults().span(2, 1).applyTo(_chkShowSliderLocation);
         }
         {
            /*
             * Size
             */

            // label
            _lblSliderLocation_Size = new Label(container, SWT.NONE);
            _lblSliderLocation_Size.setText(Messages.Slideout_Map_Options_Label_SliderLocation_Size);
            GridDataFactory.fillDefaults()
                  .indent(_firstColumnIndent, SWT.DEFAULT)
                  .align(SWT.FILL, SWT.CENTER)
                  .applyTo(_lblSliderLocation_Size);

            // size
            _spinnerSliderLocation_Size = new Spinner(container, SWT.BORDER);
            _spinnerSliderLocation_Size.setMinimum(Map25ConfigManager.SLIDER_LOCATION_SIZE_MIN);
            _spinnerSliderLocation_Size.setMaximum(Map25ConfigManager.SLIDER_LOCATION_SIZE_MAX);
            _spinnerSliderLocation_Size.setIncrement(1);
            _spinnerSliderLocation_Size.setPageIncrement(10);
            _spinnerSliderLocation_Size.addSelectionListener(_defaultSelectionListener);
            _spinnerSliderLocation_Size.addMouseWheelListener(_defaultMouseWheelListener);
         }
         {
            /*
             * Color
             */

            final int opacityMin = (int) ((Map25ConfigManager.SLIDER_LOCATION_OPACITY_MIN / 255.0f) * UI.TRANSFORM_OPACITY_MAX);
            final String tooltipText = NLS.bind(Messages.Slideout_Map_Options_Label_SliderLocation_Color_Tooltip, UI.TRANSFORM_OPACITY_MAX);

            // label
            _lblSliderLocation_Color = new Label(container, SWT.NONE);
            _lblSliderLocation_Color.setText(Messages.Slideout_Map_Options_Label_SliderLocation_Color);
            _lblSliderLocation_Color.setToolTipText(tooltipText);
            GridDataFactory.fillDefaults()
                  .indent(_firstColumnIndent, SWT.DEFAULT)
                  .align(SWT.FILL, SWT.CENTER)
                  .applyTo(_lblSliderLocation_Color);

            final Composite colorContainer = new Composite(container, SWT.NONE);
            GridDataFactory.fillDefaults().grab(true, false).applyTo(colorContainer);
            GridLayoutFactory.fillDefaults().numColumns(3).applyTo(colorContainer);
            {
               // opacity
               _spinnerSliderLocation_Opacity = new Spinner(colorContainer, SWT.BORDER);
               _spinnerSliderLocation_Opacity.setMinimum(opacityMin);
               _spinnerSliderLocation_Opacity.setMaximum(UI.TRANSFORM_OPACITY_MAX);
               _spinnerSliderLocation_Opacity.setIncrement(1);
               _spinnerSliderLocation_Opacity.setPageIncrement(10);
               _spinnerSliderLocation_Opacity.addSelectionListener(_defaultSelectionListener);
               _spinnerSliderLocation_Opacity.addMouseWheelListener(_defaultMouseWheelListener);

               // color: left
               _colorSliderLocation_Left = new ColorSelectorExtended(colorContainer);
               _colorSliderLocation_Left.addListener(_defaultPropertyChangeListener);
               _colorSliderLocation_Left.addOpenListener(this);

               // color: right
               _colorSliderLocation_Right = new ColorSelectorExtended(colorContainer);
               _colorSliderLocation_Right.addListener(_defaultPropertyChangeListener);
               _colorSliderLocation_Right.addOpenListener(this);
            }
         }
      }
   }

   private void createUI_210_SliderPath(final Composite parent) {

      final Composite container = new Composite(parent, SWT.NONE);
      GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(container);
      GridLayoutFactory.fillDefaults().numColumns(2).applyTo(container);
      {
         {
            /*
             * Slider path
             */
            // checkbox
            _chkShowSliderPath = new Button(container, SWT.CHECK);
            _chkShowSliderPath.setText(Messages.Slideout_Map_Options_Checkbox_SliderPath);
            _chkShowSliderPath.setToolTipText(Messages.Slideout_Map_Options_Checkbox_SliderPath_Tooltip);
            _chkShowSliderPath.addSelectionListener(_defaultSelectionListener);
            GridDataFactory.fillDefaults().span(2, 1).applyTo(_chkShowSliderPath);
         }
         {
            /*
             * Line width
             */

            // label
            _lblSliderPath_Width = new Label(container, SWT.NONE);
            _lblSliderPath_Width.setText(Messages.Slideout_Map_Options_Label_SliderPath_Width);
            GridDataFactory.fillDefaults()
                  .indent(_firstColumnIndent, SWT.DEFAULT)
                  .align(SWT.FILL, SWT.CENTER)
                  .applyTo(_lblSliderPath_Width);

            // spinner
            _spinnerSliderPath_LineWidth = new Spinner(container, SWT.BORDER);
            _spinnerSliderPath_LineWidth.setMinimum(Map25ConfigManager.SLIDER_PATH_LINE_WIDTH_MIN);
            _spinnerSliderPath_LineWidth.setMaximum(Map25ConfigManager.SLIDER_PATH_LINE_WIDTH_MAX);
            _spinnerSliderPath_LineWidth.setIncrement(1);
            _spinnerSliderPath_LineWidth.setPageIncrement(10);
            _spinnerSliderPath_LineWidth.addSelectionListener(_defaultSelectionListener);
            _spinnerSliderPath_LineWidth.addMouseWheelListener(_defaultMouseWheelListener);
         }
         {
            /*
             * Color + opacity
             */

            final int opacityMin = (int) ((Map25ConfigManager.SLIDER_PATH_OPACITY_MIN / 255.0f) * UI.TRANSFORM_OPACITY_MAX);
            final String tooltipText = NLS.bind(Messages.Slideout_Map_Options_Label_SliderPath_Color_Tooltip, UI.TRANSFORM_OPACITY_MAX);

            // label
            _lblSliderPath_Color = new Label(container, SWT.NONE);
            _lblSliderPath_Color.setText(Messages.Slideout_Map_Options_Label_SliderPath_Color);
            _lblSliderPath_Color.setToolTipText(tooltipText);
            GridDataFactory.fillDefaults()
                  .indent(_firstColumnIndent, SWT.DEFAULT)
                  .align(SWT.FILL, SWT.CENTER)
                  .applyTo(_lblSliderPath_Color);

            final Composite colorContainer = new Composite(container, SWT.NONE);
            GridDataFactory.fillDefaults().grab(true, false).applyTo(colorContainer);
            GridLayoutFactory.fillDefaults().numColumns(2).applyTo(colorContainer);
            {
               // opacity
               _spinnerSliderPath_Opacity = new Spinner(colorContainer, SWT.BORDER);
               _spinnerSliderPath_Opacity.setMinimum(opacityMin);
               _spinnerSliderPath_Opacity.setMaximum(UI.TRANSFORM_OPACITY_MAX);
               _spinnerSliderPath_Opacity.setIncrement(1);
               _spinnerSliderPath_Opacity.setPageIncrement(10);
               _spinnerSliderPath_Opacity.addSelectionListener(_defaultSelectionListener);
               _spinnerSliderPath_Opacity.addMouseWheelListener(_defaultMouseWheelListener);

               // color
               _colorSliderPathColor = new ColorSelectorExtended(colorContainer);
               _colorSliderPathColor.addListener(_defaultPropertyChangeListener);
               _colorSliderPathColor.addOpenListener(this);
            }
         }
      }
   }

   private void createUI_999_ConfigName(final Composite parent) {

      /*
       * Name
       */
      {
         /*
          * Label
          */
         _lblConfigName = new Label(parent, SWT.NONE);
         _lblConfigName.setText(Messages.Slideout_Map25TrackOptions_Label_Name);
         _lblConfigName.setToolTipText(Messages.Slideout_Map_TrackOptions_Label_Title_Tooltip);
         GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).applyTo(_lblConfigName);

         /*
          * Text
          */
         _textConfigName = new Text(parent, SWT.BORDER);
         GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).applyTo(_textConfigName);
         _textConfigName.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(final ModifyEvent e) {
               onModifyName();
            }
         });
      }
   }

   private void enableControls() {

      final boolean isShowSliderPath = _chkShowSliderPath.getSelection();
      final boolean isShowSliderLocation = _chkShowSliderLocation.getSelection();
      final boolean isTrackVerticalOffset = _chkTrackVerticalOffset.getSelection();
      final boolean isShowDirectionArrows = _chkShowDirectionArrows.getSelection();
      final boolean isShowOutline = _chkShowOutline.getSelection();

      final boolean isHideDirectionArrows = isShowDirectionArrows == false;

      /*
       * Track
       */
      _chkTrackVerticalOffset.setEnabled(isHideDirectionArrows);
      _spinnerOutline_Brighness.setEnabled(isShowOutline);
      _spinnerOutline_Width.setEnabled(isShowOutline);
      _spinnerTrackVerticalOffset.setEnabled(isHideDirectionArrows && isTrackVerticalOffset);

      /*
       * Slider location
       */
      _colorSliderLocation_Left.setEnabled(isShowSliderLocation);
      _colorSliderLocation_Right.setEnabled(isShowSliderLocation);

      _lblSliderLocation_Size.setEnabled(isShowSliderLocation);
      _lblSliderLocation_Color.setEnabled(isShowSliderLocation);

      _spinnerSliderLocation_Opacity.setEnabled(isShowSliderLocation);
      _spinnerSliderLocation_Size.setEnabled(isShowSliderLocation);

      /*
       * Slider path
       */
      _colorSliderPathColor.setEnabled(isShowSliderPath);

      _lblSliderPath_Color.setEnabled(isShowSliderPath);
      _lblSliderPath_Width.setEnabled(isShowSliderPath);

      _spinnerSliderPath_LineWidth.setEnabled(isShowSliderPath);
      _spinnerSliderPath_Opacity.setEnabled(isShowSliderPath);
   }

   private void fillUI() {

      final boolean backupIsUpdateUI = _isUpdateUI;
      _isUpdateUI = true;
      {
         for (final Map25TrackConfig config : Map25ConfigManager.getAllTourTrackConfigs()) {
            _comboName.add(config.name);
         }
      }
      _isUpdateUI = backupIsUpdateUI;
   }

   private void initUI(final Composite parent) {

      _pc = new PixelConverter(parent);
      _firstColumnIndent = _pc.convertWidthInCharsToPixels(3);

      _defaultSelectionListener = widgetSelectedAdapter(selectionEvent -> onModifyConfig());

      _defaultMouseWheelListener = mouseEvent -> {
         Util.adjustSpinnerValueOnMouseScroll(mouseEvent);
         onModifyConfig();
      };

      _defaultPropertyChangeListener = propertyChangeEvent -> onModifyConfig();

      /*
       * This will fix the problem that when the list of a combobox is displayed, then the
       * slideout will disappear :-(((
       */
      _keepOpenListener = new FocusListener() {

         @Override
         public void focusGained(final FocusEvent e) {
            setIsAnotherDialogOpened(true);
         }

         @Override
         public void focusLost(final FocusEvent e) {
            setIsAnotherDialogOpened(false);
         }
      };

   }

   private void onAction_GradientColor(final MapGraphId graphId) {

      _map25View.selectColorAction(graphId);

      // MUST be selected after _map25View.selectColorAction(graphId);
      selectGradientColorAction();
   }

   private void onModifyConfig() {

      saveState();

      enableControls();

      final Map25App mapApp = _map25View.getMapApp();

      mapApp.getLayer_Tour().onModifyConfig(_isLineLayoutModified);
      mapApp.getLayer_SliderPath().onModifyConfig();
      mapApp.getLayer_SliderLocation().onModifyConfig();
   }

   private void onModifyName() {

      if (_isUpdateUI) {
         return;
      }

      // update text in the combo
      final int selectedIndex = _comboName.getSelectionIndex();

      _comboName.setItem(selectedIndex, _textConfigName.getText());

      saveState();
   }

   private void onSelectConfig() {

      final int selectedIndex = _comboName.getSelectionIndex();
      final ArrayList<Map25TrackConfig> allConfigurations = Map25ConfigManager.getAllTourTrackConfigs();

      final Map25TrackConfig selectedConfig = allConfigurations.get(selectedIndex);
      final Map25TrackConfig trackConfig = Map25ConfigManager.getActiveTourTrackConfig();

      if (selectedConfig == trackConfig) {

         // config has not changed
         return;
      }

      // keep data from previous config
      saveState();

      // update model
      Map25ConfigManager.setActiveTrackConfig(selectedConfig);

      // update UI
      updateUI_SetActiveConfig();
   }

   /**
    * Restores state values from the tour track configuration and update the UI.
    */
   private void restoreState() {

      _isUpdateUI = true;

      final Map25TrackConfig config = Map25ConfigManager.getActiveTourTrackConfig();

      // get active config AFTER getting the index because this could change the active config
      final int activeConfigIndex = Map25ConfigManager.getActiveTourTrackConfigIndex();

// SET_FORMATTING_OFF

      _comboName                       .select(activeConfigIndex);
      _textConfigName                  .setText(config.name);

      // track line
      _chkShowDirectionArrows          .setSelection(config.isShowDirectionArrow);
      _chkTrackVerticalOffset          .setSelection(config.isTrackVerticalOffset);
      _spinnerLine_Width               .setSelection((int) (config.lineWidth));
      _spinnerTrackVerticalOffset      .setSelection(config.trackVerticalOffset);

      // track color
      _colorLine_SolidColor            .setColorValue(config.lineColor);
      _rdoColorMode_Gradient           .setSelection(config.lineColorMode.equals(LineColorMode.GRADIENT));
      _rdoColorMode_Solid              .setSelection(config.lineColorMode.equals(LineColorMode.SOLID));
      _spinnerLine_Opacity             .setSelection(UI.transformOpacity_WhenRestored(config.lineOpacity));

      // track outline
      _chkShowOutline                  .setSelection(config.isShowOutline);
      _spinnerOutline_Brighness        .setSelection((int) (config.outlineBrighness * 10));
      _spinnerOutline_Width            .setSelection((int) (config.outlineWidth));

      // slider location
      _chkShowSliderLocation           .setSelection(config.isShowSliderLocation);
      _colorSliderLocation_Left        .setColorValue(config.sliderLocation_Left_Color);
      _colorSliderLocation_Right       .setColorValue(config.sliderLocation_Right_Color);
      _spinnerSliderLocation_Opacity   .setSelection(UI.transformOpacity_WhenRestored(config.sliderLocation_Opacity));
      _spinnerSliderLocation_Size      .setSelection(config.sliderLocation_Size);

      // slider path
      _chkShowSliderPath               .setSelection(config.isShowSliderPath);
      _colorSliderPathColor            .setColorValue(config.sliderPath_Color);
      _spinnerSliderPath_LineWidth     .setSelection((int) (config.sliderPath_LineWidth));
      _spinnerSliderPath_Opacity       .setSelection(UI.transformOpacity_WhenRestored(config.sliderPath_Opacity));

      _spinnerTESTValue.setSelection((config.testValue));

// SET_FORMATTING_ON

      selectGradientColorAction();

      _isUpdateUI = false;
   }

   private void saveState() {

      // update config

      final Map25TrackConfig config = Map25ConfigManager.getActiveTourTrackConfig();

      final boolean isShowDirectionArrows = _chkShowDirectionArrows.getSelection();
      final int testValue = _spinnerTESTValue.getSelection();

      _isLineLayoutModified = config.isShowDirectionArrow != isShowDirectionArrows
            || config.testValue != testValue;

// SET_FORMATTING_OFF

      config.name                         = _textConfigName.getText();

      // track line
      config.isShowDirectionArrow         = isShowDirectionArrows;
      config.lineWidth                    = _spinnerLine_Width.getSelection();

      // track color
      config.lineColorMode                = _rdoColorMode_Gradient.getSelection() ? LineColorMode.GRADIENT : LineColorMode.SOLID;
      config.lineColor                    = _colorLine_SolidColor.getColorValue();
      config.lineOpacity                  = UI.transformOpacity_WhenSaved(_spinnerLine_Opacity.getSelection());

      // track outline
      config.isShowOutline                = _chkShowOutline.getSelection();
      config.outlineBrighness             = _spinnerOutline_Brighness.getSelection() / 10.0f;
      config.outlineWidth                 = _spinnerOutline_Width.getSelection();

      // track vertical offset
      config.isTrackVerticalOffset        = _chkTrackVerticalOffset.getSelection();
      config.trackVerticalOffset          = _spinnerTrackVerticalOffset.getSelection();

      // slider location
      config.isShowSliderLocation         = _chkShowSliderLocation.getSelection();
      config.sliderLocation_Left_Color    = _colorSliderLocation_Left.getColorValue();
      config.sliderLocation_Right_Color   = _colorSliderLocation_Right.getColorValue();
      config.sliderLocation_Opacity       = UI.transformOpacity_WhenSaved(_spinnerSliderLocation_Opacity.getSelection());
      config.sliderLocation_Size          = _spinnerSliderLocation_Size.getSelection();

      // slider path
      config.isShowSliderPath             = _chkShowSliderPath.getSelection();
      config.sliderPath_Color             = _colorSliderPathColor.getColorValue();
      config.sliderPath_LineWidth         = _spinnerSliderPath_LineWidth.getSelection();
      config.sliderPath_Opacity           = UI.transformOpacity_WhenSaved(_spinnerSliderPath_Opacity.getSelection());

      config.testValue                    = testValue;

// SET_FORMATTING_ON
   }

   private void selectGradientColorAction() {

      _actionGradientColor_Elevation.setChecked(false);
      _actionGradientColor_Pulse.setChecked(false);
      _actionGradientColor_Speed.setChecked(false);
      _actionGradientColor_Pace.setChecked(false);
      _actionGradientColor_Gradient.setChecked(false);
      _actionGradientColor_HrZone.setChecked(false);

      switch (_map25View.getTrackGraphId()) {

      case Pulse:
         _actionGradientColor_Pulse.setChecked(true);
         break;

      case Speed:
         _actionGradientColor_Speed.setChecked(true);
         break;

      case Pace:
         _actionGradientColor_Pace.setChecked(true);
         break;

      case Gradient:
         _actionGradientColor_Gradient.setChecked(true);
         break;

      case HrZone:
         _actionGradientColor_HrZone.setChecked(true);
         break;

      case Altitude:
      default:
         _actionGradientColor_Elevation.setChecked(true);
         break;
      }
   }

   private void updateUI_SetActiveConfig() {

      restoreState();

      enableControls();

      final Map25App mapApp = _map25View.getMapApp();

      mapApp.getLayer_Tour().onModifyConfig(_isLineLayoutModified);
      mapApp.getLayer_SliderPath().onModifyConfig();
      mapApp.getLayer_SliderLocation().onModifyConfig();
   }
}

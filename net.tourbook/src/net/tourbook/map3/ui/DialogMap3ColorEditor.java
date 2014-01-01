/*******************************************************************************
 * Copyright (C) 2005, 2014  Wolfgang Schramm and Contributors
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
/**
 * @author Wolfgang Schramm
 * @author Alfred Barten
 */
package net.tourbook.map3.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;

import net.tourbook.application.TourbookPlugin;
import net.tourbook.common.UI;
import net.tourbook.common.color.Map3ColorDefinition;
import net.tourbook.common.color.Map3ColorManager;
import net.tourbook.common.color.Map3ColorProfile;
import net.tourbook.common.color.Map3GradientColorProvider;
import net.tourbook.common.color.MapColorProfile;
import net.tourbook.common.color.MapGraphId;
import net.tourbook.common.color.ProfileImage;
import net.tourbook.common.color.RGBVertex;
import net.tourbook.common.util.Util;
import net.tourbook.common.widgets.ColorChooser;
import net.tourbook.common.widgets.IProfileColors;
import net.tourbook.common.widgets.ImageCanvas;
import net.tourbook.map2.view.TourMapPainter;
import net.tourbook.map3.Messages;
import net.tourbook.map3.action.ActionAddVertex;
import net.tourbook.map3.action.ActionDeleteVertex;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Widget;

/**
 * This color editor is editing a clone of the provided {@link Map3ColorProfile}.
 * <p>
 * The provided {@link IMap3ColorUpdater} is called after the color is modified.
 */
public class DialogMap3ColorEditor extends TitleAreaDialog implements IProfileColors {

	/*
	 * Map2 massages are defined here that externalizing strings can be done easily by disabling
	 * temporarily the messages.
	 */
	private static final String			MAP2_MESSAGE_1			= net.tourbook.map2.Messages.legendcolor_dialog_group_minmax_value;
	private static final String			MAP2_MESSAGE_2			= net.tourbook.map2.Messages.legendcolor_dialog_chk_max_value_text;
	private static final String			MAP2_MESSAGE_3			= net.tourbook.map2.Messages.legendcolor_dialog_chk_max_value_tooltip;
	private static final String			MAP2_MESSAGE_4			= net.tourbook.map2.Messages.legendcolor_dialog_txt_max_value;
	private static final String			MAP2_MESSAGE_5			= net.tourbook.map2.Messages.legendcolor_dialog_chk_min_value_text;
	private static final String			MAP2_MESSAGE_6			= net.tourbook.map2.Messages.legendcolor_dialog_chk_min_value_tooltip;
	private static final String			MAP2_MESSAGE_7			= net.tourbook.map2.Messages.legendcolor_dialog_txt_min_value;
	private static final String			MAP2_MESSAGE_8			= net.tourbook.map2.Messages.legendcolor_dialog_group_minmax_brightness;
	private static final String			MAP2_MESSAGE_9			= net.tourbook.map2.Messages.legendcolor_dialog_max_brightness_label;
	private static final String			MAP2_MESSAGE_10			= net.tourbook.map2.Messages.legendcolor_dialog_max_brightness_tooltip;
	private static final String			MAP2_MESSAGE_11			= net.tourbook.map2.Messages.legendcolor_dialog_min_brightness_label;
	private static final String			MAP2_MESSAGE_12			= net.tourbook.map2.Messages.legendcolor_dialog_min_brightness_tooltip;
	private static final String			MAP2_MESSAGE_13			= net.tourbook.map2.Messages.LegendColor_Dialog_Check_LiveUpdate;
	private static final String			MAP2_MESSAGE_14			= net.tourbook.map2.Messages.LegendColor_Dialog_Check_LiveUpdate_Tooltip;
	private static final String			MAP2_MESSAGE_15			= net.tourbook.map2.Messages.legendcolor_dialog_error_max_greater_min;
	//
	private static final int			SPINNER_MIN_VALUE		= -200;
	private static final int			SPINNER_MAX_VALUE		= 10000;

	private static final String			STATE_IS_LIVE_UPDATE	= "STATE_IS_LIVE_UPDATE";													//$NON-NLS-1$

	private static final String			DATA_KEY_VERTEX_INDEX	= "DATA_KEY_VERTEX_INDEX";													//$NON-NLS-1$
	private static final String			DATA_KEY_SORT_ID		= "DATA_KEY_SORT_ID";														//$NON-NLS-1$

	private final IDialogSettings		_state					= TourbookPlugin.getDefault().getDialogSettingsSection(
																		getClass().getName());

	/**
	 * Contains a clone from {@link #_originalColorProvider}.
	 */
	private Map3GradientColorProvider	_dialogColorProider;
	private Map3GradientColorProvider	_originalColorProvider;

	private boolean						_isNewColorProvider;

	private boolean						_isUIValid;
	private boolean						_isInUIUpdate;

	/*
	 * UI resources
	 */
	private ColorChooser				_colorChooser;

	private IMap3ColorUpdater			_mapColorUpdater;

	private MouseWheelListener			_mouseWheelListener;
	private SelectionAdapter			_selectionAdapter;

	/*
	 * UI controls
	 */
	private Shell						_shell;
	private Composite					_vertexOuterContainer;
	private ScrolledComposite			_vertexScrolledContainer;

	private ImageCanvas					_canvasProfileImage;
	private Image						_profileImage;

	private Button						_btnApply;
	private Button						_btnSave;
//	private Button						_btnRemove;
	private Button						_chkForceMinValue;
	private Button						_chkForceMaxValue;
	private Button						_chkLiveUpdate;

	private Combo						_cboGraphType;
	private Combo						_cboMinBrightness;
	private Combo						_cboMaxBrightness;

	private Label						_lblMinValue;
	private Label						_lblMaxValue;

	private Spinner						_spinMinBrightness;
	private Spinner						_spinMaxBrightness;
	private Spinner						_spinMinValue;
	private Spinner						_spinMaxValue;

	private Text						_txtProfileName;

	// vertex fields
	private Spinner[]					_spinnerVertexValue;
	private Label[]						_lblVertexColor;
	private ActionDeleteVertex[]		_actionDeleteVertex;
//	private Label[]						_lblDebug;

	{
		_selectionAdapter = new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				onSelectControl();
			}
		};

		_mouseWheelListener = new MouseWheelListener() {
			public void mouseScrolled(final MouseEvent event) {
				UI.adjustSpinnerValueOnMouseScroll(event);
				onSelectControl();
			}
		};
	}

	/**
	 * @param parentShell
	 * @param originalColorProvider
	 * @param mapColorUpdater
	 *            This updater is called when OK or Apply are pressed or Live Update is done.
	 * @param isNewProfile
	 */
	public DialogMap3ColorEditor(	final Shell parentShell,
									final Map3GradientColorProvider originalColorProvider,
									final IMap3ColorUpdater mapColorUpdater,
									final boolean isNewProfile) {

		super(parentShell);

		_mapColorUpdater = mapColorUpdater;

		// create a profile working copy
		_dialogColorProider = originalColorProvider.clone();
		_originalColorProvider = originalColorProvider;

		_isNewColorProvider = isNewProfile;

		// make dialog resizable
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	public void actionAddVertex() {

		// create new vertex at the beginning of the list
		getProfileImage().addVertex(0, new RGBVertex(_colorChooser.getRGB()));

		updateUI_FromModel_Vertices();

		onApply(false);
	}

	public void actionRemoveVertex(final int vertexIndex) {

		// update model
		final RGBVertex removedVertex = getRgbVertices().get(vertexIndex);

		getProfileImage().removeVertex(removedVertex);

		// update UI
		updateUI_FromModel_Vertices();

		onApply(false);
	}

	@Override
	public boolean close() {

		saveState();

		return super.close();
	}

	@Override
	protected void configureShell(final Shell shell) {

		super.configureShell(shell);

		_shell = shell;

		shell.setText(Messages.Map3Color_Dialog_Title);

		shell.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(final DisposeEvent e) {
				onDispose();
			}
		});

		shell.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(final ControlEvent e) {

				// allow resizing the height, preserve minimum width

				final Point defaultSize = shell.computeSize(SWT.DEFAULT, SWT.DEFAULT);
				final Point shellSize = shell.getSize();

				if (shellSize.x < defaultSize.x) {

				}

				final int width = defaultSize.x;
				final int height = shellSize.y;

				shell.setSize(width, height);
			}
		});
	}

	@Override
	public void create() {

		// create UI
		super.create();

		setTitle(Messages.Map3Color_Dialog_Title);
		setMessage(Messages.Map3Color_Dialog_Message);

		restoreState();

		updateUI_FromModel();
		enableGraphType();

		// set UI default behaviour
		_txtProfileName.setFocus();

		if (_isNewColorProvider) {

			// select whole profile name that it can be easily overwritten
			_txtProfileName.selectAll();
		}
	}

	/**
	 * Creates an action in a toolbar.
	 * 
	 * @param parent
	 * @param action
	 * @return
	 */
	private ToolBarManager createActionButton(final Composite parent, final Action action) {

		final ToolBar toolbar = new ToolBar(parent, SWT.FLAT);

		final ToolBarManager tbm = new ToolBarManager(toolbar);
		tbm.add(action);
		tbm.update(true);

		return tbm;
	}

	@Override
	protected Control createButtonBar(final Composite parent) {

		return createUI_98_ButtonBar(parent);
	}

	@Override
	protected final void createButtonsForButtonBar(final Composite parent) {

		createUI_99_ButtonsForButtonBar(parent);
	}

	@Override
	protected Control createDialogArea(final Composite parent) {

		final Composite dlgContainer = (Composite) super.createDialogArea(parent);

		createUI(dlgContainer);

		updateUI_Initialize();

		return dlgContainer;
	}

	private void createUI(final Composite parent) {

		final Composite uiContainer = new Composite(parent, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(uiContainer);
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(uiContainer);
//		uiContainer.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW));
		{
			final Composite configContainer = new Composite(uiContainer, SWT.NONE);
			GridDataFactory.fillDefaults().grab(true, true).applyTo(configContainer);
			GridLayoutFactory.swtDefaults().numColumns(1).spacing(0, 15).applyTo(configContainer);
//			configContainer.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
			{
				createUI_10_Names(configContainer);

				final Composite configInnerContainer = new Composite(configContainer, SWT.NONE);
				GridDataFactory.fillDefaults().grab(true, true).applyTo(configInnerContainer);
				GridLayoutFactory.fillDefaults().numColumns(2).applyTo(configInnerContainer);
//				configInnerContainer.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
				{
					createUI_30_ProfileImage(configInnerContainer);

					final Composite vertexContainer = new Composite(configInnerContainer, SWT.NONE);
					GridDataFactory.fillDefaults().grab(true, true).applyTo(vertexContainer);
					GridLayoutFactory.fillDefaults().numColumns(1).applyTo(vertexContainer);
//					vertexContainer.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
					{
						createUI_40_VertexFields(vertexContainer);
						createUI_60_MinMaxValue(vertexContainer);
						createUI_62_Brightness(vertexContainer);
					}
				}
			}

			createUI_80_ColorChooser(uiContainer);
		}
	}

	private void createUI_10_Names(final Composite parent) {

		final Composite container = new Composite(parent, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(container);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(container);
		{
			{
				/*
				 * Graph type
				 */
				final Label label = new Label(container, SWT.NONE);
				label.setText(Messages.Map3Color_Dialog_Button_Label_GraphType);
				label.setToolTipText(Messages.Map3Color_Dialog_Button_Label_GraphType_Tooltip);

				_cboGraphType = new Combo(container, SWT.DROP_DOWN | SWT.READ_ONLY);
				_cboGraphType.addSelectionListener(_selectionAdapter);
			}

			{
				/*
				 * Profile name
				 */
				final Label label = new Label(container, SWT.NONE);
				label.setText(Messages.Map3Color_Dialog_Button_Label_ProfileName);

				_txtProfileName = new Text(container, SWT.BORDER);
				GridDataFactory.fillDefaults().grab(true, false).applyTo(_txtProfileName);

				_txtProfileName.addFocusListener(new FocusListener() {
					@Override
					public void focusGained(final FocusEvent e) {}

					@Override
					public void focusLost(final FocusEvent e) {
						// do live update
						onModifyProfileName();
					}
				});
			}
		}
	}

	private void createUI_30_ProfileImage(final Composite parent) {

		/*
		 * profile image
		 */
		_canvasProfileImage = new ImageCanvas(parent, SWT.DOUBLE_BUFFERED);
		GridDataFactory.fillDefaults()//
				.grab(true, true)
//				.minSize(SWT.DEFAULT, 20)
//				.hint(SWT.DEFAULT, 20)
				.applyTo(_canvasProfileImage);

		_canvasProfileImage.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(final ControlEvent e) {
				drawProfileImage();
			}
		});
	}

	private void createUI_40_VertexFields(final Composite parent) {

		/*
		 * vertex fields container
		 */
		_vertexOuterContainer = new Composite(parent, SWT.NONE);
		GridDataFactory.fillDefaults()//
				.grab(true, true)
				.applyTo(_vertexOuterContainer);

		GridLayoutFactory.fillDefaults().applyTo(_vertexOuterContainer);
//		_vertexOuterContainer.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));

		/*
		 * Create fields that they are being initially displayed, otherwise the will be created but
		 * NOT visible
		 */
		createUI_50_VertexFields();
	}

	/**
	 * Create the vertex fields from the vertex list
	 * 
	 * @param parent
	 */
	private void createUI_50_VertexFields() {

		final ArrayList<RGBVertex> rgbVerticies = getRgbVertices();

		final int vertexSize = rgbVerticies.size();

		if (vertexSize == 0) {
			// this case should not happen
			return;
		}

		// check if required vertex fields are already available
		if (_lblVertexColor != null && _lblVertexColor.length == vertexSize) {
			return;
		}

		final Composite parent = _vertexOuterContainer;
		final Display display = parent.getDisplay();

		Point scrollOrigin = null;

		// dispose previous content
		if (_vertexScrolledContainer != null) {

			// get current scroll position
			scrollOrigin = _vertexScrolledContainer.getOrigin();

			_vertexScrolledContainer.dispose();
		}

		final Composite vertexContainer = createUI_52_VertexScrolledContainer(parent);

		/*
		 * Field listener
		 */
		final MouseAdapter colorMouseListener = new MouseAdapter() {
			@Override
			public void mouseDown(final MouseEvent e) {
				onFieldMouseDown(display, e);
			}
		};

		// value listener
		final SelectionListener valueSelectionListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				onFieldSelectValue(event.widget);
			}
		};
		final MouseWheelListener valueMouseWheelListener = new MouseWheelListener() {
			public void mouseScrolled(final MouseEvent event) {
				UI.adjustSpinnerValueOnMouseScroll(event);
				onFieldSelectValue(event.widget);
			}
		};

		/*
		 * fields
		 */
//		_lblDebug = new Label[vertexSize];
		_lblVertexColor = new Label[vertexSize];
		_spinnerVertexValue = new Spinner[vertexSize];
		_actionDeleteVertex = new ActionDeleteVertex[vertexSize];

		for (int vertexIndex = 0; vertexIndex < vertexSize; vertexIndex++) {

			/*
			 * Spinner: Vertex value
			 */
			final Spinner spinnerValue = new Spinner(vertexContainer, SWT.BORDER);
			GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).applyTo(spinnerValue);
			spinnerValue.setMinimum(Integer.MIN_VALUE);
			spinnerValue.setMaximum(Integer.MAX_VALUE);
			spinnerValue.addSelectionListener(valueSelectionListener);
			spinnerValue.addMouseWheelListener(valueMouseWheelListener);

			/*
			 * Label: Value color
			 */
			final Label lblColor = new Label(vertexContainer, SWT.CENTER | SWT.BORDER | SWT.SHADOW_NONE);
			GridDataFactory.fillDefaults()//
					.grab(true, false)
					.hint(70, 10)
					.applyTo(lblColor);
			lblColor.addMouseListener(colorMouseListener);

			/*
			 * Action: Delete vertex
			 */
			final ActionDeleteVertex actionDeleteVertex = new ActionDeleteVertex(this);

			createActionButton(vertexContainer, actionDeleteVertex);

			// keep vertex controls
			_spinnerVertexValue[vertexIndex] = spinnerValue;
			_lblVertexColor[vertexIndex] = lblColor;
			_actionDeleteVertex[vertexIndex] = actionDeleteVertex;

//			/*
//			 * Debug label
//			 */
//			final Label lblDebug = new Label(vertexContainer, SWT.NONE);
//			GridDataFactory.fillDefaults().hint(200, SWT.DEFAULT).applyTo(lblDebug);
//			_lblDebug[vertexIndex] = lblDebug;

		}

		// spacer
		new Label(vertexContainer, SWT.NONE);
		new Label(vertexContainer, SWT.NONE);

		/*
		 * Action: Add vertex
		 */
		createActionButton(vertexContainer, new ActionAddVertex(this));

		_vertexOuterContainer.layout(true);

		// set scroll position to previous position
		if (scrollOrigin != null) {
			_vertexScrolledContainer.setOrigin(scrollOrigin);
		}
	}

	private Composite createUI_52_VertexScrolledContainer(final Composite parent) {

		// scrolled container
		_vertexScrolledContainer = new ScrolledComposite(parent, SWT.V_SCROLL);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(_vertexScrolledContainer);
		_vertexScrolledContainer.setExpandVertical(true);
		_vertexScrolledContainer.setExpandHorizontal(true);

		// vertex container
		final Composite vertexContainer = new Composite(_vertexScrolledContainer, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(vertexContainer);
		GridLayoutFactory.fillDefaults()//
				.numColumns(3)
//				.spacing(10, LayoutConstants.getSpacing().y)
				.applyTo(vertexContainer);
//		vertexContainer.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));

		_vertexScrolledContainer.setContent(vertexContainer);
		_vertexScrolledContainer.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(final ControlEvent e) {
				_vertexScrolledContainer.setMinSize(vertexContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			}
		});

		return vertexContainer;
	}

	private void createUI_60_MinMaxValue(final Composite parent) {

		final Group group = new Group(parent, SWT.NONE);
		GridDataFactory.fillDefaults()//
				.grab(true, false)
				.indent(0, 10)
				.applyTo(group);
		GridLayoutFactory.swtDefaults().numColumns(3).applyTo(group);
		group.setText(MAP2_MESSAGE_1);
//		group.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_MAGENTA));
		{
			{
				/*
				 * Overwrite max value
				 */
				_chkForceMaxValue = new Button(group, SWT.CHECK);
				GridDataFactory.fillDefaults()//
						.grab(true, false)
						.applyTo(_chkForceMaxValue);
				_chkForceMaxValue.setText(MAP2_MESSAGE_2);
				_chkForceMaxValue.setToolTipText(MAP2_MESSAGE_3);
				_chkForceMaxValue.addSelectionListener(_selectionAdapter);

				_lblMaxValue = new Label(group, SWT.NONE);
				_lblMaxValue.setText(MAP2_MESSAGE_4);
				GridDataFactory.fillDefaults()//
//						.indent(20, 0)
						.align(SWT.FILL, SWT.CENTER)
						.applyTo(_lblMaxValue);

				_spinMaxValue = new Spinner(group, SWT.BORDER);
				_spinMaxValue.setMinimum(SPINNER_MIN_VALUE);
				_spinMaxValue.setMaximum(SPINNER_MAX_VALUE);
				_spinMaxValue.addSelectionListener(_selectionAdapter);
				_spinMaxValue.addMouseWheelListener(_mouseWheelListener);
			}
			{
				/*
				 * Overwrite min value
				 */
				_chkForceMinValue = new Button(group, SWT.CHECK);
				GridDataFactory.fillDefaults()//
						.grab(true, false)
						.applyTo(_chkForceMinValue);
				_chkForceMinValue.setText(MAP2_MESSAGE_5);
				_chkForceMinValue.setToolTipText(MAP2_MESSAGE_6);
				_chkForceMinValue.addSelectionListener(_selectionAdapter);

				_lblMinValue = new Label(group, SWT.NONE);
				GridDataFactory.fillDefaults()//
//						.indent(20, 0)
						.align(SWT.FILL, SWT.CENTER)
						.applyTo(_lblMinValue);
				_lblMinValue.setText(MAP2_MESSAGE_7);

				_spinMinValue = new Spinner(group, SWT.BORDER);
				_spinMinValue.setMinimum(SPINNER_MIN_VALUE);
				_spinMinValue.setMaximum(SPINNER_MAX_VALUE);
				_spinMinValue.addSelectionListener(_selectionAdapter);
				_spinMinValue.addMouseWheelListener(_mouseWheelListener);
			}
		}
	}

	private void createUI_62_Brightness(final Composite parent) {

		Label label;

		final Group group = new Group(parent, SWT.NONE);
		GridDataFactory.fillDefaults()//
				.grab(true, false)
//				.indent(0, 40)
				.applyTo(group);
		GridLayoutFactory.swtDefaults().numColumns(3).applyTo(group);
		group.setText(MAP2_MESSAGE_8);
//		group.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_CYAN));
		{
			{
				/*
				 * Max brightness
				 */
				label = new Label(group, SWT.NONE);
				GridDataFactory.fillDefaults()//
						.grab(true, false)
						.align(SWT.FILL, SWT.CENTER)
						.applyTo(label);
				label.setText(MAP2_MESSAGE_9);
				label.setToolTipText(MAP2_MESSAGE_10);

				_cboMaxBrightness = new Combo(group, SWT.DROP_DOWN | SWT.READ_ONLY);
				_cboMaxBrightness.addSelectionListener(_selectionAdapter);

				_spinMaxBrightness = new Spinner(group, SWT.BORDER);
				_spinMaxBrightness.setMinimum(0);
				_spinMaxBrightness.setMaximum(100);
				_spinMaxBrightness.setPageIncrement(10);
				_spinMaxBrightness.addSelectionListener(_selectionAdapter);
				_spinMaxBrightness.addMouseWheelListener(_mouseWheelListener);
			}
			{
				/*
				 * Min brightness
				 */
				label = new Label(group, SWT.NONE);
				GridDataFactory.fillDefaults()//
						.grab(true, false)
						.align(SWT.FILL, SWT.CENTER)
						.applyTo(label);
				label.setText(MAP2_MESSAGE_11);
				label.setToolTipText(MAP2_MESSAGE_12);

				_cboMinBrightness = new Combo(group, SWT.DROP_DOWN | SWT.READ_ONLY);
				_cboMinBrightness.addSelectionListener(_selectionAdapter);

				_spinMinBrightness = new Spinner(group, SWT.BORDER);
				_spinMinBrightness.setMinimum(0);
				_spinMinBrightness.setMaximum(100);
				_spinMinBrightness.setPageIncrement(10);
				_spinMinBrightness.addSelectionListener(_selectionAdapter);
				_spinMinBrightness.addMouseWheelListener(_mouseWheelListener);
			}
		}
	}

	/**
	 * Color chooser
	 */
	private void createUI_80_ColorChooser(final Composite parent) {

		final Composite container = new Composite(parent, SWT.NONE);
		GridDataFactory.fillDefaults().grab(false, true).applyTo(container);
		GridLayoutFactory.fillDefaults()//
				.extendedMargins(10, 5, 5, 5)
				.numColumns(1)
				.applyTo(container);
		{
			_colorChooser = new ColorChooser(container, SWT.NONE);
			GridDataFactory.fillDefaults()//
					.grab(false, true)
					.applyTo(_colorChooser);

			_colorChooser.setProfileColors(this);
		}
	}

	private Control createUI_98_ButtonBar(final Composite parent) {

		Control containerButtonBar;

		/*
		 * Live update checkbox is created here that it can be left aligned in a separate container.
		 */
		final Composite container = new Composite(parent, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(container);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(container);
		{
			{
				/*
				 * Checkbox: live update
				 */
				_chkLiveUpdate = new Button(container, SWT.CHECK);
				GridDataFactory.fillDefaults()//
						.grab(true, false)
						.indent(convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN), 0)
						.applyTo(_chkLiveUpdate);
				_chkLiveUpdate.setText(MAP2_MESSAGE_13);
				_chkLiveUpdate.setToolTipText(MAP2_MESSAGE_14);
				_chkLiveUpdate.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(final SelectionEvent e) {
						onSelectControl();
					}
				});
			}

			containerButtonBar = super.createButtonBar(container);
		}

		return containerButtonBar;
	}

	private void createUI_99_ButtonsForButtonBar(final Composite parent) {

//		parent.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));

		{
			/*
			 * Button: Apply
			 */
			_btnApply = createButton(
					parent,
					IDialogConstants.CLIENT_ID + 6,
					Messages.Map3Color_Dialog_Button_Apply,
					false);
			_btnApply.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent e) {
					onApply(true);
				}
			});
		}

		// create default buttons (OK, Cancel)
		super.createButtonsForButtonBar(parent);

		{
			/*
			 * Button: Save
			 */
			// set text for the OK button
			_btnSave = getButton(IDialogConstants.OK_ID);
			_btnSave.setText(Messages.Map3Color_Dialog_Button_Save);
		}
	}

	private void drawProfileImage() {

		UI.disposeResource(_profileImage);

		final Rectangle imageBounds = _canvasProfileImage.getBounds();

		final int imageWidth = imageBounds.width;
		final int imageHeight = imageBounds.height;

		_dialogColorProider.configureColorProvider(imageHeight, getRgbVertices());

		_profileImage = TourMapPainter.createMapLegendImage(
				Display.getCurrent(),
				_dialogColorProider,
				imageWidth,
				imageHeight);

		_canvasProfileImage.setImage(_profileImage);
	}

	private void enableControls() {

		final ArrayList<RGBVertex> rgbVertices = getRgbVertices();
		final int verticesSize = rgbVertices.size();

		final boolean isValid = verticesSize > 0 && validateFields();

		// min brightness
		final int minBrightness = _cboMinBrightness.getSelectionIndex();
		_spinMinBrightness.setEnabled(minBrightness != 0);

		// max brightness
		final int maxBrightness = _cboMaxBrightness.getSelectionIndex();
		_spinMaxBrightness.setEnabled(maxBrightness != 0);

		// min value
		boolean isChecked = _chkForceMinValue.getSelection();
		_lblMinValue.setEnabled(isChecked);
		_spinMinValue.setEnabled(isChecked);

		// max value
		isChecked = _chkForceMaxValue.getSelection();
		_lblMaxValue.setEnabled(isChecked);
		_spinMaxValue.setEnabled(isChecked);

		/*
		 * Vertex trash actions
		 */
		final boolean canRemoveVertices = verticesSize > 2;
		for (final ActionDeleteVertex actionDeletevertex : _actionDeleteVertex) {
			actionDeletevertex.setEnabled(canRemoveVertices);
		}

		/*
		 * Save/Apply buttons
		 */
		final boolean isLiveUpdate = _chkLiveUpdate.getSelection();
		final boolean canSave = isValid && isLiveUpdate == false;

		_btnApply.setEnabled(canSave);
		_btnSave.setEnabled(canSave);

		_chkLiveUpdate.setEnabled(isValid);
	}

	/**
	 * A graph type can only be selected, when more than one color providers are available for the
	 * current graph type.
	 */
	private void enableGraphType() {

		boolean canEnableGraphType = false;

		if (_isNewColorProvider) {

			canEnableGraphType = true;

		} else {

			final MapGraphId graphId = _dialogColorProider.getGraphId();

			canEnableGraphType = Map3ColorManager.getColorProviders(graphId).size() > 1;
		}

		_cboGraphType.setEnabled(canEnableGraphType);
	}

	@Override
	protected IDialogSettings getDialogBoundsSettings() {

		// keep window size and position
		return _state;
	}

	private int getGraphIdIndex(final MapGraphId colorId) {

		final ArrayList<Map3ColorDefinition> colorDefinitions = Map3ColorManager.getSortedColorDefinitions();

		for (int devIndex = 0; devIndex < colorDefinitions.size(); devIndex++) {

			final Map3ColorDefinition colorDefinition = colorDefinitions.get(devIndex);

			if (colorDefinition.getGraphId().equals(colorId)) {
				return devIndex;
			}
		}

		return 0;
	}

	@Override
	protected Point getInitialSize() {

		final Point initialSize = super.getInitialSize();
		final Point defaultSize = _shell.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);

		// enforce dialog is opened and all controls are visible
		if (initialSize.y < defaultSize.y) {
			initialSize.y = defaultSize.y;
		}

		return initialSize;
	}

	@Override
	public RGB[] getProfileColors() {

		/*
		 * create a set with all profile colors
		 */
		final LinkedHashSet<RGB> profileColors = new LinkedHashSet<RGB>();

		for (final RGBVertex rgbVertex : getRgbVertices()) {
			profileColors.add(rgbVertex.getRGB());
		}

		return profileColors.toArray(new RGB[profileColors.size()]);
	}

	private ProfileImage getProfileImage() {

		return _dialogColorProider.getMap3ColorProfile().getProfileImage();
	}

	private ArrayList<RGBVertex> getRgbVertices() {

		return getProfileImage().getRgbVertices();
	}

	private MapGraphId getSelectedGraphId() {

		final ArrayList<Map3ColorDefinition> colorDefinitions = Map3ColorManager.getSortedColorDefinitions();
		final int selectionIndex = _cboGraphType.getSelectionIndex();

		final Map3ColorDefinition selectedColorDef = colorDefinitions.get(selectionIndex);

		return selectedColorDef.getGraphId();
	}

	/**
	 * Save button is pressed.
	 * <p>
	 * {@inheritDoc}
	 */
	@Override
	protected void okPressed() {

		onApply(true);

		super.okPressed();
	}

	private void onApply(final boolean isForceLiveUpdate) {

		updateModel_FromUI();

		if (isForceLiveUpdate || (_chkLiveUpdate.isEnabled() && _chkLiveUpdate.getSelection())) {

			_mapColorUpdater.applyMapColors(_originalColorProvider, _dialogColorProider, _isNewColorProvider);

			// after an update, a color provider is not new any more otherwise each update creates a new profile
			_isNewColorProvider = false;

			// set model and UI as when the dialog has been opened
			_originalColorProvider = _dialogColorProider;
			_dialogColorProider = _originalColorProvider.clone();

			updateUI_FromModel();

			enableGraphType();
		}
	}

	private void onDispose() {

		UI.disposeResource(_profileImage);
	}

	/**
	 * Set/push color.
	 * 
	 * @param display
	 * @param event
	 */
	private void onFieldMouseDown(final Display display, final MouseEvent event) {

		final Label vertexLabel = (Label) (event.widget);
		final Integer vertexIndex = (Integer) vertexLabel.getData(DATA_KEY_VERTEX_INDEX);
		final RGBVertex vertex = getRgbVertices().get(vertexIndex);

		if (event.button == 3) {

			// right button: update color chooser from vertex color

			_colorChooser.setRGB(vertex.getRGB());

		} else {

			// other buttons: update vertex color from color chooser

			final RGB rgb = _colorChooser.getRGB();
			updateUI_LabelColor(display, vertexLabel, rgb);

			vertex.setRGB(rgb);

			// invalidate cached colors
			getProfileImage().invalidateCachedColors();

			updateUI_FromModel_Vertices();

			onApply(false);
		}
	}

	private void onFieldSelectValue(final Widget widget) {

		if (_isInUIUpdate) {
			return;
		}

		final Spinner spinner = (Spinner) widget;
		final Integer vertexIndex = (Integer) spinner.getData(DATA_KEY_VERTEX_INDEX);
		final RGBVertex vertex = getRgbVertices().get(vertexIndex);

		// update model
		vertex.setValue(spinner.getSelection());

		updateModel_FromUI_Vertices();

		// update UI
		updateUI_FromModel_Vertices();

		onApply(false);
	}

	private void onModifyProfileName() {

		if (_isInUIUpdate) {
			return;
		}

		onApply(false);
	}

	private void onSelectControl() {

		updateModel_FromUI();
		updateUI_FromModel();

		onApply(false);
	}

	private void restoreState() {

		_colorChooser.restoreState(_state);

		_chkLiveUpdate.setSelection(Util.getStateBoolean(_state, STATE_IS_LIVE_UPDATE, false));
	}

	private void saveState() {

		_colorChooser.saveState(_state);

		_state.put(STATE_IS_LIVE_UPDATE, _chkLiveUpdate.getSelection());
	}

	private void updateModel_FromUI() {

		// update color provider, set graph id
		final MapGraphId selectedGraphId = getSelectedGraphId();
		_dialogColorProider.setGraphId(selectedGraphId);

		/*
		 * Update color profile
		 */
		final Map3ColorProfile colorProfile = _dialogColorProider.getMap3ColorProfile();

		colorProfile.setProfileName(_txtProfileName.getText());

		// update min/max brightness
		colorProfile.setMinBrightness(_cboMinBrightness.getSelectionIndex());
		colorProfile.setMaxBrightness(_cboMaxBrightness.getSelectionIndex());
		colorProfile.setMinBrightnessFactor(_spinMinBrightness.getSelection());
		colorProfile.setMaxBrightnessFactor(_spinMaxBrightness.getSelection());

		// update min/max value
		colorProfile.setIsMinValueOverwrite(_chkForceMinValue.getSelection());
		colorProfile.setIsMaxValueOverwrite(_chkForceMaxValue.getSelection());
		colorProfile.setMinValueOverwrite(_spinMinValue.getSelection());
		colorProfile.setMaxValueOverwrite(_spinMaxValue.getSelection());

		updateModel_FromUI_Vertices();
	}

	/**
	 * Get vertices from UI and sorts them.
	 */
	private void updateModel_FromUI_Vertices() {

		final ArrayList<RGBVertex> rgbVertices = getRgbVertices();
		final int rgbVertexListSize = rgbVertices.size();
		final ArrayList<RGBVertex> newRgbVertices = new ArrayList<RGBVertex>();

		for (int vertexIndex = 0; vertexIndex < rgbVertexListSize; vertexIndex++) {

			/*
			 * create vertices from UI controls
			 */
			final Spinner spinnerVertexValue = _spinnerVertexValue[vertexIndex];

			final int value = spinnerVertexValue.getSelection();
			final Integer sortId = (Integer) spinnerVertexValue.getData(DATA_KEY_SORT_ID);

			final RGB rgb = _lblVertexColor[vertexIndex].getBackground().getRGB();

			final RGBVertex rgbVertex = new RGBVertex(sortId);
			rgbVertex.setValue(value);
			rgbVertex.setRGB(rgb);

			newRgbVertices.add(rgbVertex);
		}

		// sort vertices by value
		Collections.sort(newRgbVertices);

		// update model
		getProfileImage().setVertices(newRgbVertices);
	}

	private void updateUI_FromModel() {

		_isInUIUpdate = true;
		{
			final Map3ColorProfile colorProfile = _dialogColorProider.getMap3ColorProfile();

			_txtProfileName.setText(colorProfile.getProfileName());

			final MapGraphId graphId = _dialogColorProider.getGraphId();

			_cboGraphType.select(getGraphIdIndex(graphId));

			// update min/max brightness
			_cboMinBrightness.select(colorProfile.getMinBrightness());
			_cboMaxBrightness.select(colorProfile.getMaxBrightness());
			_spinMinBrightness.setSelection(colorProfile.getMinBrightnessFactor());
			_spinMaxBrightness.setSelection(colorProfile.getMaxBrightnessFactor());

			// update min/max value
			_chkForceMinValue.setSelection(colorProfile.isMinValueOverwrite());
			_chkForceMaxValue.setSelection(colorProfile.isMaxValueOverwrite());
			_spinMinValue.setSelection(colorProfile.getMinValueOverwrite());
			_spinMaxValue.setSelection(colorProfile.getMaxValueOverwrite());
		}
		_isInUIUpdate = false;

		updateUI_FromModel_Vertices();

		enableControls();
	}

	private void updateUI_FromModel_Vertices() {

		// check vertex fields
		createUI_50_VertexFields();

		final ArrayList<RGBVertex> rgbVerticies = getRgbVertices();

		final int vertexSize = rgbVerticies.size();

		_isInUIUpdate = true;
		{
			for (int vertexIndex = 0; vertexIndex < vertexSize; vertexIndex++) {

				// show highest value at the top accoringly to the displayed legend
//				final int keyIndex = vertexIndex;
				final int keyIndex = vertexSize - 1 - vertexIndex;
				final RGBVertex vertex = rgbVerticies.get(keyIndex);

				// update value
				final Spinner spinnerValue = _spinnerVertexValue[vertexIndex];
				spinnerValue.setSelection(vertex.getValue());

				// update color
				final Label lblColor = _lblVertexColor[vertexIndex];
				final RGB vertexRGB = vertex.getRGB();

				lblColor.setToolTipText(NLS.bind(//
						Messages.Map3Color_Dialog_ProfileColor_Tooltip,
						new Object[] { vertexRGB.red, vertexRGB.green, vertexRGB.blue }));

				updateUI_LabelColor(lblColor.getDisplay(), lblColor, vertexRGB);

				// keep vertex references
				spinnerValue.setData(DATA_KEY_VERTEX_INDEX, keyIndex);
				spinnerValue.setData(DATA_KEY_SORT_ID, vertex.getSortId());
				lblColor.setData(DATA_KEY_VERTEX_INDEX, keyIndex);
				_actionDeleteVertex[vertexIndex].setData(DATA_KEY_VERTEX_INDEX, keyIndex);

//				// debugging
//				final Label lblDebug = _lblDebug[vertexIndex];
//				lblDebug.setText(String.format(//
//						"sort: %d\tidx: %d\tkey: %d",
//						vertex.getSortId(),
//						vertexIndex,
//						keyIndex
//				//
//						));
//				lblDebug.getParent().layout();
			}
		}
		_isInUIUpdate = false;

		/*
		 * Disable remove actions when only 2 colors are available.
		 */
		if (vertexSize <= 2) {
			for (int ix = 0; ix < vertexSize; ix++) {
				_actionDeleteVertex[ix].setEnabled(false);
			}
		}

		// update profile image
		drawProfileImage();
	}

	/**
	 * Initialize UI.
	 */
	private void updateUI_Initialize() {

		final Collection<Map3ColorDefinition> colorDefinitions = Map3ColorManager.getSortedColorDefinitions();

		for (final Map3ColorDefinition colorDef : colorDefinitions) {
			_cboGraphType.add(colorDef.getVisibleName());
		}

		for (final String comboLabel : MapColorProfile.BRIGHTNESS_LABELS) {
			_cboMinBrightness.add(comboLabel);
		}

		for (final String comboLabel : MapColorProfile.BRIGHTNESS_LABELS) {
			_cboMaxBrightness.add(comboLabel);
		}
	}

	private void updateUI_LabelColor(final Display display, final Label label, final RGB vertexRGB) {

		final Color bgColor = new Color(display, vertexRGB);
		{
			label.setBackground(bgColor);
		}
		bgColor.dispose();
	}

	private boolean validateFields() {

		_isUIValid = true;

		final boolean isMinEnabled = _chkForceMinValue.getSelection();
		final boolean isMaxEnabled = _chkForceMaxValue.getSelection();

		// check that max is larger than min
		if (isMinEnabled && isMaxEnabled && (_spinMaxValue.getSelection() <= _spinMinValue.getSelection())) {

			setErrorMessage(MAP2_MESSAGE_15);
			_isUIValid = false;
		}

		if (_isUIValid) {
			setErrorMessage(null);
		}

		return _isUIValid;
	}
}

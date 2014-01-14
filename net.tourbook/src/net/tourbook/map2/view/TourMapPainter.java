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
package net.tourbook.map2.view;

import gnu.trove.map.hash.TIntObjectHashMap;

import java.awt.Graphics2D;
import java.awt.Point;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.tourbook.application.TourbookPlugin;
import net.tourbook.chart.Util;
import net.tourbook.common.color.ColorCacheInt;
import net.tourbook.common.color.IGradientColorProvider;
import net.tourbook.common.color.IMapColorProvider;
import net.tourbook.common.color.LegendUnitFormat;
import net.tourbook.common.color.MapUnits;
import net.tourbook.common.map.GeoPosition;
import net.tourbook.common.util.StatusUtil;
import net.tourbook.data.TourData;
import net.tourbook.data.TourMarker;
import net.tourbook.data.TourWayPoint;
import net.tourbook.map2.Messages;
import net.tourbook.map3.layer.TourLegendLabel;
import net.tourbook.photo.ILoadCallBack;
import net.tourbook.photo.IPhotoPreferences;
import net.tourbook.photo.ImageQuality;
import net.tourbook.photo.Photo;
import net.tourbook.photo.PhotoImageCache;
import net.tourbook.photo.PhotoLoadManager;
import net.tourbook.photo.PhotoLoadingState;
import net.tourbook.photo.PhotoUI;
import net.tourbook.preferences.ITourbookPreferences;
import net.tourbook.preferences.PrefPageMap2Appearance;
import net.tourbook.ui.UI;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

import de.byteholder.geoclipse.map.Map;
import de.byteholder.geoclipse.map.MapPainter;
import de.byteholder.geoclipse.map.Tile;
import de.byteholder.geoclipse.mapprovider.MP;

/**
 * Paints a tour into the 2D map.
 */
public class TourMapPainter extends MapPainter {

	private static final String				VALUE_FORMAT		= "%.0f";							//$NON-NLS-1$

	private static final int				MARKER_MARGIN		= 2;
	private static final int				MARKER_POLE			= 16;

	private static IPropertyChangeListener	_prefChangeListener;

	private float[]							_dataSerie;

	private IMapColorProvider				_legendProvider;
	// painting parameter
	private int								_lineWidth;

	private int								_lineWidth2;
	private static boolean					_prefIsDrawLine;

	private static boolean					_prefIsDrawSquare;
	private static int						_prefLineWidth;
	private static boolean					_prefWithBorder;
	private static int						_prefBorderWidth;
	private static boolean					_isImageAvailable	= false;

	private static boolean					_isErrorLogged;

	/**
	 * Tour start/end image
	 */
	private static Image					_tourStartMarker;

	private static Image					_tourEndMarker;

	private static Rectangle				_twpImageBounds;
	private static TourPainterConfiguration	_tourPaintConfig;

	private final static NumberFormat		_nf1				= NumberFormat.getNumberInstance();
	{
		_nf1.setMinimumFractionDigits(1);
		_nf1.setMaximumFractionDigits(1);
	}

	/*
	 * UI resources
	 */
	private static Color					_bgColor;

	/**
	 * Tour Way Point image
	 */
	private static Image					_twpImage;

	private final static ColorCacheInt		_colorCache			= new ColorCacheInt();

	private class LoadCallbackImage implements ILoadCallBack {

		private Map		__map;
		private Tile	__tile;

		public LoadCallbackImage(final Map map, final Tile tile) {
			__map = map;
			__tile = tile;
		}

		@Override
		public void callBackImageIsLoaded(final boolean isUpdateUI) {

			if (isUpdateUI == false) {
				return;
			}

			__map.queueOverlayPainting(__tile);
//			__map.paint();
		}
	}

	public TourMapPainter() {

		super();

		/*
		 * I've not yet understood to manage this problem because TourPainter() is created from an
		 * extension point but setting the instance in the constructor is not valid according to
		 * FindBugs
		 */

		init();
	}

	/**
	 * Creates an legend image, this image must be disposed who created it.
	 * 
	 * @param display
	 * @param colorProvider
	 * @param imageWidth
	 * @param imageHeight
	 * @param isDrawVertical
	 * @param isDrawLegendText
	 * @return
	 */
	public static Image createMapLegendImage(	final Display display,
												final IGradientColorProvider colorProvider,
												final int imageWidth,
												final int imageHeight,
												final boolean isDrawVertical) {

		/*
		 * Use a color which is likely not used, the previous color 0xfefefe was used and had bad
		 * effects.
		 */
		final RGB rgbTransparent = new RGB(0xfa, 0xfb, 0xfc);

		final ImageData overlayImageData = new ImageData(//
				imageWidth,
				imageHeight,
				24,
				new PaletteData(0xff, 0xff00, 0xff0000));

		overlayImageData.transparentPixel = overlayImageData.palette.getPixel(rgbTransparent);

		final Image image = new Image(display, overlayImageData);
		final Rectangle legendImageBounds = image.getBounds();

		final Color transparentColor = new Color(display, rgbTransparent);
		final GC gc = new GC(image);
		{
			gc.setBackground(transparentColor);
			gc.fillRectangle(legendImageBounds);

			drawMapLegend(gc, legendImageBounds, colorProvider, isDrawVertical);
		}
		gc.dispose();
		transparentColor.dispose();

		return image;
	}

	private static void drawLegendGradientColors_AWT(	final Graphics2D g2d,
														final int legendWidth,
														final int legendHeight,
														final IGradientColorProvider colorProvider) {

		final MapUnits legendImageConfig = colorProvider.getMapUnits();

		// ensure units are available
		if (legendImageConfig.units == null) {
			return;
		}

		// get configuration for the legend
		final float legendMaxValue = legendImageConfig.legendMaxValue;
		final float legendMinValue = legendImageConfig.legendMinValue;
		final float legendDiffValue = legendMaxValue - legendMinValue;

		final int legendPositionX = 1;
		final int legendPositionY = 1;

		final int availableLegendPixels = legendHeight - 3;

		// pixelValue contains the value for ONE pixel
		final float pixelValue = legendDiffValue / availableLegendPixels;

		for (int pixelIndex = 0; pixelIndex <= availableLegendPixels; pixelIndex++) {

			final float legendValue = legendMinValue + pixelValue * pixelIndex;

			final int valuePositionY = legendPositionY + availableLegendPixels - pixelIndex;

			final int colorValue = colorProvider.getColorValue(legendValue);
			final int red = (colorValue & 0xFF) >>> 0;
			final int green = (colorValue & 0xFF00) >>> 8;
			final int blue = (colorValue & 0xFF0000) >>> 16;

			// draw legend color line
			g2d.setColor(new java.awt.Color(red, green, blue));
			g2d.drawLine(legendPositionX, valuePositionY, legendWidth, valuePositionY);
		}

		// draw border
		g2d.setColor(java.awt.Color.WHITE);
		g2d.drawRect(0, 0, legendWidth - 1, legendHeight - 1);
	}

	private static void drawLegendGradientColors_SWT(	final GC gc,
														final Rectangle imageBounds,
														final IGradientColorProvider colorProvider,
														final boolean isDrawVertical) {

		final MapUnits mapUnits = colorProvider.getMapUnits();

		// ensure units are available
		if (mapUnits.units == null) {

			if (!_isErrorLogged) {
				_isErrorLogged = true;
				StatusUtil.log(new Throwable("Color provider is not configured: " + colorProvider));//$NON-NLS-1$
			}

			return;
		}

		final int borderSize = 0;
//		final int borderSize = IMapColorProvider.LEGEND_IMAGE_BORDER_WIDTH;

		// get configuration for the legend
		final ArrayList<Float> legendUnits = new ArrayList<Float>(mapUnits.units);
		final float legendMaxValue = mapUnits.legendMaxValue;
		final float legendMinValue = mapUnits.legendMinValue;
		final float legendDiffValue = legendMaxValue - legendMinValue;
		final List<String> unitLabels = mapUnits.unitLabels;
		final int legendFormatDigits = mapUnits.numberFormatDigits;
		final LegendUnitFormat unitFormat = mapUnits.unitFormat;

		final String unitText = UI.SPACE + mapUnits.unitText;

		Rectangle contentBorder;

		int contentX;
		int contentY;
		int contentWidth;
		int contentHeight;
		int availableLegendPixels;

		if (isDrawVertical) {

			// vertical legend

			final int marginTopBottom = IMapColorProvider.LEGEND_MARGIN_TOP_BOTTOM;

			contentX = borderSize;
			contentY = marginTopBottom + borderSize;

			contentWidth = IMapColorProvider.DEFAULT_LEGEND_GRAPHIC_WIDTH;
			contentHeight = imageBounds.height - 2 * marginTopBottom - 2 * borderSize;

			availableLegendPixels = contentHeight - 0;

			contentBorder = new Rectangle(//
					contentX - borderSize,
					contentY - borderSize,
					contentWidth + borderSize,
					contentHeight + 2 * borderSize + 0);

		} else {

			// horizontal legend

			contentX = imageBounds.x + borderSize;
			contentY = imageBounds.y + borderSize;

			contentWidth = imageBounds.width - 2 * borderSize;
			contentHeight = imageBounds.height - 2 * borderSize;

			availableLegendPixels = contentWidth - borderSize;

			contentBorder = new Rectangle(//
					contentX - borderSize,
					contentY - borderSize,
					contentWidth + 2 * borderSize - 1,
					contentHeight + 2 * borderSize - 1);
		}

		// pixelValue contains the value for ONE pixel
		final float pixelValue = legendDiffValue / availableLegendPixels;

		final float roundingValue = pixelValue / 100;

		final Device display = gc.getDevice();

		// draw border around the colors
//		final Color borderColor = display.getSystemColor(SWT.COLOR_GRAY);
		final Color borderColor = display.getSystemColor(SWT.COLOR_RED);
		gc.setForeground(borderColor);
		gc.drawRectangle(contentBorder);

		final Color unitTextColor = display.getSystemColor(SWT.COLOR_BLACK);

		int unitLabelIndex = 0;

		final Color textBorderColor = _colorCache.get(0xF1EEE8);

		for (int pixelIndex = 0; pixelIndex < availableLegendPixels + 1; pixelIndex++) {

			final float legendValue = legendMinValue + pixelValue * pixelIndex;

			int devValue;

			if (isDrawVertical) {
				devValue = contentY + availableLegendPixels - pixelIndex;
			} else {
				devValue = contentX + pixelIndex;
			}

			/*
			 * draw legend unit
			 */

			if (isDrawVertical) {

				// find a unit which corresponds to the current legend value

				// Rounding value is necessary otherwise the uppermost unit is sometimes not drawn.
				final float legendUnitValue = legendValue + roundingValue;

				for (final Float unitValue : legendUnits) {

					if (legendUnitValue >= unitValue) {

						/*
						 * get unit label
						 */
						String valueText;
						if (unitLabels == null) {

							// set default unit label

							if (unitFormat == LegendUnitFormat.Pace) {

								valueText = Util.format_mm_ss(unitValue.longValue()) + unitText;

							} else {

								if (legendFormatDigits == 0) {
									valueText = Integer.toString(unitValue.intValue()) + UI.SPACE + unitText;
								} else {
									// currently only 1 digit is supported
									valueText = _nf1.format(unitValue) + UI.SPACE + unitText;
								}
							}

						} else {
							// when unitLabels are available, they will overwrite the default labeling
							valueText = unitLabels.get(unitLabelIndex++);
						}
						final org.eclipse.swt.graphics.Point valueTextExtent = gc.textExtent(valueText);

						final int devXText = contentWidth + 7;
						final int devYText = devValue - valueTextExtent.y / 2;

						gc.setForeground(textBorderColor);
						gc.drawText(valueText, devXText - 1, devYText, true);
						gc.drawText(valueText, devXText + 1, devYText, true);
						gc.drawText(valueText, devXText, devYText - 1, true);
						gc.drawText(valueText, devXText, devYText + 1, true);

						gc.setForeground(unitTextColor);
						gc.drawText(valueText, devXText, devYText, true);

						// prevent to draw this unit again
						legendUnits.remove(unitValue);

						break;
					}
				}
			}

			/*
			 * draw legend color line
			 */

			final int valueRGB = colorProvider.getColorValue(legendValue);
			final Color valueColor = _colorCache.get(valueRGB);

			gc.setForeground(valueColor);

			if (isDrawVertical) {

				// vertial legend

				gc.drawLine(contentX, devValue, contentWidth, devValue);

			} else {

				// horizontal legend

				gc.drawLine(devValue, contentY, devValue, contentHeight);
			}
		}

		_colorCache.dispose();
	}

	/**
	 * Draws map legend colors into the legend bounds.
	 * 
	 * @param gc
	 * @param legendImageBounds
	 * @param colorProvider
	 * @param isDrawVertical
	 * @param isDrawVertical
	 *            When <code>true</code> the legend is drawn vertically otherwise it's drawn
	 *            horizontally.
	 * @param isDrawLegendText
	 */
	public static void drawMapLegend(	final GC gc,
										final Rectangle legendImageBounds,
										final IMapColorProvider colorProvider,
										final boolean isDrawVertical) {

		if (colorProvider instanceof IGradientColorProvider) {

			drawLegendGradientColors_SWT(//
					gc,
					legendImageBounds,
					(IGradientColorProvider) colorProvider,
					isDrawVertical);
		}
	}

	public static void drawMapLegend(	final Graphics2D g2d,
										final int legendWidth,
										final int legendHeight,
										final IMapColorProvider colorProvider) {

		if (colorProvider instanceof IGradientColorProvider) {
			drawLegendGradientColors_AWT(g2d, legendWidth, legendHeight, (IGradientColorProvider) colorProvider);
		}
	}

	public static ArrayList<TourLegendLabel> getMapLegendLabels(final int legendWidth,
																final int legendHeight,
																final IGradientColorProvider colorProvider) {

		final ArrayList<TourLegendLabel> legendLabels = new ArrayList<TourLegendLabel>();

		final MapUnits legendImageConfig = colorProvider.getMapUnits();

		// ensure units are available
		if (legendImageConfig.units == null) {
			return legendLabels;
		}

		// get configuration for the legend
		final ArrayList<Float> allLegendUnits = new ArrayList<Float>(legendImageConfig.units);

		final String unitText = legendImageConfig.unitText;
		final List<String> unitLabels = legendImageConfig.unitLabels;
		final int legendFormatDigits = legendImageConfig.numberFormatDigits;
		final LegendUnitFormat unitFormat = legendImageConfig.unitFormat;

		// get configuration for the legend
		final float legendMaxValue = legendImageConfig.legendMaxValue;
		final float legendMinValue = legendImageConfig.legendMinValue;
		final float legendDiffValue = legendMaxValue - legendMinValue;

		final int legendPositionY = 1;

		final int availableLegendPixels = legendHeight - 3;

		// pixelValue contains the value for ONE pixel
		final float pixelValue = legendDiffValue / availableLegendPixels;

		float legendValue = 0;
		int unitLabelIndex = 0;

		for (int pixelIndex = 0; pixelIndex <= availableLegendPixels; pixelIndex++) {

			legendValue = legendMinValue + pixelValue * pixelIndex;

			final int valuePositionY = legendPositionY + availableLegendPixels - pixelIndex;

			// find a unit which corresponds to the current legend value
			for (final Float unitValue : allLegendUnits) {

				if (legendValue >= unitValue) {

					/*
					 * get unit label
					 */
					String valueText;
					if (unitLabels == null) {

						// set default unit label

						if (unitFormat == LegendUnitFormat.Pace) {

							valueText = Util.format_mm_ss(unitValue.longValue()) + UI.SPACE + unitText;

						} else {

							if (legendFormatDigits == 0) {
								valueText = Integer.toString(unitValue.intValue()) + UI.SPACE + unitText;
							} else {
								// currently only 1 digit is supported
								valueText = _nf1.format(unitValue) + UI.SPACE + unitText;
							}
						}

					} else {
						// when unitLabels are available, they will overwrite the default labeling
						valueText = unitLabels.get(unitLabelIndex++);
					}

					legendLabels.add(new TourLegendLabel(unitValue, valueText, valuePositionY));

					// prevent to draw this unit again
					allLegendUnits.remove(unitValue);

					break;
				}
			}
		}

		return legendLabels;
	}

	private static void getTourPainterSettings() {

		final IPreferenceStore prefStore = TourbookPlugin.getDefault().getPreferenceStore();

		final String drawSymbol = prefStore.getString(ITourbookPreferences.MAP_LAYOUT_SYMBOL);

		_prefIsDrawLine = drawSymbol.equals(PrefPageMap2Appearance.MAP_TOUR_SYMBOL_LINE);
		_prefIsDrawSquare = drawSymbol.equals(PrefPageMap2Appearance.MAP_TOUR_SYMBOL_SQUARE);

		_prefLineWidth = prefStore.getInt(ITourbookPreferences.MAP_LAYOUT_SYMBOL_WIDTH);
		_prefWithBorder = prefStore.getBoolean(ITourbookPreferences.MAP_LAYOUT_PAINT_WITH_BORDER);
		_prefBorderWidth = prefStore.getInt(ITourbookPreferences.MAP_LAYOUT_BORDER_WIDTH);
	}

	private static void init() {

		if (_bgColor != null) {
			return;
		}

		// ensure color registry is setup
		PhotoUI.init();

		final ColorRegistry colorRegistry = JFaceResources.getColorRegistry();
		_bgColor = colorRegistry.get(IPhotoPreferences.PHOTO_VIEWER_COLOR_BACKGROUND);

		_tourPaintConfig = TourPainterConfiguration.getInstance();

		/**
		 * this code optimizes the performance by reading from the pref store which is not very
		 * efficient
		 */
		getTourPainterSettings();

		// create pref listener
		_prefChangeListener = new IPropertyChangeListener() {
			@Override
			public void propertyChange(final PropertyChangeEvent event) {
				final String property = event.getProperty();

				// test if the color or statistic data have changed
				if (property.equals(ITourbookPreferences.GRAPH_COLORS_HAS_CHANGED)) {
					getTourPainterSettings();
				}
			}
		};

		// add pref listener, dispose is not removing it because it is static !!!
		TourbookPlugin.getDefault().getPreferenceStore()//
				.addPropertyChangeListener(_prefChangeListener);
	}

	private void createImages() {

		_tourStartMarker = TourbookPlugin.getImageDescriptor(Messages.Image_Map_TourStartMarker).createImage();
		_tourEndMarker = TourbookPlugin.getImageDescriptor(Messages.Image_Map_TourEndMarker).createImage();

		_twpImage = TourbookPlugin.getImageDescriptor(Messages.Image_Map_WayPoint).createImage();
		_twpImageBounds = _twpImage.getBounds();

		_isImageAvailable = true;
	}

	@Override
	protected void dispose() {

		disposeImage(_tourStartMarker);
		disposeImage(_tourEndMarker);

		_isImageAvailable = false;
	}

	private void disposeImage(final Image image) {
		if (image != null && !image.isDisposed()) {
			image.dispose();
		}
	}

	@Override
	protected void disposeTempResources() {
//		_colorCache.dispose();
	}

	@Override
	protected boolean doPaint(final GC gcTile, final Map map, final Tile tile, final int parts) {

		init();

		final ArrayList<TourData> tourDataList = _tourPaintConfig.getTourData();
		final ArrayList<Photo> photoList = _tourPaintConfig.getPhotos();

		if (tourDataList.size() == 0 && photoList.size() == 0) {
			return false;
		}

		boolean isContentInTile = false;

		if (_isImageAvailable == false) {
			createImages();
		}

		// first draw the tour, then the marker and photos
		if (_tourPaintConfig.isTourVisible) {

			final Color systemColorBlue = gcTile.getDevice().getSystemColor(SWT.COLOR_BLUE);

			for (final TourData tourData : tourDataList) {

				if (tourData == null) {
					continue;
				}

				// check if position is available
				final double[] latitudeSerie = tourData.latitudeSerie;
				final double[] longitudeSerie = tourData.longitudeSerie;
				if (latitudeSerie == null || longitudeSerie == null) {
					continue;
				}

				setDataSerie(tourData);

				final boolean isDrawTourInTile = drawTour10InTile(gcTile, map, tile, tourData, parts, systemColorBlue);

				isContentInTile = isContentInTile || isDrawTourInTile;

//				/**
//				 * DEBUG Start
//				 */
//				gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
//				gc.fillRectangle(0, 0, 2, 50);
//				gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
//				gc.fillRectangle(2, 0, 2, 50);
//				gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GREEN));
//				gc.fillRectangle(4, 0, 2, 50);
//				gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
//				gc.fillRectangle(6, 0, 2, 50);
//				isTourInTile = true;
//				/**
//				 * DEBUG End
//				 */

				// status if a marker is drawn
				int staticMarkerCounter = 0;

				// draw start/end marker
				if (_tourPaintConfig.isShowStartEndInMap) {

					// draw end marker first
					if (drawStaticMarker(
							gcTile,
							map,
							tile,
							latitudeSerie[latitudeSerie.length - 1],
							longitudeSerie[longitudeSerie.length - 1],
							_tourEndMarker,
							parts)) {

						staticMarkerCounter++;
					}

					// draw start marker above the end marker
					if (drawStaticMarker(//
							gcTile,
							map,
							tile,
							latitudeSerie[0],
							longitudeSerie[0],
							_tourStartMarker,
							parts)) {

						staticMarkerCounter++;
					}
				}

				isContentInTile = isContentInTile || staticMarkerCounter > 0;
			}

			_colorCache.dispose();
		}

		if (_tourPaintConfig.isShowTourMarker || _tourPaintConfig.isShowWayPoints) {

			// draw marker above the tour

			for (final TourData tourData : tourDataList) {

				if (tourData == null) {
					continue;
				}

				// check if geo position is available
				final double[] latitudeSerie = tourData.latitudeSerie;
				final double[] longitudeSerie = tourData.longitudeSerie;
				if (latitudeSerie == null || longitudeSerie == null) {
					continue;
				}

				setDataSerie(tourData);

				if (_tourPaintConfig.isShowTourMarker) {

					// ckeck if markers are available
					final ArrayList<TourMarker> sortedMarkers = tourData.getTourMarkersSorted();
					if (sortedMarkers.size() > 0) {

						// draw tour marker

						int markerCounter = 0;

						for (final TourMarker tourMarker : sortedMarkers) {

							// skip marker when hidden or not set
							if (tourMarker.isMarkerVisible() == false || tourMarker.getLabel().length() == 0) {
								continue;
							}

							final int serieIndex = tourMarker.getSerieIndex();

							/*
							 * check bounds because when a tour is split, it can happen that the
							 * marker serie index is out of scope
							 */
							if (serieIndex >= latitudeSerie.length) {
								continue;
							}

							// draw tour marker
							if (drawTourMarker(
									gcTile,
									map,
									tile,
									latitudeSerie[serieIndex],
									longitudeSerie[serieIndex],
									tourMarker,
									parts)) {
								markerCounter++;
							}
						}

						isContentInTile = isContentInTile || markerCounter > 0;
					}
				}

				if (_tourPaintConfig.isShowWayPoints) {

					// ckeck if way points are available
					final Set<TourWayPoint> wayPoints = tourData.getTourWayPoints();
					if (wayPoints.size() > 0) {

						/*
						 * world positions are cached to optimize performance
						 */
						final MP mp = map.getMapProvider();
						final String projectionId = mp.getProjection().getId();
						final int mapZoomLevel = map.getZoom();

						TIntObjectHashMap<Point> allWayPointWorldPixel = tourData.getWorldPositionForWayPoints(
								projectionId,
								mapZoomLevel);

						if ((allWayPointWorldPixel == null)) {
							allWayPointWorldPixel = initWorldPixelWayPoint(
									tourData,
									wayPoints,
									mp,
									projectionId,
									mapZoomLevel);
						}

						// draw tour way points

						int wayPointCounter = 0;
						for (final TourWayPoint tourWayPoint : wayPoints) {

							final Point twpWorldPixel = allWayPointWorldPixel.get(tourWayPoint.hashCode());

							if (drawTourWayPoint(gcTile, map, tile, tourWayPoint, twpWorldPixel, parts)) {
								wayPointCounter++;
							}
						}

						isContentInTile = isContentInTile || wayPointCounter > 0;
					}
				}
			}
		}

		if (_tourPaintConfig.isPhotoVisible && photoList.size() > 0) {

			/*
			 * world positions are cached to optimize performance
			 */
			final MP mp = map.getMapProvider();
			final String projectionId = mp.getProjection().getId();
			final int mapZoomLevel = map.getZoom();

			int photoCounter = 0;

//			System.out.println(net.tourbook.common.UI.timeStampNano() + " Photo\t");
//			// TODO remove SYSTEM.OUT.PRINTLN

			for (final Photo photo : photoList) {

//				System.out.println(net.tourbook.common.UI.timeStampNano() + "\t" + photo);
//				// TODO remove SYSTEM.OUT.PRINTLN

				final Point photoWorldPixel = photo.getWorldPosition(
						mp,
						projectionId,
						mapZoomLevel,
						_tourPaintConfig.isLinkPhotoDisplayed);
				if (photoWorldPixel == null) {
					continue;
				}

				if (drawPhoto(gcTile, map, tile, photo, photoWorldPixel, parts)) {
					photoCounter++;
				}
			}

			isContentInTile = isContentInTile || photoCounter > 0;
		}

		return isContentInTile;
	}

	private boolean drawPhoto(	final GC gcTile,
								final Map map,
								final Tile tile,
								final Photo photo,
								final Point photoWorldPixel,
								final int parts) {

		final MP mp = map.getMapProvider();
		final int tileSize = mp.getTileSize();

		// get world viewport for the current tile
		final int tileWorldPixelX = tile.getX() * tileSize;
		final int tilwWorldPixelY = tile.getY() * tileSize;

		// convert world position into device position
		final int devXPhoto = photoWorldPixel.x - tileWorldPixelX;
		final int devYPhoto = photoWorldPixel.y - tilwWorldPixelY;

		final org.eclipse.swt.graphics.Point photoSize = photo.getMapImageSize();

		final boolean isPhotoInTile = isPhotoInTile(photoSize, devXPhoto, devYPhoto, tileSize);

		if (isPhotoInTile) {

//			final int zoomLevel = map.getZoom();
			final int devPartOffset = ((parts - 1) / 2) * tileSize;

			final Image image = getPhotoImage(photo, map, tile);

			if (image == null) {
				return false;
			}

			final Rectangle imageSize = image.getBounds();

			final int photoWidth = photoSize.x;
			final int photoHeight = photoSize.y;

			int devX = devXPhoto - photoWidth / 2;
			int devY = devYPhoto - photoHeight;

			devX += devPartOffset;
			devY += devPartOffset;

			gcTile.drawImage(image, //
					0,
					0,
					imageSize.width,
					imageSize.height,

					//
					devX,
					devY,
					photoWidth,
					photoHeight);

			gcTile.setForeground(_bgColor);
			gcTile.setLineWidth(1);
			gcTile.drawRectangle(devX, devY, photoWidth, photoHeight);

//			System.out.println(net.tourbook.common.UI.timeStampNano()
//					+ (" image: " + imageSize.width + "x" + imageSize.height)
//					+ ("\tphoto: " + photoWidth + " x " + photoHeight));
//			// TODO remove SYSTEM.OUT.PRINTLN
		}

		return isPhotoInTile;
	}

	private boolean drawStaticMarker(	final GC gcTile,
										final Map map,
										final Tile tile,
										final double latitude,
										final double longitude,
										final Image markerImage,
										final int parts) {

		if (markerImage == null) {
			return false;
		}

		final MP mp = map.getMapProvider();
		final int zoomLevel = map.getZoom();
		final int tileSize = mp.getTileSize();
		final int devPartOffset = ((parts - 1) / 2) * tileSize;

		// get world viewport for the current tile
		final int worldPixelTileX = tile.getX() * tileSize;
		final int worldPixelTileY = tile.getY() * tileSize;

		// convert lat/long into world pixels
		final Point worldPixelMarker = mp.geoToPixel(new GeoPosition(latitude, longitude), zoomLevel);

		// convert world position into device position
		final int devMarkerPosX = worldPixelMarker.x - worldPixelTileX;
		final int devMarkerPosY = worldPixelMarker.y - worldPixelTileY;

		final boolean isMarkerInTile = isBoundsInTile(markerImage.getBounds(), devMarkerPosX, devMarkerPosY, tileSize);
		if (isMarkerInTile) {

			// get marker size
			final Rectangle bounds = markerImage.getBounds();
			final int markerWidth = bounds.width;
			final int markerWidth2 = markerWidth / 2;
			final int markerHeight = bounds.height;

			gcTile.drawImage(markerImage, //
					devMarkerPosX - markerWidth2 + devPartOffset,
					devMarkerPosY - markerHeight + devPartOffset);
		}

		return isMarkerInTile;
	}

	private boolean drawTour10InTile(	final GC gcTile,
										final Map map,
										final Tile tile,
										final TourData tourData,
										final int parts,
										final Color systemColorBlue) {

		boolean isTourInTile = false;

		final MP mp = map.getMapProvider();
		final int mapZoomLevel = map.getZoom();
		final int tileSize = mp.getTileSize();
		final int devPartOffset = ((parts - 1) / 2) * tileSize;

		// get viewport for the current tile
		final int tileWorldPixelX = tile.getX() * tileSize;
		final int tileWorldPixelY = tile.getY() * tileSize;
		final int tileWidth = tileSize;
		final int tileHeight = tileSize;

		int devFromWithOffsetX = 0;
		int devFromWithOffsetY = 0;

		final double[] latitudeSerie = tourData.latitudeSerie;
		final double[] longitudeSerie = tourData.longitudeSerie;

		/*
		 * world positions are cached to optimize performance when multiple tours are selected
		 */
		final String projectionId = mp.getProjection().getId();
		Point tourWorldPixelPosAll[] = tourData.getWorldPositionForTour(projectionId, mapZoomLevel);

		if ((tourWorldPixelPosAll == null)) {

			tourWorldPixelPosAll = initWorldPixelTour(
					tourData,
					mp,
					mapZoomLevel,
					latitudeSerie,
					longitudeSerie,
					projectionId);
		}

		gcTile.setForeground(systemColorBlue);
		gcTile.setBackground(systemColorBlue);

		int lastInsideIndex = -99;
		boolean isBorder;

		// index == 0: paint border
		// index == 1: paint tour symbol
		for (int lineIndex = 0; lineIndex < 2; lineIndex++) {

			if (lineIndex == 0) {

				if (_prefWithBorder == false) {
					// skip border
					continue;
				}

				isBorder = true;

				// draw line border
				_lineWidth = _prefLineWidth + (_prefBorderWidth * 2);

			} else if (lineIndex == 1) {

				isBorder = false;

				// draw within the border
				_lineWidth = _prefLineWidth;

			} else {
				break;
			}

			_lineWidth2 = _lineWidth / 2;

			gcTile.setLineWidth(_lineWidth);

			for (int serieIndex = 0; serieIndex < longitudeSerie.length; serieIndex++) {

				final Point tourWorldPixel = tourWorldPixelPosAll[serieIndex];
				final int tourWorldPixelX = tourWorldPixel.x;
				final int tourWorldPixelY = tourWorldPixel.y;

				int devX = tourWorldPixelX - tileWorldPixelX;
				int devY = tourWorldPixelY - tileWorldPixelY;

				if (_prefIsDrawLine) {

					// check if position is in the viewport

					// get positions with the part offset
					final int devToWithOffsetX = devX + devPartOffset;
					final int devToWithOffsetY = devY + devPartOffset;

					if (serieIndex == 0) {

						// keep position
						devFromWithOffsetX = devToWithOffsetX;
						devFromWithOffsetY = devToWithOffsetY;

						continue;
					}

					Color color = null;

					// this condition is an inline for:
					// tileViewport.contains(tileWorldPos.x, tileWorldPos.y)

					if ((tourWorldPixelX >= tileWorldPixelX)
							&& (tourWorldPixelY >= tileWorldPixelY)
							&& tourWorldPixelX < (tileWorldPixelX + tileWidth)
							&& tourWorldPixelY < (tileWorldPixelY + tileHeight)) {

						// current position is inside the tile

						// check if position has changed
						if (devToWithOffsetX != devFromWithOffsetX || devToWithOffsetY != devFromWithOffsetY) {

							isTourInTile = true;

							color = getTourColor(tourData, serieIndex, isBorder, true);

							drawTour20Line(gcTile, //
									devFromWithOffsetX,
									devFromWithOffsetY,
									devToWithOffsetX,
									devToWithOffsetY,
									color);
						}

						lastInsideIndex = serieIndex;
					}

					// current position is outside the tile

					if (serieIndex == lastInsideIndex + 1) {

						/*
						 * this position is the first which is outside of the tile, draw a line from
						 * the last inside to the first outside position
						 */

						drawTour20Line(gcTile, //
								devFromWithOffsetX,
								devFromWithOffsetY,
								devToWithOffsetX,
								devToWithOffsetY,
								color);
					}

					// keep position
					devFromWithOffsetX = devToWithOffsetX;
					devFromWithOffsetY = devToWithOffsetY;

				} else {

					// draw tour with dots/squares

					// this is an inline for: tileViewport.contains(tileWorldPos.x, tileWorldPos.y)
					// check if position is in the viewport
					if ((tourWorldPixelX >= tileWorldPixelX)
							&& (tourWorldPixelY >= tileWorldPixelY)
							&& tourWorldPixelX < (tileWorldPixelX + tileWidth)
							&& tourWorldPixelY < (tileWorldPixelY + tileHeight)) {

						// current position is inside the tile

						// optimize drawing: check if position has changed
						if (devX != devFromWithOffsetX && devY != devFromWithOffsetY) {

							isTourInTile = true;

							// adjust positions with the part offset
							devX += devPartOffset;
							devY += devPartOffset;

							final Color color = getTourColor(tourData, serieIndex, isBorder, false);

							if (_prefIsDrawSquare) {
								drawTour30Square(gcTile, devX, devY, color);
							} else {
								drawTour40Dot(gcTile, devX, devY, color);
							}

							// set previous pixel
							devFromWithOffsetX = devX;
							devFromWithOffsetY = devY;
						}
					}
				}
			}
		}

		return isTourInTile;
	}

	private void drawTour20Line(final GC gc,
								final int devXFrom,
								final int devYFrom,
								final int devXTo,
								final int devYTo,
								final Color color) {

		if (color != null) {
			gc.setForeground(color);
		}

		drawTour40Dot(gc, devXTo, devYTo, color);

		// draw line with the color from the legend provider
		gc.drawLine(devXFrom, devYFrom, devXTo, devYTo);

	}

	private void drawTour30Square(final GC gc, final int devX, final int devY, final Color color) {

		if (color != null) {
			gc.setBackground(color);
		}

		gc.fillRectangle(devX - _lineWidth2, devY - _lineWidth2, _lineWidth, _lineWidth);
	}

	private void drawTour40Dot(final GC gc, final int devX, final int devY, final Color color) {

		if (color != null) {
			gc.setBackground(color);
		}

		if (_lineWidth == 2) {
			// oval is not filled by a width of 2
			gc.fillRectangle(devX, devY, _lineWidth, _lineWidth);
		} else {
			gc.fillOval(devX - _lineWidth2, devY - _lineWidth2, _lineWidth, _lineWidth);
		}
	}

	/**
	 * @param gcTile
	 * @param map
	 * @param tile
	 * @param latitude
	 * @param longitude
	 * @param tourMarker
	 * @param parts
	 * @return Returns <code>true</code> when marker has been painted
	 */
	private boolean drawTourMarker(	final GC gcTile,
									final Map map,
									final Tile tile,
									final double latitude,
									final double longitude,
									final TourMarker tourMarker,
									final int parts) {

		final MP mp = map.getMapProvider();
		final int zoomLevel = map.getZoom();
		final int tileSize = mp.getTileSize();
		final int devPartOffset = ((parts - 1) / 2) * tileSize;

		// get world viewport for the current tile
		final int worldTileX = tile.getX() * tileSize;
		final int worldTileY = tile.getY() * tileSize;

		// convert lat/long into world pixels
		final Point worldMarkerPos = mp.geoToPixel(new GeoPosition(latitude, longitude), zoomLevel);

		// convert world position into device position
		final int devMarkerPosX = worldMarkerPos.x - worldTileX;
		final int devMarkerPosY = worldMarkerPos.y - worldTileY;

		Rectangle markerBounds = tourMarker.getMarkerBounds();
		if (markerBounds == null) {

			/*
			 * create and cache marker bounds
			 */

			final org.eclipse.swt.graphics.Point labelExtent = gcTile.textExtent(tourMarker.getLabel());

			final int bannerWidth = labelExtent.x + 2 * MARKER_MARGIN + 1;
			final int bannerHeight = labelExtent.y + 2 * MARKER_MARGIN;

			final int markerImageWidth = bannerWidth;
			final int markerImageHeight = bannerHeight + MARKER_POLE;

			markerBounds = new Rectangle(bannerWidth, bannerHeight, markerImageWidth, markerImageHeight);

			tourMarker.setMarkerBounds(markerBounds);
		}

		final boolean isMarkerInTile = isBoundsInTile(markerBounds, devMarkerPosX, devMarkerPosY, tileSize);
		if (isMarkerInTile) {

			int devX;
			int devY;

			final Image tourMarkerImage = drawTourMarkerImage(gcTile.getDevice(), tourMarker.getLabel(), markerBounds);
			{
				devX = devMarkerPosX - markerBounds.width / 2;
				devY = devMarkerPosY - markerBounds.height;

				devX += devPartOffset;
				devY += devPartOffset;

				gcTile.drawImage(tourMarkerImage, devX, devY);
			}
			tourMarkerImage.dispose();

			tile.addMarkerBounds(devX, devY, markerBounds.x, markerBounds.y, zoomLevel, parts);
		}

		return isMarkerInTile;
	}

	/**
	 * create an image for the tour marker
	 * 
	 * @param device
	 * @param markerBounds
	 * @param tourMarker
	 * @return
	 */
	private Image drawTourMarkerImage(final Device device, final String markerLabel, final Rectangle markerBounds) {

		final int bannerWidth = markerBounds.x;
		final int bannerHeight = markerBounds.y;
		final int bannerWidth2 = bannerWidth / 2;

		final int markerImageWidth = markerBounds.width;
		final int markerImageHeight = markerBounds.height;

		final int arcSize = 5;

		final RGB rgbTransparent = Map.getTransparentRGB();

		final ImageData markerImageData = new ImageData(//
				markerImageWidth,
				markerImageHeight,
				24,
				new PaletteData(0xff, 0xff00, 0xff0000));

		markerImageData.transparentPixel = markerImageData.palette.getPixel(rgbTransparent);

		final Image markerImage = new Image(device, markerImageData);
		final Rectangle markerImageBounds = markerImage.getBounds();

		final Color transparentColor = new Color(device, rgbTransparent);
		final Color bannerColor = new Color(device, 0x65, 0xF9, 0x1F);
		final Color bannerBorderColor = new Color(device, 0x69, 0xAF, 0x3D);

		final GC gc = new GC(markerImage);

		{
			// fill transparent color
			gc.setBackground(transparentColor);
			gc.fillRectangle(markerImageBounds);

			gc.setBackground(bannerColor);
			gc.fillRoundRectangle(0, 0, bannerWidth, bannerHeight, arcSize, arcSize);

			// draw banner border
			gc.setForeground(bannerBorderColor);
			gc.drawRoundRectangle(0, 0, bannerWidth - 1, bannerHeight - 1, arcSize, arcSize);

			// draw text
			gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
			gc.drawText(markerLabel, //
					MARKER_MARGIN + 1,
					MARKER_MARGIN,
					true);

			// draw pole
			gc.setForeground(bannerBorderColor);
			gc.drawLine(bannerWidth2 - 1, bannerHeight, bannerWidth2 - 1, bannerHeight + MARKER_POLE);
			gc.drawLine(bannerWidth2 + 1, bannerHeight, bannerWidth2 + 1, bannerHeight + MARKER_POLE);

			gc.setForeground(bannerColor);
			gc.drawLine(bannerWidth2 - 0, bannerHeight, bannerWidth2 - 0, bannerHeight + MARKER_POLE);

			// draw image debug border
//			gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
//			gc.drawRectangle(0, 0, markerImageBounds.width - 1, markerImageBounds.height - 1);
		}

		gc.dispose();

		bannerColor.dispose();
		bannerBorderColor.dispose();
		transparentColor.dispose();

		return markerImage;
	}

	/**
	 * @param gcTile
	 * @param map
	 * @param tile
	 * @param twp
	 * @param twpWorldPixel
	 * @param parts
	 * @return Returns <code>true</code> when way point has been painted
	 */
	private boolean drawTourWayPoint(	final GC gcTile,
										final Map map,
										final Tile tile,
										final TourWayPoint twp,
										final Point twpWorldPixel,
										final int parts) {

		final MP mp = map.getMapProvider();
		final int zoomLevel = map.getZoom();
		final int tileSize = mp.getTileSize();
		final int devPartOffset = ((parts - 1) / 2) * tileSize;

		// get world viewport for the current tile
		final int tileWorldPixelX = tile.getX() * tileSize;
		final int tilwWorldPixelY = tile.getY() * tileSize;

		// convert world position into device position
		final int devWayPointX = twpWorldPixel.x - tileWorldPixelX;
		final int devWayPointY = twpWorldPixel.y - tilwWorldPixelY;

		final boolean isBoundsInTile = isBoundsInTile(_twpImageBounds, devWayPointX, devWayPointY, tileSize);

		if (isBoundsInTile) {

			int devX = devWayPointX - _twpImageBounds.width / 2;
			int devY = devWayPointY - _twpImageBounds.height;

			devX += devPartOffset;
			devY += devPartOffset;

			gcTile.drawImage(_twpImage, devX, devY);

//			gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
//			gc.setLineWidth(1);
//			gc.drawRectangle(devX, devY, _twpImageBounds.width, _twpImageBounds.height);
//
			tile.addTourWayPointBounds(//
					twp,
					new Rectangle(
							devX - devPartOffset,
							devY - devPartOffset,
							_twpImageBounds.width,
							_twpImageBounds.height),
					zoomLevel,
					parts);

			/*
			 * check if the way point paints into a neighbour tile
			 */
			if (parts > 1) {

			}
		}

		return isBoundsInTile;
	}

	/**
	 * @param legendBounds
	 * @param valueIndex
	 * @return Returns the position for the value according to the value index in the legend,
	 *         {@link Integer#MIN_VALUE} when data are not initialized
	 */
	public int getLegendValuePosition(final Rectangle legendBounds, final int valueIndex) {

		if (_dataSerie == null || valueIndex >= _dataSerie.length || //
				// check legend provider type
				_legendProvider instanceof IGradientColorProvider == false//
		) {
			return Integer.MIN_VALUE;
		}

		/*
		 * ONLY VERTICAL LEGENDS ARE SUPPORTED
		 */

		final float dataValue = _dataSerie[valueIndex];

		int valuePosition = 0;

		final MapUnits config = ((IGradientColorProvider) _legendProvider).getMapUnits();

//		final Integer unitFactor = config.unitFactor;
//		dataValue /= unitFactor;

		final float legendMaxValue = config.legendMaxValue;
		final float legendMinValue = config.legendMinValue;
		final float legendDiffValue = legendMaxValue - legendMinValue;

		if (dataValue >= legendMaxValue) {

			// value >= max

		} else if (dataValue <= legendMinValue) {

			// value <= min

		} else {

			// min < value < max

			final int legendPositionY = legendBounds.y + IMapColorProvider.LEGEND_MARGIN_TOP_BOTTOM;
			final int legendHeight = legendBounds.height - 2 * IMapColorProvider.LEGEND_MARGIN_TOP_BOTTOM;

			final int pixelDiff = legendHeight - 1;

			final float dataValue0 = dataValue - legendMinValue;
			final float ratio = pixelDiff / legendDiffValue;

			valuePosition = legendPositionY + (int) (dataValue0 * ratio);
		}

		return valuePosition;
	}

	private Image getPhotoImage(final Photo photo, final Map map, final Tile tile) {

		Image photoImage = null;

		final ImageQuality requestedImageQuality = ImageQuality.THUMB;

		// check if image has an loading error
		final PhotoLoadingState photoLoadingState = photo.getLoadingState(requestedImageQuality);

		if (photoLoadingState != PhotoLoadingState.IMAGE_IS_INVALID) {

			// image is not yet loaded

			// check if image is in the cache
			photoImage = PhotoImageCache.getImage(photo, requestedImageQuality);

			if ((photoImage == null || photoImage.isDisposed())
					&& photoLoadingState == PhotoLoadingState.IMAGE_IS_IN_LOADING_QUEUE == false) {

				// the requested image is not available in the image cache -> image must be loaded

				final ILoadCallBack imageLoadCallback = new LoadCallbackImage(map, tile);

				PhotoLoadManager.putImageInLoadingQueueThumbMap(photo, requestedImageQuality, imageLoadCallback);
			}
		}

		return photoImage;
	}

	private Color getTourColor(	final TourData tourData,
								final int serieIndex,
								final boolean isBorder,
								final boolean isDrawLine) {

		if (_dataSerie == null) {
			return null;
		}

		int colorValue = 0;
		if (_legendProvider instanceof IGradientColorProvider) {

			colorValue = ((IGradientColorProvider) _legendProvider).getColorValue(_dataSerie[serieIndex]);

		} else if (_legendProvider instanceof IDiscreteColorProvider) {

			colorValue = ((IDiscreteColorProvider) _legendProvider).getColorValue(tourData, serieIndex, isDrawLine);
		}

		if (isBorder) {

			// paint the border in a darker color

			final float brightness = 0.8f;

			final int red = (int) (((colorValue & 0xFF) >>> 0) * brightness);
			final int green = (int) (((colorValue & 0xFF00) >>> 8) * brightness);
			final int blue = (int) (((colorValue & 0xFF0000) >>> 16) * brightness);

			colorValue = ((red & 0xFF) << 0) | ((green & 0xFF) << 8) | ((blue & 0xFF) << 16);
		}

		return _colorCache.get(colorValue);
	}

	/**
	 * world pixels are not yet cached, create them now
	 * 
	 * @param tourData
	 * @param mp
	 * @param mapZoomLevel
	 * @param latitudeSerie
	 * @param longitudeSerie
	 * @param projectionId
	 * @return
	 */
	private Point[] initWorldPixelTour(	final TourData tourData,
										final MP mp,
										final int mapZoomLevel,
										final double[] latitudeSerie,
										final double[] longitudeSerie,
										final String projectionId) {

		final Point[] tourWorldPixelPosAll = new Point[latitudeSerie.length];

		for (int serieIndex = 0; serieIndex < longitudeSerie.length; serieIndex++) {

			// convert lat/long into world pixels which depends on the map projection

			tourWorldPixelPosAll[serieIndex] = mp.geoToPixel(//
					new GeoPosition(latitudeSerie[serieIndex], longitudeSerie[serieIndex]),
					mapZoomLevel);
		}

		tourData.setWorldPixelForTour(tourWorldPixelPosAll, mapZoomLevel, projectionId);
		return tourWorldPixelPosAll;
	}

	private TIntObjectHashMap<Point> initWorldPixelWayPoint(final TourData tourData,
															final Set<TourWayPoint> wayPoints,
															final MP mp,
															final String projectionId,
															final int mapZoomLevel) {
		// world pixels are not yet cached, create them now

		final TIntObjectHashMap<Point> allWayPointWorldPixel = new TIntObjectHashMap<Point>();

		for (final TourWayPoint twp : wayPoints) {

			// convert lat/long into world pixels which depends on the map projection

			final GeoPosition geoPosition = new GeoPosition(twp.getLatitude(), twp.getLongitude());

			allWayPointWorldPixel.put(twp.hashCode(), mp.geoToPixel(geoPosition, mapZoomLevel));
		}

		tourData.setWorldPixelForWayPoints(allWayPointWorldPixel, mapZoomLevel, projectionId);

		return allWayPointWorldPixel;
	}

	/**
	 * Checks if an image bounds is within the tile. The image is above the image position and one
	 * half to the left and right side
	 * 
	 * @param imageBounds
	 *            bounds of the image
	 * @param devImagePosX
	 *            x position for the image
	 * @param devImagePosY
	 *            y position for the image
	 * @param tileSize
	 *            width and height of the tile
	 * @return Returns <code>true</code> when the image is visible in the tile
	 */
	private boolean isBoundsInTile(	final Rectangle imageBounds,
									final int devImagePosX,
									final int devImagePosY,
									final int tileSize) {

		// get image size
		final int imageWidth = imageBounds.width;
		final int imageWidth2 = imageWidth / 2;
		final int imageHeight = imageBounds.height;

		final int devImagePosLeft = devImagePosX - imageWidth2;
		final int devImagePosRight = devImagePosX + imageWidth2;

		// image position top is in the opposite direction
		final int devImagePosTop = devImagePosY - imageHeight;

		if (((devImagePosLeft >= 0 && devImagePosLeft <= tileSize) || (devImagePosRight >= 0 && devImagePosRight <= tileSize))
				&& (devImagePosY >= 0 && devImagePosY <= tileSize || devImagePosTop >= 0 && devImagePosTop <= tileSize)) {
			return true;
		}

		return false;
	}

	@Override
	protected boolean isPaintingNeeded(final Map map, final Tile tile) {

		final ArrayList<TourData> tourDataList = _tourPaintConfig.getTourData();
		final ArrayList<Photo> photoList = _tourPaintConfig.getPhotos();

		if (tourDataList.size() == 0 && photoList.size() == 0) {
			return false;
		}

		if (_isImageAvailable == false) {
			createImages();
		}

		final MP mp = map.getMapProvider();
		final int mapZoomLevel = map.getZoom();
		final int tileSize = mp.getTileSize();
		final String projectionId = mp.getProjection().getId();

		// get viewport for the current tile
		final int tileWorldPixelLeft = tile.getX() * tileSize;
		final int tileWorldPixelRight = tileWorldPixelLeft + tileSize;

		final int tileWorldPixelTop = tile.getY() * tileSize;
		final int tileWorldPixelBottom = tileWorldPixelTop + tileSize;

		if (_tourPaintConfig.isTourVisible && tourDataList.size() > 0) {

			if (isPaintingNeeded_Tours(
					tourDataList,
					mp,
					mapZoomLevel,
					projectionId,
					tileWorldPixelLeft,
					tileWorldPixelRight,
					tileWorldPixelTop,
					tileWorldPixelBottom)) {

				return true;
			}
		}

		if (_tourPaintConfig.isPhotoVisible && photoList.size() > 0) {

			if (isPaintingNeeded_Photos(
					photoList,
					mp,
					mapZoomLevel,
					projectionId,
					tileWorldPixelLeft,
					tileWorldPixelRight,
					tileWorldPixelTop,
					tileWorldPixelBottom)) {

				return true;
			}
		}

		return false;
	}

	private boolean isPaintingNeeded_Photos(final ArrayList<Photo> photoList,
											final MP mp,
											final int mapZoomLevel,
											final String projectionId,
											final int tileWorldPixelLeft,
											final int tileWorldPixelRight,
											final int tileWorldPixelTop,
											final int tileWorldPixelBottom) {
		/*
		 * check photos
		 */
		for (final Photo photo : photoList) {

			final Point photoWorldPixel = photo.getWorldPosition(
					mp,
					projectionId,
					mapZoomLevel,
					_tourPaintConfig.isLinkPhotoDisplayed);

			if (photoWorldPixel == null) {
				continue;
			}

			final org.eclipse.swt.graphics.Point photoSize = photo.getMapImageSize();
			final int tileSize = mp.getTileSize();

			// convert world position into tile position
			final int devXPhoto = photoWorldPixel.x - tileWorldPixelLeft;
			final int devYPhoto = photoWorldPixel.y - tileWorldPixelTop;

			final boolean isPhotoInTile = isPhotoInTile(photoSize, devXPhoto, devYPhoto, tileSize);

			if (isPhotoInTile) {
				return true;
			}
		}

		return false;
	}

	private boolean isPaintingNeeded_Tours(	final ArrayList<TourData> tourDataList,
											final MP mp,
											final int mapZoomLevel,
											final String projectionId,
											final int tileWorldPixelLeft,
											final int tileWorldPixelRight,
											final int tileWorldPixelTop,
											final int tileWorldPixelBottom) {
		/*
		 * check tours
		 */
		for (final TourData tourData : tourDataList) {

			// check tour data
			if (tourData == null) {
				continue;
			}

			// check if position is available
			final double[] latitudeSerie = tourData.latitudeSerie;
			final double[] longitudeSerie = tourData.longitudeSerie;
			if (latitudeSerie != null && longitudeSerie != null) {

				/*
				 * world positions are cached to optimize performance when multiple tours are
				 * selected
				 */
				Point tourWorldPixelPosAll[] = tourData.getWorldPositionForTour(projectionId, mapZoomLevel);
				if ((tourWorldPixelPosAll == null)) {

					// world pixels are not yet cached, create them now

					tourWorldPixelPosAll = initWorldPixelTour(
							tourData,
							mp,
							mapZoomLevel,
							latitudeSerie,
							longitudeSerie,
							projectionId);
				}

				for (int serieIndex = 0; serieIndex < longitudeSerie.length; serieIndex++) {

					final Point tourWorldPixel = tourWorldPixelPosAll[serieIndex];

					// this is an inline for: tileViewport.contains(tileWorldPos.x, tileWorldPos.y)
					final int tourWorldPixelX = tourWorldPixel.x;
					final int tourWorldPixelY = tourWorldPixel.y;

					// check if position is within the tile viewport
					if ((tourWorldPixelX >= tileWorldPixelLeft)
							&& (tourWorldPixelY >= tileWorldPixelTop)
							&& tourWorldPixelX < tileWorldPixelRight
							&& tourWorldPixelY < tileWorldPixelBottom) {

						// current position is inside the tile

						return true;
					}
				}
			}

			/*
			 * check way points
			 */
			final Set<TourWayPoint> wayPoints = tourData.getTourWayPoints();
			if (wayPoints.size() > 0) {

				TIntObjectHashMap<Point> allWayPointWorldPixel = tourData.getWorldPositionForWayPoints(
						projectionId,
						mapZoomLevel);

				if ((allWayPointWorldPixel == null)) {
					allWayPointWorldPixel = initWorldPixelWayPoint(tourData, wayPoints, mp, projectionId, mapZoomLevel);
				}

				// get image size
				final int imageWidth = _twpImageBounds.width;
				final int imageWidth2 = imageWidth / 2;
				final int imageHeight = _twpImageBounds.height;

				for (final TourWayPoint twp : wayPoints) {

					final Point twpWorldPixel = allWayPointWorldPixel.get(twp.hashCode());

					if (twpWorldPixel == null) {
						// this happened but should not
						continue;
					}

					// this is an inline for: tileViewport.contains(tileWorldPos.x, tileWorldPos.y)
					final int twpWorldPixelX = twpWorldPixel.x;
					final int twpWorldPixelY = twpWorldPixel.y;

					final int twpImageWorldPixelX = twpWorldPixelX - imageWidth2;

					// check if twp image is within the tile viewport
					if (twpImageWorldPixelX + imageWidth >= tileWorldPixelLeft
							&& twpWorldPixelX < tileWorldPixelRight
							&& twpWorldPixelY >= tileWorldPixelTop
							&& twpWorldPixelY < tileWorldPixelBottom + imageHeight) {

						// current position is inside the tile

						return true;
					}
				}
			}
		}

		return false;
	}

	private boolean isPhotoInTile(	final org.eclipse.swt.graphics.Point photoSize,
									final int devXPhoto,
									final int devYPhoto,
									final int tileSize) {

		// get image size
		final int imageWidth = photoSize.x;
		final int imageWidth2 = imageWidth / 2;
		final int imageHeight = photoSize.y;

		final int devImagePosLeft = devXPhoto - imageWidth2;
		final int devImagePosRight = devXPhoto + imageWidth2;

		// image position top is in the opposite direction
		final int devImagePosTop = devYPhoto - imageHeight;

		if (((devImagePosLeft >= 0 && devImagePosLeft <= tileSize) || (devImagePosRight >= 0 && devImagePosRight <= tileSize))
				&& (devYPhoto >= 0 && devYPhoto <= tileSize || devImagePosTop >= 0 && devImagePosTop <= tileSize)) {
			return true;
		}

		return false;
	}

	/**
	 * Set the data serie which is painted
	 * 
	 * @param tourData
	 */
	private void setDataSerie(final TourData tourData) {

		final IMapColorProvider legendProvider = _tourPaintConfig.getMapColorProvider();

		if (legendProvider == null) {
			_dataSerie = null;
			return;
		}

		_legendProvider = legendProvider;

		switch (_legendProvider.getGraphId()) {
		case Altitude:
			_dataSerie = tourData.getAltitudeSerie();
			break;

		case Gradient:
			_dataSerie = tourData.getGradientSerie();
			break;

		case Pulse:
			_dataSerie = tourData.pulseSerie;
			break;

		case Speed:
			_dataSerie = tourData.getSpeedSerie();
			break;

		case Pace:
			_dataSerie = tourData.getPaceSerieSeconds();
			break;

		case HrZone:
			_dataSerie = tourData.pulseSerie;
			break;

		default:
			break;
		}
	}

}

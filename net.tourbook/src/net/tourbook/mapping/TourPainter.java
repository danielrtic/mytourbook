/*******************************************************************************
 * Copyright (C) 2005, 2008  Wolfgang Schramm and Contributors
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

package net.tourbook.mapping;

import java.util.ArrayList;
import java.util.List;

import net.tourbook.data.TourData;
import net.tourbook.plugin.TourbookPlugin;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

import de.byteholder.geoclipse.map.Tile;
import de.byteholder.geoclipse.map.TileFactory;
import de.byteholder.geoclipse.swt.Map;
import de.byteholder.geoclipse.swt.MapPainter;
import de.byteholder.gpx.GeoPosition;

/**
 * Paints the tour into the map
 */
public class TourPainter extends MapPainter {

	private static final String	SPACER				= " ";
	private static final String	IMAGE_START_MARKER	= "map-marker-start.png";	//$NON-NLS-1$
	private static final String	IMAGE_END_MARKER	= "map-marker-end.png";	//$NON-NLS-1$

	private static TourPainter	fInstance;

	private final Image			fImageStartMarker;
	private final Image			fImageEndMarker;
	private final Image			fPositionImage;
	private final Image			fMarkerImage;

	private int[]				fDataSerie;
	private ILegendProvider		fLegendProvider;

	public TourPainter() {

		super();

		fInstance = this;

		final Display display = Display.getCurrent();
		final Color systemColorBlue = display.getSystemColor(SWT.COLOR_BLUE);
		final Color systemColorRed = display.getSystemColor(SWT.COLOR_RED);
		fPositionImage = createPositionImage(systemColorBlue);
		fMarkerImage = createPositionImage(systemColorRed);

		fImageStartMarker = TourbookPlugin.getImageDescriptor(IMAGE_START_MARKER).createImage();
		fImageEndMarker = TourbookPlugin.getImageDescriptor(IMAGE_END_MARKER).createImage();
	}

	/**
	 * Draw legend colors into the legend bounds
	 * 
	 * @param gc
	 * @param legendBounds
	 * @param isVertical
	 *        when <code>true</code> the legend is drawn vertical, when false the legend is drawn
	 *        horizontal
	 * @param colorId
	 */
	public static void drawLegendColors(final GC gc,
										final Rectangle legendBounds,
										final ILegendProvider legendProvider,
										final boolean isVertical) {

		if (legendProvider == null) {
			return;
		}

		final LegendConfig config = legendProvider.getLegendConfig();

		// get configuration for the legend 
		final ArrayList<Integer> legendUnits = new ArrayList<Integer>(config.units);
		final Integer unitFactor = config.unitFactor;
		final int legendMaxValue = config.legendMaxValue;
		final int legendMinValue = config.legendMinValue;
		final int legendDiffValue = legendMaxValue - legendMinValue;
		final String unitText = config.unitText;
		final List<String> unitLabels = config.unitLabels;

		int legendWidth;
		int legendHeight;
		int legendPositionX;
		int legendPositionY;
		int availableLegendPixels;

		Rectangle legendBorder;

		if (isVertical) {

			// vertical legend

			legendPositionX = legendBounds.x + 1;
			legendPositionY = legendBounds.y + MappingView.LEGEND_MARGIN_TOP_BOTTOM;
			legendWidth = 20;
			legendHeight = legendBounds.height - 2 * MappingView.LEGEND_MARGIN_TOP_BOTTOM;

			availableLegendPixels = legendHeight - 1;

			legendBorder = new Rectangle(legendPositionX - 1, legendPositionY - 1, legendWidth + 1, legendHeight + 1);

		} else {

			// horizontal legend

			legendPositionX = legendBounds.x + 1;
			legendPositionY = legendBounds.y + 1;
			legendWidth = legendBounds.width - 1;
			legendHeight = legendBounds.height;

			availableLegendPixels = legendWidth - 1;

			legendBorder = legendBounds;
		}

		// pixelValue contains the value for ONE pixel
		final float pixelValue = (float) legendDiffValue / availableLegendPixels;

		// draw border around the colors
		final Color borderColor = Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);
//		final Color borderColor = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
		gc.setForeground(borderColor);
		gc.drawRectangle(legendBorder);

		final Color legendTextColor = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
		final Color legendTextBackgroundColor = Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);

		Color lineColor = null;
		int legendValue = 0;

		int unitLabelIndex = 0;

		for (int pixelIndex = 0; pixelIndex <= availableLegendPixels; pixelIndex++) {

			legendValue = (int) (legendMinValue + pixelValue * pixelIndex);

			int valuePosition;
			if (isVertical) {
				valuePosition = legendPositionY + availableLegendPixels - pixelIndex;
			} else {
				valuePosition = legendPositionX + availableLegendPixels - pixelIndex;
			}

			/*
			 * draw legend unit
			 */

			if (isVertical) {

				// find a unit which corresponds to the current legend value

				for (final Integer unitValue : legendUnits) {
					if (legendValue >= unitValue) {

						/*
						 * get unit label
						 */
						String valueText;
						if (unitLabels == null) {
							final int unit = unitValue / unitFactor;
							valueText = Integer.toString(unit) + SPACER + unitText;
						} else {
							valueText = unitLabels.get(unitLabelIndex++);
						}
						final Point valueTextExtent = gc.textExtent(valueText);

						gc.setForeground(legendTextColor);
						gc.setBackground(legendTextBackgroundColor);

						gc.drawLine(legendWidth, // 
								valuePosition, //
								legendWidth + 5,
								valuePosition);

						// draw unit value and text
						if (unitLabels == null) {
//					gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
							gc.fillRectangle(legendWidth + 5,
									valuePosition - valueTextExtent.y / 2,
									valueTextExtent.x,
									valueTextExtent.y);
						}

						gc.drawText(valueText, //
								legendWidth + 5, //
								valuePosition - valueTextExtent.y / 2, //
								true);

						// prevent to draw this unit again
						legendUnits.remove(unitValue);

						break;
					}
				}
			}

			/*
			 * draw legend color line
			 */

			lineColor = legendProvider.getValueColor(legendValue);

			if (lineColor != null) {
				gc.setForeground(lineColor);
			}

			if (isVertical) {
				gc.drawLine(legendPositionX, valuePosition, legendWidth, valuePosition);
			} else {
				gc.drawLine(valuePosition, legendPositionY, valuePosition, legendHeight);
			}

			if (lineColor != null) {
				lineColor.dispose();
			}
		}

	}

	public static TourPainter getInstance() {

		if (fInstance == null) {
			fInstance = new TourPainter();
		}

		return fInstance;
	}

	static Color getLegendColor(final LegendConfig legendConfig, final LegendColor legendColor, final int legendValue) {

		int red = 0;
		int green = 0;
		int blue = 0;

		final ValueColor[] valueColors = legendColor.valueColors;
		final float minBrightnessFactor = legendColor.minBrightnessFactor / (float) 100;
		final float maxBrightnessFactor = legendColor.maxBrightnessFactor / (float) 100;
		ValueColor valueColor;

		/*
		 * find the valueColor for the current value
		 */
		ValueColor minValueColor = null;
		ValueColor maxValueColor = null;

		for (int colorIndex = 0; colorIndex < valueColors.length; colorIndex++) {

			valueColor = valueColors[colorIndex];
			if (legendValue > valueColor.value) {
				minValueColor = valueColor;
			}
			if (legendValue <= valueColor.value) {
				maxValueColor = valueColor;
			}

			if (minValueColor != null && maxValueColor != null) {
				break;
			}
		}

		if (minValueColor == null) {

			// legend value is smaller than minimum value, dimm the color

			valueColor = valueColors[0];
			red = valueColor.red;
			green = valueColor.green;
			blue = valueColor.blue;

			final int minValue = valueColor.value;
			final int minDiff = legendConfig.legendMinValue - minValue;

			final float ratio = minDiff == 0 ? 1 : (legendValue - minValue) / (float) minDiff;
			final float dimmRatio = minBrightnessFactor * ratio;

			if (legendColor.minBrightness == LegendColor.BRIGHTNESS_DIMMING) {

				red = red - (int) (dimmRatio * red);
				green = green - (int) (dimmRatio * green);
				blue = blue - (int) (dimmRatio * blue);

			} else if (legendColor.minBrightness == LegendColor.BRIGHTNESS_LIGHTNING) {

				red = red + (int) (dimmRatio * (255 - red));
				green = green + (int) (dimmRatio * (255 - green));
				blue = blue + (int) (dimmRatio * (255 - blue));
			}

		} else if (maxValueColor == null) {

			// legend value is larger than maximum value, dimm the color

			valueColor = valueColors[valueColors.length - 1];
			red = valueColor.red;
			green = valueColor.green;
			blue = valueColor.blue;

			final int maxValue = valueColor.value;
			final int maxDiff = legendConfig.legendMaxValue - maxValue;

			final float ratio = maxDiff == 0 ? 1 : (legendValue - maxValue) / (float) maxDiff;
			final float dimmRatio = maxBrightnessFactor * ratio;

			if (legendColor.maxBrightness == LegendColor.BRIGHTNESS_DIMMING) {

				red = red - (int) (dimmRatio * red);
				green = green - (int) (dimmRatio * green);
				blue = blue - (int) (dimmRatio * blue);

			} else if (legendColor.maxBrightness == LegendColor.BRIGHTNESS_LIGHTNING) {

				red = red + (int) (dimmRatio * (255 - red));
				green = green + (int) (dimmRatio * (255 - green));
				blue = blue + (int) (dimmRatio * (255 - blue));
			}

		} else {

			// legend value is in the min/max range

			final int maxValue = maxValueColor.value;
			final int minValue = minValueColor.value;
			final int minRed = minValueColor.red;
			final int minGreen = minValueColor.green;
			final int minBlue = minValueColor.blue;

			final int redDiff = maxValueColor.red - minRed;
			final int greenDiff = maxValueColor.green - minGreen;
			final int blueDiff = maxValueColor.blue - minBlue;

			final int ratioDiff = maxValue - minValue;
			final float ratio = ratioDiff == 0 ? 1 : (legendValue - minValue) / (float) (ratioDiff);

			red = (int) (minRed + redDiff * ratio);
			green = (int) (minGreen + greenDiff * ratio);
			blue = (int) (minBlue + blueDiff * ratio);
		}

//		System.out.println(legendValue + "\t" + red + "\t" + green + "\t" + blue);

		red = Math.min(255, Math.max(0, red));
		green = Math.min(255, Math.max(0, green));
		blue = Math.min(255, Math.max(0, blue));

		return new Color(Display.getCurrent(), new RGB(red, green, blue));
	}

	private Image createPositionImage(final Color positionColor) {

		final Display display = Display.getCurrent();

		final int width = 8;
		final int height = 8;

		final Image positionImage = new Image(display, width, height);
		final Color colorTransparent = new Color(display, 0xff, 0xff, 0xfe);

		final GC gc = new GC(positionImage);

//		gc.setAntialias(SWT.ON);

		gc.setBackground(colorTransparent);
		gc.fillRectangle(0, 0, width, height);

		gc.setBackground(positionColor);
		gc.fillOval(1, 1, width - 2, height - 2);

		/*
		 * set transparency
		 */
		final ImageData imageData = positionImage.getImageData();
		imageData.transparentPixel = imageData.getPixel(0, 0);
		final Image transparentImage = new Image(display, imageData);

//		gc.setAntialias(SWT.OFF);

		gc.dispose();
		positionImage.dispose();
		colorTransparent.dispose();

		return transparentImage;
	}

	@Override
	protected void dispose() {

		disposeImage(fImageStartMarker);
		disposeImage(fImageEndMarker);
		disposeImage(fPositionImage);
		disposeImage(fMarkerImage);

	}

	private void disposeImage(final Image image) {
		if (image != null && !image.isDisposed()) {
			image.dispose();
		}
	}

	@Override
	protected void doPaint(final GC gc, final Map map) {}

	@Override
	protected boolean doPaint(final GC gc, final Map map, final Tile tile) {

		final PaintManager paintManager = PaintManager.getInstance();

		final TourData tourData = paintManager.getTourData();
		if (tourData == null) {
			return false;
		}

		final double[] latitudeSerie = tourData.latitudeSerie;
		final double[] longitudeSerie = tourData.longitudeSerie;
		if (latitudeSerie == null || longitudeSerie == null) {
			return false;
		}

		inizializeLegendData(tourData);

		// draw tour
		boolean isOverlayInTile = drawTourInTile(gc, map, tile, tourData);

		boolean isMarkerInTile = false;

		// draw end marker
		isMarkerInTile = drawMarker(gc,
				map,
				tile,
				latitudeSerie[latitudeSerie.length - 1],
				longitudeSerie[longitudeSerie.length - 1],
				fImageEndMarker);
		isOverlayInTile = isOverlayInTile || isMarkerInTile;

		// draw start marker
		isMarkerInTile = drawMarker(gc, map, tile, latitudeSerie[0], longitudeSerie[0], fImageStartMarker);
		isOverlayInTile = isOverlayInTile || isMarkerInTile;

		return isOverlayInTile;
	}

	private boolean drawMarker(	final GC gc,
								final Map map,
								final Tile tile,
								final double latitude,
								final double longitude,
								final Image markerImage) {

		if (markerImage == null) {
			return false;
		}

		final TileFactory tileFactory = map.getTileFactory();
		final int zoomLevel = map.getZoom();
		final int tileSize = tileFactory.getInfo().getTileSize();

		// get world viewport for the current tile
		final int worldTileX = tile.getX() * tileSize;
		final int worldTileY = tile.getY() * tileSize;

		// convert lat/long into world pixels
		final java.awt.Point worldMarkerPos = tileFactory.geoToPixel(new GeoPosition(latitude, longitude), zoomLevel);

		// convert world position into device position
		final int devMarkerPosX = worldMarkerPos.x - worldTileX;
		final int devMarkerPosY = worldMarkerPos.y - worldTileY;

		final boolean isMarkerInTile = isMarkerInTile(markerImage.getBounds(), devMarkerPosX, devMarkerPosY, tileSize);
		if (isMarkerInTile) {

			// get marker size
			final Rectangle bounds = markerImage.getBounds();
			final int markerWidth = bounds.width;
			final int markerWidth2 = markerWidth / 2;
			final int markerHeight = bounds.height;

			gc.drawImage(markerImage, devMarkerPosX - markerWidth2, devMarkerPosY - markerHeight);
		}

		return isMarkerInTile;
	}

	private boolean drawTourInTile(final GC gc, final Map map, final Tile tile, final TourData tourData) {

		final int lineWidth = 7;

		final TileFactory tileFactory = map.getTileFactory();
		final int zoomLevel = map.getZoom();
		final int tileSize = tileFactory.getInfo().getTileSize();

		// get viewport for the current tile
		final int worldTileX = tile.getX() * tileSize;
		final int worldTileY = tile.getY() * tileSize;
		final java.awt.Rectangle tileViewport = new java.awt.Rectangle(worldTileX, worldTileY, tileSize, tileSize);

		java.awt.Point worldPosition = null;
		java.awt.Point devPosition = null;
		java.awt.Point devPreviousPosition = null;

		final double[] latitudeSerie = tourData.latitudeSerie;
		final double[] longitudeSerie = tourData.longitudeSerie;

		final Display display = Display.getCurrent();
		final Color systemColorBlue = display.getSystemColor(SWT.COLOR_BLUE);
		gc.setForeground(systemColorBlue);
		gc.setLineWidth(lineWidth);

		boolean isTourInTile = false;
		int lastInsideIndex = -99;
		java.awt.Point lastInsidePosition = null;

		for (int serieIndex = 0; serieIndex < longitudeSerie.length; serieIndex++) {

			// convert lat/long into world pixels
			worldPosition = tileFactory.geoToPixel(new GeoPosition(latitudeSerie[serieIndex],
					longitudeSerie[serieIndex]), zoomLevel);

			// convert world position into device position
			devPosition = new java.awt.Point(worldPosition.x - worldTileX, worldPosition.y - worldTileY);

			// initialize previous pixel
			if (devPreviousPosition == null) {
				devPreviousPosition = devPosition;
			}

			// check if position is in the viewport or position has changed
			if (tileViewport.contains(worldPosition)) {

				// current position is inside the tile

				if (devPosition.equals(devPreviousPosition) == false) {

					isTourInTile = true;

//					gc.drawImage(fPositionImage, devPosition.x - posImageWidth, devPosition.y - posImageHeight);

					drawTourLine(gc, serieIndex, devPosition, devPreviousPosition);
				}

				lastInsideIndex = serieIndex;
				lastInsidePosition = devPosition;

			} else {

				// current position is outside the tile

				if (serieIndex == lastInsideIndex + 1) {

					/*
					 * this position is the first which is outside of the tile, draw a line from the
					 * last inside to the first outside position
					 */

					drawTourLine(gc, serieIndex, devPosition, lastInsidePosition);
				}
			}

			devPreviousPosition = devPosition;
		}

		return isTourInTile;
	}

	private void drawTourLine(	final GC gc,
								final int serieIndex,
								final java.awt.Point devPosition,
								final java.awt.Point devPreviousPosition) {

		if (fDataSerie == null) {

			gc.drawLine(devPreviousPosition.x, devPreviousPosition.y, devPosition.x, devPosition.y);

		} else {

			final Color lineColor = fLegendProvider.getValueColor(fDataSerie[serieIndex]);

			{
				gc.setForeground(lineColor);
				gc.drawLine(devPreviousPosition.x, devPreviousPosition.y, devPosition.x, devPosition.y);
			}

			lineColor.dispose();
		}

	}

	private void inizializeLegendData(final TourData tourData) {

		fLegendProvider = PaintManager.getInstance().getLegendProvider();

		switch (fLegendProvider.getTourColorId()) {
		case MappingView.TOUR_COLOR_ALTITUDE:

			final int[] altitudeSerie = tourData.getAltitudeSerie();
			if (altitudeSerie == null) {
				fDataSerie = null;
			} else {
				fDataSerie = altitudeSerie;
			}
			break;

		case MappingView.TOUR_COLOR_GRADIENT:

			final int[] gradientSerie = tourData.getGradientSerie();
			if (gradientSerie == null) {
				fDataSerie = null;
			} else {
				fDataSerie = gradientSerie;
			}
			break;

		case MappingView.TOUR_COLOR_PULSE:

			final int[] pulseSerie = tourData.pulseSerie;
			if (pulseSerie == null) {
				fDataSerie = null;
			} else {
				fDataSerie = pulseSerie;
			}
			break;

		case MappingView.TOUR_COLOR_SPEED:

			final int[] speedSerie = tourData.getSpeedSerie();
			if (speedSerie == null) {
				fDataSerie = null;
			} else {
				fDataSerie = speedSerie;
			}
			break;

		case MappingView.TOUR_COLOR_PACE:

			final int[] paceSerie = tourData.getPaceSerie();
			if (paceSerie == null) {
				fDataSerie = null;
			} else {
				fDataSerie = paceSerie;
			}
			break;

		default:
			break;
		}
	}

	/**
	 * Checks if the marker position is within the tile. The marker is above the marker position and
	 * one half to the left and right side
	 * 
	 * @param markerBounds
	 *        marker bounds
	 * @param devMarkerPosX
	 *        x position for the marker
	 * @param devMarkerPosY
	 *        y position for the marker
	 * @param tileSize
	 *        width and height of the tile
	 * @return Returns <code>true</code> when the marker is visible in the tile
	 */
	private boolean isMarkerInTile(	final Rectangle markerBounds,
									final int devMarkerPosX,
									final int devMarkerPosY,
									final int tileSize) {

		// get marker size
		final int markerWidth = markerBounds.width;
		final int markerWidth2 = markerWidth / 2;
		final int markerHeight = markerBounds.height;

		final int devMarkerPosLeft = devMarkerPosX - markerWidth2;
		final int devMarkerPosRight = devMarkerPosX + markerWidth2;

		// marker position top is in the opposite direction
		final int devMarkerPosTop = devMarkerPosY - markerHeight;

		if ((devMarkerPosLeft >= 0 && devMarkerPosLeft <= tileSize)
				|| (devMarkerPosRight >= 0 && devMarkerPosRight <= tileSize)) {

			if (devMarkerPosY >= 0 && devMarkerPosY <= tileSize || devMarkerPosTop >= 0 && devMarkerPosTop <= tileSize) {
				return true;
			}
		}

		return false;
	}
}

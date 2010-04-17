/*******************************************************************************
 * Copyright (C) 2005, 2010 Wolfgang Schramm and Contributors
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
package net.tourbook.device.gpx;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.TimeZone;

import net.tourbook.data.TimeData;
import net.tourbook.data.TourData;
import net.tourbook.data.TourWayPoint;
import net.tourbook.device.DeviceReaderTools;
import net.tourbook.importdata.DeviceData;
import net.tourbook.importdata.TourbookDevice;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class GPX_SAX_Handler extends DefaultHandler {

	private static final String				NAME_SPACE_GPX_1_0		= "http://www.topografix.com/GPX/1/0";				//$NON-NLS-1$
	private static final String				NAME_SPACE_GPX_1_1		= "http://www.topografix.com/GPX/1/1";				//$NON-NLS-1$

	// namespace for extensions used by Garmin
//	private static final String				NAME_SPACE_TPEXT		= "http://www.garmin.com/xmlschemas/TrackPointExtension/v1";	//$NON-NLS-1$

	private static final int				GPX_VERSION_1_0			= 10;
	private static final int				GPX_VERSION_1_1			= 11;

	/*
	 * gpx tags, attributes
	 */
	private static final String				TAG_GPX					= "gpx";											//$NON-NLS-1$

	private static final String				TAG_TRK					= "trk";											//$NON-NLS-1$
	private static final String				TAG_TRKPT				= "trkpt";											//$NON-NLS-1$

	private static final String				TAG_TIME				= "time";											//$NON-NLS-1$
	private static final String				TAG_ELE					= "ele";											//$NON-NLS-1$

	// Extension element for temperature, heart rate, cadence
	private static final String				TAG_EXT_CAD				= "gpxtpx:cad";									//$NON-NLS-1$
	private static final String				TAG_EXT_HR				= "gpxtpx:hr";										//$NON-NLS-1$
	private static final String				TAG_EXT_TEMP			= "gpxtpx:atemp";									//$NON-NLS-1$

	private static final String				ATTR_LATITUDE			= "lat";											//$NON-NLS-1$
	private static final String				ATTR_LONGITUDE			= "lon";											//$NON-NLS-1$

	private static final String				TAG_WPT					= "wpt";											//$NON-NLS-1$
	private static final Object				TAG_WPT_ELE				= "ele";											//$NON-NLS-1$
	private static final Object				TAG_WPT_TIME			= "time";											//$NON-NLS-1$
	private static final Object				TAG_WPT_NAME			= "name";											//$NON-NLS-1$
	private static final Object				TAG_WPT_CMT				= "cmt";											//$NON-NLS-1$
	private static final Object				TAG_WPT_DESC			= "desc";											//$NON-NLS-1$
	private static final Object				TAG_WPT_SYM				= "sym";											//$NON-NLS-1$
	private static final Object				TAG_WPT_TYPE			= "type";											//$NON-NLS-1$
	////////////////////////

	private static final Calendar			_calendar				= GregorianCalendar.getInstance();

	private static final DateTimeFormatter	_dtIsoParser			= ISODateTimeFormat.dateTimeParser();

	private static final SimpleDateFormat	GPX_TIME_FORMAT			= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); //$NON-NLS-1$
	private static final SimpleDateFormat	GPX_TIME_FORMAT_SSSZ	= new SimpleDateFormat(
																			"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");			//$NON-NLS-1$
	private static final SimpleDateFormat	GPX_TIME_FORMAT_RFC822	= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");	//$NON-NLS-1$

	private int								_gpxVersion				= -1;

	private boolean							_isInTrk				= false;
	private boolean							_isInTrkPt				= false;

	private boolean							_isInTime				= false;
	private boolean							_isInEle				= false;
	private final boolean					_isInName				= false;

	// gpx extensions
	private boolean							_isInCadence			= false;
	private boolean							_isInHr					= false;
	private boolean							_isInTemp				= false;

	/*
	 * wap points
	 */
	private boolean							_isInWpt				= false;
	private boolean							_isInWptEle				= false;
	private boolean							_isInWptTime			= false;
	private boolean							_isInWptName			= false;
	private boolean							_isInWptCmt				= false;
	private boolean							_isInWptDesc			= false;
	private boolean							_isInWptSym				= false;
	private boolean							_isInWptType			= false;

	private final ArrayList<TimeData>		_timeDataList			= new ArrayList<TimeData>();
	private TimeData						_timeData;
	private TimeData						_prevTimeData;

	private final TourbookDevice			_deviceDataReader;
	private final String					_importFilePath;
	private final HashMap<Long, TourData>	_tourDataMap;
	private int								_lapCounter;
	private ArrayList<Long>					_lapStart;

	private final ArrayList<TourWayPoint>	_wptList				= new ArrayList<TourWayPoint>();
	private TourWayPoint					_wpt;

	private boolean							_isSetLapMarker			= false;
	private boolean							_isSetLapStartTime		= false;

	private long							_currentTime;
	private float							_absoluteDistance;

	private boolean							_isImported;
	private boolean							_isError				= false;

	private final StringBuilder				_characters				= new StringBuilder();

	{
		GPX_TIME_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC")); //$NON-NLS-1$
		GPX_TIME_FORMAT_SSSZ.setTimeZone(TimeZone.getTimeZone("UTC")); //$NON-NLS-1$
		GPX_TIME_FORMAT_RFC822.setTimeZone(TimeZone.getTimeZone("UTC")); //$NON-NLS-1$
	}

	public GPX_SAX_Handler(	final TourbookDevice deviceDataReader,
							final String importFileName,
							final DeviceData deviceData,
							final HashMap<Long, TourData> tourDataMap) {

		_deviceDataReader = deviceDataReader;
		_importFilePath = importFileName;
		_tourDataMap = tourDataMap;
	}

	@Override
	public void characters(final char[] chars, final int startIndex, final int length) throws SAXException {

		if (_isInTime //
				|| _isInEle
				|| _isInName
				|| _isInCadence
				|| _isInHr
				|| _isInTemp
				//
				|| _isInWptCmt
				|| _isInWptDesc
				|| _isInWptEle
				|| _isInWptName
				|| _isInWptSym
				|| _isInWptTime
				|| _isInWptType) {

			_characters.append(chars, startIndex, length);
		}
	}

	private void displayError(final ParseException e) {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				final String message = e.getMessage();
				MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", message); //$NON-NLS-1$
				System.err.println(message + " in " + _importFilePath); //$NON-NLS-1$
			}
		});
	}

	@Override
	public void endElement(final String uri, final String localName, final String name) throws SAXException {

//		System.out.println("</" + name + ">");

		if (_isError) {
			return;
		}

		try {

			if (_isInTrkPt) {

				final String charData = _characters.toString();

				if (name.equals(TAG_ELE)) {

					// </ele>

					_isInEle = false;

					_timeData.absoluteAltitude = getFloatValue(charData);

				} else if (name.equals(TAG_TIME)) {

					// </time>

					_isInTime = false;

					try {
						_timeData.absoluteTime = _currentTime = _dtIsoParser.parseDateTime(charData).getMillis();
					} catch (final Exception e0) {
						try {
							_timeData.absoluteTime = _currentTime = GPX_TIME_FORMAT.parse(charData).getTime();
						} catch (final ParseException e1) {
							try {
								_timeData.absoluteTime = _currentTime = GPX_TIME_FORMAT_SSSZ.parse(charData).getTime();
							} catch (final ParseException e2) {
								try {
									_timeData.absoluteTime = _currentTime = GPX_TIME_FORMAT_RFC822
											.parse(charData)
											.getTime();
								} catch (final ParseException e3) {

									_isError = true;

									displayError(e3);
								}
							}
						}
					}

				} else if (name.equals(TAG_EXT_CAD)) {

					// </gpxtpx:cad>

					_isInCadence = false;
					_timeData.cadence = getIntValue(charData);

				} else if (name.equals(TAG_EXT_HR)) {

					// </gpxtpx:hr>

					_isInHr = false;
					_timeData.pulse = getIntValue(charData);

				} else if (name.equals(TAG_EXT_TEMP)) {

					// </gpxtpx:atemp>

					_isInTemp = false;
					_timeData.temperature = Math.round(getFloatValue(charData));
				}

			} else if (_isInWpt) {

				// in <wpt>

				final String charData = _characters.toString();

				if (name.equals(TAG_WPT_ELE)) {

					// </ele> elevation

					_isInWptEle = false;

					_wpt.setAltitude(getFloatValue(charData));

				} else if (name.equals(TAG_WPT_TIME)) {

					// </time>

					_isInWptTime = false;

					try {
						_wpt.setTime(_dtIsoParser.parseDateTime(charData).getMillis());
					} catch (final Exception e0) {
						try {
							_wpt.setTime(GPX_TIME_FORMAT.parse(charData).getTime());
						} catch (final ParseException e1) {
							try {
								_wpt.setTime(GPX_TIME_FORMAT_SSSZ.parse(charData).getTime());
							} catch (final ParseException e2) {
								try {
									_wpt.setTime(GPX_TIME_FORMAT_RFC822.parse(charData).getTime());
								} catch (final ParseException e3) {

									_isError = true;

									displayError(e3);
								}
							}
						}
					}

				} else if (name.equals(TAG_WPT_NAME)) {

					// </name> name

					_isInWptName = false;
					_wpt.setName(charData);

				} else if (name.equals(TAG_WPT_CMT)) {

					// </cmt> comment

					_isInWptCmt = false;
					_wpt.setComment(charData);

				} else if (name.equals(TAG_WPT_DESC)) {

					// </desc> description

					_isInWptDesc = false;
					_wpt.setDescription(charData);

				} else if (name.equals(TAG_WPT_SYM)) {

					// </sym> symbol

					_isInWptSym = false;
					_wpt.setSymbol(charData);

				} else if (name.equals(TAG_WPT_TYPE)) {

					// </type> type/category

					_isInWptType = true;
					_wpt.setCategory(charData);
				}
			}

			if (name.equals(TAG_TRKPT)) {

				/*
				 * trackpoint ends
				 */

				_isInTrkPt = false;

				finalizeTrackpoint();

			} else if (name.equals(TAG_TRK)) {

				/*
				 * track ends
				 */

				_isInTrk = false;

				if (_deviceDataReader.isMergeTracks == false) {
					setTourData();
				}

			} else if (name.equals(TAG_WPT)) {

				/*
				 * way point ends
				 */

				_isInWpt = false;

				finalizeWayPoint();

			} else if (name.equals(TAG_GPX)) {

				/*
				 * file end
				 */

				if (_deviceDataReader.isMergeTracks) {
					setTourData();
				}
			}

		} catch (final NumberFormatException e) {
			e.printStackTrace();
		}

	}

	private void finalizeTrackpoint() {

		if (_timeData == null) {
			return;
		}

		if (_isSetLapMarker) {
			_isSetLapMarker = false;

			_timeData.marker = 1;
			_timeData.markerLabel = Integer.toString(_lapCounter - 1);
		}

		_timeDataList.add(_timeData);

		if (_isSetLapStartTime) {
			_isSetLapStartTime = false;
			_lapStart.add(_currentTime);
		}

		// calculate distance
		if (_prevTimeData == null) {
			// first time data
			_timeData.absoluteDistance = 0;
		} else {
			if (_timeData.absoluteDistance == Float.MIN_VALUE) {
				_timeData.absoluteDistance = _absoluteDistance += DeviceReaderTools.computeDistance(
						_prevTimeData.latitude,
						_prevTimeData.longitude,
						_timeData.latitude,
						_timeData.longitude);
			}
		}

		// set virtual time if time is not available
		if (_timeData.absoluteTime == Long.MIN_VALUE) {

			_calendar.set(2000, 0, 1, 0, 0, 0);
			_timeData.absoluteTime = _calendar.getTimeInMillis();
		}

		_prevTimeData = _timeData;
	}

	private void finalizeWayPoint() {

		if (_wpt == null) {
			return;
		}

		// lat/lon are required fields
		if ((_wpt.getLatitude() == Double.MIN_VALUE) || (_wpt.getLongitude() == Double.MIN_VALUE)) {
			_wpt = null;
			return;
		}

		_wptList.add(_wpt);

		_wpt = null;
	}

	private double getDoubleValue(final String textValue) {

		try {
			if (textValue != null) {
				return Double.parseDouble(textValue);
			} else {
				return Double.MIN_VALUE;
			}

		} catch (final NumberFormatException e) {
			return Double.MIN_VALUE;
		}
	}

	private float getFloatValue(final String textValue) {

		try {
			if (textValue != null) {
				return Float.parseFloat(textValue);
			} else {
				return Float.MIN_VALUE;
			}

		} catch (final NumberFormatException e) {
			return Float.MIN_VALUE;
		}
	}

	private int getIntValue(final String textValue) {
		try {
			if (textValue != null) {
				return Integer.parseInt(textValue);
			} else {
				return Integer.MIN_VALUE;
			}

		} catch (final NumberFormatException e) {
			return Integer.MIN_VALUE;
		}
	}

//	private short getShortValue(String textValue) {
//
//		try {
//			if (textValue != null) {
//				return Short.parseShort(textValue);
//			} else {
//				return Short.MIN_VALUE;
//			}
//		} catch (NumberFormatException e) {
//			return Short.MIN_VALUE;
//		}
//	}

	private void initNewTrack() {
		_timeDataList.clear();
		_absoluteDistance = 0;
		_prevTimeData = null;
	}

	/**
	 * @return Returns <code>true</code> when a tour was imported
	 */
	public boolean isImported() {

		if (_isError) {
			return false;
		}

		return _isImported;
	}

	private void setTourData() {

		if (_timeDataList.size() == 0) {
			// there is not data
			return;
		}

		// create data object for each tour
		final TourData tourData = new TourData();

		/*
		 * set tour start date/time
		 */
		_calendar.setTimeInMillis(_timeDataList.get(0).absoluteTime);

		tourData.setStartHour((short) _calendar.get(Calendar.HOUR_OF_DAY));
		tourData.setStartMinute((short) _calendar.get(Calendar.MINUTE));
		tourData.setStartSecond((short) _calendar.get(Calendar.SECOND));

		tourData.setStartYear((short) _calendar.get(Calendar.YEAR));
		tourData.setStartMonth((short) (_calendar.get(Calendar.MONTH) + 1));
		tourData.setStartDay((short) _calendar.get(Calendar.DAY_OF_MONTH));
		tourData.setWeek(tourData.getStartYear(), tourData.getStartMonth(), tourData.getStartDay());

		tourData.setDeviceTimeInterval((short) -1);
		tourData.importRawDataFile = _importFilePath;
		tourData.setTourImportFilePath(_importFilePath);

		tourData.createTimeSeries(_timeDataList, true);
		tourData.computeAltitudeUpDown();

		tourData.setWayPoints(_wptList);

//		System.out.println();
//		// TODO remove SYSTEM.OUT.PRINTLN
//		for (final TourWayPoint wpt : _wptList) {
//			System.out.println(wpt);
//			// TODO remove SYSTEM.OUT.PRINTLN
//		}
//		System.out.println();
//		// TODO remove SYSTEM.OUT.PRINTLN

		// after all data are added, the tour id can be created
		final int[] distanceSerie = tourData.getMetricDistanceSerie();
		String uniqueKey;

		if (_deviceDataReader.isCreateTourIdWithTime) {

			/*
			 * 23.3.2009: added recording time to the tour distance for the unique key because tour
			 * export and import found a wrong tour when exporting was done with camouflage speed ->
			 * this will result in a NEW tour
			 */
			final int tourRecordingTime = tourData.getTourRecordingTime();

			if (distanceSerie == null) {
				uniqueKey = Integer.toString(tourRecordingTime);
			} else {

				final long tourDistance = distanceSerie[(distanceSerie.length - 1)];

				uniqueKey = Long.toString(tourDistance + tourRecordingTime);
			}

		} else {

			/*
			 * original version to create tour id
			 */
			if (distanceSerie == null) {
				uniqueKey = "42984"; //$NON-NLS-1$
			} else {
				uniqueKey = Integer.toString(distanceSerie[distanceSerie.length - 1]);
			}
		}

		final Long tourId = tourData.createTourId(uniqueKey);

		// check if the tour is already imported
		if (_tourDataMap.containsKey(tourId) == false) {

			tourData.computeTourDrivingTime();
			tourData.computeComputedValues();

			tourData.setDeviceId(_deviceDataReader.deviceId);
			tourData.setDeviceName(_deviceDataReader.visibleName);

			// add new tour to other tours
			_tourDataMap.put(tourId, tourData);
		}

		_isImported = true;
	}

	@Override
	public void startElement(final String uri, final String localName, final String name, final Attributes attributes)
			throws SAXException {

//		System.out.print("<" + name + ">");

		if (_isError) {
			return;
		}

		if (_gpxVersion < 0) {

			// gpx version is not set

			if (name.equals(TAG_GPX)) {

				/*
				 * get version of the xml file
				 */
				for (int attrIndex = 0; attrIndex < attributes.getLength(); attrIndex++) {

					final String value = attributes.getValue(attrIndex);

					if (value.contains(NAME_SPACE_GPX_1_0)) {

						_gpxVersion = GPX_VERSION_1_0;

						if (_deviceDataReader.isMergeTracks) {
							initNewTrack();
						}

						break;

					} else if (value.contains(NAME_SPACE_GPX_1_1)) {

						_gpxVersion = GPX_VERSION_1_1;

						if (_deviceDataReader.isMergeTracks) {
							initNewTrack();
						}

						break;
					}
				}
			}

		} else if ((_gpxVersion == GPX_VERSION_1_0) || (_gpxVersion == GPX_VERSION_1_1)) {

			// name space: http://www.topografix.com/GPX/1/0/gpx.xsd

			if (_isInTrk) {

				if (_isInTrkPt) {

					if (name.equals(TAG_ELE)) {

						_isInEle = true;
						_characters.delete(0, _characters.length());

					} else if (name.equals(TAG_TIME)) {

						_isInTime = true;
						_characters.delete(0, _characters.length());

					} else if (name.equals(TAG_EXT_CAD)) {

						_isInCadence = true;
						_characters.delete(0, _characters.length());

					} else if (name.equals(TAG_EXT_HR)) {

						_isInHr = true;
						_characters.delete(0, _characters.length());

					} else if (name.equals(TAG_EXT_TEMP)) {

						_isInTemp = true;
						_characters.delete(0, _characters.length());
					}

				} else if (name.equals(TAG_TRKPT) /* || name.equals(TAG_RTEPT) */) {

					/*
					 * new trackpoing
					 */
					_isInTrkPt = true;

					// create new time item
					_timeData = new TimeData();

					// get attributes
					_timeData.latitude = getDoubleValue(attributes.getValue(ATTR_LATITUDE));
					_timeData.longitude = getDoubleValue(attributes.getValue(ATTR_LONGITUDE));
				}

			} else if (_isInWpt) {

				if (name.equals(TAG_WPT_ELE)) {

					_isInWptEle = true;
					_characters.delete(0, _characters.length());

				} else if (name.equals(TAG_WPT_TIME)) {

					_isInWptTime = true;
					_characters.delete(0, _characters.length());

				} else if (name.equals(TAG_WPT_NAME)) {

					_isInWptName = true;
					_characters.delete(0, _characters.length());

				} else if (name.equals(TAG_WPT_CMT)) {

					_isInWptCmt = true;
					_characters.delete(0, _characters.length());

				} else if (name.equals(TAG_WPT_DESC)) {

					_isInWptDesc = true;
					_characters.delete(0, _characters.length());

				} else if (name.equals(TAG_WPT_SYM)) {

					_isInWptSym = true;
					_characters.delete(0, _characters.length());

				} else if (name.equals(TAG_WPT_TYPE)) {

					_isInWptType = true;
					_characters.delete(0, _characters.length());

				}

			} else if (name.equals(TAG_TRK)) {

				/*
				 * new track starts
				 */

				_isInTrk = true;

				if (_deviceDataReader.isMergeTracks == false) {
					initNewTrack();
				}

			} else if (name.equals(TAG_WPT)) {

				/*
				 * new way point starts
				 */

				_isInWpt = true;

				_wpt = new TourWayPoint();

				// get attributes
				_wpt.setLatitude(getDoubleValue(attributes.getValue(ATTR_LATITUDE)));
				_wpt.setLongitude(getDoubleValue(attributes.getValue(ATTR_LONGITUDE)));
			}
		}
	}
}

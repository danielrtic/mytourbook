/*******************************************************************************
 * Copyright (C) 2005, 2010  Wolfgang Schramm and Contributors
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
package net.tourbook.statistics;

import java.util.ArrayList;
import java.util.HashMap;

public class TourDayData {

	long[]							tourIds;

	long[]							typeIds;
	int[]							typeColorIndex;

	int[]							yearValues;
	int[]							monthValues;
	int[]							doyValues;
	int[]							weekValues;

	int[]							years;
	int[]							yearDays;
	int								allDaysInAllYears;

	int[]							distanceLow;
	int[]							altitudeLow;
	int[]							timeLow;

	int[]							distanceHigh;
	int[]							altitudeHigh;
	int[]							timeHigh;

	int[]							tourStartValues;
	int[]							tourEndValues;

	int[]							tourDistanceValues;
	int[]							tourAltitudeValues;

	ArrayList<String>				tourTitle;

	ArrayList<Integer>				tourRecordingTimeValues;
	ArrayList<Integer>				tourDrivingTimeValues;

	ArrayList<String>				tourDescription;

	/**
	 * hashmap contains the tags for the tour where the key is the tour ID
	 */
	HashMap<Long, ArrayList<Long>>	tagIds;


}

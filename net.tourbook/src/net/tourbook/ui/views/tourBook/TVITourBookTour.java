/*******************************************************************************
 * Copyright (C) 2005, 2007  Wolfgang Schramm
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
package net.tourbook.ui.views.tourBook;

import net.tourbook.tour.TreeViewerItem;

public class TVITourBookTour extends TourBookTreeViewerItem {

	long	fColumnStartDistance;
	long	fTourId;
	long	fTourTypeId;
	short	fColumnTimeInterval;

	public TVITourBookTour(TourBookView view, TreeViewerItem parentItem) {

		super(view);

		setParentItem(parentItem);
	}

	/**
	 * tour items do not have children
	 * 
	 * @see net.tourbook.tour.TreeViewerItem#hasChildren()
	 */
	public boolean hasChildren() {
		return false;
	}

	public Long getTourId() {
		return fTourId;
	}

	protected void fetchChildren() {}

	protected void remove() {}

	public long getColumnStartDistance() {
		return fColumnStartDistance;
	}

	public long getTourTypeId() {
		return fTourTypeId;
	}

	public short getColumnTimeInterval() {
		return fColumnTimeInterval;
	}

}

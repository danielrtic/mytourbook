/*******************************************************************************
 * Copyright (C) 2020 Frédéric Bard and Contributors
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
package net.tourbook.chart;

import org.eclipse.jface.action.Action;

public class ActionSelectAllTimeSlices extends Action {

	private Chart	_chart;

	public ActionSelectAllTimeSlices(final Chart chart) {

		_chart = chart;

      setText("TODO");//Messages.Action_move_left_slider_here);
	}

	@Override
	public void run() {
		_chart.onExecuteMoveLeftSliderHere();

      /*
       * final TourDataEditorView tourDataEditor = TourManager.getTourDataEditor();
       * if (tourDataEditor != null) {
       * tourDataEditor.updateUI(tourData, true);
       * fireTourChangeEvent(tourData);
       * }
       */
//      final TourDataEditorView tourDataEditor = TourManager.getTourDataEditor();
//      TourManager.fireEventWithCustomData(//
//            TourEventId.SLIDER_POSITION_CHANGED,
//            chartInfo,
//            TourChartView.this);
	}

}

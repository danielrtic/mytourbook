/*******************************************************************************
 * Copyright (C) 2023, 2024 Frédéric Bard and Contributors
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
package net.tourbook.export;

import java.util.List;

import net.tourbook.common.UI;
import net.tourbook.data.TourData;
import net.tourbook.extension.export.ExportTourExtension;

import org.eclipse.swt.widgets.Display;

/**
 * Export tours in the FIT data format
 */
public class ExportTourFIT extends ExportTourExtension {

   /**
    * Plugin extension constructor
    */
   public ExportTourFIT() {

      setImageDescriptor(Activator.getImageDescriptor(UI.IS_DARK_THEME
            ? ExportImages.Export_FIT_Logo_Dark
            : ExportImages.Export_FIT_Logo));
   }

   @Override
   public void exportTours(final List<TourData> tourDataList, final int tourStartIndex, final int tourEndIndex) {

      new DialogExportTour(
            Display.getCurrent().getActiveShell(),
            this,
            tourDataList,
            tourStartIndex,
            tourEndIndex,
            "fit", //$NON-NLS-1$
            getImageDescriptor()).open();
   }
}

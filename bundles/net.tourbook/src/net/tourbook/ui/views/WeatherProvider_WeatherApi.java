/*******************************************************************************
 * Copyright (C) 2022, 2024 Frédéric Bard
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
package net.tourbook.ui.views;

import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import java.time.LocalDate;

import net.tourbook.Messages;
import net.tourbook.common.UI;
import net.tourbook.common.time.TimeTools;
import net.tourbook.weather.HistoricalWeatherRetriever;
import net.tourbook.weather.weatherapi.WeatherApiRetriever;
import net.tourbook.web.WEB;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class WeatherProvider_WeatherApi implements IWeatherProvider {

   private static final String URL_WEATHERAPI_COM = "https://www.weatherapi.com/";//$NON-NLS-1$

   public WeatherProvider_WeatherApi() {}

   @Override
   public Composite createUI(final WeatherProvidersUI weatherProvidersUI,
                             final Composite parent,
                             final FormToolkit formToolkit) {

      final PixelConverter pc = new PixelConverter(parent);

      final Composite container = formToolkit.createComposite(parent, SWT.NONE);
      GridDataFactory.fillDefaults().grab(true, false).applyTo(container);
      GridLayoutFactory.fillDefaults().applyTo(container);
      {
         /*
          * Weather API webpage
          */
         final Link linkApiSignup = new Link(container, SWT.PUSH);
         linkApiSignup.setText(UI.getLinkFromText(URL_WEATHERAPI_COM));
         linkApiSignup.setEnabled(true);
         linkApiSignup.addListener(SWT.Selection, event -> WEB.openUrl(URL_WEATHERAPI_COM));

         GridDataFactory.fillDefaults()
               .indent(DEFAULT_H_INDENT, 0)
               .applyTo(linkApiSignup);
      }
      {
         /*
          * Button: test connection
          */
         final Button btnTestConnection = new Button(container, SWT.NONE);
         btnTestConnection.setText(Messages.Pref_Weather_Button_TestHTTPConnection);
         btnTestConnection.addSelectionListener(widgetSelectedAdapter(selectionEvent -> {

            HistoricalWeatherRetriever.checkVendorConnection(
                  WeatherApiRetriever.getBaseApiUrl() +
                        "?lat=40&lon=-105&dt=" + //$NON-NLS-1$
                        LocalDate.now().format(TimeTools.Formatter_YearMonthDay),
                  IWeatherProvider.WEATHER_PROVIDER_WEATHERAPI_NAME);
         }));

         GridDataFactory.fillDefaults()
               .indent(DEFAULT_H_INDENT, 0)
               .align(SWT.BEGINNING, SWT.FILL)
               .applyTo(btnTestConnection);
      }

      {
         /*
          * Label:
          */
         final Label note = UI.createLabel(container, Messages.Pref_Weather_Label_WeatherApi_SevenDaysLimit, SWT.WRAP);
         GridDataFactory.fillDefaults()
               .grab(true, false)
               .indent(DEFAULT_H_INDENT, 0)
               .hint(pc.convertWidthInCharsToPixels(40), SWT.DEFAULT)
               .applyTo(note);
      }
      return container;
   }

   @Override
   public void dispose() {}

   @Override
   public void performDefaults() {}

   @Override
   public void saveState() {}
}

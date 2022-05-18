/*******************************************************************************
 * Copyright (C) 2022 Frédéric Bard
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
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class WeatherProvider_WeatherApi implements IWeatherProvider {

   private static final String URL_WEATHERAPI_COM = "https://www.weatherapi.com/";//$NON-NLS-1$

   /*
    * UI controls
    */
   private Button _btnTestConnection;

   public WeatherProvider_WeatherApi() {}

   @Override
   public Composite createUI(final WeatherProvidersUI weatherProvidersUI,
                             final Composite parent,
                             final FormToolkit formToolkit) {

      final int defaultHIndent = 16;

      final Composite container = formToolkit.createComposite(parent, SWT.NONE);
      GridDataFactory.fillDefaults().grab(true, false).applyTo(container);
      GridLayoutFactory.fillDefaults().applyTo(container);
      {
         /*
          * Weather API webpage
          */
         final Link linkApiSignup = new Link(container, SWT.PUSH);
         linkApiSignup.setText(UI.LINK_TAG_START + URL_WEATHERAPI_COM + UI.LINK_TAG_END);
         linkApiSignup.setEnabled(true);
         linkApiSignup.addListener(SWT.Selection, event -> WEB.openUrl(URL_WEATHERAPI_COM));

         GridDataFactory.fillDefaults()
               .span(2, 1)
               .indent(defaultHIndent, 0)
               .applyTo(linkApiSignup);
      }
      {
         /*
          * Button: test connection
          */
         _btnTestConnection = new Button(container, SWT.NONE);
         _btnTestConnection.setText(Messages.Pref_Weather_Button_TestHTTPConnection);
         _btnTestConnection.addSelectionListener(widgetSelectedAdapter(selectionEvent -> {

            HistoricalWeatherRetriever.checkVendorConnection(
                  WeatherApiRetriever.getBaseApiUrl() +
                        "?lat=0&lon=0&dt=" + //$NON-NLS-1$
                        LocalDate.now().format(TimeTools.Formatter_YearMonthDay),
                  IWeatherProvider.WEATHER_PROVIDER_WEATHERAPI_NAME);
         }));

         GridDataFactory.fillDefaults()
               .indent(defaultHIndent, 0)
               .align(SWT.BEGINNING, SWT.FILL)
               .span(2, 1)
               .applyTo(_btnTestConnection);
      }

      {
         /*
          * Label:
          */
         final Label note = new Label(container, SWT.NONE);
         note.setText(Messages.Pref_Weather_WeatherApi_Label_SevenDaysLimit);

         GridDataFactory.fillDefaults()
               .indent(defaultHIndent, 0)
               .align(SWT.BEGINNING, SWT.FILL)
               .span(2, 1)
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

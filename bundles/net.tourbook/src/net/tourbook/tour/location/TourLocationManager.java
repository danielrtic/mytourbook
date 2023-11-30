/*******************************************************************************
 * Copyright (C) 2023 Wolfgang Schramm and Contributors
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
package net.tourbook.tour.location;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpConnectTimeoutException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import net.tourbook.application.ApplicationVersion;
import net.tourbook.application.TourbookPlugin;
import net.tourbook.common.UI;
import net.tourbook.common.time.TimeTools;
import net.tourbook.common.util.StatusUtil;
import net.tourbook.common.util.StringUtils;
import net.tourbook.common.util.Util;
import net.tourbook.data.TourData;
import net.tourbook.data.TourLocation;
import net.tourbook.database.TourDatabase;
import net.tourbook.tour.TourEvent;
import net.tourbook.tour.TourEventId;
import net.tourbook.tour.TourLogManager;
import net.tourbook.tour.TourManager;
import net.tourbook.ui.Messages;
import net.tourbook.web.WEB;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.nebula.widgets.opal.duallist.mt.MT_DLItem;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

/**
 * Source: <a href=
 * "https://nominatim.org/release-docs/develop/api/Reverse/">https://nominatim.org/release-docs/develop/api/Reverse/</a>
 * <p>
 *
 * The main format of the reverse API is
 * <a href=
 * "https://nominatim.openstreetmap.org/reverse?lat=<value>&lon=<value>&<params>">https://nominatim.openstreetmap.org/reverse?lat=<value>&lon=<value>&<params></a>
 * <p>
 *
 * Limits and download policy:
 * <a href=
 * "https://operations.osmfoundation.org/policies/nominatim/">https://operations.osmfoundation.org/policies/nominatim/</a>
 * <p>
 *
 * Requested feature: <a href=
 * "https://github.com/mytourbook/mytourbook/issues/878">https://github.com/mytourbook/mytourbook/issues/878</a>
 * <p>
 */
public class TourLocationManager {

   private static final char   NL                              = UI.NEW_LINE;

   private static final String SYS_PROP__LOG_ADDRESS_RETRIEVAL = "logAddressRetrieval";                                      //$NON-NLS-1$
   private static boolean      _isLogging_AddressRetrieval     = System.getProperty(SYS_PROP__LOG_ADDRESS_RETRIEVAL) != null;

   static {

      if (_isLogging_AddressRetrieval) {
         Util.logSystemProperty_IsEnabled(TourManager.class, SYS_PROP__LOG_ADDRESS_RETRIEVAL, "OSM address retrieval is logged"); //$NON-NLS-1$
      }
   }

   private static final Bundle                    _bundle                    = TourbookPlugin.getDefault().getBundle();
   private static final IPath                     _stateLocation             = Platform.getStateLocation(_bundle);

   private static final String                    TOUR_LOCATION_FILE_NAME    = "tour-location.xml";                                   //$NON-NLS-1$
   private static final int                       TOUR_LOCATION_VERSION      = 1;

   private static final String                    TAG_ROOT                   = "TourLocationProfiles";                                //$NON-NLS-1$
   private static final String                    TAG_PROFILE                = "Profile";                                             //$NON-NLS-1$
   private static final String                    TAG_PARTS                  = "Parts";                                               //$NON-NLS-1$
   private static final String                    TAG_PART                   = "Part";                                                //$NON-NLS-1$

   private static final String                    ATTR_ACTIVE_PROFILE_ID     = "activeProfileId";                                     //$NON-NLS-1$
   private static final String                    ATTR_NAME                  = "name";                                                //$NON-NLS-1$
   private static final String                    ATTR_PROFILE_ID            = "profileID";                                           //$NON-NLS-1$
   private static final String                    ATTR_PROFILE_NAME          = "profileName";                                         //$NON-NLS-1$
   private static final String                    ATTR_TOUR_LOCATION_VERSION = "tourLocationVersion";                                 //$NON-NLS-1$

   static final String                            KEY_LOCATION_PART_ID       = "KEY_LOCATION_PART_ID";                                //$NON-NLS-1$
   static final String                            KEY_IS_NOT_AVAILABLE       = "KEY_IS_NOT_AVAILABLE";                                //$NON-NLS-1$

   private static final String                    SUB_TASK_MESSAGE           = "%d / %d - waited %d ms";                              //$NON-NLS-1$
   private static final String                    SUB_TASK_MESSAGE_SKIPPED   = "%d / %d";                                             //$NON-NLS-1$

   private static final String                    _userAgent                 = "MyTourbook/" + ApplicationVersion.getVersionSimple(); //$NON-NLS-1$

   private static final HttpClient                _httpClient                = HttpClient

         .newBuilder()
         .connectTimeout(Duration.ofSeconds(10))
         .build();

   private static final StringBuilder             _displayNameBuffer         = new StringBuilder();
   private static final Set<String>               _usedDisplayNames          = new HashSet<>();

   /**
    * Contains all available profiles
    */
   private static final List<TourLocationProfile> _allLocationProfiles       = new ArrayList<>();
   private static TourLocationProfile             _defaultProfile;

   /**
    * Zoom address detail
    *
    * 3 country
    * 5 state
    * 8 county
    * 10 city
    * 12 town / borough
    * 13 village / suburb
    * 14 neighbourhood
    * 15 any settlement
    * 16 major streets
    * 17 major and minor streets
    * 18 building
    */
   private static final int                       _zoomLevel                 = 18;

   private static long                            _lastRetrievalTimeMS;

// SET_FORMATTING_OFF

   static Map<LocationPartID, String>             allLocationPartLabel = Map.ofEntries(

         Map.entry(LocationPartID.OSM_DEFAULT_NAME,                  Messages.Tour_Location_Part_OsmDefaultName),
         Map.entry(LocationPartID.OSM_NAME,                          Messages.Tour_Location_Part_OsmName),

         Map.entry(LocationPartID.CUSTOM_CITY_LARGEST,               Messages.Tour_Location_Part_City_Largest),
         Map.entry(LocationPartID.CUSTOM_CITY_SMALLEST,              Messages.Tour_Location_Part_City_Smallest),
         Map.entry(LocationPartID.CUSTOM_CITY_WITH_ZIP_LARGEST,      Messages.Tour_Location_Part_CityWithZip_Largest),
         Map.entry(LocationPartID.CUSTOM_CITY_WITH_ZIP_SMALLEST,     Messages.Tour_Location_Part_CityWithZip_Smalles),

         Map.entry(LocationPartID.CUSTOM_STREET_WITH_HOUSE_NUMBER,   Messages.Tour_Location_Part_StreeWithHouseNumber),

         Map.entry(LocationPartID.continent,                         Messages.Tour_Location_Part_Continent),
         Map.entry(LocationPartID.country,                           Messages.Tour_Location_Part_Country),
         Map.entry(LocationPartID.country_code,                      Messages.Tour_Location_Part_CountryCode),

         Map.entry(LocationPartID.region,                            Messages.Tour_Location_Part_Region),
         Map.entry(LocationPartID.state,                             Messages.Tour_Location_Part_State),
         Map.entry(LocationPartID.state_district,                    Messages.Tour_Location_Part_StateDistrict),
         Map.entry(LocationPartID.county,                            Messages.Tour_Location_Part_County),

         Map.entry(LocationPartID.municipality,                      Messages.Tour_Location_Part_Municipality),
         Map.entry(LocationPartID.city,                              Messages.Tour_Location_Part_City),
         Map.entry(LocationPartID.town,                              Messages.Tour_Location_Part_Town),
         Map.entry(LocationPartID.village,                           Messages.Tour_Location_Part_Village),

         Map.entry(LocationPartID.city_district,                     Messages.Tour_Location_Part_CityDistrict),
         Map.entry(LocationPartID.district,                          Messages.Tour_Location_Part_District),
         Map.entry(LocationPartID.borough,                           Messages.Tour_Location_Part_Borough),
         Map.entry(LocationPartID.suburb,                            Messages.Tour_Location_Part_Suburb),
         Map.entry(LocationPartID.subdivision,                       Messages.Tour_Location_Part_Subdivision),

         Map.entry(LocationPartID.hamlet,                            Messages.Tour_Location_Part_Hamlet),
         Map.entry(LocationPartID.croft,                             Messages.Tour_Location_Part_Croft),
         Map.entry(LocationPartID.isolated_dwelling,                 Messages.Tour_Location_Part_IsolatedDwelling),

         Map.entry(LocationPartID.neighbourhood,                     Messages.Tour_Location_Part_Neighbourhood),
         Map.entry(LocationPartID.allotments,                        Messages.Tour_Location_Part_Allotments),
         Map.entry(LocationPartID.quarter,                           Messages.Tour_Location_Part_Quarter),

         Map.entry(LocationPartID.city_block,                        Messages.Tour_Location_Part_CityBlock),
         Map.entry(LocationPartID.residential,                       Messages.Tour_Location_Part_Residential),
         Map.entry(LocationPartID.farm,                              Messages.Tour_Location_Part_Farm),
         Map.entry(LocationPartID.farmyard,                          Messages.Tour_Location_Part_Farmyard),
         Map.entry(LocationPartID.industrial,                        Messages.Tour_Location_Part_Industrial),
         Map.entry(LocationPartID.commercial,                        Messages.Tour_Location_Part_Commercial),
         Map.entry(LocationPartID.retail,                            Messages.Tour_Location_Part_Retail),

         Map.entry(LocationPartID.road,                              Messages.Tour_Location_Part_Road),

         Map.entry(LocationPartID.house_name,                        Messages.Tour_Location_Part_HouseName),
         Map.entry(LocationPartID.house_number,                      Messages.Tour_Location_Part_HouseNumber),

         Map.entry(LocationPartID.aerialway,                         Messages.Tour_Location_Part_Aerialway),
         Map.entry(LocationPartID.aeroway,                           Messages.Tour_Location_Part_Aeroway),
         Map.entry(LocationPartID.amenity,                           Messages.Tour_Location_Part_Amenity),
         Map.entry(LocationPartID.boundary,                          Messages.Tour_Location_Part_Boundary),
         Map.entry(LocationPartID.bridge,                            Messages.Tour_Location_Part_Bridge),
         Map.entry(LocationPartID.club,                              Messages.Tour_Location_Part_Club),
         Map.entry(LocationPartID.craft,                             Messages.Tour_Location_Part_Craft),
         Map.entry(LocationPartID.emergency,                         Messages.Tour_Location_Part_Emergency),
         Map.entry(LocationPartID.historic,                          Messages.Tour_Location_Part_Historic),
         Map.entry(LocationPartID.landuse,                           Messages.Tour_Location_Part_Landuse),
         Map.entry(LocationPartID.leisure,                           Messages.Tour_Location_Part_Leisure),
         Map.entry(LocationPartID.man_made,                          Messages.Tour_Location_Part_ManMade),
         Map.entry(LocationPartID.military,                          Messages.Tour_Location_Part_Military),
         Map.entry(LocationPartID.mountain_pass,                     Messages.Tour_Location_Part_MountainPass),
         Map.entry(LocationPartID.natural2,                          Messages.Tour_Location_Part_Natural),
         Map.entry(LocationPartID.office,                            Messages.Tour_Location_Part_Office),
         Map.entry(LocationPartID.place,                             Messages.Tour_Location_Part_Place),
         Map.entry(LocationPartID.railway,                           Messages.Tour_Location_Part_Railway),
         Map.entry(LocationPartID.shop,                              Messages.Tour_Location_Part_Shop),
         Map.entry(LocationPartID.tourism,                           Messages.Tour_Location_Part_Tourism),
         Map.entry(LocationPartID.tunnel,                            Messages.Tour_Location_Part_Tunnel),
         Map.entry(LocationPartID.waterway,                          Messages.Tour_Location_Part_Waterway),

         Map.entry(LocationPartID.postcode,                          Messages.Tour_Location_Part_Postcode)
      );

// SET_FORMATTING_ON

   /**
    * Append text to the display name in {@link #_displayNameBuffer}
    *
    * @param text
    */
   private static void appendPart(final String text) {

      if (StringUtils.isNullOrEmpty(text)) {
         return;
      }

      // prevent to show duplicated fields, this can happen when the "name" field contains also e.g. the road name
      if (_usedDisplayNames.contains(text)) {
         return;
      }

      if (_displayNameBuffer.length() > 0) {
         _displayNameBuffer.append(UI.SYMBOL_COMMA + UI.SPACE);
      }

      _displayNameBuffer.append(text);

      _usedDisplayNames.add(text);
   }

   public static String createJoinedPartNames(final TourLocationProfile profile, final String delimiter) {

      final String joinedParts = profile.allParts.stream()

            .map(locationPart -> {

               String label;

               switch (locationPart) {
               case OSM_DEFAULT_NAME:
               case OSM_NAME:
               case CUSTOM_CITY_LARGEST:
               case CUSTOM_CITY_SMALLEST:
               case CUSTOM_CITY_WITH_ZIP_LARGEST:
               case CUSTOM_CITY_WITH_ZIP_SMALLEST:
               case CUSTOM_STREET_WITH_HOUSE_NUMBER:

                  label = TourLocationManager.createPartName_Combined(locationPart);
                  break;

               default:

                  label = TourLocationManager.allLocationPartLabel.get(locationPart);
                  break;
               }

               return label;
            })

            .collect(Collectors.joining(delimiter));

      return joinedParts;
   }

   public static String createLocationDisplayName(final List<MT_DLItem> allSelectedItems) {

      // reset buffers
      _displayNameBuffer.setLength(0);
      _usedDisplayNames.clear();

      for (final MT_DLItem partItem : allSelectedItems) {

         final Boolean isNotAvailable = (Boolean) partItem.getData(KEY_IS_NOT_AVAILABLE);

         if (isNotAvailable != null && isNotAvailable) {

            /*
             * Skip parts which are not available in the downloaded address data, this happens
             * when a profile was created with this part
             */

            continue;
         }

         appendPart(partItem.getText());
      }

      return _displayNameBuffer.toString();
   }

   /**
    * Creates the location name from different name parts.
    *
    * @return Returns an empty string when a display name not available
    */
   public static String createLocationDisplayName(final TourLocation tourLocation) {

      if (tourLocation == null) {

         return UI.EMPTY_STRING;
      }

      if (_defaultProfile != null) {

         // create name from a profile

         return createLocationDisplayName(tourLocation, _defaultProfile);

      } else {

         // use osm default name

         return tourLocation.display_name;
      }
   }

   /**
    * Creates location display name by applying the provided profile
    *
    * @param tourLocation
    *           OSM location data
    * @param profile
    *
    * @return
    */
   public static String createLocationDisplayName(final TourLocation tourLocation,
                                                  final TourLocationProfile profile) {

      // reset buffers
      _displayNameBuffer.setLength(0);
      _usedDisplayNames.clear();

      for (final LocationPartID locationPart : profile.allParts) {

         switch (locationPart) {

// SET_FORMATTING_OFF

         // ignore
         case NONE:                             break;
         case ISO3166_2_lvl4:                   break;

         case OSM_DEFAULT_NAME:                 appendPart(tourLocation.display_name);                         break;
         case OSM_NAME:                         appendPart(tourLocation.name);                                 break;

         case CUSTOM_CITY_LARGEST:              appendPart(getCombined_City_Largest(tourLocation));            break;
         case CUSTOM_CITY_SMALLEST:             appendPart(getCombined_City_Smallest(tourLocation));           break;

         case CUSTOM_CITY_WITH_ZIP_LARGEST:     appendPart(getCombined_CityWithZip_Largest(tourLocation));     break;
         case CUSTOM_CITY_WITH_ZIP_SMALLEST:    appendPart(getCombined_CityWithZip_Smallest(tourLocation));    break;

         case CUSTOM_STREET_WITH_HOUSE_NUMBER:  appendPart(getCombined_StreetWithHouseNumber(tourLocation));   break;

// SET_FORMATTING_ON

         default:

            /*
             * Append all other fieds
             */
            try {

               final String fieldName = locationPart.name();
               final Field addressField = tourLocation.getClass().getField(fieldName);

               final Object fieldValue = addressField.get(tourLocation);

               if (fieldValue instanceof final String textValue) {

                  appendPart(textValue);
               }

            } catch (NoSuchFieldException
                  | SecurityException
                  | IllegalArgumentException
                  | IllegalAccessException e) {

               StatusUtil.log(e);
            }

            break;
         }
      }

      return _displayNameBuffer.toString();
   }

   static String createPartName_Combined(final LocationPartID locationPart) {

      final String label = allLocationPartLabel.get(locationPart);

      return UI.SYMBOL_STAR + UI.SPACE + label;
   }

   static String createPartName_NotAvailable(final LocationPartID locationPart) {

      final String label = allLocationPartLabel.get(locationPart);

      return UI.SYMBOL_STAR + UI.SYMBOL_STAR + UI.SPACE + label;
   }

   /**
    * Create a {@link TourLocation}
    *
    * @param osmLocation
    * @param latitude
    * @param longitude
    *
    * @return
    */
   private static TourLocation createTourLocation(final OSMLocation osmLocation,
                                                  final double latitude,
                                                  final double longitude) {

      if (osmLocation == null) {
         return null;
      }

      final OSMAddress osmAddress = osmLocation.address;

      // "boundingbox":
      // [
      //    "47.1159171",
      //    "47.1163167",
      //    "7.9895150",
      //    "7.9897759"
      // ]
      final double[] boundingbox = osmLocation.boundingbox;
      if (boundingbox == null || boundingbox.length != 4) {
         return null;
      }

      final int[] boundingBoxE6 = Util.convertDoubleSeries_ToE6(boundingbox);

      // convert possible negative values into positive values, it's easier to math it
      final int latitudeMinE6_Normalized = boundingBoxE6[0] + 90_000_000;
      final int latitudeMaxE6_Normalized = boundingBoxE6[1] + 90_000_000;
      final int longitudeMinE6_Normalized = boundingBoxE6[2] + 180_000_000;
      final int longitudeMaxE6_Normalized = boundingBoxE6[3] + 180_000_000;

      final long boundingBoxKey = latitudeMinE6_Normalized
            + latitudeMaxE6_Normalized
            + longitudeMinE6_Normalized
            + longitudeMaxE6_Normalized;

      final TourLocation osmTourLocation = new TourLocation(latitude, longitude);

// SET_FORMATTING_OFF

      osmTourLocation.boundingBoxKey            = boundingBoxKey;
      osmTourLocation.latitudeMinE6_Normalized  = osmTourLocation.latitudeMinExpandedE6_Normalized  = latitudeMinE6_Normalized;
      osmTourLocation.latitudeMaxE6_Normalized  = osmTourLocation.latitudeMaxExpandedE6_Normalized  = latitudeMaxE6_Normalized;
      osmTourLocation.longitudeMinE6_Normalized = osmTourLocation.longitudeMinExpandedE6_Normalized = longitudeMinE6_Normalized;
      osmTourLocation.longitudeMaxE6_Normalized = osmTourLocation.longitudeMaxExpandedE6_Normalized = longitudeMaxE6_Normalized;

//      final TourLocation bboxTourLocation = TourDatabase.getTourLocation(osmTourLocation);
//      if (bboxTourLocation != null) {
//
//
//
//
//         return bboxTourLocation;
//      }

      osmTourLocation.name                      = validString(osmLocation.name);
      osmTourLocation.display_name              = validString(osmLocation.display_name);


      if (osmAddress != null) {

         osmTourLocation.continent              = validString(osmAddress.continent);
         osmTourLocation.country                = validString(osmAddress.country);
         osmTourLocation.country_code           = validString(osmAddress.country_code);

         osmTourLocation.region                 = validString(osmAddress.region);
         osmTourLocation.state                  = validString(osmAddress.state);
         osmTourLocation.state_district         = validString(osmAddress.state_district);
         osmTourLocation.county                 = validString(osmAddress.county);

         osmTourLocation.municipality           = validString(osmAddress.municipality);
         osmTourLocation.city                   = validString(osmAddress.city);
         osmTourLocation.town                   = validString(osmAddress.town);
         osmTourLocation.village                = validString(osmAddress.village);
         osmTourLocation.postcode               = validString(osmAddress.postcode);

         osmTourLocation.road                   = validString(osmAddress.road);
         osmTourLocation.house_number           = validString(osmAddress.house_number);
         osmTourLocation.house_name             = validString(osmAddress.house_name);

         // Area I
         osmTourLocation.city_district          = validString(osmAddress.city_district);
         osmTourLocation.district               = validString(osmAddress.district);
         osmTourLocation.borough                = validString(osmAddress.borough);
         osmTourLocation.suburb                 = validString(osmAddress.suburb);
         osmTourLocation.subdivision            = validString(osmAddress.subdivision);

         // Area II
         osmTourLocation.hamlet                 = validString(osmAddress.hamlet);
         osmTourLocation.croft                  = validString(osmAddress.croft);
         osmTourLocation.isolated_dwelling      = validString(osmAddress.isolated_dwelling);

         // Area III
         osmTourLocation.neighbourhood          = validString(osmAddress.neighbourhood);
         osmTourLocation.allotments             = validString(osmAddress.allotments);
         osmTourLocation.quarter                = validString(osmAddress.quarter);

         // Area IV
         osmTourLocation.city_block             = validString(osmAddress.city_block);
         osmTourLocation.residential            = validString(osmAddress.residential);
         osmTourLocation.farm                   = validString(osmAddress.farm);
         osmTourLocation.farmyard               = validString(osmAddress.farmyard);
         osmTourLocation.industrial             = validString(osmAddress.industrial);
         osmTourLocation.commercial             = validString(osmAddress.commercial);
         osmTourLocation.retail                 = validString(osmAddress.retail);

         osmTourLocation.aerialway              = validString(osmAddress.aerialway);
         osmTourLocation.aeroway                = validString(osmAddress.aeroway);
         osmTourLocation.amenity                = validString(osmAddress.amenity);
         osmTourLocation.boundary               = validString(osmAddress.boundary);
         osmTourLocation.bridge                 = validString(osmAddress.bridge);
         osmTourLocation.club                   = validString(osmAddress.club);
         osmTourLocation.craft                  = validString(osmAddress.craft);
         osmTourLocation.emergency              = validString(osmAddress.emergency);
         osmTourLocation.historic               = validString(osmAddress.historic);
         osmTourLocation.landuse                = validString(osmAddress.landuse);
         osmTourLocation.leisure                = validString(osmAddress.leisure);
         osmTourLocation.man_made               = validString(osmAddress.man_made);
         osmTourLocation.military               = validString(osmAddress.military);
         osmTourLocation.mountain_pass          = validString(osmAddress.mountain_pass);
         osmTourLocation.natural2               = validString(osmAddress.natural2);
         osmTourLocation.office                 = validString(osmAddress.office);
         osmTourLocation.place                  = validString(osmAddress.place);
         osmTourLocation.railway                = validString(osmAddress.railway);
         osmTourLocation.shop                   = validString(osmAddress.shop);
         osmTourLocation.tourism                = validString(osmAddress.tourism);
         osmTourLocation.tunnel                 = validString(osmAddress.tunnel);
         osmTourLocation.waterway               = validString(osmAddress.waterway);

         osmTourLocation.convertStringValues();
      }

// SET_FORMATTING_ON

      return osmTourLocation;
   }

   public static boolean deleteTourLocations(final List<TourLocation> allLocations) {
      // TODO Auto-generated method stub

      // ensure that a tour is NOT modified in the tour editor
      if (TourManager.isTourEditorModified(false)) {
         return false;
      }

      final ArrayList<Long> allTourIds = getToursWithLocations(allLocations);

      System.out.println("numTours: " + allTourIds.size());
// TODO remove SYSTEM.OUT.PRINTLN

      String dialogMessage;
      String actionDeleteTags;

      if (allLocations.size() == 1) {

         // delete one location

         dialogMessage = Messages.Tour_Location_Dialog_DeleteLocation_Message.formatted(

               allLocations.get(0).display_name,
               allLocations.size());

         actionDeleteTags = Messages.Tour_Location_Action_DeleteLocation;

      } else {

         // remove multiple tags

         dialogMessage = Messages.Tour_Location_Dialog_DeleteLocations_Message.formatted(

               allLocations.size(),
               allTourIds.size());

         actionDeleteTags = Messages.Tour_Location_Action_DeleteLocations;
      }

      final Display display = Display.getDefault();

//      // confirm deletion, show tag name and number of tours which contain a tag
//      final MessageDialog dialog = new MessageDialog(
//            display.getActiveShell(),
//            Messages.Tag_Manager_Dialog_DeleteTag_Title,
//            null,
//            dialogMessage,
//            MessageDialog.QUESTION,
//            new String[] {
//                  actionDeleteTags,
//                  IDialogConstants.CANCEL_LABEL },
//            1);
//
      final boolean[] returnValue = { false };
//
//      if (dialog.open() == Window.OK) {
//
//         BusyIndicator.showWhile(display, () -> {
//
//            if (deleteTourTag_10(allLocations)) {
//
//               clearAllTagResourcesAndFireModifyEvent();
//
//               updateTourTagFilterProfiles(allLocations);
//
//               returnValue[0] = true;
//            }
//         });
//      }

      return returnValue[0];
   }

   /**
    * Places are sorted by number of inhabitants, only some or nothing are available
    *
    * place = city,
    * place = town,
    * place = village,
    * place = hamlet
    * place = isolated_dwelling
    *
    * https://wiki.openstreetmap.org/wiki/Key:place
    *
    * @param tourLocation
    *
    * @return
    */
   static String getCombined_City_Largest(final TourLocation tourLocation) {

// SET_FORMATTING_OFF

      final String adrCity                = tourLocation.city;
      final String adrTown                = tourLocation.town;
      final String adrVillage             = tourLocation.village;
      final String adrHamlet              = tourLocation.hamlet;
      final String adrIsolated_dwelling   = tourLocation.isolated_dwelling;

      String city = null;

      if (adrCity != null) {                    city = adrCity;               }
      else if (adrTown != null) {               city = adrTown;               }
      else if (adrVillage != null) {            city = adrVillage;            }
      else if (adrHamlet != null) {             city = adrHamlet;             }
      else if (adrIsolated_dwelling != null) {  city = adrIsolated_dwelling;  }

// SET_FORMATTING_ON

      return city;
   }

   static String getCombined_City_Smallest(final TourLocation tourLocation) {

// SET_FORMATTING_OFF

      final String adrCity                = tourLocation.city;
      final String adrTown                = tourLocation.town;
      final String adrVillage             = tourLocation.village;
      final String adrHamlet              = tourLocation.hamlet;
      final String adrIsolatedDwelling    = tourLocation.isolated_dwelling;

      String city = null;

      if (adrIsolatedDwelling != null) { city = adrIsolatedDwelling;  }
      else if (adrHamlet != null) {       city = adrHamlet;             }
      else if (adrVillage != null) {      city = adrVillage;            }
      else if (adrTown != null) {         city = adrTown;               }
      else if (adrCity != null) {         city = adrCity;               }

// SET_FORMATTING_ON

      return city;
   }

   static String getCombined_CityWithZip_Largest(final TourLocation tourLocation) {

      final String city = getCombined_City_Largest(tourLocation);
      final String adrPostCode = tourLocation.postcode;

      if (city == null || adrPostCode == null) {
         return null;
      }

      final String adrCountryCode = tourLocation.country_code;

      if ("us".equals(adrCountryCode)) {

         // city + zip code

         return city + UI.SPACE + adrPostCode;

      } else {

         // zip code + city

         return adrPostCode + UI.SPACE + city;
      }
   }

   static String getCombined_CityWithZip_Smallest(final TourLocation tourLocation) {

      final String city = getCombined_City_Smallest(tourLocation);
      final String adrPostCode = tourLocation.postcode;

      if (city == null || adrPostCode == null) {
         return null;
      }

      final String adrCountryCode = tourLocation.country_code;

      if ("us".equals(adrCountryCode)) {

         // city + zip code

         return city + UI.SPACE + adrPostCode;

      } else {

         // zip code + city

         return adrPostCode + UI.SPACE + city;
      }
   }

   static String getCombined_StreetWithHouseNumber(final TourLocation tourLocation) {

      final String adrRoad = tourLocation.road;
      final String adrHouseNumber = tourLocation.house_number;

      if (adrRoad == null && adrHouseNumber == null) {

         return null;

      } else if (adrRoad == null) {

         return adrHouseNumber;

      } else if (adrHouseNumber == null) {

         return adrRoad;
      }

      // road and house number are available

      final String countryCode = tourLocation.country_code;

      if ("us".equals(countryCode)) {

         return adrHouseNumber + UI.SPACE + adrRoad;

      } else {

         return adrRoad + UI.SPACE + adrHouseNumber;
      }
   }

   /**
    * @return Returns the default profile or <code>null</code> when a profile is not set.
    */
   public static TourLocationProfile getDefaultProfile() {

      return _defaultProfile;
   }

   /**
    * Retrieve location data when not yet available
    *
    * @param latitude
    * @param longitude
    *
    * @return
    */
   public static TourLocationData getLocationData(final double latitude,
                                                  final double longitude) {

      /*
       * Check if a tour location is already saved
       */
      final TourLocation dbTourLocation = TourDatabase.getTourLocation(latitude, longitude);
      if (dbTourLocation != null) {

         return new TourLocationData(dbTourLocation);
      }

      /*
       * Retrieve location
       */
      final TourLocationData tourLocationData = getName_10_RetrieveData(latitude, longitude, _zoomLevel);

      if (tourLocationData == null) {
         return null;
      }

      final OSMLocation osmLocation = getName_20_DeserializeData(tourLocationData.downloadedData);

      tourLocationData.tourLocation = createTourLocation(osmLocation, latitude, longitude);

      if (_isLogging_AddressRetrieval && osmLocation != null) {

         System.out.println("Default name      " + osmLocation.display_name);

         if (osmLocation.name != null && osmLocation.name.length() > 0) {
            System.out.println("name              " + osmLocation.name);
         }

         System.out.println(" Waiting time     %d ms".formatted(tourLocationData.waitingTime));
         System.out.println(" Download time    %d ms".formatted(tourLocationData.downloadTime));

         if (osmLocation.address != null) {
            System.out.println(osmLocation.address.logAddress());
         }
      }

      return tourLocationData;
   }

   /**
    * Limits and download policy:
    * <a href=
    * "https://operations.osmfoundation.org/policies/nominatim/">https://operations.osmfoundation.org/policies/nominatim/</a>
    * <p>
    *
    * @param latitude
    * @param longitude
    * @param zoomLevel
    *
    * @return Returns <code>null</code> or {@link TourLocationData}
    */
   private static TourLocationData getName_10_RetrieveData(final double latitude,
                                                           final double longitude,
                                                           final int zoomLevel) {

      final long now = System.currentTimeMillis();
      long waitingTime = now - _lastRetrievalTimeMS;

      if (waitingTime < 1000) {

         /*
          * Max requests are limited to 1 per second, we have to wait
          * https://operations.osmfoundation.org/policies/nominatim/
          */

         waitingTime = 1000 - waitingTime;

         try {

            Thread.sleep(waitingTime);

         } catch (final InterruptedException e) {
            StatusUtil.showStatus(e);
            Thread.currentThread().interrupt();
         }

      } else {

         // waiting time >= 1000 ms -> adjust value for log message

         waitingTime = 0;
      }

      final long retrievalStartTime = System.currentTimeMillis();
      _lastRetrievalTimeMS = retrievalStartTime;

      final String requestUrl = UI.EMPTY_STRING

            + "https://nominatim.openstreetmap.org/reverse?" //$NON-NLS-1$

            + "format=json" //               //$NON-NLS-1$
            + "&addressdetails=1" //         //$NON-NLS-1$

            + "&lat=" + latitude //          //$NON-NLS-1$
            + "&lon=" + longitude //         //$NON-NLS-1$
            + "&zoom=" + zoomLevel //        //$NON-NLS-1$

//          + "&extratags=1" //$NON-NLS-1$
//          + "&namedetails=1" //$NON-NLS-1$
//          + "&layer=address,poi,railway,natural,manmade" //$NON-NLS-1$

//          + "&accept-language=1" //$NON-NLS-1$
      ;

      String downloadedData = UI.EMPTY_STRING;

      try {

         final HttpRequest request = HttpRequest
               .newBuilder(URI.create(requestUrl))
               .header(WEB.HTTP_HEADER_USER_AGENT, _userAgent)
               .GET()
               .build();

         final HttpResponse<String> response = _httpClient.send(request, BodyHandlers.ofString());

         downloadedData = response.body();

         if (response.statusCode() != HttpURLConnection.HTTP_OK) {

            logError(downloadedData);

            return null;
         }

      } catch (final HttpConnectTimeoutException ex) {

         StatusUtil.showStatus(ex);

         logException(ex);

         return null;

      } catch (final Exception ex) {

         logException(ex);

//       Thread.currentThread().interrupt();

         return null;
      }

      final long retrievalEndTime = System.currentTimeMillis();

      final long retrievalDuration = retrievalEndTime - retrievalStartTime;

      return new TourLocationData(downloadedData, retrievalDuration, waitingTime);
   }

   private static OSMLocation getName_20_DeserializeData(final String osmLocationString) {

      OSMLocation osmLocation = null;

      try {

         osmLocation = new ObjectMapper().readValue(osmLocationString, OSMLocation.class);

      } catch (final Exception e) {

         StatusUtil.logError(

               TourLocationManager.class.getSimpleName() + ".deserializeLocationData : " //$NON-NLS-1$
                     + "Error while deserializing the location JSON object : " //$NON-NLS-1$
                     + osmLocationString + NL + e.getMessage());
      }

      return osmLocation;
   }

   public static List<TourLocationProfile> getProfiles() {

      return _allLocationProfiles;
   }

   /**
    * @param allLocations
    *
    * @return Returns a list with all tour id's which are containing the tour location.
    */
   private static ArrayList<Long> getToursWithLocations(final List<TourLocation> allLocations) {

      final ArrayList<Long> allTourIds = new ArrayList<>();

      final ArrayList<Long> allSqlParameter = new ArrayList<>();
      final StringBuilder sqlParameterPlaceholder = new StringBuilder();

      boolean isFirst = true;

      for (final TourLocation tourLocation : allLocations) {

         if (isFirst) {
            isFirst = false;
            sqlParameterPlaceholder.append(TourDatabase.PARAMETER_FIRST);
         } else {
            sqlParameterPlaceholder.append(TourDatabase.PARAMETER_FOLLOWING);
         }

         allSqlParameter.add(tourLocation.getLocationId());
      }

      final String sqlParameter = sqlParameterPlaceholder.toString();

      final String sql = UI.EMPTY_STRING

            + "SELECT" + NL //                                             //$NON-NLS-1$

            + " DISTINCT TourId" + NL //                                   //$NON-NLS-1$

            + " FROM TourData" + NL //                                     //$NON-NLS-1$

            + " WHERE tourLocationStart_LocationID IN (" + sqlParameter + ")" + NL //  //$NON-NLS-1$ //$NON-NLS-2$
            + "    OR tourLocationEnd_LocationID IN   (" + sqlParameter + ")" + NL //  //$NON-NLS-1$ //$NON-NLS-2$

            + " ORDER BY tourId" //                                        //$NON-NLS-1$
      ;

      PreparedStatement statement = null;

      try (Connection conn = TourDatabase.getInstance().getConnection()) {

         statement = conn.prepareStatement(sql);

         // fillup parameters for 2 fields
         final int numParameters = allSqlParameter.size();
         int sqlIndex = 1;
         for (int parameterIndex = 0; parameterIndex < numParameters; parameterIndex++) {
            statement.setLong(sqlIndex++, allSqlParameter.get(parameterIndex));
         }
         for (int parameterIndex = 0; parameterIndex < numParameters; parameterIndex++) {
            statement.setLong(sqlIndex++, allSqlParameter.get(parameterIndex));
         }

         final ResultSet result = statement.executeQuery();
         while (result.next()) {
            allTourIds.add(result.getLong(1));
         }

      } catch (final SQLException e) {

         StatusUtil.logError(sql);
         net.tourbook.ui.UI.showSQLException(e);

      } finally {
         Util.closeSql(statement);
      }

      return allTourIds;
   }

   private static File getXmlFile() {

      final File layerFile = _stateLocation.append(TOUR_LOCATION_FILE_NAME).toFile();

      return layerFile;
   }

   private static void logError(final String exceptionMessage) {

      TourLogManager.log_ERROR(NLS.bind(
            "Error while retrieving tour location data: \"{1}\"", //$NON-NLS-1$
            exceptionMessage));
   }

   private static void logException(final Exception ex) {

      TourLogManager.log_EXCEPTION_WithStacktrace("Error while retrieving tour location data", ex); //$NON-NLS-1$
   }

   public static void restoreState() {

      xmlRead_Profiles();
   }

   public static void saveState() {

      final XMLMemento xmlRoot = xmlWrite_Profiles();
      final File xmlFile = getXmlFile();

      Util.writeXml(xmlRoot, xmlFile);
   }

   static void setDefaultProfile(final TourLocationProfile defaultProfile) {

      _defaultProfile = defaultProfile;
   }

   /**
    * Set tour locations for the requested tours, when not available, then download and save tour
    * locations
    *
    * @param requestedTours
    * @param locationProfile
    */
   public static void setTourLocations(final List<TourData> requestedTours,
                                       final TourLocationProfile locationProfile) {

      final ArrayList<TourData> savedTours = new ArrayList<>();

      try {

         final IRunnableWithProgress runnable = new IRunnableWithProgress() {
            @Override
            public void run(final IProgressMonitor monitor)
                  throws InvocationTargetException, InterruptedException {

               final int numTours = requestedTours.size();
               final int numRequests = numTours * 2;
               int numWorked = 0;

               monitor.beginTask("Retrieving %d tour locations".formatted(numRequests), numRequests);

               for (final TourData tourData : requestedTours) {

                  if (monitor.isCanceled()) {
                     break;
                  }

                  final double[] latitudeSerie = tourData.latitudeSerie;
                  final double[] longitudeSerie = tourData.longitudeSerie;

                  if (latitudeSerie == null || latitudeSerie.length == 0) {

                     // needed data are not available

                     numWorked++;
                     numWorked++;

                     monitor.worked(2);
                     monitor.subTask(SUB_TASK_MESSAGE_SKIPPED.formatted(numWorked, numRequests));

                     continue;
                  }

                  boolean isModified = false;
                  long waitingTime;

                  /*
                   * Start location
                   */
                  waitingTime = 0;

                  TourLocation tourLocationStart = tourData.getTourLocationStart();
                  if (tourLocationStart == null) {

                     final TourLocationData startLocationData = getLocationData(latitudeSerie[0], longitudeSerie[0]);

                     if (startLocationData != null) {

                        waitingTime = startLocationData.waitingTime;
                        tourLocationStart = startLocationData.tourLocation;
                     }
                  }

                  if (tourLocationStart != null) {

                     final String startLocationText = createLocationDisplayName(tourLocationStart, locationProfile);

                     tourData.setTourStartPlace(startLocationText);
                     tourData.setTourLocationStart(tourLocationStart);

                     isModified = true;
                  }

                  monitor.worked(1);
                  monitor.subTask(SUB_TASK_MESSAGE.formatted(++numWorked, numRequests, waitingTime));

                  /*
                   * End location
                   */
                  waitingTime = 0;

                  TourLocation tourLocationEnd = tourData.getTourLocationEnd();
                  if (tourLocationEnd == null) {

                     final int lastIndex = latitudeSerie.length - 1;
                     final TourLocationData endLocationData = getLocationData(latitudeSerie[lastIndex], longitudeSerie[lastIndex]);

                     if (endLocationData != null) {

                        waitingTime = endLocationData.waitingTime;
                        tourLocationEnd = endLocationData.tourLocation;
                     }
                  }

                  if (tourLocationEnd != null) {

                     final String endLocationText = createLocationDisplayName(tourLocationEnd, locationProfile);

                     tourData.setTourEndPlace(endLocationText);
                     tourData.setTourLocationEnd(tourLocationEnd);

                     isModified = true;
                  }

                  monitor.worked(1);
                  monitor.subTask(SUB_TASK_MESSAGE.formatted(++numWorked, numRequests, waitingTime));

                  if (isModified) {

                     TourManager.saveModifiedTour(tourData, false);

                     savedTours.add(tourData);
                  }
               }
            }
         };

         new ProgressMonitorDialog(TourbookPlugin.getAppShell()).run(true, true, runnable);

         if (savedTours.size() > 0) {

            final TourEvent tourEvent = new TourEvent(savedTours);

            // this must be fired in the UI thread
            TourManager.fireEvent(TourEventId.TOUR_CHANGED, tourEvent);
         }

      } catch (final InvocationTargetException | InterruptedException e) {

         StatusUtil.showStatus(e);
         Thread.currentThread().interrupt();
      }
   }

   private static String validString(final String stringValue) {

      if (stringValue == null) {

         return null;

      } else if (stringValue.length() <= TourLocation.DB_FIELD_LENGTH) {

         return stringValue;

      } else {

         return stringValue.substring(0, TourLocation.DB_FIELD_LENGTH);
      }
   }

   /**
    * Read tour location xml file.
    *
    * @return
    */
   private static void xmlRead_Profiles() {

      final File xmlFile = getXmlFile();

      if (xmlFile.exists() == false) {
         return;
      }

      Integer activeProfileId = null;

      try (InputStreamReader reader = new InputStreamReader(new FileInputStream(xmlFile), UI.UTF_8)) {

         // <TourLocationProfiles>
         final XMLMemento xmlRoot = XMLMemento.createReadRoot(reader);

         activeProfileId = Util.getXmlInteger(xmlRoot, ATTR_ACTIVE_PROFILE_ID, null);

         // loop: all location profiles
         for (final IMemento mementoChild : xmlRoot.getChildren()) {

            final XMLMemento xmlProfile = (XMLMemento) mementoChild;

            if (TAG_PROFILE.equals(xmlProfile.getType())) {

               // <Profile>

               final TourLocationProfile profile = new TourLocationProfile();

               // id
               final int profileId = Util.getXmlInteger(xmlProfile, ATTR_PROFILE_ID, -1);
               if (profileId != -1) {
                  profile.profileId = profileId;
               }

               profile.name = Util.getXmlString(xmlProfile, ATTR_PROFILE_NAME, UI.EMPTY_STRING);

               final IMemento xmlParts = xmlProfile.getChild(TAG_PARTS);

               if (xmlParts != null) {

                  // <Parts>

                  for (final IMemento xmlPart : xmlParts.getChildren()) {

                     final LocationPartID part = (LocationPartID) Util.getXmlEnum(xmlPart, ATTR_NAME, LocationPartID.NONE);

                     if (part.equals(LocationPartID.NONE) == false) {

                        profile.allParts.add(part);
                     }
                  }
               }

               _allLocationProfiles.add(profile);
            }
         }

      } catch (final Exception e) {
         StatusUtil.log(e);
      }

      /*
       * Select profile
       */
      if (activeProfileId != null) {

         // select last active profile

         for (final TourLocationProfile locationProfile : _allLocationProfiles) {
            if (locationProfile.profileId == activeProfileId) {

               _defaultProfile = locationProfile;
               break;
            }
         }
      }

      if (_defaultProfile == null && _allLocationProfiles.size() > 0) {

         // select first profile

         _defaultProfile = _allLocationProfiles.get(0);
      }
   }

   private static XMLMemento xmlWrite_Profiles() {

      XMLMemento xmlRoot = null;

      try {

         // <TourLocationProfiles>
         xmlRoot = xmlWrite_Profiles_10_Root();

         if (_defaultProfile != null) {
            xmlRoot.putInteger(ATTR_ACTIVE_PROFILE_ID, _defaultProfile.profileId);
         }

         // loop: all location profiles
         for (final TourLocationProfile locationProfile : _allLocationProfiles) {

            // <Profile>
            final IMemento xmlLocation = xmlRoot.createChild(TAG_PROFILE);

            xmlLocation.putInteger(ATTR_PROFILE_ID, locationProfile.profileId);
            xmlLocation.putString(ATTR_PROFILE_NAME, locationProfile.getName());

            // <Parts>
            final IMemento xmlParts = xmlLocation.createChild(TAG_PARTS);

            // loop: all location parts
            for (final LocationPartID locationPart : locationProfile.allParts) {

               // <Part>
               final IMemento xmlPart = xmlParts.createChild(TAG_PART);

               Util.setXmlEnum(xmlPart, ATTR_NAME, locationPart);
            }
         }

      } catch (final Exception e) {
         StatusUtil.log(e);
      }

      return xmlRoot;
   }

   private static XMLMemento xmlWrite_Profiles_10_Root() {

      final XMLMemento xmlRoot = XMLMemento.createWriteRoot(TAG_ROOT);

      // date/time
      xmlRoot.putString(Util.ATTR_ROOT_DATETIME, TimeTools.now().toString());

      // plugin version
      final Version version = _bundle.getVersion();
      xmlRoot.putInteger(Util.ATTR_ROOT_VERSION_MAJOR, version.getMajor());
      xmlRoot.putInteger(Util.ATTR_ROOT_VERSION_MINOR, version.getMinor());
      xmlRoot.putInteger(Util.ATTR_ROOT_VERSION_MICRO, version.getMicro());
      xmlRoot.putString(Util.ATTR_ROOT_VERSION_QUALIFIER, version.getQualifier());

      // layer structure version
      xmlRoot.putInteger(ATTR_TOUR_LOCATION_VERSION, TOUR_LOCATION_VERSION);

      return xmlRoot;
   }

}

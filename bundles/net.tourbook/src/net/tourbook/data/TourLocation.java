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
package net.tourbook.data;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import net.tourbook.common.UI;
import net.tourbook.common.util.Util;
import net.tourbook.database.TourDatabase;

/**
 * Possible address fields are from <a href=
 * "https://nominatim.org/release-docs/develop/api/Output/#addressdetails">https://nominatim.org/release-docs/develop/api/Output/#addressdetails</a>
 *
 * <pre>
 *
 *   {
 *      "place_id"      : 78981669,
 *      "osm_id"        : 44952014,

 *      "licence"       : "Data © OpenStreetMap contributors, ODbL 1.0. http://osm.org/copyright",
 *
 *      "place_rank"    : 30,
 *      "importance"    : 0.00000999999999995449,
 *
 *      "addresstype"   : "leisure",
 *      "class"         : "leisure",
 *      "osm_type"      : "way",
 *      "type"          : "pitch",

 *      "lat"           : "47.116116899999994",
 *      "lon"           : "7.989645450000001",
 *
 *      "name"          : "",
 *      "display_name"  : "5a, Schlossfeldstrasse, St. Niklausenberg, Guon, Willisau, Luzern, 6130, Schweiz/Suisse/Svizzera/Svizra",
 *
 *      "address": {
 *         "house_number"     : "5a",
 *         "road"             : "Schlossfeldstrasse",
 *         "neighbourhood"    : "St. Niklausenberg",
 *         "farm"             : "Guon",
 *         "village"          : "Willisau",
 *         "state"            : "Luzern",
 *         "ISO3166-2-lvl4"   : "CH-LU",
 *         "postcode"         : "6130",
 *         "country"          : "Schweiz/Suisse/Svizzera/Svizra",
 *         "country_code"     : "ch"
 *      },
 *
 *      "boundingbox":
 *      [
 *         "47.1159171",
 *         "47.1163167",
 *         "7.9895150",
 *         "7.9897759"
 *      ]
 *   }
 *
 * </pre>
 */
@Entity
public class TourLocation implements Serializable {

   private static final long serialVersionUID = 1L;

   private static final char NL               = UI.NEW_LINE;

   public static final int   DB_FIELD_LENGTH  = 1000;

   /**
    * Contains the entity id
    */
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private long              locationID       = TourDatabase.ENTITY_IS_NOT_SAVED;

   /*
    * Fields from {@link OSMLocation}, the field names are kept from the downloaded location data
    * when possible
    */
   public String name;
   public String display_name;

   /*
    * Location bounding box values
    */

   /**
    * Contains the normalized latitude value of the requested location
    * <p>
    * <code>normalized = latitude + 90</code>
    */
   public int latitudeE6_Normalized;

   /**
    * Contains the normalized longitude value of the requested location
    * <p>
    * normalized = longitude + 180
    */
   public int longitudeE6_Normalized;

   /**
    * Contains the normalized latitude min value
    * <p>
    * <code>normalized = latitude + 90</code>
    */
   public int latitudeMinE6_Normalized;

   /**
    * Contains the normalized latitude max value
    * <p>
    * normalized = latitude + 90
    */
   public int latitudeMaxE6_Normalized;

   /**
    * Contains the normalized longitude min value
    * <p>
    * normalized = longitude + 180
    */
   public int longitudeMinE6_Normalized;

   /**
    * Contains the normalized longitude max value
    * <p>
    * normalized = longitude + 180
    */
   public int longitudeMaxE6_Normalized;

   /*
    * Fields from {@link OSMAddress}
    */
   public String continent;

   public String country;
   public String country_code;
   public String region;

   public String state;
   public String state_district;
   public String county;
   public String municipality;

   public String city;
   public String town;
   public String village;
   public String city_district;

   public String district;
   public String borough;
   public String suburb;
   public String subdivision;
   public String hamlet;

   public String croft;
   public String isolated_dwelling;
   public String neighbourhood;

   public String allotments;
   public String quarter;
   public String city_block;

   public String residential;
   public String farm;
   public String farmyard;
   public String industrial;
   public String commercial;
   public String retail;
   public String road;

   public String house_number;

   public String house_name;
   public String aerialway;

   public String aeroway;
   public String amenity;
   public String boundary;
   public String bridge;
   public String club;
   public String craft;
   public String emergency;
   public String historic;
   public String landuse;
   public String leisure;
   public String man_made;
   public String military;
   public String mountain_pass;

   /**
    * "natural" seems to be a SQL name :-?
    * <p>
    * ERROR 42X01: Syntax error: Encountered "natural" at line 55, column 4.
    */
   public String natural2;

   public String office;
   public String place;
   public String railway;
   public String shop;
   public String tourism;
   public String tunnel;
   public String waterway;
   public String postcode;

   @Transient
   public double latitude;
   @Transient
   public double longitude;

   @Transient
   public int    latitudeE6;
   @Transient
   public int    longitudeE6;

   @Transient
   public double latitudeMin;
   @Transient
   public double latitudeMax;
   @Transient
   public double longitudeMin;
   @Transient
   public double longitudeMax;

   /**
    * Default constructor used also in ejb
    */
   public TourLocation() {}

   public TourLocation(final double latitude, final double longitude) {

      this.latitude = latitude;
      this.longitude = longitude;

      latitudeE6 = Util.convertDouble_ToE6(latitude);
      longitudeE6 = Util.convertDouble_ToE6(longitude);

      latitudeE6_Normalized = latitudeE6 + 90_000_000;
      longitudeE6_Normalized = longitudeE6 + 180_000_000;
   }

   @Override
   public boolean equals(final Object obj) {

      if (this == obj) {
         return true;
      }

      if (obj == null) {
         return false;
      }

      if (getClass() != obj.getClass()) {
         return false;
      }

      final TourLocation other = (TourLocation) obj;

      return locationID == other.locationID;
   }

   public long getLocationId() {
      return locationID;
   }

   @Override
   public int hashCode() {

      return Objects.hash(locationID);
   }

   private String log(final String field, final int value) {

      return field + value + NL;
   }

   private String log(final String field, final String value) {

      if (value == null || value.length() == 0) {
         return UI.EMPTY_STRING;
      }

      return field + value + NL;
   }

   @Override
   public String toString() {

      return UI.EMPTY_STRING

            + "TourLocation" + NL //                                       //$NON-NLS-1$

            + " locationID          = " + locationID + NL //               //$NON-NLS-1$

            + log(" name                = ", name) //                      //$NON-NLS-1$
            + log(" display_name        = ", display_name) //              //$NON-NLS-1$

            + log(" continent           = ", continent) //                 //$NON-NLS-1$
            + log(" country             = ", country) //                   //$NON-NLS-1$
            + log(" country_code        = ", country_code) //              //$NON-NLS-1$
            + log(" region              = ", region) //                    //$NON-NLS-1$
            + log(" state               = ", state) //                     //$NON-NLS-1$
            + log(" state_district      = ", state_district) //            //$NON-NLS-1$
            + log(" county              = ", county) //                    //$NON-NLS-1$
            + log(" municipality        = ", municipality) //              //$NON-NLS-1$
            + log(" city                = ", city) //                      //$NON-NLS-1$
            + log(" town                = ", town) //                      //$NON-NLS-1$
            + log(" village             = ", village) //                   //$NON-NLS-1$
            + log(" city_district       = ", city_district) //             //$NON-NLS-1$
            + log(" district            = ", district) //                  //$NON-NLS-1$
            + log(" borough             = ", borough) //                   //$NON-NLS-1$
            + log(" suburb              = ", suburb) //                    //$NON-NLS-1$
            + log(" subdivision         = ", subdivision) //               //$NON-NLS-1$
            + log(" hamlet              = ", hamlet) //                    //$NON-NLS-1$
            + log(" croft               = ", croft) //                     //$NON-NLS-1$
            + log(" isolated_dwelling   = ", isolated_dwelling) //         //$NON-NLS-1$
            + log(" neighbourhood       = ", neighbourhood) //             //$NON-NLS-1$
            + log(" allotments          = ", allotments) //                //$NON-NLS-1$
            + log(" quarter             = ", quarter) //                   //$NON-NLS-1$
            + log(" city_block          = ", city_block) //                //$NON-NLS-1$
            + log(" residential         = ", residential) //               //$NON-NLS-1$
            + log(" farm                = ", farm) //                      //$NON-NLS-1$
            + log(" farmyard            = ", farmyard) //                  //$NON-NLS-1$
            + log(" industrial          = ", industrial) //                //$NON-NLS-1$
            + log(" commercial          = ", commercial) //                //$NON-NLS-1$
            + log(" retail              = ", retail) //                    //$NON-NLS-1$
            + log(" road                = ", road) //                      //$NON-NLS-1$
            + log(" house_number        = ", house_number) //              //$NON-NLS-1$
            + log(" house_name          = ", house_name) //                //$NON-NLS-1$
            + log(" aerialway           = ", aerialway) //                 //$NON-NLS-1$
            + log(" aeroway             = ", aeroway) //                   //$NON-NLS-1$
            + log(" amenity             = ", amenity) //                   //$NON-NLS-1$
            + log(" boundary            = ", boundary) //                  //$NON-NLS-1$
            + log(" bridge              = ", bridge) //                    //$NON-NLS-1$
            + log(" club                = ", club) //                      //$NON-NLS-1$
            + log(" craft               = ", craft) //                     //$NON-NLS-1$
            + log(" emergency           = ", emergency) //                 //$NON-NLS-1$
            + log(" historic            = ", historic) //                  //$NON-NLS-1$
            + log(" landuse             = ", landuse) //                   //$NON-NLS-1$
            + log(" leisure             = ", leisure) //                   //$NON-NLS-1$
            + log(" man_made            = ", man_made) //                  //$NON-NLS-1$
            + log(" military            = ", military) //                  //$NON-NLS-1$
            + log(" mountain_pass       = ", mountain_pass) //             //$NON-NLS-1$
            + log(" natural2            = ", natural2) //                  //$NON-NLS-1$
            + log(" office              = ", office) //                    //$NON-NLS-1$
            + log(" place               = ", place) //                     //$NON-NLS-1$
            + log(" railway             = ", railway) //                   //$NON-NLS-1$
            + log(" shop                = ", shop) //                      //$NON-NLS-1$
            + log(" tourism             = ", tourism) //                   //$NON-NLS-1$
            + log(" tunnel              = ", tunnel) //                    //$NON-NLS-1$
            + log(" waterway            = ", waterway) //                  //$NON-NLS-1$
            + log(" postcode            = ", postcode) //                  //$NON-NLS-1$

            + NL

            + log(" latitudeE6                = ", latitudeE6) //                   //$NON-NLS-1$
            + log(" longitudeE6               = ", longitudeE6) //                  //$NON-NLS-1$
            + log(" latitudeMinE6_Normalized  = ", latitudeMinE6_Normalized) //     //$NON-NLS-1$
            + log(" latitudeMaxE6_Normalized  = ", latitudeMaxE6_Normalized) //     //$NON-NLS-1$
            + log(" longitudeMinE6_Normalized = ", longitudeMinE6_Normalized) //    //$NON-NLS-1$
            + log(" longitudeMaxE6_Normalized = ", longitudeMaxE6_Normalized) //    //$NON-NLS-1$

      ;
   }

}
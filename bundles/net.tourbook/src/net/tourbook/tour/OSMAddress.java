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
package net.tourbook.tour;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.lang.reflect.Field;

import net.tourbook.common.util.StatusUtil;

/**
 * Possible address fields are from <a href=
 * "https://nominatim.org/release-docs/develop/api/Output/#addressdetails">https://nominatim.org/release-docs/develop/api/Output/#addressdetails</a>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class OSMAddress {

   private static final String FIELD_LOG = "%-17s %s\n"; //$NON-NLS-1$

   public String               continent;
   public String               country;
   public String               country_code;

   public String               region;
   public String               state;
   public String               state_district;
   public String               county;

   @JsonAlias({ "ISO3166-2-lvl4" })
   public String               ISO3166_2_lvl4;

   public String               municipality;
   public String               city;
   public String               town;
   public String               village;

   public String               city_district;
   public String               district;
   public String               borough;
   public String               suburb;
   public String               subdivision;

   public String               hamlet;
   public String               croft;
   public String               isolated_dwelling;

   public String               neighbourhood;
   public String               allotments;
   public String               quarter;

   public String               city_block;
   public String               residential;
   public String               farm;
   public String               farmyard;
   public String               industrial;
   public String               commercial;
   public String               retail;

   public String               road;

   public String               house_number;
   public String               house_name;

   public String               aerialway;
   public String               aeroway;
   public String               amenity;
   public String               boundary;
   public String               bridge;
   public String               club;
   public String               craft;
   public String               emergency;
   public String               historic;
   public String               landuse;
   public String               leisure;
   public String               man_made;
   public String               military;
   public String               mountain_pass;
   public String               natural;
   public String               office;
   public String               place;
   public String               railway;
   public String               shop;
   public String               tourism;
   public String               tunnel;
   public String               waterway;

   public String               postcode;

   public String logAddress() {

      final StringBuilder sb = new StringBuilder();

      try {

         final Field[] allFields = this.getClass().getFields();

         for (final Field field : allFields) {

            // skip names
            if ("ISO3166_2_lvl4".equals(field.getName())) { //$NON-NLS-1$
               continue;
            }

            final Object fieldValue = field.get(this);

            if (fieldValue instanceof final String stringValue) {

               // log only fields with value
               if (stringValue.length() > 0) {

                  sb.append(FIELD_LOG.formatted(field.getName(), fieldValue));
               }
            }
         }

      } catch (IllegalArgumentException | IllegalAccessException e) {
         StatusUtil.log(e);
      }

      return sb.toString();
   }

}

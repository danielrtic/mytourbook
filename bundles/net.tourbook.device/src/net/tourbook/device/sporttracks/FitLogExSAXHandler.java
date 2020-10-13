/*******************************************************************************
 * Copyright (C) 2020 Frédéric Bard
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
package net.tourbook.device.sporttracks;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import net.tourbook.device.sporttracks.FitLogSAXHandler.Equipment;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class FitLogExSAXHandler extends DefaultHandler {

   private static final String TAG_ACTIVITY_CUSTOM_DATA_FIELD_DEFINITION  = "CustomDataFieldDefinition";                     //$NON-NLS-1$
   private static final String TAG_ACTIVITY_CUSTOM_DATA_FIELD_DEFINITIONS = TAG_ACTIVITY_CUSTOM_DATA_FIELD_DEFINITION + "s"; //$NON-NLS-1$

   private static final String TAG_ACTIVITY_EQUIPMENT                      = "Equipment";        //$NON-NLS-1$
   private static final String ATTRIB_CUSTOM_DATA_FIELD_DEFINITION_NAME    = "Name";             //$NON-NLS-1$
   private static final String ATTRIB_CUSTOM_DATA_FIELD_DEFINITION_OPTIONS = "Options";          //$NON-NLS-1$
   private static final String ATTRIB_CUSTOM_DATA_FIELD_DEFINITION_TRIMP   = "TRIMP";            //$NON-NLS-1$
   private static final String ATTRIB_EQUIPMENT_BRAND                      = "Brand";            //$NON-NLS-1$
   private static final String ATTRIB_EQUIPMENT_DATE_PURCHASED             = "DatePurchased";    //$NON-NLS-1$
   private static final String ATTRIB_EQUIPMENT_MODEL                      = "Model";            //$NON-NLS-1$
   private static final String ATTRIB_EQUIPMENT_NOTES                      = "Notes";            //$NON-NLS-1$
   private static final String ATTRIB_EQUIPMENT_PURCHASE_LOCATION          = "PurchaseLocation"; //$NON-NLS-1$
   private static final String ATTRIB_EQUIPMENT_TYPE                       = "Type";             //$NON-NLS-1$
   private static final String ATTRIB_EQUIPMENT_WEIGHT_KILOGRAMS           = "WeightKilograms";  //$NON-NLS-1$
   private static final String ATTRIB_EQUIPMENT_ID                         = "Id";               //$NON-NLS-1$

   private LinkedHashMap<String, Integer> _customDataFieldDefinitions;
   private ArrayList<Equipment>           _equipments;

   private boolean                        _isInCustomDataFieldDefinitions;
   private boolean                        _isInEquipment;

   private StringBuilder                  _characters = new StringBuilder();

   public FitLogExSAXHandler() {

      _customDataFieldDefinitions = new LinkedHashMap<>();
      _equipments = new ArrayList<>();
   }

   @Override
   public void characters(final char[] chars, final int startIndex, final int length) throws SAXException {

      if (_isInEquipment) {

         _characters.append(chars, startIndex, length);
      }
   }

   @Override
   public void endElement(final String uri, final String localName, final String name) throws SAXException {

      if (name.equals(TAG_ACTIVITY_CUSTOM_DATA_FIELD_DEFINITIONS)) {

         _isInCustomDataFieldDefinitions = false;

      } else if (!_isInEquipment) {
         return;
      }

      if (name.equals(TAG_ACTIVITY_EQUIPMENT)) {

         _isInEquipment = false;

      } else if (name.equals(ATTRIB_EQUIPMENT_BRAND) ||
            name.equals(ATTRIB_EQUIPMENT_MODEL) ||
            name.equals(ATTRIB_EQUIPMENT_DATE_PURCHASED) ||
            name.equals(ATTRIB_EQUIPMENT_NOTES) ||
            name.equals(ATTRIB_EQUIPMENT_PURCHASE_LOCATION) ||
            name.equals(ATTRIB_EQUIPMENT_TYPE) ||
            name.equals(ATTRIB_EQUIPMENT_WEIGHT_KILOGRAMS)) {

         parseEquipment(name);
      }
   }

   public LinkedHashMap<String, Integer> getCustomDataFieldDefinitions() {
      return _customDataFieldDefinitions;
   }

   public ArrayList<Equipment> getEquipments() {
      return _equipments;
   }

   private void parseEquipment(final String name) {

      final int numberOfEquipments = _equipments.size();
      if (numberOfEquipments == 0) {
         return;
      }

      final Equipment currentEquipment = _equipments.get(_equipments.size() - 1);

      if (name.equals(ATTRIB_EQUIPMENT_BRAND)) {

         currentEquipment.Brand = _characters.toString();

      } else if (name.equals(ATTRIB_EQUIPMENT_MODEL)) {

         currentEquipment.Model = _characters.toString();

      } else if (name.equals(ATTRIB_EQUIPMENT_DATE_PURCHASED)) {

         currentEquipment.DatePurchased = _characters.toString().substring(0, 10);

      } else if (name.equals(ATTRIB_EQUIPMENT_NOTES)) {

         currentEquipment.Notes = _characters.toString();

      } else if (name.equals(ATTRIB_EQUIPMENT_PURCHASE_LOCATION)) {

         currentEquipment.PurchaseLocation = _characters.toString();

      } else if (name.equals(ATTRIB_EQUIPMENT_TYPE)) {

         currentEquipment.Type = _characters.toString();

      } else if (name.equals(ATTRIB_EQUIPMENT_WEIGHT_KILOGRAMS)) {

         currentEquipment.WeightKilograms = _characters.toString();
      }
   }

   @Override
   public void startElement(final String uri, final String localName, final String name, final Attributes attributes)
         throws SAXException {

      if (_isInCustomDataFieldDefinitions) {

         /**
          * We parse and save the CustomDataFieldDefinitions in order to be able to format the
          * double
          * values to the configured
          *
          * @param importFilePath
          *           The file path of the FitLog or FitLogEx file
          */
         if (name.equals(TAG_ACTIVITY_CUSTOM_DATA_FIELD_DEFINITION)) {

            final String customFieldName = attributes.getValue(ATTRIB_CUSTOM_DATA_FIELD_DEFINITION_NAME);

            final String customFieldOptions = attributes.getValue(ATTRIB_CUSTOM_DATA_FIELD_DEFINITION_OPTIONS);

            if (customFieldOptions != null && customFieldOptions.trim().length() != 0) {

               final String[] tokens = customFieldOptions.split("\\|"); //$NON-NLS-1$
               if (tokens.length < 2) {
                  return;
               }

               final int numberOfDecimals = Integer.parseInt(tokens[1]);

               _customDataFieldDefinitions.put(customFieldName, numberOfDecimals);

            } else if (customFieldName.equals(ATTRIB_CUSTOM_DATA_FIELD_DEFINITION_TRIMP)) {
               // We make an exception for the TRIMP custom data field as its definition doesn't specify any specific number of decimals :
               // <.... Name="TRIMP" GroupAggregation="Sum" Options="">
               // However, this is how a TRIMP value is being exported
               // <... . name="TRIMP" v="81.8000717163086" />
               _customDataFieldDefinitions.put(customFieldName, 0);
            }
         }
      } else if (name.equals(TAG_ACTIVITY_CUSTOM_DATA_FIELD_DEFINITIONS)) {

         _isInCustomDataFieldDefinitions = true;

      } else if (_isInEquipment) {

         _characters.delete(0, _characters.length());

      } else if (name.equals(TAG_ACTIVITY_EQUIPMENT)) {

         _isInEquipment = true;

         final Equipment newEquipment = new Equipment();
         newEquipment.Id = attributes.getValue(ATTRIB_EQUIPMENT_ID);

         _equipments.add(newEquipment);

      }
   }
}

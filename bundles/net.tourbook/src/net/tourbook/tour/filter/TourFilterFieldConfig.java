/*******************************************************************************
 * Copyright (C) 2005, 2017 Wolfgang Schramm and Contributors
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
package net.tourbook.tour.filter;

import java.util.Arrays;

import net.tourbook.common.UI;

/**
 * Configuration for a tour filter field
 */
public class TourFilterFieldConfig {

	/**
	 * Visible field name
	 */
	final String				name;

	/**
	 * Every field must have a unique id, it is <code>null</code> when it's a category. config.
	 */
	TourFilterFieldId			fieldId;

	TourFilterFieldType			fieldType;

	/**
	 * Default field id when {@link #fieldType} is {@link TourFilterFieldType#CATEGORY}
	 */
	TourFilterFieldId			categoryDefaultFieldId;

	/**
	 * Contains all operators which are allowed for this field
	 */
	TourFilterFieldOperator[]	fieldOperators;

	int							minValue;
	int							maxValue		= Integer.MAX_VALUE;

	int							pageIncrement	= 10;

	/**
	 * Number of digits, default is 0.
	 */
	int							numDigits;

	FieldValueConverter			fieldValueConverter;

	String						unitLabel		= UI.EMPTY_STRING;

	private TourFilterFieldConfig(final String name) {
		this.name = name;
	}

	/**
	 * Create a field category configuration.
	 * 
	 * @param name
	 * @param fieldType
	 * @param categoryDefaultFieldId
	 */
	public TourFilterFieldConfig(final String name, final TourFilterFieldId categoryDefaultFieldId) {

		this.name = createLabel_Category(name);

		this.fieldId = null;
		this.fieldType = TourFilterFieldType.CATEGORY;

		this.fieldOperators = null;

		this.categoryDefaultFieldId = categoryDefaultFieldId;
	}

	private static String createLabel_Category(final String category) {

		final String label = UI.EMPTY_STRING
//				+ UI.SYMBOL_DBL_ANGLE_QMARK_RIGHT
//				+ UI.SYMBOL_DBL_ANGLE_QMARK_RIGHT
				+ UI.SPACE
				+ UI.SYMBOL_DBL_ANGLE_QMARK_RIGHT
				+ UI.SPACE
				+ UI.SYMBOL_DBL_ANGLE_QMARK_RIGHT
				+ UI.SPACE
				+ UI.SYMBOL_DBL_ANGLE_QMARK_RIGHT
				+ UI.SPACE
				+ category.toUpperCase()
				+ UI.SPACE
				+ UI.SYMBOL_DBL_ANGLE_QMARK_LEFT
				+ UI.SPACE
				+ UI.SYMBOL_DBL_ANGLE_QMARK_LEFT
				+ UI.SPACE
				+ UI.SYMBOL_DBL_ANGLE_QMARK_LEFT
				+ UI.SPACE
//				+ UI.SYMBOL_DBL_ANGLE_QMARK_LEFT
//				+ UI.SYMBOL_DBL_ANGLE_QMARK_LEFT

		;

		return label;
	}

	/**
	 * Creates a default {@link TourFilterFieldConfig} with
	 * <p>
	 * field type {@link TourFilterFieldType#NUMBER_INTEGER} <br>
	 * field operators {@link TourFilterManager#FILTER_OPERATORS_NUMBER}
	 * 
	 * @param name
	 * @return
	 */
	static TourFilterFieldConfig name(final String name) {

		final TourFilterFieldConfig config = new TourFilterFieldConfig(name);

		config.fieldType = TourFilterFieldType.NUMBER_INTEGER;
		config.fieldOperators = TourFilterManager.FILTER_OPERATORS_NUMBER;

		return config;
	}

	TourFilterFieldConfig fieldId(final TourFilterFieldId fieldId) {
		this.fieldId = fieldId;
		return this;
	}

	/**
	 * Set the field operators, default is {@link TourFilterManager#FILTER_OPERATORS_NUMBER}
	 * 
	 * @param fieldOperators
	 * @return
	 */
	TourFilterFieldConfig fieldOperators(final TourFilterFieldOperator[] fieldOperators) {
		this.fieldOperators = fieldOperators;
		return this;
	}

	/**
	 * Set the field type, default is {@link TourFilterFieldType#NUMBER_INTEGER}
	 * 
	 * @param fieldType
	 * @return
	 */
	TourFilterFieldConfig fieldType(final TourFilterFieldType fieldType) {
		this.fieldType = fieldType;
		return this;
	}

	/**
	 * Set the field value provider, default is <code>null</code>.
	 * 
	 * @param fieldValueProvider
	 * @return
	 */
	TourFilterFieldConfig fieldValueProvider(final FieldValueConverter fieldValueProvider) {
		this.fieldValueConverter = fieldValueProvider;
		return this;
	}

	/**
	 * Set maximum integer value, default is {@link Integer#MAX_VALUE}.
	 * 
	 * @param maxValue
	 * @return
	 */
	TourFilterFieldConfig maxValue(final int maxValue) {
		this.maxValue = maxValue;
		return this;
	}

	TourFilterFieldConfig minValue(final int minValue) {
		this.minValue = minValue;
		return this;
	}

	TourFilterFieldConfig numDigits(final int numDigits) {
		this.numDigits = numDigits;
		return this;
	}

	TourFilterFieldConfig pageIncrement(final int pageIncrement) {
		this.pageIncrement = pageIncrement;
		return this;
	}

	@Override
	public String toString() {
		return "TourFilterFieldConfig [\n" //$NON-NLS-1$
				+ ("name=" + name + ", \n") //$NON-NLS-1$ //$NON-NLS-2$
				+ ("fieldId=" + fieldId + ", \n") //$NON-NLS-1$ //$NON-NLS-2$
				+ ("fieldType=" + fieldType + ", \n") //$NON-NLS-1$ //$NON-NLS-2$
				+ ("fieldOperators=" + Arrays.toString(fieldOperators)) //$NON-NLS-1$
				+ "\n]\n"; //$NON-NLS-1$
	}

	/**
	 * Default is an empty string.
	 * 
	 * @param unitLabel
	 * @return
	 */
	TourFilterFieldConfig unitLabel(final String unitLabel) {
		this.unitLabel = unitLabel;
		return this;
	}

}

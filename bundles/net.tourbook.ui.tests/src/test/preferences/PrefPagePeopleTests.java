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
package preferences;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import utils.UITest;

public class PrefPagePeopleTests extends UITest {

   @Test
   void testPeopleBmi() {

      bot.toolbarButtonWithTooltip("Preferences (Ctrl+Shift+P)").click(); //$NON-NLS-1$
      bot.tree().getTreeItem("People").select(); //$NON-NLS-1$

      //70kg
      bot.spinner(0).setSelection(700);
      //1.80m
      bot.spinner(1).setSelection(180);

      //21.6 BMI
      assertEquals("21.6", bot.text(3).getText()); //$NON-NLS-1$

//      bot.cTabItem(Messages.Pref_People_Tab_HRZone).activate();
//      bot.cTabItem(Messages.Pref_People_Tab_DataTransfer).activate();

      bot.button("Apply and Close").click(); //$NON-NLS-1$
   }
}
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
package utils;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import net.tourbook.Messages;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

public class Utils {

   private static final String TOURBOOK_VIEW_NAME   = "Tour Book";                    //$NON-NLS-1$
   public static final String  STATISTICS_VIEW_NAME = "Statistics";

   public static final String  workingDirectory     = System.getProperty("user.dir"); //$NON-NLS-1$

   public static SWTBotTreeItem getTour(final SWTWorkbenchBot bot) {

      showTourBookView(bot);

      bot.toolbarButtonWithTooltip(Messages.App_Action_CollapseAll).click();

      final SWTBotTreeItem tour = bot.tree().getTreeItem("2021   2").expand() //$NON-NLS-1$
            .getNode("Jan   2").expand().select().getNode("31").select(); //$NON-NLS-1$ //$NON-NLS-2$
      assertNotNull(tour);

      return tour;
   }

   public static SWTBotView showTourBookView(final SWTWorkbenchBot bot) {

      return showView(bot, TOURBOOK_VIEW_NAME);
   }

   public static SWTBotView showView(final SWTWorkbenchBot bot, final String viewName) {

      final SWTBotView view = bot.viewByTitle(viewName);
      assertNotNull(view);
      view.show();

      return view;
   }

   public static void showViewFromMenu(final SWTWorkbenchBot bot, final String menuName, final String viewName) {

      final SWTBotMenu viewMenu = bot.menu(menuName).menu(viewName).click();
      assertNotNull(viewMenu);
   }
}

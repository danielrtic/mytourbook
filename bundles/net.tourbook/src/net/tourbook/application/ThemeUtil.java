package net.tourbook.application;

import java.util.ArrayList;
import java.util.List;

import net.tourbook.common.UI;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.css.swt.theme.ITheme;
import org.eclipse.e4.ui.css.swt.theme.IThemeEngine;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

public class ThemeUtil {

   /*
    * Copied from org.eclipse.e4.ui.internal.workbench.swt.E4Application
    */
   public static final String THEME_ID               = "cssTheme";                                  //$NON-NLS-1$
   public static final String HIGH_CONTRAST_THEME_ID = "org.eclipse.e4.ui.css.theme.high-contrast"; //$NON-NLS-1$

   /*
    * Copied from org.eclipse.e4.ui.css.swt.internal.theme.ThemeEngine
    */
   public static final String  E4_DARK_THEME_ID = "org.eclipse.e4.ui.css.theme.e4_dark"; //$NON-NLS-1$

   private static IThemeEngine _themeEngine;

   public static List<ITheme> getAllThemes() {

      setupTheme();

      final ArrayList<ITheme> allThemes = new ArrayList<>();

      for (final ITheme theme : _themeEngine.getThemes()) {

         /*
          * When we have Win32 OS - when the high contrast mode is enabled on the
          * platform, we display the 'high-contrast' special theme only. If not, we don't
          * want to mess the themes combo with the theme since it is the special
          * variation of the 'classic' one
          * When we have GTK - we have to display the entire list of the themes since we
          * are not able to figure out if the high contrast mode is enabled on the
          * platform. The user has to manually select the theme if they need it
          */
//         if (!highContrastMode && !Util.isGtk() && theme.getId().equals(E4Application.HIGH_CONTRAST_THEME_ID)) {
//            continue;
//         }

         // hide high contrast theme in all cases to have a clean combo
         if (theme.getId().equals(HIGH_CONTRAST_THEME_ID)) {
            continue;
         }

         allThemes.add(theme);
      }

      // sort themes by their name
      allThemes.sort((final ITheme t1, final ITheme t2) -> t1.getLabel().compareTo(t2.getLabel()));

      return allThemes;
   }

   public static final void setupTheme() {

      if (_themeEngine != null) {
         return;
      }

      final MApplication application = PlatformUI.getWorkbench().getService(MApplication.class);
      final IEclipseContext context = application.getContext();

      _themeEngine = context.get(org.eclipse.e4.ui.css.swt.theme.IThemeEngine.class);

      final ITheme activeTheme = _themeEngine.getActiveTheme();
      if (activeTheme != null) {

         final boolean isDarkThemeSelected = E4_DARK_THEME_ID.equals(activeTheme.getId());

         setWinDarkThemeHack(isDarkThemeSelected);
      }
   }

   /**
    * Copied from org.eclipse.swt.internal.win32.OS.setTheme(boolean)
    * <p>
    * See also {@link "https://www.eclipse.org/eclipse/news/4.16/platform_isv.html#win-dark-tweaks"}
    * <p>
    * Experimental API for dark theme.
    * <p>
    * On Windows, there is no OS API for dark theme yet, and this method only
    * configures various tweaks. Some of these tweaks have drawbacks. The tweaks
    * are configured with defaults that fit Eclipse. Non-Eclipse applications are
    * expected to configure individual tweaks instead of calling this method.
    * Please see <code>Display#setData()</code> and documentation for string keys
    * used there.
    *
    * @param isDarkTheme
    *           <code>true</code> for dark theme
    */
   public static final void setWinDarkThemeHack(final boolean isDarkTheme) {

      if (!UI.IS_WIN) {

         // this hack is only for windows

         return;
      }

      /**
       */

      final Display display = Display.getDefault();

// SET_FORMATTING_OFF

      display.setData("org.eclipse.swt.internal.win32.useDarkModeExplorerTheme",       isDarkTheme);
      display.setData("org.eclipse.swt.internal.win32.menuBarForegroundColor",         isDarkTheme ? new Color(0xD0, 0xD0, 0xD0) : null);
      display.setData("org.eclipse.swt.internal.win32.menuBarBackgroundColor",         isDarkTheme ? new Color(0x30, 0x30, 0x30) : null);
      display.setData("org.eclipse.swt.internal.win32.menuBarBorderColor",             isDarkTheme ? new Color(0x50, 0x50, 0x50) : null);
      display.setData("org.eclipse.swt.internal.win32.Canvas.use_WS_BORDER",           isDarkTheme);
      display.setData("org.eclipse.swt.internal.win32.List.use_WS_BORDER",             isDarkTheme);
      display.setData("org.eclipse.swt.internal.win32.Table.use_WS_BORDER",            isDarkTheme);
      display.setData("org.eclipse.swt.internal.win32.Text.use_WS_BORDER",             isDarkTheme);
      display.setData("org.eclipse.swt.internal.win32.Tree.use_WS_BORDER",             isDarkTheme);
      display.setData("org.eclipse.swt.internal.win32.Table.headerLineColor",          isDarkTheme ? new Color(0x50, 0x50, 0x50) : null);
      display.setData("org.eclipse.swt.internal.win32.Label.disabledForegroundColor",  isDarkTheme ? new Color(0x80, 0x80, 0x80) : null);
      display.setData("org.eclipse.swt.internal.win32.Combo.useDarkTheme",             isDarkTheme);
      display.setData("org.eclipse.swt.internal.win32.ProgressBar.useColors",          isDarkTheme);

// SET_FORMATTING_ON
   }
}

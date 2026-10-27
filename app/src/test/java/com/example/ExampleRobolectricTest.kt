package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Flathub", appName)
  }

  @Test
  fun `verify Flatpak helper URLs and commands`() {
    val appId = "re.sonny.Tangram"
    assertEquals("https://flathub.org/apps/re.sonny.Tangram", com.example.util.FlatpakHelper.getFlathubWebUrl(appId))
    assertEquals("https://dl.flathub.org/repo/appstream/re.sonny.Tangram.flatpakref", com.example.util.FlatpakHelper.getFlatpakrefUrl(appId))
    assertEquals("flatpak install flathub re.sonny.Tangram", com.example.util.FlatpakHelper.getInstallCommand(appId))
  }

  @Test
  fun `verify ThemePreferences saves and retrieves theme correctly`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val themePrefs = com.example.util.ThemePreferences(context)

    themePrefs.setTheme(com.example.ui.theme.AppTheme.DARK)
    assertEquals(com.example.ui.theme.AppTheme.DARK, themePrefs.getTheme())

    themePrefs.setTheme(com.example.ui.theme.AppTheme.LIGHT)
    assertEquals(com.example.ui.theme.AppTheme.LIGHT, themePrefs.getTheme())
  }
}

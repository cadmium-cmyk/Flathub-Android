package com.example.util

import android.app.DownloadManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.widget.Toast

object FlatpakHelper {

    fun getFlathubWebUrl(appId: String): String {
        return "https://flathub.org/apps/$appId"
    }

    fun getFlatpakrefUrl(appId: String): String {
        return "https://dl.flathub.org/repo/appstream/$appId.flatpakref"
    }

    fun getInstallCommand(appId: String): String {
        return "flatpak install flathub $appId"
    }

    fun getRunCommand(appId: String): String {
        return "flatpak run $appId"
    }

    /**
     * Downloads the .flatpakref file directly to the system Downloads folder.
     */
    fun downloadFlatpakref(context: Context, appId: String, appName: String): Boolean {
        return try {
            val downloadUrl = getFlatpakrefUrl(appId)
            val request = DownloadManager.Request(Uri.parse(downloadUrl)).apply {
                setTitle("$appName.flatpakref")
                setDescription("Flatpak reference file for $appName")
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    "$appId.flatpakref"
                )
                setMimeType("application/vnd.flatpak.ref")
                setAllowedOverMetered(true)
                setAllowedOverRoaming(true)
            }

            val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager
            manager?.enqueue(request)
            Toast.makeText(
                context,
                "Downloading $appId.flatpakref to Downloads folder...",
                Toast.LENGTH_SHORT
            ).show()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                context,
                "Unable to start download: ${e.localizedMessage}",
                Toast.LENGTH_SHORT
            ).show()
            false
        }
    }

    /**
     * Copies the Flathub website link to clipboard.
     */
    fun copyFlathubLink(context: Context, appId: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Flathub Link", getFlathubWebUrl(appId))
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Link copied to clipboard!", Toast.LENGTH_SHORT).show()
    }

    /**
     * Copies the terminal flatpak install command to clipboard.
     */
    fun copyInstallCommand(context: Context, appId: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Flatpak Install Command", getInstallCommand(appId))
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Command copied: ${getInstallCommand(appId)}", Toast.LENGTH_SHORT).show()
    }

    /**
     * Triggers Android share sheet to send link or install details to another device (e.g., via KDE Connect, LocalSend, QuickShare, messaging).
     */
    fun shareApp(context: Context, appId: String, appName: String) {
        val shareText = buildString {
            append("Check out $appName on Flathub:\n")
            append(getFlathubWebUrl(appId))
            append("\n\nInstall via Linux Terminal:\n")
            append(getInstallCommand(appId))
            append("\n\nDownload .flatpakref:\n")
            append(getFlatpakrefUrl(appId))
        }

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            putExtra(Intent.EXTRA_SUBJECT, "$appName on Flathub")
            type = "text/plain"
        }
        val chooser = Intent.createChooser(sendIntent, "Send $appName to device")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }

    /**
     * Opens an external URL in the default browser.
     */
    fun openInBrowser(context: Context, url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "No browser found to open link", Toast.LENGTH_SHORT).show()
        }
    }
}

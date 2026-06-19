package io.github.karino2.kakito

import android.app.Activity
import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.core.content.edit
import io.github.karino2.fastfile.FastFile

data class Entry(val yomi: String, val kanji: String) {
    companion object {
        const val LAST_URI_KEY = "last_uri_path"

        fun sharedPreferences(ctx: Context) = ctx.getSharedPreferences("KAKITO", Context.MODE_PRIVATE)!!
        fun lastUri(ctx: Context) = sharedPreferences(ctx).getString(LAST_URI_KEY, null)?.toUri()

        fun writeLastUri(ctx:Context, uri : Uri) = sharedPreferences(ctx)
            .edit(commit = true) {
                putString(LAST_URI_KEY, uri.toString())
            }

        fun fromLastUri(activity: Activity): List<Entry> {
            val uri = lastUri(activity) ?: return emptyList<Entry>()
            val ff = FastFile.fromDocUri(activity.contentResolver, uri) ?: return emptyList<Entry>()
            return ff.readText()
                .split("\n")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .mapNotNull { line ->
                    val cells = line.split(" ")
                    if (cells.size >= 2) {
                        Entry(cells[0], cells[1])
                    } else {
                        null
                    }
                }
                .toList()
        }

        fun writeToLastUri(activity: Activity, entries: List<Entry>) {
            val uri = lastUri(activity) ?: return
            val ff = FastFile.fromDocUri(activity.contentResolver, uri) ?: return
            val content = entries.joinToString("\n") { "${it.yomi} ${it.kanji}" }
            ff.writeText(content)
        }
    }



}
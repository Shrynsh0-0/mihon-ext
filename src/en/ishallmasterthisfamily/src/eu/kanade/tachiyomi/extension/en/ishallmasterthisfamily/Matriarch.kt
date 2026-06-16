package eu.kanade.tachiyomi.extension.en.ishallmasterthisfamily

import okhttp3.Request
import okhttp3.Response
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class Matriarch {

    val name = "I Shall Master This Family"
    val baseUrl = "https://ishallmasterthisfamily.art"
    val lang = "en"

    // 1. POPULAR / LATEST COMICS LIST
    fun popularMangaRequest(page: Int): Request {
        return Request.Builder().url("$baseUrl/page/$page").build()
    }

    fun popularMangaParse(response: Response): List<MangaSelector> {
        val document = org.jsoup.Jsoup.parse(response.body?.string() ?: "")
        return document.select("div.post").map { element ->
            MangaSelector(
                title = element.select("h2, h3, .title").text(),
                url = element.select("a").attr("href"),
                thumbnailUrl = element.select("img").attr("src")
            )
        }
    }

    // 2. CHAPTER LIST
    fun chapterListParse(response: Response): List<ChapterSelector> {
        val document = org.jsoup.Jsoup.parse(response.body?.string() ?: "")
        return document.select("li.chapter-item, div.chapter-link a").map { element ->
            ChapterSelector(
                name = element.text(),
                url = element.attr("href")
            )
        }
    }

    // 3. IMAGES INSIDE A CHAPTER
    fun pageListParse(response: Response): List<String> {
        val document = org.jsoup.Jsoup.parse(response.body?.string() ?: "")
        return document.select("div.reading-content img, entry-content img").map { element ->
            element.attr("src")
        }
    }
}

data class MangaSelector(val title: String, val url: String, val thumbnailUrl: String)
data class ChapterSelector(val name: String, val url: String)
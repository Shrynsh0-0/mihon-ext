package eu.kanade.tachiyomi.extension.en.ishallmasterthisfamily

import eu.kanade.tachiyomi.network.GET
import eu.kanade.tachiyomi.source.model.FilterList
import eu.kanade.tachiyomi.source.model.MangasPage
import eu.kanade.tachiyomi.source.model.SChapter
import eu.kanade.tachiyomi.source.model.SManga
import eu.kanade.tachiyomi.source.online.HttpSource
import okhttp3.Request
import okhttp3.Response
import org.jsoup.nodes.Document
import java.util.Calendar

class Matriarch : HttpSource() {

    override val name = "I Shall Master This Family"
    override val baseUrl = "https://ishallmasterthisfamily.art"
    override val lang = "en"
    override val supportsLatest = true

    override fun popularMangaRequest(page: Int): Request {
        return GET("$baseUrl/page/$page", headers)
    }

    override fun popularMangaParse(response: Response): MangasPage {
        val document = response.asJsoup()
        val mangas = document.select("div.post").map { element ->
            SManga.create().apply {
                title = element.select("h2, h3, .title").text()
                url = element.select("a").attr("abs:href").substringAfter(baseUrl)
                thumbnail_url = element.select("img").attr("abs:src")
            }
        }
        return MangasPage(mangas, hasNextPage = true)
    }

    override fun latestUpdatesRequest(page: Int): Request = popularMangaRequest(page)
    override fun latestUpdatesParse(response: Response): MangasPage = popularMangaParse(response)

    override fun searchMangaRequest(page: Int, query: String, filters: FilterList): Request {
        return GET("$baseUrl/?s=$query", headers)
    }
    override fun searchMangaParse(response: Response): MangasPage = popularMangaParse(response)

    override fun mangaDetailsParse(document: Document): SManga = SManga.create().apply {
        description = "Custom saved extension for reading I Shall Master This Family."
    }

    override fun chapterListParse(response: Response): List<SChapter> {
        val document = response.asJsoup()
        return document.select("li.chapter-item a, div.chapter-link a").map { element ->
            SChapter.create().apply {
                this.name = element.text()
                this.url = element.attr("abs:href").substringAfter(baseUrl)
                this.date_upload = Calendar.getInstance().timeInMillis
            }
        }
    }

    override fun pageListParse(document: Document): List<eu.kanade.tachiyomi.source.model.Page> {
        return document.select("div.reading-content img, entry-content img").mapIndexed { index, element ->
            eu.kanade.tachiyomi.source.model.Page(index, "", element.attr("abs:src"))
        }
    }

    override fun imageUrlParse(document: Document): String = throw UnsupportedOperationException()
    
    private fun Response.asJsoup(): Document {
        return org.jsoup.Jsoup.parse(this.body?.string() ?: "", this.request.url.toString())
    }
}
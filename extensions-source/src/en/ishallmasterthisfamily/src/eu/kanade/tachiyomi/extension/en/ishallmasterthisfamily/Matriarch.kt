package eu.kanade.tachiyomi.extension.en.ishallmasterthisfamily

import eu.kanade.tachiyomi.network.GET
import eu.kanade.tachiyomi.source.model.FilterList
import eu.kanade.tachiyomi.source.model.MangasPage
import eu.kanade.tachiyomi.source.model.Page
import eu.kanade.tachiyomi.source.model.SChapter
import eu.kanade.tachiyomi.source.model.SManga
import eu.kanade.tachiyomi.source.online.HttpSource
import eu.kanade.tachiyomi.util.asJsoup
import keiyoushi.annotation.Source
import okhttp3.Request
import okhttp3.Response

@Source
open class Matriarch : HttpSource() { // ADDED 'open' TO UNLOCK THE CLASS FOR THE COMPILER

    override val name = "I Shall Master This Family"
    override val baseUrl = "https://ishallmasterthisfamily.art"
    override val lang = "en"
    override val supportsLatest = false

    override fun popularMangaRequest(page: Int): Request = GET(baseUrl, headers)

    override fun popularMangaParse(response: Response): MangasPage {
        val document = response.asJsoup()
        val manga = SManga.create().apply {
            title = document.select("h1.title").text().ifEmpty { "I Shall Master This Family" }
            url = "/"
            thumbnail_url = document.select(".left-column img").attr("abs:src")
        }
        return MangasPage(listOf(manga), hasNextPage = false)
    }

    override fun latestUpdatesRequest(page: Int): Request = popularMangaRequest(page)
    override fun latestUpdatesParse(response: Response): MangasPage = popularMangaParse(response)

    override fun searchMangaRequest(page: Int, query: String, filters: FilterList): Request = popularMangaRequest(page)
    override fun searchMangaParse(response: Response): MangasPage = popularMangaParse(response)

    override fun mangaDetailsParse(response: Response): SManga {
        val document = response.asJsoup()
        return SManga.create().apply {
            title = document.select("h1.title").text()
            thumbnail_url = document.select(".left-column img").attr("abs:src")
            description = document.select(".wp-block-list li:first-child").text().substringAfter("Summary :").trim()
            author = document.select(".wp-block-list li:contains(Author)").text().substringAfter(":").trim()
            artist = document.select(".wp-block-list li:contains(Studio)").text().substringAfter(":").trim()
            genre = document.select(".wp-block-list li:contains(Genre)").text().substringAfter(":").trim()
            status = SManga.ONGOING
        }
    }

    override fun chapterListParse(response: Response): List<SChapter> {
        val document = response.asJsoup()
        return document.select("div.list-body ul li.item a").map { element ->
            SChapter.create().apply {
                name = element.select("span:first-child").text()
                url = element.attr("abs:href").substringAfter(baseUrl)
                date_upload = 0L
            }
        }
    }

    override fun pageListParse(response: Response): List<Page> {
        val document = response.asJsoup()
        return document.select("div.reading-content img, div.entry-content img, div.post-content img").mapIndexed { index, element ->
            val imageUrl = element.attr("abs:src").ifEmpty { element.attr("abs:data-src") }
            Page(index, "", imageUrl)
        }
    }

    override fun imageUrlParse(response: Response): String = throw UnsupportedOperationException()
}

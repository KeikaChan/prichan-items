import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.io.File

fun main() {
    Crawl().collectFirst()
}

class Crawl {
    companion object {
        const val coordeTest = "https://prichan.jp/season1/items/"
        const val urlString = "./urls.txt"
        const val first = "1"
        const val second = "2"
    }

    /**
     * 動的にURLを変更できるように外に書き出しておく
     */
    fun getUrls(): HashMap<String, String> {
        val urls = File(urlString).readLines()
        val urlMap = hashMapOf<String, String>()
        urls.forEach {
            val splitted = it.split(",")
            val season = splitted.first()
            val url = splitted.last()
            urlMap[season] = url
        }
        return urlMap
    }

    fun collectFirst() {
        // とりあえずリストの最初を辿れるようにする。
        collectionNav()
    }

    fun collectionNav() {
        val navigations = connection(coordeTest)!!.getElementsByClass("items-nav").select("li")
        var navigationList = mutableListOf<Pair<String, String>>()
        navigations.forEach {
            var nameElement = it.getElementsByTag("a").first().toString()
            val name = Jsoup.parse(nameElement.replace("<rt>.+?<\\/rt>".toRegex(), "")).text()
            val url = it.getElementsByTag("a").attr("abs:href")
            val element = Pair(name, url)
            navigationList.add(element)
        }
        // first が 名前　secondがurl
        navigationList =
            navigationList.filterTo(mutableListOf()) { !it.second.contains("index.html") } //index.htmlになっているのを弾く
//        navigationList.forEach { println(it.first + ":" + it.second) }
        collectionOutfit(navigationList.first().second)
    }

    fun collectionOutfit(listUrl: String) {
        val coorditem = connection(listUrl)!!.getElementsByClass("coordinate-list")
//        coorditem.forEach { println(it.toString()) }
        val coords = mutableListOf<Coord>()

        coorditem.forEach {
            val outfits =
                it.getElementsByClass("-outfit").first().getElementsByTag("img").first().attr("abs:data-src") + ""
            var details = mutableListOf<String>()

            it.getElementsByTag("a").forEach { element ->
                details.add(element.attr("abs:href"))
            }
        }

    }

    fun collectionDetails(detailElement: Elements): List<Coord> {
    }


    /**
     * 接続用の関数
     * 404以外は5回までループして集める。
     */
    fun connection(url: String, count: Int = 0): Document? {
        var document: Document? = null
        try {
            document = Jsoup.connect(url).get()
            document.outputSettings(Document.OutputSettings().prettyPrint(false))
        } catch (e: HttpStatusException) {
            if (e.statusCode != 404 && count < 10) {
                return connection(url, count + 1)
            }
        }
        return document
    }


}

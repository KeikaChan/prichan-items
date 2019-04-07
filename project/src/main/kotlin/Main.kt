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
        val navigationList = collectionNav()
        val coordList = mutableListOf<Coord>()

        navigationList.forEach {
            coordList.addAll(collectDetails(it.first, it.second))
        }

        coordList.forEach { println(it.toString()) }
        exportCSV(coordList, "output.csv")
    }
/**/
    /**
     * ナビゲーションデータの収集
     */
    fun collectionNav(): List<Pair<String, String>> {
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
            navigationList.filterTo(mutableListOf()) {
                !it.second.contains("index.html") && !it.second.contains("promotion.html") && !it.second.contains(
                    "ticket.html"
                )
            } //index.htmlになっているのを弾く


        // TODO: あとで全適用に直す

        return navigationList
    }

    /**
     * それぞれのコーデデータの収集
     */
    fun collectDetails(listName: String, listUrl: String): List<Coord> {
        println(listName + " " + listUrl)
        val coorditem = connection(listUrl)!!.getElementsByClass("coordinate-list")
        val coords = mutableListOf<Coord>()

        coorditem.forEach {
            val outfit =
                it.getElementsByClass("-outfit").first()
                    .getElementsByTag("img").first()
                    .attr("abs:data-src") + ""

            var details = mutableListOf<String>()

            it.getElementsByTag("a").forEach { element ->
                details.add(element.attr("abs:href"))
            }

            coords.addAll(collectionDetails(listName, listUrl, outfit, details))
        }
        return coords
    }

    fun collectionDetails(listName: String, listUrl: String, outfitUrl: String, detailsUrl: List<String>): List<Coord> {

        var coordList = mutableListOf<Coord>()

        detailsUrl.forEach { detailUrl ->
            // 並び順
            // name	image_url	category	item_id	ticket_id	color	brand	brand_image_url
            // genre	genre_image_url	rarity	like	outfit_id	outfit_image_url	note
            // detail_url	series_name	series_url


            var coord = Coord()
            val article = connection(detailUrl)!!
            val detailsElements = article.getElementsByClass("-details").first().getElementsByClass("-detail")

            coord.name = article.getElementsByClass("-title").first().text()

            coord.image_url =
                article.getElementsByClass("-thumb").first()
                    .getElementsByTag("img").first()
                    .attr("abs:data-src")


            coord.category = detailsElements[0].getElementsByClass("-value").text()

            coord.item_id = article.getElementsByClass("-thumb").first()
                .getElementsByClass("-id").text()

            coord.ticket_id = detailUrl.split("/").last().replace(".html", "")

            coord.color = detailsElements[1].getElementsByClass("-value").text()

            coord.brand_image_url = article.getElementsByClass("-detail -brand").first()
                .getElementsByTag("img").first()
                .attr("abs:data-src")

            coord.brand = coord.brand_image_url.split("/").last()
                .replace("logo-", "")
                .replace(".png", "")


            coord.genre_image_url = article.getElementsByClass("-detail -genre").first()
                .getElementsByTag("img").first()
                .attr("abs:data-src")

            coord.genre = coord.genre_image_url.split("/").last()
                .replace("icon-", "")
                .replace(".png", "")

            coord.rarity = article.getElementsByClass("-rarity").text()

            coord.like = article.getElementsByClass("-like").text()

            coord.outfit_image_url = outfitUrl

            coord.outfit_id = outfitUrl.split("/").last().replace(".png", "")

            coord.note = ""

            coord.detail_url = detailUrl

            coord.season = first //ここは決め打ち

            coord.series_name = listName

            coord.series_url = listUrl


            coordList.add(coord)
        }
        return coordList
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

    /**
     * csvエクスポート用
     */
    fun exportCSV(coordList: List<Coord>, output: String) {
        File(output).apply {
            appendText(coordList.first().getCSVTitle())

            coordList.forEach {
                appendText(it.getCSVString())
            }
        }
    }

}

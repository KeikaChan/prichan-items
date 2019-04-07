package seasons

import org.jsoup.Jsoup

class Season1 : Season {

    override fun crawsCoords(baseUrl: String) {
        val navigationList = collectionNav(baseUrl)
        val coordList = hashSetOf<Coord>()

        navigationList.forEach {
            coordList.addAll(collectDetails(it.first, it.second))
        }

        coordList.forEach { println(it.toString()) }
        Common.exportCSV(coordList.toList(), "output.csv")
        Common.exportJson(coordList.toList(), "jsonout.json")
    }

    /**
     * ナビゲーションデータの収集
     */
    fun collectionNav(url: String): List<Pair<String, String>> {
        val navigations = Common.connection(url)!!.getElementsByClass("items-nav").select("li")
        var navigationList = mutableListOf<Pair<String, String>>()
        navigations.forEach {
            var nameElement = it.getElementsByTag("a").first().toString()
            val name = Jsoup.parse(nameElement.replace("<rt>.+?<\\/rt>".toRegex(), "")).text()
            val url = it.getElementsByTag("a").attr("abs:href")
            val element = Pair(name, url)
            navigationList.add(element)
        }
        // first が 名前　secondがurl
        val urlHashSet = hashSetOf<String>()
        navigationList =
            navigationList.filterTo(mutableListOf()) {
                !it.second.contains("index.html")
                        && !it.second.contains("promotion.html")
                        && !it.second.contains("ticket.html")
                        && urlHashSet.add(it.second)
            } //使わないやつを弾く


        return navigationList
    }

    /**
     * それぞれのコーデデータの収集
     */
    fun collectDetails(listName: String, listUrl: String): List<Coord> {
        println(listName + " " + listUrl)
        val coorditem = Common.connection(listUrl)!!.getElementsByClass("coordinate-list")
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
            val article = Common.connection(detailUrl)!!
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

            coord.season = Common.first //ここは決め打ち

            coord.series_name = listName

            coord.series_url = listUrl


            coordList.add(coord)
        }
        return coordList
    }

}
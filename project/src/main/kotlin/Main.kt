import org.jsoup.Jsoup
import seasons.Coord
import seasons.Season
import seasons.Season1
import seasons.exportCSV
import java.io.File

fun main() {
    val one: Season =Season1()
    one.crawsCoords("https://prichan.jp/season1/items/")
}

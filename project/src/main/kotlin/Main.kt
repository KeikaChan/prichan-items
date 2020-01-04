import resource.Season1Resource
import seasons.Juel

fun main() {
//    val one: Season = Season1Resource()
//    one.crawsCoords("https://prichan.jp/season1/items/")
    val juel: Season = Juel()
    juel.crawsCoords("https://prichan.jp/item/J01/")
}

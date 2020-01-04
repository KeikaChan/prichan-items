import com.fasterxml.jackson.databind.ObjectMapper
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.File

/**
 * 色々
 */
class Common {
    companion object {
        const val coordeTest = "https://prichan.jp/season1/items/"
        const val urlString = "./urls.txt"
        const val first = "season1"
        const val second = "juel"

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

        /**
         * jsonとして出力
         */
        fun exportJson(coordList: List<Coord>, output: String){

            File(output).apply {
                writeText(ObjectMapper().writeValueAsString(coordList))
            }
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
    }


}
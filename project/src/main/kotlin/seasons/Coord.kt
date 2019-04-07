package seasons

class Coord {
    //名前はHTMLの要素名とpython実装時に合わせています。

    var name: String = ""
    var image_url: String = ""
    var category: String = ""
    var item_id: String = ""
    var ticket_id: String = ""
    var color: String = ""
    var brand: String = ""
    var brand_image_url: String = ""
    var genre: String = ""
    var genre_image_url: String = ""
    var rarity: String = ""
    var like: String = ""
    var outfit_id: String = ""
    var outfit_image_url: String = ""
    var detail_url: String = ""
    var series_name: String = ""
    var series_url: String = ""
    var season: String = "" // 新しく追加。シーズン season1 (無印) / season 2 (ジュエル)
    var note: String = ""

    override fun toString(): String {
        return "name:" + name + "\n" +
                "image_url:" + image_url + "\n" +
                "category:" + category + "\n" +
                "item_id:" + item_id + "\n" +
                "ticket_id:" + ticket_id + "\n" +
                "color:" + color + "\n" +
                "brand:" + brand + "\n" +
                "brand_image_url:" + brand_image_url + "\n" +
                "genre:" + genre + "\n" +
                "genre_image_url:" + genre_image_url + "\n" +
                "rarity:" + rarity + "\n" +
                "like:" + like + "\n" +
                "outfit_id:" + outfit_id + "\n" +
                "outfit_image_url:" + outfit_image_url + "\n" +
                "detail_url:" + detail_url + "\n" +
                "series_name:" + series_name + "\n" +
                "series_url:" + series_url + "\n" +
                "season:" + season + "\n" +
                "note:" + note + "\n"
    }

    fun getCSVTitle(): String {
        return "name,image_url,category,item_id,ticket_id,color,brand,brand_image_url,genre,genre_image_url,rarity,like,outfit_id,outfit_image_url,detail_url,series_name,series_url,season,note\n"
    }

    fun getCSVString(): String {
        return "$name,$image_url,$category,$item_id,$ticket_id,$color,$brand,$brand_image_url,$genre,$genre_image_url,$rarity,$like,$outfit_id,$outfit_image_url,$detail_url,$series_name,$series_url,$season,$note\n"
    }
}

package resource

import com.fasterxml.jackson.annotation.JsonIgnore
import Coord

class Resource: Coord() {


    @JsonIgnore
    override fun getCSVTitle(): String {
        return "name,image_url,item_id,ticket_id,brand,brand_image_url,genre,genre_image_url,outfit_id,outfit_image_url,detail_url,series_url\n"
    }

    @JsonIgnore
    override fun getCSVString(): String {
        return "$name,$image_url,$item_id,$ticket_id,$brand,$brand_image_url,$genre,$genre_image_url,$outfit_id,$outfit_image_url,$detail_url,$series_url\n"
    }
}

package `in`.bps.prolist.models

data class Product(
    val _id : String
    ,
    val category : String,
    val measureIn : String,
    val productImage : String,
    val productName_en : String,
    val productName_kn : String,
    val productPrice : Int
)

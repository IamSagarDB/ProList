package `in`.bps.prolist.models

data class ApiResponse(
    val statusCode: Int,
    val message: String,
    val product: Product,
    val products: List<Product>
)

package `in`.bps.prolist.api

import `in`.bps.prolist.helper.Constants
import `in`.bps.prolist.models.ApiResponse
import `in`.bps.prolist.models.Product
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    // Get All Products
    @GET(Constants.getProducts)
    suspend fun getAllProducts() : ApiResponse

    // Get Product by Id
    @GET(Constants.getProductById)
    suspend fun getProductById(@Path("id") id : String) : ApiResponse

    // Add new Product
    @POST(Constants.getProducts)
    suspend fun addNewProduct(@Body params: Product) : ApiResponse

    // Delete a product
    @DELETE(Constants.getProductById)
    suspend fun deleteProduct(@Path("id") id : String) : ApiResponse

    // Patch / Update product
    @PATCH(Constants.getProductById)
    suspend fun updateProduct(@Body params: Product, @Path("id") id : String) : ApiResponse
}
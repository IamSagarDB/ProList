package `in`.bps.prolist.api

import `in`.bps.prolist.models.ApiResponse
import `in`.bps.prolist.models.Product
import javax.inject.Inject

class ApiServiceImp @Inject constructor(private val apiService: ApiService) {
    suspend fun getAllProducts() : ApiResponse = apiService.getAllProducts()
    suspend fun getProductById(id : String) : ApiResponse = apiService.getProductById(id)
    suspend fun addNewProduct(product: Product) : ApiResponse = apiService.addNewProduct(product)
    suspend fun deleteProduct(id: String) : ApiResponse = apiService.deleteProduct(id)
    suspend fun updateProduct(product: Product) : ApiResponse = apiService.updateProduct(product)
}
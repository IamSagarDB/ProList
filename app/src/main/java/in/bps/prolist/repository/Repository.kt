package `in`.bps.prolist.repository

import `in`.bps.prolist.api.ApiServiceImp
import `in`.bps.prolist.models.ApiResponse
import `in`.bps.prolist.models.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class Repository @Inject constructor(private val apiServiceImp: ApiServiceImp) {

    fun getAllProducts(): Flow<ApiResponse> = flow {
        emit(apiServiceImp.getAllProducts())
    }.flowOn(Dispatchers.IO)

    fun getProductById(id: String): Flow<ApiResponse> = flow {
        emit(apiServiceImp.getProductById(id))
    }.flowOn(Dispatchers.IO)

    fun addNewProduct(product: Product): Flow<ApiResponse> = flow {
        emit(apiServiceImp.addNewProduct(product))
    }.flowOn(Dispatchers.IO)

    fun deleteProduct(id: String) : Flow<ApiResponse> = flow {
        emit(apiServiceImp.deleteProduct(id))
    }.flowOn(Dispatchers.IO)

    fun updateProduct(product: Product, id :String) : Flow<ApiResponse>  = flow {
        emit(apiServiceImp.updateProduct(product, id))
    }.flowOn(Dispatchers.IO)
}
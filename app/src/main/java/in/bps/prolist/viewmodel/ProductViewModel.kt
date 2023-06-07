package `in`.bps.prolist.viewmodel

import `in`.bps.prolist.api.ApiState
import `in`.bps.prolist.models.Product
import `in`.bps.prolist.repository.Repository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    private val _getAllProducts: MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Empty)
    val getAllProducts : StateFlow<ApiState> get() = _getAllProducts

    private val _getProductById: MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Empty)
    val getProductById : StateFlow<ApiState> get() = _getProductById

    private val _addNewProduct: MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Empty)
    val addNewProduct : StateFlow<ApiState> get() = _addNewProduct

    private val _deleteProduct: MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Empty)
    val deleteProduct : StateFlow<ApiState> get() = _deleteProduct

    private val _updateProduct: MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Empty)
    val updateProduct : StateFlow<ApiState> get() = _updateProduct



    fun getAllProducts() = viewModelScope.launch {
        _getAllProducts.value = ApiState.Loading
        repository.getAllProducts().catch { e ->
            _getAllProducts.value = ApiState.Failure(e)
        }.collect{ data ->
            _getAllProducts.value = ApiState.Success(data)
        }
    }

    fun getProductById(id: String) = viewModelScope.launch {
        _getProductById.value = ApiState.Loading
        repository.getProductById(id).catch { e ->
            _getProductById.value = ApiState.Failure(e)
        }.collect{ data ->
            _getProductById.value = ApiState.Success(data)
        }
    }

    fun addNewProduct(product: Product) = viewModelScope.launch {
        _addNewProduct.value = ApiState.Loading
        repository.addNewProduct(product).catch { e ->
            _addNewProduct.value = ApiState.Failure(e)
        }.collect{ data ->
            _addNewProduct.value = ApiState.Success(data)
        }
    }

    fun deleteProduct(id: String) = viewModelScope.launch {
        _deleteProduct.value = ApiState.Loading
        repository.deleteProduct(id).catch { e ->
            _deleteProduct.value = ApiState.Failure(e)
        }.collect{ data ->
            _deleteProduct.value = ApiState.Success(data)
        }
    }
    fun updateProduct(product: Product) = viewModelScope.launch {
        _updateProduct.value = ApiState.Loading
        repository.updateProduct(product).catch { e ->
            _updateProduct.value = ApiState.Failure(e)
        }.collect{ data ->
            _updateProduct.value = ApiState.Success(data)
        }
    }

}
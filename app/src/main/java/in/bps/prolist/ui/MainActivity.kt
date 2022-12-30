package `in`.bps.prolist.ui

import `in`.bps.prolist.R
import `in`.bps.prolist.adapter.ProductAdapter
import `in`.bps.prolist.api.ApiState
import `in`.bps.prolist.api.OnClickProduct
import `in`.bps.prolist.databinding.ActivityMainBinding
import `in`.bps.prolist.helper.CustomDialog
import `in`.bps.prolist.helper.CustomSnackBar
import `in`.bps.prolist.models.Product
import `in`.bps.prolist.viewmodel.ProductViewModel
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var progressDialog: CustomDialog? = null
    private val productViewModel: ProductViewModel by viewModels()
    private val _products = ArrayList<Product>()
    private lateinit var productAdapter : ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = CustomDialog(this)

        getProductList()

        // new Product
        binding.addProduct.setOnClickListener {
            val intent = Intent(this, UpsertProductActivity::class.java)
            intent.putExtra("documentId", "new")
            intent.putExtra("productName", "new")
            intent.putExtra("productNameEN", "new")
            intent.putExtra("productNameKN", "new")
            intent.putExtra("productPrice", 0)
            intent.putExtra("measuredIn", "new")
            intent.putExtra("category", "new")
            intent.putExtra("imageUrl", "new")
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val search : MenuItem? = menu?.findItem(R.id.nav_search)
        val searchView = search?.actionView as SearchView
        searchView.queryHint = "Search Item"

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }

        })

        return super.onCreateOptionsMenu(menu)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun filterList(newText: String?) {
        val filterList = ArrayList<Product>()
        for (item in _products) {
            if (item.productName_kn.lowercase(Locale.getDefault()).contains(newText!!.lowercase(Locale.getDefault()))) {
                filterList.add(item)
            }
        }
        if (filterList.isEmpty()) {
            CustomSnackBar.make(
                this@MainActivity,
                findViewById(android.R.id.content),
                "No data found",
                CustomSnackBar.WARNING,
                CustomSnackBar.SHORT
            )
        } else {
            productAdapter.setFilterData(filterList)
        }
    }

    private fun getProductList() {
        productViewModel.getAllProducts()
        lifecycleScope.launchWhenStarted {
            productViewModel.getAllProducts.collect {
                when(it) {
                    is ApiState.Loading -> {
                        progressDialog!!.show()
                    }
                    is ApiState.Success -> {
                        progressDialog!!.dismiss()
                        if (it.data.statusCode == 200) {
                             productAdapter = ProductAdapter(object : OnClickProduct {
                                override fun onClicked(item: Product) {
                                    val intent = Intent(this@MainActivity, ProductDetailsActivity::class.java)
                                    intent.putExtra("productNameEN", item.productName_en)
                                    intent.putExtra("productNameKN", item.productName_kn)
                                    intent.putExtra("productPrice", item.productPrice)
                                    intent.putExtra("measuredIn", item.measureIn)
                                    intent.putExtra("category", item.category)
                                    intent.putExtra("documentId", item._id)
                                    intent.putExtra("imageUrl", item.productImage)
                                    startActivity(intent)
                                }
                            })
                            _products.clear()
                            _products.addAll(it.data.products)
                            productAdapter.setFilterData(_products)
                            binding.productRV.apply {
                                adapter = productAdapter
                                setHasFixedSize(true)
                            }
                        }else {
                            CustomSnackBar.make(
                                this@MainActivity,
                                findViewById(android.R.id.content),
                                it.data.message,
                                CustomSnackBar.ERROR,
                                CustomSnackBar.LONG
                            )
                        }
                    }
                    is ApiState.Empty -> {
                    }
                    is ApiState.Failure -> {
                        progressDialog!!.dismiss()
                        CustomSnackBar.make(
                            this@MainActivity,
                            findViewById(android.R.id.content),
                            it.msg.message,
                            CustomSnackBar.ERROR,
                            CustomSnackBar.LONG
                        )
                    }
                }
            }
        }
    }

    override fun onRestart() {
        super.onRestart()
        getProductList()
    }
}
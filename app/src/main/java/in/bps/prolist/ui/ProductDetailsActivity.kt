package `in`.bps.prolist.ui

import `in`.bps.prolist.R
import `in`.bps.prolist.api.ApiState
import `in`.bps.prolist.databinding.ActivityProductDetailsBinding
import `in`.bps.prolist.helper.CustomDialog
import `in`.bps.prolist.helper.CustomSnackBar
import `in`.bps.prolist.viewmodel.ProductViewModel
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.ceil
import kotlin.math.floor

@AndroidEntryPoint
class ProductDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductDetailsBinding
    private var progressDialog: CustomDialog? = null
    private val productViewModel: ProductViewModel by viewModels()
    private var productNameEN = ""
    private var productNameKN = ""
    private var productPrice = 0
    private var measuredIn  = ""
    private var category = ""
    private var imageUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = CustomDialog(this)
        val documentId = intent.getStringExtra("documentId")

        productViewModel.getProductById(documentId!!)
        lifecycleScope.launchWhenStarted {
            productViewModel.getProductById.collect {
                when(it)  {
                    is ApiState.Loading -> {
                        progressDialog!!.show()
                    }
                    is ApiState.Success -> {
                        progressDialog!!.hide()
                        if (it.data.statusCode == 200) {
                            val product = it.data.product;
                            productNameEN = product.productName_en
                            productNameKN = product.productName_kn
                            productPrice = product.productPrice
                            measuredIn  = product.measureIn
                            category = product.category
                            imageUrl = product.productImage

                            binding.productName.text = "Product Name: $productNameKN / $productNameEN"
                            binding.productPrice.text = "Price: $productPrice Rs / $measuredIn"


                            binding.price50gm.text = "${ceil((productPrice * 0.05)).toInt()} Rs"
                            binding.price100gm.text = "${ceil((productPrice * 0.1)).toInt()} Rs"
                            binding.price250gm.text = "${ceil((productPrice * 0.25)).toInt()} Rs"
                            binding.price500gm.text = "${ceil((productPrice * 0.5)).toInt()} Rs"
                            binding.price750gm.text = "${ceil((productPrice * 0.75)).toInt()} Rs"
                            binding.price1kg.text = "$productPrice Rs"

                        }else {
                            CustomSnackBar.make(
                                this@ProductDetailsActivity,
                                findViewById(android.R.id.content),
                                it.data.message,
                                CustomSnackBar.ERROR,
                                CustomSnackBar.LONG
                            )
                        }
                    }
                    is ApiState.Failure -> {
                        progressDialog!!.hide()
                        CustomSnackBar.make(
                            this@ProductDetailsActivity,
                            findViewById(android.R.id.content),
                            it.msg.message,
                            CustomSnackBar.ERROR,
                            CustomSnackBar.LONG
                        )
                    }
                    is ApiState.Empty -> {
                        progressDialog!!.hide()
                    }
                }
            }
        }

        binding.deleteProduct.setOnClickListener {
            deleteProduct(documentId);
        }

        binding.updateProduct.setOnClickListener {
            val intent = Intent(this, UpsertProductActivity::class.java)
            intent.putExtra("productNameEN", productNameEN)
            intent.putExtra("productNameKN", productNameKN)
            intent.putExtra("productPrice", productPrice)
            intent.putExtra("measuredIn", measuredIn)
            intent.putExtra("documentId", documentId)
            intent.putExtra("category", category)
            intent.putExtra("imageUrl", imageUrl)
            startActivity(intent)
        }

        binding.getWeightButton.setOnClickListener {
            val amount: String = binding.inputAmountET.text.toString().trim()

            if (amount.isEmpty()) {
                binding.amountToWeightTV.text = "Please Enter The Amount"
            } else {
                val amountDouble: Double = amount.toDouble()
                val actualPrice: Double = productPrice.toDouble()
                val price: Double = amountDouble / actualPrice
                val weightInGram: Double = (price * 1000.0)
                binding.amountToWeightTV.text = "${floor(weightInGram)} gm for $amount Rs"
            }
        }
    }

    private fun deleteProduct(productId: String) {
        productViewModel.deleteProduct(productId)
        lifecycleScope.launchWhenStarted {
            productViewModel.deleteProduct.collect {
                when(it){
                    is ApiState.Loading -> {
                        progressDialog!!.show()
                    }
                    is ApiState.Success -> {
                        progressDialog!!.dismiss()
                        if (it.data.statusCode == 200){
                            CustomSnackBar.make(
                                this@ProductDetailsActivity,
                                findViewById(android.R.id.content),
                                it.data.message,
                                CustomSnackBar.SUCCESS,
                                CustomSnackBar.LONG
                            )
                            val intent = Intent(this@ProductDetailsActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()

                        }else {
                            CustomSnackBar.make(
                                this@ProductDetailsActivity,
                                findViewById(android.R.id.content),
                                it.data.message,
                                CustomSnackBar.ERROR,
                                CustomSnackBar.LONG
                            )
                        }
                    }
                    is ApiState.Failure -> {
                        progressDialog!!.dismiss()
                        CustomSnackBar.make(
                            this@ProductDetailsActivity,
                            findViewById(android.R.id.content),
                            it.msg.message,
                            CustomSnackBar.ERROR,
                            CustomSnackBar.LONG
                        )
                    }
                    is ApiState.Empty -> {
                        progressDialog!!.dismiss()
                    }
                }
            }
        }
    }
}
package `in`.bps.prolist.ui

import `in`.bps.prolist.R
import `in`.bps.prolist.api.ApiState
import `in`.bps.prolist.databinding.ActivityUpsertProductBinding
import `in`.bps.prolist.helper.CustomDialog
import `in`.bps.prolist.helper.CustomSnackBar
import `in`.bps.prolist.models.Product
import `in`.bps.prolist.viewmodel.ProductViewModel
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import java.io.FileNotFoundException

@AndroidEntryPoint
class UpsertProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpsertProductBinding
    private val SELECT_PHOTO: Int = 2000
    private lateinit var storage: FirebaseStorage
    private lateinit var downloadImageUrl: Uri
    private var isImageUploaded: Boolean = false
    private var progressDialog: CustomDialog? = null
    private val productViewModel: ProductViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpsertProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storage = Firebase.storage
        progressDialog = CustomDialog(this)

        val productNameENBundle = intent.getStringExtra("productNameEN")
        val productNameKNBundle = intent.getStringExtra("productNameKN")
        val productPriceBundle = intent.getIntExtra("productPrice", 0)
        val measuredInBundle = intent.getStringExtra("measuredIn")
        val documentId = intent.getStringExtra("documentId")
        val categoryBundle = intent.getStringExtra("category")
        val imageUrl = intent.getStringExtra("imageUrl")

        if (documentId!!.isNotBlank() || documentId.isNotEmpty()) {
            binding.productNameKN.setText(productNameKNBundle)
            binding.productNameEN.setText(productNameENBundle)
            binding.productPrice.setText(productPriceBundle.toString())
            binding.productMeasuredIn.setText(measuredInBundle)
            binding.productCategory.setText(categoryBundle)
            downloadImageUrl = Uri.parse(imageUrl)
            Picasso.get()
                .load(downloadImageUrl)
                .into(binding.productImage)
            isImageUploaded = true
        }

        binding.productImage.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, SELECT_PHOTO)
        }

        binding.updateProduct.setOnClickListener {

            val productNameKn = binding.productNameKN.text!!.trim().toString()
            val productNameEN = binding.productNameEN.text!!.trim().toString()
            val productPrice = binding.productPrice.text!!.trim().toString()
            val measure = binding.productMeasuredIn.text!!.trim().toString()
            val category = binding.productCategory.text!!.trim().toString()

            if (productNameKn.isNotEmpty() && productNameEN.isNotEmpty() && measure.isNotEmpty() && category.isNotEmpty() && isImageUploaded && productPrice.isNotEmpty()) {
                var product = Product (category = category, measureIn = measure, productImage = downloadImageUrl.toString(), productName_en = productNameEN, productName_kn = productNameKn , productPrice = productPrice.toInt(), id = documentId!! );
                // add new
                if (documentId.isBlank() || documentId.isEmpty()){
                    addNewProduct(product)
                } else {
                    // update existing one
                    updateProduct(product)
                }
            }

        }
    }

    private fun addNewProduct(product: Product) {
        productViewModel.addNewProduct(product)
        lifecycleScope.launchWhenStarted {
            productViewModel.addNewProduct.collect {
                when(it) {
                    is ApiState.Loading -> {
                        progressDialog!!.show()
                    }
                    is ApiState.Success -> {
                        progressDialog!!.hide()
                        if (it.data.statusCode == 200) {
                            CustomSnackBar.make(
                                this@UpsertProductActivity,
                                findViewById(android.R.id.content),
                                it.data.message,
                                CustomSnackBar.SUCCESS,
                                CustomSnackBar.LONG
                            )

                            val intent = Intent(this@UpsertProductActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()

                        }else {
                            CustomSnackBar.make(
                                this@UpsertProductActivity,
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
                            this@UpsertProductActivity,
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
    }

    private fun  updateProduct(product: Product) {
        productViewModel.updateProduct(product)
        lifecycleScope.launchWhenStarted {
            productViewModel.updateProduct.collect {
                when(it) {
                    is ApiState.Loading -> {
                        progressDialog!!.show()
                    }
                    is ApiState.Success -> {
                        progressDialog!!.hide()
                        if (it.data.statusCode == 200) {
                            CustomSnackBar.make(
                                this@UpsertProductActivity,
                                findViewById(android.R.id.content),
                                it.data.message,
                                CustomSnackBar.SUCCESS,
                                CustomSnackBar.LONG
                            )
                            val intent = Intent(this@UpsertProductActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()

                        }else {
                            CustomSnackBar.make(
                                this@UpsertProductActivity,
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
                            this@UpsertProductActivity,
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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SELECT_PHOTO -> if (resultCode == RESULT_OK) {
                try {
                    val imageUri: Uri? = data?.data
                    val sd = getFileName(applicationContext, imageUri!!)
                    val storageRef = storage.reference
                    val uploadTask = storageRef.child("productImg/$sd").putFile(imageUri)

                    uploadTask.addOnSuccessListener {

                        storageRef.child("productImg/$sd").downloadUrl.addOnSuccessListener {
                            downloadImageUrl = it
                            isImageUploaded = true
                            Picasso.get()
                                .load(it)
                                .into(binding.productImage)

                        }.addOnFailureListener {
                            Toast.makeText(this, "Filed to get image", Toast.LENGTH_SHORT).show()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
                    }

                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
    }

    @SuppressLint("Range")
    private fun getFileName(context: Context, uri: Uri): String? {
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor.use {
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        return cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    }
                }
            }
        }
        return uri.path?.lastIndexOf('/')?.let { uri.path?.substring(it) }
    }
}
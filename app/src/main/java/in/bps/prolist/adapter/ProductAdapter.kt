package `in`.bps.prolist.adapter

import `in`.bps.prolist.api.OnClickProduct
import `in`.bps.prolist.databinding.ProductItemListBinding
import `in`.bps.prolist.models.Product
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class ProductAdapter constructor(
    private val onClickProduct: OnClickProduct,
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(val binding: ProductItemListBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val diffCallBack = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return newItem == oldItem
        }
    }


    private val differ = AsyncListDiffer(this, diffCallBack)

    var productResult: List<Product>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }

    fun setFilterData(filterData: List<Product>) {
        productResult = filterData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(ProductItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
       val productItem = productResult[position]

        holder.binding.apply {
            productNameListItem.text =
                "${productItem.productName_kn} / ${productItem.productName_en}"
            productPriceListItem.text = "${productItem.productPrice}Rs / ${productItem.measureIn}"

            catalogCardView.setOnClickListener {
                onClickProduct.onClicked(productItem)
            }

            Picasso.get().load(productItem.productImage).into(productImageview)
        }

    }

    override fun getItemCount() = productResult.size
}
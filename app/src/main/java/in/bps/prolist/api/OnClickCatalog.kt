package `in`.bps.prolist.api

import `in`.bps.prolist.models.Product

interface OnClickProduct {
    fun onClicked(product: Product)
}
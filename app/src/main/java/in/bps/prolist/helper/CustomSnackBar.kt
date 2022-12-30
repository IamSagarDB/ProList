package `in`.bps.prolist.helper

import `in`.bps.prolist.R
import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar

object CustomSnackBar {
    const val SUCCESS = 0
    const val ERROR = 1
    const val DEFAULT = -1
    const val WARNING = 2
    const val LONG = 0
    const val SHORT = 1
    fun make(context: Context?, view: View?, message: String?, type: Int, duration: Int) {
        val color: Int = when (type) {
            SUCCESS -> ContextCompat.getColor(context!!, R.color.dark_green)
            ERROR -> ContextCompat.getColor(context!!, R.color.dark_red)
            WARNING -> ContextCompat.getColor(context!!, R.color.orange)
            else -> ContextCompat.getColor(context!!, R.color.grey_bg)
        }
        val snack = Snackbar.make(view!!, message!!, duration)
        snack.setBackgroundTint(color)
        snack.show()
    }
}

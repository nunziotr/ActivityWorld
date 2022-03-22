package com.example.activityworld.home.model

import android.os.Parcelable
import androidx.annotation.DrawableRes
import com.example.activityworld.home.constant.FieldType
import kotlinx.parcelize.Parcelize
import java.text.NumberFormat

/**
 *  Data class for playing fields
 */
@Parcelize
data class PlayingField(
    val id: Long = counter++,
    val type: FieldType,
    val price: Double,
    val description: String,
    val name: String = type.name.lowercase()+"_"+id,
): Parcelable {

    @DrawableRes
    val imageResourceId = type.drawableRes

    /**
     * Getter method for price.
     * Includes formatting.
     */
    fun getFormattedPrice(): String = NumberFormat.getCurrencyInstance().format(price)

    companion object {
        var counter: Long = 0L
    }
}


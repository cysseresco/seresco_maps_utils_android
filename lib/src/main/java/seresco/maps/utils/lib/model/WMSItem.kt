package seresco.maps.utils.lib.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WMSItem(
    var url: String,
    val description: String,
    var isSelected: Boolean = false
) : Parcelable {
}
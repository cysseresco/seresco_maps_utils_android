package seresco.maps.utils.lib.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WMSItem(
    val url: String,
    val description: String
) : Parcelable {
}
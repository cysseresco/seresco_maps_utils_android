package seresco.maps.utils.lib.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WMSLayer(
    val name: String,
    val items: List<WMSItem>
) : Parcelable {
}
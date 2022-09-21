package seresco.maps.utils.lib.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.os.bundleOf
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import seresco.maps.utils.lib.R
import kotlinx.android.synthetic.main.bottom_sheet_detail.*

class DetailBottomSheet(listener: DetailItemClicked): BottomSheetDialogFragment() {

    private var dismissWithAnimation = false
    private val mListener = listener
    private var currentKmlSettingStatus: KmlSettingType = KmlSettingType.UPDATE_BORDER

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.bottom_sheet_detail, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        setUpListeners()
    }

    private fun setUpViews() {

    }

    private fun setUpListeners() {
        ivEdit.setOnClickListener {
            rlUpdateName.visibility = View.GONE
            llColors.visibility = View.VISIBLE
            llTransparency.visibility = View.GONE
            currentKmlSettingStatus = KmlSettingType.UPDATE_BORDER
        }
        ivPaint.setOnClickListener {
            rlUpdateName.visibility = View.GONE
            llColors.visibility = View.VISIBLE
            llTransparency.visibility = View.GONE
            currentKmlSettingStatus = KmlSettingType.UPDATE_FILL
        }
        ivTransparency.setOnClickListener {
            rlUpdateName.visibility = View.GONE
            llColors.visibility = View.GONE
            llTransparency.visibility = View.VISIBLE
        }
        llRed.setOnClickListener {
            mListener.onDetailItemClicked(currentKmlSettingStatus, R.color.red)
            dismiss()
        }
        llYellow.setOnClickListener {
            mListener.onDetailItemClicked(currentKmlSettingStatus, R.color.yellow)
            dismiss()
        }
        llBlue.setOnClickListener {
            mListener.onDetailItemClicked(currentKmlSettingStatus, R.color.blue)
            dismiss()
        }
        llSkyblue.setOnClickListener {
            mListener.onDetailItemClicked(currentKmlSettingStatus, R.color.sky_blue)
            dismiss()
        }
        llGreen.setOnClickListener {
            mListener.onDetailItemClicked(currentKmlSettingStatus, R.color.green)
            dismiss()
        }
        llBrown.setOnClickListener {
            mListener.onDetailItemClicked(currentKmlSettingStatus, R.color.brown)
            dismiss()
        }
        bUpdate.setOnClickListener {
//            mListener.onDetailItemClicked(5, etName.text.toString())
            dismiss()
        }
        sbTransparency.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                val decimal = (p1 * 255)/100
                mListener.onDetailItemClicked(KmlSettingType.TRANSPARENCY, decimal)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) { }

            override fun onStopTrackingTouch(p0: SeekBar?) { }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dismissWithAnimation = arguments?.getBoolean(DetailBottomSheet.ARG_DISMISS_WITH_ANIMATION) ?: false
        (requireDialog() as BottomSheetDialog).dismissWithAnimation = dismissWithAnimation
    }

    companion object {
        const val TAG = "detailBottomSheet"
        const val ARG_DISMISS_WITH_ANIMATION = "dismiss_with_animation"
        fun newInstance(dismissWithAnimation: Boolean, listener: DetailItemClicked): DetailBottomSheet {
            val modalSimpleListSheet = DetailBottomSheet(listener)
            modalSimpleListSheet.arguments = bundleOf(ARG_DISMISS_WITH_ANIMATION to dismissWithAnimation)
            return modalSimpleListSheet
        }
    }

    interface DetailItemClicked {
        fun onDetailItemClicked(kmlSettingType: KmlSettingType, color: Int = 0)
    }
}

enum class KmlSettingType(val value: Int) {
    UPDATE_BORDER(1),
    UPDATE_FILL(2),
    TRANSPARENCY(3);

    companion object {
        fun valueOf(value: Int) = KmlSettingType.values().find { it.value == value }
    }
}


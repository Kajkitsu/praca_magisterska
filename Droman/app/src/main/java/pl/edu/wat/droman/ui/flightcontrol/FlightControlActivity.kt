package pl.edu.wat.droman.ui.flightcontrol

import android.annotation.SuppressLint
import android.graphics.Point
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import com.dji.mapkit.core.maps.DJIMap
import dji.keysdk.CameraKey
import dji.keysdk.KeyManager
import dji.ux.utils.DJIProductUtil
import dji.ux.widget.FPVWidget
import pl.edu.wat.droman.R
import pl.edu.wat.droman.databinding.ActivityFlightControlBinding
import pl.edu.wat.droman.ui.DjiApplication
import pl.edu.wat.droman.ui.FeedbackUtils


/**
 * Activity that shows all the UI elements together
 */
@Suppress("DEPRECATION")
class FlightControlActivity : AppCompatActivity() {
    private var isMapMini = true

    private var height = 0
    private var width = 0
    private var margin = 0
    private var deviceWidth = 0
    private var deviceHeight = 0


    private lateinit var username: String
    private lateinit var password: String
    private lateinit var ipAddress: String
    private lateinit var clientId: String
    private lateinit var binding: ActivityFlightControlBinding
    private lateinit var flightControlViewModel: FlightControlViewModel

    @SuppressLint("ResourceType", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFlightControlBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_flight_control)
        DjiApplication.eventBus.register(this)

        initCredentialsValue()
        DjiApplication.aircraftInstance?.let {
            flightControlViewModel = ViewModelProvider(
                this,
                FlightControlViewModelFactory(
                    username,
                    password,
                    ipAddress,
                    applicationContext,
                    clientId
                )
            )[FlightControlViewModel::class.java]
        }



        height = DensityUtil.dip2px(this, 100f)
        width = DensityUtil.dip2px(this, 150f)
        margin = DensityUtil.dip2px(this, 12f)
        val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val outPoint = Point()
        display.getRealSize(outPoint)
        deviceHeight = outPoint.y
        deviceWidth = outPoint.x

        val scrollView = findViewById<ScrollView>(R.id.scroll_view)
        val logTextView = findViewById<TextView>(R.id.log_text_view)
        FeedbackUtils.logLiveData.observe(this) {
            logTextView.append("\n" + it)
        }
        logTextView.doAfterTextChanged {
            scrollView.fullScroll(ScrollView.FOCUS_DOWN)
        }

        binding.mapWidget.initGoogleMap { map: DJIMap ->
            map.setOnMapClickListener { onViewClick(binding.mapWidget) }
        }
        binding.mapWidget.onCreate(savedInstanceState)
        binding.fpvWidget.setOnClickListener { onViewClick(binding.fpvWidget) }
        binding.secondaryFpvWidget.setOnClickListener { swapVideoSource() }
        binding.fpvWidget.setCameraIndexListener { _: Int, _: Int ->
            cameraWidgetKeyIndexUpdated(
                binding.fpvWidget.cameraKeyIndex,
                binding.fpvWidget.lensKeyIndex
            )
        }
        updateSecondaryVideoVisibility()
    }

    private fun initCredentialsValue() {
        val intent = intent
        username = intent.getStringExtra("username")!!
        password = intent.getStringExtra("password")!!
        ipAddress = intent.getStringExtra("ipAddress")!!
        clientId = intent.getStringExtra("clientId")!!
    }


    private fun onViewClick(view: View?) {
        if (view === binding.fpvWidget && !isMapMini) {
            resizeFPVWidget(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT,
                0,
                0
            )
            reorderCameraCapturePanel()
            val mapViewAnimation =
                ResizeAnimation(binding.mapWidget, deviceWidth, deviceHeight, width, height, margin)
            binding.mapWidget.startAnimation(mapViewAnimation)
            isMapMini = true
        } else if (view === binding.mapWidget && isMapMini) {
            hidePanels()
            resizeFPVWidget(width, height, margin, 12)
            reorderCameraCapturePanel()
            val mapViewAnimation =
                ResizeAnimation(binding.mapWidget, width, height, deviceWidth, deviceHeight, 0)
            binding.mapWidget.startAnimation(mapViewAnimation)
            isMapMini = false
        }
    }

    private fun resizeFPVWidget(width: Int, height: Int, margin: Int, fpvInsertPosition: Int) {
        val fpvParams = binding.fpvContainer.layoutParams as RelativeLayout.LayoutParams
        fpvParams.height = height
        fpvParams.width = width
        fpvParams.rightMargin = margin
        fpvParams.bottomMargin = margin
        if (isMapMini) {
            fpvParams.addRule(RelativeLayout.CENTER_IN_PARENT, 0)
            fpvParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE)
            fpvParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
        } else {
            fpvParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0)
            fpvParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0)
            fpvParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
        }
        binding.fpvContainer.layoutParams = fpvParams
        binding.rootView.removeView(binding.fpvContainer)
        binding.rootView.addView(binding.fpvContainer, fpvInsertPosition)
    }

    private fun reorderCameraCapturePanel() {
        val cameraCapturePanel = findViewById<View>(R.id.cameraCapturePanel)
        binding.rootView.removeView(cameraCapturePanel)
        binding.rootView.addView(cameraCapturePanel, if (isMapMini) 9 else 13)
    }

    private fun swapVideoSource() {
        if (binding.secondaryFpvWidget.videoSource == FPVWidget.VideoSource.SECONDARY) {
            binding.fpvWidget.videoSource = FPVWidget.VideoSource.SECONDARY
            binding.secondaryFpvWidget.videoSource = FPVWidget.VideoSource.PRIMARY
        } else {
            binding.fpvWidget.videoSource = FPVWidget.VideoSource.PRIMARY
            binding.secondaryFpvWidget.videoSource = FPVWidget.VideoSource.SECONDARY
        }
    }

    private fun cameraWidgetKeyIndexUpdated(keyIndex: Int, subKeyIndex: Int) {
        binding.cameraCapturePanel.updateKeyOnIndex(keyIndex, subKeyIndex)
        binding.cameraSettingExposurePanel.updateKeyOnIndex(keyIndex, subKeyIndex)
        binding.cameraSettingAdvancedPanel.updateKeyOnIndex(keyIndex, subKeyIndex)
        binding.cameraConfigIsoAndEiWidget.updateKeyOnIndex(keyIndex, subKeyIndex)
        binding.cameraConfigShutterWidget.updateKeyOnIndex(keyIndex, subKeyIndex)
        binding.cameraConfigApertureWidget.updateKeyOnIndex(keyIndex, subKeyIndex)
        binding.cameraConfigEvWidget.updateKeyOnIndex(keyIndex, subKeyIndex)
        binding.cameraConfigWbWidget.updateKeyOnIndex(keyIndex, subKeyIndex)
        binding.cameraConfigStorageWidget.updateKeyOnIndex(keyIndex, subKeyIndex)
        binding.cameraConfigSsdWidget.updateKeyOnIndex(keyIndex, subKeyIndex)
        binding.cameraLensControl.updateKeyOnIndex(keyIndex, subKeyIndex)
        binding.thermalPalletteWidget.updateKeyOnIndex(keyIndex, subKeyIndex)
        binding.fpvOverlayWidget.updateKeyOnIndex(keyIndex, subKeyIndex)
    }

    private fun updateSecondaryVideoVisibility() {
        if (binding.secondaryFpvWidget.videoSource == null || !DJIProductUtil.isSupportMultiCamera()) {
            binding.secondaryVideoView.visibility = View.GONE
        } else {
            binding.secondaryVideoView.visibility = View.VISIBLE
        }
    }

    private fun hidePanels() {
        //These panels appear based on keys from the drone itself.
        if (KeyManager.getInstance() != null) {
            KeyManager.getInstance().setValue(
                CameraKey.create(CameraKey.HISTOGRAM_ENABLED, binding.fpvWidget.cameraKeyIndex),
                false,
                null
            )
            KeyManager.getInstance().setValue(
                CameraKey.create(
                    CameraKey.COLOR_WAVEFORM_ENABLED,
                    binding.fpvWidget.cameraKeyIndex
                ), false, null
            )
        }

        //These panels have buttons that toggle them, so call the methods to make sure the button state is correct.
        binding.cameraCapturePanel.setAdvancedPanelVisibility(false)
        binding.cameraCapturePanel.setExposurePanelVisibility(false)

        //These panels don't have a button state, so we can just hide them.
        findViewById<View>(R.id.pre_flight_check_list).visibility = View.GONE
        findViewById<View>(R.id.rtk_panel).visibility = View.GONE
        //findViewById(R.id.simulator_panel).setVisibility(View.GONE);
        findViewById<View>(R.id.spotlight_panel).visibility = View.GONE
        findViewById<View>(R.id.speaker_panel).visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()

        // Hide both the navigation bar and the status bar.
        val decorView = window.decorView
        decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        binding.mapWidget.onResume()
    }

    override fun onPause() {
        binding.mapWidget.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        DjiApplication.eventBus.unregister(this)
        binding.mapWidget.onDestroy()
        flightControlViewModel.destroy()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapWidget.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapWidget.onLowMemory()
    }

    private inner class ResizeAnimation(
        private val mView: View?,
        private val mFromWidth: Int,
        private val mFromHeight: Int,
        private val mToWidth: Int,
        private val mToHeight: Int,
        private val mMargin: Int
    ) : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            val height = (mToHeight - mFromHeight) * interpolatedTime + mFromHeight
            val width = (mToWidth - mFromWidth) * interpolatedTime + mFromWidth
            val p = mView!!.layoutParams as RelativeLayout.LayoutParams
            p.height = height.toInt()
            p.width = width.toInt()
            p.rightMargin = mMargin
            p.bottomMargin = mMargin
            mView.requestLayout()
        }

        init {
            duration = 300
        }
    }
}
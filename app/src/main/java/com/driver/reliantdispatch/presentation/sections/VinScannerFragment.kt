package com.driver.reliantdispatch.presentation.sections

import android.Manifest
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.driver.reliantdispatch.R
import com.driver.reliantdispatch.databinding.FragmentVinScannerBinding
import com.driver.reliantdispatch.domain.ARG_2
import com.driver.reliantdispatch.presentation.MY_PERMISSIONS_CAMERA_REQ_CODE
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import kotlinx.android.synthetic.main.fragment_vin_scanner.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async


class VinScannerFragment: BaseSectionFragment() {

    var isFlashlightAvailable: Boolean = false
    var flashToggle: Boolean = false
    var mOrientationValues = arrayOf(
        ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE,
        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,
        ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE,
        ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mViewModel = ViewModelProviders.of(this).get(VinScannerViewModel::class.java)

        mBinding = FragmentVinScannerBinding.inflate(layoutInflater, container, false)
        with (mBinding as FragmentVinScannerBinding) {
            lifecycleOwner = this@VinScannerFragment
            fragment = this@VinScannerFragment
            viewModel = mViewModel as VinScannerViewModel
        }
        return mBinding.root
    }

    override fun onResume() {
        super.onResume()
        barcodeScanner.resume()
    }

    override fun onPause() {
        super.onPause()
        barcodeScanner.pause()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        isFlashlightAvailable = context?.packageManager?.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH) ?: false
        if (savedInstanceState == null) {
            setOrientation(0)
            checkCameraPermissions()
        }

        barcodeScanner.decodeContinuous(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult) {
                barcodeScanner.pause()
                result.text?.let {
                    GlobalScope.async(Dispatchers.IO) {
                        val mp = MediaPlayer.create(activity, R.raw.beep1)
                        mp.start()
                    }

                    val vin = it.toUpperCase().replace("[IOQ]".toRegex(), "")
                    if (vin.length != 17) {
                        val builder = AlertDialog.Builder(context!!)
                        builder.setTitle(R.string.dialog_title_error).setMessage(R.string.error_vin)
                            .setPositiveButton(
                                android.R.string.ok
                            ) { dialog, _ ->
                                dialog.cancel()
                                barcodeScanner.resume()
                            }
                        val dialog = builder.create()
                        dialog.show()
                    } else activity?.let{ activity ->
                        val args = Bundle()
                        args.putString(ARG_2, vin)
                        Navigation.findNavController(activity, R.id.nav_host_fragment_main).navigate(R.id.nav_vehicle_info, args)
                    }
                }
            }

            override fun possibleResultPoints(resultPoints: List<ResultPoint>) {}
        })

        barcodeScanner.setTorchListener(object : DecoratedBarcodeView.TorchListener{
            override fun onTorchOn() {
                flashToggle = true
            }

            override fun onTorchOff() {
                flashToggle = false
            }
        })
        barcodeScanner.setStatusText("")
        barcodeScanner.resume()
    }

    fun onFlashClick(){
        if (isFlashlightAvailable)
            if (!flashToggle){
                barcodeScanner.setTorchOn();
            } else {
                barcodeScanner.setTorchOff();
            }
    }

    fun onRotateLeft() = with (mViewModel as VinScannerViewModel) {
        mOrientationInd--
        if (mOrientationInd < 0) mOrientationInd = mOrientationValues.size - 1
        setOrientation(mOrientationInd)
    }

    fun onRotateRight() = with (mViewModel as VinScannerViewModel) {
        mOrientationInd++
        if (mOrientationInd > mOrientationValues.size-1) mOrientationInd = 0
        setOrientation(mOrientationInd)
    }

    private fun setOrientation(index: Int) {
        activity?.requestedOrientation = mOrientationValues[index]
    }

    private fun checkCameraPermissions(){
        if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.CAMERA),
                MY_PERMISSIONS_CAMERA_REQ_CODE
            )
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_CAMERA_REQ_CODE -> {
                if (grantResults.isNotEmpty()
                    && permissions.any{it == Manifest.permission.CAMERA}
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    barcodeScanner?.resume()
                }
                return
            }
        }
    }

}
package ir.jin724.videochat.webRTC.utils

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.os.Build
import timber.log.Timber

/**
 * AppRTCUtils provides helper functions for managing thread safety.
 */
class AppRTCUtils private constructor() {


    companion object {
        /**
         * Helper method which throws an exception  when an assertion has failed.
         */
        @JvmStatic
        fun assertIsTrue(condition: Boolean) {
            if (!condition) {
                throw AssertionError("Expected condition to be true")
            }
        }

        /**
         * Helper method for building a string of thread information.
         */
        @JvmStatic
        val threadInfo: String
            get() = ("@[name=" + Thread.currentThread().name + ", id=" + Thread.currentThread().id
                    + "]")

        /**
         * Information about the current build, taken from system properties.
         */
        @SuppressLint("BinaryOperationInTimber")
        fun logDeviceInfo(tag: String?) {
            Timber.d(
                "Android SDK: " + Build.VERSION.SDK_INT + ", "
                        + "Release: " + Build.VERSION.RELEASE + ", "
                        + "Brand: " + Build.BRAND + ", "
                        + "Device: " + Build.DEVICE + ", "
                        + "Id: " + Build.ID + ", "
                        + "Hardware: " + Build.HARDWARE + ", "
                        + "Manufacturer: " + Build.MANUFACTURER + ", "
                        + "Model: " + Build.MODEL + ", "
                        + "Product: " + Build.PRODUCT
            )
        }

        fun requestEnableBluetooth(){
            /*if (bluetoothAdapter?.isEnabled == false) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            }*/
        }
    }
}
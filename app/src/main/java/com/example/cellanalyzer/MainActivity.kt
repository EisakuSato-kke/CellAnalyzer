package com.example.cellanalyzer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.CellInfo
import android.telephony.CellInfoLte
import android.telephony.TelephonyManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {

    private lateinit var textView: TextView
    private val PERMISSION_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView = findViewById(R.id.textView)

        // 権限の確認
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 権限のリクエスト
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_PHONE_STATE
                ),
                PERMISSION_REQUEST_CODE
            )
        } else {
            getNetworkInfo()
        }
    }

    private fun getNetworkInfo() {
        val telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val cellInfoList: List<CellInfo>? = telephonyManager.allCellInfo

            if (!cellInfoList.isNullOrEmpty()) {
                val info = StringBuilder()

                for (cellInfo in cellInfoList) {
                    if (cellInfo is CellInfoLte) {
                        val cellSignalStrengthLte = cellInfo.cellSignalStrength
                        val cellIdentityLte = cellInfo.cellIdentity

                        if (cellInfo.isRegistered) {
                            info.append("Connected Cell:\n")
                        } else {
                            info.append("Nearby Cell:\n")
                        }

                        val rssi = cellSignalStrengthLte.dbm
                        val rsrp = cellSignalStrengthLte.rsrp
                        val rsrq = cellSignalStrengthLte.rsrq
                        val band = cellIdentityLte.earfcn

                        info.append("RSSI: $rssi dBm\n")
                        info.append("RSRP: $rsrp dBm\n")
                        info.append("RSRQ: $rsrq dB\n")
                        info.append("Band: $band\n\n")
                    }
                }


                textView.text = info.toString()
            } else {
                textView.text = "No cell information available."
            }
        }
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getNetworkInfo()
            } else {
                textView.text = "Permission denied."
            }
        }
    }
}

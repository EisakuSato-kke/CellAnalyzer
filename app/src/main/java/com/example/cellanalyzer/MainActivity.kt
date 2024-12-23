package com.example.cellanalyzer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.*
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
            val cellInfoList = telephonyManager.allCellInfo

            if (!cellInfoList.isNullOrEmpty()) {
                val info = StringBuilder()

                for (cellInfo in cellInfoList) {
                    when (cellInfo) {
                        is CellInfoLte -> {
                            val cellSignalStrengthLte = cellInfo.cellSignalStrength
                            val cellIdentityLte = cellInfo.cellIdentity

                            if (cellInfo.isRegistered) {
                                info.append("Connected LTE Cell:\n")
                            } else {
                                info.append("Nearby LTE Cell:\n")
                            }

                            val rssi = cellSignalStrengthLte.dbm
                            val rsrp = cellSignalStrengthLte.rsrp
                            val rsrq = cellSignalStrengthLte.rsrq
                            val band = cellIdentityLte.earfcn

                            info.append("LTE: RSSI: $rssi dBm\n")
                            info.append("LTE: RSRP: $rsrp dBm\n")
                            info.append("LTE: RSRQ: $rsrq dB\n")
                            info.append("LTE: EARFCN: $band\n\n")
                        }
                        is CellInfoNr -> {
                            val cellSignalStrengthNr = cellInfo.cellSignalStrength
                            val signalStrengthNr = cellInfo.cellSignalStrength as CellSignalStrengthNr
                            val cellIdentityNr = cellInfo.cellIdentity as CellIdentityNr

                            if (cellInfo.isRegistered) {
                                info.append("Connected 5G NR Cell:\n")
                            } else {
                                info.append("Nearby 5G NR Cell:\n")
                            }

                            val ssRsrp = signalStrengthNr.ssRsrp
                            val ssRsrq = signalStrengthNr.ssRsrq
                            val ssSinr = signalStrengthNr.ssSinr
                            val nrarfcn = cellIdentityNr.nrarfcn

                            info.append("NR: RSRP: $ssRsrp dBm\n")
                            info.append("NR: RSRQ: $ssRsrq dB\n")
                            info.append("NR: SINR: $ssSinr dB\n")
                            info.append("NR: ARFCN: $nrarfcn\n\n")
                        }
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

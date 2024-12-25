package com.example.cellanalyzer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import android.widget.Button

fun Int.intMax2NA(): String {
    return if (this == Integer.MAX_VALUE) "N/A" else this.toString()
}

class MainActivity : AppCompatActivity() {

    private lateinit var textView: TextView
    private val PERMISSION_REQUEST_CODE = 100
    private lateinit var updateButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView = findViewById(R.id.textView)
        updateButton = findViewById(R.id.updateButton)

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
            setupButton()
        }
    }

    private fun setupButton() {
        updateButton.setOnClickListener {
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

                for (cellInfo in cellInfoList) when (cellInfo) {
                    is CellInfoLte -> {
                        val cellSignalStrengthLte = cellInfo.cellSignalStrength
                        val cellIdentityLte = cellInfo.cellIdentity

                        if (cellInfo.isRegistered) {
                            info.append("Connected LTE Cell:\n")
                        } else {
                            info.append("Nearby LTE Cell:\n")
                        }

                        // cellSignalStrengthLte
                        val rssi = cellSignalStrengthLte.dbm.intMax2NA()
                        val rsrp = cellSignalStrengthLte.rsrp.intMax2NA()
                        val rsrq = cellSignalStrengthLte.rsrq.intMax2NA()
                        val cqi = cellSignalStrengthLte.cqi.intMax2NA()
                        val snr = cellSignalStrengthLte.rssnr.intMax2NA()


                        // cellIdentityLte
                        val earfcn = cellIdentityLte.earfcn.intMax2NA()
                        val band = (cellIdentityLte.bands).joinToString(",")
                        val bandwidth = cellIdentityLte.bandwidth.intMax2NA()
                        val NWoperator = cellIdentityLte.mobileNetworkOperator
                        val mcc = cellIdentityLte.mccString // Mobile Country Code
                        val mnc = cellIdentityLte.mncString // Mobile Network Code

                        info.append("RSSI: $rssi dBm\n")
                        info.append("RSRP: $rsrp dBm\n")
                        info.append("RSRQ: $rsrq dB\n")
                        info.append("CQI: $cqi \n")
                        info.append("RS-SNR: $snr dB\n")
                        info.append("EARFCN: $earfcn\n")
                        info.append("BAND: $band\n")
                        info.append("BANDWIDTH: $bandwidth\n")
                        info.append("NetworkOperator: $NWoperator\n\n")
                    }
                    is CellInfoNr -> {
                        val signalStrengthNr = cellInfo.cellSignalStrength as CellSignalStrengthNr
                        val cellIdentityNr = cellInfo.cellIdentity as CellIdentityNr

                        if (cellInfo.isRegistered) {
                            info.append("Connected 5G NR Cell:\n")
                        } else {
                            info.append("Nearby 5G NR Cell:\n")
                        }
                        // signalStrengthNr
                        val ssRsrp = signalStrengthNr.ssRsrp.intMax2NA()
                        val ssRsrq = signalStrengthNr.ssRsrq.intMax2NA()
                        val ssSinr = signalStrengthNr.ssSinr.intMax2NA()
                        val cqi = (signalStrengthNr.csiCqiReport).joinToString(",")
                        val csiRsrp = signalStrengthNr.csiRsrp.intMax2NA()
                        val csiRsrq = signalStrengthNr.csiRsrq.intMax2NA()
                        val csiSinr = signalStrengthNr.csiSinr.intMax2NA()

                        // cellIdentityNr
                        val nrarfcn = cellIdentityNr.nrarfcn.intMax2NA()
                        val bands = (cellIdentityNr.bands).joinToString(",")
                        val mcc = cellIdentityNr.mccString // Mobile Country Code
                        val mnc = cellIdentityNr.mncString // Mobile Network Code

                        info.append("SS-RSRP: $ssRsrp dBm\n")
                        info.append("SS-RSRQ: $ssRsrq dB\n")
                        info.append("SS-SINR: $ssSinr dB\n")
                        info.append("CQI: $cqi \n")
                        info.append("CSI-RSRP: $csiRsrp dB\n")
                        info.append("CSI-RSRQ: $csiRsrq dB\n")
                        info.append("CSI-Sinr: $csiSinr dB\n")
                        info.append("ARFCN: $nrarfcn\n")
                        info.append("BAND: $bands\n\n")
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

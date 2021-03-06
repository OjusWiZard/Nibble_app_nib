package com.example.ncscommunity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_attendance.*
import kotlinx.android.synthetic.main.activity_schedule.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.text.SimpleDateFormat
import java.util.*

class attendance : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance)

        val today  = Calendar.getInstance()
        val date = SimpleDateFormat("MMMM d,Y").format(today.time)
        attendance_date.text = date

        scan_btn.setOnClickListener {
            val scanner =  IntentIntegrator(this)
            scanner.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            scanner.setPrompt("Scan NCS barcode")
            scanner.initiateScan()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode== Activity.RESULT_OK){
            val result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data)
            if(result != null) {
                if(result.contents == null){
                    Toast.makeText(this,"Cancelled",Toast.LENGTH_SHORT).show()
                }
                else {
                    //Toast.makeText(this,"Scanned :" + result.contents,Toast.LENGTH_SHORT).show()
                        val client = OkHttpClient().newBuilder()
                            .build()
                        val mediaType = MediaType.parse("text/plain")
                        val body = RequestBody.create(mediaType, "")
                        val request = Request.Builder()
                            .url("https://ojuswi.pythonanywhere.com/Attend/" + result.contents + "/")
                            .method("POST", body)
                            .addHeader(
                                "Authorization",
                                "Token 484abdce98871be6ed3dc97d31d4b9da36aac4e0"
                            )
                            .build()
                    GlobalScope.launch (Dispatchers.Main) {
                         val response = withContext(Dispatchers.IO){ client.newCall(request).execute()}
                        println(response)
                    }
                }
            }
            else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }
}
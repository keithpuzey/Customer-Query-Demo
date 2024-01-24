package com.example.sapquery

import android.os.AsyncTask
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var jsonAdapter: JsonAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val editText = findViewById<EditText>(R.id.editText)
        val button = findViewById<Button>(R.id.button)
        val textView = findViewById<TextView>(R.id.textView)
        // Declare versionTextView and retrieve the version dynamically
        val versionTextView = findViewById<TextView>(R.id.versionTextView)
        val appVersion = packageManager.getPackageInfo(packageName, 0).applicationInfo.metaData.getString("appVersion")

        // Set the version dynamically
        versionTextView.text = "Version $appVersion"

        button.setOnClickListener {
            val details = editText.text.toString()
            if (details.isNotEmpty()) {

                val apiUrl = "https://registrationservice333780.mock.blazemeter.com?details=$details"
                MyAsyncTask(textView).execute(apiUrl)
            }
        }
    }

    private inner class MyAsyncTask(private val textView: TextView) :
        AsyncTask<String, Void, JSONObject>() {

        override fun doInBackground(vararg params: String): JSONObject? {
            val apiUrl = params[0]
            val url = URL(apiUrl)
            val urlConnection = url.openConnection() as HttpURLConnection

            try {
                val reader = BufferedReader(InputStreamReader(urlConnection.inputStream))
                val response = StringBuilder()
                var line: String?

                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }

                return JSONObject(response.toString())
            } finally {
                urlConnection.disconnect()
            }
        }

        override fun onPostExecute(result: JSONObject?) {
            result?.let {
                val jsonList = mutableListOf<JsonItem>()

                val keys = it.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    val value = it.getString(key)
                    jsonList.add(JsonItem(key, value))
                }

                jsonAdapter = JsonAdapter(jsonList)
                recyclerView.adapter = jsonAdapter

                // Display the result in textView
                textView.text = result.toString()
            }
        }
    }
}

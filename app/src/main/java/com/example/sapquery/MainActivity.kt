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

        // Access the TextView
        val versionTextView = findViewById<TextView>(R.id.versionTextView)

        // Get the version string from resources
        val version = getString(R.string.app_version)

        // Set the version to the TextView
        versionTextView.text = "Version $version"

        button.setOnClickListener {
            val details = editText.text.toString()
            if (details.isNotEmpty()) {

                val apiUrl = "https://registrationservice333780.mock.blazemeter.com?details=$details"
                MyAsyncTask(textView).execute(apiUrl)
                // Clear the EditText after the button is pressed
                editText.text.clear()
            }
        }
    }

    private inner class MyAsyncTask(private val textView: TextView) :
        AsyncTask<String, Void, JSONObject?>() {

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

                return try {
                    JSONObject(response.toString())
                } catch (e: Exception) {
                    // Handle the case where the response is not a JSON object
                    null
                }
            } finally {
                urlConnection.disconnect()
            }
        }

        override fun onPostExecute(result: JSONObject?) {
            if (result != null) {
                val jsonList = mutableListOf<JsonItem>()

                val keys = result.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    val value = result.getString(key)
                    jsonList.add(JsonItem(key, value))
                }

                jsonAdapter = JsonAdapter(jsonList)
                recyclerView.adapter = jsonAdapter

                // Display the result in textView
                textView.text = result.toString()
            } else {
                // Handle the case where the response is not a JSON object
                textView.text = "Error: Non-JSON response received"
            }
        }
    }
}

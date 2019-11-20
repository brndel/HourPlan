package com.brndl.hourplan

import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_substitution.*
import org.jsoup.Jsoup
import java.lang.ref.WeakReference

/*
import org.json.JSONObject
import java.io.*
import java.lang.StringBuilder
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.URLEncoder
import java.net.URL
import java.net.HttpURLConnection.HTTP_OK
import javax.net.ssl.HttpsURLConnection
import com.google.gson.JsonSyntaxException
import com.google.gson.Gson
import java.security.KeyManagementException
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.cert.X509Certificate
*/

class SubstitutionFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_substitution, container, false)
    }

    val url = "https://webuntis.com"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //webView.webViewClient = WebViewClient()
        //webView.loadUrl(url)
        //val webViewSettings = webView.settings
        //webViewSettings.javaScriptEnabled = true

        buttonRequestData.setOnClickListener {
            doit(url, WeakReference(textViewResult)).execute()
        }


    }

    class doit(val url: String, val textRef: WeakReference<TextView>) :
        AsyncTask<String, String, String>() {

        var words: String = ""

        override fun doInBackground(vararg p0: String?): String {

            return try {

                val doc: org.jsoup.nodes.Document = Jsoup.connect(url).get()

                words = doc.text()
                words

            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }

        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            textRef.get()?.text = result
        }

    }
}


/*
    class RequestAsync(val textRef: WeakReference<TextView>) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String? {
            return try {

                //GET Request
                //return RequestHandler.sendGet("https://prodevsblog.com/android_get.php");

                // POST Request
                val postDataParams = JSONObject()
                postDataParams.put("values", "search:")

                RequestHandler().sendPost(
                    "https://mobile.webuntis.com/ms/schoolquery2",
                    postDataParams
                )
            } catch(e : java.lang.Exception) {
                null
            }
        }

        override fun onPostExecute(result: String?) {
            if(result != null){
                textRef.get()?.text = result
            }
        }

    }

}

class RequestHandler {
    @Throws(Exception::class)
    fun encodeParams(params: JSONObject): String {
        val result = StringBuilder()
        var first = true
        val itr = params.keys()
        while (itr.hasNext()) {
            val key = itr.next()
            val value = params.get(key)
            if (first)
                first = false
            else
                result.append("&")

            result.append(URLEncoder.encode(key, "UTF-8"))
            result.append("=")
            result.append(URLEncoder.encode(value.toString(), "UTF-8"))
        }
        return result.toString()
    }

    @Throws(IOException::class)
    fun sendGet(url: String): String {
        val obj = URL(url)
        val con = obj.openConnection() as HttpURLConnection
        con.requestMethod = "GET"
        val responseCode = con.responseCode
        println("Response Code :: $responseCode")
        return if (responseCode == HTTP_OK) { // connection ok
            val income = BufferedReader(InputStreamReader(con.inputStream))
            val response = StringBuffer()

            while (true) {
                val inputLine = income.readLine() ?: break
                response.append(inputLine)
            }
            income.close()
            response.toString()
        } else {
            ""
        }
    }

    @Throws(Exception::class)
    fun sendPost(r_url: String, postDataParams: JSONObject): String? {
        val url = URL(r_url)

        val conn = url.openConnection() as HttpURLConnection
        conn.readTimeout = 20000
        conn.connectTimeout = 20000
        conn.requestMethod = "POST"
        conn.doInput = true
        conn.doOutput = true

        val os = conn.outputStream
        val writer = BufferedWriter(OutputStreamWriter(os, "UTF-8"))
        writer.write(encodeParams(postDataParams))
        writer.flush()
        writer.close()
        os.close()

        val responseCode = conn.responseCode
        if (responseCode == HttpsURLConnection.HTTP_OK) {

            val income = BufferedReader(InputStreamReader(conn.inputStream))
            val sb = StringBuffer("")
            while (true) {
                val line = income.readLine() ?: break
                sb.append(line)
                break
            }
            income.close()
            return sb.toString()
        }
        return null
    }
}
*/
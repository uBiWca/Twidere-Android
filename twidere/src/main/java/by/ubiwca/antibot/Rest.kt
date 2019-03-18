package by.ubiwca.antibot

/**
 * Created by Пользователь on 12.03.2019.
 */
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import kotlin.concurrent.thread

/**
 * Created by Пользователь on 07.03.2019.
 */
class BotRest(val Url: String) {
   // val tempUrl = "https://blocktogether.org/show-blocks/SiJai3FyVmodO0XxkL2r-pezIK_oahHRwqv9I6U3.csv"
    fun getBlockList(): List<String> {
        val entryPoint = URL(Url)
        val myConnection = entryPoint.openConnection() as HttpsURLConnection
        var streamReader: BufferedReader
        var outputList: MutableList<String> = mutableListOf()

        val myThread = thread {
            try {
                if (myConnection.responseCode == 200) {
                    streamReader = BufferedReader(InputStreamReader(myConnection.inputStream, "UTF-8"))
                    while (streamReader.readLine() != null) outputList.add(streamReader.readLine())
                }
            } catch (e: Exception) {
                Log.d("getBlockList", e.message)
            } finally {
                myConnection.disconnect()
            }
        }

        while (myThread.isAlive) {
        }
        if (outputList.size > 0) return outputList else {
            outputList.add("Empty response!")
            return outputList
        }
    }


}
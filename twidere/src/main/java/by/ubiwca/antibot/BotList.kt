package by.ubiwca.antibot

import android.content.Context
import android.util.Log
import android.widget.Toast
import java.io.*

/**
 * Created by Пользователь on 12.03.2019.
 */
public class BotList(context : Context) {
    //var list: List<String> = Rest("https://blocktogether.org/show-blocks/SiJai3FyVmodO0XxkL2r-pezIK_oahHRwqv9I6U3.csv").getBlockList()
    lateinit var list :List<String>
    fun isBot( id:String) = list.contains(id)
    init {

        try {
              val  botFile = FileInputStream("botList.dat")
            if (botFile.available()>0) {
                var reader = BufferedReader(InputStreamReader(botFile))
                list = reader.readLines()
                Log.d("in botList", "${list.size.toString()} lines read")
            }
        }
        catch(e: FileNotFoundException) {
            val botFile = File("botList.dat")
            try {
                botFile.createNewFile()
            }
            catch (e:Exception) {
               // Toast.makeText(context, context.resources.getString(org.mariotaku.twidere.R.string.file_error), Toast.LENGTH_LONG).show()
                Log.d("in botList", e.message)
            }
        }



    }


}
package by.ubiwca.antibot

import android.util.Log
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

/**
 * Created by Пользователь on 18.03.2019.
 */
class BotListIO {
    private var botList:List<String>? = null
    fun getBotList():List<String>? {
        return botList
    }
    fun setBotList(list:List<String>?) {
        botList = list
    }
    fun loadListFromFile() {
        try {
            val fis = FileInputStream("botlist.dat")
            val ois = ObjectInputStream(fis)
            val list = ois.readObject() as BotListSerializable
            setBotList(list.botList)
        }
        catch (e:Exception) {
            Log.d("BotListIO", e.message)
        }
    }
    fun saveListToFile() {
        try {
            val fos = FileOutputStream("botlist.dat")
            val oos = ObjectOutputStream(fos)
            val oo = BotListSerializable()
            oo.botList = getBotList()
            oos.writeObject(oo)
            oos.flush()
            oos.close()
        }
        catch (e:Exception) {
            Log.d("BotListIO", e.message)
        }

    }
}
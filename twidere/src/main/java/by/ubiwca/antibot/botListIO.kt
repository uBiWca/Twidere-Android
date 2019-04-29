package by.ubiwca.antibot

import android.arch.persistence.room.Room
import android.content.Context
import android.util.Log
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

/**
 * Created by Пользователь on 18.03.2019.
 */
class BotListIO (val context:Context){

    val db = Room.databaseBuilder(context, BotDatabase::class.java, "botDatabase").build()
    fun updateDB(botList:List<String>) {
        Log.d("BotListIO", " Updating DB with ${botList.size} records")
        db.getBotDao().deleteAll()
        for (botId in botList) db.getBotDao().insertBot(Bot(botId))
    }
    fun isBot(id:String) : Boolean {
        Log.d("BotListIO", " Checking ID $id")
        val resultList = db.getBotDao().getBotById(id)
        if ((resultList.isEmpty())||(resultList==null)) return false else return true
    }
    fun getFullList() : List<Bot> {
        return db.getBotDao().getAllBots()
    }
    fun recordsCount(): Int {
        val list = db.getBotDao().getAllBots()
        return list.size
    }
    fun clear() {
        db.close()
    }

}
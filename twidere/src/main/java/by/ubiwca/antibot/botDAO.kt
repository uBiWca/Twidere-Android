package by.ubiwca.antibot

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

/**
 * Created by Пользователь on 19.03.2019.
 */
@Dao
        interface BotDao {
    @Query("SELECT * FROM bots WHERE id = :id")
    fun getBotById(id:String) : List<Bot>
    @Insert
    fun insertBot(bot: Bot)
    @Query("DELETE FROM bots")
    fun deleteAll()
    @Query("SELECT * FROM bots")
    fun getAllBots() : List<Bot>


}

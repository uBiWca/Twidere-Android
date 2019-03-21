package by.ubiwca.antibot

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

/**
 * Created by Пользователь on 19.03.2019.
 */
@Database (entities = arrayOf(Bot::class), version = 1)
        public abstract class BotDatabase : RoomDatabase() {
        public abstract fun getBotDao() : BotDao

}
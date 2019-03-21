package by.ubiwca.antibot

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Created by Пользователь on 19.03.2019.
 */
@Entity(tableName = "bots")
data class Bot (@PrimaryKey val id:String)
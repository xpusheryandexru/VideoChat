package com.example.firebasertcandroid.components

import android.content.Context
import androidx.lifecycle.viewModelScope
import androidx.room.*
import androidx.work.CoroutineWorker
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import java.util.*
import javax.inject.Inject

@InstallIn(SingletonComponent::class)
@Module
class RoomDb @Inject constructor( appContext: Context)
{

    val db:AppDatabase = Room.databaseBuilder(
       appContext,
       AppDatabase::class.java,
        "app_log"
   ).build()


    fun getUsers(): Flow<List<EntryLog>> {
        return db.userDao().getAll()
    }

    suspend fun insertAll(users: List<EntryLog>)
    {
        users.forEach {
            db.userDao().insertAll(it)
        }
    }

    suspend fun log(s: String) {

            db.userDao().insertAll(
                EntryLog(s,)
            )

    }

}

@Entity
data class EntryLog
    (
    @PrimaryKey(autoGenerate = true) val uid: Int=0,
    @ColumnInfo(name = "timestamp") val date: String,
    @ColumnInfo(name = "text") val text: String?

)
{
    constructor(text: String) : this(0, Date().toString(), text)

    override fun toString(): String {
        return "$text \n id: $uid date: $date"
    }
}

@Dao
interface EntryLogDao
{
    @Query("SELECT * FROM EntryLog order by uid desc")
    fun getAll(): Flow<List<EntryLog>>

    @Insert
    suspend fun insertAll(vararg entityLog: EntryLog)

    @Delete
    fun delete(entityLog: EntryLog)
}

@Database(entities = [EntryLog::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase()
{
    abstract fun userDao(): EntryLogDao
}
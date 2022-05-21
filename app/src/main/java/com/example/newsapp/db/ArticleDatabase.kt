package com.example.newsapp.db

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.newsapp.models.Article

@Database(
    entities = [Article::class],
    version = 2,
    autoMigrations = [
        AutoMigration (from = 1, to = 2)
    ],
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class ArticleDatabase : RoomDatabase() {

    abstract fun getArticleDao(): ArticleDao

    companion object {
        //other threads can see when one thread changes this instance
        @Volatile
        private var instance: ArticleDatabase? = null
        //synchronize setting the instance (singleton)
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK){
            instance ?: createDatabase(context).also { instance = it}
        }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                ArticleDatabase::class.java,
                "article_db.db"
            )
            .addMigrations(Migration_1_2)
            .build()

        private val Migration_1_2 = object: Migration(1,2){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE articles ADD COLUMN userId TEXT")
            }
        }
    }
}
import android.content.Context
import androidx.room.Room
import com.example.myapplication.data.database.AppDatabase

object DatabaseClient {

    private var instance: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {

        if (instance == null) {
            synchronized(AppDatabase::class.java) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java, "app_database"
                    )
                        .fallbackToDestructiveMigration(true)
                        .build()
                }
            }
        }
        return instance!!
    }
}
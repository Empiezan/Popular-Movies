package android.example.popularmovies;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {MovieListing.class}, version = 1, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class MovieDatabase extends RoomDatabase {

    private static MovieDatabase shared;
    private static final Object LOCK = new Object();
    private static final String LOG_TAG = MovieDatabase.class.getSimpleName();
    private static final String DATABASE_NAME = "movies";

    public static MovieDatabase getInstance(Context context) {
        if (shared == null) {
            synchronized (LOCK) {
                Log.d(LOG_TAG, "Creating new database instance");
                shared = Room.databaseBuilder(context.getApplicationContext(),
                        MovieDatabase.class, MovieDatabase.DATABASE_NAME).build();
            }
        }
        Log.d(LOG_TAG, "Getting the database instance");
        return shared;
    }

    public abstract MovieDao movieDao();

}

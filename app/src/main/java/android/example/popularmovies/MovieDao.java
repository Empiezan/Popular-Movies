package android.example.popularmovies;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM movies ORDER BY dateAdded")
    LiveData<List<MovieListing>> loadFavorites();

    @Query("SELECT * FROM movies WHERE id = :id")
    MovieListing loadMovie(int id);

    @Insert
    void insertMovie(MovieListing listing);

    @Delete
    void deleteMovie(MovieListing listing);

}

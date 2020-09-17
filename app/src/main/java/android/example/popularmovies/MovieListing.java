package android.example.popularmovies;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;

import java.util.Date;

@Entity(tableName = "movies")
public class MovieListing {
    @PrimaryKey
    int id;
    String title;
    Date dateAdded;
    String posterPath;

    public MovieListing(int id, String title, Date dateAdded, String posterPath) {
        this.id = id;
        this.title = title;
        this.dateAdded = dateAdded;
        this.posterPath = posterPath;
    }

}


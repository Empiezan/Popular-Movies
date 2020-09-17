package android.example.popularmovies;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainViewModel extends AndroidViewModel {

    private static final String api_key = "f0fdbb856988d78cf65e60ed30a865d0";
    private static final String baseURL = "https://api.themoviedb.org/3/";

    private MutableLiveData<List<MovieListing>> popularMovies;
    private MutableLiveData<List<MovieListing>> highestRatedMovies;
    private LiveData<List<MovieListing>> favoriteMovies = new MutableLiveData<>();

    public enum MovieTypes {
        popular,
        highestRated,
        favorites
    }

    private MovieTypes movieType = MovieTypes.popular;

    private MutableLiveData<List<MovieListing>> currentMovies = new MutableLiveData<>();

    public LiveData<List<MovieListing>> getMovies() {
        if (currentMovies == null) {
            currentMovies.setValue(new LinkedList<>());
        }

        if (popularMovies == null) {
            popularMovies = new MutableLiveData<>();
            loadPopularMovies();
        }

        return currentMovies;
    }

    public LiveData<List<MovieListing>> getFavoriteMovies() {
        if (favoriteMovies == null) {
            loadFavoriteMovies();
        }
        return favoriteMovies;
    }

    public MainViewModel(@NonNull Application application) {
        super(application);
        loadFavoriteMovies();
    }

    private void setupObservers() {
    }

    void loadPopularMovies() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MovieService service = retrofit.create(MovieService.class);
        Call<MovieResults> call = service.getPopularMovies(api_key);
        new MovieTask(MovieTypes.popular).execute(call);
    }

    void loadHighestRatedMovies() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MovieService service = retrofit.create(MovieService.class);
        Call<MovieResults> call = service.getHighestRatedMovies(api_key);
        new MovieTask(MovieTypes.highestRated).execute(call);
    }

    void loadFavoriteMovies() {
        MovieDatabase database = MovieDatabase.getInstance(getApplication());
        favoriteMovies = database.movieDao().loadFavorites();
    }

    public void setMovieType(MovieTypes type) {
        this.movieType = type;

        switch (movieType) {
            case popular:
                if (popularMovies == null) {
                    popularMovies = new MutableLiveData<>();
                    loadPopularMovies();
                } else { currentMovies.postValue(popularMovies.getValue() ); }
                break;
            case highestRated:
                if (highestRatedMovies == null) {
                    highestRatedMovies = new MutableLiveData<>();
                    loadHighestRatedMovies();
                } else { currentMovies.postValue(highestRatedMovies.getValue()); };
                break;
            case favorites:
                currentMovies.postValue(favoriteMovies.getValue());
        }
    }

    public MovieTypes getMovieType() {
        return this.movieType;
    }

    private class MovieTask extends AsyncTask<Call<MovieResults>, Void, List<MovieListing>> {

        private MovieTypes type;

        MovieTask(MovieTypes type) {
            this.type = type;
        }

        @Override
        protected List<MovieListing> doInBackground(Call<MovieResults>... call) {
            try {
                Response<MovieResults> response = call[0].execute();
                MovieResult[] movieResults = response.body().results;
                List<MovieListing> movieListings = new LinkedList<>();
                for (MovieResult movie : movieResults) {
                    MovieListing listing = new MovieListing(movie.id, movie.title,null, movie.poster_path);
                    movieListings.add(listing);
                }
                return movieListings;
            } catch (IOException error) {
                System.out.println(error.getStackTrace());
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<MovieListing> results) {
            switch (type) {
                case popular:
                    popularMovies.postValue(results);
                    break;
                case highestRated:
                    highestRatedMovies.postValue(results);
                     break;
            }
            currentMovies.postValue(results);
            super.onPostExecute(results);
        }
    }

}

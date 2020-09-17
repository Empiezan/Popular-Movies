package android.example.popularmovies;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailViewModel extends AndroidViewModel {

    private String api_key = "f0fdbb856988d78cf65e60ed30a865d0";
    private static final String baseUrl = "https://api.themoviedb.org/3/";

    private MovieService service = new Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MovieService.class);

    private MutableLiveData<MovieDetails> movieDetailLiveData;
    public LiveData<MovieDetails> getMovieDetails() {
        if (movieDetailLiveData == null) {
            movieDetailLiveData = new MutableLiveData<>();
            loadMovieDetails();
        }
        return movieDetailLiveData;
    }

    private MutableLiveData<MovieVideos> videosMutableLiveData;
    public LiveData<MovieVideos> getMovieVideos() {
        if (videosMutableLiveData == null) {
            videosMutableLiveData = new MutableLiveData<>();
            loadMovieTrailers();
        }
        return videosMutableLiveData;
    }

    private MutableLiveData<MovieReviews> reviewsMutableLiveData;
    public LiveData<MovieReviews> getMovieReviews() {
        if (reviewsMutableLiveData == null) {
            reviewsMutableLiveData = new MutableLiveData<>();
            loadReviews();
        }
        return reviewsMutableLiveData;
    }

    private MutableLiveData<Boolean> isFavorite;
    public LiveData<Boolean> getIsFavorite() {
        if (isFavorite == null) {
            isFavorite = new MutableLiveData<>();
            loadFavoriteStatus();
        }
        return isFavorite;
    }

    private final int movieId;
    private final String movieTitle;
    private final String posterPath;

    public DetailViewModel(@NonNull Application application, int movieId, String movieTitle, String posterPath) {
        super(application);
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.posterPath = posterPath;
    }

    public interface FavoriteButtonDelegate {
        void setFavorite(boolean isFavorite);
    }

    public void loadFavoriteStatus() {
        new QueryFavoriteTask().execute();
    }

    public void toggleFavorite() {
        if (isFavorite.getValue()) {
            new DeleteFavoriteTask().execute();
        } else {
            new AddFavoriteTask().execute();
        }
    }

    private void loadMovieDetails() {
        Call<MovieDetails> call = service.getMovieDetails(movieId, api_key);
        new DetailTask().execute(call);
    }

    private void loadMovieTrailers() {
        Call<MovieVideos> call = service.getMovieVideos(movieId, api_key);
        new VideoTask().execute(call);
    }

    private void loadReviews() {
        Call<MovieReviews> call = service.getReviews(movieId, api_key);
        new ReviewTask().execute(call);
    }

    private class AddFavoriteTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            MovieDatabase database = MovieDatabase.getInstance(getApplication());
            MovieListing listing = new MovieListing(movieId, movieTitle, new Date(), posterPath);
            database.movieDao().insertMovie(listing);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            isFavorite.postValue(true);
        }
    }

    private class DeleteFavoriteTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            MovieDatabase database = MovieDatabase.getInstance(getApplication());
            MovieListing listing = new MovieListing(movieId, movieTitle, new Date(), posterPath);
            database.movieDao().deleteMovie(listing);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            isFavorite.postValue(false);
        }
    }

    private class QueryFavoriteTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            MovieDatabase database = MovieDatabase.getInstance(getApplication());
            MovieListing listing = database.movieDao().loadMovie(movieId);
            boolean favorited = listing != null;
            return favorited;
        }

        @Override
        protected void onPostExecute(Boolean favorite) {
            super.onPostExecute(favorite);
            isFavorite.postValue(favorite);
        }
    }

    private class DetailTask extends AsyncTask<Call<MovieDetails>, Void, MovieDetails> {

        @Override
        protected MovieDetails doInBackground(Call<MovieDetails>... calls) {
            try {
                Response<MovieDetails> response = calls[0].execute();
                return response.body();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(MovieDetails movieDetails) {
            super.onPostExecute(movieDetails);
            movieDetailLiveData.postValue(movieDetails);
        }
    }

    private class VideoTask extends AsyncTask<Call<MovieVideos>, Void, MovieVideos> {

        @Override
        protected MovieVideos doInBackground(Call<MovieVideos>... calls) {
            try {
                Response<MovieVideos> response = calls[0].execute();
                return response.body();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(MovieVideos videos) {
            super.onPostExecute(videos);
            videosMutableLiveData.postValue(videos);
        }
    }

    private class ReviewTask extends AsyncTask<Call<MovieReviews>, Void, MovieReviews> {

        @Override
        protected MovieReviews doInBackground(Call<MovieReviews>... calls) {
            try {
                Response<MovieReviews> response = calls[0].execute();
                return response.body();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(MovieReviews reviews) {
            super.onPostExecute(reviews);
            reviewsMutableLiveData.postValue(reviews);
        }
    }


}

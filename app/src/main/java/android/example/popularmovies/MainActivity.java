package android.example.popularmovies;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieOnClickListener {

    private RecyclerView recyclerView;
    private MovieAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        layoutManager = new GridLayoutManager(this, 3 );
        adapter = new MovieAdapter(this);
        recyclerView = (RecyclerView) findViewById(R.id.movie_recycler_view);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        MainViewModel viewModel = new ViewModelProvider(this, new MainViewModelFactory(getApplication())).get(MainViewModel.class);
        viewModel.getMovies().observe(this, (movies) -> {
            adapter.setMovies(movies);
        });

        viewModel.getFavoriteMovies().observe(this, (movies) -> {
            if (viewModel.getMovieType() == MainViewModel.MovieTypes.favorites) {
                adapter.setMovies(movies);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(getString(R.string.channel_id), getString(R.string.channel_name), NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    @Override
    public void selectedMovie(MovieListing movie) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra(getString(R.string.movieIdExtra), movie.id);
        intent.putExtra(getString(R.string.movieTitleExtra), movie.title);
        intent.putExtra(getString(R.string.posterPathExtra), movie.posterPath);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sort, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        MainViewModel viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        if (id == R.id.popular) {
            viewModel.setMovieType(MainViewModel.MovieTypes.popular);
        }

        if (id == R.id.rating) {
            viewModel.setMovieType(MainViewModel.MovieTypes.highestRated);
        }

        if (id == R.id.showFavorites) {
            viewModel.setMovieType(MainViewModel.MovieTypes.favorites);
        }

        return super.onOptionsItemSelected(item);
    }






}
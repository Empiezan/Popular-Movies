package android.example.popularmovies;

import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.example.popularmovies.databinding.ActivityDetailBinding;
import android.net.Uri;
import android.os.Bundle;

import java.util.List;
import java.util.stream.Collectors;

public class DetailActivity extends AppCompatActivity implements DetailAdapter.TrailerOnClickListener, DetailAdapter.FavoriteOnClickListener {

    ActivityDetailBinding binding;
    private DetailAdapter detailAdapter;
    private LinearLayoutManager linearLayoutManager;

    private String imageURL = "https://image.tmdb.org/t/p/w342";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        linearLayoutManager = new LinearLayoutManager(this);

        detailAdapter = new DetailAdapter(this, this);
        binding.detailRecyclerView.setLayoutManager(linearLayoutManager);
        binding.detailRecyclerView.setAdapter(detailAdapter);

        Intent intent = getIntent();

        int movieId = intent.getIntExtra(getApplication().getString(R.string.movieIdExtra), -1);
        if (movieId == -1) { return; }

        String movieTitle = intent.getStringExtra(getApplication().getString(R.string.titleExtra));
        String posterPath = intent.getStringExtra(getApplication().getString(R.string.posterPathExtra));

        DetailViewModelFactory detailViewModelFactory = new DetailViewModelFactory(getApplication(), movieId, movieTitle, posterPath);
        final DetailViewModel viewModel = new ViewModelProvider(this, detailViewModelFactory).get(DetailViewModel.class);
        viewModel.getMovieDetails().observe(this, (details) -> {
            if (details == null) { return; }
            detailAdapter.setDetails(details);
            detailAdapter.notifyDataSetChanged();
        });

        viewModel.getMovieVideos().observe(this, (videos) -> {
            if (videos == null) { return; }
            List<VideoResult> trailers = videos.results.stream()
                    .filter((video) -> video.type.equals(getString(R.string.trailerVideoType)))
                    .collect(Collectors.toList());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    detailAdapter.setTrailers(trailers);
                    detailAdapter.notifyDataSetChanged();
                }
            });
        });

        viewModel.getMovieReviews().observe(this, (reviews) -> {
            if (reviews == null) { return; }

            detailAdapter.setReviews(reviews.results);
            detailAdapter.notifyDataSetChanged();
        });

        viewModel.getIsFavorite().observe(this, (isFavorite) -> {
            detailAdapter.setFavorite(isFavorite);
            detailAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void selectedTrailer(String trailerURL) {
        Uri uri = Uri.parse(trailerURL);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    @Override
    public void favorited() {
        Intent intent = getIntent();
        int movieId = intent.getIntExtra(getApplication().getString(R.string.movieIdExtra), -1);
        if (movieId == -1) { return; }

        String movieTitle = intent.getStringExtra(getApplication().getString(R.string.titleExtra));
        String posterPath = intent.getStringExtra(getApplication().getString(R.string.posterPathExtra));

        DetailViewModelFactory detailViewModelFactory = new DetailViewModelFactory(getApplication(), movieId, movieTitle, posterPath);
        final DetailViewModel viewModel = new ViewModelProvider(this, detailViewModelFactory).get(DetailViewModel.class);
        viewModel.toggleFavorite();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getString(R.string.channel_id))
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Favorited Movie")
                .setContentText("Successfully saved " + movieTitle);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(12, builder.build());
    }

}
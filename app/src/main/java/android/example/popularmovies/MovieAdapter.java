package android.example.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    List<MovieListing> movies = new LinkedList<>();
    MovieOnClickListener listener;
    private String imageURL = "https://image.tmdb.org/t/p/w342";

    MovieAdapter(MovieOnClickListener listener) {
        super();
        this.listener = listener;
    }

    void setMovies(List<MovieListing> movies) {
        this.movies = movies;

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.movie, parent, false);

        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        MovieListing movie = movies.get(position);

        Picasso.get().load(imageURL + movie.posterPath).into(holder.imageView);
        holder.textView.setText(movie.title);
    }

    @Override
    public int getItemCount() {
        if (movies == null) { return 0; }
        return movies.size();
    }

    public interface MovieOnClickListener {
        void selectedMovie(MovieListing movie);
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView;
        TextView textView;

        MovieViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.movie_image);
            textView = view.findViewById(R.id.movie_title);
            imageView.setOnClickListener(this);
            textView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int index = getAdapterPosition();
            MovieListing movie = movies.get(index);
            listener.selectedMovie(movie);
        }
    }

}
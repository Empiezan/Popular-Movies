package android.example.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.List;

public class DetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private TrailerOnClickListener listener;
    private FavoriteOnClickListener favoriteOnClickListener;

    private String imageURL = "https://image.tmdb.org/t/p/w342";

    private MovieDetails details;
    private List<VideoResult> trailers = new LinkedList<>();
    private List<Review> reviews = new LinkedList<>();
    private boolean isFavorite = false;

    public DetailAdapter(TrailerOnClickListener listener, FavoriteOnClickListener favoriteOnClickListener) {
        this.listener = listener;
        this.favoriteOnClickListener = favoriteOnClickListener;
    }

    public interface TrailerOnClickListener {
        void selectedTrailer(String trailerURL);
    }

    public interface FavoriteOnClickListener {
        void favorited();
    }

    public void setTrailers(List<VideoResult> trailers) {
        this.trailers = trailers;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public void setDetails(MovieDetails details) { this.details = details; }

    public void setFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        if (viewType == title) {
            View view = inflater.inflate(R.layout.title, parent, false);
            return new GeneralViewHolder(view);
        } else if (viewType == detail) {
            View view = inflater.inflate(R.layout.details, parent, false);
            return new DetailViewHolder((view));
        } else if (viewType == overview) {
            View view = inflater.inflate(R.layout.overview, parent, false);
            return  new GeneralViewHolder(view);
        } else if (viewType == header) {
            View view = inflater.inflate(R.layout.list_header, parent, false);
            return new GeneralViewHolder(view);
        } else if (viewType == trailer) {
            View view = inflater.inflate(R.layout.trailer, parent, false);
            return new TrailerViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.review, parent, false);
            return new GeneralViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position == title) {
            if (details == null || details.title == null) { return; }
            ((AppCompatTextView) holder.itemView).setText(details.title);
        } else if (position == detail) {
            if (details == null) { return; }
            ImageView image = (ImageView) holder.itemView.findViewById(R.id.movie_image);
            Picasso.get().load(imageURL + details.poster_path).into(image);

            TextView releaseDate = (TextView) holder.itemView.findViewById(R.id.release_date);
            releaseDate.setText(details.release_date.substring(0, 4));

            TextView runtime = (TextView) holder.itemView.findViewById(R.id.movie_duration);
            runtime.setText(details.runtime + "min");

            TextView rating = (TextView) holder.itemView.findViewById(R.id.vote_average);
            rating.setText(details.vote_average + "/10");

            Button favoriteButton = (Button) holder.itemView.findViewById(R.id.favoriteMovieButton);
            favoriteButton.setText(isFavorite ? "REMOVE FROM FAVORITES" : "MARK AS FAVORITE");
        } else if (position == overview) {
            if (details == null) { return; }
            TextView overview = (TextView) holder.itemView;
            overview.setText(details.overview);
        } else if (position == 3) {
            TextView header = (TextView) holder.itemView.findViewById(R.id.list_header);
            header.setText("Trailers:");
        } else if (position < 4 + trailers.size()) {
            TextView trailerTitle = (TextView) holder.itemView.findViewById(R.id.trailer_title);
            String name = trailers.get(position - 4).name;
            trailerTitle.setText(name);
        } else if (position == 4 + trailers.size()) {
            TextView header = (TextView) holder.itemView.findViewById(R.id.list_header);
            header.setText("Reviews:");
        } else {
            TextView author = (TextView) holder.itemView.findViewById(R.id.author);
            Review review = reviews.get(position - 5 - trailers.size());
            author.setText(review.author);

            TextView content = (TextView) holder.itemView.findViewById(R.id.review_content);
            content.setText(review.content);
        }
    }

    @Override
    public int getItemCount() {
        int itemCount = 0;

        if (details != null) {
            itemCount += 3;
        }

        if (trailers.size() > 0) {
            // Add another item for the header
            itemCount += trailers.size() + 1;
        }

        if (reviews.size() > 0) {
            itemCount += reviews.size() + 1;
        }

        return itemCount;
    }

    class GeneralViewHolder extends RecyclerView.ViewHolder {
        GeneralViewHolder(View view) {
            super(view);
        }
    }

    class DetailViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        DetailViewHolder(View view) {
            super(view);
            Button favoriteButton = (Button) view.findViewById(R.id.favoriteMovieButton);
            favoriteButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            favoriteOnClickListener.favorited();
        }
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TrailerViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int index = getAdapterPosition();
            VideoResult video = trailers.get(index - 4);
            if (video.site.equals("YouTube")) {
                String trailerURL = "https://www.youtube.com/watch?v=" + video.key;
                listener.selectedTrailer(trailerURL);
            } else if (video.site.equals("Vimeo")) {
                String trailerURL = "https://www.vimeo.com/" + video.key;
                listener.selectedTrailer(trailerURL);
            }
        }
    }

    static final int title = 0;
    static final int detail = 1;
    static final int overview = 2;
    static final int header = 3;
    static final int trailer = 4;
    static final int review = 5;

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return title;
        } else if (position == 1) {
            return detail;
        } else if (position == 2) {
            return overview;
        } else if (position == 3) {
            return header;
        } else if (position < 4 + trailers.size()) {
            return trailer;
        } else if (position == 4 + trailers.size()) {
            return header;
        } else {
            return review;
        }
    }
}

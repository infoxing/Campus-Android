package de.tum.in.tumcampusapp.component.ui.tufilm;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import de.tum.in.tumcampusapp.R;
import de.tum.in.tumcampusapp.component.ui.tufilm.viewmodel.KinoViewEntity;
import de.tum.in.tumcampusapp.model.Const;

/**
 * Fragment for KinoDetails. Manages content that gets shown on the pagerView
 */
public class KinoDetailsFragment extends Fragment {

    private KinoViewEntity kino;

    public static KinoDetailsFragment newInstance(KinoViewEntity kino) {
        KinoDetailsFragment fragment = new KinoDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(Const.KINO, kino);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            kino = getArguments().getParcelable(Const.KINO);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_kinodetails_section, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        loadPoster(view, kino);

        TextView dateTextView = view.findViewById(R.id.dateTextView);
        dateTextView.setText(kino.getFormattedDate());

        TextView runtimeTextView = view.findViewById(R.id.runtimeTextView);
        runtimeTextView.setText(kino.getRuntime());

        TextView ratingTextView = view.findViewById(R.id.ratingTextView);
        ratingTextView.setText(kino.getRating());

        int colorPrimary = ContextCompat.getColor(requireContext(), R.color.color_primary);
        setCompoundDrawablesTint(dateTextView, colorPrimary);
        setCompoundDrawablesTint(runtimeTextView, colorPrimary);
        setCompoundDrawablesTint(ratingTextView, colorPrimary);

        TextView descriptionTextView = view.findViewById(R.id.descriptionTextView);
        descriptionTextView.setText(kino.getDescription());

        TextView genresTextView = view.findViewById(R.id.genresTextView);
        genresTextView.setText(kino.getGenre());

        TextView releaseYearTextView = view.findViewById(R.id.releaseYearTextView);
        releaseYearTextView.setText(kino.getYear());

        TextView actorsTextView = view.findViewById(R.id.actorsTextView);
        actorsTextView.setText(kino.getActors());

        TextView directorTextView = view.findViewById(R.id.directorTextView);
        directorTextView.setText(kino.getDirector());

        MaterialButton moreInfoButton = view.findViewById(R.id.moreInfoButton);
        moreInfoButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(kino.getLink()));
            startActivity(intent);
        });
    }

    private void loadPoster(View rootView, KinoViewEntity kino) {
        MaterialButton trailerButton = rootView.findViewById(R.id.trailerButton);
        trailerButton.setOnClickListener(v -> showTrailer(kino));

        ImageView posterView = rootView.findViewById(R.id.kino_cover);
        ProgressBar progressBar = rootView.findViewById(R.id.kino_cover_progress);

        Picasso.get()
                .load(kino.getCoverUrl())
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        progressBar.setVisibility(View.GONE);
                        posterView.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        // Free ad space
                    }
                });
    }

    private void setCompoundDrawablesTint(TextView textView, int color) {
        for (Drawable drawable : textView.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
            }
        }
    }

    private void showTrailer(KinoViewEntity kino) {
        String url = kino.getTrailerUrl();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        requireActivity().startActivity(intent);
    }

}

package de.tum.in.tumcampusapp.component.ui.news;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;

import de.tum.in.tumcampusapp.R;
import de.tum.in.tumcampusapp.navigation.NavigationDestination;
import de.tum.in.tumcampusapp.navigation.SystemIntent;
import de.tum.in.tumcampusapp.component.ui.overview.CardManager;
import de.tum.in.tumcampusapp.component.ui.overview.card.Card;
import de.tum.in.tumcampusapp.component.ui.overview.card.CardViewHolder;
import de.tum.in.tumcampusapp.core.Const;
import de.tum.in.tumcampusapp.core.DateTimeUtils;
import de.tum.in.tumcampusapp.core.Utils;

/**
 * Shows important news
 */
public class TopNewsCard extends Card {
    private ImageView imageView;
    private ProgressBar progress;
    private Context context;

    public TopNewsCard(Context context) {
        super(CardManager.CARD_TOP_NEWS, context, "top_news");
        this.context = context;
    }

    public static CardViewHolder inflateViewHolder(ViewGroup parent) {
        return new CardViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_top_news, parent, false));
    }

    private void updateImageView() {
        String imageURL = Utils.getSetting(context, Const.NEWS_ALERT_IMAGE, "");
        if (imageURL.isEmpty() || imageView == null) {
            return;
        }
        Picasso.get()
                .load(imageURL)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        // remove progress bar
                        progress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        discardCard();
                    }
                });
    }

    @Override
    public int getId() {
        return 0;
    }

    @Nullable
    @Override
    public NavigationDestination getNavigationDestination() {
        String url = Utils.getSetting(getContext(), Const.NEWS_ALERT_LINK, "");
        if (!url.isEmpty()) {
            Intent data = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            return new SystemIntent(data);
        }

        // If there is no link, don't react to clicks
        return null;
    }

    @Override
    public void updateViewHolder(@NonNull RecyclerView.ViewHolder viewHolder) {
        super.updateViewHolder(viewHolder);
        imageView = viewHolder.itemView.findViewById(R.id.top_news_img);
        progress = viewHolder.itemView.findViewById(R.id.top_news_progress);
        updateImageView();
    }

    @Override
    protected boolean shouldShow(@NonNull SharedPreferences prefs) {
        // don't show if the showUntil date does not exist or is in the past
        String untilDateString = Utils.getSetting(context, Const.NEWS_ALERT_SHOW_UNTIL, "");
        if (untilDateString.isEmpty()) {
            return false;
        }

        DateTime until = DateTimeUtils.INSTANCE.parseIsoDateWithMillis(untilDateString);
        if (until == null) {
            return false;
        }
        return Utils.getSettingBool(context, CardManager.SHOW_TOP_NEWS, true)
                && until.isAfterNow();
    }

    @Override
    public void discard(@NonNull SharedPreferences.Editor editor) {
        Utils.setSetting(this.getContext(), CardManager.SHOW_TOP_NEWS, false);
    }
}

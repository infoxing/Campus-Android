package de.tum.in.tumcampusapp.component.ui.news;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.ViewGroup;

import org.joda.time.DateTime;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import de.tum.in.tumcampusapp.R;
import de.tum.in.tumcampusapp.navigation.NavigationDestination;
import de.tum.in.tumcampusapp.navigation.SystemIntent;
import de.tum.in.tumcampusapp.component.ui.news.viewmodel.NewsViewEntity;
import de.tum.in.tumcampusapp.component.ui.overview.CardManager;
import de.tum.in.tumcampusapp.component.ui.overview.card.Card;
import de.tum.in.tumcampusapp.component.ui.overview.card.CardViewHolder;
import de.tum.in.tumcampusapp.core.Utils;

/**
 * Card that shows selected news
 */
public class NewsCard extends Card {

    private static NewsInflater mNewsInflater;

    private NewsViewEntity mNews;

    public NewsCard(Context context) {
        this(CardManager.CARD_NEWS, context);
    }

    public NewsCard(int type, Context context) {
        super(type, context, "card_news");
    }

    public static CardViewHolder inflateViewHolder(ViewGroup parent, int viewType) {
        mNewsInflater = new NewsInflater(parent.getContext());
        return mNewsInflater.onCreateNewsView(parent, viewType, true);
    }

    @Override
    public int getOptionsMenuResId() {
        return R.menu.card_popup_menu;
    }

    @Override
    public int getId() {
        return Integer.parseInt(mNews.getId());
    }

    @NonNull
    public String getTitle() {
        return mNews.getTitle();
    }

    public String getSource() {
        return mNews.getSrc();
    }

    public DateTime getDate() {
        return mNews.getDate();
    }

    @Override
    public void updateViewHolder(@NonNull RecyclerView.ViewHolder viewHolder) {
        super.updateViewHolder(viewHolder);
        NewsViewHolder holder = (NewsViewHolder) viewHolder;
        mNewsInflater.onBindNewsView(holder, mNews);
    }

    /**
     * Sets the information needed to show news
     *
     * @param n News object
     */
    public void setNews(NewsViewEntity n) {
        mNews = n;
    }

    @Override
    protected boolean shouldShow(@NonNull SharedPreferences prefs) {
        return (mNews.getDismissed() & 1) == 0;
    }

    @Nullable
    @Override
    public NavigationDestination getNavigationDestination() {
        String url = mNews.getLink();
        if (url.isEmpty()) {
            Utils.showToast(getContext(), R.string.no_link_existing);
            return null;
        }

        Intent data = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        return new SystemIntent(data);
    }

    @Override
    protected void discard(@NonNull SharedPreferences.Editor editor) {
        NewsController newsController = new NewsController(getContext());
        newsController.setDismissed(mNews.getId(), mNews.getDismissed() | 1);
    }

}

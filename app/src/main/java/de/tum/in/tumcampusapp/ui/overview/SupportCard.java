package de.tum.in.tumcampusapp.ui.overview;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.tum.in.tumcampusapp.R;
import de.tum.in.tumcampusapp.ui.overview.card.Card;
import de.tum.in.tumcampusapp.ui.overview.card.CardViewHolder;
import de.tum.in.tumcampusapp.core.Utils;

/**
 * Card that describes how to dismiss a card
 */
public class SupportCard extends Card {

    public SupportCard(Context context) {
        super(CardManager.CARD_SUPPORT, context, "");
    }

    public static CardViewHolder inflateViewHolder(ViewGroup parent) {
        final View view = LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.card_support, parent, false);

        view.findViewById(R.id.facebook_button)
            .setOnClickListener(v -> {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setData(Uri.parse(view.getContext()
                                                    .getString(R.string.facebook_link)));
                v.getContext()
                 .startActivity(browserIntent);
            });

        view.findViewById(R.id.github_button)
            .setOnClickListener(v -> {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setData(Uri.parse(view.getContext()
                                                    .getString(R.string.github_link)));
                v.getContext()
                 .startActivity(browserIntent);
            });

        view.findViewById(R.id.email_button)
            .setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                Uri uri = Uri.parse(view.getContext().getString(R.string.support_email_link));
                intent.setDataAndType(uri, "text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, view.getContext().getString(R.string.feedback));
                v.getContext()
                 .startActivity(Intent.createChooser(intent, "Send Email"));
            });

        return new CardViewHolder(view);
    }

    @Override
    public void discard(@NonNull Editor editor) {
        Utils.setSetting(getContext(), CardManager.SHOW_SUPPORT, false);
    }

    @Override
    protected boolean shouldShow(@NonNull SharedPreferences prefs) {
        return Utils.getSettingBool(getContext(), CardManager.SHOW_SUPPORT, true);
    }

    @Override
    public int getId() {
        return 0;
    }
}

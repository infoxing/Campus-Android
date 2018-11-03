package de.tum.in.tumcampusapp.component.tumui.tutionfees;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jetbrains.annotations.Nullable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.tum.in.tumcampusapp.R;
import de.tum.in.tumcampusapp.component.other.navigation.NavigationDestination;
import de.tum.in.tumcampusapp.component.other.navigation.SystemActivity;
import de.tum.in.tumcampusapp.component.tumui.tutionfees.viewmodel.TuitionViewEntity;
import de.tum.in.tumcampusapp.component.ui.overview.CardManager;
import de.tum.in.tumcampusapp.component.ui.overview.card.Card;
import de.tum.in.tumcampusapp.component.ui.overview.card.CardViewHolder;

/**
 * Card that shows information about your fees that have to be paid or have been paid
 */
public class TuitionFeesCard extends Card {

    private static final String LAST_FEE_FRIST = "fee_frist";
    private static final String LAST_FEE_SOLL = "fee_soll";

    private TuitionViewEntity mTuition;

    TuitionFeesCard(Context context) {
        super(CardManager.CARD_TUITION_FEE, context, "card_tuition_fee");
    }

    public static CardViewHolder inflateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                                  .inflate(R.layout.card_tuition_fees, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public int getOptionsMenuResId() {
        return R.menu.card_popup_menu;
    }

    public String getTitle() {
        return getContext().getString(R.string.tuition_fees);
    }

    @Override
    public int getId() {
        return 0;
    }

    @Nullable
    @Override
    public NavigationDestination getNavigationDestination() {
        return new SystemActivity(TuitionFeesActivity.class, null);
    }

    @Override
    public void updateViewHolder(@NonNull RecyclerView.ViewHolder viewHolder) {
        super.updateViewHolder(viewHolder);

        TextView reregisterInfoTextView =
                viewHolder.itemView.findViewById(R.id.reregister_info_text_view);
        TextView outstandingBalanceTextView =
                viewHolder.itemView.findViewById(R.id.outstanding_balance_text_view);

        if (mTuition.isPaid()) {
            String placeholderText = getContext().getString(R.string.reregister_success);
            String text = String.format(placeholderText, mTuition.getSemester());
            reregisterInfoTextView.setText(text);
        } else {
            String dateText = mTuition.getFormattedDeadline();

            String text = String.format(getContext().getString(R.string.reregister_todo), dateText);
            reregisterInfoTextView.setText(text);

            String textWithPlaceholder = getContext().getString(R.string.amount_dots_card);
            String balanceText = String.format(textWithPlaceholder, mTuition.getFormattedAmountText());
            outstandingBalanceTextView.setText(balanceText);
            outstandingBalanceTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected boolean shouldShow(@NonNull SharedPreferences prefs) {
        String prevDeadline = prefs.getString(LAST_FEE_FRIST, "");
        String prevAmount = prefs.getString(LAST_FEE_SOLL, Float.toString(mTuition.getAmount()));

        // If app gets started for the first time and fee is already paid don't annoy user
        // by showing him that he has been re-registered successfully
        String deadline = mTuition.getFormattedDeadline();
        String amount = Float.toString(mTuition.getAmount());
        return !(prevDeadline.isEmpty() && mTuition.isPaid()) &&
               (prevDeadline.compareTo(deadline) < 0 || prevAmount.compareTo(amount) > 0);
    }

    public void discard(@NonNull Editor editor) {
        String deadline = mTuition.getFormattedDeadline();
        String amount = Float.toString(mTuition.getAmount());
        editor.putString(LAST_FEE_FRIST, deadline);
        editor.putString(LAST_FEE_SOLL, amount);
    }

    public void setTuition(@NonNull TuitionViewEntity tuition) {
        mTuition = tuition;
    }

}

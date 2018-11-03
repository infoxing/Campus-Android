package de.tum.in.tumcampusapp.component.ui.ticket.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import de.tum.in.tumcampusapp.R;
import de.tum.in.tumcampusapp.component.ui.overview.card.CardViewHolder;
import de.tum.in.tumcampusapp.component.ui.ticket.EventCard;
import de.tum.in.tumcampusapp.component.ui.ticket.EventDiffUtil;
import de.tum.in.tumcampusapp.component.ui.ticket.EventsController;
import de.tum.in.tumcampusapp.component.ui.ticket.activity.ShowTicketActivity;
import de.tum.in.tumcampusapp.component.ui.ticket.viewmodel.EventViewEntity;
import de.tum.in.tumcampusapp.model.Const;
import de.tum.in.tumcampusapp.utils.Utils;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {

    private static final Pattern COMPILE = Pattern.compile("^[0-9]+\\. [0-9]+\\. [0-9]+:[ ]*");

    private Context mContext;
    private EventsController mEventsController;

    private List<EventViewEntity> mEvents = new ArrayList<>();

    public EventsAdapter(Context context) {
        mContext = context;
        mEventsController = new EventsController(context);
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_events_item, parent, false);
        return new EventViewHolder(view, false);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        EventViewEntity event = mEvents.get(position);

        EventCard eventCard = new EventCard(mContext);
        eventCard.setEvent(event);
        holder.setCurrentCard(eventCard);

        boolean hasTicket = mEventsController.isEventBooked(event.getId());
        holder.bind(event, hasTicket);
    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }

    public void update(List<EventViewEntity> newEvents) {
        DiffUtil.Callback callback = new EventDiffUtil(mEvents, newEvents);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(callback);

        mEvents.clear();
        mEvents.addAll(newEvents);

        diffResult.dispatchUpdatesTo(this);
    }

    public static class EventViewHolder extends CardViewHolder {

        private boolean showOptionsButton;
        Group optionsButtonGroup;

        CardView cardView;
        ProgressBar progressBar;
        ImageView imageView;
        TextView titleTextView;
        TextView localityTextView;
        TextView dateTextView;
        MaterialButton ticketButton;

        public EventViewHolder(View view, boolean showOptionsButton) {
            super(view);
            this.showOptionsButton = showOptionsButton;

            cardView = (CardView) view;
            progressBar = view.findViewById(R.id.poster_progress_bar);
            optionsButtonGroup = view.findViewById(R.id.cardMoreIconGroup);
            imageView = view.findViewById(R.id.events_img);
            titleTextView = view.findViewById(R.id.events_title);
            localityTextView = view.findViewById(R.id.events_src_locality);
            dateTextView = view.findViewById(R.id.events_src_date);
            ticketButton = view.findViewById(R.id.ticketButton);
        }

        public void bind(EventViewEntity event, boolean hasTicket) {
            String imageUrl = event.getImageUrl();
            boolean showImage = imageUrl != null && !imageUrl.isEmpty();

            optionsButtonGroup.setVisibility(showOptionsButton ? VISIBLE : GONE);

            if (showImage) {
                Picasso.get()
                        .load(imageUrl)
                        .into(imageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                progressBar.setVisibility(GONE);
                            }

                            @Override
                            public void onError(Exception e) {
                                Utils.log(e);
                                progressBar.setVisibility(GONE);
                            }
                        });
            } else {
                progressBar.setVisibility(GONE);
                imageView.setVisibility(GONE);
            }

            String title = event.getTitle();
            title = COMPILE.matcher(title).replaceAll("");
            titleTextView.setText(title);

            String locality = event.getLocality();
            localityTextView.setText(locality);

            String startTime = event.getFormattedStartTime();
            dateTextView.setText(startTime);

            if (!hasTicket) {
                ticketButton.setVisibility(GONE);
                return;
            }

            ticketButton.setVisibility(VISIBLE);
            ticketButton.setOnClickListener(v -> {
                Context context = itemView.getContext();
                Intent intent = new Intent(context, ShowTicketActivity.class);
                intent.putExtra(Const.KEY_EVENT_ID, event.getId());
                context.startActivity(intent);
            });
        }

    }

}

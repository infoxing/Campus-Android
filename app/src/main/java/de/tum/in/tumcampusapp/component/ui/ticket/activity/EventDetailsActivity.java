package de.tum.in.tumcampusapp.component.ui.ticket.activity;

import android.os.Bundle;

import de.tum.in.tumcampusapp.R;
import de.tum.in.tumcampusapp.component.other.generic.activity.BaseActivity;
import de.tum.in.tumcampusapp.component.ui.ticket.fragment.EventDetailsFragment;
import de.tum.in.tumcampusapp.model.ticket.RawEvent;
import de.tum.in.tumcampusapp.utils.Const;


public class EventDetailsActivity extends BaseActivity {

    public EventDetailsActivity() {
        super(R.layout.activity_event_details);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RawEvent event = getIntent().getParcelableExtra(Const.KEY_EVENT);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, EventDetailsFragment.newInstance(event))
                .commit();
    }

}

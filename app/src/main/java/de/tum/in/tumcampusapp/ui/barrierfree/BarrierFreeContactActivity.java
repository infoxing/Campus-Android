package de.tum.in.tumcampusapp.ui.barrierfree;

import android.os.Bundle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.tum.in.tumcampusapp.R;
import de.tum.in.tumcampusapp.api.app.TUMCabeClient;
import de.tum.in.tumcampusapp.ui.generic.activity.ActivityForLoadingInBackground;
import de.tum.in.tumcampusapp.model.barrierfree.BarrierfreeContact;
import de.tum.in.tumcampusapp.ui.barrierfree.viewmodel.BarrierfreeContactViewEntity;
import de.tum.in.tumcampusapp.core.Utils;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class BarrierFreeContactActivity extends ActivityForLoadingInBackground<Void, List<BarrierfreeContact>> {

    public StickyListHeadersListView listview;

    public BarrierFreeContactActivity() {
        super(R.layout.activity_barrier_free_list_info);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listview = findViewById(R.id.activity_barrier_info_list_view);
        startLoading();
    }

    @Override
    protected List<BarrierfreeContact> onLoadInBackground(Void... arg) {
        showLoadingStart();
        List<BarrierfreeContact> result = new ArrayList<>();
        try {
            result = TUMCabeClient.getInstance(this)
                                  .getBarrierfreeContactList();
        } catch (IOException e) {
            Utils.log(e);
            return result;
        }
        return result;
    }

    @Override
    protected void onLoadFinished(List<BarrierfreeContact> result) {
        showLoadingEnded();
        if (result == null || result.isEmpty()) {
            showErrorLayout();
            return;
        }

        List<BarrierfreeContactViewEntity> viewEntities = new ArrayList<>();
        for (BarrierfreeContact contact : result) {
            viewEntities.add(BarrierfreeContactViewEntity.create(contact));
        }

        BarrierfreeContactAdapter adapter = new BarrierfreeContactAdapter(this, viewEntities);
        listview.setAdapter(adapter);
    }
}

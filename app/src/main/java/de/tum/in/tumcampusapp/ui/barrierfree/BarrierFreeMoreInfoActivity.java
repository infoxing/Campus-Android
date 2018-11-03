package de.tum.in.tumcampusapp.ui.barrierfree;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.tum.in.tumcampusapp.R;
import de.tum.in.tumcampusapp.api.app.TUMCabeClient;
import de.tum.in.tumcampusapp.ui.generic.activity.ActivityForLoadingInBackground;
import de.tum.in.tumcampusapp.model.barrierfree.BarrierfreeMoreInfo;
import de.tum.in.tumcampusapp.ui.barrierfree.viewmodel.BarrierfreeMoreInfoViewEntity;
import de.tum.in.tumcampusapp.core.Utils;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class BarrierFreeMoreInfoActivity
        extends ActivityForLoadingInBackground<Void, List<BarrierfreeMoreInfo>>
        implements AdapterView.OnItemClickListener {

    public StickyListHeadersListView listview;
    public List<BarrierfreeMoreInfoViewEntity> infos;
    public BarrierfreeMoreInfoAdapter adapter;

    public BarrierFreeMoreInfoActivity() {
        super(R.layout.activity_barrier_free_list_info);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listview = findViewById(R.id.activity_barrier_info_list_view);

        startLoading();
    }

    @Override
    protected void onLoadFinished(List<BarrierfreeMoreInfo> result) {
        showLoadingEnded();
        if (result == null || result.isEmpty()) {
            showErrorLayout();
            return;
        }

        List<BarrierfreeMoreInfoViewEntity> viewEntities = new ArrayList<>();
        for (BarrierfreeMoreInfo info : result) {
            viewEntities.add(BarrierfreeMoreInfoViewEntity.create(info));
        }

        infos = viewEntities;

        adapter = new BarrierfreeMoreInfoAdapter(this, infos);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(this);
    }

    @Override
    protected List<BarrierfreeMoreInfo> onLoadInBackground(Void... arg) {
        showLoadingStart();
        List<BarrierfreeMoreInfo> result = new ArrayList<>();
        try {
            result = TUMCabeClient.getInstance(this)
                                  .getMoreInfoList();
        } catch (IOException e) {
            Utils.log(e);
            return result;
        }
        return result;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String url = infos.get(position)
                          .getUrl();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);

    }
}

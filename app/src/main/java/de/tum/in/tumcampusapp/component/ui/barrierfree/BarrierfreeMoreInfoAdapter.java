package de.tum.in.tumcampusapp.component.ui.barrierfree;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import de.tum.in.tumcampusapp.R;
import de.tum.in.tumcampusapp.component.other.generic.adapter.SimpleStickyListHeadersAdapter;
import de.tum.in.tumcampusapp.component.ui.barrierfree.viewmodel.BarrierfreeMoreInfoViewEntity;

public class BarrierfreeMoreInfoAdapter
        extends SimpleStickyListHeadersAdapter<BarrierfreeMoreInfoViewEntity> {

    public BarrierfreeMoreInfoAdapter(Context context, List<BarrierfreeMoreInfoViewEntity> infos) {
        super(context, infos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        View view = convertView;
        if (view == null) {
            view = getInflater().inflate(R.layout.activity_barrier_free_more_info_listview, parent, false);

            // Crate UI element
            holder = new ViewHolder();
            holder.title = view.findViewById(R.id.barrierfreeMoreInfoTitle);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        // set title
        final BarrierfreeMoreInfoViewEntity info = getInfoList().get(position);
        if (info != null) {
            holder.title.setText(info.getTitle());
        }

        return view;
    }

    // the layout of the list
    static class ViewHolder {
        TextView title;
    }

}
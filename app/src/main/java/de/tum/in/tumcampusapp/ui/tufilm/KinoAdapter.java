package de.tum.in.tumcampusapp.ui.tufilm;

import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import de.tum.in.tumcampusapp.ui.tufilm.viewmodel.KinoViewEntity;

public class KinoAdapter extends FragmentStatePagerAdapter {

    private final List<KinoViewEntity> movies;

    KinoAdapter(FragmentManager fm, List<KinoViewEntity> kinos) {
        super(fm);
        movies = kinos;
    }

    @Override
    public Fragment getItem(int position) {
        return KinoDetailsFragment.newInstance(movies.get(position));
    }

    @Override
    public int getCount() {
        return movies.size();
    }

}

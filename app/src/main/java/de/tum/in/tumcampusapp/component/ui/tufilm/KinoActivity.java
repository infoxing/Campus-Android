package de.tum.in.tumcampusapp.component.ui.tufilm;

import android.os.Bundle;

import java.util.List;

import androidx.viewpager.widget.ViewPager;
import de.tum.in.tumcampusapp.R;
import de.tum.in.tumcampusapp.component.other.generic.activity.ProgressActivity;
import de.tum.in.tumcampusapp.component.ui.tufilm.viewmodel.KinoViewEntity;
import de.tum.in.tumcampusapp.component.ui.tufilm.viewmodel.KinoViewEntityMapper;
import de.tum.in.tumcampusapp.database.TcaDb;
import de.tum.in.tumcampusapp.core.Const;
import de.tum.in.tumcampusapp.core.Utils;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Activity to show TU Kino films
 */
public class KinoActivity extends ProgressActivity<Void> {

    private int startPosition;
    private ViewPager mPager;

    public KinoActivity() {
        super(R.layout.activity_kino);
    }

    private final CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(R.color.secondary_window_background);

        KinoLocalRepository.db = TcaDb.getInstance(this);

        KinoViewModel kinoViewModel = new KinoViewModel(
                KinoLocalRepository.INSTANCE, KinoRemoteRepository.INSTANCE, disposables);

        String date = getIntent().getStringExtra(Const.KINO_DATE);
        startPosition = (date != null) ? kinoViewModel.getPosition(date) : 0;

        mPager = findViewById(R.id.pager);

        int margin = getResources().getDimensionPixelSize(R.dimen.material_default_padding);
        mPager.setPageMargin(margin);

        KinoViewEntityMapper mapper = new KinoViewEntityMapper();

        Disposable disposable = kinoViewModel
                .getAllKinos()
                .map(mapper::apply)
                .subscribe(this::showKinosOrPlaceholder, throwable -> {
                    Utils.log(throwable);
                    showError(R.string.error_something_wrong);
                });

        disposables.add(disposable);
    }

    private void showKinosOrPlaceholder(List<KinoViewEntity> kinos) {
        if (kinos.isEmpty()) {
            showEmptyResponseLayout(R.string.no_movies, R.drawable.no_movies);
            return;
        }

        KinoAdapter kinoAdapter = new KinoAdapter(getSupportFragmentManager(), kinos);
        mPager.setAdapter(kinoAdapter);
        mPager.setCurrentItem(startPosition);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.dispose();
    }

}


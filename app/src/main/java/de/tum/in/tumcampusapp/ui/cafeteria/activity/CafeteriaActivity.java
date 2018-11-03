package de.tum.in.tumcampusapp.ui.cafeteria.activity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.viewpager.widget.ViewPager;
import de.tum.in.tumcampusapp.R;
import de.tum.in.tumcampusapp.api.app.TUMCabeClient;
import de.tum.in.tumcampusapp.core.Const;
import de.tum.in.tumcampusapp.core.NetUtils;
import de.tum.in.tumcampusapp.core.Utils;
import de.tum.in.tumcampusapp.database.TcaDb;
import de.tum.in.tumcampusapp.locations.LocationManager;
import de.tum.in.tumcampusapp.model.cafeteria.Cafeteria;
import de.tum.in.tumcampusapp.model.locations.Geo;
import de.tum.in.tumcampusapp.ui.cafeteria.CafeteriaMenuInflater;
import de.tum.in.tumcampusapp.ui.cafeteria.controller.CafeteriaManager;
import de.tum.in.tumcampusapp.ui.cafeteria.details.CafeteriaDetailsSectionsPagerAdapter;
import de.tum.in.tumcampusapp.ui.cafeteria.details.CafeteriaViewModel;
import de.tum.in.tumcampusapp.ui.cafeteria.repository.CafeteriaLocalRepository;
import de.tum.in.tumcampusapp.ui.cafeteria.repository.CafeteriaRemoteRepository;
import de.tum.in.tumcampusapp.ui.calendar.CalendarController;
import de.tum.in.tumcampusapp.ui.generic.activity.ActivityForDownloadingExternal;
import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Lists all dishes at selected cafeteria
 * <p>
 * OPTIONAL: Const.CAFETERIA_ID set in incoming bundle (cafeteria to show)
 */
public class CafeteriaActivity extends ActivityForDownloadingExternal implements AdapterView.OnItemSelectedListener {

    private ViewPager mViewPager;
    private int mCafeteriaId = -1;
    private CafeteriaViewModel cafeteriaViewModel;
    private List<Cafeteria> mCafeterias = new ArrayList<>();

    private final CompositeDisposable mDisposable = new CompositeDisposable();

    public CafeteriaActivity() {
        super(Const.CAFETERIAS, R.layout.activity_cafeteria);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get id from intent if specified
        final Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null
                && intent.getExtras().containsKey(Const.CAFETERIA_ID)) {
            mCafeteriaId = intent.getExtras()
                                 .getInt(Const.CAFETERIA_ID);
        } else {
            // If we're not provided with a cafeteria ID, we choose the best matching cafeteria.
            int cafeteriaId = new CafeteriaManager(this).getBestMatchMensaId();
            if (cafeteriaId == -1) {
                mCafeteriaId = cafeteriaId;
            }
        }

        mViewPager = findViewById(R.id.pager);

        /*
         *set pagelimit to avoid losing toggle button state.
         *by default it's 1.
         */
        mViewPager.setOffscreenPageLimit(50);

        CafeteriaRemoteRepository remoteRepository = CafeteriaRemoteRepository.INSTANCE;
        remoteRepository.setTumCabeClient(TUMCabeClient.getInstance(this));

        CafeteriaLocalRepository localRepository = CafeteriaLocalRepository.INSTANCE;
        localRepository.setDb(TcaDb.getInstance(this));

        cafeteriaViewModel = new CafeteriaViewModel(localRepository, remoteRepository, mDisposable);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Add info icon to show ingredients
        getMenuInflater().inflate(R.menu.menu_section_fragment_cafeteria_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_ingredients) {
            // Build a alert dialog containing the mapping of ingredients to the numbers
            String ingredients = getString(R.string.cafeteria_ingredients);
            SpannableString title = CafeteriaMenuInflater.menuToSpan(this, ingredients);

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.action_ingredients)
                    .setMessage(title)
                    .setPositiveButton(R.string.ok, null)
                    .create();

            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_corners_background);
            }

            dialog.show();
            return true;
        }
        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(this, CafeteriaNotificationSettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Setup action bar navigation (to switch between cafeterias)
     */
    @Override
    protected void onStart() {
        super.onStart();

        // Adapter for drop-down navigation
        ArrayAdapter<Cafeteria> adapterCafeterias = new ArrayAdapter<Cafeteria>(
                this, R.layout.simple_spinner_item_actionbar, android.R.id.text1, mCafeterias) {
            final LayoutInflater inflater = LayoutInflater.from(getContext());

            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                View v = inflater.inflate(R.layout.simple_spinner_dropdown_item_actionbar_two_line, parent, false);
                Cafeteria c = getItem(position);

                TextView name = v.findViewById(android.R.id.text1); // Set name
                TextView address = v.findViewById(android.R.id.text2); // Set address
                TextView dist = v.findViewById(R.id.distance); // Set distance

                if (c != null) {
                    name.setText(c.getName());
                    address.setText(c.getAddress());
                    dist.setText(Utils.formatDistance(c.getDistance()));
                }

                return v;
            }
        };

        Spinner spinner = findViewById(R.id.spinnerToolbar);
        spinner.setAdapter(adapterCafeterias);
        spinner.setOnItemSelectedListener(this);

        CalendarController calendarController = new CalendarController(this);
        Geo nextLocation = calendarController.getNextCalendarItemGeo();

        LocationManager locationManager = new LocationManager(this);
        Location currLocation = locationManager.getCurrentOrNextLocation(nextLocation);

        Flowable<List<Cafeteria>> cafeterias = cafeteriaViewModel.getAllCafeterias(currLocation);
        mDisposable.add(
                cafeterias.subscribe(
                        it -> {
                            validateList(it);
                            mCafeterias.clear();
                            mCafeterias.addAll(it);
                            adapterCafeterias.notifyDataSetChanged();
                            setCurrentSelectedCafeteria(spinner);
                        }, throwable -> Utils.logwithTag("CafeteriaActivity", throwable.getMessage())
                ));
    }

    private void validateList(Collection<Cafeteria> cafeterias) {
        if (cafeterias.isEmpty()) {
            if (NetUtils.isConnected(this)) {
                showErrorLayout();
            } else {
                showNoInternetLayout();
            }
        }
    }

    private void setCurrentSelectedCafeteria(Spinner spinner) {
        int selIndex = -1;
        for (int i = 0; i < mCafeterias.size(); i++) {
            Cafeteria c = mCafeterias.get(i);
            if (mCafeteriaId == -1 || mCafeteriaId == c.getId()) {
                mCafeteriaId = c.getId();
                selIndex = i;
                break;
            }
        }
        if (selIndex > -1) {
            spinner.setSelection(selIndex);
        }
    }

    /**
     * Switch cafeteria if a new cafeteria has been selected
     *
     * @param parent the parent view
     * @param pos    index of the new selection
     * @param id     id of the selected item
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        Intent intent = getIntent();
        //check if Activity triggered from favoriteDish Notification
        if (intent != null && intent.getExtras() != null && intent.getExtras()
                                                                  .containsKey(Const.MENSA_FOR_FAVORITEDISH)) {
            for (int i = 0; i < parent.getCount(); i++) {
                //get mensaId from extra to redirect the user to it.
                if (intent.getExtras()
                          .getInt(Const.MENSA_FOR_FAVORITEDISH) == mCafeterias.get(i)
                                                                              .getId()) {
                    mCafeteriaId = mCafeterias.get(i)
                                              .getId();
                    parent.setSelection(i);
                    intent.removeExtra(Const.MENSA_FOR_FAVORITEDISH);
                    break;
                }
            }
        } else {
            mCafeteriaId = mCafeterias.get(pos)
                                      .getId();
        }

        CafeteriaDetailsSectionsPagerAdapter mSectionsPagerAdapter
                = new CafeteriaDetailsSectionsPagerAdapter(getSupportFragmentManager());
        // Create the adapter that will return a fragment for each of the primary sections of the app.
        mViewPager.setAdapter(null); //unset the adapter for updating
        mSectionsPagerAdapter.setCafeteriaId(this, mCafeteriaId);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //Don't change anything
    }

    @Override
    protected void onDestroy() {
        mDisposable.clear();
        super.onDestroy();
    }
}
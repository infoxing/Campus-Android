package de.tum.in.tumcampusapp.ui.cafeteria.controller;

import android.content.Context;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import de.tum.in.tumcampusapp.api.app.TUMCabeClient;
import de.tum.in.tumcampusapp.api.shared.CacheControl;
import de.tum.in.tumcampusapp.core.Utils;
import de.tum.in.tumcampusapp.database.TcaDb;
import de.tum.in.tumcampusapp.locations.LocationManager;
import de.tum.in.tumcampusapp.model.cafeteria.CafeteriaMenu;
import de.tum.in.tumcampusapp.model.locations.Geo;
import de.tum.in.tumcampusapp.notifications.ProvidesNotifications;
import de.tum.in.tumcampusapp.ui.cafeteria.CafeteriaMenuCard;
import de.tum.in.tumcampusapp.ui.cafeteria.details.CafeteriaViewModel;
import de.tum.in.tumcampusapp.ui.cafeteria.repository.CafeteriaLocalRepository;
import de.tum.in.tumcampusapp.ui.cafeteria.repository.CafeteriaRemoteRepository;
import de.tum.in.tumcampusapp.ui.cafeteria.viewmodel.CafeteriaMenuViewEntity;
import de.tum.in.tumcampusapp.ui.cafeteria.viewmodel.CafeteriaWithMenus;
import de.tum.in.tumcampusapp.ui.calendar.CalendarController;
import de.tum.in.tumcampusapp.ui.overview.card.Card;
import de.tum.in.tumcampusapp.ui.overview.card.ProvidesCard;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Cafeteria Manager, handles database stuff, external imports
 */
public class CafeteriaManager implements ProvidesCard, ProvidesNotifications {

    private Context mContext;
    private final CafeteriaViewModel cafeteriaViewModel;
    private final CompositeDisposable compositeDisposable;
    private final CalendarController calendarController;

    /**
     * Constructor, open/create database, create table if necessary
     *
     * @param context Context
     */
    public CafeteriaManager(Context context) {
        mContext = context;

        TcaDb db = TcaDb.getInstance(context);
        compositeDisposable = new CompositeDisposable();
        calendarController = new CalendarController(context);

        CafeteriaLocalRepository localRepository = CafeteriaLocalRepository.INSTANCE;
        localRepository.setDb(db);

        CafeteriaRemoteRepository remoteRepository = CafeteriaRemoteRepository.INSTANCE;
        remoteRepository.setTumCabeClient(TUMCabeClient.getInstance(context));

        cafeteriaViewModel = new CafeteriaViewModel(localRepository, remoteRepository, compositeDisposable);
    }

    @NotNull
    @Override
    public List<Card> getCards(@NonNull CacheControl cacheControl) {
        List<Card> results = new ArrayList<>();

        CafeteriaWithMenus cafeteria = getCafeteriaWithMenus();
        if (cafeteria == null) {
            return results;
        }

        CafeteriaMenuCard card = new CafeteriaMenuCard(mContext);
        card.setCafeteriaWithMenus(cafeteria);

        results.add(card.getIfShowOnStart());
        return results;
    }

    @Override
    public boolean hasNotificationsEnabled() {
        return Utils.getSettingBool(mContext, "card_cafeteria_phone", true);
    }

    @Nullable
    private CafeteriaWithMenus getCafeteriaWithMenus() {
        // Choose which mensa should be shown
        Geo likelyNextLocation = calendarController.getNextCalendarItemGeo();
        int cafeteriaId = new LocationManager(mContext).getCafeteria(likelyNextLocation);
        if (cafeteriaId == -1) {
            return null;
        }
        return cafeteriaViewModel.getCafeteriaWithMenus(cafeteriaId);
    }

    /**
     * Returns a list of {@link CafeteriaMenu}s of the best-matching cafeteria. If there's no
     * best-matching cafeteria, it returns an empty list.
     */
    public List<CafeteriaMenuViewEntity> getBestMatchCafeteriaMenus() {
        int cafeteriaId = getBestMatchMensaId();
        if (cafeteriaId == -1) {
            return Collections.emptyList();
        }

        return getCafeteriaMenusByCafeteriaId(cafeteriaId);
    }

    public int getBestMatchMensaId() {
        // Choose which mensa should be shown
        Geo likelyNextLocation = calendarController.getNextCalendarItemGeo();
        int cafeteriaId = new LocationManager(mContext).getCafeteria(likelyNextLocation);
        if (cafeteriaId == -1) {
            Utils.log("could not get a Cafeteria from locationManager!");
        }
        return cafeteriaId;
    }

    private List<CafeteriaMenuViewEntity> getCafeteriaMenusByCafeteriaId(int cafeteriaId) {
        CafeteriaWithMenus cafeteria = new CafeteriaWithMenus(cafeteriaId);

        List<DateTime> menuDates = CafeteriaLocalRepository.INSTANCE.getAllMenuDates();
        cafeteria.setMenuDates(menuDates);

        DateTime nextMenuDate = cafeteria.getNextMenuDate();
        List<CafeteriaMenuViewEntity> menus =
                CafeteriaLocalRepository.INSTANCE.getCafeteriaMenus(cafeteriaId, nextMenuDate);
        cafeteria.setMenus(menus);

        return cafeteria.getMenus();
    }

}
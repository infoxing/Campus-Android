package de.tum.in.tumcampusapp.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import de.tum.in.tumcampusapp.database.daos.WidgetsTimetableBlacklistDao;
import de.tum.in.tumcampusapp.database.migrations.Migration1to2;
import de.tum.in.tumcampusapp.model.transportation.TransportFavorites;
import de.tum.in.tumcampusapp.model.transportation.WidgetsTransport;
import de.tum.in.tumcampusapp.core.Const;
import de.tum.in.tumcampusapp.database.daos.ActiveAlarmsDao;
import de.tum.in.tumcampusapp.database.daos.BuildingToGpsDao;
import de.tum.in.tumcampusapp.database.daos.CafeteriaDao;
import de.tum.in.tumcampusapp.database.daos.CafeteriaLocationDao;
import de.tum.in.tumcampusapp.database.daos.CafeteriaMenuDao;
import de.tum.in.tumcampusapp.database.daos.CalendarDao;
import de.tum.in.tumcampusapp.database.daos.ChatMessageDao;
import de.tum.in.tumcampusapp.database.daos.ChatRoomDao;
import de.tum.in.tumcampusapp.database.daos.EventDao;
import de.tum.in.tumcampusapp.database.daos.FavoriteDishDao;
import de.tum.in.tumcampusapp.database.daos.KinoDao;
import de.tum.in.tumcampusapp.database.daos.NewsDao;
import de.tum.in.tumcampusapp.database.daos.NewsSourcesDao;
import de.tum.in.tumcampusapp.database.daos.NotificationDao;
import de.tum.in.tumcampusapp.database.daos.RecentsDao;
import de.tum.in.tumcampusapp.database.daos.RoomLocationsDao;
import de.tum.in.tumcampusapp.database.daos.StudyRoomDao;
import de.tum.in.tumcampusapp.database.daos.StudyRoomGroupDao;
import de.tum.in.tumcampusapp.database.daos.SyncDao;
import de.tum.in.tumcampusapp.database.daos.TicketDao;
import de.tum.in.tumcampusapp.database.daos.TicketTypeDao;
import de.tum.in.tumcampusapp.database.daos.TransportDao;
import de.tum.in.tumcampusapp.database.daos.WifiMeasurementDao;
import de.tum.in.tumcampusapp.model.activealarms.ActiveAlarm;
import de.tum.in.tumcampusapp.model.activealarms.ScheduledNotification;
import de.tum.in.tumcampusapp.model.activealarms.ScheduledNotificationsDao;
import de.tum.in.tumcampusapp.model.alarms.FcmNotification;
import de.tum.in.tumcampusapp.model.cafeteria.Cafeteria;
import de.tum.in.tumcampusapp.model.cafeteria.CafeteriaMenu;
import de.tum.in.tumcampusapp.model.cafeteria.FavoriteDish;
import de.tum.in.tumcampusapp.model.cafeteria.Location;
import de.tum.in.tumcampusapp.model.calendar.CalendarItem;
import de.tum.in.tumcampusapp.model.calendar.WidgetsTimetableBlacklist;
import de.tum.in.tumcampusapp.model.chat.ChatMessage;
import de.tum.in.tumcampusapp.model.chat.ChatRoomDbRow;
import de.tum.in.tumcampusapp.model.lecture.RoomLocations;
import de.tum.in.tumcampusapp.model.locations.BuildingToGps;
import de.tum.in.tumcampusapp.model.news.News;
import de.tum.in.tumcampusapp.model.news.NewsSources;
import de.tum.in.tumcampusapp.model.recents.Recent;
import de.tum.in.tumcampusapp.model.studyroom.StudyRoom;
import de.tum.in.tumcampusapp.model.studyroom.StudyRoomGroup;
import de.tum.in.tumcampusapp.model.ticket.RawEvent;
import de.tum.in.tumcampusapp.model.ticket.RawTicket;
import de.tum.in.tumcampusapp.model.ticket.TicketType;
import de.tum.in.tumcampusapp.model.tufilm.RawKino;
import de.tum.in.tumcampusapp.model.wifimeasurement.WifiMeasurement;
import de.tum.in.tumcampusapp.model.sync.Sync;

@Database(version = 2, entities = {
        Cafeteria.class,
        CafeteriaMenu.class,
        FavoriteDish.class,
        Sync.class,
        BuildingToGps.class,
        RawKino.class,
        RawEvent.class,
        RawTicket.class,
        TicketType.class,
        ChatMessage.class,
        Location.class,
        News.class,
        NewsSources.class,
        CalendarItem.class,
        RoomLocations.class,
        WidgetsTimetableBlacklist.class,
        WifiMeasurement.class,
        Recent.class,
        StudyRoomGroup.class,
        StudyRoom.class,
        FcmNotification.class,
        TransportFavorites.class,
        WidgetsTransport.class,
        ChatRoomDbRow.class,
        ScheduledNotification.class,
        ActiveAlarm.class
})
@TypeConverters(Converters.class)
public abstract class TcaDb extends RoomDatabase {
    private static final Migration[] migrations = {
            new Migration1to2()
    };

    private static TcaDb instance;

    public static synchronized TcaDb getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), TcaDb.class, Const.DATABASE_NAME)
                    .allowMainThreadQueries()
                    .addMigrations(migrations)
                    .build();
        }
        return instance;
    }

    public abstract CafeteriaDao cafeteriaDao();

    public abstract CafeteriaMenuDao cafeteriaMenuDao();

    public abstract FavoriteDishDao favoriteDishDao();

    public abstract SyncDao syncDao();

    public abstract BuildingToGpsDao buildingToGpsDao();

    public abstract KinoDao kinoDao();

    public abstract EventDao eventDao();

    public abstract TicketDao ticketDao();

    public abstract TicketTypeDao ticketTypeDao();

    public abstract CafeteriaLocationDao locationDao();

    public abstract ChatMessageDao chatMessageDao();

    public abstract NewsDao newsDao();

    public abstract NewsSourcesDao newsSourcesDao();

    public abstract CalendarDao calendarDao();

    public abstract RoomLocationsDao roomLocationsDao();

    public abstract WidgetsTimetableBlacklistDao widgetsTimetableBlacklistDao();

    public abstract WifiMeasurementDao wifiMeasurementDao();

    public abstract RecentsDao recentsDao();

    public abstract StudyRoomGroupDao studyRoomGroupDao();

    public abstract StudyRoomDao studyRoomDao();

    public abstract NotificationDao notificationDao();

    public abstract TransportDao transportDao();

    public abstract ChatRoomDao chatRoomDao();

    public abstract ScheduledNotificationsDao scheduledNotificationsDao();

    public abstract ActiveAlarmsDao activeNotificationsDao();

    /**
     * Drop all tables, so we can do a complete clean start
     * Careful: After executing this method, almost all the managers are in an illegal state, and
     * can't do any SQL anymore. So take care to actually reinitialize all Managers
     *
     * @param c context
     */
    public static void resetDb(Context c) {
        TcaDb.getInstance(c).clearAllTables();
    }

}

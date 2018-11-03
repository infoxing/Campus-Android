package de.tum.in.tumcampusapp.database.daos;

import org.joda.time.DateTime;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import de.tum.in.tumcampusapp.model.calendar.CalendarItem;

@Dao
public interface CalendarDao {
    @Query("SELECT c.* FROM calendar c WHERE status != 'CANCEL'")
    List<CalendarItem> getAllNotCancelled();

    @Query("SELECT c.* FROM calendar c WHERE dtstart LIKE '%' || :date || '%' ORDER BY dtstart ASC")
    List<CalendarItem> getAllByDate(DateTime date);

    @Query("SELECT c.* FROM calendar c WHERE dtend BETWEEN :from AND :to "
            + "ORDER BY dtstart, title, location ASC")
    List<CalendarItem> getAllBetweenDates(DateTime from, DateTime to);

    @Query("SELECT c.* FROM calendar c WHERE dtend BETWEEN :from AND :to "
            + "AND STATUS != 'CANCEL'"
            + "ORDER BY dtstart, title, location ASC")
    List<CalendarItem> getAllNotCancelledBetweenDates(DateTime from, DateTime to);

    @Query("SELECT c.* FROM calendar c WHERE dtend BETWEEN :from AND :to "
            + "AND STATUS != 'CANCEL'"
            + "AND NOT EXISTS (SELECT * FROM widgets_timetable_blacklist WHERE widget_id = :widgetId"
            + "                AND lecture_title = c.title)"
            + "ORDER BY dtstart ASC")
    List<CalendarItem> getNextDays(DateTime from, DateTime to, String widgetId);

    @Query("SELECT c.* FROM calendar c WHERE datetime('now', 'localtime') BETWEEN dtstart AND dtend AND status != 'CANCEL' ORDER BY title")
    List<CalendarItem> getCurrentLectures();

    @Query("SELECT COUNT(*) FROM calendar")
    boolean hasLectures();

    @Query("SELECT c.* FROM calendar c, widgets_timetable_blacklist " +
            "WHERE widget_id=:widgetId AND lecture_title=title " +
            "GROUP BY title")
    List<CalendarItem> getLecturesInBlacklist(String widgetId);

    @Query("SELECT c.* FROM calendar c " +
            "WHERE NOT EXISTS (SELECT * FROM widgets_timetable_blacklist " +
            "WHERE widget_id=:widgetId AND c.title=lecture_title) " +
            "GROUP BY c.title")
    List<CalendarItem> getLecturesNotInBlacklist(String widgetId);

    @Query("DELETE FROM calendar")
    void flush();

    @Query("DELETE FROM calendar WHERE nr=:eventNr")
    void delete(String eventNr);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CalendarItem... cal);

    @Query("SELECT c.* " +
            "FROM calendar c LEFT JOIN room_locations r ON " +
            "c.location=r.title " +
            "WHERE coalesce(r.latitude, '') = '' " +
            "GROUP BY c.location")
    List<CalendarItem> getLecturesWithoutCoordinates();

    @Query("SELECT c.* FROM calendar c JOIN " +
            "(SELECT dtstart AS maxstart FROM calendar WHERE status!='CANCEL' AND datetime('now', 'localtime')<dtstart " +
            "ORDER BY dtstart LIMIT 1) ON status!='CANCEL' AND datetime('now', 'localtime')<dtend AND dtstart<=maxstart " +
            "ORDER BY dtend, dtstart LIMIT 4")
    List<CalendarItem> getNextCalendarItems();

    @Query("SELECT * FROM calendar " +
            "WHERE status!='CANCEL' " +
            "AND dtstart > datetime('now', 'localtime') " +
            "GROUP BY title, dtstart, dtend " +
            "ORDER BY dtstart LIMIT 4")
    List<CalendarItem> getNextUniqueCalendarItems();

    @Query("SELECT location FROM calendar "
            + "WHERE title = (SELECT title FROM calendar WHERE nr=:id) "
            + "AND dtstart = (SELECT dtstart FROM calendar WHERE nr=:id) "
            + "AND dtend = (SELECT dtend FROM calendar WHERE nr=:id) "
            + "AND status != 'CANCEL' "
            + "ORDER BY location ASC")
    List<String> getNonCancelledLocationsById(String id);

    @Query("SELECT * FROM calendar WHERE nr=:id"
            + " UNION "
            + "SELECT * FROM calendar "
            + "WHERE title = (SELECT title FROM calendar WHERE nr=:id) "
            + "AND dtstart = (SELECT dtstart FROM calendar WHERE nr=:id) "
            + "AND dtend = (SELECT dtend FROM calendar WHERE nr=:id) "
            + "AND nr != :id "
            + "ORDER BY location ASC")
    List<CalendarItem> getCalendarItemsById(String id);

    @Query("SELECT * FROM calendar WHERE nr=:id")
    CalendarItem getCalendarItemById(String id);
}

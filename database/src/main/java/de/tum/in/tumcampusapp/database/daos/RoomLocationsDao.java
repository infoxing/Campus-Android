package de.tum.in.tumcampusapp.database.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import de.tum.in.tumcampusapp.model.lecture.RoomLocations;

@Dao
public interface RoomLocationsDao {
    @Query("SELECT r.* " +
           "FROM calendar c, room_locations r " +
           "WHERE datetime('now', 'localtime') < datetime(c.dtstart, '+1800 seconds') AND " +
           "datetime('now','localtime') < c.dtend AND r.title == c.location AND c.status!='CANCEL'" +
           "ORDER BY dtstart LIMIT 1")
    RoomLocations getNextLectureCoordinates();

    @Insert
    void insert(RoomLocations roomLocations);

    @Query("DELETE FROM room_locations")
    void flush();
}
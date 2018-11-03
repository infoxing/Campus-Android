package de.tum.in.tumcampusapp.component.ui.ticket;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import de.tum.in.tumcampusapp.component.ui.ticket.model.RawEvent;

@Dao
public interface EventDao {

    @Query("SELECT * FROM events ORDER BY start_time")
    LiveData<List<RawEvent>> getAll();

    @Query("SELECT * FROM events WHERE start_time > date('now') ORDER BY start_time LIMIT 1")
    RawEvent getNextEvent();

    @Query("SELECT * FROM events where id = :id")
    RawEvent getEventById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<RawEvent> events);

    @Query("UPDATE events SET dismissed = 1 WHERE id = :eventId")
    void setDismissed(int eventId);

    @Query("DELETE FROM events WHERE start_time < date('now')")
    void removePastEvents();

    @Query("DELETE FROM events")
    void removeAll();

}

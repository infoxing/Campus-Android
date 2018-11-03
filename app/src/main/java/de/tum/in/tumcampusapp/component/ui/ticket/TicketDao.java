package de.tum.in.tumcampusapp.component.ui.ticket;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import de.tum.in.tumcampusapp.component.ui.ticket.model.RawTicket;

@Dao
public interface TicketDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(RawTicket... ticket);

    @Query("SELECT * FROM tickets")
    LiveData<List<RawTicket>> getAll();

    @Query("SELECT * FROM tickets where event_id = :eventId")
    RawTicket getByEventId(int eventId);

    @Query("DELETE FROM tickets")
    void flush();
}

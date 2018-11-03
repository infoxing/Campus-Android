package de.tum.in.tumcampusapp.database.daos;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import de.tum.in.tumcampusapp.model.ticket.TicketType;

@Dao
public interface TicketTypeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<TicketType> ticketTypes);

    @Query("SELECT * FROM ticket_types")
    List<TicketType> getAll();

    @Query("SELECT * FROM ticket_types WHERE id = :id")
    TicketType getById(int id);

    @Query("DELETE FROM ticket_types")
    void flush();
}

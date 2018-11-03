package de.tum.in.tumcampusapp.database.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import de.tum.in.tumcampusapp.model.transportation.TransportFavorites;
import de.tum.in.tumcampusapp.model.transportation.WidgetsTransport;

@Dao
public interface TransportDao {

    @Query("SELECT EXISTS(SELECT * FROM transport_favorites WHERE symbol = :symbol)")
    boolean isFavorite(String symbol);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addFavorite(TransportFavorites transportFavorites);

    @Query("DELETE FROM transport_favorites WHERE symbol = :symbol")
    void deleteFavorite(String symbol);

    @Query("SELECT * FROM widgets_transport WHERE id = :id")
    WidgetsTransport getAllWithId(int id);

    @Query("DELETE FROM widgets_transport WHERE id = :id")
    void deleteWidget(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void replaceWidget(WidgetsTransport widgetsTransport);

    @Query("DELETE FROM transport_favorites")
    void removeCache();
}

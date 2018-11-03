package de.tum.in.tumcampusapp.component.ui.tufilm;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import de.tum.in.tumcampusapp.component.ui.tufilm.model.RawKino;
import io.reactivex.Flowable;

@Dao
public interface KinoDao {

    /**
     * Removes all old items
     */
    @Query("DELETE FROM kino WHERE date < date('now')")
    void cleanUp();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(RawKino kino);

    @Query("SELECT * FROM kino ORDER BY date")
    Flowable<List<RawKino>> getAll();

    @Query("SELECT id FROM kino ORDER BY id DESC LIMIT 1")
    String getLatestId();

    @Query("SELECT count(*) FROM kino WHERE date < :date")
    int getPosition(String date);

    @Query("SELECT * FROM kino ORDER BY date LIMIT 1 OFFSET :position")
    Flowable<RawKino> getByPosition(int position);

    @Query("DELETE FROM kino")
    void flush();
}

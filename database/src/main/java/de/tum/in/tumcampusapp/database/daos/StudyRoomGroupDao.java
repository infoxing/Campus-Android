package de.tum.in.tumcampusapp.database.daos;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import de.tum.in.tumcampusapp.model.studyroom.StudyRoomGroup;

@Dao
public interface StudyRoomGroupDao {

    @Query("SELECT * FROM study_room_groups")
    List<StudyRoomGroup> getAll();

    @Query("DELETE FROM study_room_groups")
    void removeCache();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(StudyRoomGroup... studyRoomGroup);

}

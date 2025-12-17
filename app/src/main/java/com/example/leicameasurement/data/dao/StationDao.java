package com.example.leicameasurement.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.leicameasurement.data.entity.Station;

import java.util.List;

@Dao
public interface StationDao {
    @Insert
    void insert(Station station);

    @Query("SELECT * FROM stations WHERE project_id_fk = :projectId ORDER BY station_id ASC")
    LiveData<List<Station>> getStationsForProject(long projectId);
}



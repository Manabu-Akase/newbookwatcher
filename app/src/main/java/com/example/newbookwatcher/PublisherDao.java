package com.example.newbookwatcher;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface PublisherDao {

    @Insert
    void insert(Publisher publisher);

    @Query("SELECT * FROM publishers")
    List<Publisher> getAllPublisher();

    @Query("SELECT publisherName FROM publishers WHERE publisherId = :id")
    String getPublisherNameById(int id);

}

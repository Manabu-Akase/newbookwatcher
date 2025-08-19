package com.example.newbookwatcher;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AuthorDao {

    @Insert
    long insert(Author author);

    @Query("SELECT * FROM authors")
    List<Author> getAllAuthors();

    @Query("SELECT authorName FROM authors WHERE authorId = :id")
    String getAuthorNameById(int id);

}

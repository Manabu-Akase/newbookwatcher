package com.example.newbookwatcher;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface BookDao {

    @Insert
    void insertBook(Book book);

    @Update
    void update(Book book);

    @Delete
    void delete(Book book);

    @Query("SELECT * FROM books ORDER BY added_date DESC")
    List<Book> getAllBooks();

    @Query("SELECT * FROM books WHERE title LIKE '%' || :keyword || '%'")
    List<Book> searchBookByTitle(String keyword);

}

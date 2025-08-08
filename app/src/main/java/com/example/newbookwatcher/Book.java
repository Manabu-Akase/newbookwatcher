package com.example.newbookwatcher;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName ="books")
public class Book {

    @PrimaryKey(autoGenerate = true)
    public int bookId ;

    @ColumnInfo(name = "title")
    public String title ;

    @ColumnInfo(name = "authorId")
    public int authorId ;

    @ColumnInfo(name = "publisherId")
    public int publisherId ;

    @ColumnInfo(name = "release_date")
    public Date release_date ;

    @ColumnInfo(name = "image_url")
    public String image_url ;

    @ColumnInfo(name = "added_date")
    public  Date added_date ;

    @ColumnInfo(name ="isFavorite")
    public Boolean isFavorite ;
}

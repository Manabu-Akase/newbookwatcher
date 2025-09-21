package com.example.newbookwatcher;
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName ="books")
public class Book {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name ="bookId")
    public int bookId ;

    @ColumnInfo(name = "title")
    public String title ;

    /*中間テーブルで管理するため削除
    @ColumnInfo(name = "authorId")
    public int authorId ;
     */

    @ColumnInfo(name = "publisherId")
    public int publisherId ;

    @ColumnInfo(name = "release_date")
    public Date release_date ;

    @ColumnInfo(name = "image_url")
    public String image_url ;

    @ColumnInfo(name="price")
    public int price;


    @ColumnInfo(name = "added_date")
    public Date added_date ;

    @ColumnInfo(name ="isFavorite")
    public boolean isFavorite ;
    @ColumnInfo(name= "isbn")
    public String isbn ;
}

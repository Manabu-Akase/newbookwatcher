package com.example.newbookwatcher;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {Book.class, Author.class, Publisher.class}, version = 2)
@TypeConverters({Converters.class})

public abstract class AppDatabase extends RoomDatabase {
    public abstract BookDao bookDao();
    public abstract AuthorDao authorDao();
    public abstract PublisherDao publisherDao();

}

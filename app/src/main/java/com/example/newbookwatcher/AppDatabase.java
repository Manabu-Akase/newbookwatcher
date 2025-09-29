package com.example.newbookwatcher;
import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {Book.class, Author.class, Publisher.class,BookAuthorsCrossRef.class}, version = 3)
@TypeConverters({Converters.class})

public abstract class AppDatabase extends RoomDatabase {
    public abstract BookDao bookDao();
    public abstract AuthorDao authorDao();
    public abstract PublisherDao publisherDao();
    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context){
        if (INSTANCE == null){
            synchronized (AppDatabase.class) {
                INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(),
                        AppDatabase.class,
                        "book-database"
                ).build();

            }
        }return INSTANCE;
    }
}

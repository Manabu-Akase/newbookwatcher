package com.example.newbookwatcher;

import android.os.Bundle;
import android.service.media.MediaBrowserService;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.example.newbookwatcher.BookAdapter;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Date;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private BookAdapter bookAdapter;
    private Map<Integer,String> authorMap = new HashMap<>();
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.search_result);


        db = Room.databaseBuilder(
                getApplicationContext(),
                AppDatabase.class,
                "book-database").build();

        AppDatabase db = Room.databaseBuilder(
                        getApplicationContext(),
                        AppDatabase.class,
                        "book-database")
                .fallbackToDestructiveMigration() // ← これを追加！
                .build();

        RecyclerView recyclerView = findViewById (R.id.recyclerViewResults);

        bookAdapter = new BookAdapter(new ArrayList<>(),this ,authorMap);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(bookAdapter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Author> authors = db.authorDao().getAllAuthors();
                for (Author author: authors){
                    authorMap.put(author.authorId,author.name+"("+ author.authorKana+")");
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });

            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Book testBook = new Book();
                testBook.title = "サンプル本";
                testBook.authorId = 1;
                testBook.publisherId = 1;
                testBook.release_date = new Date();  // 今日の日付
                testBook.image_url = "https://example.com/sample.jpg";
                testBook.added_date = new Date();

                db.bookDao().insertBook(testBook);
            }
        }).start();


        Button searchButton = findViewById(R.id.SearchButton);
        EditText searchBox = findViewById(R.id.editTextSearch);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String keyword = searchBox.getText().toString().trim();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<Book> result = db.bookDao().searchBookByTitle("%"+keyword+"%");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                bookAdapter.updateData(result);
                            }
                        });
                    }
                }).start();
            }
        });
    }
}
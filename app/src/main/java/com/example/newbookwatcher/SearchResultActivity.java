package com.example.newbookwatcher;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

public class SearchResultActivity extends AppCompatActivity {

    private BookAdapter bookAdapter;
    private Map<Integer,String> authorMap = new HashMap<>();
    private AppDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.search_result);

        //戻るボタンが押された時の処理
        Button returnButton = findViewById(R.id.returnButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //お気に入り一覧ボタンが押された時の処理
        Button favoriteListButton =findViewById(R.id.favoriteListButton);
        favoriteListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchResultActivity.this, FavoriteActivity.class);
                startActivity(intent);
            }
        });

        //データベースを入れる
        db = Room.databaseBuilder(
                getApplicationContext(),
                AppDatabase.class,
                "book-database")
                .fallbackToDestructiveMigration()
                .build();

        //リサイクラービューの処理
        RecyclerView recyclerView = findViewById (R.id.recyclerViewResults);

        bookAdapter = new BookAdapter(new ArrayList<>(),this ,authorMap,db);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(bookAdapter);


        //著者一覧データベースからを取得してauthorMapに保存する処理
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Author> authors = db.authorDao().getAllAuthors();
                for (Author author: authors){
                    authorMap.put(author.authorId,author.name+"("+ author.authorKana+")");
                }

                //画面を止めないため、UI更新をメインスレッドで
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });

            }
        }).start();

        //テスト用の本のデータをデータベースに登録する処理
        new Thread(new Runnable() {
            @Override
            public void run() {
                //サンプル用の本を作成
                Book testBook = new Book();
                testBook.title = "サンプル本";
                testBook.authorId = 1;
                testBook.publisherId = 1;
                testBook.release_date = new Date();
                testBook.image_url = "https://example.com/sample.jpg";
                testBook.added_date = new Date();

                db.bookDao().insertBook(testBook);
            }
        }).start();

        // メニュー画面から検索された本のタイトルをテキストビューで表示する処理
        TextView SearchResultTitle = findViewById(R.id.tvSearchTitle);
        //キーワードを取得して表示
        String keyword = getIntent().getStringExtra("keyword");
        SearchResultTitle.setText("検索:"+keyword);


        /*検索欄にタイトルを入れる＋検索ボタンを押した時の処理 →入力したキーワードで本を探す
        → 結果を画面のリストに表示する。 */

        Button searchButton = findViewById(R.id.SearchButton);
        EditText searchBox = findViewById(R.id.editTextSearch);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //入力されたキーワードの取得
                final String keyword = searchBox.getText().toString().trim();

                //スレッドでタイトルにキーワードが含まれる本を検索する
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
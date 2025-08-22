package com.example.newbookwatcher;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.util.Log;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FavoriteActivity extends AppCompatActivity {

    private FavoriteAdapter favoriteAdapter;
    private Map<Integer,String>authorMap = new HashMap<>();
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.favorite_list);

    //データベースの準備
    db = Room.databaseBuilder(
    getApplicationContext(),
    AppDatabase.class,
            "book-database").build();

    //リサイクラービューの設定→アダプターの作成と処理、縦に並べる
    RecyclerView recyclerView = findViewById(R.id.favoriteBookList);

    favoriteAdapter = new FavoriteAdapter(new ArrayList<BookWithAuthors>(), FavoriteActivity.this, authorMap, new FavoriteAdapter.OnFavoriteChangeListener() {
        //コールバックを追加、お気に入り状態を再読み込みして表示。
        @Override
        public void onFavoriteChanged() {
            loadFavorites();
        }
    });
        recyclerView.setAdapter(favoriteAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(FavoriteActivity.this));

    //戻るボタンを押した時に前の画面に戻る処理
    Button returnButton = findViewById(R.id.returnButton);
    returnButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //
            finish();
        }
    });

    //通知設定ボタンを押した際に通知設定画面に遷移する処理
    Button notificationButton = findViewById(R.id.notificationButton);
        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FavoriteActivity.this, NotificationSettingActivity.class);
                startActivity(intent);
            }
        });

        //お気に入りの本を読み込んで表示するメソッド
        loadFavorites();
    }
    //お気に入り登録された本、著者をデータベースから呼び出す処理
    private void loadFavorites(){
        //スレッドでデータを読み込む
        new Thread(new Runnable() {

            @Override
            public void run() {
                //著者情報をデータベースから全部取り出す　→　マップ著者idと名前（＋カナ）を入れる
                List<Author> authors = db.authorDao().getAllAuthors();
                for (Author author : authors){
                    authorMap.put(author.authorId, author.authorName+"("+author.authorKana+")");
                }

                //お気に入りされた本をデータベースから取り出す
                List<BookWithAuthors> favorites = db.bookDao().getFavoriteBooksWithAuthors();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        favoriteAdapter.updateData(favorites);
                    }
                });
            }
        }).start();
    }
}

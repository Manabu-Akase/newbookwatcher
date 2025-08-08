package com.example.newbookwatcher;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);


        //検索欄にタイトルを入れて検索ボタンを押すと検索結果一覧画面で本を表示する処理(APIは未実装、画面遷移のみ)
        Button seaerchButton = findViewById(R.id.SearchButton);
        EditText searchBox = findViewById(R.id.editTextSearch);

        seaerchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String keyword = searchBox.getText().toString().trim();

                //検索欄にタイトルを入れる＋検索ボタンを押した時の処理→入力したキーワードで本を探す、結果を画面のリストに表示する。
                Button searchButton = findViewById(R.id.SearchButton);
                EditText searchBox = findViewById(R.id.editTextSearch);

                searchButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //入力されたキーワードの取得
                        final String keyword = searchBox.getText().toString().trim();

                        //入力されたキーワードからタイトルを取得して画面遷移(intent)
                        //テキストボックスが空白時の判定 → .isEmpty()の否定がなにかしら入力されている状態！
                        if (!keyword.isEmpty()) {
                            Intent intent = new Intent(MenuActivity.this,SearchResultActivity.class);
                            intent.putExtra("keyword",keyword);
                            startActivity(intent);

                            //エラー文を出す
                        } else {searchBox.setError("検索する本のタイトルを入力してください");


                        }
                    }
                });
            }
        });

        //お気に入り一覧画面に遷移するボタンの処理
        Button favoriteListButton =findViewById(R.id.favoriteListButton);
        favoriteListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(MenuActivity.this, FavoriteActivity.class);
                startActivity(intent);
            }
        });

        //通知設定画面に遷移するボタンの処理
        Button notificationButton = findViewById(R.id.notificationButton);
        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, NotificationSettingActivity.class);
                startActivity(intent);
            }
        });
    }
}
package com.example.newbookwatcher;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class SearchResultActivity extends AppCompatActivity {
    private AppDatabase db;
    private RakutenBookAdapter rakutenBookAdapter;

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
        rakutenBookAdapter = new RakutenBookAdapter(new ArrayList<>(), this , db);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(rakutenBookAdapter);

        // メニュー画面から検索された本のタイトルをテキストビューで表示する処理
        TextView searchResultTitle = findViewById(R.id.tvSearchTitle);
        //キーワードを取得して表示
        String keyword = getIntent().getStringExtra("keyword");
        searchResultTitle.setText("検索結果:" + keyword);

        if (keyword != null && !keyword.isEmpty()){
            fetchBooksFromApi(keyword);
        }

        /*検索欄にタイトルを入れる＋検索ボタンを押した時の処理 →入力したキーワードで本を探す
        → 結果を画面のリストに表示する。 */

        Button searchButton = findViewById(R.id.SearchButton);
        EditText searchBox = findViewById(R.id.editTextSearch);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //入力されたキーワードの取得
                String keyword = searchBox.getText().toString().trim();
                fetchBooksFromApi(keyword);
            }
        });
    }
    private void fetchBooksFromApi(String keyword) {
        //楽天APIを紐づける処理
        String appId = "1033766590408149408";
        RakutenBooksApi api = RakutenApiClient.getApiService();
        Call<RakutenBookResponse> call = api.searchBooks(appId, keyword);

        call.enqueue(new Callback<RakutenBookResponse>() {
            @Override
            public void onResponse(Call<RakutenBookResponse> call, Response<RakutenBookResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<RakutenBookWrapper> items = response.body().Items;
                    List<RakutenItem> rakutenItems = new ArrayList<>();
                    for (RakutenBookWrapper wrapper : items) {
                        RakutenItem item = wrapper.item;
                        rakutenItems.add(item);
                        Log.d("API", "タイトル" + item.title);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            rakutenBookAdapter.updateData(rakutenItems);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<RakutenBookResponse> call, Throwable t) {
                Log.e("API", "エラー");
                if (t != null) {
                    Log.e("API", "Throwableメッセージ:" + t.getMessage());
                    t.printStackTrace();
                } else {
                    Log.e("API", "Throwableがnull");
                }
            }
        });
    }
}
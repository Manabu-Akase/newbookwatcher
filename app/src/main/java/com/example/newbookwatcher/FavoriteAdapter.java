package com.example.newbookwatcher;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Map;

import kotlinx.coroutines.flow.LintKt;

//Adapterクラスを作成
public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {

    private List<BookWithAuthors> FavoriteBookList;
    private Context context;
    private AppDatabase db;
    private OnFavoriteChangeListener favoriteChangeListener ;

public FavoriteAdapter(List<BookWithAuthors> FavoriteBookList,Context context,OnFavoriteChangeListener favoriteChangeListener){
    this.FavoriteBookList = FavoriteBookList;
    this.context = context ;
    this.db = Room.databaseBuilder(context.getApplicationContext(),AppDatabase.class,"book-database").build();
    this.favoriteChangeListener = favoriteChangeListener;
}

//ViewHolderクラスを作成
public static class ViewHolder extends RecyclerView.ViewHolder{

    ImageView imageView ;
    TextView tvBookTitle,tvAuthor,tvDate;
    ImageButton favoriteButton;

    public ViewHolder(View itemView){
        super(itemView);
        imageView= itemView.findViewById(R.id.imageView);
        tvBookTitle= itemView.findViewById(R.id.tvBookTitle);
        tvAuthor=itemView.findViewById(R.id.tvAuthor);
        tvDate=itemView.findViewById(R.id.tvDate);
        favoriteButton=itemView.findViewById(R.id.favoriteButton);

    }
}
    //コールバックのインターフェース追加
    public interface OnFavoriteChangeListener{
        void onFavoriteChanged();
    }

@Override
    public FavoriteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_item,parent,false );
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull FavoriteAdapter.ViewHolder holder , int position){
        Log.d("!!!!!","NULL");
        BookWithAuthors bookWithAuthors = FavoriteBookList.get(position);
        Book book = bookWithAuthors.book;

        //書籍情報の表示
        holder.tvBookTitle.setText(book.title);

        if(book.release_date != null) {
            holder.tvDate.setText(book.release_date.toString());
        }else{
            holder.tvDate.setText("日付未定");
        }

        //複数著者をカンマ繋げで表示する処理

        StringBuilder sb = new StringBuilder();
        for (int i =0; i < bookWithAuthors.authors.size(); i++){
            sb.append(bookWithAuthors.authors.get(i).authorName);
            if (i<bookWithAuthors.authors.size()-1){
                sb.append(",");
            }
        }
        //Glideで取得した本の画像を表示する処理
        if (book.image_url != null && !book.image_url.isEmpty()){
            Glide.with(context)
                    .load(book.image_url)
                    .placeholder(android.R.drawable.ic_menu_gallery)    //読み込み中の画像
                    .error(android.R.drawable.ic_delete)         //エラー時の画像
                    .into(holder.imageView);
        }else{
            holder.imageView.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        holder.tvAuthor.setText(sb.toString());

        //お気に入りアイコンの初期状態
        holder.favoriteButton.setImageResource(
                book.isFavorite ? android.R.drawable.star_on : android.R.drawable.star_off);

        //アイコンが押されたらお気に入り解除、リストからも削除する処理
        holder.favoriteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
               boolean newStatus = !book.isFavorite;
               book.isFavorite = newStatus;

               new Thread(new Runnable() {
                   @Override
                   public void run() {
                       // DB更新をバックグラウンドで実行
                       db.bookDao().updateFavorite(book.bookId, book.isFavorite);
                       // UI操作はUIスレッドで実行
                       ((FavoriteActivity) context).runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                               if (newStatus) {
                                   // お気に入り追加
                                   ReminderScheduler.scheduleReminder(context, book, null);
                                   ReminderScheduler.scheduleTestReminder(context, book);
                                   Toast.makeText(context, "お気に入りに追加されました", Toast.LENGTH_SHORT).show();
                                   holder.favoriteButton.setImageResource(android.R.drawable.star_on);
                               } else {
                                   // お気に入り解除
                                   ReminderScheduler.cancelBookReminder(context, book.isbn);
                                   Toast.makeText(context, "お気に入りから削除されました！", Toast.LENGTH_SHORT).show();
                                   holder.favoriteButton.setImageResource(android.R.drawable.star_off);

                                   // リストから削除
                                   int position = holder.getAdapterPosition();
                                   if (position != RecyclerView.NO_POSITION) {
                                       FavoriteBookList.remove(position);
                                       notifyItemRemoved(position);
                                       notifyItemRangeChanged(position, FavoriteBookList.size());
                                   }
                               }

                               if (favoriteChangeListener != null) {
                                            favoriteChangeListener.onFavoriteChanged();
                               }
                           }
                       });
                   }
               }).start();
            }
        });
    }
    @Override
    public int getItemCount(){
        return FavoriteBookList.size();
    }
    //お気に入り情報を更新する
    public void updateData(List<BookWithAuthors> newFavoriteBookList){
        FavoriteBookList.clear();
        FavoriteBookList.addAll(newFavoriteBookList) ;
        notifyDataSetChanged();

    }
}

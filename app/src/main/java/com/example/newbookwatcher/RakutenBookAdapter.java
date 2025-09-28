package com.example.newbookwatcher;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class RakutenBookAdapter extends RecyclerView.Adapter<RakutenBookAdapter.ViewHolder> {
    private List<RakutenItem>itemList;
    private Context context ;
    private AppDatabase db;

    public RakutenBookAdapter(List<RakutenItem>itemList,Context context,AppDatabase database){
        this.itemList = itemList;
        this.context = context;
        this.db = database;
    }
    public void updateData(List<RakutenItem> newList){
        itemList.clear();
        itemList.addAll(newList);
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(context).inflate(R.layout.book_item , parent ,false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,int position){
        Log.d("!!!!!","NULL");
        RakutenItem item = itemList.get(position);
        holder.title.setText(item.title);
        holder.author.setText(item.author);
        holder.date.setText(item.salesDate);

        //Glideで画像を読み込む処理
        //largeImageUrlがないときはsmallImageUrlを使う,どちらもない時は❌を表示
        String imageUrl = (item.largeImageUrl != null && item.largeImageUrl.isEmpty())
                ? item.largeImageUrl
                : item.smallImageUrl;

        Glide.with(context)
                .load(imageUrl) //楽天APIから取得
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_delete)
                .into(holder.imageView);

        new Thread(new Runnable() {
            @Override
            public void run() {
                //DBからお気に入り状態を取得して反映する
                Book book = db.bookDao().findBookByIsbn(item.isbn);
                boolean isFavorite = (book != null && book.isFavorite);

                holder.itemView.post(new Runnable() {
                    @Override
                    public void run() {
                        holder.favoriteButton.setImageResource(
                                isFavorite ? android.R.drawable.star_on : android.R.drawable.star_off
                        );
                    }
                });
            }
        }).start();

        holder.favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Book book = db.bookDao().findBookByIsbn(item.isbn);
                        final boolean newStatus;

                        if(book == null){
                            Book newBook = new Book();
                            newBook.title = item.title;
                            newBook.isbn = item.isbn;
                            newBook.isFavorite = true ;
                            db.bookDao().insertBook(newBook);
                            newStatus = true ;
                        } else {
                            newStatus = !book.isFavorite;
                            db.bookDao().updateFavorite(book.bookId, newStatus);
                        }

                        holder.itemView.post(new Runnable() {
                            @Override
                            public void run() {
                                holder.favoriteButton.setImageResource(
                                        newStatus ? android.R.drawable.star_on : android.R.drawable.star_off
                                );
                            }
                        });
                    }
                }).start();
            }
        });
    }

    @Override
    public int getItemCount(){
        return itemList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, author, date;
        ImageButton favoriteButton;
        ImageView imageView ;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvBookTitle);
            author = itemView.findViewById(R.id.tvAuthor);
            date = itemView.findViewById(R.id.tvDate);
            favoriteButton = itemView.findViewById(R.id.favoriteButton);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}

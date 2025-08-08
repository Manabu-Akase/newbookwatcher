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
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import java.util.List;
import java.util.Map;

//Adapterクラスを作成
public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {

    private List<Book> FavoriteBookList;
    private Context context;
    private Map<Integer,String>authorMap;
    private AppDatabase db;

public FavoriteAdapter(List<Book> FavoriteBookList,Context context,Map<Integer,String> authorMap){
    this.FavoriteBookList = FavoriteBookList;
    this.context = context ;
    this.authorMap = authorMap ;
    this.db = Room.databaseBuilder(context.getApplicationContext(),AppDatabase.class,"book-database").build();

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

@Override
    public FavoriteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_item,parent,false );
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull FavoriteAdapter.ViewHolder holder , int position){
        Book book = FavoriteBookList.get(position);
        holder.tvBookTitle.setText(book.title);
        holder.tvDate.setText(book.release_date.toString());
        holder.tvAuthor.setText(authorMap.get(book.authorId));
        holder.favoriteButton.setImageResource(android.R.drawable.star_on);

        //アイコンが押されたらお気に入り解除、リストからも削除する処理
        holder.favoriteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(book.isFavorite){

                    //お気に入りリストからなくなることをダイアログで忠告
                    AlertDialog.Builder builder = new AlertDialog.Builder(context)
                            .setCancelable(false)
                            .setTitle("お気に入り解除の確認")
                            .setMessage("『"+book.title+"』"+"をお気に入りを解除すると、このリストから削除されます。よろしいですか。")
                            .setPositiveButton("はい", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    book.isFavorite = false;

                                    //データベースを更新する
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            db.bookDao().updateFavorite(book.bookId, false);

                                        }
                                    }).start();

                                    //リストから削除する処理
                                    int removePosition = holder.getAdapterPosition();
                                    notifyItemRemoved(removePosition);

                                }

                                //ダイアログの中で「いいえ」を押すと何もせず本の画面に戻る処理
                            }).setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });builder.show();
                }

            }

        });

    }

    @Override
    public int getItemCount(){
        return FavoriteBookList.size();
    }

    public void updateData(List<Book> newFavoriteBookList){
        FavoriteBookList = newFavoriteBookList ;
        notifyDataSetChanged();

    }
}

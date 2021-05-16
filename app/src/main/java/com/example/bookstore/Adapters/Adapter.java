package com.example.bookstore.Adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookstore.Interfaces.OnItemClickListener;
import com.example.bookstore.Model.Author;
import com.example.bookstore.Model.BookItem;
import com.example.bookstore.R;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private Context mContext;
    private List<BookItem> mBookList;
    private OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public Adapter(Context context, List<BookItem> bookList) {
        mContext = context;
        mBookList = bookList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(mContext).inflate(R.layout.recycler_view_book_item, parent,
                false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final BookItem currentItem = mBookList.get(position);
        final List<Author> authors = currentItem.getAuthors();
        final StringBuilder sb2 = new StringBuilder();
        for (int i = 0; i < authors.size(); i++) {
            sb2.append(String.format("%s %s",
                    authors.get(i).getLastName(), authors.get(i).getFirstName()));
            if (i != authors.size() - 1) sb2.append(System.getProperty("line.separator"));
        }
        final String imageUrl = currentItem.getImageUrl();
        final String title = currentItem.getTitle();
        final double price = currentItem.getPrice();

        if (price == (long) price) holder.mPrice.setText(String.format(Locale.ENGLISH,
                "%d руб.", (long) price));
        else holder.mPrice.setText(String.format(Locale.ENGLISH,"%s руб.", price));
        holder.mAuthor.setText(String.format(Locale.ENGLISH,"%s", sb2.toString()));
        holder.mTitle.setText(String.format(Locale.ENGLISH,"%s",title));
        Picasso.get().load(imageUrl).fit().centerInside().into(holder.mBookImage);
    }

    @Override
    public int getItemCount() {
        return mBookList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mBookImage;
        public TextView mTitle, mAuthor, mPrice;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mBookImage = itemView.findViewById(R.id.recycler_image);
            mTitle = itemView.findViewById(R.id.recycler_title);
            mAuthor = itemView.findViewById(R.id.recycler_author);
            mPrice = itemView.findViewById(R.id.recycler_price);

            itemView.setOnClickListener(v -> {
                if (mListener != null) {
                    final int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) mListener.onItemClick(position);
                }
            });
        }
    }
}

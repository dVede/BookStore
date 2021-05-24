package com.example.bookstore.Adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookstore.Interfaces.OnItemButtonClickListener;
import com.example.bookstore.Interfaces.OnItemClickListener;
import com.example.bookstore.Model.Author;
import com.example.bookstore.Model.BookItem;
import com.example.bookstore.Model.CartItem;
import com.example.bookstore.R;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

import static com.example.bookstore.Utils.DOT_END_REGEX;
import static com.example.bookstore.Utils.DOT_REGEX;
import static com.example.bookstore.Utils.EMPTY_CHARACTER;
import static com.example.bookstore.Utils.TRAILING_ZERO_REGEX;
import static com.example.bookstore.Utils.TRAILING_ZERO_REPLACEMENT;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private Context mContext;
    private List<CartItem> mCartList;
    private OnItemClickListener mListener;
    private OnItemButtonClickListener mButtonListener;

    public void setOnItemButtonClickListener(OnItemButtonClickListener listener) {
        mButtonListener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public CartAdapter(Context context, List<CartItem> cartList) {
        mContext = context;
        mCartList = cartList;
    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(mContext).inflate(R.layout.recycler_view_cart_item, parent,
                false);
        return new CartAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, int position) {
        final CartItem currentItem = mCartList.get(position);
        final BookItem bookItem = currentItem.getBookItem();
        final List<Author> authors = bookItem.getAuthors();
        final StringBuilder sb2 = new StringBuilder();
        for (int i = 0; i < authors.size(); i++) {
            sb2.append(String.format("%s %s",
                    authors.get(i).getLastName(), authors.get(i).getFirstName()));
            if (i != authors.size() - 1) sb2.append(System.getProperty("line.separator"));
        }
        final String imageUrl = bookItem.getImageUrl();
        final String title = bookItem.getTitle();
        final double price = bookItem.getPrice();
        final double totalPrice = price * currentItem.getAmount();

        final String priceS = String.valueOf(totalPrice)
                .replaceAll(TRAILING_ZERO_REGEX, TRAILING_ZERO_REPLACEMENT)
                .replaceAll(DOT_END_REGEX, EMPTY_CHARACTER);
        holder.mPrice.setText(String.format(Locale.ENGLISH, "%s %s", priceS,
                mContext.getResources().getString(R.string.rubles)));
        holder.mAuthor.setText(String.format(Locale.ENGLISH,"%s", sb2.toString()));
        holder.mTitle.setText(String.format(Locale.ENGLISH,"%s",title));
        holder.mAmount.setText(String.format(Locale.ENGLISH,"%s", currentItem.getAmount()));
        Picasso.get().load(imageUrl).fit().centerInside().into(holder.mBookImage);
    }

    @Override
    public int getItemCount() {
        return mCartList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mBookImage;
        public TextView mTitle, mAuthor, mAmount, mPrice;
        public Button mDeleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mDeleteButton = itemView.findViewById(R.id.delete_button);
            mBookImage = itemView.findViewById(R.id.recycler_image);
            mTitle = itemView.findViewById(R.id.recycler_title);
            mAuthor = itemView.findViewById(R.id.recycler_author);
            mPrice = itemView.findViewById(R.id.recycler_price);
            mAmount = itemView.findViewById(R.id.amount);

            mDeleteButton.setOnClickListener(v -> {
                if (mButtonListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        mButtonListener.onItemButtonClick(position, v);
                    }
                }
            });

            itemView.setOnClickListener(v -> {
                if (mListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        mListener.onItemClick(position);
                    }
                }
            });
        }
    }
}

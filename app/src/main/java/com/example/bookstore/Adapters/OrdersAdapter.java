package com.example.bookstore.Adapters;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookstore.Interfaces.OnItemClickListener;
import com.example.bookstore.Model.OrderItem;
import com.example.bookstore.Model.OrderStatus;
import com.example.bookstore.R;
import com.example.bookstore.Utils;

import java.util.List;
import java.util.Locale;

import static com.example.bookstore.Utils.DOT_REGEX;
import static com.example.bookstore.Utils.DOT_END_REGEX;
import static com.example.bookstore.Utils.EMPTY_CHARACTER;
import static com.example.bookstore.Utils.TRAILING_ZERO_REGEX;
import static com.example.bookstore.Utils.TRAILING_ZERO_REPLACEMENT;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {
    private Context mContext;
    private List<OrderItem> mOrderList;
    private OnItemClickListener mListener;

    public OrdersAdapter(Context context, List<OrderItem> orderList) {
        mContext = context;
        mOrderList = orderList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public OrdersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(mContext).inflate(R.layout.recycler_view_order_item, parent,
                false);
        return new OrdersAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersAdapter.ViewHolder holder, int position) {
        final OrderItem orderItem = mOrderList.get(position);
        final String[] str = orderItem.getBookIds();
        final String timestamp = orderItem.getTimestamp();
        final OrderStatus orderStatus = orderItem.getStatus();
        final double totalPrice = orderItem.getTotalSum();
        final String bookIds = TextUtils.join(Utils.COMMA_DELIMITER_WITH_SPACE ,str);
        final String[] timestampList = timestamp.replace(Utils.TIMESTAMP_SEPARATOR,
                Utils.SPACE_CHARACTER).split(DOT_REGEX);
        final String priceS = String.valueOf(totalPrice)
                .replaceAll(TRAILING_ZERO_REGEX, TRAILING_ZERO_REPLACEMENT)
                .replaceAll(DOT_END_REGEX, EMPTY_CHARACTER);
        holder.mTimestamp.setText(timestampList[0]);
        holder.mStatus.setText(orderStatus.toString());
        holder.mArticles.setText(bookIds);
        holder.mTotalSum.setText(String.format(Locale.ENGLISH,"%s %s", priceS,
                mContext.getResources().getString(R.string.rubles)));
    }

    @Override
    public int getItemCount() {
        return mOrderList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mArticles, mTimestamp, mStatus, mTotalSum;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mArticles = itemView.findViewById(R.id.book_ids);
            mTimestamp = itemView.findViewById(R.id.timestamp);
            mStatus = itemView.findViewById(R.id.status);
            mTotalSum = itemView.findViewById(R.id.totalSum);
            itemView.setOnClickListener(v -> {
                if (mListener != null) {
                    final int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        mListener.onItemClick(position);
                    }
                }
            });
        }
    }
}

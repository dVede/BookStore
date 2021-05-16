package com.example.bookstore.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookstore.Model.Comment;
import com.example.bookstore.Model.CommentItem;
import com.example.bookstore.R;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {
    private Context mContext;
    private List<CommentItem> mCommentList;

    public CommentsAdapter(Context context, List<CommentItem> commentList) {
        mContext = context;
        mCommentList = commentList;
    }

    @NonNull
    @Override
    public CommentsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(mContext).inflate(R.layout.comment_item, parent,
                false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsAdapter.ViewHolder holder, int position) {
        final Comment comment = mCommentList.get(position).getComment();
        final String username  = mCommentList.get(position).getUsername();
        holder.mUser.setText(String.format("User: %s", username));
        holder.mComment.setText(comment.getComment());
    }

    @Override
    public int getItemCount() {
        return mCommentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mComment, mUser;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mComment = itemView.findViewById(R.id.comment_comment);
            mUser = itemView.findViewById(R.id.comment_username);
        }
    }
}

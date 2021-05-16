package com.example.bookstore.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookstore.Adapters.CommentsAdapter;
import com.example.bookstore.Model.CommentItem;
import com.example.bookstore.R;
import com.example.bookstore.Utils;
import com.example.bookstore.ViewModels.CommentsFragmentViewModel;

import java.util.ArrayList;
import java.util.List;

public class CommentsFragment extends Fragment implements View.OnClickListener {

    private CommentsAdapter mAdapter;
    private Context context;
    private EditText commentText;
    private List<CommentItem> mCommentItem;
    private CommentsFragmentViewModel commentsFragmentViewModel;
    private int bid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_comments, container, false);
        context = container.getContext();
        init(v);
        return v;
    }

    private void init(View v) {
        final RecyclerView mRecyclerView = v.findViewById(R.id.comment_recycler_view);
        final Button sendButton = v.findViewById(R.id.send_button);
        commentText = v.findViewById(R.id.comment_editText);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(v.getContext()));
        mCommentItem = new ArrayList<>();
        mAdapter = new CommentsAdapter(context, mCommentItem);
        mRecyclerView.setAdapter(mAdapter);
        commentsFragmentViewModel = new ViewModelProvider(this).get(CommentsFragmentViewModel.class);
        sendButton.setOnClickListener(this);
    }
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Bundle bundle = this.getArguments();
        if (bundle != null) {
            bid = bundle.getInt(Utils.EXTRA_ID, 0);
        }
        commentsFragmentViewModel.getBookComments(bid).observe(getViewLifecycleOwner(), commentItem -> {
            mCommentItem.clear();
            mCommentItem.addAll(commentItem);
            mAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onClick(View v) {
        String comment = commentText.getText().toString();
        if (comment.isEmpty() || comment.length() > 300 )
            if (comment.isEmpty()) commentText.setError("Empty");
            else commentText.setError("Too big comment");
        else {
            commentsFragmentViewModel.addComment(bid, comment);
            commentText.getText().clear();
        }
    }
}
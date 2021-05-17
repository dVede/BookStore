package com.example.bookstore.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookstore.Adapters.Adapter;
import com.example.bookstore.Interfaces.OnItemClickListener;
import com.example.bookstore.Model.BookItem;
import com.example.bookstore.R;
import com.example.bookstore.Utils;
import com.example.bookstore.ViewModels.BookItemViewModel;

import java.util.ArrayList;
import java.util.List;

public class BookFragment extends Fragment implements OnItemClickListener {
    private Adapter mAdapter;
    private List<BookItem> mBookList;
    private Context context;
    private BookItemViewModel mViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.recycler_fragment, container, false);
        context = container.getContext();
        init(v);
        return v;
    }

    @Override
    public void onItemClick(int position) {
        final BookItem bookItem = mBookList.get(position);
        final Bundle bundle = new Bundle();
        bundle.putInt(Utils.EXTRA_ID,bookItem.getId());
        final BookDetailFragment bookDetailFragment = new BookDetailFragment();
        bookDetailFragment.setArguments(bundle);
        final FragmentTransaction fTrans = getParentFragmentManager().beginTransaction();
        fTrans.addToBackStack(null);
        fTrans.replace(R.id.fragment_container_view, bookDetailFragment).commit();
    }

    private void init(View v) {
        final RecyclerView mRecyclerView = v.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(v.getContext(),2);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mBookList = new ArrayList<>();
        mAdapter = new Adapter(context, mBookList);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        mViewModel = new ViewModelProvider(this).get(BookItemViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LiveData<List<BookItem>> liveData = mViewModel.getBookItem();
        liveData.observe(getViewLifecycleOwner(), bookItems -> {
            mBookList.clear();
            mBookList.addAll(bookItems);
            mAdapter.notifyDataSetChanged();
        });
    }
}
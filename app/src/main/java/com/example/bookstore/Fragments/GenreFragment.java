package com.example.bookstore.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookstore.Adapters.Adapter;
import com.example.bookstore.Interfaces.OnItemClickListener;
import com.example.bookstore.Model.BookItem;
import com.example.bookstore.Model.Genre;
import com.example.bookstore.Model.GenreItem;
import com.example.bookstore.R;
import com.example.bookstore.Utils;
import com.example.bookstore.ViewModels.GenreFragmentViewModel;

import java.util.ArrayList;
import java.util.List;

public class GenreFragment extends Fragment implements OnItemClickListener {

    private Adapter mAdapter;
    private Context context;
    private List<BookItem> mBookList;
    private TextView genreName;
    private GenreFragmentViewModel genreFragmentViewModel;
    private int gid;

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState);}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_genre, container, false);
        context = container.getContext();
        init(v);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Bundle bundle = this.getArguments();
        if (bundle != null) {
            gid = bundle.getInt(Utils.EXTRA_ID, 0);
        }
        MutableLiveData<GenreItem> data = genreFragmentViewModel.getBookItem(gid);
        data.observe(getViewLifecycleOwner(), genreItems -> {
            final Genre genre = genreItems.getGenre();
            genreName.setText(genre.getGenre());
            mBookList.clear();
            mBookList.addAll(genreItems.getBookItems());
            mAdapter.notifyDataSetChanged();
        });
    }

    private void init(View v) {
        genreName = v.findViewById(R.id.genre_name);
        final RecyclerView mRecyclerView = v.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(v.getContext(),2));
        mBookList = new ArrayList<>();
        mAdapter = new Adapter(context, mBookList);
        mRecyclerView.setAdapter(mAdapter);
        genreFragmentViewModel = new ViewModelProvider(this).get(GenreFragmentViewModel.class);
        mAdapter.setOnItemClickListener(this);
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
}
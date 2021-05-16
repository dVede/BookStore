package com.example.bookstore.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bookstore.Adapters.OrdersAdapter;
import com.example.bookstore.Interfaces.OnItemClickListener;
import com.example.bookstore.Model.BookItem;
import com.example.bookstore.Model.OrderItem;
import com.example.bookstore.R;
import com.example.bookstore.ViewModels.OrderFragmentViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OrdersFragment extends Fragment implements OnItemClickListener {
    private OrdersAdapter mAdapter;
    private List<OrderItem> mOrderList;
    private Context context;
    private OrderFragmentViewModel mViewModel;
    public static final String EXTRA_IDS = "id";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_orders, container, false);
        context = container.getContext();
        init(v);
        return v;
    }

    private void init(View v) {
        final RecyclerView mRecyclerView = v.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(v.getContext()));
        mOrderList = new ArrayList<>();
        mAdapter = new OrdersAdapter(context, mOrderList);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        mViewModel = new ViewModelProvider(this).get(OrderFragmentViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MutableLiveData<List<OrderItem>> data = mViewModel.getOrderItem();
        data.observe(getViewLifecycleOwner(), orderItems -> {
            mOrderList.clear();
            mOrderList.addAll(orderItems);
            mAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onItemClick(int position) {
        final OrderItem orderItem = mOrderList.get(position);
        final Bundle bundle = new Bundle();
        bundle.putString(EXTRA_IDS, Arrays.toString(orderItem.getBookIds()));
        //TODO()
        /*final BookOrder bookDetailFragment = new BookDetailFragment();
        bookDetailFragment.setArguments(bundle);
        final FragmentTransaction fTrans = getParentFragmentManager().beginTransaction();
        fTrans.addToBackStack(null);
        fTrans.replace(R.id.fragment_container_view, bookDetailFragment).commit();*/
    }
}
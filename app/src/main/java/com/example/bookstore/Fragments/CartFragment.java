package com.example.bookstore.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.bookstore.Adapters.CartAdapter;
import com.example.bookstore.Fragments.BookDetailFragment;
import com.example.bookstore.Interfaces.OnItemButtonClickListener;
import com.example.bookstore.Interfaces.OnItemClickListener;
import com.example.bookstore.Model.CartFragmentItem;
import com.example.bookstore.Model.CartItem;
import com.example.bookstore.R;
import com.example.bookstore.Utils;
import com.example.bookstore.ViewModels.CartFragmentViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartFragment extends Fragment implements OnItemClickListener,
        OnItemButtonClickListener {
    private CartAdapter mAdapter;
    private Context context;
    private TextView totalPrice;
    private List<CartItem> mCartList;
    private CartFragmentViewModel cartFragmentViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_cart, container, false);
        context = container.getContext();
        init(v);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LiveData<CartFragmentItem> liveData = cartFragmentViewModel.getBookItem();
        liveData.observe(getViewLifecycleOwner(), cartFragmentItem -> {
            double totalPrice1 = cartFragmentItem.getTotalPrice();
            if (totalPrice1 == (long) totalPrice1) totalPrice.setText(String.format(Locale.ENGLISH,
                    "%d" + getResources().getString(R.string.rubles), (long) totalPrice1));
            else totalPrice.setText(String.format(Locale.ENGLISH,"%s"
                    + getResources().getString(R.string.rubles), totalPrice1));
            mCartList.clear();
            mCartList.addAll(cartFragmentItem.getCartItem());
            mAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onItemClick(int position) {
        final CartItem cartItem = mCartList.get(position);
        final Bundle bundle = new Bundle();
        bundle.putInt(Utils.EXTRA_ID,cartItem.getBookItem().getId());
        final BookDetailFragment bookDetailFragment = new BookDetailFragment();
        bookDetailFragment.setArguments(bundle);
        final FragmentTransaction fTrans = getParentFragmentManager().beginTransaction();
        fTrans.addToBackStack(null);
        fTrans.replace(R.id.fragment_container_view, bookDetailFragment).commit();
    }


    private void init(View v) {
        Button buyButton = v.findViewById(R.id.buy_button);
        totalPrice = v.findViewById(R.id.total_Price);
        final RecyclerView mRecyclerView = v.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(v.getContext()));
        mCartList = new ArrayList<>();
        mAdapter = new CartAdapter(context, mCartList);
        mRecyclerView.setAdapter(mAdapter);
        cartFragmentViewModel = new ViewModelProvider(this).get(CartFragmentViewModel.class);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemButtonClickListener(this);
    }

    @Override
    public void onItemButtonClick(int position, View v) {
        if (v.getId() == R.id.delete_button) {
            cartFragmentViewModel.deleteBookItem(position);
        }
    }
}
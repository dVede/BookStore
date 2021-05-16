package com.example.bookstore.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.bookstore.LoginActivity;
import com.example.bookstore.Model.User;
import com.example.bookstore.R;
import com.example.bookstore.SingletonClasses.SharedPrefManager;
import com.example.bookstore.ViewModels.ProfileViewModel;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private ProfileViewModel mViewModel;
    private TextView login, email, address, telephone;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.profile_fragment, container, false);
        init(v);
        return v;
    }

    private void init(View v) {
        login = v.findViewById(R.id.profile_username);
        email = v.findViewById(R.id.profile_email);
        address = v.findViewById(R.id.textViewAddress);
        telephone = v.findViewById(R.id.textViewTelephone);
        final Button logoutButton = v.findViewById(R.id.logout);
        final Button changeEmailButton = v.findViewById(R.id.change_email_profile);
        final Button changePasswordButton = v.findViewById(R.id.change_password_profile);
        mViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        logoutButton.setOnClickListener(this);
        changeEmailButton.setOnClickListener(this);
        changePasswordButton.setOnClickListener(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final LiveData<User> data = mViewModel.getUser();
        data.observe(getViewLifecycleOwner(), user -> {
            login.setText(user.getLogin());
            email.setText(user.getEmail());
            address.setText(user.getAddress());
            telephone.setText(user.getTelephone());
        });
    }

    @Override
    public void onClick(View v) {
        final FragmentTransaction fTrans = getParentFragmentManager().beginTransaction();
        switch (v.getId()) {
            case R.id.change_password_profile:
                final ChangePasswordFragment changePasswordFragment = new ChangePasswordFragment();
                fTrans.addToBackStack(null);
                fTrans.replace(R.id.fragment_container_view, changePasswordFragment).commit();
                break;
            case R.id.change_email_profile:
                final ChangeProfileInfoFragment changeProfileInfoFragment = new ChangeProfileInfoFragment();
                fTrans.addToBackStack(null);
                fTrans.replace(R.id.fragment_container_view, changeProfileInfoFragment).commit();
                break;
            case R.id.logout:
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                final SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(getContext());
                sharedPrefManager.clear();
                startActivity(intent);
                requireActivity().finish();
                break;
            default:
                break;
        }
    }
}
package com.example.bookstore;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import com.example.bookstore.Dialog.GenreCreateDialog;
import com.example.bookstore.Dialog.PublisherAddDialog;
import com.example.bookstore.Fragments.BookFragment;
import com.example.bookstore.Fragments.CartFragment;
import com.example.bookstore.Fragments.OrdersFragment;
import com.example.bookstore.Fragments.ProfileFragment;
import com.example.bookstore.Model.Role;
import com.example.bookstore.SingletonClasses.SharedPrefManager;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        final int id = item.getItemId();
        final FragmentTransaction fTrans = getSupportFragmentManager().beginTransaction();
        switch (id) {
            case R.id.nav_profile:
                final ProfileFragment profileFragment = new ProfileFragment();
                fTrans.replace(R.id.fragment_container_view, profileFragment).commit();
                break;
            case R.id.nav_books:
                final BookFragment bookFragment = new BookFragment();
                fTrans.replace(R.id.fragment_container_view, bookFragment).commit();
                break;
            case R.id.nav_wishlist:
                final WishListFragment wishlistFragment = new WishListFragment();
                fTrans.replace(R.id.fragment_container_view, wishlistFragment).commit();
                break;
            case R.id.nav_cart:
                final CartFragment cartFragment = new CartFragment();
                fTrans.replace(R.id.fragment_container_view, cartFragment).commit();
                break;
            case R.id.nav_orders:
                final OrdersFragment ordersFragment = new OrdersFragment();
                fTrans.replace(R.id.fragment_container_view, ordersFragment).commit();
                break;
            case R.id.nav_add_genre:
                GenreCreateDialog genreCreateDialog = new GenreCreateDialog();
                genreCreateDialog.show(fTrans, "genreCreateDialog");
                break;
            case R.id.nav_add_publisher:
                PublisherAddDialog publisherAddDialog = new PublisherAddDialog();
                publisherAddDialog.show(fTrans, "publisherAddDialog");
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void init() {
        drawerLayout = findViewById(R.id.drawer_layout);
        final NavigationView navigationView = findViewById(R.id.nav_view);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        navigationView.inflateMenu(isAdmin() ? R.menu.admin_menu : R.menu.main_menu);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                    R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        }

    private boolean isAdmin() {
        final String check = Role.ADMIN.toString();
        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(getApplication());
        final String t = sharedPrefManager.getUser().getToken();
        final String[] arr = Utils.decodeJWT(t).split(Utils.COLON_CHARACTER);
        String[] arr2 = arr[2].replace("\"", Utils.EMPTY_CHARACTER).split(Utils.COMMA_CHARACTER);
        return check.equals(arr2[0]);
    }
}
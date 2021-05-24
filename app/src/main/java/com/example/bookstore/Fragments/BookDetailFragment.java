package com.example.bookstore.Fragments;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.example.bookstore.Dialog.AddToCartDialog;
import com.example.bookstore.Dialog.RatingDialog;
import com.example.bookstore.Model.Author;
import com.example.bookstore.Model.Book;
import com.example.bookstore.Model.BookDetailed;
import com.example.bookstore.Model.Genre;
import com.example.bookstore.Model.Publisher;
import com.example.bookstore.R;
import com.example.bookstore.SingletonClasses.QueueSingleton;
import com.example.bookstore.SingletonClasses.SharedPrefManager;
import com.example.bookstore.Utils;
import com.example.bookstore.ViewModels.BookDetailViewModel;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static com.example.bookstore.Utils.DOT_END_REGEX;
import static com.example.bookstore.Utils.EMPTY_CHARACTER;
import static com.example.bookstore.Utils.TRAILING_ZERO_REGEX;
import static com.example.bookstore.Utils.TRAILING_ZERO_REPLACEMENT;

public class BookDetailFragment extends Fragment implements LifecycleObserver, View.OnClickListener {

    private TextView titleText, authorsText, descriptionText, isbnText, publisherText,
            yearText, pagesText, priceText, genresText, stockText, rating, numberOfVotes;
    private ImageView bookImage;
    private Button wishlistButton;
    private Boolean isWishlisted;
    private RatingBar ratingBar;
    private RequestQueue requestQueue;
    private BookDetailViewModel bookDetailViewModel;
    private SharedPrefManager sharedPrefManager;
    private int bid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_book_detail, container, false);
        init(v);
        if (bookDetailViewModel != null) {
            bookDetailViewModel.refresh(bid);
        }
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Bundle bundle = this.getArguments();
        if (bundle != null) bid = bundle.getInt(Utils.EXTRA_ID, 0);
        bookDetailViewModel = new ViewModelProvider(this).get(BookDetailViewModel.class);
        final LiveData<BookDetailed> data = bookDetailViewModel.getBook(bid);
        data.observe(getViewLifecycleOwner(), this::onLiveDataUpdate);
    }

    private void init(View v) {
        final Button addToCart = v.findViewById(R.id.add_to_cart);
        sharedPrefManager = SharedPrefManager.getInstance(getContext());
        requestQueue = QueueSingleton.getInstance(getContext()).getRequestQueue();
        ratingBar = v.findViewById(R.id.ratingBar2);
        descriptionText = v.findViewById(R.id.descriptionText);
        titleText = v.findViewById(R.id.titleText);
        authorsText = v.findViewById(R.id.authors_data);
        genresText = v.findViewById(R.id.genres_data);
        isbnText = v.findViewById(R.id.isbn_data);
        publisherText = v.findViewById(R.id.publisher_data);
        yearText = v.findViewById(R.id.year_data);
        pagesText = v.findViewById(R.id.pages_data);
        stockText = v.findViewById(R.id.stock_data);
        wishlistButton = v.findViewById(R.id.wishlist_button);
        bookImage = v.findViewById(R.id.bookImage);
        priceText = v.findViewById(R.id.priceText);
        rating = v.findViewById(R.id.rating);
        numberOfVotes = v.findViewById(R.id.number_of_vote);
        descriptionText.setMovementMethod(new ScrollingMovementMethod());
        authorsText.setMovementMethod(new ScrollingMovementMethod());
        genresText.setMovementMethod(new ScrollingMovementMethod());
        wishlistButton.setOnClickListener(v1 -> wishListClick());
        addToCart.setOnClickListener(this);
        ratingBar.setStepSize(0.1f);
        View ratingView = v.findViewById(R.id.rating_view);
        ratingView.setOnClickListener(this);
        Button commentButton = v.findViewById(R.id.comment_open_button);
        commentButton.setOnClickListener(this);
    }

    public void onLiveDataUpdate(BookDetailed bookDetailed) {
        final List<Author> authors = bookDetailed.getAuthor();
        final Book book = bookDetailed.getBook();
        final Publisher publisher = bookDetailed.getPublisher();
        final List<Genre> genres = bookDetailed.getGenre();
        final float averageRating = bookDetailed.getAverageRating();
        final int votes = bookDetailed.getVotesNumber();
        int bookId = book.getId();
        isWishlisted = bookDetailed.getWishlisted();

        createSpannableGenresText(genres);
        createSpannableAuthorText(authors);
        createSpannablePublisherText(publisher);

        final String title = book.getTitle();
        final String isbn = book.getIsbn();
        final String year = book.getYear();
        final String pages = book.getPages();
        final String description = book.getDescription();

        final int stock = book.getNumberInStock();
        final double price = book.getPrice();
        final String priceS = String.valueOf(price)
                .replaceAll(TRAILING_ZERO_REGEX, TRAILING_ZERO_REPLACEMENT)
                .replaceAll(DOT_END_REGEX, EMPTY_CHARACTER);

        Glide.with(this).load("http://goo.gl/gEgYUd").into(bookImage);
        titleText.setText(title);

        descriptionText.setText(description);
        isbnText.setText(isbn);
        publisherText.setMovementMethod(LinkMovementMethod.getInstance());
        yearText.setText(year);
        pagesText.setText(pages);
        stockText.setText(String.format(Locale.ENGLISH,"%d", stock));
        ratingBar.setRating(averageRating);
        numberOfVotes.setText(String.valueOf(votes));
        rating.setText(String.valueOf(averageRating));
        priceText.setText(String.format(Locale.ENGLISH,"%s "
                + getResources().getString(R.string.rubles), priceS));
        if (isWishlisted) wishlistButton.setBackgroundResource(R.drawable.ic_baseline_red_24);
    }

    private void createSpannablePublisherText(Publisher publisher) {
        final String publisherName = publisher.getName() + Utils.SPACE_CHARACTER;
        final SpannableString sPublisher = new SpannableString(publisherName);
        final ClickableSpan clickableSpan3 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                final int pid = publisher.getId();
                final Bundle bundle = new Bundle();
                bundle.putInt(Utils.EXTRA_ID, pid);
                final PublisherFragment publisherFragment = new PublisherFragment();
                publisherFragment.setArguments(bundle);
                final FragmentTransaction fTrans = getParentFragmentManager().beginTransaction();
                fTrans.addToBackStack(null);
                fTrans.replace(R.id.fragment_container_view, publisherFragment).commit();
            }
        };
        sPublisher.setSpan(clickableSpan3, 0, publisherName.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        publisherText.setText(sPublisher);
    }

    private void createSpannableAuthorText(List<Author> authors) {
        final StringBuilder sb2 = new StringBuilder();
        for (int i = 0; i < authors.size(); i++) {
            sb2.append(String.format("%s %s ",
                    authors.get(i).getLastName(), authors.get(i).getFirstName()));
            if (i != authors.size() - 1) sb2.append(System.getProperty("line.separator"));
        }
        if (!sb2.toString().equals(Utils.EMPTY_CHARACTER)) {
            final SpannableString sAuthors = new SpannableString(sb2.toString());
            final String[] str = sb2.toString().split(Objects.requireNonNull(System.getProperty("line.separator")));
            int start = 0;
            int end;
            for (int i = 0; i < str.length; i++) {
                int finalI = i;
                final ClickableSpan clickableSpan2 = new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        final int aid = authors.get(finalI).getId();
                        final Bundle bundle = new Bundle();
                        bundle.putInt(Utils.EXTRA_ID, aid);
                        final AuthorFragment authorFragment = new AuthorFragment();
                        authorFragment.setArguments(bundle);
                        final FragmentTransaction fTrans = getParentFragmentManager().beginTransaction();
                        fTrans.addToBackStack(null);
                        fTrans.replace(R.id.fragment_container_view, authorFragment).commit();
                    }
                };
                end = start + str[i].length() - 1;
                sAuthors.setSpan(clickableSpan2, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                start = start + str[i].length() + 1;
                authorsText.setText(sAuthors);
                authorsText.setMovementMethod(LinkMovementMethod.getInstance());
            }
        }
        else authorsText.setText(sb2.toString());
    }

    private void createSpannableGenresText(List<Genre> genres) {
        final StringBuilder sb1 = new StringBuilder();
        for (int i = 0; i < genres.size(); i++) {
            sb1.append(String.format("%s ",genres.get(i).getGenre()));
            if (i != genres.size() - 1) sb1.append(System.getProperty("line.separator"));
        }
        if (!sb1.toString().equals(Utils.EMPTY_CHARACTER)) {
            final SpannableString sGenres = new SpannableString(sb1.toString());
            final String[] str = sb1.toString().split(Objects.requireNonNull(System.getProperty("line.separator")));
            int start = 0;
            int end;
            for (int i = 0; i < str.length; i++) {
                int finalI = i;
                final ClickableSpan clickableSpan1 = new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        final int gid = genres.get(finalI).getId();
                        final Bundle bundle = new Bundle();
                        bundle.putInt(Utils.EXTRA_ID, gid);
                        final GenreFragment genreFragment = new GenreFragment();
                        genreFragment.setArguments(bundle);
                        final FragmentTransaction fTrans = getParentFragmentManager().beginTransaction();
                        fTrans.addToBackStack(null);
                        fTrans.replace(R.id.fragment_container_view, genreFragment).commit();
                    }
                };
                end = start + str[i].length() - 1;
                sGenres.setSpan(clickableSpan1, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                start += str[i].length() + 1;
                genresText.setText(sGenres);
                genresText.setMovementMethod(LinkMovementMethod.getInstance());
            }
        }
        else genresText.setText(sb1.toString());
    }

    private void wishListClick() {
        final StringRequest requestDeleteWishList = new StringRequest(Request.Method.POST,
                Utils.DELETE_FROM_WISHLIST,
                response -> {
                    isWishlisted = false;
                    wishlistButton.setBackgroundResource(R.drawable.ic_baseline_shadow_24);
                },
                error -> {
            error.printStackTrace();
            final String errorMsg = Utils.getErrorMessage(error, getContext());
            Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("book",String.format(Locale.ENGLISH,"%d",bid));
                params.put("consumer",String.format(Locale.ENGLISH,"%d",sharedPrefManager.getUser().getId()));
                return params;
            }
            @Override
            public Map<String, String> getHeaders() {
                Map<String,String> params = new HashMap<>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };

        final StringRequest requestInsertWishList = new StringRequest(Request.Method.POST,
                Utils.INSERT_TO_WISHLIST,
                response -> {
                    isWishlisted = true;
                    wishlistButton.setBackgroundResource(R.drawable.ic_baseline_red_24);
                },
                error -> {
            error.printStackTrace();
            final String errorMsg = Utils.getErrorMessage(error, getContext());
            Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("book",String.format(Locale.ENGLISH,"%d",bid));
                params.put("consumer",String.format(Locale.ENGLISH,"%d",sharedPrefManager.getUser().getId()));
                return params;
            }
            @Override
            public Map<String, String> getHeaders() {
                Map<String,String> params = new HashMap<>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };

        if (isWishlisted) requestQueue.add(requestDeleteWishList);
        else requestQueue.add(requestInsertWishList);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_to_cart:
                AddToCartDialog addToCartDialog = new AddToCartDialog(bid, sharedPrefManager.getUser().getId());
                addToCartDialog.show(getParentFragmentManager(), "addCartDialog");
                break;
            case R.id.rating_view:
                RatingDialog ratingDialog = new RatingDialog(bid, sharedPrefManager.getUser().getId());
                ratingDialog.show(getParentFragmentManager(), "ratingDialog");
                break;
            case R.id.comment_open_button:
                final Bundle bundle = new Bundle();
                bundle.putInt(Utils.EXTRA_ID, bid);
                final FragmentTransaction fTrans = getParentFragmentManager().beginTransaction();
                final CommentsFragment commentsFragment = new CommentsFragment();
                commentsFragment.setArguments(bundle);
                fTrans.addToBackStack(null);
                fTrans.replace(R.id.fragment_container_view, commentsFragment).commit();
                break;
        }
    }
}
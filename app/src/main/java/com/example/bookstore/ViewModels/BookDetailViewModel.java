package com.example.bookstore.ViewModels;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.LruCache;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.bookstore.CacheRequest;
import com.example.bookstore.CacheRequestTtl;
import com.example.bookstore.Model.Author;
import com.example.bookstore.Model.Book;
import com.example.bookstore.Model.BookDetailed;
import com.example.bookstore.Model.Genre;
import com.example.bookstore.Model.Publisher;
import com.example.bookstore.SingletonClasses.QueueSingleton;
import com.example.bookstore.SingletonClasses.SharedPrefManager;
import com.example.bookstore.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

public class BookDetailViewModel extends AndroidViewModel {
    private MutableLiveData<BookDetailed> book;
    private Book mBook;
    private List<Author> mAuthors = new ArrayList<>();
    private Publisher mPublisher;
    private Boolean mWishlist;
    private CountDownLatch mCountDownLatch;

    private List<Genre> mGenres = new ArrayList<>();
    private float mAverageRating;
    private int mVote;
    private RequestQueue queue = QueueSingleton.getInstance(getApplication()).getRequestQueue();
    private SharedPrefManager preferencesHelper = SharedPrefManager.getInstance(getApplication());


    public BookDetailViewModel(@NonNull Application application) { super(application); }

    public LiveData<BookDetailed> getBook(int id) {
        book = new MutableLiveData<>();
        loadBook(id);
        return book;
    }

    private void loadBook(int id) {
        final int userId = preferencesHelper.getUser().getId();
        publisherGetRequest(id, queue);
        authorGetRequest(id, queue);
        bookInfoRequest(id, queue);
        wishListRequest(id, userId, queue);
        genreRequest(id, queue);
        ratingRequest(id, queue);
        mCountDownLatch = new CountDownLatch(6);
        final Handler mainThreadHandler = new Handler(Looper.getMainLooper());
        new Thread(() -> {
            try {
                mCountDownLatch.await();
                final BookDetailed bookDetailed = new BookDetailed(mBook, mAuthors, mPublisher, mGenres,
                        mWishlist, mVote, mAverageRating);
                mainThreadHandler.post(() -> book.postValue(bookDetailed));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void genreRequest(int id, RequestQueue queue) {
        CacheRequestTtl requestGenres = new CacheRequestTtl(
                String.format(Locale.ENGLISH, Utils.GET_GENRES_BOOK, id),
                response -> {
                    try {
                        mGenres = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jrespone = response.getJSONObject(i);
                            if (jrespone.has("gid")) {
                                final String[] gidsList = jrespone.getString("gid").split(",");
                                final String[] genresList = jrespone.getString("genre").split(",");
                                final String[] pidsList = jrespone.getString("pid").split(",");
                                for (int i1 = 0; i1 < genresList.length; i1++) {
                                    mGenres.add(new Genre(Integer.parseInt(gidsList[i1]),
                                            Integer.parseInt(pidsList[i1]), genresList[i1]));
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mCountDownLatch.countDown();
                },
                error -> {
                    System.out.println(error.getMessage());
                    mCountDownLatch.countDown();
                });
        queue.add(requestGenres);
    }

    private void wishListRequest(int id, int userId, RequestQueue queue) {
        JsonArrayRequest requestIsInWishlist = new JsonArrayRequest(
                String.format(Locale.ENGLISH, Utils.IS_IN_WISHLIST, id, userId),
                response -> {
                    try {
                        mWishlist = response.getJSONObject(0).getBoolean("isinwishlist");
                        System.out.println(mWishlist);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mCountDownLatch.countDown();
                },
                error -> {
                    System.out.println(error.getMessage());
                    mCountDownLatch.countDown();
                });
        queue.add(requestIsInWishlist);
    }

    private void bookInfoRequest(int id, RequestQueue queue) {
        JsonArrayRequest requestBookInfo = new JsonArrayRequest(
                String.format(Locale.ENGLISH, Utils.GET_BOOK_INFO, id),
                response -> {
                    try {
                        final int publisherid = response.getJSONObject(0).getInt("publisherid");
                        final String isbn = response.getJSONObject(0).getString("isbn");
                        final String imageurl = response.getJSONObject(0).getString("imageurl");
                        final String title = response.getJSONObject(0).getString("title");
                        final String year = response.getJSONObject(0).getString("year");
                        final String pages = response.getJSONObject(0).getString("pages");
                        final int numberInStock = response.getJSONObject(0).getInt("numberinstock");
                        final double price = response.getJSONObject(0).getDouble("price");
                        final String description = response.getJSONObject(0).getString("description");
                        mBook = new Book(isbn, imageurl, title, year, pages, numberInStock, description, price, id, publisherid);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mCountDownLatch.countDown();
                },
                error -> {
                    System.out.println(error.getMessage());
                    mCountDownLatch.countDown();
                });
        queue.add(requestBookInfo);
    }

    private void authorGetRequest(int id, RequestQueue queue) {
        JsonArrayRequest requestBookAuthor = new JsonArrayRequest(
                String.format(Locale.ENGLISH, Utils.GET_AUTHORS_BOOK, id),
                response -> {
                    try {
                        mAuthors = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            final JSONObject jrespone = response.getJSONObject(i);
                            final String[] aids = jrespone.getString("aid").split(",");
                            final String[] aimageurls = jrespone.getString("aimageurl").split(",");
                            final String[] lastnames = jrespone.getString("lastname").split(",");
                            final String[] firstnames = jrespone.getString("firstname").split(",");
                            final String[] middlenames = jrespone.getString("middlename").split(",");
                            for (int i1 = 0; i1 < lastnames.length; i1++) {
                                mAuthors.add(new Author(Integer.parseInt(aids[i1]), lastnames[i1],
                                        firstnames[i1], middlenames[i1], aimageurls[i1]));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mCountDownLatch.countDown();
                },
                error -> {
                    System.out.println(error.getMessage());
                    mCountDownLatch.countDown();
                });
        queue.add(requestBookAuthor);
    }

    private void publisherGetRequest(int id, RequestQueue queue) {
        JsonArrayRequest requestBookPublisher = new JsonArrayRequest(
                String.format(Locale.ENGLISH, Utils.GET_PUBLISHER_OF_BOOK, id),
                response -> {
                    try {
                        final int ids = response.getJSONObject(0).getInt("id");
                        final String name = response.getJSONObject(0).getString("name");
                        final String address = response.getJSONObject(0).getString("address");
                        final String email = response.getJSONObject(0).getString("email");
                        mPublisher = new Publisher(ids, name, address, email);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mCountDownLatch.countDown();
                },
                error -> {
                    System.out.println(error.getMessage());
                    mCountDownLatch.countDown();
                });
        queue.add(requestBookPublisher);
    }

    private void ratingRequest(int id, RequestQueue queue) {
        JsonArrayRequest ratingRequest = new JsonArrayRequest(
                String.format(Locale.ENGLISH, Utils.GET_AVG_RATING, id),
                response -> {
                    try {
                        mAverageRating = (float) response.getJSONObject(0).getDouble("avg_rating");
                        mVote = response.getJSONObject(0).getInt("number_of_votes");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mCountDownLatch.countDown();
                },
                error -> {
                    System.out.println(error.getMessage());
                    mCountDownLatch.countDown();
                });
        queue.add(ratingRequest);
    }

    public void refresh(int bid) {
        loadBook(bid);
    }
}

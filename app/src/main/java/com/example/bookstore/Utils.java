package com.example.bookstore;

import org.apache.commons.codec.binary.Base64;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Utils {

    public static final String GET_USER_INFO = Utils.IP_ADDRESS + "consumer?login=eq.%s";
    public static final String GET_TOKEN = Utils.IP_ADDRESS + "rpc/login?username=%s";
    public static final String GET_CART_BOOKS= Utils.IP_ADDRESS + "rpc/get_cart_books?uid=%d";
    public static final String GET_AUTHOR_BOOKS = Utils.IP_ADDRESS + "rpc/get_author_books?aid=%d";
    public static final String GET_GENRE_BOOKS = Utils.IP_ADDRESS + "rpc/get_genre_books?gid=%d";
    public static final String GET_ALL_BOOKS = Utils.IP_ADDRESS + "rpc/get_all_booki";
    public static final String GET_ORDERS = Utils.IP_ADDRESS + "rpc/get_orders?uid=%d";
    public static final String UPSERT_RATING = Utils.IP_ADDRESS + "rpc/rateupdate";
    public static final String GET_BOOK_INFO = Utils.IP_ADDRESS + "rpc/get_book_info?ids=%d";
    public static final String GET_AUTHORS_BOOK = Utils.IP_ADDRESS + "rpc/get_authors_book?bid=%d";
    public static final String GET_GENRES_BOOK = Utils.IP_ADDRESS + "rpc/get_genres_book?bid=%d";
    public static final String GET_AVG_RATING = Utils.IP_ADDRESS + "rpc/get_avg_rating?bid=%d";
    public static final String CHANGE_PASSWORD = Utils.IP_ADDRESS + "rpc/update_password";
    public static final String INSERT_TO_WISHLIST = Utils.IP_ADDRESS + "rpc/insert_to_wishlist";
    public static final String UPDATE_PROFILE_INFO = Utils.IP_ADDRESS + "rpc/update_profile_info";
    public static final String DELETE_CART_BOOK = Utils.IP_ADDRESS + "rpc/delete_cart_book";
    public static final String ADD_TO_CART = Utils.IP_ADDRESS + "rpc/add_to_cart";
    public static final String IS_IN_CART = Utils.IP_ADDRESS + "rpc/is_in_cart?uid=%d&bid=%d";
    public static final String ADD_GENRE = Utils.IP_ADDRESS + "rpc/add_genre";
    public static final String ADD_PUBLISHER = Utils.IP_ADDRESS + "rpc/add_publisher";
    public static final String GET_BOOK_COMMENTS = Utils.IP_ADDRESS + "rpc/get_book_comments?bid=%d";
    public static final String ADD_COMMENT = Utils.IP_ADDRESS + "rpc/add_comment";
    public static final String GET_GENRES = Utils.IP_ADDRESS + "rpc/get_genres";
    public static final String CREATE_USER = Utils.IP_ADDRESS + "rpc/create_user";
    public static final String UPDATE_CART = Utils.IP_ADDRESS + "rpc/update_cart";
    public static final String DELETE_FROM_WISHLIST = Utils.IP_ADDRESS + "rpc/delete_from_wishlist";
    public static final String GET_ALL_WISHLISTED_BOOKS =
            Utils.IP_ADDRESS + "rpc/get_all_wishlisted_books?consumer=%d";
    public static final String EMAIL_TELEPHONE_CHECK =
            Utils.IP_ADDRESS + "rpc/is_email_and_telephone_exist?emailnew=%s&telephonenew=%s";
    public static final String GET_PUBLISHER_BOOK =
            Utils.IP_ADDRESS + "rpc/get_publisher_book?pid=%d";
    public static final String GET_PUBLISHER_OF_BOOK =
            Utils.IP_ADDRESS + "rpc/get_publisher_of_book?ids=%d";
    public static final String IS_IN_WISHLIST =
            Utils.IP_ADDRESS + "rpc/is_in_wishlist2?book=%d&consumer=%d";
    public static final String GET_USER_PASSWORD =
            Utils.IP_ADDRESS + "consumer?login=eq.%s&select=password_salt,password_hash,id";
    public static final String EMAIL_TELEPHONE_LOGIN_CHECK =
            Utils.IP_ADDRESS + "rpc/is_email_telephone_or_login_exist" +
                    "?emailcheck=%s&telephonecheck=%s&logincheck=%s";
    public static final String EXTRA_ID = "id";
    public static final String TIMESTAMP_SEPARATOR = "T";
    public static final String SPACE_CHARACTER = " ";
    public static final String COMMA_CHARACTER = ",";
    public static final String RIGHT_BRACE = "}";
    public static final String EMPTY_CHARACTER = "";
    public static final String COLON_CHARACTER = ":";
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    public static final String COMMA_DELIMITER_WITH_SPACE = ", ";
    public static final String IP_ADDRESS = "http://192.168.1.40:3000/";
    public static final String SHARED_PREF_NAME = "user_info";

    public static final String EMAIL_REGEX = "(?:[a-z0-9!#$%&''*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&''*" +
            "+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\" +
            "\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a" +
            "-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:" +
            "25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e" +
            "-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
    public static final String TELEPHONE_REGEX = "^((\\+7)+([0-9]){10})";

    private static final Random RANDOM = new SecureRandom();

    public static byte[] hash(char[] password, byte[] salt) {
        PBEKeySpec spec = new PBEKeySpec(password, salt, 10000, 256);
        Arrays.fill(password, Character.MIN_VALUE);
        try {
            final SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new AssertionError("Error while hashing a password: " + e.getMessage(), e);
        } finally {
            spec.clearPassword();
        }
    }

    public static byte[] saltGenerate() {
        byte[] salt = new byte[32];
        RANDOM.nextBytes(salt);
        return salt;
    }

    public static String decodeJWT(String jwt) {
        final String[] tokens = jwt.split("\\.");
        final String base64EncodedBody = tokens[1];
        final Base64 base64Url = new Base64(true);
        return new String(base64Url.decode(base64EncodedBody));
    }
}

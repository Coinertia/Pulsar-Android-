package com.pulsar.android;

import android.util.Base64;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.pulsar.android.Models.HistoryItem;
import com.pulsar.android.auth.WavesWallet;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by ABC on 3/29/2018.
 */

public class GlobalVar {
    public static String strSeeds;
    public static List<String> mSeeds;
    public static String strPwd;
    public static String strAddress;
    public static String strAddressEncrypted;
    public static String strPrivate;
    public static String strPublic;
    public static ArrayList<HistoryItem> mHistoryData = new ArrayList<>();
    public static ArrayList<HistoryItem> mUnconfirmedData = new ArrayList<>();

    public static ArrayList<HistoryItem> mHistoryDataAll = new ArrayList<>();
    public static ArrayList<HistoryItem> mUnconfirmedDataAll = new ArrayList<>();
    public static HistoryItem mLastSend, mLastReceive;

    public static WavesWallet mWallet;
    public static final String KEY_INTENT_PASSWORD = "intent_password";
    public static final String KEY_INTENT_ADDRESS = "intent_address";
    public static final String KEY_INTENT_PRIVATE = "intent_private";
    public static final String KEY_INTENT_PUBLIC = "intent_public";
    public static final String KEY_INTENT_LOCAL = "0123456789ABCDEF";
    public static final String BASE_URL = "http://207.148.29.110:9069";
    public static final String[] assetID = new String[]{"9aJNRabwrAhGQnUQ5dVg8B6JNZ65j2US936dm9wr91ZZ", "HCbhRTNotKQ6KjZUdn2TiRDi3nhqwKKkea8gGDtEJS88", "ESM3Lra8itkcBEiKW1otiqzQn4bw7QmtbycBFzEz5zGN"};//, "5qm13UNbyyA3bZAfndJZUupvVWA4TGy1WCQKsuiAyDkF"};
    public static double[] balances = new double[]{0,0,0};

    public static StorageReference mStorageRef;
    public static DatabaseReference mDatabaseRef;

    public static SecretKey generateKey()
            throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        return new SecretKeySpec(KEY_INTENT_LOCAL.getBytes(), "AES");
    }

    public static String encryptMsg(String message)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidParameterSpecException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException
    {
   /* Encrypt the message. */
        SecretKey secret = null;
        try {
            secret = generateKey();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        Cipher cipher = null;
        cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secret);
        byte[] cipherText = cipher.doFinal(message.getBytes("UTF-8"));
        String encrypedString= Base64.encodeToString(cipherText, Base64.DEFAULT);
        return encrypedString;
    }

    public static String decryptMsg(String encrypedStr)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidParameterSpecException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException
    {
    /* Decrypt the message, given derived encContentValues and initialization vector. */
        byte[] cipherText = Base64.decode(encrypedStr, Base64.DEFAULT);
        SecretKey secret = null;
        try {
            secret = generateKey();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        Cipher cipher = null;
        cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secret);
        String decryptString = new String(cipher.doFinal(cipherText), "UTF-8");
        return decryptString;
    }
}

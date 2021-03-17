package com.shaayaari.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shaayaari.R;
import com.shaayaari.models.CategoryModel;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class AppUtils {
    private static final String TAG = "AppUtils";

    static ProgressDialog progressDialog;


    public static Animation fadeIn(Activity activity) {
        return AnimationUtils.loadAnimation(activity, R.anim.fade_in);
    }

    public static Animation fadeOut(Activity activity) {
        return AnimationUtils.loadAnimation(activity, R.anim.fade_out);
    }

    public static void updateFavouriteIds(boolean var, Object model) {
        Log.d(TAG, "updateFavouriteIds: " + (String) model);
        if (var) {
            AppUtils.getFireStoreReference().collection(AppConstant.DATA)
                    .document((String) model)
                    .update(AppConstant.FAVOURITE_IDS, FieldValue.arrayUnion(getUid()));
            Toast.makeText(App.context, "Added as favourite !!", Toast.LENGTH_SHORT).show();
        } else {
            AppUtils.getFireStoreReference().collection(AppConstant.DATA)
                    .document((String) model)
                    .update(AppConstant.FAVOURITE_IDS, FieldValue.arrayRemove(getUid()));
            Toast.makeText(App.context, "Removed From Favourite !!", Toast.LENGTH_SHORT).show();

        }
    }

    public static Map<String, Object> getFavouriteMap(CategoryModel msgModel) {
        Map<String, Object> map = new HashMap<>();
        map.put(AppConstant.MSG, msgModel.getMsg());
        map.put(AppConstant.ID, getUid());
        map.put(AppConstant.MSG_ID, msgModel.getId());
        map.put(AppConstant.IMAGE, msgModel.getImage());
        map.put(AppConstant.TIMESTAMP, System.currentTimeMillis());
        return map;
    }

    public static Map<String, Object> getLikeUpdateMap(boolean status) {
        Map<String, Object> map = new HashMap<>();
        map.put(AppConstant.LIKES, status ? FieldValue.increment(1) : FieldValue.increment(-1));
        map.put(AppConstant.LIKE_IDS, status ? FieldValue.arrayUnion(getUid()) : FieldValue.arrayRemove(getUid()));
        return map;

    }

    public static String getCountInRomanFormat(Number number) {
        char[] suffix = {' ', 'k', 'M', 'B', 'T', 'P', 'E'};
        long numValue = number.longValue();
        int value = (int) Math.floor(Math.log10(numValue));
        int base = value / 3;
        if (value >= 3 && base < suffix.length) {
            return new DecimalFormat("#0.0").format(numValue / Math.pow(10, base * 3)) + suffix[base];
        } else {
            return new DecimalFormat("#,##0").format(numValue);
        }
    }

    public static String getCountInRomanFormat(String num) {
        Number number = Integer.parseInt(num);
        char[] suffix = {' ', 'k', 'M', 'B', 'T', 'P', 'E'};
        long numValue = number.longValue();
        int value = (int) Math.floor(Math.log10(numValue));
        int base = value / 3;
        if (value >= 3 && base < suffix.length) {
            return new DecimalFormat("#0.0").format(numValue / Math.pow(10, base * 3)) + suffix[base];
        } else {
            return new DecimalFormat("#,##0").format(numValue);
        }
    }

    public static FirebaseFirestore getFireStoreReference() {
        return FirebaseFirestore.getInstance();
    }

    public static FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public static String getUid() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (null != user)
            return user.getUid();
        else return null;
    }

    public static String getCurrencyFormat(double num) {
        String COUNTRY = "IN";
        String LANGUAGE = "en";
        return NumberFormat.getCurrencyInstance(new Locale(LANGUAGE, COUNTRY)).format(num);
    }

    public static String getCurrencyFormat(long num) {
        String COUNTRY = "IN";
        String LANGUAGE = "en";
        return NumberFormat.getCurrencyInstance(new Locale(LANGUAGE, COUNTRY)).format(num);
    }

    public static String getCurrencyFormat(String num) {
        Double number = Double.parseDouble(num);
        String COUNTRY = "IN";
        String LANGUAGE = "en";
        return NumberFormat.getCurrencyInstance(new Locale(LANGUAGE, COUNTRY)).format(number);

    }

    public static void showToolbar(Activity activity) {
        Objects.requireNonNull(((AppCompatActivity) activity).getSupportActionBar()).show();
    }

    public static void hideToolbar(Activity activity) {
        Objects.requireNonNull(((AppCompatActivity) activity).getSupportActionBar()).hide();
    }

    public static String getDateInDMYFormatFromTimestamp(long currentTimeMillis) {
        try {
            String value = new java.text.SimpleDateFormat("yyyy-MM-dd").
                    format(new java.util.Date(currentTimeMillis));
            return value;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public static void showRequestDialog(Activity activity) {
        try {
            if (!((Activity) activity).isFinishing()) {
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(activity);
                    progressDialog = ProgressDialog.show(activity, null, null, false, true);
                    progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(activity.getResources().getColor(android.R.color.transparent)));
                    progressDialog.setContentView(R.layout.progress_bar);

                    progressDialog.show();
                } else {
                    progressDialog = ProgressDialog.show(activity, null, null, false, true);
                    progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(activity.getResources().getColor(android.R.color.transparent)));
                    progressDialog.setContentView(R.layout.progress_bar);
                    progressDialog.show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void showRequestDialog(Activity activity, boolean isCancelable) {
        try {
            if (!((Activity) activity).isFinishing()) {
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(activity);

                    progressDialog = ProgressDialog.show(activity, null, null, false, true);
                    progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(activity.getResources().getColor(android.R.color.transparent)));
                    progressDialog.setCancelable(isCancelable);
                    progressDialog.setContentView(R.layout.progress_bar);

                    progressDialog.show();
                } else {
                    progressDialog = ProgressDialog.show(activity, null, null, false, true);
                    progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(activity.getResources().getColor(android.R.color.transparent)));
                    progressDialog.setContentView(R.layout.progress_bar);
                    progressDialog.setCancelable(isCancelable);
                    progressDialog.show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void hideDialog() {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        if (activity != null) {
            try {
                @SuppressLint("WrongConstant") InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService("input_method");
                View view = activity.getCurrentFocus();
                if (view != null) {
                    IBinder binder = view.getWindowToken();
                    if (binder != null) {
                        inputMethodManager.hideSoftInputFromWindow(binder, 0);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }
}

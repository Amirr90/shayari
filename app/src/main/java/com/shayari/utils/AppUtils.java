package com.shayari.utils;

import com.google.firebase.firestore.FirebaseFirestore;

public class AppUtils {

    public static FirebaseFirestore getFireStoreReference() {
        return FirebaseFirestore.getInstance();
    }
}

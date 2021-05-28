package com.example.clonewhatsapp.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ConfigFireBase {

    private static FirebaseAuth auth;
    private static DatabaseReference reference;
    private static StorageReference firebaseStorage;

    //retorna a instância do FBAuth
    public static FirebaseAuth getFBAuth(){
        if (auth == null){
            auth = FirebaseAuth.getInstance();
        }
        return auth;
    }

    //retorna a insância do FBdb
    public static DatabaseReference getFBDatabase(){
        if(reference==null){
            reference = FirebaseDatabase.getInstance().getReference();
        }
        return reference;
    }

    //retorna a instância do storage
    public static StorageReference getGBStorage(){
        if(firebaseStorage==null){
            firebaseStorage = FirebaseStorage.getInstance().getReference();
        }
        return firebaseStorage;
    }
}
package com.example.cloneorganizze.config;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConfigFireBase {

    private static FirebaseAuth auth;
    private static DatabaseReference reference;

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
}

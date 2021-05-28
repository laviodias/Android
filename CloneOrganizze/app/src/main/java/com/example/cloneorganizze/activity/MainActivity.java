package com.example.cloneorganizze.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.cloneorganizze.R;
import com.example.cloneorganizze.config.ConfigFireBase;
import com.google.firebase.auth.FirebaseAuth;
import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;

public class MainActivity extends IntroActivity {

    private FirebaseAuth auth;

    @Override
    protected void onStart() {
        super.onStart();
        verificarUserLogado();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        //Remover os botões laterais:
        setButtonBackVisible(false);
        setButtonNextVisible(false);


        //Slides de fragments:
        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro_1)
                .build()
        );

        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro_2)
                .build()
        );

        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro_3)
                .build()
        );

        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro_4)
                .build()
        );

        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro_cadastro)
                .canGoForward(false)
                .build()
        );
    }

    public void btnLogin(View view){
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void btnCadastro(View view){
        startActivity(new Intent(this, CadastroActivity.class));
    }

    public void verificarUserLogado(){
        
        auth = ConfigFireBase.getFBAuth();
        
        if(auth.getCurrentUser()!=null){
            abirTelaPrincipal();
        }

    }

    public void abirTelaPrincipal(){
        startActivity(new Intent(this, PrincipalActivity.class));
    }
}
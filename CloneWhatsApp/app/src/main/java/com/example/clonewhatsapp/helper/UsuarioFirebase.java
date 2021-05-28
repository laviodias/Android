package com.example.clonewhatsapp.helper;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.clonewhatsapp.config.ConfigFireBase;
import com.example.clonewhatsapp.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class UsuarioFirebase {

    public static String getIdUser(){

        FirebaseAuth auth = ConfigFireBase.getFBAuth();
        String email = auth.getCurrentUser().getEmail();
        String idUser = Base64Custom.codificarBase64(email);

        return idUser;
    }

    public static FirebaseUser getUserAtual(){
        FirebaseAuth auth = ConfigFireBase.getFBAuth();

        return auth.getCurrentUser();
    }

    public static boolean atualizarFoto(Uri url){

        try{
            FirebaseUser user = getUserAtual();

            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(url).build();

            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(!task.isSuccessful()){
                        Log.d("Perfil", "Erro ao atualizar a foto de perfil");
                    }
                }
            });

            return true;

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }

    public static boolean atualizarNome(String nome){

        try{
            FirebaseUser user = getUserAtual();

            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(nome).build();

            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(!task.isSuccessful()){
                        Log.d("Perfil", "Erro ao atualizar o nome do usuário");
                    }
                }
            });

            return true;

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }

    public static Usuario getDadosUser(){
        FirebaseUser firebaseUser = getUserAtual();

        Usuario usuario = new Usuario();

        usuario.setEmail(firebaseUser.getEmail());
        usuario.setNome(firebaseUser.getDisplayName());

        if(firebaseUser.getPhotoUrl() == null){
            usuario.setFoto("");
        }else{
            usuario.setFoto(firebaseUser.getPhotoUrl().toString());
        }

        return usuario;
    }
}

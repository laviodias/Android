package com.example.cloneorganizze.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cloneorganizze.R;
import com.example.cloneorganizze.config.ConfigFireBase;
import com.example.cloneorganizze.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class LoginActivity extends AppCompatActivity {

    private Button btnLogar;
    private EditText editSenha, editEmail;
    private Usuario usuario;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogar = findViewById(R.id.btnEntrar);
        editEmail = findViewById(R.id.editLoginEmail);
        editSenha = findViewById(R.id.editLoginSenha);

        btnLogar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String textoEmail = editEmail.getText().toString();
                String textoSenha = editSenha.getText().toString();

                if (!textoEmail.isEmpty()) {
                    if (!textoSenha.isEmpty()) {

                        usuario = new Usuario();
                        usuario.setEmail(textoEmail);
                        usuario.setSenha(textoSenha);
                        validarLogin();

                    } else {
                        Toast.makeText(LoginActivity.this, "Preencha a sua senha!", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(LoginActivity.this, "Preencha o seu e-mail!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void validarLogin(){
        auth = ConfigFireBase.getFBAuth();

        auth.signInWithEmailAndPassword(usuario.getEmail(), usuario.getSenha()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    abirTelaPrincipal();
                }else{
                    String excessao = "";
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        excessao = "Usuário não corresponde com a senha ou não está cadastrado";
                    }catch (FirebaseAuthInvalidUserException e){
                        excessao = "E-mail incorreto ou não cadastrado!";
                   }catch (Exception e){
                        excessao = "Erro ao fazer login: " + e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(LoginActivity.this, excessao, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void abirTelaPrincipal(){
        startActivity(new Intent(LoginActivity.this, PrincipalActivity.class));
        finish();
    }
}
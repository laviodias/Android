package com.example.clonewhatsapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clonewhatsapp.R;
import com.example.clonewhatsapp.config.ConfigFireBase;
import com.example.clonewhatsapp.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private Button btnLogin;
    private TextInputEditText editEmail, editSenha;
    private Usuario usuario;
    private FirebaseAuth auth = ConfigFireBase.getFBAuth();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogin = findViewById(R.id.btnLogin);
        editEmail = findViewById(R.id.editLoginEmail);
        editSenha = findViewById(R.id.editLoginSenha);

    }

    public void logar(View view){
        String textoEmail = editEmail.getText().toString();
        String textoSenha = editSenha.getText().toString();

        if (!textoEmail.isEmpty()) {
            if (!textoSenha.isEmpty()) {

                usuario = new Usuario();
                usuario.setEmail(textoEmail);
                usuario.setSenha(textoSenha);
                validarLogin();

            } else {
                Toast.makeText(this, "Preencha a sua senha!", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(this, "Preencha o seu e-mail!", Toast.LENGTH_LONG).show();
        }
    }
    public void validarLogin(){
        auth.signInWithEmailAndPassword(usuario.getEmail(), usuario.getSenha()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    abrirTelaPrincipal();
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

                    Toast.makeText(MainActivity.this, excessao, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void abrirCadastro(View view){
        startActivity(new Intent(this, CadastroActivity.class));
    }

    public void abrirTelaPrincipal(){
        startActivity(new Intent(MainActivity.this, PrincipalActivity.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(auth.getCurrentUser()!=null){
            abrirTelaPrincipal();
        }
    }
}
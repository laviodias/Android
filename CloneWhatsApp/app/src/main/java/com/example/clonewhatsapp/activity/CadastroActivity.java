package com.example.clonewhatsapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.clonewhatsapp.R;
import com.example.clonewhatsapp.config.ConfigFireBase;
import com.example.clonewhatsapp.helper.Base64Custom;
import com.example.clonewhatsapp.helper.UsuarioFirebase;
import com.example.clonewhatsapp.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class CadastroActivity extends AppCompatActivity {

    private TextInputEditText editNome, editEmail, editSenha;
    private Button btnCadastrar;
    private Usuario usuario;
    private FirebaseAuth auth = ConfigFireBase.getFBAuth();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);


        editEmail = findViewById(R.id.editEmail);
        editNome = findViewById(R.id.editNome);
        editSenha = findViewById(R.id.editSenha);
        btnCadastrar = findViewById(R.id.btnCadastrar);

        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textoEmail = editEmail.getText().toString();
                String textoNome = editNome.getText().toString();
                String textoSenha = editSenha.getText().toString();

                //Validação dos campos:
                if(!textoNome.isEmpty()){
                    if(!textoEmail.isEmpty()){
                        if(!textoSenha.isEmpty()){

                            usuario = new Usuario();
                            usuario.setEmail(textoEmail);
                            usuario.setNome(textoNome);
                            usuario.setSenha(textoSenha);

                            cadastrarUser();


                        }else {
                            Toast.makeText(CadastroActivity.this, "Preencha a sua senha!", Toast.LENGTH_LONG).show();
                        }

                    }else {
                        Toast.makeText(CadastroActivity.this, "Preencha o seu e-mail!", Toast.LENGTH_LONG).show();
                    }

                }else {
                    Toast.makeText(CadastroActivity.this, "Preencha o seu nome!", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    public void cadastrarUser(){

        auth.createUserWithEmailAndPassword(usuario.getEmail(), usuario.getSenha()).addOnCompleteListener(CadastroActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    UsuarioFirebase.atualizarNome(usuario.getNome());

                    String idUser = Base64Custom.codificarBase64(usuario.getEmail());
                    usuario.setIdUser(idUser);
                    usuario.salvarUser();

                    finish();
                }else {

                    String excessao = "";
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e){
                        excessao = "Digite uma senha mais forte!";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        excessao = "Por favor, digite um e-mail válido";
                    }catch (FirebaseAuthUserCollisionException e){
                        excessao = "E-mail já cadastrado";
                    }catch (Exception e){
                        excessao = "Erro ao cadastrar usuário: " + e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(CadastroActivity.this, excessao, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
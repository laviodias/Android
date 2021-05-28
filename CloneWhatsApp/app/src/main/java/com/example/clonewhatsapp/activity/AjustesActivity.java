package com.example.clonewhatsapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.clonewhatsapp.R;
import com.example.clonewhatsapp.config.ConfigFireBase;
import com.example.clonewhatsapp.helper.Permissao;
import com.example.clonewhatsapp.helper.UsuarioFirebase;
import com.example.clonewhatsapp.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import de.hdodenhof.circleimageview.CircleImageView;

public class AjustesActivity extends AppCompatActivity {

    private ImageButton imgbtnCamera, imgbtnGaleria;
    private CircleImageView fotoPerfil;
    private EditText editNomeUser;

    private static final int SELECAO_CAMERA = 100;
    private static final int SELECAO_GALERIA = 200;

    private FirebaseAuth auth = ConfigFireBase.getFBAuth();
    private DatabaseReference database = ConfigFireBase.getFBDatabase();
    private StorageReference storage = ConfigFireBase.getGBStorage();
    private FirebaseUser usuario = UsuarioFirebase.getUserAtual();
    private Usuario usuarioLogado = UsuarioFirebase.getDadosUser();

    private String idUser = UsuarioFirebase.getIdUser();

    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);

        imgbtnCamera = findViewById(R.id.imgbtnCamera);
        imgbtnGaleria = findViewById(R.id.imgbtnGaleria);
        fotoPerfil = findViewById(R.id.fotoPerfil);
        editNomeUser = findViewById(R.id.editNomeUser);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Ajustes");
        setSupportActionBar(toolbar);

        //Exibir a opção de voltar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Valida permissões
        Permissao.validarPermissao(permissoes, this, 1);

        imgbtnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Abrir camera:
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, SELECAO_CAMERA);
                }

            }

        });

        imgbtnGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Abrir galeria:
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if(i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, SELECAO_GALERIA);
                }
            }
        });

        //Recupera dados do usuário:
        editNomeUser.setText(usuario.getDisplayName());

        Uri url = usuario.getPhotoUrl();
        if(url!=null){
            Glide.with(AjustesActivity.this).load(url).into(fotoPerfil);
        }else{
            fotoPerfil.setImageResource(R.drawable.padrao);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            Bitmap imagem = null;
            try{
                switch (requestCode){
                    case SELECAO_CAMERA:
                        imagem = (Bitmap) data.getExtras().get("data");
                        break;
                    case SELECAO_GALERIA:
                        Uri localImgSelecionado = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImgSelecionado);
                        break;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            if(imagem!=null){
                fotoPerfil.setImageBitmap(imagem);

                //recuperar dados da imagem para o firebase:
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                byte[] dadosImg = baos.toByteArray();

                //Salvar imagem no firebase
                final StorageReference imagemRef = storage
                        .child("imagens")
                        .child("perfil")
                        .child(idUser + ".jpeg");

                UploadTask uploadTask = imagemRef.putBytes(dadosImg);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AjustesActivity.this, "Erro ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(AjustesActivity.this, "Sucesso ao fazer upload da imagem", Toast.LENGTH_SHORT).show();

                        //Recuperar url da imagem
                        imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                Uri url = task.getResult();
                                atualizarFotoUser(url);
                            }
                        });
                    }
                });

            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for(int permissaoResultado: grantResults){
            if(permissaoResultado== PackageManager.PERMISSION_DENIED){
                alertaValidacao();
            }
        }
    }

    public void atualizarFotoUser(Uri url){
        UsuarioFirebase.atualizarFoto(url);
        usuarioLogado.setFoto(url.toString());
        usuarioLogado.atualizarUser();
    }

    public void atualizarNomeUser(View view){
        String nome = editNomeUser.getText().toString();
        boolean retorno = UsuarioFirebase.atualizarNome(nome);
        if(retorno){

            usuarioLogado.setNome(nome);
            usuarioLogado.atualizarUser();

            Toast.makeText(AjustesActivity.this, "Nome atualizado com sucesso", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(AjustesActivity.this, "Falha ao atualizar nome", Toast.LENGTH_SHORT).show();
        }
     }

    private void alertaValidacao(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle("Permissões negadas")
                    .setMessage("Para usar o app é necessário aceitar as permissões")
                    .setCancelable(false)
                    .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });

        AlertDialog dialog = builder.create();
        dialog.show();
    }



}
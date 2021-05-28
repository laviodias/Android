package com.example.clonewhatsapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.clonewhatsapp.adapter.MensagensAdapter;
import com.example.clonewhatsapp.config.ConfigFireBase;
import com.example.clonewhatsapp.helper.Base64Custom;
import com.example.clonewhatsapp.helper.UsuarioFirebase;
import com.example.clonewhatsapp.model.Conversa;
import com.example.clonewhatsapp.model.Mensagem;
import com.example.clonewhatsapp.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clonewhatsapp.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private TextView txtNomeChat;
    private CircleImageView fotoPerfilChat;
    private ImageView imageCamera;
    private EditText editMensagem;
    private FloatingActionButton fabEnviar;
    private RecyclerView recyclerMensagens;

    private Usuario userDestinatario;
    private DatabaseReference database;
    private DatabaseReference mensagensRef;
    private StorageReference storage;
    private String idUserRemetente;
    private String idUserDestinatario;

    private static final int SELECAO_CAMERA = 100;

    private MensagensAdapter adapter;
    private List<Mensagem> mensagens = new ArrayList<>();
    private ChildEventListener childEventListenerMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        //Exibir a opção de voltar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Configurações iniciais
        txtNomeChat = findViewById(R.id.txtNomeChat);
        fotoPerfilChat = findViewById(R.id.fotoPerfilChat);
        editMensagem = findViewById(R.id.txtMensagem);
        fabEnviar = findViewById(R.id.fabEnviar);
        recyclerMensagens = findViewById(R.id.recylcerMensagens);
        imageCamera = findViewById(R.id.imageCamera);

        idUserRemetente = UsuarioFirebase.getIdUser();

        //Recupera o usuário, passado do ContatosFragment
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            userDestinatario = (Usuario) bundle.getSerializable("chatContato");

            txtNomeChat.setText(userDestinatario.getNome());

            String foto = userDestinatario.getFoto();
            if(foto!=null){
                Uri url = Uri.parse(userDestinatario.getFoto());

                Glide.with(ChatActivity.this)
                        .load(url)
                        .into(fotoPerfilChat);
            }else{
                fotoPerfilChat.setImageResource(R.drawable.padrao);
            }

            idUserDestinatario = Base64Custom.codificarBase64(userDestinatario.getEmail());

        }

        //Configuração adapter
        adapter = new MensagensAdapter(mensagens, getApplicationContext());

        //Configuração recyclerview
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerMensagens.setLayoutManager(layoutManager);
        recyclerMensagens.setHasFixedSize(true);
        recyclerMensagens.setAdapter(adapter);

        //Configurar database
        database = ConfigFireBase.getFBDatabase();
        storage = ConfigFireBase.getGBStorage();
        mensagensRef = database.child("mensagens")
                .child(idUserRemetente)
                .child(idUserDestinatario);

        //Configurar clique na camera
        imageCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Abrir camera:
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, SELECAO_CAMERA);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            Bitmap imagem = null;

            try {
                switch (requestCode) {
                    case SELECAO_CAMERA:
                        imagem = (Bitmap) data.getExtras().get("data");
                        break;
                }

            }catch (Exception e){
                e.printStackTrace();
            }

            if (imagem!=null){
                //recuperar dados da imagem para o firebase:
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                byte[] dadosImg = baos.toByteArray();

                //Criar nome da imagem
                String nomeImagem = UUID.randomUUID().toString();

                //Salvar imagem no FB
                StorageReference imagemRef = storage.child("imagens")
                                            .child("fotos")
                                            .child(idUserRemetente)
                                            .child(nomeImagem);

                UploadTask uploadTask = imagemRef.putBytes(dadosImg);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Erro", "Erro ao fazer upload da imagem");
                        Toast.makeText(ChatActivity.this,"Erro ao fazer upload da imagem", Toast.LENGTH_LONG).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                String url = task.getResult().toString();

                                Mensagem mensagem = new Mensagem();
                                mensagem.setIdUser(idUserRemetente);
                                mensagem.setMensagem("imagem.jpeg");
                                mensagem.setImagem(url);

                                //Salvar msg para o remetente
                                salvarMensagem(idUserRemetente, idUserDestinatario, mensagem);

                                //Salvar msg para o destinatario
                                salvarMensagem(idUserDestinatario, idUserRemetente, mensagem);
                            }
                        });
                    }
                });

            }

        }
    }

    public void enviarMensagem(View view){
        String txtMensagem = editMensagem.getText().toString();

        if(!txtMensagem.isEmpty()){
            Mensagem mensagem = new Mensagem();
            mensagem.setIdUser(idUserRemetente);
            mensagem.setMensagem(txtMensagem);

            //Salvar mensagem
            salvarMensagem(idUserRemetente, idUserDestinatario, mensagem);

            //Salvar conversa
            salvarConversa(mensagem);

        }else {
            Toast.makeText(ChatActivity.this,"Digite uma mensagem para enviar!", Toast.LENGTH_LONG).show();
        }

    }

    private void salvarConversa(Mensagem msg){
        Conversa conversaRemetente = new Conversa();
        conversaRemetente.setIdRemetente(idUserRemetente);
        conversaRemetente.setIdDestinatario(idUserDestinatario);
        conversaRemetente.setUltimaMensagem(msg.getMensagem());
        conversaRemetente.setUsuarioExibicao(userDestinatario);

        conversaRemetente.salvar();

    }

    private void salvarMensagem(String idRemetente, String idDestinatario, Mensagem mensagem){
        DatabaseReference database = ConfigFireBase.getFBDatabase();
        DatabaseReference mensagemRef = database.child("mensagens");

        //Salvar para o remetente
        mensagemRef.child(idRemetente)
                .child(idDestinatario)
                .push()
                .setValue(mensagem);

        //Salvar para o destinatário
        mensagemRef.child(idDestinatario)
                .child(idRemetente)
                .push()
                .setValue(mensagem);

        //Limpar texto
        editMensagem.setText("");
    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarMensagens();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mensagensRef.removeEventListener(childEventListenerMsg);
    }

    private void recuperarMensagens(){

        childEventListenerMsg = mensagensRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Mensagem msg = snapshot.getValue(Mensagem.class);
                mensagens.add(msg);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
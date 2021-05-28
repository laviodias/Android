package com.example.clonewhatsapp.activity;

import android.os.Bundle;

import com.example.clonewhatsapp.adapter.ContatosAdapter;
import com.example.clonewhatsapp.config.ConfigFireBase;
import com.example.clonewhatsapp.helper.RecyclerItemClickListener;
import com.example.clonewhatsapp.helper.UsuarioFirebase;
import com.example.clonewhatsapp.model.Usuario;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.AdapterView;

import com.example.clonewhatsapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GrupoActivity extends AppCompatActivity {

    private RecyclerView recyclerMembros, recyclerMembrosSelecionados;
    private ContatosAdapter contatosAdapter;
    private List<Usuario> listaMembros = new ArrayList<>();
    private List<Usuario> listaSelecionados = new ArrayList<>();
    private ValueEventListener valueEventListenerMembros;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grupo);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Criar grupo");
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerMembros = findViewById(R.id.recyclerMembros);
        recyclerMembrosSelecionados = findViewById(R.id.recyclerMembrosSelecionados);

        userRef = ConfigFireBase.getFBDatabase().child("usuarios");

        //Configura adapter
        contatosAdapter = new ContatosAdapter(listaMembros, getApplicationContext());

        //Configura recyclerview
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerMembros.setLayoutManager(layoutManager);
        recyclerMembros.setHasFixedSize(true);
        recyclerMembros.setAdapter(contatosAdapter);

        recyclerMembros.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(),
                        recyclerMembros,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Usuario userSelecionado = listaMembros.get(position);

                                //remover o usuario selecionado da listaMembros
                                listaMembros.remove(userSelecionado);
                                contatosAdapter.notifyDataSetChanged();

                                //add na lista de selecionados
                                listaSelecionados.add(userSelecionado);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                ));

    }

    public void recuperarContatos(){
        valueEventListenerMembros = userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dados: snapshot.getChildren()){
                    Usuario usuario = dados.getValue(Usuario.class);
                    if(!usuario.getEmail().equals(UsuarioFirebase.getUserAtual().getEmail())) {
                        listaMembros.add(usuario);
                    }
                }
                contatosAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onStart() {
        listaMembros.clear();
        super.onStart();
        recuperarContatos();
    }


    @Override
    public void onStop() {
        super.onStop();
        userRef.removeEventListener(valueEventListenerMembros);
    }

}
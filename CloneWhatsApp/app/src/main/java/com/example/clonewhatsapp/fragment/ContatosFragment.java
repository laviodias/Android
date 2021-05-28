package com.example.clonewhatsapp.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.renderscript.Sampler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.clonewhatsapp.R;
import com.example.clonewhatsapp.activity.ChatActivity;
import com.example.clonewhatsapp.activity.GrupoActivity;
import com.example.clonewhatsapp.adapter.ContatosAdapter;
import com.example.clonewhatsapp.config.ConfigFireBase;
import com.example.clonewhatsapp.helper.RecyclerItemClickListener;
import com.example.clonewhatsapp.helper.UsuarioFirebase;
import com.example.clonewhatsapp.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContatosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContatosFragment extends Fragment {

    private RecyclerView recyclerViewContatos;
    private ContatosAdapter adapter;
    private ArrayList<Usuario> listaContatos = new ArrayList<>();

    private DatabaseReference userRef = ConfigFireBase.getFBDatabase().child("usuarios");

    private ValueEventListener valueEventListenerContatos;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ContatosFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContatosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContatosFragment newInstance(String param1, String param2) {
        ContatosFragment fragment = new ContatosFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contatos, container, false);


        recyclerViewContatos = view.findViewById(R.id.reciclerViewContatos);

        //Configurar Adapter:
        adapter = new ContatosAdapter(listaContatos, getActivity());

        //Configurar RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewContatos.setLayoutManager(layoutManager);
        recyclerViewContatos.setHasFixedSize(true);
        recyclerViewContatos.setAdapter(adapter);

        //Configurar evento de clique no RV:
        recyclerViewContatos.addOnItemTouchListener(new RecyclerItemClickListener(
                getActivity(), recyclerViewContatos, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Usuario userSelecionado = listaContatos.get(position);

                boolean cabecalho = userSelecionado.getEmail().isEmpty();

                if (cabecalho){
                    Intent i = new Intent(getActivity(), GrupoActivity.class);
                    startActivity(i);

                }else{
                    Intent i = new Intent(getActivity(), ChatActivity.class);

                    //Passa o usuário para o ChatActivity
                    i.putExtra("chatContato", userSelecionado);

                    startActivity(i);
                }


            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }
        ));

        itemGrupo();

        return view;
    }

    public void itemGrupo(){
        Usuario itemGrupo = new Usuario();
        itemGrupo.setNome("Novo Grupo");
        itemGrupo.setEmail("");

        listaContatos.add(itemGrupo);
    }

    public void recuperarContatos(){
        valueEventListenerContatos = userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //listaContatos.clear();
                for(DataSnapshot dados: snapshot.getChildren()){
                    Usuario usuario = dados.getValue(Usuario.class);
                    if(!usuario.getEmail().equals(UsuarioFirebase.getUserAtual().getEmail())) {
                        listaContatos.add(usuario);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onStart() {
        listaContatos.clear();
        itemGrupo();
        super.onStart();
        recuperarContatos();
    }


    @Override
    public void onStop() {
        super.onStop();
        userRef.removeEventListener(valueEventListenerContatos);
    }
}
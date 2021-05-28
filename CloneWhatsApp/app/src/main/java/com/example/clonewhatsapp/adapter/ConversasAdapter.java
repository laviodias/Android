package com.example.clonewhatsapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.clonewhatsapp.R;
import com.example.clonewhatsapp.model.Conversa;
import com.example.clonewhatsapp.model.Usuario;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversasAdapter extends RecyclerView.Adapter<ConversasAdapter.MyViewHolder> {

    private List<Conversa> conversas;
    private Context context;

    public ConversasAdapter(List<Conversa> lista, Context c) {
        this.context = c;
        this.conversas = lista;
    }

    @NonNull
    @Override
    public ConversasAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_conversas, parent, false);
        return new ConversasAdapter.MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversasAdapter.MyViewHolder holder, int position) {
        Conversa conversa = conversas.get(position);
        holder.ultimaMensagem.setText(conversa.getUltimaMensagem());

        Usuario usuario = conversa.getUsuarioExibicao();
        holder.nome.setText(usuario.getNome());


        if(usuario.getFoto()!=null){
            Uri url = Uri.parse(usuario.getFoto());
            Glide.with(context).load(url).into(holder.foto);
        }else{
            holder.foto.setImageResource(R.drawable.padrao);
        }
    }

    @Override
    public int getItemCount() {
        return conversas.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        CircleImageView foto;
        TextView nome, ultimaMensagem;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            foto = itemView.findViewById(R.id.imageFotoConversa);
            nome = itemView.findViewById(R.id.txtNomeConversa);
            ultimaMensagem = itemView.findViewById(R.id.txtUltimaMensagem);
        }
    }
}

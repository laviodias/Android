package com.example.clonewhatsapp.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clonewhatsapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class GrupoAdapter extends RecyclerView.Adapter<GrupoAdapter.MyViewHolder>{



    @NonNull
    @Override
    public GrupoAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull GrupoAdapter.MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        CircleImageView foto;
        TextView nome;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            foto = itemView.findViewById(R.id.imageFotoConversa);
            nome = itemView.findViewById(R.id.txtNomeConversa);

        }
    }
}

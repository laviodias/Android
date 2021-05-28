package com.example.clonewhatsapp.model;

import com.example.clonewhatsapp.config.ConfigFireBase;
import com.google.firebase.database.DatabaseReference;

public class Conversa {

    private String idRemetente, idDestinatario, UltimaMensagem;
    private Usuario usuarioExibicao;

    public Conversa() {
    }

    public void salvar(){
        DatabaseReference databaseReference = ConfigFireBase.getFBDatabase();
        DatabaseReference conversaRef = databaseReference.child("conversas");

        conversaRef.child(this.getIdRemetente())
                    .child(this.getIdDestinatario())
                    .setValue(this);
    }

    public String getIdRemetente() {
        return idRemetente;
    }

    public void setIdRemetente(String idRemetente) {
        this.idRemetente = idRemetente;
    }

    public String getIdDestinatario() {
        return idDestinatario;
    }

    public void setIdDestinatario(String idDestinatario) {
        this.idDestinatario = idDestinatario;
    }

    public String getUltimaMensagem() {
        return UltimaMensagem;
    }

    public void setUltimaMensagem(String ultimaMensagem) {
        UltimaMensagem = ultimaMensagem;
    }

    public Usuario getUsuarioExibicao() {
        return usuarioExibicao;
    }

    public void setUsuarioExibicao(Usuario usuarioExibicao) {
        this.usuarioExibicao = usuarioExibicao;
    }
}

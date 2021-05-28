package com.example.clonewhatsapp.model;

import com.example.clonewhatsapp.config.ConfigFireBase;
import com.example.clonewhatsapp.helper.UsuarioFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Usuario implements Serializable {

    private String nome, email, senha, idUser, foto;

    public Usuario() {
    }

    public void salvarUser(){
        DatabaseReference reference = ConfigFireBase.getFBDatabase();
        reference.child("usuarios")
                .child(this.idUser)
                .setValue(this);
    }

    public void atualizarUser(){
        String identificador = UsuarioFirebase.getIdUser();

        DatabaseReference database = ConfigFireBase.getFBDatabase();

        DatabaseReference userRef = database.child("usuarios").child(identificador);

        Map<String, Object> valoresUser = converterParaMap();

        userRef.updateChildren(valoresUser);
    }

    @Exclude
    public Map<String, Object> converterParaMap(){
        HashMap<String, Object> userMap = new HashMap<>();

        userMap.put("email", getEmail());
        userMap.put("nome", getNome());
        userMap.put("foto", getFoto());

        return userMap;
    }


    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    @Exclude
    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }
}

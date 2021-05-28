package com.example.cloneorganizze.model;

import com.example.cloneorganizze.config.ConfigFireBase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

public class Usuario {

    private String nome, email, senha, idUser;
    private Double receitaTotal = 0.0;
    private Double despesaTotal = 0.0;

    public Usuario() {
    }

    public void salvarUser(){
        DatabaseReference reference = ConfigFireBase.getFBDatabase();
        reference.child("usuarios")
                .child(this.idUser)
                .setValue(this);
    }

    @Exclude
    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
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

    public Double getReceitaTotal() {
        return receitaTotal;
    }

    public void setReceitaTotal(Double receitaTotal) {
        this.receitaTotal = receitaTotal;
    }

    public Double getDespesaTotal() {
        return despesaTotal;
    }

    public void setDespesaTotal(Double despesaTotal) {
        this.despesaTotal = despesaTotal;
    }
}

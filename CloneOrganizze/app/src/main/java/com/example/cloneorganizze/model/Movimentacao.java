package com.example.cloneorganizze.model;

import com.example.cloneorganizze.config.ConfigFireBase;
import com.example.cloneorganizze.helper.Base64Custom;
import com.example.cloneorganizze.helper.DateUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class Movimentacao {

    private String data, categoria, descricao, tipo, chave;
    private double valor;

    public Movimentacao() {
    }

    public void salvar(String data){

        FirebaseAuth auth = ConfigFireBase.getFBAuth();

        //recuperar email do usuário:
        String idUser = Base64Custom.codificarBase64(auth.getCurrentUser().getEmail());

        DatabaseReference reference = ConfigFireBase.getFBDatabase();

        reference.child("movimentacao")
                .child(idUser)
                .child(DateUtil.formatarData(data))
                .push()
                .setValue(this);

    }

    public String getChave() {
        return chave;
    }

    public void setChave(String chave) {
        this.chave = chave;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
}

package com.example.cloneorganizze.helper;

import java.text.SimpleDateFormat;

public class DateUtil {

    private String dia, mes, ano;

    public static String dataAtual(){

        long data = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        String dataString = simpleDateFormat.format(data);

        return dataString;
    }

    public static String formatarData(String data){

       String retorno[] = data.split("/");

       String mesAno = retorno[1] + retorno[2];

       return mesAno;
    }

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }
}

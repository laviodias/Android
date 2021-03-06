package com.example.cloneorganizze.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cloneorganizze.R;
import com.example.cloneorganizze.config.ConfigFireBase;
import com.example.cloneorganizze.helper.Base64Custom;
import com.example.cloneorganizze.helper.DateUtil;
import com.example.cloneorganizze.model.Movimentacao;
import com.example.cloneorganizze.model.Usuario;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


public class DespesasActivity extends AppCompatActivity {

    private TextInputEditText campoData, campoCategoria, campoDescricao;
    private EditText campoValor;
    private Double despesaTotal, despesaPreenchida, despesaAtualizada;
    private Movimentacao movimentacao;
    private DatabaseReference DbRefence = ConfigFireBase.getFBDatabase();
    private FirebaseAuth AuthReference = ConfigFireBase.getFBAuth();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_despesas);

        campoCategoria = findViewById(R.id.editCategoria);
        campoValor = findViewById(R.id.editValor);
        campoData = findViewById(R.id.editData);
        campoDescricao = findViewById(R.id.editDescricao);

        campoData.setText(DateUtil.dataAtual());

        recuperarDespesa();
    }

    public void salvarDespesa(View view){

        String textoValor = campoValor.getText().toString();
        String textoCategoria = campoCategoria.getText().toString();
        String textoDescricao = campoDescricao.getText().toString();
        String textoData = campoData.getText().toString();

        if(validarCampos()) {
            movimentacao = new Movimentacao();

            movimentacao.setValor(Double.parseDouble(textoValor));
            movimentacao.setCategoria(textoCategoria);
            movimentacao.setDescricao(textoDescricao);
            movimentacao.setData(textoData);
            movimentacao.setTipo("d");

            despesaPreenchida = Double.parseDouble(textoValor);
            despesaAtualizada = despesaPreenchida + despesaTotal;
            atualizarDespesa(despesaAtualizada);

            movimentacao.salvar(textoData);
            finish();
        }
    }

    public Boolean validarCampos(){
        String textoValor = campoValor.getText().toString();
        String textoCategoria = campoCategoria.getText().toString();
        String textoDescricao = campoDescricao.getText().toString();
        String textoData = campoData.getText().toString();

        if(!textoValor.isEmpty()){
            if(!textoData.isEmpty()){
                if(!textoCategoria.isEmpty()){
                    if(!textoDescricao.isEmpty()){
                        return true;
                    }else{
                        Toast.makeText(DespesasActivity.this, "Preencha a descri????o!", Toast.LENGTH_LONG).show();
                        return false;
                    }
                }else{
                    Toast.makeText(DespesasActivity.this, "Preencha a categoria!", Toast.LENGTH_LONG).show();
                    return false;
                }
            }else{
                Toast.makeText(DespesasActivity.this, "Preencha a data!", Toast.LENGTH_LONG).show();
                return false;
            }
        }else{
            Toast.makeText(DespesasActivity.this, "Preencha o valor!", Toast.LENGTH_LONG).show();
            return false;
        }

    }

    public void recuperarDespesa(){

        String idUser = Base64Custom.codificarBase64(AuthReference.getCurrentUser().getEmail());

        DatabaseReference userRef = DbRefence.child("usuarios").child(idUser);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);

                despesaTotal = usuario.getDespesaTotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void atualizarDespesa(Double valor){
        String idUser = Base64Custom.codificarBase64(AuthReference.getCurrentUser().getEmail());

        DatabaseReference userRef = DbRefence.child("usuarios").child(idUser);

        userRef.child("despesaTotal").setValue(valor);
    }
}
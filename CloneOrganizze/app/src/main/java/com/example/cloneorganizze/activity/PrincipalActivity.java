package com.example.cloneorganizze.activity;

import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.cloneorganizze.adapter.AdapterMovimentacao;
import com.example.cloneorganizze.config.ConfigFireBase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cloneorganizze.R;
import com.example.cloneorganizze.helper.Base64Custom;
import com.example.cloneorganizze.helper.DateUtil;
import com.example.cloneorganizze.model.Movimentacao;
import com.example.cloneorganizze.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PrincipalActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private TextView txtUser, txtSaldo;
    private double receitaTotal, despesaTotal;
    private MaterialCalendarView calendarView;
    private RecyclerView recyclerMov;
    private String mesAno, mesSelecionado;

    private DatabaseReference DbRefence = ConfigFireBase.getFBDatabase();
    private FirebaseAuth AuthReference = ConfigFireBase.getFBAuth();
    private DatabaseReference userRef;
    private DatabaseReference movRef;
    private ValueEventListener valueEventListenerUser;
    private ValueEventListener valueEventListenerMov;
    private AdapterMovimentacao adapterMovimentacao;
    private List<Movimentacao> lista = new ArrayList<>();
    private Movimentacao movimentacao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("");

        txtSaldo = findViewById(R.id.txtSaldo);
        txtUser = findViewById(R.id.txtUser);
        calendarView = findViewById(R.id.calendarView);
        recyclerMov = findViewById(R.id.recyclerMov);

        configCalendario();
        swipe();

        //Config recyclerview:
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerMov.setLayoutManager(layoutManager);
        recyclerMov.setHasFixedSize(true);

        adapterMovimentacao = new AdapterMovimentacao(lista, this);
        recyclerMov.setAdapter(adapterMovimentacao);

    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarResumo();
        recuperarMov();
    }

    @Override
    protected void onStop() {
        super.onStop();
        userRef.removeEventListener(valueEventListenerUser);
        movRef.removeEventListener(valueEventListenerMov);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuSair:
                sair();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public void swipe(){
        ItemTouchHelper.Callback itemTouch = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int dragFlags = ItemTouchHelper.ACTION_STATE_IDLE;
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                excluirMov(viewHolder);
            }
        };

        new ItemTouchHelper(itemTouch).attachToRecyclerView(recyclerMov);
    }

    public void excluirMov(RecyclerView.ViewHolder viewHolder){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Excluir Movimentação");
        alertDialog.setMessage("Você tem certeza que deseja excluir esta movimentação?");
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton("Confimar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int position = viewHolder.getAdapterPosition();
                movimentacao = lista.get(position);

                String idUser = Base64Custom.codificarBase64(AuthReference.getCurrentUser().getEmail());
                movRef = DbRefence.child("movimentacao")
                        .child(idUser)
                        .child(mesAno);

                movRef.child(movimentacao.getChave()).removeValue();
                adapterMovimentacao.notifyItemRemoved(position);
                atualizarSaldo();
            }
        });

        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(PrincipalActivity.this, "Cancelado", Toast.LENGTH_LONG).show();
                adapterMovimentacao.notifyDataSetChanged();
            }
        });

        AlertDialog alert = alertDialog.create();
        alert.show();
    }

    public void atualizarSaldo(){

        String idUser = Base64Custom.codificarBase64(AuthReference.getCurrentUser().getEmail());
        userRef = DbRefence.child("usuarios").child(idUser);

        if(movimentacao.getTipo().equals("r")) {
            receitaTotal-=movimentacao.getValor();
            userRef.child("receitaTotal").setValue(receitaTotal);
        }else{
            despesaTotal-=movimentacao.getValor();
            userRef.child("despesaTotal").setValue(despesaTotal);
        }
    }

    public void recuperarMov(){
        String idUser = Base64Custom.codificarBase64(AuthReference.getCurrentUser().getEmail());

        movRef = DbRefence.child("movimentacao")
                        .child(idUser)
                        .child(mesAno);

        valueEventListenerMov = movRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lista.clear();
                for(DataSnapshot dados: snapshot.getChildren()){
                    Movimentacao movimentacao = dados.getValue(Movimentacao.class);
                    movimentacao.setChave(dados.getKey());
                    lista.add(movimentacao);
                }
                adapterMovimentacao.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void recuperarResumo(){

        String idUser = Base64Custom.codificarBase64(AuthReference.getCurrentUser().getEmail());

        userRef = DbRefence.child("usuarios").child(idUser);

        valueEventListenerUser = userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);

                txtUser.setText("Olá, " + usuario.getNome() + "!");

                receitaTotal = usuario.getReceitaTotal();
                despesaTotal = usuario.getDespesaTotal();

                Double saldo = receitaTotal - despesaTotal;

                DecimalFormat decimalFormat = new DecimalFormat("0.00");
                String saldo_formatado = decimalFormat.format(saldo);
                txtSaldo.setText("R$ " + saldo_formatado);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void addReceita(View view){
        startActivity(new Intent(PrincipalActivity.this, ReceitasActivity.class));
    }

    public void addDespesa(View view){
        startActivity(new Intent(PrincipalActivity.this, DespesasActivity.class));
    }

    public void sair(){
        auth = ConfigFireBase.getFBAuth();

        auth.signOut();

        startActivity(new Intent(PrincipalActivity.this, LoginActivity.class));
        finish();
    }

    public void configCalendario(){
        String meses[] = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto",
                "Setembro","Outubro","Novembro","Dezembro"};
        calendarView.setTitleMonths(meses);

        mesSelecionado = String.format("%02d",calendarView.getCurrentDate().getMonth());

        mesAno = mesSelecionado + "" + calendarView.getCurrentDate().getYear();


        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                String mesSelecionado = String.format("%02d", date.getMonth());
                mesAno = String.valueOf(mesSelecionado + "" + date.getYear());

                movRef.removeEventListener(valueEventListenerMov);
                recuperarMov();
            }
        });

    }
}
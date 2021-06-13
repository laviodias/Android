package com.laviodias.youtube.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.laviodias.youtube.R;
import com.laviodias.youtube.adapter.AdapterVideo;
import com.laviodias.youtube.api.YoutubeService;
import com.laviodias.youtube.helper.RecyclerItemClickListener;
import com.laviodias.youtube.helper.RetrofitConfig;
import com.laviodias.youtube.helper.YoutubeConfig;
import com.laviodias.youtube.model.canal.Canal;
import com.laviodias.youtube.model.Item;
import com.laviodias.youtube.model.Resultado;
import com.laviodias.youtube.model.Video;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerVideos;
    private MaterialSearchView searchView;

    private List<Item> videos = new ArrayList<>();
    private AdapterVideo adapterVideo;

    private Retrofit retrofit;
    private YoutubeService youtubeService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //configura toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("YouTube");
        setSupportActionBar(toolbar);

        //inicializa componentes
        recyclerVideos = findViewById(R.id.recyclerVideos);
        searchView = findViewById(R.id.searchView);

        //configurações iniciais
        retrofit = RetrofitConfig.getRetrofit();

        //valor pesquisa vazio retorna a lista inteira
        recuperarVideos("");


        //configura metodos para searchview
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                recuperarVideos(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {
                recuperarVideos("");
            }
        });
    }

    public void configRecyclerView(){
        adapterVideo = new AdapterVideo(videos, this);
        recyclerVideos.setHasFixedSize(true);
        recyclerVideos.setLayoutManager(new LinearLayoutManager(this));
        recyclerVideos.setAdapter(adapterVideo);

        //configura evento de clique
        recyclerVideos.addOnItemTouchListener(
                new RecyclerItemClickListener(
                this, recyclerVideos, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Item video = videos.get(position);
                        String idVideo = video.id.videoId;


                        youtubeService.recuperarEstatisticas("statistics", idVideo, YoutubeConfig.CHAVE_YT_API).enqueue(new Callback<Video>() {
                            @Override
                            public void onResponse(Call<Video> call, Response<Video> response) {
                                String likes = response.body().items.get(0).statistics.likeCount;
                                String dislikes = response.body().items.get(0).statistics.dislikeCount;
                                String views = response.body().items.get(0).statistics.viewCount;
                                String commentCount = response.body().items.get(0).statistics.commentCount;

                                Intent i = new Intent(MainActivity.this, PlayerActivity.class);

                                        i.putExtra("idVideo", idVideo)
                                        .putExtra("titulo", video.snippet.title)
                                        .putExtra("descricao", video.snippet.description)
                                        .putExtra("data", video.snippet.publishedAt)
                                        .putExtra("likes", likes)
                                        .putExtra("dislikes", dislikes)
                                        .putExtra("views", views)
                                        .putExtra("commentCount", commentCount);

                                youtubeService.recuperarCanal("snippet","statistics", YoutubeConfig.CANAL_ID, YoutubeConfig.CHAVE_YT_API).enqueue(new Callback<Canal>() {
                                    @Override
                                    public void onResponse(Call<Canal> call, Response<Canal> response) {

                                        if(response.isSuccessful()){
                                            String url = response.body().items.get(0).snippet.thumbnails.high.url;
                                            i.putExtra("url", url);

                                            String subscribers = response.body().items.get(0).statistics.subscriberCount;
                                            i.putExtra("subsCount", subscribers);

                                            String title = response.body().items.get(0).snippet.title;
                                            i.putExtra("channel", title);

                                            startActivity(i);

                                        }

                                    }

                                    @Override
                                    public void onFailure(Call<Canal> call, Throwable t) {
                                        System.out.println(t.getMessage());
                                    }
                                });

                            }

                            @Override
                            public void onFailure(Call<Video> call, Throwable t) {

                            }
                        });

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }
        ));
    }

    private void recuperarVideos(String pesquisa){
        youtubeService = retrofit.create(YoutubeService.class);

        String q = pesquisa.replaceAll(" ", "+");

        youtubeService.recuperarVideos(
           "snippet",
           "date",
           "20",
            YoutubeConfig.CHAVE_YT_API,
            YoutubeConfig.CANAL_ID,
            q

        ).enqueue(new Callback<Resultado>() {
            @Override
            public void onResponse(Call<Resultado> call, Response<Resultado> response) {
                if (response.isSuccessful()){
                    Resultado resultado = response.body();
                    videos = resultado.items;

                    configRecyclerView();
                }
            }

            @Override
            public void onFailure(Call<Resultado> call, Throwable t) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.menu_search);
        searchView.setMenuItem(item);

        return true;
    }
}
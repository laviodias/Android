package com.laviodias.youtube.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.laviodias.youtube.R;
import com.laviodias.youtube.helper.YoutubeConfig;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class PlayerActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    private YouTubePlayerView playerView;
    private String idVideo, titulo, descricao, likes, dislikes, views, commentCount, subsCount, channel;
    private TextView txtTitulo, txtDescricao, txtViews, txtLikes, txtDislikes, txtComments, txtSubsCount, txtCanal;
    private ImageView imageSeta;
    private boolean active = false;
    CircleImageView imageCanal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        playerView = findViewById(R.id.playerVideo);
        imageSeta = findViewById(R.id.imageSeta);
        txtTitulo = findViewById(R.id.txtTitulo);
        txtDescricao = findViewById(R.id.txtDescricao);
        txtViews = findViewById(R.id.txtViews);
        txtLikes = findViewById(R.id.txtLikes);
        txtDislikes = findViewById(R.id.txtDislikes);
        txtComments = findViewById(R.id.txtComments);
        imageCanal = findViewById(R.id.imageCanal);
        txtSubsCount = findViewById(R.id.txtSubsCount);
        txtCanal = findViewById(R.id.txtCanal);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            idVideo = bundle.getString("idVideo");
            titulo = bundle.getString("titulo");
            descricao = bundle.getString("descricao");
            likes = bundle.getString("likes");
            dislikes = bundle.getString("dislikes");
            views = bundle.getString("views");
            commentCount = bundle.getString("commentCount");
            subsCount = bundle.getString("subsCount");
            channel = bundle.getString("channel");

            DecimalFormat format = (DecimalFormat) NumberFormat.getInstance(Locale.US);
            format.applyPattern("#,###,###,###");

            String viewsF = format.format(Long.parseLong(views));
            String likesF = format.format(Long.parseLong(likes));
            String dislikesF = format.format(Long.parseLong(dislikes));
            String commentCountF = format.format(Long.parseLong(commentCount));
            String subsCountF = format.format(Long.parseLong(subsCount));

            txtTitulo.setText(titulo);
            txtDescricao.setText(descricao);
            txtViews.setText(viewsF + " visualizações");
            txtLikes.setText(likesF);
            txtDislikes.setText(dislikesF);
            txtComments.setText(commentCountF);
            txtSubsCount.setText(subsCountF + " inscritos");
            txtCanal.setText(channel);

            String url = bundle.getString("url");
            Picasso.get().load(url).resize(180,180).into(imageCanal);

            playerView.initialize(YoutubeConfig.CHAVE_YT_API, this);


        }
    }

    public void mostrarDescricao(View view){
        if(!active){
            txtDescricao.setVisibility(View.VISIBLE);
            Drawable img = getResources().getDrawable(R.drawable.ic_seta_cima);
            imageSeta.setImageDrawable(img);
        }else{
            txtDescricao.setVisibility(View.GONE);
            Drawable img = getResources().getDrawable(R.drawable.ic_seta_baixo);
            imageSeta.setImageDrawable(img);
        }

        active = !active;
    }


    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {

        if(!b){
            youTubePlayer.cueVideo(idVideo);
        }

    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }
}
package com.laviodias.youtube.api;

import com.laviodias.youtube.model.canal.Canal;
import com.laviodias.youtube.model.Resultado;
import com.laviodias.youtube.model.Video;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface YoutubeService {

    /*
      https://www.googleapis.com/youtube/v3/
      search?part=snippet
      &order=date
      &maxResults=20
      &key=AIzaSyClkVdR2Rh-ILMCeCmooJWoKqO7sKWf3xs
      &channelId=UCf_kacKyoRRUP0nM3obzFbg
      &q=pesquisa
    * */

    @GET("search")
    Call<Resultado> recuperarVideos(@Query("part") String part,
                                    @Query("order") String order,
                                    @Query("maxResults") String maxResults,
                                    @Query("key") String key,
                                    @Query("channelId") String channelId,
                                    @Query("q") String q
                                    );

    @GET("videos")
    Call<Video> recuperarEstatisticas(@Query("part") String part,
                                      @Query("id") String id,
                                      @Query("key") String key);

    @GET("channels")
    Call<Canal> recuperarCanal(@Query("part") String part,
                               @Query("part") String part2,
                               @Query("id") String id,
                               @Query("key") String key);
}

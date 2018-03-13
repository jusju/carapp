package com.example.jusju.carapp;


import java.util.Map;


import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by villeaaltonen on 04/10/16.
 */

public interface PassiClient {

    @GET("user/{userID}")
    Call<User> haeKayttaja(
            @Path("userID") String username
    );



    @POST("upload/{filename_without_extension}")
    Call<ResponseBody> tallennaKuva(
            @Path("filename_without_extension") String nimi,
            @Body RequestBody byteKuva
    );

    @GET("feedbackmap/{group_id}/{user_id}")
    Call<ResponseBody> haeVastausMap(@Path("group_id") Integer groupId, @Path("user_id") Integer userId);

    @DELETE("answer/{vastaus_id}/{user_id}")
    Call<ResponseBody> poistaVastaus(@Path("vastaus_id") Integer vastausID, @Path("user_id") Integer userID);



    @GET("join/{join_key}/{user_id}")
    Call<ResponseBody> LiityRyhmaan(@Path("join_key") String groupID,
                                    @Path("user_id") Integer userId);



    @GET("progress/")
    Call<Map<String, Integer>> getProgress();

}

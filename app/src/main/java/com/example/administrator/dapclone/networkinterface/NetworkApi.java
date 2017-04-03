package com.example.administrator.dapclone.networkinterface;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by Administrator on 03/22/2017.
 */

public interface NetworkApi {
	@GET
	@Streaming
	Call<ResponseBody> download(@Url String url, @Header("Range") String rangeDownload, @Header("Connection") String connection);
}

package com.softactive.core.manager;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public abstract class CoreHttpRequester<R_HELPER extends AbstractRequester<?,?>> implements Callback{
	private R_HELPER rHelper;
	private OkHttpClient client;
	
	public CoreHttpRequester() {
		client = createClient();
	}
	
	private OkHttpClient createClient() {
		OkHttpClient.Builder builder = new OkHttpClient.Builder();
		builder.connectTimeout(30, TimeUnit.SECONDS);
		builder.readTimeout(30, TimeUnit.SECONDS);
		builder.writeTimeout(30, TimeUnit.SECONDS);
		builder.retryOnConnectionFailure(true);
		return builder.build();
	}

	@Override
	public void onFailure(Call call, IOException e) {
		onAnswer(rHelper, false, e.getMessage());
	}

	@Override
	public void onResponse(Call call, Response response) throws IOException {
		onAnswer(rHelper, true, response.body().string());		
	}
	
	public abstract void onAnswer(R_HELPER rHelper, boolean successfull, String answer);
	
	public void request(R_HELPER rHelper) {
		client.newCall(createRequest(rHelper)).enqueue(this);
	}
	
	private Request createRequest(R_HELPER rHelper) {
		this.rHelper = rHelper;
		String url = rHelper.calculatedUrl();
		System.out.println("Requesting:");
		System.out.println(url);
		Request.Builder requestBuilder = new Request.Builder().url(url).addHeader("charset", "utf-8");
		return requestBuilder.build();
	}
}

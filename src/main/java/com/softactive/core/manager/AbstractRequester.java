package com.softactive.core.manager;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.softactive.core.exception.MyError;
import com.softactive.core.object.CoreConstants;

import lombok.Getter;
import lombok.Setter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public abstract class AbstractRequester<DAO, CUSTOM extends DAO> implements Callback, CoreConstants, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4263228814206806463L;
	@Getter	@Setter
	protected AbstractHandler<Response, ?, ?, ?, CUSTOM, ?> handler;
	protected OkHttpClient client;
	protected int pageIndex = 0;
	protected int pageCount = 0;
	@Getter	@Setter
	private PostTask post;
	@Setter
	private Integer progress = null;
	@Getter @Setter
	private Error error;
	@Getter
	protected Map<String, Object> sharedParams = null;

	
	public AbstractRequester() {
		OkHttpClient.Builder builder = new OkHttpClient.Builder();
		builder.connectTimeout(30, TimeUnit.SECONDS);
		builder.readTimeout(30, TimeUnit.SECONDS);
		builder.writeTimeout(30, TimeUnit.SECONDS);
		builder.retryOnConnectionFailure(true);
		client = builder.build();
		pageIndex = 0;
		progress = null;
	}
	
	public void clear() {
		pageIndex = 0;
		progress = null;
		handler.clear();
	}

	public Integer getProgress() {
		if (progress == null) {
			progress = 0;
		}
		if(pageCount==0) {
			return 0;
		}
		return 100 * pageIndex / pageCount;
	}

	public void setPostTask(PostTask post) {
		this.post = post;
	}

	protected abstract HttpUrl onSetParameters(HttpUrl.Builder urlBuilder, Map<String, Object> sharedParams);
	
	protected AbstractHandler<Response, ?, ?, ?, CUSTOM, ?> onCreateHandler(Map<String, Object> sharedParams){
		return handler;
	}

	@Override
	public void onResponse(Call call, Response response)  {
		handler = onCreateHandler(sharedParams);
		sharedParams = handler.handle(response);
		boolean hasNext = (boolean) sharedParams.get(PARAM_HAS_NEXT);
		if (hasNext) {
			pageIndex++;
			request();
		} else {
			pageIndex = 0;
			pageCount = 0;
			if (post != null) {
				post.onPost(sharedParams);
			}
		}
	}

	@Override
	public void onFailure(Call call, IOException e) {
		MyError error = new MyError(ERROR_CONNECTION, e.getMessage());
		sharedParams.put(PARAM_ERROR, error);
		handler = onCreateHandler(sharedParams);
		handler.onError();
		request();
	}
	
	protected Request onRequestBuilding(Request.Builder builder) {
		return builder.build();
	}
	
	abstract protected String onSetUrl(Map<String, Object> sharedParams);
	
	private Request createRequest() {
		HttpUrl httpUrl = httpUrl();
		Request.Builder requestBuilder = new Request.Builder().url(httpUrl).addHeader("charset", "utf-8");
		return onRequestBuilding(requestBuilder);
	}
	
	private HttpUrl httpUrl() {
		String url = onSetUrl(sharedParams);
		if(url == null) {
			clear();
			return null;
		}
		HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
		return onSetParameters(urlBuilder, sharedParams);
	}
	
	public String calculatedUrl() {
		HttpUrl url = httpUrl();
		return url.toString();
	}
	
	public void nextPage() {
		pageIndex++;
	}

	public void request() {
		Request request = createRequest();
		client.newCall(request).enqueue(this);
	}
}
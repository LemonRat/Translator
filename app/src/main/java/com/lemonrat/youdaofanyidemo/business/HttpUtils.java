package com.lemonrat.youdaofanyidemo.business;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpUtils {

    private static final String TAG = "HttpUtils";
    String mUrl;
    Map<String, String> mParam;
    HttpResponse mHttpResponse;

    Handler myHandler = new Handler(Looper.getMainLooper());

    private final OkHttpClient client = new OkHttpClient();

    public interface HttpResponse {
        void onSuccess(Object object);
        void onFail(String error);
    }

    public HttpUtils(HttpResponse response) {
        mHttpResponse = response;
    }

    public void sendGetHttp(String url, Map<String, String> param) {
        mUrl = url;
        mParam = param;

        //编写http请求逻辑
        run();
    }

    private void run() {
        //request请求创建
        final Request request = createRequest();
        //创建请求队列
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (mHttpResponse != null) {
                    myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mHttpResponse.onFail("请求错误");
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (mHttpResponse == null) return;

                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!response.isSuccessful()) {
                            mHttpResponse.onFail("请求失败：code"+response);
                        } else {
                            try {
                                mHttpResponse.onSuccess(response.body().string());
                            } catch (IOException e) {
                                mHttpResponse.onFail("结果转换失败");
                                Log.d(TAG, "结果转换");
                            }
                        }
                    }
                });
            }
        });
    }

    private Request createRequest() {
        String urlStr = mUrl+MapParamToString(mParam);
        Request request = new Request.Builder().url(urlStr).build();
        return request;
    }

    private String MapParamToString(Map<String, String> param) {
        StringBuilder stringBuilder = new StringBuilder();
        Iterator<Map.Entry<String, String>> iterator = param.entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            stringBuilder.append(entry.getKey()+"="+entry.getValue()+"&");
        }
        String str = stringBuilder.toString().substring(0,stringBuilder.length()-1);
        return str;
    }
}

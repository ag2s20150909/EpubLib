package me.ag2s.base;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpTool {


    public static InputStream httpCacheIS(String url) {
        OkHttpClient client = APP.getCachehttpClient();
        Request.Builder requestbuilder = new Request.Builder().get().url(url);
        requestbuilder.header("Referer", url);

        requestbuilder.header("User-Agent", UA);

        Request request = requestbuilder.build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body().byteStream();
            } else {
                return null;// "error:" + response.message() + " errorcode:" + response.code();
            }
        } catch (Exception e) {
            return null; //"error:" + e.getMessage();
        }

    }

    public static String httpCacheGet(String url, String cs) {
        OkHttpClient client = APP.getCachehttpClient();
        Request.Builder requestbuilder = new Request.Builder().get().url(url);
        requestbuilder.header("Referer", url);

        requestbuilder.header("User-Agent", UA);

        Request request = requestbuilder.build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return new String(response.body().bytes(), cs);
            } else {
                return "error:" + response.message() + " errorcode:" + response.code();
            }
        } catch (Exception e) {
            return "error:" + e.getMessage();
        }

    }

    public static String httpGet(String url) {
        OkHttpClient client = APP.getOkhttpClient();
        Request.Builder requestbuilder = new Request.Builder().get().url(url);
        requestbuilder.header("Referer", url);
        requestbuilder.header("User-Agent", UA);
        Request request = requestbuilder.build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return new String(response.body().bytes(), StandardCharsets.UTF_8);
            } else {
                return "error:" + response.message() + " errorcode:" + response.code();
            }
        } catch (Exception e) {
            return "error:" + e.getMessage();
        }
    }

    public static String httpGetPC(String url) {
        OkHttpClient client = APP.getOkhttpClient();
        Request.Builder requestbuilder = new Request.Builder().get().url(url);
        requestbuilder.header("Referer", url);
        requestbuilder.header("User-Agent", UA1);
        Request request = requestbuilder.build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return new String(response.body().bytes(), StandardCharsets.UTF_8);
            } else {
                return "error:" + response.message() + " errorcode:" + response.code();
            }
        } catch (Exception e) {
            return "error:" + e.getMessage();
        }
    }

    public static Bitmap getBitmap(String url) {
        final OkHttpClient client = APP.getCachehttpClient();
        Request.Builder requestbuilder = new Request.Builder().get().url(url);
        requestbuilder.header("Referer", url);


        requestbuilder.header("User-Agent", UA);

        final Request request = requestbuilder.build();


        try {
            // TODO: Implement this method
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                // return new String(response.body().bytes(), "utf-8");
                byte[] bs = response.body().bytes();
                return BitmapFactory.decodeByteArray(bs, 0, bs.length);
                //iv.setImageBitmap(b);
            } else {

                FileTool.writeError(response.message() + ";\nhttp:" + response.code());
                return getErrorBitmap(response.message() + "" + response.code());

                // return "error:" + response.message() + " errorcode:" + response.code();
            }
        } catch (Exception e) {
            return getErrorBitmap(e.toString());
            //return "error:" + e.getMessage();
        }


    }


    public static Bitmap getErrorBitmap(String t) {
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        Bitmap b = Bitmap.createBitmap(300, 400, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        c.drawText(t, 9.f, 9.f, paint);
        return b;
    }

    public static String httpPost(String url, HashMap map) {
        OkHttpClient.Builder buider = new OkHttpClient.Builder();
        OkHttpClient client = buider.build();
        FormBody.Builder params = new FormBody.Builder();
        Iterator iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String) entry.getKey();
            String val = (String) entry.getValue();
            params.add(key, val);
        }

        Request request = new Request.Builder()
                .url(url)
                .header("Referer", url)


                .header("User-Agent", UA)

                .post(params.build())
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return new String(response.body().bytes(), StandardCharsets.UTF_8);
            } else {
                return "error:" + response.message() + " errorcode:" + response.code();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "error:" + e.getMessage();
        }

    }


    public static final String UA1 = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36";
    public static final String UA = "Mozilla/5.0 (Linux; Android 7.1.2; vivo X9 Build/N2G47H; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/77.0.3865.66 Mobile Safari/537.36";


}

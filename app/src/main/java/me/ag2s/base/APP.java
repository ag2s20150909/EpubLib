package me.ag2s.base;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import me.ag2s.base.okhttp.HTTPDnsTool;
import me.ag2s.base.okhttp.OkHttpDns;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;


/**
 * Created by asus on 2017/12/29.
 */

public class APP extends Application {
    @SuppressLint("StaticFieldLeak")

    static okhttp3.OkHttpClient mClient;
    static okhttp3.OkHttpClient mCacheClient;
    static Context mContext;


    public static Context getContext() {
        return mContext;
    }

    public static okhttp3.OkHttpClient getOkhttpClient() {
        if (mClient == null) {
//			ClearableCookieJar cookieJar =
//				new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(APP.getContext()));
//
            mClient = new OkHttpClient.Builder()
                    .dns(OkHttpDns.getInstance(mContext))
                    .authenticator(new okhttp3.Authenticator() {

                        @Override
                        public Request authenticate(Route p1, Response p2) throws IOException {
                            // TODO: Implement this method
                            return null;
                        }
                    })
                    //.dns(new HttpDNS())
                    //.addNetworkInterceptor(new DnsVisitNetInterceptor())
                    //. connectTimeout(60, TimeUnit.SECONDS)
                    //.addInterceptor(new MMjpgInterceptor())
                    //.addInterceptor(new GzipRequestInterceptor())
//				.cookieJar(cookieJar)
                    .build();
        }
        return mClient;
    }

    public static okhttp3.OkHttpClient getCachehttpClient() {
        if (mCacheClient == null) {
//			ClearableCookieJar cookieJar =
//				new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(APP.getContext()));
            mCacheClient = new OkHttpClient.Builder()
                    //.cookieJar(cookieJar)
                    //.dns(OkHttpDns.getInstance(mContext))
                    //.dns(new HttpDNS())
                    //.addNetworkInterceptor(new DnsVisitNetInterceptor())
                    //.addInterceptor(new MMjpgInterceptor())
                    //.addInterceptor(new GzipRequestInterceptor())
                    .cache(new okhttp3.Cache(new File(mContext.getExternalCacheDir(), "okhttpcache"), 900 * 1024 * 1024))
                    .build();
        }
        return mCacheClient;
    }

    public static boolean isNightMode = true;

    @Override
    public void onCreate() {

        mContext = this;
        super.onCreate();

        CrashHandler.getInstance().init(this)
                .setOnCrashListener(new CrashHandler.OnCrashListener() {

                    @Override
                    public void onCrash(Context context, String errorMsg) {
                        // TODO: Implement this method
                        Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show();
                    }
                })
                .setCrashSave()
                .setCrashSaveTargetFolder(this.getExternalFilesDir("crash").getPath());


        File file = new File(mContext.getExternalCacheDir(), "okimgcache");
        if (!file.exists()) {
            file.mkdirs();
        }

        okhttp3.Cache cache = new okhttp3.Cache(file, 1024 * 1024 * 500);
        okhttp3.OkHttpClient client = new OkHttpClient.Builder()
                //设置代理,需要替换
                //.proxy(new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress("122.243.8.73", 9000)))
                //.addInterceptor(new KingCardInterceptor())
                .cache(cache)
                .dns(OkHttpDns.getInstance(this))
                //.dns(new HttpDNS())
                //.addNetworkInterceptor(new DnsVisitNetInterceptor())
                //.cookieJar(new CookiesManager(mContext))
                //.addInterceptor(new MMjpgInterceptor())
                //.addInterceptor(new GzipRequestInterceptor())
                .build();

        OkHttp3Downloader okHttp3Downloader = new OkHttp3Downloader(client);

        Picasso.Builder picassoBuilder = new Picasso.Builder(mContext);
        picassoBuilder.downloader(okHttp3Downloader).build();
        picassoBuilder.indicatorsEnabled(true);

        picassoBuilder.listener(new Picasso.Listener() {

            @Override
            public void onImageLoadFailed(Picasso p1, Uri p2, Exception p3) {
                // TODO: Implement this method
                FileTool.writeError(p3);
            }
        });

        Picasso picasso = picassoBuilder.build();

        Picasso.setSingletonInstance(picasso);
        //initDNS();


    }

    @Override
    protected void attachBaseContext(Context base) {
        // TODO: Implement this method
        mContext = base;
        super.attachBaseContext(base);
    }


    public void initDNS() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                ArrayList<String> hosts = new ArrayList<>();

                // TODO: Implement this method
                for (String h : hosts) {
                    HTTPDnsTool.getInstence().getQuad9Ip(h);


                }
            }


        }).start();
    }

    public static void showToast(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
    }


}

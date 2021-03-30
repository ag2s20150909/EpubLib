package me.ag2s.base.okhttp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.ag2s.base.FileTool;
import me.ag2s.base.HttpTool;

public class HTTPDnsTool {
    public ArrayList<String> mBlackList;
    public static HTTPDnsTool mInstance;
    private final ExecutorService singleThreadExecutor;

    public static HTTPDnsTool getInstence() {
        if (mInstance == null) {
            mInstance = new HTTPDnsTool();
        }
        return mInstance;
    }

    private HTTPDnsTool() {
        singleThreadExecutor = Executors.newFixedThreadPool(3);
        mBlackList = new ArrayList<>();
    }


    public static void getCloudflareIP(String name) {
        String url = "https://cloudflare-dns.com/dns-query?type=A&name=" + name;


        // TODO: Implement this method
        String json = HttpTool.httpGet(url);
        try {
            String ip = "";
            JSONArray ja = new JSONObject(json).getJSONArray("Answer");
            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);
                if (jo.getInt("type") == 1) {
                    ip += jo.getString("data") + ";";
                }
            }
            if (ip.length() > 4) {
                ip = ip.substring(0, ip.lastIndexOf(";"));
                FileTool.writeLog("httpdns.txt", "GETDNS:" + name + "IP:" + ip);

                DiskCache.getInstance().saveString(name, ip, 0);
            }
        } catch (Exception e) {

        }


    }


    public void getQuad9Ip(final String name) {
        Runnable runable = new Runnable() {

            @Override
            public void run() {
                // TODO: Implement this method
                //String url="https://dns.twnic.tw/dns-query?name=" + name;
                String url = "https://hk-dns.233py.com/dns-query?name=" + name;
                //url="https://dns.alidns.com/dns-query?name="+name;
                //url="https://dns.google/dns-query?name="+name;
                //url="https://223.5.5.5/dns-query?name="+name;
                //url="https://doh.pub/dns-query?name="+name;

                if (DiskCache.getInstance().getString(name) != null) {
                    return;
                }
                // TODO: Implement this method
                String json = HttpTool.httpGet(url);

                try {
                    String ip = "";
                    String str = "";
                    JSONArray ja = new JSONObject(json).getJSONArray("Answer");
                    for (int i = 0; i < ja.length(); i++) {
                        JSONObject jo = ja.getJSONObject(i);
                        if (jo.getInt("type") == 1 || jo.getInt("type") == 28) {
                            ip += jo.getString("data") + ";";
                            //str=jo.getString("Expires");
                        }
                    }
                    if (ip.length() > 3) {
                        ip = ip.substring(0, ip.lastIndexOf(";"));
//							Date old=new Date(Date.parse(str));
//							Date now=new Date();
//							int time=(int) (old.getTime()-now.getTime())/1000;
                        DiskCache.getInstance().saveString(name, ip, 60 * 60 * 6);
                        //FileTool.writeLog("httpdns.txt","GETDNS:"+name+"IP:"+ip+"TIME:"+time);
                    }
                } catch (Exception e) {
                    FileTool.writeError(e);
                }
            }

        };

        singleThreadExecutor.execute(runable);


    }

    public static void getTencentIP(String name) {
        //http://203.107.1.34/198121/d?host=api073nwc.zhuishushenqi.com
        String api = "http://119.29.29.29/d?dn=" + name;
        String ip = HttpTool.httpGet(api);
//		if(!(ip.isEmpty()|ip.startsWith("error:"))){
//			APP.getACache().put(name,ip,ACache.TIME_HOUR);
//		}

    }

}

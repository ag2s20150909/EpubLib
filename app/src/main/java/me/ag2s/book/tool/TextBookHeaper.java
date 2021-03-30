package me.ag2s.book.tool;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.ag2s.base.ComonTool;
import me.ag2s.base.HttpTool;

public class TextBookHeaper {

    public static void getNetBookInfo(TextBook tbook, TXTCallBack cb) {
        TextBook tb = null;
        cb.OnStart(tbook);
        cb.onRun("开始从起点获取书籍数据");
        tb = getQiDian(tbook);
        if (tb != null) {
            cb.onRun("从起点获取数据成功");
            cb.onFinish(tb);
            return;
        }
        cb.onRun("未在起点发现书籍数据");
        cb.onRun("开始从纵横获取书籍数据");
        tb = getZongHeng(tbook);
        if (tb != null) {
            cb.onRun("从纵横获取数据成功");
            cb.onFinish(tb);
            return;
        }
        cb.onRun("未在纵横发现书籍数据");
        cb.onRun("开始从晋江获取书籍数据");
        tb = getJinjiang(tbook);
        if (tb != null) {
            cb.onRun("从晋江获取数据成功");
            cb.onFinish(tb);
            return;
        }
        tbook.des = "ggggydgfvydf";

        cb.onFinish(tbook);

    }


    public static boolean guestBook(TextBook b) {
        String txtPath = b.getTxtPath();
        txtPath = txtPath.substring(txtPath.lastIndexOf("/") + 1);
        Pattern r = Pattern.compile("(.*?) by (.*?).txt");
        Pattern r2 = Pattern.compile("《(.*?)》.*?作者：(.*?).txt");
        Matcher m = r.matcher(txtPath);
        Matcher m2 = r2.matcher(txtPath);
        if (m.find()) {
            b.setBooktName(m.group(1));
            b.setAuthor(m.group(2));
            return true;
        }
        if (m2.find()) {
            b.setBooktName(m2.group(1));
            b.setAuthor(m2.group(2));
            return true;
        }

        return false;
    }


    public static TextBook getQiDian(TextBook tbook) {
        TextBook tb = tbook;
        String url = "https://m.qidian.com/search?kw=" + ComonTool.getURLEncoderString(tbook.name + " " + tbook.author);
        String html = HttpTool.httpGet(url);
        Document doc = Jsoup.parse(html, url);
        Elements els = doc.select(".book-li");
        for (Element el : els) {
            String title = el.select(".book-title").text();
            String author = el.select(".book-author").first().ownText().trim();
            if (title.equals(tbook.name) && author.equals(tbook.author)) {
                tbook.imgpath = el.select(".book-cover").first().attr("abs:data-src").replace("/150", "/");
                tbook.des = el.select(".book-desc").text();
                tb.ok = true;
                tbook.types = "起点 " + title + " " + author + " " + el.select(".tag-small").text();
                return tbook;
            }

        }
        tb.ok = false;
        tb.msg = html;
        return tb;
    }

    public static TextBook getZongHeng(TextBook tbook) {
        //String url="https://m.zongheng.com/h5/search?field=all&h5=1&keywords="+ComonTool.getURLEncoderString(tbook.name+" "+tbook.author);
        String url = "https://m.zongheng.com/h5/ajax/search?h5=1&pageNum=1&field=all&pageCount=5&keywords=" + ComonTool.getURLEncoderString(tbook.name + " " + tbook.author);
        String html = HttpTool.httpGet(url);
        try {
            JSONArray ja = new JSONObject(html).getJSONObject("searchlist").getJSONArray("searchBooks");
            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);
                String author = jo.getString("authorName");
                String name = jo.getString("bookName");
                if (name.equals(tbook.name) && author.equals(tbook.author)) {
                    tbook.imgpath = "http://static.zongheng.com/upload" + jo.getString("coverUrl");
                    tbook.des = jo.getString("description");
                    tbook.types = "纵横 " + name + " " + author;
                    return tbook;
                }
            }

        } catch (JSONException e) {
            return null;
        }
        //Document doc=Jsoup.parse(html,url);
        //Elements els=doc.select(".search-result-list");

        return null;
    }


    public static TextBook getJinjiang(TextBook tbook) {
        String url = "https://m.jjwxc.net/search?t=1&kw=" + ComonTool.getURLEncoderString(tbook.name, "gbk");
        String html = HttpTool.httpCacheGet(url, "gbk");
        Document doc = Jsoup.parse(html, url);
        //tbook.des=doc.html();
        //return tbook;
        Elements els = doc.select("ul").select("li");
        for (Element el : els) {
            String title = el.select("a").get(0).text();
            String author = el.select("a").get(1).ownText().trim();
            if (title.equals(tbook.name) && author.equals(tbook.author)) {
                String s = el.select("a").get(0).absUrl("href");
                url = "http://www.jjwxc.net/onebook.php?novelid=" + s.substring(s.lastIndexOf("/") + 1);
                html = HttpTool.httpCacheGet(url, "gbk");
                doc = Jsoup.parse(html, url);
                tbook.des = doc.select("#novelintro").text();
                tbook.imgpath = doc.select(".noveldefaultimage").first().absUrl("_src");
                tbook.types = "晋江 " + title + " " + author + " " + doc.select(".bluetext").text();
                return tbook;
            }
            tbook.des = el.toString();

        }
        //tbook.des=tb.toString();//tbody

        return null;
    }

}

package me.ag2s.book.tool;


import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.ag2s.base.APP;
import me.ag2s.base.HttpTool;
import me.ag2s.base.tools.FileTools;
import me.ag2s.epublib.domain.Author;
import me.ag2s.epublib.domain.Book;
import me.ag2s.epublib.domain.Metadata;
import me.ag2s.epublib.domain.Resource;
import me.ag2s.epublib.epub.EpubWriter;
import me.ag2s.epublib.util.ResourceUtil;

public class TextPrase {

    private static final String TAG = TextPrase.class.getName();
    public String filepath;

    public TextPrase(String path) {
        this.filepath = path;
    }

    @SuppressLint("SdCardPath")
    public void prase(TextBook tbook, TXTCallBack cb) {
        //book.txtPath=book.getTxtPath();
        //File file = new File(tbook.temppath);
        BufferedReader reader = null;
        InputStream inputStream = null;
        try {
            // Create new Book

            cb.OnStart(tbook);
            Book book = new Book();
            ContentResolver resolver = APP.getContext().getContentResolver();
            String[] openableMimeTypes = resolver.getStreamTypes(tbook.uri, "text/*");
            if (openableMimeTypes == null ||
                    openableMimeTypes.length < 1) {
                throw new FileNotFoundException();
            }

            inputStream = resolver.
                    openTypedAssetFileDescriptor(tbook.uri,
                            openableMimeTypes[0], null)
                    .createInputStream();

            //判断的文件输入流的编码
            String c = new FileCharsetDetector().guestFileEncoding(inputStream);
            Log.d(TAG, c);
            cb.onRun("文本编码:" + c);

            inputStream = resolver.
                    openTypedAssetFileDescriptor(tbook.uri,
                            openableMimeTypes[0], null)
                    .createInputStream();


            Metadata metadata = book.getMetadata();
            metadata.addDescription(tbook.des);
            metadata.setSubjects(Arrays.asList(tbook.types.split(" ")));
            // Set the title
            metadata.addTitle(tbook.getBookName());
            cb.onRun("开始下载封面");

            book.setCoverImage(new Resource(HttpTool.httpCacheIS(tbook.imgpath), "cover.jpg"));
            cb.onRun("封面下载完成");
            // Add an Author
            metadata.addAuthor(new Author(tbook.getAuthor()));
            book.getResources().add(new Resource("h1 {color: blue;}p {text-indent:2em;}".getBytes(), "css/style.css"));

            reader = new BufferedReader(new InputStreamReader(inputStream, c));


            TextChapter tc = new TextChapter("简介");
            String line = null;
            while ((line = reader.readLine()) != null) {
                line = FixTrim(line);
                if (isBlank(line)) {
                    continue;
                } else if (isTitleLine(line)) {
                    //tbook.addChapter(tc);
                    cb.onRun(tc.getTitle());
                    book.addSection(tc.getTitle(), ResourceUtil.createHTMLResource(tc.getTitle(), tc.getContent()));
                    line = fixTitle(line);
                    tc = new TextChapter(line);
                    //Log.d("debug",line);
                } else {
                    tc.addContent(line);
                }

            }
            book.addSection(tc.getTitle(), ResourceUtil.createHTMLResource(tc.getTitle(), tc.getContent()));

            cb.onRun("开始创建epub文件");
            EpubWriter epubWriter = new EpubWriter();
            cb.onRun("开始写入epub文件");


            OutputStream outputStream = FileTools.getOutStream("ag2sapp", tbook.getEpubFileName());

            //OutputStream outputStream = new FileOutputStream("");
            epubWriter.write(book, outputStream);

            cb.onRun("写入epub文件完成");
            cb.onRun("文件路径:/sdcard/ag2sapp" + tbook.getEpubFileName());
            cb.onFinish(tbook);
        } catch (Exception e) {
            //Log.e(TAG,e.getLocalizedMessage());
            e.printStackTrace();
            cb.onRun(e.getLocalizedMessage());
        }


    }


    public boolean isNotBlank(String s) {
        return !isBlank(s);
    }

    public boolean isBlank(String s) {
        return s == null || s.isEmpty() || s.trim().isEmpty();
    }

    public static String FixTrim(String s) {
        Pattern r = Pattern.compile("^[\\s]{1,9}(.*?)[\\s]{1,9}$");
        Matcher m = r.matcher(s);
        if (m.find()) {
            return m.group(1);
        }
        //移除GBK中文全角空格
        s = s.replace("　", "");
        return s;
    }

    public String fixTitle(String title) {
        Pattern r = Pattern.compile("(.*?)（.*?）.*?");
        Matcher m = r.matcher(title);
        if (m.find()) {
            return m.group(1);
        }
        String tag = "（感谢";
        if (title.contains(tag)) {
            title = title.substring(0, title.indexOf(tag));
        }
        return title;
    }

    public boolean isTitleLine(String line) {
        line = line.trim();
        return Pattern.matches("第[1234567890零一二三四五六七八九十百千]{1,10}[卷章回节] (.*?)", line);
    }

    public boolean isSubTitleLine(String line) {
        return false;
    }


}

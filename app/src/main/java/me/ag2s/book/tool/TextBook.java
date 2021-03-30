package me.ag2s.book.tool;

import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class TextBook {
    public boolean ok;
    public String msg;
    public String name = "默认名称";
    public String author = "默认作者";
    public String des = "";
    public Uri uri;
    public String imgpath = "";
    public String txtPath;
    //public String epubfff;
    public String types = "";
    public List<TextChapter> chapters;

    public TextBook(String name, String author) {
        this.name = name;
        this.author = author;
        this.chapters = new ArrayList<>();
    }

    public TextBook() {
        this.chapters = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "TextBook{" +
                "ok=" + ok +
                ", msg=" + msg +
                ", name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", des='" + des + '\'' +
                ", uri=" + uri +
                ", imgpath='" + imgpath + '\'' +
                ", txtPath='" + txtPath + '\'' +
//				", epubpath='" + epubfff + '\'' +
                ", types='" + types + '\'' +
                ", chapters=" + chapters +
                '}';
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthor() {
        return author;
    }

    public void setBooktName(String name) {
        this.name = name;
    }

    public String getBookName() {
        return name;
    }

    public void setTxtPath(String txtPath) {
        this.txtPath = txtPath;
    }

    public String getTxtPath() {
        return txtPath;
    }


    public List<TextChapter> getChapters() {
        return chapters;
    }

    public void addChapter(TextChapter tc) {
        this.chapters.add(tc);
    }

    public String getEpubFileName() {
        return "《" + this.name + "》" + "作者：" + this.author + ".epub";
    }


}

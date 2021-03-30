package me.ag2s.book.tool;

import java.util.ArrayList;
import java.util.List;

public class TextChapter {
    public String title;
    public StringBuilder content;
    public List<TextSubChapter> childs;

    public TextChapter(String title) {
        this.title = title;
        childs = new ArrayList<>();
        content = new StringBuilder();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void addContent(String line) {
        this.content.append(line).append("\n");
    }

    public String getContent() {
        return content.toString();
    }


}

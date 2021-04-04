package me.ag2s.base.file;

import android.net.Uri;

import androidx.documentfile.provider.DocumentFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CachingDocumentFile implements Comparable<CachingDocumentFile> {
    private final DocumentFile documentFile;
    private final String name;
    private final String type;
    private final boolean isDirectory;
    private final Uri uri;

    public CachingDocumentFile(DocumentFile documentFile) {
        this.documentFile = documentFile;
        this.name = documentFile.getName();
        this.type = documentFile.getType();
        this.isDirectory = documentFile.isDirectory();
        this.uri = documentFile.getUri();
    }

    public CachingDocumentFile rename(String newName) {
        this.documentFile.renameTo(newName);
        return new CachingDocumentFile(this.documentFile);
    }

    public DocumentFile getDocumentFile() {
        return documentFile;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public Uri getUri() {
        return uri;
    }

    public static List<CachingDocumentFile> toCachingList(List<DocumentFile> dfs) {
        //事前定义长度优化性能
        List<CachingDocumentFile> data = new ArrayList<>();
        for (DocumentFile df : dfs) {
            if (df == null) {

            } else if (df.isDirectory()) {
                toCachingList(Arrays.asList(df.listFiles()));
            } else {
                data.add(new CachingDocumentFile(df));
            }

        }
        Collections.sort(data);
        return data;
    }

    @Override
    public int compareTo(CachingDocumentFile o) {
        if (this.isDirectory != o.isDirectory) {
            if (this.isDirectory) {
                return -1;
            } else {
                return 1;
            }
        } else {
            return this.name.compareTo(o.name);
        }
    }
}

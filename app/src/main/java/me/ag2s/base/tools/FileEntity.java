package me.ag2s.base.tools;

public class FileEntity {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String name;
    public String path;
    public String size;
    public String id;
    public String time;
    public String filetype;

    @Override
    public String toString() {
        return "FileEntity{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", size=" + size +
                ", id='" + id + '\'' +
                ", time='" + time + '\'' +
                '}';
    }

    public void setFileType(String string) {
        this.filetype = string;
    }
}

package me.hsgamer.eosclientdownloader.data;

public class FileData {
    private final String id;
    private final String name;
    private final String type;
    private final String md5;

    public FileData(String id, String name, String type, String md5) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.md5 = md5;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getMd5() {
        return md5;
    }
}

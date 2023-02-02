package com.optum.repository;

public class FileInfo {
    private String fileName;
    private String fileUrl;

    public FileInfo(String fileName, String fileUrl) {
        this.fileName = fileName;
        this.fileUrl = fileUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }
}

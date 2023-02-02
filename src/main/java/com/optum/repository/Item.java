package com.optum.repository;

public class Item {
    private String name;
    private String path;
    private String sha;
    private String url;
    private String git_url;
    private String html_url;
    private Repository repository;

    public Item(String name, String path, String sha, String url, String git_url, String html_url, Repository repository) {
        this.name = name;
        this.path = path;
        this.sha = sha;
        this.url = url;
        this.git_url = git_url;
        this.html_url = html_url;
        this.repository = repository;
    }
    public Item(){}
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

    public String getSha() {
        return sha;
    }

    public void setSha(String sha) {
        this.sha = sha;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getGitUrl() {
        return git_url;
    }

    public void setGitUrl(String git_url) {
        this.git_url = git_url;
    }

    public String getHtmlUrl() {
        return html_url;
    }

    public void setHtmlUrl(String html_url) {
        this.html_url = html_url;
    }


    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public String getHtml_url() {
        return html_url;
    }

    public void setHtml_url(String html_url) {
        this.html_url = html_url;
    }

    public String getGit_url() {
        return git_url;
    }

}

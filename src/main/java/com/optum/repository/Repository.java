package com.optum.repository;


import java.util.ArrayList;
import java.util.List;

public class Repository {


    private static final String GITHUB_API_BASE_URL = "https://api.github.com";
    private String name;


    private String description;
    private String url;
    private boolean readmeExists;

    public  static  List<String> RepoList = new ArrayList<>(20);



    public void setRepoList(List<String> repoList) {
        RepoList = repoList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    // Constructors, getters, and setters


    public boolean isReadmeExists() {
        return readmeExists;
    }

    public void setReadmeExists(boolean readmeExists) {
        this.readmeExists = readmeExists;
    }

    //get readme url
    public String getReadmeUrl() {
        return GITHUB_API_BASE_URL + "/repos/" + name + "/contents/README.md";
    }

  public Repository(String name, boolean hasReadme){
        this.name=name;
        this.readmeExists=hasReadme;
  }
    public boolean hasReadme() {
        return readmeExists;}

public Repository(){

}
}

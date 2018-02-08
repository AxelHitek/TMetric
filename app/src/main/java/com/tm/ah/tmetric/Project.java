package com.tm.ah.tmetric;

/**
 * Created by AH on 2/6/2018.
 */

public class Project {

    public String projectName;
    public String client;
    public String projectTask;

    public Project(){

    }

    public Project(String projectName, String client, String projectTask) {
        this.projectName = projectName;
        if(client.equals(null))
            this.client = "empty";
        else
            this.client=client;
        if(projectTask.equals(null))
            this.projectTask= "empty";
        else
            this.projectTask = projectTask;
    }

}

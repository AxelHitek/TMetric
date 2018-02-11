package com.tm.ah.tmetric.holders;

import java.util.List;

/**
 * Created by AH on 2/7/2018.
 */

public class ClientHolder {

    public String clientName;
    public List<TaskHolder> tasks;

    public ClientHolder(){}

    public ClientHolder(String clientName, List<TaskHolder> tasks) {
        this.clientName = clientName;
        this.tasks = tasks;
    }
}

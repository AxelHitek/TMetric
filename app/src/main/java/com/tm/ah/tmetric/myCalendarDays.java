package com.tm.ah.tmetric;

/**
 * Created by AH on 2/6/2018.
 */

public class myCalendarDays {

    public String day;
    public String projectName;
    public String clientName;
    public String taskName;
    public int minutesWorked;

    public myCalendarDays(){

    }

    public myCalendarDays(String day, String projectName, String clientName, String taskName, int minutesWorked) {
        this.day = day;
        this.projectName = projectName;
        this.clientName = clientName;
        this.taskName = taskName;
        this.minutesWorked = minutesWorked;
    }
}

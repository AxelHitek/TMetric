# TMetric

TMetric is an app that helps you track your progress in handling projects you are currentlly working on.
It's split into 2 activities:<br /> 
  *   one which helps you add new projects to your roster and update your daily progress (DayPeojectActivity)
  *   anothe that helps you keep track of the progress so far (MainActivity.java)

### MainActivity
 The activity where you can check the work you done by selecting a date from the given calendar. If there is no work done on any project that day it will display nothing. If there is work at least on one project a dialog with all said projects will show alowing the user to select one. After selecting a project the client(s) of he project will be displayed, they can also be selected. After selecting a client you can select the task(s) that yo worked on and the amount of time worked on it.
  From this activity you can go to the second activity by presing the "Manage day" button.
  
### DayProjectActivity
  The activity where all the current projects with it's associated clients and tasks are displaye (display format: project-client-task). By cliking any of the available projects you can input the time you worked that day. If you want to add a ne (project/client/task) you click the "+" button and give the specific project information.

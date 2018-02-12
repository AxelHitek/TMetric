package com.tm.ah.tmetric;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.tm.ah.tmetric.formater.TimeFormatter;
import com.tm.ah.tmetric.holders.TaskHolder;
import com.tm.ah.tmetric.listAdapters.CalendarLstViewAdapter;
import com.tm.ah.tmetric.listAdapters.ClientLstAdapter;
import com.tm.ah.tmetric.listAdapters.TaskLstAdapter;

import java.util.ArrayList;
import java.util.Calendar;

import static java.lang.Integer.parseInt;

public class MainActivity extends AppCompatActivity {

    //define view objects
    private MaterialCalendarView materialCalendarView;
    private Button manageDayButton;
    private ListView calendarProjectListView;
    private ListView calendarClientListView;
    private ListView calendarTaskListView;
    private DatabaseReference databaseReference;
    private TimeFormatter timeFormatter;
    //TODO create bottom left button "Export data (as)"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        materialCalendarView = (MaterialCalendarView) findViewById(R.id.materialCalendarView);
        manageDayButton = (Button) findViewById(R.id.manageDayButton);
        timeFormatter = new TimeFormatter();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Days");

        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.MONDAY)
                .setMinimumDate(CalendarDay.from(2005, 1, 1))
                .setMaximumDate(CalendarDay.from(2055, 12, 31))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull final CalendarDay date, boolean selected) {

                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                final View mView = getLayoutInflater().inflate(R.layout.calendar_day_projects, null);
                mBuilder.setView(mView);

                calendarProjectListView = (ListView) mView.findViewById(R.id.calendarProjectListView);
                calendarProjectListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        AlertDialog.Builder secondBuilder = new AlertDialog.Builder(MainActivity.this);
                        View secView = getLayoutInflater().inflate(R.layout.project_item_calendar, null);
                        secondBuilder.setView(secView);

                        final String selectedDate = timeFormatter.formatSystemDayToMatchCalendarDayFormat(timeFormatter.transformMaterialCalendarDateIntoSystemCalendarDate(date));
                        final String selectedProjectNameFromDialog = ((TextView) (((ViewGroup) view).getChildAt(0))).getText().toString();

                        displayClientDialogForSelectedProject(selectedProjectNameFromDialog, selectedDate);

                    }
                });
                displayDialogForWorkedDays(date, mBuilder);
            }
        });

        manageDayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MainActivity.this, DayProjectActivity.class);
                MainActivity.this.startActivity(myIntent);
            }
        });
    }

    private void showProjectsForSelectedDate(CalendarDay calendarDay) {

        final ArrayList projectsAvailable = new ArrayList<String>();
        final CalendarLstViewAdapter adapter = new CalendarLstViewAdapter(MainActivity.this, R.layout.project_item_calendar, R.id.projectNameCalendar, projectsAvailable);
        calendarProjectListView.setAdapter(adapter);

        String selectedDay = timeFormatter.formatSystemDayToMatchCalendarDayFormat(timeFormatter.transformMaterialCalendarDateIntoSystemCalendarDate(calendarDay));
        DatabaseReference databaseReferenceProjectsPerDay = FirebaseDatabase.getInstance().getReference().child("Days").child(selectedDay);
        databaseReferenceProjectsPerDay.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //Log.d("projNAM: ",snapshot.getKey().toString());
                    projectsAvailable.add(snapshot.getKey().toString());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void displayTaskDialogForSelectedClient(String selectedDay, String selectedProject, String selectedClient) {
        final ArrayList clientsForProject = new ArrayList<String>();
        final TaskLstAdapter adapter = new TaskLstAdapter(MainActivity.this, R.layout.task_item_calendar, R.id.taskNameCalendar, clientsForProject);

        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.calendar_day_tasks, null);
        calendarTaskListView = (ListView) mView.findViewById(R.id.calendarTaskListView);
        calendarTaskListView.setAdapter(adapter);
        mBuilder.setView(mView);

        final DatabaseReference databaseReferenceProjectsPerProject = FirebaseDatabase.getInstance().getReference().child("Days")
                .child(selectedDay)
                .child(selectedProject)
                .child(selectedClient);
        databaseReferenceProjectsPerProject.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                for (DataSnapshot child : children) {
                    TaskHolder task = child.getValue(TaskHolder.class);
                    String taskName = child.getKey().toString();
                    clientsForProject.add(taskName + " -- " + task.minutesWorked + "min");
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        AlertDialog thirdDialog = mBuilder.create();
        thirdDialog.show();

    }

    private void displayClientDialogForSelectedProject(final String selectedProjectName, final String selectedDate) {

        final ArrayList clientsForProject = new ArrayList<String>();
        final ClientLstAdapter adapter = new ClientLstAdapter(MainActivity.this, R.layout.client_item_calendar, R.id.clientNameCalendar, clientsForProject);
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.calendar_day_clients, null);
        calendarClientListView = (ListView) mView.findViewById(R.id.calendarClientListView);
        calendarClientListView.setAdapter(adapter);
        mBuilder.setView(mView);


        final DatabaseReference databaseReferenceProjectsPerProject = FirebaseDatabase.getInstance().getReference().child("Days")
                .child(selectedDate)
                .child(selectedProjectName);
        databaseReferenceProjectsPerProject.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child : children) {
                    String clientName = child.getKey().toString();
                    clientsForProject.add(clientName);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        AlertDialog secDialog = mBuilder.create();
        secDialog.show();

        calendarClientListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                displayTaskDialogForSelectedClient(selectedDate, selectedProjectName, ((TextView) (((ViewGroup) view).getChildAt(0))).getText().toString());
            }
        });
    }

    private void displayDialogForWorkedDays(final CalendarDay calendarDay, final AlertDialog.Builder mBuilder) {
        final String calDayStr = timeFormatter.transformMaterialCalendarDateIntoSystemCalendarDate(calendarDay);
        //TODO get the worked dates only when starting activity not at every date change to minimize network trafic and make app run faster
        //databaseReference = FirebaseDatabase.getInstance().getReference().child("Days");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String dbDate = timeFormatter.formatDataBaseDayToMatchCalendarDayFormat(snapshot.getKey());

                    if (calDayStr.equals(dbDate)) {
                        showProjectsForSelectedDate(calendarDay);
                        final AlertDialog dialog = mBuilder.create();
                        dialog.show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


}

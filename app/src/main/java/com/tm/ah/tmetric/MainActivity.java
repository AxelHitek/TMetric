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
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //instanciate view elements
        materialCalendarView = (MaterialCalendarView) findViewById(R.id.materialCalendarView);
        manageDayButton = (Button) findViewById(R.id.manageDayButton);

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
                View mView = getLayoutInflater().inflate(R.layout.calendar_day_projects, null);
                mBuilder.setView(mView);

                calendarProjectListView= (ListView) mView.findViewById(R.id.calendarProjectListView);
                calendarProjectListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                        Toast.makeText(MainActivity.this, "An item of the ListView is clicked.", Toast.LENGTH_LONG).show();

                        AlertDialog.Builder secondBuilder = new AlertDialog.Builder(MainActivity.this);
                        View secView = getLayoutInflater().inflate(R.layout.project_item_calendar, null);
                        secondBuilder.setView(secView);

                        /*final ArrayList projectsAvailable=new ArrayList<String>();
                        final CalendarLstViewAdapter adapter=new CalendarLstViewAdapter(MainActivity.this,R.layout.project_item_calendar,R.id.projectNameCalendar,projectsAvailable);
                        calendarProjectListView.setAdapter(adapter);*/

                        //DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child()
                        String selectedDate = formatSystemDayToMatchCalendarDayFormat(transformMaterialCalendarDateIntoSystemCalendarDate(date));
                        Log.d("****Date****",selectedDate);

                        String selectedProjectNameFromDialog = ((TextView)(((ViewGroup) view).getChildAt(0))).getText().toString();
                        Log.d("***Project***", selectedProjectNameFromDialog);

                        displayClientDialogForSelectedDay(selectedProjectNameFromDialog,selectedDate);


                        //displayClientDialogForSelectedDay(selectedProjectNameFromDialog,selectedDate);

                        /*DatabaseReference databaseReferenceProjectsPerProject = FirebaseDatabase.getInstance().getReference().child("Days")
                                .child(formatSystemDayToMatchCalendarDayFormat(transformMaterialCalendarDateIntoSystemCalendarDate(date)))
                                .child(selectedProjectNameFromDialog);
                        databaseReferenceProjectsPerProject.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                                    Log.d("CLIENT NAME: ",snapshot.getKey().toString());
                                    projectsAvailable.add(snapshot.getKey().toString());
                                    adapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {}
                        });*/

                        //AlertDialog secDialog = secondBuilder.create();
                        //secDialog.show();
                    }
                });
                displayDialogForWorkedDays(date,mBuilder);
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

    //111111111111111
    private void showProjectsForSelectedDate(CalendarDay calendarDay){

        final ArrayList projectsAvailable=new ArrayList<String>();
        final CalendarLstViewAdapter adapter=new CalendarLstViewAdapter(MainActivity.this,R.layout.project_item_calendar,R.id.projectNameCalendar,projectsAvailable);
        calendarProjectListView.setAdapter(adapter);

        String selectedDay =formatSystemDayToMatchCalendarDayFormat(transformMaterialCalendarDateIntoSystemCalendarDate(calendarDay));
        DatabaseReference databaseReferenceProjectsPerDay = FirebaseDatabase.getInstance().getReference().child("Days").child(selectedDay);
        databaseReferenceProjectsPerDay.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        Log.d("projNAM: ",snapshot.getKey().toString());
                        projectsAvailable.add(snapshot.getKey().toString());
                        adapter.notifyDataSetChanged();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
    }

    //22222222222222
    private void displayClientDialogForSelectedDay(String selectedProjectName, String selectedDate){

        final ArrayList clientsForProject = new ArrayList<String>();
        final ClientLstAdapter adapter = new ClientLstAdapter(MainActivity.this, R.layout.client_item_calendar,R.id.clientNameCalendar,clientsForProject);
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.calendar_day_clients, null);
        calendarClientListView = (ListView) mView.findViewById(R.id.calendarClientListView);
        calendarClientListView.setAdapter(adapter);
        mBuilder.setView(mView);

        Log.d("This should be DATE", selectedDate);
        Log.d("This should be PROJECT", selectedProjectName);

        final DatabaseReference databaseReferenceProjectsPerProject = FirebaseDatabase.getInstance().getReference().child("Days")
                .child(selectedDate)
                .child(selectedProjectName);
        databaseReferenceProjectsPerProject.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                calendarProjectListView.setAdapter(adapter);
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                Log.d("# of clients", String.valueOf(dataSnapshot.getChildrenCount()));

                for (DataSnapshot child: children) {
                    Log.d("CLIENT NAME: ",child.getKey().toString());
                    String clientName = child.getKey().toString();
                    clientsForProject.add(clientName);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        AlertDialog secDialog = mBuilder.create();
        secDialog.show();
    }

    //333333333333333
    private void displayDialogForWorkedDays(final CalendarDay calendarDay, final AlertDialog.Builder mBuilder){

        final String calDayStr= transformMaterialCalendarDateIntoSystemCalendarDate(calendarDay);

        //TODO get the worked dates only when starting activity not at every date change to minimize network trafic and make app run faster
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Days");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    String dbDate = formatDataBaseDayToMatchCalendarDayFormat(snapshot.getKey());

                    if(calDayStr.equals(dbDate)) {
                        showProjectsForSelectedDate(calendarDay);
                        final AlertDialog dialog= mBuilder.create();
                        dialog.show();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private String calculateTotalHoursWorkedOnProject(){
        return "5 hours";
    }

    private String transformMaterialCalendarDateIntoSystemCalendarDate(CalendarDay calendarDay){
        return calendarDay.toString().replace("CalendarDay{","").replace("}","");
    }

    private String formatDataBaseDayToMatchCalendarDayFormat(String dataBaseDate){
        String[] date = dataBaseDate.split("-");
        String formatedDate = date[0]+"-"+(parseInt(date[1])-1)+"-"+parseInt(date[2]);
        return formatedDate;
    }

    private String formatSystemDayToMatchCalendarDayFormat(String dataBaseDate){
        String[] date = dataBaseDate.split("-");
        String formatedDate = date[0]+"-";
        if((parseInt(date[1])+1)<10){
            formatedDate+="0"+(parseInt(date[1])+1);
        }
        else{
            formatedDate+=""+(parseInt(date[1])+1);
        }
        formatedDate+="-";
        if((parseInt(date[2]))<10){
            formatedDate+="0"+(parseInt(date[2]));
        }
        else{
            formatedDate+=""+(parseInt(date[2]));
        }
        return formatedDate;
    }
}

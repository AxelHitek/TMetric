package com.tm.ah.tmetric;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tm.ah.tmetric.holders.Project;
import com.tm.ah.tmetric.listAdapters.LstViewAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static java.lang.Integer.parseInt;

public class DayProjectActivity extends AppCompatActivity {

    private ListView projectListView;
    private TextView dateTextView;
    private DatabaseReference databaseReference;
    private DatabaseReference daysDBReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_project);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        showCurrentDate();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Project");
        daysDBReference = FirebaseDatabase.getInstance().getReference().child("Days");

        projectListView = (ListView) findViewById(R.id.projectListView);
        projectListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final AlertDialog.Builder timePickerBuilder = new AlertDialog.Builder(DayProjectActivity.this);
                View timePickerView = getLayoutInflater().inflate(R.layout.dialog_time_pickers, null);

                Button buttonStartHour = (Button) timePickerView.findViewById(R.id.buttonStartHour);
                Button buttonFinishHour = (Button) timePickerView.findViewById(R.id.buttonFinishHour);
                Button buttonCalculateTimeWorked = (Button) timePickerView.findViewById(R.id.buttonCalculateTimeWorked);
                Button buttonSubmitHours = (Button) timePickerView.findViewById(R.id.buttonSubmitWorkedHours);
                final TextView textViewstartHour = (TextView) timePickerView.findViewById(R.id.textViewStartHour);
                final TextView textViewfinishHour = (TextView) timePickerView.findViewById(R.id.textViewFinishHour);
                final TextView textViewTimeWorkedHour = (TextView) timePickerView.findViewById(R.id.textViewTimeWorked);

                timePickerBuilder.setView(timePickerView);
                final AlertDialog dialog = timePickerBuilder.create();

                buttonStartHour.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int mStartHour, mStartMinute;
                        // Get Current Time
                        final Calendar c1 = Calendar.getInstance();
                        mStartHour = c1.get(Calendar.HOUR_OF_DAY);
                        mStartMinute = c1.get(Calendar.MINUTE);

                        //to avoid attempts to "trick the system" by inputting a valid set of hours and then changing it and hitting submit
                        if (!textViewfinishHour.getText().toString().equals(getString(R.string.button_pick_start_hour))) {
                            textViewTimeWorkedHour.setText(R.string.main_activity_time_worked_dialog_title);
                        }

                        // Launch Time Picker Dialog
                        TimePickerDialog startTimePickerDialog = new TimePickerDialog(view.getContext(),
                                new TimePickerDialog.OnTimeSetListener() {

                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay,
                                                          int minute) {
                                        textViewstartHour.setText(hourOfDay + ":" + minute);
                                    }
                                }, mStartHour, mStartMinute, true);
                        startTimePickerDialog.show();
                    }
                });

                buttonFinishHour.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        int mFinishHour, mFinishMinute;
                        // Get Current Time
                        final Calendar c2 = Calendar.getInstance();
                        mFinishHour = c2.get(Calendar.HOUR_OF_DAY);
                        mFinishMinute = c2.get(Calendar.MINUTE);

                        //to avoid attempts to "trick the system" by inputting a valid set of hours and then changing it and hitting submit
                        if (!textViewstartHour.getText().toString().equals(getString(R.string.button_pick_start_hour))) {
                            textViewTimeWorkedHour.setText(R.string.dialog_text_view_total_time_worked);
                        }

                        TimePickerDialog finishTimePickerDialog = new TimePickerDialog(view.getContext(),
                                new TimePickerDialog.OnTimeSetListener() {

                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay,
                                                          int minute) {

                                        textViewfinishHour.setText(hourOfDay + ":" + minute);
                                    }
                                }, mFinishHour, mFinishMinute, true);
                        finishTimePickerDialog.show();
                    }
                });

                dialog.show();

                buttonCalculateTimeWorked.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!textViewstartHour.getText().equals(getString(R.string.button_pick_start_hour)) && !textViewfinishHour.getText().equals(getString(R.string.button_pick_finish_hour))) {
                            textViewTimeWorkedHour.setText(calculateTimeWorked(textViewstartHour.getText().toString(), textViewfinishHour.getText().toString()));
                        }
                    }
                });

                //get the text of the project
                LinearLayout ll = (LinearLayout) view;
                View v = ll.getChildAt(0); // because we want the textView from the LinearLayout
                v.toString();
                final TextView e = (TextView) v;

                buttonSubmitHours.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!textViewTimeWorkedHour.getText().toString().trim().equals("Total time worked"))
                            setHoursWorkedToday(e.getText().toString().trim(), textViewTimeWorkedHour.getText().toString());
                        else
                            Toast.makeText(DayProjectActivity.this, R.string.warning_click_both_buttons_before_submit, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        final ArrayList projectsAvailable = new ArrayList<String>();
        final LstViewAdapter adapter = new LstViewAdapter(this, R.layout.project_item, R.id.projectName, projectsAvailable);
        projectListView.setAdapter(adapter);

        displayProjectsOnActivityLaunch(adapter, projectsAvailable);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(DayProjectActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.add_project_layout, null);

                ImageButton createProject = (ImageButton) mView.findViewById(R.id.imageButtonAddProject);
                ImageButton cancelProjectCreation = (ImageButton) mView.findViewById(R.id.imageButtonCancelProjecCreation);
                final EditText projectName = (EditText) mView.findViewById(R.id.editTextProjectName);
                final EditText clientName = (EditText) mView.findViewById(R.id.editTextClient);
                final EditText taskName = (EditText) mView.findViewById(R.id.editTextTask);

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                createProject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        createProject(projectName.getText().toString().trim(), clientName.getText().toString().trim(), taskName.getText().toString().trim(), projectsAvailable, adapter, dialog);
                    }
                });

                cancelProjectCreation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        });
    }


    private void showCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

        String currentDate = sdf.format(new Date());
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);
        dateTextView = (TextView) findViewById(R.id.dateTextView);
        dateTextView.setText(dateTextView.getText() + " " + dayFormat.format(Calendar.getInstance().getTime()) + " " + currentDate);
    }

    private void createProject(String projectNameET, String clientNameET, String taskNameET, ArrayList projectsAvailable, LstViewAdapter adapter, AlertDialog dialog) {
        if (!projectNameET.isEmpty() && !clientNameET.isEmpty() && !taskNameET.isEmpty()) {


            String projectName = projectNameET;
            String clientName = clientNameET;
            String taskName = taskNameET;

            insertProjectIntoDB(projectName, clientName, taskName);
            Toast.makeText(this, R.string.message_info_saved_succesfully, Toast.LENGTH_LONG).show();
            displayNewProject(adapter, projectsAvailable, projectName, clientName, taskName);
            dialog.dismiss();
        } else {
            Toast.makeText(dialog.getContext(), R.string.warning_fill_all_fields_before_add_project, Toast.LENGTH_LONG).show();
        }
    }

    private void displayProjectsOnActivityLaunch(final LstViewAdapter adapter, final ArrayList projectsAvailable) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Project");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child : children) {
                    Project project = child.getValue(Project.class);
                    projectsAvailable.add(project.projectName + "-" + project.client + "-" + project.projectTask);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void displayNewProject(final LstViewAdapter adapter, final ArrayList projectsAvailable, String projectName, String clientName, String taskName) {
        projectsAvailable.add(projectName + "-" + clientName + "-" + taskName);
        adapter.notifyDataSetChanged();
    }

    private void insertProjectIntoDB(String projectName, String clientName, String taskName) {
        Project project = new Project(projectName, clientName, taskName);
        databaseReference.child(projectName + "-" + clientName + "-" + taskName).setValue(project);
    }

    private void setHoursWorkedToday(String projectWithClientAndTask, String houresWorked) {

        String[] workedTime = houresWorked.split(":");
        int effectiveMinutesWorked = parseInt(workedTime[0]) * 60 + parseInt(workedTime[1]);

        String pname = projectWithClientAndTask.split("-")[0];
        String cname = projectWithClientAndTask.split("-")[1];
        String tname = projectWithClientAndTask.split("-")[2];


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = simpleDateFormat.format(new Date());

        daysDBReference.child(currentDate).child(pname).child(cname).child(tname).child("minutesWorked").setValue(effectiveMinutesWorked);
        Toast.makeText(this, R.string.message_progress_submitted_for_project, Toast.LENGTH_LONG).show();
    }

    private String calculateTimeWorked(String textViewstartHour, String textViewfinishHour) {

        //TODO extract check to different method
        //TODO find way to anounce users of problem
        Date d1 = null;
        Date d2 = null;
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        try {
            d1 = format.parse(textViewstartHour);
            d2 = format.parse(textViewfinishHour);
        } catch (ParseException e) {
            e.printStackTrace();
            return "Total time worked";
        }

        if (d1.before(d2)) {
            long diff = d2.getTime() - d1.getTime();

            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;

            Log.e("DIFFERENCE", Math.abs(diffHours) + ":" + Math.abs(diffMinutes));
            return new String(Math.abs(diffHours) + ":" + Math.abs(diffMinutes));

        }

        return "Total time worked";

    }

}

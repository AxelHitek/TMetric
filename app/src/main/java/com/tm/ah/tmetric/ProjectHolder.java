package com.tm.ah.tmetric;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * Created by AH on 2/7/2018.
 */

public class ProjectHolder {

    public String projectName;
    public List<String> clients;

    public ProjectHolder(){}

    public ProjectHolder(String projectName, List<String> waasf) {
        this.projectName = projectName;

        DatabaseReference datababaseReference = FirebaseDatabase.getInstance().getReference().child("PROJ").child("project_"+projectName);
        datababaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child: children) {
                    clients.add(child.getKey().toString());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        this.clients = clients;
    }
}

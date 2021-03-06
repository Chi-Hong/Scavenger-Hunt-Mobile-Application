package com.example.login.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.login.R;
import com.example.login.main.Student;
import com.example.login.main.ViewSubmission;
import com.example.login.adaptor.StudentSubmissionAdaptor;
import com.example.login.support.SubmissionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StudentSubmission extends Fragment implements AdapterView.OnItemClickListener {
    private ListView listView;
    private ArrayList<SubmissionManager> studentSubmissionList;
    private StudentSubmissionAdaptor studentSubmissionAdaptor;

    private FirebaseUser mUser;
    private DatabaseReference rootRef, studentRef, studentRoomRef, studentSubmissionRef;
    private Query studentSubmissionQuery;

    private String roomSelected;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_view, container, false);

        roomSelected = ((Student) getActivity()).getRoomSelected();

        if (roomSelected == null) {
            return inflater.inflate(R.layout.fragment_empty_task, container, false);
        }

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        rootRef = FirebaseDatabase.getInstance().getReference();
        studentRef = rootRef.child("Student").child(mUser.getUid());
        studentRoomRef = studentRef.child("Rooms");
        studentSubmissionRef = studentRoomRef.child(roomSelected).child("Submissions");
        studentSubmissionQuery = studentSubmissionRef.orderByChild("description");

        listView = view.findViewById(R.id.listView);
        studentSubmissionList = new ArrayList<>();
        studentSubmissionAdaptor = new StudentSubmissionAdaptor(getActivity(), studentSubmissionList);

        listView.setAdapter(studentSubmissionAdaptor);
        studentSubmissionQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                studentSubmissionList.clear();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    SubmissionManager submissionManager = dataSnapshot1.getValue(SubmissionManager.class);

                    studentSubmissionList.add(submissionManager);
                    studentSubmissionAdaptor.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(this.toString(), databaseError.getMessage());
            }
        });

        listView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
        SubmissionManager submissionManager = studentSubmissionList.get(i);

        Intent intent = new Intent(getActivity(), ViewSubmission.class);
        intent.putExtra("taskContent", submissionManager.getContent());

        startActivity(intent);
    }
}

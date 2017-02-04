package ng.com.techdepo.firebaserw;

import android.gesture.Prediction;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;



public class AddStaff extends AppCompatActivity {

    private static final String TAG = "AddStaff";

    private static final String REQUIRED = "Required";

    private DatabaseReference mDatabase;

    private EditText mStaffName;
    private EditText mStaffAge;
    private EditText mStaffLevel;
    private Button mAddStaff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_staff);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END initialize_database_ref]
        mStaffName = (EditText) findViewById(R.id.staff_name_edit);
        mStaffAge = (EditText) findViewById(R.id.staff_age_edit);
        mStaffLevel = (EditText) findViewById(R.id.staff_level_edit);
        mAddStaff = (Button) findViewById(R.id.add_staff_button);

        mAddStaff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPost();
            }
        });
    }

    private void setEditingEnabled(boolean enabled) {
        mStaffName.setEnabled(enabled);
        mStaffAge.setEnabled(enabled);
        mStaffLevel.setEnabled(enabled);

        if (enabled) {
            mAddStaff.setVisibility(View.VISIBLE);
        } else {
            mAddStaff.setVisibility(View.GONE);
        }
    }

    private void submitPost() {
        final String staff_Name = mStaffName.getText().toString();
        final String staff_Age = mStaffAge.getText().toString();
        final String staff_level = mStaffLevel.getText().toString();


        if (TextUtils.isEmpty(staff_Name)) {
            mStaffName.setError(REQUIRED);
            return;
        }


        if (TextUtils.isEmpty(staff_Age)) {
            mStaffAge.setError(REQUIRED);
            return;
        }

        if (TextUtils.isEmpty(staff_level)) {
            mStaffLevel.setError(REQUIRED);
            return;
        }

        // Disable button so there are no multi-posts
        setEditingEnabled(false);
        Toast.makeText(this, "Posting...", Toast.LENGTH_SHORT).show();

        // [START single_value_read]

        mDatabase.child("staff").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        addNewStaff(staff_Name, staff_Age, staff_level);


                        // Finish this Activity, back to the stream
                        setEditingEnabled(true);
                        finish();
                        // [END_EXCLUDE]
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                        // [START_EXCLUDE]
                        setEditingEnabled(true);
                        // [END_EXCLUDE]
                    }
                });
    }

    // [START write_fan_out]
    private void addNewStaff(String name, String age,String level ) {


        // String key = mDatabase.push().getKey(); // this will create a new unique key
        String key = mDatabase.child("staff").push().getKey();
        Staff_Model staff = new Staff_Model(name,age,level);
        Map<String, Object> postValues = staff.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/staff/" + "/"  +key, postValues);

        mDatabase.updateChildren(childUpdates);
    }



}

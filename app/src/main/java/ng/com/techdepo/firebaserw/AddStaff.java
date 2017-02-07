package ng.com.techdepo.firebaserw;


import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;


import java.util.HashMap;
import java.util.Map;



public class AddStaff extends AppCompatActivity implements View.OnClickListener{

    // Firebase instance variables
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private static final String TAG = "AddStaff";

    private static final String REQUIRED = "Required";

    private DatabaseReference mDatabase;

    private EditText mStaffName;
    private EditText mStaffAge;
    private EditText mStaffLevel;
    private Button mAddStaff;
    private Button mFetchNewConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_staff);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize Firebase Remote Config.
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        // Define Firebase Remote Config Settings.
        FirebaseRemoteConfigSettings firebaseRemoteConfigSettings =
                new FirebaseRemoteConfigSettings.Builder()
                        .setDeveloperModeEnabled(true)
                        .build();
        // Define default config values. Defaults are used when fetched config values are not
// available. Eg: if an error occurred fetching values from the server.
        Map<String, Object> defaultConfigMap = new HashMap<>();
        defaultConfigMap.put("staff_name_limit", 10L);
        mFetchNewConfig = (Button) findViewById(R.id.fetch_new_config);
        mFetchNewConfig.setOnClickListener(this);
// Apply config settings and default values.
        mFirebaseRemoteConfig.setConfigSettings(firebaseRemoteConfigSettings);
        mFirebaseRemoteConfig.setDefaults(defaultConfigMap);
        // Fetch remote config.
        fetchConfig();
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

    // Fetch the config to determine the allowed length of messages.
    public void fetchConfig() {
        long cacheExpiration = 3600; // 1 hour in seconds
        // If developer mode is enabled reduce cacheExpiration to 0 so that
        // each fetch goes to the server. This should not be used in release
        // builds.
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings()
                .isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }
        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Make the fetched config available via
                        // FirebaseRemoteConfig get<type> calls.
                        mFirebaseRemoteConfig.activateFetched();
                        applyRetrievedLengthLimit();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // There has been an error fetching the config
                        Log.w(TAG, "Error fetching config: " +
                                e.getMessage());
                        applyRetrievedLengthLimit();
                    }
                });
    }


    /**
     * Apply retrieved length limit to edit text field.
     * This result may be fresh from the server or it may be from cached
     * values.
     */
    private void applyRetrievedLengthLimit() {
        Long staff_name_limit =
                mFirebaseRemoteConfig.getLong("staff_name_limit");
        mStaffName.setFilters(new InputFilter[]{new
                InputFilter.LengthFilter(staff_name_limit.intValue())});
        Log.d(TAG, "SNL is: " + staff_name_limit);
    }

    @Override
    public void onClick(View view) {
        if (view == mFetchNewConfig)
        {fetchConfig();
        }
        // Do something
    }



}

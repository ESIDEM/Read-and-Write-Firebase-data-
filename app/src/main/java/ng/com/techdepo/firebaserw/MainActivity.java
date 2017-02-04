package ng.com.techdepo.firebaserw;

import android.content.Intent;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;



import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;


import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;




public  class MainActivity extends AppCompatActivity implements View.OnClickListener{

    // Firebase instance variables
    private FirebaseUser mFirebaseUser;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mFirebaseDatabaseReference;
    private RecyclerView staffRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private FirebaseRecyclerAdapter<Staff_Model, MessageViewHolder>mFirebaseAdapter;
    public static final String MESSAGES_CHILD = "staff";
    private static final String TAG = "MainActivity";
    private TextView textViewUserEmail;
    private Button buttonLogout;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,AddStaff.class);
                startActivity(intent);
            }
        });
        mFirebaseAuth = FirebaseAuth.getInstance();

        //if the user is not logged in
        //that means current user will return null
        if(mFirebaseAuth.getCurrentUser() == null){

            //starting login activity
            startActivity(new Intent(this, SignInActivity.class));
            //closing this activity
            finish();
            return;
        }else {

            mFirebaseUser = mFirebaseAuth.getCurrentUser();
            textViewUserEmail = (TextView) findViewById(R.id.userEmailText);
            buttonLogout = (Button) findViewById(R.id.logOutButton);

            //displaying logged in user name
            textViewUserEmail.setText(mFirebaseUser.getEmail());
        }
        //adding listener to button
        buttonLogout.setOnClickListener(this);

        staffRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Staff_Model, MessageViewHolder>(
                Staff_Model.class,
                R.layout.staff_item,
                MessageViewHolder.class,
                mFirebaseDatabaseReference.child(MESSAGES_CHILD)) {

            @Override
            protected void populateViewHolder(MessageViewHolder viewHolder, Staff_Model staff_model, int position) {

                viewHolder.nameTextView.setText(staff_model.getStaffName());
                viewHolder.ageTextView.setText(staff_model.getStaffAge());
                viewHolder.levelTextView.setText(staff_model.getStaffLevel());

            }
        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the user is at the bottom of the list, scroll
                // to the bottom of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) && lastVisiblePosition == (positionStart - 1))) {
                    staffRecyclerView.scrollToPosition(positionStart);
                }
            }
        });

        staffRecyclerView.setLayoutManager(mLinearLayoutManager);
        staffRecyclerView.setAdapter(mFirebaseAdapter);


        }

    @Override
    public void onClick(View view) {
        //if logout is pressed
        if(view == buttonLogout){
            //logging out the user
            mFirebaseAuth.signOut();
            //closing activity
            finish();
            //starting login activity
            startActivity(new Intent(this, SignInActivity.class));
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



}


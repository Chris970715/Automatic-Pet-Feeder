/*
File: Source Code of Android Studio
Course: TPJ655 NCD
Name: Sunghyun Park, Gyubum Kim
Student #: 109226209, 101492171
Date: 2023 - 04 - 07
*/

package com.example.petfeedtwo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference mDatabaseRef;
    private Button mFeedButton;
    private Button mStopFeedButton;
    private TextView mCurrentTime;

    FirebaseAuth auth;
    Button button;
    FirebaseUser user;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get a reference to the Firebase Realtime Database
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        // Find the "Feed Now" button in the layout
        mFeedButton = findViewById(R.id.button_feed);
        // Find the "Stop Feeding" button in the layout
        mStopFeedButton = findViewById(R.id.button_stop_feed);
        // Find the TextView to display the current time
        mCurrentTime = findViewById(R.id.current_time_text);

        auth = FirebaseAuth.getInstance();
        button = findViewById(R.id.logout);
        user = auth.getCurrentUser();
        textView = findViewById(R.id.user_details);

        if (user == null){
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }
        else {
            textView.setText(user.getEmail());
        }

        // Get a reference to the current user's data in Firebase Realtime Database
        DatabaseReference currentUserRef = mDatabaseRef.child("users").child(user.getUid());
        // Add a ValueEventListener to retrieve the user data
        currentUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Retrieve the user's data as a User object
                User currentUser = dataSnapshot.getValue(User.class);
                if (currentUser != null) {
                    // Display the user's email and password in the UI
                    String userPassword = currentUser.getPassword();
                    String userEmail = currentUser.getEmail();
                    textView.setText(userEmail);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });


        // Set an OnClickListener for a button view
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Sign the user out of Firebase Authentication
                FirebaseAuth.getInstance().signOut();
                // Create an Intent to start the login activity
                Intent intent = new Intent(getApplicationContext(), Login.class);
                // Start the login activity
                startActivity(intent);
                // Finish the current activity
                finish();
            }
        });


        // Set an onClickListener on the "Feed Now" button
        mFeedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Change the value of the "LED_STATUS" variable to false
                mDatabaseRef.child("test/FEED_STATUS").setValue(true);
            }
        });

        // Set an onClickListener on the "Stop Feeding" button
        mStopFeedButton.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabaseRef.child("test/FEED_STATUS").setValue(false);
            }
        }));
        // Set the time zone to the time zone of your country
        TimeZone timeZone = TimeZone.getTimeZone("America/Toronto"); // replace with your country's time zone ID

// Create a SimpleDateFormat object for the date (year and month/day)
        final SimpleDateFormat dateFormatDate = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
        dateFormatDate.setTimeZone(timeZone);

// Create a SimpleDateFormat object for the time (hour and minute)
        final SimpleDateFormat dateFormatTime = new SimpleDateFormat("h:mm:ss a", Locale.getDefault());
        dateFormatTime.setTimeZone(timeZone);

// Find the TextView widgets to display the date and time
        TextView currentDate = findViewById(R.id.current_date_text);
        TextView currentTime = findViewById(R.id.current_time_text);

// Update the current date TextView every second
        final Calendar calendar = Calendar.getInstance(timeZone);
        final Runnable updateDateRunnable = new Runnable() {
            @Override
            public void run() {
                calendar.setTimeInMillis(System.currentTimeMillis());
                currentDate.setText(dateFormatDate.format(calendar.getTime()));
                currentDate.postDelayed(this, 1000);
            }
        };
        updateDateRunnable.run();

// Update the current time TextView every second
        final Runnable updateTimeRunnable = new Runnable() {
            @Override
            public void run() {
                calendar.setTimeInMillis(System.currentTimeMillis());
                currentTime.setText(dateFormatTime.format(calendar.getTime()));
                currentTime.postDelayed(this, 1000);
            }
        };
        updateTimeRunnable.run();
    }
}
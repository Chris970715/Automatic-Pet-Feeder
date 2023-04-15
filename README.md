#This code contains two parts: an Arduino sketch (C++) and an Android app (Java) that work together to create a pet feeder controlled through the internet using Firebase Realtime Database.

![Untitled video - Made with Clipchamp (7)](https://user-images.githubusercontent.com/39882035/232186975-6f295037-e422-475b-a933-a941638bf567.gif)


The Arduino sketch controls a servo motor to feed the pet, while the Android app allows the user to log in and control the feeder remotely.
-----------------------------------------------------------------------------------------------------------------------------------------------------------------------
Arduino sketch (C++):
-----------------------------------------------------------------------------------------------------------------------------------------------------------------------

![Untitled video - Made with Clipchamp (8)](https://user-images.githubusercontent.com/39882035/232186957-62c1ad88-8552-4842-94a8-26bd89248191.gif)


1. Import necessary libraries
```
#include <Servo.h>
#include <Arduino.h>
#include <FirebaseESP32.h>
#include <FirebaseESP8266.h>
#include <Firebase_ESP_Client.h>
#include <addons/TokenHelper.h>
#include <addons/RTDBHelper.h>
```
2. Set Wi-Fi, Firebase API Key, and user credentials
```
#define WIFI_SSID "BELL879"
#define WIFI_PASSWORD "AFD4519DCFFA"
#define API_KEY "AIzaSyA4w_wr6fsM14Rcc6rwDL4hRTxn_R1NOzc"
#define DATABASE_URL "nodemcu8266-9edee-default-rtdb.firebaseio.com"
#define USER_EMAIL "User_email"
#define USER_PASSWORD "User_password"
```

3. Define Firebase Data object, FirebaseAuth, and FirebaseConfig objects
```
FirebaseData fbdo;
FirebaseAuth auth;
FirebaseConfig config;
```
4.Initialize the Servo motor on pin 4
```
byte servoPin = 4;
Servo s1;
```
5.Set up the Wi-Fi connection in the setup() function
```
WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
```
6. Initialize the Firebase library and set up the automatic Wi-Fi reconnection
```
Firebase.begin(&config, &auth);
Firebase.reconnectWiFi(true);
```
7. Set the Firebase token status callback function
```
config.token_status_callback = tokenStatusCallback;
```
8. Control servo motor based on the value of "FEED_STATUS" in the loop() function
```
if (Firebase.ready() && (millis() - sendDataPrevMillis > 5000 || sendDataPrevMillis == 0))
{
  sendDataPrevMillis = millis();
  if (Firebase.getBool(fbdo, "/test/FEED_STATUS"))
  {
    bool ledStatus = fbdo.boolData();
    if (ledStatus)
    {
      Serial.println("Servo Motor ON");
      s1.write(0);
      Firebase.setBool(fbdo, "/test/FEED_STATUS", false);
    }
    else
    {
      Serial.println("Servo Motor OFF");
      s1.write(90);
    }
  }
}

```

![Untitled video - Made with Clipchamp](https://user-images.githubusercontent.com/39882035/232187100-a2f3be9b-aed5-40ce-882a-83a67d046747.gif)


Android Application ( Java )
-----------------------------------------------------------------------------------------------------------------------------------------------------------------------
Login.java
-----------------------------------------------------------------------------------------------------------------------------------------------------------------------

![u](https://user-images.githubusercontent.com/39882035/232186914-6bdfff7c-05dc-4a07-aed1-059910882331.png)


This code is an Android application activity called Login, which is responsible for handling user login for an app named "PetFeedTwo". The activity uses Firebase Authentication to authenticate the users. Here's a detailed explanation of the code:

1. Import necessary libraries and classes for the Android application activity and Firebase Authentication.
```
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
```
2. Define the Login activity class, which extends AppCompatActivity.
```
public class Login extends AppCompatActivity {
```
3. Declare UI elements and FirebaseAuth instance variables.
```
TextInputEditText editTextEmail, editTextPassword;
Button buttonLogin;
FirebaseAuth mAuth;
ProgressBar progressBar;
TextView textView;
```
4. Override the onStart() method to check for an existing user session. If a user is already logged in, navigate to the MainActivity and finish the current activity.
```
@Override
public void onStart() {
    super.onStart();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    if(currentUser != null){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }
}
```
5. Override the onCreate() method to initialize UI elements, set up event listeners, and handle user login.
```
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
```
6. Initialize UI elements and FirebaseAuth instance.
```
editTextEmail = findViewById(R.id.email);
editTextPassword = findViewById(R.id.password);
buttonLogin = findViewById(R.id.btn_login);
mAuth = FirebaseAuth.getInstance();
progressBar = findViewById(R.id.progressBar);
textView= findViewById(R.id.registerNow);
```
7. Set an OnClickListener for the textView to navigate to the Register activity.
```
textView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(getApplicationContext(), Register.class);
        startActivity(intent);
        finish();
    }
});
```
8. Set an OnClickListener for the buttonLogin to handle user login with email and password.
```
buttonLogin.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        progressBar.setVisibility(View.VISIBLE);
        String email, password;
        email = String.valueOf(editTextEmail.getText());
        password = String.valueOf(editTextPassword.getText());

        if (TextUtils.isEmpty(email)){
            Toast.makeText(Login.this, "Enter your email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)){
            Toast.makeText(Login.this, "Enter your password", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
});
```

---------------------------------------------------------------------------------------------------------------------------
MainActivity.java
---------------------------------------------------------------------------------------------------------------------------
This code below uses the Firebase Realtime Database and Firebase Authentication to authenticate and retrieve data of the currently logged-in user. It also has functionality to control a feed dispenser by changing the value of a variable in the Firebase Realtime Database.
---------------------------------------------------------------------------------------------------------------------------
  When the value is True
  ![Untitled video - Made with Clipchamp (2)](https://user-images.githubusercontent.com/39882035/232187257-5019c561-8aa0-419b-819b-e44c0e9e5313.gif)
  
  When the value is false
  
  ![Untitled video - Made with Clipchamp (3)](https://user-images.githubusercontent.com/39882035/232187323-9ee1bfb4-8c81-43cb-9da5-1a2455e12437.gif)


  
  1. The imports:
```
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

```
This section imports necessary classes and libraries used in the code, such as the Android activities, Firebase authentication and database, and date/time formatting libraries.

  2. Class definition
```
public class MainActivity extends AppCompatActivity
```
This is the definition of the MainActivity class that extends the AppCompatActivity class in Android.

  3.Class variables declaration
```
private DatabaseReference mDatabaseRef;
private Button mFeedButton;
private Button mStopFeedButton;
private TextView mCurrentTime;

FirebaseAuth auth;
Button button;
FirebaseUser user;
TextView textView;
```
These are the class variables declared for later use in the code. They include DatabaseReference object to reference the Firebase Realtime Database, Buttons to trigger actions, TextViews to display data, and FirebaseAuth and FirebaseUser objects to manage authentication.

  4.onCreate() method
```
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
```
This is the method that runs when the activity is created. It sets the layout of the activity using the setContentView() method, which inflates the activity layout from the XML file activity_main.xml.

  5. Firebase Authentication
```
auth = FirebaseAuth.getInstance();
button = findViewById(R.id.logout);
user = auth.getCurrentUser();
textView = findViewById(R.id.user_details);
```
This section initializes Firebase Authentication and gets the current user. It also finds and sets the textView object to display the user's email in the UI.

  6. Firebase Realtime Database
```
mDatabaseRef = FirebaseDatabase.getInstance().getReference();
```
This initializes the Firebase Realtime Database and gets a reference to its root node.

  7.Retrieving user data
  
```
DatabaseReference currentUserRef = mDatabaseRef.child("users").child(user.getUid());
currentUserRef.addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        User currentUser = dataSnapshot.getValue(User.class);
        if (currentUser != null) {
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
```
This section gets a reference to the current user's data in the Firebase Realtime Database, adds a ValueEventListener to retrieve the user data, and displays the user's email in the UI.

  8.Signing out
```
button.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getApplicationContext(), Login.class);
        startActivity(intent);
        finish();
    }
});
```
This code sets an OnClickListener on the logout button that signs out the current user from Firebase Authentication and starts the Login activity.

  9. Changing variable value in Firebase Realtime Database
```
mFeedButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        mDatabaseRef.child("test/FEED_STATUS").setValue(true);
    }
});

mStopFeedButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        mDatabaseRef.child("test/FEED_STATUS").setValue(false);
    }
});
```
These code snippets set OnClickListener on the mFeedButton and mStopFeedButton buttons that change the value of the "FEED_STATUS" variable in the Firebase Realtime Database to either true or false, respectively. The mDatabaseRef.child() method specifies the location of the variable in the database.

  10. Displaying current time and date
```
TimeZone timeZone = TimeZone.getTimeZone("America/Toronto");

final SimpleDateFormat dateFormatDate = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
dateFormatDate.setTimeZone(timeZone);

final SimpleDateFormat dateFormatTime = new SimpleDateFormat("h:mm:ss a", Locale.getDefault());
dateFormatTime.setTimeZone(timeZone);

TextView currentDate = findViewById(R.id.current_date_text);
TextView currentTime = findViewById(R.id.current_time_text);

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

final Runnable updateTimeRunnable = new Runnable() {
    @Override
    public void run() {
        calendar.setTimeInMillis(System.currentTimeMillis());
        currentTime.setText(dateFormatTime.format(calendar.getTime()));
        currentTime.postDelayed(this, 1000);
    }
};
updateTimeRunnable.run();
```
This section sets the time zone, creates two SimpleDateFormat objects to format the date and time, finds the TextView widgets to display the date and time, creates a Calendar object to get the current date and time, and sets two Runnable objects to update the date and time TextViews every second.


This is an Android Studio project written in Java for user registration with Firebase Authentication and Firebase Realtime Database. Below is an explanation of the code lines in the Register.java file
--------------------------------------------------------------------------------------------------------------------------
Register.java
--------------------------------------------------------------------------------------------------------------------------
```
package com.example.petfeedtwo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword;
    Button buttonReg;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView;

    DatabaseReference mDatabase;

    // Override the onStart method of the activity
    @Override
    public void onStart() {
        super.onStart();
        // Get the current user from the FirebaseAuth instance
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // If the current user is not null, start the MainActivity and finish the current activity
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
```
1. This is the Java class named Register extending AppCompatActivity class.

2. It imports necessary classes required to use in the Java class.

3. The onStart() method is overridden, which is executed every time the activity starts.

4. editTextEmail, editTextPassword, buttonReg, progressBar, textView are declared as private instances of their respective classes.

5. mAuth and mDatabase are declared as private instances of FirebaseAuth and DatabaseReference classes respectively.

```
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonReg = findViewById(R.id.btn_register);
        mAuth = FirebaseAuth.getInstance();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        progressBar = findViewById(R.id.progressBar);
        textView= findViewById(R.id.loginNow);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent((getApplicationContext()), Login.class);
                startActivity(intent);
                finish();
            }
        });
```
1.The onCreate() method is overridden, which is executed once the activity is created.

2. setContentView() sets the layout of the activity to the XML file activity_register.xml.

3. findViewById() retrieves the respective XML elements by their ID.

4. mAuth is initialized with the FirebaseAuth instance using the getInstance() method.

5. mDatabase is initialized with the FirebaseDatabase instance using the getInstance() method and then with getReference() method to access the root node of the database.

6. An OnClickListener is set on the textView to navigate to the Login activity when clicked.

```
                // Check if the email or password are empty, and display a toast message if they are
                if (TextUtils.isEmpty(email)){
                    Toast.makeText(Register.this, "Enter your email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)){
                   Toast.makeText(Register.this, "Enter your password", Toast.LENGTH_SHORT).show();
                   return;
                }

                // Register the user with the entered email and password using the createUserWithEmailAndPassword method
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // Hide the progress bar
                                progressBar.setVisibility(View.GONE);
                                // If the registration is successful, display a toast message and finish the activity
                                if (task.isSuccessful()) {
                                    String userId = mAuth.getCurrentUser().getUid();
                                    User user = new User(email, password);
                                    mDatabase.child("users").child(userId).setValue(user);
                                    Toast.makeText(Register.this, "Account Created.",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(Register.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}
```

1. An OnClickListener is set on the buttonReg to register a new user when clicked.

2. progressBar is set to be visible during the registration process.

3. The user's entered email and password are retrieved from the editTextEmail and editTextPassword fields respectively.

4. TextUtils.isEmpty() method checks whether email or password fields are empty or not. If empty, a toast message will be displayed asking the user to fill in the fields and the method will return without executing further.

5. If both fields are filled, createUserWithEmailAndPassword() method of FirebaseAuth class is called to register the user with the entered email and password.

6. addOnCompleteListener() method is used to monitor the registration process, whether it's successful or not.

7. If the registration is successful, a new user is created with a unique userId and the user's email and password using the User class. The user data is then stored in the Firebase Realtime Database under the users node with the userId as the key using setValue() method.

8. A toast message is displayed to notify the user about the successful registration and the activity is finished.

9 .If the registration fails, a toast message is displayed notifying the user about the authentication failure.

--------------------------------------------------------------------------------------------------------------------------
User.java
--------------------------------------------------------------------------------------------------------------------------

```
package com.example.petfeedtwo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class User {
    public String email;
    public String password;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }
```
1 .This is a simple Java class named User containing two instance variables email and password.

2. The User class has a default constructor that is required for calls to DataSnapshot.getValue(User.class) in Firebase Realtime Database.

```
    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }
```
1. The User class has a constructor that takes in two parameters email and password to initialize the instance variables email and password.

```
    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }
```
1. The getPassword() and getEmail() methods are getter methods that return the values of the password and email instance variables respectively. These methods are used to retrieve user data from the Firebase Realtime Database in other parts of the application.

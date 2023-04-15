#This code contains two parts: an Arduino sketch (C++) and an Android app (Java) that work together to create a pet feeder controlled through the internet using Firebase Realtime Database.

The Arduino sketch controls a servo motor to feed the pet, while the Android app allows the user to log in and control the feeder remotely.
-----------------------------------------------------------------------------------------------------------------------------------------------------------------------
Arduino sketch (C++):
-----------------------------------------------------------------------------------------------------------------------------------------------------------------------
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


-----------------------------------------------------------------------------------------------------------------------------------------------------------------------
Android app (Java):

Login.java
-----------------------------------------------------------------------------------------------------------------------------------------------------------------------
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

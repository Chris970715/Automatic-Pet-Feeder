# Automatic-Pet-Feeder

This code is a C++ sketch for an Arduino-based microcontroller (ESP32, ESP8266, or PICO_RP2040) that controls a servo motor based on data stored in a Firebase Realtime Database. The sketch is intended to be compiled and uploaded to a compatible microcontroller using the Arduino IDE.

Here's a detailed overview of the code that you can use as a description for your GitHub repository:

Title: Servo Motor Control using Firebase Realtime Database

Description:

This project demonstrates how to control a servo motor using an ESP32, ESP8266, or PICO_RP2040 microcontroller connected to a Firebase Realtime Database. The microcontroller retrieves the status of the motor from the database and controls the motor accordingly.

Features:

Compatible with ESP32, ESP8266, and PICO_RP2040 microcontrollers
Connects to Firebase Realtime Database to retrieve motor status
Controls a servo motor based on the motor status from the database
Dependencies:

Servo library
Arduino core library
WiFi library for ESP8266
WiFi library for ESP32
Firebase ESP32 library
Firebase ESP8266 library
Firebase ESP Client library for PICO_RP2040
TokenHelper.h and RTDBHelper.h from the Firebase ESP Client library
How it works:

Connect to the Wi-Fi network using the provided credentials.
Authenticate and connect to the Firebase Realtime Database using the API key, database URL, and user credentials.
In the main loop, check if the Firebase connection is ready and if it's time to send data.
Retrieve the value of "/test/FEED_STATUS" from the Firebase Realtime Database.
If the value is true, turn on the servo motor by setting its angle to 0 degrees and set "/test/FEED_STATUS" to false.
If the value is false, turn off the servo motor by setting its angle to 90 degrees.
Increment a global count variable.
With this description, users visiting your GitHub repository will have a clear understanding of the purpose, functionality, and dependencies of the project.

```
#include <Servo.h>
#include <Arduino.h>
#if defined(ESP32)
#include <WiFi.h>
#include <FirebaseESP32.h>
#elif defined(ESP8266)
#include <ESP8266WiFi.h>
#include <FirebaseESP8266.h>
#elif defined(PICO_RP2040)
#include <WiFi.h>
#include <FirebaseESP8266.h>
#include <Firebase_ESP_Client.h>
#endif

/*
<Servo.h>: Servo library for controlling servo motors.
<Arduino.h>: Arduino core library for basic functions.
<WiFi.h> and <ESP8266WiFi.h>: Wi-Fi library depending on the microcontroller.
<FirebaseESP32.h> and <FirebaseESP8266.h>: Firebase library depending on the microcontroller.
<Firebase_ESP_Client.h>: Firebase library for PICO_RP2040.
<TokenHelper.h>: Helper functions for handling Firebase authentication tokens.
<RTDBHelper.h>: Helper functions for Firebase Realtime Database operations.
*/
```

The above code includes necessary libraries for the Arduino board used. It includes the Servo library for controlling the servo motor and libraries for connecting to Firebase Realtime Database using WiFi.



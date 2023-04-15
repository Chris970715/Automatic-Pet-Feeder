# Automatic-Pet-Feeder

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
```

The above code includes necessary libraries for the Arduino board used. It includes the Servo library for controlling the servo motor and libraries for connecting to Firebase Realtime Database using WiFi.

<Servo.h>: Servo library for controlling servo motors.
<Arduino.h>: Arduino core library for basic functions.
<WiFi.h> and <ESP8266WiFi.h>: Wi-Fi library depending on the microcontroller.
<FirebaseESP32.h> and <FirebaseESP8266.h>: Firebase library depending on the microcontroller.
<Firebase_ESP_Client.h>: Firebase library for PICO_RP2040.
<TokenHelper.h>: Helper functions for handling Firebase authentication tokens.
<RTDBHelper.h>: Helper functions for Firebase Realtime Database operations.

/*
File: Source Code of Android Studio
Course: TPJ655 NCD
Name: Sunghyun Park, Gyubum Kim
Student #: 109226209, 101492171
Date: 2023 - 04 - 07
*/

package com.example.petfeedtwo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class User {
    public String email;
    public String password;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }
}
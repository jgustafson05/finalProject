package com.example.finalproject;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;


public class NewSurveyActivity extends AppCompatActivity {

  ArrayList<Variable> variables = new ArrayList<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_new_survey);

    //Need to change layout based on screen size

    String surveyTitle = getIntent().getStringExtra("title");
    File surveyFile = new File(getApplicationContext().getFilesDir(), surveyTitle);

    Button newVariable = findViewById(R.id.newVariable);
    Button createSurvey = findViewById(R.id.createSurvey);

    newVariable.setOnClickListener(unused -> createNewVariableDialog());
  }

  private void createNewVariableDialog() {

  }

}

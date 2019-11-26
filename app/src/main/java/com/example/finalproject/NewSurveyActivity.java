package com.example.finalproject;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AlertDialog;
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
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setView(R.layout.dialog_new_variable);

    // Making a dialog to request title for survey
    builder.setTitle(R.string.dialog_new_variable_title)
            .setPositiveButton(R.string.dialog_create, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(final DialogInterface dialog, final int which) {
                Dialog dialogView = (Dialog) dialog;
                TextView variablePrompt = dialogView.findViewById(R.id.variablePrompt);
                ToggleButton variableTypeButton = dialogView.findViewById(R.id.variableTypeButton);
                if (variablePrompt.getText().toString().equals("")) {
                  //Alert user somehow
                  return;
                }
                variables.add(new Variable(variablePrompt.getText().toString(),
                        variableTypeButton.isActivated()));
              }
            })
            .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(final DialogInterface dialog, final int which) {
              }
            });
    builder.show();
  }

  private void updateVariables() {

  }

}

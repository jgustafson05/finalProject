package com.example.finalproject;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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
                  //Don't allow same name here
                  //Alert user somehow
                  return;
                }

                Variable toAdd = new Variable(variablePrompt.getText().toString(),
                        variableTypeButton.isActivated());
                boolean addedVariable = false;
                if (variables.size() == 0) {
                  variables.add(0, toAdd);
                  addedVariable = true;
                } else {
                  //Places new variable in lexicographic order
                  for (int i = 0; i < variables.size(); i++) {
                    if (toAdd.compareTo(variables.get(i)) > 0) {
                      continue;
                    }
                    variables.add(i, toAdd);
                    addedVariable = true;
                    break;
                  }
                }
                if (!addedVariable) {
                  variables.add(toAdd);
                }

                //Add to the file
                updateVariablesUI();
              }
            })
            .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(final DialogInterface dialog, final int which) {
              }
            });
    builder.show();
  }

  private void updateVariablesUI() {
    LinearLayout variablesList = findViewById(R.id.variableContainer);

    //Clear current chunks
    variablesList.setVisibility(View.GONE);
    variablesList.removeAllViews();

    for (int i = 0; i < variables.size(); i++) {
      //Add chunk
      View variableChunk = getLayoutInflater().inflate(R.layout.chunk_variable,
              variablesList, false);

      //Set name
      TextView variableName = variableChunk.findViewById(R.id.variableName);
      variableName.setText(variables.get(i).getName());

      //Set spinner default

      //Setup remove button
      final Variable toRemove = variables.get(i);
      Button removeVariable = variableChunk.findViewById(R.id.removeVariable);
      removeVariable.setOnClickListener(unused -> {
        variables.remove(toRemove);
        updateVariablesUI();
      });
      variablesList.addView(variableChunk);
    }
    variablesList.setVisibility(View.VISIBLE);
  }

}

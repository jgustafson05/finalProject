package com.example.finalproject;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
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
    createSurvey.setOnClickListener(unused -> {
      //Add an 'are you sure' here?
      if (variables.isEmpty()) {
        Toast.makeText(this, "Add a response variable first.",
                Toast.LENGTH_SHORT).show();
        return;
      }
      try {
        if (FileWriter.writeFile(this, surveyFile, variables, null)) {
          Intent intent = new Intent(this, MainActivity.class);
          Bundle bundle = new Bundle();
          bundle.putString("title", surveyTitle);
          //Add to intent here

          intent.putExtras(bundle);
          startActivity(intent);
          finish();
        } else {
          surveyFile.delete();
        }
      } catch (Exception e) {
        Toast.makeText(this, "Not enough space", Toast.LENGTH_SHORT).show();
      }
    });
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

                for (int i = 0; i < variables.size(); i++) {
                  if (variablePrompt.getText().toString().equals(variables.get(i).getName())) {
                    Toast sameName = Toast.makeText(getApplicationContext(),
                            "'" + variablePrompt.getText().toString()
                                    + "'" + " is already a variable name.",
                            Toast.LENGTH_LONG);
                    sameName.show();
                    return;
                  }
                }
                addVariable(new Variable(variablePrompt.getText().toString(),
                        variableTypeButton.isChecked()));

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
      Spinner variableType = variableChunk.findViewById(R.id.variableType);

      int spinnerPosition = 1;
      if (variables.get(i).isCategorical()) {
        spinnerPosition = 0;
      }
      variableType.setSelection(spinnerPosition);

      //Spinner listener to change variable type
      final int variableNumber = i;
      variableType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
          if (position == 0) {
            variables.get(variableNumber).setCategorical(true);
          } else {
            variables.get(variableNumber).setCategorical(false);
          }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
      });

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

  private boolean setResponseVariable(int index) {
    if (variables.size() == 0 || variables.size() == 1) {
      return false;
    }
    if (index < 0 || index >= variables.size()) {
      return false;
    }
    variables.add(0, variables.remove(index));
    return addVariable(variables.remove(1));
  }

  private boolean addVariable(Variable variable) {
    //Places new variable in lexicographic order
    for (int j = 1; j < variables.size(); j++) {
      if (variable.compareTo(variables.get(j)) <= 0) {
        if (variable.compareTo(variables.get(j)) == 0) {
          return false;
        }
        variables.add(j, variable);
        return true;
      }
    }
    return variables.add(variable);
  }

}

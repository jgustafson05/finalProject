package com.example.finalproject;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;

/** Requires and completes creation or loading of a new file. */
public class LaunchActivity extends AppCompatActivity {

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_launch);

    Button createFile = findViewById(R.id.createFile);
    Button loadFile = findViewById(R.id.loadFile);

    createFile.setOnClickListener(unused -> createNewFileDialog());
    loadFile.setOnClickListener(unused -> createLoadFileDialog());
  }

  private void createNewFileDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setView(R.layout.dialog_new_file);

    // Making a dialog to request title for survey
    builder.setTitle(R.string.dialog_new_file_title)
           .setPositiveButton(R.string.dialog_create, new DialogInterface.OnClickListener() {
             @Override
             public void onClick(final DialogInterface dialog, final int which) {
               Dialog dialogView = (Dialog) dialog;
               TextView dialogPrompt = dialogView.findViewById(R.id.filePrompt);
               if (dialogPrompt.getText().toString().equals("")) {
                 //Alert user somehow
                 return;
               }
               try {
                 File surveyFile = new File(getApplicationContext().getFilesDir(),
                           dialogPrompt.getText().toString());
                 if (!surveyFile.mkdir()
                         || new File(surveyFile.getPath(), "variables").createNewFile()
                         || new File(surveyFile.getPath(),"sample_points").createNewFile()
                 ) {
                   Toast.makeText(LaunchActivity.this,
                           "A survey by this name already exists", Toast.LENGTH_SHORT)
                           .show();
                   return;
                 }
               } catch (IOException e) {
                 Toast ioProblem = Toast.makeText(getApplicationContext(),
                         "Error: not enough space to make a file", Toast.LENGTH_LONG);
                 ioProblem.show();
               }
               Intent intent = new Intent(getApplicationContext(), NewSurveyActivity.class);
               Bundle bundle = new Bundle();
               bundle.putString("title", dialogPrompt.getText().toString());
               //Add to intent here

               intent.putExtras(bundle);
               startActivity(intent);
               finish();
             }
           })
           .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
             @Override
             public void onClick(final DialogInterface dialog, final int which) {
             }
           });
    builder.show();
  }

  private void createLoadFileDialog() {
    File[] fileArray = getApplicationContext().getFilesDir().listFiles();
    View view = getLayoutInflater().inflate(R.layout.dialog_load_file, null);
    LinearLayout fileContainer = view.findViewById(R.id.fileContainer);

    if (fileArray != null) {
      fileContainer.removeAllViews();
      for (int i = 0; i < fileArray.length; i++) {
        View fileChunk = getLayoutInflater().inflate(R.layout.chunk_file, fileContainer, false);

        TextView fileName = fileChunk.findViewById(R.id.fileName);
        fileName.setText(fileArray[i].getName());
        //ADD SOME FUNCTIONALITY

        fileContainer.addView(fileChunk);
      }
    }
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setView(view);
    builder.setTitle(R.string.dialog_load_file_title)
            .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
              }
            });
    builder.show();
  }

}

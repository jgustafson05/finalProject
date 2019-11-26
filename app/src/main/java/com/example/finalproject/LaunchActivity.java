package com.example.finalproject;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

/** Requires and completes creation or loading of a new file. */
public class LaunchActivity extends AppCompatActivity {

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_launch);

    Button createFile = findViewById(R.id.createFile);
    Button loadFile = findViewById(R.id.loadFile);

    createFile.setOnClickListener(unused -> {
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setView(R.layout.dialog_newfile);

      // Making a dialog to request title for survey
      builder.setTitle(R.string.dialog_title)
          .setPositiveButton(R.string.dialog_create, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
              Dialog dialogView = (Dialog) dialog;
              TextView dialogPrompt = dialogView.findViewById(R.id.dialogPrompt);
              if (dialogPrompt.getText().toString().equals("")) {
                //Alert user somehow
                return;
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
    });
    loadFile.setOnClickListener(unused -> { });
  }
}

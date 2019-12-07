package com.example.finalproject;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

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
               File surveyFile = new File(getApplicationContext().getFilesDir(),
                           dialogPrompt.getText().toString());
               if (surveyFile.exists()) {
                 Toast.makeText(LaunchActivity.this,
                         "A survey with this name already exists", Toast.LENGTH_SHORT).show();
                 createNewFileDialog();
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
  }

  private void createLoadFileDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

        fileName.setOnClickListener(unused -> {
          //Add some make sures here in the future.
          Intent intent = new Intent(this, MainActivity.class);
          Bundle bundle = new Bundle();

          bundle.putString("title", fileName.getText().toString());
          bundle.putBoolean("hasSamplePoints", true);

          intent.putExtras(bundle);
          startActivity(intent);
          finish();
        });

        fileName.setOnLongClickListener(unused -> {
          // TODO: Make this open a rename file dialog.
          return true;
        });

        Button delete = fileChunk.findViewById(R.id.delete);

        final File toDelete = fileArray[i];
        delete.setOnClickListener(unused ->  {
          if (toDelete.delete()) {
            Toast.makeText(this, "it worked", Toast.LENGTH_SHORT).show();
          }
        });

        registerForContextMenu(fileName);

        fileContainer.addView(fileChunk);
      }
    }


    builder.setView(view);
    builder.setTitle(R.string.dialog_load_file_title)
            .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
              }
            });
    builder.show();
  }

  @Override
  public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
    super.onCreateContextMenu(menu, v, menuInfo);
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_load_file, menu);
  }

  @Override
  public boolean onContextItemSelected(MenuItem item) {
    AdapterView.AdapterContextMenuInfo info =
            (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
    Toast.makeText(this, item.getItemId(), Toast.LENGTH_SHORT).show();
    switch (item.getItemId()) {
      case R.id.renameFile:
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.dialog_new_file).setTitle(R.string.menu_rename)
          .setPositiveButton(R.string.daialog_rename_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              Dialog dialogView = (Dialog) dialog;
              TextView dialogPrompt = dialogView.findViewById(R.id.filePrompt);
              if (dialogPrompt.getText().toString().equals("")) {
                Toast.makeText(LaunchActivity.this, "Enter a file name",
                        Toast.LENGTH_SHORT).show();
                return;
              }
              TextView oldText = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo())
                      .targetView.findViewById(R.id.fileName);
              File oldFile = new File(getApplicationContext().getFilesDir(),
                      oldText.getText().toString());
              File newFile = new File(getApplicationContext().getFilesDir(),
                      dialogPrompt.getText().toString());
              if (!oldFile.renameTo(newFile)) {
                Toast.makeText(LaunchActivity.this,
                        "A survey with this name already exists", Toast.LENGTH_SHORT)
                        .show();
              }
            }
          }).setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
          });
        builder.show();
      case R.id.deleteFile:
        TextView chosenText = info.targetView.findViewById(R.id.fileName);
        File toDestroy = new File(getApplicationContext().getFilesDir(),
                chosenText.getText().toString());
        if (toDestroy.delete()) {
          Toast.makeText(this, "yeet", Toast.LENGTH_SHORT).show();
        }
        return true;
      default:
        return super.onContextItemSelected(item);
    }
  }
}

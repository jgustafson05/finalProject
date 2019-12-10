package com.example.finalproject;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements SampleItemFragment.OnFragmentInteractionListener {

  private static int CSV_REQUEST_CODE = 1;

  private List<Variable> variables;
  private List<SamplePoint> orderedSamplePoints;
  private List<SamplePoint> alphabeticSamplePoints;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);


    File file = new File(getApplicationContext().getFilesDir(),
            getIntent().getStringExtra("title"));

    Log.println(Log.ERROR, "error", getIntent().getStringExtra("title"));
    variables = FileReader.readVariables(getApplicationContext(), file);
    orderedSamplePoints = FileReader.readSamplePoints(getApplicationContext(), file, variables);

    if (orderedSamplePoints != null) {
      alphabeticSamplePoints = ListSorter.mergeSort(new ArrayList<>(orderedSamplePoints));
    }
    for (SamplePoint s : alphabeticSamplePoints) {
      Log.e("main", Boolean.toString(s.valueIsSet(variables.get(0))));
    }

    updateRecentList();
    updateAlphaList();
  }

  private void updateRecentList() {
    LinearLayout recentList = findViewById(R.id.recentSamplePointContainer);

    //Clear current chunks
    recentList.setVisibility(View.GONE);
    recentList.removeAllViews();

    for (int i = orderedSamplePoints.size() - 1;
         i >= orderedSamplePoints.size() - 3 && i >= 0; i--) {
      //Add chunk
      View sampleChunk = getLayoutInflater().inflate(R.layout.chunk_sample_point,
              recentList, false);

      //Set name
      TextView samplePointName = sampleChunk.findViewById(R.id.pointName);
      samplePointName.setText(orderedSamplePoints.get(i).getName());

      final int index = i;
      samplePointName.setOnClickListener(unused ->
              openSamplePointFragment(orderedSamplePoints.get(index)));

      //Setup remove button
      final int toRemove = i;
      Button removeSamplePoint = sampleChunk.findViewById(R.id.removePoint);
      removeSamplePoint.setOnClickListener(unused -> {
        removePointByOrder(toRemove);
        updateAlphaList();
        updateRecentList();
      });
      recentList.addView(sampleChunk);
    }
    recentList.setVisibility(View.VISIBLE);
  }

  private void updateAlphaList() {
    LinearLayout alphaList = findViewById(R.id.samplePointContainer);

    //Clear current chunks
    alphaList.setVisibility(View.GONE);
    alphaList.removeAllViews();

    for (int i = 0; i < alphabeticSamplePoints.size(); i++) {
      //Add chunk
      View sampleChunk = getLayoutInflater().inflate(R.layout.chunk_sample_point,
              alphaList, false);

      //Set name
      TextView samplePointName = sampleChunk.findViewById(R.id.pointName);
      samplePointName.setText(alphabeticSamplePoints.get(i).getName());


      final int index = i;
      samplePointName.setOnClickListener(unused ->
              openSamplePointFragment(alphabeticSamplePoints.get(index)));

      //Setup remove button
      Button removeSamplePoint = sampleChunk.findViewById(R.id.removePoint);
      removeSamplePoint.setOnClickListener(unused -> {
        removePointAlphabetically(index);
        updateAlphaList();
        updateRecentList();
      });
      alphaList.addView(sampleChunk);
    }
    alphaList.setVisibility(View.VISIBLE);
  }

  private void addSamplePoint(@NonNull String name) {
    SamplePoint point = new SamplePoint(name);
    for (int i = 0; i < alphabeticSamplePoints.size(); i++) {
      if (name.compareTo(alphabeticSamplePoints.get(i).getName()) <= 0) {
        if (name.compareTo(alphabeticSamplePoints.get(i).getName()) == 0) {
          return;
        }
        orderedSamplePoints.add(point);
        alphabeticSamplePoints.add(i, point);
        //Update layout
        updateAlphaList();
        updateRecentList();
        return;
      }
    }
    orderedSamplePoints.add(point);
    alphabeticSamplePoints.add(point);
    //Update Layout
    updateAlphaList();
    updateRecentList();
  }

  private void createNewSamplePointDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    View view = getLayoutInflater().inflate(R.layout.dialog_new_file, null);
    TextView prompt = view.findViewById(R.id.filePrompt);
    prompt.setHint("Item name");
    builder.setView(view);

    // Making a dialog to request title for survey
    builder.setTitle("New Sample Item")
            .setPositiveButton(R.string.dialog_create, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(final DialogInterface dialog, final int which) {
                Dialog dialogView = (Dialog) dialog;
                TextView dialogPrompt = dialogView.findViewById(R.id.filePrompt);
                if (dialogPrompt.getText().toString().equals("")) {
                  //Alert user somehow
                  return;
                }
                String pointName = dialogPrompt.getText().toString();
                for (int i = 0; i < alphabeticSamplePoints.size(); i++) {
                  if (alphabeticSamplePoints.get(i).getName().equals(pointName)) {
                    Toast.makeText(getApplicationContext(),
                            "An item with this name already exists", Toast.LENGTH_SHORT).show();
                    return;
                  }
                }
                addSamplePoint(pointName);
              }
            })
            .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(final DialogInterface dialog, final int which) {
              }
            });
    builder.show();
  }

  private void removePointAlphabetically(int alphabeticIndex) {
    orderedSamplePoints.remove(alphabeticSamplePoints.remove(alphabeticIndex));
  }

  private void removePointByOrder(int orderedIndex) {
    alphabeticSamplePoints.remove(orderedSamplePoints.remove(orderedIndex));
  }

  private void openSamplePointFragment(SamplePoint point) {
    Variable[] variableArray = new Variable[variables.size()];
    for (int i = 0; i < variables.size(); i++) {
      variableArray[i] = variables.get(i);
    }
    Fragment pointView = SampleItemFragment.newInstance(variableArray, point, point.hashCode());

    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    transaction.replace(R.id.fragmentHolder, pointView);
    transaction.addToBackStack(null);

    transaction.commit();
    setTitle(point.getName());
  }

  public void onAttachFragment(Fragment fragment) {
    if (fragment instanceof SampleItemFragment) {
      SampleItemFragment item = (SampleItemFragment) fragment;
      item.setOnFragmentInteractionListener(this);
    }
  }

  public void updateSamplePoint(SampleItemFragment fragment, SamplePoint point, int key) {
    for (int i = 0; i < alphabeticSamplePoints.size(); i++) {
      if (alphabeticSamplePoints.hashCode() == key) {
        alphabeticSamplePoints.set(i, point);
        break;
      }
    }
    for (int i = 0; i < orderedSamplePoints.size(); i++) {
      if (orderedSamplePoints.get(i).hashCode() == key) {
        orderedSamplePoints.set(i, point);
        break;
      }
    }
    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    transaction.remove(fragment);
    transaction.commit();
    File file = new File(getApplicationContext().getFilesDir(),
            getIntent().getStringExtra("title"));
    FileWriter.writeFile(getApplicationContext(), file, variables, orderedSamplePoints);
    setTitle(getIntent().getStringExtra("title"));
  }

  public void addVariableCategory(Variable updated, int index) {

    Variable old = variables.get(index);

    for (SamplePoint s : orderedSamplePoints) {
      if (s.valueIsSet(old)) {
        Log.e("main", "updated");
        s.setValue(updated, (int) s.getValue(old));
      }
    }
    variables.set(index, updated);
  }

  private void createCsv() {
    File file = new File(getApplicationContext().getFilesDir(),
            getIntent().getStringExtra("title"));
    FileWriter.writeFile(getApplicationContext(), file, variables, orderedSamplePoints);
    Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
    intent.addCategory(Intent.CATEGORY_OPENABLE);
    intent.setType("text/csv");
    intent.putExtra(Intent.EXTRA_TITLE, getIntent().getStringExtra("title") + ".csv");

    startActivityForResult(intent, CSV_REQUEST_CODE);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
    if (requestCode == CSV_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
      if (resultData != null) {
        FileWriter.writeCsv(this, resultData.getData(), variables, alphabeticSamplePoints);
      }
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menuAddItem:
        createNewSamplePointDialog();
        return true;
      case R.id.menuExport:
        createCsv();
        return true;
      default: return false;
    }
  }

}

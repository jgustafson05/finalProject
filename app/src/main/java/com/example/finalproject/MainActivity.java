package com.example.finalproject;

import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

  private List<Variable> variables;
  private List<SamplePoint> orderedSamplePoints;
  private List<SamplePoint> alphabeticSamplePoints;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);


    File file = new File(getApplicationContext().getFilesDir(),
            getIntent().getStringExtra("title"));

    variables = FileReader.readVariables(getApplicationContext(), file);
    orderedSamplePoints = FileReader.readSamplePoints(getApplicationContext(), file);

    if (orderedSamplePoints != null) {
      alphabeticSamplePoints = ListSorter.mergeSort(orderedSamplePoints);
    }
  }

  private boolean addSamplePoint(@NonNull String name) {
    SamplePoint point = new SamplePoint(name);
    for (int i = 0; i < alphabeticSamplePoints.size(); i++) {
      if (name.compareTo(alphabeticSamplePoints.get(i).getName()) <= 0) {
        if (name.compareTo(alphabeticSamplePoints.get(i).getName()) == 0) {
          return false;
        }
        orderedSamplePoints.add(point);
        alphabeticSamplePoints.add(i, point);
        //Update layout
        return true;
      }
    }
    orderedSamplePoints.add(point);
    //Update Layout
    return alphabeticSamplePoints.add(point);
  }

  private void removePointAlphabetically(int alphabeticIndex) {
    orderedSamplePoints.remove(alphabeticSamplePoints.remove(alphabeticIndex));
  }

  private void removePointByOrder(int orderedIndex) {
    alphabeticSamplePoints.remove(orderedSamplePoints.remove(orderedIndex));
  }

  /*@Override
  public boolean onCreateOptionsMenu(Menu menu) {
    Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_scrolling, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }
    return super.onOptionsItemSelected(item);
  }*/
}

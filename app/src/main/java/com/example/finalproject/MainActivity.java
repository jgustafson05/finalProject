package com.example.finalproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.io.File;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SampleItemFragment.OnFragmentInteractionListener {

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
    addSamplePoint("Test_Point");
    Button test = findViewById(R.id.launchFragment);
    test.setOnClickListener(unused ->
            openSamplePointFragment(orderedSamplePoints.get(orderedSamplePoints.size() - 1)));
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

  private void openSamplePointFragment(SamplePoint point) {
    Variable[] variableArray = new Variable[variables.size()];
    for (int i = 0; i < variables.size(); i++) {
      variableArray[i] = variables.get(i);
    }
    try {
      Fragment pointView = SampleItemFragment.newInstance(variableArray, point, point.hashCode());
      FrameLayout fragmentHolder = findViewById(R.id.fragmentHolder);

      FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
      transaction.replace(R.id.fragmentHolder, pointView);
      transaction.addToBackStack(null);

      transaction.commit();
      setTitle(point.getName());

      Button test = findViewById(R.id.launchFragment);
    } catch (Exception e) {
      Log.e("fragment", "caught at main activity" + e.toString());
    }


  }

  public void onAttachFragment(Fragment fragment) {
    if (fragment instanceof SampleItemFragment) {
      SampleItemFragment item = (SampleItemFragment) fragment;
      item.setOnFragmentInteractionListener(this);
    }
  }


  public void updateSamplePoint(SamplePoint point, int key) {
    for (int i = 0; i < alphabeticSamplePoints.size(); i++) {
      if (alphabeticSamplePoints.hashCode() == key) {
        alphabeticSamplePoints.set(i, point);
      }
      if (orderedSamplePoints.get(i).hashCode() == key) {
        orderedSamplePoints.set(i, point);
      }
    }
  }

  public void updateVariableCategories(String category, int index) {
    variables.get(index).addCategory(category);
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

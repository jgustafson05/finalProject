package com.example.finalproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

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
    Log.println(Log.ERROR, "error", "made to main activity");
    setContentView(R.layout.activity_main);


    File file = new File(getApplicationContext().getFilesDir(),
            getIntent().getStringExtra("title"));

    Log.println(Log.ERROR, "error", getIntent().getStringExtra("title"));
    variables = FileReader.readVariables(getApplicationContext(), file);
    orderedSamplePoints = FileReader.readSamplePoints(getApplicationContext(), file);

    Log.println(Log.ERROR, "error", orderedSamplePoints.toString());
    if (orderedSamplePoints != null) {
      alphabeticSamplePoints = ListSorter.mergeSort(orderedSamplePoints);
    }
    LinearLayout alphaList = findViewById(R.id.samplePointContainer);

    //Clear current chunks
    alphaList.setVisibility(View.GONE);
    alphaList.removeAllViews();

    updateRecentList();
    //updateAlphaList() crashes app
    updateAlphaList();
    addSamplePoint("Test_Point");
    Button test = findViewById(R.id.launchFragment);
    test.setOnClickListener(unused ->
            openSamplePointFragment(orderedSamplePoints.get(orderedSamplePoints.size() - 1)));
  }

  private void updateRecentList() {
    LinearLayout recentList = findViewById(R.id.recentSamplePointContainer);

    //Clear current chunks
    recentList.setVisibility(View.GONE);
    recentList.removeAllViews();

    for (int i = orderedSamplePoints.size() - 1; i >= 0; i--) {
      //Add chunk
      View sampleChunk = getLayoutInflater().inflate(R.layout.chunk_sample_point,
              recentList, false);

      //Set name
      TextView samplePointName = sampleChunk.findViewById(R.id.pointName);
      samplePointName.setText(orderedSamplePoints.get(i).getName());

      //Setup remove button
      final SamplePoint toRemove = orderedSamplePoints.get(i);
      Button removeSamplePoint = sampleChunk.findViewById(R.id.removePoint);
      removeSamplePoint.setOnClickListener(unused -> {
        orderedSamplePoints.remove(toRemove);
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

    for (int i = 0; i < orderedSamplePoints.size(); i++) {
      //Add chunk
      View sampleChunk = getLayoutInflater().inflate(R.layout.chunk_sample_point,
              alphaList, false);

      //Set name
      TextView samplePointName = sampleChunk.findViewById(R.id.pointName);
      samplePointName.setText(alphabeticSamplePoints.get(i).getName());

      //Setup remove button
      final SamplePoint toRemove = alphabeticSamplePoints.get(i);
      Button removeSamplePoint = sampleChunk.findViewById(R.id.removePoint);
      removeSamplePoint.setOnClickListener(unused -> {
        alphabeticSamplePoints.remove(toRemove);
        updateRecentList();
      });
      alphaList.addView(sampleChunk);
    }
    alphaList.setVisibility(View.VISIBLE);
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
    updateAlphaList();
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
      Fragment pointView = SampleItemFragment.newInstance(variableArray, point);
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


  public void onFragmentInteraction(Uri uri) {
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

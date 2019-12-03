package com.example.finalproject;

import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {

  private List<Variable> variables;
  private List<SamplePoint> samplePoints;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);


    File file = new File(getApplicationContext().getFilesDir(),
            getIntent().getStringExtra("title"));

    variables = FileReader.readVariables(getApplicationContext(), file);
    samplePoints = FileReader.readSamplePoints(getApplicationContext(), file);

    if (samplePoints == null) {
      Toast.makeText(this, "samplePoint error", Toast.LENGTH_SHORT).show();
    }

  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
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
  }
}

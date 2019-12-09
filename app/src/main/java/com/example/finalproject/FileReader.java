package com.example.finalproject;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FileReader {

  public static List<Variable> readVariables(Context context, File file) {
    try {
      FileInputStream fis = context.openFileInput(file.getName());
      InputStreamReader inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);
      StringBuilder stringBuilder = new StringBuilder();
      try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
        String line = reader.readLine();
        while (line != null) {
          stringBuilder.append(line).append('\n');
          line = reader.readLine();
        }
        String contents = stringBuilder.toString();
        Log.println(Log.ERROR,"error", contents);
        List<Variable> toReturn = new ArrayList<>();

        String[] dataSplit = contents.split(";");
        String[] variableLines = dataSplit[0].split("\n");
        for (String l : variableLines) {
          Log.println(Log.ERROR, "error", l);
          String[] parts = l.split(":");
          toReturn.add(toReturn.size(), new Variable(parts[0], parts[1].equals("t")));
          if (!parts[2].equals("null")) {
            String[] categories = parts[2].split(",");
            for (String c : categories) {
              toReturn.get(toReturn.size() - 1).addCategory(c);
            }
          }
        }
        return toReturn;
      } catch (IOException e) {
        System.err.println(e.toString());
        return null;
      }
    } catch (FileNotFoundException e) {
      System.err.println(e.toString());
      return null;
    }
  }

  public static List<SamplePoint> readSamplePoints(Context context, File file,
                                                   List<Variable> matchedList) {
    try {
      FileInputStream fis = context.openFileInput(file.getName());
      InputStreamReader inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);
      StringBuilder stringBuilder = new StringBuilder();
      try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
        String line = reader.readLine();
        while (line != null) {
          stringBuilder.append(line).append('\n');
          line = reader.readLine();
        }
        String contents = stringBuilder.toString();

        String[] dataSplit = contents.split(";");
        if (dataSplit.length == 1) {
          return new ArrayList<>();
        }
        List<SamplePoint> toReturn = new ArrayList<>();

        String[] lines = dataSplit[1].split("\n");

        for (int i = 0; i < lines.length; i++) {
          String[] dataPoints = lines[i].split(":");
          toReturn.add(new SamplePoint(dataPoints[0]));
          for (int j = 0; j < matchedList.size(); j++) {
            if (matchedList.get(j) == null || dataPoints[j + 1].equals("n")) {
              continue;
            }
            if (matchedList.get(j).isCategorical()) {
              toReturn.get(i).setValue(matchedList.get(j), Integer.parseInt(dataPoints[j + 1]));
              Log.e("reader", Double.toString(toReturn.get(i).getValue(matchedList.get(j))));
            } else {
              toReturn.get(i).setValue(matchedList.get(j), Double.parseDouble(dataPoints[j + 1]));
              Log.e("reader", Double.toString(toReturn.get(i).getValue(matchedList.get(j))));
            }
          }
        }
        for (SamplePoint s : toReturn) {
          Log.e("eee", s.getName());
          Log.e("eee", Boolean.toString(s.valueIsSet((matchedList.get(0)))));
        }
        return toReturn;
      } catch (IOException e) {
        System.err.println(e.toString());
        return null;
      }
    } catch (FileNotFoundException e) {
      System.err.println(e.toString());
      return null;
    }
  }
}

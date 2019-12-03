package com.example.finalproject;

import android.content.Context;

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
        List<Variable> toReturn = new ArrayList<>();

        String[] lines = contents.split("\n");
        for (String l : lines) {
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
                                                   List<Variable> variables) {
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
        List<SamplePoint> toReturn = new ArrayList<>();

        String[] lines = contents.split("\n");

        //Find hash matches for variables
        String[] key = lines[0].split(":");
        List<Variable> matchedList = new ArrayList<>(key.length);
        for (int i = 0; i < key.length; i++) {
          int toMatch = Integer.parseInt(key[i]);
          for (int j = 0; j < variables.size(); j++) {
            if (variables.get(j).hashCode() == toMatch) {
              matchedList.add(i, variables.remove(j));
              break;
            }
          }
        }

        for (int i = 1; i < lines.length; i++) {
          String[] dataPoints = lines[i].split(":");
          toReturn.add(new SamplePoint(dataPoints[0]));
          for (int j = 0; j < matchedList.size(); j++) {
            if (matchedList.get(j) == null || !dataPoints[j + 1].equals("n")) {
              continue;
            }
            if (matchedList.get(j).isCategorical()) {
              toReturn.get(i).setValue(matchedList.get(j), Integer.parseInt(dataPoints[j + 1]));
            } else {
              toReturn.get(i).setValue(matchedList.get(j), Double.parseDouble(dataPoints[j + 1]));
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
}

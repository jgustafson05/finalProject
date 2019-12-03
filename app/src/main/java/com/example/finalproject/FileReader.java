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

        String[] dataSplit = contents.split(";");
        String[] variableLines = contents.split("\n");
        for (String l : variableLines) {
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

  public static List<SamplePoint> readSamplePoints(Context context, File file) {
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

        List<Variable> matchedList = new ArrayList<>();

        String[] dataSplit = contents.split(";");
        if (dataSplit.length == 1) {
          return new ArrayList<>();
        }
        String[] variableLines = dataSplit[0].split("\n");
        for (String l : variableLines) {
          String[] parts = l.split(":");
          matchedList.add(matchedList.size(), new Variable(parts[0], parts[1].equals("t")));
          if (!parts[2].equals("null")) {
            String[] categories = parts[2].split(",");
            for (String c : categories) {
              matchedList.get(matchedList.size() - 1).addCategory(c);
            }
          }
        }
        List<SamplePoint> toReturn = new ArrayList<>();

        String[] lines = dataSplit[1].split("\n");

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

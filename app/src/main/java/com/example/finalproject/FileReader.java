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

}

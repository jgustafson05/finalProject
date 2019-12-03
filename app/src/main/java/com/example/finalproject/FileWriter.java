package com.example.finalproject;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class FileWriter {

  public static boolean writeVariables(Context context, File file, List<Variable> variables) {

    file.delete();
    try {
      file.createNewFile();
    } catch (Exception e) {
      Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
      return false;
    }

    StringBuilder fileContentsBuilder = new StringBuilder();
    for (int i = 0; i < variables.size(); i++) {
      String typeString = "f";
      String categoriesString = "null";
      if (variables.get(i).isCategorical()) {
        typeString = "t";
        List<String> categories = variables.get(i).getCategories();
        if (!categories.isEmpty()) {
          StringBuilder categoriesStringBuilder = new StringBuilder();
          for (int j = 0; j < categories.size(); j++) {
            categoriesStringBuilder.append(categories.get(j));
            if (j != categories.size() - 1) {
              categoriesStringBuilder.append(",");
            }
          }
          categoriesString = categoriesStringBuilder.toString();
        }
      }
      String newLine = variables.get(i).getName() + ":"
              + typeString + ":"
              + categoriesString + "\n";
      fileContentsBuilder.append(newLine);
    }
    String fileContents = fileContentsBuilder.toString();

    try (FileOutputStream fos = context.openFileOutput(file.getName(), Context.MODE_PRIVATE)) {
      fos.write(fileContents.getBytes());
    } catch (Exception e) {
      System.err.println(e.toString());
      return false;
    }
    return true;
  }

  public static boolean writeSamplePoints(Context context, File file, List<Variable> variables,
                                          List<SamplePoint> samplePoints) {
    file.delete();
    try {
      file.createNewFile();
    } catch (Exception e) {
      Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
      return false;
    }

    StringBuilder fileContentsBuilder = new StringBuilder();

    //Add ordered hash of variables for reference
    for (int i = 0; i < variables.size(); i++) {
      fileContentsBuilder.append(variables.get(i).hashCode()).append(":");
    }
    fileContentsBuilder.deleteCharAt(fileContentsBuilder.length() - 1).append("\n");

    //Table of values with variables as columns
    for (int i = 0; i < samplePoints.size(); i++) {
      StringBuilder newLine = new StringBuilder(samplePoints.get(i).getName()).append(":");
      for (int j = 0; j < variables.size(); j++) {
        try {
          newLine.append(samplePoints.get(i).getValue(variables.get(j)));
        } catch (NullPointerException e) {
          newLine.append("n");
        } finally {
          newLine.append(":");
        }
      }
      newLine.deleteCharAt(newLine.length() - 1).append("\n");
      fileContentsBuilder.append(newLine);
    }
    String fileContents = fileContentsBuilder.toString();

    try (FileOutputStream fos = context.openFileOutput(file.getName(), Context.MODE_PRIVATE)) {
      fos.write(fileContents.getBytes());
    } catch (Exception e) {
      System.err.println(e.toString());
      return false;
    }
    return true;
  }
}

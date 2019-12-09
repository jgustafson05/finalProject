package com.example.finalproject;

import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class FileWriter {

  public static boolean writeVariables(Context context, File file, List<Variable> variables) {

    file.delete();
    Log.e("writer", "writing");
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

  public static boolean writeFile(Context context, File file, List<Variable> variables,
                                  @Nullable List<SamplePoint> samplePoints) {

    file.delete();
    Log.e("writer", "writing");
    try {
      file.createNewFile();
    } catch (Exception e) {
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
    fileContentsBuilder.append(";");
    if (samplePoints != null) {
      for (int i = 0; i < samplePoints.size(); i++) {
        StringBuilder newLine = new StringBuilder(samplePoints.get(i).getName()).append(":");
        for (int j = 0; j < variables.size(); j++) {
          try {
            if (variables.get(j).isCategorical()) {
              newLine.append((int) samplePoints.get(i).getValue(variables.get(j)));
            } else {
              newLine.append(samplePoints.get(i).getValue(variables.get(j)));
            }
          } catch (NullPointerException e) {
            newLine.append("n");
          } finally {
            newLine.append(":");
          }
        }
        newLine.deleteCharAt(newLine.length() - 1).append("\n");
        fileContentsBuilder.append(newLine);
      }
    }
    String fileContents = fileContentsBuilder.toString();
    Log.e("writer", fileContents);

    try (FileOutputStream fos = context.openFileOutput(file.getName(), Context.MODE_PRIVATE)) {
      fos.write(fileContents.getBytes());
    } catch (Exception e) {
      System.err.println(e.toString());
      return false;
    }
    return true;
  }

  public static boolean writeCsv(Context context, Uri uri,
                                  List<Variable> variables, List<SamplePoint> samplePoints) {
    try {
      ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "w");
      FileOutputStream fos = new FileOutputStream(pfd.getFileDescriptor());

      StringBuilder fileContentsBuilder = new StringBuilder("Name,");
      for (Variable v : variables) {
        fileContentsBuilder.append(v.getName()).append(",");
      }
      fileContentsBuilder.deleteCharAt(fileContentsBuilder.length() - 1);
      fileContentsBuilder.append("\n");
      for (SamplePoint s : samplePoints) {
        fileContentsBuilder.append(s.getName()).append(",");
        for (Variable v : variables) {
          if (!s.valueIsSet(v)) {
            fileContentsBuilder.append("");
          } else if (v.isCategorical()) {
            fileContentsBuilder.append(v.getCategories().get((int) s.getValue(v)));
          } else {
            fileContentsBuilder.append(s.getValue(v));
          }
          fileContentsBuilder.append(",");
        }
        fileContentsBuilder.deleteCharAt(fileContentsBuilder.length() - 1);
        fileContentsBuilder.append("\n");
      }
      Log.e("csv_contents", fileContentsBuilder.toString());
      fos.write(fileContentsBuilder.toString().getBytes());
      fos.close();
      pfd.close();
      return true;
    } catch (Exception e) {
      Log.e("writer", "stack trace coming");
      Log.e("writer", Log.getStackTraceString(e));
      return false;
    }
  }
}
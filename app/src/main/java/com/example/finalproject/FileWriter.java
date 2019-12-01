package com.example.finalproject;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class FileWriter {

  public static boolean writeVariables(Context context, File file, List<Variable> variables) {
    String filename = file.getName();
    file.delete();
    try {
      file.createNewFile();
    } catch (Exception e) {
      System.err.println(e.toString());
      return false;
    }

    StringBuilder fileContentsBuilder = new StringBuilder("variables:\n");
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

    try (FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE)) {
      fos.write(fileContents.getBytes());
    } catch (Exception e) {
      System.err.println(e.toString());
      return false;
    }
    return true;
  }

}

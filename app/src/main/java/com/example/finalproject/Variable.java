package com.example.finalproject;

import androidx.annotation.NonNull;

import java.util.List;

public class Variable {
  private String varName;
  private String varType;
  private List varCategories;

  Variable(@NonNull final String name,
           @NonNull final String type,
           @NonNull final List<String> categories) {
    if (type != "categorical" || type != "quantitative") {
      throw new IllegalArgumentException();
    }
    varName = name;
    varType = type;
    varCategories = categories;
  }
}

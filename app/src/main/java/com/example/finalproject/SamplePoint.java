package com.example.finalproject;

import java.util.ArrayList;
import java.util.List;

public class SamplePoint {

  protected class Value {

    private boolean categorical;

    private int category;

    private double measurement;

    Value(int sampleCategory) {
      category = sampleCategory;
      categorical = true;
    }

    Value(double sampleMeasurement) {
      measurement = sampleMeasurement;
      categorical = false;
    }

    private boolean isCategorical() {
      return categorical;
    }

    private double getValue() {
      if (categorical) {
        return category;
      }
      return measurement;
    }
  }

  private String name;

  private List<Value> values;

  SamplePoint(String pointName, int variableCount, double responseMeasurement) {
    name = pointName;
    values = new ArrayList<>(variableCount);
    values.set(0, new Value(responseMeasurement));
  }

  SamplePoint(String pointName, int variableCount, int responseCategory) {
    name = pointName;
    values = new ArrayList<>(variableCount);
    values.set(0, new Value(responseCategory));
  }

  /**
   * Returns the number of the category at the given index.
   * @param index index of the value to be found
   * @return the number of the category at this index
   * @throws IllegalStateException if this value is not categorical
   * @throws NullPointerException if this value has not been defined
   */
  public int getCategory(int index) {
    try {
      if (values.get(index).isCategorical()) {
        throw new IllegalStateException("The value is not categorical.");
      }
      return (int) values.get(index).getValue();
    } catch (NullPointerException e) {
      throw new NullPointerException("This value has not been defined");
    }
  }

  /**
   * Returns the measured value at the given index.
   * @param index index of the value to be found
   * @return the measured value at this index
   * @throws IllegalStateException if this value is not quantitative
   * @throws NullPointerException if this value has not been defined
   */
  public double getMeasurement(int index) {
    try {
      if (values.get(index).isCategorical()) {
        throw new IllegalStateException("This variable is not quantitative.");
      }
      return values.get(index).getValue();
    } catch (NullPointerException e) {
      throw new NullPointerException("This value has not been defined");
    }
  }

  public void setValue(int index, int sampleCategory) {
    try {
      values.set(index, new Value(sampleCategory));
    } catch (IndexOutOfBoundsException e) {
      throw new IndexOutOfBoundsException("There is no variable for this index");
    }
  }

  public void addVariable(int index) {
    values.add(index, null);
  }

  public boolean valueIsSet(int index) {
    return (values.get(index) != null);
  }

  public String getName() {
    return name;
  }

  public void setName(String newName) {
    name = newName;
  }

}

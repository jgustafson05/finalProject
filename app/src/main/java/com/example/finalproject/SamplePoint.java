package com.example.finalproject;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Represents a point from the sample.
 * does not check variables list for errors
 * must be updated when variables are added or removed */
public class SamplePoint implements Comparable<SamplePoint>, Parcelable {

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

    private double getValue() {
      if (categorical) {
        return category;
      }
      return measurement;
    }
  }

  public static final Parcelable.Creator<SamplePoint> CREATOR
          = new Parcelable.Creator<SamplePoint>() {
            public SamplePoint createFromParcel(Parcel in) {
              try {
                SamplePoint point = new SamplePoint(in.readString());
                Parcelable[] variables = in.readParcelableArray(getClass().getClassLoader());
                double[] doubles = in.createDoubleArray();
                for (int i = 0; i < variables.length; i++) {
                  Variable v = (Variable) variables[i];
                  if (v.isCategorical()) {
                    point.setValue(v, doubles[i]);
                  } else {
                    point.setValue(v, (int) doubles[i]);
                  }
                }
                return point;
              } catch (NullPointerException e) {
                throw new InvalidParameterException();
              }
            }

            public SamplePoint[] newArray(int size) {
              return new SamplePoint[size];
            }
          };

  private String name;

  private Map<Variable, Value> valueMap;

  SamplePoint(@NonNull String pointName) {
    name = pointName;
    valueMap = new HashMap<>();
  }

  SamplePoint(@NonNull String pointName, @NonNull Variable responseVariable,
              double responseMeasurement) {
    name = pointName;
    valueMap = new HashMap<>();
    valueMap.put(responseVariable, new Value(responseMeasurement));
  }

  SamplePoint(@NonNull String pointName, @NonNull Variable responseVariable, int responseCategory) {
    name = pointName;
    valueMap = new HashMap<>();
    valueMap.put(responseVariable, new Value(responseCategory));
  }

  /**
   * Returns the category or measurement related to this variable for this SamplePoint.
   * @param relatedVariable variable to search for
   * @return the category or measurement of this variable
   * @throws NullPointerException if the variable has not been defined for this SamplePoint
   */
  public double getValue(Variable relatedVariable) throws NullPointerException {
    try {
      return valueMap.get(relatedVariable).getValue();
    } catch (NullPointerException e) {
      throw new NullPointerException("This value has not been defined");
    }
  }

  /**
   * Sets the value of a categorical variable for this SamplePoint.
   * @param relatedVariable the variable for which the value was found
   * @param sampleCategory the category of the variable for this SamplePoint
   */
  public void setValue(Variable relatedVariable, int sampleCategory) {
    valueMap.put(relatedVariable, new Value(sampleCategory));
  }

  /**
   * Sets the value of a quantitative variable for this SamplePoint.
   * @param relatedVariable the variable for which the value was found
   * @param sampleMeasurement the measurement of the variable for this SamplePoint
   */
  public void setValue(Variable relatedVariable, double sampleMeasurement) {
    valueMap.put(relatedVariable, new Value(sampleMeasurement));
  }

  public int compareTo(@NonNull SamplePoint point) {
    return name.compareTo(point.getName());
  }

  public int describeContents() {
    return 0;
  }

  public void writeToParcel(Parcel out, int flags) {
    out.writeString(name);
    Set<Map.Entry<Variable, Value>> entries = valueMap.entrySet();
    List<Variable> variables = new ArrayList<>();
    List<Value> values = new ArrayList<>();
    for (Map.Entry<Variable, Value> entry : entries) {
      variables.add(entry.getKey());
      values.add(entry.getValue());
    }
    double[] doubles = new double[values.size()];
    Variable[] varArray = new Variable[variables.size()];
    for (int i = 0; i < values.size(); i++) {
      doubles[i] = values.get(i).getValue();
      varArray[i] = variables.get(i);
    }
    out.writeParcelableArray(varArray, 0);
    out.writeDoubleArray(doubles);
  }

  public void removeVariable(Variable toRemove) {
    valueMap.remove(toRemove);
  }

  public boolean valueIsSet(Variable relatedVariable) {
    return (valueMap.get(relatedVariable) != null);
  }

  public String getName() {
    return name;
  }

  public void setName(String newName) {
    name = newName;
  }
}

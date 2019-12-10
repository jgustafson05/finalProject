package com.example.finalproject;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/** Represents a variable used in the survey. */
public class Variable implements Comparable<Variable>, Parcelable {

  /** Name of the variable. */
  private String name;

  /** If true, the variable is categorical. Otherwise it is quantitative. */
  private boolean categorical;

  /** The list of categories for a categorical variable. */
  private ArrayList<String> categories;

  public static final Parcelable.Creator<Variable> CREATOR
      = new Parcelable.Creator<Variable>() {

        public Variable createFromParcel(Parcel in) {
          try {
            Variable created = new Variable(in.readString(), in.readInt() == 1);
            if (created.isCategorical()) {
              for (String s : in.createStringArray()) {
                created.addCategory(s);
              }
            }
            return created;
          } catch (NullPointerException e) {
            throw new InvalidParameterException();
          }
        }

        public Variable[] newArray(int size) {
          return new Variable[size];
        }
      };

  /**
   * Creates a new variable for the survey.
   * @param varName name of the variable
   * @param isCategorical is a categorical variable or else is quantitative
   */
  Variable(@NonNull final String varName, final boolean isCategorical) {
    name = varName;
    categorical = isCategorical;
    if (isCategorical) {
      categories = new ArrayList<>();
    }
  }

  public int compareTo(Variable other) {
    return name.compareTo(other.getName());
  }

  /**
   * Adds a new category to the categories list.
   * @param category the new category
   * @return false if the list already contained this category
   * @throws NullPointerException if this variable is not categorical
   */
  public boolean addCategory(String category) throws NullPointerException {
    if (!categorical) {
      throw new NullPointerException("A quantitative variable does not have categories.");
    }
    for (int i = 0; i < categories.size(); i++) {
      if (category.compareTo(categories.get(i)) <= 0) {
        if (category.compareTo(categories.get(i)) == 0)  {
          return false;
        }
        categories.add(i, category);
        return true;
      }
    }
    return categories.add(category);
  }

  /**
   * Removes the specified category from the categories list.
   * @param category the category to be removed
   * @return true if the list contained the category
   * @throws NullPointerException if this variable is not categorical
   */
  public boolean removeCategory(String category) throws NullPointerException {
    if (!categorical) {
      throw new NullPointerException("A quantitative variable does not have categories.");
    }
    return categories.remove(category);
  }


  /**
   * Returns the list of categories.
   * @return the list of categories or null if it does not exist
   */
  public List<String> getCategories() {
    return categories;
  }

  public Variable copy() {
    Variable toReturn = new Variable(name, categorical);
    if (toReturn.isCategorical()) {
      for (String s : categories) {
        toReturn.addCategory(s);
      }
    }
    return toReturn;
  }

  public String getName() {
    return name;
  }

  public void setName(@NonNull String newName) {
    name = newName;
  }

  public boolean isCategorical() {
    return categorical;
  }

  public void setCategorical(boolean isCategorical) {
    categorical = isCategorical;
    if (isCategorical) {
      categories = new ArrayList<>();
    } else {
      categories = null;
    }
  }

  public void writeToParcel(Parcel out, int flags) {
    out.writeString(name);
    out.writeInt(isCategorical() ? 1 : 0);
    if (isCategorical()) {
      out.writeStringList(getCategories());
    }
  }

  public int describeContents() {
    return 0;
  }

  @Override
  public int hashCode() {
    int hash = 29;
    int adjust = 8;
    if (categorical) {
      adjust = 0;
    }
    hash = 31 * hash + adjust;
    hash = 31 * hash + (name == null ? 0 : name.hashCode());
    if (categorical) {
      for (String category : categories) {
        hash = 31 * hash + category.hashCode();
      }
    }
    return hash;
  }

}

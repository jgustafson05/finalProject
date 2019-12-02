package com.example.finalproject;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/** Represents a variable used in the survey. */
public class Variable implements Comparable<Variable> {

  /** Name of the variable. */
  private String name;

  /** If true, the variable is categorical. Otherwise it is quantitative. */
  private boolean categorical;

  /** The list of categories for a categorical variable. */
  private ArrayList<String> categories;

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
    for (int i = 1; i < categories.size(); i++) {
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
   * Moves category to front of the list as default and puts old default back in lexographic order.
   * @param index index of the new default category
   * @return true if the array is large enough to set a default
   * @throws NullPointerException if this variable is not categorical
   * @throws IndexOutOfBoundsException if the index is out of range
   */
  public boolean setDefaultCategory(int index)
          throws NullPointerException, IndexOutOfBoundsException {
    if (!categorical) {
      throw new NullPointerException("A quantitative variable does not have categories.");
    }
    if (categories.size() == 0 || categories.size() == 1) {
      return false;
    }
    categories.add(0, categories.remove(index));
    for (int i = 2; i < categories.size(); i++) {
      if (categories.get(1).compareTo(categories.get(i)) <= 0) {
        categories.add(i, categories.remove(1));
        return true;
      }
    }
    return categories.add(categories.remove(1));
  }

  /**
   * Returns the list of categories.
   * @return the list of categories or null if it does not exist
   */
  public List<String> getCategories() {
    return categories;
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

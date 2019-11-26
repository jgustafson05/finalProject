package com.example.finalproject;

import androidx.annotation.NonNull;

import java.util.LinkedList;
import java.util.List;

/** Represents a variable used in the survey, does not modify the file. */
public class Variable {

  /** Name of the variable. */
  private String name;

  /** If true, the variable is categorical. Otherwise it is quantitative. */
  private boolean categorical;

  /** The list of categories for a categorical variable. */
  private LinkedList<String> categories;

  /**
   * Creates a new variable for the survey.
   * @param varName name of the variable
   * @param isCategorical is a categorical variable or else is quantitative
   */
  Variable(@NonNull final String varName, final boolean isCategorical) {
    name = varName;
    categorical = isCategorical;
    if (isCategorical) {
      categories = new LinkedList<>();
    }
  }

  /**
   * Adds a new category to the categories list.
   * @param category the new category
   * @return false if the list already contained this category
   * @throws NullPointerException if this variable is not categorical
   */
  public boolean addCategory(String category) {
    if (!categorical) {
      throw new NullPointerException("A quantitative variable does not have categories.");
    }
    if (categories.contains(category)) {
      return false;
    }
    categories.add(category);
    return true;
  }

  /**
   * Removes the specified category from the categories list.
   * @param category the category to be removed
   * @return true if the list contained the category
   * @throws NullPointerException if this variable is not categorical
   */
  public boolean removeCategory(String category) {
    if (!categorical) {
      throw new NullPointerException("A quantitative variable does not have categories.");
    }
    return categories.remove(category);
  }

  /**
   * Returns the list of categories.
   * @return the list of categories
   * @throws NullPointerException if this variable is not categorical
   */
  public List<String> getCategories() {
    if (!categorical) {
      throw new NullPointerException("A quantitative variable does not have categories.");
    }
    return categories;
  }

  public String getName() {
    return name;
  }

  public void setName(String newName) {
    name = newName;
  }

  public boolean isCategorical() {
    return categorical;
  }

}

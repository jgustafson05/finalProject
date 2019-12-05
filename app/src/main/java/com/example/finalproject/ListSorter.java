package com.example.finalproject;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class ListSorter {

  public static List<SamplePoint> mergeSort(@NonNull List<SamplePoint> list) {
    return sort(list);
  }

  private static List<SamplePoint> sort(List<SamplePoint> list) {
    if (list.isEmpty() || list.size() == 1) {
      return list;
    }
    int middle = (list.size() - 1) / 2;
    List<SamplePoint> left = sort(list.subList(0, middle));
    List<SamplePoint> right = sort(list.subList(middle + 1, list.size() - 1));
    int i = 0;
    int j = 0;
    List<SamplePoint> toReturn = new ArrayList<>();
    while (i <= left.size() && j <= right.size()) {
      if (left.get(i).compareTo(right.get(j)) <= 0) {
        toReturn.add(left.get(i));
        i++;
        continue;
      }
      toReturn.add(right.get(j));
      j++;
    }
    return toReturn;
  }
}

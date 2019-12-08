package com.example.finalproject;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.activity.ComponentActivity;
import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SampleItemFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SampleItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SampleItemFragment extends Fragment {
  private static final String ARG_SAMPLE_POINT = "sample_point";

  private int key;
  private SamplePoint point;
  private Variable[] variables;

  private OnFragmentInteractionListener mListener;

  public SampleItemFragment() {
    // Required empty public constructor
  }

  public static SampleItemFragment newInstance(Variable[] variableArray, SamplePoint samplePoint,
                                               int hashCode) {
    SampleItemFragment fragment = new SampleItemFragment();
    Bundle args = new Bundle();
    args.putParcelableArray("variables", variableArray);
    args.putParcelable("point", samplePoint);
    args.putInt("key", hashCode);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      point = getArguments().getParcelable("point");
      variables = (Variable[]) getArguments().getParcelableArray("variables");
      key = getArguments().getInt("key");
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_sample_item, container, false);
    LinearLayout variableHolder = view.findViewById(R.id.fragmentVariables);

    for (int i = 0; i < variables.length; i++) {
      View variableChunk = inflater.inflate(R.layout.chunk_dialog_variable, variableHolder);

      // TODO: Add functionality

      variableHolder.addView(variableChunk);
    }


    return view;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof OnFragmentInteractionListener) {
      mListener = (OnFragmentInteractionListener) context;
    } else {
      throw new RuntimeException(context.toString()
              + " must implement OnFragmentInteractionListener");
    }
  }


  public void returnData() {
    mListener.updateSamplePoint(point, key);
  }


  @Override
  public void onDetach() {
    super.onDetach();
    mListener = null;
  }

  public void setOnFragmentInteractionListener(OnFragmentInteractionListener callback) {
    mListener = callback;
  }

  public interface OnFragmentInteractionListener {
    // TODO: Update argument type and name
    void updateSamplePoint(SamplePoint updatedPoint, int key);
    void updateVariableCategories(String category, int index);
  }
}

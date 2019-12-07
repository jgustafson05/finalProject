package com.example.finalproject;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


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

  private SamplePoint point;
  private Variable[] variables;

  private OnFragmentInteractionListener mListener;

  public SampleItemFragment() {
    // Required empty public constructor
  }

  public static SampleItemFragment newInstance(Variable[] variableArray, SamplePoint samplePoint) {
    SampleItemFragment fragment = new SampleItemFragment();
    Bundle args = new Bundle();
    args.putParcelableArray("variables", variableArray);
    args.putParcelable("point", samplePoint);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      point = getArguments().getParcelable("point");
      variables = (Variable[]) getArguments().getParcelableArray("variables");
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_sample_item, container, false);
  }

  public void onButtonPressed(Uri uri) {
    if (mListener != null) {
      mListener.onFragmentInteraction(uri);
    }
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
    void onFragmentInteraction(Uri uri);
  }
}

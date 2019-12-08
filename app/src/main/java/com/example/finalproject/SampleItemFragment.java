package com.example.finalproject;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
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
      View variableChunk = inflater.inflate(R.layout.chunk_dialog_variable, variableHolder, false);

      TextView variableName = variableChunk.findViewById(R.id.chunkVariableName);
      Spinner variableCategories = variableChunk.findViewById(R.id.chunkCategories);
      EditText variableValue = variableChunk.findViewById(R.id.chunkVariableValue);
      final int index = i;

      variableName.setText(variables[i].getName());
      if (variables[i].isCategorical()) {
        variableValue.setVisibility(View.GONE);
        ArrayList<String> categoriesList = new ArrayList<>(variables[i].getCategories());
        categoriesList.add(0, "");
        categoriesList.add("(Add new category)");

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, new ArrayList<>(categoriesList));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        variableCategories.setAdapter(adapter);

        variableCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
          @Override
          public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (position == categoriesList.size() - 1) {
              createNewCategoryDialog(variables[index], index);
            } else if (position > 0) {
              point.setValue(variables[index], position);
            }
          }

          @Override
          public void onNothingSelected(AdapterView<?> parent) {
          }
        });
      } else {
        variableCategories.setVisibility(View.GONE);
        variableValue.setOnEditorActionListener(new TextView.OnEditorActionListener() {
          @Override
          public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            switch (actionId) {
              case EditorInfo.IME_ACTION_DONE:
              case EditorInfo.IME_ACTION_NEXT:
              case EditorInfo.IME_ACTION_PREVIOUS:
                point.setValue(variables[index], Double.parseDouble(v.getText().toString()));
                return true;
              default:
                return false;
            }
          }
        });
      }

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

  private void createNewCategoryDialog(Variable categoricalVariable, int index)
          throws IllegalArgumentException {

    if (!categoricalVariable.isCategorical()) {
      throw new IllegalArgumentException("variable is not categorical");
    }

    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
    View view = getLayoutInflater().inflate(R.layout.dialog_new_file, null);
    TextView prompt = view.findViewById(R.id.filePrompt);
    prompt.setHint("Category name");
    builder.setView(view);

    // Making a dialog to request title for survey
    builder.setTitle("New Category")
            .setPositiveButton(R.string.dialog_create, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(final DialogInterface dialog, final int which) {
                Dialog dialogView = (Dialog) dialog;
                TextView dialogPrompt = dialogView.findViewById(R.id.filePrompt);
                if (dialogPrompt.getText().toString().equals("")) {
                  //Alert user somehow
                  return;
                }
                String categoryName = dialogPrompt.getText().toString();
                for (int i = 0; i < categoricalVariable.getCategories().size(); i++) {
                  if (categoricalVariable.getCategories().get(i).equals(categoryName)) {
                    Toast.makeText(requireContext().getApplicationContext(),
                            "The variable already has this category", Toast.LENGTH_SHORT).show();
                    return;
                  }
                }
                categoricalVariable.addCategory(categoryName);
                mListener.updateVariableCategories(categoryName, index);
              }
            })
            .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(final DialogInterface dialog, final int which) {
              }
            });
    builder.show();
  }

  @Override
  public void onDetach() {
    super.onDetach();
    returnData();
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

package com.example.finalproject;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

  private List<String> forbidSpinner = new ArrayList<>();
  private int key;
  private SamplePoint point;
  private Variable[] variables;

  private OnFragmentInteractionListener interactionListener;

  public SampleItemFragment() {
    // Required empty public constructor
  }

  public static SampleItemFragment newInstance(Variable[] variableArray, SamplePoint samplePoint,
                                               int hashCode) {
    Bundle args = new Bundle();
    args.putParcelableArray("variables", variableArray);
    args.putParcelable("point", samplePoint);
    args.putInt("key", hashCode);

    SampleItemFragment fragment = new SampleItemFragment();
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

    // Button Listener
    Button returnToActivity = view.findViewById(R.id.returnToActivity);
    returnToActivity.setOnClickListener(unused -> returnData());

    updateLayout(view);
    return view;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof OnFragmentInteractionListener) {
      interactionListener = (OnFragmentInteractionListener) context;
    } else {
      throw new RuntimeException(context.toString()
              + " must implement OnFragmentInteractionListener");
    }
  }

  private void updateLayout(View fragmentView) {

    if (fragmentView == null) {
      fragmentView = getView();
    }
    LinearLayout variableHolder = fragmentView.findViewById(R.id.fragmentVariables);
    variableHolder.setVisibility(View.GONE);
    variableHolder.removeAllViews();

    for (int i = 0; i < variables.length; i++) {
      View variableChunk = getLayoutInflater().inflate(R.layout.chunk_dialog_variable,
              variableHolder, false);

      TextView variableName = variableChunk.findViewById(R.id.chunkVariableName);
      Spinner variableCategories = variableChunk.findViewById(R.id.chunkCategories);
      EditText variableValue = variableChunk.findViewById(R.id.chunkVariableValue);
      final int index = i;

      variableName.setText(variables[i].getName());

      if (variables[i].isCategorical()) {
        variableValue.setVisibility(View.GONE);
        ArrayList<String> categoriesList = new ArrayList<>(variables[i].getCategories());
        if (categoriesList.isEmpty()) {
          categoriesList.add(0, "");
        }
        categoriesList.add("(Add new category)");

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, new ArrayList<>(categoriesList));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        variableCategories.setAdapter(adapter);
        if (point.valueIsSet(variables[i])) {
          variableCategories.setSelection((int) point.getValue(variables[i]));
        }

        variableCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
          @Override
          public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (position == categoriesList.size() - 1) {
              createNewCategoryDialog(index, (categoriesList.get(0).equals("")));
            } else {
              point.setValue(variables[index], position);
            }
          }

          @Override
          public void onNothingSelected(AdapterView<?> parent) {
          }
        });
      } else {
        variableCategories.setVisibility(View.GONE);
        if (point.valueIsSet(variables[i])) {
          variableValue.setText(Double.toString((point.getValue(variables[i]))));
        }
        variableValue.setOnEditorActionListener(new TextView.OnEditorActionListener() {
          @Override
          public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            switch (actionId) {
              case EditorInfo.IME_ACTION_DONE:
              case EditorInfo.IME_ACTION_NEXT:
              case EditorInfo.IME_ACTION_PREVIOUS:
                if (v.getText().toString().equals("")) {
                  point.setValue(variables[index], 0.0);
                  v.setText("0.0");
                } else {
                  point.setValue(variables[index], Double.parseDouble(v.getText().toString()));
                  Log.e("eee", v.getText().toString());
                }
                v.clearFocus();
                return false;
              default:
                return false;
            }
          }
        });
      }
      variableHolder.addView(variableChunk);
    }
    variableHolder.setVisibility(View.VISIBLE);
  }


  public void returnData() {
    for (Variable v : variables) {
      if (v.isCategorical() && !point.valueIsSet(v) && !v.getCategories().isEmpty()) {
        point.setValue(v, 0);
      }
    }
    interactionListener.updateSamplePoint(this, point, key);
  }

  private void createNewCategoryDialog(int variableIndex, boolean first)
          throws IllegalArgumentException {

    if (!variables[variableIndex].isCategorical()) {
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
                for (int i = 0; i < variables[variableIndex].getCategories().size(); i++) {
                  if (variables[variableIndex].getCategories().get(i).equals(categoryName)) {
                    Toast.makeText(requireContext().getApplicationContext(),
                            "The variable already has this category", Toast.LENGTH_SHORT).show();
                    updateLayout(null);
                    return;
                  }
                }
                Variable updated = variables[variableIndex].copy();
                updated.addCategory(categoryName);

                if (!variables[variableIndex].getCategories().isEmpty()) {
                  final String selected = variables[variableIndex].getCategories()
                          .get((int) point.getValue(variables[variableIndex]));
                  point.setValue(updated, updated.getCategories().indexOf(selected));
                } else {
                  point.setValue(updated, 0);
                }
                interactionListener.addVariableCategory(updated, variableIndex);
                variables[variableIndex] = updated;


                updateLayout(null);
              }
            })
            .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(final DialogInterface dialog, final int which) {
              }
            });
    builder.show();
  }

  public void onPause() {
    super.onPause();
    returnData();
  }

  @Override
  public void onStop() {
    super.onStop();
    returnData();
  }

  @Override
  public void onDetach() {
    super.onDetach();
    interactionListener = null;
  }

  public void setOnFragmentInteractionListener(OnFragmentInteractionListener callback) {
    interactionListener = callback;
  }

  public interface OnFragmentInteractionListener {
    void updateSamplePoint(SampleItemFragment thisFragment, SamplePoint updatedPoint, int key);

    void addVariableCategory(Variable updated, int index);
  }
}

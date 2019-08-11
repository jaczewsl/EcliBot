package com.example.test;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;

public class DialogFragment extends androidx.fragment.app.DialogFragment implements NumberPicker.OnValueChangeListener{

    private int iteration;      // holds hom many times the loop of instructions will be executed

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_dialog, container, false);         // inflate fragment_dialog view
        getDialog().setTitle("Simple Dialog");                                                          // sets title for Dialog

        final NumberPicker np = rootView.findViewById(R.id.numberPicker);
        np.setMinValue(1);                                                  // sets minimal value
        np.setMaxValue(5);                                                  // sets maximum value
        np.setOnValueChangedListener(this);                                 // calls onValue

        Button ok = rootView.findViewById(R.id.btn_ok);                     // finds the views from the layout resource file

        ok.setOnClickListener(new View.OnClickListener() {                  // sets button listener
            @Override
            public void onClick(View v) {
                iteration = np.getValue();                                  // when ok is clicked the value from number picker
                dismiss();                                                  // will be assigned to iteration variable and then number picker will be closed
            }
        });

        return rootView;                                                    // shows/displays View of the NumberPicker
    }

    @Override
    public void onValueChange(NumberPicker numberPicker, int i, int i1) {
        iteration = numberPicker.getValue();                                // each time value is changed is automatically assigned to iteration variable
    }

    public int getIterationNum(){                                           // returns iteration value
        return iteration;
    }
}


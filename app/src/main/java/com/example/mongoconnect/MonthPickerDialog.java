package com.example.mongoconnect;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.NumberPicker;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;


// monthpicker dialog box (Alert Dialog Box) i.e. custom date picker
public class MonthPickerDialog extends DialogFragment {

    public static final int MAX_YEAR = 2100;
    private DatePickerDialog.OnDateSetListener listener;

    public void setListener(DatePickerDialog.OnDateSetListener listener) {

        this.listener = listener;

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        Calendar cal = Calendar.getInstance();

        View dialog = inflater.inflate(R.layout.activity_month_picker_dialog, null);
        final NumberPicker monthPicker = (NumberPicker) dialog.findViewById(R.id.monthpicker);
        final NumberPicker yearPicker = (NumberPicker) dialog.findViewById(R.id.yearpicker);

        monthPicker.setMaxValue(12);
        monthPicker.setMinValue(1);
        monthPicker.setValue(1);

        yearPicker.setMinValue(1900);
        yearPicker.setMaxValue(MAX_YEAR);
        yearPicker.setValue(1970);

        builder.setView(dialog)
        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onDateSet(null, yearPicker.getValue(), monthPicker.getValue(), 0);

            }
        })
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MonthPickerDialog.this.getDialog().cancel();
            }
        });

        return builder.create();


    }

}

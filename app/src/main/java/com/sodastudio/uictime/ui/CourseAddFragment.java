package com.sodastudio.uictime.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sodastudio.uictime.R;
import com.sodastudio.uictime.manager.ScheduleManager;
import com.sodastudio.uictime.model.DetailCourse;

/**
 * Created by Jun on 7/27/2017.
 */

public class CourseAddFragment extends DialogFragment {

    private int mTerm;          // Fall 2017
    private String mSubject;    // CS
    private int mNumber;        // 141
    private String mTitle;      // Program Desgin II
    private String mCredits;    // 3 Hours
    private int mCRN;
    private String mType;
    private String mDays;
    private String mTime;
    private String mRoom;
    private String mInstructor;

    private EditText subjectText;
    private EditText numberText;
    private EditText titleText;
    private Spinner mCreditSpinner;
    private CheckBox mondayCheckbox;
    private CheckBox tuesdayCheckbox;
    private CheckBox wednesdayCheckbox;
    private CheckBox thursdayCheckbox;
    private CheckBox fridayCheckbox;
    private TextView timeText;
    private EditText roomText;
    private EditText instructorText;

    private Button cancelButton;
    private Button addButton;

    private ArrayAdapter mAdapter;
    private ScheduleManager mScheduleManager;
    private Toast mToast;
    private AlertDialog mAlertDialog;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_course_add, null);

        subjectText = (EditText)v.findViewById(R.id.subject_add_text);
        numberText = (EditText)v.findViewById(R.id.number_add_text);
        titleText = (EditText)v.findViewById(R.id.title_add_text);
        mCreditSpinner = (Spinner)v.findViewById(R.id.credit_add_spinner);
        mondayCheckbox = (CheckBox)v.findViewById(R.id.monday_checkbox);
        tuesdayCheckbox = (CheckBox)v.findViewById(R.id.tuesday_checkbox);
        wednesdayCheckbox = (CheckBox)v.findViewById(R.id.wednesday_checkbox);
        thursdayCheckbox = (CheckBox)v.findViewById(R.id.thursday_checkbox);
        fridayCheckbox = (CheckBox)v.findViewById(R.id.friday_checkbox);
        timeText = (TextView)v.findViewById(R.id.time_add_text);
        roomText = (EditText)v.findViewById(R.id.room_add_text);
        instructorText = (EditText)v.findViewById(R.id.instructor_add_text);

        cancelButton = (Button)v.findViewById(R.id.cancel_course_Button);
        addButton = (Button)v.findViewById(R.id.add_course_Button);

        mAdapter = ArrayAdapter.createFromResource(getContext(), R.array.credits, android.R.layout.simple_spinner_dropdown_item);
        mCreditSpinner.setAdapter(mAdapter);

        timeText.setOnClickListener(mListener);
        cancelButton.setOnClickListener(mListener);
        addButton.setOnClickListener(mListener);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.add_course_text)
                .create();
    }

    private View.OnClickListener mListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            int id = v.getId();

            if(id == R.id.cancel_course_Button)
            {
                //showToast("Cancel!");
                getDialog().dismiss();
            }
            else if(id == R.id.add_course_Button)
            {
                if(subjectText.getText().toString().equals("") || numberText.getText().toString().equals("") ||
                        titleText.getText().toString().equals("") || getCheckedDays().equals("") || timeText.getText().toString().equals(""))
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    mAlertDialog = builder.setMessage("Please fill all required blanks.")
                            .setNegativeButton("OK", null)
                            .create();
                    mAlertDialog.show();
                    return;
                }

                //TODO::get all values and create DetailCourse and add to ScheduleManager
                mTerm = CourseListFragment.TERM_ID;
                mSubject = subjectText.getText().toString().toUpperCase();
                mNumber = Integer.valueOf( numberText.getText().toString());
                mTitle = titleText.getText().toString();
                mCredits = mCreditSpinner.getSelectedItem().toString() + " Hours";
                mCRN = 0;
                mType = "";
                mDays = getCheckedDays();
                mTime = timeText.getText().toString();
                mRoom = roomText.getText().toString();
                mInstructor = instructorText.getText().toString();

                DetailCourse detailCourse = new DetailCourse(mTerm, mSubject, mNumber, mTitle,
                        mCredits, mCRN, mType, mDays, mTime, mRoom, mInstructor);

                mScheduleManager = ScheduleManager.getInstance(getActivity());

                if( mScheduleManager.addSchedule(detailCourse) ){

                    // Add toast
                    String text = subjectText.getText().toString() + numberText.getText().toString()
                            + mTitle + " add success!";
                    showToast(text);
                } else {
                    showToast("Course already in schedule!");
                }

                getDialog().dismiss();
            }
            else if(id == R.id.time_add_text)
            {
                //TODO::dialog time picker
                showToast("Time picker!");
            }
        }
    };

    private String getCheckedDays(){
        String days = "";

        if(mondayCheckbox.isChecked())
            days += "M";

        if(tuesdayCheckbox.isChecked())
            days += "T";

        if(wednesdayCheckbox.isChecked())
            days += "W";

        if(thursdayCheckbox.isChecked())
            days += "R";

        if(fridayCheckbox.isChecked())
            days += "F";

        return days;
    }

    private void showToast(String text) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(getContext(), text, Toast.LENGTH_SHORT);
        mToast.show();
    }

}
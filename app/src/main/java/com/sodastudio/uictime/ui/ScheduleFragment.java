package com.sodastudio.uictime.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sodastudio.uictime.R;

/**
 * Created by Jun on 7/26/2017.
 */

public class ScheduleFragment extends Fragment {

    public static final String TAG = "ScheduleFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_schedule_view, container, false);

        return view;

    }

}

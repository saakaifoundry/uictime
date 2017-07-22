package com.sodastudio.uictime.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sodastudio.uictime.CourseManager;
import com.sodastudio.uictime.R;
import com.sodastudio.uictime.model.Course;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by Jun on 7/20/2017.
 */

public class CourseListFragment extends Fragment {

    private static final String COURSE_SELECTOR = "CourseSelector";

    private static final int COURSE_SELECT = 0;

    private RecyclerView mRecyclerView;
    private CourseAdapter mAdapter;

    private Button mselectButton;
    private String mSubject;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.fragment_courselist, container, false);

        mRecyclerView = (RecyclerView)view.findViewById(R.id.course_list_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mselectButton = (Button)view.findViewById(R.id.select_button);
        mselectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                CoursePickerFragment dialog = new CoursePickerFragment();
                dialog.setTargetFragment(CourseListFragment.this, COURSE_SELECT);
                dialog.show(manager, COURSE_SELECTOR);
            }
        });


        updateUI();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode != Activity.RESULT_OK) {
            return;
        }

        if(requestCode == COURSE_SELECT){
            String term = (String)data.getSerializableExtra(CoursePickerFragment.EXTRA_TERM);
            String subject = (String)data.getSerializableExtra(CoursePickerFragment.EXTRA_SUBJECT);

            Toast.makeText(getActivity(), "Selected: " + term + ", " + subject, Toast.LENGTH_SHORT).show();

            //TODO:: Convert 'Computer Science to CS'
            // mSubject = subject;
            mSubject = "CS";
            new BackgroundTask().execute();
        }

    }

    private void updateUI(){

        CourseManager courseManager = CourseManager.getInstance(getActivity());
        List<Course> courses = courseManager.getCourses();

        mAdapter = new CourseAdapter(courses);
        mRecyclerView.setAdapter(mAdapter);
    }

    private class CourseHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        private Course mCourse;
        private TextView subjectText;
        private TextView numberText;
        private TextView titleText;
        private Button addButton;

        private CourseHolder(View itemView){
            super(itemView);

            itemView.setOnClickListener(this);

            subjectText = (TextView)itemView.findViewById(R.id.course_subject_text);
            numberText = (TextView)itemView.findViewById(R.id.course_number_text);
            titleText = (TextView)itemView.findViewById(R.id.course_title_text);
            addButton = (Button)itemView.findViewById(R.id.course_add_button);

            addButton.setVisibility(View.INVISIBLE);
            addButton.setActivated(false);
        }

        private void bindCourse(Course course){
            mCourse = course;

            subjectText.setText(mCourse.getSubject());
            numberText.setText(String.valueOf(mCourse.getNumber()));
            titleText.setText(mCourse.getTitle());
        }

        @Override
        public void onClick(View v) {

            if( addButton.isActivated() )
            {
                addButton.setActivated(false);
                addButton.setVisibility(View.INVISIBLE);
            }
            else
            {
                addButton.setActivated(true);
                addButton.setVisibility(View.VISIBLE);
            }

        }
    }

    private class CourseAdapter extends RecyclerView.Adapter<CourseHolder>{

        private List<Course> mCourses;

        private CourseAdapter(List<Course> courses){
            mCourses = courses;
        }

        @Override
        public CourseHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater
                    .inflate(R.layout.course, parent, false);

            return new CourseHolder(view);
        }

        @Override
        public void onBindViewHolder(CourseHolder holder, int position) {
            Course course = mCourses.get(position);

            holder.bindCourse(course);
        }

        @Override
        public int getItemCount() {

            return mCourses.size();
        }

    }

    class BackgroundTask extends AsyncTask<Void, Void, String>
    {
        String target;

        @Override
        protected void onPreExecute(){
            try {
                target = "http://junlee7.cafe24.com/uictime/CourseList.php?courseSubject=" + URLEncoder.encode(mSubject, "UTF-8");
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... params) {

            try{
                URL url = new URL(target);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                InputStream inputstream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputstream));
                String temp;
                StringBuilder stringBuilder = new StringBuilder();
                while((temp = bufferedReader.readLine()) != null)
                {
                    stringBuilder.append(temp + "\n");
                }

                bufferedReader.close();
                inputstream.close();
                httpURLConnection.disconnect();

                return stringBuilder.toString().trim();

            }catch (Exception e)            {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            try{
                AlertDialog dialog;
                AlertDialog.Builder builder = new AlertDialog.Builder(CourseListFragment.this.getContext());
                dialog = builder.setMessage(result)
                        .setPositiveButton("Ok", null)
                        .create();
                dialog.show();

                //TODO: Update RecyclerView

            } catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
}

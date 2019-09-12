package com.example.dora.huawei;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


import java.io.InputStream;
import org.json.JSONObject;
import org.json.JSONArray;

import android.util.Log;
/**
 * Created by Dora on 3/11/17.
 */

public class ProjectShowCaseFragment extends Fragment {

    private static final String ARG_MESSAGE = "ProjectShowCaseFragment.MESSAGE";
    private String message;
    private MainFragmentCallbacks callbacks;
    private RecyclerView mRecyclerView;
    private ArrayList<Project> projectArray;
    private ProjectAdaptor mAdapter;
    /**
     *
     * @param message Message passed from Activity during creation of a new Fragment instance.
     * @return A new instance of fragment WorkExpienrenceFragment
     */
    public static ProjectShowCaseFragment newInstance(String message) {
        ProjectShowCaseFragment fragment = new ProjectShowCaseFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);
        fragment.setArguments(args);
        return fragment;
    }

    public ProjectShowCaseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        projectArray = new ArrayList<>();
        String json = null;

        try {
            InputStream is = getContext().getAssets().open("test.json");
            Log.d("check inputstream","inputstream execute");
            int size = is.available();
            Log.d("check size",Integer.toString(size));
            byte[] buffer = new byte[size];
            is.read(buffer);
            Log.d("check buffer","read buffer success");
            is.close();
            json = new String(buffer, "UTF-8");

            JSONObject obj = new JSONObject(json);
            Log.d("check JSONObject",obj.toString());

            //JSONObject m_jArry= (JSONObject) obj.get("Work");
            JSONArray m_jArry = obj.getJSONArray("Project");
            Log.d("check m_jArry",m_jArry.toString());

            for (int a = 0; a < m_jArry.length(); a++)
            {
                JSONObject projectitem = (JSONObject) m_jArry.get(a);
                Project s = new Project();
                Log.d("check loop", projectitem.getString("Name"));
                s.setProjectName(projectitem.getString("Name"));
                s.setTime(projectitem.getString("Time"));
                s.setDescription(projectitem.getString("Description"));
                projectArray.add(s);
            }

        } catch (Exception exception){
            Log.d("exception","Exception happens");
        }

        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            message = getArguments().getString(ARG_MESSAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.basic_info_fragment, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.myList);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();
        return view;
    }

    private void updateUI(){
        Log.d("workarraysize",Integer.toString(projectArray.size()));
        mAdapter = new ProjectAdaptor(projectArray);
        mRecyclerView.setAdapter(mAdapter);
    }

    private class ProjectHolder extends RecyclerView.ViewHolder{
        private Project projectList;
        public TextView mNameTextView;
        public TextView mValueTextView;
        public TextView job_descriptionTextView;

        public ProjectHolder(View itemView){
            super(itemView);
            mNameTextView = (TextView) itemView.findViewById(R.id.company);
            mValueTextView = (TextView) itemView.findViewById(R.id.job_period);
            job_descriptionTextView = (TextView) itemView.findViewById(R.id.job_description);

           /* itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(),
                            mScientist.getName() + " clicked!", Toast.LENGTH_SHORT)
                            .show();
                }
            });*/
        }
        public void bindData(Project s){
            projectList = s;
            mNameTextView.setText(s.getProjectName());
            mValueTextView.setText(s.getTime());
            job_descriptionTextView.setText(s.getDescription());
        }
    }

    private class ProjectAdaptor extends RecyclerView.Adapter<ProjectHolder>{
        private ArrayList<Project> projectlist;
        public ProjectAdaptor(ArrayList<Project> list){
            projectlist = list;
        }
        @Override
        public ProjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.recycle_item_work,parent,false);
            return new ProjectHolder(view);
        }
        @Override
        public void onBindViewHolder(ProjectHolder holder, int position) {
            Project s = projectlist.get(position);
            holder.bindData(s);
        }
        @Override
        public int getItemCount() {
            return projectlist.size();
        }
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            callbacks = (MainFragmentCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement MainFragmentCallbacks");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callbacks = null;
    }
}



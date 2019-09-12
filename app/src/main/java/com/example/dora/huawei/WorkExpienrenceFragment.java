package com.example.dora.huawei;

/**
 * Created by Dora on 3/11/17.
 */

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

public class WorkExpienrenceFragment extends Fragment{
    private static final String ARG_MESSAGE = "WorkExpienrenceFragment.MESSAGE";
    private String message;
    private MainFragmentCallbacks callbacks;
    private RecyclerView mRecyclerView;
    private ArrayList<Work> workArray;
    private WorkAdapter mAdapter;
    /**
     *
     * @param message Message passed from Activity during creation of a new Fragment instance.
     * @return A new instance of fragment WorkExpienrenceFragment
     */
    public static WorkExpienrenceFragment newInstance(String message) {
        WorkExpienrenceFragment fragment = new WorkExpienrenceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);
        fragment.setArguments(args);
        return fragment;
    }

    public WorkExpienrenceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        workArray = new ArrayList<>();
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
            JSONArray m_jArry = obj.getJSONArray("Work");
            Log.d("check m_jArry",m_jArry.toString());

            for (int a = 0; a < m_jArry.length(); a++)
            {
                JSONObject workitem = (JSONObject) m_jArry.get(a);
                Work s = new Work();
                Log.d("check loop", workitem.getString("Company"));
                s.setCompany(workitem.getString("Company"));
                s.setTime(workitem.getString("Time"));
                s.setDescription(workitem.getString("Description"));
                workArray.add(s);
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
        Log.d("workarraysize",Integer.toString(workArray.size()));
        mAdapter = new WorkAdapter(workArray);
        mRecyclerView.setAdapter(mAdapter);
    }

    private class WorkHolder extends RecyclerView.ViewHolder{
        private Work workList;
        public TextView mNameTextView;
        public TextView mValueTextView;
        public TextView job_descriptionTextView;

        public WorkHolder(View itemView){
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
        public void bindData(Work s){
            workList = s;
            mNameTextView.setText(s.getCompany());
            mValueTextView.setText(s.getTime());
            job_descriptionTextView.setText(s.getDescription());
        }
    }

    private class WorkAdapter extends RecyclerView.Adapter<WorkHolder>{
        private ArrayList<Work> joblist;
        public WorkAdapter(ArrayList<Work> list){
            joblist = list;
        }
        @Override
        public WorkHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.recycle_item_work,parent,false);
            return new WorkHolder(view);
        }
        @Override
        public void onBindViewHolder(WorkHolder holder, int position) {
            Work s = joblist.get(position);
            holder.bindData(s);
        }
        @Override
        public int getItemCount() {
            return joblist.size();
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

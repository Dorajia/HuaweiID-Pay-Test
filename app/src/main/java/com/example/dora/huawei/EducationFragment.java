package com.example.dora.huawei;

/**
 * Created by Dora on 3/11/17.
 */

import android.app.Activity;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.dora.huawei.technicalSkillFragment.GridSpacingItemDecoration;


import java.util.ArrayList;


import java.io.InputStream;
import org.json.JSONObject;
import org.json.JSONArray;

import android.util.Log;

public class EducationFragment extends Fragment{
    private static final String ARG_MESSAGE = "EducationFragment.MESSAGE";
    private String message;
    private MainFragmentCallbacks callbacks;
    private RecyclerView mRecyclerView;
    private ArrayList<Education> educationArray;
    private EducationAdaptor mAdapter;
    /**
     *
     * @param message Message passed from Activity during creation of a new Fragment instance.
     * @return A new instance of fragment WorkExpienrenceFragment
     */
    public static EducationFragment newInstance(String message) {
        EducationFragment fragment = new EducationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);
        fragment.setArguments(args);
        return fragment;
    }

    public EducationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        educationArray = new ArrayList<>();
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
            JSONArray m_jArry = obj.getJSONArray("Education");
            Log.d("check m_jArry",m_jArry.toString());

            for (int a = 0; a < m_jArry.length(); a++)
            {
                JSONObject educationitem = (JSONObject) m_jArry.get(a);
                Education s = new Education();
                Log.d("check loop", educationitem.getString("School"));
                s.setUniversity(educationitem.getString("School"));
                s.setTime(educationitem.getString("Time"));
                s.setDegree(educationitem.getString("Degree"));
                educationArray.add(s);
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
/*        Drawable mDivider;
        mDivider = this.getResources().getDrawable(R.drawable.line_divider);

        RecyclerViewDivider firstDivider = RecyclerViewDivider.with(getActivity())
                .drawable(mDivider)
                .size(6)
                .build();

        firstDivider.addTo(mRecyclerView);*/

        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        updateUI();
        return view;
    }

    private void updateUI(){
        Log.d("workarraysize",Integer.toString(educationArray.size()));
        mAdapter = new EducationAdaptor(educationArray);
        mRecyclerView.setAdapter(mAdapter);
    }

    private class EducationHolder extends RecyclerView.ViewHolder{
        private Education educationList;
        public TextView mNameTextView;
        public TextView mValueTextView;
        public TextView job_descriptionTextView;

        public EducationHolder(View itemView){
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
        public void bindData(Education s){
            educationList = s;
            mNameTextView.setText(s.getDegree());
            mValueTextView.setText(s.getTime());
            job_descriptionTextView.setText(s.getUniversity());
        }
    }

    private class EducationAdaptor extends RecyclerView.Adapter<EducationHolder>{
        private ArrayList<Education> educationlist;
        public EducationAdaptor(ArrayList<Education> list){
            educationlist = list;
        }
        @Override
        public EducationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.recycle_item_work,parent,false);
            return new EducationHolder(view);
        }
        @Override
        public void onBindViewHolder(EducationHolder holder, int position) {
            Education s = educationlist.get(position);
            holder.bindData(s);
        }
        @Override
        public int getItemCount() {
            return educationlist.size();
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

    public int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callbacks = null;
    }

}

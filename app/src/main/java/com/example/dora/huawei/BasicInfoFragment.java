package com.example.dora.huawei;

/**
 * Created by Dora on 3/10/17.
 */

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.graphics.Color;
import com.fondesa.recyclerviewdivider.RecyclerViewDivider;

import java.util.ArrayList;
import java.io.InputStream;
import org.json.JSONObject;

import android.widget.TextView;
import android.util.Log;


public class BasicInfoFragment extends Fragment{
    private static final String ARG_MESSAGE = "BasicInfoFragment.MESSAGE";
    private String message;
    private MainFragmentCallbacks callbacks;
    private RecyclerView mRecyclerView;
    private ArrayList<BasicInfo> basicInfor;
    private ScientistAdapter mAdapter;

    /**
     *
     * @param message Message passed from Activity during creation of a new Fragment instance.
     * @return A new instance of fragment BasicInfoFragment
     */
    public static BasicInfoFragment newInstance(String message) {
        BasicInfoFragment fragment = new BasicInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);
        fragment.setArguments(args);
        return fragment;
    }

    public BasicInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("check oncreate","on create execute");

        basicInfor = new ArrayList<>();
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

        RecyclerViewDivider firstDivider = RecyclerViewDivider.with(getActivity())
                .color(Color.GRAY)
                .size(6)
                .build();

        firstDivider.addTo(mRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();
        return view;
    }

    private void updateUI(){
        mAdapter = new BasicInfoFragment.ScientistAdapter(basicInfor);
        mRecyclerView.setAdapter(mAdapter);
    }

    private class ScientistHolder extends RecyclerView.ViewHolder{
        private BasicInfo mScientist;
        public TextView mNameTextView;
        public TextView mValueTextView;

        public ScientistHolder(View itemView){
            super(itemView);
            mNameTextView = (TextView) itemView.findViewById(R.id.textview_attribute);
            mValueTextView = (TextView) itemView.findViewById(R.id.textview_value);
           /* itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(),
                            mScientist.getName() + " clicked!", Toast.LENGTH_SHORT)
                            .show();
                }
            });*/
        }
        public void bindData(BasicInfo s){
            mScientist = s;
            mNameTextView.setText(s.getName());
        }
    }

    private class ScientistAdapter extends RecyclerView.Adapter<BasicInfoFragment.ScientistHolder>{
        private ArrayList<BasicInfo> mScientists;
        public ScientistAdapter(ArrayList<BasicInfo> Scientists){
            mScientists = Scientists;
        }
        @Override
        public BasicInfoFragment.ScientistHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.grid_technical_view,parent,false);
            return new BasicInfoFragment.ScientistHolder(view);
        }
        @Override
        public void onBindViewHolder(BasicInfoFragment.ScientistHolder holder, int position) {
            BasicInfo s = mScientists.get(position);
            holder.bindData(s);
        }
        @Override
        public int getItemCount() {
            return mScientists.size();
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

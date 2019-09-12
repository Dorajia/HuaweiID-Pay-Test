package com.example.dora.huawei;

/**
 * Created by Dora on 3/10/17.
 */

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.DefaultItemAnimator;
import java.util.ArrayList;
import java.io.InputStream;
import org.json.JSONObject;
import org.json.JSONArray;
import android.widget.TextView;
import android.util.Log;
import android.content.res.Resources;
import android.util.TypedValue;



public class technicalSkillFragment extends Fragment{
    private static final String ARG_MESSAGE = "technicalSkillFragment.MESSAGE";
    private String message;
    private MainFragmentCallbacks callbacks;
    private RecyclerView mRecyclerView;
    private tsAdapter mAdapter;
    private ArrayList<SkillSet> skillsets;
    /**
     *
     * @param message Message passed from Activity during creation of a new Fragment instance.
     * @return A new instance of fragment technicalSkillFragment
     */
    public static technicalSkillFragment newInstance(String message) {
        technicalSkillFragment fragment = new technicalSkillFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);
        fragment.setArguments(args);
        return fragment;
    }

    public technicalSkillFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("check oncreate","on create execute");

        skillsets = new ArrayList<>();
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
            JSONArray technicaltool = obj.getJSONArray("TechnicalTool");
            for (int a = 0; a < technicaltool.length(); a++)
            {
                JSONObject skillitem = (JSONObject) technicaltool.get(a);
                SkillSet s = new SkillSet();
                s.setSkill(skillitem.getString("skill"));
                s.setLevel(skillitem.getString("level"));

                skillsets.add(s);
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

        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mAdapter = new technicalSkillFragment.tsAdapter(skillsets);
        mRecyclerView.setAdapter(mAdapter);


        return view;
    }

    private class tsHolder extends RecyclerView.ViewHolder{
        private SkillSet skills;
        public TextView mNameTextView;
        public TextView mValueTextView;

        public tsHolder(View itemView){
            super(itemView);
            mNameTextView = (TextView) itemView.findViewById(R.id.skill);
            mValueTextView = (TextView) itemView.findViewById(R.id.level);
           /* itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(),
                            mScientist.getName() + " clicked!", Toast.LENGTH_SHORT)
                            .show();
                }
            });*/
        }
        public void bindData(SkillSet s){
            skills = s;
            mNameTextView.setText(s.getSkill());
            mValueTextView.setText(s.getLevel());
        }
    }

    public static class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }
    public int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    private class tsAdapter extends RecyclerView.Adapter<technicalSkillFragment.tsHolder>{
        private ArrayList<SkillSet> skillsetlist;
        public tsAdapter(ArrayList<SkillSet> skillList){
            skillsetlist = skillList;
        }
        @Override
        public technicalSkillFragment.tsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.grid_technical_view,parent,false);
            return new technicalSkillFragment.tsHolder(view);
        }
        @Override
        public void onBindViewHolder(technicalSkillFragment.tsHolder holder, int position) {
            SkillSet s = skillsetlist.get(position);
            holder.bindData(s);
        }
        @Override
        public int getItemCount() {
            return skillsetlist.size();
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

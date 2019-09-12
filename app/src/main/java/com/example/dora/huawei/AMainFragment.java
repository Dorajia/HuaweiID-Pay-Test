package com.example.dora.huawei;

import android.app.Activity;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import java.util.ArrayList;
import android.content.Intent;
import java.io.InputStream;
import org.json.JSONObject;
import org.json.JSONArray;
import android.util.Log;
import android.widget.TextView;

public class AMainFragment extends Fragment {
    private static final String ARG_MESSAGE = "AMainFragment.MESSAGE";
    private String message;
    private MainFragmentCallbacks callbacks;
    private String name;
    private String summary;
    private String linkedinURL;
    private String facebookURL;
    private String twitterURL;
    private String githubURL;
    private RecyclerView mRecyclerView;
    private SummaryAdapter mAdapter;
    private ArrayList<String> summaryList = new ArrayList();
    /**
     * Factory method to create a new instance of AMainFragment.
     *
     * @param message Message passed from Activity during creation of a new Fragment instance.
     * @return A new instance of fragment AMainFragment.
     */
    public static AMainFragment newInstance(String message) {
        AMainFragment fragment = new AMainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);
        fragment.setArguments(args);
        return fragment;
    }

    public AMainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            String userName = (String) obj.getString("Name");
            Log.d("check firstname",userName);
            name = userName;
            JSONArray summaryObj= (JSONArray) obj.get("Summary");


            for (int a = 0; a < summaryObj.length(); a++)
            {
                String summaryitem = (String) summaryObj.get(a);
                Log.d("check get summary",summaryitem);
                summaryList.add(summaryitem);
            }


            JSONObject buttons= (JSONObject) obj.get("Social");
            //String[] summaryList = new String[summaryObj.length()];
            linkedinURL = buttons.getString("Linkedin");
            facebookURL = buttons.getString("Facebook");
            githubURL = buttons.getString("Github");
            twitterURL = buttons.getString("Twitter");


        } catch (Exception exception){
            Log.d("exception","Exception happens");
        }

        if (getArguments() != null) {
            message = getArguments().getString(ARG_MESSAGE);
        }
    }

    protected void openWebsite(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW); // Create a new intent - stating you want to 'view something'
        i.setData(Uri.parse(url));  // Add the url data (allowing android to realise you want to open the browser)
        startActivity(i); // Go go go!
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_amain, container, false);
        TextView nameTextview;
        TextView summaryTextview;
        nameTextview = (TextView) view.findViewById(R.id.title);
//        summaryTextview = (TextView) view.findViewById(R.id.summary);
        nameTextview.setText(name);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.Summaryrecycle);
/*        Drawable mDivider;
        mDivider = this.getResources().getDrawable(R.drawable.line_divider);

        RecyclerViewDivider firstDivider = RecyclerViewDivider.with(getActivity())
                .drawable(mDivider)
                .size(6)
                .build();

        firstDivider.addTo(mRecyclerView);*/

        mRecyclerView.addItemDecoration(new technicalSkillFragment.GridSpacingItemDecoration(2, dpToPx(10), true));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
       // summaryTextview.setText(summary);
        // Event handlers
        // Note: Define the onClickListener inline.
        ((ImageButton) view.findViewById(R.id.facebook))
            .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openWebsite(facebookURL);
                }
            });

        ((ImageButton) view.findViewById(R.id.linkedin))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openWebsite(linkedinURL);
                    }
                });
        ((ImageButton) view.findViewById(R.id.github))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openWebsite(githubURL);
                    }
                });
        ((ImageButton) view.findViewById(R.id.twitter))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openWebsite(twitterURL);
                    }
                });

        mAdapter = new SummaryAdapter(summaryList);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    private class SummaryHolder extends RecyclerView.ViewHolder{
        private String summaryArray;
        public TextView mNameTextView;

        public SummaryHolder(View itemView){
            super(itemView);
            mNameTextView = (TextView) itemView.findViewById(R.id.summary);
           /* itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(),
                            mScientist.getName() + " clicked!", Toast.LENGTH_SHORT)
                            .show();
                }
            });*/
        }
        public void bindData(String s){
            summaryArray = s;
            mNameTextView.setText(s);
        }
    }

    private class SummaryAdapter extends RecyclerView.Adapter<SummaryHolder>{
        private ArrayList<String> SummaryLists;
        public SummaryAdapter(ArrayList<String> summarys){
            SummaryLists = summarys;
        }
        @Override
        public SummaryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.recycle_item_summary,parent,false);
            return new SummaryHolder(view);
        }
        @Override
        public void onBindViewHolder(SummaryHolder holder, int position) {
            String s = SummaryLists.get(position);
            holder.bindData(s);
        }
        @Override
        public int getItemCount() {
            return SummaryLists.size();
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

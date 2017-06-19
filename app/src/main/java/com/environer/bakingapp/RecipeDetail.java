package com.environer.bakingapp;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.environer.bakingapp.adapter.StepsAdapter;
import com.environer.bakingapp.model.Ingredients;
import com.environer.bakingapp.model.Steps;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RecipeDetail.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RecipeDetail#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecipeDetail extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ArrayList<Steps> stepsArrayList;
    Steps step;
    Ingredients ingredient;
    int recipeTypeClicked;
    SimpleExoPlayerView exoPlayerView;
    SimpleExoPlayer mExoPlayer;
    Toolbar toolbar;
    private OnFragmentInteractionListener mListener;
    private String userAgent;
    private int position;
    private LinearLayout rootLayout;
    boolean isLandscape;

    public RecipeDetail() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecipeDetail.
     */
    // TODO: Rename and change types and number of parameters
    public static RecipeDetail newInstance(String param1, String param2) {
        RecipeDetail fragment = new RecipeDetail();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    TextView ingredTv;
    RecyclerView recyclerView;
    StepsAdapter stepsAdapter;
    ProgressBar progressBar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Recipe Details");
        // Inflate the layout for this fragment
        toolbar = (Toolbar)getActivity().findViewById(R.id.myToolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack();
            }
        });
        View view =  inflater.inflate(R.layout.fragment_recipe_detail, container, false);
        if(getArguments()!=null){
            recipeTypeClicked = getArguments().getInt("recipeType");
//            Toast.makeText(getContext(), String.valueOf(recipeTypeClicked), Toast.LENGTH_SHORT).show();
        }
        getIngrediants();
        ingredTv = (TextView)view.findViewById(R.id.textViewIngred);
        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerViewSteps);
        exoPlayerView = (SimpleExoPlayerView) view.findViewById(R.id.exoplayer);

        //I have try to show the progress bar but it is not showing???
        //Where I am wrong
//        progressBar =new ProgressBar(getContext());
//        rootLayout = (LinearLayout)view.findViewById(R.id.detailRoot);
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100,100);
//        params.gravity = Gravity.CENTER;
//        rootLayout.addView(progressBar);
//        progressBar.bringToFront();
//
//        progressBar.setVisibility(View.VISIBLE);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mExoPlayer!=null){
            mExoPlayer.release();
            mExoPlayer.stop();
            mExoPlayer = null;
        }
    }

    private void goBack() {
        if(mExoPlayer!=null){
            mExoPlayer.release();
            mExoPlayer.stop();
            mExoPlayer = null;
        }
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentFrontView fragmentFrontView = new FragmentFrontView();
        fragmentManager.beginTransaction().replace(R.id.fragment_container,fragmentFrontView).commit();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(!isLandscape){
            SimpleExoPlayer currExoplayer = stepsAdapter.returnInstance();
            if(currExoplayer!=null){
                currExoplayer.release();
                currExoplayer.stop();
                currExoplayer = null;
            }
        }else{
            if(mExoPlayer != null)
            {
                mExoPlayer.release();
                mExoPlayer.stop();
                mExoPlayer = null;
            }
        }
    }

    private void getIngrediants() {
        //Send request to the api for ingrediants and store that in array of ingrediants class
        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, getString(R.string.json_request), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                PraseData(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(stringRequest);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(!isLandscape){
        SimpleExoPlayer currExoplayer = stepsAdapter.returnInstance();
        if(currExoplayer!=null){
            currExoplayer.release();
            currExoplayer.stop();
            currExoplayer = null;
        }
        }else{
            if(mExoPlayer != null)
            {
                mExoPlayer.release();
                mExoPlayer.stop();
                mExoPlayer = null;
            }
        }
    }

    private void PraseData(String response) {
        String ingrediatnsString="";
        try {

            JSONArray jsonArray = new JSONArray(response);
            JSONObject recipeObject = jsonArray.getJSONObject(recipeTypeClicked);
            JSONArray ingrediantsArray = recipeObject.getJSONArray("ingredients");
            JSONArray stepsArray = recipeObject.getJSONArray("steps");

            //Parsing the ingrediants details
            for(int i = 0;i<ingrediantsArray.length();i++) {
                ingredient = new Ingredients();
                JSONObject currentObj = ingrediantsArray.getJSONObject(i);
                ingrediatnsString += String.valueOf((i+1))+". " + currentObj.getString("quantity") + " "
                        +currentObj.getString("measure")
                        + " "+ currentObj.getString("ingredient");
                if(i!=ingrediantsArray.length()-1){
                    ingrediatnsString+="   ";
                }
            }

            //Parsing the steps details
            stepsArrayList = new ArrayList<>();
            for(int i=0;i<stepsArray.length();i++){
                step = new Steps();
                JSONObject currentObj = stepsArray.getJSONObject(i);
                step.setId(currentObj.getString("id"));
                step.setShortDescription(currentObj.getString("shortDescription"));
                step.setDescription(currentObj.getString("description"));
                step.setVideoUrl(currentObj.getString("videoURL"));
                step.setThumbnailUrl(currentObj.getString("thumbnailURL"));
                stepsArrayList.add(step);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        isLandscape = getResources().getBoolean(R.bool.isLandscape);
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        boolean isTabletLand = getResources().getBoolean(R.bool.isTabletLand);
        if(!isTablet) {
            if (!isLandscape) {
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                ingredTv.setText(ingrediatnsString);

                recyclerView.setLayoutManager(linearLayoutManager);
                stepsAdapter = new StepsAdapter(stepsArrayList, getContext(), exoPlayerView, mExoPlayer);
                recyclerView.setAdapter(stepsAdapter);
            } else {
                toolbar.setVisibility(View.GONE);
                playVideo(Uri.parse(stepsArrayList.get(0).getVideoUrl()));
                mExoPlayer.addListener(new ExoPlayer.EventListener() {
                    @Override
                    public void onLoadingChanged(boolean isLoading) {

                    }

                    @Override
                    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                        if (playbackState == ExoPlayer.STATE_ENDED) {
                            Uri uri = Uri.parse(stepsArrayList.get(++position).getVideoUrl());
                            if (uri == null) {
                                uri = Uri.parse((stepsArrayList.get(++position).getThumbnailUrl()));
                            }
                            playVideo(uri);
                        }
                    }

                    @Override
                    public void onTimelineChanged(Timeline timeline, Object manifest) {

                    }

                    @Override
                    public void onPlayerError(ExoPlaybackException error) {

                    }

                    @Override
                    public void onPositionDiscontinuity() {

                    }
                });
            }
        }
        else if(isTablet){
            if(isTabletLand){
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                ingredTv.setText(ingrediatnsString);

                recyclerView.setLayoutManager(linearLayoutManager);
                stepsAdapter = new StepsAdapter(stepsArrayList, getContext(), exoPlayerView, mExoPlayer);
                recyclerView.setAdapter(stepsAdapter);
            }
            else{
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                ingredTv.setText(ingrediatnsString);

                recyclerView.setLayoutManager(linearLayoutManager);
                stepsAdapter = new StepsAdapter(stepsArrayList, getContext(), exoPlayerView, mExoPlayer);
                recyclerView.setAdapter(stepsAdapter);
            }
        }

    }

    void playVideo(Uri uri){
        if(mExoPlayer!=null){
            mExoPlayer.stop();
        }

        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
        android.os.Handler handler = new android.os.Handler();
        TrackSelector trackSelector =  new DefaultTrackSelector(handler,videoTrackSelectionFactory);
        LoadControl loadControl= new DefaultLoadControl();
        mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(),trackSelector,loadControl);
        exoPlayerView.setPlayer(mExoPlayer);

        userAgent = Util.getUserAgent(getContext(),"BakingApp");
        if(uri!=null && !uri.toString().equals("")){
            MediaSource mediaSource = new ExtractorMediaSource(uri,new DefaultDataSourceFactory(getContext(),userAgent),new DefaultExtractorsFactory(),null,null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);


        }
        else{
                Toast.makeText(getContext(), stepsArrayList.get(position).getDescription(), Toast.LENGTH_LONG).show();
            playVideo(Uri.parse(stepsArrayList.get(++position).getVideoUrl()));
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

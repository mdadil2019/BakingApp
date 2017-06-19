package com.environer.bakingapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.environer.bakingapp.adapter.FrontRecipeAdapter;
import com.environer.bakingapp.model.Recipe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentFrontView.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentFrontView#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentFrontView extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayList<Recipe> recipes;
    private Recipe recipe;

    private OnFragmentInteractionListener mListener;
    private RecyclerView recyclerView;
    private FrontRecipeAdapter frontRecipeAdapter;
    private Toolbar toolbar;
    ProgressBar progressBar;
    RelativeLayout mainRootLayout;

    public FragmentFrontView() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentFrontView.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentFrontView newInstance(String param1, String param2) {
        FragmentFrontView fragment = new FragmentFrontView();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.front_view_fragment, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Baking Time");
        toolbar = (Toolbar)getActivity().findViewById(R.id.myToolbar);
        toolbar.setNavigationIcon(null);
        getRecipeData();
        progressBar = new ProgressBar(getContext());
        mainRootLayout = (RelativeLayout)view.findViewById(R.id.mainRoot);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100,100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mainRootLayout.addView(progressBar,params);

        progressBar.setVisibility(View.VISIBLE);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    private void getRecipeData() {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET,getString(R.string.json_request), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(!response.equals("null"))
                    ParseJson(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(stringRequest);
    }

    private void ParseJson(String response) {
        try {
            recipes = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(response);
            for(int i = 0;i<jsonArray.length();i++){
                recipe = new Recipe();
                JSONObject currentObject = jsonArray.getJSONObject(i);
                recipe.setId(currentObject.getString("id"));
                recipe.setName(currentObject.getString("name"));
                recipe.setServings(currentObject.getString("servings"));
                recipes.add(recipe);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Before setting the layout manager to the recyelrview check the orentation of the deivce
        //if landscape then use Horizontal Layout manager

        recyclerView = (RecyclerView) getView().findViewById(R.id.mainVrecyclerView);
        boolean isLandscape = getResources().getBoolean(R.bool.isLandscape);
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        boolean isTabletLand = getResources().getBoolean(R.bool.isTabletLand);
        if(!isTablet) {
            LinearLayoutManager layoutManager = null;
            if (!isLandscape)
                layoutManager = new LinearLayoutManager(getContext());
            else
                layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(layoutManager);
        }
        else if(isTablet){
            GridLayoutManager gridLayoutManager;
            if(!isTabletLand){
                gridLayoutManager = new GridLayoutManager(getContext(),numberofColumn());
            }
            else{
                gridLayoutManager = new GridLayoutManager(getContext(),3);
            }
            recyclerView.setLayoutManager(gridLayoutManager);
        }
        Fragment fragment = this;
        frontRecipeAdapter = new FrontRecipeAdapter(recipes,getContext(),getActivity(),fragment);
        recyclerView.setAdapter(frontRecipeAdapter);
        progressBar.setVisibility(View.INVISIBLE);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    private int numberofColumn(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int widthDivider = 400;
        int width = displayMetrics.widthPixels;
        int nColumns = width / widthDivider;
        if(nColumns<2)return 2;
        return nColumns;
    }

    @Override
    public void onStop() {
        super.onStop();
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

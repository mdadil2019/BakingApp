package com.environer.bakingapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.environer.bakingapp.R;
import com.environer.bakingapp.RecipeDetail;
import com.environer.bakingapp.model.Recipe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Mohammad Adil on 07-06-2017.
 */

public class FrontRecipeAdapter extends RecyclerView.Adapter<FrontRecipeAdapter.MyFrontViewHolder> {
    ArrayList<Recipe>recipeData;
    boolean[] boolsArray = new boolean[4];
    Context context;
    FragmentActivity refActivity;
    Fragment refFragment;
    public FrontRecipeAdapter(ArrayList<Recipe>recps, Context c,FragmentActivity ref, Fragment refFrag){
        recipeData = recps;
        context = c;
        refActivity = ref   ;
        refFragment = refFrag;
    }

    @Override
    public MyFrontViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.each_recipe,parent,false);
        MyFrontViewHolder viewHolder = new MyFrontViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyFrontViewHolder holder, int position) {
       //check if the image is not set then set it else not do any thing
        if(!boolsArray[position]){
            if(position==0){
                Picasso.with(context).load(context.getString(R.string.recipe1)).into(holder.recipeImgView);
            }else if(position == 1){
                Picasso.with(context).load(context.getString(R.string.recipe2)).into(holder.recipeImgView);
            }else if(position == 2){
                Picasso.with(context).load(context.getString(R.string.recipe3)).into(holder.recipeImgView);
            }else if(position == 3){
                Picasso.with(context).load(context.getString(R.string.recipe4)).into(holder.recipeImgView);
            }
            boolsArray[position] = true;
        }
        holder.recipeImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentManager fragmentManager = refActivity.getSupportFragmentManager();
                RecipeDetail recipeDetail = new RecipeDetail();
                Bundle bundle = new Bundle();
                bundle.putInt("recipeType",holder.getAdapterPosition());
                recipeDetail.setArguments(bundle);
                fragmentManager.beginTransaction().remove(refFragment).add(R.id.fragment_container,recipeDetail).commit();
            }
        });

        //populate the text views
        holder.recipeTv.setText(recipeData.get(position).getName());
        holder.survingTv.setText(recipeData.get(position).getServings());
    }

    @Override
    public int getItemCount() {
        return recipeData.size();
    }


    public class MyFrontViewHolder extends RecyclerView.ViewHolder{
//        @BindView(R.id.textViewSurvingLabel)TextView survingTv;
//        @BindView(R.id.textViewRecipeLabel)TextView recipeTv;
//        @BindView(R.id.imageViewRecipe)ImageView recipeImgView;
TextView survingTv;TextView recipeTv;ImageView recipeImgView;
        public MyFrontViewHolder(View itemView) {
            super(itemView);
//            ButterKnife.bind(itemView);
            recipeTv = (TextView) itemView.findViewById(R.id.textViewRecipeLabel);
            recipeImgView  = (ImageView) itemView.findViewById(R.id.imageViewRecipe);
            survingTv = (TextView)itemView.findViewById(R.id.textViewSurvingLabel);
        }
    }
}

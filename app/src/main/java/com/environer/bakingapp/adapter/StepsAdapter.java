package com.environer.bakingapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.environer.bakingapp.R;
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
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;
import java.util.logging.Handler;

/**
 * Created by Mohammad Adil on 07-06-2017.
 */

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.MyStepsViewHolder> {
    ArrayList<Steps>stepsData;
    Context context;
    SimpleExoPlayerView exoPlayerView;
    SimpleExoPlayer mExoPlayer;
    MyStepsViewHolder prevViewHolder;
    int currentPlayingPosition;
    String userAgent;
    boolean isAutoplaying;
    Uri myUri;

    public StepsAdapter(){

    }
    public StepsAdapter(ArrayList<Steps> steps,Context con, SimpleExoPlayerView exoP, SimpleExoPlayer exoPlay){
        stepsData = steps;
        context = con;
        exoPlayerView = exoP;
        mExoPlayer = exoPlay;
    }
    @Override
    public MyStepsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.step_detail_layout,parent,false);
        MyStepsViewHolder viewHolder = new MyStepsViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(final MyStepsViewHolder holder, final int position) {

        if(position == 0 && myUri == null) {
             myUri = Uri.parse(stepsData.get(position).getVideoUrl());
            if (myUri == null) {
                myUri = Uri.parse(stepsData.get(position).getThumbnailUrl());
            }
            playVideo(myUri,holder);
        }
        String shortDescription = stepsData.get(position).getShortDescription();
        String fullDescription = stepsData.get(position).getDescription();
        holder.shortDesc.setText(shortDescription);
        holder.fullDescr.setText(fullDescription);

        mExoPlayer.addListener(new ExoPlayer.EventListener() {
            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if(playbackState == ExoPlayer.STATE_ENDED && currentPlayingPosition<stepsData.size()-1){
                    currentPlayingPosition++;
                    playNextVideo();
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
        holder.playIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //when user tap on play then currentPlayingPosition will be updated
                //else it will be increase automatically to play the next step
                isAutoplaying = false;//if user click on the play then it means it is autoplaying
                currentPlayingPosition = holder.getAdapterPosition();
                    myUri = Uri.parse(stepsData.get(holder.getAdapterPosition()).getVideoUrl());
                    if(myUri ==null){
                        myUri = Uri.parse(stepsData.get(holder.getAdapterPosition()).getThumbnailUrl());
                    }
                    playVideo(myUri,holder);


                }

        });
    }

    public SimpleExoPlayer returnInstance(){
        return mExoPlayer;
    }
    void playVideo(Uri uri,MyStepsViewHolder holder){
        if(mExoPlayer!=null){
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }

        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
        android.os.Handler handler = new android.os.Handler();
        TrackSelector trackSelector =  new DefaultTrackSelector(handler,videoTrackSelectionFactory);
        LoadControl loadControl= new DefaultLoadControl();
        mExoPlayer = ExoPlayerFactory.newSimpleInstance(context,trackSelector,loadControl);
        exoPlayerView.setPlayer(mExoPlayer);

        userAgent = Util.getUserAgent(context,"BakingApp");
        if(uri!=null && !uri.toString().equals("")){
            MediaSource mediaSource = new ExtractorMediaSource(uri,new DefaultDataSourceFactory(context,userAgent),new DefaultExtractorsFactory(),null,null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
            if(prevViewHolder!=null){
                prevViewHolder.rootLayout.setBackgroundColor(Color.WHITE);
            }
            holder.rootLayout.setBackgroundColor(Color.LTGRAY);
            prevViewHolder = holder;
        }
        else{
            if(!isAutoplaying)
                Toast.makeText(context, stepsData.get(holder.getAdapterPosition()).getDescription(), Toast.LENGTH_LONG).show();
            else {
                Toast.makeText(context, stepsData.get(currentPlayingPosition).getDescription(), Toast.LENGTH_LONG).show();

            }
        }
    }
    void playNextVideo(){
        isAutoplaying = true;

        Uri uri = Uri.parse(stepsData.get(currentPlayingPosition).getVideoUrl());
        if(uri==null){
            uri = Uri.parse(stepsData.get(currentPlayingPosition).getThumbnailUrl());
        }
        playVideo(uri,prevViewHolder);//here I want to pass reference of another(next) view holder so that I can change the color
                        //of next view holder's layout

    }
    @Override
    public int getItemCount() {
        return stepsData.size();
    }

    public class MyStepsViewHolder extends RecyclerView.ViewHolder{

        ImageView playIcon;
        TextView shortDesc,fullDescr;
        LinearLayout rootLayout;
        public MyStepsViewHolder(View itemView) {
            super(itemView);
            playIcon = (ImageView)itemView.findViewById(R.id.imageViewPlayIcon);
            shortDesc = (TextView)itemView.findViewById(R.id.textViewShortDes);
            fullDescr = (TextView)itemView.findViewById(R.id.textViewFullDes);
            rootLayout = (LinearLayout)itemView.findViewById(R.id.stepDetailRoot);
        }
    }
}

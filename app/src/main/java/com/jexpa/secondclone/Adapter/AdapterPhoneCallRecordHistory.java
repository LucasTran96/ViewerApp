/*
  ClassName: AdapterPhoneCallRecordHistory.java
  @Project: SecondClone
  @author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description: class AdapterCallHistory used to customize the adapter for the RecyclerView of the "CallHistory.class"
  History:2018-10-08
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.jexpa.secondclone.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jexpa.secondclone.Database.DatabaseAmbientRecord;
import com.jexpa.secondclone.Database.DatabasePhoneCallRecord;
import com.jexpa.secondclone.Model.AudioGroup;
import com.jexpa.secondclone.Model.PhoneCallRecordJson;
import com.jexpa.secondclone.R;
import com.jexpa.secondclone.View.MyApplication;
import com.jexpa.secondclone.View.PhoneCallRecordHistory;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.jexpa.secondclone.API.APIMethod.formateMilliSeccond;
import static com.jexpa.secondclone.API.APIURL.isConnected;
import static com.jexpa.secondclone.API.Global.DEFAULT_PRODUCT_NAME;

public class AdapterPhoneCallRecordHistory extends RecyclerView.Adapter<AdapterPhoneCallRecordHistory.ViewHolder> {

    private Activity mActivity;
    private static List<AudioGroup> mData;
    private ImageView imageViewSaving, imageViewPlay;
    private AVLoadingIndicatorView avIndicatorView;
    private SeekBar seekBarPlay;
    private TextView txt_saving, txt_Star, txt_end;
    private String fileName, urlAudio;
    private AudioGroup phoneCallRecorded;
    private int totalTime;
    private boolean mediaPlayerStart;
    private static MediaPlayer mp;
    private DatabasePhoneCallRecord databasePhoneCallRecord;
    private DatabaseAmbientRecord databaseAmbientRecord;
    private boolean checkAmbient = true;


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView txt_Name_PhoneCallRecord_History, txt_time_PhoneCallRecord_History, txt_Date_PhoneCallRecord_History;
        View mView;
        CardView cv_PhoneCallRecord_History;
        ImageView img_Detail_PhoneCallRecord;

        ViewHolder(View v) {
            super(v);
            txt_Name_PhoneCallRecord_History = v.findViewById(R.id.txt_Name_PhoneCallRecord_History);
            txt_time_PhoneCallRecord_History = v.findViewById(R.id.txt_time_PhoneCallRecord_History);
            txt_Date_PhoneCallRecord_History = v.findViewById(R.id.txt_Date_PhoneCallRecord_History);
            img_Detail_PhoneCallRecord = v.findViewById(R.id.img_Detail_PhoneCallRecord);
            cv_PhoneCallRecord_History = v.findViewById(R.id.cv_PhoneCallRecord_History);
            mView = v;
            v.setOnLongClickListener(this);
            //cv_PhoneCallRecord_History.setOnClickListener(this);
            v.setOnClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            ((com.jexpa.secondclone.View.PhoneCallRecordHistory) mActivity).prepareToolbar(getAdapterPosition());
            return true;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            /* Lightning click event. */
            if (com.jexpa.secondclone.View.PhoneCallRecordHistory.isInActionMode) {
                ((com.jexpa.secondclone.View.PhoneCallRecordHistory) mActivity).prepareSelection(getAdapterPosition());
                notifyItemChanged(getAdapterPosition());
            } else if (position != RecyclerView.NO_POSITION)
            {
                AudioGroup phoneCallRecord = mData.get(position);
                if (PhoneCallRecordHistory.request) {
                    final AlertDialog.Builder mBuilder = new AlertDialog.Builder(mActivity);
                    @SuppressLint("InflateParams") View mView = LayoutInflater.from(mActivity).inflate(R.layout.item_dialog_phonecallrecord, null);
                    final ImageView img_Loading_PhoneCallRecord, img_Play_PhoneCallRecord;
                    final SeekBar sb_Play_PhoneCallRecord;
                    final AVLoadingIndicatorView avLoadingIndicatorView;
                    final TextView txt_Time_Start_PhoneCallRecord, txt_Time_End_PhoneCallRecord, txt_ContactName_PhoneCallRecord;
                    img_Loading_PhoneCallRecord = mView.findViewById(R.id.img_Loading_PhoneCallRecord);
                    avLoadingIndicatorView = mView.findViewById(R.id.aviDownloadAudio);
                    // aviDownloadAudio
                    img_Play_PhoneCallRecord = mView.findViewById(R.id.img_Play_PhoneCallRecord);
                    sb_Play_PhoneCallRecord = mView.findViewById(R.id.sb_Play_PhoneCallRecord);
                    txt_Time_Start_PhoneCallRecord = mView.findViewById(R.id.txt_Time_Start_PhoneCallRecord);
                    txt_Time_End_PhoneCallRecord = mView.findViewById(R.id.txt_Time_End_PhoneCallRecord);
                    txt_ContactName_PhoneCallRecord = mView.findViewById(R.id.txt_ContactName_PhoneCallRecord);
                    mBuilder.setView(mView);
                    final AlertDialog dialog = mBuilder.create();
                    Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                    txt_saving = txt_ContactName_PhoneCallRecord;
                    txt_ContactName_PhoneCallRecord.setText(phoneCallRecord.getContactName());
                    txt_Time_End_PhoneCallRecord.setText("-:-");
                    txt_Time_Start_PhoneCallRecord.setText("-:-");
                    imageViewPlay = img_Play_PhoneCallRecord;
                    txt_Star = txt_Time_Start_PhoneCallRecord;
                    txt_end = txt_Time_End_PhoneCallRecord;
                    seekBarPlay = sb_Play_PhoneCallRecord;
                    imageViewSaving = img_Loading_PhoneCallRecord;
                    avIndicatorView = avLoadingIndicatorView;

                    phoneCallRecorded = phoneCallRecord;
                    Log.d("textIS", phoneCallRecord.getIsSave()+"");
                    if (phoneCallRecord.getIsSave() == 1) {
                        img_Play_PhoneCallRecord.setEnabled(true);
                        sb_Play_PhoneCallRecord.setEnabled(true);
                        String filePath = Environment.getExternalStorageDirectory() +"/" + DEFAULT_PRODUCT_NAME + "/" + phoneCallRecorded.getAudioName();
                        File file = new File(filePath);
                        if(file.exists())
                        {
                            Log.d("cehck","file exits");
                            playAudio();

                        }
                        else
                        {
                            Log.d("cehck","file not exits"+ " === "+filePath );
                            img_Play_PhoneCallRecord.setEnabled(false);
                            sb_Play_PhoneCallRecord.setEnabled(false);
                            fileName = phoneCallRecord.getAudioName();
                            // txt_ContactName_PhoneCallRecord.setText("Loading...");
                            Log.d("fileName", fileName);
                            urlAudio = phoneCallRecord.getURL_Audio();
                            txt_ContactName_PhoneCallRecord.setText(phoneCallRecord.getContactName());
                            img_Loading_PhoneCallRecord.setImageResource(R.drawable.download);
                            new DownloadPhoneCallRecordTask().execute();
                        }


                    } else {
                        if (isConnected(mActivity)) {
                            img_Play_PhoneCallRecord.setEnabled(false);
                            sb_Play_PhoneCallRecord.setEnabled(false);
                            fileName = phoneCallRecord.getAudioName();
                            // txt_ContactName_PhoneCallRecord.setText("Loading...");
                            Log.d("fileName", fileName);
                            urlAudio = phoneCallRecord.getURL_Audio();
                            txt_ContactName_PhoneCallRecord.setText(phoneCallRecord.getContactName());
                            img_Loading_PhoneCallRecord.setImageResource(R.drawable.download);
                            new DownloadPhoneCallRecordTask().execute();
                        } else {
                            Toast.makeText(mActivity, MyApplication.getResourcses().getString(R.string.TurnOn), Toast.LENGTH_SHORT).show();
                        }
                    }
                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            if (mediaPlayerStart) {
                                try {
                                    mp.stop();
                                }catch (Exception e)
                                {
                                    e.getMessage();
                                }

                            }
                            ((com.jexpa.secondclone.View.PhoneCallRecordHistory) mActivity).reload();
                        }
                    });
                } else {
                    Toast.makeText(mActivity, "You please accept the file read permission to save image!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void playAudio() {
        MyApplication.getInstance().trackEvent("PhoneCallRecordHistory", "Download and play audio", "Play PhoneCallRecord");
        mediaPlayerStart = true;
        String fileName =  "/" + DEFAULT_PRODUCT_NAME + "/" + phoneCallRecorded.getAudioName();
        Log.d("AmbientMediaLink",fileName);
        mp = MediaPlayer.create(mActivity, Uri.parse(Environment.getExternalStorageDirectory() + fileName));
        mp.setLooping(true);
        mp.seekTo(0);
        mp.setVolume(0.5f, 0.5f);
        totalTime = mp.getDuration();
        mp.start();
        imageViewPlay.setImageResource(R.drawable.icons_pause);
        seekBarPlay.setMax(totalTime);
        seekBarPlay.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            mp.seekTo(progress);
                            seekBarPlay.setProgress(progress);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                }
        );

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mp != null) {
                    try {
                        Message msg = new Message();
                        msg.what = mp.getCurrentPosition();
                        handler.sendMessage(msg);
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }).start();

        imageViewPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mp.isPlaying()) {
                    // Stopping
                    mp.start();
                    imageViewPlay.setImageResource(R.drawable.icons_pause);

                } else {
                    // Playing
                    mp.pause();
                    imageViewPlay.setImageResource(R.drawable.icons_circled);
                }
            }
        });
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(Message msg) {
            int currentPosition = msg.what;
            /* Update positionBar. */
            seekBarPlay.setProgress(currentPosition);
            /* Update Labels. */
            String elapsedTime = createTimeLabel(currentPosition);
            txt_Star.setText(elapsedTime);
            String remainingTime = createTimeLabel(totalTime - currentPosition);
            txt_end.setText(remainingTime);
        }
    };

    private String createTimeLabel(int time) {
        String timeLabel;
        int min = time / 1000 / 60;
        int sec = time / 1000 % 60;
        timeLabel = min + ":";
        if (sec < 10) timeLabel += "0";
        timeLabel += sec;
        return timeLabel;
    }


    public AdapterPhoneCallRecordHistory(Activity activity, List<AudioGroup> myData) {
        mActivity = activity;
        mData = myData;
        databasePhoneCallRecord = new DatabasePhoneCallRecord(mActivity);
        databaseAmbientRecord = new DatabaseAmbientRecord(mActivity);
    }

    @NonNull
    @Override
    public AdapterPhoneCallRecordHistory.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                       int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rcv_phonecallrecord_history, parent, false);
        return new ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AudioGroup phoneCallRecord = mData.get(position);
        if(phoneCallRecord != null)
        {

            holder.txt_Name_PhoneCallRecord_History.setText(phoneCallRecord.getContactName());
            //holder.txt_time_PhoneCallRecord_History.setText(phoneCallRecord.getDuration() + "s");
            Log.d("tttt",phoneCallRecord.getDuration() );
            if(phoneCallRecord.getIsAmbient() == 1)
                holder.txt_time_PhoneCallRecord_History.setText(phoneCallRecord.getDuration()+"s");
            else
                holder.txt_time_PhoneCallRecord_History.setText(formateMilliSeccond(Long.parseLong(phoneCallRecord.getDuration())*1000)+"s");

            holder.txt_Date_PhoneCallRecord_History.setText(phoneCallRecord.getDate());
            if (phoneCallRecord.getIsSave() == 0) {
                holder.img_Detail_PhoneCallRecord.setImageResource(R.drawable.download);
            }
            else {
                holder.img_Detail_PhoneCallRecord.setImageResource(R.drawable.micro_sd_card);
            }

            if (com.jexpa.secondclone.View.PhoneCallRecordHistory.isInActionMode) {
                if (com.jexpa.secondclone.View.PhoneCallRecordHistory.selectionList.contains(mData.get(position))) {
                    holder.cv_PhoneCallRecord_History.setCardBackgroundColor(mActivity.getResources().getColor(R.color.grey_200));
                }
                else {
                    holder.cv_PhoneCallRecord_History.setCardBackgroundColor(mActivity.getResources().getColor(R.color.white));
                }
            }else {
                holder.cv_PhoneCallRecord_History.setCardBackgroundColor(mActivity.getResources().getColor(R.color.white));
            }
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void removeData(ArrayList<AudioGroup> list) {
        for (AudioGroup phoneCallRecord : list) {
            mData.remove(phoneCallRecord);
        }
        notifyDataSetChanged();
    }

    @SuppressLint("StaticFieldLeak")
    private class DownloadPhoneCallRecordTask extends AsyncTask<Void, Void, Void> {

        File apkStorage = null;
        File outputFile = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //imageViewSaving.setImageResource(R.drawable.downloading);
            imageViewSaving.setVisibility(View.GONE);
            avIndicatorView.setVisibility(View.VISIBLE);
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(Void result) {
            try {
                if (outputFile != null) {
                    if (phoneCallRecorded.getIsAmbient() == 0)
                    {
                        Log.d("ddsds",  phoneCallRecorded.getDeviceID()+ " ==== "+ phoneCallRecorded.getID());
                        databasePhoneCallRecord.update_PhoneCallRecord_History(1, phoneCallRecorded.getDeviceID(), phoneCallRecorded.getID());
                    }else {
                        databaseAmbientRecord.update_AmbientRecord_History(1, phoneCallRecorded.getDeviceID(), phoneCallRecorded.getContactName());
                    }

                    // phoneCallRecorded.setIsSaved(1);
                    imageViewPlay.setEnabled(true);
                    seekBarPlay.setEnabled(true);
                    imageViewSaving.setImageResource(R.drawable.call_phonerecord); //If Download completed then change button text
                    imageViewSaving.setVisibility(View.VISIBLE);
                    avIndicatorView.setVisibility(View.GONE);
                    playAudio();
                } else {
                    txt_saving.setText("download failed!");
                    imageViewSaving.setImageResource(R.drawable.downloading); //If download failed change button text
                    imageViewSaving.setVisibility(View.VISIBLE);
                    avIndicatorView.setVisibility(View.GONE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            imageViewSaving.setImageResource(R.drawable.downloading); //Change button text again after 3sec
                            imageViewSaving.setVisibility(View.VISIBLE);
                            avIndicatorView.setVisibility(View.GONE);
                        }
                    }, 3000);

                    Log.e("CheckDownload", result + "");

                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(mActivity, "error", Toast.LENGTH_SHORT).show();
                //Change button text if exception occurs
                //imageView.setImageResource(R.drawable.downloading);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        imageViewSaving.setImageResource(R.drawable.downloading);
                        imageViewSaving.setVisibility(View.VISIBLE);
                        avIndicatorView.setVisibility(View.GONE);
                    }
                }, 3000);
            }
            super.onPostExecute(result);
        }

        @Override
        protected Void doInBackground(Void... arg0)
        {
            try {
                URL url = new URL(urlAudio);//Create Download URl
                HttpURLConnection c = (HttpURLConnection) url.openConnection();//Open Url Connection
                c.setRequestMethod("GET");//Set Request Method to "GET" since we are getting data
                c.connect();//connect the URL Connection
                //If Connection response is not OK then show Logs
                if (c.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e("Server returned", "Server returned HTTP " + c.getResponseCode()
                            + " " + c.getResponseMessage());
                }
                //Get File if SD card is present
                if (isSDCardPresent()) {
                    apkStorage = new File(Environment.getExternalStorageDirectory() + "/" + "SecondClone");
                } else {
                    Toast.makeText(mActivity, "There is no SD Card!", Toast.LENGTH_SHORT).show();
                }
                //If File is not present create directory
                if (!apkStorage.exists()) {
                    apkStorage.mkdir();
                    Log.e("Directory Created.", "Directory Created.");
                }
                outputFile = new File(apkStorage, fileName);//Create Output file in Main File
                //Create New File if not present
                if (!outputFile.exists()) {
                    outputFile.createNewFile();
                    Log.e("File Created", "File Created");
                }
                FileOutputStream fos = new FileOutputStream(outputFile);//Get OutputStream for NewFile Location
                InputStream is = c.getInputStream();//Get InputStream for connection
                byte[] buffer = new byte[1024];//Set buffer type
                int len1;//init length
                while ((len1 = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len1);//Write new file
                }
                //Close all connection after doing task
                fos.close();
                is.close();
            } catch (Exception e) {
                //Read exception if something went wrong
                e.printStackTrace();
                outputFile = null;
                Log.e("failed", "Download Error Exception " + e.getMessage());
            }
            return null;
        }
    }

    private boolean isSDCardPresent() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

}
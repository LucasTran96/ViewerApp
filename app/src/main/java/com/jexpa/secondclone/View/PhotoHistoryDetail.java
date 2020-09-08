package com.jexpa.secondclone.View;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.jexpa.secondclone.API.APIMethod;
import com.jexpa.secondclone.API.APIURL;
import com.jexpa.secondclone.API.DepthPageTransformer;
import com.jexpa.secondclone.Database.DatabasePhotos;
import com.jexpa.secondclone.Model.Photo;
import com.jexpa.secondclone.R;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import static com.jexpa.secondclone.API.APIMethod.getProgressDialog;
import static com.jexpa.secondclone.API.APIURL.bodyLogin;
import static com.jexpa.secondclone.API.APIURL.deviceObject;
import static com.jexpa.secondclone.API.APIURL.isConnected;
import static com.jexpa.secondclone.API.Global.File_PATH_SAVE_IMAGE;

public class PhotoHistoryDetail extends AppCompatActivity {

    public static DatabasePhotos databasePhotos;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    public List<Photo> data = new ArrayList<>();
    int position;
    @SuppressLint("StaticFieldLeak")
    public static Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_photo);
        toolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        if (toolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        databasePhotos = new DatabasePhotos(this);
        data = getIntent().getParcelableArrayListExtra("data");
        position = getIntent().getIntExtra("position", 0);
        setTitle("");
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), data);
        ViewPager mViewPager = findViewById(R.id.container);
        mViewPager.setPageTransformer(true, new DepthPageTransformer());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(position);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int positioned) {
                position = positioned;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_action_mode, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.item_delete) {
            if (isConnected(PhotoHistoryDetail.this)) {
                getProgressDialog(getString(R.string.Loading)+"...",this);
                new clear_PhotoAsyncTask().execute();
            } else {
                Toast.makeText(this, getResources().getString(R.string.TurnOn), Toast.LENGTH_SHORT).show();
            }
        } else if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public List<Photo> data;

        SectionsPagerAdapter(FragmentManager fm, List<Photo> data) {
            super(fm);
            this.data = data;
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position, data.get(position).getCaption(), data.get(position).getCDN_URL() + data.get(position).getMedia_URL() + "/" + data.get(position).getFile_Name(), data.get(position).getFile_Name());
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return data.get(position).getCaption();
        }
    }

    public static class PlaceholderFragment extends Fragment {

        String title, name, url;
        int pos;
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String ARG_IMG_TITLE = "image_title";
        private static final String ARG_IMG_URL = "image_url";
        private static final String ARG_IMG_FILENAME = "image_name";

        @Override
        public void setArguments(Bundle args) {
            super.setArguments(args);
            this.pos = args.getInt(ARG_SECTION_NUMBER);
            this.title = args.getString(ARG_IMG_TITLE);
            this.url = args.getString(ARG_IMG_URL);
            this.name = args.getString(ARG_IMG_FILENAME);
        }

        public static PlaceholderFragment newInstance(int sectionNumber, String title, String url, String name) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putString(ARG_IMG_TITLE, title);
            args.putString(ARG_IMG_URL, url);
            args.putString(ARG_IMG_FILENAME, name);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public void onStart() {
            super.onStart();
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail_photo, container, false);
            PhotoView imageView = rootView.findViewById(R.id.detail_image);

            if (isConnected(getContext())) {
                //Glide.with(getActivity()).load(url).thumbnail(0.1f).error(R.drawable.no_image).placeholder(R.drawable.spinner).into(imageView);
                Picasso.with(getActivity()).load(url).error(R.drawable.no_image).placeholder(R.drawable.spinner).into(imageView);

            } else {
                Picasso.with(getContext()).load(new File(File_PATH_SAVE_IMAGE, name)).error(R.drawable.no_image).placeholder(R.drawable.spinner).into(imageView);
            }
            final boolean[] count = {false};
            imageView.setOnPhotoTapListener(new OnPhotoTapListener() {
                @Override
                public void onPhotoTap(ImageView view, float x, float y) {
                    count[0] = !count[0];
                    if (count[0]) {
                        toolbar.setVisibility(View.GONE);
                    } else {
                        toolbar.setVisibility(View.VISIBLE);
                    }
                }
            });
            return rootView;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class clear_PhotoAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {

            String value = "<RequestParams Device_ID=\"" + data.get(position).getDevice_ID() + "\" List_ID=\"" + data.get(position).getID() + "\" List_URL=\"\"/>";
            String function = "ClearMultiPhoto";
            return APIURL.POST(value, function);

        }

        @Override
        protected void onPostExecute(String s) {

            deviceObject(s);
            if (bodyLogin.getIsSuccess().equals("1") && bodyLogin.getIsSuccess().equals("1")) {

                databasePhotos.delete_Photos_History_File(data.get(position).getID());
                clearFileImage(data.get(position));
                mSectionsPagerAdapter.notifyDataSetChanged();
                finish();
            } else {

                Toast.makeText(PhotoHistoryDetail.this, bodyLogin.getDescription(), Toast.LENGTH_SHORT).show();
            }
            APIMethod.progressDialog.dismiss();
        }
    }

    public void clearFileImage(Photo photo) {

        File file = new File(File_PATH_SAVE_IMAGE + "/" + photo.getFile_Name());
        file.delete();
        getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (APIMethod.progressDialog != null && APIMethod.progressDialog.isShowing()) {
            APIMethod.progressDialog.dismiss();
        }
    }

}

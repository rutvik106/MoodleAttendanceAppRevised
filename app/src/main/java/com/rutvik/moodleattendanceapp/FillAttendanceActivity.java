package com.rutvik.moodleattendanceapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class FillAttendanceActivity extends AppCompatActivity {

    int coursePosition, attendancePosition;

    String sessionId, actionBarTitle, actionBarSubTitle;

    ListView lvAttendanceData;

    SwipeRefreshLayout swipeRefreshLayout;

    AttendanceDataListAdapter attendanceDataListAdapter;

    Toolbar mActionBar;

    HashMap<String, AttendanceData> attendanceDataMap = new HashMap<String, AttendanceData>();

    ArrayList<String> ids = new ArrayList<String>();

    RadioGroup rgAttendanceSelector;

    SharedPreferences mSharedPreferences;

    Menu menu;

    Boolean isFresh = false;

    Boolean isChanged = false;


    public void sortStudentsByName(HashMap<String, AttendanceData> map) {
        HashMap<String, String> list = new HashMap<String, String>();

        for (int i = 0; i < map.size(); i++) {
            String filteredName = map.get(ids.get(i)).getFirst_name();

            if (GlobalSettings.getInstance().sortStudentByName()) {

                int count = filteredName.length() - filteredName.replace("_", "").length();

                if (count == 1) {

                    try {
                        filteredName = filteredName.substring(filteredName.indexOf("_") + 1, filteredName.length());

                        Log.i("Moodle Attendance", "Filtered Name: " + filteredName);
                    } catch (Exception e) {

                        MoodleAttendanceApp.getInstance().trackEvent("sorting attendance", filteredName, "");

                        MoodleAttendanceApp.getInstance().trackException(e);

                        e.printStackTrace();
                    }

                }

            }

            list.put(map.get(ids.get(i)).getId(), filteredName);
        }


        Comparator<Map.Entry<String, String>> byMapValues = new Comparator<Map.Entry<String, String>>() {
            @Override
            public int compare(Map.Entry<String, String> left, Map.Entry<String, String> right) {
                return left.getValue().compareTo(right.getValue());
            }
        };


        List<Map.Entry<String, String>> sorted = new ArrayList<Map.Entry<String, String>>();

        sorted.addAll(list.entrySet());

        Collections.sort(sorted, byMapValues);

        ids.clear();

        for (int i = 0; i < sorted.size(); i++) {
            Log.i("Moodle", "Key: " + sorted.get(i).getKey() + " Value: " + sorted.get(i).getValue());
            ids.add(sorted.get(i).getKey());
        }

    }


    private void closeActivity() {

        if (isChanged) {

            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
            dlgAlert.setMessage("Are you sure you want to go back? Any unsaved changes will be lost.");
            dlgAlert.setTitle("Unsaved Changes!");
            dlgAlert.setPositiveButton("Go Back", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Intent returnIntent = new Intent();
                    setResult(RESULT_OK, returnIntent);
                    FillAttendanceActivity.this.finish();

                }


            });

            dlgAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();

                }
            });

            dlgAlert.setCancelable(true);
            dlgAlert.create().show();

        } else {
            Intent returnIntent = new Intent();
            setResult(RESULT_OK, returnIntent);
            this.finish();
        }

    }

    @Override
    public void onBackPressed() {

        closeActivity();

    }

    public void setAttendanceSelector(ArrayList<Statuses> s) {

        rgAttendanceSelector.removeAllViews();


        for (RadioButton r : generateSelectorRadioButtons(s)) {
            rgAttendanceSelector.addView(r);
        }

        //rgAttendanceSelector.clearCheck();

    }

    ArrayList<RadioButton> selectorRadioArrList = new ArrayList<RadioButton>();

    public ArrayList<RadioButton> generateSelectorRadioButtons(ArrayList<Statuses> statuses) {

        for (Statuses s : statuses) {
            if (s.getVisible().equals("1")) {
                RadioButton r = new RadioButton(this);
                r.setHint(s.getDescription());
                r.setHintTextColor(Color.WHITE);
                r.setText(s.getAcronym());
                r.setTag(s);

                RadioGroup.LayoutParams p = new RadioGroup.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                p.weight = 1;
                r.setLayoutParams(p);
                r.setTextColor(Color.WHITE);

                selectorRadioArrList.add(r);
            }

        }

        return selectorRadioArrList;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        actionBarSubTitle = getIntent().getStringExtra("sub_title");

        actionBarTitle = getIntent().getStringExtra("title");

        mSharedPreferences = getSharedPreferences("moodle_attendance_app_shared_pref", Context.MODE_PRIVATE);

        GlobalJSONObjects.getInstance().restoreJSONObjects(mSharedPreferences);


        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fill_attendance);

        SetActionBar();

        coursePosition = getIntent().getIntExtra("course_position", 0);

        attendancePosition = getIntent().getIntExtra("attendance_position", 0);

        sessionId = getIntent().getStringExtra("session_id");

        lvAttendanceData = (ListView) findViewById(R.id.lvAttendanceDataList);

        //lvAttendanceData.setItemsCanFocus(true);

        rgAttendanceSelector = (RadioGroup) findViewById(R.id.rgAttendanceSelector);


        rgAttendanceSelector.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                try {
                    RadioButton r = (RadioButton) findViewById(checkedId);
                    Statuses s = (Statuses) r.getTag();

                    for (String id : ids) {
                        attendanceDataMap.get(id).setAcronym(s.getAcronym());

                        attendanceDataMap.get(id).setStatus_id(s.getId());
                    }

                    r.setChecked(false);

                    attendanceDataListAdapter.notifyDataSetChanged();
                } catch (NullPointerException e) {

                }

            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.attendanceSwipeContainer);


        try {

            if (ServiceHandler.hasActiveInternetConnection(this)) {

                getAttendanceForSession(sessionId, this);
            }


        } catch (Exception e) {


        }

        swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {

                if (ServiceHandler.hasActiveInternetConnection(FillAttendanceActivity.this)) {

                    new AsyncTask<Void, Void, Void>() {

                        String response = "";

                        @Override
                        protected Void doInBackground(Void... params) {

                            ServiceHandler sh = new ServiceHandler();
                            response = sh.getAttendanceData(GlobalJSONObjects.getInstance().getUser().getToken(), sessionId);

                            Log.i("MAA", " session id is: " + sessionId + "");
                            Log.i("MAA", response);

                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void result) {
                            try {
                                JSONObject obj = new JSONObject(response);
                                JSONArray j = obj.getJSONArray("attendance_data");

                                if (j.length() == 0) {

                                } else {

                                    for (int i = 0; i < j.length(); i++) {
                                        AttendanceData ad = new AttendanceData(j.getJSONObject(i));
                                        attendanceDataMap.put(ad.getId(), ad);
                                        //postAttendanceDataMap.put(ad.getId(), new PostAttendanceData(ad.getId(), ad.getAcronym(), ad.getRemarks()));
                                    }

                                    attendanceDataListAdapter.notifyDataSetChanged();

                                }


                            } catch (JSONException e) {

                                Log.i("MAA", "(PULL TO REFRESH) Inside Catch!!");

                            }

                            swipeRefreshLayout.setRefreshing(false);

                        }

                    }.execute();
                }

            }
        });

        swipeRefreshLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


    }

    public int countByAcronym(String a) {
        int c = 0;
        for (String i : ids) {
            if (attendanceDataMap.get(i).getAcronym().equals(a)) {
                c++;
            }
        }
        return c;
    }

    public ArrayList<String> getSummary() {
        ArrayList<Statuses> sataus = GlobalJSONObjects.getInstance().getUser().getCourse().get(coursePosition).getAttendance().get(attendancePosition).getStatuses();
        ArrayList<String> msg = new ArrayList<String>();
        for (Statuses s : sataus) {
            msg.add("Total " + s.getDescription() + " :" + countByAcronym(s.getAcronym()) + "\n\n");
        }
        return msg;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.fill_attendance_menu, menu);

        this.menu = menu;

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:


                closeActivity();


                return true;

            case R.id.action_save_attendance:

                String msg = "";

                for (String s : getSummary()) {
                    msg = msg + s;
                }

                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
                dlgAlert.setMessage(msg);
                dlgAlert.setTitle("Attendance Summary");
                dlgAlert.setPositiveButton("Save", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Dialog d = (Dialog) dialog;
                        Context context = d.getContext();

                        if (ServiceHandler.hasActiveInternetConnection(context)) {
                            saveAttendance(context);
                        }


                    }
                });
                dlgAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                });

                dlgAlert.setCancelable(true);
                dlgAlert.create().show();

                return true;


        }

        return super.onOptionsItemSelected(item);
    }


    public void saveAttendance(Context c) {
        final String statusSet = GlobalJSONObjects.getInstance().getUser().getCourse().get(coursePosition).getAttendance().get(attendancePosition).getStatusSet();
        final String takenBy = GlobalJSONObjects.getInstance().getUser().getId();

        ArrayList<AttendanceData> tmpAttendanceData = new ArrayList<AttendanceData>();

        for (String id : ids) {
            tmpAttendanceData.add(attendanceDataMap.get(id));
        }

        final String data = AttendanceData.toJSON(tmpAttendanceData);

        final Context context = c;

        final Boolean fresh = isFresh;

        Log.i("MAA", "SESSION ID: " + sessionId + " status set: " + statusSet + " TAKEN BY " + takenBy + " DATA " + data);

        new AsyncTask<Void, Void, Void>() {

            String response = "";

            ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {

                progressDialog = null;
                progressDialog = ProgressDialog.show(context, "Please Wait...", "Saving Attendance...", true);
                progressDialog.show();

            }

            @Override
            protected Void doInBackground(Void... params) {
                ServiceHandler sh = new ServiceHandler();
                long unixTime = System.currentTimeMillis() / 1000L;
                String timestamp = String.valueOf(unixTime);
                if (fresh) {
                    response = sh.uploadAttendanceData(sessionId, statusSet, takenBy, timestamp, data);
                } else {
                    response = sh.updateAttendanceData(sessionId, statusSet, takenBy, timestamp, data);
                }
                return null;
            }


            @Override
            protected void onPostExecute(Void result) {

                Log.i("MAA", "response: " + response);

                try {
                    JSONObject obj = new JSONObject(response);
                    Response resp = new Response(obj.getJSONObject("response"));
                    Log.i("MAA", "Message: " + resp.getMessage() + "Comment: " + resp.getComment());

                    Toast.makeText(context, resp.getComment(), Toast.LENGTH_SHORT).show();

                    //FillAttendanceActivity.this.finish();

                } catch (JSONException e) {


                    try {
                        ErrorObj err = new ErrorObj(response);
                        Log.i("MAA", "Message: " + err.getMessage() + "Comment: " + err.getComment());
                    } catch (JSONException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }


                    e.printStackTrace();
                }


                isChanged = false;

                progressDialog.dismiss();


            }


        }.execute();
    }


    public void SetActionBar() {
        mActionBar = (Toolbar) findViewById(R.id.app_bar);
        mActionBar.setTitle(actionBarTitle);
        mActionBar.setSubtitle(actionBarSubTitle);
        //mActionBar.setDisplayHomeAsUpEnabled(true);
        setSupportActionBar(mActionBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    public void getAttendanceForSession(String sessionId, Context c) {
        final String sesId = sessionId;

        final Context context = c;

        new AsyncTask<Void, Void, Void>() {

            String response = "";

            ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                progressDialog = null;
                progressDialog = ProgressDialog.show(context, "Please Wait...", "Preparing Attendance...", true);
                progressDialog.show();
            }

            @Override
            protected Void doInBackground(Void... params) {

                ServiceHandler sh = new ServiceHandler();
                response = sh.getAttendanceData(GlobalJSONObjects.getInstance().getUser().getToken(), sesId);


                Log.i("MAA", " session id is: " + sesId + "");
                Log.i("MAA", response);

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {

                setAttendanceSelector(GlobalJSONObjects.getInstance().getUser().getCourse().get(coursePosition).getAttendance().get(attendancePosition).getStatuses());

                try {
                    JSONObject obj = new JSONObject(response);
                    JSONArray j = obj.getJSONArray("attendance_data");

                    if (j.length() == 0) {
                        throw new JSONException("Empty JSON ARRAY");
                    } else {

                        for (int i = 0; i < j.length(); i++) {
                            AttendanceData ad = new AttendanceData(j.getJSONObject(i));
                            attendanceDataMap.put(ad.getId(), ad);
                            ids.add(ad.getId());

                            //postAttendanceDataMap.put(ad.getId(), new PostAttendanceData(ad.getId(), ad.getAcronym(), ad.getRemarks()));
                        }

                    }


                    //success=true;

                    if (GlobalSettings.getInstance().useIdAsPrefix()) {
                        Map<String, AttendanceData> map = new TreeMap<String, AttendanceData>(attendanceDataMap);
                        setupListView(context, map, ids);
                    } else {
                        sortStudentsByName(attendanceDataMap);
                        setupListView(context, attendanceDataMap, ids);
                    }


                } catch (JSONException e) {

                    isFresh = true;

                    Log.i("MAA", "Inside Catch!!");

                    for (EnrolledStudents s : GlobalJSONObjects.getInstance().getUser().getCourse().get(coursePosition).getEnrolledStudents()) {
                        Log.i("MAA", s.getFirst_name());
                        ids.add(s.getUser_id());
                        attendanceDataMap.put(s.getUser_id(), new AttendanceData(s.getUser_id(), GlobalJSONObjects.getInstance().getUser().getCourse().get(coursePosition).getAttendance().get(attendancePosition).getStatuses().get(0).getId(), GlobalJSONObjects.getInstance().getUser().getCourse().get(coursePosition).getAttendance().get(attendancePosition).getStatuses().get(0).getAcronym(), "", s.getFirst_name(), s.getLast_name()));
                    }

                    sortStudentsByName(attendanceDataMap);

                    setupListView(context, attendanceDataMap, ids);

                }

                progressDialog.dismiss();

            }

        }.execute();
    }


    public void setupListView(Context context, Map<String, AttendanceData> attendanceDataMap, ArrayList<String> ids) {
        attendanceDataListAdapter = new AttendanceDataListAdapter(context, attendanceDataMap, ids);

        lvAttendanceData.setAdapter(attendanceDataListAdapter);

        //lvAttendanceData.setOnItemClickListener(null);
        lvAttendanceData.setOnItemClickListener(attendanceDataListAdapter);
    }


    class AttendanceDataListAdapter extends BaseAdapter implements OnItemClickListener {

        //ArrayList<EnrolledStudents> enrolledStudents=new ArrayList<EnrolledStudents>();


        Map<String, AttendanceData> attendanceDataMap = new HashMap<String, AttendanceData>();

        ArrayList<String> ids = new ArrayList<String>();

        //ArrayList<String> radioStatus=new ArrayList<String>();

        Context context;

        public AttendanceDataListAdapter(Context context, Map<String, AttendanceData> attendanceDataMap, ArrayList<String> ids) {

            this.context = context;
            this.attendanceDataMap = attendanceDataMap;
            this.ids = ids;

        }


        private class RadioBtn {

            ArrayList<RadioButton> radioArrList = new ArrayList<RadioButton>();

            public ArrayList<RadioButton> generateRadioButtons(ArrayList<Statuses> statuses) {

                for (Statuses s : statuses) {
                    if (s.getVisible().equals("1")) {
                        RadioButton r = new RadioButton(context);
                        r.setText(s.getAcronym());
                        r.setHint(s.getDescription());
                        r.setHintTextColor(Color.WHITE);
                        r.setTag(s);
                        r.setFocusable(false);
                        r.setFocusableInTouchMode(false);
                        RadioGroup.LayoutParams p = new RadioGroup.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        p.weight = 1;
                        r.setLayoutParams(p);
                        r.setTextColor(Color.WHITE);
                        radioArrList.add(r);
                    }

                }

                return radioArrList;

            }

            public RadioButton getByAcronym(String a) {
                for (RadioButton r : radioArrList) {
                    if (r.getText().equals(a)) {
                        return r;
                    }
                }
                return null;
            }

        }


        private class ViewHolder {
            TextView tvStudentFullName;

            RadioGroup radioGroup;

            RadioBtn rb;

            public void setRadioBtn(ArrayList<Statuses> s) {
                rb = new RadioBtn();
                for (RadioButton r : rb.generateRadioButtons(s)) {
                    radioGroup.addView(r);
                }
            }

        }


        @Override
        public int getCount() {

            return attendanceDataMap.size();
        }

        @Override
        public Object getItem(int arg0) {

            return attendanceDataMap.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final int pos = position;

            final ViewHolder holder;
            //Log.v("ConvertView", String.valueOf(position));

            //final View v=convertView;

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);

                convertView = vi.inflate(R.layout.single_attendance_data_row, null);

                holder = new ViewHolder();
                holder.radioGroup = (RadioGroup) convertView.findViewById(R.id.rgAttendanceStatus);
                holder.setRadioBtn(GlobalJSONObjects.getInstance().getUser().getCourse().get(coursePosition).getAttendance().get(attendancePosition).getStatuses());

                holder.tvStudentFullName = (TextView) convertView.findViewById(R.id.tvStudentFullName);




                convertView.setTag(holder);




            } else {

                holder = (ViewHolder) convertView.getTag();

            }


            if (GlobalSettings.getInstance().useIdAsPrefix() == true && GlobalSettings.getInstance().showStudentFullName() == true) {
                holder.tvStudentFullName.setText(attendanceDataMap.get(ids.get(position)).getId() + ". " + attendanceDataMap.get(ids.get(position)).getFirst_name() + " " + attendanceDataMap.get(ids.get(position)).getLast_name());
            }
            if (GlobalSettings.getInstance().useIdAsPrefix() == false && GlobalSettings.getInstance().showStudentFullName() == false) {
                holder.tvStudentFullName.setText(attendanceDataMap.get(ids.get(position)).getFirst_name());
            }
            if (GlobalSettings.getInstance().useIdAsPrefix() == false && GlobalSettings.getInstance().showStudentFullName() == true) {
                holder.tvStudentFullName.setText(attendanceDataMap.get(ids.get(position)).getFirst_name() + " " + " " + attendanceDataMap.get(ids.get(position)).getLast_name());
            }
            if (GlobalSettings.getInstance().useIdAsPrefix() == true && GlobalSettings.getInstance().showStudentFullName() == false) {
                holder.tvStudentFullName.setText(attendanceDataMap.get(ids.get(position)).getId() + ". " + attendanceDataMap.get(ids.get(position)).getFirst_name());
            }




            holder.radioGroup.setOnCheckedChangeListener(null);

            String acronym = GlobalJSONObjects.getInstance().getUser().getCourse().get(coursePosition).getAttendance().get(attendancePosition).getStatuses().get(0).getAcronym();

            if (attendanceDataMap.get(ids.get(position)).getAcronym().equals(acronym)) {
                holder.rb.getByAcronym(attendanceDataMap.get(ids.get(position)).getAcronym()).setChecked(true);
                Log.i("MAA", "checking in if radioStatus at pos " + position + " is " + attendanceDataMap.get(ids.get(position)));

            } else {
                Log.i("MAA", "checking in else radioStatus at pos " + position + " is " + attendanceDataMap.get(ids.get(position)));
                try {
                    holder.rb.getByAcronym(attendanceDataMap.get(ids.get(position)).getAcronym()).setChecked(true);
                } catch (NullPointerException e) {
                    // TODO: handle exception
                }
            }


            holder.radioGroup.setBackgroundColor(Color.parseColor(GlobalSettings.getInstance().getStatusColor(GlobalSettings.getInstance().getmSharedPreferences(), attendanceDataMap.get(ids.get(position)).getAcronym())));
            holder.tvStudentFullName.setBackgroundColor(Color.parseColor(GlobalSettings.getInstance().getStatusColor(GlobalSettings.getInstance().getmSharedPreferences(), attendanceDataMap.get(ids.get(position)).getAcronym())));


            //			   if(attendanceDataMap.get(ids.get(position)).getAcronym().equals("P") || attendanceDataMap.get(ids.get(position)).getAcronym().equals("L"))
            //			   {
            //				   holder.radioGroup.setBackgroundColor(Color.parseColor("#8BC34A"));
            //				   holder.tvStudentFullName.setBackgroundColor(Color.parseColor("#8BC34A"));
            //			   }
            //			   else
            //			   {
            //				   if(attendanceDataMap.get(ids.get(position)).getAcronym().equals("A"))
            //				   {
            //					   holder.radioGroup.setBackgroundColor(Color.parseColor("#F44336"));
            //					   holder.tvStudentFullName.setBackgroundColor(Color.parseColor("#F44336"));
            //				   }
            //				   else
            //				   {
            //					   holder.radioGroup.setBackgroundColor(Color.parseColor("#EF6C00"));
            //					   holder.tvStudentFullName.setBackgroundColor(Color.parseColor("#EF6C00"));
            //				   }
            //			   }


            holder.radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {

                    isChanged = true;

                    RadioButton rb = (RadioButton) findViewById(checkedId);
                    Statuses s = (Statuses) rb.getTag();

                    //radioStatus.set(pos, s.getAcronym());

                    attendanceDataMap.get(ids.get(pos)).setAcronym(s.getAcronym());

                    attendanceDataMap.get(ids.get(pos)).setStatus_id(s.getId());

                    Log.i("MAA", "id: " + s.getId() + " description: " + s.getDescription() + " local");
                    Log.i("MAA", "id: " + attendanceDataMap.get(ids.get(pos)).getStatus_id() + " description: " + attendanceDataMap.get(ids.get(pos)).getAcronym() + " from hashmap");

                    //postAttendanceDataMap.get(students.get(pos).getUser_id()).setStatus(s.getId());


                    holder.radioGroup.setBackgroundColor(Color.parseColor(GlobalSettings.getInstance().getStatusColor(GlobalSettings.getInstance().getmSharedPreferences(), s.getAcronym())));
                    holder.tvStudentFullName.setBackgroundColor(Color.parseColor(GlobalSettings.getInstance().getStatusColor(GlobalSettings.getInstance().getmSharedPreferences(), s.getAcronym())));

                    //						if(s.getAcronym().equals("P") || s.getAcronym().equals("L"))
                    //						   {
                    //							   holder.radioGroup.setBackgroundColor(Color.parseColor("#8BC34A"));
                    //							   holder.tvStudentFullName.setBackgroundColor(Color.parseColor("#8BC34A"));
                    //						   }
                    //						   else
                    //						   {
                    //							   if(s.getAcronym().equals("A"))
                    //							   {
                    //								   holder.radioGroup.setBackgroundColor(Color.parseColor("#F44336"));
                    //								   holder.tvStudentFullName.setBackgroundColor(Color.parseColor("#F44336"));
                    //							   }
                    //							   else
                    //							   {
                    //								   holder.radioGroup.setBackgroundColor(Color.parseColor("#EF6C00"));
                    //								   holder.tvStudentFullName.setBackgroundColor(Color.parseColor("#EF6C00"));
                    //							   }
                    //						   }


                    //Log.i("MAA", "(present) id: "+postAttendanceDataMap.get(students.get(pos).getUser_id()).getId()+" status:"+postAttendanceDataMap.get(students.get(pos).getUser_id()).getStatus());
                    //Log.i("MAA", "radio status: "+radioStatus.get(pos));
                    //Log.i("MAA", "{\"d\":"+AttendanceData.toJSON(attendanceData)+"}");

                }
            });

            return convertView;

        }

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int p,
                                long arg3) {


            String fn = "First Name: " + attendanceDataMap.get(ids.get(p)).getFirst_name();
            String ln = "Last Name: " + attendanceDataMap.get(ids.get(p)).getLast_name();
            String as = "Attendance Status: " + attendanceDataMap.get(ids.get(p)).getDescription();
            String tb;
            if (attendanceDataMap.get(ids.get(p)).getTaken_by() != null) {
                tb = "Taken By: " + attendanceDataMap.get(ids.get(p)).getTaken_by().getFirst_name() + " " + attendanceDataMap.get(ids.get(p)).getTaken_by().getLast_name();
            } else {
                tb = "Taken By: none";
            }
            String rem = "Remark: " + attendanceDataMap.get(ids.get(p)).getRemarks();
            String t = "Time Taken: " + attendanceDataMap.get(ids.get(p)).getTime_taken(false);

            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(context);
            dlgAlert.setMessage(fn + "\n\n" + ln + "\n\n" + as + "\n\n" + tb + "\n\n" + rem + "\n\n" + t);
            dlgAlert.setTitle("Student Information");
            dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();

                }
            });
            dlgAlert.setCancelable(true);
            dlgAlert.create().show();

        }


    }

}

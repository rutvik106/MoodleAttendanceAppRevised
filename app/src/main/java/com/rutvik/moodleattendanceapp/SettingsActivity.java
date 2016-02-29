package com.rutvik.moodleattendanceapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.rutvik.moodleattendanceapp.R;

public class SettingsActivity extends AppCompatActivity {
	
	
	Toolbar mActionBar;
	
	ListView listView;
	
	HashMap<String, String> colorMap=new HashMap<String, String>();
	
	List<String> colorList = new ArrayList<String>();
	
	ArrayList<String> arrayListAcronym=new ArrayList<String>();
	
	SettingsListViewAdapter listViewAdapter;
	
	ArrayAdapter<String> spinnerAdapter;
	
	SharedPreferences mSharedPreferences;
	
	Menu menu;
	
	CheckBox chkUsePrefix,chkShowUserFullName,chkSortStudentByName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		mSharedPreferences = getSharedPreferences("moodle_attendance_app_shared_pref", Context.MODE_PRIVATE);
		
		GlobalJSONObjects.getInstance().restoreJSONObjects(mSharedPreferences);
		

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

        SetActionBar();
		
		mSharedPreferences = getSharedPreferences("moodle_attendance_app_shared_pref", Context.MODE_PRIVATE);

		chkUsePrefix=(CheckBox)findViewById(R.id.chk_toggleIdAsPrefix);

		chkUsePrefix.setChecked(GlobalSettings.getInstance().useIdAsPrefix());
		
		chkUsePrefix.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
				GlobalSettings.getInstance().useIdAsPrefix(isChecked);
				
			}
		});
		
		chkShowUserFullName=(CheckBox)findViewById(R.id.chk_showStudentFullName);
		
		chkShowUserFullName.setChecked(GlobalSettings.getInstance().showStudentFullName());
		
		chkShowUserFullName.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
				GlobalSettings.getInstance().showStudentFullName(isChecked);
				
			}
		});
		
		chkSortStudentByName=(CheckBox) findViewById(R.id.chk_toggleSortStudentByName);
		
		chkSortStudentByName.setChecked(GlobalSettings.getInstance().sortStudentByName());
		
		chkSortStudentByName.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
				GlobalSettings.getInstance().sortStudentByName(isChecked);
				
			}
		});
		
		listView=(ListView) findViewById(R.id.lvSettingsAttributes);
		
		colorMap.put("Red", "#F44336");
		colorMap.put("Green", "#8BC34A");
		colorMap.put("None", "#FF9800");
		
		colorList.add("Red");
		colorList.add("Green");
		colorList.add("None");
		
		
		
		spinnerAdapter = new ArrayAdapter<String>(this,
			R.layout.spinner_item, colorList);
		
		arrayListAcronym =GlobalJSONObjects.getInstance().getUser().getAcronymArrayList();
		
		
		listViewAdapter=new SettingsListViewAdapter(this, arrayListAcronym, spinnerAdapter);
		
		listView.setAdapter(listViewAdapter);
		
	}
	
	
	public void SetActionBar() {
		mActionBar = (Toolbar) findViewById(R.id.app_bar);
		//mActionBar.setDisplayShowTitleEnabled(true);

		mActionBar.setTitle("Settings");
		
		setSupportActionBar(mActionBar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId()==android.R.id.home)
		{
			this.finish();
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	
	class SettingsListViewAdapter extends BaseAdapter
	{

		ArrayList<String> arrayListAcronym;
		
		Context context;
		
		ArrayAdapter<String> spinnerAdapter;
		
		public SettingsListViewAdapter(Context context,ArrayList<String> arrayListAcronym,ArrayAdapter<String> spinnerAdapter) {
			
			this.context=context;
			this.arrayListAcronym=arrayListAcronym;
			this.spinnerAdapter=spinnerAdapter;
			this.spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
			
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return arrayListAcronym.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return arrayListAcronym.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return arrayListAcronym.get(position).hashCode();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			final ListDataHolder holder;
			
			if(convertView==null)
			{
				 LayoutInflater vi = (LayoutInflater)getSystemService(
					     Context.LAYOUT_INFLATER_SERVICE);
					   
					   convertView = vi.inflate(R.layout.single_settings_row, null);
					 
					   holder = new ListDataHolder();
					   holder.settingAttribute=(TextView) convertView.findViewById(R.id.tvAttribute);
					   holder.spinner=(Spinner) convertView.findViewById(R.id.spinValues);
					   
					   holder.spinner.setAdapter(spinnerAdapter);
					   
					   
					   convertView.setTag(holder);
				
			}
			else
			{
				holder=(ListDataHolder) convertView.getTag();
			}
			
			holder.settingAttribute.setText(arrayListAcronym.get(position));
			
			holder.spinner.setOnItemSelectedListener(null);	
			
			String colorValue=GlobalSettings.getInstance().getStatusColor(mSharedPreferences, arrayListAcronym.get(position));
			
			int index=0;
			
			if(colorValue.equals("#F44336"))
			{
				index=colorList.indexOf("Red");
			}
			if(colorValue.equals("#8BC34A"))
			{
				index=colorList.indexOf("Green");
			}
			if(colorValue.equals("#FF9800"))
			{
				index=colorList.indexOf("None");
			}
			
			holder.spinner.setSelection(index);
			
			holder.spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int pos, long arg3) {
					
					Log.i("MAA","Acronym: "+holder.settingAttribute.getText().toString()+"Selected Color: "+colorList.get(pos)+"value is: "+colorMap.get(colorList.get(pos)) );
					
					GlobalJSONObjects.getInstance().getUser().setStatusColor(mSharedPreferences, holder.settingAttribute.getText().toString(), colorMap.get(colorList.get(pos)));
					
					
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub
					
				}
			});			
			
			return convertView;
			
		}
		
		
		
		private class ListDataHolder
		{
			TextView settingAttribute;
			Spinner spinner;
		}



	
		
		
	}
	
	
}

package com.benaliahmed.dailyfitness.run;

import android.app.AlertDialog;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.benaliahmed.dailyfitness.R;
import com.benaliahmed.dailyfitness.database.LocalDbManager;


public class progressFragment extends Fragment {
    ListView listView;
    listViewAdapter adapter;
    String date, duration, distance, speed, calories;
    String str_duration, str_distance, str_speed, str_calories;
    String id;
    AlertDialog dialog;
    AlertDialog.Builder dialog_builder;
    Button yes_btn, no_btn;

    @Override
    public void onStart() {
        super.onStart();
        adapter = new listViewAdapter();
        listView = getView().findViewById(R.id.list_view);
        listView.setAdapter(adapter);

        Cursor cursor = null;
        try {
            LocalDbManager dbManager = new LocalDbManager(getContext());
            SQLiteDatabase sdb = dbManager.getReadableDatabase();
            cursor = sdb.rawQuery("select id,date,distance,duration,speed,calories from stats_table", null);
            while (cursor.moveToNext()) {
                id = cursor.getString(0);
                date = cursor.getString(1);
                distance = cursor.getString(2);
                duration = cursor.getString(3);
                speed = cursor.getString(4);
                calories = cursor.getString(5);
                str_distance="distance= "+distance+" Km";
                str_duration="duration= "+duration+" Min";
                str_speed="average speed= "+speed+" M/Min";
                str_calories="calories= "+calories;
                adapter.addItem(Integer.parseInt(id), date, str_duration, str_distance, str_speed, str_calories);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {

                dialog_builder = new AlertDialog.Builder(getContext());
                final View popup_view = getLayoutInflater().inflate(R.layout.delete_popup, null);
                yes_btn = popup_view.findViewById(R.id.yes_btn);
                no_btn = popup_view.findViewById(R.id.no_btn);

                dialog_builder.setView(popup_view);
                dialog = dialog_builder.create();
                dialog.show();

                yes_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String id = String.valueOf(adapter.getItemId(i));
                        //Toast.makeText(getContext(), id, Toast.LENGTH_SHORT).show();
                        try {
                            LocalDbManager dbManager = new LocalDbManager(getContext());
                            SQLiteDatabase sdb = dbManager.getWritableDatabase();
                            sdb.execSQL("delete from stats_table where id='" + id + "' ");
                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                            Fragment frag = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                            ft.detach(frag).attach(frag).commit();
                            dialog.dismiss();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                });
                no_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_progress, container, false);
        // Inflate the layout for this fragment
        return view;
    }
}
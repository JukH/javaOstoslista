package com.example.jukka.javaostoslista;

import android.app.AlertDialog;
import android.app.LauncherActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class reseptiLista extends Activity {


    ArrayList<String> reseptit = null;
    ArrayAdapter<String> adapter = null;
    ListView lv = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resepti_lista);


        reseptit = new ArrayList<>();
        Collections.addAll(reseptit, "Linssikeitto", "Kasvislasagne", "Soijalasagne", "Soijakastike ja spagetti", "Tortillapizzat");
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, reseptit); lv = (ListView) findViewById(R.id.listView); lv.setAdapter(adapter);


        //lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                //public void onItemClick(AdapterView<?> parentAdapter, View view, int position,
                  //                      long id) {

                    //AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    //alertDialog.setTitle("Ohje");
                    //alertDialog.setMessage("Valitsemalla ruokalajin tarvittavat raaka-aineet lisätään ostoslistalle. +-Symbolia painamalla voit lisätä yksittäisiä ostoksia.");
                    //alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                      //      new DialogInterface.OnClickListener() {
                        //        public void onClick(DialogInterface dialog, int which) {
                          //          dialog.dismiss();
                            //    }
                            //});
                    //alertDialog.show();

             //   }
            //});
        }
    }



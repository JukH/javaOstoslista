package com.example.jukka.javaostoslista;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import android.app.AlertDialog;
import android.widget.EditText;
import android.content.DialogInterface;
import android.content.Context;
import android.content.SharedPreferences;
import android.app.Activity;

import android.widget.Toast;
import android.widget.AdapterView;
import android.widget.TextView;
import android.view.View;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    int sipMäärä = 1;
    int valkSipMäärä = 1;
    int linssitMäärä = 1;
    int öljyMäärä = 1;
    int limeMäärä = 1;
    int pyreeMäärä = 1;
    int suolaMäärä = 1;
    int chiliMäärä = 1;
    int oreMäärä = 1;
    int soijaMäärä = 1;
    int jauhoMäärä = 1;
    int muskottiMäärä = 1;
    int murskaMäärä = 1;
    int pippuriMäärä = 1;
    int levyMäärä = 1;
    int paViMäärä = 1;
    int juustoMäärä = 1;
    int riisiMäärä = 1;
    int kookosMaitoMäärä = 1;
    int tortillaMäärä = 1;
    int pastaKastikeMäärä = 1;
    int homeMäärä = 1;
    int mozzarellaMäärä = 1;
    int wipesMäärä = 1;
    int riisiKakkuMäärä = 1;
    int pepsiMäärä = 1;
    int pestoMäärä = 1;
    int jogurttiMäärä = 1;

    ArrayList<String> shoppingList = null;
    ArrayAdapter<String> adapter = null;
    ListView lv = null;


    //18.1.2019
    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //18.1.2019 //////////////////////////////////////////////////////////////////////////
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("ostos");

        //lv = (ListView) findViewById(R.id.listView); // TARVIIKO TÄTÄ TOISTA?
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                String value = dataSnapshot.getValue(String.class);
                shoppingList.add(value); //Lisää tuplana listalle uudelleen avatteassa appia?
                adapter.notifyDataSetChanged();
                Collections.sort(shoppingList);
                //storeArrayVal(shoppingList, getApplicationContext()); TÄSSÄ VIKA ETTÄ LISÄSI TUPLANA KÄYNNISTETTÄESSÄ.. TUTKI LISÄÄ!
                lv.setAdapter(adapter);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                //18.1.2019/////////////////////////////////////////////////////////
                String value = dataSnapshot.getValue(String.class);
                shoppingList.remove(value);
                adapter.notifyDataSetChanged();
                Collections.sort(shoppingList);
                //storeArrayVal(shoppingList, getApplicationContext());
                lv.setAdapter(adapter);
                ////////////////////////////////////////////////////////////////////
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        ///////////////////////////////////////////////////////////////////////////18.1.2019


        shoppingList = getArrayVal(getApplicationContext());
        Collections.sort(shoppingList);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, shoppingList);
        lv = (ListView) findViewById(R.id.listView);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View view, final int position, long id) {
                String selectedItem = ((TextView) view).getText().toString();
                if (selectedItem.trim().equals(shoppingList.get(position).trim())) {
                    removeElement(selectedItem, position);
                } else {
                    Toast.makeText(getApplicationContext(),"Can not be removed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_ohje) {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Ohje");
            alertDialog.setMessage("Testataan branchjuttuja");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }

        //if (id == R.id.action_settings) {
          //  Intent mene = new Intent(MainActivity.this, reseptiLista.class );
           // startActivity(mene);
           // return true;
       // }
        //Ylläolevalla saadaan avattua uusi activity menun kautta, jätetään muistiinpanoksi.
        //*******************************************************************************************************Yksittäisen ostoksen lisäystoiminnot:
        if(id == R.id.lisääOstos) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Lisää ostos");
            final EditText input = new EditText(this);
            builder.setView(input);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //LISÄÄ TÄHÄN ETTÄ MENEE FIREBASEEN!

                    //KOMMENTIKSI MUUTETTU 18.1.2019 (OFFLINE-LISÄYS LISTALLE)

                    //shoppingList.add(preferredCase(input.getText().toString())); //OFFLINE-LISÄYS
                    //Collections.sort(shoppingList);
                    //storeArrayVal(shoppingList, getApplicationContext());
                    //lv.setAdapter(adapter);

                    //FIREBASETESTAUS/LISÄÄMINEN
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference();
                    String key = input.getText().toString();

                    myRef.child("ostos").child(key).setValue(input.getText().toString());
                    //myRef.child("ostos").push().setValue(input.getText().toString()); //Määritetään tietokannan juurelle lapsi johon laitetaan dataa
                    String pushId = myRef.getKey();



                    //*****************************

                    // Attach a listener to read the data at our posts reference



                    //LISÄÄ TÄHÄN ETTÄ MENEE FIREBASEEN!
                }
            });
            builder.setNegativeButton("Peruuta", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.cancel();
                }
            });
            builder.show();
            return true;
        }
        //KOKO LISTAN TYHJENNYS ////////////////////////////////////////////////////////////////////////////////////////////////////////
        if(id == R.id.action_clear) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Tyhjennä koko lista?");
            builder.setPositiveButton("Tyhjennä", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //shoppingList.clear();
                    //Collections.sort(shoppingList);
                    //storeArrayVal(shoppingList, getApplicationContext());
                    //lv.setAdapter(adapter);
                    myRef.setValue(null);
                }
            });
            builder.setNegativeButton("Peruuta", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
            return true;
        }

        if(id == R.id.action_reseptiOpetus){


            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Opeta uusi resepti");
            builder.setPositiveButton("Peruuta", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {


                }
            });
            builder.setNegativeButton("Lisää raaka-aineet", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();
            return true;
        }

        if(id == R.id.action_kasvisLasagne) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Kasvislasagne");
            builder.setPositiveButton("Lisää listalle", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {



                }
            });
            builder.setNegativeButton("Valmistusohje", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();
            return true;
        }

        if(id == R.id.action_kasvisKastikeRiisillä) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Kasviskastike riisillä");
            builder.setPositiveButton("Lisää listalle", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {



                }
            });
            builder.setNegativeButton("Valmistusohje", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();
            return true;
        }

        if(id == R.id.action_tortillaPizzat) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Tortillapizzat");
            builder.setPositiveButton("Lisää listalle", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.setNegativeButton("Valmistusohje", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();
            return true;
        }

        if(id == R.id.action_perusSetti) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Perussetti");
            builder.setPositiveButton("Lisää listalle", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {



                }
            });
            builder.setNegativeButton("Sisältö", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    public static String preferredCase(String original)
    {
        if (original.isEmpty())
            return original;

        return original.substring(0, 1).toUpperCase() + original.substring(1).toLowerCase();
    }


    public static void storeArrayVal( ArrayList<String> inArrayList, Context context)
    {
        Set<String> WhatToWrite = new HashSet<String>(inArrayList);
        SharedPreferences WordSearchPutPrefs = context.getSharedPreferences("dbArrayValues", Activity.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = WordSearchPutPrefs.edit();
        prefEditor.putStringSet("myArray", WhatToWrite);
        prefEditor.commit();
    }

    public static ArrayList getArrayVal( Context dan)
    {
        SharedPreferences WordSearchGetPrefs = dan.getSharedPreferences("dbArrayValues",Activity.MODE_PRIVATE);
        Set<String> tempSet = new HashSet<String>();
        tempSet = WordSearchGetPrefs.getStringSet("myArray", tempSet);
        return new ArrayList<String>(tempSet);
    }
    //TUOTTEEN POISTAMINEN
    public void removeElement(final String selectedItem, final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Poista " + selectedItem + "?");
        builder.setPositiveButton("Poista", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //19.1.2019///////////////////////////////////
                String listaTeksti =(lv.getItemAtPosition(position).toString());
                myRef.child(listaTeksti).removeValue();
                //////////////////////////////////////////////
                shoppingList.remove(position);
                //18.1.2019//POISTO MYÖS FIREBASESTA/////////////////////////////
                //myRef.setValue(null); //poistaa kaiken TOIMII!!! JATKA TÄSTÄ!!!!

                /////////////////////////////////////////////////////////////////
                adapter.notifyDataSetChanged();
                Collections.sort(shoppingList);
                //storeArrayVal(shoppingList, getApplicationContext());
                lv.setAdapter(adapter);




            }
        });
        builder.setNegativeButton("Peruuta", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
}

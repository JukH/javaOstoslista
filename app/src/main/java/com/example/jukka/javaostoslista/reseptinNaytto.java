package com.example.jukka.javaostoslista;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;

public class reseptinNaytto extends AppCompatActivity {


    ArrayList<String> raaka_aineet = null;
    ArrayAdapter<String> adapter = null;
    ListView lv = null;

    FirebaseDatabase database;
    DatabaseReference myRef;





    //Toimii ja asettaa reseptinäytölle oikean tittelin 19.2.2019
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reseptin_naytto);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Tuodaan edellisestä activitystä ruokalajin nimi
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String  ruokaLaji = bundle.getString("key");
        reseptinNaytto.this.setTitle(ruokaLaji + " ainekset");







        raaka_aineet = new ArrayList<>();
        Collections.addAll(raaka_aineet); //Lisää kokonaisen setin tarvittaessa.
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, raaka_aineet);
        lv = (ListView) findViewById(R.id.listView);
        lv.setAdapter(adapter);



        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("reseptit/"+ruokaLaji);

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                String value = dataSnapshot.getValue(String.class);
                raaka_aineet.add(value); //Lisää tuplana listalle uudelleen avatteassa appia?
                adapter.notifyDataSetChanged();
                Collections.sort(raaka_aineet);
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
                raaka_aineet.remove(value);
                adapter.notifyDataSetChanged();
                Collections.sort(raaka_aineet);
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



        /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nyt ollaan reseptiruudussa");
        final EditText raaka_aine = new EditText(this);
        builder.setView(raaka_aine);
        builder.setPositiveButton("Lisää", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //LISÄÄ TÄHÄN ETTÄ MENEE FIREBASEEN!

                //KOMMENTIKSI MUUTETTU 18.1.2019 (OFFLINE-LISÄYS LISTALLE)

                //shoppingList.add(preferredCase(input.getText().toString())); //OFFLINE-LISÄYS
                //Collections.sort(shoppingList);
                //storeArrayVal(shoppingList, getApplicationContext());
                //lv.setAdapter(adapter);

                // LISÄTÄÄN RAAKA-AINEET LISTALLE JOTTA NIITÄ VOI TARKASTELLA/POISTAA 3.2.2019

                raaka_aineet.add(raaka_aine.getText().toString());


                //FIREBASETESTAUS/LISÄÄMINEN
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference();
                String key = raaka_aine.getText().toString();

                //Bundle extras = getIntent().getExtras(); //3.2.2019 TUODAAN mainactivitystä ruokalajin nimi

                //String ruokaLaji = extras.getString("key");
                //The key argument here must match that used in the other activity


                //myRef.child("reseptit").child(ruokaLaji).setValue(raaka_aine.getText().toString()); //3.2.2019 Annetaan ruokalajille inputin mukaan nimi tietokantaan
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
        builder.show(); */



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




    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_reseptinnaytto, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_ohje){
            Toast.makeText(this, "Lisää raaka-aine", Toast.LENGTH_SHORT).show();
        }

        if (id == R.id.lisääRaakaAine){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Lisää raaka-aine");
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

                        Intent intent1 = getIntent();
                        Bundle bundle1 = intent1.getExtras();
                        String resepti = bundle1.getString("key");


                    //FIREBASETESTAUS/LISÄÄMINEN
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("reseptit/" + resepti);
                    String key = input.getText().toString();

                    myRef.child(key).setValue(input.getText().toString());
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
        Toast.makeText(this, "Raaka-aine lisätty", Toast.LENGTH_SHORT).show();

        return super.onOptionsItemSelected(item);
    }
}



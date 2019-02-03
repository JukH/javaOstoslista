package com.example.jukka.javaostoslista;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;

public class reseptinNäyttö extends AppCompatActivity {


    ArrayList<String> reseptit = null;
    ArrayAdapter<String> adapter = null;
    ListView lv = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reseptin_naytto);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);





        reseptit = new ArrayList<>();
        Collections.addAll(reseptit); //Lisää kokonaisen setin tarvittaessa.
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, reseptit); lv = (ListView) findViewById(R.id.listView); lv.setAdapter(adapter);



        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

                reseptit.add(raaka_aine.getText().toString());


                //FIREBASETESTAUS/LISÄÄMINEN
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference();
                String key = raaka_aine.getText().toString();

                Bundle extras = getIntent().getExtras(); //3.2.2019 TUODAAN mainactivitystä ruokalajin nimi

                String ruokaLaji = extras.getString("key");
                //The key argument here must match that used in the other activity


                myRef.child("reseptit").child(ruokaLaji).setValue(raaka_aine.getText().toString()); //3.2.2019 Annetaan ruokalajille inputin mukaan nimi tietokantaan
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
        inflater.inflate(R.menu.menu_reseptilista, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_ohje){
            Toast.makeText(this, "Lisää raaka-aine", Toast.LENGTH_SHORT).show();
        }


        return super.onOptionsItemSelected(item);
    }
}



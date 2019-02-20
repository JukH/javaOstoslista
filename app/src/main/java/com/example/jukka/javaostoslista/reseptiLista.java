package com.example.jukka.javaostoslista;

import android.app.AlertDialog;
import android.app.LauncherActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.example.jukka.javaostoslista.MainActivity.getArrayVal;

public class reseptiLista extends AppCompatActivity {


    ArrayList<String> reseptit = null;
    ArrayAdapter<String> adapter = null;
    ListView lv = null;
    String key;


    FirebaseDatabase database;
    DatabaseReference myRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resepti_lista);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        reseptit = new ArrayList<>();
        Collections.addAll(reseptit); //Lisää kokonaisen setin tarvittaessa.
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, reseptit);
        lv = (ListView) findViewById(R.id.listView);
        lv.setAdapter(adapter);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("reseptit");

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                try {
                    //String value = dataSnapshot.getValue(String.class);
                    String testi = dataSnapshot.getKey().toString();
                    reseptit.add(testi); //Lisää tuplana listalle uudelleen avatteassa appia?
                    adapter.notifyDataSetChanged();
                    Collections.sort(reseptit);
                    //storeArrayVal(shoppingList, getApplicationContext()); TÄSSÄ VIKA ETTÄ LISÄSI TUPLANA KÄYNNISTETTÄESSÄ.. TUTKI LISÄÄ!
                    lv.setAdapter(adapter);
                } catch (Exception e){
                    Toast.makeText(reseptiLista.this, "Virhe", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                //18.1.2019/////////////////////////////////////////////////////////
                //String value = dataSnapshot.getValue(String.class);
                String testi = dataSnapshot.getKey().toString();
                reseptit.remove(testi);
                adapter.notifyDataSetChanged();
                Collections.sort(reseptit);
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


       /* AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Lisää raaka-aine");
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
        });*/
        reseptit = getArrayVal(getApplicationContext());
        Collections.sort(reseptit);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, reseptit);
        lv = (ListView) findViewById(R.id.listView);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View view, final int position, long id) {
                String selectedItem = ((TextView) view).getText().toString();
                if (selectedItem.trim().equals(reseptit.get(position).trim())) {
                    removeElement(selectedItem, position);
                } else {
                    Toast.makeText(getApplicationContext(),"Can not be removed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_reseptilista,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        /*if(id == R.id.lisääOstos) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Lisää raaka-aine");
            final EditText input = new EditText(this);
            builder.setView(input);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {





                    //FIREBASETESTAUS/LISÄÄMINEN
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference();
                    String key = input.getText().toString();

                    Bundle extras = getIntent().getExtras(); //3.2.2019 TUODAAN mainactivitystä ruokalajin nimi

                    String ruokaLaji = extras.getString("key");

                    myRef.child("reseptit").child(ruokaLaji).child(key).setValue(input.getText().toString());
                    //myRef.child("ostos").push().setValue(input.getText().toString()); //Määritetään tietokannan juurelle lapsi johon laitetaan dataa
                    String pushId = myRef.getKey();

                    reseptit.add(input.getText().toString());

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
        } */

        if(id == R.id.action_ohje){
            AlertDialog alertDialog = new AlertDialog.Builder(reseptiLista.this).create();
            alertDialog.setTitle("Ohje");
            alertDialog.setMessage("Klikkaamalla reseptin nimeä voit joko muokata reseptiä, poistaa sen tai lisätä reseptin raaka-aineet ostoslistalle.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }

        if(id == R.id.action_reseptiOpetus){
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Anna nimi reseptille:");
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
                    key = input.getText().toString();

                    myRef.child("reseptit").child(key).setValue(input.getText().toString()); // 2.2.2019 Menee oikeaan osoitteeseen, nyt vielä reseptit omiin lokereoihin.
                    //myRef.child("ostos").push().setValue(input.getText().toString()); //Määritetään tietokannan juurelle lapsi johon laitetaan dataa
                    String pushId = myRef.getKey();

                    Intent mene = new Intent(reseptiLista.this, reseptinNaytto.class ); //2.2.2019 avataan toinen luokka jotta saadaan listalle reseptiobjektit
                    mene.putExtra("key",key);
                    startActivity(mene);

                    //reseptit.add(input.getText().toString());

                    //String ruokaNimi = input.getText().toString(); //Luodaan muuttuja joka viedään reseptilista-activityyn 3.2.2019
                    //Intent mene = new Intent(reseptiLista.this, reseptinNaytto.class ); //2.2.2019 avataan toinen luokka jotta saadaan listalle reseptiobjektit
                    //mene.putExtra("key",ruokaNimi);
                    //startActivity(mene);





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




        return super.onOptionsItemSelected(item);
    }


    public void removeElement(final String selectedItem, final int position){ //pöljä nimi, kokeile voiko vaihtaa.. 19.2.2019
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(selectedItem);
        builder.setPositiveButton("Poista", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //19.1.2019///////////////////////////////////
                String listaTeksti =(lv.getItemAtPosition(position).toString());
                myRef.child(listaTeksti).removeValue();
                //////////////////////////////////////////////
                reseptit.remove(position);
                //18.1.2019//POISTO MYÖS FIREBASESTA/////////////////////////////
                //myRef.setValue(null); //poistaa kaiken TOIMII!!! JATKA TÄSTÄ!!!!

                /////////////////////////////////////////////////////////////////
                adapter.notifyDataSetChanged();
                Collections.sort(reseptit);
                //storeArrayVal(shoppingList, getApplicationContext());
                lv.setAdapter(adapter);




            }
        });

        builder.setNeutralButton("Lisää listalle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setNegativeButton("Muokkaa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent mene = new Intent(reseptiLista.this, reseptinNaytto.class ); //2.2.2019 avataan toinen luokka jotta saadaan listalle reseptiobjektit
                mene.putExtra("key",selectedItem);
                startActivity(mene);
            }
        });
        builder.show();
    }
}
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
            alertDialog.setMessage("Valitsemalla ruokalajin voit katsoa valmistusohjeen tai lisätä tarvitut raaka-aineet ostoslistalle. +-Symbolia painamalla voit lisätä yksittäisiä ostoksia.");
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

        if(id == R.id.action_linssiKeitto){


            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Linssikeitto");
            builder.setPositiveButton("Lisää listalle", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if(shoppingList.contains("Sipuli (4kpl)") || shoppingList.contains("Sipuli (4kpl) X" + sipMäärä)){
                        shoppingList.remove("Sipuli (4kpl)");
                        shoppingList.remove("Sipuli (4kpl) X" + sipMäärä);
                        sipMäärä++;
                        shoppingList.add("Sipuli (4kpl) X" + sipMäärä);
                    } else {
                        shoppingList.add("Sipuli (4kpl)");
                    }
                    if(shoppingList.contains("Valkosipuli (1kpl)") || shoppingList.contains("Valkosipuli (1kpl) X" + valkSipMäärä)){
                        shoppingList.remove("Valkosipuli (1kpl)");
                        shoppingList.remove("Valkosipuli (1kpl) X" + valkSipMäärä);
                        valkSipMäärä++;
                        shoppingList.add("Valkosipuli (1kpl) X" + valkSipMäärä);
                    } else {
                        shoppingList.add("Valkosipuli (1kpl)");
                    }

                    if(shoppingList.contains("Öljy (loraus)") || shoppingList.contains("Öljy (loraus) X" + öljyMäärä)){
                        shoppingList.remove("Öljy (loraus)");
                        shoppingList.remove("Öljy (loraus) X" + öljyMäärä);
                        öljyMäärä++;
                        shoppingList.add("Öljy (loraus) X" + öljyMäärä);
                    } else {
                        shoppingList.add("Öljy (loraus)");
                    }

                    if(shoppingList.contains("Punaiset linssit (1pkt)") || shoppingList.contains("Punaiset linssit (1pkt) X" + linssitMäärä)){
                        shoppingList.remove("Punaiset linssit (1pkt)");
                        shoppingList.remove("Punaiset linssit (1pkt) X" + linssitMäärä);
                        linssitMäärä++;
                        shoppingList.add("Punaiset linssit (1pkt) X" + linssitMäärä);
                    } else {
                        shoppingList.add("Punaiset linssit (1pkt)");
                    }

                    if(shoppingList.contains("Tomaattipyree (1 tuubi)") || shoppingList.contains("Tomaattipyree (1 tuubi) X" + pyreeMäärä)){
                        shoppingList.remove("Tomaattipyree (1 tuubi)");
                        shoppingList.remove("Tomaattipyree (1 tuubi) X" + pyreeMäärä);
                        pyreeMäärä++;
                        shoppingList.add("Tomaattipyree (1 tuubi) X" + pyreeMäärä);
                    } else {
                        shoppingList.add("Tomaattipyree (1 tuubi)");
                    }

                    if(shoppingList.contains("Oregano") || shoppingList.contains("Oregano X" + oreMäärä)){
                        shoppingList.remove("Oregano");
                        shoppingList.remove("Oregano X" + oreMäärä);
                        oreMäärä++;
                        shoppingList.add("Oregano X" + oreMäärä);
                    } else {
                        shoppingList.add("Oregano");
                    }

                    if(shoppingList.contains("Chilijauhe") || shoppingList.contains("Chilijauhe X" + chiliMäärä)){
                        shoppingList.remove("Chilijauhe");
                        shoppingList.remove("Chilijauhe X" + chiliMäärä);
                        chiliMäärä++;
                        shoppingList.add("Chilijauhe X" + chiliMäärä);
                    } else {
                        shoppingList.add("Chilijauhe");
                    }

                    if(shoppingList.contains("Lime (Puolikas)") || shoppingList.contains("Lime (Puolikas) X" + limeMäärä)){
                        shoppingList.remove("Lime (Puolikas)");
                        shoppingList.remove("Lime (Puolikas) X" + limeMäärä);
                        limeMäärä++;
                        shoppingList.add("Lime (Puolikas) X" + limeMäärä);
                    } else {
                        shoppingList.add("Lime (Puolikas)");
                    }

                    if(shoppingList.contains("Suola") || shoppingList.contains("Suola X" + suolaMäärä)){
                        shoppingList.remove("Suola");
                        shoppingList.remove("Suola X" + suolaMäärä);
                        suolaMäärä++;
                        shoppingList.add("Suola X" + suolaMäärä);
                    } else {
                        shoppingList.add("Suola");
                    }


                    Collections.sort(shoppingList);
                    storeArrayVal(shoppingList, getApplicationContext());
                    lv.setAdapter(adapter);
                }
            });
            builder.setNegativeButton("Valmistusohje", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle("Ohje");
                    alertDialog.setMessage("Sipuli\n" +
                            "valkosipuli\n" +
                            "Öljy\n" +
                            "Tomaattipyree\n" +
                            "Punaiset linssit (1pkt)\n" +
                            "Oregano\n" +
                            "Chilijauhe\n" +
                            "Lime (Puolikas)\n" +
                            "Suola\n" + "\n" + "Pilko ja paista sipulit sekä valkosipuli öljyssä pehmeiksi. Lisää tomaattipyree ja mausteet. Lisää vettä ja linssit. Lisää kookosmaito ja sekoita. Purista sekaan puolikkaan limetin mehu. Keitä n. 15min.");
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Sulje",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }

                            });

                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Lisää listalle",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    if(shoppingList.contains("Sipuli (4kpl)") || shoppingList.contains("Sipuli (4kpl) X" + sipMäärä)){
                                        shoppingList.remove("Sipuli (4kpl)");
                                        shoppingList.remove("Sipuli (4kpl) X" + sipMäärä);
                                        sipMäärä++;
                                        shoppingList.add("Sipuli (4kpl) X" + sipMäärä);
                                    } else {
                                        shoppingList.add("Sipuli (4kpl)");
                                    }
                                    if(shoppingList.contains("Valkosipuli (1kpl)") || shoppingList.contains("Valkosipuli (1kpl) X" + valkSipMäärä)){
                                        shoppingList.remove("Valkosipuli (1kpl)");
                                        shoppingList.remove("Valkosipuli (1kpl) X" + valkSipMäärä);
                                        valkSipMäärä++;
                                        shoppingList.add("Valkosipuli (1kpl) X" + valkSipMäärä);
                                    } else {
                                        shoppingList.add("Valkosipuli (1kpl)");
                                    }

                                    if(shoppingList.contains("Öljy (loraus)") || shoppingList.contains("Öljy (loraus) X" + öljyMäärä)){
                                        shoppingList.remove("Öljy (loraus)");
                                        shoppingList.remove("Öljy (loraus) X" + öljyMäärä);
                                        öljyMäärä++;
                                        shoppingList.add("Öljy (loraus) X" + öljyMäärä);
                                    } else {
                                        shoppingList.add("Öljy (loraus)");
                                    }

                                    if(shoppingList.contains("Punaiset linssit (1pkt)") || shoppingList.contains("Punaiset linssit (1pkt) X" + linssitMäärä)){
                                        shoppingList.remove("Punaiset linssit (1pkt)");
                                        shoppingList.remove("Punaiset linssit (1pkt) X" + linssitMäärä);
                                        linssitMäärä++;
                                        shoppingList.add("Punaiset linssit (1pkt) X" + linssitMäärä);
                                    } else {
                                        shoppingList.add("Punaiset linssit (1pkt)");
                                    }

                                    if(shoppingList.contains("Tomaattipyree (1 tuubi)") || shoppingList.contains("Tomaattipyree (1 tuubi) X" + pyreeMäärä)){
                                        shoppingList.remove("Tomaattipyree (1 tuubi)");
                                        shoppingList.remove("Tomaattipyree (1 tuubi) X" + pyreeMäärä);
                                        pyreeMäärä++;
                                        shoppingList.add("Tomaattipyree (1 tuubi) X" + pyreeMäärä);
                                    } else {
                                        shoppingList.add("Tomaattipyree (1 tuubi)");
                                    }

                                    if(shoppingList.contains("Oregano") || shoppingList.contains("Oregano X" + oreMäärä)){
                                        shoppingList.remove("Oregano");
                                        shoppingList.remove("Oregano X" + oreMäärä);
                                        oreMäärä++;
                                        shoppingList.add("Oregano X" + oreMäärä);
                                    } else {
                                        shoppingList.add("Oregano");
                                    }

                                    if(shoppingList.contains("Chilijauhe") || shoppingList.contains("Chilijauhe X" + chiliMäärä)){
                                        shoppingList.remove("Chilijauhe");
                                        shoppingList.remove("Chilijauhe X" + chiliMäärä);
                                        chiliMäärä++;
                                        shoppingList.add("Chilijauhe X" + chiliMäärä);
                                    } else {
                                        shoppingList.add("Chilijauhe");
                                    }

                                    if(shoppingList.contains("Lime (Puolikas)") || shoppingList.contains("Lime (Puolikas) X" + limeMäärä)){
                                        shoppingList.remove("Lime (Puolikas)");
                                        shoppingList.remove("Lime (Puolikas) X" + limeMäärä);
                                        limeMäärä++;
                                        shoppingList.add("Lime (Puolikas) X" + limeMäärä);
                                    } else {
                                        shoppingList.add("Lime (Puolikas)");
                                    }

                                    if(shoppingList.contains("Suola") || shoppingList.contains("Suola X" + suolaMäärä)){
                                        shoppingList.remove("Suola");
                                        shoppingList.remove("Suola X" + suolaMäärä);
                                        suolaMäärä++;
                                        shoppingList.add("Suola X" + suolaMäärä);
                                    } else {
                                        shoppingList.add("Suola");
                                    }
                                    Collections.sort(shoppingList);
                                    storeArrayVal(shoppingList, getApplicationContext());
                                    lv.setAdapter(adapter);
                                }

                            });
                    alertDialog.show();
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


                    if(shoppingList.contains("Sipuli (4kpl)") || shoppingList.contains("Sipuli (4kpl) X" + sipMäärä)){
                        shoppingList.remove("Sipuli (4kpl)");
                        shoppingList.remove("Sipuli (4kpl) X" + sipMäärä);
                        sipMäärä++;
                        shoppingList.add("Sipuli (4kpl) X" + sipMäärä);
                    } else {
                        shoppingList.add("Sipuli (4kpl)");
                    }
                    if(shoppingList.contains("Valkosipuli (1kpl)") || shoppingList.contains("Valkosipuli (1kpl) X" + valkSipMäärä)){
                        shoppingList.remove("Valkosipuli (1kpl)");
                        shoppingList.remove("Valkosipuli (1kpl) X" + valkSipMäärä);
                        valkSipMäärä++;
                        shoppingList.add("Valkosipuli (1kpl) X" + valkSipMäärä);
                    } else {
                        shoppingList.add("Valkosipuli (1kpl)");
                    }

                    if(shoppingList.contains("Öljy (loraus)") || shoppingList.contains("Öljy (loraus) X" + öljyMäärä)){
                        shoppingList.remove("Öljy (loraus)");
                        shoppingList.remove("Öljy (loraus) X" + öljyMäärä);
                        öljyMäärä++;
                        shoppingList.add("Öljy (loraus) X" + öljyMäärä);
                    } else {
                        shoppingList.add("Öljy (loraus)");
                    }

                    if(shoppingList.contains("Soijamaito") || shoppingList.contains("Soijamaito X" + soijaMäärä)){
                        shoppingList.remove("Soijamaito");
                        shoppingList.remove("Soijamaito X" + soijaMäärä);
                        soijaMäärä++;
                        shoppingList.add("Soijamaito X" + soijaMäärä);
                    } else {
                        shoppingList.add("Soijamaito");
                    }

                    if(shoppingList.contains("Tomaattimurska (2prk)") || shoppingList.contains("Tomaattimurska (2prk) X" + murskaMäärä)){
                        shoppingList.remove("Tomaattimurska (2prk)");
                        shoppingList.remove("Tomaattimurska (2prk) X" + murskaMäärä);
                        murskaMäärä++;
                        shoppingList.add("Tomaattimurska (2prk) X" + murskaMäärä);
                    } else {
                        shoppingList.add("Tomaattimurska (2prk)");
                    }

                    if(shoppingList.contains("Muskottipähkinä") || shoppingList.contains("Muskottipähkinä X" + muskottiMäärä)){
                        shoppingList.remove("Muskottipähkinä");
                        shoppingList.remove("Muskottipähkinä X" + muskottiMäärä);
                        muskottiMäärä++;
                        shoppingList.add("Muskottipähkinä X" + muskottiMäärä);
                    } else {
                        shoppingList.add("Muskottipähkinä");
                    }

                    if(shoppingList.contains("Vehnäjauho (1,5dl)") || shoppingList.contains("Vehnäjauho (1,5dl) X" + jauhoMäärä)){
                        shoppingList.remove("Vehnäjauho (1,5dl)");
                        shoppingList.remove("Vehnäjauho (1,5dl) X" + jauhoMäärä);
                        jauhoMäärä++;
                        shoppingList.add("Vehnäjauho (1,5dl) X" + jauhoMäärä);
                    } else {
                        shoppingList.add("Vehnäjauho (1,5dl)");
                    }

                    if(shoppingList.contains("Pippuri") || shoppingList.contains("Pippuri X" + pippuriMäärä)){
                        shoppingList.remove("Pippuri");
                        shoppingList.remove("Pippuri X" + pippuriMäärä);
                        pippuriMäärä++;
                        shoppingList.add("Pippuri X" + pippuriMäärä);
                    } else {
                        shoppingList.add("Pippuri");
                    }

                    if(shoppingList.contains("Suola") || shoppingList.contains("Suola X" + suolaMäärä)){
                        shoppingList.remove("Suola");
                        shoppingList.remove("Suola X" + suolaMäärä);
                        suolaMäärä++;
                        shoppingList.add("Suola X" + suolaMäärä);
                    } else {
                        shoppingList.add("Suola");
                    }

                    if(shoppingList.contains("Lasagnelevyt") || shoppingList.contains("Lasagnelevyt X" + levyMäärä)){
                        shoppingList.remove("Lasagnelevyt");
                        shoppingList.remove("Lasagnelevyt X" + levyMäärä);
                        levyMäärä++;
                        shoppingList.add("Lasagnelevyt X" + levyMäärä);
                    } else {
                        shoppingList.add("Lasagnelevyt");
                    }

                    if(shoppingList.contains("Pakastevihannekset") || shoppingList.contains("Pakastevihannekset X" + paViMäärä)){
                        shoppingList.remove("Pakastevihannekset");
                        shoppingList.remove("Pakastevihannekset X" + paViMäärä);
                        paViMäärä++;
                        shoppingList.add("Pakastevihannekset X" + paViMäärä);
                    } else {
                        shoppingList.add("Pakastevihannekset");
                    }

                    if(shoppingList.contains("Juusto (500g)") || shoppingList.contains("Juusto (500g) X" + juustoMäärä)){
                        shoppingList.remove("Juusto (500g)");
                        shoppingList.remove("Juusto (500g) X" + juustoMäärä);
                        juustoMäärä++;
                        shoppingList.add("Juusto (500g) X" + juustoMäärä);
                    } else {
                        shoppingList.add("Juusto (500g)");
                    }

                    Collections.sort(shoppingList);
                    storeArrayVal(shoppingList, getApplicationContext());
                    lv.setAdapter(adapter);
                }
            });
            builder.setNegativeButton("Valmistusohje", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle("Ohje");
                    alertDialog.setMessage("Soijamaito x 1\n" +
                            "Öljy (paistamiseen)\n" +
                            "Vehnäjauho (1,5dl)\n" +
                            "Muskottipähkinä\n" +
                            "Suola\n"+
                            "Pakastevihannekset (1kg)\n" +
                            "Lasagnelevyt (1pkt)\n" +
                            "Tomaattimurska (2prk)\n" +
                            "Sipuli (4kpl)\n" +
                            "valkosipuli (1kpl)\n" +
                            "Oregano\n" +
                            "pippuri\n"+"Juusto (500g)\n" + "\n" +"Paista sipulit ja valkosipuli öljyssä. Lisää tomaattimurska, pakastevihannekset ja mausteet. Valkokastike: Kaada soijamaito kattilaan, lisää jauhot, muskottipähkinä, öljy ja suola. Pinoa lasagne järjestyksessä: Tomaattikastike, valkokastike, levyt. Päälle juustoraastetta.");
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Sulje",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Lisää listalle",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    if(shoppingList.contains("Sipuli (4kpl)") || shoppingList.contains("Sipuli (4kpl) X" + sipMäärä)){
                                        shoppingList.remove("Sipuli (4kpl)");
                                        shoppingList.remove("Sipuli (4kpl) X" + sipMäärä);
                                        sipMäärä++;
                                        shoppingList.add("Sipuli (4kpl) X" + sipMäärä);
                                    } else {
                                        shoppingList.add("Sipuli (4kpl)");
                                    }
                                    if(shoppingList.contains("Valkosipuli (1kpl)") || shoppingList.contains("Valkosipuli (1kpl) X" + valkSipMäärä)){
                                        shoppingList.remove("Valkosipuli (1kpl)");
                                        shoppingList.remove("Valkosipuli (1kpl) X" + valkSipMäärä);
                                        valkSipMäärä++;
                                        shoppingList.add("Valkosipuli (1kpl) X" + valkSipMäärä);
                                    } else {
                                        shoppingList.add("Valkosipuli (1kpl)");
                                    }

                                    if(shoppingList.contains("Öljy (loraus)") || shoppingList.contains("Öljy (loraus) X" + öljyMäärä)){
                                        shoppingList.remove("Öljy (loraus)");
                                        shoppingList.remove("Öljy (loraus) X" + öljyMäärä);
                                        öljyMäärä++;
                                        shoppingList.add("Öljy (loraus) X" + öljyMäärä);
                                    } else {
                                        shoppingList.add("Öljy (loraus)");
                                    }

                                    if(shoppingList.contains("Soijamaito") || shoppingList.contains("Soijamaito X" + soijaMäärä)){
                                        shoppingList.remove("Soijamaito");
                                        shoppingList.remove("Soijamaito X" + soijaMäärä);
                                        soijaMäärä++;
                                        shoppingList.add("Soijamaito X" + soijaMäärä);
                                    } else {
                                        shoppingList.add("Soijamaito");
                                    }

                                    if(shoppingList.contains("Tomaattimurska (2prk)") || shoppingList.contains("Tomaattimurska (2prk) X" + murskaMäärä)){
                                        shoppingList.remove("Tomaattimurska (2prk)");
                                        shoppingList.remove("Tomaattimurska (2prk) X" + murskaMäärä);
                                        murskaMäärä++;
                                        shoppingList.add("Tomaattimurska (2prk) X" + murskaMäärä);
                                    } else {
                                        shoppingList.add("Tomaattimurska (2prk)");
                                    }

                                    if(shoppingList.contains("Muskottipähkinä") || shoppingList.contains("Muskottipähkinä X" + muskottiMäärä)){
                                        shoppingList.remove("Muskottipähkinä");
                                        shoppingList.remove("Muskottipähkinä X" + muskottiMäärä);
                                        muskottiMäärä++;
                                        shoppingList.add("Muskottipähkinä X" + muskottiMäärä);
                                    } else {
                                        shoppingList.add("Muskottipähkinä");
                                    }

                                    if(shoppingList.contains("Vehnäjauho (1,5dl)") || shoppingList.contains("Vehnäjauho (1,5dl) X" + jauhoMäärä)){
                                        shoppingList.remove("Vehnäjauho (1,5dl)");
                                        shoppingList.remove("Vehnäjauho (1,5dl) X" + jauhoMäärä);
                                        jauhoMäärä++;
                                        shoppingList.add("Vehnäjauho (1,5dl) X" + jauhoMäärä);
                                    } else {
                                        shoppingList.add("Vehnäjauho (1,5dl)");
                                    }

                                    if(shoppingList.contains("Pippuri") || shoppingList.contains("Pippuri X" + pippuriMäärä)){
                                        shoppingList.remove("Pippuri");
                                        shoppingList.remove("Pippuri X" + pippuriMäärä);
                                        pippuriMäärä++;
                                        shoppingList.add("Pippuri X" + pippuriMäärä);
                                    } else {
                                        shoppingList.add("Pippuri");
                                    }

                                    if(shoppingList.contains("Suola") || shoppingList.contains("Suola X" + suolaMäärä)){
                                        shoppingList.remove("Suola");
                                        shoppingList.remove("Suola X" + suolaMäärä);
                                        suolaMäärä++;
                                        shoppingList.add("Suola X" + suolaMäärä);
                                    } else {
                                        shoppingList.add("Suola");
                                    }

                                    if(shoppingList.contains("Lasagnelevyt") || shoppingList.contains("Lasagnelevyt X" + levyMäärä)){
                                        shoppingList.remove("Lasagnelevyt");
                                        shoppingList.remove("Lasagnelevyt X" + levyMäärä);
                                        levyMäärä++;
                                        shoppingList.add("Lasagnelevyt X" + levyMäärä);
                                    } else {
                                        shoppingList.add("Lasagnelevyt");
                                    }

                                    if(shoppingList.contains("Pakastevihannekset") || shoppingList.contains("Pakastevihannekset X" + paViMäärä)){
                                        shoppingList.remove("Pakastevihannekset");
                                        shoppingList.remove("Pakastevihannekset X" + paViMäärä);
                                        paViMäärä++;
                                        shoppingList.add("Pakastevihannekset X" + paViMäärä);
                                    } else {
                                        shoppingList.add("Pakastevihannekset");
                                    }

                                    if(shoppingList.contains("Juusto (500g)") || shoppingList.contains("Juusto (500g) X" + juustoMäärä)){
                                        shoppingList.remove("Juusto (500g)");
                                        shoppingList.remove("Juusto (500g) X" + juustoMäärä);
                                        juustoMäärä++;
                                        shoppingList.add("Juusto (500g) X" + juustoMäärä);
                                    } else {
                                        shoppingList.add("Juusto (500g)");
                                    }
                                    Collections.sort(shoppingList);
                                    storeArrayVal(shoppingList, getApplicationContext());
                                    lv.setAdapter(adapter);
                                }

                            });

                    alertDialog.show();
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


                    if(shoppingList.contains("Kookosmaito") || shoppingList.contains("Kookosmaito X" + kookosMaitoMäärä)){
                        shoppingList.remove("Kookosmaito");
                        shoppingList.remove("Kookosmaito X" + kookosMaitoMäärä);
                        kookosMaitoMäärä++;
                        shoppingList.add("Kookosmaito X" + kookosMaitoMäärä);
                    } else {
                        shoppingList.add("Kookosmaito");
                    }

                    if(shoppingList.contains("Riisi") || shoppingList.contains("Riisi X" + riisiMäärä)){
                        shoppingList.remove("Riisi");
                        shoppingList.remove("Riisi X" + riisiMäärä);
                        riisiMäärä++;
                        shoppingList.add("Riisi X" + riisiMäärä);
                    } else {
                        shoppingList.add("Riisi");
                    }

                    if(shoppingList.contains("Sipuli (4kpl)") || shoppingList.contains("Sipuli (4kpl) X" + sipMäärä)){
                        shoppingList.remove("Sipuli (4kpl)");
                        shoppingList.remove("Sipuli (4kpl) X" + sipMäärä);
                        sipMäärä++;
                        shoppingList.add("Sipuli (4kpl) X" + sipMäärä);
                    } else {
                        shoppingList.add("Sipuli (4kpl)");
                    }

                    if(shoppingList.contains("Valkosipuli (1kpl)") || shoppingList.contains("Valkosipuli (1kpl) X" + valkSipMäärä)){
                        shoppingList.remove("Valkosipuli (1kpl)");
                        shoppingList.remove("Valkosipuli (1kpl) X" + valkSipMäärä);
                        valkSipMäärä++;
                        shoppingList.add("Valkosipuli (1kpl) X" + valkSipMäärä);
                    } else {
                        shoppingList.add("Valkosipuli (1kpl)");
                    }

                    if(shoppingList.contains("Tomaattimurska (2prk)") || shoppingList.contains("Tomaattimurska (2prk) X" + murskaMäärä)){
                        shoppingList.remove("Tomaattimurska (2prk)");
                        shoppingList.remove("Tomaattimurska (2prk) X" + murskaMäärä);
                        murskaMäärä++;
                        shoppingList.add("Tomaattimurska (2prk) X" + murskaMäärä);
                    } else {
                        shoppingList.add("Tomaattimurska (2prk)");
                    }

                    if(shoppingList.contains("Pakastevihannekset") || shoppingList.contains("Pakastevihannekset X" + paViMäärä)){
                        shoppingList.remove("Pakastevihannekset");
                        shoppingList.remove("Pakastevihannekset X" + paViMäärä);
                        paViMäärä++;
                        shoppingList.add("Pakastevihannekset X" + paViMäärä);
                    } else {
                        shoppingList.add("Pakastevihannekset");
                    }

                    Collections.sort(shoppingList);
                    storeArrayVal(shoppingList, getApplicationContext());
                    lv.setAdapter(adapter);
                }
            });
            builder.setNegativeButton("Valmistusohje", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle("Ohje");
                    alertDialog.setMessage("Riisi (500g)\n" +
                            "Tomaattimurska (2prk)\n" +
                            "Sipuli (4kpl)\n" +
                            "valkosipuli (1kpl)\n" +
                            "kookosmaito (1prk)\n" +
                            "Pakastevihannekset\n" + "\n" + "Paista sipuli ja valkosipuli, lisää tomaattimurska, pakastevihannekset ja kookosmaito. Tarjoile riisin kera.");
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Sulje",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Lisää listalle",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    if(shoppingList.contains("Kookosmaito") || shoppingList.contains("Kookosmaito X" + kookosMaitoMäärä)){
                                        shoppingList.remove("Kookosmaito");
                                        shoppingList.remove("Kookosmaito X" + kookosMaitoMäärä);
                                        kookosMaitoMäärä++;
                                        shoppingList.add("Kookosmaito X" + kookosMaitoMäärä);
                                    } else {
                                        shoppingList.add("Kookosmaito");
                                    }

                                    if(shoppingList.contains("Riisi") || shoppingList.contains("Riisi X" + riisiMäärä)){
                                        shoppingList.remove("Riisi");
                                        shoppingList.remove("Riisi X" + riisiMäärä);
                                        riisiMäärä++;
                                        shoppingList.add("Riisi X" + riisiMäärä);
                                    } else {
                                        shoppingList.add("Riisi");
                                    }

                                    if(shoppingList.contains("Sipuli (4kpl)") || shoppingList.contains("Sipuli (4kpl) X" + sipMäärä)){
                                        shoppingList.remove("Sipuli (4kpl)");
                                        shoppingList.remove("Sipuli (4kpl) X" + sipMäärä);
                                        sipMäärä++;
                                        shoppingList.add("Sipuli (4kpl) X" + sipMäärä);
                                    } else {
                                        shoppingList.add("Sipuli (4kpl)");
                                    }

                                    if(shoppingList.contains("Valkosipuli (1kpl)") || shoppingList.contains("Valkosipuli (1kpl) X" + valkSipMäärä)){
                                        shoppingList.remove("Valkosipuli (1kpl)");
                                        shoppingList.remove("Valkosipuli (1kpl) X" + valkSipMäärä);
                                        valkSipMäärä++;
                                        shoppingList.add("Valkosipuli (1kpl) X" + valkSipMäärä);
                                    } else {
                                        shoppingList.add("Valkosipuli (1kpl)");
                                    }

                                    if(shoppingList.contains("Tomaattimurska (2prk)") || shoppingList.contains("Tomaattimurska (2prk) X" + murskaMäärä)){
                                        shoppingList.remove("Tomaattimurska (2prk)");
                                        shoppingList.remove("Tomaattimurska (2prk) X" + murskaMäärä);
                                        murskaMäärä++;
                                        shoppingList.add("Tomaattimurska (2prk) X" + murskaMäärä);
                                    } else {
                                        shoppingList.add("Tomaattimurska (2prk)");
                                    }

                                    if(shoppingList.contains("Pakastevihannekset") || shoppingList.contains("Pakastevihannekset X" + paViMäärä)){
                                        shoppingList.remove("Pakastevihannekset");
                                        shoppingList.remove("Pakastevihannekset X" + paViMäärä);
                                        paViMäärä++;
                                        shoppingList.add("Pakastevihannekset X" + paViMäärä);
                                    } else {
                                        shoppingList.add("Pakastevihannekset");
                                    }
                                    Collections.sort(shoppingList);
                                    storeArrayVal(shoppingList, getApplicationContext());
                                    lv.setAdapter(adapter);
                                }

                            });

                    alertDialog.show();
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


                    if(shoppingList.contains("Tortillapohjat") || shoppingList.contains("Tortillapohjat X" + tortillaMäärä)){
                        shoppingList.remove("Tortillapohjat");
                        shoppingList.remove("Tortillapohjat X" + tortillaMäärä);
                        tortillaMäärä++;
                        shoppingList.add("Tortillapohjat X" + tortillaMäärä);
                    } else {
                        shoppingList.add("Tortillapohjat");
                    }

                    if(shoppingList.contains("Pastakastikepurkki") || shoppingList.contains("Pastakastikepurkki X" + pastaKastikeMäärä)){
                        shoppingList.remove("Pastakastikepurkki");
                        shoppingList.remove("Pastakastikepurkki X" + pastaKastikeMäärä);
                        pastaKastikeMäärä++;
                        shoppingList.add("Pastakastikepurkki X" + pastaKastikeMäärä);
                    } else {
                        shoppingList.add("Pastakastikepurkki");
                    }

                    if(shoppingList.contains("Juusto (500g)") || shoppingList.contains("Juusto (500g) X" + juustoMäärä)){
                        shoppingList.remove("Juusto (500g)");
                        shoppingList.remove("Juusto (500g) X" + juustoMäärä);
                        juustoMäärä++;
                        shoppingList.add("Juusto (500g) X" + juustoMäärä);
                    } else {
                        shoppingList.add("Juusto (500g)");
                    }

                    if(shoppingList.contains("Homejuusto (1pkt)") || shoppingList.contains("Homejuusto (1pkt) X" + homeMäärä)){
                        shoppingList.remove("Homejuusto (1pkt)");
                        shoppingList.remove("Homejuusto (1pkt) X" + homeMäärä);
                        homeMäärä++;
                        shoppingList.add("Homejuusto (1pkt) X" + homeMäärä);
                    } else {
                        shoppingList.add("Homejuusto (1pkt)");
                    }

                    if(shoppingList.contains("Mozzarella") || shoppingList.contains("Mozzarella X" + mozzarellaMäärä)){
                        shoppingList.remove("Mozzarella");
                        shoppingList.remove("Mozzarella X" + mozzarellaMäärä);
                        mozzarellaMäärä++;
                        shoppingList.add("Mozzarella X" + mozzarellaMäärä);
                    } else {
                        shoppingList.add("Mozzarella");
                    }

                    Collections.sort(shoppingList);
                    storeArrayVal(shoppingList, getApplicationContext());
                    lv.setAdapter(adapter);
                    //shoppingList.clear();
                    //lv.setAdapter(adapter);
                }
            });
            builder.setNegativeButton("Valmistusohje", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle("Ohje");
                    alertDialog.setMessage("Tortillapohjat (2pkt)\n" +
                            "pastakastikepurkki (2prk)\n" +
                            "Juusto (500g)\n" +
                            "Homejuusto (1pkt)\n" +
                            "Mozzarella (3pkt)\n" + "\n" + "Sivele pastakastike tortillapohjille. Täytä mieleisillä täytteillä ja paista 200-asteisessa uunissa 10 min.");
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Sulje",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Lisää listalle",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    if(shoppingList.contains("Tortillapohjat") || shoppingList.contains("Tortillapohjat X" + tortillaMäärä)){
                                        shoppingList.remove("Tortillapohjat");
                                        shoppingList.remove("Tortillapohjat X" + tortillaMäärä);
                                        tortillaMäärä++;
                                        shoppingList.add("Tortillapohjat X" + tortillaMäärä);
                                    } else {
                                        shoppingList.add("Tortillapohjat");
                                    }

                                    if(shoppingList.contains("Pastakastikepurkki") || shoppingList.contains("Pastakastikepurkki X" + pastaKastikeMäärä)){
                                        shoppingList.remove("Pastakastikepurkki");
                                        shoppingList.remove("Pastakastikepurkki X" + pastaKastikeMäärä);
                                        pastaKastikeMäärä++;
                                        shoppingList.add("Pastakastikepurkki X" + pastaKastikeMäärä);
                                    } else {
                                        shoppingList.add("Pastakastikepurkki");
                                    }

                                    if(shoppingList.contains("Juusto (500g)") || shoppingList.contains("Juusto (500g) X" + juustoMäärä)){
                                        shoppingList.remove("Juusto (500g)");
                                        shoppingList.remove("Juusto (500g) X" + juustoMäärä);
                                        juustoMäärä++;
                                        shoppingList.add("Juusto (500g) X" + juustoMäärä);
                                    } else {
                                        shoppingList.add("Juusto (500g)");
                                    }

                                    if(shoppingList.contains("Homejuusto (1pkt)") || shoppingList.contains("Homejuusto (1pkt) X" + homeMäärä)){
                                        shoppingList.remove("Homejuusto (1pkt)");
                                        shoppingList.remove("Homejuusto (1pkt) X" + homeMäärä);
                                        homeMäärä++;
                                        shoppingList.add("Homejuusto (1pkt) X" + homeMäärä);
                                    } else {
                                        shoppingList.add("Homejuusto (1pkt)");
                                    }

                                    if(shoppingList.contains("Mozzarella") || shoppingList.contains("Mozzarella X" + mozzarellaMäärä)){
                                        shoppingList.remove("Mozzarella");
                                        shoppingList.remove("Mozzarella X" + mozzarellaMäärä);
                                        mozzarellaMäärä++;
                                        shoppingList.add("Mozzarella X" + mozzarellaMäärä);
                                    } else {
                                        shoppingList.add("Mozzarella");
                                    }
                                    Collections.sort(shoppingList);
                                    storeArrayVal(shoppingList, getApplicationContext());
                                    lv.setAdapter(adapter);
                                }

                            });

                    alertDialog.show();
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


                    if(shoppingList.contains("Babywipes (x4)") || shoppingList.contains("Babywipes (x4) X" + wipesMäärä)){
                        shoppingList.remove("Babywipes (x4)");
                        shoppingList.remove("Babywipes (x4) X" + wipesMäärä);
                        wipesMäärä++;
                        shoppingList.add("Babywipes (x4) X" + wipesMäärä);
                    } else {
                        shoppingList.add("Babywipes (x4)");
                    }

                    if(shoppingList.contains("Riisikakut (x5)") || shoppingList.contains("Riisikakut (x5) X" + riisiKakkuMäärä)){
                        shoppingList.remove("Riisikakut (x5)");
                        shoppingList.remove("Riisikakut (x5) X" + riisiKakkuMäärä);
                        riisiKakkuMäärä++;
                        shoppingList.add("Riisikakut (x5) X" + riisiKakkuMäärä);
                    } else {
                        shoppingList.add("Riisikakut (x5)");
                    }

                    if(shoppingList.contains("Pesto (x5)") || shoppingList.contains("Pesto (x5) X" + pestoMäärä)){
                        shoppingList.remove("Pesto (x5)");
                        shoppingList.remove("Pesto (x5) X" + pestoMäärä);
                        pestoMäärä++;
                        shoppingList.add("Pesto (x5) X" + pestoMäärä);
                    } else {
                        shoppingList.add("Pesto (x5)");
                    }

                    if(shoppingList.contains("Pepsi max") || shoppingList.contains("Pepsi max X" + pepsiMäärä)){
                        shoppingList.remove("Pepsi max");
                        shoppingList.remove("Pepsi max X" + pepsiMäärä);
                        pepsiMäärä++;
                        shoppingList.add("Pepsi max X" + pepsiMäärä);
                    } else {
                        shoppingList.add("Pepsi max");
                    }

                    if(shoppingList.contains("Jogurtti (x3)") || shoppingList.contains("Jogurtti (x3) X" + jogurttiMäärä)){
                        shoppingList.remove("Jogurtti (x3)");
                        shoppingList.remove("Jogurtti (x3) X" + jogurttiMäärä);
                        jogurttiMäärä++;
                        shoppingList.add("Jogurtti (x3) X" + jogurttiMäärä);
                    } else {
                        shoppingList.add("Jogurtti (x3)");
                    }

                    Collections.sort(shoppingList);
                    storeArrayVal(shoppingList, getApplicationContext());
                    lv.setAdapter(adapter);
                }
            });
            builder.setNegativeButton("Sisältö", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle("Perustarpeet");
                    alertDialog.setMessage("Babywipes (x4)\n" +
                            "Riisikakut (x3)\n" +
                            "Pesto (x3)\n" +
                            "Pepsi max \n" +
                            "Jogurtti (x2)\n" +
                            "Wc-paperi\n" +
                            "Kahvi\n");
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Sulje",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Lisää listalle",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    if(shoppingList.contains("Babywipes (x4)") || shoppingList.contains("Babywipes (x4) X" + wipesMäärä)){
                                        shoppingList.remove("Babywipes (x4)");
                                        shoppingList.remove("Babywipes (x4) X" + wipesMäärä);
                                        wipesMäärä++;
                                        shoppingList.add("Babywipes (x4) X" + wipesMäärä);
                                    } else {
                                        shoppingList.add("Babywipes (x4)");
                                    }

                                    if(shoppingList.contains("Riisikakut (x5)") || shoppingList.contains("Riisikakut (x5) X" + riisiKakkuMäärä)){
                                        shoppingList.remove("Riisikakut (x5)");
                                        shoppingList.remove("Riisikakut (x5) X" + riisiKakkuMäärä);
                                        riisiKakkuMäärä++;
                                        shoppingList.add("Riisikakut (x5) X" + riisiKakkuMäärä);
                                    } else {
                                        shoppingList.add("Riisikakut (x5)");
                                    }

                                    if(shoppingList.contains("Pesto (x5)") || shoppingList.contains("Pesto (x5) X" + pestoMäärä)){
                                        shoppingList.remove("Pesto (x5)");
                                        shoppingList.remove("Pesto (x5) X" + pestoMäärä);
                                        pestoMäärä++;
                                        shoppingList.add("Pesto (x5) X" + pestoMäärä);
                                    } else {
                                        shoppingList.add("Pesto (x5)");
                                    }

                                    if(shoppingList.contains("Pepsi max") || shoppingList.contains("Pepsi max X" + pepsiMäärä)){
                                        shoppingList.remove("Pepsi max");
                                        shoppingList.remove("Pepsi max X" + pepsiMäärä);
                                        pepsiMäärä++;
                                        shoppingList.add("Pepsi max X" + pepsiMäärä);
                                    } else {
                                        shoppingList.add("Pepsi max");
                                    }

                                    if(shoppingList.contains("Jogurtti (x3)") || shoppingList.contains("Jogurtti (x3) X" + jogurttiMäärä)){
                                        shoppingList.remove("Jogurtti (x3)");
                                        shoppingList.remove("Jogurtti (x3) X" + jogurttiMäärä);
                                        jogurttiMäärä++;
                                        shoppingList.add("Jogurtti (x3) X" + jogurttiMäärä);
                                    } else {
                                        shoppingList.add("Jogurtti (x3)");
                                    }
                                    Collections.sort(shoppingList);
                                    storeArrayVal(shoppingList, getApplicationContext());
                                    lv.setAdapter(adapter);
                                }

                            });
                    alertDialog.show();
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

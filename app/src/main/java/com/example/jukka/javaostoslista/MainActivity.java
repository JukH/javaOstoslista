package com.example.jukka.javaostoslista;

import android.content.Intent;
import android.graphics.Color;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {


    ArrayList<String> shoppingList = null;
    ArrayAdapter<String> adapter = null;
    ListView lvMain = null;
    private static MainActivity instance;

    String kayttaja_id = FirebaseAuth.getInstance().getCurrentUser().getUid(); //Otetaan user-id talteen

    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Luodaan viittaus tietokantaan (polkuun /ostos) johon voidaan tämän luokan kautta lisätä tuotteita (Näkyy oikeassa listanäkymässä, eli varsinaisella ostoslistalla, ei mene esim. reseptilistaan)

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users/" + kayttaja_id + "/ostos"); // TESTATAAN JOSKO MENISI JOKAISEN KÄYTTÄJÄN OMAAN POLKUUN

        //Luodaan kuuntelija tietokantaan, jotta ohjelma osaa päivittää itsensä muutoksien tapahtuessa
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                String value = dataSnapshot.getValue(String.class); //Poimitaan valittu tuote

                shoppingList.add(value); //Lisätään uusi tuote ostoslistaan
                adapter.notifyDataSetChanged(); //Ilmoitetaan adapterille että on tapahtunut muutos listassa
                Collections.sort(shoppingList); //Järjestetään lista uusiksi päivitettynä
                lvMain.setAdapter(adapter); //Näytetään päivitetty lista

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                //Kun tuote poistetaan, tämä metodi poistaa tuotteen listanäkymästä
                String value = dataSnapshot.getValue(String.class);
                shoppingList.remove(value); //Poistetaan näkyvältä ostoslistalta valittu tuote
                adapter.notifyDataSetChanged(); //Ilmoitetaan lista-adapterille että on tapahtunut muutos
                Collections.sort(shoppingList); //Järjestetään lista uusiksi päivitettynä
                lvMain.setAdapter(adapter); // Näytetään päivitetty lista

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Koska ollaan onCreate-metodissa:
        shoppingList = getArrayVal(getApplicationContext()); //Annetaan listalle sisältö
        Collections.sort(shoppingList); //Järjestetään listan sisältö (aakkosjärjestys)
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, shoppingList); //Alustetaan lista-adapteri
        lvMain = (ListView)findViewById(R.id.listView); //Annetaan listanäkymälle ulkonäkö XML-tiedostosta (id = listView)
        lvMain.setAdapter(adapter); //Asetetaan adapteri (Näytetään sisältö)

        //Luodaan kuuntelija, jonka avulla voidaan poistaa valittu tuote listalta
        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View view, final int position, long id) {
                String selectedItem = ((TextView) view).getText().toString();
                if (selectedItem.trim().equals(shoppingList.get(position).trim())) {
                    removeElement(selectedItem, position);
                } else {
                    Toast.makeText(getApplicationContext(),"Ei voida poistaa", Toast.LENGTH_LONG).show();
                }
            }
        });

           // lvMain.setBackgroundColor(Color.LTGRAY); Saa vaihdettua listan taustavärin



    }
    //Asetetaan menu näkyväksi
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    //Asetetaan kuuntelijat valikko-itemeiden käyttöä varten
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        int id = item.getItemId();
        //Jos valitaan "ohje", suoritetaan seuraavat toiminnot:
        if (id == R.id.action_ohje) {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create(); //Avataan pikku-ikkuna
            alertDialog.setTitle(getString(R.string.ohje)); //Titteli ikkunalle
            alertDialog.setMessage(getString(R.string.ohjeet_main)); //Asetetaan viesti
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", //Luodaan "kuittaus"-nappi...
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss(); //...jota painaessa suljetaan ohje.
                        }
                    });
            alertDialog.show(); //Asetetaan viesti-ikkuna näkyväksi
        }

        if(id == R.id.lisääOstos) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.lisaa_ostos));
            final EditText input = new EditText(this);
            builder.setView(input);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    //Otetaan yhteys tietokantaan
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference();


                        String key = input.getText().toString(); //Poimitaan text-inputista String
                    if(key.equals(null) || key.equals("")) { //Tsekataan ettei ole tyhjä input (kaatuu muuten)
                        Toast.makeText(getApplicationContext(),getString(R.string.pakkolisata), Toast.LENGTH_LONG).show();
                    } else {
                        key = key.substring(0, 1).toUpperCase() + key.substring(1).toLowerCase(); //Asetetaan annettu String alkavaksi isolla alkukirjaimella (Listan siisteys)

                        myRef.child("Users/" + kayttaja_id + "/ostos").child(key).setValue(key); //Sijoitetaan annettu input tietokantaan polkuun: /ostos/input
                    }

                }
            });
            builder.setNegativeButton(getString(R.string.peruuta), new DialogInterface.OnClickListener() { //Voidaan peruuttaa input
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.cancel(); //Suljetaan input-ikkuna jos klikataan "Peruuta"
                }
            });
            builder.show();
            return true;
        }
        //Koko listan tyhjentäminen kerralla
        if(id == R.id.action_clear) { //Jos klikataan valikossa "tyhennä koko lista"..
            AlertDialog.Builder builder = new AlertDialog.Builder(this); //..avataan dialogi-ikkuna
            builder.setTitle(getString(R.string.tyhjenna_lista)); //Asetetaan viesti ikkunaan
            builder.setPositiveButton(getString(R.string.tyhjenna), new DialogInterface.OnClickListener() { //Asetetaan tyhjennynappi
                @Override
                public void onClick(DialogInterface dialog, int which) { //Jos klikataan "Tyhjennä"
                    myRef.setValue(null); //Tyhjennetään annettu tietokantapolku (myref = /ostos), eli kaikki ostokset poistuvat listalta
                }
            });
            builder.setNegativeButton(getString(R.string.peruuta), new DialogInterface.OnClickListener() { //Peruutus-nappi
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel(); //Klikatessa "Peruuta"-nappia, suljetaan ikkuna
                }
            });
            builder.show();
            return true;
        }

        if(id == R.id.action_reseptit){ //Jos klikataan valikossa "reseptit"..

            Intent mene = new Intent(MainActivity.this, reseptiLista.class ); //...luodaan intent jolla voidaan avata toinen luokka (reseptilista) jotta saadaan listalle reseptiobjektit
            startActivity(mene); //Suoritetaan intent -> avataan reseptiLista-luokka

        }
        return super.onOptionsItemSelected(item);
    }

    public static ArrayList getArrayVal( Context dan) //Listan sisällön luonti, tämä metodi hieman hämärän peitossa itsellä
    {
        SharedPreferences WordSearchGetPrefs = dan.getSharedPreferences("dbArrayValues",Activity.MODE_PRIVATE);
        Set<String> tempSet = new HashSet<String>();
        tempSet = WordSearchGetPrefs.getStringSet("myArray", tempSet);
        return new ArrayList<String>(tempSet);
    }
    //Tuotteen poisto-metodi
    public void removeElement(final String selectedItem, final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.poista) + selectedItem + "?");
        builder.setPositiveButton(getString(R.string.poista_), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String listaTeksti =(lvMain.getItemAtPosition(position).toString()); //Poimitaan klikatusta lista-itemistä String
                myRef.child(listaTeksti).removeValue(); //Poistetaan saadun Stringin avulla tietokannasta valittu item, tietokantakuuntelijan kautta päivittyy myös itse listanäkymä
            }
        });
        builder.setNegativeButton(getString(R.string.peruuta), new DialogInterface.OnClickListener() { //Peruutusnappi
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

}

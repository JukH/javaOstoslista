package com.example.jukka.javaostoslista;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ListaListaActivity extends AppCompatActivity {


    ArrayList<String> Listat = null;
    ArrayAdapter<String> adapter = null;
    ListView lvMain = null;
    private static MainActivity instance;
    String uusiLista;
    //boolean onkoJaettu; //Koitetaan vaihtaa listanäkymä tätä manipuloimalla jotta jaettu lista näkyy kaikilla jakajilla, eikä vain perustajalla

    String kayttaja_id = FirebaseAuth.getInstance().getCurrentUser().getUid(); //Otetaan user-id talteen
    String kayttaja_email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    String jakajan_email;

    FirebaseDatabase database;
    DatabaseReference ListatRef;
    DatabaseReference nimiRef, kaveriRef, haeAdmininEmailRef, kayttajanIdHakuRef, onkoJaettuRef, onkoJaettuRef2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ListaListaActivity.this.setTitle("Listat");


        //Luodaan viittaus tietokantaan (polkuun /ostos) johon voidaan tämän luokan kautta lisätä tuotteita (Näkyy oikeassa listanäkymässä, eli varsinaisella ostoslistalla, ei mene esim. reseptilistaan)
        final String kayttaja_email = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", ","); //Otetaan nykyisen käyttäjän s.posti talteen ja kuunnellaan jos joku lisää sen listalleen (haluaa jakaa hänen kanssaan)
        database = FirebaseDatabase.getInstance();
        ListatRef = database.getReference("Users/" + kayttaja_email + "/listat"); // TESTATAAN JOSKO MENISI JOKAISEN KÄYTTÄJÄN OMAAN POLKUUN
        nimiRef = database.getReference("Users/" +  kayttaja_id);
        //kaveriRef = database.getReference("Users/" + kayttaja_id + "/lista" + "/kaveri");







       /* kaveriRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long value = dataSnapshot.getChildrenCount();
                if(value > 0){
                    Intent mene = new Intent(MainActivity.this, JaettuActivity.class);
                    startActivity(mene);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        }); */





        //Luodaan kuuntelija tietokantaan, jotta ohjelma osaa päivittää itsensä muutoksien tapahtuessa
        ListatRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                String value = dataSnapshot.getKey(); //Poimitaan valittu tuote

                Listat.add(value); //Lisätään uusi tuote ostoslistaan
                adapter.notifyDataSetChanged(); //Ilmoitetaan adapterille että on tapahtunut muutos listassa
                Collections.sort(Listat); //Järjestetään lista uusiksi päivitettynä
                lvMain.setAdapter(adapter); //Näytetään päivitetty lista



            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                //Kun tuote poistetaan, tämä metodi poistaa tuotteen listanäkymästä
                String value = dataSnapshot.getKey();
                Listat.remove(value); //Poistetaan näkyvältä ostoslistalta valittu tuote
                adapter.notifyDataSetChanged(); //Ilmoitetaan lista-adapterille että on tapahtunut muutos
                Collections.sort(Listat); //Järjestetään lista uusiksi päivitettynä
                lvMain.setAdapter(adapter); // Näytetään päivitetty lista

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////





        //Koska ollaan onCreate-metodissa:
        Listat = getArrayVal(getApplicationContext()); //Annetaan listalle sisältö
        Collections.sort(Listat); //Järjestetään listan sisältö (aakkosjärjestys)
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, Listat); //Alustetaan lista-adapteri
        lvMain = (ListView)findViewById(R.id.listView); //Annetaan listanäkymälle ulkonäkö XML-tiedostosta (id = listView)
        lvMain.setAdapter(adapter); //Asetetaan adapteri (Näytetään sisältö)

        //Lisätään tähän intent, jolla päästään omalle listalle
        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View view, final int position, long id) {
                final String selectedItem = ((TextView) view).getText().toString();
                //
                onkoJaettuRef = database.getReference("Users/" + kayttaja_email + "/listat/" + selectedItem);
                //
                AlertDialog.Builder builder = new AlertDialog.Builder(ListaListaActivity.this); //Avataan pikku-ikkuna
                builder.setTitle("Valinnat:"); //Titteli ikkunalle

                builder.setPositiveButton("Poista lista", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (selectedItem.trim().equals(Listat.get(position).trim())) {
                            removeElement(selectedItem, position);
                        } else {
                            Toast.makeText(getApplicationContext(),"Ei voida poistaa", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                builder.setNegativeButton("Siirry listalle", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        //Koitetaan haistella onko lista sinun vai kaverin tekemä
                        onkoJaettuRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if(dataSnapshot.hasChild("kaveri")){ //olet listan luoja
                                    onkoJaettuRef2 = database.getReference("Users/" + kayttaja_email + "/listat/" + selectedItem + "/kaveri");
                                    onkoJaettuRef2.addListenerForSingleValueEvent(new ValueEventListener() {

                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            String kaverinEmail = dataSnapshot.child("kaveri").getValue(String.class);

                                            if (selectedItem.trim().equals(Listat.get(position).trim())) {
                                                Intent meneJakoListalle = new Intent(ListaListaActivity.this, JaettuActivity.class);
                                                meneJakoListalle.putExtra("key2", kayttaja_email);
                                                meneJakoListalle.putExtra("key3", selectedItem);
                                                meneJakoListalle.putExtra("key7", kaverinEmail);
                                                startActivity(meneJakoListalle);
                                            } else {
                                                Toast.makeText(getApplicationContext(),"Siirtyminen ei onnistu jaetulle", Toast.LENGTH_LONG).show();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });


                                }else if(dataSnapshot.hasChild("jakaja")) { // olet käyttäjä:

                                    haeAdmininEmailRef = database.getReference("Users/"+ kayttaja_email + "/listat/" + selectedItem + "/jakaja" + "/jakaja");
                                    haeAdmininEmailRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            String jakajan_email = dataSnapshot.getValue(String.class);
                                            if (selectedItem.trim().equals(Listat.get(position).trim())) {
                                                Intent meneJakoListalle = new Intent(ListaListaActivity.this, JaettuActivity.class);
                                                meneJakoListalle.putExtra("key2", jakajan_email);
                                                meneJakoListalle.putExtra("key3", selectedItem);
                                                startActivity(meneJakoListalle);
                                            } else {
                                                Toast.makeText(getApplicationContext(),"Siirtyminen ei onnistu jaetulle listalle!", Toast.LENGTH_LONG).show();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });



                                } else {
                                    if (selectedItem.trim().equals(Listat.get(position).trim())) { //Olet ainoa käyttäjä
                                        Intent meneListalle = new Intent(ListaListaActivity.this, MainActivity.class);
                                        meneListalle.putExtra("uudenListanNimi", selectedItem);
                                        meneListalle.putExtra("key2", jakajan_email);
                                        startActivity(meneListalle);
                                    } else {
                                        Toast.makeText(getApplicationContext(),"Siirtyminen ei onnistu omalle", Toast.LENGTH_LONG).show();
                                    }


                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        //

                    }
                });
                builder.show(); //Asetetaan viesti-ikkuna näkyväksi
                //

            }
        });

        // lvMain.setBackgroundColor(Color.LTGRAY); Saa vaihdettua listan taustavärin



    }
    //Asetetaan menu näkyväksi
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_listalista, menu);
        return true;
    }
    //Asetetaan kuuntelijat valikko-itemeiden käyttöä varten
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        int id = item.getItemId();
        //Jos valitaan "ohje", suoritetaan seuraavat toiminnot:
        if (id == R.id.action_ohje) {
            AlertDialog alertDialog = new AlertDialog.Builder(ListaListaActivity.this).create(); //Avataan pikku-ikkuna
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
            builder.setTitle(getString(R.string.lisää_lista));
            final EditText input = new EditText(this);
            builder.setView(input);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    //Otetaan yhteys tietokantaan
                    // FirebaseDatabase database = FirebaseDatabase.getInstance();
                    //DatabaseReference myRef = database.getReference();


                    uusiLista = input.getText().toString(); //Poimitaan text-inputista String
                    if(uusiLista.equals(null) || uusiLista.equals("")) { //Tsekataan ettei ole tyhjä input (kaatuu muuten)
                        Toast.makeText(getApplicationContext(),getString(R.string.pakkolisata), Toast.LENGTH_LONG).show();
                    } else {
                        uusiLista = uusiLista.substring(0, 1).toUpperCase() + uusiLista.substring(1).toLowerCase(); //Asetetaan annettu String alkavaksi isolla alkukirjaimella (Listan siisteys)

                        ListatRef.child(uusiLista).setValue(uusiLista); //Sijoitetaan annettu input tietokantaan polkuun: /ostos/input
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
                    ListatRef.setValue(null); //Tyhjennetään annettu tietokantapolku (myref = /ostos), eli kaikki ostokset poistuvat listalta
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

            Intent mene = new Intent(ListaListaActivity.this, reseptiLista.class ); //...luodaan intent jolla voidaan avata toinen luokka (reseptilista) jotta saadaan listalle reseptiobjektit
            startActivity(mene); //Suoritetaan intent -> avataan reseptiLista-luokka

        }
/*
        if(id == R.id.action_jaa){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.jaa_lista));
            final EditText input = new EditText(this);
            builder.setMessage("Anna henkilön sähköposti, jonka listalle haluat liittyä:");
            builder.setView(input);
            builder.setPositiveButton("Liity", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final String jako_sposti = input.getText().toString().trim();
                    //imiRef.child("/jasenet").child("/toinen").setValue(jako_sposti);
                    //imiRef.child("/jasenet").child("/admin").setValue(kayttaja_email);
                    final String jako_sposti2 = jako_sposti.replace(".",",");
                    kayttajanIdHakuRef.child(jako_sposti2).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            jakajan_id = (String) snapshot.getValue();
                            Toast.makeText(ListaListaActivity.this, jakajan_id, Toast.LENGTH_LONG).show();
                            jakoRef = database.getReference("Users/" + jakajan_id + "/lista" + "/ostos");
                            kaveriRef = database.getReference("Users/" + jakajan_id + "/lista" + "/kaveri");
                            kaveriRef.child(kayttaja_id).setValue(kayttaja_id);
                            Intent mene = new Intent(ListaListaActivity.this, JaettuActivity.class ); //...luodaan intent jolla voidaan avata toinen luokka (reseptilista) jotta saadaan listalle reseptiobjektit
                            mene.putExtra("key", jako_sposti2);
                            mene.putExtra("key2", jakajan_id);
                            startActivity(mene); //Suoritetaan intent -> avataan reseptiLista-luokka
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                    //
                    Intent mene = new Intent(ListaListaActivity.this, JaettuActivity.class ); //...luodaan intent jolla voidaan avata toinen luokka (reseptilista) jotta saadaan listalle reseptiobjektit
                    mene.putExtra("key", jako_sposti2);
                    startActivity(mene); //Suoritetaan intent -> avataan reseptiLista-luokka
                    //
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
        } */
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
                ListatRef.child(listaTeksti).removeValue(); //Poistetaan saadun Stringin avulla tietokannasta valittu item, tietokantakuuntelijan kautta päivittyy myös itse listanäkymä
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
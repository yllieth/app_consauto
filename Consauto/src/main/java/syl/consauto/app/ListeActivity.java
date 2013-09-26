package syl.consauto.app;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class ListeActivity extends Activity {

    // #############################################################################################
    // ###                                       VARIABLES                                       ###
    // #############################################################################################

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liste);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        populateList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateList();
    }


    private void populateList() {
        // récupération des données
        SQLiteDatabase bdd = new ConnexionBDD(this).open();
        Cursor cursor = bdd.rawQuery(RecordPleinHandler.getAllTxtQuery(), null);

        // maping pour indiquer quelle données va dans quelle partie du layout d'une ligne de la liste
        String[] fromFieldNames = new String[]{
                RecordPleinHandler.FIELD_DATE,
                RecordPleinHandler.FIELD_PRIX,
                RecordPleinHandler.FIELD_CARBURANT,
                RecordPleinHandler.FIELD_CONSOMMATION,
                RecordPleinHandler.FIELD_DISTANCE
        };
        int[] toListView = new int[]{
                R.id.liste_pleins_item_date,
                R.id.liste_pleins_item_prix,
                R.id.liste_pleins_item_carburant,
                R.id.liste_pleins_item_consommation,
                R.id.liste_pleins_item_distance
        };

        // formatage des valeurs
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,               // context
                R.layout.item_liste_pleins, // layout (modèle) d'une ligne de la liste
                cursor,             // requête
                fromFieldNames,     // valeurs de la base de données
                toListView          // identifiants du layout qui va recevoir les données
        );

        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int column) {
                TextView textView = (TextView) view;

                if (column == RecordPleinHandler.NUM_FIELD_PRIX) {
                    float prix = Float.parseFloat(cursor.getString(column));
                    String lstPrix = (prix > 0)
                            ? String.valueOf(prix / RecordPleinHandler.STORAGE_COEFF_PRIX)
                            : "--";
                    textView.setText(lstPrix + " €");
                    return true;
                } else if (column == RecordPleinHandler.NUM_FIELD_CONSOMMATION) {
                    if (cursor.getString(column) != null) {
                        float conso = Float.parseFloat(cursor.getString(column));
                        textView.setText(String.valueOf(conso / RecordPleinHandler.STORAGE_COEFF_CONSOMMATION) + " l/100");
                    } else {
                        textView.setText("");
                    }
                    return true;
                } else if (column == RecordPleinHandler.NUM_FIELD_DISTANCE) {
                    if (cursor.getString(column) != null) {
                        float distance = Float.parseFloat(cursor.getString(column));
                        textView.setText(String.valueOf(distance / RecordPleinHandler.STORAGE_COEFF_DISTANCE) + " km");
                    } else {
                        textView.setText("");
                    }
                    return true;
                }

                return false;
            }
        });

        // affichage des données
        ListView listView = (ListView) findViewById(R.id.lst_pleins);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parentView, View childView, int position, long id) {
                //RecordPlein recordPlein = new RecordPlein((Cursor) parentView.getItemAtPosition(position));

                Intent i = new Intent(getApplicationContext(), FaireLePleinActivity.class);
                i.putExtra("editID", id);
                startActivity(i);
            }
        });
    }
}
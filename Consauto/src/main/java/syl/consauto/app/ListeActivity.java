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

    // #############################################################################################
    // ###                                     CONSTRUCTEURS                                     ###
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

    // #############################################################################################
    // ###                                   FONCTIONS PRIVEES                                   ###
    // #############################################################################################

    /**
     * Peuple la liste des pleins.
     *
     * <h2>Design</h2>
     * Chaque élément de la liste utilise le design défini par le layout <code>item_liste_pleins.xml</code>.
     * C'est ainsi qu'on peut afficher, pour chaque plein :
     * <ul>
     *  <li>la date</li>
     *  <li>le prix</li>
     *  <li>le carburant utilisé</li>
     *  <li>la consommation (si renseigné)</li>
     *  <li>la distance parcourue avec ce plein</li>
     * </ul>
     *
     * <h2>Peuplement de la liste</h2>
     * Pour afficher tous les pleins enregistrés, il faut :
     * <ul>
     *  <li>les récupérer en base de données</li>
     *  <li>traiter les données, notamment pour afficher les unités</li>
     *  <li>leur appliquer le layout approprié</li>
     *  <li>leur attacher les différents listeners</li>
     * </ul>
     *
     * <h3>Récupération en base de données</h3>
     * La requête utilisée est définie dans <code>RecordPleinHandler.getAllTxtQuery()</code>.
     * Après son exécution, on va récupérer un curseur qui va permettre de peupler la liste en données brutes.
     *
     * <h3>Traitement des données brutes</h3>
     * Cependant les données récupérées en base de données doivent être mise en forme avant d'être affichées.
     * C'est le but du <code>ViewBinder</code>. Il va permettre de décorer l'affichage du prix, de la distance
     * et de la consommation. En effet, ces trois données ont été multipliées pour pouvoir être stockée sous
     * la forme d'entiers. Il faut donc diviser la donnée brute par le coefficient adéquat pour retrouver
     * la valeur initialement saisie. On en profite également pour ajouter les unités appropriées.
     *
     * <h3>Application du layout</h3>
     * C'est le role du <code>SimpleCursorAdapter</code>. Qui, entres autres, prend 2 tableaux en paramètres.
     * Il permettent de mapper l'identifiant des données stockées dans le curseur avec l'élément du layout.
     * On indique ainsi où va aller se placer telle ou telle données.
     * Il est entendu que ces 2 tableaux doivent avoir le même nombre d'éléments.
     *
     * <h3>Listeners</h3>
     * Les listeners utilisés permettent de :
     * <ul>
     *  <li>Modifier un item de la liste lorsqu'on clique dessus. Cette action utiliser le même formulaire
     *  que l'ajout standard d'un plein (<code>FaireLePleinActivity</code>)</li>
     * </ul>
     *
     * <h2>Appels</h2>
     * Cette fonction est appelée dans <code>onPostCreate()</code> et dans <code>onResume()</code>.
     * On permet ainsi de mettre à jour la liste dans tous les cas où elle est affichée (y compris
     * lorsqu'on vient d'ajouter/modifier un plein)
     *
     * @author Sylvain{26/09/2013}
     */
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
package syl.consauto.app;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.List;

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

        // récupération des données
        SQLiteDatabase bdd = new ConnexionBDD(this).open();
        Cursor cursor = bdd.rawQuery(RecordPleinHandler.getAllTxtQuery(), null);

        // maping pour indiquer quelle données va dans quelle partie du layout d'une ligne de la liste
        String[] fromFieldNames = new String[]{
                RecordPleinHandler.FIELD_DATE,
                RecordPleinHandler.FIELD_PRIX
        };
        int[] toListView = new int[]{
                R.id.liste_pleins_item_date,
                R.id.liste_pleins_item_prix
        };

        // formatage des valeurs
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,               // context
                R.layout.liste_pleins_item, // layout (modèle) d'une ligne de la liste
                cursor,             // requête
                fromFieldNames,     // valeurs de la base de données
                toListView          // identifiants du layout qui va recevoir les données
        );

        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int column) {
                if (column == RecordPleinHandler.NUM_FIELD_PRIX) {
                    int prix = Integer.parseInt(cursor.getString(column));
                    TextView textView = (TextView) view;
                    textView.setText(String.valueOf(prix / RecordPleinHandler.STORAGE_COEFF_PRIX) + " €");
                    return true;
                }

                return false;
            }
        });

        // affichage des données
        ListView listView = (ListView) findViewById(R.id.lst_pleins);
        listView.setAdapter(adapter);
    }
}
package syl.consauto.app;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RecordPleinHandler {

    // #############################################################################################
    // ###                                        VARIABLES                                      ###
    // #############################################################################################

    public static final String TABLE_NAME = "Plein";
    ConnexionBDD connexion;

    public static final String FIELD_ID = "id";
    public static final int NUM_FIELD_ID = 0;

    public static final String FIELD_DATE = "Date";
    public static final int NUM_FIELD_DATE = 1;

    public static final String FIELD_QUANTITE = "Quantite";
    public static final int NUM_FIELD_QUANTITE = 2;
    public static final int STORAGE_COEFF_QUANTITE = 100;

    public static final String FIELD_PRIX = "Prix";
    public static final int NUM_FIELD_PRIX = 3;
    public static final int STORAGE_COEFF_PRIX = 100;

    public static final String FIELD_DISTANCE = "Distance";
    public static final int NUM_FIELD_DISTANCE = 4;
    public static final int STORAGE_COEFF_DISTANCE = 100;

    public static final String FIELD_CONSOMMATION = "Consommation";
    public static final int NUM_FIELD_CONSOMMATION = 5;
    public static final int STORAGE_COEFF_CONSOMMATION = 10;

    public static final String FIELD_IS_PLEIN = "Plein";
    public static final int NUM_FIELD_IS_PLEIN = 6;

    // #############################################################################################
    // ###                                      CONSTRUCTEURS                                    ###
    // #############################################################################################

    RecordPleinHandler(ConnexionBDD connexion) {
        this.connexion = connexion;
    }

    public static String createTableQuery() {
        return "CREATE TABLE " + TABLE_NAME + " ("
                + FIELD_ID           + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FIELD_DATE         + " TEXT NOT NULL, "
                + FIELD_QUANTITE     + " INTEGER NOT NULL, "
                + FIELD_PRIX         + " INTEGER NOT NULL, "
                + FIELD_DISTANCE     + " INTEGER, "
                + FIELD_CONSOMMATION + " INTEGER, "
                + FIELD_IS_PLEIN     + " TINYINT"
                + ");";
    }

    public static String dropTableQuery() {
        return "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
    }

    // #############################################################################################
    // ###                                   METHODES D'INSTANCE                                 ###
    // #############################################################################################

    /**
     * Renvoie la liste des colonnes de la table "access".
     *
     * @return String[]
     * @author Sylvain{18/009/2013}
     */
    private String[] getColumns()
    {
        return new String[] {
            FIELD_ID,
            FIELD_DATE,
            FIELD_QUANTITE,
            FIELD_PRIX,
            FIELD_DISTANCE,
            FIELD_CONSOMMATION,
            FIELD_IS_PLEIN
        };
    }

    /**
     * Transforme un record en ContentValues pour l'insertion en base de données
     *
     * @param record RecordPlein
     * @return ContentValues
     * @author Sylvain{18/009/2013}
     */
    private ContentValues formatToBDD(RecordPlein record)
    {
        ContentValues values = new ContentValues();

        values.put(FIELD_DATE,         record.getFormattedDate());
        values.put(FIELD_QUANTITE,     Math.round(record.getQuantite()) * STORAGE_COEFF_QUANTITE);
        values.put(FIELD_PRIX,         Math.round(record.getPrix()) * STORAGE_COEFF_PRIX);

        if (record.getDistance() >= 0)
            values.put(FIELD_DISTANCE, Math.round(record.getDistance()) * STORAGE_COEFF_DISTANCE);

        if (record.getConsommation() >= 0)
            values.put(FIELD_CONSOMMATION, Math.round(record.getConsommation()) * STORAGE_COEFF_CONSOMMATION);

        values.put(FIELD_IS_PLEIN,     (record.isPlein()) ? 1 : 0);

        return values;
    }

    /**
     * Enregistre le Record en base de données.
     *
     * @param record RecordPlein
     * @return Boolean
     * @author Sylvain{18/009/2013}
     */
    public Boolean save(RecordPlein record)
    {
        SQLiteDatabase bdd = connexion.open();
        long insert = bdd.insert(TABLE_NAME, null, formatToBDD(record));
        connexion.close();

        return (insert != -1) ? true : false;
    }

    /**
     * Renvoie un Record précis à partir de son identifiant.
     *
     * @param number int
     * @return Record
     * @author Sylvain{18/009/2013}
     */
    public RecordPlein get(int number)
    {
        SQLiteDatabase bdd = connexion.open();
        Cursor c = bdd.query(
                TABLE_NAME,				// table
                this.getColumns(),		// colonnes
                FIELD_ID,			    // clef
                null, null, null, null	// args, groupBy, having, orderBy
        );

        c.moveToFirst();
        RecordPlein record = new RecordPlein(c);
        connexion.close();

        return record;
    }

    /**
     * Renvoie l'ensemble des Record contnus dans la table "access"
     *
     * @return List
     * @author Sylvain{18/009/2013}
     */
    public List<RecordPlein> getAll()
    {
        List<RecordPlein> collection = new ArrayList<RecordPlein>();

        SQLiteDatabase bdd = connexion.open();
        Cursor c = bdd.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        if (c.moveToFirst()){
            do {
                collection.add(new RecordPlein(c));
            } while (c.moveToNext());
        }

        return collection;
    }

    /**
     * Renvoie le nombre de Record de la table "access"
     *
     * @return int
     * @author Sylvain{18/009/2013}
     */
    public int count()
    {
        return getAll().size();
    }
}

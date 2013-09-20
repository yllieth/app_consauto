package syl.consauto.app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 *
 * @see "http://developer.android.com/guide/topics/data/data-storage.html#db"
 * @see "http://www.tutomobile.fr/comment-utiliser-sqlite-sous-android-tutoriel-android-n%C2%B019/19/10/2010/"
 * @author Sylvain
 */
public class ConnexionBDD extends SQLiteOpenHelper {

    // ####################################################################
    // ###                          CONSTANTES                          ###
    // ####################################################################

    private static final int      DATABASE_VERSION = 1;
    private static final String   DATABASE_NAME = "CONSAUTO";

    // ####################################################################
    // ###                          VARIABLES                           ###
    // ####################################################################



    // ####################################################################
    // ###                         CONSTRUCTEUR                         ###
    // ####################################################################

    ConnexionBDD(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Crée la base de donnée lors de l'installation de l'application
     *
     * @param db SQLiteDatabase
     * @author Sylvain{18/09/2013}
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(RecordPleinHandler.createTableQuery());
    }

    /**
     * Mise à jour de la structure de la base de données lors d'une mise à jour
     *
     * @param db SQLiteDatabase
     * @param oldVersion int
     * @param newVersion int
     * @author Sylvain {18/09/2013}
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(RecordPleinHandler.dropTableQuery());
        onCreate(db);
    }

    // ####################################################################
    // ###                   ACCES A LA BASE DE DONNEES                 ###
    // ####################################################################

    /**
     * Renvoie le gestionnaire de requêtes sur la table "access".
     *
     * @return RecordHandler
     * @author Sylvain {18/09/2013}
     */
    public RecordPleinHandler getTableRecord()
    {
        return new RecordPleinHandler(this);
    }

    /**
     * Ouvre la connexion à la base de données précédemment initialisée.
     *
     * @return SQLiteDatabase
     * @author Sylvain {18/09/2013}
     */
    public SQLiteDatabase open()
    {
        return getWritableDatabase();
    }
}
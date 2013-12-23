package syl.consauto.app;

import android.database.Cursor;
import android.util.Log;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RecordPlein {

    // #############################################################################################
    // ###                                        VARIABLES                                      ###
    // #############################################################################################

    private int id;
    private Date date;
    private String carburant;
    private float quantite;
    private float prix;
    private float distance;
    private float consommation;
    private boolean plein;

    // #############################################################################################
    // ###                                      CONSTRUCTEURS                                    ###
    // #############################################################################################

    RecordPlein(){}

    RecordPlein(Cursor c) {
        setId(c.getInt(RecordPleinHandler.NUM_FIELD_ID));
        setDate(c.getString(RecordPleinHandler.NUM_FIELD_DATE), "yyyy-MM-dd");
        setCarburant(c.getString(RecordPleinHandler.NUM_FIELD_CARBURANT));
        setQuantite((float) c.getInt(RecordPleinHandler.NUM_FIELD_QUANTITE) / RecordPleinHandler.STORAGE_COEFF_QUANTITE);               // (float) 5.12 litres est stocké (int) 512
        setPrix((float) c.getInt(RecordPleinHandler.NUM_FIELD_PRIX) / RecordPleinHandler.STORAGE_COEFF_PRIX);                           // (float) 5.12 euros est stocké (int) 512
        setDistance((float) c.getInt(RecordPleinHandler.NUM_FIELD_DISTANCE) / RecordPleinHandler.STORAGE_COEFF_DISTANCE);               // (float) 5.12 km est stocké (int) 512
        setConsommation((float) c.getInt(RecordPleinHandler.NUM_FIELD_CONSOMMATION) / RecordPleinHandler.STORAGE_COEFF_CONSOMMATION);   // (float) 5.12 litres est stocké (int) 512
        setPlein((c.getInt(RecordPleinHandler.NUM_FIELD_IS_PLEIN) == 0) ? false : true);
    }

    @Override
    public String toString() {
        return getFormattedDate();
    }

    // #############################################################################################
    // ###                                       ACCESSEURS                                      ###
    // #############################################################################################


    public int getId() {
        return id;
    }

    public RecordPlein setId(int id) {
        this.id = id;
        return this;
    }

    public Date getDate() {
        return date;
    }

    public RecordPlein setDate(Date date) {
        this.date = date;
        return this;
    }

    public String getCarburant() {
        return carburant;
    }

    public RecordPlein setCarburant(String carburant) {
        this.carburant = carburant;
        return this;
    }

    public float getQuantite() {
        return quantite;
    }

    public RecordPlein setQuantite(float quantite) {
        this.quantite = quantite;
        return this;
    }

    public float getPrix() {
        return prix;
    }

    public RecordPlein setPrix(float prix) {
        this.prix = prix;
        return this;
    }

    public float getDistance() {
        return distance;
    }

    public RecordPlein setDistance(float distance) {
        this.distance = distance;
        return this;
    }

    public float getConsommation() {
        return consommation;
    }

    public RecordPlein setConsommation(float consommation) {
        this.consommation = consommation;
        return this;
    }

    public boolean isPlein() {
        return plein;
    }

    public RecordPlein setPlein(boolean plein) {
        this.plein = plein;
        return this;
    }

    // #############################################################################################
    // ###                                ACCESSEURS ADDITIONNELS                                ###
    // #############################################################################################

    /**
     * Calculele prix du litre.
     *
     * Pour ce faire, il faut que <code>quantite</code> et <code>prix</code> soient définis. Dans le
     * cas contraire, on renvoie 0.
     *
     * @bug Sans indiquer le <code>decimalSeparator</code> de manière explicite, le convertisseur utilise
     *      celui courrament utilisé en fonction de la <code>LOCALE</code>. Or, même si l'émulateur ne
     *      voit aucun problème, mon téléphone plantait systématiquement, considérent que 1,632 n'est pas
     *      un <code>Float</code> valide (alors que 1.632, l'est).
     * @param decimalSeparator {@link java.lang.String} Indique quel caractère utiliser pour la séparation de la partie décimale.
     * @return {@link java.lang.Float} Le prix d'un kilomètre
     * @author Sylvain{03/10/2013}
     */
    public float getPrixAuLitre(char decimalSeparator) {
        DecimalFormatSymbols s = new DecimalFormatSymbols(Locale.FRANCE);
                             s.setDecimalSeparator(decimalSeparator);
        DecimalFormat formatter = new DecimalFormat("0.000", s);

        return (quantite == 0) ? 0 : Float.parseFloat(formatter.format(prix / quantite));
    }

    /**
     * Calculele prix d'un kilomètre.
     *
     * Pour ce faire, il faut que <code>distance</code> et <code>prix</code> soient définis. Dans le
     * cas contraire, on renvoie 0.
     *
     * @bug Sans indiquer le <code>decimalSeparator</code> de manière explicite, le convertisseur utilise
     *      celui courrament utilisé en fonction de la <code>LOCALE</code>. Or, même si l'émulateur ne
     *      voit aucun problème, mon téléphone plantait systématiquement, considérent que 1,632 n'est pas
     *      un <code>Float</code> valide (alors que 1.632, l'est).
     * @param decimalSeparator {@link java.lang.String} Indique quel caractère utiliser pour la séparation de la partie décimale.
     * @return {@link java.lang.Float} Le prix d'un kilomètre
     * @author Sylvain{03/10/2013}
     */
    public float getPrixDuKilometre(char decimalSeparator) {
        DecimalFormatSymbols s = new DecimalFormatSymbols(Locale.FRANCE);
                             s.setDecimalSeparator(decimalSeparator);
        DecimalFormat formatter = new DecimalFormat("0.000", s);

        return (distance == 0) ? 0 : Float.parseFloat(formatter.format(prix / distance));
    }

    /**
     * Renvoie la date du plein au format <code>"dd / MM / yyyy"</code>
     *
     * @return {@link java.lang.String}
     * @author Sylvain{--/10/2013}
     */
    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd / MM / yyyy");
        return sdf.format(this.getDate());
    }

    /**
     * Renvoie la date du plein.
     *
     * @param  format {@link java.lang.String} Indique le format dans lequel on veut renvoyer la date.
     * @return {@link java.lang.String}
     * @author Sylvain{--/10/2013}
     */
    public String getFormattedDate(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(this.getDate());
    }

    /**
     * Défini la date à laquelle le plein a été fait.
     *
     * @param date {@link java.lang.String} Date du plein
     * @param format {@link java.lang.String} Format de la <code>date</code>
     * @return {@link syl.consauto.app.RecordPlein} <em>fluent interface</em>
     * @author Sylvain{--/10/2013}
     */
    public RecordPlein setDate(String date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            setDate(sdf.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return this;
    }
}

package syl.consauto.app;

import android.database.Cursor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        setId(       c.getInt(RecordPleinHandler.NUM_FIELD_ID));
        setDate(     c.getString(RecordPleinHandler.NUM_FIELD_DATE));
        setCarburant(c.getString(RecordPleinHandler.NUM_FIELD_CARBURANT));
        setQuantite( c.getInt(RecordPleinHandler.NUM_FIELD_QUANTITE     / RecordPleinHandler.STORAGE_COEFF_QUANTITE));       // (float) 5.12 litres est stocké (int) 512
        setPrix(     c.getInt(RecordPleinHandler.NUM_FIELD_PRIX         / RecordPleinHandler.STORAGE_COEFF_PRIX));           // (float) 5.12 euros est stocké (int) 512

        if (c != null)
            setDistance(    c.getInt(RecordPleinHandler.NUM_FIELD_DISTANCE     / RecordPleinHandler.STORAGE_COEFF_DISTANCE));       // (float) 5.12 km est stocké (int) 512

        if (c != null)
            setConsommation(c.getInt(RecordPleinHandler.NUM_FIELD_CONSOMMATION / RecordPleinHandler.STORAGE_COEFF_CONSOMMATION));   // (float) 5.12 litres est stocké (int) 512

        setPlein(      (c.getInt(RecordPleinHandler.NUM_FIELD_IS_PLEIN) == 0) ? false : true);
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

    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd / MM / yyyy");
        return sdf.format(this.getDate());
    }

    public String getFormattedDate(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(this.getDate());
    }

    public Date getDate() {
        return date;
    }

    public RecordPlein setDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd / MM / yyyy");
        try {
            setDate(sdf.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return this;
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
}

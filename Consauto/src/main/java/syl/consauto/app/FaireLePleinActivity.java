package syl.consauto.app;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Permet d'enregistrer un nouveau plein
 *
 * @author Sylvain{17/09/2013}
 */
public class FaireLePleinActivity extends Activity {

    private EditText editDate;
    private Spinner spinChoixCarburant;
    private EditText editPrix;
    private EditText editQuantite;
    private EditText editPrixLitre;
    private EditText editDistance;
    private EditText editConsommation;
    private CheckBox chkComplet;
    private Button bt_enregistrer;
    private ConnexionBDD connexion;
    private RecordPleinHandler pleinHandler;
    private int editID;



    private void init() {
        // champs text
        editDate         = (EditText) findViewById(R.id.date_form_plein_date);
        editPrix         = (EditText) findViewById(R.id.nb_form_plein_prix);
        editQuantite     = (EditText) findViewById(R.id.nb_form_plein_quantite);
        editPrixLitre    = (EditText) findViewById(R.id.nb_form_plein_prixLitre);
        editDistance     = (EditText) findViewById(R.id.nb_form_plein_distance);
        editConsommation = (EditText) findViewById(R.id.nb_form_plein_conso);

        // listes déroulantes
        spinChoixCarburant = (Spinner) findViewById(R.id.spin_form_plein_choix_carburant);

        // cases à cocher
        chkComplet = (CheckBox) findViewById(R.id.chk_form_plein_complet);

        // boutons
        bt_enregistrer = (Button) findViewById(R.id.bt_form_plein_save);
    }

    private void initEditId(Bundle savedInstanceState) {
        editID = -1;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                editID = (int) extras.getLong("editID");
            }
        } else {
            editID = Integer.parseInt((String) savedInstanceState.getSerializable("editID"));
        }
    }

    private void updatePrixLitre(EditText editPrix, EditText editQuantite, EditText editPrixLitre) {
        if (editPrix.getText().length() > 0 && editQuantite.getText().length() > 0) {
            float prix = Float.parseFloat(editPrix.getText().toString());
            float quantite = Float.parseFloat(editQuantite.getText().toString());

            DecimalFormat formatter = new DecimalFormat("0.000");
            editPrixLitre.setText(String.valueOf(formatter.format(prix/quantite)));
        }
    }

    private float getFloatValueFrom(EditText editText, String tag) {
        return Float.parseFloat
            (
                editText.getText().toString().equals("")
                    ? "-1"
                    : editText.getText().toString()
            );
    }

    private Date getDateValueFrom(EditText editText) {
        try {
            return new SimpleDateFormat(getString(R.string.format_date_standard)).parse(editDate.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    private int getPositionFromCarburant(String carburant) {
        String[] carburants = getResources().getStringArray(R.array.liste_carburants);
        int position = -1;

        for (int i = 0 ; i < carburants.length ; i++) {
            if (carburants[i].equals(carburant)) {
                return i;
            }
        }

        return position;
    }

    private boolean isEditContext() {
        return editID > 0;
    }

    // #############################################################################################
    // ###                                     CONSTRUCTEURS                                     ###
    // #############################################################################################

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_plein);


        // connexion à la base de données
        connexion = new ConnexionBDD(this);
        pleinHandler = new RecordPleinHandler(connexion);

        initEditId(savedInstanceState);
        init();

        if (isEditContext()) {
            defaults(pleinHandler.get(editID));
            setEditLabels();
        } else {
            defaults();
        }
        listeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initEditId(null);
    }

    // #############################################################################################
    // ###                              VALEURS PAR DEFAUT COMPLEXES                             ###
    // #############################################################################################

    public void defaults() {
        // date d'aujourd'hui
        editDate.setText(new SimpleDateFormat(getString(R.string.format_date_standard)).format(new Date()));

        // liste des carburants
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.liste_carburants, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinChoixCarburant.setAdapter(adapter);
    }

    private void defaults(RecordPlein recordPlein) {
        defaults();

        editDate.setText(recordPlein.getFormattedDate());
        editQuantite.setText(String.valueOf(recordPlein.getQuantite()));
        spinChoixCarburant.setSelection(getPositionFromCarburant(recordPlein.getCarburant()));
        editPrix.setText(String.valueOf(recordPlein.getPrix()));
        editDistance.setText(String.valueOf(recordPlein.getDistance()));
        editConsommation.setText(String.valueOf(recordPlein.getConsommation()));
        chkComplet.setChecked(recordPlein.isPlein());

        updatePrixLitre(editPrix, editQuantite, editPrixLitre);
    }

    private void setEditLabels()
    {
        TextView titre = (TextView) findViewById(R.id.txt_form_plein);
        titre.setText(R.string.form_plein_titre_modifier);

        TextView bouton = (TextView) findViewById(R.id.bt_form_plein_save);
        bouton.setText(R.string.form_plein_modifier);
    }

    // #############################################################################################
    // ###                                 LISTENERS / HANDLERS                                  ###
    // #############################################################################################

    public void listeners() {
        // calcul du prix au litre par modification de la quantité
        editQuantite.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {}
            public void afterTextChanged(Editable editable) { updatePrixLitre(editPrix, editQuantite, editPrixLitre);}
        });

        // calcul du prix au litre par modification du prix
        editPrix.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {}
            public void afterTextChanged(Editable editable) { updatePrixLitre(editPrix, editQuantite, editPrixLitre);}
        });

        // enregistrement du formulaire
        bt_enregistrer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecordPlein plein = new RecordPlein();

                plein.setDate(getDateValueFrom(editDate))
                     .setCarburant(spinChoixCarburant.getSelectedItem().toString())
                     .setQuantite(getFloatValueFrom(editQuantite, "quantite"))
                     .setPrix(getFloatValueFrom(editPrix, "prix"))
                     .setDistance(getFloatValueFrom(editDistance, "distance"))
                     .setConsommation(getFloatValueFrom(editConsommation, "consommation"))
                     .setPlein((chkComplet.isChecked()) ? true : false);


                boolean isSuccess = (isEditContext())
                    ? pleinHandler.update(editID, plein)
                    : pleinHandler.save(plein);

                if (isSuccess)
                    startActivity(new Intent(getApplicationContext(), ListeActivity.class));
            }
        });
    }

    /**
     *
     * @note If your app supports versions of Android lower than 3.0, be sure that you call
     * getSupportFragmentManager() to acquire an instance of FragmentManager. Also make sure that3
     * your activity that displays the time picker extends FragmentActivity instead of the standard
     * Activity class.
     *
     * @see "http://developer.android.com/guide/topics/ui/controls/pickers.html"
     * @param v
     * @author Sylvain{18/09/2013}
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private class DatePickerFragment extends DialogFragment
                                     implements DatePickerDialog.OnDateSetListener
    {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year  = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day   = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.format_date_standard));

            EditText date_nouveau_plein = (EditText) findViewById(R.id.date_form_plein_date);
            date_nouveau_plein.setText(sdf.format(new Date(year, month, day)));
        }
    }
}
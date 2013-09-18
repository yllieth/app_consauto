package syl.consauto.app;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Permet d'enregistrer un nouveau plein
 *
 * @author Sylvain{17/09/2013}
 */
public class FaireLePleinActivity extends Activity {

    private void updatePrixLitre(EditText editPrix, EditText editQuantite, EditText editPrixLitre) {
        if (editPrix.getText().length() > 0 && editQuantite.getText().length() > 0) {
            float prix = Float.parseFloat(editPrix.getText().toString());
            float quantite = Float.parseFloat(editQuantite.getText().toString());

            DecimalFormat formatter = new DecimalFormat("0.000");
            editPrixLitre.setText(String.valueOf(formatter.format(prix/quantite)));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nouveau_plein);

        defaults();
        listeners();
    }

    // #############################################################################################
    // ###                              VALEURS PAR DEFAUT COMPLEXES                             ###
    // #############################################################################################

    public void defaults() {
        EditText editDate = (EditText) findViewById(R.id.date_new_plein_date);
        editDate.setText(new SimpleDateFormat(getString(R.string.format_date_standard)).format(new Date()));
    }

    // #############################################################################################
    // ###                                 LISTENERS / HANDLERS                                  ###
    // #############################################################################################

    public void listeners() {
        final EditText editPrix = (EditText) findViewById(R.id.nb_new_plein_prix);
        final EditText editQuantite = (EditText) findViewById(R.id.nb_new_plein_quantite);
        final EditText editPrixLitre = (EditText) findViewById(R.id.nb_new_plein_prixLitre);

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

            EditText date_nouveau_plein = (EditText) findViewById(R.id.date_new_plein_date);
            date_nouveau_plein.setText(sdf.format(new Date(year, month, day)));
        }
    }
}
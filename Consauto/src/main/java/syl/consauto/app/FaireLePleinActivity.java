package syl.consauto.app;

import android.app.Activity;
import android.os.Bundle;

/**
 * Permet d'enregistrer un nouveau plein
 *
 * @author Sylvain{17/09/2013}
 */
public class FaireLePleinActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nouveau_plein);
    }
}
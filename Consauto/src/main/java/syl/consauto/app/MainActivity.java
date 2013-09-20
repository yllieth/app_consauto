package syl.consauto.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();

        ConnexionBDD connexionBDD = new ConnexionBDD(this);
        RecordPleinHandler handler = new RecordPleinHandler(connexionBDD);
        int count = handler.count();

        TextView txt = (TextView) findViewById(R.id.txt_home);
        txt.setText("Nombre d'enregistrements : " + count);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;

            case R.id.action_faireLePlein:
                startActivity(new Intent(this, FaireLePleinActivity.class));
                return true;

        }
        return false;
    }

}

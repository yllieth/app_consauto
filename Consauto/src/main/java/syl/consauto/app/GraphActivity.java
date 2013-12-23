package syl.consauto.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.achartengine.ChartFactory;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.text.DecimalFormat;
import java.util.List;

public class GraphActivity extends Activity {

    // #############################################################################################
    // ###                                        VARIABLES                                      ###
    // #############################################################################################

    /**
     * Donne accès aux requêtes pour récupérer les données en base.
     *
     * @return {@link syl.consauto.app.RecordPleinHandler}
     */
    RecordPleinHandler handler;

    // #############################################################################################
    // ###                                      CONSTRUCTEURS                                    ###
    // #############################################################################################

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(createView(getApplicationContext()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.graph, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;

            case R.id.action_main:
                startActivity(new Intent(this, MainActivity.class));
                return true;

            case R.id.action_faireLePlein:
                startActivity(new Intent(this, FaireLePleinActivity.class));
                return true;

            case R.id.action_liste:
                startActivity(new Intent(this, ListeActivity.class));
                return true;

        }
        return false;
    }

    // #############################################################################################
    // ###                                GENERATION DU GRAPHIQUE                                ###
    // #############################################################################################

    /**
     * Crée la vue qui contient le graphique
     *
     * On souhaite afficher, sous forme de coube :
     * - l'évolution de la consommation
     * - l'évolution du prix au litre
     * - l'évolution du prix au kilomètre
     *
     * <h3>La bibliothèque <em>aChartEngine</em></h3>
     * On utilise pour cela la bibliothèe <em>aChartEngine</em> {@link "https://code.google.com/p/achartengine/"}
     * Pour s'installer, elle nécessite :
     * - la déclaration d'une nouvelle <em>Activity</em> dans le manifest :
     *   {@code <activity android:name="org.achartengine.GraphicalActivity"></activity>}
     * - l'importation des classes qui vont être nécessaires, et notamment {@link org.achartengine.ChartFactory}
     *
     * @param context
     * @return {@link android.view.View}
     * @author Sylvain{03/10/2013}
     */
    public View createView(Context context) {
        handler = new RecordPleinHandler(new ConnexionBDD(context));

        XYMultipleSeriesDataset dataset = buildGraphDataset();
        XYMultipleSeriesRenderer renderer = buildGraphRenderer();
        String graphDateFormat = context.getString(R.string.format_date_standard);

        return ChartFactory.getTimeChartView(context, dataset, renderer, graphDateFormat);
    }

    /**
     * Crée les séries qui seront affichées sous la forme de courbes.
     *
     * On compte 3 {@link org.achartengine.model.TimeSeries}, une pour chaque courbe souhaitée.
     * Pour créer ces séries, on commence par récupérer tous les enregistrement de la base de donées.
     * On ne va traiter que ceux qui toutes les données nécessaire (le prix, la distance et la consommation).
     *
     * On calcule également, les valeurs moyennes de chaques séries pour les afficher dans leurs
     * titres respectifs.
     *
     * @return {@link org.achartengine.model.XYMultipleSeriesDataset}
     * @author Sylvain{03/10/2013}
     */
    private XYMultipleSeriesDataset buildGraphDataset() {
        // initialisations
        List<RecordPlein> pleins;
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        TimeSeries consommation = new TimeSeries("Consommation");
        TimeSeries prixDistance = new TimeSeries("Prix au kilomètre");
        TimeSeries prixLitre    = new TimeSeries("Prix du litre");
        float sommeConsommation = 0;
        int   nbConsommation    = 0;
        float sommePrixDistance = 0;
        int   nbPrixDistance    = 0;
        float sommePrixLitre    = 0;
        int   nbPrixLitre       = 0;


        // récupération des données
        pleins = handler.getAll();

        // traitement des données
        for (RecordPlein plein : pleins) {
            if (plein.getDistance() > 0 && plein.getPrix() > 0 && plein.getConsommation() > 0) {
                // consommation
                consommation.add(plein.getDate(), plein.getConsommation());
                sommeConsommation += plein.getConsommation();
                nbConsommation    += 1;

                // prix du kilomètre
                prixDistance.add(plein.getDate(), plein.getPrixDuKilometre('.'));
                sommePrixDistance += plein.getPrixDuKilometre('.');
                nbPrixDistance    += 1;

                // prix du litre
                prixLitre.add(plein.getDate(), plein.getPrixAuLitre('.'));
                sommePrixLitre += plein.getPrixAuLitre('.');
                nbPrixLitre    += 1;
            }
        }

        // affichage des moyennes
        float moyConsommation = (nbConsommation > 0) ? sommeConsommation / nbConsommation : 0;
        float moyPrixDistance = (nbPrixDistance > 0) ? sommePrixDistance / nbPrixDistance : 0;
        float moyPrixLitre    = (nbPrixLitre    > 0) ? sommePrixLitre    / nbPrixLitre : 0;
        DecimalFormat f1 = new DecimalFormat("0.0");
        DecimalFormat f3 = new DecimalFormat("0.000");

        consommation.setTitle(consommation.getTitle() + " (moy. " + f1.format(moyConsommation) + " l/100km)");
        prixDistance.setTitle(prixDistance.getTitle() + " (moy. " + f3.format(moyPrixDistance) + " €/km)");
        prixLitre.setTitle(prixLitre.getTitle() + " (moy. " + f3.format(moyPrixLitre) + " €)");

        dataset.addSeries(consommation);
        dataset.addSeries(prixDistance);
        dataset.addSeries(prixLitre);
        return dataset;
    }

    /**
     * Configure l'affichage de chaque série ainsi que le graphique dans son ensemble.
     *
     * @return {@link org.achartengine.renderer.XYMultipleSeriesRenderer}
     * @author Sylvain{03/10/2013}
     */
    private XYMultipleSeriesRenderer buildGraphRenderer() {
        int[] colors = {Color.RED, Color.MAGENTA, Color.BLUE};
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer(3);

        for (int color : colors) {
            XYSeriesRenderer r = new XYSeriesRenderer();
            r.setColor(color);
            r.setLineWidth(3);
            renderer.addSeriesRenderer(r);
        }

        renderer.setMarginsColor(Color.WHITE);      // Arrière plan (hors graphique) en blanc
        renderer.setBackgroundColor(Color.GRAY);    // Arrière plan (zone graphique) en gris
        renderer.setZoomEnabled(true, true);        // Possibilité de zoomer
        renderer.setZoomButtonsVisible(true);       // Affiche les boutons de zoom
        renderer.setLegendTextSize(25);             // Agandi la taille de la légende pour quelle soit lisible
        renderer.setXLabelsColor(Color.BLACK);      // Couleurs des dates en abscisse

        return renderer;
    }
}

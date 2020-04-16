package com.example.mongoconnect;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

public class PieActivity extends AppCompatActivity {

    PieChart pieChart;
    ArrayList<JSONObject>jsonObjects;
    private Button pie_plot;
    private TextView tvdate;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private static final String TAG = "PieActivity";

    //#############################################################################################################################

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie);

        tvdate = (TextView) findViewById(R.id.tvdate);
        tvdate.setText("mm-yyyy");

        Toast.makeText(PieActivity.this, tvdate.getText(), Toast.LENGTH_LONG).show();

        //$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
        // create date set Listener for activity after date is selected
        //$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date = month+ "-" + year;
                tvdate.setText(date);

                plot();
            }
        };


        //$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
        // on button click month and year will be selected
        //$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$

        pie_plot = (Button) findViewById(R.id.pieplot);
        pie_plot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(PieActivity.this, ""+tvdate.getText().toString()+ " "+!tvdate.equals("mm-yyyy"), Toast.LENGTH_LONG).show();

                MonthPickerDialog pd = new MonthPickerDialog();
                pd.setListener(dateSetListener);
                pd.show(getSupportFragmentManager(), "MonthPickerDialog" );


            }
        });

    }

    //#################################################################################################################################
    //plot is used to plot pie chart
    public void plot()
    {

        HashMap<String, Double> pie_pair = new HashMap<String, Double>();
        //int entered_month = 1;
        //int entered_month = Integer.parseInt(edit_month.getText().toString());
        Toast.makeText(PieActivity.this, ""+tvdate.getText().toString(), Toast.LENGTH_LONG).show();
        int entered_month = Integer.parseInt(tvdate.getText().toString().substring(0,1));
        int entered_year = Integer.parseInt(tvdate.getText().toString().substring(2,6));

        Log.d(TAG, entered_month+" "+entered_year);

        pieChart = (PieChart)findViewById(R.id.PieChart);

        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5,10,5,5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);

        pieChart.setDrawHoleEnabled(true);
        // pieChart.setDrawHoleEnabled(false);
        pieChart.setHoleColor(Color.BLUE);
        pieChart.setTransparentCircleRadius(61f);

        MongoConnection mongoConnection = new MongoConnection(getApplicationContext());
        try {
            jsonObjects = mongoConnection.execute("").get();
        }
        catch(Exception e)
        {

        }

        //$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
        // 1. extract month and prepare data
        //$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$

        for(int i=0;i<jsonObjects.size(); i++) {
            try {

                String type = jsonObjects.get(i).getJSONObject("_id").getString("type");
                double units = jsonObjects.get(i).getInt("units");
                String date = jsonObjects.get(i).getJSONObject("_id").getString("Date");

                int month1 = Integer.parseInt(date.substring(5, 7));
                int year1 = Integer.parseInt(date.substring(0,4));
                Log.d(TAG, month1 + " "+year1 );

                if(month1 == entered_month && year1 == entered_year)
                {
                    double temp = 0;
                    if(pie_pair.containsKey(type))
                        temp = pie_pair.get(type);
                    //pie_pair.remove(type);
                    pie_pair.put(type, temp + units);
                }

            } catch(Exception e) {
                Toast.makeText(PieActivity.this, "error: "+e.toString(), Toast.LENGTH_LONG).show();
            }
        }

        ArrayList<PieEntry> yValues = new ArrayList<>();


        for(String i: pie_pair.keySet()){
            yValues.add(new PieEntry(pie_pair.get(i).floatValue(), i));
            Toast.makeText(PieActivity.this, pie_pair.get(i) + " "+ i,Toast.LENGTH_LONG).show();
        }


        //$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
        //2: plot data
        //$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$

        PieDataSet dataSet = new PieDataSet(yValues,"Monthly units");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(10f);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        pieChart.animateY(1000, Easing.EasingOption.EaseInOutCirc);
        Description description = new Description();
        description.setText("Device wise electricity consumption");
        description.setTextSize(7f);
        pieChart.setDescription(description);


        PieData data = new PieData(dataSet);
        data.setValueTextColor(Color.BLACK);
        data.setValueTextSize(10f);

        pieChart.setData(data);
    }

}
//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@



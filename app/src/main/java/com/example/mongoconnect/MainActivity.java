package com.example.mongoconnect;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.mongodb.Mongo;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.connection.ClusterSettings;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static java.util.Arrays.asList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText editText;
    BarChart barChart;
    String[] dates=new String[366];
    String FromDate="",ToDate="";

    ArrayList<BarEntry> yValues = new ArrayList<>();


    int switched_on_time=0,units_consumed=0,k=0;

    ArrayList<JSONObject>jsonObjects;

    private DatePickerDialog.OnDateSetListener dateSetListener;
    Button DatePicker;
    Spinner DatePicker2;
    TextView DateFrom,DateTo;
    Button button_pie;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*button_pie = (Button) findViewById(R.id.piechart);
        button_pie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPieActivity();
            }
        });*/

        Intent intent = new Intent(MainActivity.this,LineChartVis.class);
        startActivity(intent);

//####################################################################################
        barChart=(BarChart)findViewById(R.id.BarChart1);
        barChart.getDescription().setEnabled(false);
        barChart.setFitBars(true);

//################################################################################
        DatePicker = (Button)findViewById(R.id.DatePicker);
        DatePicker2 = (Spinner) findViewById(R.id.DatePicker2);
        DatePicker2.setOnItemSelectedListener(MainActivity.this);
        DateFrom = (TextView)findViewById(R.id.DateFrom);
        DateTo = (TextView)findViewById(R.id.DateTo);
        DateFrom.setText("From : ");
        DateTo.setText("To : ");


//####################################################################################
        final MongoConnection mongoConnection = new MongoConnection(getApplicationContext());
        try {
            String Device_name = "iron";
            jsonObjects = mongoConnection.execute(Device_name).get();
        }
        catch(Exception e)
        {

        }
//######################################################################################

        DatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar DateFrom_To = Calendar.getInstance();
               // int year = DateFrom_To.get(Calendar.YEAR);
                //int month = DateFrom_To.get(Calendar.MONTH);
                //int day = DateFrom_To.get(Calendar.DAY_OF_MONTH);
                int year = 1970;
                int month = 0;
                int day = 1;

                DatePickerDialog dialog = new DatePickerDialog(MainActivity.this,android.R.style.Theme_Material_Dialog
                        ,dateSetListener,year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(android.widget.DatePicker datePicker, int year, int month, int day) {

                month+=1;
                    if(FromDate.equals("true"))
                    {
                        FromDate = String.valueOf(year)+"-"+month+"-"+day;
                        DateFrom.setText("From : "+FromDate);

                        if(!ToDate.equals("true") && !ToDate.equals(""))
                            show();
                    }
                    else
                    {
                        ToDate = String.valueOf(year)+"-"+month+"-"+day;
                        DateTo.setText("To : "+ToDate);

                        if(!FromDate.equals("true") && !FromDate.equals(""))
                             show();
                    }

            }
        };
//##################################################################################################

    }

    /*public void openPieActivity() {
        Intent intent = new Intent(this, PieActivity.class);
        startActivity(intent);
    }*/

//##################################################################################################
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(adapterView.getSelectedItem().toString().equals("SELECT FROM DATE"))
        {
            FromDate = "true";

        }
        else {
            ToDate = "true";
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
//###################################################################################################




    public void show()
    {
        switched_on_time=0;
        units_consumed=0;
        yValues.clear();

        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");

        k=1;

        for(int i=0;i<jsonObjects.size();i++)
        {
            try {
                String date=jsonObjects.get(i).getJSONObject("_id").getString("Date");
                Date FetchedDate=simpleDateFormat.parse(date);

                Date from=simpleDateFormat.parse(FromDate);
                Date to=simpleDateFormat.parse(ToDate);

                float watt=0;

                if(from.compareTo(to)>0)
                {
                    Toast.makeText(this,"Please Enter Valid Date Range",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(FetchedDate.compareTo(from)>=0 && FetchedDate.compareTo(to)<=0) {
                    watt=(float)jsonObjects.get(i).getDouble("units");
                    yValues.add(new BarEntry(k,(float) watt));
                    dates[k-1]=date;
                    k++;
                }

               }
            catch(Exception e){}
            }

            plot_data();

    }


    private void plot_data() {
        XAxis xAxis=barChart.getXAxis();
        Toast.makeText(this,"length"+yValues.size(),Toast.LENGTH_LONG).show();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        BarDataSet barDataSet = new BarDataSet(yValues, "Data Set");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setDrawValues(true);
        BarData data = new BarData(barDataSet);
        data.setValueFormatter(new MyValueFormatter());
        xAxis.setValueFormatter(new MyAxisValueFormatter(dates));
        xAxis.setTextSize(0.5f);
        xAxis.setLabelRotationAngle(90);
        barChart.setData(data);
        barChart.invalidate();
        barChart.setFitBars(true);
        barChart.animateXY(500, 3500, Easing.EasingOption.EaseInBounce, Easing.EasingOption.EaseInBounce);
    }



    private class MyValueFormatter implements IValueFormatter
    {

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return String.valueOf(value);
        }
    }

    private class MyAxisValueFormatter implements IAxisValueFormatter
    {
        String[]names=new String[100];
        public MyAxisValueFormatter(String[]arr)
        {
            this.names=arr;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {

            if (value <=yValues.size())
            {
                for (float i = 1.0f; i < yValues.size()+1.0f; i += 1.0f)
                {
                    if (value == i)
                        return names[(int) (i-1)];
                }
            }
            return "";
        }
    }



}



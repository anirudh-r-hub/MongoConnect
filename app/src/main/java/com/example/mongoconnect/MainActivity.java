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

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static java.util.Arrays.asList;

public class MainActivity extends AppCompatActivity {
    EditText editText;
    BarChart barChart;
    String[] dates=new String[100];
    ArrayList<BarEntry> yValues = new ArrayList<>();
    String name;
    int switched_on_time=0,units_consumed=0,k=0;
    ArrayList<JSONObject>jsonObjects;
    ArrayList<String>collection_names=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        editText = (EditText) findViewById(R.id.hello);
        barChart=(BarChart)findViewById(R.id.BarChart1);
        barChart.getDescription().setEnabled(false);


        barChart.setFitBars(true);
        MongoConnection mongoConnection = new MongoConnection(getApplicationContext());
        try {
            jsonObjects = mongoConnection.execute(new Void[0]).get();
        }
        catch(Exception e)
        {

        }





    }


    public int check_timestamp_range(int time_stamp)
    {
        Date date=new Date(time_stamp*1000L);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return calendar.get(Calendar.DAY_OF_MONTH);




    }

    public void show(View view)
    {
        switched_on_time=0;
        yValues.clear();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-mm-dd");
        k=1;
        units_consumed=0;
        for(int i=0;i<jsonObjects.size();i++)
        {
            try {
                String date=jsonObjects.get(i).getJSONObject("_id").getString("Date");
                Date xyz=simpleDateFormat.parse(date);

                Date from=simpleDateFormat.parse("1970-01-01");
                Date to=simpleDateFormat.parse("1970-01-08");

                int start=0,end=0,watt=0;

                if(xyz.compareTo(from)>=0 && xyz.compareTo(to)<=0) {
                    watt=jsonObjects.get(i).getInt("count");
                    //Toast.makeText(this,date,Toast.LENGTH_LONG).show();
                    yValues.add(new BarEntry(k,(float)watt));
                    dates[k-1]=date;
                    //Toast.makeText(this,"X: "+yValues.get(k-1).getX(),Toast.LENGTH_LONG).show();
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



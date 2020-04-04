package com.example.mongoconnect;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
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
    String name;
    int switched_on_time=0,units_consumed=0;
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
        //editText.setText("List of collections wil be displayed in the form of toast messages");
        MongoConnection mongoConnection = new MongoConnection(getApplicationContext());
        try {
            jsonObjects = mongoConnection.execute(new Void[0]).get();
        }
        catch(Exception e)
        {

        }
        Toast.makeText(this,"hello",Toast.LENGTH_LONG).show();




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
        //Toast.makeText(this,"Plotting data",Toast.LENGTH_LONG).show();
        int time_stamp_end=0,count=0,time_stamp_start=0;
        int user_entered_day=Integer.parseInt(editText.getText().toString());
        switched_on_time=0;
        units_consumed=0;
        for(int i=0;i<jsonObjects.size();i++)
        {
            try {
                time_stamp_end = Integer.parseInt(jsonObjects.get(i).getString("end"));
                time_stamp_start = Integer.parseInt(jsonObjects.get(i).getString("start"));
                int topic=Integer.parseInt(jsonObjects.get(i).getString("topic"));
                int day_of_month_end=check_timestamp_range(time_stamp_end);
                int day_of_month_start=check_timestamp_range(time_stamp_start);
                int start=0,end=0,watt=0;

                //Toast.makeText(this,"Plotting data",Toast.LENGTH_LONG).show();
                if(day_of_month_end==user_entered_day && day_of_month_start==user_entered_day && topic==1001) {
                    count++;
                    //Toast.makeText(this,""+end,Toast.LENGTH_LONG).show();
                    start=jsonObjects.get(i).getInt("start");
                    end=jsonObjects.get(i).getInt("end");
                    watt=jsonObjects.get(i).getInt("watt");
                    switched_on_time+=(end-start);
                    //Toast.makeText(this,""+(end-start),Toast.LENGTH_LONG).show();
                    units_consumed+=(watt)*((end-start)/3600);

                }



               }
            catch(Exception e){}
            }
            plot_data();
        //Toast.makeText(this,"count: "+count,Toast.LENGTH_LONG).show();

    }

    private void plot_data() {

        switched_on_time=switched_on_time/3600;
        ArrayList<BarEntry> yValues = new ArrayList<>();

        yValues.add(new BarEntry(switched_on_time,units_consumed));


        BarDataSet barDataSet = new BarDataSet(yValues,"Data Set");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setDrawValues(true);
        BarData data = new BarData(barDataSet);

        barChart.setData(data);
        barChart.invalidate();

        // barChart.animateY(2500, Easing.EasingOption.EaseInBounce);
        // barChart.animateX(1500,Easing.EasingOption.EaseInBounce);
        barChart.animateXY(2500,4500, Easing.EasingOption.EaseInBounce,Easing.EasingOption.EaseInBounce);



    }


}

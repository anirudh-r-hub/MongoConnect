package com.example.mongoconnect;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class LineChartVis extends AppCompatActivity {

    LineChart lineChart;
    ArrayList<JSONObject>jsonObjects;
    HashMap<String, Double> LineChartData = new HashMap<String, Double>();
    String[] Months = {"JAN","FEB","MAR","APR","MAY","JUNE","JULY","AUG","SEP","OCT","NOV","DEC"};
    String[] CurrentMonths = new String[12];
    ArrayList<Entry> yValues = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_chart_vis);



        lineChart = (LineChart)findViewById(R.id.Linechart);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.getXAxis().setTextColor(Color.WHITE);
        lineChart.getAxisLeft().setTextColor(Color.WHITE);
        lineChart.getAxisRight().setTextColor(Color.WHITE);
        lineChart.getXAxis().setTextSize(15f);
        lineChart.getAxisLeft().setTextSize(15f);
        lineChart.getAxisRight().setTextSize(15f);
        lineChart.getLegend().setTextColor(Color.WHITE);
        lineChart.getLegend().setTextSize(15f);
        lineChart.setBackgroundColor(Color.parseColor("#757271"));
        lineChart.setActivated(true);
        lineChart.setFocusable(true);
        lineChart.setBorderColor(Color.WHITE);
        lineChart.animateXY(2250, 4500, Easing.EasingOption.EaseInOutBounce, Easing.EasingOption.EaseInOutCubic);

//###################################################################################################
        final MongoConnection mongoConnection = new MongoConnection(getApplicationContext());
        try {
            String Device_name = "iron";
            jsonObjects = mongoConnection.execute(Device_name).get();
        }
        catch(Exception e)
        {

        }
//######################################################################################


                plot();


    }







   //#####################################################################################################
        void plot()
        {
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
            Calendar calendar = Calendar.getInstance();
            int FetchedMonth;
            int j=1;
            for(int i=0;i<jsonObjects.size();i++)
            {
                try {
                    String Fetched_Date = jsonObjects.get(i).getJSONObject("_id").getString("Date");
                    Date FetchedDate=simpleDateFormat.parse(Fetched_Date);
                    calendar.setTime(FetchedDate);
                    FetchedMonth = calendar.get(Calendar.MONTH);
                    String MonthString = Months[FetchedMonth];

                    if(!LineChartData.containsKey(MonthString))
                        LineChartData.put(MonthString,0.0);

                    LineChartData.put(MonthString,LineChartData.get(MonthString)+jsonObjects.get(i).getDouble("units"));
                    //Toast.makeText(this,MonthString+LineChartData.get(MonthString)+":",Toast.LENGTH_SHORT).show();


                }catch (Exception e){}

            }

            for(String key : LineChartData.keySet())
            {
                yValues.add(new Entry(j, LineChartData.get(key).floatValue()));
                CurrentMonths[j-1]=key;
                //Toast.makeText(this,"Month:"+CurrentMonths[j-1],Toast.LENGTH_SHORT).show();

                j++;
            }

            XAxis xAxis = lineChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

            LineDataSet set1 = new LineDataSet(yValues,"IRON");
            set1.setFillAlpha(110);
            set1.setLineWidth(3f);
            set1.setColor(Color.RED);
            set1.setValueTextSize(10f);
            set1.setValueTextColor(Color.WHITE);
            set1.setCircleColor(Color.GREEN);
            set1.setCircleRadius(6f);

            ArrayList<ILineDataSet> dataset = new ArrayList<>();
            dataset.add(set1);
            LineData data = new LineData(dataset);
            data.setValueFormatter(new LineChartVis.MyValueFormatter());
            xAxis.setValueFormatter(new LineChartVis.MyAxisValueFormatter(CurrentMonths));
            lineChart.setData(data);



        }
    //#####################################################################################################





    private class MyValueFormatter implements IValueFormatter
    {

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return String.valueOf(value);
        }
    }

    private class MyAxisValueFormatter implements IAxisValueFormatter
    {
        String[] XAxisMonths = new String[12];
        public MyAxisValueFormatter(String[] CurrentMonths)
        {
            this.XAxisMonths = CurrentMonths;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {

            if (value <=yValues.size())
            {

                for (float i = 1.0f; i < yValues.size()+1.0f; i += 1.0f)
                {
                    if (value == i)
                        return XAxisMonths[(int) (i-1)];
                }
            }
            return "";
        }
    }
//########################################################################################################
}

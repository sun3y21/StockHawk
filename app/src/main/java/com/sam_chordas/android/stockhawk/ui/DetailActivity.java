package com.sam_chordas.android.stockhawk.ui;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    LineChart mChart;
    String symbol;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent i=getIntent();
        symbol=i.getStringExtra("symbol");
        mChart=(LineChart)findViewById(R.id.chart1);
        mChart.setPinchZoom(true);
        setData();
    }


    private void setData() {

        ArrayList<Entry> values = new ArrayList<Entry>();

        Cursor cursor = getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                null, QuoteColumns.SYMBOL + "= ?",
                new String[] { symbol }, null);


        cursor.moveToFirst();
        int i=0;
        while(!cursor.isLast())
        {
            values.add(new Entry(i,cursor.getFloat(cursor.getColumnIndex("bid_price"))));
            i++;
            cursor.moveToNext();
        }
        values.add(new Entry(i,cursor.getFloat(cursor.getColumnIndex("bid_price"))));
        i++;
        if(cursor.getCount()==0)
        {
            //dummy values
            values.add(new Entry(10,10));
            values.add(new Entry(20,20));
        }


        LineDataSet set1;

        if (mChart.getData() != null && mChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet)mChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        }
        else
        {
            // create a dataset and give it a type
            set1 = new LineDataSet(values, symbol.toUpperCase()+" Stock");

            // set the line to be drawn like this "- - - - - -"
            set1.enableDashedLine(10f, 5f, 0f);
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setColor(Color.WHITE);
            set1.setCircleColor(Color.GREEN);
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set1.setDrawFilled(true);
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);
            set1.setFillColor(Color.WHITE);


            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1); // add the datasets

            // create a data object with the datasets
            LineData data = new LineData(dataSets);

            // set data
            mChart.setData(data);
        }
    }
}

package com.android.heartbeat;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private LineGraphSeries<DataPoint> series;
    private int lastX=0;
    private int a = 0;
    private long ans = 0;
    int hbeat = 0;
    FirebaseDatabase database;
    DatabaseReference myRef;
    ImageView imageView;
    Animation anim;
    TextView bpm;
    JSONObject obj;
    AlertDialog.Builder alertDialogBuilder;
    LineChart chart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("data");
        alertDialogBuilder = new AlertDialog.Builder(this);
        imageView = (ImageView) findViewById(R.id.image);
        anim = AnimationUtils.loadAnimation(this, R.anim.pulse);
        bpm = (TextView) findViewById(R.id.bpm);
        GraphView graph = (GraphView) findViewById(R.id.graph);
        // data
        series = new LineGraphSeries<DataPoint>();
        graph.addSeries(series);
        series.setThickness(8);
        // customize a little bit viewport
        Viewport viewport = graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(20);
        viewport.setMaxY(120);
        viewport.setScrollable(true);


        /*chart = (LineChart) findViewById(R.id.chart);
        chart.setNoDataText("");
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setDrawGridBackground(false);
        chart.setPinchZoom(true);
        chart.setBackgroundColor(Color.LTGRAY);

        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);

        Legend l=chart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.WHITE);

        XAxis x1=chart.getXAxis();
        x1.setTextColor(Color.WHITE);
        x1.setDrawGridLines(true);
        x1.setAvoidFirstLastClipping(true);

        YAxis y1=chart.getAxisLeft();
        y1.setTextColor(Color.WHITE);
        y1.setAxisMaximum(120f);
        y1.setDrawGridLines(true);

        YAxis y2=chart.getAxisRight();
        y2.setEnabled(false);*/
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try {
                    obj = new JSONObject(dataSnapshot.getValue().toString());
                    int n = Integer.parseInt(obj.getString("bpm"));
                    if (n >120 || n < 20)
                    {
                        emergency(n);
                    }
                    bpm.setText(obj.getString("bpm"));
                    setbeat(n);
                    ans = 60000 / n;
                    anim.setDuration(ans);
                    imageView.setAnimation(anim);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        // we're going to simulate real time with thread that append data to the graph
        new Thread(new Runnable() {

            @Override
            public void run() {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            addEntry(getbeat());
                        }
                    });

                    // sleep to slow down the add of entries
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        // manage error ...
                    }
                }

        }).start();
    }



    // add random data to graph
    private void addEntry(int b) {
        // here, we choose to display max 10 points on the viewport and we scroll to end
        series.appendData(new DataPoint(lastX++, b), true, 5);
        /*LineData data=chart.getLineData();

        if(data!=null) {
            LineDataSet set = (LineDataSet) data.getDataSetByIndex(0);
            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }
            data.addEntry(new Entry(((float) b), set.getEntryCount()), 0);

            chart.notifyDataSetChanged();

            chart.setVisibleXRange(0f, 6f);

            chart.moveViewToX(data.getXMax() - 7);
        }*/

    }


    private LineDataSet createSet()
    {
        LineDataSet set=new LineDataSet(null, "Data");
        set.setDrawCircles(true);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.getHoloBlue());
        set.setCircleColor(ColorTemplate.getHoloBlue());
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(244,117,177));
        return set;
    }

    public void setbeat(int a)
    {
        hbeat=a;
    }

    public int getbeat()
    {
        return hbeat;
    }

    private void emergency(int a)
    {
        if(a>120)
        {
            alertDialogBuilder.setMessage("Possible problem: Trachvcardia");
            alertDialogBuilder.setPositiveButton("Would like to call Doctor",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            startActivity(intent);

                        }
                    });

            alertDialogBuilder.setNegativeButton("No such issue",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        else if(a<20)
        {
            alertDialogBuilder.setMessage("Possible problem: Dizzyness, Fainting");
            alertDialogBuilder.setPositiveButton("Would like to call Doctor",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            startActivity(intent);

                        }
                    });

            alertDialogBuilder.setNegativeButton("No such issue",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }
}



package com.example.android.projectfanta;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Calendar;
import java.util.Date;


public class WeekFragment extends Fragment {
    //Instance variable so that view can be accessed by createGraphWeek method as well
    public View view;

    /**
     * numDays The number of days including starting day to end day. E.g from 12/10 - 12/12, numDays
     * would be 3.
     * startDayCalendar The Calendar Object for starting day
     * nutrient The name of the nutrient
     * double[] An array of nutrient intake
     * standardIntake I assume the intake is constant throughout... If not please tell me to fix it
     * I didn't check for nutrientIntake.length = numDays, I assume they are equal
     *
     * How to create and set a Calendar instance
     * Calendar calendar = Calendar.getInstance();
     * Month Field 0-11 Represent January to December
     * calendar.set(2017,11,1);
     */
    public void createGraphWeek(int numDays, Calendar startDayCalendar, String nutrient,
    double[] nutrientIntake, double standardIntake)
    {
        Calendar calendar1 = (Calendar)startDayCalendar.clone();
        Calendar calendar2 = (Calendar)startDayCalendar.clone();

        DataPoint dp[] = new DataPoint[numDays];
        long start = calendar1.getTimeInMillis();       //Use for the viewingWindow

        //Create Data set for user line
        for(int i = 0; i < dp.length; i++)
        {
            DataPoint point = new DataPoint(calendar1.getTime(), nutrientIntake[i]);
            dp[i] = point;
            calendar1.add(Calendar.DATE,1);
        }
        long end = calendar1.getTimeInMillis();         //Use for the viewingWindow

        //Create Data set for standard
        DataPoint dpStd[] = new DataPoint[numDays];
        for(int i = 0; i < numDays; i++)
        {
            dpStd[i] = new DataPoint(calendar2.getTime(),standardIntake);
            calendar2.add(Calendar.DATE,1);
        }

        //Drawing the graph on View
        GraphView graph = (GraphView) view.findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dp);
        series.setTitle("User");

        // set date label formatter
        // use static labels for horizontal and vertical labels
        // set date label formatter
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
        graph.getGridLabelRenderer().setHorizontalLabelsAngle(35);
        graph.getGridLabelRenderer().setNumHorizontalLabels(numDays+1);
        graph.getGridLabelRenderer().setTextSize(36);

        // set manual x bounds to have nice steps
        //Axis Label
        graph.getViewport().setMinX(start);
        graph.getViewport().setMaxX(end);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getGridLabelRenderer().setHumanRounding(false);

        //Legend
        graph.getGridLabelRenderer().setHorizontalAxisTitle("date");
        graph.getGridLabelRenderer().setVerticalAxisTitle("mg");

        //Graphing standard intake
        LineGraphSeries<DataPoint> standard = new LineGraphSeries<>(dpStd);
        standard.setTitle("Standard");
        standard.setColor(Color.GREEN);

        //Adding them to the view
        graph.addSeries(series);
        graph.addSeries(standard);
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.BOTTOM);
        graph.setTitle(nutrient+" "+"Intake");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_week, container, false);
        return view;
    }
}

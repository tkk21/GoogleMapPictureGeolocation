package com.example.haotian.tutorial32;

import android.location.Location;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class LocationCSV {

    private File file;
    private BufferedWriter bufferedWriter;
    private static final String attributes = "TimeStamp,Latitude,Longitude";

    public LocationCSV() {
        String root = Environment.getExternalStorageDirectory().toString();
        File csvDir = new File (root + "/DCIM/");
        csvDir.mkdir();
        file = new File(csvDir, "BuildingLocationData.csv");
    }

    public void init (){
        boolean fileExists = file.exists();
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(file, true));
        }
        catch(IOException e){
            Log.wtf("LocationCSV", String.format("The file %s already exists", file.toString()));
            return;
        }
        try {
            if (!fileExists) {
                bufferedWriter.append(attributes);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        }
        catch(IOException e){
            Log.wtf("LocationCSV", "could not write the attributes");
        }
    }

    public void write (String timeStamp, Location location){
        try {
            bufferedWriter.append(
                    String.format("%s,%f,%f",
                            timeStamp, location.getLatitude(), location.getLongitude()));
            bufferedWriter.newLine();
            bufferedWriter.flush();
        }
        catch(IOException e){
            Log.wtf("LocationCSV", "could not write a line of data");
            e.printStackTrace();
        }
    }

    public void close (){
        try {
            bufferedWriter.close();
        }
        catch(IOException e){
            Log.wtf("LocationCSV", "failed to close the file");
        }
    }
}
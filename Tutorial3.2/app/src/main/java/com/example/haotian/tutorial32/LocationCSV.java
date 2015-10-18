package com.example.haotian.tutorial32;

import android.location.Location;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class LocationCSV {

    private File file;
    private ArrayList<String> timeStampList;
    private ArrayList<Location> locationList;
    private static final String attributes = "TimeStamp,Latitude,Longitude";

    public LocationCSV(File file) {
        this.file = file;
        timeStampList = new ArrayList<String>();
        locationList = new ArrayList<Location>();
    }

    public void record (String timeStamp, Location location){
        timeStampList.add(timeStamp);
        locationList.add(location);
    }

    public void write (){
        BufferedWriter bufferedWriter;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(file));
        }
        catch(IOException e){
            Log.wtf("LocationCSV", String.format("The file %s already exists", file.toString()));
            return;
        }
        try{
            bufferedWriter.append(attributes);
            bufferedWriter.newLine();
            for (int i = 0; i<timeStampList.size(); i++){
                Location location = locationList.get(i);
                bufferedWriter.append(
                        String.format("%s,%f,%f",
                                timeStampList.get(i), location.getLatitude(), location.getLongitude()));
                bufferedWriter.newLine();
            }
        }
        catch (IOException e){
            Log.wtf("LocationCSV", "Could not write to file");
            e.printStackTrace();
        }
        finally {
            try{
                bufferedWriter.close();
            }
            catch(IOException e){
                Log.wtf("LocationCSV", "Could not close the writer");
            }
        }
    }
}
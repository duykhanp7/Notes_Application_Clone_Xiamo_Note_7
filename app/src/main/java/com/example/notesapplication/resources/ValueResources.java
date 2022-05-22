package com.example.notesapplication.resources;

import android.annotation.SuppressLint;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ValueResources {



    List<String> listDateString = new ArrayList<>();
    List<Date> listDateDate = new ArrayList<>();
    public List<String> dateFormatStandardList = new ArrayList<>();



    String[] strHours = new String[]{"00","01","02","03","04","05","06","07","08","09","10"
            ,"11","12","13","14","15","16","17","18","19","20","21","22","23"};
    String[] strMinutes = new String[]{"00","01","02","03","04","05","06","07","08","09","10"
            ,"11","12","13","14","15","16","17","18","19","20","21"
            ,"22","23","24","25","26","27","28","29","30","31","32"
            ,"33","34","35","36","37","38","39","40","41","42","43"
            ,"44","45","46","47","48","49","50","51","52","53","54"
            ,"55","56","57","58","59"};

    public ValueResources(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                InitDateStringListAndDateList();
            }
        }).start();
    }

    public void InitDateStringListAndDateList(){
        Calendar calendarStart = Calendar.getInstance();
        calendarStart.set(Calendar.YEAR,1901);
        calendarStart.set(Calendar.MONTH,1);
        calendarStart.set(Calendar.DATE,1);
        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.set(Calendar.YEAR,2099);
        calendarEnd.set(Calendar.MONTH,12);
        calendarEnd.set(Calendar.DATE,31);

        listDateDate = getAllDaysBetweenTwoDate(calendarStart.getTime(),calendarEnd.getTime());
        listDateString.addAll(convertListDateToStringList(listDateDate));
        dateFormatStandardList.addAll(convertToListDateFormatStandard(listDateDate));
    }

    public List<Date> getAllDaysBetweenTwoDate(Date start, Date end){
        List<Date> dateList = new ArrayList<>();
        Calendar calendarStart = Calendar.getInstance();
        calendarStart.setTime(start);
        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.setTime(end);

        while (calendarStart.before(calendarEnd)){
            dateList.add(calendarStart.getTime());
            calendarStart.add(Calendar.DATE,1);
        }

        return dateList;
    }

    public List<String> convertListDateToStringList(List<Date> dateList){
        List<String> stringList = new ArrayList<>();

        for (Date date : dateList){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            String dateString = String.valueOf(returnDayString(calendar.get(Calendar.DAY_OF_WEEK))+", "+
                    String.valueOf(calendar.get(Calendar.DATE))+" Thg "+String.valueOf(calendar.get(Calendar.MONTH)+1));
            stringList.add(dateString);
        }

        return stringList;
    }

    public String convertDateToStringFormatStandard(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return simpleDateFormat.format(date);
    }

    public List<String> convertToListDateFormatStandard(List<Date> dateList){
        List<String> list = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        for (Date date : dateList){
            list.add(simpleDateFormat.format(date));
        }
        return list;
    }

    public String convertDateToDateString(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String dateString = String.valueOf(returnDayString(calendar.get(Calendar.DAY_OF_WEEK))+", "+
                String.valueOf(calendar.get(Calendar.DATE))+" Thg "+String.valueOf(calendar.get(Calendar.MONTH)+1));
        return dateString;
    }

    public String returnDayString(int day){
        String[] str = new String[]{"Cn","Th 2","Th 3","Th 4","Th 5","Th 6","Th 7"};
        return str[day-1];
    }

    public String getItemListDateString(int pos) {
        return listDateString.get(pos);
    }

    public Date getItemListDateDate(int pos) {
        return listDateDate.get(pos);
    }

    public String getStrHours(int pos) {
        return strHours[pos];
    }

    public String getStrMinutes(int pos) {
        return strMinutes[pos];
    }

    public List<String> getListDateString() {
        return listDateString;
    }

    public String[] getStrHours() {
        return strHours;
    }

    public String[] getStrMinutes() {
        return strMinutes;
    }

    public String getItemDateFormatStandard(int pos) {
        return dateFormatStandardList.get(pos);
    }

    public List<String> getDateFormatStandardList() {
        return dateFormatStandardList;
    }

    public void setDateFormatStandardList(List<String> dateFormatStandardList) {
        this.dateFormatStandardList = dateFormatStandardList;
    }

}

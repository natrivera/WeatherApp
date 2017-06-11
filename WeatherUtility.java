//Nat Rivera
import java.io.*;
import java.net.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.*;


public class WeatherUtility
{
    InputStream local, weatherApi;
    Date date;
    String temperature = "";
    String country = "";
    String state = "";
    String latitude = "";
    String longitude = "";
    String city = "If you can see this there is a problem.";
    String zipApi, symbol, description, time;
    String tommorrow, dayAfter, dayDayAfter;
    String tempTom, tempDay, tempDayDay;
    ArrayList<String> forecast = new ArrayList();
    ArrayList<Forecast> fore = new ArrayList();
    
    //class constructor
    WeatherUtility() 
    {
        localApi();
    }
    
    public void localApi()
    {
        try
        {
            local = new URL("http://ip-api.com/xml").openConnection().getInputStream();
           
        } 
        catch (IOException ex)
        {}
    }
    
    //if int == 1, will get location using zip
    //else will get location using ip addr
    public void getLocation(int i)
    {
        if (i == 1)
        {
            try
            {
                local = new URL(zipApi).openConnection().getInputStream();
            } 
            catch (MalformedURLException ex)
            {} 
            catch (IOException ex)
            {}
        }
            // create the XMLInputFactory object
            XMLInputFactory inputFactory = 
                XMLInputFactory.newInstance();
            try
            {           
               //create xml stream reader
                XMLStreamReader reader = 
                    inputFactory.createXMLStreamReader(local);

                // Read XML here
                reader.next();
           
                while(reader.hasNext())
                {
                    //System.out.println(reader.getLocalName());
                    int eventType = reader.getEventType();
                    if(eventType == XMLStreamReader.START_ELEMENT)
                    {
                        String el = reader.getLocalName();

                        if(el.equals("city"))
                        {
                            city = reader.getElementText();
                        }

                        if(el.equals("region"))
                        {
                            state = reader.getElementText();
                        }

                        if(el.equals("country"))
                        {
                            country = reader.getElementText();
                        }
                        
                        if(el.equals("lat"))
                        {
                            latitude = reader.getElementText();
                        }
                        
                        if(el.equals("lon"))
                        {
                            longitude = reader.getElementText();
                            //System.out.println("here");
                        }
                        
                        if(el.equals("formatted_address"))
                        {
                            String[] arr = reader.getElementText().split(",");
                            state = arr[1].substring(1,3);
                        }
                    }
                    
                    reader.next();
                }
                reader.close();
            } 
            catch (XMLStreamException e)
            {}
            catch (Exception e)
            {}
            localApi();
    }
    
    public String latWeather()
    {
        getLocation(0);
        String wapi = "http://api.openweathermap.org/data/2.5/forecast?lat=" + latitude + "&lon=" + longitude + "&units=imperial&mode=xml&appid=ThisHasBeenAlyeredToHideTheKey";
        return wapi;
    }
    
    public String zipWeather(int i)
    {
        int zip= 900001;
        zip = i;
        String wapi = "http://api.openweathermap.org/data/2.5/forecast?zip="+ zip +"&units=imperial&mode=xml&appid=ThisHasBeenAlyeredToHideTheKey";
        zipApi = "http://maps.googleapis.com/maps/api/geocode/xml?address=" + zip;
        getLocation(1);
        //System.out.println(state);
        return wapi;
    }
    
    public void getWeather(String s)
    {
        //clear array that holds Forecast objects
        fore.clear();
        //clear the array that holds temperatures
        forecast.clear();
        //api to use to lookup the weather
        String wapi = s;
        //System.out.println(wapi);
        try 
        {
            weatherApi = new URL(wapi).openConnection().getInputStream();
            
             // create the XMLInputFactory object
            XMLInputFactory inputFactory = 
                XMLInputFactory.newInstance();
            
            //create xml stream reader
                XMLStreamReader reader = 
                    inputFactory.createXMLStreamReader(weatherApi);

                // Read XML here
                reader.next();
                while(reader.hasNext())
                {
                    //for each line, check the tag type and get relavat info from it
                    //System.out.println(reader.getLocalName());
                    int eventType = reader.getEventType();
                    if(eventType == XMLStreamReader.START_ELEMENT)
                    {
                        String el = reader.getLocalName();
                        
                        if(el.equals("temperature"))
                        {
                            temperature = reader.getAttributeValue(1);
                            //add temp to array
                            forecast.add(temperature);
                        }
                        if(el.equals("name"))
                        {
                            city = reader.getElementText();                            
                        } 
                        
                        if(el.equals("time"))
                        {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                            time = reader.getAttributeValue(0); //yyyy-MM-dd'T'HH:mm:ss
                            try
                            {
                                date = sdf.parse(time);
                            } catch (ParseException ex)
                            {}
                        }
                        
                        if(el.equals("symbol"))
                        {
                            symbol = reader.getAttributeValue(2);
                            description = reader.getAttributeValue(1);
                            //System.out.println(el);
                            //add the info to array
                            Forecast temp = new Forecast(temperature, symbol, description, date);
                            fore.add(temp);
                        }      
                    }
                    reader.next();
                }
        }
        catch(IOException e)
        {} 
        catch (XMLStreamException ex)
        {}
        
        if(forecast.isEmpty())
        {
            forecast.add("0");
            fore.add(new Forecast());
        }
        else
        {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            //array to store any intinces where time is new day
            ArrayList<Integer> array = new ArrayList();
            
            for(int i = 0; i < fore.size(); i++)
            {
               String now = sdf.format(fore.get(i).time);
               if (now.equals("09:00:00"))
               {
                    array.add(i);
               }
            }
            
            //getting the info for future forecast from array of forecast
            int a = array.get(0);
            int b = array.get(1);
            int c = array.get(2);
            
            //arrays to store the temps of the different days
            ArrayList<Double> day1 = new ArrayList(); 
            ArrayList<Double> day2 = new ArrayList();
            ArrayList<Double> day3 = new ArrayList();
            
            //loop through fore array and add temps to each array
            for (int i = a; i < b; i++)
            {
                Double temp = Double.parseDouble(fore.get(i).temperature);
                day1.add(temp);
            }
            
            for (int i = b; i < c; i++)
            {
                Double temp = Double.parseDouble(fore.get(i).temperature);
                day2.add(temp);
            }    
            
            for (int i = c; i < (c+8); i++)
            {
                Double temp = Double.parseDouble(fore.get(i).temperature);
                day3.add(temp);
            }
            
            //sort the new arrays
            Collections.sort(day1);
            Collections.sort(day2);
            Collections.sort(day3);
            
            //get the highest value of each array and cast to integer
            Integer one = day1.get(day1.size() -1).intValue();
            Integer two = day2.get(day1.size() -1).intValue();
            Integer three = day3.get(day1.size() -1).intValue();
            
            //prepare for display
            tempTom = one.toString();
            tempDay = two.toString();
            tempDayDay = three.toString();
            
            //System.out.println(array);
            //System.out.println(day1);
            //System.out.println(day2);
            //System.out.println(day3);
            
            //put together the data for future forecast //next three days
            tommorrow = " " + fore.get(a).time.toString().substring(0, 4) + " " + tempTom + "º  " + fore.get(a).description;
            dayAfter = " " + fore.get(b).time.toString().substring(0, 4) + " " + tempDay + "º  " + fore.get(b).description;
            dayDayAfter = " " + fore.get(c).time.toString().substring(0, 4) + " " +  tempDayDay + "º  " + fore.get(c).description;
        }
        
        //System.out.println(forecast.size());
        //System.out.println(fore.size());
        //System.out.println(fore.get(0).time);
        //System.out.println(wapi);
        
    }
    
    //returns the string that is as url to get icon from
    public String getIcon()
    {
        String s = "http://openweathermap.org/img/w/" + fore.get(0).symbol +".png";
        return s;
    }
      
    public String getLatitude()
    {
        return latitude;
    }
    
    public String getLongtitude()
    {
        return longitude;
    }
    
    public String getCity()
    {
        return city;
    }
    
    public String getState()
    {
        return state;
    }
    
    public String getCountry()
    {
        return country;
    }
}

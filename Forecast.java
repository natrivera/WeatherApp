
import java.util.Date;

public class Forecast
{
    String temperature, symbol, description;
    Date time;
    
    Forecast(){}
    Forecast(String a, String b, String c, Date d)
    {
        temperature = a;
        symbol = b;
        description = c;
        time = d;
    }
    
}

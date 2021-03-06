package org.agmip.translators.apsim.core;

import java.text.ParseException;
import java.util.Calendar;
import org.agmip.translators.apsim.events.Event;
import org.agmip.translators.apsim.events.Planting;
import org.agmip.translators.apsim.util.Util;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * @author Dean Holzworth, CSIRO
 * @author Ioannis N. Athanasiadis, DUTh
 * @since Jul 13, 2012
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SimulationRun {
    
  

	// Initial condition
    @JsonProperty("initial_conditions")
    private InitialCondition initialCondition;
    public InitialCondition getInitialCondition() { return initialCondition; }
    
    @JsonIgnore
    private Weather weather;
    public Weather getWeather() { return weather; }
    public void setWeather(Weather w) { weather = w; }
    
    @JsonProperty("wst_id")
    private String weatherID;
    public String getWeatherID(){return weatherID;}
    
    
    // management.
    private Management management;
    public Management getManagement() { return management; }
    
    @JsonIgnore
    private Soil soil;
    public Soil getSoil() { return soil; }
    public void setSoil(Soil s) { soil =s;}
    
    @JsonProperty("soil_id")
    private String soilID;
    public String getSoilID(){return soilID;}
    
    // experimentName
    @JsonProperty("exname")
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_DEFAULT)
    private String experimentName = "default";
    public String getExperimentName() { return experimentName; }
    
    // experimentName
    @JsonProperty("trt_name")
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_DEFAULT)
    private String treatmentName = "";
    public String getTreatmentName() { return treatmentName; }
    
    public String getUniqueName() {
        if ("".equals(treatmentName))
            return experimentName;
        else
            return experimentName + "-" + treatmentName;
    }
    // latitude
    @JsonProperty("fl_lat")
    private String latitude = "?";
    public String getLatitude() { return latitude; }
    
    // longitude
    @JsonProperty("fl_long")
    private String longitude = "?";
    public String getLongitude() { return longitude; }

    // log
    @JsonIgnore
    private String log;
    public String getLog() { return log; }
    
    // startDate
    public String getStartDate() {
        if (management != null) {
            for (Event event : management.getEvents()) {
                if (event instanceof Planting) {
                    return event.getDate();
                }
            }
            return "?";
        } else {
            return "?";
        }
    }

    // endDate
    public String getEndDate() throws ParseException {
        if ("?".equals(getStartDate())) 
            return "?";
        Calendar endDate = Util.toCalendar(getStartDate());
        endDate.add(Calendar.YEAR, 1);
        return Util.toApsimDateString(endDate);
    }

    // Needed for Jackson
    public SimulationRun() {}
    
    // initialise the SimulationRun instance.
    public void initialise() throws Exception {
        log = "";
        
        // Check the start and end date.
        if ("?".equals(getStartDate()))
           log += "  * Clock ERROR: Missing a simulation start date.\r\n";
        else
           log += "  * Clock ASSUMPTION: end date assumed to be one year after start date.\r\n";

        // Check the weather latitude.
        if (weather == null)
            log += "  * Met ERROR: Missing weather data.\r\n";
        else if ("?".equals(weather.getLatitude())) {
            if ("?".equals(latitude))
                log += "  * Met ERROR: No latitude found in weather data or experiment.\r\n";
            else {
                log += "  * Met ASSUMPTION: No latitude found in weather data. Using experiment latitude instead.\r\n";
                weather.setLatitude(latitude);
            }
        }
        log += "  * Met ERROR: No TAV and AMP have been specified in met file.\r\n";
        
        // Check the soil
        if (soil == null)
            log += "  * Soil: No soil found in AgMIP dataset.\r\n";
        else {
            soil.initialise();
            log += soil.getLog();
        }        
        
        if (initialCondition == null) {
            initialCondition = new InitialCondition();
            log += "  * Initial conditions ERROR: Missing initial conditions (NO3, NH4, SW, SurfaceOM.residueWeight)\r\n";
        }
        if (soil != null) {
            initialCondition.initialise(soil.getLayers());
            log += initialCondition.getLog();
        }
        
        if (management == null)
            log += "  * Operations ERROR: Missing all management events.\r\n";
        else {
            management.initialise();
            log += management.getLog();
        }
    }
    
    
    public void setInitialCondition(InitialCondition initialCondition) {
  		this.initialCondition = initialCondition;
  	}
  	public void setWeatherID(String weatherID) {
  		this.weatherID = weatherID;
  	}
  	public void setManagement(Management management) {
  		this.management = management;
  	}
  	public void setSoilID(String soilID) {
  		this.soilID = soilID;
  	}
  	public void setExperimentName(String experimentName) {
  		this.experimentName = experimentName;
  	}
  	public void setTreatmentName(String treatmentName) {
  		this.treatmentName = treatmentName;
  	}
  	public void setLatitude(String latitude) {
  		this.latitude = latitude;
  	}
  	public void setLongitude(String longitude) {
  		this.longitude = longitude;
  	}
  	public void setLog(String log) {
  		this.log = log;
  	}
}

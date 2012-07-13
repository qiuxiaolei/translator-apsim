package org.agmip.translators.apsim;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.net.URL;

import java.util.List;
import java.util.Set;
import java.util.Scanner;

import junit.framework.TestCase;

import org.agmip.translators.apsim.core.DailyWeather;
import org.agmip.translators.apsim.core.Weather;
import org.agmip.translators.apsim.util.Converter;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.junit.Test;

public class ConverterTest extends TestCase {
	Weather w = new Weather();

	public void setUp() throws Exception {
		w.averageTemperature = "11";
		w.longName = "Station Name, Somewhere in the Galaxy";
		w.shortName = "STA0001";
		w.latitude = "-11.2345";
		w.longitude = "-11.2345";

		List<DailyWeather> recs = w.records;

		{
			DailyWeather r = new DailyWeather();
			r.date = "20120101";
			r.solarRadiation = "0"; // radn
			r.maxTemperature = "13"; // maxt
			r.minTemperature = "-12"; // mint
			r.rainfall = "0.2";
			recs.add(r);
		}

		{
			DailyWeather r = new DailyWeather();
			r.date = "20120102";
			r.solarRadiation = "0.3"; // radn
			r.maxTemperature = "18"; // maxt
			r.minTemperature = "0.2"; // mint
			r.rainfall = "0.2";
			recs.add(r);
		}

	}

	@Test
	public void testGenerateWeatherFiles() {
		try {
			Converter.generateWeatherFiles(new File("src/test/resources/"), w);
		} catch (Exception e) {
			assertTrue(false);
		}
	}

	@Test
	public void testGetYear() throws Exception {
		assertEquals("2012", Converter.GetYear("20121212"));
		assertEquals("2000", Converter.GetYear("20000921"));
	}

	@Test
	public void testGetDay() throws Exception{
		assertEquals("1", Converter.GetDay("20120101"));
		assertEquals("32", Converter.GetDay("20120201"));
	}
	
	
	@Test
	public void testConvertDayte() throws Exception{
		assertEquals("01/01/2012", Converter.convertDate("20120101"));
		assertEquals("19/05/2011", Converter.convertDate("20110519"));
	}
	
	

	@Test
	public void testJSON() {

		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
		try {
			PrintWriter out = new PrintWriter(new FileWriter(new File(
					"src/test/resources/test01.json")));
			out.print(mapper.writeValueAsString(w));
			out.close();

		} catch (Exception e) {
			assertTrue(false);
		}

	}
	
	@Test
	public void testJSONReader()  throws Exception{

		ObjectMapper mapper = new ObjectMapper();
		Weather value = mapper.readValue(new File("src/test/resources/test02.json"), Weather.class);
			assertEquals("-11.2345",value.latitude);


	}



}
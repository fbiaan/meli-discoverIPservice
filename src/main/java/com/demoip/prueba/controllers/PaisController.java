package com.demoip.prueba.controllers;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demoip.prueba.models.CountryService;
import com.demoip.prueba.models.DiscoverIpTO;
import com.demoip.prueba.models.Ipapicom;
import com.demoip.prueba.models.IppaisTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.util.EntityUtils;
//import com.demoip.prueba.services.PaisServices;


@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/util")
public class PaisController {

	

	   
	   @GetMapping("/hola")
	    public String Saludo() {
	        return "HOLA MELI - challenge ip";
	      }
	   
	   @GetMapping("/procesaip/{dirip}")
	    public DiscoverIpTO Procesaip(@PathVariable (value="dirip") String dirip) throws Exception {
		try {  			
			ObjectMapper mapper = null;
			mapper = new ObjectMapper();

			String Url1 = "https://api.ip2country.info/ip?" + dirip;
			
			String res = Request.Get(Url1)
					.execute().returnContent().asString();
			
			//control
					
			
			IppaisTO ippais =  mapper.readValue(res, IppaisTO.class );
			System.out.println("resultado:");
			System.out.println(ippais);
			 
			// api restcountries
			ObjectMapper mappercoun = null;
			mappercoun = new ObjectMapper();
			//CountryService conser = new CountryService();
			String prefijoPais = ippais.getCountryCode3();
			String Url2 = "https://restcountries.com/v3.1/alpha/" + prefijoPais;
			String res2 = Request.Get(Url2)
						.execute()
						.returnContent()
						.asString();
			List<Map<String, Object>> lstCountry = mappercoun.readValue(res2, new TypeReference<List<Map<String, Object>>>(){});
			String idiomas = lstCountry.get(0).get("languages").toString();
			String usoHora = lstCountry.get(0).get("timezones").toString();
			// api ip-api.com
			ObjectMapper mapperip = null;
			mapperip = new ObjectMapper();

			String Url3 = "http://ip-api.com/json/" + dirip;
			
			String res3 = Request.Get(Url3)
					.execute().returnContent().asString();

			Ipapicom ipapi =  mapperip.readValue(res3, Ipapicom.class );

			System.out.println("api ip json:");
			System.out.println(ipapi);

			
			// fin ip-api.com
			
			// armo respuesta 
			DiscoverIpTO discoverip = new DiscoverIpTO();
			discoverip.setIpSearch(dirip);
			String timeStamp = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime());
			discoverip.setFecha(timeStamp);
			discoverip.setPais(ipapi.getCountry());
			discoverip.setIsoCode(ipapi.getCountryCode());
			discoverip.setIdiomas(idiomas);
			//hora estimada
			ObjectMapper mapperHo = null;
			mapperHo = new ObjectMapper();
			//List<String> lstHora = mapperHo.readValue(usoHora, new TypeReference<List<String>>(){});
			
			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat fmt = new SimpleDateFormat("HH:mm:ss");
			// armo utc a gtm
			String timeZone = "GMT-" + usoHora.substring(5, 10);
		    fmt.setTimeZone(TimeZone.getTimeZone(timeZone));
		    System.out.println("hora : ");
		    System.out.println(fmt.format(calendar.getTime()));
			discoverip.setUsoHorario(fmt.format(calendar.getTime()) + " - (" + usoHora.toString() + ")");
			//distancia estimada
			double lat1= Double.valueOf(ipapi.getLat()); 
			double lng1 = Double.valueOf(ipapi.getLon());
			double lat2 = -34.58;
			double lng2 = -58.67;
			double diskm = distanciaCoord(lat1, lng1, lat2, lng2 );
			long dislng =  (long) diskm;
			discoverip.setDistanciaBsAs(dislng);
			discoverip.setMoneda("No desarrollado.");
			
			return discoverip;
			}
		catch (IOException e ) { }
		DiscoverIpTO discoveripErr = new DiscoverIpTO();
			discoveripErr.setPais("Error ip mal ingresada o no valida.");
			return discoveripErr;
	    }
	   
	    public double distanciaCoord(double lat1, double lng1, double lat2, double lng2) {  
	        //double radioTierra = 3958.75;//en millas  
	        double radioTierra = 6371;//en kilómetros  
	        double dLat = Math.toRadians(lat2 - lat1);  
	        double dLng = Math.toRadians(lng2 - lng1);  
	        double sindLat = Math.sin(dLat / 2);  
	        double sindLng = Math.sin(dLng / 2);  
	        double va1 = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)  
	                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));  
	        double va2 = 2 * Math.atan2(Math.sqrt(va1), Math.sqrt(1 - va1));  
	        double distancia = radioTierra * va2;  
	   
	        return distancia;  
	    }  
	   
}

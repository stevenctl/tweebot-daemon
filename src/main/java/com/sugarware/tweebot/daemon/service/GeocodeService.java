package com.sugarware.tweebot.daemon.service;

import java.math.BigDecimal;

import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GeocodeService {

	public String getGeocode(int zip, int radius) throws Exception {
		RestTemplate restTemplate = new RestTemplate();

		if (("" + zip).length() != 5) {
			throw new IllegalArgumentException("Zip code must be 5 digits");
		}
		ResponseEntity<String> response = restTemplate
				.getForEntity("http://maps.googleapis.com/maps/api/geocode/json?address=" + zip, String.class);

		if (!response.getStatusCode().is2xxSuccessful()) {
			throw new Exception("Failed to get coordinates for zip");
		}
		JSONObject geo = new JSONObject(response.getBody()).getJSONArray("results").getJSONObject(0)
				.getJSONObject("geometry").getJSONObject("location");

		double lng = geo.getDouble("lng");
		double lat = geo.getDouble("lat");

		return round(lat, 5) + "," + round(lng, 5) + "," + radius + "mi";
	}

	public static double round(double value, int numberOfDigitsAfterDecimalPoint) {
		BigDecimal bigDecimal = new BigDecimal(value);
		bigDecimal = bigDecimal.setScale(numberOfDigitsAfterDecimalPoint, BigDecimal.ROUND_HALF_UP);
		return bigDecimal.doubleValue();
	}

}

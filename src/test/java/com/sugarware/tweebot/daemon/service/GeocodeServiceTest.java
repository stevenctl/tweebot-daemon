package com.sugarware.tweebot.daemon.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.fail;

import org.junit.Test;

public class GeocodeServiceTest {

	@Test
	public void testRound() {
		double num = 38.78468000000001d;
		assertThat(GeocodeService.round(num, 5), equalTo(38.78468));
	}

	@Test
	public void getGeocode() {
		GeocodeService service = new GeocodeService();
		try {
			String geocode = service.getGeocode(63368, 1);

			assertThat(geocode, equalTo("38.74505,-90.72477,1mi"));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

}

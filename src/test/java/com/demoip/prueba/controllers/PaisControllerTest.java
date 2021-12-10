package com.demoip.prueba.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;

import com.demoip.prueba.models.DiscoverIpTO;

@ExtendWith(SpringExtension.class)
@WebMvcTest(PaisController.class)
class PaisControllerTest {

    @Autowired
    private MockMvc mvc;
	
	@Test
	void testSaludo()  throws Exception {
        RequestBuilder request = get("/util/hola");
        MvcResult result = mvc.perform(request).andReturn();
        assertEquals("HOLA MELI - challenge ip", result.getResponse().getContentAsString());
    }

	@Test
	void testProcesaip() throws Exception {
		DiscoverIpTO discoverIp = new DiscoverIpTO();
		discoverIp.setIpSearch("183.44.196.93");
		discoverIp.setIsoCode("CN");
        RequestBuilder request2 = get("/util/procesaip/183.44.196.93");
        MvcResult result2 = mvc.perform(request2).andReturn();
        //assertEquals(discoverIp, result2.getResponse().getContentAsString());
        MockHttpServletResponse response = result2.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
	}


}

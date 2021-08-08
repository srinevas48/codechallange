package com.aig.dcp.nextgen.core.models.impl;


import java.io.IOException;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aig.dcp.nextgen.core.pojo.ProvincePojo;
import com.google.gson.Gson; 

@Model(adaptables = { Resource.class })
public class ProductInfoModel {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductInfoModel.class);

	@SlingObject
	private Resource resource;

	ProvincePojo[] provinceList2;
	
	String endPointUrl = "https://fakestoreapi.com/products?limit=5";

	public ProvincePojo[] getProvinceList2() {
		return provinceList2;
	}

	@PostConstruct
	protected void init() {
		
		try (final CloseableHttpClient client = HttpClients.createDefault()) {

			final HttpRequestBase request = new HttpGet(endPointUrl);
			CloseableHttpResponse response = client.execute(request);
			final HttpEntity entity = response.getEntity();
			String resp = EntityUtils.toString(entity, "UTF-8");
			LOGGER.info("{}", resp);
			if (StringUtils.isNotBlank(resp)) {
				Gson respJson = new Gson();
				provinceList2 = respJson.fromJson(resp, ProvincePojo[].class);
			}
			
		} catch (final IOException exception) {
			LOGGER.error("IOException exception occured in Covid19DataFetchService...{}", exception);
		} catch (final Exception e) {
			LOGGER.error("Exception occured in makeGetWSCall method of Covid19DataFetchService: {}", e);
		}	
	}
}
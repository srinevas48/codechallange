package com.codechallenge.core.services;

import java.io.IOException;
import java.net.SocketTimeoutException;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codechallenge.core.pojos.ProvincePojo;
import com.day.cq.commons.jcr.JcrConstants;
import com.google.gson.Gson;

@Component(service = Covid19DataFetchService.class, immediate = true, property = {
		"process.label= Covid 19 Data Fecth Service" })
public class Covid19DataFetchService {
	private static final Logger log = LoggerFactory.getLogger(Covid19DataFetchService.class);
	@Reference
	GetResourceResolver getResourceResolver;

	public boolean fetchAndStoreCovid19Data(String nodePath, String endPointUrl) {
		try (final CloseableHttpClient client = HttpClients.createDefault()) {
			ResourceResolver resourceResolver = getResourceResolver.getResourceResolver();
			ProvincePojo[] provinceList;
			final HttpRequestBase request = new HttpGet(endPointUrl);
			CloseableHttpResponse response = client.execute(request);
			final HttpEntity entity = response.getEntity();
			String resp = EntityUtils.toString(entity, "UTF-8");
			log.info("{}", resp);
			if (StringUtils.isNotBlank(resp)) {
				Gson respJson = new Gson();
				provinceList = respJson.fromJson(resp, ProvincePojo[].class);
				if (provinceList.length > 0) {
					Session session = resourceResolver.adaptTo(Session.class);
					if (null != session) {
						if (session.nodeExists(nodePath)) {
							session.removeItem(nodePath);
						}
						final Node rootNode = session.getRootNode();
						final Node covidNode = rootNode.addNode(StringUtils.substring(nodePath, 1),
								JcrConstants.NT_UNSTRUCTURED);
						if (null != covidNode) {
							processProvinces(covidNode, provinceList);
						}
						session.save();
						return true;
					}
				}
			}
		} catch (final SocketTimeoutException exception) {
			log.error("SocketTimeoutException exception occured in Covid19DataFetchService...{}", exception);
		} catch (final IOException exception) {
			log.error("IOException exception occured in Covid19DataFetchService...{}", exception);
		} catch (final Exception e) {
			log.error("Exception occured in makeGetWSCall method of Covid19DataFetchService: {}", e);
		}
		return false;
	}

	private void processProvinces(Node covidNode, ProvincePojo[] provinceList) throws RepositoryException {
		for (ProvincePojo provincePojo : provinceList) {
			String country = provincePojo.getCountry().toLowerCase();
			if (StringUtils.isNotBlank(country)) {
				Node countryNode = covidNode.hasNode(country) ? covidNode.getNode(country) : covidNode.addNode(country);
				if (null != countryNode) {
					String province = provincePojo.getProvince().toLowerCase();
					Node provinceNode = countryNode.hasNode(province) ? countryNode.getNode(province)
							: countryNode.addNode(province);
					if (null != provinceNode) {
						provinceNode.setProperty("id", provincePojo.getID());
						provinceNode.setProperty("country", provincePojo.getCountry());
						provinceNode.setProperty("countryCode", provincePojo.getCountryCode());
						provinceNode.setProperty("province", provincePojo.getProvince());
						provinceNode.setProperty("city", provincePojo.getCity());
						provinceNode.setProperty("cityCode", provincePojo.getCityCode());
						provinceNode.setProperty("lat", provincePojo.getLat());
						provinceNode.setProperty("lon", provincePojo.getLon());
						provinceNode.setProperty("confirmed", provincePojo.getConfirmed());
						provinceNode.setProperty("deaths", provincePojo.getDeaths());
						provinceNode.setProperty("recovered", provincePojo.getRecovered());
						provinceNode.setProperty("active", provincePojo.getActive());
						provinceNode.setProperty("date", provincePojo.getDate());
					}
				}
			}
		}
	}
}

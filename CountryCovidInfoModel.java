package com.codechallenge.core.models;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codechallenge.core.pojos.ProvincePojo;

@Model(adaptables = { Resource.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CountryCovidInfoModel {
	private static final Logger LOGGER = LoggerFactory.getLogger(CountryCovidInfoModel.class);

	@SlingObject
	private Resource resource;

	@Inject
	@Optional
	private String countryDataPath;

	private List<ProvincePojo> provinceList = new ArrayList<>();

	@PostConstruct
	protected void init() {
		try {
			if (null != resource && StringUtils.isNotBlank(countryDataPath)) {
				Resource countryNodeResource = resource.getResourceResolver().getResource(countryDataPath);
				if (null != countryNodeResource) {
					Node countryNode = countryNodeResource.adaptTo(Node.class);
					NodeIterator provinces = countryNode.getNodes();
					while (provinces.hasNext()) {
						Node provinceNode = provinces.nextNode();
						ProvincePojo province = new ProvincePojo();
						province.setID(provinceNode.getProperty("id").getValue().getString());
						province.setCountry(provinceNode.getProperty("country").getValue().toString());
						province.setCountryCode(provinceNode.getProperty("countryCode").getValue().toString());
						province.setProvince(provinceNode.getProperty("province").getValue().toString());
						province.setCity(provinceNode.getProperty("city").getValue().toString());
						province.setCityCode(provinceNode.getProperty("cityCode").getValue().toString());
						province.setLat(provinceNode.getProperty("lat").getValue().toString());
						province.setLon(provinceNode.getProperty("lon").getValue().toString());
						province.setConfirmed(Integer.parseInt(provinceNode.getProperty("confirmed").getValue().toString()));
						province.setRecovered(Integer.parseInt(provinceNode.getProperty("recovered").getValue().toString()));
						province.setDeaths(Integer.parseInt(provinceNode.getProperty("deaths").getValue().toString()));
						province.setActive(Integer.parseInt(provinceNode.getProperty("active").getValue().toString()));
						province.setDate(provinceNode.getProperty("date").toString());
						provinceList.add(province);
					}
				}
			}
		} catch (RepositoryException e) {
			LOGGER.error("Repository Exception Occurred:: {}", e.getMessage());
		}
	}

	public List<ProvincePojo> getProvinceList() {
		return provinceList;
	}

	public void setProvinceList(List<ProvincePojo> provinceList) {
		this.provinceList = provinceList;
	}
}

package com.aig.dcp.nextgen.core.pojo;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProvincePojo {

	@SerializedName("id")
	@Expose
	private String id;
	@SerializedName("title")
	@Expose
	private String title;
	
	@SerializedName("price")
	@Expose
	private String price;
	@SerializedName("description")
	@Expose
	private String description;
	@SerializedName("category")
	@Expose
	private String category;
	@SerializedName("image")
	@Expose
	private String image;
	
	public String getid() {
		return id;
	}
	public String getTitle() {
		return title;
	}
	public String getPrice() {
		return price;
	}
	public String getDescription() {
		return description;
	}
	public String getCategory() {
		return category;
	}
	public String getImage() {
		return image;
	}
	
}

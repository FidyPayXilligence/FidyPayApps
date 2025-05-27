package com.fidypay.request;

import javax.validation.constraints.NotBlank;

public class ImageURLs {

	@NotBlank(message = "imageUrl1 can not be blank")
	private String imageUrl1;

	@NotBlank(message = "imageUrl2 can not be blank")
	private String imageUrl2;

	public String getImageUrl1() {
		return imageUrl1;
	}

	public String getImageUrl2() {
		return imageUrl2;
	}

}

package com.uni.viss;

import java.util.ArrayList;

public class ResponseDataInJson {
	private String base64Img;
	private byte[] clickedImage;
	private ArrayList<BookInfo> booksResult;
	private ArrayList<String> keywords;
	private String success;
	private String error;

	public String getBase64Img() {
		return base64Img;
	}

	public void setBase64Img(String base64Img) {
		this.base64Img = base64Img;
	}
	
	public ArrayList<BookInfo> getBooksResult() {
		return booksResult;
	}

	public void setBooksResult(ArrayList<BookInfo> booksResult) {
		this.booksResult = booksResult;
	}
	
	public String getSuccess() {
		return success;
	}

	public void setSuccess(String success) {
		this.success = success;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public byte[] getClickedImage() {
		return clickedImage;
	}

	public void setClickedImage(byte[] clickedImage) {
		this.clickedImage = clickedImage;
	}

	public ArrayList<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(ArrayList<String> keywords) {
		this.keywords = keywords;
	}	
}

package com.uni.viss;

public class BookInfo {
	private String name;
	private String author;
	private String desc;
	private String isbn;
	private String associatedKeywords;
	private int coverId = 1;
	private String openLibId;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getAssociatedKeywords() {
		return associatedKeywords;
	}
	public void setAssociatedKeywords(String associatedKeywords) {
		this.associatedKeywords = associatedKeywords;
	}
	public String getIsbn() {
		return isbn;
	}
	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}
	public int getCoverId() {
		return coverId;
	}
	public void setCoverId(int coverId) {
		this.coverId = coverId;
	}
	public String getOpenLibId() {
		return openLibId;
	}
	public void setOpenLibId(String openLibId) {
		this.openLibId = openLibId;
	}
	
	
}

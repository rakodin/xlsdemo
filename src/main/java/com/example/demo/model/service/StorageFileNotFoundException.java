package com.example.demo.model.service;

public class StorageFileNotFoundException extends StorageException {
	public StorageFileNotFoundException(String s) {
		super(s);
	}

	public StorageFileNotFoundException(String s, Throwable t) {
		super(s,t);
	}
}

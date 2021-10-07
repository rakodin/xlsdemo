package com.example.demo.model.bean;

import lombok.Getter;

@Getter
public enum OperationType {

	INBOUND("приход"),
	OUTBOUND("расход");

	private final String description;

	OperationType(String description) {
		this.description = description;
	}
}

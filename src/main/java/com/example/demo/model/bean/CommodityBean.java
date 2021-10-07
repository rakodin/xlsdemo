package com.example.demo.model.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class CommodityBean {
	private Long id;
	private String name;
	private String nomenclatureId;
	private Integer count;
	private Long docHeaderId;
}

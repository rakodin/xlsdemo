package com.example.demo.model.bean;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class DocHeaderBean {
	private Long id;
	private OperationType operationType;
	private String documentNumber;
	private LocalDate documentDate;

	private List<CommodityBean> commodityList;
}

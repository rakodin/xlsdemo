package com.example.demo.model.service;

import com.example.demo.model.bean.CommodityBean;
import com.example.demo.model.entity.CommodityEntity;
import com.example.demo.model.repository.CommodityRepository;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@Slf4j
@Getter
public class CommodityService {

	private final CommodityRepository repository;

	@Autowired
	public CommodityService(CommodityRepository repository) {
		this.repository = repository;
	}

	public CommodityBean insert(CommodityBean bean) {
		CommodityEntity entity = convertToEntity(bean);
		entity = repository.save(entity);
		return convertToBean(entity);
	}

	public List<CommodityBean> getAll() {
		Iterable<CommodityEntity> all = repository.findAll();
		List<CommodityBean> ret = new ArrayList<>();
		all.forEach(e-> {
			CommodityBean item = convertToBean(e);
			ret.add(item);
		});
		return ret;
	}

	public List<CommodityBean> insertAll(List<CommodityBean> all) {
		List<CommodityBean> ret = new ArrayList<>();
		all.forEach(b-> ret.add(insert(b)));
		return ret;
	}

	public void remove(Long commodityId) {
		repository.deleteById(commodityId);
	}

	public CommodityBean getById(Long commodityId) {
		CommodityEntity entity = repository.findById(commodityId).orElse(null);
		return entity != null? convertToBean(entity): null;
	}

	public static CommodityEntity convertToEntity(CommodityBean bean) {
		CommodityEntity ret  = new CommodityEntity();
		ret.setId(bean.getId());
		ret.setName(bean.getName());
		ret.setNomenclatureId(bean.getNomenclatureId());
		ret.setCount(bean.getCount());
		return ret;
	}

	public static CommodityBean convertToBean(CommodityEntity entity) {
		CommodityBean ret  = new CommodityBean();
		ret.setId(entity.getId());
		ret.setName(entity.getName());
		ret.setNomenclatureId(entity.getNomenclatureId());
		ret.setCount(entity.getCount());
		if (entity.getDocHeader() != null) {
			ret.setDocHeaderId(entity.getDocHeader().getId());
		}
		return ret;
	}

	public List<CommodityBean> getAllByDocumentId(Long documentId) {
		List<CommodityEntity> list = repository.findByDocumentId(documentId);
		List<CommodityBean> ret = new ArrayList<>();
		list.forEach(e -> ret.add(convertToBean(e)));
		return ret;
	}
}

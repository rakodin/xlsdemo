package com.example.demo.model.service;

import com.example.demo.model.bean.CommodityBean;
import com.example.demo.model.bean.DocHeaderBean;
import com.example.demo.model.entity.CommodityEntity;
import com.example.demo.model.entity.DocHeaderEntity;
import com.example.demo.model.repository.DocHeaderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DocHeaderService {
	private final DocHeaderRepository docHeaderRepository;
	private final ApplicationContext context;
	@Autowired
	public DocHeaderService(DocHeaderRepository docHeaderRepository, ApplicationContext ctx) {
		this.docHeaderRepository = docHeaderRepository;
		this.context = ctx;
	}

	public List<DocHeaderBean> getAllDocuments() {
		Iterable<DocHeaderEntity> entities = docHeaderRepository.findAll();
		List<DocHeaderBean> ret = new ArrayList<>();
		CommodityService commodityService = context.getBean(CommodityService.class);
		entities.forEach(e -> {
			DocHeaderBean dh = convertToBean(e);
			dh.setCommodityList(commodityService.getAllByDocumentId(e.getId()));
			ret.add(dh);
		});
		return ret;
	}

	public DocHeaderBean save(DocHeaderBean bean) {
		DocHeaderEntity result = docHeaderRepository.save(convertToEntity(bean));
		CommodityService commodityService = context.getBean(CommodityService.class);
		bean.getCommodityList().forEach(cb -> {
			CommodityEntity ce = CommodityService.convertToEntity(cb);
			ce.setDocHeader(result);
			ce = commodityService.getRepository().save(ce);
			cb.setDocHeaderId(result.getId());
			cb.setId(ce.getId());
		});
		DocHeaderBean ret = convertToBean(result);
		ret.setCommodityList(bean.getCommodityList());
		return ret;
	}

	public void cleanAll() {
		CommodityService commodityService = context.getBean(CommodityService.class);
		commodityService.getRepository().deleteAll();
		docHeaderRepository.deleteAll();
	}

	private static DocHeaderBean convertToBean(DocHeaderEntity entity) {
		DocHeaderBean ret = new DocHeaderBean();
		ret.setId(entity.getId());
		ret.setOperationType(entity.getOperationType());
		ret.setDocumentDate(entity.getDocumentDate());
		ret.setDocumentNumber(entity.getDocumentNumber());
		return ret;
	}

	private static DocHeaderEntity convertToEntity(DocHeaderBean bean) {
		DocHeaderEntity ret = new DocHeaderEntity();
		ret.setId(bean.getId());
		ret.setOperationType(bean.getOperationType());
		ret.setDocumentDate(bean.getDocumentDate());
		ret.setDocumentNumber(bean.getDocumentNumber());
		return ret;
	}
}

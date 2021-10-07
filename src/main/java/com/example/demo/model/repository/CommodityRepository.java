package com.example.demo.model.repository;

import com.example.demo.model.entity.CommodityEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommodityRepository extends CrudRepository<CommodityEntity, Long> {
	@Query("select c from CommodityEntity c where c.docHeader.id = :documentId")
	List<CommodityEntity> findByDocumentId(@Param("documentId") Long documentId);
}

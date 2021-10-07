package com.example.demo.model.repository;

import com.example.demo.model.entity.CommodityEntity;
import com.example.demo.model.entity.DocHeaderEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocHeaderRepository extends CrudRepository<DocHeaderEntity, Long> {

}

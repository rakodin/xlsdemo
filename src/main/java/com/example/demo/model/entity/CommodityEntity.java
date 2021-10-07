package com.example.demo.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "commodity")
@NoArgsConstructor
@Getter
@Setter
public class CommodityEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name")
	private String name;

	@Column(name = "nomenclature_id")
	private String nomenclatureId;

	@Column(name = "count")
	private Integer count;

	@JoinColumn(name = "document_header_id")
	@ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE})
	private DocHeaderEntity docHeader;
}

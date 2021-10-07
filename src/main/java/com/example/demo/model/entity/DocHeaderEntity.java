package com.example.demo.model.entity;

import com.example.demo.model.bean.OperationType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "document_header")
@NoArgsConstructor
@Getter
@Setter
public class DocHeaderEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name="doc_num", unique = true, nullable = false)
	private String documentNumber;

	@Column(name = "operation_type", nullable = false)
	@Enumerated(EnumType.STRING)
	private OperationType operationType;


	@Column(name = "doc_date")
	private LocalDate  documentDate;
}

package com.example.demo.model.service;

import com.example.demo.model.bean.CommodityBean;
import com.example.demo.model.bean.DocHeaderBean;
import com.example.demo.model.bean.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
public class XLSParserService {
	private final DocHeaderService docHeaderService;

	private static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	public XLSParserService(DocHeaderService docHeaderService) {
		this.docHeaderService = docHeaderService;
	}

	public void cleanAll() {
		docHeaderService.cleanAll();
	}

	public List<DocHeaderBean> getAllDocuments() {
		return docHeaderService.getAllDocuments();
	}

	/*
	xlsx template
	row0 col1: operation
	row1 col2: document number
	row2 col2: document date
	row3: skip (header)
	row4 col0..col2: code || count || name
	row5....
	*/
	public String parseXLS(File file) {
		FileInputStream fis = null;
		XSSFWorkbook workbook = null;
		List<CommodityBean> res = new ArrayList<>();
		AtomicReference<DocHeaderBean> dhRef = new AtomicReference<>(new DocHeaderBean());
		try {
			fis = openStream(file);
			workbook = new XSSFWorkbook(fis);
			XSSFSheet sheet = workbook.getSheetAt(0);
			sheet.rowIterator().forEachRemaining(row -> {
				try {
					//parse header
					DocHeaderBean dh = dhRef.get();
					if (row.getRowNum() == 0) {
						dh.setOperationType(parseOperationType(row.getCell(1)));
					} else if (row.getRowNum() == 1) {
						dh.setDocumentNumber(row.getCell(1).getStringCellValue());
					} else if (row.getRowNum() == 2) {
						dh.setDocumentDate(parseLocalDate(row.getCell(1)));
					} else if (row.getRowNum() > 3) {
						//parse data
						if (row.getFirstCellNum() >= 0) {
							CommodityBean cb = new CommodityBean();
							cb.setNomenclatureId(row.getCell(0).getStringCellValue());
							cb.setCount(parseInteger(row.getCell(1)));
							cb.setName(row.getCell(2).getStringCellValue());
							res.add(cb);
						}
					}
				} catch (IOException e) {
					throw new RuntimeException("Parse error on row " + row.getRowNum(), e);
				}
			});
			DocHeaderBean dh = dhRef.get();
			dh.setCommodityList(res);
			docHeaderService.save(dh);
			return "Загружено " + (res.size()) + " записей из файла.";
		} catch (IOException | RuntimeException e) {
			log.error("XLSX Load error:", e);
			return "Не могу загрузить файл: "  + e.getMessage();
		} finally {
			closeWorkbook(fis, workbook);
		}
	}

	private FileInputStream openStream(File file) throws IOException {
		return new FileInputStream(file);
	}

	private void closeWorkbook(FileInputStream stream, Workbook workbook) {
		if (stream == null) {
			return;
		}
		try {
			workbook.close();
			stream.close();
		} catch (IOException e) {
			log.error("Can't close input stream or workbook", e);
		}
	}

	private static OperationType parseOperationType(Cell cell) throws IOException {
		String operationStr = cell.getStringCellValue();
		OperationType found = Arrays.stream(OperationType.values())
				.filter(ot -> ot.getDescription().equals(operationStr.trim().toLowerCase()))
				.findFirst().orElse(null);
		if (found != null) {
			return found;
		}
		log.error("Invalid operation type description: '" + operationStr + "'");
		throw new IOException("Invalid operation type description: '" + operationStr + "'");
	}

	private static LocalDate parseLocalDate(Cell cell) throws IOException {
		Date input;
		if (cell.getCellType().equals(CellType.STRING)) {
			return LocalDate.parse(cell.getStringCellValue(), fmt);
		} else if (cell.getCellType().equals(CellType.NUMERIC)) {
			input = cell.getDateCellValue();
			return LocalDate.ofInstant(input.toInstant(), ZoneId.systemDefault());
		}
		log.error("Invalid document date field type : '" + cell.getCellType() + "'");
		throw new IOException("Invalid document date field type : '" + cell.getCellType() + "'");
	}

	private Integer parseInteger(Cell cell) throws IOException {
		if (CellType.NUMERIC.equals(cell.getCellType())) {
			return Double.valueOf(cell.getNumericCellValue()).intValue();
		} else if (CellType.STRING.equals(cell.getCellType())) {
			return Integer.parseInt(cell.getStringCellValue());
		}
		log.error("Invalid document date field type : '" + cell.getCellType() + "'");
		throw new IOException("Invalid numeric field field type : '" + cell.getCellType() + "'");
	}
}

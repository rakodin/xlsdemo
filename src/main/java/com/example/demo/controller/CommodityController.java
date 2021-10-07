package com.example.demo.controller;

import com.example.demo.model.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Path;
import java.util.stream.Collectors;

@Controller
@Slf4j
public class CommodityController {
	private final StorageService storageService;
	private final XLSParserService xlsParserService;

	public CommodityController(StorageService storageService, XLSParserService xlsParserService) {
		this.xlsParserService = xlsParserService;
		this.storageService = storageService;
	}

	@GetMapping("/")
	public String listUploadedFiles(Model model) {
		model.addAttribute("files", storageService.loadAll().map(
						path -> MvcUriComponentsBuilder.fromMethodName(CommodityController.class,
								"serveFile", path.getFileName().toString())
								.build().toUri().toString())
				.collect(Collectors.toList()));
		model.addAttribute("documents", xlsParserService.getAllDocuments());
		return "uploadForm";
	}

	@RequestMapping("/clean")
	public String cleanDirectory() {
		xlsParserService.cleanAll();
		storageService.deleteAll();
		storageService.init();
		return "redirect:/";
	}



	@GetMapping("/files/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@PathVariable  String filename) {
		Resource file = storageService.loadAsResource(filename);
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
				"attachment; filename=\"" + file.getFilename() + "\"").body(file);
	}

	@PostMapping("/")
	public String handleFileUpload(@RequestParam("file") MultipartFile file,
								   RedirectAttributes redirectAttributes) {
		Path resFile = storageService.store(file);
		//TODO: parse XLS file
		String result = xlsParserService.parseXLS(storageService.loadAsFile(resFile));
		redirectAttributes.addFlashAttribute("message",
				"Файл " + file.getOriginalFilename() + " успешно загружен. " + result);

		return "redirect:/";
	}

	@ExceptionHandler({StorageFileNotFoundException.class, StorageException.class})
	public ResponseEntity<?> handleStorageFileNotFound(RuntimeException e) {
		log.error(e.getMessage(), e);
		return ResponseEntity.notFound().build();
	}
}

package com.expose.web;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.StringJoiner;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.expose.ApplicationException;
import com.expose.model.ImageInfo;
import com.expose.service.GalleryService;

@Controller
@RequestMapping("/gallery")
public class GalleryController {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	GalleryService galleryService;

	@GetMapping("/upload")
	String getUploadPage() {
		return "upload";
	}

	@GetMapping
	String getGalaPage(Model model,  RedirectAttributes redirectAttributes) {
		try {
			List<ImageInfo> infos = galleryService.getImageInfos();
			model.addAttribute("infos",infos);
		} catch (ApplicationException e) {
			log.warn("Error retrieving gallery image ", e);
			redirectAttributes.addFlashAttribute("error", "Error saving image");
			return  "redirect:/index";
		}
		return "gallery";
	}

	@GetMapping("/gallery/info/{imageId}")
	@ResponseBody
	String getImage(@PathVariable Long imageId) {
		try {
			ImageInfo imageInfo = galleryService.getImage(imageId);
			byte[] arr=  galleryService.getImage(imageInfo);
			return Base64.getEncoder().encodeToString(arr);
		} catch (ApplicationException e) {
			log.warn("Error reading gallery image ", e);
		}
		return "";
	}
	
	@PostMapping("/upload")
	String multiFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
		if (file.isEmpty()) {
			redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
			return "redirect:/gallery/upload";
		}

		// Get the file and save it somewhere
		try {
			byte[] bytes = file.getBytes();
			galleryService.storeImage(bytes, file.getOriginalFilename());
		} catch (IOException | ApplicationException e) {
			log.warn("Error saving image ", e);
			redirectAttributes.addFlashAttribute("error", "Error saving image");
			return "redirect:/gallery/upload";
		}

		redirectAttributes.addFlashAttribute("message",
				"You successfully uploaded '" + file.getOriginalFilename() + "'");

		return "redirect:/gallery/upload";
	}

}

package com.jeojck.imgtohtml.controller;

import com.jeojck.imgtohtml.service.ConverterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
public class ImgToHtmlController {

    private static final Logger log = LoggerFactory.getLogger(ImgToHtmlController.class);
    private final ConverterService converterService;

    public ImgToHtmlController(ConverterService converterService) {
        this.converterService = converterService;
    }

    @GetMapping(value = "/")
    public String index(Model model) {
        log.info("Loading homepage");
        return "index";
    }

    @PostMapping(value = "/convert")
    public String convert(@RequestParam("file")MultipartFile file, RedirectAttributes redirectAttributes, Model model) {
        if(file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return "redirect:/";
        }

        String contentType = file.getContentType();

        if(!("image/png".equals(contentType) || "image/jpeg".equals(contentType))) {
            redirectAttributes.addFlashAttribute("message", "Please upload an image file (PNG/JPG)");
            return "redirect:/";
        }

        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        log.info("The filename is {}", filename );
        log.info("File size: {}", file.getSize());

        try {
            String table = converterService.imgToHtml(file, model);
        } catch (IOException e) {
            log.error("Exception thrown: {}", e.toString() );
            redirectAttributes.addFlashAttribute("message", "There was an error converting your file");
            return "redirect:/";
        }

        return "convert";
    }
}

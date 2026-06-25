package spring_ocr.pdfscanner.Controllers;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import net.sourceforge.tess4j.TesseractException;
import spring_ocr.pdfscanner.Services.OCRService;

@RestController
@RequiredArgsConstructor
public class OCRController {
    
    private final OCRService ocrService;
    private final Logger log = LoggerFactory.getLogger(OCRController.class);

    @PostMapping(value="/getOCR", consumes = "multipart/form-data", produces = "text/plain")
    public ResponseEntity<String> processImage(@RequestParam("image") MultipartFile image) throws IOException, TesseractException { 
        log.info("processing the image");
        return ResponseEntity.status(HttpStatus.OK).body(ocrService.processImage(image));
    }
}

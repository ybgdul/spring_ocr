package spring_ocr.pdfscanner.Utilities.Exceptions;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import net.sourceforge.tess4j.TesseractException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> globalHandler(Exception e) { 
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<?> ioHandler(IOException e) { 
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or unreadable file");
    }

    @ExceptionHandler(TesseractException.class)
    public ResponseEntity<?> tesseractHandler(TesseractException e) { 
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body("OCR processing failed");
    } 
}

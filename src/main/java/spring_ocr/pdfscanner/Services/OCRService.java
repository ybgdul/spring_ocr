package spring_ocr.pdfscanner.Services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.imageio.ImageIO;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.awt.image.BufferedImage;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.TesseractException;
import spring_ocr.pdfscanner.Components.TesseractFactory;

@Service
public class OCRService {
    
    private final TesseractFactory tesseractFactory;

    private static final Logger log = LoggerFactory.getLogger(OCRService.class);

    public OCRService(TesseractFactory tesseractFactory) { 
        this.tesseractFactory = tesseractFactory;
    }

    private List<BufferedImage> convertToImage(MultipartFile file) throws IOException, TesseractException{ 
        if(file == null) { 
            log.error("File is empty");
            throw new IllegalArgumentException("File is empty"); }

        String contentType = file.getContentType();

        if(contentType != null && contentType.startsWith("image/")) { 
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            if(bufferedImage == null) { 
                log.error("Conversion result returned empty");
                throw new IOException("Conversion result was null");
            }
            return List.of(bufferedImage);
        }
        
        if(contentType != null && contentType.startsWith("pdf/")) {
            List<BufferedImage> pages = new ArrayList<>();

            byte[] bytes= file.getInputStream().readAllBytes();

            try (PDDocument document = Loader.loadPDF(bytes)) {
                PDFRenderer renderer = new PDFRenderer(document);

                int pageCount = document.getNumberOfPages();

                for(int i = 0; i < pageCount; i++) { 
                    BufferedImage image = renderer.renderImageWithDPI(i, 300);
                    pages.add(image);
                }
            }

            return pages;
        }
        
        throw new IllegalArgumentException("Unsupported file format: " + contentType);
    }

    public String processImage(MultipartFile image) throws IOException, TesseractException { 
        try { 
            List<BufferedImage> pages = convertToImage(image);

            ExecutorService ocrPool = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors()  
            );
            
            List<Future<String>> futureStrings = new ArrayList<>();

            for(BufferedImage page : pages) { 
                futureStrings.add(ocrPool.submit(() -> {
                    ITesseract tess = tesseractFactory.create();
                    return tess.doOCR(page);
                }));
            }

            StringBuilder fulltext = new StringBuilder();

            for(Future<String> f : futureStrings) { 
                fulltext.append(f);
                fulltext.append("\n");
            }

            ocrPool.shutdown();
            return cleanText(fulltext.toString());
        } catch(IllegalArgumentException e) { 
            log.error("file can't be read");
            throw new IOException("File can't be read");
        }
    }

    private String cleanText(String text) { 
        return text.replaceAll("-\\n", "").replaceAll("\\s+", " ").trim();
    }

}

package spring_ocr.pdfscanner.Services;

import java.io.ByteArrayInputStream;
import java.io.IOException;


import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.awt.image.BufferedImage;
import jakarta.annotation.PostConstruct;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

@Service
public class OCRService {
    
    @Value("${tess.path}")
    private String TESSRACT_PATH;

    private static final Logger log = LoggerFactory.getLogger(OCRService.class);

    @PostConstruct
    public void init() { 
        checkIfTessractPathIsSet();
    }

    private String runTesseract(BufferedImage bufferedImage) throws TesseractException{ 
        ITesseract tesseract = new Tesseract();
        tesseract.setDatapath(TESSRACT_PATH);
        return tesseract.doOCR(bufferedImage);
    }

    private BufferedImage convertToImage(MultipartFile file) throws IOException, TesseractException{ 
        if(file == null) { 
            log.error("File is empty");
            throw new IllegalArgumentException("File is empty"); }
        byte[] arr = file.getBytes();
        BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(arr));
        if(bufferedImage == null) { 
            log.error("Conversion result returned empty");
            throw new IOException("Conversion result was null");
        }
        return bufferedImage;
    }

    public String processImage(MultipartFile image) throws IOException, TesseractException { 
        try { 
            BufferedImage bufferedImage = convertToImage(image);
            return runTesseract(bufferedImage);
        } catch(IllegalArgumentException e) { 
            log.error("file can't be read");
            throw new IOException("File can't be read");
        }
    }


    private void checkIfTessractPathIsSet() {
        if(TESSRACT_PATH == null || TESSRACT_PATH.isEmpty()) { 
            log.error("Tesseract path is not set");;
            throw new IllegalArgumentException("Tessract data path is not set"); }
    }
}

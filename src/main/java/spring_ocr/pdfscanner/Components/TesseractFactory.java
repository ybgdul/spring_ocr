package spring_ocr.pdfscanner.Components;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import net.sourceforge.tess4j.ITessAPI;
import net.sourceforge.tess4j.Tesseract;
import spring_ocr.pdfscanner.Configurations.TesseractConfig;

@Component
@RequiredArgsConstructor
public class TesseractFactory {
    
    private final TesseractConfig ocrProperties;

    public Tesseract create() { 
        Tesseract t = new Tesseract();

        if(ocrProperties.datapath() != null && !ocrProperties.datapath().isBlank()) t.setDatapath(ocrProperties.datapath());
        t.setLanguage(ocrProperties.language());
        t.setOcrEngineMode(ITessAPI.TessOcrEngineMode.OEM_TESSERACT_LSTM_COMBINED);
        t.setPageSegMode(ocrProperties.pageSegMode());

        return t;
    }
}

package spring_ocr.pdfscanner.Configurations;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix="ocr")
public record TesseractConfig(
    String datapath,
    String language,
    int pageSegMode,
    int ocrEngineMode
) {}
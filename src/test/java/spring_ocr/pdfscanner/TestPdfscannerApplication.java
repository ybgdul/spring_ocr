package spring_ocr.pdfscanner;

import org.springframework.boot.SpringApplication;

public class TestPdfscannerApplication {

	public static void main(String[] args) {
		SpringApplication.from(PdfscannerApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}

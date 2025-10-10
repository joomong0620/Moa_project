package edu.og.moa.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.MultipartConfigElement;

@Configuration // 설정용 Bean을 생성하는 클래스
@PropertySource("classpath:/config.properties")
public class FileUploadConfig implements WebMvcConfigurer {
	// WebMvcConfigurer : Spring MVC의 설정을 커스마이징 할 수 있게 해주는 인터페이스
	//                    -> 오버라이딩을 통해 필요한 부분만 직접 설정할 수 있다.
	
	
	// 파일을 hdd에 저장하기 전 임시로 가지고 있을 메모리 용량
	@Value("${spring.servlet.multipart.file-size-threshold}")
	private long fileSizeThreshold;
	
	// 파일 1개 크기 제한
	@Value("${spring.servlet.multipart.max-file-size}")
	private long maxFileSize;
	
	// 요청당 파일 크기 제한
	@Value("${spring.servlet.multipart.max-request-size}")
	private long maxRequestSize;

	
	@Bean // 개발자가 수동으로 Bean 등록(생성은 개발자, 관리는 Spring)
	public MultipartConfigElement configElement() {
		
		MultipartConfigFactory factory = new MultipartConfigFactory();
		// MultipartConfigFactory : 파일 업로드 관련 설정을 구성하기 위한 클래스
		
		factory.setFileSizeThreshold(DataSize.ofBytes(fileSizeThreshold));
		
		factory.setMaxFileSize(DataSize.ofBytes(maxFileSize));
		
		factory.setMaxRequestSize(DataSize.ofBytes(maxRequestSize));
		
		// 설정된 내용을 기반으로 MultipartConfigElement 생성 및 반환
		return factory.createMultipartConfig();
	}
	
	@Bean
	public MultipartResolver multipartResolver() {
		// MultipartResolver : 파일은 파일로, 텍스트는 텍스트로 자동 구분
		
		return new StandardServletMultipartResolver();
	}

	// 웹에서 사용하는 자원을 다루는 방법을 설정
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		
		// /images/ 로 시작되는 요청
		String webPath = "/images/**";
		
		// 실제로 자원이 저장되어있는 로컬 경로
		String resourcePath = "file:///C:/uploadImages/";
		
		// /images/로 시작하는 요청이 오면
		// C:/uploadImages/와 연결
		registry.addResourceHandler(webPath)
				.addResourceLocations(resourcePath);
		
		
		
	}
	
	
	
	
	
	
	
	
	
	
}

package com.gy.swggerstarter.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableConfigurationProperties(value = SwaggerServiceProperties.class)
@ConditionalOnProperty(prefix = "ConfigurationProperties", value = "enable", matchIfMissing = true)
@EnableSwagger2
public class SwaggerAutoConfiguration  implements WebMvcConfigurer {

	@Autowired
	private SwaggerServiceProperties properties;

	// swagger2的配置文件，这里可以配置swagger2的一些基本的内容，比如扫描的包等等
	@Bean
	public Docket createRestApi() {

		return new Docket(DocumentationType.SWAGGER_2).enable(properties.getEnable()).apiInfo(apiInfo()).select()
				// 为当前包路径
				.apis(RequestHandlerSelectors.basePackage(properties.getBasePackage())).paths(PathSelectors.any())
				.build();
	}

	// 构建 api文档的详细信息函数,注意这里的注解引用的是哪个
	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
				// 页面标题
				.title(properties.getTitle())
				// 创建人
				.contact(new Contact(properties.getContactName(), properties.getContactMail(), ""))
				// 版本号
				.version(properties.getVersion())
				// 描述
				.description(properties.getDescription()).build();
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// 解决本地静态资源访问
		registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
		// 解决 SWAGGER 404报错
		if (properties.getEnable()) {
			registry.addResourceHandler("/swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
			registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
		}
	}
}

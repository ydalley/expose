package com.expose.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ITemplateResolver;

import com.expose.data.repo.CommonRepositoryImpl;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import nz.net.ultraq.thymeleaf.LayoutDialect;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass=true)
@EnableJpaRepositories(repositoryBaseClass = CommonRepositoryImpl.class,basePackages= {"com.expose.data.repo"})
public class AppConfig {

	
	@Bean
	@Autowired
	public SpringTemplateEngine templateEngine(ITemplateResolver templateResolver) {
		SpringTemplateEngine engine = new SpringTemplateEngine();
		engine.setTemplateResolver(templateResolver);
		// Enable shiro dialect for thymeleaf
		engine.addDialect(new ShiroDialect());
		engine.addDialect(new LayoutDialect());
		return engine;
	}
	
	@Bean
	public ResourceBundleMessageSource messageSource() {
		ResourceBundleMessageSource source = new ResourceBundleMessageSource();
		String[] baseNames= new String[]{"messages"};
		source.setBasenames(baseNames);
		source.setCacheSeconds(60*30);
		source.setUseCodeAsDefaultMessage(true);
		return source;
	}
	
	

}

package com.loan_system.lls.configuration;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;

@Configuration
public class SwaggerConfig {

	@Value("${openapi.dev-url}")
	private String devUrl;

	@Value("${openapi.prod-url}")
	private String prodUrl;

	@Bean
	public OpenAPI myOpenAPI() {
		Server devServer = new Server();
		devServer.setUrl(devUrl);
		devServer.setDescription("Server URL in Development environment");

		Server prodServer = new Server();
		prodServer.setUrl(prodUrl);
		prodServer.setDescription("Server URL in Production environment");

		Contact contact = new Contact();
		contact.setEmail("aniket.shinde@merce.co");
		contact.setName("SMA");
		contact.setUrl("https://www.merce.co");

		License mitLicense = new License().name("Merce License").url("https://merce.co/licenses");
		
		Info info = new Info().title("Loan Lending System API").version("1.0").contact(contact)
				.description("This is a simple Loan Lending System API based on mini project assinged to us.You can manage loan applications and approvals as well as create role based user's and verify them.")
				.termsOfService("https://www.merce.co/terms").license(mitLicense);

		List<Tag> tag = List.of(new Tag().name("User").description("Everything about user"),
				new Tag().name("Loan").description("Everything about loan"));

		return new OpenAPI().info(info).servers(List.of(devServer, prodServer)).tags(tag);
	}

}

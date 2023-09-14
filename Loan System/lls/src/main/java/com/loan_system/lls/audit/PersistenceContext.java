package com.loan_system.lls.audit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
// @EnableJpaAuditing(dateTimeProviderRef = "dateTimeProvider")
@EnableJpaRepositories(basePackages = {
        "com.loan_system.lls.repository", "com.loan_system.lls.token"
})
@EnableTransactionManagement
public class PersistenceContext {

    @Bean
    public AuditorAware<String> auditorAware() {
        return new CustomAuditAware();
    }
}

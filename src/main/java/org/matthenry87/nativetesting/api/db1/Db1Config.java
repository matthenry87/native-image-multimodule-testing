package org.matthenry87.nativetesting.api.db1;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "db1EntityManagerFactory",
        transactionManagerRef = "db1TransactionManager",
        basePackages = "org.matthenry87.nativetesting.api.db1")
class Db1Config {

    private final DbConfigProps dbConfigProps;

    @Bean(name = "db1ServerDataSource")
    HikariDataSource db1ServerDataSource() {

        return DataSourceBuilder.create()
                .url(dbConfigProps.getUrl())
                .username(dbConfigProps.getUsername())
                .password(dbConfigProps.getPassword())
                .driverClassName("org.h2.Driver")
                .type(HikariDataSource.class)
                .build();
    }

    @Bean(name = "db1EntityManagerFactory")
    EntityManagerFactory db1EntityManagerFactory(@Qualifier("db1ServerDataSource") DataSource db1ServerDataSource) {

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "create-drop");
        properties.put("generate-ddl", "true");

        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(db1ServerDataSource);
        emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        emf.setPersistenceUnitName("db1Persist");
        emf.setPersistenceProviderClass(HibernatePersistenceProvider.class);
        emf.setPackagesToScan("org.matthenry87.nativetesting.api.db1");
        emf.setJpaPropertyMap(properties);
        emf.afterPropertiesSet();

        return emf.getObject();
    }

    @Bean(name = "db1TransactionManager")
    JpaTransactionManager db1TransactionManager(@Autowired @Qualifier("db1EntityManagerFactory") EntityManagerFactory db1EntityManagerFactory) {

        return new JpaTransactionManager(db1EntityManagerFactory);
    }

}

@Getter
@Setter
@Component
@ConfigurationProperties("db1")
class DbConfigProps {

    private String url;
    private String username;
    private String password;
}

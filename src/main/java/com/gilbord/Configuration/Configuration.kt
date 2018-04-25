package com.com.gilbord.Configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.PropertySource
import org.springframework.core.env.Environment
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor
import org.springframework.jdbc.datasource.DriverManagerDataSource
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import java.util.*
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource


@Configuration
@PropertySource("application.properties")
@EnableTransactionManagement
open class Configuration {

    @Bean
    @Primary
    open fun objectMapper() = ObjectMapper().apply {
        registerModule(KotlinModule())
    }

    @Autowired
    var environment: Environment? = null

    @Bean
    fun entityManagerFactory(): LocalContainerEntityManagerFactoryBean {
        val em = LocalContainerEntityManagerFactoryBean()
        em.dataSource = dataSource()
        em.setPackagesToScan("com.com.gilbord.Entities")
        val vendorAdapter = HibernateJpaVendorAdapter()
        em.jpaVendorAdapter = vendorAdapter
        em.setJpaProperties(additionalProperties())
        return em
    }

    @Bean
    fun dataSource(): DataSource {
        val dataSource = DriverManagerDataSource()
        dataSource.setDriverClassName(environment!!.getProperty("spring.datasource.driver"))
        dataSource.url = environment!!.getProperty("spring.datasource.url")
        dataSource.username = environment!!.getProperty("spring.datasource.username")
        dataSource.password = environment!!.getProperty("spring.datasource.password")
        return dataSource
    }

    @Bean
    fun transactionManager(emf: EntityManagerFactory): PlatformTransactionManager {
        val transactionManager = JpaTransactionManager()
        transactionManager.entityManagerFactory = emf
        return transactionManager
    }

    @Bean
    fun exceptionTranslation(): PersistenceExceptionTranslationPostProcessor {
        return PersistenceExceptionTranslationPostProcessor()
    }

    fun additionalProperties(): Properties {
        val properties = Properties()
        properties.setProperty("hibernate.hbm2ddl.auto", environment!!.getProperty("hibernate.hbm2ddl.auto"))
        properties.setProperty("hibernate.dialect", environment!!.getProperty("hibernate.dialect"))
        return properties
    }
}

package com.example.kotlinmvc


import com.zaxxer.hikari.HikariDataSource
import jakarta.persistence.EntityManagerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.core.env.Environment
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.testcontainers.containers.OracleContainer
import org.testcontainers.junit.jupiter.Container
import java.util.*
import javax.sql.DataSource


@TestConfiguration
class OracleTestProfileJPAConfig {
    @Autowired
    private val env: Environment? = null

    @Bean
    fun dataSource(): DataSource {
        container.start()
        val dataSource = HikariDataSource()
        dataSource.jdbcUrl = container.jdbcUrl
        dataSource.username = container.username
        dataSource.password = container.password
        return dataSource
    }

    @Bean
    fun entityManagerFactory(): LocalContainerEntityManagerFactoryBean {
        val em = LocalContainerEntityManagerFactoryBean()
        em.dataSource = dataSource()
        em.setPackagesToScan(*arrayOf("com.example.kotlinmvc"))
        em.jpaVendorAdapter = HibernateJpaVendorAdapter()
        em.setJpaProperties(additionalProperties())
        return em
    }

    @Bean
    fun transactionManager(entityManagerFactory: EntityManagerFactory?): JpaTransactionManager {
        val transactionManager = JpaTransactionManager()
        transactionManager.entityManagerFactory = entityManagerFactory
        return transactionManager
    }

    fun additionalProperties(): Properties {
        val hibernateProperties = Properties()
        hibernateProperties.setProperty("hibernate.hbm2ddl.auto", env!!.getProperty("hibernate.hbm2ddl.auto"))
        hibernateProperties.setProperty("hibernate.dialect", env.getProperty("hibernate.dialect"))
        hibernateProperties.setProperty("hibernate.show_sql", env.getProperty("hibernate.show_sql"))
        return hibernateProperties
    }

    companion object {
        @Container
        var container: OracleContainer = OracleContainer("gvenzl/oracle-xe")
            .withReuse(true)
    }
}

package com.tykj.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PerformanceInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * @author  huran
 */
@Configuration
public class DatasourceConfig implements EnvironmentAware {
    private RelaxedPropertyResolver propertyResolver;

    @Override
    public void setEnvironment(Environment environment) {
        this.propertyResolver = new RelaxedPropertyResolver(environment, "spring.datasource.");
    }

    @Value("${spring.datasource.url}")
    private String myUrl;

    @Primary
    @Bean
    public DataSource writeDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(propertyResolver.getProperty("url"));
        dataSource.setUsername(propertyResolver.getProperty("username"));
        dataSource.setPassword(propertyResolver.getProperty("password"));
        dataSource.setDriverClassName(propertyResolver.getProperty("driverClassName"));
        dataSource.setInitialSize(Integer.parseInt(propertyResolver.getProperty("initialSize")));
        dataSource.setMaxActive(Integer.parseInt(propertyResolver.getProperty("maxActive")));
        dataSource.setMinIdle(Integer.parseInt(propertyResolver.getProperty("minIdle")));
        dataSource.setMaxWait(Integer.parseInt(propertyResolver.getProperty("maxWait")));
        dataSource.setUseUnfairLock(Boolean.valueOf(propertyResolver.getProperty("useUnfairLock")));
        dataSource.setValidationQuery(propertyResolver.getProperty("validationQuery"));
        dataSource.setTestOnBorrow(Boolean.valueOf(propertyResolver.getProperty("testOnBorrow")));
        dataSource.setTestOnReturn(Boolean.valueOf(propertyResolver.getProperty("testOnReturn")));
        dataSource.setTestWhileIdle(Boolean.valueOf(propertyResolver.getProperty("testWhileIdle")));
        dataSource.setTimeBetweenEvictionRunsMillis(Integer.parseInt(propertyResolver.getProperty("timeBetweenEvictionRunsMillis")));
        dataSource.setMinEvictableIdleTimeMillis(Integer.parseInt(propertyResolver.getProperty("minEvictableIdleTimeMillis")));
        try {
            dataSource.setFilters(propertyResolver.getProperty("filters"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return dataSource;
    }
    /***
     * plus 的性能优化,开发模式使用
     * @return
     */
    @Bean
    @ConditionalOnProperty(name = "spring.profiles.active",havingValue = "dev")
    public PerformanceInterceptor performanceInterceptor() {
        PerformanceInterceptor performanceInterceptor = new PerformanceInterceptor();
        performanceInterceptor.setMaxTime(1000);//sql 最大执行时长
        performanceInterceptor.setFormat(true);
        return performanceInterceptor;
    }

    /**
     * 分页插件
     * @return
     */
    @Bean
    public PaginationInterceptor paginationInterceptor(){
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
           paginationInterceptor.setDialectType("mysql");
        return paginationInterceptor;
    }
}

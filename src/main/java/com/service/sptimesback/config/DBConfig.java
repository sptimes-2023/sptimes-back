package com.service.sptimesback.config;

import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Created by Hyunsik Lee on 2023-02-04. Blog : https://hs95blue.github.io/ Github :
 * https://github.com/hs95blue
 */
@EnableTransactionManagement
@Configuration
@MapperScan(value = "com.service.sptimesback.user.mapper", sqlSessionFactoryRef="mainSqlSessionFactory")
public class DBConfig {

  @Bean(name="mainDataSource")
  @ConfigurationProperties(prefix = "spring.main.datasource.hikari")
  public DataSource mainDataSource() {
    //return DataSourceBuilder.create().build();
    return DataSourceBuilder.create().type(HikariDataSource.class).build();
  }

  @Bean(name="mainSqlSessionFactory")
  public SqlSessionFactory mainSqlSessionFactory(@Qualifier("mainDataSource") DataSource mainDataSource ) throws Exception {

    SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
    sqlSessionFactoryBean.setDataSource(mainDataSource);
    Resource[] res = new PathMatchingResourcePatternResolver().getResources("classpath:mainMapper/*Mapper.xml");
    sqlSessionFactoryBean.setMapperLocations(res);
    return sqlSessionFactoryBean.getObject();
  }

  @Bean(name="mainSqlSessionTemplate")
  public SqlSessionTemplate mainSqlSessionTemplate(SqlSessionFactory mainSqlSessionFactory) throws Exception{
    return new SqlSessionTemplate(mainSqlSessionFactory);
  }

  @Bean(name = "mainTransactionManager")
  public DataSourceTransactionManager transactionManager (@Qualifier("mainDataSource") DataSource dataSource) {
    DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
    return transactionManager;
  }

}

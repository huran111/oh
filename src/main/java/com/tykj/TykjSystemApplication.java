package com.tykj;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ImportResource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author huran
 */
@ServletComponentScan(basePackages = {"com.tykj.config"})
@MapperScan("com.tykj.*.mapper")
//@ImportResource(value = "classpath:spring/job.xml")
@EnableTransactionManagement
@SpringBootApplication
public class TykjSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(TykjSystemApplication.class, args);
	}

}

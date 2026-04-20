package com.student.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * 数据源配置类
 * 处理 Railway/Neon 的 DATABASE_URL 格式转换
 * 
 * Neon 连接字符串格式：postgresql://user:pass@host/db?sslmode=require
 * Spring Boot 需要格式：jdbc:postgresql://host:5432/db?sslmode=require
 */
@Configuration
public class DataSourceConfig {

    @Bean
    @Primary
    public DataSource dataSource(DataSourceProperties properties) {
        String url = properties.getUrl();
        
        // 如果是 postgresql:// 格式，转换为 jdbc:postgresql://
        if (url != null && url.startsWith("postgresql://")) {
            try {
                // 解析 postgresql:// 格式的 URL
                // 格式: postgresql://[user[:password]@][host][:port][/database][?params]
                String remaining = url.substring("postgresql://".length());
                
                String username = null;
                String password = null;
                String hostPortDb = remaining;
                
                // 提取用户信息（如果有 @ 符号）
                int atIndex = remaining.indexOf('@');
                if (atIndex > 0) {
                    String userInfo = remaining.substring(0, atIndex);
                    hostPortDb = remaining.substring(atIndex + 1);
                    
                    // 解析用户名密码
                    int colonIndex = userInfo.indexOf(':');
                    if (colonIndex > 0) {
                        username = userInfo.substring(0, colonIndex);
                        password = userInfo.substring(colonIndex + 1);
                    } else {
                        username = userInfo;
                    }
                }
                
                // 构建新的 JDBC URL
                String jdbcUrl = "jdbc:postgresql://" + hostPortDb;
                
                // 如果原 URL 没有指定 sslmode，添加 sslmode=require（Neon 要求）
                if (!jdbcUrl.contains("sslmode")) {
                    jdbcUrl = jdbcUrl + (jdbcUrl.contains("?") ? "&" : "?") + "sslmode=require";
                }
                
                System.out.println("[DataSourceConfig] Converted DATABASE_URL to JDBC format");
                
                // 使用 HikariCP 创建 DataSource
                HikariDataSource dataSource = new HikariDataSource();
                dataSource.setJdbcUrl(jdbcUrl);
                dataSource.setDriverClassName("org.postgresql.Driver");
                
                if (username != null) {
                    dataSource.setUsername(username);
                }
                if (password != null) {
                    dataSource.setPassword(password);
                }
                
                return dataSource;
                
            } catch (Exception e) {
                System.err.println("[DataSourceConfig] Failed to parse DATABASE_URL: " + e.getMessage());
                throw new RuntimeException("Invalid DATABASE_URL format", e);
            }
        }
        
        // 如果已经是 jdbc: 格式或为空，使用默认配置
        return properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }
}

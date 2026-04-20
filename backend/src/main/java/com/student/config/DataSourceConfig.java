package com.student.config;

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
            // postgresql://user:password@host:port/database?params
            // 转换为 jdbc:postgresql://host:port/database?params
            // 用户名密码从 URL 中提取
            
            try {
                // 解析 postgresql:// 格式的 URL
                // 格式: postgresql://[user[:password]@][host][:port][/database][?params]
                String remaining = url.substring("postgresql://".length());
                
                String userInfo = "";
                String hostPortDb = remaining;
                
                // 提取用户信息（如果有 @ 符号）
                int atIndex = remaining.indexOf('@');
                if (atIndex > 0) {
                    userInfo = remaining.substring(0, atIndex);
                    hostPortDb = remaining.substring(atIndex + 1);
                }
                
                // 构建新的 JDBC URL
                String jdbcUrl = "jdbc:postgresql://" + hostPortDb;
                
                // 如果原 URL 没有指定 sslmode，添加 sslmode=require（Neon 要求）
                if (!jdbcUrl.contains("sslmode")) {
                    jdbcUrl = jdbcUrl + (jdbcUrl.contains("?") ? "&" : "?") + "sslmode=require";
                }
                
                System.out.println("[DataSourceConfig] Converted URL: postgresql://***@*** -> jdbc:postgresql://***");
                
                // 创建 DataSource 并设置转换后的 URL
                org.apache.tomcat.jdbc.pool.DataSource ds = new org.apache.tomcat.jdbc.pool.DataSource();
                ds.setUrl(jdbcUrl);
                ds.setDriverClassName("org.postgresql.Driver");
                
                // 从 userInfo 中提取用户名密码
                if (!userInfo.isEmpty()) {
                    int colonIndex = userInfo.indexOf(':');
                    if (colonIndex > 0) {
                        ds.setUsername(userInfo.substring(0, colonIndex));
                        ds.setPassword(userInfo.substring(colonIndex + 1));
                    } else {
                        ds.setUsername(userInfo);
                    }
                } else {
                    // 如果 URL 中没有用户信息，使用环境变量
                    String username = System.getenv("DB_USER");
                    String password = System.getenv("DB_PASSWORD");
                    if (username != null) ds.setUsername(username);
                    if (password != null) ds.setPassword(password);
                }
                
                return ds;
                
            } catch (Exception e) {
                System.err.println("[DataSourceConfig] Failed to parse DATABASE_URL: " + e.getMessage());
                throw new RuntimeException("Invalid DATABASE_URL format", e);
            }
        }
        
        // 如果已经是 jdbc: 格式，使用默认配置
        return properties.initializeDataSourceBuilder().build();
    }
}

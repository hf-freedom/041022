package com.barbershop.saas.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.barbershop.saas.common.MerchantContext;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.apache.ibatis.reflection.MetaObject;

import java.sql.SQLException;
import java.time.LocalDateTime;

@Configuration
public class MybatisPlusConfig {

    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
                this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
                this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
                this.strictInsertFill(metaObject, "deleted", Integer.class, 0);
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
            }
        };
    }

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        interceptor.addInnerInterceptor(new DataIsolationInterceptor());
        return interceptor;
    }

    public static class DataIsolationInterceptor implements InnerInterceptor {
        @Override
        public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        }

        @Override
        public void beforeUpdate(Executor executor, MappedStatement ms, Object parameter) throws SQLException {
            Long merchantId = MerchantContext.getMerchantId();
            if (merchantId != null && parameter != null) {
                try {
                    java.lang.reflect.Field field = parameter.getClass().getDeclaredField("merchantId");
                    field.setAccessible(true);
                    if (field.get(parameter) == null) {
                        field.set(parameter, merchantId);
                    }
                } catch (Exception ignored) {
                }
            }
        }

        private boolean needIsolation(String tableName) {
            return tableName != null && !tableName.equals("t_merchant");
        }

        private String getTableName(String statementId) {
            if (statementId.contains("Member") || statementId.contains("member")) return "t_member";
            if (statementId.contains("Barber") || statementId.contains("barber")) return "t_barber";
            if (statementId.contains("Hairstyle") || statementId.contains("hairstyle")) return "t_hairstyle";
            if (statementId.contains("Order") || statementId.contains("order")) return "t_order";
            if (statementId.contains("Recharge") || statementId.contains("recharge")) return "t_recharge_record";
            return null;
        }

        private String addMerchantWhere(String sql, Long merchantId) {
            if (sql.toLowerCase().contains("where")) {
                return sql.replaceFirst("(?i)where", "WHERE merchant_id = " + merchantId + " AND ");
            } else if (sql.toLowerCase().contains("order by")) {
                return sql.replaceFirst("(?i)order by", "WHERE merchant_id = " + merchantId + " ORDER BY ");
            } else if (sql.toLowerCase().contains("limit")) {
                return sql.replaceFirst("(?i)limit", "WHERE merchant_id = " + merchantId + " LIMIT ");
            } else {
                return sql + " WHERE merchant_id = " + merchantId;
            }
        }
    }
}
package com.renseki.orm.test;

import com.efitrac.commons.model.ModelCache;
import com.efitrac.commons.service.MySQLService;
import com.efitrac.commons.service.ServiceBeanResolver;
import com.efitrac.commons.service.ThreadService;
import com.efitrac.commons.service.impl.obfuscate.MySQLServiceImplObf;
import com.efitrac.commons.util.mysql.ConnectionContainer;
import com.efitrac.commons.util.mysql.SqlUtil;
import com.renseki.orm.SqlTable;
import com.renseki.orm.builder.ModelToSqlTableBuilder;
import com.renseki.orm.builder.SqlDbTableBuilder;
import com.renseki.test.integration.spring.AbstractIntegrationTest;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import cucumber.api.PendingException;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.Test;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FooTest extends AbstractIntegrationTest {

    @Test
    public void test() throws SQLException, InterruptedException, IOException {
        MySQLService mySQLService = ServiceBeanResolver.getMysqlService();
        DataSource ds = mySQLService.getDataSource().get();
//
        MySQLServiceImplObf.Parameters param = mySQLService.getParameters();

        StringBuilder sbSchema = new StringBuilder();
        StringBuilder sbAlter = new StringBuilder();
        for ( String modelName : ModelCache.keySet() ) {

            try ( Connection connection = ds.getConnection() ) {

                final long start = System.currentTimeMillis();
                SqlTable resUsers = new ModelToSqlTableBuilder(
                    modelName, param.getDatabase(), connection)
                    .build();

                sbSchema.append(resUsers.toSqlSchema())
                    .append("\n");

                Optional<String> optAlter = resUsers.toSqlAlterColumnModification();
                optAlter.ifPresent( str -> sbAlter.append(str).append("\n") );

                System.out.println((System.currentTimeMillis() - start) + "ms #" + modelName);
            }
        }


        File f = new File("/home/ian/Workstation/Experimental/renseki-orm/sql.log");
        FileUtils.write(f, sbSchema.toString());
        FileUtils.write(f, sbAlter.toString().trim(), true);

//        String asd = resUsers.toSqlSchema();
//        Optional<String> zxv = resUsers.toSqlAlter();
        String qwe = "";
    }


}

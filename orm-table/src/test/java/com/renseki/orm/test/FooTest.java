package com.renseki.orm.test;

import com.efitrac.commons.service.MySQLService;
import com.efitrac.commons.service.ServiceBeanResolver;
import com.efitrac.commons.service.impl.obfuscate.MySQLServiceImplObf;
import com.efitrac.commons.util.mysql.SqlUtil;
import com.renseki.orm.SqlTable;
import com.renseki.orm.builder.ModelToSqlTableBuilder;
import com.renseki.orm.builder.SqlDbTableBuilder;
import com.renseki.test.integration.spring.AbstractIntegrationTest;
import cucumber.api.PendingException;
import org.testng.annotations.Test;

import java.sql.SQLException;
import java.util.Optional;

public class FooTest extends AbstractIntegrationTest {

    @Test
    public void test() throws SQLException {
        MySQLService mySQLService = ServiceBeanResolver.getMysqlService();
        MySQLServiceImplObf.Parameters param = mySQLService.getParameters();

        SqlTable sqlTable = new SqlDbTableBuilder(
            param.getDatabase(),
            "res_partner",
            SqlUtil.getConnection()
        ).build();

        SqlTable resUsers = new ModelToSqlTableBuilder("res.partner")
            .build();

        SqlTable conjoin = sqlTable.join(resUsers);

        String asd = conjoin.toSqlSchema();
        Optional<String> zxv = conjoin.toSqlAlter();
        String qwe = "";
    }


}

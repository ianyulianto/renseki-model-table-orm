package com.renseki.orm.builder;

import com.efitrac.commons.util.mysql.SqlUtil;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.renseki.orm.Column;
import com.renseki.orm.SqlTable;
import com.renseki.orm.builder.column.SqlDbTableColumnBuilder;
import com.renseki.orm.helper.SqlColumn;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class SqlDbTableBuilder extends SqlTable.Builder {

    private static final Gson GSON = new GsonBuilder()
        .setPrettyPrinting()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .create();

    private final String database;
    private final Connection connection;
    public SqlDbTableBuilder(String database, String table, Connection connection) {
        this.database = database;
        this.connection = connection;

        this.table = table;
    }

    private List<SqlColumn> getAllColumns(String database, String table, Connection connection) {
        List<SqlColumn> res = new ArrayList<>();

        final String query = "SELECT \n" +
            "cols.COLUMN_NAME AS \"column_name\",\n" +
            "col_usage.CONSTRAINT_NAME AS \"constraint_name\", \n" +
            "col_usage.REFERENCED_TABLE_NAME AS \"referenced_table_name\",\n" +
            "col_usage.REFERENCED_COLUMN_NAME AS \"referenced_column_name\",\n" +
            "cols.IS_NULLABLE AS \"is_nullable\",\n" +
            "cols.COLUMN_TYPE AS \"column_type\"\n" +
            "FROM\n" +
            "INFORMATION_SCHEMA.COLUMNS cols\n" +
            "\n" +
            "LEFT JOIN \n" +
            "INFORMATION_SCHEMA.KEY_COLUMN_USAGE col_usage\n" +
            "ON\n" +
            "cols.TABLE_NAME = col_usage.TABLE_NAME\n" +
            "AND cols.TABLE_SCHEMA = col_usage.TABLE_SCHEMA\n" +
            "AND cols.COLUMN_NAME = col_usage.COLUMN_NAME\n" +
            "\n" +
            "WHERE\n" +
            "cols.TABLE_SCHEMA = '" + database + "' \n" +
            "AND cols.TABLE_NAME = '" + table + "' \n" +
            "\n" +
            "GROUP BY COLUMN_NAME";
        try (
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query)
        ) {
            List<Map<String, Object>> list = SqlUtil.toList(rs);

            for ( Map<String, Object> map : list ) {
                final String json = GSON.toJson(map);

                res.add(GSON.fromJson(json, SqlColumn.class));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return res;
    }

    private boolean isTableExist(String table, Connection connection) {
        final String query = "SELECT COUNT(1) FROM `" + table + "`";

        boolean any = false;
        try (
            Statement st = connection.createStatement();
            ResultSet ignored = st.executeQuery(query)
        ) {
            any = true;
        } catch (SQLException ignored) { }

        return any;
    }

    @Override
    public SqlTable build() {
        this.exist = this.isTableExist(table, connection);
        if ( !this.exist ) {
            return new SqlTable(this);
        }

        //  Parse Columns
        final List<Column> columns = new ArrayList<>();
        for ( SqlColumn sqlColumn : this.getAllColumns(database, table, connection) ) {
            Column column =
                new SqlDbTableColumnBuilder(sqlColumn)
                .build();
            columns.add(column);
        }
        this.columns.addAll(columns);

        //  Parse Constrains
        final Map<String, String> constraints = new HashMap<>();
        for ( Column column : columns ) {
            if ( column.getConstraint() != null ) {
                constraints.put(column.getName(), column.getConstraint());
            }
        }
        this.constraints.putAll(constraints);

        return new SqlTable(this);
    }
}

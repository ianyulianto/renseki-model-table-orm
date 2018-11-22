package com.renseki.orm;

import com.renseki.orm.builder.ConjoinSqlTableBuilder;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class SqlTable {

    public static String getRelationKey(String column, String srcTable, String targetTable) {
        return String.format("%s_%s_%s_fk",
            column,
            srcTable,
            targetTable);
    }

    private final boolean exist;
    private final String table;
    private final List<Column> columns;
    private final Map<String, String> constraints;

    public SqlTable(Builder b) {
        this.table = b.table;
        this.columns = b.columns;
        this.constraints = b.constraints;
        this.exist = b.exist;
    }

    public boolean isExist() {
        return exist;
    }

    public String getTable() {
        return table;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public Map<String, String> getConstraints() {
        return constraints;
    }

    public Optional<String> toSqlAlter() {
        final String res;
        if ( !this.exist ) {
            res = null;
        }
        else {
            StringBuilder sb = new StringBuilder();

            //  ALTER ADDITIONAL
            for ( Column column : this.columns ) {
                //  M2M Column - Pivot table
                if ( column.getPivotTable() != null ) {
                    SqlTable pivotTable = column.getPivotTable();

                    sb.append(pivotTable.toSqlSchema().trim())
                        .append("\n");
                }
                //  Common Column
                else {
                    sb.append(this.generateAlterSqlScript(column).trim())
                        .append("\n");
                }
            }

            res = sb.toString().trim();
        }

        return Optional.ofNullable(res);
    }

    public String toSqlSchema() {
        return this.generateSqlCreateTableScript().trim();
    }

    private List<Column> getRelationalColumns() {
        List<Column> res = new ArrayList<>();
        for ( Column col : columns ) {
            if ( col.getRelation() != null ) {
                res.add(col);
            }
        }

        return res;
    }

    private String generateSqlColumnDefinitionScript(Column column) {
        String[] strs = new String[]{
            "`" + column.getName() + "`",
            column.getType(),
            !column.isNullable() ? "NOT NULL" : "",
        };

        return StringUtils.join(strs, ' ').trim();
    }

    private String getPrimaryKeysAsString() {
        List<String> res = new ArrayList<>();
        for ( Column col : columns ) {
            if ( col.isPrimary() ) {
                res.add(col.getName());
            }
        }

        return StringUtils.join(res.toArray(new String[res.size()]), ',').trim();
    }

    private String generateAlterSqlScript(Column column) {
        StringBuilder sb = new StringBuilder();

        //  Column definition
        switch ( column.getMode() ) {
            case NEW:
                sb.append(String.format("ALTER TABLE %s ADD COLUMN %s;",
                    table,
                    this.generateSqlColumnDefinitionScript(column)));
                break;

            case UPDATE:
                sb.append(String.format("ALTER TABLE %s MODIFY COLUMN %s;",
                    table,
                    this.generateSqlColumnDefinitionScript(column)));
                break;

            default:
                return "";
        }

        //  Relation
        if ( column.getRelation() != null ) {
            Relation relation = column.getRelation();
            String res = String.format(
                "ALTER TABLE %s ADD CONSTRAINT %s FOREIGN KEY (`%s`) REFERENCES %s(`%s`);",
                table,
                relation.getName(),
                column.getName(),
                relation.getTableRef(),
                relation.getColRef());

            sb.append("\n")
                .append(res);
        }

        return sb.toString().trim();
    }

    private String generateSqlCreateTableScript() {
        //  CREATE IF NOT EXISTS
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS ")
            .append(table)
            .append(" (\n");

        //--    Columns
        final List<Column> columns = this.rearrangeColumns(this.columns);
        for ( Column column : columns ) {
            //  Schema semua yang Normal
            if ( column.getPivotTable() == null ) {
                sb.append(this.generateSqlColumnDefinitionScript(column))
                    .append(", \n");
            }
        }

        //--    Primary
        final String primaryKeys = this.getPrimaryKeysAsString().trim();
        if ( !primaryKeys.isEmpty() ) {
            sb.append("PRIMARY KEY (")
                .append("`")
                .append(this.getPrimaryKeysAsString())
                .append("`")
                .append("), \n");
        }

        //--    Foreign Key
        for ( Column relationalColumn : this.getRelationalColumns() ) {
            Relation relation = relationalColumn.getRelation();

            String res = String.format("FOREIGN KEY %s(`%s`) REFERENCES %s(`%s`)",
                relation.getName(),
                relationalColumn.getName(),
                relation.getTableRef(),
                relation.getColRef());

            sb.append(res)
                .append(",\n");
        }

        //--    Closing Query
        final String createQuery = sb.toString().trim();
        if ( createQuery.endsWith(",") ) {
            sb = new StringBuilder(createQuery.substring(0, createQuery.length() - 1));
        }
        sb.append(")ENGINE=InnoDB;");

        return sb.toString().trim();
    }

    protected List<Column> rearrangeColumns(List<Column> columns) {
        List<Column> res = new ArrayList<>();
        for ( Column column : columns ) {
            if ( column.isPrimary() ) {
                res.add(0, column);
            }
            else {
                res.add(column);
            }
        }
        return res;
    }

    public SqlTable join(SqlTable sqlTable) {
        return new ConjoinSqlTableBuilder(this, sqlTable)
            .build();
    }

    public static abstract class Builder {

        protected boolean exist = false;
        protected String table;
        protected List<Column> columns = new ArrayList<>();
        protected Map<String, String> constraints = new HashMap<>();

        public abstract SqlTable build();
    }

}

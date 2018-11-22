package com.renseki.orm.builder.column;

import com.renseki.orm.Column;
import com.renseki.orm.Relation;
import com.renseki.orm.helper.SqlColumn;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SqlDbTableColumnBuilder extends Column.Builder {

    private final SqlColumn sqlColumn;
    public SqlDbTableColumnBuilder(SqlColumn sqlColumn) {
        this.sqlColumn = sqlColumn;
    }

    @Override
    public Column build() {
        this.name = sqlColumn.getColumnName();
        this.type = sqlColumn.getColumnType();
        this.nullable = sqlColumn.getIsNullable().equals("YES");
        this.mode = Column.Mode.NOTHING;
        this.constraint = sqlColumn.getConstraintName();

        if ( this.constraint != null &&
             this.constraint.equalsIgnoreCase("primary") ) {

            this.primary = true;
        }

        if ( sqlColumn.getReferencedColumnName() != null ) {
            this.relation = new Relation(
                this.constraint,
                sqlColumn.getReferencedColumnName(),
                sqlColumn.getReferencedTableName()
            );
        }

        return new Column(this);
    }

    private boolean isColumnPrimary(ResultSet rs) throws SQLException {
        final String constraintName = rs.getString("CONSTRAINT_NAME");
        return constraintName.equals("PRIMARY");
    }
}

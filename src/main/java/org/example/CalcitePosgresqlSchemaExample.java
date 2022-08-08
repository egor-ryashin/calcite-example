package org.example;

import org.apache.calcite.adapter.jdbc.JdbcSchema;
import org.apache.calcite.jdbc.CalciteConnection;
import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelRoot;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.tools.Planner;
import org.apache.calcite.tools.RelConversionException;
import org.apache.calcite.tools.ValidationException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class CalcitePosgresqlSchemaExample
{
    public static void main( String[] args )
        throws SQLException, SqlParseException, ValidationException, RelConversionException
    {

        Connection connection = DriverManager.getConnection("jdbc:calcite:");
        CalciteConnection calciteConnection = connection.unwrap(CalciteConnection.class);
        SchemaPlus rootSchema = calciteConnection.getRootSchema();
        final DataSource ds = JdbcSchema.dataSource(
            "jdbc:postgresql://localhost:5432/postgres",
            "org.postgresql.Driver",
            "postgres",
            "postgres");
        rootSchema.add("pg_catalog", JdbcSchema.create(rootSchema, "pg_catalog", ds, null, "pg_catalog"));
        FrameworkConfig config = Frameworks.newConfigBuilder()
                                           .defaultSchema(rootSchema)
                                           .build();

        Planner planner = Frameworks.getPlanner(config);

        SqlNode sqlNode = planner.parse("select \"tablename\" from \"pg_catalog\".\"pg_tables\"");
        System.out.println(sqlNode.toString());

        sqlNode = planner.validate(sqlNode);

        RelRoot relRoot = planner.rel(sqlNode);
        System.out.println(relRoot.toString());

        RelNode relNode = relRoot.project();
        System.out.println(RelOptUtil.toString(relNode));
    }
}

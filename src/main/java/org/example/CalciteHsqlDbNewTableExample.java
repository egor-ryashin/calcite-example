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
import java.sql.Statement;

public class CalciteHsqlDbNewTableExample
{
    public static void main( String[] args )
        throws SQLException, SqlParseException, ValidationException, RelConversionException
    {

        Connection connection = DriverManager.getConnection("jdbc:calcite:");
        CalciteConnection calciteConnection = connection.unwrap(CalciteConnection.class);
        SchemaPlus rootSchema = calciteConnection.getRootSchema();
        final DataSource ds = JdbcSchema.dataSource(
            "jdbc:hsqldb:mem:db",
            "org.hsqldb.jdbc.JDBCDriver",
            "SA",
            "");
        Connection connection1 = ds.getConnection();
        Statement statement = connection1.createStatement();
        statement.execute("create table if not exists PUBLIC.\"heroes\" (\"name\" varchar(255))");
        statement.close();
        connection1.close();
        rootSchema.add("main", JdbcSchema.create(rootSchema, "main", ds, null, "PUBLIC"));
        FrameworkConfig config = Frameworks.newConfigBuilder()
                                           .defaultSchema(rootSchema)
                                           .build();

        Planner planner = Frameworks.getPlanner(config);

        SqlNode sqlNode = planner.parse("select \"name\" from \"main\".\"heroes\"");
        System.out.println(sqlNode.toString());

        sqlNode = planner.validate(sqlNode);

        RelRoot relRoot = planner.rel(sqlNode);
        System.out.println(relRoot.toString());

        RelNode relNode = relRoot.project();
        System.out.println(RelOptUtil.toString(relNode));
    }
}

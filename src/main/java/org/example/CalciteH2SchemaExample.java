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

public class CalciteH2SchemaExample
{
    public static void main( String[] args )
        throws SQLException, SqlParseException, ValidationException, RelConversionException
    {

        Connection connection = DriverManager.getConnection("jdbc:calcite:");
        CalciteConnection calciteConnection = connection.unwrap(CalciteConnection.class);
        SchemaPlus rootSchema = calciteConnection.getRootSchema();
        final DataSource ds = JdbcSchema.dataSource(
            "jdbc:h2:mem:db",
//            "jdbc:hsqldb:mem:db",
            "org.h2.Driver",
            "SA",
            "");
        rootSchema.add("main", JdbcSchema.create(rootSchema, "main", ds, null, "INFORMATION_SCHEMA"));
        FrameworkConfig config = Frameworks.newConfigBuilder()
                                           .defaultSchema(rootSchema)
                                           .build();

        Planner planner = Frameworks.getPlanner(config);

//        SqlNode sqlNode = planner.parse("select \"id\" from \"main\".\"heroes2\"");
        SqlNode sqlNode = planner.parse("select \"TABLE_NAME\" from \"main\".\"TABLES\"");
        System.out.println(sqlNode.toString());

        sqlNode = planner.validate(sqlNode);

        RelRoot relRoot = planner.rel(sqlNode);
        System.out.println(relRoot.toString());

        RelNode relNode = relRoot.project();
        System.out.println(RelOptUtil.toString(relNode));
    }
}

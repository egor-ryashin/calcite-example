package org.example;

import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.parser.ddl.SqlDdlParserImpl;
import org.apache.calcite.sql.validate.SqlConformanceEnum;
import org.apache.calcite.tools.RelConversionException;
import org.apache.calcite.tools.ValidationException;
import org.apache.calcite.util.SourceStringReader;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Should print:
 * NlsString ';'
 * BigDecimal 1
 */
public class CalciteMultiStatementExample
{
    public static void main( String[] args )
        throws SQLException, SqlParseException, ValidationException, RelConversionException, IOException
    {
        SqlParser.Config parserConfig = SqlParser
            .config()
            .withConformance(SqlConformanceEnum.BABEL)
            .withParserFactory(SqlDdlParserImpl.FACTORY);

        SqlNodeList list = SqlParser.create(new SourceStringReader("select ';' ; select 1"), parserConfig).parseStmtList();
        for (SqlNode stmt : list.stream().toList()) {
            for (SqlNode sqlNode1 : ((SqlSelect)stmt).getSelectList()) {
                SqlLiteral sqlNode11 = (SqlLiteral) sqlNode1;
                System.out.println(sqlNode11.getValue().getClass().getSimpleName() + " " + sqlNode11.getValue());
            }
        }
    }
}

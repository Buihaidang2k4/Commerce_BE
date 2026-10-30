package com.example.MyShop_API.mybatis;

import com.example.MyShop_API.mybatis.mapper.OrderReportMapper;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.Configuration;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class OrderReportMapperTest {
    @Test void parsesMapperAndBindsDatesWithoutStringInterpolation() throws Exception {
        Configuration configuration = new Configuration();
        String resource = "mybatis/mapper/OrderReportMapper.xml";
        try (var stream = Resources.getResourceAsStream(resource)) {
            new XMLMapperBuilder(stream, configuration, resource, configuration.getSqlFragments()).parse();
        }
        assertTrue(configuration.hasMapper(OrderReportMapper.class));
        var statement = configuration.getMappedStatement(OrderReportMapper.class.getName() + ".summarizeByStatus");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("fromDate", LocalDate.of(2026, 1, 1));
        parameters.put("toDate", LocalDate.of(2026, 12, 31));
        var sql = statement.getBoundSql(parameters);
        assertEquals(2, sql.getParameterMappings().size());
        assertFalse(sql.getSql().contains("2026"));
        parameters.clear();
        assertFalse(statement.getBoundSql(parameters).getSql().contains("WHERE"));
        parameters.put("toDate", LocalDate.of(2026, 12, 31));
        assertFalse(statement.getBoundSql(parameters).getSql().matches("(?s).*WHERE\\s+AND.*"));
    }
}

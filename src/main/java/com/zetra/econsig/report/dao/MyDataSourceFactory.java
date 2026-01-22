package com.zetra.econsig.report.dao;

import java.sql.ResultSetMetaData;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.helper.sistema.DBHelper;

import net.sf.jasperreports.engine.JRDataSource;

/**
 * <p> Title: MyDataSourceFactory</p>
 * <p> Description: Utilizada pelo arquivo iReports (.jasper) para a recuperação do 
 * Data Source que será utilizado para o preenchimento do relatório.</p>
 * <p> Copyright: Copyright (c) 2006 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class MyDataSourceFactory {
    public JRDataSource getMyDataSource(String sql, MapSqlParameterSource queryParams) {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final Stream<Map<String, Object>> stream = jdbc.queryForStream(sql, queryParams, (rs, rowNum) -> {
            Map<String, Object> row = new LinkedHashMap<>();
            ResultSetMetaData metData = rs.getMetaData();
            for (int i = 1; i <= metData.getColumnCount(); i++) {
                row.put(metData.getColumnLabel(i).toUpperCase(), rs.getObject(i));
            }
            return row;
        });

        return new MyDataSource(stream);
    }
}

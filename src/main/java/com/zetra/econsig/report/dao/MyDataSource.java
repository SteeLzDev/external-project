package com.zetra.econsig.report.dao;

import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRField;

/**
 * <p> Title: MyDataSource</p>
 * <p> Description: Gerencia a consulta de dados no Data Source</p>
 * <p> Copyright: Copyright (c) 2006 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class MyDataSource implements JRDataSource {
    private final Stream<Map<String, Object>> rowStream;
    private final Iterator<Map<String, Object>> rowIterator;
    private Map<String, Object> currentRow;

    public MyDataSource(Stream<Map<String, Object>> rowStream) {
        this.rowStream = rowStream;
        this.rowIterator = rowStream.iterator();
    }

    @Override
    public boolean next() {
        if (rowIterator.hasNext()) {
            currentRow = rowIterator.next();
            return true;
        }
        rowStream.close();
        return false;
    }

    @Override
    public Object getFieldValue(JRField jrField) {
        return currentRow.get(jrField.getName().toUpperCase());
    }
}

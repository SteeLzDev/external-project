package com.zetra.econsig.report.dao.hibernate;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.ScrollableResults;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

/**
 * <p>Title: HibernateQueryResultDataSource</p>
 * <p>Description: Implementação Hibernate para o JRDataSource.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class HibernateQueryResultDataSource implements JRDataSource {
    protected Map<String, Integer> fieldsToIdxMap = new HashMap<>();
    protected Iterator<Object[]> iterator;
    protected Object[] currentValue;
    protected ScrollableResults<Object[]> scroll;

    public HibernateQueryResultDataSource(Iterator<Object[]> iterator, String query) {
        prepareQuery(query);
        this.iterator = iterator;
    }

    public HibernateQueryResultDataSource(ScrollableResults<Object[]> scroll, String query) {
        prepareQuery(query);
        this.scroll = scroll;
    }

    public HibernateQueryResultDataSource(List<Object[]> list, String query) {
        this(list.iterator(), query);
    }

    public HibernateQueryResultDataSource(List<Object[]> list, String[] fields) {
        for (int i = 0; i < fields.length; i++) {
            fieldsToIdxMap.put(fields[i].toLowerCase(), i);
        }
        this.iterator = list.iterator();
    }

    private void prepareQuery(String query) {
        query = query.toLowerCase();

        //Procura por subquery no select, caso encontre procura pelo próximo "from" que não faça parte de subquery
        int start = query.indexOf("select");
        int stop = query.indexOf(" from ");

        String subQuery = query.substring(start + 6, stop);
        int startSubQuery = start;

        while (subQuery.contains("select")) {
            startSubQuery = stop;
            stop = query.indexOf(" from ", stop + 5);
            subQuery = query.substring(startSubQuery, stop);
        }

        start += "select".length();
        String parameters = query.substring(start, stop + 5);
        parameters = parameters.trim();

        final Pattern pattern = Pattern.compile("as(\\s+)(\\w+)(\\s*)(,|(\\s+)from)");
        final Matcher matcher = pattern.matcher(parameters);

        int idx = 0;
        while (matcher.find()) {
            String parameter = matcher.group();
            final int firstSpace = parameter.indexOf(" ");

            int virgula = parameter.indexOf(",");
            if (virgula < 0) {
                virgula = parameter.indexOf(" from");
            }

            parameter = parameter.substring(firstSpace, virgula).trim();
            fieldsToIdxMap.put(parameter.trim(), Integer.valueOf(idx));
            idx++;
        }
    }

    @Override
    public Object getFieldValue(JRField field) throws JRException {
        final Integer idxInt = fieldsToIdxMap.get(field.getName().toLowerCase());

        final Object[] values = currentValue;

        return values[idxInt];
    }

    @Override
    public boolean next() throws JRException {
        if (scroll != null) {
            if (scroll.next()) {
                currentValue = scroll.get();
                return currentValue != null;
            } else {
                return false;
            }
        }

        currentValue = iterator.hasNext() ? iterator.next() : null;
        return currentValue != null;
    }
}

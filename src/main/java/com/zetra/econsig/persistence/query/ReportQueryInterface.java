package com.zetra.econsig.persistence.query;

import java.util.Iterator;
import java.util.List;

import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.report.reports.ReportTemplate;

public interface ReportQueryInterface {
    public String getQueryString();

    public Query<Object[]> preparar(Session session) throws HQueryException;

    public void setCriterios(TransferObject criterio);

    public <T> List<T> executarLista() throws HQueryException;

    public Iterator<Object[]> executarIterator(Session session) throws HQueryException;

    public <T> List<T> executarLista(Session session) throws HQueryException;

    public void setReportTemplate(ReportTemplate reportTemplate);

    public ReportTemplate getReportTemplate();

    public ScrollableResults<Object[]> executarScroll(Session session) throws HQueryException;

    public String[] getReportFields();
}

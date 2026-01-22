package com.zetra.econsig.persistence.query;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.report.reports.ReportTemplate;

/**
 * <p>Title: ReportHNativeQuery</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class ReportHNativeQuery extends HNativeQuery implements ReportQueryInterface {
    protected ReportTemplate reportTemplate;

    @Override
    public ReportTemplate getReportTemplate() {
        return reportTemplate;
    }

    @Override
    public void setReportTemplate(ReportTemplate reportTemplate) {
        this.reportTemplate = reportTemplate;
    }

    @Override
    public void setCriterios(TransferObject criterio) {
    }

    @Override
    public String[] getReportFields() {
        return null;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        return null;
    }
}

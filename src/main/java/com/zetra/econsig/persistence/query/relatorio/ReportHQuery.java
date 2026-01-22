package com.zetra.econsig.persistence.query.relatorio;

import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.persistence.query.ReportQueryInterface;
import com.zetra.econsig.report.reports.ReportTemplate;
import com.zetra.econsig.values.CamposRelatorioSinteticoEnum;

/**
 * <p> Title: ReportHQuery</p>
 * <p> Description: Classe base para HQueries de relatórios</p>
 * <p> Copyright: Copyright (c) 2006 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class ReportHQuery extends HQuery implements ReportQueryInterface {

    private static final String ASC_DESC_REGEX = "(?i)(ASC|DESC)";

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

    public static final String parseOderByClause(String order, Map<String,String> tipoOrdMap) {
        StringBuilder orderBuilder = new StringBuilder();
        for (String orderField : order.split("\s*,\s*")) {
            String[] orderFieldParts = orderField.split(" ");
            CamposRelatorioSinteticoEnum orderFieldEnum = CamposRelatorioSinteticoEnum.recuperaCampo(orderFieldParts[0]);
            if (orderFieldEnum != null) {
                orderBuilder.append(orderFieldEnum.getCampoOrderBy());

                // Direção ASC / DESC : pode estar no tipoOrdMap ou no próprio texto do campo
                if (tipoOrdMap != null && tipoOrdMap.get(orderFieldEnum.getCodigo()) != null && tipoOrdMap.get(orderFieldEnum.getCodigo()).matches(ASC_DESC_REGEX)) {
                    orderBuilder.append(" ").append(tipoOrdMap.get(orderFieldEnum.getCodigo()));
                } else if (orderFieldParts.length > 1 && orderFieldParts[1].matches(ASC_DESC_REGEX)) {
                    orderBuilder.append(" ").append(orderFieldParts[1]);
                }

                orderBuilder.append(",");
            }
        }
        // Remove a última vírgula
        if (orderBuilder.length() > 0) {
            orderBuilder.deleteCharAt(orderBuilder.length() - 1);
        }

        return orderBuilder.toString();
    }
}

package com.zetra.econsig.report.jasper.dinamico;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.report.config.Relatorio;

import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.DynamicReportBuilder;
import ar.com.fdvs.dj.domain.builders.GroupBuilder;
import ar.com.fdvs.dj.domain.constants.GroupLayout;
import ar.com.fdvs.dj.domain.entities.DJGroup;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import ar.com.fdvs.dj.domain.entities.columns.PropertyColumn;

/**
 * <p>Title: RelatorioEditavelInfo</p>
 * <p>Description: Definição de colunas do Relatório Editável</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioEditavelInfo extends DynamicReportInfo {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RelatorioEditavelInfo.class);

    public RelatorioEditavelInfo(Relatorio relatorio) {
        super(relatorio);
    }

    @Override
    protected DynamicReportBuilder reportDesign(AcessoSistema responsavel) {
        DynamicReportBuilder reportBuilder = new DynamicReportBuilder();
        reportBuilder.setWhenNoDataAllSectionNoDetail();

        try {
            String sql = relatorio.getTemplateSql();
            List<String> campos = getColumns(sql);

            boolean possuiAgrupamento = !TextHelper.isNull(relatorio.getAgrupamento());

            Style borda = new Style("borda");
            GroupLayout groupLayout = new GroupLayout(true, true, true, false, false);

            if (campos != null) {
                Iterator<String> ite = campos.iterator();
                while (ite.hasNext()) {
                    String campo = ite.next();

                    //coluna
                    ColumnBuilder columnBuilder = buildColumn(campo.toUpperCase(), 70, false, campo, String.class.getName(), borda);
                    AbstractColumn columnCor = columnBuilder.build();
                    reportBuilder.addColumn(columnCor);

                    if (possuiAgrupamento && relatorio.getAgrupamento().equalsIgnoreCase(campo)) {
                        GroupBuilder groupBuilder = new GroupBuilder();
                        DJGroup columnGroup = groupBuilder.setStartInNewPage(true).setCriteriaColumn((PropertyColumn) columnCor).setGroupLayout(groupLayout).build();
                        reportBuilder.addGroup(columnGroup);
                    }
                }

                //reportBuilder.build();
            }


        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return reportBuilder;
    }

    private List<String> getColumns(String query) {
        List<String> campos = new ArrayList<>();
        query = query.toLowerCase();
        query = query.replaceAll("\\r\\n|\\r|\\n", " ");
        query = query.replaceAll(System.getProperty("line.separator"), " ");

        int start = query.indexOf("select ");
        int stop = query.indexOf(" from ");

        start += "select".length();
        String parameters = query.substring(start,stop + 5);
        parameters = parameters.trim();

        Pattern pattern = Pattern.compile("as(\\s+)(\\w+|(.?)|'(.*?)'|\"(.*?)\")(\\s*)(,|(\\s+)from)");
        Matcher matcher = pattern.matcher(parameters);

        while (matcher.find()) {
            String parameter = matcher.group();
            int firstSpace = parameter.indexOf(" ");

            int virgula = parameter.indexOf(",");
            if (virgula < 0) {
                virgula = parameter.indexOf(" from");
            }

            parameter = parameter.substring(firstSpace, virgula).trim();
            campos.add(parameter.replaceAll("'|\"", "").trim());
        }

        return campos;
    }
}

package com.zetra.econsig.report.jasper.dinamico;

import java.util.Iterator;
import java.util.List;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.report.config.Relatorio;

import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.DynamicReportBuilder;

/**
 * <p>Title: RelatorioIntegracaoSemMapeamento.java</p>
 * <p>Description: Definição de colunas do Relatório Integracao Sem Processamento XLS</p>
 * <p>Copyright: Copyright (c) 2015</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioIntegracaoMapeamentoMultiploInfo extends DynamicReportInfo{
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RelatorioIntegracaoMapeamentoMultiploInfo.class);

    public RelatorioIntegracaoMapeamentoMultiploInfo(Relatorio relatorio) {
        super(relatorio);
    }

    @Override
    protected DynamicReportBuilder reportDesign(AcessoSistema responsavel) {
        DynamicReportBuilder reportBuilder = new DynamicReportBuilder();
        reportBuilder.setWhenNoDataAllSectionNoDetail();

        try {
            List<String> campos = (List<String>) criterioQuery.getAttribute("CAMPOS");
            List<String> colunas = (List<String>) criterioQuery.getAttribute("COLUNAS");

            Style borda = new Style("borda");

            Iterator<String> iteratorCampos = campos.iterator();
            Iterator<String> iteratorColunas = colunas.iterator();

            String nomeColuna = null;
            String nomeCampo = null;

            while(iteratorCampos.hasNext() && iteratorColunas.hasNext()){
                nomeCampo = iteratorCampos.next();
                nomeColuna = iteratorColunas.next();

                ColumnBuilder columnBuilder = buildColumn(nomeColuna, 50, false, nomeCampo, String.class.getName(), borda);
                reportBuilder.addColumn(columnBuilder.build());
            }

        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return reportBuilder;
    }

}
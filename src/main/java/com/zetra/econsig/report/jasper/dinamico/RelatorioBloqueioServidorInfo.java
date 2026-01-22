package com.zetra.econsig.report.jasper.dinamico;

import java.util.List;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.values.Columns;

import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.DynamicReportBuilder;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;

/**
 * <p>Title: RelatorioBloqueioServidorInfo</p>
 * <p>Description: Definição de colunas do Relatório de Bloqueio de Servidor</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioBloqueioServidorInfo extends DynamicReportInfo {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RelatorioBloqueioServidorInfo.class);

    public RelatorioBloqueioServidorInfo(Relatorio relatorio) {
        super(relatorio);
    }

    @Override
    protected DynamicReportBuilder reportDesign(AcessoSistema responsavel) {
        DynamicReportBuilder reportBuilder = new DynamicReportBuilder();
        reportBuilder.setWhenNoDataAllSectionNoDetail();

        try {
            List<String> campos = (List<String>) criterioQuery.getAttribute("CAMPOS");

            Style borda = new Style("borda");
            Style bordaNumerica = new Style("bordaNumerica");

            if (campos != null) {
                if (campos.contains(Columns.SER_NOME)) {
                    //coluna servidor
                    ColumnBuilder columnBuilder = buildColumn(ApplicationResourcesHelper.getMessage("rotulo.servidor.singular", responsavel), 70, false, "ser_nome", String.class.getName(), borda);

                    AbstractColumn columnCor = columnBuilder.build();

                    reportBuilder.addColumn(columnCor);
                }

                if (campos.contains(Columns.SER_CPF)) {
                    //coluna servidor
                    ColumnBuilder columnBuilder = buildColumn(ApplicationResourcesHelper.getMessage("rotulo.servidor.cpf", responsavel), 50, false, "ser_cpf", String.class.getName(), borda);

                    AbstractColumn columnCor = columnBuilder.build();

                    reportBuilder.addColumn(columnCor);
                }

                if (campos.contains(Columns.SRS_DESCRICAO)) {
                    //coluna situação do servidor
                    ColumnBuilder columnBuilder = buildColumn(ApplicationResourcesHelper.getMessage("rotulo.servidor.status", responsavel), 35, false, "srs_descricao", String.class.getName(), borda);

                    AbstractColumn columnCor = columnBuilder.build();

                    reportBuilder.addColumn(columnCor);
                }

                if (campos.contains(Columns.SVC_DESCRICAO)) {
                    //coluna serviço
                    ColumnBuilder columnBuilder = buildColumn(ApplicationResourcesHelper.getMessage("rotulo.servico.singular", responsavel), 50, false, "svc_descricao", String.class.getName(), borda);

                    AbstractColumn columnCor = columnBuilder.build();

                    reportBuilder.addColumn(columnCor);
                }

                if (campos.contains(Columns.ORG_NOME)) {
                    //coluna orgao
                    ColumnBuilder columnBuilder = buildColumn(ApplicationResourcesHelper.getMessage("rotulo.orgao.singular", responsavel), 70, false, "org_nome", String.class.getName(), borda);

                    AbstractColumn columnCor = columnBuilder.build();

                    reportBuilder.addColumn(columnCor);
                }

                if (campos.contains(Columns.CSA_NOME)) {
                    //coluna nome consignataria
                    ColumnBuilder columnBuilder = buildColumn(ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular", responsavel), 70, false, "csa_nome", String.class.getName(), borda);

                    AbstractColumn columnNomAbrev = columnBuilder.build();

                    reportBuilder.addColumn(columnNomAbrev);

                }

                if (campos.contains(Columns.CNV_COD_VERBA)) {
                    //coluna verba
                    ColumnBuilder columnBuilder = buildColumn(ApplicationResourcesHelper.getMessage("rotulo.verba.singular", responsavel), 30, false, "cnv_cod_verba", String.class.getName(), borda);

                    AbstractColumn columnEst = columnBuilder.build();

                    reportBuilder.addColumn(columnEst);
                }

                if (campos.contains(Columns.PCR_VLR)) {
                    //coluna valor
                    ColumnBuilder columnBuilder = buildColumn(ApplicationResourcesHelper.getMessage("rotulo.bloqueio.servidor.valor", responsavel), 20, false, "pcr_vlr", String.class.getName(), bordaNumerica);

                    AbstractColumn columnOrgao = columnBuilder.build();

                    reportBuilder.addColumn(columnOrgao);
                }

                if (campos.contains(Columns.PCR_OBS)) {
                    //coluna motivo
                    ColumnBuilder columnBuilder = buildColumn(ApplicationResourcesHelper.getMessage("rotulo.bloqueio.servidor.motivo", responsavel), 50, false, "pcr_obs", String.class.getName(), borda);

                    AbstractColumn columnEst = columnBuilder.build();

                    reportBuilder.addColumn(columnEst);
                }

                if (campos.contains(Columns.PCR_DATA_CADASTRO)) {
                    //coluna data
                    ColumnBuilder columnBuilder = buildColumn(ApplicationResourcesHelper.getMessage("rotulo.bloqueio.servidor.data", responsavel), 50, false, "pcr_data_cadastro", String.class.getName(), borda);

                    AbstractColumn columnEst = columnBuilder.build();

                    reportBuilder.addColumn(columnEst);
                }

            }

        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return reportBuilder;
    }

}

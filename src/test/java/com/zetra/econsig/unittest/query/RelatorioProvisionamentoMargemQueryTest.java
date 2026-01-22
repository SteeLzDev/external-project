package com.zetra.econsig.unittest.query;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.persistence.query.relatorio.RelatorioProvisionamentoMargemQuery;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.values.Columns;

public class RelatorioProvisionamentoMargemQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        final TransferObject criterio = new CustomTransferObject();
        criterio.setAttribute(ReportManager.PARAM_NAME_PERIODO_INICIO, "2023-01-01 00:00:00");
        criterio.setAttribute(ReportManager.PARAM_NAME_PERIODO_FIM, "2023-01-01 23:59:59");
        criterio.setAttribute(Columns.SVC_CODIGO, List.of("050E8080808080808080808080808280", "1"));
        criterio.setAttribute(Columns.ORG_CODIGO, List.of("751F8080808080808080808080809780"));
        criterio.setAttribute(Columns.CSA_CODIGO, "267");
        criterio.setAttribute(Columns.COR_CODIGO, null);
        criterio.setAttribute("adeNuncaExistiuLancamento", false);
        criterio.setAttribute("adeAptasPortabilidade", false);

        final RelatorioProvisionamentoMargemQuery query = new RelatorioProvisionamentoMargemQuery();
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);
        query.setCriterios(criterio);

        executarConsulta(query);
    }

    @Test
    public void test_02() throws com.zetra.econsig.exception.ZetraException {
        final TransferObject criterio = new CustomTransferObject();
        criterio.setAttribute(ReportManager.PARAM_NAME_PERIODO_INICIO, "2023-01-01 00:00:00");
        criterio.setAttribute(ReportManager.PARAM_NAME_PERIODO_FIM, "2023-01-01 23:59:59");
        criterio.setAttribute(Columns.SVC_CODIGO, List.of("050E8080808080808080808080808280", "1"));
        criterio.setAttribute(Columns.ORG_CODIGO, List.of("751F8080808080808080808080809780"));
        criterio.setAttribute(Columns.CSA_CODIGO, "267");
        criterio.setAttribute(Columns.COR_CODIGO, null);
        criterio.setAttribute("adeNuncaExistiuLancamento", true);
        criterio.setAttribute("adeAptasPortabilidade", false);

        final RelatorioProvisionamentoMargemQuery query = new RelatorioProvisionamentoMargemQuery();
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);
        query.setCriterios(criterio);

        executarConsulta(query);
    }

    @Test
    public void test_03() throws com.zetra.econsig.exception.ZetraException {
        final TransferObject criterio = new CustomTransferObject();
        criterio.setAttribute(ReportManager.PARAM_NAME_PERIODO_INICIO, "2023-01-01 00:00:00");
        criterio.setAttribute(ReportManager.PARAM_NAME_PERIODO_FIM, "2023-01-01 23:59:59");
        criterio.setAttribute(Columns.SVC_CODIGO, List.of("050E8080808080808080808080808280", "1"));
        criterio.setAttribute(Columns.ORG_CODIGO, List.of("751F8080808080808080808080809780"));
        criterio.setAttribute(Columns.CSA_CODIGO, "267");
        criterio.setAttribute(Columns.COR_CODIGO, null);
        criterio.setAttribute("adeNuncaExistiuLancamento", true);
        criterio.setAttribute("adeAptasPortabilidade", true);

        final RelatorioProvisionamentoMargemQuery query = new RelatorioProvisionamentoMargemQuery();
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);
        query.setCriterios(criterio);

        executarConsulta(query);
    }
}



package com.zetra.econsig.unittest.query;

import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.persistence.query.relatorio.RelatorioSinteticoDecisaoJudicialQuery;
import com.zetra.econsig.values.CamposRelatorioSinteticoEnum;

public class RelatorioSinteticoDecisaoJudicialQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        TransferObject criterio = new CustomTransferObject();
        criterio.setAttribute("TIPO_ENTIDADE", "ORG");
        criterio.setAttribute("DATA_INI", "2023-01-01 00:00:00");
        criterio.setAttribute("DATA_FIM", "2023-01-01 23:59:59");
        criterio.setAttribute("SVC_CODIGO", List.of("050E8080808080808080808080808280", "1"));
        criterio.setAttribute("ORG_CODIGO", List.of("751F8080808080808080808080809780"));
        criterio.setAttribute("CSA_CODIGO", "267");
        criterio.setAttribute("CAMPOS", List.of(CamposRelatorioSinteticoEnum.CAMPO_CONSIGNATARIA_ABREV.getCodigo(), CamposRelatorioSinteticoEnum.CAMPO_ESTABELECIMENTO.getCodigo()));
        criterio.setAttribute("ORDER", CamposRelatorioSinteticoEnum.CAMPO_CONSIGNATARIA_ABREV.getCodigo());
        criterio.setAttribute("TIPO_ORD", new HashMap<String,String>());

        RelatorioSinteticoDecisaoJudicialQuery query = new RelatorioSinteticoDecisaoJudicialQuery();
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);
        query.setCriterios(criterio);

        executarConsulta(query);
    }
}



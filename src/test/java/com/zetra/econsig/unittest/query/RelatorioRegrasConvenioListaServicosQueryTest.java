package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.persistence.query.relatorio.RelatorioRegrasConvenioListaServicosQuery;

public class RelatorioRegrasConvenioListaServicosQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();

        String csaCodigo = "267";
        com.zetra.econsig.helper.seguranca.AcessoSistema responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);

        RelatorioRegrasConvenioListaServicosQuery query = new RelatorioRegrasConvenioListaServicosQuery(csaCodigo, responsavel);
        query.setCriterios(criterios);

        executarConsulta(query);
    }
}


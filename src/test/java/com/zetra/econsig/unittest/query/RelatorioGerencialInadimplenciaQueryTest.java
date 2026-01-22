package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.CustomTransferObject;

import com.zetra.econsig.persistence.query.relatorio.RelatorioGerencialInadimplenciaQuery;

public class RelatorioGerencialInadimplenciaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();

        RelatorioGerencialInadimplenciaQuery query = new RelatorioGerencialInadimplenciaQuery();
        query.setCriterios(criterios);

        query.periodo = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);

        executarConsulta(query);
    }
}


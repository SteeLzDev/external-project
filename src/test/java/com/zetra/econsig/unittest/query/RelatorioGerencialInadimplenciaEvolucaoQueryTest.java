package com.zetra.econsig.unittest.query;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.query.relatorio.RelatorioGerencialInadimplenciaEvolucaoQuery;

public class RelatorioGerencialInadimplenciaEvolucaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();

        RelatorioGerencialInadimplenciaEvolucaoQuery query = new RelatorioGerencialInadimplenciaEvolucaoQuery();
        query.setCriterios(criterios);

        query.periodos = List.of(DateHelper.getSystemDate());
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);

        executarConsulta(query);
    }
}


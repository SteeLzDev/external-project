package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.CustomTransferObject;

import com.zetra.econsig.persistence.query.saldodevedor.ListaSolicitacaoSaldoDevedorSemRespostaQuery;

public class ListaSolicitacaoSaldoDevedorSemRespostaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();

        ListaSolicitacaoSaldoDevedorSemRespostaQuery query = new ListaSolicitacaoSaldoDevedorSemRespostaQuery();
        query.setCriterios(criterios);

        query.csaCodigo = "267";
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);

        executarConsulta(query);
    }
}


package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.persistence.query.consignacao.ListaSolicitacaoSaldoDevedorPorRegistroServidorQuery;

public class ListaSolicitacaoSaldoDevedorPorRegistroServidorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();

        ListaSolicitacaoSaldoDevedorPorRegistroServidorQuery query = new ListaSolicitacaoSaldoDevedorPorRegistroServidorQuery();
        query.setCriterios(criterios);

        query.rseCodigo = "123";

        executarConsulta(query);
    }
}


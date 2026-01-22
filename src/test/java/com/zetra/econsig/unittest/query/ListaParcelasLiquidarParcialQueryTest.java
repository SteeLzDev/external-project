package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.CustomTransferObject;

import com.zetra.econsig.persistence.query.parcela.ListaParcelasLiquidarParcialQuery;

public class ListaParcelasLiquidarParcialQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();

        ListaParcelasLiquidarParcialQuery query = new ListaParcelasLiquidarParcialQuery();
        query.setCriterios(criterios);

        query.adeCodigo = "731A8D1EAZ564668A4Z0004423D9A1BD";
        query.prdCodigo = 1;
        query.prdNumero = 1;
        query.prdDataDesconto = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.ordenaDataDescontoDesc = true;
        query.permiteLiquidarParcelaParcial = true;

        executarConsulta(query);
    }
}


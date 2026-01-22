package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.persistence.query.compra.ListaAcompanhamentoCompraQuery;

public class ListaAcompanhamentoCompraQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaAcompanhamentoCompraQuery query = new ListaAcompanhamentoCompraQuery();
        query.parametrosTO = new CustomTransferObject();
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);
        query.csaCodigo = "267";
        query.corCodigo = "EF128080808080808080808080809980";
        query.orgCodigos = java.util.List.of("1", "2");
        query.matriculaExataSoap = true;
        query.orderByCsaNome = true;

        executarConsulta(query);
    }
}


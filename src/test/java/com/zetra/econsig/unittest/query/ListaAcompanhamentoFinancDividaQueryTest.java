package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.proposta.ListaAcompanhamentoFinancDividaQuery;

public class ListaAcompanhamentoFinancDividaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaAcompanhamentoFinancDividaQuery query = new ListaAcompanhamentoFinancDividaQuery();
        query.count = false;
        query.csaCodigo = "267";
        query.tipoFiltro = "0";
        query.periodoIni = "01/01/2023";
        query.periodoFim = "01/01/2023";
        query.adeNumero = 1l;
        query.rseMatricula = "123";
        query.serCpf = "123";

        executarConsulta(query);
    }
}


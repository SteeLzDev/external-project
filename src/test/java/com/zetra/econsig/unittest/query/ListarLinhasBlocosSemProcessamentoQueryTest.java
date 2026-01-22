package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.folha.ListarLinhasBlocosSemProcessamentoQuery;

public class ListarLinhasBlocosSemProcessamentoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        ListarLinhasBlocosSemProcessamentoQuery query = new ListarLinhasBlocosSemProcessamentoQuery();
        query.tipoEntidade = "ORG";
        query.codigoEntidade = "123";
        query.semProcessamento = true;
        executarConsulta(query);
    }

    @Test
    public void test_02() throws com.zetra.econsig.exception.ZetraException {
        ListarLinhasBlocosSemProcessamentoQuery query = new ListarLinhasBlocosSemProcessamentoQuery();
        query.tipoEntidade = "ORG";
        query.codigoEntidade = "123";
        query.semProcessamento = false;
        executarConsulta(query);
    }

    @Test
    public void test_03() throws com.zetra.econsig.exception.ZetraException {
        ListarLinhasBlocosSemProcessamentoQuery query = new ListarLinhasBlocosSemProcessamentoQuery();
        query.tipoEntidade = "CSE";
        query.codigoEntidade = "1";
        query.semProcessamento = true;
        executarConsulta(query);
    }

    @Test
    public void test_04() throws com.zetra.econsig.exception.ZetraException {
        ListarLinhasBlocosSemProcessamentoQuery query = new ListarLinhasBlocosSemProcessamentoQuery();
        query.tipoEntidade = "CSE";
        query.codigoEntidade = "1";
        query.semProcessamento = false;
        executarConsulta(query);
    }
}

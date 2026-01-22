package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.leilao.ListaAcompanhamentoLeilaoSolicitacaoQuery;

public class ListaAcompanhamentoLeilaoSolicitacaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaAcompanhamentoLeilaoSolicitacaoQuery query = new ListaAcompanhamentoLeilaoSolicitacaoQuery();
        query.count = false;
        query.adeCodigo = "731A8D1EAZ564668A4Z0004423D9A1BD";
        query.csaCodigo = "267";
        query.tipoFiltro = "0";
        query.dataAberturaIni = "01/01/2023";
        query.dataAberturaFim = "01/01/2023";
        query.horasFimLeilao = "123";
        query.rseMatricula = "123";
        query.serCpf = "123";
        query.cidCodigo = "123";
        query.posCodigo = "123";
        query.rsePontuacao = "123";
        query.rseMargemLivre = "123";
        query.arrRisco = "123";
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);
        query.ordenacao = "ORD01;ASC";

        executarConsulta(query);
    }
}


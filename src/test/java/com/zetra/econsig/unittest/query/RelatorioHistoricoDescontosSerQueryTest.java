package com.zetra.econsig.unittest.query;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioHistoricoDescontosSerQuery;
import com.zetra.econsig.values.CodedValues;

public class RelatorioHistoricoDescontosSerQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        RelatorioHistoricoDescontosSerQuery query = new RelatorioHistoricoDescontosSerQuery();
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);
        query.serCpf = "123";
        query.rseMatricula = "123";
        query.adeNumeroLista = List.of(1l, 2l);
        query.csaCodigo = "267";
        query.corCodigo = null;
        query.cnvCodVerba = "123";
        query.sadCodigos = CodedValues.SAD_CODIGOS_ATIVOS;
        query.svcCodigo = List.of("050E8080808080808080808080808280", "1");

        executarConsulta(query);
    }
}



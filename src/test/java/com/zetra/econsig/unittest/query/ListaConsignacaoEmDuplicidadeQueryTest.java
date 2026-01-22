package com.zetra.econsig.unittest.query;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoEmDuplicidadeQuery;

public class ListaConsignacaoEmDuplicidadeQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConsignacaoEmDuplicidadeQuery query = new ListaConsignacaoEmDuplicidadeQuery();
        query.rseCodigo = "123";
        query.csaCodigo = "267";
        query.nseCodigo = "123";
        query.adeVlr = BigDecimal.ONE;
        query.adePrazo = 1;
        query.adeAnoMesIni = DateHelper.toSQLDate(DateHelper.getSystemDate());
        query.sadCodigos = java.util.List.of("1", "2");
        query.adeCodigosNaoConsiderar = java.util.List.of("1", "2");
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);

        executarConsulta(query);
    }
}


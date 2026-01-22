package com.zetra.econsig.unittest.query;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioGerencialQtdeSerPorFaixaMargemQuery;
import com.zetra.econsig.values.CodedValues;

public class RelatorioGerencialQtdeSerPorFaixaMargemQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        java.math.BigDecimal mediaMargem = BigDecimal.ONE;
        java.math.BigDecimal desvioMargem = BigDecimal.ONE;
        java.lang.Short incideMargem = CodedValues.INCIDE_MARGEM_SIM;

        RelatorioGerencialQtdeSerPorFaixaMargemQuery query = new RelatorioGerencialQtdeSerPorFaixaMargemQuery(mediaMargem, desvioMargem, incideMargem);

        executarConsulta(query);
    }
}


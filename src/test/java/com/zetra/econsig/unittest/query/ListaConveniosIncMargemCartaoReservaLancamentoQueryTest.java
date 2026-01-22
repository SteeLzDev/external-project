package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.convenio.ListaConveniosIncMargemCartaoReservaLancamentoQuery;
import com.zetra.econsig.values.CodedValues;

public class ListaConveniosIncMargemCartaoReservaLancamentoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConveniosIncMargemCartaoReservaLancamentoQuery query = new ListaConveniosIncMargemCartaoReservaLancamentoQuery();
        query.marCodigo = CodedValues.INCIDE_MARGEM_SIM_3;
        query.buscaCnvReservaCartao = true;

        executarConsulta(query);
    }

    @Test
    public void test_02() throws com.zetra.econsig.exception.ZetraException {

        ListaConveniosIncMargemCartaoReservaLancamentoQuery query = new ListaConveniosIncMargemCartaoReservaLancamentoQuery();
        query.marCodigo = CodedValues.INCIDE_MARGEM_SIM_3;

        executarConsulta(query);
    }
}


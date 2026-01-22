package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.definicaotaxajuros.PesquisarDefinicaoTaxaJurosQuery;
import com.zetra.econsig.values.CodedValues;

public class PesquisarDefinicaoTaxaJurosQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        PesquisarDefinicaoTaxaJurosQuery query = new PesquisarDefinicaoTaxaJurosQuery();
        query.csaCodigo = "267";
        query.orgCodigo = "751F8080808080808080808080809780";
        query.svcCodigo = "050E8080808080808080808080808280";
        query.funCodigo = "123";
        query.statusRegra = CodedValues.REGRA_NOVA_TABELA_INICIADA;
        query.data = "01/01/2023";
        query.offset = 1;
        query.count = false;
        query.pesquisaComDataVigenciaFim = true;

        executarConsulta(query);
    }
}


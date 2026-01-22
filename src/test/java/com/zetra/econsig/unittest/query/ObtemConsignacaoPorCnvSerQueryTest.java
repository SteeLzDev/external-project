package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.persistence.query.consignacao.ObtemConsignacaoPorCnvSerQuery;

public class ObtemConsignacaoPorCnvSerQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemConsignacaoPorCnvSerQuery query = new ObtemConsignacaoPorCnvSerQuery();
        query.codVerba = "123";
        query.cnvCodigo = "751F808080808080808080809090Z85";
        query.csaCodigo = "267";
        query.rseCodigo = "123";
        query.rseMatricula = "123";
        query.serCpf = "123";
        query.orgIdentificador = "123";
        query.svcIdentificador = "123";
        query.estIdentificador = "123";
        query.sadCodigos = java.util.List.of("1", "2");
        query.cnvAtivo = true;
        query.criterio = new CustomTransferObject();
        query.nseCodigo = "123";
        query.adeDataUltConciliacao = null;
        query.buscaContratoBeneficio = true;

        executarConsulta(query);
    }
}


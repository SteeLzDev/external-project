package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.CustomTransferObject;

import com.zetra.econsig.persistence.query.prazo.ListaPrazoCoeficienteEmprestimoQuery;

public class ListaPrazoCoeficienteEmprestimoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();

        ListaPrazoCoeficienteEmprestimoQuery query = new ListaPrazoCoeficienteEmprestimoQuery();
        query.setCriterios(criterios);

        query.orgCodigo = "751F8080808080808080808080809780";
        query.dia = 1;

        executarConsulta(query);
    }
}


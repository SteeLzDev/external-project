package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.CustomTransferObject;

import com.zetra.econsig.persistence.query.seguranca.ListarUsuariosLiberacaoMargemParaBloqueioQuery;

public class ListarUsuariosLiberacaoMargemParaBloqueioQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();

        ListarUsuariosLiberacaoMargemParaBloqueioQuery query = new ListarUsuariosLiberacaoMargemParaBloqueioQuery();
        query.setCriterios(criterios);

        query.csaCodigo = "267";

        executarConsulta(query);
    }
}


package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.sdp.permissionario.ListaPermissionarioByEnderecoQuery;

public class ListaPermissionarioByEnderecoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaPermissionarioByEnderecoQuery query = new ListaPermissionarioByEnderecoQuery();
        query.echCodigo = "123";
        query.prmComplEndereco = "123";
        query.csaCodigo = "267";

        executarConsulta(query);
    }
}


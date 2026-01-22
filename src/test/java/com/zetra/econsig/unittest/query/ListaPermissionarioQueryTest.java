package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.sdp.permissionario.ListaPermissionarioQuery;

public class ListaPermissionarioQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaPermissionarioQuery query = new ListaPermissionarioQuery();
        query.count = false;
        query.retornaPrmExcluido = true;
        query.prmCodigo = "123";
        query.rseCodigo = "123";
        query.rseMatricula = "123";
        query.serCpf = "123";
        query.serNome = "123";
        query.csaCodigo = "267";
        query.echCodigo = "123";
        query.echDescricao = "123";
        query.endereco = "123";
        query.posCodigo = "123";
        query.decDataRetroativa = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();

        executarConsulta(query);
    }
}


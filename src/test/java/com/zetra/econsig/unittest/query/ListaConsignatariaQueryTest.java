package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignataria.ListaConsignatariaQuery;

public class ListaConsignatariaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConsignatariaQuery query = new ListaConsignatariaQuery();
        query.csaIdentificador = "123";
        query.csaNome = "123";
        query.csaNomeAbrev = "123";
        query.csaCodigo = "267";
        query.csaAtivo = null;
        query.csaCodigos = java.util.List.of("1", "2");
        query.dataExpiracao = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.csaIdentificadorInterno = "123";
        query.csaProjetoInadimplencia = "123";
        query.ncaExibeSer = "123";
        query.ncaCodigo = "1";
        query.cnvCodVerba = "123";
        query.count = false;

        executarConsulta(query);
    }
}


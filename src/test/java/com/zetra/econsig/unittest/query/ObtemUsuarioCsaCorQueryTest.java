package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.usuario.ObtemUsuarioCsaCorQuery;

public class ObtemUsuarioCsaCorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemUsuarioCsaCorQuery query = new ObtemUsuarioCsaCorQuery();
        query.tipoEntidade = "CSA";
        query.codigoEntidade = "267";
        query.usuCodigo = "123";
        query.usuCpf = "123";
        query.validaServidor = true;

        executarConsulta(query);
    }
}


package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.funcao.ObtemFuncaoQuery;

public class ObtemFuncaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemFuncaoQuery query = new ObtemFuncaoQuery();
        query.funCodigo = "123";
        query.funExigeTmo = "123";
        query.funExigeSegundaSenha = "123";
        query.funRestritaNca = true;

        executarConsulta(query);
    }
}


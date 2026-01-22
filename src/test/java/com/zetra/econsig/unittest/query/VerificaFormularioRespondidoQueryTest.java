package com.zetra.econsig.unittest.query;

import com.zetra.econsig.persistence.query.formulariopesquisa.VerificaFormularioRespondidoQuery;
import org.junit.jupiter.api.Test;

public class VerificaFormularioRespondidoQueryTest extends AbstractQueryTest {
    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        VerificaFormularioRespondidoQuery query = new VerificaFormularioRespondidoQuery();
        query.usuCodigo = "123";

        executarConsulta(query);
    }
}

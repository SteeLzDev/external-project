package com.zetra.econsig.unittest.query;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.persistence.query.formulariopesquisa.BuscaFormularioPesquisaSemRespostaQuery;
import org.junit.jupiter.api.Test;

public class BuscaFormularioPesquisaSemRespostaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws ZetraException {
        BuscaFormularioPesquisaSemRespostaQuery query = new BuscaFormularioPesquisaSemRespostaQuery();
        query.usuCodigo = "123";

        executarConsulta(query);
    }
}

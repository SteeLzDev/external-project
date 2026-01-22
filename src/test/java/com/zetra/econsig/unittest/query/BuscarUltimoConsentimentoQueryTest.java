package com.zetra.econsig.unittest.query;
import com.zetra.econsig.persistence.query.consentimento.BuscarUltimoConsentimentoQuery;
import org.junit.jupiter.api.Test;

public class BuscarUltimoConsentimentoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        BuscarUltimoConsentimentoQuery query = new BuscarUltimoConsentimentoQuery();
            query.cpf = "111.111.111-11";
            query.tadCodigo = "1";   
        executarConsulta(query);
    }
}

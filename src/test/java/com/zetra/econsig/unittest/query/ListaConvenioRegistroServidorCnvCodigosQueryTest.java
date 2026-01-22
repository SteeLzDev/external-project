package com.zetra.econsig.unittest.query;

import java.util.ArrayList;
import java.util.List;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servidor.ListaConvenioRegistroServidorCnvCodigosQuery;

public class ListaConvenioRegistroServidorCnvCodigosQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        final List<String> cnvCodigos = new ArrayList<>();
        cnvCodigos.add("05108080808080808080808080808780");

        final ListaConvenioRegistroServidorCnvCodigosQuery query = new ListaConvenioRegistroServidorCnvCodigosQuery();
        query.cnvCodigos = cnvCodigos;
        query.responsavel = AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);

        executarConsulta(query);
    }
}


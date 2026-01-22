package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.servidor.ListaConvenioRegistroServidorEntidadeQuery;

public class ListaConvenioRegistroServidorEntidadeQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        final AcessoSistema responvelCsa = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);
        responvelCsa.setTipoEntidade(AcessoSistema.ENTIDADE_CSA);

        final ListaConvenioRegistroServidorEntidadeQuery query = new ListaConvenioRegistroServidorEntidadeQuery();
        query.csaCodigo = "267";
        query.responsavel = responvelCsa;

        executarConsulta(query);
    }

    @Test
    public void test_02() throws com.zetra.econsig.exception.ZetraException {
        final ListaConvenioRegistroServidorEntidadeQuery query = new ListaConvenioRegistroServidorEntidadeQuery();
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);
        executarConsulta(query);
    }
}


package com.zetra.econsig.unittest.query;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.query.anexo.ListaConsignacaoPendenciaAnexoQuery;

public class ListaConsignacaoPendenciaAnexoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        ListaConsignacaoPendenciaAnexoQuery query = new ListaConsignacaoPendenciaAnexoQuery(com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null));
        query.estCodigo = "751F8080808080808080808080809680";
        query.orgCodigo = "751F8080808080808080808080809780";
        query.csaCodigo = "267";
        query.corCodigo = null;
        query.svcCodigo = "050E8080808080808080808080808280";
        query.adeDataIni = DateHelper.getSystemDatetime();
        query.adeDataFim = DateHelper.getSystemDatetime();
        query.adeNumero = List.of(1l, 2l);
        query.serCpf = "123";
        query.rseMatricula = "123";
        query.pendenciaAnexo = true;
        query.count = false;

        executarConsulta(query);
    }
}



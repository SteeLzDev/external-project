package com.zetra.econsig.unittest.query;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioDescontosQuery;
import com.zetra.econsig.values.CodedValues;

public class RelatorioDescontosQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        RelatorioDescontosQuery query = new RelatorioDescontosQuery();
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);
        query.tipoEntidade = "ORG";
        query.periodo = "2023-01-01";
        query.dataIni = "2023-01-01 00:00:00";
        query.dataFim = "2023-01-01 23:59:59";
        query.orgCodigos = List.of("751F8080808080808080808080809780", "1");
        query.estCodigo = "751F8080808080808080808080809680";
        query.csaCodigo = "267";
        query.corCodigos = null;
        query.sboCodigo = "123";
        query.uniCodigo = "123";
        query.svcCodigo = List.of("050E8080808080808080808080808280", "1");
        query.sadCodigos = CodedValues.SAD_CODIGOS_ATIVOS;
        query.tocCodigos = CodedValues.TOC_CODIGOS_AUTORIZACAO;
        query.order = "CONSIGNATARIA";
        query.origemAdes = List.of(CodedValues.ORIGEM_ADE_NOVA);
        query.motivoTerminoAdes = List.of(CodedValues.TERMINO_ADE_CONCLUSAO);
        query.nseCodigos = List.of(CodedValues.NSE_EMPRESTIMO, CodedValues.NSE_SALARYPAY);
        query.tmoDecisaoJudicial = true;
        query.echCodigo = "123";
        query.plaCodigo = "123";
        query.cnvCodVerba = "123";

        executarConsulta(query);
    }
}



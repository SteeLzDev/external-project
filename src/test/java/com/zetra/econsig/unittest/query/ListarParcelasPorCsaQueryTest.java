package com.zetra.econsig.unittest.query;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.parcela.ListarParcelasPorCsaQuery;
import com.zetra.econsig.values.CodedValues;

public class ListarParcelasPorCsaQueryTest extends AbstractQueryTest {
    
    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        ListarParcelasPorCsaQuery query = new ListarParcelasPorCsaQuery();
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);
        query.csaCodigo = "267";
        query.adeNumero = null;
        query.adeIdentificador = null;
        query.spdCodigos = List.of(CodedValues.SPD_LIQUIDADAFOLHA);
        query.serCpf = "123";
        query.rseMatricula = "123";
        query.svcIdentificador = "001";
        query.prdDataDesconto = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.estCodigo = "01";
        query.orgCodigo = "10";
        query.cnvCodVerba = null;
        query.parcelaDescontoPeriodo = false;

        executarConsulta(query);
    }
    
    @Test
    public void test_02() throws com.zetra.econsig.exception.ZetraException {
        ListarParcelasPorCsaQuery query = new ListarParcelasPorCsaQuery();
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);
        query.csaCodigo = "267";
        query.adeNumero = null;
        query.adeIdentificador = null;
        query.spdCodigos = List.of(CodedValues.SPD_LIQUIDADAFOLHA);
        query.serCpf = "123";
        query.rseMatricula = "123";
        query.svcIdentificador = "001";
        query.prdDataDesconto = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.estCodigo = "01";
        query.orgCodigo = "10";
        query.cnvCodVerba = null;
        query.parcelaDescontoPeriodo = true;

        executarConsulta(query);
    }

}

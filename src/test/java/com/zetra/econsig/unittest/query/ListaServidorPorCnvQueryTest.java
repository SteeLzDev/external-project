package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servidor.ListaServidorPorCnvQuery;

public class ListaServidorPorCnvQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        ListaServidorPorCnvQuery query = new ListaServidorPorCnvQuery();
        query.codVerba = "123";
        query.csaCodigo = "267";
        query.tipo = "CSA";
        query.tipoCodigo = "267";
        query.estIdentificador = "123";
        query.orgIdentificador = "123";
        query.rseMatricula = "123";
        query.serCpf = "123";
        query.svcIdentificador = "1";
        query.nseCodigo = "1";
        query.cnvAtivo = true;
        query.serAtivo = true;
        query.inclusao = true;
        query.renegociacao = false;
        //query.ignorarRseCodigo = List.of("123", "456");
        query.numeroContratoBeneficio = "123";
        query.buscaBeneficiario = false;

        executarConsulta(query);
    }


    @Test
    public void test_02() throws com.zetra.econsig.exception.ZetraException {
        ListaServidorPorCnvQuery query = new ListaServidorPorCnvQuery();
        query.codVerba = "123";
        query.csaCodigo = "267";
        query.tipo = "COR";
        query.tipoCodigo = "001";
        query.estIdentificador = "123";
        query.orgIdentificador = "123";
        query.rseMatricula = "123";
        query.serCpf = "123";
        query.svcIdentificador = "1";
        query.nseCodigo = "1";
        query.cnvAtivo = true;
        query.serAtivo = true;
        query.inclusao = true;
        query.renegociacao = false;
        //query.ignorarRseCodigo = List.of("123", "456");
        query.numeroContratoBeneficio = "123";
        query.buscaBeneficiario = false;

        executarConsulta(query);
    }
}
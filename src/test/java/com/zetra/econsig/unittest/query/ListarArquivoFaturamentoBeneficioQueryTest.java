package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.beneficios.faturamento.ListarArquivoFaturamentoBeneficioQuery;

public class ListarArquivoFaturamentoBeneficioQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListarArquivoFaturamentoBeneficioQuery query = new ListarArquivoFaturamentoBeneficioQuery();
        query.fatCodigo = "123";
        query.rseMatricula = "123";
        query.cbeNumero = "123";
        query.bfcCpf = "123";
        query.afbCodigo = 1;
        query.contador = true;

        executarConsulta(query);
    }
}


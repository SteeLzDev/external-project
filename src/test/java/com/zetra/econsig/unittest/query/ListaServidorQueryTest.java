package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servidor.ListaServidorQuery;

public class ListaServidorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        com.zetra.econsig.helper.seguranca.AcessoSistema responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);

        ListaServidorQuery query = new ListaServidorQuery(responsavel);
        query.tipo = "123";
        query.codigo = "123";
        query.estCodigo = "751F8080808080808080808080809680";
        query.estIdentificador = "123";
        query.orgCodigo = "751F8080808080808080808080809780";
        query.orgIdentificador = "123";
        query.rseCodigo = "123";
        query.rseMatricula = "123";
        query.serCPF = "123";
        query.serNome = "123";
        query.serSobrenome = "123";
        query.serDataNascimento = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.numerContratoBeneficio = "123";
        query.count = false;
        query.pesquisaExata = true;
        query.rseSrsCodigo = java.util.List.of("1", "2");
        query.listaMatricula = java.util.List.of("1", "2");
        query.validaPermissionario = true;
        query.categoria = "123";
        query.temContrato = true;
        query.status = java.util.List.of("1", "2");
        query.operacao = "123";
        query.atributTemContratoNaoEspecificado = true;
        query.buscaBeneficiario = true;
        query.vrsCodigo = "123";

        executarConsulta(query);
    }
}


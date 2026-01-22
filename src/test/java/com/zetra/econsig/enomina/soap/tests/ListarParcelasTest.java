package com.zetra.econsig.enomina.soap.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.zetra.econsig.econsig.utils.LoginInfo;
import com.zetra.econsig.econsig.values.LoginValues;
import com.zetra.econsig.enomina.config.ENominaContextSpringConfiguration;
import com.zetra.econsig.enomina.soap.client.ListarParcelasClient;
import com.zetra.econsig.soap.ListarParcelasResponse;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class ListarParcelasTest extends ENominaContextSpringConfiguration {

    private final LoginInfo loginCsa = LoginValues.csa2;

    private final LoginInfo loginSuporte = LoginValues.cor1;

    private final String dataDesconto = "07/2021";

    private final String dataDescontoInvalido = "05/";

    private final String servicoCodigo = "001";

    private final Long adeNumero = (long) 61023;

    private final String adeIdentificador = "";

    private final String codEstabelecimento = "-1";

    private final String codOrgao = "-1";

    @Autowired
    private ListarParcelasClient listarParcelasClient;

    @Test
    public void listarParcelasComSucesso() {
        log.info("Listar parcelas com sucesso");

        final ListarParcelasResponse listarParcelasResponse = listarParcelasClient.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), dataDesconto);

        assertEquals("Operação realizada com sucesso.", listarParcelasResponse.getMensagem());
        assertEquals("000", listarParcelasResponse.getCodRetorno().getValue());
        assertTrue(listarParcelasResponse.isSucesso());
        assertTrue(listarParcelasResponse.getParcelaConsignacao().size() > 0);
    }

    @Test
    public void tentarListarParcelasSemDataDesconto() {
        log.info("Tentar listar parcelas sem o campo obrigátorio data desconto");

        final SoapFaultClientException thrown = assertThrows(SoapFaultClientException.class,
                                                             () -> listarParcelasClient.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), ""));

        assertEquals("Validation error", thrown.getMessage());
    }

    @Test
    public void tentarListarParcelasComDataDescontoInvalido() {
        log.info("Tentar listar parcelas com data desconto inválido");

        final ListarParcelasResponse listarParcelasResponse = listarParcelasClient.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), dataDescontoInvalido);

        assertEquals("O período informado é inválido.", listarParcelasResponse.getMensagem());
        assertEquals("409", listarParcelasResponse.getCodRetorno().getValue());
        assertFalse(listarParcelasResponse.isSucesso());
    }

    @Test
    public void tentarListarParcelasComUsuarioInvalido() {
        log.info("Tentar listar parcelas com usuário inválido");

        final ListarParcelasResponse listarParcelasResponse = listarParcelasClient.getResponse("csa1", loginCsa.getSenha(), dataDesconto);

        assertEquals("Usuário ou senha inválidos", listarParcelasResponse.getMensagem());
        assertEquals("358", listarParcelasResponse.getCodRetorno().getValue());
        assertFalse(listarParcelasResponse.isSucesso());
    }

    @Test
    public void tentarListarParcelasComUsuarioSemPermissao() {
        log.info("Tentar listar parcelas com usuário sem permissão");

        final ListarParcelasResponse listarParcelasResponse = listarParcelasClient.getResponse(loginSuporte.getLogin(), loginSuporte.getSenha(), dataDesconto);

        assertEquals("O usuário não tem permissão para executar esta operação", listarParcelasResponse.getMensagem());
        assertEquals("329", listarParcelasResponse.getCodRetorno().getValue());
        assertFalse(listarParcelasResponse.isSucesso());
    }

    @Test
    public void tentarListarParcelasComSenhaInvalida() {
        log.info("Tentar listar parcelas com senha inválida");

        final ListarParcelasResponse listarParcelasResponse = listarParcelasClient.getResponse(loginCsa.getLogin(), "abc123", dataDesconto);

        assertEquals("Usuário ou senha inválidos", listarParcelasResponse.getMensagem());
        assertEquals("358", listarParcelasResponse.getCodRetorno().getValue());
        assertFalse(listarParcelasResponse.isSucesso());
    }

    @Test
    public void listarParcelasPorAdeNumero() {
        log.info("listar parcelas por número ade");

        final ListarParcelasResponse listarParcelasResponse = listarParcelasClient.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), dataDesconto, null,
                                                                                               adeNumero, "", null, "", "", "", "", "", "");

        assertEquals("Operação realizada com sucesso.", listarParcelasResponse.getMensagem());
        assertEquals("000", listarParcelasResponse.getCodRetorno().getValue());
        assertTrue(listarParcelasResponse.isSucesso());
        assertTrue(listarParcelasResponse.getParcelaConsignacao().size() > 0);
        assertEquals(adeNumero, listarParcelasResponse.getParcelaConsignacao().get(0).getAdeNumero());
    }

    @Test
    public void listarParcelasPorAdeIdentificador() {
        log.info("Listar parcelas por adeIdentificador");

        final ListarParcelasResponse listarParcelasResponse = listarParcelasClient.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), dataDesconto, null,
                                                                                               null, adeIdentificador, null, "", "", "", "", "", "");

        assertEquals("Operação realizada com sucesso.", listarParcelasResponse.getMensagem());
        assertEquals("000", listarParcelasResponse.getCodRetorno().getValue());
        assertTrue(listarParcelasResponse.isSucesso());
        assertTrue(listarParcelasResponse.getParcelaConsignacao().size() > 0);
        assertEquals(adeIdentificador, listarParcelasResponse.getParcelaConsignacao().get(0).getAdeIdentificador());
    }

    //Aguardando correção da tarefa DESENV-21723
    /*@Test
    public void listarParcelasPorMatricula() {
        log.info("Listar parcelas por matricula");

        final ListarParcelasResponse listarParcelasResponse = listarParcelasClient.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), dataDesconto, null,
                                                                                               null, "", null, "", "", "", "", "", matricula);
        assertEquals("Operação realizada com sucesso.", listarParcelasResponse.getMensagem());
        assertEquals("000", listarParcelasResponse.getCodRetorno().getValue());
        assertTrue(listarParcelasResponse.isSucesso());
        assertTrue(listarParcelasResponse.getParcelaConsignacao().size() > 0);
        assertEquals(matricula, listarParcelasResponse.getParcelaConsignacao().get(0).getMatricula());
    }

    @Test
    public void listarParcelasPorCPF() {
        log.info("Listar parcelas por CPF");

        final ListarParcelasResponse listarParcelasResponse = listarParcelasClient.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), dataDesconto, null,
                                                                                               null, "", null, "", "", "", "", cpf, "");
        assertEquals("Operação realizada com sucesso.", listarParcelasResponse.getMensagem());
        assertEquals("000", listarParcelasResponse.getCodRetorno().getValue());
        assertTrue(listarParcelasResponse.isSucesso());
        assertTrue(listarParcelasResponse.getParcelaConsignacao().size() > 0);
        assertEquals(cpf, listarParcelasResponse.getParcelaConsignacao().get(0).getCpf());
    }*/

    @Test
    public void listarParcelasPorServicoCodigo() {
        log.info("Listar parcelas por código servico");

        final ListarParcelasResponse listarParcelasResponse = listarParcelasClient.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), dataDesconto, null,
                                                                                               null, "", null, "", "", servicoCodigo, "", "", "");

        assertEquals("Operação realizada com sucesso.", listarParcelasResponse.getMensagem());
        assertEquals("000", listarParcelasResponse.getCodRetorno().getValue());
        assertTrue(listarParcelasResponse.isSucesso());
        assertTrue(listarParcelasResponse.getParcelaConsignacao().size() > 0);
        assertEquals(servicoCodigo, listarParcelasResponse.getParcelaConsignacao().get(0).getServicoCodigo());
    }

    @Test
    public void listarParcelasPorEstabelecimentoInexistente() {
        log.info("Listar parcelas por estabelecimento inexistente");

        final ListarParcelasResponse listarParcelasResponse = listarParcelasClient.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), dataDesconto, null,
                                                                                               null, "", null, codEstabelecimento, "", "", "", "", "");

        assertEquals("Estabelecimento não encontrado.", listarParcelasResponse.getMensagem());
        assertEquals("290", listarParcelasResponse.getCodRetorno().getValue());
        assertFalse(listarParcelasResponse.isSucesso());
    }

    @Test
    public void listarParcelasPorOrgaoInexistente() {
        log.info("Listar parcelas por órgão inexistente");

        final ListarParcelasResponse listarParcelasResponse = listarParcelasClient.getResponse(loginCsa.getLogin(), loginCsa.getSenha(), dataDesconto, null,
                                                                                               null, "", null, "", codOrgao, "", "", "", "");

        assertEquals("Órgão não encontrado.", listarParcelasResponse.getMensagem());
        assertEquals("291", listarParcelasResponse.getCodRetorno().getValue());
        assertFalse(listarParcelasResponse.isSucesso());
    }
}

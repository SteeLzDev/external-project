package com.zetra.econsig.webservice.rest.request;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * <p>Title: ServidorRestResponse</p>
 * <p>Description: Rest response com dados do servidor.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class ServidorRestResponse {
    public String nome;
    public String dataNascimento;
    public String nomeMae;
    public String iban;
    public String sexo;
    public String nroIdentidade;
    public String dataIdentidade;
    public String nacionalidade;
    public String naturalidade;
    public String salario;
    public String dataAdmissao;
    public String cpf;
    public String telefone;
    public String celular;
    public String email;
    public String token;
    public String usuCodigo;
    public String orgIdentificador;
    public String orgCodigo;
    public String orgNome;
    public String estIdentificador;
    public String estCodigo;
    public String estNome;
    public String imagem;
    public String endereco;
    public String numero;
    public String complemento;
    public String bairro;
    public String cidade;
    public String cep;
    public String uf;
    public String municipioLotacao;
    public String rseCodigo;
    public String rseMatricula;
    public String rseTipo;
    public String srsDescricao;
    public String srsCodigo;
    public Boolean exibeBotaoIniciarLeilao;
    public Boolean podeSimular;
    public Boolean podeSolicitar;
    public Boolean exigeCadTelPrimeiroAcesso;
    public Boolean exigeCadEmailPrimeiroAcesso;
    public Boolean simulaAgrupadoPorNatureza;
    public String id;
    public String dataValidacaoEmail;
    public Boolean permiteAlterarEmail;
    public String dataIdentificacaoPessoal;
    public String statusUsuario;
    public Boolean permiteSobreporSenha;
    public String serCodigo;
    public String dataUltimoAcesso;
    public Boolean integracaoEconsigSalaryPay;
    public Boolean validaKycFacesWeb;
    public Boolean validaKycBankAsService;
    public Boolean integraSalaryPayCielo;
    public Boolean apuracaoAutomaticaCadastroFacesWeb;
    public String rseMotivoFaltaMargem;
    public String usuDataExpSenha;
    public Boolean possuiFormularioPesquisaSemResposta;

    public List<String> permissoes;
    public Map<String, String> nseCodigos;

    public ServidorRestResponse() {}
}

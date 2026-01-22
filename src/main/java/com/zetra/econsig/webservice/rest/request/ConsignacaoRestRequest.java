package com.zetra.econsig.webservice.rest.request;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * <p>Title: ResponseRestRequest</p>
 * <p>Description: Requisição Rest de mensagem de retorno padrão.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class ConsignacaoRestRequest {

    public Integer adePrazo;
    
    public String adeCodigo;

    public Integer adeCarencia;

    public String cnvCodVerba;

    public String svcIdentificador;

    public String csaIdentificador;

    public String corIdentificador;

    public String csaCodigo;

    public Integer rsePrazo;

    public BigDecimal adeVlr;

    public BigDecimal adeVlrTac;

    public BigDecimal adeVlrIof;

    public BigDecimal adeVlrLiquido;

    public BigDecimal adeVlrMensVinc;

    public BigDecimal valorLiberado;

    public BigDecimal adeTaxaJuros;

    public String nseCodigo;

    public String svcCodigo;

    public String tipoOperacao;

    public String cseCodigo;

    public String cnvCodigo;

    public String corCodigo;

    public String adeIndice;

    public String adeIdentificador;

    //Dados da conta
    public String numBanco;

    public String numAgencia;

    public String numConta;

	// Dados do servidor

    public String serNome;

    public String serSexo;

    public String serCodigo;

    public String serNroIdt;

    public Date serDataIdt;

    public String serCelular;

    public String serNacionalidade;

    public String serCidNasc;

    public String serUfNasc;

    public String serCpf;

    public String rseCodigo;

    public String serEndereco;

    public String serNro;

    public String serComplemento;

    public String serBairro;

    public String serCidade;

    public String serUf;

    public String serCep;

    public String serTelefone;

    public String serTelefoneSolicitacao;

    public String rseMunicipioLotacao;

    public String serSenha;

    public String isReservaCartao;

    public String serDataNasc;

    public String periodo;

    public boolean iniciarLeilaoReverso = false;

    public boolean simulacaoPorAdeVlr = false;

    public boolean otpValidado = false;

    public String cidCod;

    public String telefoneLeilao;

    public String emailLeilao;

    public String otp;

    //Dados Registro Servidor

    public String iban;

    public BigDecimal rseSalario;

    public Timestamp rseDataAdmissao;

    public Boolean confirmacaoLeitura;

    //e-mail solicitação
    public String TDA_40;

    //obs solicitação
    public String TDA_41;

    //nome do estabelecimento SalaryPay
    public String TDA_48;

    //informações do estabelecimento SalaryPay
    public String TDA_49;

    //Taxa cobrada do PIX SalaryPay
    public String TDA_90;

    //Valor do PIX Creditado SalaryPay
    public String TDA_91;

    public HashMap<String, String>[] anexos;

    public List<String> adeCodigosRenegociacao;

    public String cftCodigo;

    public String dtjCodigo;

    public String rseBancoSal;

    public String rseAgenciaSal;

    public String rseContaSal;

    public String rseBancoSal2;

    public String rseAgenciaSal2;

    public String rseContaSal2;
    
    public Boolean isMobile;

}

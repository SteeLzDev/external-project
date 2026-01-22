package com.zetra.econsig.webservice.rest.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConsultarSalarioResponse {

    public String matricula;
    public String nome;
    public String cpf;
    public String consignanteNome;
    public String consignanteCnpj;
    public String municipioLotacao;
    public String cargo;
    public BigDecimal salario;
    public BigDecimal proventos;
    public BigDecimal compulsorios;
    public BigDecimal facultativos;
    public BigDecimal emprestimos;
    public BigDecimal outros;
    public String dataUltSalario;
    public String dataDesligamento;
    public String dataAdmissao;
    public String dataPagamento;
    public String anoMes;
    public boolean salarioCalculado;
    public MargemSalarioResponse margemIncideEmprestimo;
}
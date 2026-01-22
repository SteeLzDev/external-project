package com.zetra.econsig.webclient.creditotrabalhador;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmprestimoCreditoTrabalhadorPojo
{
    @JsonProperty("ifConcessora.codigo")
    public Integer ifConcessoraCodigo;

    @JsonProperty("ifConcessora.descricao")
    public String ifConcessoraDescricao;

    @JsonProperty("contrato")
    public String contrato;

    @JsonProperty("cpf")
    public String cpf;

    @JsonProperty("matricula")
    public String matricula;

    @JsonProperty("inscricaoEmpregador.codigo")
    public Integer inscricaoEmpregadorCodigo;

    @JsonProperty("inscricaoEmpregador.descricao")
    public String inscricaoEmpregadorDescricao;

    @JsonProperty("numeroInscricaoEmpregador")
    public String numeroInscricaoEmpregador;

    @JsonProperty("inscricaoEstabelecimento.codigo")
    public Integer inscricaoEstabelecimentoCodigo;

    @JsonProperty("inscricaoEstabelecimento.descricao")
    public String inscricaoEstabelecimentoDescricao;

    @JsonProperty("numeroInscricaoEstabelecimento")
    public String numeroInscricaoEstabelecimento;

    @JsonProperty("nomeTrabalhador")
    public String nomeTrabalhador;

    @JsonProperty("nomeEmpregador")
    public String nomeEmpregador;

    @JsonProperty("dataInicioContrato")
    public String dataInicioContrato;

    @JsonProperty("dataFimContrato")
    public String dataFimContrato;

    @JsonProperty("competenciaInicioDesconto")
    public String competenciaInicioDesconto;

    @JsonProperty("competenciaFimDesconto")
    public String competenciaFimDesconto;

    @JsonProperty("totalParcelas")
    public Integer totalParcelas;

    @JsonProperty("valorParcela")
    public BigDecimal valorParcela;

    @JsonProperty("valorEmprestimo")
    public BigDecimal valorEmprestimo;

    @JsonProperty("valorLiberado")
    public BigDecimal valorLiberado;

    @JsonProperty("categoriaTrabalhador.codigo")
    public Integer categoriaTrabalhadorCodigo;

    @JsonProperty("categoriaTrabalhador.descricao")
    public String categoriaTrabalhadorDescricao;

    @JsonProperty("qtdPagamentos")
    public Integer qtdPagamentos;

    @JsonProperty("qtdEscrituracoes")
    public Integer qtdEscrituracoes;

    @JsonProperty("competencia")
    public String competencia;

    @JsonProperty("dataAdmissao")
    public String dataAdmissao;

    public Date getDataInicioContratoDate() throws ParseException {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.parse(this.dataInicioContrato);
    }

    public Date getDataFimContratoDate() throws ParseException {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.parse(this.dataFimContrato);
    }

    public Date getCompetenciaInicioDescontoDate() throws ParseException {
        this.competenciaInicioDesconto = "01/" + this.competenciaInicioDesconto;
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.parse(this.competenciaInicioDesconto);
    }

    public Date getCompetenciaFimDescontoDate() throws ParseException {
        this.competenciaFimDesconto = "01/" + this.competenciaFimDesconto;
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.parse(this.competenciaFimDesconto);
    }

    public Date getCompetenciaDate() throws ParseException {
        this.competencia = "01/" + this.competencia;
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.parse(this.competencia);
    }

    public Date getDataAdmissaoDate() throws ParseException {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.parse(this.dataAdmissao);
    }


}




package com.zetra.econsig.persistence.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "tb_arquivo_retorno")
@IdClass(ArquivoRetornoId.class)
public class ArquivoRetorno implements java.io.Serializable {

    private static final long serialVersionUID = 2L;

    //--- ENTITY PRIMARY KEY
    @Id
    @Column(name = "nome_arquivo", nullable = false, length = 100)
    private String nomeArquivo;

    @Id
    @Column(name = "id_linha", nullable = false)
    private Integer idLinha;

    @Column(name = "est_identificador", length = 40)
    private String estIdentificador;

    @Column(name = "org_identificador", length = 40)
    private String orgIdentificador;

    @Column(name = "csa_identificador", length = 40)
    private String csaIdentificador;

    @Column(name = "svc_identificador", length = 40)
    private String svcIdentificador;

    @Column(name = "pos_identificador", length = 40)
    private String posIdentificador;

    @Column(name = "cnv_cod_verba", nullable = false, length = 32)
    private String cnvCodVerba;

    @Temporal(TemporalType.DATE)
    @Column(name = "ano_mes_desconto")
    private Date anoMesDesconto;

    @Column(name = "prd_vlr_realizado", nullable = false)
    private BigDecimal prdVlrRealizado;

    @Temporal(TemporalType.DATE)
    @Column(name = "prd_data_realizado")
    private Date prdDataRealizado;

    @Column(name = "ade_indice", length = 32)
    private String adeIndice;

    @Column(name = "ade_cod_reg", length = 1)
    private Character adeCodReg;

    @Temporal(TemporalType.DATE)
    @Column(name = "ade_ano_mes_ini")
    private Date adeAnoMesIni;

    @Temporal(TemporalType.DATE)
    @Column(name = "ade_ano_mes_fim")
    private Date adeAnoMesFim;

    @Column(name = "ade_prd_pagas")
    private Integer adePrdPagas;

    @Column(name = "ade_prazo")
    private Integer adePrazo;

    @Column(name = "ade_carencia")
    private Short adeCarencia;

    @Column(name = "ocp_obs", nullable = false, length = 65535)
    private String ocpObs;

    @Column(name = "spd_codigo", nullable = false, length = 32)
    private String spdCodigo;

    @Column(name = "quitacao", nullable = false, length = 1)
    private String quitacao;

    @Column(name = "tipo_envio", nullable = false, length = 1)
    private String tipoEnvio;

    @Column(name = "rse_matricula", nullable = false, length = 35)
    private String rseMatricula;

    @Column(name = "ser_nome", length = 255)
    private String serNome;

    @Column(name = "ser_cpf", length = 19)
    private String serCpf;

    @Column(name = "mapeada", nullable = false, length = 1)
    private String mapeada;

    @Column(name = "processada", nullable = false, length = 1)
    private String processada;

    @Column(name = "pode_pagar_consolidacao_exata", nullable = false, length = 1)
    private String podePagarConsolidacaoExata;

    @Column(name = "linha", nullable = false, length = 65535)
    private String linha;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ade_data")
    private Date adeData;

    @Column(name = "tde_codigo", length = 32)
    private String tdeCodigo;

    @Column(name = "csa_codigo", length = 32)
    private String csaCodigo;

    @Column(name = "ade_numero")
    private Long adeNumero;

    @Column(name = "art_ferias", length = 1)
    private Short artFerias;

    public ArquivoRetorno() {
        super();
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public Integer getIdLinha() {
        return idLinha;
    }

    public void setIdLinha(Integer idLinha) {
        this.idLinha = idLinha;
    }

    public String getEstIdentificador() {
        return estIdentificador;
    }

    public void setEstIdentificador(String estIdentificador) {
        this.estIdentificador = estIdentificador;
    }

    public String getOrgIdentificador() {
        return orgIdentificador;
    }

    public void setOrgIdentificador(String orgIdentificador) {
        this.orgIdentificador = orgIdentificador;
    }

    public String getCsaIdentificador() {
        return csaIdentificador;
    }

    public void setCsaIdentificador(String csaIdentificador) {
        this.csaIdentificador = csaIdentificador;
    }

    public String getSvcIdentificador() {
        return svcIdentificador;
    }

    public void setSvcIdentificador(String svcIdentificador) {
        this.svcIdentificador = svcIdentificador;
    }

    public String getPosIdentificador() {
        return posIdentificador;
    }

    public void setPosIdentificador(String posIdentificador) {
        this.posIdentificador = posIdentificador;
    }

    public String getCnvCodVerba() {
        return cnvCodVerba;
    }

    public void setCnvCodVerba(String cnvCodVerba) {
        this.cnvCodVerba = cnvCodVerba;
    }

    public Date getAnoMesDesconto() {
        return anoMesDesconto;
    }

    public void setAnoMesDesconto(Date anoMesDesconto) {
        this.anoMesDesconto = anoMesDesconto;
    }

    public BigDecimal getPrdVlrRealizado() {
        return prdVlrRealizado;
    }

    public void setPrdVlrRealizado(BigDecimal prdVlrRealizado) {
        this.prdVlrRealizado = prdVlrRealizado;
    }

    public Date getPrdDataRealizado() {
        return prdDataRealizado;
    }

    public void setPrdDataRealizado(Date prdDataRealizado) {
        this.prdDataRealizado = prdDataRealizado;
    }

    public String getAdeIndice() {
        return adeIndice;
    }

    public void setAdeIndice(String adeIndice) {
        this.adeIndice = adeIndice;
    }

    public Character getAdeCodReg() {
        return adeCodReg;
    }

    public void setAdeCodReg(Character adeCodReg) {
        this.adeCodReg = adeCodReg;
    }

    public Date getAdeAnoMesIni() {
        return adeAnoMesIni;
    }

    public void setAdeAnoMesIni(Date adeAnoMesIni) {
        this.adeAnoMesIni = adeAnoMesIni;
    }

    public Date getAdeAnoMesFim() {
        return adeAnoMesFim;
    }

    public void setAdeAnoMesFim(Date adeAnoMesFim) {
        this.adeAnoMesFim = adeAnoMesFim;
    }

    public Integer getAdePrdPagas() {
        return adePrdPagas;
    }

    public void setAdePrdPagas(Integer adePrdPagas) {
        this.adePrdPagas = adePrdPagas;
    }

    public Integer getAdePrazo() {
        return adePrazo;
    }

    public void setAdePrazo(Integer adePrazo) {
        this.adePrazo = adePrazo;
    }

    public Short getAdeCarencia() {
        return adeCarencia;
    }

    public void setAdeCarencia(Short adeCarencia) {
        this.adeCarencia = adeCarencia;
    }

    public String getOcpObs() {
        return ocpObs;
    }

    public void setOcpObs(String ocpObs) {
        this.ocpObs = ocpObs;
    }

    public String getSpdCodigo() {
        return spdCodigo;
    }

    public void setSpdCodigo(String spdCodigo) {
        this.spdCodigo = spdCodigo;
    }

    public String getQuitacao() {
        return quitacao;
    }

    public void setQuitacao(String quitacao) {
        this.quitacao = quitacao;
    }

    public String getTipoEnvio() {
        return tipoEnvio;
    }

    public void setTipoEnvio(String tipoEnvio) {
        this.tipoEnvio = tipoEnvio;
    }

    public String getRseMatricula() {
        return rseMatricula;
    }

    public void setRseMatricula(String rseMatricula) {
        this.rseMatricula = rseMatricula;
    }

    public String getSerNome() {
        return serNome;
    }

    public void setSerNome(String serNome) {
        this.serNome = serNome;
    }

    public String getSerCpf() {
        return serCpf;
    }

    public void setSerCpf(String serCpf) {
        this.serCpf = serCpf;
    }

    public String getMapeada() {
        return mapeada;
    }

    public void setMapeada(String mapeada) {
        this.mapeada = mapeada;
    }

    public String getProcessada() {
        return processada;
    }

    public void setProcessada(String processada) {
        this.processada = processada;
    }

    public String getPodePagarConsolidacaoExata() {
        return podePagarConsolidacaoExata;
    }

    public void setPodePagarConsolidacaoExata(String podePagarConsolidacaoExata) {
        this.podePagarConsolidacaoExata = podePagarConsolidacaoExata;
    }

    public String getLinha() {
        return linha;
    }

    public void setLinha(String linha) {
        this.linha = linha;
    }

    public Date getAdeData() {
        return adeData;
    }

    public void setAdeData(Date adeData) {
        this.adeData = adeData;
    }

    public String getTdeCodigo() {
        return tdeCodigo;
    }

    public void setTdeCodigo(String tdeCodigo) {
        this.tdeCodigo = tdeCodigo;
    }

    public String getCsaCodigo() {
        return csaCodigo;
    }

    public void setCsaCodigo(String csaCodigo) {
        this.csaCodigo = csaCodigo;
    }

    public Long getAdeNumero() {
        return adeNumero;
    }

    public void setAdeNumero(Long adeNumero) {
        this.adeNumero = adeNumero;
    }

    public Short getArtFerias() {
        return artFerias;
    }

    public void setArtFerias(Short artFerias) {
        this.artFerias = artFerias;
    }

    @Override
    public int hashCode() {
        return Objects.hash(adeAnoMesFim, adeAnoMesIni, adeCarencia, adeCodReg, adeData, adeIndice, adeNumero, adePrazo, adePrdPagas, anoMesDesconto, artFerias, cnvCodVerba, csaCodigo, csaIdentificador, estIdentificador, idLinha, linha, mapeada, nomeArquivo, ocpObs, orgIdentificador, podePagarConsolidacaoExata, posIdentificador, prdDataRealizado, prdVlrRealizado, processada, quitacao, rseMatricula, serCpf, serNome, spdCodigo, svcIdentificador, tdeCodigo, tipoEnvio);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ArquivoRetorno other = (ArquivoRetorno) obj;
        return Objects.equals(adeAnoMesFim, other.adeAnoMesFim) && Objects.equals(adeAnoMesIni, other.adeAnoMesIni) && Objects.equals(adeCarencia, other.adeCarencia) && Objects.equals(adeCodReg, other.adeCodReg) && Objects.equals(adeData, other.adeData) && Objects.equals(adeIndice, other.adeIndice) && Objects.equals(adeNumero, other.adeNumero) && Objects.equals(adePrazo, other.adePrazo) && Objects.equals(adePrdPagas, other.adePrdPagas) && Objects.equals(anoMesDesconto, other.anoMesDesconto) && Objects.equals(artFerias, other.artFerias) && Objects.equals(cnvCodVerba, other.cnvCodVerba)
                && Objects.equals(csaCodigo, other.csaCodigo) && Objects.equals(csaIdentificador, other.csaIdentificador) && Objects.equals(estIdentificador, other.estIdentificador) && Objects.equals(idLinha, other.idLinha) && Objects.equals(linha, other.linha) && Objects.equals(mapeada, other.mapeada) && Objects.equals(nomeArquivo, other.nomeArquivo) && Objects.equals(ocpObs, other.ocpObs) && Objects.equals(orgIdentificador, other.orgIdentificador) && Objects.equals(podePagarConsolidacaoExata, other.podePagarConsolidacaoExata) && Objects.equals(posIdentificador, other.posIdentificador)
                && Objects.equals(prdDataRealizado, other.prdDataRealizado) && Objects.equals(prdVlrRealizado, other.prdVlrRealizado) && Objects.equals(processada, other.processada) && Objects.equals(quitacao, other.quitacao) && Objects.equals(rseMatricula, other.rseMatricula) && Objects.equals(serCpf, other.serCpf) && Objects.equals(serNome, other.serNome) && Objects.equals(spdCodigo, other.spdCodigo) && Objects.equals(svcIdentificador, other.svcIdentificador) && Objects.equals(tdeCodigo, other.tdeCodigo) && Objects.equals(tipoEnvio, other.tipoEnvio);
    }

    @Override
    public String toString() {
        return "ArquivoRetorno [nomeArquivo=" + nomeArquivo + ", idLinha=" + idLinha + ", estIdentificador=" + estIdentificador + ", orgIdentificador=" + orgIdentificador + ", csaIdentificador=" + csaIdentificador + ", svcIdentificador=" + svcIdentificador + ", posIdentificador=" + posIdentificador + ", cnvCodVerba=" + cnvCodVerba + ", anoMesDesconto=" + anoMesDesconto + ", prdVlrRealizado=" + prdVlrRealizado + ", prdDataRealizado=" + prdDataRealizado + ", adeIndice=" + adeIndice + ", adeCodReg=" + adeCodReg + ", adeAnoMesIni=" + adeAnoMesIni + ", adeAnoMesFim=" + adeAnoMesFim
                + ", adePrdPagas=" + adePrdPagas + ", adePrazo=" + adePrazo + ", adeCarencia=" + adeCarencia + ", ocpObs=" + ocpObs + ", spdCodigo=" + spdCodigo + ", quitacao=" + quitacao + ", tipoEnvio=" + tipoEnvio + ", rseMatricula=" + rseMatricula + ", serNome=" + serNome + ", serCpf=" + serCpf + ", mapeada=" + mapeada + ", processada=" + processada + ", podePagarConsolidacaoExata=" + podePagarConsolidacaoExata + ", linha=" + linha + ", adeData=" + adeData + ", tdeCodigo=" + tdeCodigo + ", csaCodigo=" + csaCodigo + ", adeNumero=" + adeNumero + ", artFerias=" + artFerias + "]";
    }



}

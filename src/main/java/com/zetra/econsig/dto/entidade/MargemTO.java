package com.zetra.econsig.dto.entidade;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: MargemTO</p>
 * <p>Description: Transfer Object da tabela de margem</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@JsonIgnoreProperties({ "atributos" })
public class MargemTO extends CustomTransferObject {
    /**
     *
     */
    private static final long serialVersionUID = 15L;

    private boolean temMargemDisponivel = true;
    private boolean servidorBloqueado;
    private String observacao;
    private BigDecimal margemLimite;

    public MargemTO() {
        super();
    }

    public MargemTO(Short marCodigo) {
        this();
        setAttribute(Columns.MAR_CODIGO, marCodigo);
    }

    public MargemTO(Short marCodigo, String marDescricao) {
        this();
        setAttribute(Columns.MAR_CODIGO, marCodigo);
        setAttribute(Columns.MAR_DESCRICAO, marDescricao);
    }

    public MargemTO(MargemTO other) {
        this();
        setAtributos(other.getAtributos());
    }

    // Getter
    public Short getMarCodigo() {
        return (Short) getAttribute(Columns.MAR_CODIGO);
    }

    public Short getMarCodAdequacao() {
        return (Short) getAttribute(Columns.MAR_COD_ADEQUACAO);
    }

    public Short getMarCodigoPai() {
        return (Short) getAttribute(Columns.MAR_CODIGO_PAI);
    }

    public String getMarDescricao() {
        return (String) getAttribute(Columns.MAR_DESCRICAO);
    }

    public Short getMarSequencia() {
        return (Short) getAttribute(Columns.MAR_SEQUENCIA);
    }

    public BigDecimal getMarPorcentagem() {
        return (BigDecimal) getAttribute(Columns.MAR_PORCENTAGEM);
    }

    public Character getMarExibeCse() {
        return (Character) getAttribute(Columns.MAR_EXIBE_CSE);
    }

    public Character getMarExibeOrg() {
        return (Character) getAttribute(Columns.MAR_EXIBE_ORG);
    }

    public Character getMarExibeSer() {
        return (Character) getAttribute(Columns.MAR_EXIBE_SER);
    }

    public Character getMarExibeSup() {
        return (Character) getAttribute(Columns.MAR_EXIBE_SUP);
    }

    public Character getMarExibeCsa() {
        return (Character) getAttribute(Columns.MAR_EXIBE_CSA);
    }

    public Character getMarExibeCor() {
        return (Character) getAttribute(Columns.MAR_EXIBE_COR);
    }

    public Character getMarExibeAltMultContratos() {
        return (Character) getAttribute(Columns.MAR_EXIBE_ALT_MULT_CONTRATOS);
    }

    public Character getMarTipoVlr() {
        return (Character) getAttribute(Columns.MAR_TIPO_VLR);
    }

    public BigDecimal getMrsMargem() {
        return (BigDecimal) getAttribute(Columns.MRS_MARGEM);
    }

    public BigDecimal getMrsMargemRest() {
        return (BigDecimal) getAttribute(Columns.MRS_MARGEM_REST);
    }

    public BigDecimal getMrsMargemUsada() {
        return (BigDecimal) getAttribute(Columns.MRS_MARGEM_USADA);
    }

    public BigDecimal getMrsMediaMargem () {
        return (BigDecimal) getAttribute(Columns.MRS_MEDIA_MARGEM);
    }

    @JsonIgnore
    public BigDecimal getVariacaoMediaMargem() {
        if (!TextHelper.isNull(getMrsMediaMargem())) {
            return getMrsMargem().multiply(BigDecimal.valueOf(100.00)).divide(getMrsMediaMargem(), RoundingMode.HALF_UP).subtract(BigDecimal.valueOf(100.00));
        }

        return BigDecimal.ZERO;
    }

    public boolean temMargemDisponivel() {
        return temMargemDisponivel;
    }

    public boolean isServidorBloqueado() {
        return servidorBloqueado;
    }

    public String getObservacao() {
        return observacao;
    }

    public BigDecimal getMargemLimite() {
        return margemLimite;
    }

    // Setter
    public void setMarCodigo(Short marCodigo) {
        setAttribute(Columns.MAR_CODIGO, marCodigo);
    }

    public void setMarCodigoPai(Short marCodigoPai) {
        setAttribute(Columns.MAR_CODIGO_PAI, marCodigoPai);
    }

    public void setMarDescricao(String marDescricao) {
        setAttribute(Columns.MAR_DESCRICAO, marDescricao);
    }

    public void setMarSequencia(Short marSequencia) {
        setAttribute(Columns.MAR_SEQUENCIA, marSequencia);
    }

    public void setMarPorcentagem(BigDecimal marPorcentagem) {
        setAttribute(Columns.MAR_PORCENTAGEM, marPorcentagem);
    }

    public void setMarExibeCse(Character marExibeCse) {
        setAttribute(Columns.MAR_EXIBE_CSE, marExibeCse);
    }

    public void setMarExibeOrg(Character marExibeOrg) {
        setAttribute(Columns.MAR_EXIBE_ORG, marExibeOrg);
    }

    public void setMarExibeSer(Character marExibeSer) {
        setAttribute(Columns.MAR_EXIBE_SER, marExibeSer);
    }

    public void setMarExibeSup(Character marExibeSup) {
        setAttribute(Columns.MAR_EXIBE_SUP, marExibeSup);
    }

    public void setMarExibeCsa(Character marExibeCsa) {
        setAttribute(Columns.MAR_EXIBE_CSA, marExibeCsa);
    }

    public void setMarExibeCor(Character marExibeCor) {
        setAttribute(Columns.MAR_EXIBE_COR, marExibeCor);
    }

    public void setMarExibeAltMultContratos(String marExibeAltMultContratos) {
        setAttribute(Columns.MAR_EXIBE_ALT_MULT_CONTRATOS, marExibeAltMultContratos);
    }

    public void setMarTipoVlr(Character marTipoVlr) {
        setAttribute(Columns.MAR_TIPO_VLR, marTipoVlr);
    }

    public void setMrsMargem(BigDecimal mrsMargem) {
        setAttribute(Columns.MRS_MARGEM, mrsMargem);
    }

    public void setMrsMargemRest(BigDecimal mrsMargemRest) {
        setAttribute(Columns.MRS_MARGEM_REST, mrsMargemRest);
    }

    public void setMrsMargemUsada(BigDecimal mrsMargemUsada) {
        setAttribute(Columns.MRS_MARGEM_USADA, mrsMargemUsada);
    }

    public void setMrsMediaMargem(BigDecimal mrsMediaMargem) {
        setAttribute(Columns.MRS_MEDIA_MARGEM, mrsMediaMargem);
    }

    public void setTemMargemDisponivel(boolean temMargemDisponivel) {
        this.temMargemDisponivel = temMargemDisponivel;
    }

    public void setServidorBloqueado(boolean servidorBloqueado) {
        this.servidorBloqueado = servidorBloqueado;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public void addObservacao(String observacao) {
        this.observacao = (!TextHelper.isNull(this.observacao) && !TextHelper.isNull(observacao) ? this.observacao + " " + observacao : observacao);
    }

    public void setMargemLimite(BigDecimal margemLimite) {
        this.margemLimite = margemLimite;
    }
}
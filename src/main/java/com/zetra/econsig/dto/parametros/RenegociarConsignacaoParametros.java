package com.zetra.econsig.dto.parametros;

/**
 * <p>Title: RenegociarConsignacaoParametros</p>
 * <p>Description: Parâmetros necessários na renegociação de contrato.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RenegociarConsignacaoParametros extends ReservarMargemParametros {

    private String tipo;
    private Boolean compraContrato;
    private String serEmail;
    private String ppdCodigo;
    private boolean alterarDataEncerramento;
    private boolean portabilidadeCartao = false;

    private String numCipCompra;
    private String anexoDocAdicionalCompra;

    public RenegociarConsignacaoParametros(){
        super();

        requiredFieldsMap.put(ParametrosFieldNames.TIPO, null);
        requiredFieldsMap.put(ParametrosFieldNames.COMPRA_CONTRATO, null);

        requiredFieldsMap.remove(ParametrosFieldNames.CNV_ATIVO);
        requiredFieldsMap.remove(ParametrosFieldNames.CSA_ATIVO);
        requiredFieldsMap.remove(ParametrosFieldNames.CSE_ATIVO);
        requiredFieldsMap.remove(ParametrosFieldNames.ORG_ATIVO);
        requiredFieldsMap.remove(ParametrosFieldNames.EST_ATIVO);
        requiredFieldsMap.remove(ParametrosFieldNames.SER_CNV_ATIVO);
        requiredFieldsMap.remove(ParametrosFieldNames.PERMITIR_VALIDACAO_TAXA);
        requiredFieldsMap.remove(ParametrosFieldNames.SER_ATIVO);
        requiredFieldsMap.remove(ParametrosFieldNames.SVC_ATIVO);
        requiredFieldsMap.remove(ParametrosFieldNames.VALIDAR);
        requiredFieldsMap.remove(ParametrosFieldNames.ADE_TIPO_VLR);
        requiredFieldsMap.remove(ParametrosFieldNames.ADE_INT_FOLHA);
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
        requiredFieldsMap.put(ParametrosFieldNames.TIPO, tipo);
    }

    public Boolean getCompraContrato() {
        return compraContrato;
    }

    public void setCompraContrato(Boolean compraContrato) {
        this.compraContrato = compraContrato;
        requiredFieldsMap.put(ParametrosFieldNames.COMPRA_CONTRATO, compraContrato);
    }

    public Boolean getPortabilidadeCartao() {
        return portabilidadeCartao;
    }

    public void setPortabilidadeCartao(Boolean portabilidadeCartao) {
        this.portabilidadeCartao = portabilidadeCartao;
    }

    public String getSerEmail() {
        return serEmail;
    }

    public void setSerEmail(String serEmail) {
        this.serEmail = serEmail;
    }

    public String getPpdCodigo() {
        return ppdCodigo;
    }

    public void setPpdCodigo(String ppdCodigo) {
        this.ppdCodigo = ppdCodigo;
    }

    public String getNumCipCompra() {
        return numCipCompra;
    }

    public void setNumCipCompra(String numCipCompra) {
        this.numCipCompra = numCipCompra;
    }

    public String getAnexoDocAdicionalCompra() {
        return anexoDocAdicionalCompra;
    }

    public void setAnexoDocAdicionalCompra(String anexoDocAdicionalCompra) {
        this.anexoDocAdicionalCompra = anexoDocAdicionalCompra;
    }

    public boolean isAlterarDataEncerramento() {
        return alterarDataEncerramento;
    }

    public void setAlterarDataEncerramento(boolean alterarDataEncerramento) {
        this.alterarDataEncerramento = alterarDataEncerramento;
    }
}

package com.zetra.econsig.dto.parametros;

/**
 * <p>Title: AlongarConsignacaoParametros</p>
 * <p>Description: Parâmetros necessários no alongamento de contrato.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class AlongarConsignacaoParametros extends ReservarMargemParametros {
    private String adeCodigo = null;
    
    public AlongarConsignacaoParametros() {
        super();
        requiredFieldsMap.put(ParametrosFieldNames.ADE_CODIGO, adeCodigo);
        
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

    public String getAdeCodigo() {
        return adeCodigo;
    }

    public void setAdeCodigo(String adeCodigo) {
        this.adeCodigo = adeCodigo;
        this.requiredFieldsMap.put(ParametrosFieldNames.ADE_CODIGO, adeCodigo);
    }
}

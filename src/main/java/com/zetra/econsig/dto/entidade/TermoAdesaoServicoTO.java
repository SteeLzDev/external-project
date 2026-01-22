package com.zetra.econsig.dto.entidade;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: TermoAdesaoServicoTO</p>
 * <p>Description: Transfer Object da tabela de termo de adesao do serviço</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class TermoAdesaoServicoTO extends CustomTransferObject {

    public TermoAdesaoServicoTO() {
        super();
    }

    public TermoAdesaoServicoTO(String csaCodigo, String svcCodigo, String terAdsTexto) {
        this();
        setAttribute(Columns.TAS_CSA_CODIGO, csaCodigo);
        setAttribute(Columns.TAS_SVC_CODIGO, svcCodigo);
        setAttribute(Columns.TAS_TEXTO, terAdsTexto);
    }

    public TermoAdesaoServicoTO(TermoAdesaoServicoTO other) {
        this();
        setAtributos(other.getAtributos());
    }

    // Getter
    public String getCsaCodigo() {
        return (String) getAttribute(Columns.TAS_CSA_CODIGO);
    }

    public String getSvcCodigo() {
        return (String) getAttribute(Columns.TAS_SVC_CODIGO);
    }

    public String getTasTexto() {
        return (String) getAttribute(Columns.TAS_TEXTO);
    }

    // Setter
    public void setCsaCodigo(String csaCodigo) {
        setAttribute(Columns.TAS_CSA_CODIGO, csaCodigo);
    }

    public void setSvcCodigo(String svcCodigo) {
        setAttribute(Columns.TAS_SVC_CODIGO, svcCodigo);
    }

    public void setTasTexto(String tasTexto) {
        setAttribute(Columns.TAS_TEXTO, tasTexto);
    }
}
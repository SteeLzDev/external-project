package com.zetra.econsig.dto.entidade;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: PrazoTransferObject</p>
 * <p>Description: Transfer Object de Prazo</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Igor
 * $Author$
 * $Revision$
 * $Date$
 */
public class PrazoTransferObject extends CustomTransferObject implements Comparable<PrazoTransferObject> {

    public PrazoTransferObject() {
        super();
    }

    public PrazoTransferObject(String przCodigo) {
        this();
        setAttribute(Columns.PRZ_CODIGO, przCodigo);
    }

    public PrazoTransferObject(PrazoTransferObject prazo) {
        this();
        setAtributos(prazo.getAtributos());
    }

    // Getter
    public String getPrzCodigo() {
        return (String) getAttribute(Columns.PRZ_CODIGO);
    }

    public String getSvcCodigo() {
        return (String) getAttribute(Columns.PRZ_SVC_CODIGO);
    }

    public Short getPrzVlr() {
        return (Short) getAttribute(Columns.PRZ_VLR);
    }

    public Short getPrzAtivo() {
        return (Short) getAttribute(Columns.PRZ_ATIVO);
    }

    public Short getPzcAtivo() {
        return (Short) getAttribute(Columns.PZC_ATIVO);
    }

    // Setter
    public void setSvcCodigo(String svcCodigo) {
        setAttribute(Columns.PRZ_SVC_CODIGO, svcCodigo);
    }

    public void setPrzVlr(Short przVlr) {
        setAttribute(Columns.PRZ_VLR, przVlr);
    }

    public void setPrzAtivo(Short przAtivo) {
        setAttribute(Columns.PRZ_ATIVO, przAtivo);
    }

    public void setPzcAtivo(Short pzcAtivo) {
        setAttribute(Columns.PZC_ATIVO, pzcAtivo);
    }

    // Equals
    @Override
    public boolean equals(Object prazo) {
        try {
            PrazoTransferObject prazoTO = (PrazoTransferObject) prazo;
            if (prazoTO != null &&
                prazoTO.getPrzCodigo().equals(getPrzCodigo()) &&
                prazoTO.getSvcCodigo().equals(getSvcCodigo()) &&
                prazoTO.getPrzVlr().equals(getPrzVlr())) {
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int result = 17;

        result = 37 * result + ( getPrzCodigo() == null ? 0 : getPrzCodigo().hashCode() );
        result = 37 * result + ( getSvcCodigo() == null ? 0 : getSvcCodigo().hashCode() );
        result = 37 * result + ( getPrzVlr() == null ? 0 : getPrzVlr().hashCode() );
        return result;
    }

    // Compare
    @Override
    public int compareTo(PrazoTransferObject prazo2) {
        return getPrzVlr().compareTo(prazo2.getPrzVlr());
    }
}

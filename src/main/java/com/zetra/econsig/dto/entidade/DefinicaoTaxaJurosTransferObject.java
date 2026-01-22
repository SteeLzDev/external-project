package com.zetra.econsig.dto.entidade;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: DefinicaoTaxaJurosTransferObject</p>
 * <p>Description: Transfer Object da definição de regra de taxa de juros</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author rodrigo.rosa
 * $Author: rodrigo.rosa $
 * $Revision: 10037 $
 * $Date: 2019-04-10 10:33:55 -0300 (qua, 10 abr 2019) $
 */
public class DefinicaoTaxaJurosTransferObject extends CustomTransferObject {

    public DefinicaoTaxaJurosTransferObject() {
        super();
    }

    public DefinicaoTaxaJurosTransferObject(String dtjCodigo) {
        this();
        setAttribute(Columns.DTJ_CODIGO, dtjCodigo);
    }

    public DefinicaoTaxaJurosTransferObject(DefinicaoTaxaJurosTransferObject definicaoTaxaJuros) {
        this();
        setAtributos(definicaoTaxaJuros.getAtributos());
    }

    // Getter
    public String getDtjCodigo() {
        return (String) getAttribute(Columns.DTJ_CODIGO);
    }

    // Setter
    public void setDtjCodigo(String dtjCodigo) {
        setAttribute(Columns.DTJ_CODIGO, dtjCodigo);
    }
}

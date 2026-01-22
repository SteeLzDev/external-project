package com.zetra.econsig.dto.entidade;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: GrupoConsignatariaTransferObject</p>
 * <p>Description: Transfer Object do Consignataria</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonardo Marteleto
 * $Author$
 * $Revision$
 * $Date$
 */
public class GrupoConsignatariaTransferObject extends CustomTransferObject {

    public GrupoConsignatariaTransferObject() {
        super();
    }

    public GrupoConsignatariaTransferObject(String tgcCodigo) {
        this();
        setAttribute(Columns.TGC_CODIGO, tgcCodigo);
    }

    public GrupoConsignatariaTransferObject(GrupoConsignatariaTransferObject grupo) {
        this();
        setAtributos(grupo.getAtributos());
    }

    // Getter Grupo de Consignatarias
    public String getGrupoCsaCodigo() {
        return (String) getAttribute(Columns.TGC_CODIGO);
    }

    public String getGrupoCsaIdentificador() {
        return (String) getAttribute(Columns.TGC_IDENTIFICADOR);
    }

    public String getGrupoCsaDescricao() {
        return (String) getAttribute(Columns.TGC_DESCRICAO);
    }

    // Setter Id e Descricao
    public void setGrupoCsaIdentificador(String tgcId) {
        setAttribute(Columns.TGC_IDENTIFICADOR, tgcId);
    }

    public void setGrupoCsaDescricao(String tgcDescricao) {
        setAttribute(Columns.TGC_DESCRICAO, tgcDescricao);
    }
}

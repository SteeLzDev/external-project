package com.zetra.econsig.dto.entidade;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: GrupoServicoTransferObject</p>
 * <p>Description: Transfer Object do Servico</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonardo Marteleto
 * $Author$
 * $Revision$
 * $Date$
 */
public class GrupoServicoTransferObject extends CustomTransferObject {

    public GrupoServicoTransferObject() {
        super();
    }

    public GrupoServicoTransferObject(String tgsCodigo) {
        this();
        setAttribute(Columns.TGS_CODIGO, tgsCodigo);
    }

    public GrupoServicoTransferObject(GrupoServicoTransferObject grupo) {
        this();
        setAtributos(grupo.getAtributos());
    }

    // Getter Grupo de Serviço
    public String getGrupoSvcCodigo() {
        return (String) getAttribute(Columns.TGS_CODIGO);
    }

    public String getGrupoSvcGrupo() {
        return (String) getAttribute(Columns.TGS_GRUPO);
    }

    public Integer getGrupoSvcQuantidade() {
        return (Integer) getAttribute(Columns.TGS_QUANTIDADE);
    }

    public Integer getGrupoSvcQuantidadePorCsa() {
        return (Integer) getAttribute(Columns.TGS_QUANTIDADE_POR_CSA);
    }

    public String getGrupoSvcIdentificador() {
        return (String) getAttribute(Columns.TGS_IDENTIFICADOR);
    }

    // Setter Grupo e Serviço
    public void setGrupoSvcGrupo(String tgsGrupo) {
        setAttribute(Columns.TGS_GRUPO, tgsGrupo);
    }

    public void setGrupoSvcQuantidade(Integer tgsQuantidade) {
        setAttribute(Columns.TGS_QUANTIDADE, tgsQuantidade);
    }

    public void setGrupoSvcQuantidadePorCsa(Integer tgsQuantidadePorCsa) {
        setAttribute(Columns.TGS_QUANTIDADE_POR_CSA, tgsQuantidadePorCsa);
    }

    public void setGrupoSvcIdentificador(String tgsIdentificador) {
        setAttribute(Columns.TGS_IDENTIFICADOR, tgsIdentificador);
    }
}

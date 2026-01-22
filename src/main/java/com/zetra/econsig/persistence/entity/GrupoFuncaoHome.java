package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: GrupoFuncaoHome</p>
 * <p>Description: Classe Home para a entidade GrupoFuncao</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class GrupoFuncaoHome extends AbstractEntityHome {


    public static GrupoFuncao findByPrimaryKey(String grfCodigo) throws FindException {
        GrupoFuncao grupoFuncao = new GrupoFuncao();
        grupoFuncao.setGrfCodigo(grfCodigo);
        return find(grupoFuncao, grfCodigo);
    }

    public static GrupoFuncao create() throws CreateException {
        throw new CreateException("mensagem.erro.metodo.nao.implementado", (AcessoSistema) null);
    }


}

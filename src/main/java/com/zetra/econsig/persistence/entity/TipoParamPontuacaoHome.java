package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: TipoParamPontuacaoHome</p>
 * <p>Description: Classe Home para a entidade TipoParamPontuacao</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class TipoParamPontuacaoHome extends AbstractEntityHome {

    public static TipoParamPontuacao findByPrimaryKey(String tpoCodigo) throws FindException {
        TipoParamPontuacao tipoParamPontuacao = new TipoParamPontuacao();
        tipoParamPontuacao.setTpoCodigo(tpoCodigo);
        return find(tipoParamPontuacao, tpoCodigo);
    }

    public static TipoParamPontuacao create() throws CreateException {
        throw new CreateException("mensagem.erro.metodo.nao.implementado", (AcessoSistema) null);
    }
}

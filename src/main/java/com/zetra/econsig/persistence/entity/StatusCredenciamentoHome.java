package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: StatusSolicitacaoHome</p>
 * <p>Description: Classe Home para a entidade StatusCredenciamento</p>
 * <p>Copyright: Copyright (c) 2002-2022</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class StatusCredenciamentoHome extends AbstractEntityHome {

    public static StatusCredenciamento findByPrimaryKey(String scrCodigo) throws FindException {
        StatusCredenciamento statusCredenciamento = new StatusCredenciamento();
        statusCredenciamento.setScrCodigo(scrCodigo);
        return find(statusCredenciamento, scrCodigo);
    }

    public static StatusCredenciamento create() throws CreateException {
        throw new CreateException("mensagem.erro.metodo.nao.implementado", (AcessoSistema) null);
    }

    public static List<StatusCredenciamento> lstStatusCredByScrCodigos(List<String> scrCodigos) throws FindException {
        String query = "FROM StatusCredenciamento scr WHERE scr.scrCodigo IN (:scrCodigos) ";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("scrCodigos", scrCodigos);
        return findByQuery(query, parameters);
    }
}
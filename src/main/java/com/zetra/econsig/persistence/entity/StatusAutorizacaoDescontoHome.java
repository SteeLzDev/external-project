package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: StatusAutorizacaoDescontoHome</p>
 * <p>Description: Classe Home para a entidade StatusAutorizacaoDesconto</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class StatusAutorizacaoDescontoHome extends AbstractEntityHome {

    public static StatusAutorizacaoDesconto findByPrimaryKey(String sadCodigo) throws FindException {
        StatusAutorizacaoDesconto statusAutorizacaoDesconto = new StatusAutorizacaoDesconto();
        statusAutorizacaoDesconto.setSadCodigo(sadCodigo);
        return find(statusAutorizacaoDesconto, sadCodigo);
    }

    public static StatusAutorizacaoDesconto findStatusAutorizacaoContrato(String adeCodigo) throws FindException {
        String query = "FROM StatusAutorizacaoDesconto sad inner join sad.autDescontoSet ade WHERE ade.adeCodigo = :adeCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("adeCodigo", adeCodigo);

        List<Object[]> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            Object[] entidades = result.get(0);
            for (Object entidade: entidades) {
                if (entidade instanceof StatusAutorizacaoDesconto) {
                    return (StatusAutorizacaoDesconto) entidade;
                }
            }
            return null;
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static StatusAutorizacaoDesconto create(String sadCodigo, String sadDescricao) throws CreateException {
        StatusAutorizacaoDesconto bean = new StatusAutorizacaoDesconto();

        bean.setSadCodigo(sadCodigo);
        bean.setSadDescricao(sadDescricao);
        create(bean);
        return bean;
    }
}

package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: DadosAutorizacaoDescontoHome</p>
 * <p>Description: Classe Home para a entidade DadosAutorizacaoDesconto</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class DadosAutorizacaoDescontoHome extends AbstractEntityHome {

    public static DadosAutorizacaoDesconto findByPrimaryKey(DadosAutorizacaoDescontoId pk) throws FindException {
        DadosAutorizacaoDesconto dadosAutDesconto = new DadosAutorizacaoDesconto();
        dadosAutDesconto.setId(pk);
        return find(dadosAutDesconto, pk);
    }

    public static DadosAutorizacaoDesconto findArquivadoByPrimaryKey(DadosAutorizacaoDescontoId pk) throws FindException {
        HtDadosAutorizacaoDesconto dadosAutDesconto = new HtDadosAutorizacaoDesconto();
        dadosAutDesconto.setId(pk);
        return new DadosAutorizacaoDesconto(find(dadosAutDesconto, pk));
    }

    public static List<DadosAutorizacaoDesconto> findByAdeCodigo(String adeCodigo) throws FindException {
        String query = "FROM DadosAutorizacaoDesconto dad WHERE dad.autDesconto.adeCodigo = :adeCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("adeCodigo", adeCodigo);

        return findByQuery(query, parameters);
    }

    public static DadosAutorizacaoDesconto create(String adeCodigo, String tdaCodigo, String dadValor) throws CreateException {
        DadosAutorizacaoDesconto bean = new DadosAutorizacaoDesconto();

        DadosAutorizacaoDescontoId id = new DadosAutorizacaoDescontoId();
        id.setAdeCodigo(adeCodigo);
        id.setTdaCodigo(tdaCodigo);
        bean.setId(id);
        bean.setDadValor(dadValor);

        create(bean);
        return bean;
    }


}

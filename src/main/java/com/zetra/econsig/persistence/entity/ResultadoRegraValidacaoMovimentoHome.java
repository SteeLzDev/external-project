package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: ResultadoRegraValidacaoMovimentoHome</p>
 * <p>Description: Classe Home para a entidade ResultadoRegraValidMov</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ResultadoRegraValidacaoMovimentoHome extends AbstractEntityHome {
    
    public static ResultadoRegraValidMov findByPrimaryKey(ResultadoRegraValidMovId pk) throws FindException {
        ResultadoRegraValidMov rRegraValidacao = new ResultadoRegraValidMov();
        rRegraValidacao.setId(pk);
        return find(rRegraValidacao, pk);
    }
    
    public static ResultadoRegraValidMov create(String rvaCodigo, String rvmCodigo, String rrvResultado, String rrvValorEncontrado) throws CreateException {
        ResultadoRegraValidMov bean = new ResultadoRegraValidMov();

        ResultadoRegraValidMovId id = new ResultadoRegraValidMovId();
        id.setRvaCodigo(rvaCodigo);
        id.setRvmCodigo(rvmCodigo);
        bean.setId(id);
        bean.setRrvResultado(rrvResultado);
        bean.setRrvValorEncontrado(rrvValorEncontrado);
        
        create(bean);
        return bean;
    }
    
}

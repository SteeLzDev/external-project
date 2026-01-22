package com.zetra.econsig.persistence.entity;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: ParamPontuacaoRseHome</p>
 * <p>Description: Classe Home para a entidade ParamPontuacaoRse</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ParamPontuacaoRseHome extends AbstractEntityHome {


    public static Collection<ParamPontuacaoRse> findByTipoAndValue(String tpoCodigo, Integer tpoValue) throws FindException {
        String query = "FROM ParamPontuacaoRse p WHERE p.tipoParamPontuacao.tpoCodigo = :tpoCodigo "
                     + "and p.ppoLimInferior <= :tpoValue and p.ppoLimSuperior >= :tpoValue";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("tpoCodigo", tpoCodigo);
        parameters.put("tpoValue", tpoValue);

        List<ParamPontuacaoRse> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result;
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }


    public static Collection<ParamPontuacaoRse> findByTipo(String tpoCodigo) throws FindException {
        String query = "FROM ParamPontuacaoRse AS p WHERE p.tipoParamPontuacao.tpoCodigo = :tpoCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("tpoCodigo", tpoCodigo);

        List<ParamPontuacaoRse> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result;
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }


    public static ParamPontuacaoRse findByPrimaryKey(Integer ppoCodigo) throws FindException {
        ParamPontuacaoRse paramPontuacaoRse = new ParamPontuacaoRse();
        paramPontuacaoRse.setPpoCodigo(ppoCodigo);
        return find(paramPontuacaoRse, ppoCodigo);
    }

    public static TipoParamPontuacao create() throws CreateException {
        throw new CreateException("mensagem.erro.metodo.nao.implementado", (AcessoSistema) null);
    }
}

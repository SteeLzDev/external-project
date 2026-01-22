package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: CidadeHome</p>
 * <p>Description: Classe Home para a entidade Cidade</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class CidadeHome extends AbstractEntityHome {

    public static Cidade findByPrimaryKey(String cidCodigo) throws FindException {
        Cidade cidade = new Cidade();
        cidade.setCidCodigo(cidCodigo);
        return find(cidade, cidCodigo);
    }

    public static Cidade findByCodigoIBGE(String cidCodigoIbge) throws FindException {
        String query = "FROM Cidade cid JOIN FETCH cid.uf where cid.cidCodigoIbge = :cidCodigoIbge";
        Map<String, Object> parameters = new HashMap<>();

        parameters.put("cidCodigoIbge", cidCodigoIbge);

        List<Cidade> cidades = findByQuery(query, parameters);
        if (cidades == null || cidades.size() == 0) {
            return null;
        } else if (cidades.size() == 1) {
            return cidades.get(0);
        } else {
            throw new FindException("mensagem.erro.mais.de.um.resultado.encontrado", (AcessoSistema) null);
        }
    }

    public static Cidade create() throws CreateException {
        throw new CreateException("mensagem.erro.metodo.nao.implementado", (AcessoSistema) null);
    }
}

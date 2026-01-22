package com.zetra.econsig.persistence.entity;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;

/**
 * <p>Title: SubrelatorioHome</p>
 * <p>Description: Querys da entidade subrelatorio</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Eduardo Fortes
 */
public class SubrelatorioHome extends AbstractEntityHome{

    public static Subrelatorio findSubrelatorio(String sreCodigo, String relCodigo) throws FindException{
        String query = "FROM Subrelatorio sre WHERE sre.relCodigo = :relCodigo and sre.sreCodigo = :sreCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("relCodigo", relCodigo);
        parameters.put("sreCodigo", sreCodigo);
        
        List<Subrelatorio> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }
    
    public static Collection<Subrelatorio> findByRelCodigo(String relCodigo) throws FindException {
        String query = "FROM Subrelatorio sre "
                + "JOIN FETCH sre.relatorio "
                + "WHERE sre.relCodigo = :relCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("relCodigo", relCodigo);

        return findByQuery(query, parameters);
    }
    
    public static void create(String relCodigo, String nomeAnexoSubrelatorio, String sreNomeParametro, String sreTemplateSql, AcessoSistema responsavel) throws CreateException, MissingPrimaryKeyException{
        Subrelatorio subrelatorio = new Subrelatorio();
        subrelatorio.setSreCodigo(DBHelper.getNextId());
        subrelatorio.setRelCodigo(relCodigo);
        subrelatorio.setSreTemplateJasper(nomeAnexoSubrelatorio);
        subrelatorio.setSreNomeParametro(sreNomeParametro);
        subrelatorio.setSreTemplateSql(sreTemplateSql);
        SubrelatorioHome.create(subrelatorio);
    }
}

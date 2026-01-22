package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.SessionUtil;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: FuncaoHome</p>
 * <p>Description: Classe Home para a entidade Funcao</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class FuncaoHome extends AbstractEntityHome {

    public static Funcao findByPrimaryKey(String funCodigo) throws FindException {
        String query = "FROM Funcao fun WHERE fun.funCodigo = :funCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("funCodigo", funCodigo);

        List<Funcao> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static Funcao create(String grfCodigo, String funDescricao, String funPermiteBloqueio, String funExigeTmo,
            String funExigeSegundaSenhaCse, String funExigeSegundaSenhaSup, String funExigeSegundaSenhaOrg, String funExigeSegundaSenhaCsa, String funExigeSegundaSenhaCor, String funExigeSegundaSenhaSer, String funAuditavel) throws CreateException {

        Session session = SessionUtil.getSession();
        Funcao bean = new Funcao();

        try {
            bean.setFunCodigo(DBHelper.getNextId());
            bean.setGrupoFuncao(session.getReference(GrupoFuncao.class, grfCodigo));
            bean.setFunDescricao(funDescricao);
            bean.setFunPermiteBloqueio(!TextHelper.isNull(funPermiteBloqueio) ? funPermiteBloqueio : CodedValues.TPC_NAO);
            bean.setFunExigeTmo(!TextHelper.isNull(funExigeTmo) ? funExigeTmo : CodedValues.TPC_NAO);
            bean.setFunExigeSegundaSenhaCse(!TextHelper.isNull(funExigeSegundaSenhaCse) ? funExigeSegundaSenhaCse : CodedValues.TPC_NAO);
            bean.setFunExigeSegundaSenhaSup(!TextHelper.isNull(funExigeSegundaSenhaSup) ? funExigeSegundaSenhaSup : CodedValues.TPC_NAO);
            bean.setFunExigeSegundaSenhaOrg(!TextHelper.isNull(funExigeSegundaSenhaOrg) ? funExigeSegundaSenhaOrg : CodedValues.TPC_NAO);
            bean.setFunExigeSegundaSenhaCsa(!TextHelper.isNull(funExigeSegundaSenhaCsa) ? funExigeSegundaSenhaCsa : CodedValues.TPC_NAO);
            bean.setFunExigeSegundaSenhaCor(!TextHelper.isNull(funExigeSegundaSenhaCor) ? funExigeSegundaSenhaCor : CodedValues.TPC_NAO);
            bean.setFunExigeSegundaSenhaSer(!TextHelper.isNull(funExigeSegundaSenhaSer) ? funExigeSegundaSenhaSer : CodedValues.TPC_NAO);
            bean.setFunAuditavel(!TextHelper.isNull(funAuditavel) ? funAuditavel : CodedValues.TPC_NAO);
            bean.setFunRestritaNca(CodedValues.TPC_NAO);
            bean.setFunLiberaMargem(CodedValues.TPC_NAO);
            create(bean, session);
        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }
}

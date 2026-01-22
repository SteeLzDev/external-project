package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: SolicitacaoSuporteHome</p>
 * <p>Description: CRUD para solicitação suporte</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class SolicitacaoSuporteHome extends AbstractEntityHome {

    public static SolicitacaoSuporte findByPrimaryKey(String sosCodigo) throws FindException {
        SolicitacaoSuporte sos = new SolicitacaoSuporte();
        sos.setSosCodigo(sosCodigo);
        return find(sos, sosCodigo);
    }

    public static List<SolicitacaoSuporte> findByUsuario(String usuCodigo) throws FindException {
        String query = "FROM SolicitacaoSuporte sos inner join sos.usuario usu WHERE usu.usuCodigo = :usuCodigo order by sos.sosDataCadastro desc";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("usuCodigo", usuCodigo);

        return findByQuery(query, parameters);
    }

    public static SolicitacaoSuporte create(String sosChave, String usuCodigo, String sosSumario, String sosPrioridade) throws CreateException {

        Session session = SessionUtil.getSession();
        SolicitacaoSuporte bean = new SolicitacaoSuporte();

        try {
            bean.setSosCodigo(DBHelper.getNextId());
            bean.setUsuario((Usuario) session.getReference(Usuario.class, usuCodigo));
            bean.setSosSumario(sosSumario);
            bean.setSosChave(sosChave);
            bean.setSosDataCadastro(DateHelper.getSystemDatetime());
            //Por enquanto esse valor ficará fixo porque ele também é enviado fixo na criação da tarefa
            //no momento em que for implementado o item 1.3 da DESENV-9788, esse valor poderá se tornar dinâmico.
            bean.setSosPrioridade("Baixa");
            create(bean, session);
        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }
}

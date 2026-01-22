package com.zetra.econsig.persistence.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: ResultadoValidacaoMovimentoHome</p>
 * <p>Description: Classe Home para a entidade ResultadoValidacaoMov</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ResultadoValidacaoMovimentoHome extends AbstractEntityHome {

    public static ResultadoValidacaoMov findByPrimaryKey(String rvaCodigo) throws FindException {
        ResultadoValidacaoMov bean = new ResultadoValidacaoMov();
        bean.setRvaCodigo(rvaCodigo);
        return find(bean, rvaCodigo);
    }

    public static ResultadoValidacaoMov findByNomeArquivo(String rvaNomeArquivo) throws FindException {
        String query = "FROM ResultadoValidacaoMov AS r WHERE r.rvaNomeArquivo = :rvaNomeArquivo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("rvaNomeArquivo", rvaNomeArquivo);

        List<ResultadoValidacaoMov> resultSet = findByQuery(query, parameters);

        if (!resultSet.isEmpty()) {
            return resultSet.get(0);
        }

        throw new FindException("mensagem.erro.entidade.nao.encontrada", AcessoSistema.getAcessoUsuarioSistema());
    }

    public static ResultadoValidacaoMov create(String usuCodigo, String rvaNomeArquivo, String rvaResultado, Boolean rvaAceite, Date rvaDataAceite, Date rvaPeriodo, Date rvaDataProcesso) throws CreateException {

        Session session = SessionUtil.getSession();
        ResultadoValidacaoMov bean = new ResultadoValidacaoMov();

        String objectId = null;
        try {
            objectId = DBHelper.getNextId();
            bean.setRvaCodigo(objectId);
            bean.setUsuario(session.getReference(Usuario.class, usuCodigo));
            bean.setRvaNomeArquivo(rvaNomeArquivo);
            bean.setRvaPeriodo(rvaPeriodo);
            bean.setRvaDataProcesso(rvaDataProcesso);
            bean.setRvaResultado(rvaResultado);
            bean.setRvaAceite(rvaAceite);
            bean.setRvaDataAceite(rvaDataAceite);
            create(bean, session);

        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }
}

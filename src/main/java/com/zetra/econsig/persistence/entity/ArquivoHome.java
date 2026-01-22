package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: ArquivoHome</p>
 * <p>Description: Classe Home para a entidade Arquivo</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ArquivoHome extends AbstractEntityHome {

    public static Arquivo findByPrimaryKey(String arqCodigo) throws FindException {
        Arquivo arquivo = new Arquivo();
        arquivo.setArqCodigo(arqCodigo);
        return find(arquivo, arqCodigo);
    }

    public static Arquivo create(String arqConteudo, String tarCodigo) throws CreateException {
        Session session = SessionUtil.getSession();
        Arquivo bean = new Arquivo();

        try {
            bean.setArqCodigo(DBHelper.getNextId());
            bean.setArqConteudo(arqConteudo);
            bean.setTipoArquivo(session.getReference(TipoArquivo.class, tarCodigo));

            create(bean);
        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }

    public static void removeBySer(String serCodigo) throws FindException, RemoveException {
        String query = "FROM ArquivoSer ase WHERE ase.id.serCodigo = :serCodigo";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("serCodigo", serCodigo);

        List<ArquivoSer> resultado = findByQuery(query, parameters);

        if (resultado != null && !resultado.isEmpty()) {
            for (ArquivoSer ase : resultado) {
                // Remove ligação usuário servidor
                remove(ase);
                // Remove ligação usuário
                remove(findByPrimaryKey(ase.getArquivo().getArqCodigo()));
            }
        }
    }
}

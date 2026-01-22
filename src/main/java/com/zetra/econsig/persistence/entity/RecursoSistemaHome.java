package com.zetra.econsig.persistence.entity;

import java.util.List;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: RecursoSistemaHome</p>
 * <p>Description: Classe Home para a entidade RecursoSistema</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RecursoSistemaHome extends AbstractEntityHome {

    public static RecursoSistema findByPrimaryKey(String resChave) throws FindException {
        RecursoSistema recursoSistema = new RecursoSistema();
        recursoSistema.setResChave(resChave);
        return find(recursoSistema, resChave);
    }

    public static List<RecursoSistema> listAll() throws FindException {
        String query = "FROM RecursoSistema res";
        return findByQuery(query, null);
    }

    public static RecursoSistema create(String resChave, String resConteudo) throws CreateException {
        RecursoSistema recursoSistema = new RecursoSistema();
        recursoSistema.setResChave(resChave);
        recursoSistema.setResConteudo(resConteudo);
        create(recursoSistema);
        return recursoSistema;
    }
}

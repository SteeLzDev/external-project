package com.zetra.econsig.persistence.entity;

import java.util.List;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: PapelHome</p>
 * <p>Description: Classe Home para a entidade Papel</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class PapelHome extends AbstractEntityHome {


    public static Papel findByPrimaryKey(String papCodigo) throws FindException {
        Papel papel = new Papel();
        papel.setPapCodigo(papCodigo);
        return find(papel, papCodigo);
    }

    public static List<Papel> listarPapeis() throws FindException {
        String query = "FROM Papel";

        return findByQuery(query, null);
    }

    public static Papel create() throws CreateException {
        throw new CreateException("mensagem.erro.metodo.nao.implementado", (AcessoSistema) null);
    }


}

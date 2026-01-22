package com.zetra.econsig.persistence.entity;

import java.util.List;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.sistema.DBHelper;

/**
 * <p>Title: TipoEnderecoHome</p>
 * <p>Description: Classe da entidade TipoEndereco.</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: Nostrum Consultoria e Projetos</p>
 * $Author$
 * $Revision$
 * $Date$
 */

public class TipoEnderecoHome extends AbstractEntityHome {

    public static TipoEndereco findByPrimaryKey(String tieCodigo) throws FindException {
        TipoEndereco tipoEndereco = new TipoEndereco();
        tipoEndereco.setTieCodigo(tieCodigo);

        return find(tipoEndereco, tieCodigo);
    }

    public static List<TipoEndereco> listAll() throws FindException {
        String query = "FROM TipoEndereco tie";

        return findByQuery(query, null);

    }

    public static TipoEndereco create(String descricao) throws CreateException {
        TipoEndereco tipoEndereco = new TipoEndereco();

        try {
            tipoEndereco.setTieCodigo(DBHelper.getNextId());
            tipoEndereco.setTieDescricao(descricao);
        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        }

        create(tipoEndereco);

        return tipoEndereco;
    }

}

package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.sistema.DBHelper;

/**
 * <p>Title: TipoArquivoHome</p>
 * <p>Description: Classe para encapsular acesso a entidade TipoArquivo.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class TipoArquivoHome extends AbstractEntityHome {

    public static TipoArquivo findByPrimaryKey(String codigo) throws FindException {
        TipoArquivo tipoArquivo = new TipoArquivo();
        tipoArquivo.setTarCodigo(codigo);
        return find(tipoArquivo, codigo);
    }

    public static TipoArquivo create(String descricao) throws CreateException {
        TipoArquivo bean = new TipoArquivo();
        try {
            String objectId = DBHelper.getNextId();
            bean.setTarCodigo(objectId);
            bean.setTarDescricao(descricao);
        } catch (MissingPrimaryKeyException e) {
            throw new CreateException(e);
        }

        create(bean);
        return bean;
    }
}

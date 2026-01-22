package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.DateHelper;

public class ControleDocumentoMargemHome extends AbstractEntityHome {

    public static ControleDocumentoMargem create(String chave, String rseCodigo, String localArquivo) throws CreateException {
        ControleDocumentoMargem bean = new ControleDocumentoMargem();
        try {
            bean.setCdmCodigo(DBHelper.getNextId());
            bean.setCdmCodigoAuth(chave);
            bean.setCdmLocalArquivo(localArquivo);
            bean.setRseCodigo(rseCodigo);
            bean.setCdmData(DateHelper.getSystemDatetime());
        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        }
        return create(bean);
    }
}

package com.zetra.econsig.persistence.entity;

import java.util.Calendar;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: ChaveCriptografiaArquivoHome</p>
 * <p>Description: Classe home para a entidade Chave Criptografia Arquivo</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ChaveCriptografiaArquivoHome extends AbstractEntityHome {

    public static ChaveCriptografiaArquivo findByPrimaryKey(String papCodigo, String tarCodigo, String caaCodigoEnt) throws FindException {
        ChaveCriptografiaArquivoId id = new ChaveCriptografiaArquivoId(papCodigo, tarCodigo, caaCodigoEnt);
        ChaveCriptografiaArquivo bean = new ChaveCriptografiaArquivo();
        bean.setId(id);
        return find(bean, id);
    }

    public static ChaveCriptografiaArquivo create(String papCodigo, String tarCodigo, String caaCodigoEnt, String caaChave) throws CreateException {
        ChaveCriptografiaArquivoId id = new ChaveCriptografiaArquivoId(papCodigo, tarCodigo, caaCodigoEnt);
        ChaveCriptografiaArquivo bean = new ChaveCriptografiaArquivo();
        bean.setId(id);
        bean.setCaaChave(caaChave);
        bean.setCaaData(Calendar.getInstance().getTime());

        create(bean);
        return bean;
    }
}

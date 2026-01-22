package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: PerguntaDadosCadastraisHome</p>
 * <p>Description: Classe Home para a entidade PerguntaDadosCadastrais</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class PerguntaDadosCadastraisHome extends AbstractEntityHome {

    public static PerguntaDadosCadastrais findByPrimaryKey(PerguntaDadosCadastraisId id) throws FindException {
        PerguntaDadosCadastrais bean = new PerguntaDadosCadastrais();
        bean.setId(id);
        return find(bean, id);
    }

    public static PerguntaDadosCadastrais create(Short pdcGrupo, Short pdcNumero, Short pdcStatus, String pdcTexto, String pdcCampo) throws CreateException {
        PerguntaDadosCadastrais bean = new PerguntaDadosCadastrais();

        PerguntaDadosCadastraisId id = new PerguntaDadosCadastraisId();
        id.setPdcGrupo(pdcGrupo);
        id.setPdcNumero(pdcNumero);

        bean.setId(id);
        bean.setPdcStatus(pdcStatus);
        bean.setPdcTexto(pdcTexto);
        bean.setPdcCampo(pdcCampo);

        create(bean);

        return bean;
    }
}

package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: TermoAdesaoServicoHome</p>
 * <p>Description: Classe Home para a entidade TermoAdesaoServico</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class TermoAdesaoServicoHome extends AbstractEntityHome {

    public static TermoAdesaoServico findByPrimaryKey(TermoAdesaoServicoId pk) throws FindException {
        final TermoAdesaoServico termoAdesao = new TermoAdesaoServico();
        termoAdesao.setId(pk);
        return find(termoAdesao, pk);
    }

    public static TermoAdesaoServico create(String csaCodigo, String svcCodigo, String terAdsTexto) throws CreateException {
        final TermoAdesaoServico bean = new TermoAdesaoServico();

        final TermoAdesaoServicoId id = new TermoAdesaoServicoId();
        id.setCsaCodigo(csaCodigo);
        id.setSvcCodigo(svcCodigo);
        bean.setId(id);
        bean.setTasTexto(terAdsTexto);

        create(bean);
        return bean;
    }
}

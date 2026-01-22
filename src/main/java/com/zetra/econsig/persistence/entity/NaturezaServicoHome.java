package com.zetra.econsig.persistence.entity;

import java.util.List;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: NaturezaServicoHome</p>
 * <p>Description: Classe Home para a entidade NaturezaServico</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class NaturezaServicoHome extends AbstractEntityHome {

    public static NaturezaServico findByPrimaryKey(String nseCodigo) throws FindException {
        NaturezaServico naturezaServico = new NaturezaServico();
        naturezaServico.setNseCodigo(nseCodigo);
        return find(naturezaServico, nseCodigo);
    }

    public static NaturezaServico create(String nseCodigo, String nseDescricao) throws CreateException {
        NaturezaServico bean = new NaturezaServico();
        bean.setNseCodigo(nseCodigo);
        bean.setNseDescricao(nseDescricao);
        create(bean);
        return bean;
    }

    public static List<NaturezaServico> listaNaturezas(AcessoSistema responsavel) throws FindException {
        String query = "FROM NaturezaServico nse ";

        List<NaturezaServico> naturezas = findByQuery(query, null);

        if (naturezas == null || naturezas.size() == 0) {
            return null;
        } else {
            return naturezas;
        }
    }
}

package com.zetra.econsig.persistence.entity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: RelatorioFiltroHome</p>
 * <p>Description: Classe Home para a entidade RelatorioFiltro</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioFiltroHome extends AbstractEntityHome {

    public static RelatorioFiltro findByPrimaryKey(String relCodigo, String tfrCodigo) throws FindException {
        RelatorioFiltroId id = new RelatorioFiltroId();
        id.setRelCodigo(relCodigo);
        id.setTfrCodigo(tfrCodigo);

        RelatorioFiltro relatorio = new RelatorioFiltro();
        relatorio.setId(id);
        return find(relatorio, id);
    }

    public static Collection<RelatorioFiltro> findByRelCodigo(String relCodigo) throws FindException {
        String query = "FROM RelatorioFiltro rfi WHERE rfi.id.relCodigo = :relCodigo";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("relCodigo", relCodigo);

        return findByQuery(query, parameters);
    }

    public static RelatorioFiltro create(String relCodigo, String tfrCodigo, String rfiExibeCse, String rfiExibeOrg, String rfiExibeCsa, String rfiExibeCor, String rfiExibeSer, String rfiExibeSup, String rfiParametro, Short rfiSequencia) throws CreateException {
        RelatorioFiltro bean = new RelatorioFiltro();

        RelatorioFiltroId id = new RelatorioFiltroId();
        id.setRelCodigo(relCodigo);
        id.setTfrCodigo(tfrCodigo);
        bean.setId(id);

        bean.setRfiExibeCor(rfiExibeCor);
        bean.setRfiExibeCsa(rfiExibeCsa);
        bean.setRfiExibeCse(rfiExibeCse);
        bean.setRfiExibeOrg(rfiExibeOrg);
        bean.setRfiExibeSer(rfiExibeSer);
        bean.setRfiExibeSup(rfiExibeSup);
        bean.setRfiParametro(rfiParametro);
        bean.setRfiSequencia(rfiSequencia);

        create(bean);
        return bean;
    }
}

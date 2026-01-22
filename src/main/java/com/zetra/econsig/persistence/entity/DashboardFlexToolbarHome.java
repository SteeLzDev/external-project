package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

public class DashboardFlexToolbarHome extends AbstractEntityHome {

    public static DashboardFlexToolbar findByPrimaryKey(String dftCodigo) throws FindException {
        DashboardFlexToolbar dashboardFlexToolbar = new DashboardFlexToolbar();
        dashboardFlexToolbar.setDftCodigo(dftCodigo);
        return find(dashboardFlexToolbar, dftCodigo);
    }

    public static DashboardFlexToolbar createDashboardFlex(DashboardFlexToolbar dashboardFlexToolbar) throws CreateException {
        throw new CreateException("mensagem.erro.metodo.nao.implementado", (AcessoSistema) null);
    }

    public static List<DashboardFlexToolbar> listarDashboardFlexTollbar(List<String> dfoCodigos) throws FindException {
        StringBuilder query = new StringBuilder();

        query.append("FROM DashboardFlexToolbar dft ");
        query.append(" WHERE 1=1 ");
        
        if (dfoCodigos != null && !dfoCodigos.isEmpty()) {
            query.append(" AND dft.dfoCodigo IN (:dfoCodigos) ");
        }
        
        final Map<String, Object> parameters = new HashMap<>();
        if (dfoCodigos != null && !dfoCodigos.isEmpty()) {
            parameters.put("dfoCodigos", dfoCodigos);
        }

        return findByQuery(query.toString(), parameters);
    }
}
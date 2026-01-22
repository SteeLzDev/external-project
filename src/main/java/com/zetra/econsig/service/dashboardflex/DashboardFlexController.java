package com.zetra.econsig.service.dashboardflex;

import java.util.List;

import com.zetra.econsig.exception.DashboardFlexControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.DashboardFlex;
import com.zetra.econsig.persistence.entity.DashboardFlexConsulta;
import com.zetra.econsig.persistence.entity.DashboardFlexToolbar;

public interface DashboardFlexController {
    
    public List<DashboardFlex> listarDashboardFlex(boolean somenteAtivos, String papCodigo, List<String> funCodigos, AcessoSistema responsavel) throws DashboardFlexControllerException;
    public List<DashboardFlexConsulta> listarDashboardFlexConsulta(List<String> dflCodigos, boolean somenteAtivos, AcessoSistema responsavel) throws DashboardFlexControllerException;
    public List<DashboardFlexToolbar> listarDashboardFlexToolbar(List<String> dfoCodigos, AcessoSistema responsavel) throws DashboardFlexControllerException;
    public DashboardFlexConsulta getDashboardFlexConsulta(String dflCodigo, AcessoSistema responsavel) throws DashboardFlexControllerException;
}

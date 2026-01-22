package com.zetra.econsig.service.dashboardflex;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zetra.econsig.exception.DashboardFlexControllerException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.DashboardFlex;
import com.zetra.econsig.persistence.entity.DashboardFlexConsulta;
import com.zetra.econsig.persistence.entity.DashboardFlexConsultaHome;
import com.zetra.econsig.persistence.entity.DashboardFlexHome;
import com.zetra.econsig.persistence.entity.DashboardFlexToolbar;
import com.zetra.econsig.persistence.entity.DashboardFlexToolbarHome;

@Service
@Transactional
public class DashboardFlexControllerBean implements DashboardFlexController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(DashboardFlexControllerBean.class);

    @Override
    public List<DashboardFlex> listarDashboardFlex(boolean somenteAtivos, String papCodigo, List<String> funCodigos, AcessoSistema responsavel) throws DashboardFlexControllerException {
        try {
            return DashboardFlexHome.listarDashboardFlex(somenteAtivos, papCodigo, funCodigos);
        } catch (FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DashboardFlexControllerException("mensagem.erroInternoSistema", responsavel);
        }
    }

    @Override
    public List<DashboardFlexConsulta> listarDashboardFlexConsulta(List<String> dflCodigos, boolean somenteAtivos, AcessoSistema responsavel) throws DashboardFlexControllerException {
        try {
            return DashboardFlexConsultaHome.listarDashboardFlexConsulta(dflCodigos, somenteAtivos);
        } catch (FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DashboardFlexControllerException("mensagem.erroInternoSistema", responsavel);
        }
    }

    @Override
    public List<DashboardFlexToolbar> listarDashboardFlexToolbar(List<String> dfoCodigos, AcessoSistema responsavel) throws DashboardFlexControllerException {
        try {
            return DashboardFlexToolbarHome.listarDashboardFlexTollbar(dfoCodigos);
        } catch (FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DashboardFlexControllerException("mensagem.erroInternoSistema", responsavel);
        }
    }

    @Override
    public DashboardFlexConsulta getDashboardFlexConsulta(String dfoCodigo, AcessoSistema responsavel) throws DashboardFlexControllerException {
        try {
            return DashboardFlexConsultaHome.findByPrimaryKey(dfoCodigo);
        } catch (FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DashboardFlexControllerException("mensagem.erroInternoSistema", responsavel);
        }
    }

}

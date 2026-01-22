package com.zetra.econsig.service.folha;


import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.BlocoProcessamentoControllerException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.dashboardprocessamento.ListarBlocosProcessamentoDashboardQuery;
import com.zetra.econsig.persistence.query.dashboardprocessamento.ListarHistoricoMediaMargemDashboardQuery;
import com.zetra.econsig.persistence.query.dashboardprocessamento.ObtemMediaMargemProcessadaDashboardQuery;
import com.zetra.econsig.persistence.query.dashboardprocessamento.ObtemMenorDataProcessamentoBlocoQuery;
import com.zetra.econsig.persistence.query.dashboardprocessamento.ObtemTotalParcelasRejeitadasPeriodoQuery;

/**
 * <p>Title: BlocoProcessamentoControllerBean</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class BlocoProcessamentoControllerBean implements BlocoProcessamentoController {

    @Override
    public List<TransferObject> listarBlocosProcessamentoDashboard(String bprPeriodo, List<String> sbpCodigos, List<String> tbpCodigos, String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) throws BlocoProcessamentoControllerException {
        try {
            ListarBlocosProcessamentoDashboardQuery query = new ListarBlocosProcessamentoDashboardQuery();
            query.tipoEntidade = tipoEntidade;
            query.codigoEntidade = codigoEntidade;
            query.bprPeriodo = bprPeriodo;
            query.sbpCodigos = sbpCodigos;
            query.tbpCodigos = tbpCodigos;
            return query.executarDTO();

        } catch (HQueryException ex) {
            throw new BlocoProcessamentoControllerException(ex);
        }
    }

    @Override
    public int countParcelasRejeitadasPeriodoAtual(Date periodo, String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) throws BlocoProcessamentoControllerException {
        try {
            ObtemTotalParcelasRejeitadasPeriodoQuery query = new ObtemTotalParcelasRejeitadasPeriodoQuery();
            query.tipoEntidade = tipoEntidade;
            query.codigoEntidade = codigoEntidade;
            query.periodo = periodo;

            return query.executarContador();

        } catch (HQueryException ex) {
            throw new BlocoProcessamentoControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> listarHistoricoMediaMargem(Date periodoIni, Date periodoFim, String tipoEntidade, String codigoEntidade, Short marCodigo, AcessoSistema responsavel) throws BlocoProcessamentoControllerException {
        try {
            ListarHistoricoMediaMargemDashboardQuery query = new ListarHistoricoMediaMargemDashboardQuery();
            query.tipoEntidade = tipoEntidade;
            query.codigoEntidade = codigoEntidade;
            query.periodoIni = periodoIni;
            query.periodoFim = periodoFim;
            query.marCodigo = marCodigo;
            return query.executarDTO();

        } catch (HQueryException ex) {
            throw new BlocoProcessamentoControllerException(ex);
        }
    }

    @Override
    public Date obterInicioProcessamento(List<String> sbpCodigos, AcessoSistema responsavel) throws BlocoProcessamentoControllerException {
        try {
            ObtemMenorDataProcessamentoBlocoQuery query = new ObtemMenorDataProcessamentoBlocoQuery();
            query.sbpCodigos = sbpCodigos;

            List<Date> result = query.executarLista();
            if (result != null && result.size() > 0 && result.get(0) != null) {
                return result.get(0);
            }
            return null;
        } catch (HQueryException ex) {
            throw new BlocoProcessamentoControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> obterMediaMargemProcessada(String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) throws BlocoProcessamentoControllerException {
        try {
            ObtemMediaMargemProcessadaDashboardQuery query = new ObtemMediaMargemProcessadaDashboardQuery();
            query.tipoEntidade = tipoEntidade;
            query.codigoEntidade = codigoEntidade;
            return query.executarDTO();
        } catch (HQueryException ex) {
            throw new BlocoProcessamentoControllerException(ex);
        }
    }
}

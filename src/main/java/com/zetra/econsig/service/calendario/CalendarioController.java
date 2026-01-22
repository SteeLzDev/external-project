package com.zetra.econsig.service.calendario;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.CalendarioTO;
import com.zetra.econsig.exception.CalendarioControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.Calendario;

/**
 * <p>Title: CalendarioController</p>
 * <p>Description: Session Bean para a rotina de manutenção de calendário, período, etc.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface CalendarioController {

    /** MANUTENÇÃO DE CALENDÁRIO **/
    public CalendarioTO findCalendario(java.util.Date calData, AcessoSistema responsavel) throws CalendarioControllerException;

    public void updateCalendario(CalendarioTO calendario, AcessoSistema responsavel) throws CalendarioControllerException;

    public List<CalendarioTO> lstCalendario(TransferObject criterio, AcessoSistema responsavel) throws CalendarioControllerException;

    List<Calendario> lstCalendariosAPartirDe(Date calData, boolean diasUteis, Integer numDatasRecuperar) throws CalendarioControllerException;

    public void atualizaCalendarioOffset(java.util.Date calData, AcessoSistema responsavel) throws CalendarioControllerException;

    /** MANUTENÇÃO DE CALENDÁRIO BASE **/
    public TransferObject findCalendarioBase(java.util.Date cabData, AcessoSistema responsavel) throws CalendarioControllerException;

    public List<CalendarioTO> lstCalendarioBase(TransferObject criterio, AcessoSistema responsavel) throws CalendarioControllerException;

    /** MANUTENÇÃO DE CALENDÁRIO FOLHA **/
    public boolean existeCalendarioFolhaCse(AcessoSistema responsavel) throws CalendarioControllerException;

    public Map<Integer, TransferObject> lstCalendarioFolhaAno(Integer ano, String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) throws CalendarioControllerException;

    public void updateCalendarioFolha(List<TransferObject> lstCalendarioFolhaAlteracao, List<TransferObject> lstCalendarioFolhaExclusao, String tipoEntidade, String codigoEntidade, boolean alteraPeriodoVigente, AcessoSistema responsavel) throws CalendarioControllerException;

    public void updateTodosCalendarioFolha(Integer novoDiaCorte, String tipoEntidade, String codigoEntidade, boolean alteraPeriodoVigente, AcessoSistema responsavel) throws CalendarioControllerException;

    public void updateCalendarioFolhaRetorno(Integer novoDiaPrevisaoRetorno, String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) throws CalendarioControllerException;

    public void atualizaCalendarioFolhaReplica(List<TransferObject> lstCalendarioFolhaBase, List<TransferObject> lstCalendarioFolhaExclusao, Integer anoBase, Integer anoInicioReplica, Integer anoFimReplica, String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) throws CalendarioControllerException;

    public void atualizarCalendarioFolhaPorTipoEntidade(String tipoEntidade, String codigoEntidade, Integer diaCorte, String periodoInicial, Integer quantidadePeriodos, Integer diaMesPrevisao,  AcessoSistema responsavel) throws CalendarioControllerException;

    public Date findProximoDiaUtil(Date dataInicio, Integer diaApos) throws CalendarioControllerException;

    public Date findProximoDiaUtil(Date dataInicio, Date dataFim, Integer diaApos) throws CalendarioControllerException;

    public boolean exibirCalendarioFiscal(String tipoEntidade, String estCodigo, String orgCodigo, AcessoSistema responsavel);

    public void carregaCalendarioFolhaAutomatico(AcessoSistema responsavel) throws CalendarioControllerException;
}


package com.zetra.econsig.service.validardocumento;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.exception.ValidarDocumentoControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.entity.AbstractEntityHome;
import com.zetra.econsig.persistence.entity.AnexoAutorizacaoDesconto;
import com.zetra.econsig.persistence.entity.AnexoAutorizacaoDescontoHome;
import com.zetra.econsig.persistence.entity.AutDesconto;
import com.zetra.econsig.persistence.entity.AutDescontoHome;
import com.zetra.econsig.persistence.entity.RegistroServidorHome;
import com.zetra.econsig.persistence.entity.SolicitacaoAutorizacao;
import com.zetra.econsig.persistence.entity.SolicitacaoAutorizacaoHome;
import com.zetra.econsig.persistence.query.validardocumento.AuditoriaValidarDocumentosQuery;
import com.zetra.econsig.persistence.query.validardocumento.ListaContratosValidarDocumentosQuery;
import com.zetra.econsig.persistence.query.validardocumento.ListaSolicitacaoAutorizacaoValidarDocumentosQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.StatusSolicitacaoEnum;
import com.zetra.econsig.values.TipoSolicitacaoEnum;

/**
 * <p>Title: ValidarDocumentoControllerBean</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class ValidarDocumentoControllerBean implements ValidarDocumentoController {

    @Override
    public List<TransferObject> listarContratosStatusSolicitacaoIniFimPeriodo(String ssoCodigo, Date periodo, AcessoSistema responsavel) throws ValidarDocumentoControllerException{
        try {
            final ListaContratosValidarDocumentosQuery lstContratos = new ListaContratosValidarDocumentosQuery(responsavel);
            lstContratos.ssoCodigo = ssoCodigo;
            lstContratos.periodo = periodo;

            return lstContratos.executarDTO();
        } catch (final ZetraException ex) {
            throw new ValidarDocumentoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> auditoriaContratos(Date periodo, boolean incluiOrgao, AcessoSistema responsavel) throws ValidarDocumentoControllerException{
        try {
            final AuditoriaValidarDocumentosQuery lstContratos = new AuditoriaValidarDocumentosQuery();
            lstContratos.periodo = periodo;
            lstContratos.incluiOrgao = incluiOrgao;

            return lstContratos.executarDTO();
        } catch (final HQueryException ex) {
            throw new ValidarDocumentoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void aprovarContrato(String soaCodigo, String adeCodigo, String observacao, AcessoSistema responsavel) throws ValidarDocumentoControllerException{
        try {
            final SolicitacaoAutorizacao solicitacaoAutorizacao = SolicitacaoAutorizacaoHome.findByPrimaryKey(soaCodigo);
            solicitacaoAutorizacao.setSoaDataResposta(DateHelper.getSystemDatetime());
            AbstractEntityHome.update(solicitacaoAutorizacao);

            final Date periodoSolicitacao = solicitacaoAutorizacao.getSoaPeriodo();
            final Date periodoAprovacao = calculaPeriodoSolicitacao(adeCodigo, responsavel);
            SolicitacaoAutorizacaoHome.createPendenteAprovacao(adeCodigo, responsavel.getUsuCodigo(), TipoSolicitacaoEnum.SOLICITACAO_DEPENDE_AUTORIZACAO.getCodigo(),
            		StatusSolicitacaoEnum.VALIDACAO_DOCUMENTO_APROVADA.getCodigo(), null, DateHelper.getSystemDatetime(), observacao, solicitacaoAutorizacao.getOrigemSolicitacao().getOsoCodigo(),
            		periodoAprovacao);

            final List<AnexoAutorizacaoDesconto> anexos = AnexoAutorizacaoDescontoHome.lstAnexosAdePosPeriodo(adeCodigo, periodoSolicitacao);
            if ((anexos != null) && !anexos.isEmpty()) {
                for (final AnexoAutorizacaoDesconto anexo : anexos) {
                    anexo.setAadPeriodo(periodoAprovacao);
                    AbstractEntityHome.update(anexo);
                }
            }
        } catch (UpdateException | CreateException | FindException | PeriodoException ex) {
            throw new ValidarDocumentoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void reprovarContrato(String soaCodigo, String adeCodigo, String observacao, AcessoSistema responsavel) throws ValidarDocumentoControllerException{
        try {
            final SolicitacaoAutorizacao solicitacaoAutorizacao = SolicitacaoAutorizacaoHome.findByPrimaryKey(soaCodigo);
            solicitacaoAutorizacao.setSoaDataResposta(DateHelper.getSystemDatetime());
            AbstractEntityHome.update(solicitacaoAutorizacao);

            SolicitacaoAutorizacaoHome.createPendenteAprovacao(adeCodigo, responsavel.getUsuCodigo(), TipoSolicitacaoEnum.SOLICITACAO_DEPENDE_AUTORIZACAO.getCodigo(),
            		StatusSolicitacaoEnum.VALIDACAO_DOCUMENTO_REPROVADA.getCodigo(), null, DateHelper.getSystemDatetime(), observacao, solicitacaoAutorizacao.getOrigemSolicitacao().getOsoCodigo(),
            		calculaPeriodoSolicitacao(adeCodigo, responsavel));
        } catch (UpdateException | CreateException | FindException | PeriodoException ex) {
            throw new ValidarDocumentoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public SolicitacaoAutorizacao listUltSolicitacaoValidacao(String adeCodigo, AcessoSistema responsavel) throws ValidarDocumentoControllerException{
        try {
            return SolicitacaoAutorizacaoHome.findLastByAdeCodigoTisCodigo(adeCodigo, TipoSolicitacaoEnum.SOLICITACAO_DEPENDE_AUTORIZACAO.getCodigo());
        } catch (final FindException ex) {
            throw new ValidarDocumentoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void submeterContratoNovaAnalise(String soaCodigo, String adeCodigo, Date periodo, String observacao, AcessoSistema responsavel) throws ValidarDocumentoControllerException{
        try {
            final SolicitacaoAutorizacao solicitacaoAutorizacao = SolicitacaoAutorizacaoHome.findByPrimaryKey(soaCodigo);
            solicitacaoAutorizacao.setSoaDataResposta(DateHelper.getSystemDatetime());
            AbstractEntityHome.update(solicitacaoAutorizacao);

            SolicitacaoAutorizacaoHome.createPendenteAprovacao(adeCodigo, responsavel.getUsuCodigo(), TipoSolicitacaoEnum.SOLICITACAO_DEPENDE_AUTORIZACAO.getCodigo(),
            		StatusSolicitacaoEnum.PENDENTE_VALIDACAO_DOCUMENTOS.getCodigo(), null, null, observacao, solicitacaoAutorizacao.getOrigemSolicitacao().getOsoCodigo(),
            		periodo);
        } catch (UpdateException | CreateException | FindException ex) {
            throw new ValidarDocumentoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void submeterContratoAguardandoDocumentacao(String soaCodigo, String adeCodigo, Date periodo, String observacao, AcessoSistema responsavel) throws ValidarDocumentoControllerException{
        try {
            final SolicitacaoAutorizacao solicitacaoAutorizacao = SolicitacaoAutorizacaoHome.findByPrimaryKey(soaCodigo);

            SolicitacaoAutorizacaoHome.createPendenteAprovacao(adeCodigo, responsavel.getUsuCodigo(), TipoSolicitacaoEnum.SOLICITACAO_DEPENDE_AUTORIZACAO.getCodigo(),
                    StatusSolicitacaoEnum.AGUARDANDO_DOCUMENTO.getCodigo(), null, null, observacao, solicitacaoAutorizacao.getOrigemSolicitacao().getOsoCodigo(),
                    periodo);
        } catch (CreateException | FindException ex) {
            throw new ValidarDocumentoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstSolicitacaoAutorizacaoValidarDocumentos(String adeCodigo, AcessoSistema responsavel) throws ValidarDocumentoControllerException{
        try {
            final ListaSolicitacaoAutorizacaoValidarDocumentosQuery solicitacaoAutorizacao = new ListaSolicitacaoAutorizacaoValidarDocumentosQuery();
            solicitacaoAutorizacao.adeCodigo = adeCodigo;
            return solicitacaoAutorizacao.executarDTO();
        } catch (final HQueryException ex) {
            throw new ValidarDocumentoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private Date calculaPeriodoSolicitacao(String adeCodigo, AcessoSistema responsavel)	throws FindException, PeriodoException {
		final AutDesconto ade = AutDescontoHome.findByPrimaryKey(adeCodigo);
        final String orgCodigo = RegistroServidorHome.findByPrimaryKey(ade.getRegistroServidor().getRseCodigo()).getOrgCodigo();
        Date periodoAtual = PeriodoHelper.getInstance().getPeriodoAtual(orgCodigo, responsavel);
		final Date adePeriodo = DateHelper.clearHourTime(ade.getAdeAnoMesIni());

        if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_PERIODO_ACEITA_APENAS_REDUCOES, CodedValues.TPC_SIM, responsavel)) {
            final java.sql.Date periodo = PeriodoHelper.getInstance().validarAdeAnoMesIni(orgCodigo, DateHelper.toSQLDate(periodoAtual), responsavel);
            periodoAtual = new java.util.Date(periodo.getTime());
        }

		return DateHelper.dayDiff(periodoAtual, adePeriodo) >= 0 ? periodoAtual : adePeriodo;
	}
    @Override
    public List<TransferObject> auditoriaUsuarios(Date periodo, AcessoSistema responsavel) throws ValidarDocumentoControllerException{
        try {
            final AuditoriaValidarDocumentosQuery lstContratos = new AuditoriaValidarDocumentosQuery();
            lstContratos.periodo = periodo;
            lstContratos.usuarios = true;

            return lstContratos.executarDTO();
        } catch (final HQueryException ex) {
            throw new ValidarDocumentoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }
}

package com.zetra.econsig.webservice.command.entrada.v8;

import static com.zetra.econsig.webservice.CamposAPI.ANEXOS_CONSIGNACAO;
import static com.zetra.econsig.webservice.CamposAPI.STATUS_VALIDACAO;
import static com.zetra.econsig.webservice.CamposAPI.VALIDAR_DOCUMENTACAO;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.service.consignacao.EditarAnexoConsignacaoController;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.folha.PeriodoController;
import com.zetra.econsig.service.validardocumento.ValidarDocumentoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusSolicitacaoEnum;
import com.zetra.econsig.values.TipoArquivoEnum;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.command.entrada.RequisicaoExternaCommand;

/**
 * <p>Title: ConsultarValidacaoDocumentacaoCommand</p>
 * <p>Description: classe command que trata requisição externa ao eConsig de validação de documentos</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ConsultarValidacaoDocumentacaoCommand extends RequisicaoExternaCommand {

    public ConsultarValidacaoDocumentacaoCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        final ValidarDocumentoController validarDocumentoController = ApplicationContextProvider.getApplicationContext().getBean(ValidarDocumentoController.class);
        final PeriodoController periodoController = ApplicationContextProvider.getApplicationContext().getBean(PeriodoController.class);
        final ConsignanteController consignanteController = ApplicationContextProvider.getApplicationContext().getBean(ConsignanteController.class);
        final EditarAnexoConsignacaoController editarAnexoConsignacaoController = ApplicationContextProvider.getApplicationContext().getBean(EditarAnexoConsignacaoController.class);

        List<TransferObject> lstSituacaoContratos = new ArrayList<>();
        final List<Date> periodos = new ArrayList<>();

        final List<String> ssoCodigos = (List<String>) parametros.get(STATUS_VALIDACAO);
        boolean pendentes = false;
        boolean aprovados = false;

        for (final String ssoCodigo : ssoCodigos) {
            if ((StatusSolicitacaoEnum.PENDENTE_VALIDACAO_DOCUMENTOS.getCodigo().equals(ssoCodigo))) {
                pendentes = true;
            }

            if ((StatusSolicitacaoEnum.VALIDACAO_DOCUMENTO_APROVADA.getCodigo().equals(ssoCodigo))) {
                aprovados = true;
            }
        }

        final CustomTransferObject criterio = new CustomTransferObject();
        criterio.setAttribute(Columns.ORG_ATIVO, CodedValues.STS_ATIVO);

        final List<TransferObject> lstOrgaos = consignanteController.lstOrgaos(criterio, responsavel);
        final List<String> orgCodigos = new ArrayList<>();
        for (final TransferObject orgao : lstOrgaos) {
            orgCodigos.add((String) orgao.getAttribute(Columns.ORG_CODIGO));
        }

        final List<TransferObject> lstPeriodoAtual = periodoController.obtemPeriodoAtual(orgCodigos, null, responsavel);

        for (final TransferObject periodo : lstPeriodoAtual) {
            final Date periodoOrgao = (Date) periodo.getAttribute(Columns.PEX_PERIODO);
            if (!periodos.contains(periodoOrgao)) {
                periodos.add(periodoOrgao);
            }
        }

        if (pendentes) {
            lstSituacaoContratos = validarDocumentoController.listarContratosStatusSolicitacaoIniFimPeriodo(StatusSolicitacaoEnum.AGUARDANDO_DOCUMENTO.getCodigo(), null, responsavel);
            lstSituacaoContratos.addAll(validarDocumentoController.listarContratosStatusSolicitacaoIniFimPeriodo(StatusSolicitacaoEnum.VALIDACAO_DOCUMENTO_REPROVADA.getCodigo(), null, responsavel));
        }

        for (final Date periodoAtual : periodos) {
            if (aprovados) {
                lstSituacaoContratos.addAll(validarDocumentoController.listarContratosStatusSolicitacaoIniFimPeriodo(StatusSolicitacaoEnum.VALIDACAO_DOCUMENTO_APROVADA.getCodigo(), periodoAtual, responsavel));
            }
        }

        final HashMap<String, String> hashAnexos = new HashMap<>();
        final List<String> tarCodigos = new ArrayList<>();
        tarCodigos.add(TipoArquivoEnum.ARQUIVO_ANEXO_RG.getCodigo());
        tarCodigos.add(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_PAGAMENTO.getCodigo());
        tarCodigos.add(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_CONTRACHEQUE.getCodigo());
        tarCodigos.add(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_OUTRO.getCodigo());

        final List<TransferObject> lstAnexos = editarAnexoConsignacaoController.lstAnexoMaxPeriodo(tarCodigos, responsavel);
        for (final TransferObject anexo : lstAnexos) {
            final String adeCodigo = (String) anexo.getAttribute(Columns.ADE_CODIGO);
            final String tipoArquivo = (String) anexo.getAttribute(Columns.TAR_CODIGO);
            final String aadNome = (String) anexo.getAttribute(Columns.AAD_NOME);
            final String key = adeCodigo + ";" + tipoArquivo;

            if (!hashAnexos.containsKey(key)) {
                hashAnexos.put(key, aadNome);
            }
        }

        parametros.put(VALIDAR_DOCUMENTACAO, lstSituacaoContratos);
        parametros.put(ANEXOS_CONSIGNACAO, hashAnexos);
    }
}

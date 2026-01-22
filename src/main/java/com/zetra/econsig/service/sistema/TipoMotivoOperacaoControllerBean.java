package com.zetra.econsig.service.sistema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.TipoMotivoOperacaoTransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.exception.TipoMotivoOperacaoControllerException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.FuncaoExigeMotivo;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.Acao;
import com.zetra.econsig.persistence.entity.OcorrenciaAutorizacao;
import com.zetra.econsig.persistence.entity.OcorrenciaAutorizacaoHome;
import com.zetra.econsig.persistence.entity.TipoEntidade;
import com.zetra.econsig.persistence.entity.TipoEntidadeHome;
import com.zetra.econsig.persistence.entity.TipoMotivoOperacao;
import com.zetra.econsig.persistence.entity.TipoMotivoOperacaoHome;
import com.zetra.econsig.persistence.query.admin.ListaMotivoOperacaoQuery;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.values.AcaoEnum;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: TipoMotivoOperacaoControllerBean</p>
 * <p>Description: Implementação do controller para gerenciamento de tipo de motivo da operacao</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class TipoMotivoOperacaoControllerBean implements TipoMotivoOperacaoController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(TipoMotivoOperacaoControllerBean.class);

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Override
    public String createMotivoOperacao(TipoMotivoOperacaoTransferObject motivoOperacao, AcessoSistema responsavel) throws TipoMotivoOperacaoControllerException {
        String motivoOperacaoCodigo = null;
        try {
            // Verifica se não existe outro tipo motivo operação com o mesmo ID
            TipoMotivoOperacaoTransferObject motivoOperacaoOutro = new TipoMotivoOperacaoTransferObject();
            motivoOperacaoOutro.setTmoIdentificador(motivoOperacao.getTmoIdentificador());

            boolean existe = false;
            try {
                findMotivoOperacaoByCodIdent(motivoOperacaoOutro, responsavel);
                existe = true;
            } catch (TipoMotivoOperacaoControllerException ex) {
            }
            if (existe) {
                throw new TipoMotivoOperacaoControllerException("mensagem.erro.nao.possivel.inserir.tipo.motivo.reclamacao.existe.outro.mesma.descricao", responsavel);
            }

            TipoMotivoOperacao motivoOperacaoBean = TipoMotivoOperacaoHome.create(motivoOperacao.getTmoDescricao(), motivoOperacao.getTmoIdentificador(), motivoOperacao.getTenCodigo(), CodedValues.STS_ATIVO, motivoOperacao.getTmoExigeObs(), motivoOperacao.getTmoDecisaoJudicial());
            motivoOperacaoCodigo = motivoOperacaoBean.getTmoCodigo();
            LogDelegate log = new LogDelegate(responsavel, Log.TIPO_MOTIVO_OPERACAO, Log.CREATE, Log.LOG_INFORMACAO);
            log.setTipoMotivoOperacao(motivoOperacaoCodigo);
            log.setTipoEntidade(motivoOperacao.getTenCodigo());
            log.getUpdatedFields(motivoOperacao.getAtributos(), null);
            log.write();

            FuncaoExigeMotivo.getInstance().reset();

        } catch (LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new TipoMotivoOperacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            TipoMotivoOperacaoControllerException excecao = new TipoMotivoOperacaoControllerException("mensagem.erro.nao.possivel.criar.tipo.motivo.reclamacao.excecao.getmessage.arg0", responsavel, "<br>" + ApplicationResourcesHelper.getMessage("mensagem.erro.interno.argumento", responsavel, ex.getMessage()));
            if (ex.getMessage().indexOf("Invalid argument value") != -1) {
                excecao = new TipoMotivoOperacaoControllerException("mensagem.erro.nao.possivel.criar.tipo.motivo.reclamacao.existe.outro.mesmo.codigo.sistema", responsavel);
            }
            throw excecao;
        }
        return motivoOperacaoCodigo;
    }

    @Override
    public TipoMotivoOperacaoTransferObject findMotivoOperacao(String tmoCodigo, AcessoSistema responsavel) throws TipoMotivoOperacaoControllerException {
        TipoMotivoOperacaoTransferObject motivoOperacao = new TipoMotivoOperacaoTransferObject(tmoCodigo);
        return findMotivoOperacao(motivoOperacao, responsavel);
    }

    @Override
    public TipoMotivoOperacaoTransferObject findMotivoOperacaoByCodIdent(String tmoIdentificador, AcessoSistema responsavel) throws TipoMotivoOperacaoControllerException {
        TipoMotivoOperacaoTransferObject motivoOperacao = new TipoMotivoOperacaoTransferObject();
        motivoOperacao.setTmoIdentificador(tmoIdentificador);
        return findMotivoOperacao(motivoOperacao, responsavel);
    }

    @Override
    public TipoMotivoOperacaoTransferObject findMotivoOperacao(TipoMotivoOperacaoTransferObject motivoOperacao, AcessoSistema responsavel) throws TipoMotivoOperacaoControllerException {
        return setMotivoOperacaoValues(findMotivoOperacaoByCodIdent(motivoOperacao, responsavel));
    }

    @Override
    public List<TipoMotivoOperacaoTransferObject> findByTmoExigeObsObrigatorio(AcessoSistema responsavel) throws TipoMotivoOperacaoControllerException {
        List<TipoMotivoOperacaoTransferObject> retorno = null;

        try {
            List<TipoMotivoOperacao> lstTmoExigeObs = TipoMotivoOperacaoHome.findByTmoExigeObsObrigatorio();

            if (lstTmoExigeObs != null && !lstTmoExigeObs.isEmpty()) {
                retorno = new ArrayList<>();
                for (TipoMotivoOperacao tipoMotivoOperacao : lstTmoExigeObs) {
                    retorno.add(setMotivoOperacaoValues(tipoMotivoOperacao));
                }
            }

        } catch (FindException ex) {
            throw new TipoMotivoOperacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
        return retorno;
    }

    /**
     * Recupera o Tipo de Motivo da Operacao primeiramente pelo código informado.
     * Se não encontrar pelo código, busca pelo identificador.
     * Caso não seja encontrado o tipo de motivo da operacao, levanta uma exceção.
     *
     * @param motivoOperacao TransferObject com os dados do tipo de motivo da operacao
     * @param responsavel Responsável pela operação
     * @return Retorna uma tipo de motivo da operacao
     * @throws TipoMotivoOperacaoControllerException
     */
    private TipoMotivoOperacao findMotivoOperacaoByCodIdent(TipoMotivoOperacaoTransferObject motivoOperacao, AcessoSistema responsavel) throws TipoMotivoOperacaoControllerException {
        TipoMotivoOperacao motivoOperacaoBean = null;
        if (motivoOperacao.getTmoCodigo() != null) {
            try {
                motivoOperacaoBean = TipoMotivoOperacaoHome.findByPrimaryKey(motivoOperacao.getTmoCodigo());
            } catch (FindException ex) {
            }
        }
        if (motivoOperacaoBean == null && motivoOperacao.getTmoIdentificador() != null) {
            try {
                motivoOperacaoBean = TipoMotivoOperacaoHome.findByIdn(motivoOperacao.getTmoIdentificador());
            } catch (FindException ex) {
            }
        }
        if (motivoOperacaoBean == null) {
            throw new TipoMotivoOperacaoControllerException("mensagem.erro.tipo.motivo.nao.encontrado", responsavel);
        }

        return motivoOperacaoBean;
    }

    @Override
    public void removeMotivoOperacao(TipoMotivoOperacaoTransferObject motivoOperacao, AcessoSistema responsavel) throws TipoMotivoOperacaoControllerException {
        try {
            TipoMotivoOperacao motivoOperacaoBean = findMotivoOperacaoByCodIdent(motivoOperacao, responsavel);
            String motivoOperacaoCodigo = motivoOperacaoBean.getTmoCodigo();
            TipoMotivoOperacaoHome.remove(motivoOperacaoBean);

            LogDelegate log = new LogDelegate(responsavel, Log.TIPO_MOTIVO_OPERACAO, Log.DELETE, Log.LOG_INFORMACAO);
            log.setTipoMotivoOperacao(motivoOperacaoCodigo);
            log.getUpdatedFields(motivoOperacao.getAtributos(), null);
            log.write();

            FuncaoExigeMotivo.getInstance().reset();

        } catch (LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new TipoMotivoOperacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (RemoveException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new TipoMotivoOperacaoControllerException("mensagem.erro.nao.possivel.excluir.tipo.motivo.reclamacao.pois.possui.dependentes", responsavel);
        }

    }

    @Override
    public List<TransferObject> lstMotivoOperacaoConsignacao(Short tmoAtivo, AcessoSistema responsavel) throws TipoMotivoOperacaoControllerException {
        List<String> tenCodigos = new ArrayList<>();
        tenCodigos.add(Log.GERAL);
        tenCodigos.add(Log.AUTORIZACAO);
        return lstMotivoOperacao(tenCodigos, tmoAtivo, responsavel);
    }

    @Override
    public List<TransferObject> lstMotivoOperacaoUsuario(Short tmoAtivo, AcessoSistema responsavel) throws TipoMotivoOperacaoControllerException {
        List<String> tenCodigos = new ArrayList<>();
        tenCodigos.add(Log.GERAL);
        tenCodigos.add(Log.USUARIO);
        return lstMotivoOperacao(tenCodigos, tmoAtivo, responsavel);
    }

    @Override
    public List<TransferObject> lstMotivoOperacaoRegistroServidor(Short tmoAtivo, AcessoSistema responsavel) throws TipoMotivoOperacaoControllerException {
        List<String> tenCodigos = new ArrayList<>();
        tenCodigos.add(Log.GERAL);
        tenCodigos.add(Log.REGISTRO_SERVIDOR);
        return lstMotivoOperacao(tenCodigos, tmoAtivo, responsavel);
    }

    @Override
    public List<TransferObject> lstMotivoOperacaoServico(Short tmoAtivo, AcessoSistema responsavel) throws TipoMotivoOperacaoControllerException {
        List<String> tenCodigos = new ArrayList<>();
        tenCodigos.add(Log.GERAL);
        tenCodigos.add(Log.SERVICO);
        return lstMotivoOperacao(tenCodigos, tmoAtivo, responsavel);
    }

    @Override
    public List<TransferObject> lstMotivoOperacaoConvenio(Short tmoAtivo, AcessoSistema responsavel) throws TipoMotivoOperacaoControllerException {
        List<String> tenCodigos = new ArrayList<>();
        tenCodigos.add(Log.GERAL);
        tenCodigos.add(Log.CONVENIO);
        return lstMotivoOperacao(tenCodigos, tmoAtivo, responsavel);
    }

    @Override
    public List<TransferObject> lstMotivoOperacaoDispensaValidacaoDigital(Short tmoAtivo, AcessoSistema responsavel) throws TipoMotivoOperacaoControllerException {
        List<String> tenCodigos = new ArrayList<>();
        tenCodigos.add(Log.DISPENSA_VALIDACAO_DIGITAL);
        return lstMotivoOperacao(tenCodigos, tmoAtivo, responsavel);
    }

    @Override
    public List<TransferObject> lstMotivoOperacaoConsignataria(Short tmoAtivo, AcessoSistema responsavel) throws TipoMotivoOperacaoControllerException {
        List<String> tenCodigos = new ArrayList<>();
        tenCodigos.add(Log.CONSIGNATARIA);
        return lstMotivoOperacao(tenCodigos, tmoAtivo, responsavel);
    }

    @Override
    public List<TransferObject> lstMotivoOperacao(List<String> tenCodigos, Short tmoAtivo, AcessoSistema responsavel) throws TipoMotivoOperacaoControllerException {
        try {
            ListaMotivoOperacaoQuery query = new ListaMotivoOperacaoQuery();
            query.tenCodigos = tenCodigos;
            query.tmoAtivo = tmoAtivo;
            return query.executarDTO();
        } catch (HQueryException ex) {
            throw new TipoMotivoOperacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private TipoMotivoOperacaoTransferObject setMotivoOperacaoValues(TipoMotivoOperacao motivoOperacaoBean) {
        TipoMotivoOperacaoTransferObject motivoOperacao = new TipoMotivoOperacaoTransferObject(motivoOperacaoBean.getTmoCodigo());
        motivoOperacao.setTmoDescricao(motivoOperacaoBean.getTmoDescricao());
        motivoOperacao.setTmoIdentificador(motivoOperacaoBean.getTmoIdentificador());
        motivoOperacao.setTmoAtivo(motivoOperacaoBean.getTmoAtivo());
        motivoOperacao.setTenCodigo(motivoOperacaoBean.getTipoEntidade().getTenCodigo());
        motivoOperacao.setTmoExigeObs(motivoOperacaoBean.getTmoExigeObs());
        motivoOperacao.setTmoDecisalJudicial(motivoOperacaoBean.getTmoDecisaoJudicial());

        return motivoOperacao;
    }

    @Override
    public void updateMotivoOperacao(TipoMotivoOperacaoTransferObject motivoOperacao, AcessoSistema responsavel) throws TipoMotivoOperacaoControllerException {

        try {
            TipoMotivoOperacao motivoOperacaoBean = findMotivoOperacaoByCodIdent(motivoOperacao, responsavel);
            LogDelegate log = new LogDelegate(responsavel, Log.TIPO_MOTIVO_OPERACAO, Log.UPDATE, Log.LOG_INFORMACAO);
            log.setTipoMotivoOperacao(motivoOperacaoBean.getTmoCodigo());
            log.getUpdatedFields(motivoOperacao.getAtributos(), null);

            /* Compara a versão do cache com a passada por parâmetro */
            TipoMotivoOperacaoTransferObject motivoOperacaoCache = setMotivoOperacaoValues(motivoOperacaoBean);
            CustomTransferObject merge = log.getUpdatedFields(motivoOperacao.getAtributos(), motivoOperacaoCache.getAtributos());

            if (merge.getAtributos().containsKey(Columns.TMO_IDENTIFICADOR)) {

                // Verifica se não existe outro tipo motivo operação com o mesmo ID
                TipoMotivoOperacaoTransferObject motivoOperacaoOutro = new TipoMotivoOperacaoTransferObject();
                motivoOperacaoOutro.setTmoIdentificador((String) merge.getAttribute(Columns.TMO_IDENTIFICADOR));

                boolean existe = false;
                try {
                    findMotivoOperacaoByCodIdent(motivoOperacaoOutro, responsavel);
                    existe = true;
                } catch (TipoMotivoOperacaoControllerException ex) {
                }
                if (existe) {
                    throw new TipoMotivoOperacaoControllerException("mensagem.erro.nao.possivel.criar.tipo.motivo.reclamacao.existe.outro.mesmo.codigo.sistema", responsavel);
                }
                motivoOperacaoBean.setTmoIdentificador((String) merge.getAttribute(Columns.TMO_IDENTIFICADOR));
            }

            if (merge.getAtributos().containsKey(Columns.TMO_DESCRICAO)) {
                motivoOperacaoBean.setTmoDescricao((String) merge.getAttribute(Columns.TMO_DESCRICAO));
            }

            if (merge.getAtributos().containsKey(Columns.TMO_ATIVO)) {
                motivoOperacaoBean.setTmoAtivo((Short) merge.getAttribute(Columns.TMO_ATIVO));
            }

            if (merge.getAtributos().containsKey(Columns.TMO_TEN_CODIGO)) {
                TipoEntidade tipoEntidade = TipoEntidadeHome.findByPrimaryKey((String) merge.getAttribute(Columns.TMO_TEN_CODIGO));
                motivoOperacaoBean.setTipoEntidade(tipoEntidade);
                if (!TextHelper.isNull(merge.getAttribute(Columns.TMO_TEN_CODIGO))) {
                    log.setTipoEntidade((String) merge.getAttribute(Columns.TMO_TEN_CODIGO));
                }
            }

            if (merge.getAtributos().containsKey(Columns.TMO_EXIGE_OBS)) {
                motivoOperacaoBean.setTmoExigeObs((String) merge.getAttribute(Columns.TMO_EXIGE_OBS));
            }

            if (merge.getAtributos().containsKey(Columns.TMO_DECISAO_JUDICIAL)) {
                motivoOperacaoBean.setTmoDecisaoJudicial((String) merge.getAttribute(Columns.TMO_DECISAO_JUDICIAL));
            }

            TipoMotivoOperacaoHome.update(motivoOperacaoBean);

            FuncaoExigeMotivo.getInstance().reset();

            log.write();
        } catch (UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new TipoMotivoOperacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new TipoMotivoOperacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new TipoMotivoOperacaoControllerException("mensagem.erro.nao.possivel.encontrar.tipo.entidade", responsavel, ex);
        }
    }

    @Override
    public void gravarMotivoOperacaoConsignacao(CustomTransferObject dadosOperacao, AcessoSistema responsavel) throws TipoMotivoOperacaoControllerException {
        try {
            String strAdeCodigo = dadosOperacao.getAttribute(Columns.ADE_CODIGO) != null ? dadosOperacao.getAttribute(Columns.ADE_CODIGO).toString() : null;
            String strOcaCodigo = dadosOperacao.getAttribute(Columns.OCA_CODIGO) != null ? dadosOperacao.getAttribute(Columns.OCA_CODIGO).toString() : null;
            String strTmoCodigo = dadosOperacao.getAttribute(Columns.TMO_CODIGO) != null ? dadosOperacao.getAttribute(Columns.TMO_CODIGO).toString() : null;
            String strTmoIdentificador = dadosOperacao.getAttribute(Columns.TMO_IDENTIFICADOR) != null ? dadosOperacao.getAttribute(Columns.TMO_IDENTIFICADOR).toString() : null;
            String strOcaObservacao = dadosOperacao.getAttribute(Columns.OCA_OBS) != null ? dadosOperacao.getAttribute(Columns.OCA_OBS).toString() : "";
            Short tmoAtivo = (Short) dadosOperacao.getAttribute(Columns.TMO_ATIVO);

            // Recupera tipo motivo operacao pelo código ou pelo identificador
            TipoMotivoOperacaoTransferObject motivoOperacao = new TipoMotivoOperacaoTransferObject(strTmoCodigo);
            motivoOperacao.setTmoIdentificador(strTmoIdentificador);
            motivoOperacao.setTmoAtivo(tmoAtivo);
            TipoMotivoOperacao tipoMotivoOperacao = findMotivoOperacaoByCodIdent(motivoOperacao, responsavel);

            // Faz as alterações na ocorrencia acrescentando motivo da operacao e observações da consignatária
            // Seleciona ocorrência pelo OCA_CODIGO caso tenha sido informado,
            // se não seleciona de uma lista de ocorrências de informação ordenada pela data da ocorrência
            Object objMaxData = null;
            if (!TextHelper.isNull(strOcaCodigo)) {
                objMaxData = OcorrenciaAutorizacaoHome.findByPrimaryKey(strOcaCodigo);
            } else {
                List<OcorrenciaAutorizacao> colOcorrencia = OcorrenciaAutorizacaoHome.findByAdeTocCodigo(strAdeCodigo, CodedValues.TOC_INFORMACAO);

                // Ordena as ocorrencias do contrato em ordem crescente
                // Pega a ocorrencia mais recente após a ordenação.
                objMaxData = Collections.max(colOcorrencia, (oca1, oca2) -> oca1.getOcaData().compareTo(oca2.getOcaData()));
            }

            // Salva a observação da consignatária com tipo de motivo da operacao
            OcorrenciaAutorizacao ocaMaxData = (OcorrenciaAutorizacao) objMaxData;

            // define qual o período correto da ocorrência, se houver agrupamento de períodos
            java.util.Date ocaPeriodo = null;
            if ((ParamSist.paramEquals(CodedValues.TPC_PERMITE_AGRUPAR_PERIODOS_EXPORTACAO, CodedValues.TPC_SIM, responsavel) && ParamSist.paramEquals(CodedValues.TPC_PERMITE_ESCOLHER_PERIODO_EM_AGRUPAMENTO, CodedValues.TPC_SIM, responsavel)) || ParamSist.paramEquals(CodedValues.TPC_HABILITA_EXTENSAO_PERIODO_FOLHA_AJUSTES, CodedValues.TPC_SIM, responsavel)) {
                if (tipoMotivoOperacao != null && !TextHelper.isNull(dadosOperacao.getAttribute(Columns.OCA_PERIODO))) {
                    ocaPeriodo = DateHelper.parse(dadosOperacao.getAttribute(Columns.OCA_PERIODO).toString(), "yyyy-MM-dd");
                }
            }

            ocaMaxData.setOcaObs(ocaMaxData.getOcaObs() + ((ocaMaxData.getOcaObs().lastIndexOf(".") == ocaMaxData.getOcaObs().length() - 1) ? " " : ". ") + strOcaObservacao);
            ocaMaxData.setTipoMotivoOperacao(tipoMotivoOperacao);
            if (ocaPeriodo != null) {
                ocaMaxData.setOcaPeriodo(ocaPeriodo);
            }
            OcorrenciaAutorizacaoHome.update(ocaMaxData);

            // Executar ação definida no motivo de operação
            executarAcao(tipoMotivoOperacao.getAcao(), dadosOperacao, responsavel);

        } catch (Exception ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new TipoMotivoOperacaoControllerException("mensagem.erro.nao.possivel.inserir.motivo.operacao", responsavel, ex);
        }
    }

    /**
     * Executa ações de acordo com o motivo de operação informado pelo usuário. Serve tanto
     * a operações sobre consignações quanto usuário, porém atualmente só existem ações
     * para operações de suspensão e reativação de contratos.
     * @param acao
     * @param dadosOperacao
     * @param responsavel
     * @throws AutorizacaoControllerException
     */
    private void executarAcao(Acao acao, CustomTransferObject dadosOperacao, AcessoSistema responsavel) throws AutorizacaoControllerException {
        if (acao != null) {
            // Recupera Enum de Ação para comparar com as ações já programadas
            AcaoEnum acaoOperacao = AcaoEnum.recuperaAcao(acao.getAcaCodigo());
            // Recupera a Função executada pelo usuário, já que a ação está relacionada à operação
            String funCodigo = responsavel.getFunCodigo();
            // Recupera a Aut Desconto, estará preenchida se a ação foi disparada para operações sobre consignações
            String adeCodigo = (String) dadosOperacao.getAttribute(Columns.ADE_CODIGO);

            if (!TextHelper.isNull(adeCodigo) && !TextHelper.isNull(funCodigo)) {
                TransferObject dadosAutorizacao = pesquisarConsignacaoController.findAutDesconto(adeCodigo, responsavel);

                if (ParamSist.paramEquals(CodedValues.TPC_RETEM_MARGEM_REVISAO_ACAO_SUSPENSAO, CodedValues.TPC_SIM, responsavel)) {
                    if (acaoOperacao.equals(AcaoEnum.RETER_VALOR_REVISAO_MARGEM) && funCodigo.equals(CodedValues.FUN_SUSP_CONSIGNACAO)) {
                        // Ação de retenção do valor do contrato para revisão de margem na suspensão de contratos: recupera
                        // o valor da autorização para registrar o dado
                        String dadValor = dadosAutorizacao.getAttribute(Columns.ADE_VLR).toString();
                        autorizacaoController.setDadoAutDesconto(adeCodigo, CodedValues.TDA_VALOR_RETIDO_REVISAO_MARGEM, dadValor, responsavel);

                    } else if (acaoOperacao.equals(AcaoEnum.LIBERAR_VALOR_RETIDO_REVISAO_MARGEM) && funCodigo.equals(CodedValues.FUN_REAT_CONSIGNACAO)) {
                        // Ação de liberação do valor retido para revisão na reativação dos contratos: seta dad_valor
                        // para NULL que a rotina de gravação irá removê-lo e gerar log.
                        autorizacaoController.setDadoAutDesconto(adeCodigo, CodedValues.TDA_VALOR_RETIDO_REVISAO_MARGEM, null, responsavel);
                    }
                }
            }
        }
    }

    @Override
    public List<TransferObject> lstMotivoOperacaoAcao(List<String> tenCodigos, Short tmoAtivo, String acaCodigo, AcessoSistema responsavel) throws TipoMotivoOperacaoControllerException {
        try {
            ListaMotivoOperacaoQuery query = new ListaMotivoOperacaoQuery();
            query.tenCodigos = tenCodigos;
            query.tmoAtivo = tmoAtivo;
            query.acaCodigo = acaCodigo;
            return query.executarDTO();
        } catch (HQueryException ex) {
            throw new TipoMotivoOperacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstMotivoOperacao(String tmoCodigo, AcessoSistema responsavel) throws TipoMotivoOperacaoControllerException {
        try {
            ListaMotivoOperacaoQuery query = new ListaMotivoOperacaoQuery();
            query.tmoCodigo = tmoCodigo;
            return query.executarDTO();
        } catch (HQueryException ex) {
            throw new TipoMotivoOperacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstMotivoOperacaoBeneficioSaude(Short tmoAtivo, AcessoSistema responsavel) throws TipoMotivoOperacaoControllerException {
        List<String> tenCodigos = new ArrayList<>();
        tenCodigos.add(Log.CONTRATO_BENEFICIO_TITULAR);
        tenCodigos.add(Log.CONTRATO_BENEFICIO_DEPENDENTE);
        tenCodigos.add(Log.CONTRATO_BENEFICIO_AGREGADO);
        return lstMotivoOperacao(tenCodigos, tmoAtivo, responsavel);
    }
}

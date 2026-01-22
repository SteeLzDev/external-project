package com.zetra.econsig.service.consignacao;

import static com.zetra.econsig.values.ApplicationResourcesKeys.MENSAGEM_USUARIO_SEM_PERMISSAO_AUTORIZAR_OPERACAO;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.persistence.entity.AnexoAutorizacaoDesconto;
import com.zetra.econsig.persistence.entity.AnexoAutorizacaoDescontoHome;
import com.zetra.econsig.persistence.entity.AnexoAutorizacaoDescontoId;
import com.zetra.econsig.persistence.entity.UsuarioHome;
import com.zetra.econsig.persistence.query.anexo.ListaAnexoAutorizacaoDescontoQuery;
import com.zetra.econsig.persistence.query.anexo.ListaAnexoMaxPeriodoQuery;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.simulacao.SimulacaoController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.TipoArquivoEnum;

/**
 * <p>Title: EditarAnexoConsignacaoControllerBean</p>
 * <p>Description: Session Bean para operação de edição de anexo de consignação.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@SuppressWarnings("java:S899")
@Service
@Transactional
public class EditarAnexoConsignacaoControllerBean implements EditarAnexoConsignacaoController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EditarAnexoConsignacaoControllerBean.class);

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private SimulacaoController simulacaoController;

    @Autowired
    private UsuarioController usuarioController;

    @Override
    public AnexoAutorizacaoDesconto findAnexoAutorizacaoDesconto(String adeCodigo, String aadNome, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            return AnexoAutorizacaoDescontoHome.findByPrimaryKey(new AnexoAutorizacaoDescontoId(adeCodigo, aadNome));
        } catch (FindException ex) {
            return null;
        }
    }

    @Override
    public int countAnexoAutorizacaoDesconto(String adeCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        CustomTransferObject cto = new CustomTransferObject();
        cto.setAttribute(Columns.AAD_ADE_CODIGO, adeCodigo);
        return countAnexoAutorizacaoDesconto(cto, responsavel);
    }

    @Override
    public int countAnexoAutorizacaoDesconto(CustomTransferObject cto, AcessoSistema responsavel) throws AutorizacaoControllerException {
        String adeCodigo = (cto != null ? (String) cto.getAttribute(Columns.AAD_ADE_CODIGO) : null);
        if (TextHelper.isNull(adeCodigo)) {
            throw new AutorizacaoControllerException("mensagem.erro.codigo.contrato.obrigatorio", responsavel);
        }
        try {
            adicionaClausulaBuscaAnexos(cto, responsavel);
            ListaAnexoAutorizacaoDescontoQuery query = new ListaAnexoAutorizacaoDescontoQuery();
            query.count = true;
            query.adeCodigo = adeCodigo;
            query.arquivado = (cto.getAttribute("arquivado") != null && cto.getAttribute("arquivado").equals("S"));
            query.responsavel = responsavel;

            if (cto.getAttribute(Columns.AAD_TAR_CODIGO) != null) {
                if (cto.getAttribute(Columns.AAD_TAR_CODIGO) instanceof List<?>) {
                    query.tarCodigos = (List<String>) cto.getAttribute(Columns.AAD_TAR_CODIGO);
                } else {
                    query.tarCodigo = cto.getAttribute(Columns.AAD_TAR_CODIGO).toString();
                }
            }

            if (cto.getAttribute(Columns.AAD_ATIVO) != null) {
                query.aadAtivo = (Short) cto.getAttribute(Columns.AAD_ATIVO);
            }
            if (cto.getAttribute(Columns.AAD_NOME) != null) {
                query.aadNome = (String) cto.getAttribute(Columns.AAD_NOME);
            }

            return query.executarContador();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }


    @Override
    public List<TransferObject> lstAnexoAutorizacaoDesconto(CustomTransferObject cto, int offset, int rows, AcessoSistema responsavel) throws AutorizacaoControllerException {
        String adeCodigo = (cto != null ? (String) cto.getAttribute(Columns.AAD_ADE_CODIGO) : null);
        if (TextHelper.isNull(adeCodigo)) {
            throw new AutorizacaoControllerException("mensagem.erro.codigo.contrato.obrigatorio", responsavel);
        }
        try {
            adicionaClausulaBuscaAnexos(cto, responsavel);
            ListaAnexoAutorizacaoDescontoQuery query = new ListaAnexoAutorizacaoDescontoQuery();
            query.adeCodigo = adeCodigo;
            query.arquivado = (cto.getAttribute("arquivado") != null && cto.getAttribute("arquivado").equals("S"));
            query.responsavel = responsavel;

            if (offset != -1) {
                query.firstResult = offset;
            }
            if (rows != -1) {
                query.maxResults = rows;
            }

            if (cto.getAttribute(Columns.AAD_TAR_CODIGO) != null) {
                if (cto.getAttribute(Columns.AAD_TAR_CODIGO) instanceof List<?>) {
                    query.tarCodigos = (List<String>) cto.getAttribute(Columns.AAD_TAR_CODIGO);
                } else {
                    query.tarCodigo = cto.getAttribute(Columns.AAD_TAR_CODIGO).toString();
                }
            }

            if (cto.getAttribute(Columns.AAD_ATIVO) != null) {
                query.aadAtivo = (Short) cto.getAttribute(Columns.AAD_ATIVO);
            }
            if (cto.getAttribute(Columns.AAD_NOME) != null) {
                query.aadNome = (String) cto.getAttribute(Columns.AAD_NOME);
            }

            return query.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Se é servidor, e os anexos do saldo só são enviados por e-mail, ou
     * é correspondente e os anexos não são exibidos, então adiciona cláusula
     * para não retornar os anexos destes tipos.
     * @param cto
     * @param responsavel
     * @return
     */
    private CustomTransferObject adicionaClausulaBuscaAnexos(CustomTransferObject cto, AcessoSistema responsavel) {
        if ((responsavel.isSer() && ParamSist.paramEquals(CodedValues.TPC_EMAIL_ANEXO_SALDO_DEVEDOR_SERVIDOR, CodedValues.ANEXO_SALDO_DEVEDOR_SERVIDOR_ENVIA_EMAIL, responsavel)) ||
                (responsavel.isCor() && ParamSist.paramEquals(CodedValues.TPC_EXIBE_ANEXOS_SALDO_DEVEDOR_CORRESPONDENTE, CodedValues.TPC_NAO, responsavel))) {
            List<String> tarCodigos = new ArrayList<>();
            tarCodigos.add(CodedValues.NOT_EQUAL_KEY);
            tarCodigos.add(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_DSD.getCodigo());
            tarCodigos.add(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_BOLETO.getCodigo());
            cto.setAttribute(Columns.AAD_TAR_CODIGO, tarCodigos);
        }

        return cto;
    }
    @Override
    public void createAnexoAutorizacaoDesconto(String adeCodigo, String aadNome, String aadDescricao, java.sql.Date aadPeriodo, TipoArquivoEnum tipoArquivo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        createAnexoAutorizacaoDesconto(adeCodigo, aadNome, aadDescricao, aadPeriodo, tipoArquivo, "S", "S", "S", "S", "S", "S", responsavel);
    }

    @Override
    public void createAnexoAutorizacaoDesconto(String adeCodigo, String aadNome, String aadDescricao, java.sql.Date aadPeriodo, TipoArquivoEnum tipoArquivo, String aadExibeSup, String aadExibeCse, String aadExibeOrg, String aadExibeCsa, String aadExibeCor, String aadExibeSer, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {

            // DESENV-15630: No sistema hoje existem vários lugarem que não permitem o processamento ou até o download quando o arquivo contém ".."
            // sendo assim, também estamos criando essa validação no momento de criar o upload.
            if (!TextHelper.isNull(aadNome) && aadNome.indexOf("..") != -1) {
                throw new AutorizacaoControllerException("mensagem.erro.upload.nome.arquivo.invalido", responsavel, aadNome);
            }

            TransferObject adeTO = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);

            //verifica se pode editar a ADE como dono ou por relacionamento de compra
            if (!autorizacaoController.usuarioPodeConsultarAde(adeCodigo, responsavel)){
                throw new AutorizacaoControllerException(MENSAGEM_USUARIO_SEM_PERMISSAO_AUTORIZAR_OPERACAO, responsavel);
            }

            // Verifica se o arquivo anexo existe (já foi feito o upload)
            String caminho = ParamSist.getDiretorioRaizArquivos() + File.separatorChar + "anexo" + File.separatorChar +  DateHelper.format((Date)adeTO.getAttribute(Columns.ADE_DATA), "yyyyMMdd") + File.separatorChar + adeCodigo;
            File arquivoAnexo = new File(caminho + File.separatorChar + aadNome);
            if (arquivoAnexo == null || !arquivoAnexo.exists()) {
                throw new AutorizacaoControllerException("mensagem.erro.interno.anexo.nao.copiado", responsavel);
            }

            if (tipoArquivo == TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_CREDITO_ELETRONICO) {
            	// Valida a quantidade máxima de anexos permitidos por contrato
                String paramQtdeMaxArqAnexo = (String) ParamSist.getInstance().getParam(CodedValues.TPC_QTE_MAX_ARQ_ANEXO_CONTRATO_ASSINATURA_DIGITAL, responsavel);
                int qtdeMaxArqAnexo = (!TextHelper.isNull(paramQtdeMaxArqAnexo) ? Integer.parseInt(paramQtdeMaxArqAnexo) : 3);

                CustomTransferObject cto = new CustomTransferObject();
                List<String> tarCodigos = new ArrayList<>();
                tarCodigos.add(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_CREDITO_ELETRONICO.getCodigo());
                cto.setAttribute(Columns.AAD_TAR_CODIGO, tarCodigos);
                cto.setAttribute(Columns.AAD_ADE_CODIGO, adeCodigo);

                int totalAnexos = countAnexoAutorizacaoDesconto(cto, responsavel);
                if (totalAnexos >= qtdeMaxArqAnexo) {
                	Files.delete(arquivoAnexo.toPath());
                    throw new AutorizacaoControllerException("mensagem.erro.quantidade.maxima.anexos.por.contrato.atingida", responsavel);
                }
            } else {
            	// Valida a quantidade máxima de anexos permitidos por contrato
                String paramQtdeMaxArqAnexo = (String) ParamSist.getInstance().getParam(CodedValues.TPC_QTE_MAX_ARQ_ANEXO_CONTRATO, responsavel);
                int qtdeMaxArqAnexo = (!TextHelper.isNull(paramQtdeMaxArqAnexo) ? Integer.parseInt(paramQtdeMaxArqAnexo) : 10);
                int totalAnexos = countAnexoAutorizacaoDesconto(adeCodigo, responsavel);
                if (totalAnexos >= qtdeMaxArqAnexo) {
                	Files.delete(arquivoAnexo.toPath());
                    throw new AutorizacaoControllerException("mensagem.erro.quantidade.maxima.anexos.por.contrato.atingida", responsavel);
                }
            }

            // Define os valores padrões, caso não sejam informados
            tipoArquivo = (tipoArquivo != null ? tipoArquivo : TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_GENERICO);

            try {
                AnexoAutorizacaoDesconto anexoExistente = AnexoAutorizacaoDescontoHome.findByPrimaryKey(new AnexoAutorizacaoDescontoId(adeCodigo, aadNome));
                if (anexoExistente != null) {
                    throw new AutorizacaoControllerException("mensagem.erro.anexo.ja.existe", responsavel);
                }
            } catch (FindException ex) {
                // OK, anexo não existe, prosseguir com a criação
            }

            // Grava a ligação do anexo com o contrato
            AnexoAutorizacaoDescontoHome.create(adeCodigo, aadNome, responsavel.getUsuCodigo(), aadDescricao, aadPeriodo,
                    CodedValues.STS_ATIVO, DateHelper.getSystemDatetime(), responsavel.getIpUsuario(), tipoArquivo,
                    aadExibeSup, aadExibeCse, aadExibeOrg, aadExibeCsa, aadExibeCor, aadExibeSer);

            // Se responsável for servidor e assinatura digital está habilitada, informa altera situação da solicitação de crédito eletrônico
            if (responsavel.isSer() && ParamSist.paramEquals(CodedValues.TPC_HABILITA_ASSINATURA_DIGITAL_CONSIGNACAO, CodedValues.TPC_SIM, responsavel)) {
                simulacaoController.informarDocumentacaoCreditoEletronico(adeCodigo, responsavel);
            }

            // DESENV-13634: Verifica bloqueio de CSA por possuir contratos feitos por usuário CSA/COR sem o número mínimo de anexos exigidos
            if (ParamSist.getBoolParamSist(CodedValues.TPC_BLOQUEIA_CSA_ADE_SEM_MIN_ANEXOS, responsavel)) {
                consignatariaController.verificarDesbloqueioAutomaticoConsignataria((String) adeTO.getAttribute(Columns.CSA_CODIGO), responsavel);
            }

            // Grava log da associação do arquivo anexo
            LogDelegate log = new LogDelegate(responsavel, Log.ARQUIVO, Log.UPLOAD_FILE, Log.LOG_INFORMACAO);
            log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.inclusao.anexo.contrato.arg0", responsavel, arquivoAnexo.getAbsolutePath()));
            log.setAutorizacaoDesconto(adeCodigo);
            log.write();

        } catch (ConsignatariaControllerException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            LOG.error(e.getMessage(), e);
            throw new AutorizacaoControllerException(e.getMessageKey(), responsavel, e);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            if (e.getClass().equals(AutorizacaoControllerException.class)) {
                throw (AutorizacaoControllerException) e;
            }

            LOG.error(e.getMessage(), e);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public void updateAnexoAutorizacaoDesconto(CustomTransferObject cto, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            String adeCodigo = cto.getAttribute(Columns.AAD_ADE_CODIGO).toString();
            String tipoArquivo = (String) cto.getAttribute(Columns.AAD_TAR_CODIGO);

            TransferObject obj = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);

            // não é permitido a uma CSA/COR editar anexo de terceiros
            String csaCodigoAdes = (String) obj.getAttribute(Columns.CSA_CODIGO);
            String respCsaCodigo = (responsavel.isCsa()) ? responsavel.getCodigoEntidade():responsavel.getCodigoEntidadePai();
            if ((responsavel.isCsa() || responsavel.isCor()) && !csaCodigoAdes.equals(respCsaCodigo)) {
                throw new AutorizacaoControllerException(MENSAGEM_USUARIO_SEM_PERMISSAO_AUTORIZAR_OPERACAO, responsavel);
            }

            String aadNome = cto.getAttribute(Columns.AAD_NOME).toString();
            String usuCodigo = (cto.getAttribute(Columns.AAD_USU_CODIGO) != null)? cto.getAttribute(Columns.AAD_USU_CODIGO).toString(): null;
            String aadDescricao = (cto.getAttribute(Columns.AAD_DESCRICAO) != null)? cto.getAttribute(Columns.AAD_DESCRICAO).toString(): null;
            Short aadAtivo = (cto.getAttribute(Columns.AAD_ATIVO) != null)? (Short) cto.getAttribute(Columns.AAD_ATIVO): null;
            Date aadData = (cto.getAttribute(Columns.AAD_DATA) != null)? (Date) cto.getAttribute(Columns.AAD_DATA): null;
            Date aadPeriodo = (cto.getAttribute(Columns.AAD_PERIODO) != null)? (Date) cto.getAttribute(Columns.AAD_PERIODO): null;
            String aadIpAcesso = (cto.getAttribute(Columns.AAD_IP_ACESSO) != null)? cto.getAttribute(Columns.AAD_IP_ACESSO).toString(): null;

            AnexoAutorizacaoDescontoId aapk = new AnexoAutorizacaoDescontoId(adeCodigo, aadNome);
            AnexoAutorizacaoDesconto aad = AnexoAutorizacaoDescontoHome.findByPrimaryKey(aapk);

            if (usuCodigo != null) {
                aad.setUsuario(UsuarioHome.findByPrimaryKey(usuCodigo));
            }
            if (aadDescricao != null) {
                aad.setAadDescricao(aadDescricao);
            }
            if (aadAtivo != null) {
                aad.setAadAtivo(aadAtivo);
            }
            if (aadData != null) {
                aad.setAadData(aadData);
            }
            if (aadPeriodo != null) {
                aad.setAadPeriodo(aadPeriodo);
            }
            if (aadIpAcesso != null) {
                aad.setAadIpAcesso(aadIpAcesso);
            }
            AnexoAutorizacaoDescontoHome.update(aad);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            if (e.getClass().equals(AutorizacaoControllerException.class)) {
                throw (AutorizacaoControllerException) e;
            }

            LOG.error(e.getMessage(), e);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public void removeAnexoAutorizacaoDesconto(CustomTransferObject cto, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            boolean isEditarSaldoDeveldor = CodedValues.FUN_EDT_SALDO_DEVEDOR.equals(responsavel.getFunCodigo()) && responsavel.temPermissao(CodedValues.FUN_EDT_SALDO_DEVEDOR);

            if (!responsavel.isSer() && (!responsavel.temPermissao(CodedValues.FUN_EXCLUIR_ANEXOS_CONSIGNACAO) && !isEditarSaldoDeveldor)) {
                throw new AutorizacaoControllerException(MENSAGEM_USUARIO_SEM_PERMISSAO_AUTORIZAR_OPERACAO, responsavel);
            }

            String adeCodigo = (String) cto.getAttribute(Columns.AAD_ADE_CODIGO);
            String tipoArquivo = (String) cto.getAttribute(Columns.AAD_TAR_CODIGO);

            TransferObject obj = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);

            // não é permitido a uma CSA/COR editar anexo de terceiros
            String csaCodigoAdes = (String) obj.getAttribute(Columns.CSA_CODIGO);
            String respCsaCodigo = (responsavel.isCsa()) ? responsavel.getCodigoEntidade():responsavel.getCodigoEntidadePai();
            if ((responsavel.isCsa() || responsavel.isCor()) && !csaCodigoAdes.equals(respCsaCodigo)) {
                throw new AutorizacaoControllerException(MENSAGEM_USUARIO_SEM_PERMISSAO_AUTORIZAR_OPERACAO, responsavel);
            }

            String tarCodigo = (String) cto.getAttribute(Columns.AAD_TAR_CODIGO);
            String aadNome = (String) cto.getAttribute(Columns.AAD_NOME);

            if (!TextHelper.isNull(adeCodigo) && !TextHelper.isNull(aadNome)) {
                AnexoAutorizacaoDescontoId aapk = new AnexoAutorizacaoDescontoId(adeCodigo, aadNome);
                AnexoAutorizacaoDesconto aad = AnexoAutorizacaoDescontoHome.findByPrimaryKey(aapk);
                removerAnexo(aad, responsavel);
            } else if (!TextHelper.isNull(adeCodigo) && !TextHelper.isNull(tarCodigo)) {
                Collection<AnexoAutorizacaoDesconto> list = AnexoAutorizacaoDescontoHome.findByAdeTarCodigo(adeCodigo, tarCodigo);
                for (AnexoAutorizacaoDesconto aad : list) {
                    removerAnexo(aad, responsavel);
                }
            } else {
                throw new AutorizacaoControllerException("mensagem.erro.nenhum.registro.encontrado", responsavel);
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            if (e.getClass().equals(AutorizacaoControllerException.class)) {
                throw (AutorizacaoControllerException) e;
            }

            LOG.error(e.getMessage(), e);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public void removeAnexoAutorizacaoDescontoTemp(CustomTransferObject cto, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            if (!responsavel.isSer() && (!responsavel.temPermissao(CodedValues.FUN_EDITAR_ANEXO_CONSIGNACAO))) {
                throw new AutorizacaoControllerException(MENSAGEM_USUARIO_SEM_PERMISSAO_AUTORIZAR_OPERACAO, responsavel);
            }

            String adeCodigo = (String) cto.getAttribute(Columns.AAD_ADE_CODIGO);

            TransferObject obj = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);

            // não é permitido a uma CSA/COR editar anexo de terceiros
            String csaCodigoAdes = (String) obj.getAttribute(Columns.CSA_CODIGO);
            String respCsaCodigo = (responsavel.isCsa()) ? responsavel.getCodigoEntidade():responsavel.getCodigoEntidadePai();
            if ((responsavel.isCsa() || responsavel.isCor()) && !csaCodigoAdes.equals(respCsaCodigo)) {
                throw new AutorizacaoControllerException(MENSAGEM_USUARIO_SEM_PERMISSAO_AUTORIZAR_OPERACAO, responsavel);
            }

            String aadNome = (String) cto.getAttribute(Columns.AAD_NOME);
            String hashDir = (String) cto.getAttribute("hashDir");

            if (!TextHelper.isNull(adeCodigo) && !TextHelper.isNull(aadNome)) {
                String caminho = ParamSist.getDiretorioRaizArquivos()
                        + File.separatorChar + UploadHelper.SUBDIR_ARQUIVOS_TEMPORARIOS
                        + File.separatorChar + "anexo"
                        + File.separatorChar + hashDir;

                File arquivoAnexo = new File(caminho + File.separatorChar + aadNome);

                // Remove o arquivo caso exista
                if (arquivoAnexo.exists()) {
                	Files.delete(arquivoAnexo.toPath());
                }
            } else {
                throw new AutorizacaoControllerException("mensagem.erro.nenhum.registro.encontrado", responsavel);
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            if (e.getClass().equals(AutorizacaoControllerException.class)) {
                throw (AutorizacaoControllerException) e;
            }

            LOG.error(e.getMessage(), e);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    private void removerAnexo(AnexoAutorizacaoDesconto aad, AcessoSistema responsavel) throws RemoveException, LogControllerException, AutorizacaoControllerException {
        String adeCodigo = aad.getAdeCodigo();
        String aadNome = aad.getAadNome();
        TransferObject cto = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);

        try {
            // Verifica se o responável do arquivo ou o responsável pela exclusão do arquivo é um usuário servidor,
            // caso seja, somente o servidor pode remover seu próprio arquivo e também não pode remover de um terceiro.
            String usuCodigoOwner = aad.getUsuario().getUsuCodigo();
            TransferObject owner = usuarioController.obtemUsuarioTipo(usuCodigoOwner, null, responsavel);
            if (owner != null &&
                (owner.getAttribute("TIPO").equals(AcessoSistema.ENTIDADE_SER) || responsavel.isSer()) &&
                !usuCodigoOwner.equals(responsavel.getUsuCodigo())) {
                throw new AutorizacaoControllerException("mensagem.usuarioNaoTemPermissao", responsavel);
            }
        } catch (UsuarioControllerException e) {
            LOG.debug("Responsável pelo anexo não é usuário servidor, não valida propriedade do arquivo.");
        }

        String caminho = ParamSist.getDiretorioRaizArquivos()
                + File.separatorChar + "anexo"
                + File.separatorChar + DateHelper.format((Date)cto.getAttribute(Columns.ADE_DATA), "yyyyMMdd")
                + File.separatorChar + adeCodigo
                + File.separatorChar + aadNome
                ;
        File arquivoAnexo = new File(caminho);
        if (!arquivoAnexo.exists()) {
            caminho = ParamSist.getDiretorioRaizArquivos()
                    + File.separatorChar + "anexo"
                    + File.separatorChar + adeCodigo
                    + File.separatorChar + aadNome
                    ;
            arquivoAnexo = new File(caminho);
        }

        // Grava log da exclusão
        LogDelegate log = new LogDelegate(responsavel, Log.ARQUIVO, Log.DELETE_FILE, Log.LOG_INFORMACAO);
        log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.remocao.anexo.contrato", responsavel, arquivoAnexo.getAbsolutePath()));
        log.setAutorizacaoDesconto(adeCodigo);
        log.write();

        // Remove o registro de ligação
        AnexoAutorizacaoDescontoHome.remove(aad);

        if (!arquivoAnexo.exists()) {
            LOG.warn("Removendo ligação de anexo com consignação porém o arquivo físico não existe: " + aadNome);
        } else {
            // Remove o arquivo caso exista
            boolean removido = arquivoAnexo.delete();
            if (!removido) {
                throw new AutorizacaoControllerException("mensagem.erro.arquivo.nao.removido", responsavel, aadNome);
            }
        }
        deletarArquivoConvertido(aadNome, caminho);
    }

    @Override
    public List<AnexoAutorizacaoDesconto> lstAnexoTipoArquivoPeriodo(String adeCodigo, List<String> tarCodigos, Date periodo, AcessoSistema responsavel) throws AutorizacaoControllerException{
        try {
            return AnexoAutorizacaoDescontoHome.lstAnexosTipoArquivoPeriodo(adeCodigo, tarCodigos, periodo);
        } catch (FindException e) {
            LOG.error(e.getMessage(), e);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public List<AnexoAutorizacaoDesconto> lstAnexoTipoArquivoMaxPeriodo(String adeCodigo, List<String> tarCodigos, AcessoSistema responsavel) throws AutorizacaoControllerException{
        try {
            return AnexoAutorizacaoDescontoHome.lstAnexosTipoArquivoMaxPeriodo(adeCodigo, tarCodigos);
        } catch (FindException e) {
            LOG.error(e.getMessage(), e);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

	@Override
	public List<TransferObject> lstAnexoMaxPeriodo(List<String> tarCodigos, AcessoSistema responsavel) throws AutorizacaoControllerException {
		ListaAnexoMaxPeriodoQuery lstAadMaxPeriodoQuery = new ListaAnexoMaxPeriodoQuery();
		lstAadMaxPeriodoQuery.tarCodigos = tarCodigos;

		try {
			return lstAadMaxPeriodoQuery.executarDTO();
		} catch (HQueryException e) {
			LOG.error(e.getMessage(), e);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, e);
		}
	}

	private void deletarArquivoConvertido(String aadNome, String caminho) {
	    String extensao = FilenameUtils.getExtension(aadNome);
        String[] extensoesPermitidasPdf = {"rtf", "doc", "docx", "xls", "xlsx", "txt", "csv"};

        if(extensao.equals("wma")) {
            String caminhoConvertido =  caminho.substring(0, caminho.lastIndexOf(File.separator)) + File.separatorChar + "arquivos_convertidos"+ File.separatorChar + aadNome.substring(0, aadNome.lastIndexOf(".")) + "_convertido.mp3";
            File arquivoAnexoConvertido = new File(caminhoConvertido);
            if(arquivoAnexoConvertido.exists()) {
                arquivoAnexoConvertido.delete();
            }
        }else if(Arrays.stream(extensoesPermitidasPdf).anyMatch(extensao::equals)) {
            String caminhoConvertido =  caminho.substring(0, caminho.lastIndexOf(File.separator)) + File.separatorChar + "arquivos_convertidos"+ File.separatorChar + aadNome.substring(0, aadNome.lastIndexOf(".")) + "_convertido.pdf";
            File arquivoAnexoConvertido = new File(caminhoConvertido);
            if(arquivoAnexoConvertido.exists()) {
                arquivoAnexoConvertido.delete();
            }
        }
	}
}

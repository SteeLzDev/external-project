package com.zetra.econsig.web.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ui.Model;

import com.zetra.econsig.dto.entidade.OrgaoTransferObject;
import com.zetra.econsig.dto.web.ArquivoDownload;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.persistence.entity.AnexoAutorizacaoDesconto;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.EditarAnexoConsignacaoController;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: FileAbstractWebController</p>
 * <p>Description: Controlador Web base para implementações dos casos de uso que irão manipular downloads e uploads de arquivos.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class FileAbstractWebController extends AbstractWebController {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ArquivoDownload.class);

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    private ConsignanteController consignanteController;

    @Autowired
    private EditarAnexoConsignacaoController editarAnexoConsignacaoController;

    public FileStatus processar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws IOException {

        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        final String nomeArquivo = TextHelper.isNull(request.getParameter("arquivo_nome")) ? ApplicationResourcesHelper.getMessage("rotulo.include.get.file.desconhecido", responsavel) : request.getParameter("arquivo_nome");
        String msg = request.getAttribute("msg") != null ? request.getAttribute("msg").toString() : ApplicationResourcesHelper.getMessage("mensagem.erro.include.get.file.nao.encontrado", responsavel, nomeArquivo);

        if (!TextHelper.isNull(nomeArquivo)) {
            String tipo = request.getParameter("tipo");

            if (TextHelper.isNull(tipo)) {
                tipo = "relatorio";
            }
            if ("anexo_consignacao".equals(tipo) || "anexo_solicitacao".equals(tipo)) {
                // Editar anexo de consignação ou solicitação deve ser comportar como "anexo"
                tipo = "anexo";
            }
            if ("conciliacao_multipla".equals(tipo)) {
                // conciliacao arquivo multiplo deve comportar como conciliacao
                tipo = "conciliacao";
            }
            if ("manualFolha".equals(tipo)) {
                // download de manual de integração folha é um arquigo genérico
                tipo = "generico";
            }

            // Arquivos de integração com a folha, se for ORG com permissão de acessar consignações do estabelecimento, irá procurar arquivos na pasta /est/est_codigo ao invés de /cse/org_codigo
            final boolean arquivosFolha = ("margem".equals(tipo) || "margemcomplementar".equals(tipo) || "transferidos".equals(tipo) || "retorno".equals(tipo) || "retornoatrasado".equals(tipo) || "critica".equals(tipo) || "contracheque".equals(tipo) || "historico".equals(tipo));

            String absolutePath = ParamSist.getDiretorioRaizArquivos();
            if (absolutePath != null) {
                absolutePath = new File(absolutePath).getCanonicalPath();

                final String name = java.net.URLDecoder.decode(nomeArquivo, "UTF-8");
                if (name.indexOf("..") != -1) {
                    return new FileStatus(false, msg);
                }

                final String subtipo = JspHelper.verificaVarQryStr(request, "subtipo");
                String entidade = JspHelper.verificaVarQryStr(request, "entidade");
                if (TextHelper.isNull(entidade)) {
                    if ((responsavel.isCseSup() && arquivosFolha && (name.startsWith("cse" + File.separator) || name.startsWith("est" + File.separator))) || "beneficiario".equals(tipo)) {
                        entidade = ""; // entidade estará concatenada ao nome do arquivo
                    } else if (responsavel.isCseSup() && "alteracao_multiplas_ade".equals(subtipo) && !TextHelper.isNull(request.getParameter("codigoEntidade"))) {
                        entidade = "csa";
                    } else if ((responsavel.isCseSup() && "integracao_csa".equals(subtipo)) || "recuperacao_credito".equals(tipo)) {
                        entidade = "csa";
                    } else if (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO) && arquivosFolha) {
                        entidade = "est";
                    } else {
                        entidade = "cse";
                    }
                }
                if (responsavel.isCsaCor() && !"generico".equals(tipo) && !"anexo".equals(tipo) && !"comunicacao".equals(tipo)) {
                    entidade = responsavel.getTipoEntidade().toLowerCase();
                } else if (responsavel.isSer() && "relatorio".equals(tipo)) {
                    entidade = "ser";
                }

                String fileName = null;
                if ("xml".equals(tipo) || "xmlMargemRetornoMovimento".equals(tipo)) {
                    fileName = absolutePath + File.separatorChar + "conf";
                } else if("anexo_consignacao_temp".equals(tipo)) {
                    fileName = absolutePath + File.separatorChar + UploadHelper.SUBDIR_ARQUIVOS_TEMPORARIOS
                    + File.separatorChar + "anexo"
                    + File.separatorChar + session.getId();
                } else {
                    fileName = absolutePath + File.separatorChar + tipo;
                }

                if ("anexo".equals(tipo)) {
                    try {
                        // "entidade" será o código do contrato, verifica se o usuário tem permissão de consultar o contrato
                        if (!autorizacaoController.usuarioPodeConsultarAde(entidade, responsavel)) {
                            return new FileStatus(false, ApplicationResourcesHelper.getMessage("mensagem.erro.include.get.file.permissao.consignacao", responsavel));
                        }
                        // verifica se o anexo é visível para o usuário
                        final AnexoAutorizacaoDesconto aad = editarAnexoConsignacaoController.findAnexoAutorizacaoDesconto(entidade, name, responsavel);
                        final boolean podeVisualizar = (aad != null) && ((responsavel.isSup() && "S".equals(aad.getAadExibeSup())) || (responsavel.isCse() && "S".equals(aad.getAadExibeCse())) || (responsavel.isOrg() && "S".equals(aad.getAadExibeOrg())) || (responsavel.isCsa() && "S".equals(aad.getAadExibeCsa())) || (responsavel.isCor() && "S".equals(aad.getAadExibeCor())) || (responsavel.isSer() && "S".equals(aad.getAadExibeSer())));
                        if (!podeVisualizar) {
                            return new FileStatus(false, ApplicationResourcesHelper.getMessage("mensagem.erro.include.get.file.permissao.anexo", responsavel));
                        }
                    } catch (final Exception ex) {
                        return new FileStatus(false, ex.getMessage());
                    }
                }

                if ("anexo".equals(tipo) && (request.getParameter("data") != null)) {
                    final String tempFileName = fileName + File.separatorChar + request.getParameter("data") + File.separatorChar + entidade;
                    final File arquivo = new File(tempFileName + File.separatorChar + name);
                    fileName += File.separatorChar + entidade;

                    if (arquivo.exists() && arquivo.getCanonicalPath().startsWith(absolutePath)) {
                        fileName = tempFileName;
                    }
                } else if (!"xml".equals(tipo) && !"xmlMargemRetornoMovimento".equals(tipo) && !"copia_seguranca".equals(tipo) && !"ajuda".equals(tipo) && !"comunicacao".equals(tipo) && !"anexo_consignacao_temp".equals(tipo) && !"banner".equals(subtipo) && !"".equals(entidade)) {
                    fileName += File.separatorChar + entidade;
                }

                if ("relatorio".equals(tipo) && !subtipo.isEmpty()) {
                    fileName += File.separatorChar + (responsavel.isCseSup() && "integracao_csa".equals(subtipo) ? "integracao" : subtipo);
                }

                if ("arquivo_senhas".equals(tipo)) {
                    fileName = absolutePath + File.separatorChar + "senhaservidores";
                }

                if ("anexosEmLote".equals(tipo)) {
                    fileName = absolutePath + File.separatorChar + "anexo" + File.separatorChar + "criticalote";
                }

                if (!responsavel.isCseSupOrg() && "generico".equals(tipo) && !"cse".equals(entidade)) {
                    if (responsavel.isCsa()) {
                        fileName += File.separatorChar + responsavel.getCodigoEntidade();
                    } else if (responsavel.isCor()) {
                        fileName += File.separatorChar + responsavel.getCodigoEntidadePai();
                    }
                } else if (!responsavel.isCseSup() && !"generico".equals(tipo) && !"anexosEmLote".equals(tipo) && !"anexo".equals(tipo) && !"comunicacao".equals(tipo) && !"recuperacao_credito".equals(tipo) && !(responsavel.isOrg() && ("duplicacao".equals(tipo) || "lote".equals(tipo)))) {
                    if (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO) && arquivosFolha) {
                        fileName += File.separatorChar + responsavel.getCodigoEntidadePai();
                    } else if (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO) && "movimento".equals(tipo)) {
                        // Não precisa fazer alteração no fileName
                    } else {
                        fileName += File.separatorChar + responsavel.getCodigoEntidade();
                    }
                } else if (responsavel.isCseSup() && ("lote".equals(tipo) || "conciliacao".equals(tipo) || "duplicacao".equals(tipo) || "reajuste".equals(tipo)) && (!subtipo.isEmpty())) {
                    fileName += File.separatorChar + subtipo;
                } else if (responsavel.isOrg() && ("duplicacao".equals(tipo) || "lote".equals(tipo)) && !subtipo.isEmpty()) {
                    fileName += File.separatorChar + subtipo;
                }

                if (responsavel.isCseSupOrg() && "integracao_csa".equals(subtipo)) {
                    final String codigoEntidade = request.getParameter("codigoEntidade");
                    fileName += File.separatorChar + codigoEntidade;
                }

                if ("comunicacao".equals(tipo)) {
                    fileName += File.separatorChar + request.getParameter("data") + File.separatorChar + entidade;
                }

                if ("beneficiario".equals(tipo)) {
                    fileName += File.separatorChar + request.getParameter("data") + File.separator + request.getParameter(Columns.getColumnName(Columns.BFC_CODIGO));
                }

                if ("fatura".equals(tipo)) {
                    fileName = absolutePath + File.separatorChar + "beneficio" + File.separatorChar + "fatura" + File.separatorChar + "csa" + File.separatorChar + request.getParameter("csaCodigo") + File.separatorChar + request.getParameter("FAT_CODIGO");
                }

                if ("previa".equals(tipo)) {
                    fileName = absolutePath + File.separatorChar + "fatura" + File.separatorChar + "previa" + File.separatorChar + "csa" + File.separatorChar + request.getParameter("csaCodigo") + File.separatorChar;
                }

                if ("recuperacao_credito".equals(tipo)) {
                    fileName += File.separatorChar + request.getParameter("csaCodigo");
                }

                if ("saldodevedor".equals(tipo) && responsavel.isSup()) {
                    fileName += File.separatorChar + subtipo;
                }

                if ("anexo_credenciamento".equals(tipo) && (responsavel.isCseSup() || responsavel.isCsa())) {
                    fileName = absolutePath + File.separatorChar + "credenciamento" + File.separatorChar + "modelo";
                }

                if ("anexo_credenciamento_temp".equals(tipo)) {
                    fileName = absolutePath + File.separatorChar + UploadHelper.SUBDIR_ARQUIVOS_TEMPORARIOS + File.separatorChar + "anexo" + File.separatorChar + session.getId();
                }

                if("anexo_credenciamento_termo".equals(tipo) && (responsavel.isCsa() || responsavel.isCseSup())) {
                    final String csaCodigo = JspHelper.verificaVarQryStr(request, "csaCodigo");
                    fileName = absolutePath + File.separatorChar + "credenciamento" + File.separatorChar + "csa" + File.separatorChar + csaCodigo;
                }

                if ("relatorioCustomizadoCsa".equals(tipo)) {
                        fileName = absolutePath + File.separatorChar + "relatorio" + File.separatorChar + "csa" + File.separatorChar + "customizacoes";
                }

                if ("relatorioCustomizadoCse".equals(tipo)) {
                    fileName = absolutePath + File.separatorChar + "relatorio" + File.separatorChar + "cse" + File.separatorChar + "customizacoes";
                }

                //Download de arquivos de lote de rescisão
                if ("rescisaoLote".equals(tipo) && responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)){
                    fileName = absolutePath + File.separatorChar + "rescisao" + File.separatorChar + entidade.toLowerCase() + File.separatorChar + responsavel.getCodigoEntidadePai();
                } else if ("rescisaoLote".equals(tipo) && responsavel.isOrg() && !responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)){
                    fileName = absolutePath + File.separatorChar + "rescisao" + File.separatorChar + entidade.toLowerCase() + File.separatorChar + responsavel.getCodigoEntidade();
                } else if ("rescisaoLote".equals(tipo) && responsavel.isCseSup()){
                    fileName = absolutePath + File.separatorChar + "rescisao" + File.separatorChar + entidade.toLowerCase();
                }

                if ("reimplante".equals(tipo)){
                    fileName = absolutePath + File.separatorChar + tipo + File.separatorChar + "cse";
                }

                if ("reimplanteRelatorio".equals(tipo)){
                    fileName = absolutePath + File.separatorChar + "relatorio" + File.separatorChar + "cse" + File.separatorChar + "reimplante";
                }

                //Processa arquivos do tipo anexo de consignação
                if ("consignataria".equals(tipo)) {
                    final String csaCodigo = JspHelper.verificaVarQryStr(request, "csaCodigo");
                    fileName = absolutePath + File.separatorChar + "consignataria" + File.separatorChar + csaCodigo;
                }

                if ("cadastroConsignatarias".equals(tipo)) {
                    fileName = absolutePath + File.separatorChar + "cadastroConsignatarias" + File.separatorChar + "cse";
                }

                if ("banner".equals(subtipo)) {
                    fileName += File.separatorChar + subtipo;

                    final List<String> fileSystemObjects = FileHelper.getFilesInDir(fileName);
                    final List<File> toRemoveList = new ArrayList<>();

                    for (final String aux : fileSystemObjects) {
                        if (aux.indexOf(name) != -1) {
                            final String completo = fileName + File.separatorChar + aux;
                            final File toRemove = new File(completo);
                            toRemoveList.add(toRemove);
                        }
                    }

                    return new FileStatus(toRemoveList, true);

                } else {
                    fileName += File.separatorChar + name;

                    final File arquivo = new File(fileName);
                    if (!arquivo.exists() || !arquivo.getCanonicalPath().startsWith(absolutePath)) {
                        return new FileStatus(false, ApplicationResourcesHelper.getMessage("mensagem.erro.include.get.file.nao.encontrado", responsavel, name));
                    }

                    // DESENV-13655 -> Caso o usuário for ORGAO e tiver a função 92, confere se a pasta do arquivo, ou seja, o órgão é do mesmo estabelecimento pra segurança do caso de uso.
                    // OBS: arquivos de lote e duplicação são da CSA/COR então não dá para validar a pasta pois será um código de CSA/COR
                    if (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO) && "movimento".equals(tipo)) {
                        try {
                            final String[] partesNomeArq = fileName.split(File.separator);
                            final String pastaOrgaoDownload = partesNomeArq[partesNomeArq.length - 2];
                            if (!pastaOrgaoDownload.equals(responsavel.getOrgCodigo())) {
                                // Se o arquivo é de outro órgão, verifica se é do mesmo estabelecimento
                                final OrgaoTransferObject org = consignanteController.findOrgao(pastaOrgaoDownload, responsavel);
                                if (!org.getEstCodigo().equals(responsavel.getEstCodigo())) {
                                    return new FileStatus(false, ApplicationResourcesHelper.getMessage("mensagem.erro.include.get.file.permissao.anexo.download", responsavel, name));
                                }
                            }
                        } catch (final ConsignanteControllerException ex) {
                            LOG.error(ex.getMessage(), ex);
                            return new FileStatus(false, ApplicationResourcesHelper.getMessage("mensagem.erro.include.get.file.nao.encontrado", responsavel, name));
                        }
                    }

                    return new FileStatus(arquivo, true);
                }
            } else {
                msg += ApplicationResourcesHelper.getMessage("mensagem.erro.include.get.file.configuracao.diretorio", responsavel);
                return new FileStatus(false, msg);
            }
        } else {
            return new FileStatus(false, msg);
        }

    }

    /**
     * O método processar irá retornar um objeto com esse tipo informando se o resultado foi sucesso.
     *
     * Caso sucesso o valor do resultado será true, caso contrário, false.
     *
     * Irá retornar o arquivo na propriedade arquivo, ou uma lista de arquivos na propriedade listaArquivos.
     *
     * @author moises
     *
     */
    public class FileStatus {

        private File arquivo;

        private Boolean resultado;

        private String mensagemErro;

        private List<File> listaArquivos;

        public FileStatus(File arquivo, Boolean resultado) {
            this.arquivo = arquivo;
            this.resultado = resultado;
        }

        public FileStatus(List<File> listaArquivos, Boolean resultado) {
            this.listaArquivos = listaArquivos;
            this.resultado = resultado;
        }

        public FileStatus(Boolean resultado, String mensagemErro) {
            this.resultado = resultado;
            this.mensagemErro = mensagemErro;
        }

        public File getArquivo() {
            return arquivo;
        }

        public void setArquivo(File arquivo) {
            this.arquivo = arquivo;
        }

        public Boolean getResultado() {
            return resultado;
        }

        public void setResultado(Boolean resultado) {
            this.resultado = resultado;
        }

        public String getMensagemErro() {
            return mensagemErro;
        }

        public void setMensagemErro(String mensagemErro) {
            this.mensagemErro = mensagemErro;
        }

        public List<File> getListaArquivos() {
            return listaArquivos;
        }

        public void setListaArquivos(List<File> listaArquivos) {
            this.listaArquivos = listaArquivos;
        }

    }

}

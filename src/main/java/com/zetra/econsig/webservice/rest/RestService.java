package com.zetra.econsig.webservice.rest;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.Objects;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.dto.entidade.OrgaoTransferObject;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.ControleRestricaoAcesso;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.consignacao.EditarAnexoConsignacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.service.sistema.SistemaController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.webservice.rest.request.ConsignacaoRestRequest;
import com.zetra.econsig.webservice.rest.request.ResponseRestRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public abstract class RestService {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RestService.class);

    protected static final String RETURN_CONTENT_TYPE = MediaType.APPLICATION_JSON + "; charset=UTF-8";

    /**
     * Organiza melhor uma lista de transferObjects para gerar um melhor JSON
     *
     * @param listaOriginal
     * @param filter
     *            Caso queira incluir somente algumas propriedades, incluir no
     *            filtro.
     * @return
     */
    protected List<Map<String, Object>> transformTOs(List<? extends TransferObject> listaOriginal, List<String> filter) {
        if (listaOriginal == null) {
            return new ArrayList<>();
        }
        final List<Map<String, Object>> result = new ArrayList<>();
        for (final TransferObject tObj : listaOriginal) {
            result.add(transformTO(tObj, filter));
        }
        return result;
    }

    /**
     * Organiza melhor as informações de um TransferObject para gerar um melhor JSON
     * @param tObj
     * @param filter
     * @return
     */
    protected Map<String, Object> transformTO(TransferObject tObj, List<String> filter) {
        final Map<String, Object> propriedades = new HashMap<>();
        for (final Object key : tObj.getAtributos().keySet()) {
            String chave = (String) key;
            if (chave.indexOf(".") > 0) {
                chave = chave.substring(chave.indexOf(".") + 1);
            }
            Object valor = tObj.getAttribute((String) key);
            // Trata os campos de Timestamp/DateTime
            if (valor instanceof java.sql.Timestamp) {
                valor = DateHelper.format((Timestamp) valor, "yyyy-MM-dd HH:mm:ss");
            }
            // Trata os campos de Date
            if ((valor instanceof java.sql.Date) || (valor instanceof java.util.Date)) {
                valor = DateHelper.format((Date) valor, "yyyy-MM-dd HH:mm:ss");
            }
            if ((filter == null) || filter.contains(chave)) {
                propriedades.put(chave, valor);
            }
        }
        return propriedades;
    }

    /**
     * Cria um response 409 com a mensagem de erro retornada pela exceção
     * @param e
     * @return
     */
    protected Response genericError(Exception e) {
        final ResponseRestRequest responseError = new ResponseRestRequest();
        responseError.mensagem = e.getMessage();
        return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, RETURN_CONTENT_TYPE).build();
    }

    protected Response verificaSistemaDisponivel() {
        return verificaSistemaDisponivel(AcessoSistema.getAcessoUsuarioSistema());
    }

    protected Response verificaSistemaDisponivel(AcessoSistema responsavel) {
        final Short status;
        try {
            final SistemaController sistemaController = ApplicationContextProvider.getApplicationContext().getBean(SistemaController.class);
            status = sistemaController.verificaBloqueioSistema(CodedValues.CSE_CODIGO_SISTEMA, null);

            if (!status.equals(CodedValues.STS_ATIVO) || (ControleRestricaoAcesso.possuiRestricaoAcesso(responsavel).getGrauRestricao() == ControleRestricaoAcesso.GrauRestricao.RestricaoGeral)) {
                final ResponseRestRequest responseError = new ResponseRestRequest();
                responseError.mensagem = ApplicationResourcesHelper.getMessage("rotulo.sistema.indisponivel", null);
                return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(responseError).header(HttpHeaders.CONTENT_TYPE, RETURN_CONTENT_TYPE).build();
            }

        } catch (final ZetraException e1) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", null);
            LOG.error(e1.getMessage(), e1);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, RETURN_CONTENT_TYPE).build();
        }

        return null;
    }

    protected void registraLogDownloadMobile(AcessoSistema responsavel, HttpServletRequest request, String tipoEntidade) throws LogControllerException {
        // DESENV-18533: Toda vez que um usuário concordar com os termos de download do arquivo com dados sensíveis
        // ficará registrado na tb_log que o usuário aceitou o termo.
        final LogDelegate log = new LogDelegate(responsavel, tipoEntidade, Log.TERMO_ACEITE_DOWNLOAD_ARQUIVO_DADOS_SENSIVEIS, Log.LOG_INFORMACAO);
        log.setUsuario(responsavel.getUsuCodigo());
        log.write();
    }

    protected void anexarArquivoAde(AcessoSistema responsavel, java.sql.Date periodoSql, String adeCodigo, Map<String, String> anexo) throws IOException, AutorizacaoControllerException {
        final byte[] arq = Base64.decodeBase64(anexo.get("data"));
        final String path = ParamSist.getDiretorioRaizArquivos() + File.separatorChar + "anexo" + File.separatorChar + DateHelper.format(Calendar.getInstance().getTime(), "yyyyMMdd") + File.separatorChar + adeCodigo + File.separatorChar + anexo.get("nome");
        final File arquivoAnexo = new File(path);

        if (arquivoAnexo.exists()) {
            throw new AutorizacaoControllerException("mensagem.erro.anexo.ja.existe", responsavel);
        }

        FileUtils.writeByteArrayToFile(new File(path), arq);
        final String desc = ApplicationResourcesHelper.getMessage("mensagem.consignacao.anexo.origem.mobile", responsavel);

        final EditarAnexoConsignacaoController editarAnexoConsignacaoController = ApplicationContextProvider.getApplicationContext().getBean(EditarAnexoConsignacaoController.class);
        editarAnexoConsignacaoController.createAnexoAutorizacaoDesconto(adeCodigo, anexo.get("nome"), desc, periodoSql, null, responsavel);
    }

    /**
     * valida código de verba da requisição e seta valores do svcCodigo e
     * cnvCodigo. Retorna o convenio utilizado.
     *
     * @param parametros
     * @return convenio
     * @throws ZetraException
     */
    protected TransferObject validaCodigoVerba(ConsignacaoRestRequest dados, AcessoSistema responsavel) throws ZetraException {
        TransferObject convenio = null;

        if (!TextHelper.isNull(dados.csaIdentificador)) {
            final ConsignatariaController consignatariaController = ApplicationContextProvider.getApplicationContext().getBean(ConsignatariaController.class);
            final ConsignatariaTransferObject consignatariaTO = consignatariaController.findConsignatariaByIdn(dados.csaIdentificador, responsavel);
            if (consignatariaTO == null) {
                throw new ZetraException("mensagem.erro.nenhuma.consignataria.encontrada", responsavel);
            }
            dados.csaCodigo = consignatariaTO.getCsaCodigo();
        }

        // Valida o código da verba, se foi passado
        if (((dados.cnvCodVerba != null) && !"".equals(dados.cnvCodVerba)) || ((dados.svcIdentificador != null) && !"".equals(dados.svcIdentificador))) {
            try {
                final ConvenioController convenioController = ApplicationContextProvider.getApplicationContext().getBean(ConvenioController.class);
                final List<TransferObject> servicos = convenioController.getSvcByCodVerbaSvcIdentificador(dados.svcIdentificador, dados.cnvCodVerba, responsavel.getOrgCodigo(), dados.csaCodigo, true, responsavel);
                if (servicos.size() == 0) {
                    throw new ZetraException("mensagem.erro.verba.ou.servico.invalido", responsavel);
                } else if (servicos.size() == 1) {
                    convenio = servicos.get(0);
                    dados.svcCodigo = convenio.getAttribute(Columns.SVC_CODIGO).toString();
                    dados.cnvCodigo = convenio.getAttribute(Columns.CNV_CODIGO).toString();
                } else {
                    final Map<Object, TransferObject> svcCodigosDistintos = new HashMap<>();

                    for (final TransferObject servico : servicos) {
                        if (!svcCodigosDistintos.containsKey(servico.getAttribute(Columns.SVC_CODIGO))) {
                            svcCodigosDistintos.put(servico.getAttribute(Columns.SVC_CODIGO), servico);
                        }
                    }
                    if (svcCodigosDistintos.size() == 1) {
                        final Collection<TransferObject> svcVlr = svcCodigosDistintos.values();
                        convenio = svcVlr.iterator().next();
                        dados.svcCodigo = convenio.getAttribute(Columns.SVC_CODIGO).toString();
                    } else {
                        throw new ZetraException("mensagem.maisDeUmServicoEncontrado", responsavel);
                    }
                }
            } catch (final ConvenioControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new ZetraException("mensagem.erro.verba.ou.servico.invalido", responsavel);
            }
        } else {
            throw new ZetraException("mensagem.erro.verba.ou.servico.invalido", responsavel);
        }

        return convenio;
    }

    /**
     * Retorna o boleto da nova autorização
     *
     * @param adeCodigo
     * @param responsavel
     * @return
     * @throws ZetraException
     */
    protected CustomTransferObject buscaNovaAutorizacao(String adeCodigo, AcessoSistema responsavel) throws ZetraException {
        final String serCodigo = responsavel.getSerCodigo();
        final String orgCodigo = responsavel.getOrgCodigo();

        // Busca a nova autorização
        final PesquisarConsignacaoController pesquisarConsignacaoController = ApplicationContextProvider.getApplicationContext().getBean(PesquisarConsignacaoController.class);
        final TransferObject novaAutorizacao = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);

        // Guarda o Boleto no Hash para a geração do resultado
        // Busca o servidor
        final ServidorController servidorController = ApplicationContextProvider.getApplicationContext().getBean(ServidorController.class);
        ServidorTransferObject servidor = new ServidorTransferObject(serCodigo);
        servidor = servidorController.findServidor(servidor, responsavel);
        // Pega a descrição do codigo de estado civil
        final String serEstCivil = servidorController.getEstCivil(servidor.getSerEstCivil(), responsavel);
        // Busca o órgão
        final ConsignanteController consignanteController = ApplicationContextProvider.getApplicationContext().getBean(ConsignanteController.class);
        final OrgaoTransferObject orgao = consignanteController.findOrgao(orgCodigo, responsavel);

        final CustomTransferObject boleto = new CustomTransferObject();
        // Adiciona Informações do servidor
        boleto.setAtributos(servidor.getAtributos());
        // Adiciona a descrição do estado civil
        boleto.setAttribute(Columns.SER_EST_CIVIL, serEstCivil);
        // Adiciona Informações do orgão
        boleto.setAtributos(orgao.getAtributos());
        // Adiciona Informações da autorização
        boleto.setAtributos(novaAutorizacao.getAtributos());
        // Guarda o boleto no hash para ser consultada na geração do resultado
        return boleto;
    }

    protected CustomTransferObject buscaNovaAutorizacao(String adeCodigo, String serCodigo, String orgCodigo, AcessoSistema responsavel) throws ZetraException {
        // Busca a nova autorização
        final PesquisarConsignacaoController pesquisarConsignacaoController = ApplicationContextProvider.getApplicationContext().getBean(PesquisarConsignacaoController.class);
        final TransferObject novaAutorizacao = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);

        // Guarda o Boleto no Hash para a geração do resultado
        // Busca o servidor
        final ServidorController servidorController = ApplicationContextProvider.getApplicationContext().getBean(ServidorController.class);
        ServidorTransferObject servidor = new ServidorTransferObject(serCodigo);
        servidor = servidorController.findServidor(servidor, responsavel);
        // Pega a descrição do codigo de estado civil
        final String serEstCivil = servidorController.getEstCivil(servidor.getSerEstCivil(), responsavel);
        // Busca o órgão
        final ConsignanteController consignanteController = ApplicationContextProvider.getApplicationContext().getBean(ConsignanteController.class);
        final OrgaoTransferObject orgao = consignanteController.findOrgao(orgCodigo, responsavel);

        final CustomTransferObject boleto = new CustomTransferObject();
        // Adiciona Informações do servidor
        boleto.setAtributos(servidor.getAtributos());
        // Adiciona a descrição do estado civil
        boleto.setAttribute(Columns.SER_EST_CIVIL, serEstCivil);
        // Adiciona Informações do orgão
        boleto.setAtributos(orgao.getAtributos());
        // Adiciona Informações da autorização
        boleto.setAtributos(novaAutorizacao.getAtributos());
        // Guarda o boleto no hash para ser consultada na geração do resultado
        return boleto;
    }

    protected Response validarOperacao(AcessoSistema responsavel, List<String> funcoes, List<String> entidades) {
        List<String> funcoesValidas = Objects.requireNonNullElse(funcoes, Collections.emptyList());
        List<String> entidadesValidas = Objects.requireNonNullElse(entidades, Collections.emptyList());

        if (!funcoesValidas.isEmpty()) {
            for (String funcao : funcoesValidas) {
                if (!responsavel.temPermissao(funcao)) {
                    final ResponseRestRequest responseError = new ResponseRestRequest();
                    responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.usuarioNaoTemPermissao", null);
                    return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
                }
            }
        }

        if (entidadesValidas.isEmpty() || entidadesValidas.contains(responsavel.getTipoEntidade())) {
            return null;
        } else {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.sistema.tipo.entidade.invalido", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, RETURN_CONTENT_TYPE).build();
        }
    }
}

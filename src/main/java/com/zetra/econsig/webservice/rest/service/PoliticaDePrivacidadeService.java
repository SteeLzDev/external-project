package com.zetra.econsig.webservice.rest.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.delegate.UsuarioDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.rest.RestService;
import com.zetra.econsig.webservice.rest.Secured;

/**
 * <p>Title: TermosDeUsoService</p>
 * <p>Description: Serviço REST para termos de uso.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Path("/politicaDePrivacidade")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class PoliticaDePrivacidadeService extends RestService {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(PoliticaDePrivacidadeService.class);

    @Context
    SecurityContext securityContext;

    @POST
    @Secured
    @Path("/show")
    public Response consultarPoliticaPrivacidade(String tipoTermo) {
        AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
        tipoTermo = tipoTermo == null ? "" : tipoTermo;

        if (!tipoTermo.equals("") && !FileHelper.isFilenameSafe(tipoTermo)) {
            return genericError(new Exception(ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel)));
        }

        Map<String, Object> retorno = new HashMap<>();

        String chaveAceitacaoTermoDeUso = CodedValues.TPC_DATA_POLITICA_PRIVACIDADE_CSE;
        if (responsavel.isOrg()) {
            chaveAceitacaoTermoDeUso = CodedValues.TPC_DATA_POLITICA_PRIVACIDADE_ORG;
        } else if (responsavel.isSer()) {
            chaveAceitacaoTermoDeUso = CodedValues.TPC_DATA_POLITICA_PRIVACIDADE_SER;
        } else if (responsavel.isCsa()) {
            chaveAceitacaoTermoDeUso = CodedValues.TPC_DATA_POLITICA_PRIVACIDADE_CSA;
        } else if (responsavel.isCor()) {
            chaveAceitacaoTermoDeUso = CodedValues.TPC_DATA_POLITICA_PRIVACIDADE_COR;
        } else if (responsavel.isSup()) {
            chaveAceitacaoTermoDeUso = CodedValues.TPC_DATA_POLITICA_PRIVACIDADE_SUP;
        }

        Object paramAceitacaoTermoDeUso = ParamSist.getInstance().getParam(chaveAceitacaoTermoDeUso, responsavel);
        if (!TextHelper.isNull(paramAceitacaoTermoDeUso)) {
            File arqPoliticaDePrivacidade = null;
            String absolutePath = ParamSist.getDiretorioRaizArquivos() + File.separatorChar + "politica_privacidade";

            String nomeArquivo = "";
            if (!tipoTermo.equals("")) {
                nomeArquivo = tipoTermo + "-";
            }

            if (responsavel.isCse()) {
                nomeArquivo += "cse.msg";
            } else if (responsavel.isOrg()) {
                nomeArquivo += "org.msg";
            } else if (responsavel.isSer()) {
                nomeArquivo += "ser.msg";
            } else if (responsavel.isCsa()) {
                nomeArquivo += "csa.msg";
            } else if (responsavel.isCor()) {
                nomeArquivo += "cor.msg";
            } else if (responsavel.isSup()) {
                nomeArquivo += "sup.msg";
            }
            arqPoliticaDePrivacidade = new File(absolutePath, nomeArquivo);
            if (arqPoliticaDePrivacidade == null || !arqPoliticaDePrivacidade.exists()) {
                if (!tipoTermo.equals("")) {
                    nomeArquivo = tipoTermo + "-geral.msg";
                } else {
                    nomeArquivo = "geral.msg";
                }
                arqPoliticaDePrivacidade = new File(absolutePath, nomeArquivo);
                if (!arqPoliticaDePrivacidade.exists()) {
                    arqPoliticaDePrivacidade = null;
                }
            }

            String msg = "";
            boolean aceiteValido = false;
            java.util.Date dataUltimaAceitacao = null;
            java.util.Date dataPoliticaDePrivacidade = null;
            if (arqPoliticaDePrivacidade == null) {
                return genericError(new Exception(ApplicationResourcesHelper.getMessage("mensagem.erro.interno.politica.privacidade.nao.encontrado", responsavel)));
            } else {
                try {
                    msg = FileHelper.readAll(new FileInputStream(arqPoliticaDePrivacidade.getAbsolutePath()), "ISO-8859-1".intern());
                } catch (FileNotFoundException ex) {
                    LOG.debug(ex);
                    LOG.error(ex.getMessage(), ex);
                    return genericError(ex);
                }

                String tocCodigo = CodedValues.TOC_ACEITACAO_POLITICA_PRIVACIDADE_MOBILE;
                try {
                    dataPoliticaDePrivacidade = DateHelper.parse(paramAceitacaoTermoDeUso.toString(), "yyyy-MM-dd");
                    List<String> tocCodigos = new ArrayList<>();
                    tocCodigos.add(tocCodigo);

                    CustomTransferObject filtro = new CustomTransferObject();
                    filtro.setAttribute("tocCodigos", tocCodigos);
                    filtro.setAttribute(Columns.OUS_USU_CODIGO, responsavel.getUsuCodigo());

                    UsuarioDelegate usuDelegate = new UsuarioDelegate();
                    List<TransferObject> ocorrencias = usuDelegate.lstOcorrenciaUsuario(filtro, -1, -1, responsavel);
                    if (ocorrencias.size() > 0) {
                        dataUltimaAceitacao = (java.util.Date) ocorrencias.get(0).getAttribute(Columns.OUS_DATA);
                        if (dataUltimaAceitacao.compareTo(dataPoliticaDePrivacidade) > 0) {
                            aceiteValido = true;
                        }
                    }
                } catch (UsuarioControllerException ex) {
                    LOG.error(ex.getMessage(), ex);
                    return genericError(ex);
                } catch (java.text.ParseException ex) {
                    LOG.error(ex.getMessage(), ex);
                    return genericError(ex);
                }
            }

            retorno.put("msg", msg);
            retorno.put("aceiteValido", aceiteValido);
            try {
                ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
                retorno.put("dataUltimaAtualizacaoSistema", cseDelegate.dataUltimaAtualizacaoSistema());
            } catch (ConsignanteControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                return genericError(ex);
            }
        }

        return Response.status(Response.Status.OK).entity(retorno).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
    }

    @POST
    @Secured
    @Path("/aceitarPoliticaPrivacidade")
    public Response aceitarPoliticaPrivacidade(String tipoTermo) {
        AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
        tipoTermo = tipoTermo == null ? "" : tipoTermo;

        if (!tipoTermo.equals("") && !FileHelper.isFilenameSafe(tipoTermo)) {
            return genericError(new Exception(ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel)));
        }

        String tocCodigo = CodedValues.TOC_ACEITACAO_POLITICA_PRIVACIDADE_MOBILE;

        try {
            CustomTransferObject ocorrencia = new CustomTransferObject();
            ocorrencia.setAttribute(Columns.OUS_USU_CODIGO, responsavel.getUsuCodigo());
            ocorrencia.setAttribute(Columns.OUS_TOC_CODIGO, tocCodigo);
            ocorrencia.setAttribute(Columns.OUS_OUS_USU_CODIGO, responsavel.getUsuCodigo());
            ocorrencia.setAttribute(Columns.OUS_OBS, ApplicationResourcesHelper.getMessage("mensagem.politica.privacidade.aceito.com.sucesso", responsavel));
            ocorrencia.setAttribute(Columns.OUS_IP_ACESSO, responsavel.getIpUsuario());

            UsuarioDelegate usuDelegate;
            usuDelegate = new UsuarioDelegate();
            usuDelegate.createOcorrenciaUsuario(ocorrencia, responsavel);

        } catch (UsuarioControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            return genericError(ex);
        }

        return consultarPoliticaPrivacidade(tipoTermo);
    }
}

package com.zetra.econsig.web.controller.rest;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.rmi.RemoteException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.Response;

import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.OutputStreamAppender;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.zetra.econsig.folha.importacao.ValidaArquivoEntrada;
import com.zetra.econsig.folha.importacao.ValidaImportacao;
import com.zetra.econsig.helper.folha.ExportaMovimentoHelper;
import com.zetra.econsig.helper.folha.ProcessaRetorno;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.rotinas.RemoteProxy;
import com.zetra.econsig.helper.rotinas.RotinaExternaViaProxy;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ExecutarRotinaRemotaRestController</p>
 * <p>Description: REST Controller para execução de rotinas via Script.</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@RestController
public class ExecutarRotinaRemotaRestController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ExecutarRotinaRemotaRestController.class);

    @RequestMapping(value = "/v3/executarRotina", method = RequestMethod.POST)
    public int iniciar(@RequestBody ExecucaoRemotaRequest parametros, HttpServletRequest request, HttpServletResponse response) {
        AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();

        if (parametros == null || TextHelper.isNull(parametros.getNomeClasseRotina())) {
            response.setStatus(Response.Status.NOT_FOUND.getStatusCode());
            return -1;
        }

        String ipsAcessoLiberado = (String) ParamSist.getInstance().getParam(CodedValues.TPC_IPS_LIBERADOS_PAGINA_ADMINISTRACAO, responsavel);
        if (TextHelper.isNull(ipsAcessoLiberado)) {
            ipsAcessoLiberado = "127.0.0.1";
        }

        if (!JspHelper.validaDDNS(JspHelper.getRemoteAddr(request), ipsAcessoLiberado)) {
            response.setStatus(Response.Status.FORBIDDEN.getStatusCode());
            return -1;
        }

        try {
            return executarRotina(parametros.getNomeClasseRotina(), parametros.getParametrosRotina(), parametros.getEnderecoRetornoLog(), parametros.getPortaRetornoLog(), responsavel);
        } catch (RemoteException ex) {
            response.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
            return -1;
        }
    }

    private int executarRotina(String nomeClasseRotina, String[] parametrosRotina, String enderecoRetornoLog, Integer portaRetornoLog, AcessoSistema responsavel) throws RemoteException {
        Socket client = null;
        BufferedWriter bfw = null;
        Appender appender = null;
        LoggerContext context = LoggerContext.getContext(false);
        try {
            if (!TextHelper.isNull(enderecoRetornoLog) && portaRetornoLog != null) {
                client = new Socket(enderecoRetornoLog, portaRetornoLog);
                OutputStream ou = client.getOutputStream();
                Writer ouw = new OutputStreamWriter(ou);
                bfw = new BufferedWriter(ouw);

                Layout<?> layout = context.getConfiguration().getAppender("Console").getLayout();
                appender = OutputStreamAppender.createAppender(layout, null, ou, "Logger", false, true);

                // Adiciona um novo logger à classe da rotina para impressão no socket
                context.getLogger(nomeClasseRotina).addAppender(appender);

                // Caso a classe seja de processamento de retorno, adiciona o socket logger também à classe de movimento,
                // pois é usada para impressão do período de exportação
                if (nomeClasseRotina.equals(ProcessaRetorno.class.getName())) {
                    context.getLogger(ExportaMovimentoHelper.class.getName()).addAppender(appender);
                } else if (nomeClasseRotina.equals(ValidaImportacao.class.getName())) {
                    context.getLogger(ValidaArquivoEntrada.class.getName()).addAppender(appender);
                }

                appender.start();
            }

            // Executa o método main da rotina
            RotinaExternaViaProxy rotina = (RotinaExternaViaProxy) Class.forName(nomeClasseRotina).getDeclaredConstructor().newInstance();
            int status = rotina.executar(parametrosRotina);

            if (bfw != null) {
                // Envia comando de finalização
                bfw.write(RemoteProxy.DISCONNECT_COMMAND + "\r\n");
                bfw.flush();
            }

            return status;

        } catch (IOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new RemoteException(ApplicationResourcesHelper.getMessage("mensagem.erro.execucao.remota.erro.conexao.socket.log", responsavel));

        } catch (NoSuchMethodException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new RemoteException(ApplicationResourcesHelper.getMessage("mensagem.erro.execucao.remota.metodo.nao.encontrado", responsavel, nomeClasseRotina));

        } catch (ClassNotFoundException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new RemoteException(ApplicationResourcesHelper.getMessage("mensagem.erro.execucao.remota.classe.nao.encontrada", responsavel, nomeClasseRotina));

        } catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new RemoteException(ApplicationResourcesHelper.getMessage("mensagem.erro.execucao.remota.erro.execucao.rotina", responsavel, nomeClasseRotina));

        } finally {
            try {
                if (appender != null) {
                    context.getLogger(nomeClasseRotina).removeAppender(appender);

                    // Caso a classe seja de processamento de retorno, remove o socket logger também à classe de movimento
                    if (nomeClasseRotina.equals(ProcessaRetorno.class.getName())) {
                        context.getLogger(ExportaMovimentoHelper.class.getName()).removeAppender(appender);
                    } else if (nomeClasseRotina.equals(ValidaImportacao.class.getName())) {
                        context.getLogger(ValidaArquivoEntrada.class.getName()).removeAppender(appender);
                    }
                }
                if (client != null) {
                    client.close();
                }
            } catch (IOException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }
    }
}

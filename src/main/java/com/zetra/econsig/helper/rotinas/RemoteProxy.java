package com.zetra.econsig.helper.rotinas;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Scanner;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.web.controller.rest.ExecucaoRemotaRequest;

/**
 * <p>Title: RemoteProxy</p>
 * <p>Description: Classe main para execução de rotinas via Script.</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RemoteProxy {
    public static final String DISCONNECT_COMMAND = "##disconnect##";
    private static final String NOME_CLASSE = RemoteProxy.class.getName();

    public static void main(String[] args) {
        if (args == null || args.length == 0) {
            System.err.println("USE: java " + NOME_CLASSE + " nomeClasseRotina [listaParametrosRotina]");
            return;
        }

        ServerSocket server = null;
        ServerSocketListener listener = null;
        try {
            server = new ServerSocket(0);

            listener = new ServerSocketListener(server);
            listener.start();

            String urlBase = System.getProperty("urlSistema");
            if (TextHelper.isNull(urlBase)) {
                urlBase = "http://localhost:8080/consig";
            }

            String url = urlBase + "/v3/executarRotina";

            String nomeClasseRotina = args[0];
            String[] argsArray = Arrays.copyOfRange(args, 1, args.length);

            ExecucaoRemotaRequest parametros = new ExecucaoRemotaRequest();
            parametros.setNomeClasseRotina(nomeClasseRotina);
            parametros.setParametrosRotina(argsArray);
            parametros.setEnderecoRetornoLog(server.getInetAddress().getHostAddress());
            parametros.setPortaRetornoLog(server.getLocalPort());

            Entity<ExecucaoRemotaRequest> json = null;
            if (parametros != null) {
                json = Entity.json(parametros);
            }

            AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();

            int status = -1;

            Response resposta = enviarRequisicao(url, json, responsavel);
            if (resposta != null) {
                if (resposta.getStatus() != Response.Status.OK.getStatusCode()) {
                    status = 0;
                } else {
                    status = resposta.readEntity(Integer.class);
                }
            }

            System.exit(status);

        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(-1);

        } finally {
            try {
                if (listener != null) {
                    listener.close();
                }
                if (server != null) {
                    server.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }


    private static Response enviarRequisicao(String url, Entity<?> json, AcessoSistema responsavel) throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslcontext = SSLContext.getInstance("TLS");
        sslcontext.init(null, new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException{}
                @Override
                public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException{}
                @Override
                public X509Certificate[] getAcceptedIssuers()
                {
                    return new X509Certificate[0];
                }
        }}, new java.security.SecureRandom());

        HostnameVerifier allowAll = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        Client client = ClientBuilder.newBuilder().sslContext(sslcontext).hostnameVerifier(allowAll).build();
        WebTarget webResource = client.target(url);

        return webResource.request(MediaType.APPLICATION_JSON).accept("application/json").post(json);
    }

    private static class ServerSocketListener extends Thread {
        private final ServerSocket server;

        private boolean finish = false;

        public ServerSocketListener(ServerSocket server) {
            this.server = server;
        }

        @Override
        public void run() {
            try {
                while (!finish) {
                    Socket socket = server.accept();
                    try (Scanner scanner = new Scanner(socket.getInputStream())) {
                        while (scanner.hasNext()) {
                            String line = scanner.nextLine();
                            if (line.startsWith(DISCONNECT_COMMAND)) {
                                finish = true;
                                break;
                            } else {
                                System.out.println(line);
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        public void close() {
            finish = true;
        }
    }
}
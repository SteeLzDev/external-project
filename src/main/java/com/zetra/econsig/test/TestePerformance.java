package com.zetra.econsig.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;

import javax.net.ssl.SSLException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.zetra.econsig.exception.ZetraException;

/**
 * <p>Title: TestePerformance</p>
 * <p>Description: Classe para teste de performance das operações do sistema.</p>
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: ZetraSoft</p>
  * $Author$
 * $Revision$
 * $Date$
 */
@SuppressWarnings("all")
public final class TestePerformance {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(TestePerformance.class);

    public static final String URL_REQUISICAO_XML = "http://localhost:8080/consig/xml/requisicao.jsp";
    //public static final String URL_REQUISICAO_XML = "http://localhost:8080/central/xml/requisicao.jsp";

    public static final String XML_CONSULTA_MARGEM = "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>"
                                                   + "\n<Documento ID=\"1.2.1\">"
                                                   + "\n  <Parametro Nome=\"OPERACAO\"          Valor=\"Consultar Margem\"/>"
                                                   + "\n  <Parametro Nome=\"CLIENTE\"           Valor=\"<#CLIENTE#>\"/>"
                                                   + "\n  <Parametro Nome=\"CONVENIO\"          Valor=\"<#CONVENIO#>\"/>"
                                                   + "\n  <Parametro Nome=\"USUARIO\"           Valor=\"<#USUARIO#>\"/>"
                                                   + "\n  <Parametro Nome=\"SENHA\"             Valor=\"<#SENHA#>\"/>"
                                                   + "\n  <Parametro Nome=\"MATRICULA\"         Valor=\"<#MATRICULA#>\"/>"
                                                   + "\n  <Parametro Nome=\"CPF\"               Valor=\"<#CPF#>\"/>"
                                                   + "\n  <Parametro Nome=\"VALOR_PARCELA\"     Valor=\"100,00\"/>"
                                                   + "\n  <Parametro Nome=\"SER_SENHA\"         Valor=\"123456\"/>"
                                                   + "\n</Documento>"
                                                   ;

    public static final String XML_RESERVA_MARGEM = "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>"
                                                  + "\n<Documento ID=\"1.2.1\">"
                                                  + "\n  <Parametro Nome=\"OPERACAO\"          Valor=\"Reservar Margem\"/>"
                                                  + "\n  <Parametro Nome=\"CLIENTE\"           Valor=\"<#CLIENTE#>\"/>"
                                                  + "\n  <Parametro Nome=\"CONVENIO\"          Valor=\"<#CONVENIO#>\"/>"
                                                  + "\n  <Parametro Nome=\"USUARIO\"           Valor=\"<#USUARIO#>\"/>"
                                                  + "\n  <Parametro Nome=\"SENHA\"             Valor=\"<#SENHA#>\"/>"
                                                  + "\n  <Parametro Nome=\"MATRICULA\"         Valor=\"<#MATRICULA#>\"/>"
                                                  + "\n  <Parametro Nome=\"CPF\"               Valor=\"<#CPF#>\"/>"
                                                  + "\n  <Parametro Nome=\"VALOR_LIBERADO\"    Valor=\"1100,00\"/>"
                                                  + "\n  <Parametro Nome=\"VALOR_PARCELA\"     Valor=\"100,00\"/>"
                                                  + "\n  <Parametro Nome=\"PRAZO\"             Valor=\"12\"/>"
                                                  + "\n  <Parametro Nome=\"SERVICO_CODIGO\"    Valor=\"<#SERVICO#>\"/>"
                                                  + "\n  <Parametro Nome=\"COD_VERBA\"         Valor=\"<#COD_VERBA#>\"/>"
                                                  + "\n  <Parametro Nome=\"ADE_IDENTIFICADOR\" Valor=\"<#IDENTIFICADOR#>\"/>"
                                                  + "\n</Documento>"
                                                  ;

    public static final String XML_CONSULTAR_CONSIGNACAO = "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>"
                                                         + "\n<Documento ID=\"1.2.1\">"
                                                         + "\n  <Parametro Nome=\"OPERACAO\"          Valor=\"Consultar Consignacao\"/>"
                                                         + "\n  <Parametro Nome=\"CLIENTE\"           Valor=\"<#CLIENTE#>\"/>"
                                                         + "\n  <Parametro Nome=\"CONVENIO\"          Valor=\"<#CONVENIO#>\"/>"
                                                         + "\n  <Parametro Nome=\"USUARIO\"           Valor=\"<#USUARIO#>\"/>"
                                                         + "\n  <Parametro Nome=\"SENHA\"             Valor=\"<#SENHA#>\"/>"
                                                         + "\n  <Parametro Nome=\"MATRICULA\"         Valor=\"<#MATRICULA#>\"/>"
                                                         + "\n  <Parametro Nome=\"CPF\"               Valor=\"<#CPF#>\"/>"
                                                         + "\n  <Parametro Nome=\"ADE_IDENTIFICADOR\" Valor=\"<#IDENTIFICADOR#>\"/>"
                                                         + "\n</Documento>"
                                                         ;

    public static final String XML_CANCELAR_CONSIGNACAO = "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>"
                                                        + "\n<Documento ID=\"1.2.1\">"
                                                        + "\n  <Parametro Nome=\"OPERACAO\"          Valor=\"Cancelar Consignacao\"/>"
                                                        + "\n  <Parametro Nome=\"CLIENTE\"           Valor=\"<#CLIENTE#>\"/>"
                                                        + "\n  <Parametro Nome=\"CONVENIO\"          Valor=\"<#CONVENIO#>\"/>"
                                                        + "\n  <Parametro Nome=\"USUARIO\"           Valor=\"<#USUARIO#>\"/>"
                                                        + "\n  <Parametro Nome=\"SENHA\"             Valor=\"<#SENHA#>\"/>"
                                                        + "\n  <Parametro Nome=\"MATRICULA\"         Valor=\"<#MATRICULA#>\"/>"
                                                        + "\n  <Parametro Nome=\"CPF\"               Valor=\"<#CPF#>\"/>"
                                                        + "\n  <Parametro Nome=\"ADE_IDENTIFICADOR\" Valor=\"<#IDENTIFICADOR#>\"/>"
                                                        + "\n  <Parametro Nome=\"TMO_IDENTIFICADOR\" Valor=\"777\"/>"
                                                        + "\n</Documento>"
                                                        ;

    // TROCAR PELAS MATRICULAS DA BASE DE DADOS
    /*
      select concat('{"', rse_matricula, '","', ser_cpf, '"},') as linha
      from tb_registro_servidor rse
      inner join tb_servidor ser on (rse.ser_codigo = ser.ser_codigo)
      where srs_codigo = '1'
      and rse_margem_rest > 100
      and not exists (select 1 from tb_aut_desconto ade where ade.rse_codigo = rse.rse_codigo)
      LIMIT 100;
    */
    private static final String[][] MATRICULA_CPF = {
//          {"111111", "111.111.111-11"},
    };

    // args[0] --> Quantidade de vezes que cada matrícula será usada no teste
    public static void main(String[] args) {
        final String cliente = "BRASIL";
        final String convenio = "BRASIL-LOCAL";
        final String usuario = "brasil";
        final String senha = "csa12345";
        final String codVerba = "571EMPRESTIMO";
        final String servico = "05B";
        final String adeIdentificador = String.valueOf(Math.random() * 1000000);

        final long inicio = new Date().getTime();

        final int qtdTestes = Integer.parseInt(args[0]);

        // Executa N vezes o teste para cada Matrícula/CPF
        for (int j = 0; j < qtdTestes; j++) {
            for (final String[] element : MATRICULA_CPF) {
                final String comando1 = XML_CONSULTA_MARGEM
                        .replace("<#CLIENTE#>", cliente)
                        .replace("<#CONVENIO#>", convenio)
                        .replace("<#USUARIO#>", usuario)
                        .replace("<#SENHA#>", senha)
                        .replaceAll("<#MATRICULA#>", element[0])
                        .replaceAll("<#CPF#>", element[1]);

                final String comando2 = XML_RESERVA_MARGEM
                        .replace("<#CLIENTE#>", cliente)
                        .replace("<#CONVENIO#>", convenio)
                        .replace("<#USUARIO#>", usuario)
                        .replace("<#SENHA#>", senha)
                        .replaceAll("<#MATRICULA#>", element[0])
                        .replaceAll("<#CPF#>", element[1])
                        .replace("<#COD_VERBA#>", codVerba)
                        .replace("<#SERVICO#>", servico)
                        .replaceAll("<#IDENTIFICADOR#>", adeIdentificador);

                final String comando3 = XML_CONSULTAR_CONSIGNACAO
                        .replace("<#CLIENTE#>", cliente)
                        .replace("<#CONVENIO#>", convenio)
                        .replace("<#USUARIO#>", usuario)
                        .replace("<#SENHA#>", senha)
                        .replaceAll("<#MATRICULA#>", element[0])
                        .replaceAll("<#CPF#>", element[1])
                        .replaceAll("<#IDENTIFICADOR#>", adeIdentificador);

                final String comando4 = XML_CANCELAR_CONSIGNACAO
                        .replace("<#CLIENTE#>", cliente)
                        .replace("<#CONVENIO#>", convenio)
                        .replace("<#USUARIO#>", usuario)
                        .replace("<#SENHA#>", senha)
                        .replaceAll("<#MATRICULA#>", element[0])
                        .replaceAll("<#CPF#>", element[1])
                        .replaceAll("<#IDENTIFICADOR#>", adeIdentificador);

                // Teste sequencial de consulta de margem
                try {
                    for (final String comando : new String[]{comando1, comando2, comando3, comando4}) {
                        LOG.info(comando);
                        final String resultado = fazerRequisicao(comando);
                        LOG.info(resultado);
                    }
                } catch (final Exception ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            }
        }

        final long fim = new Date().getTime();

        LOG.info("Tempo gasto no teste: " + (fim - inicio));
    }

    private static String fazerRequisicao(String comando) throws ZetraException {
        HttpPost post = null;
        InputStream inputStream = null;
        HttpResponse response = null;

        try {
            final HttpClient client = new DefaultHttpClient();

            // Define os parâmetros
            final ArrayList<NameValuePair> data = new ArrayList<>(1);
            data.add(new BasicNameValuePair("REQUISICAO", comando));

            post = new HttpPost(URL_REQUISICAO_XML);
            post.setEntity(new UrlEncodedFormEntity(data));

            int statusCode = 0;
            Exception cause = null;
            try {
                // Executa o post
                response = client.execute(post);
                statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != HttpStatus.SC_OK) {
                    String message = null;
                    if (statusCode == HttpStatus.SC_NOT_FOUND) {
                        message = "PAGE NOT FOUND: " + URL_REQUISICAO_XML;
                    } else  if (response.getStatusLine() != null) {
                        message = response.getStatusLine().getReasonPhrase();
                    }
                    if (message == null) {
                        message = "STATUS DO RETORNO DA REQUISIÇÃO: " + statusCode;
                    }
                    throw ZetraException.byMessage("904", new Exception(message));
                }
            } catch (final UnknownHostException | NoRouteToHostException | ConnectException | SSLException ex) {
                cause = ex;
            }
            if (cause != null) {
                throw ZetraException.byMessage("903", cause);
            }

            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            // Input Stream para receber o resultado da requisição
            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                inputStream = entity.getContent();
                // Grava o resultado em um buffer
                int c = -1;
                while ((c = inputStream.read()) != -1) {
                    out.write(c);
                }
            }
            return out.toString();

        } catch (final ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            throw ex;

        } catch (final Exception ex) {
            throw ZetraException.byMessage("999", ex);

        } finally {
            try {
                if (post != null) {
                    post.abort();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (final IOException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }
    }
}
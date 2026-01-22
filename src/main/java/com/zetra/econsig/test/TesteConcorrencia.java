package com.zetra.econsig.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.util.ArrayList;

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
 * <p>Title: TesteConcorrencia</p>
 * <p>Description: Classe para teste de acesso concorrente às operações do sistema.
 * Cria X threads concorrentes que disparam requisições XMLs para matrículas
 * iguais ou diferentes, sendo estas o array MATRICULA_CPF</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
  * $Author$
 * $Revision$
 * $Date$
 */
@SuppressWarnings("all")
public final class TesteConcorrencia {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(TesteConcorrencia.class);

    public static final String URL_REQUISICAO_XML = "http://localhost:8080/consig/xml/requisicao.jsp";
//  public static final String URL_REQUISICAO_XML = "http://localhost:8080/central/xml/requisicao.jsp";

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
    public static final String[][] MATRICULA_CPF = {
//      {"111111", "111.111.111-11"},
    };

    public static void main(String[] args) {
        String cliente = "BRASIL";
        String convenio = "BRASIL-LOCAL";
        String usuario = "brasil";
        String senha = "csa12345";
        String codVerba = "571EMPRESTIMO";
        String servico = "05B";
        String adeIdentificador = String.valueOf(Math.random() * 1000000);

        // Math.min para limitar o teste em X usuários ao invés do array completo
        for (int i = 0; i < Math.min(MATRICULA_CPF.length, 1000); i++) {

            String comando1 = XML_CONSULTA_MARGEM.replaceAll("<#CLIENTE#>", cliente)
                                                 .replaceAll("<#CONVENIO#>", convenio)
                                                 .replaceAll("<#USUARIO#>", usuario)
                                                 .replaceAll("<#SENHA#>", senha)
                                                 .replaceAll("<#MATRICULA#>", MATRICULA_CPF[i][0])
                                                 .replaceAll("<#CPF#>", MATRICULA_CPF[i][1]);

            String comando2 = XML_RESERVA_MARGEM.replaceAll("<#CLIENTE#>", cliente)
                                                .replaceAll("<#CONVENIO#>", convenio)
                                                .replaceAll("<#USUARIO#>", usuario)
                                                .replaceAll("<#SENHA#>", senha)
                                                .replaceAll("<#MATRICULA#>", MATRICULA_CPF[i][0])
                                                .replaceAll("<#CPF#>", MATRICULA_CPF[i][1])
                                                .replaceAll("<#COD_VERBA#>", codVerba)
                                                .replaceAll("<#SERVICO#>", servico)
                                                .replaceAll("<#IDENTIFICADOR#>", adeIdentificador);

            String comando3 = XML_CONSULTAR_CONSIGNACAO.replaceAll("<#CLIENTE#>", cliente)
                                                       .replaceAll("<#CONVENIO#>", convenio)
                                                       .replaceAll("<#USUARIO#>", usuario)
                                                       .replaceAll("<#SENHA#>", senha)
                                                       .replaceAll("<#MATRICULA#>", MATRICULA_CPF[i][0])
                                                       .replaceAll("<#CPF#>", MATRICULA_CPF[i][1])
                                                       .replaceAll("<#IDENTIFICADOR#>", adeIdentificador);

            String comando4 = XML_CANCELAR_CONSIGNACAO.replaceAll("<#CLIENTE#>", cliente)
                                                      .replaceAll("<#CONVENIO#>", convenio)
                                                      .replaceAll("<#USUARIO#>", usuario)
                                                      .replaceAll("<#SENHA#>", senha)
                                                      .replaceAll("<#MATRICULA#>", MATRICULA_CPF[i][0])
                                                      .replaceAll("<#CPF#>", MATRICULA_CPF[i][1])
                                                      .replaceAll("<#IDENTIFICADOR#>", adeIdentificador);

            // Sequencia de comandos: comando1, comando2, comando3
            Thread t = new Thread(new Requisicao(new String[]{comando1, comando2, comando3, comando4}, URL_REQUISICAO_XML));
            t.start();
            LOG.info("Thread " + i + " started!");
        }
    }
}

@SuppressWarnings("all")
class Requisicao implements Runnable {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(Requisicao.class);

    private final String[] comandos;
    private final String url;

    public Requisicao(String[] comandos, String url) {
        this.comandos = comandos;
        this.url = url;
    }

    @Override
    public void run() {
        try {
            for (String comando : comandos) {
                // Interrompe as threads para dar mais variação
                Thread.sleep(Math.round(Math.random() * 15000));
                String resultado = fazerRequisicao(comando);
                LOG.info(resultado);
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    private String fazerRequisicao(String comando) throws ZetraException {
        HttpPost post = null;
        InputStream inputStream = null;
        HttpResponse response = null;

        try {
            HttpClient client = new DefaultHttpClient();

            // Define os parâmetros
            ArrayList<NameValuePair> data = new ArrayList<>(1);
            data.add(new BasicNameValuePair("REQUISICAO", comando));

            post = new HttpPost(url);
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
                        message = "PAGE NOT FOUND: " + url;
                    } else  if (response.getStatusLine() != null) {
                        message = response.getStatusLine().getReasonPhrase();
                    }
                    if (message == null) {
                        message = "STATUS DO RETORNO DA REQUISIÇÃO: " + statusCode;
                    }
                    throw ZetraException.byMessage("904", new Exception(message));
                }
            } catch (UnknownHostException | NoRouteToHostException | ConnectException | SSLException ex) {
                cause = ex;
            }
            if (cause != null) {
                throw ZetraException.byMessage("903", cause);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            // Input Stream para receber o resultado da requisição
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                inputStream = entity.getContent();
                // Grava o resultado em um buffer
                int c = -1;
                while ((c = inputStream.read()) != -1) {
                    out.write(c);
                }
            }
            return out.toString();

        } catch (ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            throw ex;

        } catch (Exception ex) {
            throw ZetraException.byMessage("999", ex);

        } finally {
            try {
                if (post != null) {
                    post.abort();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }
    }
}
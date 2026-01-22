package com.zetra.econsig.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

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
public final class TesteConcorrenciaViaArquivo {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(TesteConcorrencia.class);

    public static final String URL_REQUISICAO_XML = "http://localhost:8080/consig/xml/requisicao.jsp";

    public static final String XML_CONSULTA_MARGEM = "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>"
        + "<Documento ID=\"1.2.1\">"
        + "  <Parametro Nome=\"OPERACAO\"          Valor=\"Consultar Margem\"/>"
        + "  <Parametro Nome=\"CLIENTE\"           Valor=\"<#CLIENTE#>\"/>"
        + "  <Parametro Nome=\"CONVENIO\"          Valor=\"<#CONVENIO#>\"/>"
        + "  <Parametro Nome=\"USUARIO\"           Valor=\"<#USUARIO#>\"/>"
        + "  <Parametro Nome=\"SENHA\"             Valor=\"<#SENHA#>\"/>"
        + "  <Parametro Nome=\"MATRICULA\"         Valor=\"<#MATRICULA#>\"/>"
        + "  <Parametro Nome=\"CPF\"               Valor=\"<#CPF#>\"/>"
        + "  <Parametro Nome=\"VALOR_PARCELA\"     Valor=\"100,00\"/>"
        + "  <Parametro Nome=\"SER_SENHA\"         Valor=\"123456\"/>"
        + "</Documento>"
        ;

    public static final String XML_RESERVA_MARGEM = "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>"
        + "<Documento ID=\"1.2.1\">"
        + "  <Parametro Nome=\"OPERACAO\"          Valor=\"Reservar Margem\"/>"
        + "  <Parametro Nome=\"CLIENTE\"           Valor=\"<#CLIENTE#>\"/>"
        + "  <Parametro Nome=\"CONVENIO\"          Valor=\"<#CONVENIO#>\"/>"
        + "  <Parametro Nome=\"USUARIO\"           Valor=\"<#USUARIO#>\"/>"
        + "  <Parametro Nome=\"SENHA\"             Valor=\"<#SENHA#>\"/>"
        + "  <Parametro Nome=\"MATRICULA\"         Valor=\"<#MATRICULA#>\"/>"
        + "  <Parametro Nome=\"CPF\"               Valor=\"<#CPF#>\"/>"
        + "  <Parametro Nome=\"VALOR_LIBERADO\"    Valor=\"1100,00\"/>"
        + "  <Parametro Nome=\"VALOR_PARCELA\"     Valor=\"100,00\"/>"
        + "  <Parametro Nome=\"PRAZO\"             Valor=\"12\"/>"
        + "  <Parametro Nome=\"COD_VERBA\"         Valor=\"<#COD_VERBA#>\"/>"
        + "  <Parametro Nome=\"ADE_IDENTIFICADOR\" Valor=\"<#IDENTIFICADOR#>\"/>"
        + "  <Parametro Nome=\"SER_SENHA\"         Valor=\"123456\"/>"
        + "</Documento>"
        ;

    public static final String XML_CONSULTAR_CONSIGNACAO = "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>"
        + "<Documento ID=\"1.2.1\">"
        + "  <Parametro Nome=\"OPERACAO\"          Valor=\"Consultar Consignacao\"/>"
        + "  <Parametro Nome=\"CLIENTE\"           Valor=\"<#CLIENTE#>\"/>"
        + "  <Parametro Nome=\"CONVENIO\"          Valor=\"<#CONVENIO#>\"/>"
        + "  <Parametro Nome=\"USUARIO\"           Valor=\"<#USUARIO#>\"/>"
        + "  <Parametro Nome=\"SENHA\"             Valor=\"<#SENHA#>\"/>"
        + "  <Parametro Nome=\"MATRICULA\"         Valor=\"<#MATRICULA#>\"/>"
        + "  <Parametro Nome=\"CPF\"               Valor=\"<#CPF#>\"/>"
        + "  <Parametro Nome=\"ADE_IDENTIFICADOR\" Valor=\"<#IDENTIFICADOR#>\"/>"
        + "</Documento>"
        ;

    public static final String XML_CANCELAR_CONSIGNACAO = "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>"
        + "<Documento ID=\"1.2.1\">"
        + "  <Parametro Nome=\"OPERACAO\"          Valor=\"Cancelar Reserva\"/>"
        + "  <Parametro Nome=\"CLIENTE\"           Valor=\"<#CLIENTE#>\"/>"
        + "  <Parametro Nome=\"CONVENIO\"          Valor=\"<#CONVENIO#>\"/>"
        + "  <Parametro Nome=\"USUARIO\"           Valor=\"<#USUARIO#>\"/>"
        + "  <Parametro Nome=\"SENHA\"             Valor=\"<#SENHA#>\"/>"
        + "  <Parametro Nome=\"MATRICULA\"         Valor=\"<#MATRICULA#>\"/>"
        + "  <Parametro Nome=\"CPF\"               Valor=\"<#CPF#>\"/>"
        + "  <Parametro Nome=\"ADE_IDENTIFICADOR\" Valor=\"<#IDENTIFICADOR#>\"/>"
        + "</Documento>"
        ;

    // args[0] --> Nome do arquivo com matrículas e CPFs separados por ponto e vírgula
    public static void main(String[] args) {
        String cliente = "BRASIL";
        String convenio = "BRASIL-LOCAL";
        String usuario = "brasil";
        String senha = "brasil123";
        String codVerba = "571EMPRESTIMO";
        String adeIdentificador = String.valueOf(Math.random() * 1000000);

        if (args == null || args.length != 1) {
            System.out.println("Informe o nome do arquivo de matrículas e CPFs (args[0])");
            return;
        }

        String arqMatriculaCpf = args[0];

        /*
        select concat(rse_matricula, ';', ser_cpf) as linha
        from tb_registro_servidor rse
        inner join tb_servidor ser on (rse.ser_codigo = ser.ser_codigo)
        where srs_codigo = '1'
        and rse_margem_rest > 100
        and not exists (select 1 from tb_aut_desconto ade where ade.rse_codigo = rse.rse_codigo)
        LIMIT 100;
        */

        // Math.min para limitar o teste em X usuários ao invés do array completo
        BufferedReader in = null;
        int i = 0;

        try {
            in = new BufferedReader(new FileReader(arqMatriculaCpf));
            String linha = null;
            while ((linha = in.readLine()) != null) {
                String[] matriculaCpf = linha.split(";");

                String comando1 = XML_CONSULTA_MARGEM.replaceAll("<#CLIENTE#>", cliente)
                                                     .replaceAll("<#CONVENIO#>", convenio)
                                                     .replaceAll("<#USUARIO#>", usuario)
                                                     .replaceAll("<#SENHA#>", senha)
                                                     .replaceAll("<#MATRICULA#>", matriculaCpf[0])
                                                     .replaceAll("<#CPF#>", matriculaCpf[1]);

                String comando2 = XML_RESERVA_MARGEM.replaceAll("<#CLIENTE#>", cliente)
                                                    .replaceAll("<#CONVENIO#>", convenio)
                                                    .replaceAll("<#USUARIO#>", usuario)
                                                    .replaceAll("<#SENHA#>", senha)
                                                    .replaceAll("<#MATRICULA#>", matriculaCpf[0])
                                                    .replaceAll("<#CPF#>", matriculaCpf[1])
                                                    .replaceAll("<#COD_VERBA#>", codVerba)
                                                    .replaceAll("<#IDENTIFICADOR#>", adeIdentificador);

                String comando3 = XML_CONSULTAR_CONSIGNACAO.replaceAll("<#CLIENTE#>", cliente)
                                                           .replaceAll("<#CONVENIO#>", convenio)
                                                           .replaceAll("<#USUARIO#>", usuario)
                                                           .replaceAll("<#SENHA#>", senha)
                                                           .replaceAll("<#MATRICULA#>", matriculaCpf[0])
                                                           .replaceAll("<#CPF#>", matriculaCpf[1])
                                                           .replaceAll("<#IDENTIFICADOR#>", adeIdentificador);

                String comando4 = XML_CANCELAR_CONSIGNACAO.replaceAll("<#CLIENTE#>", cliente)
                                                          .replaceAll("<#CONVENIO#>", convenio)
                                                          .replaceAll("<#USUARIO#>", usuario)
                                                          .replaceAll("<#SENHA#>", senha)
                                                          .replaceAll("<#MATRICULA#>", matriculaCpf[0])
                                                          .replaceAll("<#CPF#>", matriculaCpf[1])
                                                          .replaceAll("<#IDENTIFICADOR#>", adeIdentificador);

                // Sequencia de comandos: comando1, comando2, comando3
                Thread t = new Thread(new Requisicao(new String[]{comando1, comando2, comando3, comando4}, URL_REQUISICAO_XML));
                t.start();
                LOG.info("Thread " + ++i + " started!");
            }
        } catch (IOException ex) {
            LOG.error(ex.getMessage(), ex);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            }
        }
    }
}

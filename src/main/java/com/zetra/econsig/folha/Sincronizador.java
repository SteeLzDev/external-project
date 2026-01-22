package com.zetra.econsig.folha;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.rotinas.RotinaExternaViaProxy;
import com.zetra.econsig.service.folha.SincronizadorController;
import com.zetra.econsig.web.ApplicationContextProvider;


/**
 * <p>Title: Sincronizador</p>
 * <p>Description: Tendo como entrada um arquivo gerado pela folha com os contratos
 * abertos, sincroniza qualquer informação no sistema eConsig.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class Sincronizador implements RotinaExternaViaProxy {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(Sincronizador.class);
    private static final String NOME_CLASSE = Sincronizador.class.getName();

    @Override
    public int executar(String[] args) {
        String msgErroPrincipal = "USE:  \n" +
                "java " + NOME_CLASSE + " S [ULT_PERIODO] [ARQUIVO_FOLHA] --------------> Para executar as rotinas de sincronização \n" +
                "java " + NOME_CLASSE + " E [ULT_PERIODO] [CAMINHO_SAIDA] --------------> Para gerar o arquivo com as exclusões para a folha \n" +
                "java " + NOME_CLASSE + " R [ULT_PERIODO] [0|1 p/ ajustar inf. folha] --> Para incluir as ocorrências de reimplante necessárias \n"
                ;

        if (args.length != 3) {
            LOG.error(msgErroPrincipal);
            return -1;
        } else {
            try {
                LOG.info("INICIO");
                LOG.warn("O LOG DO PROCESSAMENTO SERÁ IMPRESSO JUNTO DO LOG DO JBOSS!");
                SincronizadorController sincronizadorController = ApplicationContextProvider.getApplicationContext().getBean(SincronizadorController.class);

                String ultimoPeriodo = args[1];
                LOG.info("Últ Período: " + ultimoPeriodo);
                if (args[0].equalsIgnoreCase("S")) {
                    // Realiza a sincronização do arquivo de entrada com o sistema
                    String arqEntrada = args[2];
                    LOG.info("Arq. Entrada: " + arqEntrada);
                    sincronizadorController.sincronizarFolhaEConsig(arqEntrada, ultimoPeriodo);
                } else if (args[0].equalsIgnoreCase("E")) {
                    // Gera arquivo de exclusões
                    String arqSaida = sincronizadorController.gerarArquivoExclusoes(args[2], ultimoPeriodo);
                    LOG.info("Arq. Gerado: " + arqSaida);
                } else if (args[0].equalsIgnoreCase("R")) {
                    // Inclui as ocorrências de reimplante nas consignações necessárias
                    // ajustando a data ini/fim folha e valor folha
                    boolean ajustarInfFolha = args[2].equals("1");
                    LOG.info("Ajustar inf. folha? " + ajustarInfFolha);
                    // Consignações que devem ser alteradas
                    sincronizadorController.incluirAlteracaoFolha(ajustarInfFolha, ultimoPeriodo);
                    // Consignações que devem ser reimplantadas
                    sincronizadorController.incluirReimplanteFolha(ajustarInfFolha, ultimoPeriodo);
                }
                LOG.info("FIM");
                return 0;
            } catch (ZetraException ex) {
                LOG.error(ex.getMessage(), ex);
                return -1;
            }
        }
    }
}

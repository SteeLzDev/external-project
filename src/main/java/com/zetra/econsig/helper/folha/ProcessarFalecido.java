package com.zetra.econsig.helper.folha;

import java.util.Arrays;
import java.util.List;

import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.rotinas.RotinaExternaViaProxy;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ProcessarFalecido</p>
 * <p>Description: Classe para processamento de falecido via script</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessarFalecido implements RotinaExternaViaProxy {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessarFalecido.class);
    private static final String NOME_CLASSE = ProcessarFalecido.class.getName();

    @Override
    public int executar(String args[]) {
        try {
            if (args.length < 3 || !args[0].equals("-f")) {
                printOpcoes();
                return -1;
            } else {
                AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();
                responsavel.setFunCodigo(CodedValues.FUN_IMP_SER_FALECIDO);
                LOG.debug(ParamSist.getDiretorioRaizArquivos());

                List<String> lstArgs = Arrays.asList(args);
                boolean validaSerExcluido = !(lstArgs.contains("-nsx"));
                boolean liquidarAde = (lstArgs.contains("-lc"));

                String file = null;
                String tmo = null;
                String obs = null;

                for(int i = 0; i < args.length; i++) {
                    if (args[i].equals("-tmo")) {
                        try {
                            tmo = args[++i];

                            if (!TextHelper.isNull(tmo)) {
                                for(int j = 0; j < args.length; j++) {
                                    if (args[j].equals("-tmoObs")) {
                                        obs = args[++j];

                                        if (!TextHelper.isNull(obs)) {
                                            break;
                                        }
                                    }
                                }
                            } else {
                                printOpcoes();
                                throw new ZetraException("mensagem.informe.codigo.motivo.operacao", responsavel);
                            }
                        } catch (ArrayIndexOutOfBoundsException ex) {
                            printOpcoes();
                            throw new ZetraException("mensagem.informe.codigo.motivo.operacao", responsavel);
                        }
                    } else if (args[i].equals("-file")) {
                        try {
                            file = args[++i];

                            if (TextHelper.isNull(file)) {
                                printOpcoes();
                                throw new ZetraException("mensagem.informe.arquivo.falecido", responsavel);
                            }
                        } catch (ArrayIndexOutOfBoundsException ex) {
                            printOpcoes();
                            throw new ZetraException("mensagem.informe.arquivo.falecido", responsavel);
                        }
                    }
                }

                if (TextHelper.isNull(file)) {
                    printOpcoes();
                    throw new ZetraException("mensagem.informe.arquivo.falecido", responsavel);
                }

                // Importa falecidos
                ImportarFalecidoHelper helper = new ImportarFalecidoHelper(responsavel);
                helper.importaFalecido(file, validaSerExcluido, liquidarAde, tmo, obs);
                return 0;
            }
        } catch (ViewHelperException ex) {
            LOG.error(ex.getMessage(), ex);
            return -1;
        } catch (ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            return -1;
        }
    }

    private void printOpcoes() {
        LOG.error("USE: java " + NOME_CLASSE + " -f [-file NOME DO ARQUIVO] [OPÇÕES]\n" +
                "\n\n" +
                "OPÇÕES: \n" +
                "\n" +
                "-nsx Não valida se o servidor deve estar excluído para ser falecido\n" +
                "-lc  Liquida contratos do servidor falecido \n" +
                "[-tmo IDENTIFICADOR DO MOTIVO DE OPERAÇÃO]" +
                "[-tmoObs OBSERVAÇÃO DO MOTIVO DE OPERAÇÃO]" +
                "\n\n");
    }
}

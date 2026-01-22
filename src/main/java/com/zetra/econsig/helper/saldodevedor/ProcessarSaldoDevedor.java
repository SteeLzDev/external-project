package com.zetra.econsig.helper.saldodevedor;

import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.rotinas.RotinaExternaViaProxy;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ProcessarSaldoDevedor</p>
 * <p>Description: Classe para processamento de saldo devedor</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessarSaldoDevedor implements RotinaExternaViaProxy {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessarSaldoDevedor.class);
    private static final String NOME_CLASSE = ProcessarSaldoDevedor.class.getName();

    @Override
    public int executar(String args[]) {
        try {
            if (args.length < 4) {
                printOpcoes();
                return -1;
            } else {
                AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();
                // Seta informação de saldo devedor do servidor porque a importação não considera edição de saldo servidor para compra
                responsavel.setFunCodigo(CodedValues.FUN_EDT_SALDO_DEVEDOR_SOLICITACAO_SER);
                LOG.debug(ParamSist.getDiretorioRaizArquivos());

                String csaCodigo = "";
                String nomeArquivo = null;

                for(int i = 0; i < args.length; i++) {
                    if (args[i].equals("-file")) {
                        try {
                            nomeArquivo = args[++i];

                            if (TextHelper.isNull(nomeArquivo)) {
                                printOpcoes();
                                throw new ZetraException("mensagem.informe.arquivo.saldo.devedor", responsavel);
                            }
                        } catch (ArrayIndexOutOfBoundsException ex) {
                            printOpcoes();
                            throw new ZetraException("mensagem.informe.arquivo.saldo.devedor", responsavel);
                        }
                    } else if (args[i].equals("-csa")) {
                        try {
                            csaCodigo = args[++i];

                            if (TextHelper.isNull(csaCodigo)) {
                                printOpcoes();
                                throw new ZetraException("mensagem.informe.csa.identificador", responsavel);
                            }
                        } catch (ArrayIndexOutOfBoundsException ex) {
                            printOpcoes();
                            throw new ZetraException("mensagem.informe.csa.identificador", responsavel);
                        }
                    }
                }

                if (TextHelper.isNull(nomeArquivo)) {
                    printOpcoes();
                    throw new ZetraException("mensagem.informe.arquivo.saldo.devedor", responsavel);
                }

                if (TextHelper.isNull(csaCodigo)) {
                    printOpcoes();
                    throw new ZetraException("mensagem.informe.csa.identificador", responsavel);
                }

                // Importa saldo devedor
                ImportarSaldoDevedorHelper helper = new ImportarSaldoDevedorHelper(responsavel);
                helper.importar(nomeArquivo, csaCodigo, false, responsavel);
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
        LOG.error("USE: java " + NOME_CLASSE + " [-file NOME DO ARQUIVO] [-csa CODIGO DA CONSIGNATARIA]\n");
    }
}

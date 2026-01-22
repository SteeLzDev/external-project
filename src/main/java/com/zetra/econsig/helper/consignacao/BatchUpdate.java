package com.zetra.econsig.helper.consignacao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import com.zetra.econsig.delegate.ConsignacaoDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.rotinas.RotinaExternaViaProxy;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: BatchUpdate</p>
 * <p>Description: Executa atualizações em consignações, de acordo com arquivo de entrada</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class BatchUpdate implements RotinaExternaViaProxy {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(BatchUpdate.class);
    private static final String NOME_CLASSE = BatchUpdate.class.getName();

    @Override
    public int executar(String[] args) {
        if (args.length != 2) {
            LOG.debug("USE: java " + NOME_CLASSE + " NOME_ARQUIVO LOGIN_RESPONSAVEL"
                    + "\nNOME_ARQUIVO: nome completo do arquivo de entrada, que contém em cada linha, o comando de atualização de contratos"
                    + "\nLOGIN_RESPONSAVEL: login do usuário responsável pela operação"
                    + "\n"
                    + "\nO arquivo de entrada deve ter um comando por linha, com os seguintes campos separados por ';' "
                    + "\nOPERACAO   = C (Cancelar), L (Liquidar), D (Desliquidar), S (Suspender), R (Reativar)"
                    + "\nADE_CODIGO = Código do contrato a ser modificado"
                    + "\nTMO_CODIGO = Código do motivo da operação (Opcional)"
                    + "\nOCA_OBS    = Observação do motivo de operação (Opcional)"
                    );
            return -1;
        } else {
            BufferedReader reader = null;
            PrintWriter writer = null;

            try {
                String nomeArquivo = args[0];
                String usuario = args[1];

                AcessoSistema responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(usuario, null, null);
                ConsignacaoDelegate consignacaoDelegate = new ConsignacaoDelegate();
                CustomTransferObject motivo = null;

                reader = new BufferedReader(new FileReader(nomeArquivo));
                writer = new PrintWriter(new BufferedWriter(new FileWriter(nomeArquivo + ".out")));

                String linha = null;
                String[] entrada = null;
                while ((linha = reader.readLine()) != null) {
                    entrada = linha.split(";");

                    if (entrada != null && entrada.length >= 2) {
                        String operacao = entrada[0];
                        String adeCodigo = entrada[1];

                        if (entrada.length >= 3) {
                            motivo = new CustomTransferObject();
                            motivo.setAttribute(Columns.ADE_CODIGO, adeCodigo);
                            motivo.setAttribute(Columns.TMO_CODIGO, entrada[2]);
                            if (entrada.length >= 4) {
                                motivo.setAttribute(Columns.OCA_OBS, entrada[3]);
                            }
                        } else {
                            motivo = null;
                        }

                        try {
                            if (operacao.equalsIgnoreCase("C")) {
                                // Cancelar Consignação
                                consignacaoDelegate.cancelarConsignacao(adeCodigo, motivo, responsavel);

                            } else if (operacao.equalsIgnoreCase("L")) {
                                // Liquidar Consignação
                                consignacaoDelegate.liquidarConsignacao(adeCodigo, motivo, null, responsavel);

                            } else if (operacao.equalsIgnoreCase("D")) {
                                // Desliquidar Consignação
                                consignacaoDelegate.desliquidarConsignacao(adeCodigo, motivo, responsavel);

                            } else if (operacao.equalsIgnoreCase("S")) {
                                // Suspender Consignação
                                consignacaoDelegate.suspenderConsignacao(adeCodigo, motivo, responsavel);

                            } else if (operacao.equalsIgnoreCase("R")) {
                                // Reativar Consignação
                                consignacaoDelegate.reativarConsignacao(adeCodigo, motivo, null, responsavel);

                            } else {
                                throw new AutorizacaoControllerException("mensagem.erro.operacao.nao.suportada", responsavel);
                            }

                            // Mensagem de sucesso
                            writer.println(linha + ";" + "OK");

                        } catch (AutorizacaoControllerException ex) {
                            // Mensagem de erro
                            writer.println(linha + ";" + ex.getMessage());
                        }
                    }
                }
                return 0;

            } catch (ZetraException ex) {
                LOG.error(ex.getMessage(), ex);
                return -1;
            } catch (IOException ex) {
                LOG.error(ex.getMessage(), ex);
                return -1;
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException ex) {
                        LOG.error(ex.getMessage(), ex);
                    }
                }
                if (writer != null) {
                    writer.close();
                }
            }
        }
    }
}

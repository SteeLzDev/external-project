package com.zetra.econsig.helper.sistema;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;

/**
 * <p>Title: CopiaSegurancaHelper</p>
 * <p>Description: Classe auxiliar para criação de cópias de segurança (backup) do banco de dados.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class CopiaSegurancaHelper {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(CopiaSegurancaHelper.class);

    public static final String CLASSE_AGENDAMENTO = com.zetra.econsig.job.jobs.BackupJob.class.getName();

    // Para evitar vulnerabilidade reportada pelo Veracode, os comandos são fixos, devendo ser mapeados no docker-compose para o arquivo certo
    private static final String PATH_SCRIPT_BACKUP  = "/home/eConsig/admin/bin/backup_db.sh";
    private static final String PATH_SCRIPT_RESTORE = "/home/eConsig/admin/bin/restore_db.sh";

    private CopiaSegurancaHelper() {
    }

    public static File getCaminhoArquivos(AcessoSistema responsavel) {
        File rootDir = new File(ParamSist.getDiretorioRaizArquivos());
        return new File(rootDir, "backup/cse");

    }

    public static String exportar(AcessoSistema responsavel) throws ZetraException {
        try {
            final String nomeArquivo = "";
            final File arquivo = new File(getCaminhoArquivos(responsavel), nomeArquivo);

            final File comando = new File(PATH_SCRIPT_BACKUP);
            if (!comando.exists() || !comando.canExecute()) {
                throw new ZetraException("mensagem.erro.copia.seguranca.exportar.parametro.sistema.nao.encontrado", responsavel);
            }

            final String[] cmd = { comando.getAbsolutePath(), arquivo.getAbsolutePath() };
            final Process p = new ProcessBuilder(cmd).redirectErrorStream(true).start();
            final InputStream pOut = p.getInputStream();
            final Thread outputDrainer = new Thread() {
                @Override
                public void run() {
                    try {
                        int c;
                        do {
                            c = pOut.read();
//                            if (c >= 0) {
//                                System.out.print((char)c);
//                            }
                        } while (c >= 0);
                    } catch (IOException e) {
                        LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.copia.seguranca.exportar", responsavel), e);
                    }
                }
            };
            outputDrainer.start();

            p.waitFor();

            if (!arquivo.exists()) {
                throw new ZetraException("mensagem.erro.copia.seguranca.exportar.arquivo.nao.encontrado", responsavel, nomeArquivo);
            }
            return nomeArquivo;
        } catch (InterruptedException ex) {
            // Restore interrupted state...
            Thread.currentThread().interrupt();
            throw new ZetraException(ex);
        } catch (Exception ex) {
            throw new ZetraException(ex);
        }
    }

    public static void importar(String nomeArquivo, AcessoSistema responsavel) throws ZetraException {
        try {
            final File arquivo = new File(getCaminhoArquivos(responsavel), nomeArquivo);
            if (!arquivo.exists()) {
                throw new ZetraException("mensagem.erro.copia.seguranca.importar.arquivo.nao.encontrado", responsavel, nomeArquivo);
            }

            final File comando = new File(PATH_SCRIPT_RESTORE);
            if (!comando.exists() || !comando.canExecute()) {
                throw new ZetraException("mensagem.erro.copia.seguranca.importar.parametro.sistema.nao.encontrado", responsavel);
            }

            final String[] cmd = { comando.getAbsolutePath(), arquivo.getAbsolutePath() };
            final Process p = new ProcessBuilder(cmd).redirectErrorStream(true).start();
            final InputStream pOut = p.getInputStream();
            final Thread outputDrainer = new Thread() {
                @Override
                public void run() {
                    try {
                        int c;
                        do {
                            c = pOut.read();
//                            if (c >= 0) {
//                                System.out.print((char)c);
//                            }
                        } while (c >= 0);
                    } catch (IOException e) {
                        LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.copia.seguranca.importar", responsavel), e);
                    }
                }
            };
            outputDrainer.start();

            p.waitFor();

        } catch (InterruptedException ex) {
            // Restore interrupted state...
            Thread.currentThread().interrupt();
            throw new ZetraException(ex);
        } catch (Exception ex) {
            throw new ZetraException(ex);
        }
    }
}

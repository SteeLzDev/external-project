package com.zetra.econsig.helper.criptografia;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;

import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.rotinas.RotinaExternaViaProxy;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.usuario.UsuarioHelper;
import com.zetra.econsig.service.sistema.SistemaController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.TipoArquivoEnum;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: CriptografiaArquivos</p>
 * <p>Description: Criptografa e desriptografa arquivos do sistema</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class CriptografiaArquivos implements RotinaExternaViaProxy {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(CriptografiaArquivos.class);
    private static final String NOME_CLASSE = CriptografiaArquivos.class.getName();

    /**
     * Criptografa os arquivos na pasta externa do sistema onde são gerados
     * ou enviados arquivos para uso pela aplicação e que contém dados sensíveis.
     * @param responsavel
     */
    public static void criptografarArquivosSistema(AcessoSistema responsavel) {
        try {
            TipoArquivoEnum[] tiposArquivo = {
                    TipoArquivoEnum.ARQUIVO_CADASTRO_MARGENS,
                    TipoArquivoEnum.ARQUIVO_CADASTRO_MARGEM_COMPLEMENTAR,
                    TipoArquivoEnum.ARQUIVO_TRANSFERIDOS,
                    TipoArquivoEnum.ARQUIVO_RETORNO_INTEGRACAO,
                    TipoArquivoEnum.ARQUIVO_RETORNO_ATRASADO,
                    TipoArquivoEnum.ARQUIVO_CRITICA,
                    TipoArquivoEnum.ARQUIVO_CONTRACHEQUES,
                    TipoArquivoEnum.ARQUIVO_MOVIMENTO_FINANCEIRO
            };

            SistemaController sistemaController = ApplicationContextProvider.getApplicationContext().getBean(SistemaController.class);

            for (TipoArquivoEnum tipoArquivo : tiposArquivo) {
                String caminho = FileHelper.recuperaDiretorio(tipoArquivo, responsavel);
                String[] extensoes = {"txt", "TXT", "zip", "ZIP", "prc", "PRC", "ok", "OK"};
                Collection<File> arquivos = FileUtils.listFiles(new File(caminho), extensoes, true);
                for (File arquivo : arquivos) {
                    String nomeArquivo = arquivo.getAbsolutePath().substring(caminho.length());
                    String[] partesNomeArquivo = nomeArquivo.split(File.separator);
                    String tipoEntidade = null;
                    String codigoEntidade = null;

                    // entidade/codigo/nomeArquivo.ext
                    if (partesNomeArquivo.length == 3) {
                        tipoEntidade = partesNomeArquivo[0].toUpperCase();
                        codigoEntidade = partesNomeArquivo[1];
                        if (tipoEntidade.equals(AcessoSistema.ENTIDADE_CSE)) {
                            // cse/org_codigo/nomeArquivo.ext
                            tipoEntidade = AcessoSistema.ENTIDADE_ORG;
                        }
                    } else {
                        // Para os demais, considera a entidade consignante
                        tipoEntidade = AcessoSistema.ENTIDADE_CSE;
                        codigoEntidade = CodedValues.CSE_CODIGO_SISTEMA;
                    }

                    String papCodigo = UsuarioHelper.getPapCodigo(tipoEntidade);

                    LOG.debug("Criptografando arquivo \"" + nomeArquivo + "\" de " + tipoEntidade + " = " + codigoEntidade);
                    byte[] chave = sistemaController.obtemChaveCriptografiaArquivos(papCodigo, tipoArquivo.getCodigo(), codigoEntidade, responsavel);
                    String nomeArqCompleto = caminho + File.separator + nomeArquivo;
                    String nomeArqSaida = nomeArqCompleto + ".crypt";
                    boolean sucesso = AES.encryptFile(chave, nomeArqCompleto, nomeArqSaida);
                    if (sucesso) {
                        LOG.info("Arquigo gerado: " + nomeArqSaida);

                        // Remove o arquivo original
                        arquivo.delete();
                    }
                }
            }
        } catch (ConsignanteControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    public static File descriptografarArquivo(String nomeArquivo, boolean removeCriptografado, AcessoSistema responsavel) {
        try {
            File arquivo = new File(nomeArquivo);
            if (arquivo.exists() && arquivo.canRead()) {
                String absolutePath = ParamSist.getDiretorioRaizArquivos();
                if (arquivo.getAbsolutePath().startsWith(absolutePath)) {
                    String nomeArquivoRelativo = arquivo.getAbsolutePath().substring(absolutePath.length() + 1);
                    String[] partesNomeArquivo = nomeArquivoRelativo.split(File.separator);
                    // tipo/entidade/codigo/nomeArquivo.ext
                    TipoArquivoEnum tipoArquivo = FileHelper.recuperaTipoArquivo(partesNomeArquivo[0], responsavel);
                    if (tipoArquivo != null) {
                        String tipoEntidade = null;
                        String codigoEntidade = null;

                        // tipo/entidade/codigo/nomeArquivo.ext
                        if (partesNomeArquivo.length == 4) {
                            tipoEntidade = partesNomeArquivo[1].toUpperCase();
                            codigoEntidade = partesNomeArquivo[2];
                            if (tipoEntidade.equals(AcessoSistema.ENTIDADE_CSE)) {
                                // retorno/cse/org_codigo/nomeArquivo.ext
                                tipoEntidade = AcessoSistema.ENTIDADE_ORG;
                            }
                        } else {
                            // Para os demais, considera a entidade consignante
                            tipoEntidade = AcessoSistema.ENTIDADE_CSE;
                            codigoEntidade = CodedValues.CSE_CODIGO_SISTEMA;
                        }

                        String papCodigo = UsuarioHelper.getPapCodigo(tipoEntidade);

                        LOG.debug("Descriptografando arquivo \"" + nomeArquivoRelativo + "\" de " + tipoEntidade + " = " + codigoEntidade);
                        String nomeArquivoSaida = nomeArquivo.replaceAll("\\.crypt", "");
                        SistemaController sistemaController = ApplicationContextProvider.getApplicationContext().getBean(SistemaController.class);
                        byte[] chave = sistemaController.obtemChaveCriptografiaArquivos(papCodigo, tipoArquivo.getCodigo(), codigoEntidade, responsavel);
                        boolean sucesso = AES.decryptFile(chave, nomeArquivo, nomeArquivoSaida);
                        if (sucesso) {
                            LOG.info("Arquigo gerado: " + nomeArquivoSaida);

                            if (removeCriptografado) {
                                // Remove o arquivo original
                                arquivo.delete();
                            }

                            // Retorna o arquivo gerado
                            return new File(nomeArquivoSaida);
                        }
                    } else {
                        LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.arquivo.tipo.desconhecido", responsavel, nomeArquivo));
                    }
                } else {
                    LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.arquivo.nao.pertence.sistema", responsavel, nomeArquivo));
                }
            } else {
                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.arquivo.nao.existe", responsavel, nomeArquivo));
            }
        } catch (ConsignanteControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public int executar(String[] args) {
        if (args == null || args.length < 1 || args.length > 2) {
            printOpcoes();
            return -1;
        }
        if (args[0].equals("-c") && args.length == 1) {
            criptografarArquivosSistema(AcessoSistema.getAcessoUsuarioSistema());
            return 0;
        } else if (args[0].equals("-d") && args.length == 2) {
            descriptografarArquivo(args[1], true, AcessoSistema.getAcessoUsuarioSistema());
            return 0;
        } else {
            printOpcoes();
            return -1;
        }
    }

    private void printOpcoes() {
        LOG.info("USE: \n\n"
               + "1) Para percorrer os diretorios e criptografar os arquivos: \n"
               + "java " + NOME_CLASSE + " -c \n\n"
               + "2) Para descriptografar o arquivo informado: \n"
               + "java " + NOME_CLASSE + " -d /caminho/completo/arquivo.crypt \n"
           );
    }
}

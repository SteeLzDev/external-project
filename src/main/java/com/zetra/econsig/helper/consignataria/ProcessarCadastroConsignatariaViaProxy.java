package com.zetra.econsig.helper.consignataria;

import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.rotinas.RotinaExternaViaProxy;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessarCadastroConsignatariaViaProxy</p>
 * <p>Description: Classe para processamento de arquivo de cadastro de consignatarias via script</p>
 * <p>Copyright: Copyright (c) 2024</p>
 * <p>Company: ZetraSoft</p>
 * $Author: $
 * $Revision: $
 * $Date: $
 */
public class ProcessarCadastroConsignatariaViaProxy implements RotinaExternaViaProxy {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessarCadastroConsignatariaViaProxy.class);
    private static final String NOME_CLASSE = ProcessarCadastroConsignatariaViaProxy.class.getName();

    @Override
    public int executar(String args[]) {
        String nomeArquivoEntrada = null;
        boolean validar = false;

        // Simula o responsável como sendo um usuário papel CSE com permissão para a função 401
        final AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();
        responsavel.setTipoEntidade(AcessoSistema.ENTIDADE_CSE);
        responsavel.setCodigoEntidade(CodedValues.CSE_CODIGO_SISTEMA);
        responsavel.setFunCodigo(CodedValues.FUN_EDT_CONSIGNATARIAS);
        // Validar a existência de pelo menos 2 parâmentros (-f arquivo)
        try {
            if (args.length < 2) {
                printOpcoes();
                return -1;
            } else {
                for (int i = 0; i < args.length; i++) {
                    if ("-f".equals(args[i]) && ((i + 1) < args.length)) {
                        nomeArquivoEntrada = args[i + 1];
                    } else if ("-v".equals(args[i])) {
                        validar = true;
                    }
                }

                if (TextHelper.isNull(nomeArquivoEntrada)) {
                    throw new ZetraException("mensagem.informe.arquivo.cadastro.consignatarias", responsavel);
                }
            }
        } catch (final ZetraException ex) {
            LOG.error(ex.getMessage());
            printOpcoes();
            return -1;
        }

        try {
            // Faz a importação do arquivo de lote
            final ConsignatariaController consignatariaController = ApplicationContextProvider.getApplicationContext().getBean(ConsignatariaController.class);
            consignatariaController.impCadastroConsignatarias(nomeArquivoEntrada, validar, responsavel);

            // Caso seja validação não renomeia o arquivo com informação + "ok"
            if (!validar) {
                LOG.info(ApplicationResourcesHelper.getMessage("mensagem.informacao.processamento.realizado.sucesso", responsavel));
            } else {
                LOG.info(ApplicationResourcesHelper.getMessage("mensagem.informacao.validacao.realizada.sucesso", responsavel));
            }

            return 0;
        } catch (final ConsignatariaControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            return -1;
        }
    }

    private void printOpcoes() {
        LOG.error("USE: java " + NOME_CLASSE + " -f ARQUIVO.TXT [OPÇÕES]\n"
                + "ARQUIVO.TXT: Nome do arquivo a ser processado ou validado, sem o caminho, presente na pasta de arquivos do sistema"
                + "\n\n"
                + "OPÇÕES: \n"
                + "\n"
                + "-v Caso seja apenas para validação\n"
                );
    }
}

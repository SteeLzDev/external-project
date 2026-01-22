package com.zetra.econsig.helper.servidor;

import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.rotinas.RotinaExternaViaProxy;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ProcessarDesligadosBloqueados</p>
 * <p>Description: Classe para processamento de arquivo de desligados e bloqueados via script</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author: igor.lucas $
 * $Revision: 26289 $
 * $Date: 2019-02-22 15:08:57 -0300 (sex, 22 fev 2019) $
 */
public class ProcessarDesligadoBloqueadoViaProxy implements RotinaExternaViaProxy {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessarDesligadoBloqueadoViaProxy.class);
    private static final String NOME_CLASSE = ProcessarDesligadoBloqueadoViaProxy.class.getName();

    @Override
    public int executar(String args[]) {
        String nomeArquivoEntrada = null;
        boolean validar = false;

        // Simula o responsável como sendo um usuário papel CSE com permissão para a função 401
        AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();
        responsavel.setTipoEntidade(AcessoSistema.ENTIDADE_CSE);
        responsavel.setCodigoEntidade(CodedValues.CSE_CODIGO_SISTEMA);
        responsavel.setFunCodigo(CodedValues.FUN_IMP_SER_DESLIGADO_BLOQUEADO);

        // Validar a existência de pelo menos 2 parâmentros (-f arquivo)
        try {
            if (args.length < 2) {
                printOpcoes();
                return -1;
            } else {
                for (int i = 0; i < args.length; i++) {
                    if ("-f".equals(args[i]) && i + 1 < args.length) {
                        nomeArquivoEntrada = args[i + 1];
                    } else if ("-v".equals(args[i])) {
                        validar = true;
                    }
                }

                if (TextHelper.isNull(nomeArquivoEntrada)) {
                    throw new ZetraException("mensagem.informe.arquivo.bloqueados.falecidos", responsavel);
                }
            }
        } catch (ZetraException ex) {
            LOG.error(ex.getMessage());
            printOpcoes();
            return -1;
        }

        // Execução do método importar arquivo desligado e bloqueado e inclusão de informações no arquivos processados
        try {
            // Faz a importação do arquivo de lote
            ServidorDelegate serDelegate = new ServidorDelegate();
            serDelegate.importaDesligadoBloqueado(nomeArquivoEntrada, validar, responsavel);

            // Caso seja validação não renomeia o arquivo com informação + "ok"
            if (!validar) {
                LOG.info(ApplicationResourcesHelper.getMessage("mensagem.informacao.processamento.realizado.sucesso", responsavel));
            } else {
                LOG.info(ApplicationResourcesHelper.getMessage("mensagem.informacao.validacao.realizada.sucesso", responsavel));
            }

            return 0;
        } catch (ServidorControllerException ex) {
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

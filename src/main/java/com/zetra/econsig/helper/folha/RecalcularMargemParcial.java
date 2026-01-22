package com.zetra.econsig.helper.folha;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.exception.MargemControllerException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.rotinas.RotinaExternaViaProxy;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: RecalcularMargemParcial</p>
 * <p>Description: Classe util para fazer recálculo de margem parcial via arquivo</p>
 * <p>Copyright: Copyright (c) 2020</p>
 * <p>Company: Nostrum Consultoria e Projetos.</p>
 * $Author: marcos.nolasco $
 * $Revision:  $
 * $Date: 2020-01-15 18:34:41 -0300 (ter, 15 jan 2020) $
 */
public class RecalcularMargemParcial implements RotinaExternaViaProxy {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RecalcularMargemParcial.class);
    private static final String NOME_CLASSE = RecalcularMargemParcial.class.getName();

    @Override
    public int executar(String args[]) {
        List<String> lstArgs = Arrays.asList(args);

        String arqMatriculaServidor = null;

        AcessoSistema responsavel = new AcessoSistema(CodedValues.USU_CODIGO_SISTEMA);

        if (lstArgs.isEmpty()) {
            ajuda();
            return -1;
        }

        if (lstArgs.contains("-h")) {
            ajuda();
            return -1;
        }

        if (lstArgs.contains("-arqMargParcial")) {
            arqMatriculaServidor = getOpcoesParametro("-arqMargParcial", lstArgs);
        }

        if (!TextHelper.isNull(arqMatriculaServidor) && new File(arqMatriculaServidor).exists() && arqMatriculaServidor.toLowerCase().endsWith(".txt")) {
            List<String> fileToList = FileHelper.readAllToList(arqMatriculaServidor);

            if (fileToList != null && !fileToList.isEmpty()) {
                String rseCodigo = null;
                Iterator<String> iteFileToList = fileToList.iterator();
                while (iteFileToList.hasNext()) {
                    rseCodigo = iteFileToList.next().trim();
                    List<String> rseCodigos = new ArrayList<>();
                    rseCodigos.add(rseCodigo);
                    try {
                        recalculaMargemParcial(rseCodigos, responsavel);
                    } catch (MargemControllerException e) {
                        LOG.error(ApplicationResourcesHelper.getMessage("mensagem.info.recalculo.margem.parcial.servidor", responsavel, rseCodigos.toString()));
                    }
                }
            }
        } else {
            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.info.recalculo.margem.parcial.arquivo", responsavel, arqMatriculaServidor));
            ajuda();
            return -1;
        }

        return 0;
    }

    /**
    * Realiza parse dos parametro informado.
    * @param parametro
    * @param argumentosEntrada
    * @param esperaData
    * @return
    */
    private String getOpcoesParametro(String parametro, List<String> argumentosEntrada) {
        int lastPos = argumentosEntrada.lastIndexOf(parametro);
        List<String> tmp = argumentosEntrada.subList(lastPos + 1, argumentosEntrada.size());

        StringBuilder volta = new StringBuilder();

        for (String s : tmp) {
            if (s.startsWith("-")) {
                break;
            } else {
                volta.append(s);
                volta.append(" ");
            }
        }

        return volta.toString().trim();
    }

    @Transactional
    private void recalculaMargemParcial(List<String> rseCodigos, AcessoSistema responsavel) throws MargemControllerException {

        ServidorDelegate servidor = new ServidorDelegate();

        servidor.recalculaMargemComHistorico("RSE", rseCodigos, responsavel);
        LOG.info(ApplicationResourcesHelper.getMessage("mensagem.info.recalculo.margem.parcial.servidor.sucesso", responsavel, rseCodigos.toString()));
    }

    private void ajuda() {
        StringBuilder ajuda = new StringBuilder();

        ajuda.append(System.lineSeparator());
        ajuda.append(NOME_CLASSE).append(": ");
        ajuda.append(System.lineSeparator());
        ajuda.append("-h : Exibe essa ajuda");
        ajuda.append(System.lineSeparator());
        ajuda.append("-arqMargParcial: Arquivo com a lista de matrículas, deve-se passar o caminho completo do arquivo");
        ajuda.append(System.lineSeparator());

        LOG.info(ajuda);
    }
}

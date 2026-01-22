package com.zetra.econsig.helper.relatorio;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.RelatorioControllerException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.relatorio.RelatorioController;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: RelatorioHelper</p>
 * <p>Description: Helper Class para relatórios.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioHelper {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RelatorioHelper.class);

    public static void executarLimpeza(AcessoSistema responsavel) throws RelatorioControllerException {
        String absolutePath = ParamSist.getDiretorioRaizArquivos();
        String diretorioRaiz = absolutePath + File.separatorChar + "relatorio" + File.separatorChar;

        List<TransferObject> relatorios = recuperaRelatorios();

        excluiRelatoriosAntigos(diretorioRaiz, relatorios, responsavel);

    }

    private static List<TransferObject> recuperaRelatorios() throws RelatorioControllerException {
        RelatorioController relatorioController = ApplicationContextProvider.getApplicationContext().getBean(RelatorioController.class);
        CustomTransferObject filtro = new CustomTransferObject();
        return relatorioController.lstRelatorio(filtro);
    }

    private static void excluiRelatoriosAntigos(String diretorio, List<TransferObject> relatorios, AcessoSistema responsavel) {
        File file = new File(diretorio);
        String files[] = file.list();
        Calendar cal = Calendar.getInstance();
        Date now = cal.getTime();

        int diasLimpeza = recuperaQtdDiasLimpeza(diretorio, relatorios);

        for (String pathname : files) {
            String caminhoCompleto = diretorio + pathname;
            File arquivo = new File(caminhoCompleto);

            cal.setTimeInMillis(arquivo.lastModified());
            Date date = cal.getTime();

            if (arquivo.isFile() && diasLimpeza > 0 && DateHelper.dayDiff(now, date) > diasLimpeza &&
                (pathname.toLowerCase().endsWith(".txt") || pathname.toLowerCase().endsWith(".pdf") || pathname.toLowerCase().endsWith(".zip"))) {
                try {
                    arquivo.delete();
                    LOG.debug("Exclusão do arquivo: ['" + arquivo.getAbsolutePath() + "'].");
                    LogDelegate logDelegate = new LogDelegate(responsavel, Log.ARQUIVO, Log.DELETE_FILE, Log.LOG_INFORMACAO);
                    logDelegate.add(ApplicationResourcesHelper.getMessage("rotulo.log.arquivo", responsavel, arquivo.getAbsolutePath()));
                    logDelegate.write();
                } catch (LogControllerException e) {
                    LOG.error("Não foi possível incluir log de exclusão do arquivo: ['" + arquivo.getAbsolutePath() + "'].", e);
                }
            } else if (arquivo.isDirectory()) {
                excluiRelatoriosAntigos(caminhoCompleto + File.separatorChar, relatorios, responsavel);
            }
        }
    }

    private static int recuperaQtdDiasLimpeza(String diretorio, List<TransferObject> relatorios) {
        int diasLimpeza = 0;
        Iterator<TransferObject> ite = relatorios.iterator();
        while (ite.hasNext()) {
            TransferObject to = ite.next();
            if (diretorio.contains(File.separatorChar + to.getAttribute(Columns.REL_CODIGO).toString() + File.separatorChar)) {
                diasLimpeza = Integer.parseInt(to.getAttribute(Columns.REL_QTD_DIAS_LIMPEZA).toString());
                break;
            }
        }
        return diasLimpeza;
    }

    public static String getCaminhoRelatorio(String tipoRelatorio, String csaCodigo, AcessoSistema responsavel) {
        String raizArquivos = ParamSist.getDiretorioRaizArquivos();

        if (raizArquivos == null) {
            LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return null;
        }

        String entidade = "cse";
        if (responsavel.isCseSup() && tipoRelatorio.equals("integracao_csa")) {
            entidade = "csa";
        } else if (responsavel.isCsa()) {
            entidade = "csa";
        } else if (responsavel.isCor()) {
            entidade = "cor";
        } else if (responsavel.isSer()) {
            entidade = "ser";
        }

        String tipo = tipoRelatorio;
        if (responsavel.isCseSup() && tipoRelatorio.equals("integracao_csa")) {
            tipo = "integracao";
        }

        String caminhoRelatorio = raizArquivos
                                + File.separatorChar + "relatorio"
                                + File.separatorChar + entidade
                                + File.separatorChar + tipo;

        if (!responsavel.isCseSup() && responsavel.getCodigoEntidade() != null) {
            caminhoRelatorio += File.separatorChar + responsavel.getCodigoEntidade();
        } else if (responsavel.isCseSup() && tipoRelatorio.equals("integracao_csa") && !TextHelper.isNull(csaCodigo)) {
            caminhoRelatorio += File.separatorChar + csaCodigo;
        }

        // Garante que existirão os diretórios especificados pelo path.
        try {
            FileUtils.forceMkdir(new File(caminhoRelatorio));
        } catch (IOException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        return caminhoRelatorio;
    }
}

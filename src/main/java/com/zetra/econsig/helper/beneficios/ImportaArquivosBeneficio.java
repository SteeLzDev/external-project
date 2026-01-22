package com.zetra.econsig.helper.beneficios;

import java.util.Arrays;
import java.util.List;

import com.zetra.econsig.helper.rotinas.RotinaExternaViaProxy;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.beneficios.BeneficiarioController;
import com.zetra.econsig.service.beneficios.ImportaArquivosBeneficioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ImportaArquivosBeneficio</p>
 * <p>Description: Classe util para fazer importação de arquivos do modulo de beneficio</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Nostrum Consultoria e Projetos.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ImportaArquivosBeneficio implements RotinaExternaViaProxy {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ImportaArquivosBeneficio.class);
    private static final String NOME_CLASSE = ImportaArquivosBeneficio.class.getName();

    @Override
    public int executar(String args[]) {
        List<String> lstArgs = Arrays.asList(args);

        String csaCodigo = null;
        String arquivoRetornoOperadora = null;
        String arquivoImpBeneficiarios = null;

        boolean importaArquivoOperadora = false;

        AcessoSistema responsavel = new AcessoSistema(CodedValues.USU_CODIGO_SISTEMA);

        ImportaArquivosBeneficioController importaArquivosBeneficioController = ApplicationContextProvider.getApplicationContext().getBean(ImportaArquivosBeneficioController.class);
        BeneficiarioController beneficiarioController = ApplicationContextProvider.getApplicationContext().getBean(BeneficiarioController.class);
        try {
            if (lstArgs.contains("-h")) {
                ajuda();
                return -1;
            }

            if (lstArgs.contains("-csaCodigo")) {
                csaCodigo = getOpcoesParametro("-csaCodigo", lstArgs, false);
            }

            if (lstArgs.contains("-arquivoRetornoOperadora")) {
                arquivoRetornoOperadora = getOpcoesParametro("-arquivoRetornoOperadora", lstArgs, false);
            }

            if (lstArgs.contains("-importaArquivoOperadora")) {
                importaArquivoOperadora = true;
            }

            if (lstArgs.contains("-importaBeneficiarios")) {
                arquivoImpBeneficiarios = getOpcoesParametro("-importaBeneficiarios", lstArgs, false);
            }

            if (importaArquivoOperadora) {
                importaArquivosBeneficioController.importaArquivoRetornoOperadora(csaCodigo, arquivoRetornoOperadora, responsavel);
            } else if(!TextHelper.isNull(arquivoImpBeneficiarios)) {
                beneficiarioController.importaBeneficiariosDependentes(arquivoImpBeneficiarios, responsavel);
            } else {
                ajuda();
                return -1;
            }
        } catch (Exception e) {
            LOG.error(e.getCause(), e);
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
   private String getOpcoesParametro(String parametro, List<String> argumentosEntrada, boolean esperaData) {
       int lastPos = argumentosEntrada.lastIndexOf(parametro);
       List<String> tmp = argumentosEntrada.subList(lastPos + 1, argumentosEntrada.size());

       StringBuilder volta = new StringBuilder();

       for (String s : tmp) {
           if (s.startsWith("-")) {
               break;
           } else {
               if (!esperaData) {
                   volta.append(s);
               } else {
                   volta.append(s);
                   volta.append(" ");
               }
           }
       }

       return volta.toString().trim();
   }

    private void ajuda() {
        StringBuilder ajuda = new StringBuilder();

        ajuda.append(System.lineSeparator());
        ajuda.append(NOME_CLASSE).append(": ");
        ajuda.append(System.lineSeparator());
        ajuda.append("-h : Exibe essa ajuda");
        ajuda.append(System.lineSeparator());
        ajuda.append("-importaArquivoOperadora: Importa o arquivo de retorno da operadora de beneficio.");
        ajuda.append(System.lineSeparator());
        ajuda.append("-arquivoRetornoOperadora: Informa o nome do arquivo a ser usado para importação, informar somente o nome do arquivo.");
        ajuda.append(System.lineSeparator());
        ajuda.append("-csaCodigo: Informa o CSA_CODIGO para ser utilizado na rotina de importação.");
        ajuda.append(System.lineSeparator());
        ajuda.append("-importaBeneficiarios: Informa o nome do arquivo a ser usado para importação, informar somente o nome do arquivo.");
        ajuda.append(System.lineSeparator());

        LOG.info(ajuda);
    }
}

package com.zetra.econsig.folha.margem.impl;

import com.zetra.econsig.exception.ImportaMargemException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.folha.margem.ImportaMargemBase;
import com.zetra.econsig.helper.folha.ImportarFalecidoHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImportaMargemAssisprev extends ImportaMargemBase {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ImportaMargemAssisprev.class);

    @Override
    public void posImportacaoMargem(String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws ImportaMargemException {

        if(responsavel.temPermissao(CodedValues.FUN_IMP_SER_FALECIDO)) {
            String pathDiretorio = ParamSist.getDiretorioRaizArquivos();
            String pathFalecido = pathDiretorio + File.separatorChar + "falecido" + File.separatorChar + AcessoSistema.ENTIDADE_CSE.toLowerCase();

            File diretorio = new File(pathFalecido);
            if (!diretorio.exists() && !diretorio.mkdirs()) {
                throw new ImportaMargemException("mensagem.gap.erro.criacao.diretorio", responsavel, diretorio.getAbsolutePath());
            }

            FileFilter filtro = new FileFilter() {
                @Override
                public boolean accept(File arq) {
                    String arqNome = arq.getName().toLowerCase();
                    return (arqNome.endsWith(".txt") || arqNome.endsWith(".zip"));
                }
            };

            List<File> arquivos = null;
            File[] temp = diretorio.listFiles(filtro);
            if (temp != null) {
                arquivos = new ArrayList<>(Arrays.asList(temp));
            }

            ImportarFalecidoHelper falecidoHelper = new ImportarFalecidoHelper(responsavel);

            List<String> criticas = new ArrayList<>();
            try {
                if (!TextHelper.isNull(arquivos)) {
                    for (File arquivo : arquivos) {
                        String nome = arquivo.getPath().substring(pathFalecido.length() + (pathFalecido.charAt(pathFalecido.length() - 1) == File.separatorChar ? 0 : 1));
                        if (!nome.toLowerCase().startsWith(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.critica.prefixo", responsavel))) {
                            String critica = falecidoHelper.importaFalecido(nome, true, true, null, null);
                            if (critica != null) {
                                criticas.add(critica);
                            }
                        }
                    }
                }
            } catch (ViewHelperException ex) {
                LOG.error(ex.getMessage());
            }

            if (!criticas.isEmpty()) {
                LOG.debug("Gerada critica(s) ao processar arquivo de falecidos");
            } else {
                LOG.debug("Processar arquivo de falecidos concluído");
            }
        }
    }
}

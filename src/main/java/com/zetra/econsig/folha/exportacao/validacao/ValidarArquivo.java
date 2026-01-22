package com.zetra.econsig.folha.exportacao.validacao;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.rotinas.RotinaExternaViaProxy;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.folha.ValidacaoMovimentoController;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ValidarArquivo</p>
 * <p>Description: Valida um arquivo de movimento. Classe a ser chamada por um controller, para garantir controle de transação.</p>
 * <p>Copyright: Copyright (c) 2003-2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ValidarArquivo implements RotinaExternaViaProxy {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ValidarArquivo.class);
    private static final String NOME_CLASSE = ValidarArquivo.class.getName();

    private List<String> getEstabelecimentos(AcessoSistema responsavel) throws ConsignanteControllerException {
        ConsignanteDelegate delegate = new ConsignanteDelegate();
        List<TransferObject> estabelecimentos = delegate.lstEstabelecimentos(null, responsavel);
        Iterator<TransferObject> it = estabelecimentos.iterator();
        List<String> codigos = new ArrayList<>();
        while (it.hasNext()) {
            codigos.add(it.next().getAttribute(Columns.EST_CODIGO).toString());
        }
        return codigos;
    }

    private List<String> getOrgaos(AcessoSistema responsavel) throws ConsignanteControllerException {
        ConsignanteDelegate delegate = new ConsignanteDelegate();
        List<TransferObject> orgaos = delegate.lstOrgaos(null, responsavel);
        Iterator<TransferObject> it = orgaos.iterator();
        List<String> codigos = new ArrayList<>();
        while (it.hasNext()) {
            codigos.add(it.next().getAttribute(Columns.ORG_CODIGO).toString());
        }
        return codigos;
    }

    @Override
    public int executar(String[] args) {
        try {
            if (args.length != 4) {
                LOG.debug("USE: java " + NOME_CLASSE + " NOME_ARQUIVO [ESTABELECIMENTOS] [ORGAOS] RESPONSAVEL" +
                        "\nNOME_ARQUIVO: Nome do arquivo a ser validado" +
                        "\nESTABELECIMENTOS: lista de códigos separados por vírgula. usar 'todos' para exportar todos os estabelecimentos" +
                        "\nORGAOS: lista de códigos separados por vírgula usar 'todos' para exportar todos os órgãos" +
                        "\nRESPONSAVEL: código do usuário" +
                        "\n***** Usar [] para indicar branco nos campos opcionais");
                return -1;
            }

            String arq = args[0];
            File file = new File(arq);
            if (!file.exists()) {
                throw new ZetraException("mensagem.erro.exportacao.validacao.arquivo.inexistente", (AcessoSistema)null);
            }

            String arquivo = file.getCanonicalPath();

            AcessoSistema responsavel = new AcessoSistema(args[3]);

            // O "[]" é obrigatório para prevenir problemas
            if (!(args[1].endsWith("]") && args[1].substring(0, 1).equals("[")
                    && args[2].endsWith("]") && args[2].substring(0, 1).equals("["))) {
                throw new ZetraException("mensagem.erro.exportacao.validacao.paramentros.entre.colchetes",(AcessoSistema)null);
            }

            String est = args[1].substring(1, args[1].length() - 1);
            List<String> estCodigos = est.equals("") ? null : est.equalsIgnoreCase("todos") ? getEstabelecimentos(responsavel) : Arrays.asList(TextHelper.split(est, ","));
            String org = args[2].substring(1, args[2].length() - 1);
            List<String> orgCodigos = org.equals("") ? null : org.equalsIgnoreCase("todos") ? getOrgaos(responsavel) : Arrays.asList(TextHelper.split(org, ","));

            ValidacaoMovimentoController validacaoMovimentoController = ApplicationContextProvider.getApplicationContext().getBean(ValidacaoMovimentoController.class);
            validacaoMovimentoController.validarArquivoMovimento(arquivo, estCodigos, orgCodigos, responsavel);
            return 0;

        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return -1;
        }
    }
}

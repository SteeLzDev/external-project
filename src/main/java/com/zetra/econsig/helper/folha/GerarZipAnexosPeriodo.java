package com.zetra.econsig.helper.folha;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.delegate.ConvenioDelegate;
import com.zetra.econsig.delegate.ExportaMovimentoDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.helper.rotinas.RotinaExternaViaProxy;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: GerarZipAnexosPeriodo</p>
 * <p>Description: Classe utilitária para criar arquivo compactado com os anexos de contrato
 *                 incluídos no período e que serão exportados para a folha.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
* $Author$
 * $Revision$
 * $Date$
 */
public class GerarZipAnexosPeriodo implements RotinaExternaViaProxy {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(GerarZipAnexosPeriodo.class);
    private static final String NOME_CLASSE = GerarZipAnexosPeriodo.class.getName();

    private List<String> getEstabelecimentos(AcessoSistema responsavel) throws ConsignanteControllerException {
        ConsignanteDelegate delegate = new ConsignanteDelegate();
        List<TransferObject> estabelecimentos = delegate.lstEstabelecimentos(null, responsavel);
        Iterator<TransferObject> it = estabelecimentos.iterator();
        List<String> codigos = new ArrayList<>();
        while (it.hasNext()) {
            codigos.add((String) it.next().getAttribute(Columns.EST_CODIGO));
        }
        return codigos;
    }

    private List<String> getOrgaos(AcessoSistema responsavel) throws ConsignanteControllerException {
        ConsignanteDelegate delegate = new ConsignanteDelegate();
        List<TransferObject> orgaos = delegate.lstOrgaos(null, responsavel);
        Iterator<TransferObject> it = orgaos.iterator();
        List<String> codigos = new ArrayList<>();
        while (it.hasNext()) {
            codigos.add((String) it.next().getAttribute(Columns.ORG_CODIGO));
        }
        return codigos;
    }

    private List<String> getVerbas(AcessoSistema responsavel) throws ConvenioControllerException {
        ConvenioDelegate delegate = new ConvenioDelegate();
        List<TransferObject> convenios = delegate.lstConvenios(null, null, null, null, false, responsavel);
        Iterator<TransferObject> it = convenios.iterator();
        Map<String, String> verbas = new HashMap<>();
        while (it.hasNext()) {
            verbas.put((String) it.next().getAttribute(Columns.CNV_COD_VERBA), "ok");
        }
        return (new ArrayList<>(verbas.keySet()));
    }

    @Override
    public int executar(String[] args) {
        if (args.length != 5) {
            LOG.debug("USE: java " + NOME_CLASSE + " [ESTABELECIMENTOS] [ORGAOS] [VERBAS] NOME_ARQ_SAIDA RESPONSAVEL" +
                    "\nESTABELECIMENTOS: lista de códigos separados por vírgula. usar 'todos' para exportar todos os estabelecimentos" +
                    "\nORGAOS: lista de códigos separados por vírgula usar 'todos' para exportar todos os órgãos" +
                    "\nVERBAS: lista de verbas separadas por vírgula. usar 'todas' para exportar todas as verbas" +
                    "\nNOME_ARQ_SAIDA: nome do arquivo compactado que será gerado" +
                    "\nRESPONSAVEL: código do usuário" +
                    "\n***** Usar [] para indicar branco nos campos opcionais");
            return -1;
        } else {
            ExportaMovimentoDelegate expDelegate = new ExportaMovimentoDelegate();

            AcessoSistema responsavel = new AcessoSistema(args[4]);

            try {
                String est = args[0].substring(1, args[0].length() - 1);
                List<String> estCodigos = est.equals("") ? null : est.equalsIgnoreCase("todos") ? getEstabelecimentos(responsavel) : Arrays.asList(TextHelper.split(est, ","));
                String org = args[1].substring(1, args[1].length() - 1);
                List<String> orgCodigos = org.equals("") ? null : org.equalsIgnoreCase("todos") ? getOrgaos(responsavel) : Arrays.asList(TextHelper.split(org, ","));
                String tmp;
                if(args[2].endsWith("]")){
                    tmp = args[2].substring(1, args[2].length() - 1);
                } else {
                    tmp = args[2];
                }
                List<String> verbas = tmp.equals("") ? null : tmp.equalsIgnoreCase("todas") ? getVerbas(null) : Arrays.asList(TextHelper.split(tmp, ","));

                LOG.debug("Recuperando anexos para as seguintes entidades:");
                LOG.debug("Estabelecimentos: " + estCodigos);
                LOG.debug("Orgaos..........: " + orgCodigos);
                LOG.debug("Verbas..........: " + verbas);


                String arqGerado = expDelegate.compactarAnexosAdePeriodo(orgCodigos, estCodigos, verbas, args[3], responsavel);

                LOG.debug("Arquivo compactado com anexos de contrato criado: " + arqGerado);
                return 0;

            } catch (ConsignanteControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                return -1;
            } catch (ConvenioControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                return -1;
            }
        }
    }
}

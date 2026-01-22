package com.zetra.econsig.helper.folha;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.AbstractFileFilter;
import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.helper.Pair;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ArquivoIntegracaoHelper</p>
 * <p>Description: Utilitário para arquivos de integração.</p>
 * <p>Copyright: Copyright (c) 2002-2016</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ArquivoIntegracaoHelper {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ArquivoIntegracaoHelper.class);

    public static List<Pair<File, String>> listarArquivosMargem(AcessoSistema responsavel) {
        return listarArquivosIntegracao("margem", responsavel);
    }

    public static List<Pair<File, String>> listarArquivosMargemComplementar(AcessoSistema responsavel) {
        return listarArquivosIntegracao("margemcomplementar", responsavel);
    }

    public static List<Pair<File, String>> listarArquivosTransferidos(AcessoSistema responsavel) {
        return listarArquivosIntegracao("transferidos", responsavel);
    }

    public static List<Pair<File, String>> listarArquivosRetorno(AcessoSistema responsavel) {
        return listarArquivosIntegracao("retorno", responsavel);
    }

    public static List<Pair<File, String>> listarArquivosRetornoAtrasado(AcessoSistema responsavel) {
        return listarArquivosIntegracao("retornoatrasado", responsavel);
    }

    public static List<Pair<File, String>> listarArquivosCritica(AcessoSistema responsavel) {
        return listarArquivosIntegracao("critica", responsavel);
    }

    public static List<Pair<File, String>> listarArquivosHistorico(AcessoSistema responsavel) {
        return listarArquivosIntegracao("historico", responsavel);
    }

    private static List<Pair<File, String>> listarArquivosIntegracao(String diretorioArquivos, AcessoSistema responsavel) {
        try {
            Set<String> estCodigos = new HashSet<>();
            Set<String> orgCodigos = new HashSet<>();
            Map<String, String> estIdentificadores = new HashMap<>();
            Map<String, String> orgIdentificadores = new HashMap<>();

            ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
            List<TransferObject> orgaos = cseDelegate.lstOrgaos(null, responsavel);
            for (TransferObject orgao : orgaos) {
                String estCodigo = orgao.getAttribute(Columns.EST_CODIGO).toString();
                String estIdentificador = orgao.getAttribute(Columns.EST_IDENTIFICADOR).toString();
                String orgCodigo = orgao.getAttribute(Columns.ORG_CODIGO).toString();
                String orgIdentificador = orgao.getAttribute(Columns.ORG_IDENTIFICADOR).toString();

                estCodigos.add(estCodigo);
                estIdentificadores.put(estCodigo, estIdentificador.toUpperCase());

                orgCodigos.add(orgCodigo);
                orgIdentificadores.put(orgCodigo, estIdentificador.toUpperCase() + " - " + orgIdentificador.toUpperCase());
            }

            String diretorioRaiz = ParamSist.getDiretorioRaizArquivos()
                    + File.separator + diretorioArquivos + File.separator;

            List<File> arquivos = new ArrayList<>();

            IOFileFilter filtroTipo = new SuffixFileFilter(new String[]{".txt", ".zip", ".txt.crypt", ".zip.crypt"}, IOCase.INSENSITIVE);
            IOFileFilter filtroCodigoEst = new NameFileFilter(estCodigos.toArray(new String[0]));
            IOFileFilter filtroCodigoOrg = new NameFileFilter(orgCodigos.toArray(new String[0]));
            IOFileFilter filtroProfundidade = new AbstractFileFilter() {
                @Override
                public boolean accept(File dir) {
                    String caminho = dir.getAbsolutePath();
                    String caminhoParcial = caminho.substring(Math.max(caminho.indexOf(File.separator + "cse" + File.separator), caminho.indexOf(File.separator + "est" + File.separator)));
                    return caminhoParcial.split(File.separator).length == 3;
                }
            };

            IOFileFilter filtroSubDiretorioEst = new AndFileFilter(filtroCodigoEst, filtroProfundidade);
            IOFileFilter filtroSubDiretorioOrg = new AndFileFilter(filtroCodigoOrg, filtroProfundidade);

            if (responsavel.isCseSup()) {
                File caminho1 = new File(diretorioRaiz + "cse");
                if (caminho1.exists() || caminho1.mkdirs()) {
                    arquivos.addAll(FileUtils.listFiles(caminho1, filtroTipo, filtroSubDiretorioOrg));
                }

                File caminho2 = new File(diretorioRaiz + "est");
                if (caminho2.exists() || caminho2.mkdirs()) {
                    arquivos.addAll(FileUtils.listFiles(caminho2, filtroTipo, filtroSubDiretorioEst));
                }

            } else if (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
                File caminho = new File(diretorioRaiz + "est" + File.separator + responsavel.getCodigoEntidadePai());
                if (caminho.exists() || caminho.mkdirs()) {
                    arquivos.addAll(FileUtils.listFiles(caminho, filtroTipo, TrueFileFilter.TRUE));
                }

            } else if (responsavel.isOrg()) {
                File caminho = new File(diretorioRaiz + "cse" + File.separator + responsavel.getCodigoEntidade());
                if (caminho.exists() || caminho.mkdirs()) {
                    arquivos.addAll(FileUtils.listFiles(caminho, filtroTipo, TrueFileFilter.TRUE));
                }
            }

            Collections.sort(arquivos, (f1, f2) -> {
                Long d1 = f1.lastModified();
                Long d2 = f2.lastModified();
                return d2.compareTo(d1);
            });

            List<Pair<File, String>> resultado = new ArrayList<>();
            for (File arquivo : arquivos) {
                // cse/nome_arquivo.txt
                // cse/org_codigo/nome_arquivo.txt ou
                // est/est_codigo/nome_arquivo.txt ou
                String[] partesNomeArq = arquivo.getAbsolutePath().substring(diretorioRaiz.length()).split(File.separator);
                if (partesNomeArq.length == 3) {
                    if (partesNomeArq[0].equals("cse")) {
                        resultado.add(Pair.of(arquivo, orgIdentificadores.get(partesNomeArq[1])));
                    } else if (partesNomeArq[0].equals("est")) {
                        resultado.add(Pair.of(arquivo, estIdentificadores.get(partesNomeArq[1])));
                    }
                } else {
                    resultado.add(Pair.of(arquivo, ""));
                }
            }

            return resultado;
        } catch (ConsignanteControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            return null;
        }
    }
}

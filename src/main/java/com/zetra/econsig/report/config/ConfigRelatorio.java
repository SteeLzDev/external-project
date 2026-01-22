package com.zetra.econsig.report.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.helper.cache.ExternalCacheHelper;
import com.zetra.econsig.helper.cache.ExternalMap;
import com.zetra.econsig.service.relatorio.RelatorioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p> Title: ConfigRelatorio</p>
 * <p> Description: Class POJO para informções de configuração dos relatórios.</p>
 * <p> Copyright: Copyright (c) 2006-2023</p>
 * <p> Company: ZetraSoft Ltda. </p>
 * @author Leonel Martins
 */
public class ConfigRelatorio {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ConfigRelatorio.class);

    private final Map<String, Relatorio> cache;

    private static class SingletonHelper {
        private static final ConfigRelatorio instance = new ConfigRelatorio();
    }

    public static ConfigRelatorio getInstance() {
        return SingletonHelper.instance;
    }

    private ConfigRelatorio() {
        if (ExternalCacheHelper.hasExternal()) {
            cache = new ExternalMap<>();
        } else {
            cache = new HashMap<>();
        }
        loadConfig();
    }

    private void loadConfig() {
        try {
            final RelatorioController relatorioController = ApplicationContextProvider.getApplicationContext().getBean(RelatorioController.class);
            final CustomTransferObject filtro = new CustomTransferObject();
            final List<TransferObject> relatorios = relatorioController.lstRelatorio(filtro);
            final Map<String, Relatorio> mapForLoad = ExternalCacheHelper.hasExternal() ? new HashMap<>() : cache;
            for (final TransferObject relatorio : relatorios) {
                final String classeProcesso = (String) relatorio.getAttribute(Columns.REL_CLASSE_PROCESSO);
                final String classeReport = (String) relatorio.getAttribute(Columns.REL_CLASSE_RELATORIO);
                final String funcoes = (String) relatorio.getAttribute(Columns.REL_FUN_CODIGO);
                final String jasperTemplate = (String) relatorio.getAttribute(Columns.REL_TEMPLATE_JASPER);
                final String tipo = (String) relatorio.getAttribute(Columns.REL_CODIGO);
                final String titulo = (String) relatorio.getAttribute(Columns.REL_TITULO);
                final String modeloDinamico = (String) relatorio.getAttribute(Columns.REL_TEMPLATE_DINAMICO);
                final String subreport = (String) relatorio.getAttribute(Columns.REL_TEMPLATE_SUBRELATORIO);
                final String tipoAgendamento = (String) relatorio.getAttribute(Columns.REL_TAG_CODIGO);
                final String templateSql = (String) relatorio.getAttribute(Columns.REL_TEMPLATE_SQL);
                final boolean agendado = relatorio.getAttribute(Columns.REL_AGENDADO).toString().equals(CodedValues.TPC_SIM);
                final String classeAgendamento = (String) relatorio.getAttribute(Columns.REL_CLASSE_AGENDAMENTO);
                final Short ativo = (Short) relatorio.getAttribute(Columns.REL_ATIVO);
                final String customizado = (String) relatorio.getAttribute(Columns.REL_CUSTOMIZADO);
                final String agrupamento = (String) relatorio.getAttribute(Columns.REL_AGRUPAMENTO);

                mapForLoad.put(tipo, new Relatorio(tipo, titulo, funcoes, classeReport, classeProcesso, jasperTemplate, modeloDinamico, subreport, classeAgendamento, tipoAgendamento, templateSql, ativo.equals(CodedValues.STS_ATIVO), agendado, customizado, agrupamento));
            }
            if (ExternalCacheHelper.hasExternal() && cache.isEmpty()) {
                cache.putAll(mapForLoad);
            }

        } catch (final Exception e) {
            // Erro ao tentar configurar. Deixa sem configuração.
            LOG.error("ERRO AO TENTAR CONFIGURAR RELATÓRIOS.", e);
        }
    }

    public Relatorio getRelatorio(String tipo) {
        return cache.get(tipo);
    }

    public Set<String> getTiposDiponveis() {
        return cache.keySet();
    }

    public void reset() {
        cache.clear();
        loadConfig();
    }
}

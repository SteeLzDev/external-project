package com.zetra.econsig.helper.folha;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.servico.NaturezaRelSvc;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ConciliacaoHelper</p>
 * <p>Description: Helper Class para conciliação de arquivo.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ConciliacaoHelper {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ConciliacaoHelper.class);

    /**
     * Mantém um cache de parâmetros de serviço, e de serviço por consignatária.
     * TODO: Tentar usar o codigo e logica em somente um local.
     * @param serCnvRegs
     * @param csaCodigo
     * @param responsavel
     * @throws ViewHelperException
     */
    public static void atualizaCacheParamCnv(Map<String, Object> entradaValida, List<Map<String, Object>> serCnvRegs, String csaCodigo, Map<String, Map<String, Object>> cacheParametrosCnv, AcessoSistema responsavel) throws AutorizacaoControllerException, ViewHelperException {
        ParametroController parametroController = ApplicationContextProvider.getApplicationContext().getBean(ParametroController.class);

        Iterator<Map<String, Object>> it = serCnvRegs.iterator();
        while (it.hasNext()) {
            Map<String, Object> cnvCto = it.next();
            try {
                // Cache que mapeia os parâmetros de serviço aos códigos dos convênios relacionados
                if (!cacheParametrosCnv.containsKey(cnvCto.get(Columns.CNV_CODIGO).toString())) {
                    Map<String, Object> parSvc = new HashMap<>();
                    String svcCodigo = cnvCto.get(Columns.SVC_CODIGO).toString();

                    // Parâmetros de serviços
                    List<TransferObject> parametros = parametroController.selectParamSvcCse(svcCodigo, responsavel);
                    Iterator<TransferObject> itP = parametros.iterator();
                    TransferObject paramCto = null;
                    while (itP.hasNext()) {
                        paramCto = itP.next();
                        parSvc.put(paramCto.getAttribute(Columns.TPS_CODIGO).toString(), paramCto.getAttribute(Columns.PSE_VLR));
                    }

                    // Todos os parametros de serviço que são sobrepostos a nível de CSA e SVC:
                    // ao acrescentar novos parametros, deve-se incluir aqui também
                    List<String> tpsCodigos = new ArrayList<>();
                    tpsCodigos.add(CodedValues.TPS_INDICE);
                    tpsCodigos.add(CodedValues.TPS_PERMITE_REPETIR_INDICE_CSA);
                    tpsCodigos.add(CodedValues.TPS_PERMITE_CONTRATO_SUPER_SER_CSA);
                    tpsCodigos.add(CodedValues.TPS_CARENCIA_MAXIMA);
                    tpsCodigos.add(CodedValues.TPS_CARENCIA_MINIMA);

                    String csaCodigoLinha = null;
                    if (TextHelper.isNull(csaCodigo)) {
                        csaCodigoLinha = (String) entradaValida.get("CSA_CODIGO");
                    }

                    parametros = parametroController.selectParamSvcCsa(svcCodigo, (!TextHelper.isNull(csaCodigo)) ? csaCodigo : csaCodigoLinha, tpsCodigos, false, responsavel);
                    itP = parametros.iterator();
                    while (itP.hasNext()) {
                        paramCto = itP.next();
                        if (paramCto.getAttribute(Columns.TPS_CODIGO).equals(CodedValues.TPS_CARENCIA_MINIMA)) {
                            parSvc.put("CARENCIA_MINIMA", paramCto.getAttribute(Columns.PSC_VLR));
                        } else if (paramCto.getAttribute(Columns.TPS_CODIGO).equals(CodedValues.TPS_CARENCIA_MAXIMA)) {
                            parSvc.put("CARENCIA_MAXIMA", paramCto.getAttribute(Columns.PSC_VLR));
                        } else {
                            parSvc.put(paramCto.getAttribute(Columns.TPS_CODIGO).toString(), paramCto.getAttribute(Columns.PSC_VLR));
                        }
                    }

                    // Busca o serviço de cartão de crédito do qual o serviço depende, caso exista.
                    if (NaturezaRelSvc.getInstance().exists(CodedValues.TNT_CARTAO)) {
                        List<TransferObject> servicosCartaoCredito = parametroController.getRelacionamentoSvc(CodedValues.TNT_CARTAO, null, svcCodigo, responsavel);

                        if (servicosCartaoCredito != null && servicosCartaoCredito.size() > 0) {
                            TransferObject cto = servicosCartaoCredito.get(0);
                            String svcCartao = cto.getAttribute(Columns.RSV_SVC_CODIGO_ORIGEM).toString();
                            parSvc.put("SERVICO_CARTAOCREDITO", svcCartao);
                        }
                    }

                    cacheParametrosCnv.put(cnvCto.get(Columns.CNV_CODIGO).toString(), parSvc);
                }
            } catch (ParametroControllerException e) {
                LOG.error("Erro na busca de parametro: " + e.getMessage());
                throw new ViewHelperException("mensagem.erroInternoSistema", responsavel);
            }
        }
    }

    /**
     * Remove da lista de resultados os registros ligados a serviços sem permissão de importar lote.
     * TODO: Tentar usar o codigo e logica em somente um local.
     * @param serCnvRegisters
     */
    public static void removeSvcSemImportacaoLote(List<Map<String, Object>> serCnvRegisters, Map<String, Map<String, Object>> cacheParametrosCnv) {
        Iterator<Map<String, Object>> it = serCnvRegisters.iterator();
        while (it.hasNext()) {
            Map<String, Object> serCnvReg = it.next();
            Map<String, Object> parametros = cacheParametrosCnv.get(serCnvReg.get(Columns.CNV_CODIGO));
            Object permiteImpLoteVlr = parametros.get(CodedValues.TPS_PERMITE_IMPORTACAO_LOTE);
            boolean permiteImportarLote = (permiteImpLoteVlr != null && permiteImpLoteVlr.equals("1"));
            if (!permiteImportarLote) {
                it.remove();
            }
        }
    }

    /**
     * Define quantas entidades distintas especifidas pelo nomeColuna está ligada à lista de registros
     * TODO: Tentar usar o codigo e logica em somente um local.
     * @param serCnvRegisters - lista de Maps
     * @param nomeColuna
     * @return
     */
    public static int countEntidadesDistintas(List<Map<String, Object>> serCnvRegisters, String nomeColuna) {
        List<String> listaEntidades = new ArrayList<>();
        Iterator<Map<String, Object>> regsIt = serCnvRegisters.iterator();
        int countEnt = 0;

        while (regsIt.hasNext()) {
            Map<String, Object> reg = regsIt.next();
            String entidade = reg.get(nomeColuna).toString();
            if (!listaEntidades.contains(entidade)) {
                listaEntidades.add(entidade);
                countEnt++;
            }
        }
        return countEnt;
    }

    /**
     * Consulta o parâmetro de consignatária TPA_TENTA_INCLUIR_RESERVA_TODOS_SER_LOTE para
     * determinar se, em caso de haver mais de um registro servidor disponível,
     * o sistema deve tentar incluir uma reserva de margem em todos.
     * TODO: Tentar usar o codigo e logica em somente um local.
     * @return
     */
    public static boolean tentarIncluirTodosRegistrosServidores(String csaCodigo, Map<String, Map<String, Boolean>> mapCacheParamCsa) {
        // Busca parâmetro de consignatária sobre utilização de convênio disponível
        boolean tentaIncluirReservaTodosServidores = false;
        Map<String, Boolean> cacheParametrosCsa = mapCacheParamCsa.get(csaCodigo);
        if (cacheParametrosCsa.containsKey(CodedValues.TPA_TENTA_INCLUIR_RESERVA_TODOS_SER_LOTE)) {
            tentaIncluirReservaTodosServidores = cacheParametrosCsa.get(CodedValues.TPA_TENTA_INCLUIR_RESERVA_TODOS_SER_LOTE).booleanValue();
        }
        return tentaIncluirReservaTodosServidores;
    }

    /**
     * Consulta o parâmetro de consignatária TPA_UTILIZA_SERVIDOR_COM_MAIOR_MARGEM_LOTE para
     * determinar se, em caso de haver mais de um registro servidor disponível,
     * o sistema deve utilizar aquele que possui maior margem.
     * TODO: Tentar usar o codigo e logica em somente um local.
     * @return
     */
    public static boolean utilizarRegistroServidorMaiorMargem(String csaCodigo, Map<String, Map<String, Boolean>> mapCacheParamCsa) {
        // Busca parâmetro de consignatária sobre utilização de convênio disponível
        boolean utilizaServidorMaiorMargemDisponivel = false;
        Map<String, Boolean> cacheParametrosCsa = mapCacheParamCsa.get(csaCodigo);
        if (cacheParametrosCsa.containsKey(CodedValues.TPA_UTILIZA_SERVIDOR_COM_MAIOR_MARGEM_LOTE)) {
            utilizaServidorMaiorMargemDisponivel = cacheParametrosCsa.get(CodedValues.TPA_UTILIZA_SERVIDOR_COM_MAIOR_MARGEM_LOTE).booleanValue();
        }
        return utilizaServidorMaiorMargemDisponivel;
    }

    /**
     * Consulta o parâmetro de consignatária TPA_UTILIZA_SERVIDOR_COM_MENOR_MARGEM_LOTE para
     * determinar se, em caso de haver mais de um registro servidor disponível,
     * o sistema deve utilizar aquele que possui menor margem.
     * TODO: Tentar usar o codigo e logica em somente um local.
     * @return
     */
    public static boolean utilizarRegistroServidorMenorMargem(String csaCodigo, Map<String, Map<String, Boolean>> mapCacheParamCsa) {
        // Busca parâmetro de consignatária sobre utilização de convênio disponível
        boolean utilizaServidorMenorMargemDisponivel = false;
        Map<String, Boolean> cacheParametrosCsa = mapCacheParamCsa.get(csaCodigo);
        if (cacheParametrosCsa.containsKey(CodedValues.TPA_UTILIZA_SERVIDOR_COM_MENOR_MARGEM_LOTE)) {
            utilizaServidorMenorMargemDisponivel = cacheParametrosCsa.get(CodedValues.TPA_UTILIZA_SERVIDOR_COM_MENOR_MARGEM_LOTE).booleanValue();
        }
        return utilizaServidorMenorMargemDisponivel;
    }

    /**
     * Consulta o parâmetro de consignatária TPA_PERMITE_INCLUSAO_COM_SER_DUPLICADO_LOTE para
     * determinar se, em caso de haver mais de um servidor disponível,
     * o sistema deve permitir a utilização daquele que possui maior margem ou
     * deve dar erro de duplicidade.
     * TODO: Tentar usar o codigo e logica em somente um local.
     * @return
     */
    public static boolean permiteInclusaoComServidorDuplicado(String csaCodigo, Map<String, Map<String, Boolean>> mapCacheParamCsa) {
        // Busca parâmetro de consignatária sobre possibilidade de incluir contrato mesmo com servidor duplicado
        boolean permiteInclusaoSerDuplicado = false;
        Map<String, Boolean> cacheParametrosCsa = mapCacheParamCsa.get(csaCodigo);
        if (cacheParametrosCsa.containsKey(CodedValues.TPA_PERMITE_INCLUSAO_COM_SER_DUPLICADO_LOTE)) {
            permiteInclusaoSerDuplicado = cacheParametrosCsa.get(CodedValues.TPA_PERMITE_INCLUSAO_COM_SER_DUPLICADO_LOTE).booleanValue();
        }
        return permiteInclusaoSerDuplicado;
    }

    /**
     * Retorna uma implementação de comparator de acordo com a coluna de margem que se deve comparar.
     * TODO: Tentar usar o codigo e logica em somente um local.
     * @param paramMap
     * @return
     */
    public static Comparator<Map<String, Object>> margemComparator(Map<String, Object> paramMap, BigDecimal adeVlr, Integer adePrazo, boolean decrescente) {
        Object tpsIncideMargem = paramMap.get(CodedValues.TPS_INCIDE_MARGEM);
        Short margemIncidente = (tpsIncideMargem != null ? Short.valueOf(tpsIncideMargem.toString()) : CodedValues.INCIDE_MARGEM_SIM);
        final String margemAComparar =
                (margemIncidente.equals(CodedValues.INCIDE_MARGEM_SIM)) ? Columns.RSE_MARGEM_REST :
                    (margemIncidente.equals(CodedValues.INCIDE_MARGEM_SIM_2)) ? Columns.RSE_MARGEM_REST_2 :
                        (margemIncidente.equals(CodedValues.INCIDE_MARGEM_SIM_3)) ? Columns.RSE_MARGEM_REST_3 :
                            Columns.MRS_MARGEM_REST;

        Comparator<Map<String, Object>> margemComp = (to1, to2) -> {
            double margem1 = to1.get(margemAComparar) != null ? Double.parseDouble(to1.get(margemAComparar).toString()) : 0;
            double margem2 = to2.get(margemAComparar) != null ? Double.parseDouble(to2.get(margemAComparar).toString()) : 0;
            double result = decrescente ? margem1 - margem2 : margem2 - margem1;
            Integer rsePrazo = !TextHelper.isNull(to1.get(Columns.RSE_PRAZO)) ? Integer.valueOf(to1.get(Columns.RSE_PRAZO).toString()) : null;
            Integer rsePrazo2 = !TextHelper.isNull(to2.get(Columns.RSE_PRAZO)) ? Integer.valueOf(to2.get(Columns.RSE_PRAZO).toString()) : null;

            if (result > 0 && margem1 >= adeVlr.doubleValue() && (rsePrazo == null || rsePrazo.compareTo(adePrazo) >= 0)) {
                return 1;
            } else if (result < 0 && margem2 >= adeVlr.doubleValue() && (rsePrazo2 == null || rsePrazo2.compareTo(adePrazo) >= 0)) {
                return -1;
            } else if (margem1 >= adeVlr.doubleValue() && (rsePrazo == null || rsePrazo.compareTo(adePrazo) >= 0)) {
                return 1;
            } else if (margem2 >= adeVlr.doubleValue() && (rsePrazo2 == null || rsePrazo2.compareTo(adePrazo) >= 0)) {
                return -1;
            } else if (result > 0) {
                return 1;
            } else if (result < 0) {
                return -1;
            } else {
                return 0;
            }
        };
        return margemComp;
    }

    /**
     * Da lista de servidores retornados para uma inclusão,  retorna aquele que possuem
     * maior margem disponível para a inclusão da nova reserva
     * TODO: Tentar usar o codigo e logica em somente um local.
     * @param serCnvRegisters
     * @return
     */
    public static List<Map<String, Object>> filtraRegistroMargemOrdenada(List<Map<String, Object>> serCnvRegisters, Map<String, Map<String, Object>> cacheParametrosCnv, BigDecimal adeVlr, Integer adePrazo, boolean decrescente) {
        List<Map<String, Object>> serCnvRegistersCandidatos = new ArrayList<>();

        // Pega os parâmetros do convênio do primeiro registro (teoricamente devem ser do mesmo serviço)
        Map<String, Object> paramMap = cacheParametrosCnv.get(serCnvRegisters.get(0).get(Columns.CNV_CODIGO));
        // Seleciona o servidor de maior margem, através do "comparator"
        Comparator<Map<String, Object>> compMargem = margemComparator(paramMap, adeVlr, adePrazo, decrescente);
        Map<String, Object> dadosRegistroMaiorMargem = Collections.max(serCnvRegisters, compMargem);
        String rseCodigoMaiorMargem = (String) dadosRegistroMaiorMargem.get(Columns.RSE_CODIGO);

        // Varre a lista de resultado e obtém todos os registros ligados ao
        // registro servidor de maior margem: pode existir mais de um, por
        // exemplo quando há mais de um serviço.
        Iterator<Map<String, Object>> it = serCnvRegisters.iterator();
        while (it.hasNext()) {
            Map<String, Object> serCnvReg = it.next();
            String rseCodigo = (String) serCnvReg.get(Columns.RSE_CODIGO);
            if (rseCodigo.equals(rseCodigoMaiorMargem)) {
                serCnvRegistersCandidatos.add(serCnvReg);
            }
        }

        return serCnvRegistersCandidatos;
    }

}
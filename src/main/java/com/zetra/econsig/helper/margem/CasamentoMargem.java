package com.zetra.econsig.helper.margem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.exception.MargemControllerException;
import com.zetra.econsig.helper.cache.ExternalCacheHelper;
import com.zetra.econsig.helper.cache.ExternalList;
import com.zetra.econsig.helper.cache.ExternalMap;
import com.zetra.econsig.helper.cache.ExternalSet;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.service.margem.MargemController;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: CasamentoMargem</p>
 * <p>Description: Singleton repositório das configurações de casamento de margem</p>
 * <p>Copyright: Copyright (c) 2012-2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Leonel Martins
 */
public class CasamentoMargem {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(CasamentoMargem.class);

    public static final String DIREITA  = "D";
    public static final String ESQUERDA = "E";
    public static final String LATERAL  = "L";
    public static final String MINIMO   = "M";

    /** Métodos para controle do Singleton **/
    private static class SingletonHelper {
        private static final CasamentoMargem instance = new CasamentoMargem();
    }

    public static CasamentoMargem getInstance() {
        return SingletonHelper.instance;
    }

    /** Definições da classe de configuração **/

    private List<Short> grupos;
    private Map<Short, String> tipoGrupo;
    private Map<Short, List<Short>> casamentos;
    private Set<Short> margens;

    private CasamentoMargem() {
        grupos = null;
        tipoGrupo = null;
        casamentos = null;
        margens = null;
    }

    private synchronized void load() {
        if (casamentos == null) {
            try {
                // Inicializa os objetos locais do cache
                if (ExternalCacheHelper.hasExternal()) {
                    final String prefix = getClass().getSimpleName();
                    grupos = new ExternalList<>(prefix + "-grupos");
                    tipoGrupo = new ExternalMap<>(prefix + "-tipoGrupo");
                    casamentos = new ExternalMap<>(prefix + "-casamentos");
                    margens = new ExternalSet<>(prefix + "-margens");
                } else {
                    grupos = new ArrayList<>();
                    tipoGrupo = new HashMap<>();
                    casamentos = new HashMap<>();
                    margens = new HashSet<>();
                }

                // Busca os registros de configuração do casamento de margem
                final MargemController margemController = ApplicationContextProvider.getApplicationContext().getBean(MargemController.class);
                final List<TransferObject> lstCasamentos = margemController.lstCasamentoMargem(AcessoSistema.getAcessoUsuarioSistema());

                // Caso exista algum casamento, constrói os caches de configuração
                if ((lstCasamentos != null) && !lstCasamentos.isEmpty()) {
                    final Map<Short, String> mapForLoadTipoGrupo = ExternalCacheHelper.hasExternal() ? new HashMap<>() : tipoGrupo;
                    final Map<Short, List<Short>> mapForLoadCasamentos = ExternalCacheHelper.hasExternal() ? new HashMap<>() : casamentos;
                    for (final TransferObject next : lstCasamentos) {
                        final Short camGrupo = (Short) next.getAttribute(Columns.CAM_GRUPO);
                        final Short marCodigo = (Short) next.getAttribute(Columns.CAM_MAR_CODIGO);
                        final String camTipo = (String) next.getAttribute(Columns.CAM_TIPO);

                        if ((mapForLoadTipoGrupo.get(camGrupo) != null) && !mapForLoadTipoGrupo.get(camGrupo).equals(camTipo)) {
                            casamentos = null;
                            throw new RuntimeException(ApplicationResourcesHelper.getMessage("mensagem.erro.casamento.margem.incorreta.somente.um.grupo.permitido", (AcessoSistema) null));
                        } else if (mapForLoadTipoGrupo.get(camGrupo) == null) {
                            grupos.add(camGrupo);
                            mapForLoadTipoGrupo.put(camGrupo, camTipo);
                        }

                        final List<Short> marCodigos = mapForLoadCasamentos.computeIfAbsent(camGrupo, k -> new ArrayList<>());
                        marCodigos.add(marCodigo);
                        margens.add(marCodigo);
                    }
                    if (ExternalCacheHelper.hasExternal() && tipoGrupo.isEmpty()) {
                        tipoGrupo.putAll(mapForLoadTipoGrupo);
                    }
                    if (ExternalCacheHelper.hasExternal() && casamentos.isEmpty()) {
                        casamentos.putAll(mapForLoadCasamentos);
                    }
                }
            } catch (final MargemControllerException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }
    }

    public synchronized void reset() {
        grupos = null;
        tipoGrupo = null;
        casamentos = null;
        margens = null;
    }

    /**
     * Retorna os grupos de casamento de margem.
     * @return
     */
    public List<Short> getGrupos() {
        load();
        List<Short> lstGrupos = new ArrayList<>(grupos.size());

        ///FIXME:DESENV-17859/
        // Quando o cache está habilitado no Redis, está retornando
        // uma lista de Integer ao invés de Short, então o código abaixo converte os
        // tipos da lista, evitando erro de ClassCastException.
        for (Object grupoObj : grupos) {
            if (grupoObj instanceof Short) {
                lstGrupos.add((Short) grupoObj);
            } else if (grupoObj instanceof Integer) {
                lstGrupos.add(((Integer) grupoObj).shortValue());
            } else {
                lstGrupos.add(Short.valueOf(grupoObj.toString()));
            }
        }

        return Collections.unmodifiableList(lstGrupos);
    }

    /**
     * Retorna o tipo de casamento de margem do grupo informado.
     * @param grupo
     * @return
     */
    public String getTipoGrupo(Short grupo) {
        load();
        return tipoGrupo.get(grupo);
    }

    /**
     * Retorna a lista com os códigos de margem que são casadas
     * no grupo informado por parâmetro.
     * @param grupo
     * @return
     */
    public List<Short> getMargensCasadas(Short grupo) {
        load();
        return Collections.unmodifiableList(casamentos.get(grupo));
    }

    /**
     * Retorna o conjunto com os códigos de margem que fazem parte de
     * algum grupo de casamento.
     * @return
     */
    public Set<Short> getMargens() {
        load();
        return Collections.unmodifiableSet(margens);
    }

    /**
     * Retorna o conjunto de códigos das margens que devem ser afetadas
     * por causa da alteração na margem usada de "marCodigosOrigem".
     * @param grupo
     * @param marCodigosOrigem
     * @return
     */
    public Set<Short> getMargensAfetadas(Short grupo, List<Short> marCodigosOrigem) {
        load();

        final String tipo = tipoGrupo.get(grupo);
        final List<Short> marCodigos = casamentos.get(grupo);
        final Set<Short> marCodigosAfetados = new HashSet<>();

        for (final Short marCodigoOrigem : marCodigosOrigem) {
            if (tipo.equals(DIREITA)) {
                // No casamento pela DIREITA a modificação na margem usada de uma margem X
                // só afeta a margem usada Y se a sequência de X for menor que a seq. de Y,
                // ou seja se a margem X vier antes de Y.
                final int posicao = marCodigos.indexOf(marCodigoOrigem);
                if (posicao != -1) {
                    for (int i = 0; i < posicao; i++) {
                        marCodigosAfetados.add(marCodigos.get(i));
                    }
                }
            } else if (tipo.equals(ESQUERDA)) {
                // No casamento pela ESQUERDA a modificação na margem usada de uma margem X
                // afeta a margem usada Y em qualquer sequência, pois é um casamento total.
                final int posicao = marCodigos.indexOf(marCodigoOrigem);
                if (posicao != -1) {
                    // Se a margem origem está presente no casamento pela esquerda,
                    // então retorna o código das demais margens.
                    for (int i = 0; i < marCodigos.size(); i++) {
                        if (i != posicao) {
                            marCodigosAfetados.add(marCodigos.get(i));
                        }
                    }
                }
            } else if (tipo.equals(LATERAL) || tipo.equals(MINIMO)) {
                // No casamento lateral ou mínimo, a margem usada nunca é afetada inderetamente pela
                // alteração na margem usada de outra margem.
            }
        }

        // Remove dos afetados, aqueles que são origem, ou seja já foram afetados
        for (final Short marCodigoOrigem : marCodigosOrigem) {
            marCodigosAfetados.remove(marCodigoOrigem);
        }

        return marCodigosAfetados;
    }

    /**
     * Retorna true caso alteração na margem origem afete a margem destino.
     * @param marCodigoOrigem
     * @param marCodigoDestino
     * @return
     */
    public boolean margemOrigemAfetaDestino(Short marCodigoOrigem, Short marCodigoDestino) {
        load();

        List<Short> lstGrupos = getGrupos();
        if (lstGrupos != null && !lstGrupos.isEmpty()) {
            final List<Short> marCodigos = new ArrayList<>();
            marCodigos.add(marCodigoOrigem);

            for (final Short grupo : lstGrupos) {
                if (getMargensAfetadas(grupo, marCodigos).contains(marCodigoDestino)) {
                    return true;
                }
            }
        }

        return marCodigoOrigem.equals(marCodigoDestino);
    }

    /**
     * Verifica quais margens raiz afetam as margens destinos pelo casamento de margem.
     *
     * @param lstMarCodigoDestino
     * @return
     */
    public List<Short> getMargemOrigemAfetaDestino (Short marCodigoDestino) {
        final List<Short> retorno = new ArrayList<>();

        List<MargemTO> margensRaiz = null;
        try {
            final MargemController margemController = ApplicationContextProvider.getApplicationContext().getBean(MargemController.class);
            margensRaiz = margemController.lstMargemRaiz(AcessoSistema.getAcessoUsuarioSistema());
        } catch (final MargemControllerException ex) {
            margensRaiz = new ArrayList<>();
        }

        // Verifica quais margens raiz afetam a margem destino pelo casamento de margem
        for (final MargemTO margem : margensRaiz) {
            final Short marCodigoOrigem = Short.parseShort(margem.getAttribute(Columns.MAR_CODIGO).toString());

            if (CasamentoMargem.getInstance().margemOrigemAfetaDestino(marCodigoOrigem, marCodigoDestino)) {
                retorno.add(marCodigoOrigem);
            }
        }

        return retorno;
    }

    public boolean temCasamentoDoTipo(String tipo) {
        load();

        List<Short> lstGrupos = getGrupos();
        if (lstGrupos != null && !lstGrupos.isEmpty()) {
            for (final Short grupo : lstGrupos) {
                final String tipoCasamento = getTipoGrupo(grupo);
                if (tipo.equals(tipoCasamento)) {
                    return true;
                }
            }
        }

        return false;
    }
}
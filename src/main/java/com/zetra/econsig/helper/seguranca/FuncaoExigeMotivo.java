package com.zetra.econsig.helper.seguranca;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.zetra.econsig.delegate.TipoMotivoOperacaoDelegate;
import com.zetra.econsig.delegate.UsuarioDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.TipoMotivoOperacaoTransferObject;
import com.zetra.econsig.helper.cache.ExternalCacheHelper;
import com.zetra.econsig.helper.cache.ExternalSet;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: FuncaoExigeMotivo</p>
 * <p>Description: Singleton repositório dos funções que exigem motivo da operação</p>
 * <p>Copyright: Copyright (c) 2009-2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Leonel Martins
 */
public class FuncaoExigeMotivo {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(FuncaoExigeMotivo.class);

    private Set<String> cache;

    private Set<String> cacheMotivosExigemObs;

    private static class SingletonHelper {
        private static final FuncaoExigeMotivo instance = new FuncaoExigeMotivo();
    }

    public static FuncaoExigeMotivo getInstance() {
        return SingletonHelper.instance;
    }

    private FuncaoExigeMotivo() {
        if (ExternalCacheHelper.hasExternal()) {
            final String prefix = getClass().getSimpleName();
            cache = new ExternalSet<>(prefix + "-cache");
            cacheMotivosExigemObs = new ExternalSet<>(prefix + "-cacheMotivosExigemObs");
        } else {
            cache = new HashSet<>();
            cacheMotivosExigemObs = new HashSet<>();
        }
    }

    /**
     * Retorna se a funcao exige motivo da operacao
     * @param chave : o codigo da funcao
     * @param responsavel : usuário que realiza a consulta ao banco
     * @return : true, se a funcao exige motivo da operacao. false, cc
     */
    public boolean exists(String chave, AcessoSistema responsavel) {
        // Recarrega os parâmetros apenas se o cache for vazio.
        // Para recarregar, deve-se entrar na tela de edição de parâmetros
        // do sistema e salvar a nova configuração.
        if (cache.isEmpty()) {
            synchronized (this) {
                if (cache.isEmpty()) {
                    try {
                        // Busca as funcoes que exigem motivo da operacao
                        final UsuarioDelegate usuDelegate = new UsuarioDelegate();
                        final List<TransferObject> lstFuncoesExigemTmo = usuDelegate.lstFuncaoExigeTmo(CodedValues.TPC_SIM, responsavel);
                        for (final TransferObject to : lstFuncoesExigemTmo) {
                            cache.add(to.getAttribute(Columns.FUN_CODIGO).toString());
                        }

                        // Adiciona funções avançadas
                        cache.add(CodedValues.FUN_ALT_AVANCADA_CONSIGNACAO);
                        cache.add(CodedValues.FUN_INCLUSAO_AVANCADA_CONSIGNACAO);
                        cache.add(CodedValues.FUN_DESLIQUIDACAO_AVANCADA_CONTRATO);
                        cache.add(CodedValues.FUN_SUSP_AVANCADA_CONSIGNACAO);
                    } catch (final Exception ex) {
                        LOG.error(ex.getMessage(), ex);
                    }
                }
            }
        }
        return cache.contains(chave);
    }

    public boolean motivosExigeObs(String tmoCodigo, AcessoSistema responsavel) {
        if (cacheMotivosExigemObs.isEmpty()) {
            synchronized (this) {
                if (cacheMotivosExigemObs.isEmpty()) {
                    try {
                        // Busca os motivos que exigem observacao
                        final TipoMotivoOperacaoDelegate tmoDelegate = new TipoMotivoOperacaoDelegate();
                        final List<TipoMotivoOperacaoTransferObject> lstTmoExigeObs = tmoDelegate.findByTmoExigeObsObrigatorio(responsavel);
                        if ((lstTmoExigeObs != null) && (!lstTmoExigeObs.isEmpty())) {
                            for (final TipoMotivoOperacaoTransferObject to : lstTmoExigeObs) {
                                cacheMotivosExigemObs.add(to.getTmoCodigo());
                            }
                        }
                    } catch (final Exception ex) {
                        LOG.error(ex.getMessage(), ex);
                    }
                }
            }
        }
        return cacheMotivosExigemObs.contains(tmoCodigo);
    }

    public void reset() {
        if (ExternalCacheHelper.hasExternal()) {
            if (cache != null) {
                cache.clear();
                cacheMotivosExigemObs.clear();
            }
        } else {
            cache = new HashSet<>();
            cacheMotivosExigemObs = new HashSet<>();
        }
    }
}

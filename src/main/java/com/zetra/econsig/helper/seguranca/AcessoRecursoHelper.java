package com.zetra.econsig.helper.seguranca;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.ParametroDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.cache.ExternalCacheHelper;
import com.zetra.econsig.helper.cache.ExternalMap;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: AcessoRecursoHelper</p>
 * <p>Description: Classe utilitária para gestão das regras de acesso recurso.</p>
 * <p>Copyright: Copyright (c) 2008-2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Leonel Martins
 */
public class AcessoRecursoHelper {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AcessoRecursoHelper.class);

    private final Map<String, AcessoRecurso> recursosMap;

    private final Map<String, String> recursosSup;
    private final Map<String, String> recursosCse;
    private final Map<String, String> recursosOrg;
    private final Map<String, String> recursosSer;
    private final Map<String, String> recursosCsa;
    private final Map<String, String> recursosCor;
    private final Map<String, String> recursosPublicos;
    private final Map<String, Map<String, String>> funcaoSensivelCsa;

    private boolean carregado;

    private static class SingletonHelper {
        private static final AcessoRecursoHelper instance = new AcessoRecursoHelper();
    }

    public static AcessoRecursoHelper getInstance() {
        return SingletonHelper.instance;
    }

    private AcessoRecursoHelper() {
        if (ExternalCacheHelper.hasExternal()) {
            final String prefix = getClass().getSimpleName();
            recursosMap = new ExternalMap<>(prefix + "-recursosMap");
            recursosSup = new ExternalMap<>(prefix + "-recursosSup");
            recursosCse = new ExternalMap<>(prefix + "-recursosCse");
            recursosOrg = new ExternalMap<>(prefix + "-recursosOrg");
            recursosSer = new ExternalMap<>(prefix + "-recursosSer");
            recursosCsa = new ExternalMap<>(prefix + "-recursosCsa");
            recursosCor = new ExternalMap<>(prefix + "-recursosCor");
            recursosPublicos = new ExternalMap<>(prefix + "-recursosPublicos");
            funcaoSensivelCsa = new ExternalMap<>(prefix + "-funcaoSensivelCsa");
        } else {
            recursosMap = new HashMap<>();
            recursosSup = new HashMap<>();
            recursosCse = new HashMap<>();
            recursosOrg = new HashMap<>();
            recursosSer = new HashMap<>();
            recursosCsa = new HashMap<>();
            recursosCor = new HashMap<>();
            recursosPublicos = new HashMap<>();
            funcaoSensivelCsa = new HashMap<>();
        }
    }

    private void carregar() {
        try {
            if (!carregado) {
                synchronized (this) {
                    if (!carregado) {
                        // Limpa os caches, necessário caso haja alguma regra com erro, evitando que fique duplicando os registros
                        // nos caches de recursos, mascarando eventuais erros
                        reset();

                        final AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();
                        final UsuarioController usuarioController = ApplicationContextProvider.getApplicationContext().getBean(UsuarioController.class);

                        // Recupera relacionamento entre natureza de consignatária e função, para criar
                        // cache de funções permitidas por natureza
                        final Map<String, List<String>> cacheFuncoesRestritaNca = new HashMap<>();
                        final List<TransferObject> funcoesPermitidasNca = usuarioController.selectFuncoesPermitidasNca(responsavel);
                        if ((funcoesPermitidasNca != null) && !funcoesPermitidasNca.isEmpty()) {
                            for (int i = 0; i < funcoesPermitidasNca.size(); i++) {
                                final TransferObject funTO = funcoesPermitidasNca.get(i);
                                final String funCodigo = (String) funTO.getAttribute(Columns.FPN_FUN_CODIGO);
                                final String ncaCodigo = (String) funTO.getAttribute(Columns.FPN_NCA_CODIGO);

                                List<String> naturezas = cacheFuncoesRestritaNca.get(funCodigo);
                                if (naturezas == null) {
                                    naturezas = new ArrayList<>();
                                    cacheFuncoesRestritaNca.put(funCodigo, naturezas);
                                }
                                naturezas.add(ncaCodigo);
                            }
                        }

                        // Carrega cache de funções sensíveis por CSA
                        final List<TransferObject> lstFuncoesSensiveisCsa = usuarioController.selectFuncoesSensiveisCsa(responsavel);
                        if ((lstFuncoesSensiveisCsa != null) && !lstFuncoesSensiveisCsa.isEmpty()) {
                            final Map<String, Map<String, String>> mapForLoad = ExternalCacheHelper.hasExternal() ? new HashMap<>() : funcaoSensivelCsa;
                            for (final TransferObject funcaoSensivel : lstFuncoesSensiveisCsa) {
                                final String funCodigo = funcaoSensivel.getAttribute(Columns.FSC_FUN_CODIGO).toString();
                                final String csaCodigo = funcaoSensivel.getAttribute(Columns.FSC_CSA_CODIGO).toString();
                                final String fscValor = funcaoSensivel.getAttribute(Columns.FSC_VALOR).toString();
                                final Map<String, String> consignatarias = mapForLoad.computeIfAbsent(funCodigo, k -> new HashMap<>());
                                consignatarias.put(csaCodigo, fscValor);
                            }
                            if (ExternalCacheHelper.hasExternal() && funcaoSensivelCsa.isEmpty()) {
                                funcaoSensivelCsa.putAll(mapForLoad);
                            }
                        }

                        final ParametroDelegate paramDelegate = new ParametroDelegate();
                        final List<TransferObject> recursos = paramDelegate.lstFuncoesAcessoRecurso(responsavel);
                        final Map<String, AcessoRecurso> mapForLoadRecursosMap = ExternalCacheHelper.hasExternal() ? new HashMap<>() : recursosMap;
                        final Map<String, String> mapForLoadRecursosPublicos = ExternalCacheHelper.hasExternal() ? new HashMap<>() : recursosPublicos;
                        final Map<String, String> mapForLoadRecursosSup = ExternalCacheHelper.hasExternal() ? new HashMap<>() : recursosSup;
                        final Map<String, String> mapForLoadRecursosCse = ExternalCacheHelper.hasExternal() ? new HashMap<>() : recursosCse;
                        final Map<String, String> mapForLoadRecursosOrg = ExternalCacheHelper.hasExternal() ? new HashMap<>() : recursosOrg;
                        final Map<String, String> mapForLoadRecursosSer = ExternalCacheHelper.hasExternal() ? new HashMap<>() : recursosSer;
                        final Map<String, String> mapForLoadRecursosCsa = ExternalCacheHelper.hasExternal() ? new HashMap<>() : recursosCsa;
                        final Map<String, String> mapForLoadRecursosCor = ExternalCacheHelper.hasExternal() ? new HashMap<>() : recursosCor;
                        for (final TransferObject recurso : recursos) {
                            final String acrCodigo = (String) recurso.getAttribute(Columns.ACR_CODIGO);
                            final String papCodigo = (String) recurso.getAttribute(Columns.ACR_PAP_CODIGO);
                            final String itmCodigo = (String) recurso.getAttribute(Columns.ACR_ITM_CODIGO);

                            final String acrRecurso = (String) recurso.getAttribute(Columns.ACR_RECURSO);
                            final String acrParametro = (String) recurso.getAttribute(Columns.ACR_PARAMETRO);
                            final String acrOperacao = (String) recurso.getAttribute(Columns.ACR_OPERACAO);
                            final boolean acrAtivo = ((recurso.getAttribute(Columns.ACR_ATIVO) == null) || ((Short) recurso.getAttribute(Columns.ACR_ATIVO)).equals(CodedValues.STS_ATIVO));
                            final boolean acrSessao = ((recurso.getAttribute(Columns.ACR_SESSAO) == null) || recurso.getAttribute(Columns.ACR_SESSAO).equals("S"));
                            final boolean acrBloqueio = ((recurso.getAttribute(Columns.ACR_BLOQUEIO) == null) || recurso.getAttribute(Columns.ACR_BLOQUEIO).equals("S"));
                            final boolean acrFimFluxo = ((recurso.getAttribute(Columns.ACR_FIM_FLUXO) != null) && recurso.getAttribute(Columns.ACR_FIM_FLUXO).equals("S"));
                            final boolean possuiAjuda = ((recurso.getAttribute("possui_ajuda") == null) || (((Integer) recurso.getAttribute("possui_ajuda")).intValue() == 1));
                            final String acrMetodoHttp = (String) recurso.getAttribute(Columns.ACR_METODO_HTTP);

                            final String funCodigo = (String) recurso.getAttribute(Columns.ACR_FUN_CODIGO);
                            final String funDescricao = (String) recurso.getAttribute(Columns.FUN_DESCRICAO);
                            final boolean funPermiteBloqueio = ((recurso.getAttribute(Columns.FUN_PERMITE_BLOQUEIO) != null) && recurso.getAttribute(Columns.FUN_PERMITE_BLOQUEIO).equals("S"));
                            final boolean funExigeTmo = ((recurso.getAttribute(Columns.FUN_EXIGE_TMO) != null) && recurso.getAttribute(Columns.FUN_EXIGE_TMO).equals("S"));
                            final boolean funRestritaNca = ((recurso.getAttribute(Columns.FUN_RESTRITA_NCA) != null) && recurso.getAttribute(Columns.FUN_RESTRITA_NCA).equals("S"));
                            final String funExigeSegundaSenhaSup = (String) recurso.getAttribute(Columns.FUN_EXIGE_SEGUNDA_SENHA_SUP);
                            final String funExigeSegundaSenhaCse = (String) recurso.getAttribute(Columns.FUN_EXIGE_SEGUNDA_SENHA_CSE);
                            final String funExigeSegundaSenhaOrg = (String) recurso.getAttribute(Columns.FUN_EXIGE_SEGUNDA_SENHA_ORG);
                            final String funExigeSegundaSenhaCsa = (String) recurso.getAttribute(Columns.FUN_EXIGE_SEGUNDA_SENHA_CSA);
                            final String funExigeSegundaSenhaCor = (String) recurso.getAttribute(Columns.FUN_EXIGE_SEGUNDA_SENHA_COR);
                            final String funExigeSegundaSenhaSer = (String) recurso.getAttribute(Columns.FUN_EXIGE_SEGUNDA_SENHA_SER);

                            // Parâmetro e operação devem estar preenchidos ou ambos devem ser NULL
                            if ((TextHelper.isNull(acrParametro) && !TextHelper.isNull(acrOperacao)) || (!TextHelper.isNull(acrParametro) && TextHelper.isNull(acrOperacao))) {
                                throw new ZetraException("mensagem.erro.acesso.recurso.invalido.parametro.operacao", responsavel, acrCodigo);
                            }

                            final AcessoRecurso acessoRecurso = new AcessoRecurso(acrCodigo, acrRecurso, acrParametro, acrOperacao, acrAtivo, acrSessao, acrBloqueio, acrFimFluxo, acrMetodoHttp,
                                                                                  itmCodigo, papCodigo, funCodigo, funDescricao, funPermiteBloqueio, funExigeTmo, funRestritaNca,
                                                                                  funExigeSegundaSenhaSup, funExigeSegundaSenhaCse, funExigeSegundaSenhaOrg, funExigeSegundaSenhaCsa,
                                                                                  funExigeSegundaSenhaCor, funExigeSegundaSenhaSer, possuiAjuda, cacheFuncoesRestritaNca.get(funCodigo));
                            final String chaveRecurso = acrRecurso + (!TextHelper.isNull(acrParametro) ? "?" + acrParametro + "=" + acrOperacao : "");

                            if (!acrSessao) {
                                adicionarRecurso(mapForLoadRecursosMap, mapForLoadRecursosPublicos, chaveRecurso, acessoRecurso);
                            } else if (TextHelper.isNull(papCodigo)) {
                                // Se o recurso está para todos os papéis, adiciona em todos os mapas
                                adicionarRecurso(mapForLoadRecursosMap, mapForLoadRecursosSup, chaveRecurso, acessoRecurso);
                                adicionarRecurso(mapForLoadRecursosMap, mapForLoadRecursosCse, chaveRecurso, acessoRecurso);
                                adicionarRecurso(mapForLoadRecursosMap, mapForLoadRecursosOrg, chaveRecurso, acessoRecurso);
                                adicionarRecurso(mapForLoadRecursosMap, mapForLoadRecursosSer, chaveRecurso, acessoRecurso);
                                adicionarRecurso(mapForLoadRecursosMap, mapForLoadRecursosCsa, chaveRecurso, acessoRecurso);
                                adicionarRecurso(mapForLoadRecursosMap, mapForLoadRecursosCor, chaveRecurso, acessoRecurso);
                            } else if (papCodigo.equals(CodedValues.PAP_SUPORTE)) {
                                adicionarRecurso(mapForLoadRecursosMap, mapForLoadRecursosSup, chaveRecurso, acessoRecurso);
                            } else if (papCodigo.equals(CodedValues.PAP_CONSIGNANTE)) {
                                adicionarRecurso(mapForLoadRecursosMap, mapForLoadRecursosCse, chaveRecurso, acessoRecurso);
                            } else if (papCodigo.equals(CodedValues.PAP_ORGAO)) {
                                adicionarRecurso(mapForLoadRecursosMap, mapForLoadRecursosOrg, chaveRecurso, acessoRecurso);
                            } else if (papCodigo.equals(CodedValues.PAP_SERVIDOR)) {
                                adicionarRecurso(mapForLoadRecursosMap, mapForLoadRecursosSer, chaveRecurso, acessoRecurso);
                            } else if (papCodigo.equals(CodedValues.PAP_CONSIGNATARIA)) {
                                adicionarRecurso(mapForLoadRecursosMap, mapForLoadRecursosCsa, chaveRecurso, acessoRecurso);
                            } else if (papCodigo.equals(CodedValues.PAP_CORRESPONDENTE)) {
                                adicionarRecurso(mapForLoadRecursosMap, mapForLoadRecursosCor, chaveRecurso, acessoRecurso);
                            } else {
                                throw new ZetraException("mensagem.erro.acesso.recurso.invalido.papel.invalido", responsavel, acrCodigo);
                            }
                        }
                        if (ExternalCacheHelper.hasExternal() && recursosMap.isEmpty()) {
                            recursosMap.putAll(mapForLoadRecursosMap);
                        }
                        if (ExternalCacheHelper.hasExternal() && recursosPublicos.isEmpty()) {
                            recursosPublicos.putAll(mapForLoadRecursosPublicos);
                        }
                        if (ExternalCacheHelper.hasExternal() && recursosSup.isEmpty()) {
                            recursosSup.putAll(mapForLoadRecursosSup);
                        }
                        if (ExternalCacheHelper.hasExternal() && recursosCse.isEmpty()) {
                            recursosCse.putAll(mapForLoadRecursosCse);
                        }
                        if (ExternalCacheHelper.hasExternal() && recursosOrg.isEmpty()) {
                            recursosOrg.putAll(mapForLoadRecursosOrg);
                        }
                        if (ExternalCacheHelper.hasExternal() && recursosSer.isEmpty()) {
                            recursosSer.putAll(mapForLoadRecursosSer);
                        }
                        if (ExternalCacheHelper.hasExternal() && recursosCsa.isEmpty()) {
                            recursosCsa.putAll(mapForLoadRecursosCsa);
                        }
                        if (ExternalCacheHelper.hasExternal() && recursosCor.isEmpty()) {
                            recursosCor.putAll(mapForLoadRecursosCor);
                        }

                        carregado = true;
                    }
                }
            }
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    private static void adicionarRecurso(Map<String, AcessoRecurso> recursosMap, Map<String, String> recursos, String chaveRecurso, AcessoRecurso acessoRecurso) throws ZetraException {
        if (recursos.containsKey(chaveRecurso)) {
            throw new ZetraException("mensagem.erro.acesso.recurso.duplicado", AcessoSistema.getAcessoUsuarioSistema(), acessoRecurso.acrCodigo, recursosMap.get(recursos.get(chaveRecurso)).acrCodigo);
        }
        recursos.put(chaveRecurso, acessoRecurso.getAcrCodigo());
        recursosMap.put(acessoRecurso.getAcrCodigo(), acessoRecurso);
    }

    private static String getValorFuncaoSensivelCsa(String funCodigo, AcessoSistema responsavel) {
        if (responsavel.isCsaCor()) {
            final Map<String, String> consignatarias = SingletonHelper.instance.funcaoSensivelCsa.get(funCodigo);
            if ((consignatarias != null) && !TextHelper.isNull(consignatarias.get(responsavel.getCsaCodigo()))) {
                return consignatarias.get(responsavel.getCsaCodigo());
            }
        }
        return "N";
    }

    public static AcessoRecurso identificarAcessoRecurso(String recurso, Map<String, String[]> parametros, AcessoSistema responsavel) {
        SingletonHelper.instance.carregar();

        // Procura primeiro nos acessos recursos públicos, pois mesmo usuários autenticados podem acessá-los
        String acrCodigoAcessado = identificarRecurso(recurso, parametros, SingletonHelper.instance.recursosPublicos, responsavel);

        // Se não foi identificado, então procura de acordo com o papel do usuário
        if (acrCodigoAcessado == null) {
            if (responsavel != null && responsavel.isSessaoValida()) {
                if (responsavel.isSup()) {
                    acrCodigoAcessado = identificarRecurso(recurso, parametros, SingletonHelper.instance.recursosSup, responsavel);
                } else if (responsavel.isCse()) {
                    acrCodigoAcessado = identificarRecurso(recurso, parametros, SingletonHelper.instance.recursosCse, responsavel);
                } else if (responsavel.isOrg()) {
                    acrCodigoAcessado = identificarRecurso(recurso, parametros, SingletonHelper.instance.recursosOrg, responsavel);
                } else if (responsavel.isSer()) {
                    acrCodigoAcessado = identificarRecurso(recurso, parametros, SingletonHelper.instance.recursosSer, responsavel);
                } else if (responsavel.isCsa()) {
                    acrCodigoAcessado = identificarRecurso(recurso, parametros, SingletonHelper.instance.recursosCsa, responsavel);
                } else if (responsavel.isCor()) {
                    acrCodigoAcessado = identificarRecurso(recurso, parametros, SingletonHelper.instance.recursosCor, responsavel);
                }
            } else {
                // Se não tem sessão válida, pesquisa em todos os recursos e irá redirecionar para sessão expirada
                if (acrCodigoAcessado == null) {
                    acrCodigoAcessado = identificarRecurso(recurso, parametros, SingletonHelper.instance.recursosSup, responsavel);
                }

                if (acrCodigoAcessado == null) {
                    acrCodigoAcessado = identificarRecurso(recurso, parametros, SingletonHelper.instance.recursosCse, responsavel);
                }

                if (acrCodigoAcessado == null) {
                    acrCodigoAcessado = identificarRecurso(recurso, parametros, SingletonHelper.instance.recursosOrg, responsavel);
                }

                if (acrCodigoAcessado == null) {
                    acrCodigoAcessado = identificarRecurso(recurso, parametros, SingletonHelper.instance.recursosSer, responsavel);
                }

                if (acrCodigoAcessado == null) {
                    acrCodigoAcessado = identificarRecurso(recurso, parametros, SingletonHelper.instance.recursosCsa, responsavel);
                }

                if (acrCodigoAcessado == null) {
                    acrCodigoAcessado = identificarRecurso(recurso, parametros, SingletonHelper.instance.recursosCor, responsavel);
                }
            }
        }

        AcessoRecurso recursoAcessado = null;

        if (acrCodigoAcessado != null) {
            recursoAcessado = SingletonHelper.instance.recursosMap.get(acrCodigoAcessado);
        } else if (parametros != null) {
            // Se não foi identificado algum acesso recurso, pesquisa sem os parâmetros
            recursoAcessado = identificarAcessoRecurso(recurso, null, responsavel);
        }

        if (responsavel != null) {
            responsavel.setRecursoAcessado(recursoAcessado);
        }

        return recursoAcessado;
    }

    private static String identificarRecurso(String recurso, Map<String, String[]> parametros, Map<String, String> recursos, AcessoSistema responsavel) {
        String acrCodigoAcessado = null;

        // A variável recursos terá um mapa indicando o recurso que pode ser acessado pelo usuário
        // Obtém o recurso acessado, e testa os parâmetros para ver qual está sendo acessado
        if (parametros != null && !parametros.isEmpty()) {
            outerLoop:
            for (final Map.Entry<String, String[]> parametrosEntry : parametros.entrySet()) {
                if (parametrosEntry.getValue() != null) {
                    final String paramName = parametrosEntry.getKey();
                    for (final String paramValue : parametrosEntry.getValue()) {
                        final String chaveRecurso = recurso + "?" + paramName + "=" + paramValue;
                        acrCodigoAcessado = recursos.get(chaveRecurso);
                        if (acrCodigoAcessado != null) {
                            break outerLoop;
                        }
                    }
                }
            }
        } else {
            // Se não tem parâmetros, pesquisa somente pelo recurso acessado
            acrCodigoAcessado = recursos.get(recurso);
        }

        return acrCodigoAcessado;
    }

    /**
     * recarrega o cache de acessos a recursos
     *
     */
    public static void reset() {
        SingletonHelper.instance.carregado = false;
        SingletonHelper.instance.recursosMap.clear();
        SingletonHelper.instance.recursosSup.clear();
        SingletonHelper.instance.recursosCse.clear();
        SingletonHelper.instance.recursosOrg.clear();
        SingletonHelper.instance.recursosSer.clear();
        SingletonHelper.instance.recursosCsa.clear();
        SingletonHelper.instance.recursosCor.clear();
        SingletonHelper.instance.recursosPublicos.clear();
        SingletonHelper.instance.funcaoSensivelCsa.clear();
    }

    public static class AcessoRecurso implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 9L;

        private final String acrCodigo;
        private final String acrRecurso;
        private final String acrParametro;
        private final String acrOperacao;
        private final boolean acrAtivo;
        private final boolean acrSessao;
        private final boolean acrBloqueio;
        private final boolean acrFimFluxo;
        private final String acrMetodoHttp;
        private final String itmCodigo;
        private final String papCodigo;
        private final String funCodigo;
        private final String funDescricao;
        private final boolean funPermiteBloqueio;
        private final boolean funExigeTmo;
        private final boolean funRestritaNca;
        private final List<String> restricaoNca;
        private final String funExigeSegundaSenhaSup;
        private final String funExigeSegundaSenhaCse;
        private final String funExigeSegundaSenhaOrg;
        private final String funExigeSegundaSenhaCsa;
        private final String funExigeSegundaSenhaCor;
        private final String funExigeSegundaSenhaSer;
        private final boolean possuiAjuda;

        /**
         *
         */
        // DESENV-17859 : necessário para poder fazer o deserialize do redis
        public AcessoRecurso() {
            acrCodigo = "";
            acrRecurso = "";
            acrParametro = "";
            acrOperacao = "";
            acrAtivo = false;
            acrSessao = false;
            acrBloqueio = false;
            acrFimFluxo = false;
            acrMetodoHttp = "";
            itmCodigo = "";
            papCodigo = "";
            funCodigo = "";
            funDescricao = "";
            funPermiteBloqueio = false;
            funExigeTmo = false;
            funRestritaNca = false;
            restricaoNca = null;
            funExigeSegundaSenhaSup = "";
            funExigeSegundaSenhaCse = "";
            funExigeSegundaSenhaOrg = "";
            funExigeSegundaSenhaCsa = "";
            funExigeSegundaSenhaCor = "";
            funExigeSegundaSenhaSer = "";
            possuiAjuda = false;
        }

        public AcessoRecurso(String acrCodigo, String acrRecurso, String acrParametro, String acrOperacao, boolean acrAtivo, boolean acrSessao, boolean acrBloqueio, boolean acrFimFluxo, String acrMetodoHttp, String itmCodigo, String papCodigo, String funCodigo, String funDescricao, boolean funPermiteBloqueio, boolean funExigeTmo, boolean funRestritaNca,
                String funExigeSegundaSenhaSup, String funExigeSegundaSenhaCse, String funExigeSegundaSenhaOrg, String funExigeSegundaSenhaCsa, String funExigeSegundaSenhaCor, String funExigeSegundaSenhaSer, boolean possuiAjuda, List<String> restricaoNca) {
            super();
            this.acrCodigo = acrCodigo;
            this.acrRecurso = acrRecurso;
            this.acrParametro = acrParametro;
            this.acrOperacao = acrOperacao;
            this.acrAtivo = acrAtivo;
            this.acrSessao = acrSessao;
            this.acrBloqueio = acrBloqueio;
            this.acrFimFluxo = acrFimFluxo;
            this.acrMetodoHttp = acrMetodoHttp;
            this.itmCodigo = itmCodigo;
            this.papCodigo = papCodigo;
            this.funCodigo = funCodigo;
            this.funDescricao = funDescricao;
            this.funPermiteBloqueio = funPermiteBloqueio;
            this.funExigeTmo = funExigeTmo;
            this.funRestritaNca = funRestritaNca;
            this.funExigeSegundaSenhaSup = funExigeSegundaSenhaSup;
            this.funExigeSegundaSenhaCse = funExigeSegundaSenhaCse;
            this.funExigeSegundaSenhaOrg = funExigeSegundaSenhaOrg;
            this.funExigeSegundaSenhaCsa = funExigeSegundaSenhaCsa;
            this.funExigeSegundaSenhaCor = funExigeSegundaSenhaCor;
            this.funExigeSegundaSenhaSer = funExigeSegundaSenhaSer;
            this.possuiAjuda = possuiAjuda;

            if (funRestritaNca) {
                if (restricaoNca != null) {
                    this.restricaoNca = restricaoNca;
                } else {
                    this.restricaoNca = new ArrayList<>();
                }
            } else {
                this.restricaoNca = null;
            }
        }

        public String getAcrCodigo() {
            return acrCodigo;
        }

        public String getAcrRecurso() {
            return acrRecurso;
        }

        public String getAcrParametro() {
            return acrParametro;
        }

        public String getAcrOperacao() {
            return acrOperacao;
        }

        public boolean isAcrAtivo() {
            return acrAtivo;
        }

        public boolean isAcrSessao() {
            return acrSessao;
        }

        public boolean isAcrBloqueio() {
            return acrBloqueio;
        }

        public boolean isAcrFimFluxo() {
            return acrFimFluxo;
        }

        public String getAcrMetodoHttp() {
            return acrMetodoHttp;
        }

        public String getItmCodigo() {
            return itmCodigo;
        }

        public String getPapCodigo() {
            return papCodigo;
        }

        public String getFunCodigo() {
            return funCodigo;
        }

        public String getFunDescricao() {
            return funDescricao;
        }

        public boolean isFunPermiteBloqueio() {
            return funPermiteBloqueio;
        }

        public boolean isFunExigeTmo() {
            return funExigeTmo;
        }

        public boolean isFunRestritaNca() {
            return funRestritaNca;
        }

        public boolean isPossuiAjuda() {
            return possuiAjuda;
        }

        public boolean permiteAcessoNca(String ncaCodigo) {
            if (funRestritaNca) {
                return ((restricaoNca != null) && restricaoNca.contains(ncaCodigo));
            }
            return true;
        }

        public boolean funcaoExigeSegundaSenha(AcessoSistema responsavel) {
            if (responsavel.isSup()) {
                return "S".equals(funExigeSegundaSenhaSup);
            } else if (responsavel.isCse()) {
                return "S".equals(funExigeSegundaSenhaCse);
            } else if (responsavel.isOrg()) {
                return "S".equals(funExigeSegundaSenhaOrg);
            } else if (responsavel.isCsa()) {
                return "S".equals(funExigeSegundaSenhaCsa) || "S".equals(getValorFuncaoSensivelCsa(funCodigo, responsavel));
            } else if (responsavel.isCor()) {
                return "S".equals(funExigeSegundaSenhaCor) || "S".equals(getValorFuncaoSensivelCsa(funCodigo, responsavel));
            } else {
                return false;
            }
        }

        public boolean funcaoExigePropriaSenha(AcessoSistema responsavel) {
            if (responsavel.isSup()) {
                return "P".equals(funExigeSegundaSenhaSup);
            } else if (responsavel.isCse()) {
                return "P".equals(funExigeSegundaSenhaCse);
            } else if (responsavel.isOrg()) {
                return "P".equals(funExigeSegundaSenhaOrg);
            } else if (responsavel.isCsa()) {
                return "P".equals(funExigeSegundaSenhaCsa) || "P".equals(getValorFuncaoSensivelCsa(funCodigo, responsavel));
            } else if (responsavel.isCor()) {
                return "P".equals(funExigeSegundaSenhaCor) || "P".equals(getValorFuncaoSensivelCsa(funCodigo, responsavel));
            } else if (responsavel.isSer()) {
                return "P".equals(funExigeSegundaSenhaSer);
            } else {
                return false;
            }
        }

        public boolean adicionarFuncaoFilaAutorizacao(AcessoSistema responsavel) {
            if (responsavel.temPermissao(CodedValues.FUN_CONFIRMAR_OP_FILA_AUTORIZACAO)) {
                return false;
            } else if (responsavel.isSup()) {
                return "F".equals(funExigeSegundaSenhaSup);
            } else if (responsavel.isCse()) {
                return "F".equals(funExigeSegundaSenhaCse);
            } else if (responsavel.isOrg()) {
                return "F".equals(funExigeSegundaSenhaOrg);
            } else if (responsavel.isCsa()) {
                return "F".equals(funExigeSegundaSenhaCsa) || "F".equals(getValorFuncaoSensivelCsa(funCodigo, responsavel));
            } else if (responsavel.isCor()) {
                return "F".equals(funExigeSegundaSenhaCor) || "F".equals(getValorFuncaoSensivelCsa(funCodigo, responsavel));
            } else {
                return false;
            }
        }

        public boolean exigeSenhaOpeSensiveis(AcessoSistema responsavel) {
            if (TextHelper.isNull(funCodigo)) {
                // Se não tem função definida, então não tem como exigir senha para a operação
                return false;
            }
            try {
                boolean exigeSenhaOpeSensiveis = false;
                if (funcaoExigeSegundaSenha(responsavel) || funcaoExigePropriaSenha(responsavel) || adicionarFuncaoFilaAutorizacao(responsavel)) {
                    // Se a função está configurada para exigir uma segunda senha ou a própria senha, verifica o parâmetro de sistema
                    if (responsavel.isCseOrg()) {
                        exigeSenhaOpeSensiveis = ParamSist.paramEquals(CodedValues.TPC_EXIGE_SENHA_OPERACOES_SENSIVEIS_CSE_ORG, CodedValues.TPC_SIM, responsavel);
                    } else if (responsavel.isCsaCor()) {
                        // Verifica se o parâmetro de consignatária TPA_EXIGE_SEGUNDA_SENHA deixa que a CSA solicite segunda senha
                        final ParametroDelegate paramDelegate = new ParametroDelegate();
                        final String tpaExigeSegundaSenha = paramDelegate.getParamCsa(responsavel.getCsaCodigo(), CodedValues.TPA_EXIGE_SEGUNDA_SENHA, responsavel);
                        final boolean exigeSegundaSenha = TextHelper.isNull(tpaExigeSegundaSenha) || tpaExigeSegundaSenha.equals(CodedValues.TPA_SIM);
                        exigeSenhaOpeSensiveis = exigeSegundaSenha && ParamSist.paramEquals(CodedValues.TPC_EXIGE_SENHA_OPERACOES_SENSIVEIS_CSA_COR, CodedValues.TPC_SIM, responsavel);
                    } else if (responsavel.isSup()) {
                        exigeSenhaOpeSensiveis = ParamSist.paramEquals(CodedValues.TPC_EXIGE_SENHA_OPERACOES_SENSIVEIS_SUP, CodedValues.TPC_SIM, responsavel);
                    } else if (responsavel.isSer()) {
                        exigeSenhaOpeSensiveis = ParamSist.paramEquals(CodedValues.TPC_EXIGE_SENHA_OPERACOES_SENSIVEIS_SER, CodedValues.TPC_SIM, responsavel);
                    }
                }
                return exigeSenhaOpeSensiveis;
            } catch (final ParametroControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                return false;
            }
        }
    }
}
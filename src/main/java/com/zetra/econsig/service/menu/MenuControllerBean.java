package com.zetra.econsig.service.menu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ItemMenuTO;
import com.zetra.econsig.dto.entidade.MenuTO;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.MenuControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.consignacao.SolicitacaoServidorHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.usuario.UsuarioHelper;
import com.zetra.econsig.persistence.entity.AbstractEntityHome;
import com.zetra.econsig.persistence.entity.ItemMenu;
import com.zetra.econsig.persistence.entity.ItemMenuFavorito;
import com.zetra.econsig.persistence.entity.ItemMenuFavoritoHome;
import com.zetra.econsig.persistence.entity.ItemMenuHome;
import com.zetra.econsig.persistence.entity.Menu;
import com.zetra.econsig.persistence.entity.MenuHome;
import com.zetra.econsig.persistence.query.menu.ListaItemMenuQuery;
import com.zetra.econsig.persistence.query.menu.ListaMenuQuery;
import com.zetra.econsig.persistence.query.menu.ObterMenuQuery;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.ItemMenuEnum;

/**
 * <p>Title: MenuControllerBean</p>
 * <p>Description: Fachada dos métodos de negócio de Menu.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class MenuControllerBean implements MenuController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(MenuControllerBean.class);

    private static final Integer FUNCAO_PROIBIDA = Integer.valueOf("0");

    @Autowired
    ParametroController parametrocontroller;

    @Override
    public List<MenuTO> obterMenu(AcessoSistema responsavel) throws MenuControllerException {
        final Map<Integer, MenuTO> controle = new HashMap<>();
        final List<MenuTO> menu = new ArrayList<>();

        final List<String> itemMenuComImpedimento = lstItemMenuComImpedimento(responsavel);
        final Map<String, String> mapFuncaoPrioridade = getMapFuncaoPrioridade();
        String funCodigoAnterior = "";

        Menu menuFavorito = null;
        MenuTO menuFavoritoTO;
        try {
            //Busca no banco de dados(tb_menu) pelo menu Favoritos.
            menuFavorito = MenuHome.findByPrimaryKey("0");
        } catch (final FindException e) {
            LOG.error(e.getMessage(), e);
        }
        if (menuFavorito != null) {
            menuFavoritoTO = new MenuTO(Integer.parseInt(menuFavorito.getMnuCodigo()));
            menuFavoritoTO.setMnuDescricao(menuFavorito.getMnuDescricao());
            menuFavoritoTO.setMnuImagem(menuFavorito.getMnuImagem());
        } else {
            menuFavoritoTO = new MenuTO();
            menuFavoritoTO.setMnuDescricao(ApplicationResourcesHelper.getMessage("rotulo.menu.favoritos", responsavel));
        }

        final List<TransferObject> lstItemMenu = getMenu(responsavel.getUsuCodigo(), UsuarioHelper.getPapCodigo(responsavel.getTipoEntidade()), responsavel.getUsuCentralizador());
        for (final TransferObject itemMenu : lstItemMenu) {
            final Integer mnuCodigo = Integer.parseInt(itemMenu.getAttribute(Columns.MNU_CODIGO).toString());
            final String mnuDescricao = (String) itemMenu.getAttribute(Columns.MNU_DESCRICAO);
            final String mnuImagem = (String) itemMenu.getAttribute(Columns.MNU_IMAGEM);
            final String itmCodigo = !TextHelper.isNull(itemMenu.getAttribute(Columns.ITM_CODIGO)) ? itemMenu.getAttribute(Columns.ITM_CODIGO).toString() : null;
            final String funCodigo = (String) itemMenu.getAttribute(Columns.FUN_CODIGO);
            final String acrRecurso = (String) itemMenu.getAttribute(Columns.ACR_RECURSO);
            final String acrMetodoHttp = (String) itemMenu.getAttribute(Columns.ACR_METODO_HTTP);
            final Short imfSequencia = (Short) itemMenu.getAttribute(Columns.IMF_SEQUENCIA);
            final Integer permiteFuncao = (Integer) itemMenu.getAttribute("permiteFuncao");

            ItemMenuTO itemMenuTO = null;

            if (!permiteFuncao.equals(FUNCAO_PROIBIDA) && !itemMenuComImpedimento.contains(itmCodigo)) {
                MenuTO menuTO = null;
                if (!controle.containsKey(mnuCodigo)) {
                    menuTO = new MenuTO(mnuCodigo);
                    menuTO.setMnuDescricao(mnuDescricao);
                    menuTO.setMnuImagem(mnuImagem);
                    controle.put(mnuCodigo, menuTO);
                    menu.add(menuTO);
                }
                menuTO = controle.get(mnuCodigo);
                final List<ItemMenuTO> subMenu = menuTO.getItens();

                if (!TextHelper.isNull(acrRecurso)) {
                    // Faz tratamento para método POST
                    if (!CodedValues.METODO_POST.equals(acrMetodoHttp)) {
                        itemMenu.setAttribute(Columns.ACR_RECURSO, ".." + acrRecurso);
                    } else {
                        itemMenu.setAttribute(Columns.ACR_RECURSO, "javascript:postData('.." + acrRecurso + "')");
                    }
                }

                if (!TextHelper.isNull(funCodigo)) {
                    // Se a funcao adicionada anteriormente é prioritaria em relacao a atual, entao pode pular
                    if (mapFuncaoPrioridade.containsKey(funCodigo) && funCodigoAnterior.equals(mapFuncaoPrioridade.get(funCodigo))) {
                        continue;

                    } else if (mapFuncaoPrioridade.containsKey(funCodigoAnterior) && mapFuncaoPrioridade.containsValue(funCodigo) && mapFuncaoPrioridade.get(funCodigoAnterior).equals(funCodigo)) {

                        // Se a funcao atual é prioritaria em relacao a adicionada anteriormente, entao apenas troca
                        subMenu.remove(subMenu.size() - 1);
                        itemMenuTO = new ItemMenuTO(itemMenu);
                        subMenu.add(itemMenuTO);

                        continue;
                    }

                    // Variavel auxiliar na verificacao de funcoes prioritarias
                    funCodigoAnterior = funCodigo;
                }

                if (!TextHelper.isNull(acrRecurso)) {
                    if (responsavel.isSer() && !TextHelper.isNull(funCodigo) && (CodedValues.FUN_SIM_CONSIGNACAO.equals(funCodigo) || CodedValues.FUN_RES_MARGEM.equals(funCodigo))) {
                        // Busca Lista de serviços disponíveis para solicitação pelo servidor
                        final String orgCodigo = responsavel.getOrgCodigo();
                        if (!TextHelper.isNull(orgCodigo)) {
                            try {
                                final boolean temPermissaoSimulacao = responsavel.temPermissao(CodedValues.FUN_SIM_CONSIGNACAO);
                                final boolean temPermissaoReserva = responsavel.temPermissao(CodedValues.FUN_RES_MARGEM);
                                final boolean temPermissaoSolicitacao = responsavel.temPermissao(CodedValues.FUN_SOL_EMPRESTIMO);

                                final List<TransferObject> servicosReserva = SolicitacaoServidorHelper.lstServicos(orgCodigo, null, null, temPermissaoSimulacao, temPermissaoReserva, temPermissaoSolicitacao, responsavel);

                                if (!ParamSist.getBoolParamSist(CodedValues.TPC_EXIBE_MENU_SOLICITAR_SERVICOS_OPERACIONAL_SERVIDOR, responsavel)) {
                                    if ((servicosReserva != null) && !servicosReserva.isEmpty()) {
                                        for (final TransferObject servico : servicosReserva) {
                                            final String link = servico.getAttribute("link").toString();
                                            final String label = servico.getAttribute("label").toString();
                                            final String svcCodigo = servico.getAttribute("svcCodigo").toString();

                                            if(ParamSist.getBoolParamSist(CodedValues.TPC_HABILITA_PORTAL_BENEFICIOS, responsavel)) {
                                                final CustomTransferObject ocularMenuServicoServidor = parametrocontroller.getParamSvcCse(svcCodigo, CodedValues.TPS_OCULTAR_MENU_SERVIDOR, responsavel);
                                                if ((ocularMenuServicoServidor != null) && (ocularMenuServicoServidor.getAttribute(Columns.PSE_VLR) != null) && CodedValues.PSE_BOOLEANO_SIM.equals(ocularMenuServicoServidor.getAttribute(Columns.PSE_VLR))) {
                                                    continue;
                                                }
                                            }

                                            final ItemMenuTO novoItem = new ItemMenuTO(itemMenu);
                                            novoItem.setItmDescricao(label);
                                            novoItem.setAcrRecurso("javascript:postData('" + link + "')");
                                            subMenu.add(novoItem);

                                            if (imfSequencia != null) {
                                                menuFavoritoTO.getItens().add(novoItem);
                                            }
                                        }
                                    }
                                } else if ((servicosReserva != null) && !servicosReserva.isEmpty()) {
                                    final String label = ApplicationResourcesHelper.getMessage("rotulo.menu.solicitar.servico", responsavel);

                                    final String rse = responsavel.getRseCodigo();

                                    final ItemMenuTO novoItem = new ItemMenuTO(itemMenu);
                                    novoItem.setItmDescricao(label);
                                    novoItem.setAcrRecurso("javascript:postData('../v3/simularConsignacao?acao=listarServicosServidor&RSE_CODIGO=" + rse + "')");
                                    subMenu.add(novoItem);

                                    if (imfSequencia != null) {
                                        menuFavoritoTO.getItens().add(novoItem);
                                    }
                                }

                            } catch (ViewHelperException | ParametroControllerException ex) {
                                LOG.error(ex.getMessage(), ex);
                                throw new MenuControllerException("mensagem.erroInternoSistema", responsavel, ex);
                            }
                        }
                    } else {
                        // Adiciona o item de menu ao subMenu
                        itemMenuTO = new ItemMenuTO(itemMenu);
                        subMenu.add(itemMenuTO);
                    }
                }
            }

            if ((imfSequencia != null) && (itemMenuTO != null)) {
                if (TextHelper.isNull(menuFavoritoTO.getMnuImagem())) {
                    menuFavoritoTO.setMnuImagem(mnuImagem);
                }
                menuFavoritoTO.getItens().add(itemMenuTO);
            }
        }

        if (!menuFavoritoTO.getItens().isEmpty()) {
            Collections.sort(menuFavoritoTO.getItens(), (i1, i2) -> {
            	final Short d1 = (Short) i1.getAttribute(Columns.IMF_SEQUENCIA);
            	final Short d2 = (Short) i2.getAttribute(Columns.IMF_SEQUENCIA);
                return d1.compareTo(d2);
            });
            menu.add(0, menuFavoritoTO);
        }

        return menu;
    }

    /**
     * Método que realiza a consulta ao menu disponível para o usuário
     * @param usuCodigo Usuário logado no sistema.
     * @param tipoEntidade Tipo de entidade do usuário.
     * @param responsavel Responsável pela operação.
     * @return String contendo todo o conteúdo do menu.
     * @throws MenuControllerException Exceção padrão da classe.
     */
    @Override
    public String obterMenu(String usuCodigo, String tipoEntidade, AcessoSistema responsavel) throws MenuControllerException {
        try {
            int mnuCount = 1;
            int itmCount = 1;
            int mnuCodigoAnterior = 0;
            boolean usarSeparador = false;

            final List<String> lstItemMenu = new ArrayList<>();
            final Map<Integer, List<String>> mapSubmenu = new HashMap<>();
            final List<String> lstSubmenuTemp = new ArrayList<>();

            final List<String> itemMenuComImpedimento = lstItemMenuComImpedimento(responsavel);
            final Map<String, String> mapFuncaoPrioridade = getMapFuncaoPrioridade();
            String funCodigoAnterior = "";
            Short itmSequenciaAnterior = 0;

            if (!TextHelper.isNull(tipoEntidade) && (AcessoSistema.ENTIDADE_CSE.equals(tipoEntidade) || AcessoSistema.ENTIDADE_ORG.equals(tipoEntidade) || AcessoSistema.ENTIDADE_CSA.equals(tipoEntidade) || AcessoSistema.ENTIDADE_COR.equals(tipoEntidade) || AcessoSistema.ENTIDADE_SUP.equals(tipoEntidade))) {
                for (final TransferObject to : getMenu(usuCodigo, UsuarioHelper.getPapCodigo(tipoEntidade), responsavel.getUsuCentralizador())) {
                    final Integer mnuCodigo = Integer.parseInt(to.getAttribute(Columns.MNU_CODIGO).toString());
                    final String itmCodigo = !TextHelper.isNull(to.getAttribute(Columns.ITM_CODIGO)) ? to.getAttribute(Columns.ITM_CODIGO).toString() : null;
                    final String itmCodigoPai = (to.getAttribute(Columns.ITM_CODIGO_PAI) != null) ? to.getAttribute(Columns.ITM_CODIGO_PAI).toString() : null;
                    final String itmDescricao = (String) to.getAttribute(Columns.ITM_DESCRICAO);
                    final Short itmSequencia = (Short) to.getAttribute(Columns.ITM_SEQUENCIA);
                    final String itmSeparador = (String) to.getAttribute(Columns.ITM_SEPARADOR);
                    final String acrRecurso = (String) to.getAttribute(Columns.ACR_RECURSO);
                    final String funCodigo = (String) to.getAttribute(Columns.FUN_CODIGO);
                    final Integer permiteFuncao = (Integer) to.getAttribute("permiteFuncao");
                    final String acrMetodoHttp = (String) to.getAttribute(Columns.ACR_METODO_HTTP);

                    if (permiteFuncao.equals(FUNCAO_PROIBIDA) || itemMenuComImpedimento.contains(itmCodigo)) {
                        // Funcao invalida, porem ela pode exigir um separador antes dos proximos itens
                        if (CodedValues.TPC_SIM.equals(itmSeparador) && !itmSequencia.equals(itmSequenciaAnterior)) {
                            usarSeparador = true;
                        }

                    } else {
                        // Verifica prioridade da funcao
                        if (!TextHelper.isNull(funCodigo)) {
                            // Se a funcao adicionada anteriormente é prioritaria em relacao a atual, entao pode pular
                            if (mapFuncaoPrioridade.containsKey(funCodigo) && funCodigoAnterior.equals(mapFuncaoPrioridade.get(funCodigo))) {
                                continue;

                                // Se a funcao atual é prioritaria em relacao a adicionada anteriormente, entao apenas troca
                            } else if (mapFuncaoPrioridade.containsKey(funCodigoAnterior) && mapFuncaoPrioridade.containsValue(funCodigo) && mapFuncaoPrioridade.get(funCodigoAnterior).equals(funCodigo)) {
                                funCodigoAnterior = funCodigo;
                                lstItemMenu.set(lstItemMenu.size() - 1, "['" + TextHelper.forJavaScriptBlock(itmDescricao) + "','.." + acrRecurso + "',1,0,0],");
                                continue;
                            }

                            // Variavel auxiliar na verificacao de funcoes prioritarias
                            funCodigoAnterior = funCodigo;
                        }

                        // Se mudou codigo do menu, entao deve isnerir um novo
                        if (mnuCodigoAnterior != mnuCodigo) {
                            final boolean finalizou = finalizaMenu(lstItemMenu);

                            if (finalizou) {
                                // Insere submenus armazenados no map
                                inserirSubmenu(lstItemMenu, mapSubmenu, mnuCount);
                            } else {
                                // Se a finalizacao retornou falso, entao nao usou menu anterior
                                mnuCount--;
                            }

                            // Inicializa Menu
                            lstItemMenu.add("HM_Array" + mnuCount + " = [[210,,,,,,,,,,,,,,,,,,,1,true],");
                            mnuCount++;
                            itmCount = 1;
                        }

                        // Inicializa novo submenu
                        if (TextHelper.isNull(acrRecurso)) {
                            // Armazena temporariamente pois so sera adicionado se tiver subitems
                            lstSubmenuTemp.clear();
                            if (usarSeparador || (CodedValues.TPC_SIM.equals(itmSeparador))) {
                                lstSubmenuTemp.add("['<HR>','',1,0,0],");
                                usarSeparador = false;
                            }
                            lstSubmenuTemp.add("['" + TextHelper.forJavaScriptBlock(itmDescricao) + "','',1,0,1],");

                            // Adiciona item ao menu a raiz
                        } else if (TextHelper.isNull(itmCodigoPai)) {
                            lstSubmenuTemp.clear();
                            if ((usarSeparador || (CodedValues.TPC_SIM.equals(itmSeparador))) && !lstItemMenu.get(lstItemMenu.size() - 1).startsWith("HM_Array")) {
                                lstItemMenu.add("['<HR>','',1,0,0],");
                                itmCount++;
                                usarSeparador = false;
                            }
                            if (!CodedValues.METODO_POST.equals(acrMetodoHttp)) {
                                lstItemMenu.add("['" + TextHelper.forJavaScriptBlock(itmDescricao) + "','.." + acrRecurso + "',1,0,0],");
                            } else {
                                lstItemMenu.add("['" + TextHelper.forJavaScriptBlock(itmDescricao) + "','javascript:postData(\\'.." + acrRecurso + "\\')',1,0,0],");
                            }
                            itmCount++;

                            // Subitems adicionados ao map (itmCodigoPai != null)
                        } else {
                            // Submenu pode efetivamente ser adicionado, pois possui subitems
                            if (!lstSubmenuTemp.isEmpty()) {
                                if (lstSubmenuTemp.size() > 1) {
                                    lstItemMenu.add(lstSubmenuTemp.get(0));
                                    itmCount++;
                                }
                                lstItemMenu.add(lstSubmenuTemp.get(lstSubmenuTemp.size() - 1));
                                mapSubmenu.put(itmCount, new ArrayList<>());
                                itmCount++;
                                lstSubmenuTemp.clear();
                            }
                            // Subitems sao armazenados no map
                            final List<String> temp = mapSubmenu.get(itmCount - 1);
                            if (!CodedValues.METODO_POST.equals(acrMetodoHttp)) {
                                temp.add("['" + TextHelper.forJavaScriptBlock(itmDescricao) + "','.." + acrRecurso + "',1,0,0],");
                            } else {
                                temp.add("['" + TextHelper.forJavaScriptBlock(itmDescricao) + "','javascript:postData(\\'.." + acrRecurso + "\\')',1,0,0],");
                            }
                            mapSubmenu.put(itmCount - 1, temp);
                        }

                        // Variavel auxiliar no uso de separador
                        itmSequenciaAnterior = itmSequencia;

                        // Variavel auxiliar na definicao do menu
                        mnuCodigoAnterior = mnuCodigo;
                    }
                }
                finalizaMenu(lstItemMenu);

                // Insere submenus armazenados no map
                inserirSubmenu(lstItemMenu, mapSubmenu, mnuCount);
            }

            final StringBuilder resultado = new StringBuilder();
            for (final String valor : lstItemMenu) {
                resultado.append(valor);
            }
            return resultado.toString();

        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new MenuControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Pesquisa o conjunto de menus possíveis para o papel do usuário.
     * @param usuCodigo Usuário logado.
     * @param papCodigo Codigo do papel do usuário.
     * @return Lista de menus possíveis para o papel.
     * @throws MenuControllerException Exceção padrao da classe.
     */
    private List<TransferObject> getMenu(String usuCodigo, String papCodigo, String usuCentralizador) throws MenuControllerException {
        try {
            final ObterMenuQuery query = new ObterMenuQuery();
            query.usuCodigo = usuCodigo;
            query.papCodigo = papCodigo;
            query.usuCentralizador = usuCentralizador;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new MenuControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /**
     * Lista menus de acordo com filtros passados.
     * @param criterio Dados a serem filtrados.
     * @param responsavel Responsável pela operação.
     * @return Lista de menus.
     * @throws MenuControllerException Exceção padrão da classe.
     */
    @Override
    public List<TransferObject> lstMenu(CustomTransferObject criterio, AcessoSistema responsavel) throws MenuControllerException {
        try {
            final ListaMenuQuery query = new ListaMenuQuery();
            query.mnuCodigo = criterio.getAttribute(Columns.MNU_CODIGO) != null ? criterio.getAttribute(Columns.MNU_CODIGO).toString() : null;
            query.mnuSequencia = criterio.getAttribute(Columns.MNU_SEQUENCIA) != null ? (Short) criterio.getAttribute(Columns.MNU_SEQUENCIA) : null;
            query.mnuAtivo = criterio.getAttribute(Columns.MNU_ATIVO) != null ? (Short) criterio.getAttribute(Columns.MNU_ATIVO) : null;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new MenuControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Lista itens de menu de acordo com filtros passados.
     * @param criterio Dados a serem filtrados.
     * @param responsavel Responsável pela operação.
     * @return Lista de itens de menu.
     * @throws MenuControllerException Exceção padrão da classe.
     */
    @Override
    public List<TransferObject> lstItemMenu(CustomTransferObject criterio, AcessoSistema responsavel) throws MenuControllerException {
        try {
            final ListaItemMenuQuery query = new ListaItemMenuQuery();
            query.mnuCodigo = criterio.getAttribute(Columns.MNU_CODIGO) != null ? criterio.getAttribute(Columns.MNU_CODIGO).toString() : null;
            query.itmCodigo = criterio.getAttribute(Columns.ITM_CODIGO) != null ? criterio.getAttribute(Columns.ITM_CODIGO).toString() : null;
            query.itmCodigoPai = criterio.getAttribute(Columns.ITM_CODIGO_PAI) != null ? criterio.getAttribute(Columns.ITM_CODIGO_PAI).toString() : null;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new MenuControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Atualiza dados do item de menu.
     * @param criterio Dados do item de menu.
     * @param responsavel Responsável pela operação.
     * @throws MenuControllerException Exceção padrão da classe.
     */
    @Override
    public void updateItemMenu(CustomTransferObject criterio, AcessoSistema responsavel) throws MenuControllerException {
        try {
            final String itmCodigo = (String) criterio.getAttribute(Columns.ITM_CODIGO);
            final ItemMenu menu = ItemMenuHome.findByPrimaryKey(itmCodigo);
            if (menu != null) {
                if (criterio.getAttribute(Columns.ITM_DESCRICAO) != null) {
                    menu.setItmDescricao(criterio.getAttribute(Columns.ITM_DESCRICAO).toString());
                }
                if ((criterio.getAttribute(Columns.ITM_SEQUENCIA) != null) && !"".equals(criterio.getAttribute(Columns.ITM_SEQUENCIA).toString())) {
                    menu.setItmSequencia(Short.valueOf(criterio.getAttribute(Columns.ITM_SEQUENCIA).toString()));
                }
                if ((criterio.getAttribute(Columns.ITM_ATIVO) != null) && !"".equals(criterio.getAttribute(Columns.ITM_ATIVO).toString())) {
                    menu.setItmAtivo(Short.valueOf(criterio.getAttribute(Columns.ITM_ATIVO).toString()));
                }
                if ((criterio.getAttribute(Columns.ITM_SEPARADOR) != null) && !"".equals(criterio.getAttribute(Columns.ITM_SEPARADOR).toString())) {
                    menu.setItmSeparador(criterio.getAttribute(Columns.ITM_SEPARADOR).toString());
                }
                AbstractEntityHome.update(menu);

                final LogDelegate log = new LogDelegate(responsavel, Log.ITEM_MENU, Log.UPDATE, Log.LOG_INFORMACAO);
                log.setItemMenu(itmCodigo);
                log.getUpdatedFields(criterio.getAtributos(), null);
                log.write();
            }
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new MenuControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final FindException ex) {
            throw new MenuControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final UpdateException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new MenuControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Cria Item Menu.
     * Caso não seja informado o ITM_CODIGO, será setado com o próximo ID.
     * Caso não seja informado o ITM_ATIVO, será setado como ativo.
     * Caso não seja informado o ITM_SEPARADOR, será setado como N.
     * Caso não seja informado o ITM_CENTRALIZADOR, será setado como S.
     *
     * @param criterio Informações para a criação do Item Menu.
     * @param responsavel Responsável pela operação.
     * @return Retorna o código do Item Menu criado.
     * @throws MenuControllerException
     */
    @Override
    public String createItemMenu(CustomTransferObject criterio, AcessoSistema responsavel) throws MenuControllerException {
        try {
            String itmCodigo = null;
            String mnuCodigo = null;
            String itmCodigoPai = null;
            String itmDescricao = null;
            Short itmAtivo = CodedValues.STS_ATIVO;
            Short itmSequencia = null;
            String itmSeparador = CodedValues.TPC_NAO;
            String itmCentralizador = CodedValues.TPC_SIM;
            String itmImagem = null;
            String itmTexChave = null;

            try {
                itmCodigo = criterio.getAttribute(Columns.ITM_CODIGO).toString();
            } catch (final Exception e1) {
                try {
                    itmCodigo = DBHelper.getNextId();
                } catch (final Exception e) {
                    LOG.error(e.getMessage(), e);
                    throw new MenuControllerException("mensagem.erroInternoSistema", responsavel, e);
                }
            }
            if (!TextHelper.isNull(criterio.getAttribute(Columns.MNU_CODIGO))) {
                mnuCodigo = criterio.getAttribute(Columns.MNU_CODIGO).toString();
            }
            if (!TextHelper.isNull(criterio.getAttribute(Columns.ITM_CODIGO_PAI))) {
                itmCodigoPai = criterio.getAttribute(Columns.ITM_CODIGO_PAI).toString();
            }
            if (!TextHelper.isNull(criterio.getAttribute(Columns.ITM_DESCRICAO))) {
                itmDescricao = criterio.getAttribute(Columns.ITM_DESCRICAO).toString();
            }
            if (!TextHelper.isNull(criterio.getAttribute(Columns.ITM_ATIVO))) {
                try {
                    itmAtivo = Short.valueOf(criterio.getAttribute(Columns.ITM_ATIVO).toString());
                } catch (final NumberFormatException e) {
                    LOG.warn("Não foi possível realizar o parser do código ativo do item menu.");
                }
            }
            if (!TextHelper.isNull(criterio.getAttribute(Columns.ITM_SEQUENCIA))) {
                try {
                    itmSequencia = Short.valueOf(criterio.getAttribute(Columns.ITM_SEQUENCIA).toString());
                } catch (final NumberFormatException e) {
                    LOG.warn("Sequência não informada para o item menu.", e);
                    throw new MenuControllerException("mensagem.erro.sequencia.nao.informada.para.item.menu", responsavel, e);
                }
            }
            if (!TextHelper.isNull(criterio.getAttribute(Columns.ITM_SEPARADOR))) {
                itmSeparador = criterio.getAttribute(Columns.ITM_SEPARADOR).toString();
            }
            if (!TextHelper.isNull(criterio.getAttribute(Columns.ITM_CENTRALIZADOR))) {
                itmCentralizador = criterio.getAttribute(Columns.ITM_CENTRALIZADOR).toString();
            }
            if (!TextHelper.isNull(criterio.getAttribute(Columns.ITM_IMAGEM))) {
                itmImagem = criterio.getAttribute(Columns.ITM_IMAGEM).toString();
            }
            if (!TextHelper.isNull(criterio.getAttribute(Columns.ITM_TEX_CHAVE))) {
                itmTexChave = criterio.getAttribute(Columns.ITM_TEX_CHAVE).toString();
            }
            ItemMenuHome.create(itmCodigo, mnuCodigo, itmCodigoPai, itmDescricao, itmAtivo, itmSequencia, itmSeparador, itmCentralizador, itmImagem, itmTexChave);

            final LogDelegate log = new LogDelegate(responsavel, Log.ITEM_MENU, Log.CREATE, Log.LOG_INFORMACAO);
            log.setItemMenu(itmCodigo);
            log.setMenu(mnuCodigo);
            log.getUpdatedFields(criterio.getAtributos(), null);
            log.write();

            return itmCodigo;

        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new MenuControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final com.zetra.econsig.exception.CreateException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new MenuControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void removeItemMenu(String itmCodigo, AcessoSistema responsavel) throws MenuControllerException {
        try {
            final ItemMenu itemMenu = new ItemMenu();
            itemMenu.setItmCodigo(itmCodigo);
            AbstractEntityHome.remove(itemMenu);

            final LogDelegate log = new LogDelegate(responsavel, Log.ITEM_MENU, Log.DELETE, Log.LOG_INFORMACAO);
            log.setItemMenu(itmCodigo);
            log.write();
        } catch (final LogControllerException e) {
            LOG.error(e.getMessage(), e);
            throw new MenuControllerException("mensagem.erroInternoSistema", responsavel, e);
        } catch (final RemoveException e) {
            LOG.error("Não foi possível remover o item menu.", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new MenuControllerException("mensagem.erro.nao.possivel.remover.item.menu", responsavel, e);
        }
    }

    /**
     * Lista quais sao os itens menu que possuem algum impedimento devido a parametros de sistema.
     * @param responsavel Responsável pela operação.
     * @return Lista de itens menu com impedimento.
     * @throws MenuControllerException Exceção padrão da classe.
     */
    private List<String> lstItemMenuComImpedimento(AcessoSistema responsavel) throws MenuControllerException {
        final List<String> itensMenu = new ArrayList<>();

        final boolean usaDiasUteis = ParamSist.paramEquals(CodedValues.TPC_USA_DIAS_UTEIS_CANC_AUTOMATICO_ADE, CodedValues.TPC_SIM, responsavel) || ParamSist.paramEquals(CodedValues.TPC_USA_DIAS_UTEIS_CONTROLE_COMPRA, CodedValues.TPC_SIM, responsavel) || ParamSist.paramEquals(CodedValues.TPC_USA_DIAS_UTEIS_SOLICIT_SALDO_DEVEDOR, CodedValues.TPC_SIM, responsavel) || ParamSist.paramEquals(CodedValues.TPC_USA_DIAS_UTEIS_CMN_PENDENTE, CodedValues.TPC_SIM, responsavel) || ParamSist.paramEquals(CodedValues.TPC_USA_DIAS_UTEIS_ENTRE_SOLICIT_SALDO_DEV, CodedValues.TPC_SIM, responsavel);

        if (!usaDiasUteis) {
            itensMenu.add(String.valueOf(ItemMenuEnum.CALENDARIO.getCodigo()));
        }

        final boolean temCadastroEmpresaCor = ParamSist.getBoolParamSist(CodedValues.TPC_CADASTRO_EMPRESA_CORRESPONDENTE, responsavel);
        if (!temCadastroEmpresaCor) {
            itensMenu.add(String.valueOf(ItemMenuEnum.RELATORIO_EMPRESAS_CORRESPONDENTES.getCodigo()));
            itensMenu.add(String.valueOf(ItemMenuEnum.EMPRESAS_CORRESPONDENTES.getCodigo()));
        }

        final boolean temSimulacao = ParamSist.getBoolParamSist(CodedValues.TPC_SIMULACAO_CONSIGNACAO, responsavel);
        if (!temSimulacao) {
            itensMenu.add(String.valueOf(ItemMenuEnum.SIMULAR_CONSIGNACAO.getCodigo()));
        }

        final boolean temAlongamento = ParamSist.getBoolParamSist(CodedValues.TPC_TEM_ALONGAMENTO_CONTRATO, responsavel);
        if (!temAlongamento) {
            itensMenu.add(String.valueOf(ItemMenuEnum.ALONGAR_CONTRATO.getCodigo()));
        }

        final boolean temCompra = ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_COMPRA_CONTRATO, responsavel);
        if (!temCompra) {
            itensMenu.add(String.valueOf(ItemMenuEnum.RENEGOCIAR_CONTRATOS_DE_TERCEIROS.getCodigo()));
            itensMenu.add(String.valueOf(ItemMenuEnum.ACOMPANHAR_COMPRA_CONTRATOS.getCodigo()));
            itensMenu.add(String.valueOf(ItemMenuEnum.CANCELAR_COMPRA_CONTRATO.getCodigo()));
            itensMenu.add(String.valueOf(ItemMenuEnum.CONFIRMAR_LIQUIDACAO_DE_COMPRA.getCodigo()));
            itensMenu.add(String.valueOf(ItemMenuEnum.RETIRAR_CONTRATO_DA_COMPRA.getCodigo()));
        }
        
        if (!isValidacaoTotpPermitida(responsavel)) {
            itensMenu.add(String.valueOf(ItemMenuEnum.HABILITAR_VALIDACAO_TOTP.getCodigo()));
        }

        if (ParamSist.paramEquals(CodedValues.TPC_SERVIDOR_AUTORIZA_DESCONTO_PARCIAL, CodedValues.TPC_NAO, responsavel)) {
            itensMenu.add(String.valueOf(ItemMenuEnum.AUTORIZAR_DESCONTO_PARCIAL.getCodigo()));
        }

        final boolean permiteReservaSaudeSemFluxoModuloSaude = ParamSist.paramEquals(CodedValues.TPC_PERMITE_RESERVA_SAUDE_SEM_FLUXO_MODULO_SAUDE, CodedValues.TPC_SIM, responsavel);
        if (permiteReservaSaudeSemFluxoModuloSaude) {
            itensMenu.add(String.valueOf(ItemMenuEnum.INCLUIR_BENEFICIARIO.getCodigo()));
            itensMenu.add(String.valueOf(ItemMenuEnum.LISTAR_CONTRATOS_PENDENTES_BENEFICIO.getCodigo()));
            itensMenu.add(String.valueOf(ItemMenuEnum.REATIVAR_CONTRATO_BENEFICIO.getCodigo()));
            itensMenu.add(String.valueOf(ItemMenuEnum.SIMULACAO_ALTERACAO_BENEFICIOS.getCodigo()));
            itensMenu.add(String.valueOf(ItemMenuEnum.SIMULACAO_BENEFICIOS.getCodigo()));
        }

        if (!ParamSist.getBoolParamSist(CodedValues.TPC_CONTRATOS_DEVEM_SER_VALIDADOS_PELA_CSE, responsavel)) {
            itensMenu.add(String.valueOf(ItemMenuEnum.VALIDAR_DOCUMENTOS_APROVACAO_CSE.getCodigo()));
        }
        
        if (ParamSist.getBoolParamSist(CodedValues.TPC_CRIA_AUTO_CONTRATO_RESCISAO_APOS_INFO_SALDO_DEVEDOR, responsavel)) {
            itensMenu.add(String.valueOf(ItemMenuEnum.RETER_VERBA_RESCISORIA.getCodigo()));
        }

        if (responsavel.isSer()) {
            itensMenu.add(String.valueOf(ItemMenuEnum.REATIVAR_CONSIGNACAO.getCodigo()));
        } else {
            itensMenu.add(String.valueOf(ItemMenuEnum.TERMO_DE_ADESAO.getCodigo()));
        }

        return itensMenu;
    }

    /**
     * Monta um map onde as chaves representam funções que devem ser ignoradas caso seu valor correspondente
     * ja esteja inserido no menu.
     * @return Map com prioridades entre funções do menu.
     */
    private Map<String, String> getMapFuncaoPrioridade() {
        // O valor é mais prioritário que a chave
        final Map<String, String> mapPrioridade = new HashMap<>();
        mapPrioridade.put(CodedValues.FUN_VALIDAR_PROCESSAMENTO_VIA_LOTE, CodedValues.FUN_IMPORTACAO_VIA_LOTE);
        mapPrioridade.put(CodedValues.FUN_CONS_CONSIGNANTE, CodedValues.FUN_EDT_CONSIGNANTE);
        mapPrioridade.put(CodedValues.FUN_CONS_ESTABELECIMENTOS, CodedValues.FUN_EDT_ESTABELECIMENTOS);
        mapPrioridade.put(CodedValues.FUN_CONS_ORGAOS, CodedValues.FUN_EDT_ORGAOS);
        mapPrioridade.put(CodedValues.FUN_CONS_ORGAO, CodedValues.FUN_EDT_ORGAO);
        mapPrioridade.put(CodedValues.FUN_CONSULTAR_SERVIDOR, CodedValues.FUN_EDT_SERVIDOR);
        mapPrioridade.put(CodedValues.FUN_CONS_DADOS_CADASTRAIS_SERVIDOR, CodedValues.FUN_EDT_SERVIDOR);
        mapPrioridade.put(CodedValues.FUN_CONS_GRUPO_CONSIGNATARIA, CodedValues.FUN_CRIAR_GRUPO_CONSIGNATARIA);
        mapPrioridade.put(CodedValues.FUN_CONS_CONSIGNATARIAS, CodedValues.FUN_EDT_CONSIGNATARIAS);
        mapPrioridade.put(CodedValues.FUN_CONS_GRUPO_SERVICO, CodedValues.FUN_CRIAR_GRUPO_SERVICO);
        mapPrioridade.put(CodedValues.FUN_CONS_SERVICOS, CodedValues.FUN_EDT_SERVICOS);
        mapPrioridade.put(CodedValues.FUN_CONS_CORRESPONDENTES, CodedValues.FUN_EDT_CORRESPONDENTES);
        mapPrioridade.put(CodedValues.FUN_CONS_EMPRESA_CORRESPONDENTE, CodedValues.FUN_EDT_EMPRESA_CORRESPONDENTE);
        mapPrioridade.put(CodedValues.FUN_CONSULTAR_BANNER_PROPAGANDA, CodedValues.FUN_EDITAR_BANNER_PROPAGANDA);
        mapPrioridade.put(CodedValues.FUN_CONS_USUARIOS_CSE, CodedValues.FUN_EDT_USUARIOS_CSE);
        mapPrioridade.put(CodedValues.FUN_CONS_USUARIOS_CSA, CodedValues.FUN_EDT_USUARIOS_CSA);
        mapPrioridade.put(CodedValues.FUN_CONS_USUARIOS_COR, CodedValues.FUN_EDT_USUARIOS_COR);
        mapPrioridade.put(CodedValues.FUN_CONS_USUARIOS_ORG, CodedValues.FUN_EDT_USUARIOS_ORG);
        mapPrioridade.put(CodedValues.FUN_CONS_USUARIOS_SUP, CodedValues.FUN_EDT_USUARIOS_SUP);
        mapPrioridade.put(CodedValues.FUN_CANC_MINHAS_RESERVAS, CodedValues.FUN_CANC_RESERVA);
        return mapPrioridade;
    }

    /**
     * Altera o item de menu anterior para que finalize o menu anterior. Não faz nada se a lista estiver vazia.
     * @param lstItemMenu Lista contendo os itens de menu.
     * @return Falso, se o menu anterior nao foi usado. Verdadeiro, se nao houve problema.
     * @throws MenuControllerException Exceção padrão da classe.
     */
    private boolean finalizaMenu(List<String> lstItemMenu) throws MenuControllerException {
        try {
            final String linha = lstItemMenu.get(lstItemMenu.size() - 1);
            if (!linha.endsWith("true],")) {
                lstItemMenu.set(lstItemMenu.size() - 1, linha.substring(0, linha.length() - 1) + "];");
            } else {
                lstItemMenu.remove(lstItemMenu.size() - 1);
                return false;
            }
        } catch (final IndexOutOfBoundsException ex) {
            if (!lstItemMenu.isEmpty()) {
                throw new MenuControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
            }
        }
        return true;
    }

    /**
     * Insere os submenus que estiverem armazenados temporariamente no map.
     * @param lstItemMenu Lista contendo os itens de menu.
     * @param mapSubmenu Map contendo os submenus. A chave indica a posicao do menu onde o submenu deve ser inserido.
     * @param mnuCount Identificador do menu.
     * @throws MenuControllerException Exceção padrão da classe.
     */
    private void inserirSubmenu(List<String> lstItemMenu, Map<Integer, List<String>> mapSubmenu, int mnuCount) throws MenuControllerException {
        for (final int chave : mapSubmenu.keySet()) {
            lstItemMenu.add("HM_Array" + (mnuCount - 1) + "_" + chave + " = [[],");
            lstItemMenu.addAll(mapSubmenu.get(chave));
            finalizaMenu(lstItemMenu);
        }
        mapSubmenu.clear();
    }
    
    private boolean isValidacaoTotpPermitida(AcessoSistema responsavel) throws MenuControllerException {
        boolean tpcPermiteCadastroValidacaoTotp = ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_CADASTRO_VALIDACAO_TOTP, responsavel);
        if (!responsavel.isCsa()) {
            return tpcPermiteCadastroValidacaoTotp;
        }
        try {
        	String pcsVlr = parametrocontroller.getParamCsa(responsavel.getCsaCodigo(), CodedValues.TPA_PERMITE_VALIDACAO_TOTP, responsavel);
			final boolean tpaPermiteValidacaoTotp = !TextHelper.isNull(pcsVlr) && "S".equalsIgnoreCase(pcsVlr);		
            return tpaPermiteValidacaoTotp && tpcPermiteCadastroValidacaoTotp;
        } catch (ParametroControllerException e) {
            LOG.error(e.getMessage(), e);
            throw new MenuControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public void favoritarMenu(String itmCodigo, AcessoSistema responsavel) throws MenuControllerException, FindException, UpdateException {
    	if ((responsavel != null) && !TextHelper.isNull(responsavel.getUsuCodigo()) && !TextHelper.isNull(itmCodigo)) {
    		try {
    			try {
    				// Se o item menu favorito existe, então remove do favorito
    				final ItemMenuFavorito imf = ItemMenuFavoritoHome.findByPrimaryKeyForUpdate(responsavel.getUsuCodigo(), itmCodigo);
    				AbstractEntityHome.remove(imf);
    			} catch (final FindException ex) {
    				Short nextFavorito = 1;
    				final List<MenuTO> menuFavoritos = responsavel.getMenuFavoritos();
    				if (!menuFavoritos.isEmpty()) {

    					final MenuTO menu = menuFavoritos.stream().findFirst().get();
    					final List<ItemMenuTO> itensMenu = menu.getItens();

    					if (!itensMenu.isEmpty()) {
    						nextFavorito = (short) ((Short)itensMenu.get(itensMenu.size() -1).getAttribute(Columns.IMF_SEQUENCIA) + 1);
    					}
    				}
    				// Se o item menu favorito não existe, então inclui o favorito
    				ItemMenuFavoritoHome.create(responsavel.getUsuCodigo(), itmCodigo, nextFavorito);
    			}
    		} catch (RemoveException | com.zetra.econsig.exception.CreateException ex) {
    			LOG.error(ex.getMessage(), ex);
    			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
    			throw new MenuControllerException("mensagem.erroInternoSistema", responsavel, ex);
    		}
    	}
    }

    @Override
    public void updateFavoritosDashBoard(String usuCodigo, String itmCodigo, Short imfSequencia, AcessoSistema responsavel) throws MenuControllerException, FindException, UpdateException {

    	try {
    		ItemMenuFavoritoHome.updateMenuFavoritoDashBoardByUsuCodigo(usuCodigo, itmCodigo, imfSequencia);
		} catch (final Exception e) {
			LOG.error(e.getMessage(), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new MenuControllerException("mensagem.erroInternoSistema", responsavel, e);
		}
    }
}

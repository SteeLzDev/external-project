package com.zetra.econsig.service.sistema;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.TermoAdesaoTO;
import com.zetra.econsig.dto.web.ConsentRequestDTO;
import com.zetra.econsig.dto.web.SerasaToken;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.exception.SerasaException;
import com.zetra.econsig.exception.TermoAdesaoAcaoException;
import com.zetra.econsig.exception.TermoAdesaoControllerException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.termoadesao.TermoAdesaoAcao;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.AbstractEntityHome;
import com.zetra.econsig.persistence.entity.LeituraTermoUsuario;
import com.zetra.econsig.persistence.entity.LeituraTermoUsuarioHome;
import com.zetra.econsig.persistence.entity.TermoAdesao;
import com.zetra.econsig.persistence.entity.TermoAdesaoHome;
import com.zetra.econsig.persistence.query.consentimento.BuscarUltimoConsentimentoQuery;
import com.zetra.econsig.persistence.query.termoAdesao.ListaTermosAdesaoPorUsuCodigoQuery;
import com.zetra.econsig.persistence.query.termoAdesao.ObtemTermoAdesaoPorTadCodigoQuery;
import com.zetra.econsig.persistence.query.termoAdesao.ObtemTermosAdesaoAceiteQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webclient.serasa.OAuthSerasaClient;



/**
 * <p>Title: TermoAdesaoControllerBean</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class TermoAdesaoControllerBean implements TermoAdesaoController {
    private static final Log LOG = LogFactory.getLog(TermoAdesaoControllerBean.class);

    @Autowired
    OAuthSerasaClient oAuthSerasaClient;

    @Override
    public void createTermoAdesao(TermoAdesaoTO termoAdesao, AcessoSistema responsavel) throws TermoAdesaoControllerException {
        try {
            TermoAdesaoHome.create(termoAdesao.getUsuCodigo(), termoAdesao.getFunCodigo(), termoAdesao.getTadTitulo(),
            		termoAdesao.getTadTexto(), termoAdesao.getTadSequencia(), termoAdesao.getTadExibeCse(), termoAdesao.getTadExibeOrg(),
            		termoAdesao.getTadExibeCsa(), termoAdesao.getTadExibeCor(), termoAdesao.getTadExibeSer(), termoAdesao.getTadExibeSup(),
            		termoAdesao.getTadHtml(), termoAdesao.getTadPermiteRecusar(), termoAdesao.getTadPermiteLerDepois());

        } catch (final CreateException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new TermoAdesaoControllerException("mensagem.erroInternoSistema", responsavel);
        }
    }

    @Override
    public TermoAdesaoTO findTermoAdesao(TermoAdesaoTO termoAdesao, AcessoSistema responsavel) throws TermoAdesaoControllerException {
        return setTermoAdesaoValues(findTermoAdesaoBean(termoAdesao));
    }

	private TermoAdesaoTO setTermoAdesaoValues(TermoAdesao termoAdesaoBean) {
		return new TermoAdesaoTO(termoAdesaoBean.getTadCodigo(), termoAdesaoBean.getUsuCodigo(),
				termoAdesaoBean.getFunCodigo(), termoAdesaoBean.getTadTitulo(), termoAdesaoBean.getTadTexto(),
				termoAdesaoBean.getTadData(), termoAdesaoBean.getTadSequencia(), termoAdesaoBean.getTadExibeCse(),
				termoAdesaoBean.getTadExibeOrg(), termoAdesaoBean.getTadExibeCsa(), termoAdesaoBean.getTadExibeCor(),
				termoAdesaoBean.getTadExibeSer(), termoAdesaoBean.getTadExibeSup(), termoAdesaoBean.getTadHtml(),
				termoAdesaoBean.getTadPermiteRecusar(), termoAdesaoBean.getTadPermiteLerDepois(), termoAdesaoBean.getTadClasseAcao(), termoAdesaoBean.getTadExibeAposLeitura());
	}

    private TermoAdesao findTermoAdesaoBean(TermoAdesaoTO termoAdesao) throws TermoAdesaoControllerException {
        TermoAdesao termoAdesaoBean = null;
        if (!TextHelper.isNull(termoAdesao.getTadCodigo())) {
            try {
                termoAdesaoBean = TermoAdesaoHome.findByPrimaryKey(termoAdesao.getTadCodigo());
            } catch (final FindException e) {
                throw new TermoAdesaoControllerException("mensagem.erro.termo.adesao.nao.encontrado", (AcessoSistema) null);
            }
        } else {
            throw new TermoAdesaoControllerException("mensagem.erro.termo.adesao.nao.encontrado", (AcessoSistema) null);
        }
        return termoAdesaoBean;
    }

    @Override
    public void removeTermoAdesao(TermoAdesaoTO termoAdesao, AcessoSistema responsavel) throws TermoAdesaoControllerException {
        try {
            final TermoAdesao termoAdesaoBean = findTermoAdesaoBean(termoAdesao);
            AbstractEntityHome.remove(termoAdesaoBean);
        } catch (final RemoveException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new TermoAdesaoControllerException("mensagem.erro.nao.possivel.excluir.registro.deste.termo.adesao", responsavel);
        }
    }

    @Override
    public void updateTermoAdesao(TermoAdesaoTO termoAdesao, AcessoSistema responsavel) throws TermoAdesaoControllerException {
        try {
            final TermoAdesao termoAdesaoBean = findTermoAdesaoBean(termoAdesao);

            if (!termoAdesaoBean.getTadTexto().equals(termoAdesao.getTadTexto())) {
                termoAdesaoBean.setTadTexto(termoAdesao.getTadTexto());
            }
            AbstractEntityHome.update(termoAdesaoBean);

        } catch (final UpdateException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new TermoAdesaoControllerException(e);
        }
    }

    @Override
    public List<TransferObject> findTermoAdesaoComLeituraByTadCodigo(String tadCodigo, AcessoSistema responsavel) throws TermoAdesaoControllerException {
        List<TransferObject> retorno = null;
        try {
            final ObtemTermoAdesaoPorTadCodigoQuery bean = new ObtemTermoAdesaoPorTadCodigoQuery();
            bean.tadCodigo = tadCodigo;
            bean.responsavel = responsavel;
            retorno = bean.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        return retorno;
    }

	@Override
	public List<TermoAdesaoTO> listTermoAdesaoByFuncao(String funCodigo, AcessoSistema responsavel) throws TermoAdesaoControllerException {
		final List<TermoAdesaoTO> retorno = new ArrayList<>();

		try {
			final Collection<TermoAdesao> lista = TermoAdesaoHome.findByFunCodigo(funCodigo);

			for (final TermoAdesao termoAdesao : lista) {
				retorno.add(setTermoAdesaoValues(termoAdesao));
			}
		} catch (final FindException e) {
            throw new TermoAdesaoControllerException("mensagem.erro.termo.adesao.nao.encontrado", responsavel);
		}

        return retorno;
	}

	@Override
	public List<TermoAdesaoTO> listTermoAdesaoSemLeitura(String funCodigo, List<String> termoAdesaoLerDepois, AcessoSistema responsavel) throws TermoAdesaoControllerException {
		final List<TermoAdesaoTO> retorno = new ArrayList<>();

		try {
			final Collection<TermoAdesao> lista = TermoAdesaoHome.findUnreadByUsuCodigoAndFunCodigo(responsavel.getUsuCodigo(), funCodigo, termoAdesaoLerDepois, responsavel);

			for (final TermoAdesao termoAdesao : lista) {
				retorno.add(setTermoAdesaoValues(termoAdesao));
			}
		} catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
		}

        return retorno;
	}

    @Override
    public List<TermoAdesaoTO> listTermoAdesaoSemFunCodigoExibeServidor() throws TermoAdesaoControllerException {
        final List<TermoAdesaoTO> retorno = new ArrayList<>();

        try {
            final Collection<TermoAdesao> lista = TermoAdesaoHome.listWithoutFunCodigoAndShowToServer();

            for (final TermoAdesao termoAdesao : lista) {
                retorno.add(setTermoAdesaoValues(termoAdesao));
            }
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        return retorno;
    }

    @Override
    public List<TransferObject> listTermoAdesaoByUsuCodigo(AcessoSistema responsavel) throws TermoAdesaoControllerException {
        List<TransferObject> retorno = new ArrayList<>();
        try {
            final ListaTermosAdesaoPorUsuCodigoQuery bean = new ListaTermosAdesaoPorUsuCodigoQuery();
            bean.responsavel = responsavel;
            retorno = bean.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        return retorno;
    }

    @Override
    public void createLeituraTermoAdesaoUsuario(String tadCodigo, boolean aceito, String ltuObs, AcessoSistema responsavel) throws TermoAdesaoControllerException {
        try {
            List<TransferObject> retorno = null;
			TermoAdesaoAcao termoAdesaoAcao = null;
            try {
                final ObtemTermoAdesaoPorTadCodigoQuery bean = new ObtemTermoAdesaoPorTadCodigoQuery();
                bean.tadCodigo = tadCodigo;
                bean.responsavel = responsavel;
                retorno = bean.executarDTO();
            } catch (final HQueryException ex) {
                LOG.error(ex.getMessage(), ex);
            }

            Integer versaoTermo = null;
            String enviaApiConsentimento = null;
            if ((retorno != null) && !retorno.isEmpty()) {
                final TransferObject to = retorno.get(0);
                versaoTermo = (Integer) to.getAttribute(Columns.TAD_VERSAO_TERMO);
                enviaApiConsentimento = (String) to.getAttribute(Columns.TAD_ENVIA_API_CONSENTIMENTO);

                final String termoClassName = (String) to.getAttribute(Columns.TAD_CLASSE_ACAO);

                if (!TextHelper.isNull(termoClassName)) {
                	termoAdesaoAcao = (TermoAdesaoAcao) Class.forName(termoClassName).getDeclaredConstructor().newInstance();
                }
            }

            if (termoAdesaoAcao != null) {
				if (aceito) {
					termoAdesaoAcao.preAceiteTermo(tadCodigo, responsavel);
				} else {
				    termoAdesaoAcao.preRecusaTermo(tadCodigo, responsavel);
				}
			}

            final LeituraTermoUsuario termo = LeituraTermoUsuarioHome.create(
                responsavel.getUsuCodigo(),
                tadCodigo,
                aceito ? CodedValues.TPC_SIM : CodedValues.TPC_NAO,
                responsavel.getCanal().getCodigo(),
                responsavel.getIpUsuario(),
                responsavel.getPortaLogicaUsuario(),
                ltuObs,
                versaoTermo
            );

            if (!TextHelper.isNull(enviaApiConsentimento)) {
                if (!TextHelper.isNull(versaoTermo)) {
                    enviaConsentimentoSerasa(termo, responsavel);
                } else {
                    // a Versão do termo é obrigatório para o Serasa
                    LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.configuracao.consentimento.serasa", responsavel));
                    throw new TermoAdesaoControllerException("mensagem.erro.processamento.interno", responsavel);
                }
            }

            if (termoAdesaoAcao != null) {
				if (aceito) {
				    termoAdesaoAcao.posAceiteTermo(tadCodigo, responsavel);
				} else {
				    termoAdesaoAcao.posRecusaTermo(tadCodigo, responsavel);
				}
			}
        } catch (final CreateException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | ClassNotFoundException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new TermoAdesaoControllerException("mensagem.erroInternoSistema", responsavel);
        } catch (final TermoAdesaoAcaoException ex) {
        	LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new TermoAdesaoControllerException(ex);
		}
    }

    @Override
	public List<TransferObject> findTermoAdesaoAceite(AcessoSistema responsavel) throws TermoAdesaoControllerException {
        List<TransferObject> lista = new ArrayList<>();
        try {
            final ObtemTermosAdesaoAceiteQuery bean = new ObtemTermosAdesaoAceiteQuery();
            bean.responsavel = responsavel;
            lista = bean.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        return lista;
    }

    @Override
    public List<TransferObject> findTermoAdesaoGestaoFinanceira( String usuCpf, String tadCodigo, AcessoSistema responsavel) throws TermoAdesaoControllerException {
        try {
            final BuscarUltimoConsentimentoQuery query = new BuscarUltimoConsentimentoQuery();
            query.cpf = usuCpf;
            query.tadCodigo = tadCodigo;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            throw new TermoAdesaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private void enviaConsentimentoSerasa(LeituraTermoUsuario termo, AcessoSistema responsavel) throws TermoAdesaoControllerException {
        final String urlAuth = (String) ParamSist.getInstance().getParam(CodedValues.TPC_URL_BASE_AUTH_SERASA, responsavel);
        final String clientId = (String) ParamSist.getInstance().getParam(CodedValues.TPC_CONSENT_SERASA_OAUTH_CLIENT_ID, responsavel);
        final String clientSecret = (String) ParamSist.getInstance().getParam(CodedValues.TPC_CONSENT_SERASA_OAUTH_CLIENT_SECRET, responsavel);

        if (!TextHelper.isNull(urlAuth) && !TextHelper.isNull(clientId) && !TextHelper.isNull(clientSecret)) {
            try {
                final String ltuCodigo = termo.getLtuCodigo();
                final ResponseEntity<SerasaToken> response = oAuthSerasaClient.autenticar(urlAuth, clientId, clientSecret, responsavel);
                final SerasaToken token = response.getBody();

                final String cpf = responsavel.getSerCpf();

                final ConsentRequestDTO consentData = oAuthSerasaClient.montarConsentimento(cpf, termo.getTadCodigo(), responsavel.getUsuCodigo(), token, responsavel, ltuCodigo);
                if (consentData == null) {
                    return;
                }
                oAuthSerasaClient.enviarConsentimento(consentData, responsavel, token);
            } catch (final SerasaException ex) {
                LOG.error(ex.getMessage(), ex);
                try {
                    final LogDelegate log = new LogDelegate(
                        responsavel,
                        com.zetra.econsig.helper.log.Log.REGISTRO_SERVIDOR,
                        com.zetra.econsig.helper.log.Log.CREATE ,
                        com.zetra.econsig.helper.log.Log.LOG_ERRO
                    );
                        log.add(ApplicationResourcesHelper.getMessage("mensagem.erro.comunicacao.serasa", responsavel));
                        log.write();
                    } catch (final LogControllerException e) {
                            LOG.error(e.getMessage(), e);
                    }

                throw new TermoAdesaoControllerException("mensagem.erro.enviar.consentimento.serasa", responsavel);
            }
        }
    }
}

package com.zetra.econsig.helper.seguranca;

import java.io.Serializable;
import java.sql.Time;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.zetra.econsig.delegate.ParametroDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.CalendarioTO;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.cache.ExternalCacheHelper;
import com.zetra.econsig.helper.cache.ExternalMap;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.calendario.CalendarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ControleRestricaoAcesso</p>
 * <p>Description: Classe que constrói e verifica cache de regras de restrições de acesso. </p>
 * <p>Copyright: Copyright (c) 2009-2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Leonel Martins
 */
public class ControleRestricaoAcesso implements Serializable {
	/**
     *
     */
    private static final long serialVersionUID = 1L;

    private static Map<String, HashMap<String, TreeMap<String,List<RestricaoAcesso>>>> cacheRestricaoAcesso;

    private static final String RESTRICAO_GERAL = "todos";

	public enum GrauRestricao  {SemRestricao, RestricaoGeral, RestricaoOperacao}

    private static class SingletonHelper {
        private static final ControleRestricaoAcesso instance = new ControleRestricaoAcesso();
    }

    public static ControleRestricaoAcesso getInstance() {
        return SingletonHelper.instance;
    }

	public static ControleRestricaoAcesso.RestricaoAcesso possuiRestricaoAcesso(AcessoSistema responsavel) throws ZetraException {
		if (cacheRestricaoAcesso == null) {
			constroiCache(responsavel);

			// se cache continuar nulo, não há restrições registradas.
			if (cacheRestricaoAcesso == null) {
				final ControleRestricaoAcesso.RestricaoAcesso novaRestricao =  SingletonHelper.instance. new RestricaoAcesso(null, null, null, null, null, null);
				novaRestricao.setGrauRestricao(GrauRestricao.SemRestricao);
				return novaRestricao;
			}
		}

		//TODO: atualmente só há restrição específica para CSA/COR
		String entCodigo = RESTRICAO_GERAL;
		if (responsavel.isCsa() && !TextHelper.isNull(responsavel.getCodigoEntidade())) {
			entCodigo = responsavel.getCodigoEntidade();
		} else if (responsavel.isCor() && !TextHelper.isNull(responsavel.getCodigoEntidadePai())) {
			entCodigo = responsavel.getCodigoEntidadePai();
		}

		String papCodigo = RESTRICAO_GERAL;
		if (responsavel.isCsa()) {
			papCodigo = CodedValues.PAP_CONSIGNATARIA;
		} else if (responsavel.isCor()) {
			papCodigo = CodedValues.PAP_CORRESPONDENTE;
		} else if (responsavel.isCse()) {
			papCodigo = CodedValues.PAP_CONSIGNANTE;
		} else if (responsavel.isOrg()) {
			papCodigo = CodedValues.PAP_ORGAO;
		} else if (responsavel.isSer()) {
			papCodigo = CodedValues.PAP_SERVIDOR;
		} else if (responsavel.isSup()) {
			papCodigo = CodedValues.PAP_SUPORTE;
		}

		final String funCodigo = (TextHelper.isNull(responsavel.getFunCodigo())) ? RESTRICAO_GERAL : responsavel.getFunCodigo();

		final HashMap<String, TreeMap<String,List<RestricaoAcesso>>> papCodMap = cacheRestricaoAcesso.get(entCodigo);
		HashMap<String, TreeMap<String,List<RestricaoAcesso>>> papCodMapGeral = null;
		// verifica não só as restrições específicas da entidade como as gerais que se aplicam a ela
		if (!entCodigo.equals(RESTRICAO_GERAL)) {
			papCodMapGeral = cacheRestricaoAcesso.get(RESTRICAO_GERAL);
		}

		if ((papCodMap == null) && (papCodMapGeral == null)) {
			final ControleRestricaoAcesso.RestricaoAcesso novaRestricao =  SingletonHelper.instance. new RestricaoAcesso(null, null, null, null, null, null);
			novaRestricao.setGrauRestricao(GrauRestricao.SemRestricao);
			return novaRestricao;
		} else {
			TreeMap<String,List<RestricaoAcesso>> restricaoTree = null;
			if (papCodMap != null) {
				restricaoTree = papCodMap.get(papCodigo);
			}

			TreeMap<String,List<RestricaoAcesso>> restricaoTreeGeral = null;
			if (papCodMapGeral != null) {
				restricaoTreeGeral = papCodMapGeral.get(papCodigo);
			}

			TreeMap<String,List<RestricaoAcesso>> restricaoTreeGeral2 = null;
			TreeMap<String,List<RestricaoAcesso>> restricaoTreeGeral3 = null;
			if (!papCodigo.equals(RESTRICAO_GERAL)) {
				if (papCodMap != null) {
					restricaoTreeGeral2 = papCodMap.get(RESTRICAO_GERAL);
				}
				if (papCodMapGeral != null) {
					restricaoTreeGeral3 = papCodMapGeral.get(RESTRICAO_GERAL);
				}
			}

			if ((restricaoTree == null) && ((restricaoTreeGeral == null) && (restricaoTreeGeral2 == null)) && (restricaoTreeGeral3 == null)) {
				final ControleRestricaoAcesso.RestricaoAcesso novaRestricao =  SingletonHelper.instance. new RestricaoAcesso(null, null, null, null, null, null);
				novaRestricao.setGrauRestricao(GrauRestricao.SemRestricao);
				return novaRestricao;
			} else {
				List<RestricaoAcesso> restricaoList = null;

				if (restricaoTree != null) {
					if (restricaoTree.get(funCodigo) != null) {
						restricaoList = new ArrayList<>(restricaoTree.get(funCodigo));
					}
				}

				if ((restricaoTreeGeral != null) && (restricaoTreeGeral.get(funCodigo) != null)) {
					if (restricaoList == null) {
						restricaoList = new ArrayList<>(restricaoTreeGeral.get(funCodigo));
					} else {
						restricaoList.addAll(restricaoTreeGeral.get(funCodigo));
					}
				}

				if ((restricaoTreeGeral2 != null) && (restricaoTreeGeral2.get(funCodigo) != null)) {
					if (restricaoList == null) {
						restricaoList = new ArrayList<>(restricaoTreeGeral2.get(funCodigo));
					} else {
						restricaoList.addAll(restricaoTreeGeral2.get(funCodigo));
					}
				}

				if ((restricaoTreeGeral3 != null) && (restricaoTreeGeral3.get(funCodigo) != null)) {
					if (restricaoList == null) {
						restricaoList = new ArrayList<>(restricaoTreeGeral3.get(funCodigo));
					} else {
						restricaoList.addAll(restricaoTreeGeral3.get(funCodigo));
					}
				}

				List<RestricaoAcesso> restricaoListGeral = null;
				if (!funCodigo.equals(RESTRICAO_GERAL)) {
					if ((restricaoTree != null) && (restricaoTree.get(RESTRICAO_GERAL) != null)) {
						restricaoListGeral = new ArrayList<>(restricaoTree.get(RESTRICAO_GERAL));
					}

					if ((restricaoTreeGeral != null) && (restricaoTreeGeral.get(RESTRICAO_GERAL) != null)) {
						if (restricaoListGeral == null) {
							restricaoListGeral = new ArrayList<>(restricaoTreeGeral.get(RESTRICAO_GERAL));
						} else {
							restricaoListGeral.addAll(restricaoTreeGeral.get(RESTRICAO_GERAL));
						}
					}

					if ((restricaoTreeGeral2 != null) && (restricaoTreeGeral2.get(RESTRICAO_GERAL) != null)) {
						if (restricaoListGeral == null) {
							restricaoListGeral = new ArrayList<>(restricaoTreeGeral2.get(RESTRICAO_GERAL));
						} else {
							restricaoListGeral.addAll(restricaoTreeGeral2.get(RESTRICAO_GERAL));
						}
					}

					if ((restricaoTreeGeral3 != null) && (restricaoTreeGeral3.get(RESTRICAO_GERAL) != null)) {
						if (restricaoListGeral == null) {
							restricaoListGeral = new ArrayList<>(restricaoTreeGeral3.get(RESTRICAO_GERAL));
						} else {
							restricaoListGeral.addAll(restricaoTreeGeral3.get(RESTRICAO_GERAL));
						}
					}

					if (restricaoListGeral != null) {
						if (restricaoList != null) {
							restricaoList.addAll(restricaoListGeral);
						} else {
							restricaoList = restricaoListGeral;
						}
					}
				}

				if ((restricaoList == null) || restricaoList.isEmpty()) {
					final ControleRestricaoAcesso.RestricaoAcesso novaRestricao =  SingletonHelper.instance. new RestricaoAcesso(null, null, null, null, null, null);
					novaRestricao.setGrauRestricao(GrauRestricao.SemRestricao);
					return novaRestricao;
				} else {
					final Date agora = DateHelper.getSystemDatetime();

					for (final RestricaoAcesso restricao: restricaoList) {
						// verifica se está no horário de vigor da restrição
						final String agoraString = DateHelper.format(agora, "HH:mm:ss");
						Date agoraAux;
						try {
							agoraAux = DateHelper.parse(agoraString, "HH:mm:ss");
						} catch (final ParseException e) {
							throw new ZetraException("mensagem.erroInternoSistema", responsavel);
						}
						if ((agoraAux.getTime() < restricao.horaInicio.getTime()) || (restricao.horaFim.getTime() < agoraAux.getTime())) {
							continue;
						}

						if (restricao.data != null) {
							final Date agoraHourCleaned = DateHelper.clearHourTime(agora);
							if (DateHelper.dayDiff(restricao.data, agoraHourCleaned) == 0) {
								if (restricao.funCodigo.equals(RESTRICAO_GERAL)) {
									restricao.setGrauRestricao(GrauRestricao.RestricaoGeral);
								} else {
									restricao.setGrauRestricao(GrauRestricao.RestricaoOperacao);
								}
								return restricao;
							}
						} else if (restricao.diaSemana != null) {
							switch (restricao.diaSemana) {
							    case Calendar.SUNDAY:
								    if (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
								    	if (restricao.funCodigo.equals(RESTRICAO_GERAL)) {
											restricao.setGrauRestricao(GrauRestricao.RestricaoGeral);
										} else {
											restricao.setGrauRestricao(GrauRestricao.RestricaoOperacao);
										}
										return restricao;
								    }
								    break;
							    case Calendar.MONDAY:
							    	if (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
							    		if (restricao.funCodigo.equals(RESTRICAO_GERAL)) {
											restricao.setGrauRestricao(GrauRestricao.RestricaoGeral);
										} else {
											restricao.setGrauRestricao(GrauRestricao.RestricaoOperacao);
										}
										return restricao;
							    	}
							    	break;
							    case Calendar.TUESDAY:
							    	if (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY) {
							    		if (restricao.funCodigo.equals(RESTRICAO_GERAL)) {
											restricao.setGrauRestricao(GrauRestricao.RestricaoGeral);
										} else {
											restricao.setGrauRestricao(GrauRestricao.RestricaoOperacao);
										}
										return restricao;
							    	}
							    	break;
							    case Calendar.WEDNESDAY:
							    	if (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) {
							    		if (restricao.funCodigo.equals(RESTRICAO_GERAL)) {
											restricao.setGrauRestricao(GrauRestricao.RestricaoGeral);
										} else {
											restricao.setGrauRestricao(GrauRestricao.RestricaoOperacao);
										}
										return restricao;
							    	}
							    	break;
							    case Calendar.THURSDAY:
							    	if (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {
							    		if (restricao.funCodigo.equals(RESTRICAO_GERAL)) {
											restricao.setGrauRestricao(GrauRestricao.RestricaoGeral);
										} else {
											restricao.setGrauRestricao(GrauRestricao.RestricaoOperacao);
										}
										return restricao;
							    	}
							    	break;
							    case Calendar.FRIDAY:
							    	if (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
							    		if (restricao.funCodigo.equals(RESTRICAO_GERAL)) {
											restricao.setGrauRestricao(GrauRestricao.RestricaoGeral);
										} else {
											restricao.setGrauRestricao(GrauRestricao.RestricaoOperacao);
										}
										return restricao;
							    	}
							    	break;
							    case Calendar.SATURDAY:
							    	if (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
							    		if (restricao.funCodigo.equals(RESTRICAO_GERAL)) {
											restricao.setGrauRestricao(GrauRestricao.RestricaoGeral);
										} else {
											restricao.setGrauRestricao(GrauRestricao.RestricaoOperacao);
										}
										return restricao;
							    	}
							    	break;
							    default:
							    	break;
							}
						} else if (restricao.diaUtil != null) {
						    final CalendarioController calendarioController = ApplicationContextProvider.getApplicationContext().getBean(CalendarioController.class);
							final CalendarioTO calTO = calendarioController.findCalendario(agora, responsavel);

							if (restricao.diaUtil && (calTO.getCalDiaUtil().equals(CodedValues.TPC_SIM))) {
								if (restricao.funCodigo.equals(RESTRICAO_GERAL)) {
									restricao.setGrauRestricao(GrauRestricao.RestricaoGeral);
								} else {
									restricao.setGrauRestricao(GrauRestricao.RestricaoOperacao);
								}
								return restricao;
							}

							if (!restricao.diaUtil && (calTO.getCalDiaUtil().equals(CodedValues.TPC_NAO))) {
								if (restricao.funCodigo.equals(RESTRICAO_GERAL)) {
									restricao.setGrauRestricao(GrauRestricao.RestricaoGeral);
								} else {
									restricao.setGrauRestricao(GrauRestricao.RestricaoOperacao);
								}
								return restricao;
							}
						} else  {
							if (restricao.funCodigo.equals(RESTRICAO_GERAL)) {
								restricao.setGrauRestricao(GrauRestricao.RestricaoGeral);
							} else {
								restricao.setGrauRestricao(GrauRestricao.RestricaoOperacao);
							}
							return restricao;
						}
					}
				}
			}
		}

		final ControleRestricaoAcesso.RestricaoAcesso novaRestricao =  SingletonHelper.instance. new RestricaoAcesso(null, null, null, null, null, null);
		novaRestricao.setGrauRestricao(GrauRestricao.SemRestricao);
		return novaRestricao;
	}

	private static void constroiCache(AcessoSistema responsavel) throws ZetraException {
		final ParametroDelegate paramDelegate = new ParametroDelegate();

        final List<TransferObject> restricoesAcesso = paramDelegate.lstTodasRestricoesAcesso(responsavel);

		if ((restricoesAcesso != null) && !restricoesAcesso.isEmpty()) {
			if (cacheRestricaoAcesso == null) {
		        if (ExternalCacheHelper.hasExternal()) {
		            cacheRestricaoAcesso = new ExternalMap<>();
		        } else {
		            cacheRestricaoAcesso = new HashMap<>();
		        }
			}

			for (final TransferObject registro: restricoesAcesso) {
				final CustomTransferObject restricao = (CustomTransferObject)registro;
				final String csaCodigo = (restricao.getAttribute(Columns.RCA_CSA_CODIGO) != null) ? (String) restricao.getAttribute(Columns.RCA_CSA_CODIGO): RESTRICAO_GERAL;
				final String papCodigo = (restricao.getAttribute(Columns.RRA_PAP_CODIGO) != null) ? (String) restricao.getAttribute(Columns.RRA_PAP_CODIGO): RESTRICAO_GERAL;
				final String funCodigo = (restricao.getAttribute(Columns.RRA_FUN_CODIGO) != null) ? (String) restricao.getAttribute(Columns.RRA_FUN_CODIGO): RESTRICAO_GERAL;

				final Boolean diasUteis = (TextHelper.isNull(restricao.getAttribute(Columns.RRA_DIAS_UTEIS)))
				                        ? null
		                                : (((String) restricao.getAttribute(Columns.RRA_DIAS_UTEIS)).equals("S"));

				final ControleRestricaoAcesso.RestricaoAcesso novaRestricao =  SingletonHelper.instance. new RestricaoAcesso(funCodigo, (Date) restricao.getAttribute(Columns.RRA_DATA), (Short) restricao.getAttribute(Columns.RRA_DIA_SEMANA),
						                                            diasUteis, (Time) restricao.getAttribute(Columns.RRA_HORA_INICIO), (Time) restricao.getAttribute(Columns.RRA_HORA_FIM));
				novaRestricao.setDescricao((String) restricao.getAttribute(Columns.RRA_DESCRICAO));

				addRestricaoToCache(csaCodigo, papCodigo, funCodigo, novaRestricao, responsavel);
			}
		} else {
            if (ExternalCacheHelper.hasExternal()) {
                cacheRestricaoAcesso = new ExternalMap<>();
            } else {
                cacheRestricaoAcesso = new HashMap<>();
            }
		}
	}

	public static void resetCacheRestricoes() {
		cacheRestricaoAcesso = null;
	}

	public static void addRestricao(TransferObject restricao, AcessoSistema responsavel) throws ZetraException {
		final String csaCodigo = (restricao.getAttribute(Columns.RCA_CSA_CODIGO) != null) ? (String) restricao.getAttribute(Columns.RCA_CSA_CODIGO): RESTRICAO_GERAL;
		final String papCodigo = (restricao.getAttribute(Columns.RRA_PAP_CODIGO) != null) ? (String) restricao.getAttribute(Columns.RRA_PAP_CODIGO): RESTRICAO_GERAL;
		final String funCodigo = (restricao.getAttribute(Columns.RRA_FUN_CODIGO) != null) ? (String) restricao.getAttribute(Columns.RRA_FUN_CODIGO): RESTRICAO_GERAL;

		final Boolean diasUteis = (TextHelper.isNull(restricao.getAttribute(Columns.RRA_DIAS_UTEIS)))
		                        ? null
                                : (((String) restricao.getAttribute(Columns.RRA_DIAS_UTEIS)).equals("S"));

		final ControleRestricaoAcesso.RestricaoAcesso novaRestricao =  SingletonHelper.instance. new RestricaoAcesso(funCodigo, (Date) restricao.getAttribute(Columns.RRA_DATA), (Short) restricao.getAttribute(Columns.RRA_DIA_SEMANA),
				                                            diasUteis, (Time) restricao.getAttribute(Columns.RRA_HORA_INICIO), (Time) restricao.getAttribute(Columns.RRA_HORA_FIM));
		novaRestricao.setDescricao((String) restricao.getAttribute(Columns.RRA_DESCRICAO));

		addRestricaoToCache(csaCodigo, papCodigo, funCodigo, novaRestricao, responsavel);
	}

	private static void addRestricaoToCache(String csaCodigo, String papCodigo, String funCodigo, RestricaoAcesso restricao, AcessoSistema responsavel) throws ZetraException {
		if (cacheRestricaoAcesso == null) {
			constroiCache(responsavel);

			if (cacheRestricaoAcesso == null) {
                if (ExternalCacheHelper.hasExternal()) {
                    cacheRestricaoAcesso = new ExternalMap<>();
                } else {
                    cacheRestricaoAcesso = new HashMap<>();
                }
			}
		}

		// cria entradas de restrição por consignatária
		HashMap<String, TreeMap<String,List<RestricaoAcesso>>> papCodMap = cacheRestricaoAcesso.get(csaCodigo);

		if (papCodMap == null) {
			final TreeMap<String,List<RestricaoAcesso>> restricaoTree = new TreeMap<>();
			final List<RestricaoAcesso> restricaoList = new ArrayList<>();
			restricaoList.add(restricao);
			restricaoTree.put(funCodigo, restricaoList);

			papCodMap = new HashMap<>();
			papCodMap.put(papCodigo, restricaoTree);

			final Map<String, HashMap<String, TreeMap<String,List<RestricaoAcesso>>>> synchCache = Collections.synchronizedMap(cacheRestricaoAcesso);
			synchCache.put(csaCodigo, papCodMap);
		} else {
			TreeMap<String,List<RestricaoAcesso>> restricaoTree = papCodMap.get(papCodigo);

			if (restricaoTree == null ) {
				final List<RestricaoAcesso> restricaoList = new ArrayList<>();
				restricaoList.add(restricao);

				restricaoTree = new TreeMap<>();
				restricaoTree.put(funCodigo, restricaoList);

				final Map<String, TreeMap<String,List<RestricaoAcesso>>> synchPapCodMap = Collections.synchronizedMap(papCodMap);
				synchPapCodMap.put(papCodigo, restricaoTree);
			} else {
				List<RestricaoAcesso> restricaoList = restricaoTree.get(funCodigo);

				if (restricaoList == null) {
					restricaoList = new ArrayList<>();
					restricaoList.add(restricao);

					final Map<String,List<RestricaoAcesso>> synchFunTree = Collections.synchronizedMap(restricaoTree);
					synchFunTree.put(funCodigo, restricaoList);
				} else {
					if (!restricaoList.contains(restricao)) {
						restricaoList.add(restricao);
					}
				}
			}
            if (ExternalCacheHelper.hasExternal()) {
                final Map<String, HashMap<String, TreeMap<String,List<RestricaoAcesso>>>> synchCache = Collections.synchronizedMap(cacheRestricaoAcesso);
                synchCache.put(csaCodigo, papCodMap);
            }
		}
	}

	public class RestricaoAcesso implements Serializable {
		/**
         *
         */
        private static final long serialVersionUID = 1L;

        Date data;
		Short diaSemana;
		Boolean diaUtil;
		Time horaInicio;
		Time horaFim;
		String funCodigo;
		String descricao;
		GrauRestricao grauRestricao;

		public RestricaoAcesso(String funCodigo, Date data, Short diaSemana, Boolean diaUtil, Time horaInicio, Time horaFim) {
			this.data = data;
			this.diaSemana = diaSemana;
			this.diaUtil = diaUtil;
			this.horaInicio = horaInicio;
			this.horaFim = horaFim;
			this.funCodigo = funCodigo;
		}

		public String getDescricao() {
			return descricao;
		}

		public void setDescricao(String descricao) {
			this.descricao = descricao;
		}

		public GrauRestricao getGrauRestricao() {
			return grauRestricao;
		}

		public void setGrauRestricao(GrauRestricao grauRestricao) {
			this.grauRestricao = grauRestricao;
		}

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = (prime * result) + getOuterType().hashCode();
            result = (prime * result) + ((data == null) ? 0 : data.hashCode());
            result = (prime * result) + ((descricao == null) ? 0 : descricao.hashCode());
            result = (prime * result) + ((diaSemana == null) ? 0 : diaSemana.hashCode());
            result = (prime * result) + ((diaUtil == null) ? 0 : diaUtil.hashCode());
            result = (prime * result) + ((funCodigo == null) ? 0 : funCodigo.hashCode());
            result = (prime * result) + ((grauRestricao == null) ? 0 : grauRestricao.hashCode());
            result = (prime * result) + ((horaFim == null) ? 0 : horaFim.hashCode());
            result = (prime * result) + ((horaInicio == null) ? 0 : horaInicio.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object other) {
            if ((this == other)) {
                return true;
            }
            if ((other == null)) {
                return false;
            }
            if (!(other instanceof final RestricaoAcesso castOther)) {
                return false;
            }
            return ((data == castOther.data) ||
                    ((data != null) && (castOther.data != null) && data.equals(castOther.data))) &&
                   ((diaSemana == castOther.diaSemana) || ((diaSemana != null) && (castOther.diaSemana != null) && diaSemana.equals(castOther.diaSemana))) &&
                   ((diaUtil == castOther.diaUtil) || ((diaUtil != null) && (castOther.diaUtil != null) && diaUtil.equals(castOther.diaUtil))) &&
                   ((horaInicio == castOther.horaInicio) || ((horaInicio != null) && (castOther.horaInicio != null) && (horaInicio.getTime() == castOther.horaInicio.getTime()))) &&
                   ((horaFim == castOther.horaFim) || ((horaFim != null) && (castOther.horaFim != null) && (horaFim.getTime() == castOther.horaFim.getTime())));
        }

        private ControleRestricaoAcesso getOuterType() {
            return ControleRestricaoAcesso.this;
        }
	}
}

package com.zetra.econsig.helper.folha;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.MargemControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.service.margem.MargemController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: CacheDependenciasServidor</p>
 * <p>Description: Cache para manter as dependências do servidor para utilização em rotinas de carga.</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class CacheDependenciasServidor {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(CacheDependenciasServidor.class);

    private final Map<String, List<TransferObject>> cacheByIdentificador;
    private final Map<String, List<TransferObject>> cacheByTipo;
    private final String[] identificadores;

    public static final String PREFIX_SVC_ATIVO = "SVC_ATIVO_";

    // Read and Write lock: permite múltiplos leitores, desde que não haja escritores.
    private final ReentrantReadWriteLock lock;

    public CacheDependenciasServidor() {
        this(new String[]{
                Columns.ORG_CODIGO,
                Columns.ORG_IDENTIFICADOR,
                Columns.ORG_CNPJ,
                Columns.EST_CODIGO,
                Columns.EST_IDENTIFICADOR,
                Columns.EST_CNPJ,
                Columns.SBO_CODIGO,
                Columns.SBO_IDENTIFICADOR,
                Columns.UNI_CODIGO,
                Columns.UNI_IDENTIFICADOR,
                Columns.VRS_CODIGO,
                Columns.VRS_IDENTIFICADOR,
                Columns.CRS_CODIGO,
                Columns.CRS_IDENTIFICADOR,
                Columns.PRS_CODIGO,
                Columns.PRS_IDENTIFICADOR,
                Columns.POS_CODIGO,
                Columns.POS_IDENTIFICADOR,
                Columns.THA_CODIGO,
                Columns.THA_IDENTIFICADOR,
                Columns.NES_CODIGO,
                Columns.NES_IDENTIFICADOR,
                Columns.TRS_CODIGO,
                Columns.CAP_CODIGO,
                Columns.TBC_CODIGO,
                Columns.MAR_CODIGO,
                Columns.TDA_CODIGO
        });
    }

    public CacheDependenciasServidor(String[] identificadoresUtilizados) {
        cacheByIdentificador = new HashMap<>();
        cacheByTipo = new HashMap<>();
        identificadores = identificadoresUtilizados;
        lock = new ReentrantReadWriteLock();
    }

    public boolean existeIdentificador(String valorIdentificador, String campoIdentificador) {
        lock.readLock().lock();
        try {
            String chave = campoIdentificador + ":" + valorIdentificador;
            return (cacheByIdentificador.get(chave) != null && !cacheByIdentificador.get(chave).isEmpty());
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<TransferObject> getByValorIdentificador(String valorIdentificador, String campoIdentificador) {
        lock.readLock().lock();
        try {
            String chave = campoIdentificador + ":" + valorIdentificador;
            return (cacheByIdentificador.get(chave) != null ? Collections.unmodifiableList(cacheByIdentificador.get(chave)) : null);
        } finally {
            lock.readLock().unlock();
        }
    }

    public Map<String, List<TransferObject>> getMapByCampoIdentificador(String campoIdentificador) {
        lock.readLock().lock();
        try {
            Map<String, List<TransferObject>> subCache = new HashMap<>();
            for (String chave : cacheByIdentificador.keySet()) {
                if (chave.startsWith(campoIdentificador)) {
                    String codigo = chave.substring(chave.indexOf(':') + 1);
                    subCache.put(codigo, cacheByIdentificador.get(chave));
                }
            }
            return Collections.unmodifiableMap(subCache);
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<TransferObject> getListByCampoIdentificador(String campoIdentificador) {
        lock.readLock().lock();
        try {
            return Collections.unmodifiableList(cacheByTipo.get(campoIdentificador));
        } finally {
            lock.readLock().unlock();
        }
    }

    public void carregarCache(AcessoSistema responsavel) {
        lock.writeLock().lock();
        try {
            cacheByIdentificador.clear();
            cacheByTipo.clear();

            ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
            ServidorDelegate serDelegate = new ServidorDelegate();

            for (String identificador : identificadores) {
                List<TransferObject> listaTO = null;
                if (identificador.equals(Columns.ORG_CODIGO) || identificador.equals(Columns.ORG_IDENTIFICADOR) || identificador.equals(Columns.ORG_CNPJ)) {
                    listaTO = cseDelegate.lstOrgaos(null, responsavel);
                } else if (identificador.equals(Columns.EST_CODIGO) || identificador.equals(Columns.EST_IDENTIFICADOR) || identificador.equals(Columns.EST_CNPJ)) {
                    listaTO = cseDelegate.lstEstabelecimentos(null, responsavel);
                } else if (identificador.equals(Columns.SBO_CODIGO) || identificador.equals(Columns.SBO_IDENTIFICADOR)) {
                    listaTO = serDelegate.lstSubOrgao(responsavel, null);
                } else if (identificador.equals(Columns.UNI_CODIGO) || identificador.equals(Columns.UNI_IDENTIFICADOR)) {
                    listaTO = serDelegate.lstUnidade(responsavel, null);
                } else if (identificador.equals(Columns.VRS_CODIGO) || identificador.equals(Columns.VRS_IDENTIFICADOR)) {
                    listaTO = serDelegate.selectVincRegistroServidor(false, responsavel);
                } else if (identificador.equals(Columns.CRS_CODIGO) || identificador.equals(Columns.CRS_IDENTIFICADOR)) {
                    listaTO = serDelegate.lstCargo(responsavel);
                } else if (identificador.equals(Columns.PRS_CODIGO) || identificador.equals(Columns.PRS_IDENTIFICADOR)) {
                    listaTO = serDelegate.lstPadrao(responsavel);
                } else if (identificador.equals(Columns.POS_CODIGO) || identificador.equals(Columns.POS_IDENTIFICADOR)) {
                    listaTO = serDelegate.lstPosto(responsavel);
                } else if (identificador.equals(Columns.THA_CODIGO) || identificador.equals(Columns.THA_IDENTIFICADOR)) {
                    listaTO = serDelegate.getTipoHabitacao(responsavel);
                } else if (identificador.equals(Columns.NES_CODIGO) || identificador.equals(Columns.NES_IDENTIFICADOR)) {
                    listaTO = serDelegate.getNivelEscolaridade(responsavel);
                } else if (identificador.equals(Columns.TRS_CODIGO)) {
                    listaTO = serDelegate.lstTipoRegistroServidor(responsavel);
                } else if (identificador.equals(Columns.CAP_CODIGO)) {
                    listaTO = serDelegate.lstCapacidadeCivil(responsavel);
                } else if (identificador.equals(Columns.TBC_CODIGO)) {
                    listaTO = serDelegate.lstTipoBaseCalculo(responsavel);
                } else if (identificador.equals(Columns.MAR_CODIGO)) {
                    MargemController margemController = ApplicationContextProvider.getApplicationContext().getBean(MargemController.class);
                    listaTO = margemController.lstMargem(responsavel);
                    cacheMargensComServicoAtivo(listaTO, responsavel);
                } else if (identificador.equals(Columns.TDA_CODIGO)) {
                    ServidorController servidorController = ApplicationContextProvider.getApplicationContext().getBean(ServidorController.class);
                    listaTO = servidorController.lstTipoDadoAdicionalServidorQuery(null, null, responsavel);
                }

                // Cache pelo tipo de entidade
                cacheByTipo.put(identificador, listaTO);

                // Cache pelo identificador da entidade
                for (TransferObject entidade : listaTO) {
                    if (entidade.getAttribute(identificador) != null) {
                        String chave = identificador + ":" + entidade.getAttribute(identificador).toString();
                        if (cacheByIdentificador.get(chave) == null) {
                            cacheByIdentificador.put(chave, new ArrayList<>());
                        }
                        cacheByIdentificador.get(chave).add(entidade);
                    }
                }
            }
        } catch (ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * faz cache de serviços ativos que incidem nas respectivas margens.
     *
     * @param listaMargens
     * @throws HQueryException
     */
	private void cacheMargensComServicoAtivo(List<TransferObject> listaMargens, AcessoSistema responsavel) throws MargemControllerException {
	    ServidorDelegate serDelegate = new ServidorDelegate();
		List<TransferObject> marSvcAtivo = serDelegate.lstMargemComServicoAtivo(responsavel);

		listaMargens.forEach(mar ->
		cacheByTipo.put(PREFIX_SVC_ATIVO + mar.getAttribute(Columns.MAR_CODIGO),
				marSvcAtivo.stream().filter(marTO -> marTO.getAttribute(Columns.MAR_CODIGO).toString().equals(mar.getAttribute(Columns.MAR_CODIGO).toString())).toList())
		);
	}
}

package com.zetra.econsig.service.beneficios;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.BeneficioControllerException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.Beneficio;
import com.zetra.econsig.persistence.entity.BeneficioHome;
import com.zetra.econsig.persistence.entity.BeneficioServico;
import com.zetra.econsig.persistence.entity.BeneficioServicoId;
import com.zetra.econsig.persistence.entity.Consignataria;
import com.zetra.econsig.persistence.entity.NaturezaServico;
import com.zetra.econsig.persistence.entity.RelacionamentoBeneficioServicoHome;
import com.zetra.econsig.persistence.query.beneficios.ListaBeneficioQuery;
import com.zetra.econsig.persistence.query.beneficios.ListaTipoBeneficiarioQuery;
import com.zetra.econsig.persistence.query.beneficios.ListarBeneficioByCsaCodigoAndCorCodigosAndNaturezaServicoQuery;
import com.zetra.econsig.persistence.query.beneficios.ListarBeneficioByCsaCodigoAndNaturezaServicoAndRelacionamentoServicoQuery;
import com.zetra.econsig.persistence.query.beneficios.ListarBeneficioByCsaCodigoAndNaturezaServicoQuery;
import com.zetra.econsig.persistence.query.beneficios.ListarBeneficioCsaOperadoraQuery;
import com.zetra.econsig.persistence.query.beneficios.ListarRelacaoBeneficiosObitoQuery;
import com.zetra.econsig.persistence.query.beneficios.ListarRelacaoBeneficiosQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.TipoBeneficiarioEnum;

/**
 * <p>Title: BeneficioControllerBean</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class BeneficioControllerBean implements BeneficioController {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(BeneficioControllerBean.class);

    @Override
    public List<TransferObject> listaBeneficio(TransferObject criterio, AcessoSistema responsavel) throws BeneficioControllerException {
        try {
            ListaBeneficioQuery query = new ListaBeneficioQuery();

            if (criterio != null) {
                query.csaCodigo = criterio.getAttribute(Columns.ECH_CSA_CODIGO);
                query.benCodigo = criterio.getAttribute(Columns.BEN_CODIGO);
            }

            return query.executarDTO();
        } catch (HQueryException ex) {
            throw new BeneficioControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> listaTipoBeneficiario(TransferObject criterio, AcessoSistema responsavel) throws BeneficioControllerException {
        try {
            ListaTipoBeneficiarioQuery query = new ListaTipoBeneficiarioQuery();

            if (criterio != null) {
                query.tibCodigo = criterio.getAttribute(Columns.TIB_CODIGO);
            }

            return query.executarDTO();
        } catch (HQueryException ex) {
            throw new BeneficioControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> lstBeneficioCsaOperadora(TransferObject criterio, AcessoSistema responsavel) throws BeneficioControllerException {
        try {
            ListarBeneficioCsaOperadoraQuery query = new ListarBeneficioCsaOperadoraQuery();

            if (criterio != null) {
                query.ncaCodigo = criterio.getAttribute(Columns.NCA_CODIGO);
                if (!criterio.getAttribute("filtro_tipo").equals("-1")) {
                    query.filtro = criterio.getAttribute("filtro").toString();
                    query.filtro_tipo = criterio.getAttribute("filtro_tipo").toString();
                } else {
                    query.filtro_tipo = criterio.getAttribute("filtro_tipo").toString();
                }
            }

            query.count = false;

            return query.executarDTO();
        } catch (Exception e) {
            throw new BeneficioControllerException(e);
        }
    }

    @Override
    public List<TransferObject> lstBeneficioCsaOperadoraPaginacao(TransferObject criterio, int offset, int valorFinal, AcessoSistema responsavel) throws BeneficioControllerException {
        try {
            ListarBeneficioCsaOperadoraQuery query = new ListarBeneficioCsaOperadoraQuery();

            if (criterio != null) {
                query.ncaCodigo = criterio.getAttribute(Columns.NCA_CODIGO);
                if (!criterio.getAttribute("filtro_tipo").equals("-1")) {
                    query.filtro = criterio.getAttribute("filtro").toString();
                    query.filtro_tipo = criterio.getAttribute("filtro_tipo").toString();
                } else {
                    query.filtro_tipo = criterio.getAttribute("filtro_tipo").toString();
                }
            }

            query.count = false;
            query.firstResult = offset;
            query.maxResults = valorFinal;

            return query.executarDTO();
        } catch (Exception e) {
            throw new BeneficioControllerException(e);
        }
    }

    @Override
    public int lstCountBeneficioCsaOperadora(TransferObject criterio, AcessoSistema responsavel) throws BeneficioControllerException {
        try {
            ListarBeneficioCsaOperadoraQuery query = new ListarBeneficioCsaOperadoraQuery();

            if (criterio != null) {
                query.ncaCodigo = criterio.getAttribute(Columns.NCA_CODIGO);
                if (!criterio.getAttribute("filtro_tipo").equals("-1")) {
                    query.filtro = criterio.getAttribute("filtro").toString();
                    query.filtro_tipo = criterio.getAttribute("filtro_tipo").toString();
                } else {
                    query.filtro_tipo = criterio.getAttribute("filtro_tipo").toString();
                }
            }

            query.count = true;

            return query.executarContador();
        } catch (Exception e) {
            throw new BeneficioControllerException(e);
        }
    }

    @Override
    public Beneficio findBeneficioByCodigo(String benCodigo, AcessoSistema responsavel) throws BeneficioControllerException {
        try {
            return BeneficioHome.findByPrimaryKey(benCodigo);
        } catch (Exception ex) {
            throw new BeneficioControllerException(ex);
        }
    }

    @Override
    public Beneficio findBeneficioFetchBeneficioServicoByCodigo(String benCodigo, AcessoSistema responsavel) throws BeneficioControllerException {
        try {
            return BeneficioHome.findBeneficioFetchBeneficioServicoByCodigo(benCodigo);
        } catch (Exception ex) {
            throw new BeneficioControllerException(ex);
        }
    }

    @Override
    public void update(Beneficio beneficio, AcessoSistema responsavel) throws BeneficioControllerException {
        try {
            Beneficio beneficioAntigo = BeneficioHome.findByBenCodigo(beneficio.getBenCodigo());
            LogDelegate log = new LogDelegate(responsavel, Log.BENEFICIO, Log.UPDATE, Log.LOG_INFORMACAO);

            if (beneficio.getBenCodigoContrato().compareTo(beneficioAntigo.getBenCodigoContrato()) != 0) {
                log.addChangedField(Columns.BEN_CODIGO_CONTRATO, beneficio.getBenCodigoContrato());
            }
            if (beneficio.getBenCodigoPlano().compareTo(beneficioAntigo.getBenCodigoPlano()) != 0) {
                log.addChangedField(Columns.BEN_CODIGO_PLANO, beneficio.getBenCodigoPlano());
            }
            if (beneficio.getBenCodigoRegistro().compareTo(beneficioAntigo.getBenCodigoRegistro()) != 0) {
                log.addChangedField(Columns.BEN_CODIGO_REGISTRO, beneficio.getBenCodigoRegistro());
            }
            if (beneficio.getBenDescricao().compareTo(beneficioAntigo.getBenDescricao()) != 0) {
                log.addChangedField(Columns.BEN_DESCRICAO, beneficio.getBenDescricao());
            }
            if (beneficio.getConsignataria().getCsaCodigo().compareTo(beneficioAntigo.getConsignataria().getCsaCodigo()) != 0) {
                log.addChangedField(Columns.CSA_CODIGO, beneficio.getConsignataria().getCsaCodigo());
            }
            if (beneficio.getNaturezaServico().getNseCodigo().compareTo(beneficioAntigo.getNaturezaServico().getNseCodigo()) != 0) {
                log.addChangedField(Columns.NSE_CODIGO, beneficio.getNaturezaServico().getNseCodigo());
            }
            if (beneficio.getBenAtivo().compareTo(beneficioAntigo.getBenAtivo()) != 0) {
                log.addChangedField(Columns.BEN_ATIVO, beneficio.getBenAtivo());
            }

            BeneficioHome.update(beneficio);

            log.write();
        } catch (Exception ex) {
            throw new BeneficioControllerException(ex);
        }
    }

    @Override
    public Beneficio create(Consignataria consignataria, NaturezaServico naturezaServico, String descricao, String codigoPlano, String codigoRegistro, String codigoContrato, AcessoSistema responsavel) throws BeneficioControllerException {
        try {
            Beneficio beneficio = BeneficioHome.create(consignataria, naturezaServico, descricao, codigoPlano, codigoRegistro, codigoContrato);
            LogDelegate log = new LogDelegate(responsavel, Log.BENEFICIO, Log.CREATE, Log.LOG_INFORMACAO);
            log.write();
            return beneficio;
        } catch (Exception ex) {
            throw new BeneficioControllerException(ex);
        }
    }

    @Override
    public void remove(Beneficio beneficio, AcessoSistema responsavel) throws BeneficioControllerException {
        try {

            RelacionamentoBeneficioServicoHome.deleteRelacionamentoBeneficioServicoByBenCodigo(beneficio.getBenCodigo().toString());

            BeneficioHome.remove(beneficio);
            LogDelegate log = new LogDelegate(responsavel, Log.BENEFICIO, Log.DELETE, Log.LOG_INFORMACAO);
            log.write();
        } catch (Exception ex) {
            throw new BeneficioControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> findRelacaoBeneficioByRseCodigo(CustomTransferObject criterio, AcessoSistema responsavel) throws BeneficioControllerException {
        try {
            ListarRelacaoBeneficiosQuery query = new ListarRelacaoBeneficiosQuery();

            if (criterio != null) {
                if ((String) criterio.getAttribute(Columns.SER_CODIGO) != null) {
                    query.serCodigo = (String) criterio.getAttribute(Columns.SER_CODIGO);
                } else {
                    query.rseCodigo = (String) criterio.getAttribute(Columns.RSE_CODIGO);
                }
                if ((String) criterio.getAttribute(Columns.BEN_CODIGO) != null) {
                    query.benCodigo = (String) criterio.getAttribute(Columns.BEN_CODIGO);
                }
                if ((String) criterio.getAttribute(Columns.BFC_CODIGO) != null) {
                    query.bfcCodigo = (String) criterio.getAttribute(Columns.BFC_CODIGO);
                }
                if (criterio.getAttribute("reativar") != null) {
                    query.reativar = criterio.getAttribute("reativar").equals("true");
                }
                if (criterio.getAttribute("contratosAtivos") != null) {
                    query.contratosAtivos = Boolean.valueOf(criterio.getAttribute("contratosAtivos").toString());
                }
                if ((String) criterio.getAttribute(Columns.NSE_CODIGO) != null) {
                    query.nseCodigo = (String) criterio.getAttribute(Columns.NSE_CODIGO);
                }
            }

            return query.executarDTO();
        } catch (Exception e) {
            throw new BeneficioControllerException(e);
        }
    }

    @Override
    public Beneficio create(Consignataria consignataria, NaturezaServico naturezaServico, String descricao, String codigoPlano, String codigoRegistro, String codigoContrato, Map<String, List<BeneficioServico>> relacionamentos, AcessoSistema responsavel) throws BeneficioControllerException {
        try {
            Beneficio beneficio = BeneficioHome.create(consignataria, naturezaServico, descricao, codigoPlano, codigoRegistro, codigoContrato);

            for (String chave : relacionamentos.keySet()) {

                Short bseOrdemTitular = 0;
                Short bseOrdemNaoTitular = 1;

                for (BeneficioServico relacionamentoServicoBeneficio : relacionamentos.get(chave)) {

                    BeneficioServico relacionamentoExiste = null;

                    // Verifica se já existe relacionamento com os mesmos ben_codigo, svc_codigo e tib_codigo
                    relacionamentoExiste = RelacionamentoBeneficioServicoHome.findByIdn(beneficio.getBenCodigo().toString(), relacionamentoServicoBeneficio.getServico().getSvcCodigo(), relacionamentoServicoBeneficio.getTipoBeneficiario().getTibCodigo());

                    if (relacionamentoExiste != null) {
                        throw new BeneficioControllerException("mensagem.erro.beneficio.editar", responsavel);
                    }

                    if (relacionamentoServicoBeneficio.getTipoBeneficiario().getTibCodigo().equals(TipoBeneficiarioEnum.TITULAR.tibCodigo)) {
                        criaRelacionamento(beneficio, relacionamentoServicoBeneficio, bseOrdemTitular);
                        bseOrdemTitular++;
                    } else {
                        criaRelacionamento(beneficio, relacionamentoServicoBeneficio, bseOrdemNaoTitular);
                        bseOrdemNaoTitular++;
                    }
                }
            }
            LogDelegate log = new LogDelegate(responsavel, Log.BENEFICIO, Log.CREATE, Log.LOG_INFORMACAO);
            log.write();

            return beneficio;
        } catch (Exception ex) {
            throw new BeneficioControllerException(ex);
        }
    }

    @Override
    public void update(Beneficio beneficio, Map<String, List<BeneficioServico>> relacionamentos, AcessoSistema responsavel) throws BeneficioControllerException {
        try {

            RelacionamentoBeneficioServicoHome.deleteRelacionamentoBeneficioServicoByBenCodigo(beneficio.getBenCodigo().toString());

            for (String chave : relacionamentos.keySet()) {

                Short bseOrdemTitular = 0;
                Short bseOrdemNaoTitular = 1;

                for (BeneficioServico relacionamentoServicoBeneficio : relacionamentos.get(chave)) {

                    BeneficioServico relacionamentoExiste = null;

                    // Verifica se já existe relacionamento com os mesmos ben_codigo, svc_codigo e tib_codigo
                    relacionamentoExiste = RelacionamentoBeneficioServicoHome.findByIdn(beneficio.getBenCodigo().toString(), relacionamentoServicoBeneficio.getServico().getSvcCodigo(), relacionamentoServicoBeneficio.getTipoBeneficiario().getTibCodigo());

                    if (relacionamentoExiste != null) {
                        throw new BeneficioControllerException("mensagem.erro.beneficio.editar", responsavel);
                    }

                    if (relacionamentoServicoBeneficio.getTipoBeneficiario().getTibCodigo().equals(TipoBeneficiarioEnum.TITULAR.tibCodigo)) {
                        criaRelacionamento(beneficio, relacionamentoServicoBeneficio, bseOrdemTitular);
                        bseOrdemTitular++;
                    } else {
                        criaRelacionamento(beneficio, relacionamentoServicoBeneficio, bseOrdemNaoTitular);
                        bseOrdemNaoTitular++;
                    }

                }
            }

            update(beneficio, responsavel);

        } catch (Exception ex) {
            throw new BeneficioControllerException(ex);
        }
    }

    /**
     * A lista de Beneficio não contem todos os objetos, ajustar a Query e a montagem do objeto para devolver os valores necessarios.
     * @param csaCodigo
     * @param nseCodigo
     * @param benAtivo
     * @param responsavel
     * @return
     * @throws BeneficioControllerException
     */
    @Override
    public List<Beneficio> lstBeneficioByCsaCodigoAndNaturezaServicoAndRelacionamentoServico(String csaCodigo, String nseCodigo, boolean benAtivo, AcessoSistema responsavel) throws BeneficioControllerException {
        try {
            List<Beneficio> beneficios = new ArrayList<>();

            ListarBeneficioByCsaCodigoAndNaturezaServicoAndRelacionamentoServicoQuery query = new ListarBeneficioByCsaCodigoAndNaturezaServicoAndRelacionamentoServicoQuery();
            query.csaCodigo = csaCodigo;
            query.nseCodigo = nseCodigo;
            query.benAtivo = benAtivo;

            List<TransferObject> resultados = query.executarDTO();

            for (TransferObject resultado : resultados) {
                Beneficio beneficio = new Beneficio();
                String benCodigo = resultado.getAttribute(Columns.BEN_CODIGO).toString();
                String benDescricao = resultado.getAttribute(Columns.BEN_DESCRICAO).toString();
                beneficio.setBenCodigo(benCodigo);
                beneficio.setBenDescricao(benDescricao);

                beneficios.add(beneficio);
            }

            return beneficios;
        } catch (Exception e) {
            LOG.error(e.getCause(), e);
            throw new BeneficioControllerException(e);
        }

    }

    @Override
    public List<TransferObject> lstBeneficioByCsaCodigoAndNaturezaServico(String csaCodigo, String corCodigo, String nseCodigo, AcessoSistema responsavel) throws BeneficioControllerException {

        try {

            ListarBeneficioByCsaCodigoAndNaturezaServicoQuery query = new ListarBeneficioByCsaCodigoAndNaturezaServicoQuery();
            query.csaCodigo = csaCodigo;
            query.corCodigo = corCodigo;
            query.nseCodigo = nseCodigo;

            List<TransferObject> resultados = query.executarDTO();

            return resultados;

        } catch (Exception e) {
            LOG.error(e.getCause(), e);
            throw new BeneficioControllerException(e);
        }

    }

    private void criaRelacionamento(Beneficio beneficio, BeneficioServico relacionamentoServicoBeneficio, Short bseOrdem) throws BeneficioControllerException {
        try {
            relacionamentoServicoBeneficio.setBseOrdem(bseOrdem);
            BeneficioServicoId id = new BeneficioServicoId();
            id.setBenCodigo(beneficio.getBenCodigo());
            id.setSvcCodigo(relacionamentoServicoBeneficio.getServico().getSvcCodigo());
            id.setTibCodigo(relacionamentoServicoBeneficio.getTipoBeneficiario().getTibCodigo());
            relacionamentoServicoBeneficio.setBeneficio(beneficio);
            relacionamentoServicoBeneficio.setId(id);
            RelacionamentoBeneficioServicoHome.createByObject(relacionamentoServicoBeneficio);

        } catch (Exception ex) {
            throw new BeneficioControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> findRelacaoBeneficioObitoDependente(CustomTransferObject criterio, AcessoSistema responsavel) throws BeneficioControllerException {
        try {
            ListarRelacaoBeneficiosObitoQuery query = new ListarRelacaoBeneficiosObitoQuery();

            if (criterio != null) {
                if ((String) criterio.getAttribute(Columns.SER_CODIGO) != null) {
                    query.serCodigo = (String) criterio.getAttribute(Columns.SER_CODIGO);
                } else {
                    query.rseCodigo = (String) criterio.getAttribute(Columns.RSE_CODIGO);
                }
                if ((String) criterio.getAttribute(Columns.BFC_CODIGO) != null) {
                    query.bfcCodigo = (String) criterio.getAttribute(Columns.BFC_CODIGO);
                }
            }

            return query.executarDTO();
        } catch (Exception e) {
            throw new BeneficioControllerException(e);
        }
    }

    @Override
    public void bloqueioBeneficio(Beneficio beneficio, AcessoSistema responsavel) throws BeneficioControllerException {
        try {
            if (beneficio == null || TextHelper.isNull(beneficio.getBenCodigo())) {
                throw new BeneficioControllerException("mensagem.erro.nao.encontrado.beneficio.bloqueio.desbloqueio", responsavel);
            }

            beneficio.setBenAtivo(CodedValues.STS_INATIVO);
            update(beneficio, responsavel);

        } catch (BeneficioControllerException e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(e.getMessage(), e);
            throw new BeneficioControllerException("mensagem.erro.nao.possivel.bloquear.beneficio", responsavel);
        }
    }

    @Override
    public void desbloqueioBeneficio(Beneficio beneficio, AcessoSistema responsavel) throws BeneficioControllerException {
        try {
            if (beneficio == null || TextHelper.isNull(beneficio.getBenCodigo())) {
                throw new BeneficioControllerException("mensagem.erro.nao.encontrado.beneficio.bloqueio.desbloqueio", responsavel);
            }

            beneficio.setBenAtivo(CodedValues.STS_ATIVO);
            update(beneficio, responsavel);

        } catch (BeneficioControllerException e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(e.getMessage(), e);
            throw new BeneficioControllerException("mensagem.erro.nao.possivel.desbloquear.beneficio", responsavel);
        }
    }

    @Override
    public List<TransferObject> lstBeneficioByCsaCodigoAndNaturezaServicoCorrespondentes(String csaCodigo, List<String> corCodigos, String nseCodigo, AcessoSistema responsavel) throws BeneficioControllerException {
        try {
            ListarBeneficioByCsaCodigoAndCorCodigosAndNaturezaServicoQuery query = new ListarBeneficioByCsaCodigoAndCorCodigosAndNaturezaServicoQuery();
            query.csaCodigo = csaCodigo;
            query.corCodigos = corCodigos;
            query.nseCodigo = nseCodigo;

            return query.executarDTO();
        } catch (Exception e) {
            LOG.error(e.getCause(), e);
            throw new BeneficioControllerException(e);
        }

    }

}

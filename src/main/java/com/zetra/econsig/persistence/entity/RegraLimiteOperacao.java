package com.zetra.econsig.persistence.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "tb_regra_limite_operacao")
public class RegraLimiteOperacao implements Serializable {

    private static final long serialVersionUID = 1L;

    //--- ENTITY PRIMARY KEY
    @Id
    @Column(name = "rlo_codigo", nullable = false, length = 32)
    private String rloCodigo;

    @Column(name = "usu_codigo", nullable = false, length = 32)
    private String usuCodigo;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "rlo_data_cadastro", nullable = false)
    private Date rloDataCadastro;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "rlo_data_vigencia_ini", nullable = false)
    private Date rloDataVigenciaIni;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "rlo_data_vigencia_fim")
    private Date rloDataVigenciaFim;

    @Column(name = "rlo_faixa_etaria_ini")
    private Short rloFaixaEtariaIni;

    @Column(name = "rlo_faixa_etaria_fim")
    private Short rloFaixaEtariaFim;

    @Column(name = "rlo_faixa_tempo_servico_ini")
    private Short rloFaixaTempoServicoIni;

    @Column(name = "rlo_faixa_tempo_servico_fim")
    private Short rloFaixaTempoServicoFim;

    @Column(name = "rlo_faixa_salario_ini")
    private BigDecimal rloFaixaSalarioIni;

    @Column(name = "rlo_faixa_salario_fim")
    private BigDecimal rloFaixaSalarioFim;

    @Column(name = "rlo_faixa_margem_folha_ini")
    private BigDecimal rloFaixaMargemFolhaIni;

    @Column(name = "rlo_faixa_margem_folha_fim")
    private BigDecimal rloFaixaMargemFolhaFim;

    @Column(name = "rlo_padrao_matricula", length = 65535)
    private String rloPadraoMatricula;

    @Column(name = "rlo_padrao_categoria", length = 65535)
    private String rloPadraoCategoria;

    @Column(name = "rlo_padrao_verba", length = 65535)
    private String rloPadraoVerba;

    @Column(name = "rlo_padrao_verba_ref", length = 65535)
    private String rloPadraoVerbaRef;

    @Column(name = "rlo_mensagem_erro", length = 65535)
    private String rloMensagemErro;

    @Column(name = "rlo_limite_quantidade")
    private Short rloLimiteQuantidade;

    @Temporal(TemporalType.DATE)
    @Column(name = "rlo_limite_data_fim_ade")
    private Date rloLimiteDataFimAde;

    @Column(name = "rlo_limite_prazo")
    private Short rloLimitePrazo;

    @Column(name = "rlo_limite_valor_parcela")
    private BigDecimal rloLimiteValorParcela;

    @Column(name = "rlo_limite_valor_liberado")
    private BigDecimal rloLimiteValorLiberado;

    @Column(name = "rlo_limite_capital_devido")
    private BigDecimal rloLimiteCapitalDevido;

    @Column(name = "est_codigo", nullable = false, length = 32)
    private String estCodigo;

    @Column(name = "org_codigo", nullable = false, length = 32)
    private String orgCodigo;

    @Column(name = "sbo_codigo", nullable = false, length = 32)
    private String sboCodigo;

    @Column(name = "uni_codigo", nullable = false, length = 32)
    private String uniCodigo;

    @Column(name = "svc_codigo", nullable = false, length = 32)
    private String svcCodigo;

    @Column(name = "nse_codigo", nullable = false, length = 32)
    private String nseCodigo;

    @Column(name = "nca_codigo", nullable = false, length = 32)
    private String ncaCodigo;

    @Column(name = "csa_codigo", nullable = false, length = 32)
    private String csaCodigo;

    @Column(name = "cor_codigo", nullable = false, length = 32)
    private String corCodigo;

    @Column(name = "crs_codigo", nullable = false, length = 32)
    private String crsCodigo;

    @Column(name = "cap_codigo", nullable = false, length = 32)
    private String capCodigo;

    @Column(name = "prs_codigo", nullable = false, length = 32)
    private String prsCodigo;

    @Column(name = "pos_codigo", nullable = false, length = 32)
    private String posCodigo;

    @Column(name = "srs_codigo", nullable = false, length = 32)
    private String srsCodigo;

    @Column(name = "trs_codigo", nullable = false, length = 32)
    private String trsCodigo;

    @Column(name = "vrs_codigo", nullable = false, length = 32)
    private String vrsCodigo;

    @Column(name = "fun_codigo", nullable = false, length = 32)
    private String funCodigo;

    //--- ENTITY LINKS ( RELATIONSHIP )
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "est_codigo", referencedColumnName = "est_codigo", insertable = false, updatable = false)
    private Estabelecimento estabelecimento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_codigo", referencedColumnName = "org_codigo", insertable = false, updatable = false)
    private Orgao orgao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sbo_codigo", referencedColumnName = "sbo_codigo", insertable = false, updatable = false)
    private SubOrgao subOrgao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uni_codigo", referencedColumnName = "uni_codigo", insertable = false, updatable = false)
    private Unidade unidade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "svc_codigo", referencedColumnName = "svc_codigo", insertable = false, updatable = false)
    private Servico servico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nse_codigo", referencedColumnName = "nse_codigo", insertable = false, updatable = false)
    private NaturezaServico naturezaServico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nca_codigo", referencedColumnName = "nca_codigo", insertable = false, updatable = false)
    private NaturezaConsignataria naturezaConsignataria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "csa_codigo", referencedColumnName = "csa_codigo", insertable = false, updatable = false)
    private Consignataria consignataria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cor_codigo", referencedColumnName = "cor_codigo", insertable = false, updatable = false)
    private Correspondente correspondente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crs_codigo", referencedColumnName = "crs_codigo", insertable = false, updatable = false)
    private CargoRegistroServidor cargoRegistroServidor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cap_codigo", referencedColumnName = "cap_codigo", insertable = false, updatable = false)
    private CapacidadeRegistroSer capacidadeRegistroSer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prs_codigo", referencedColumnName = "prs_codigo", insertable = false, updatable = false)
    private PadraoRegistroServidor padraoRegistroServidor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pos_codigo", referencedColumnName = "pos_codigo", insertable = false, updatable = false)
    private PostoRegistroServidor postoRegistroServidor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "srs_codigo", referencedColumnName = "srs_codigo", insertable = false, updatable = false)
    private StatusRegistroServidor statusRegistroServidor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trs_codigo", referencedColumnName = "trs_codigo", insertable = false, updatable = false)
    private TipoRegistroServidor tipoRegistroServidor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vrs_codigo", referencedColumnName = "vrs_codigo", insertable = false, updatable = false)
    private VinculoRegistroServidor vinculoRegistroServidor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fun_codigo", referencedColumnName = "fun_codigo", insertable = false, updatable = false)
    private Funcao funcao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usu_codigo", referencedColumnName = "usu_codigo", insertable = false, updatable = false)
    private Usuario usuario;

    /**
     * Constructor
     */
    public RegraLimiteOperacao() {
        super();
    }

    //--- GETTERS & SETTERS FOR FIELDS
    public String getRloCodigo() {
        return rloCodigo;
    }

    public void setRloCodigo(String rloCodigo) {
        this.rloCodigo = rloCodigo;
    }

    public Date getRloDataCadastro() {
        return rloDataCadastro;
    }

    public void setRloDataCadastro(Date rloDataCadastro) {
        this.rloDataCadastro = rloDataCadastro;
    }

    public Date getRloDataVigenciaIni() {
        return rloDataVigenciaIni;
    }

    public void setRloDataVigenciaIni(Date rloDataVigenciaIni) {
        this.rloDataVigenciaIni = rloDataVigenciaIni;
    }

    public Date getRloDataVigenciaFim() {
        return rloDataVigenciaFim;
    }

    public void setRloDataVigenciaFim(Date rloDataVigenciaFim) {
        this.rloDataVigenciaFim = rloDataVigenciaFim;
    }

    public Short getRloFaixaEtariaIni() {
        return rloFaixaEtariaIni;
    }

    public void setRloFaixaEtariaIni(Short rloFaixaEtariaIni) {
        this.rloFaixaEtariaIni = rloFaixaEtariaIni;
    }

    public Short getRloFaixaEtariaFim() {
        return rloFaixaEtariaFim;
    }

    public void setRloFaixaEtariaFim(Short rloFaixaEtariaFim) {
        this.rloFaixaEtariaFim = rloFaixaEtariaFim;
    }

    public Short getRloFaixaTempoServicoIni() {
        return rloFaixaTempoServicoIni;
    }

    public void setRloFaixaTempoServicoIni(Short rloFaixaTempoServicoIni) {
        this.rloFaixaTempoServicoIni = rloFaixaTempoServicoIni;
    }

    public Short getRloFaixaTempoServicoFim() {
        return rloFaixaTempoServicoFim;
    }

    public void setRloFaixaTempoServicoFim(Short rloFaixaTempoServicoFim) {
        this.rloFaixaTempoServicoFim = rloFaixaTempoServicoFim;
    }

    public BigDecimal getRloFaixaSalarioIni() {
        return rloFaixaSalarioIni;
    }

    public void setRloFaixaSalarioIni(BigDecimal rloFaixaSalarioIni) {
        this.rloFaixaSalarioIni = rloFaixaSalarioIni;
    }

    public BigDecimal getRloFaixaSalarioFim() {
        return rloFaixaSalarioFim;
    }

    public void setRloFaixaSalarioFim(BigDecimal rloFaixaSalarioFim) {
        this.rloFaixaSalarioFim = rloFaixaSalarioFim;
    }

    public BigDecimal getRloFaixaMargemFolhaIni() {
        return rloFaixaMargemFolhaIni;
    }

    public void setRloFaixaMargemFolhaIni(BigDecimal rloFaixaMargemFolhaIni) {
        this.rloFaixaMargemFolhaIni = rloFaixaMargemFolhaIni;
    }

    public BigDecimal getRloFaixaMargemFolhaFim() {
        return rloFaixaMargemFolhaFim;
    }

    public void setRloFaixaMargemFolhaFim(BigDecimal rloFaixaMargemFolhaFim) {
        this.rloFaixaMargemFolhaFim = rloFaixaMargemFolhaFim;
    }

    public String getRloPadraoMatricula() {
        return rloPadraoMatricula;
    }

    public void setRloPadraoMatricula(String rloPadraoMatricula) {
        this.rloPadraoMatricula = rloPadraoMatricula;
    }

    public String getRloPadraoCategoria() {
        return rloPadraoCategoria;
    }

    public void setRloPadraoCategoria(String rloPadraoCategoria) {
        this.rloPadraoCategoria = rloPadraoCategoria;
    }

    public String getRloPadraoVerba() {
        return rloPadraoVerba;
    }

    public void setRloPadraoVerba(String rloPadraoVerba) {
        this.rloPadraoVerba = rloPadraoVerba;
    }

    public String getRloPadraoVerbaRef() {
        return rloPadraoVerbaRef;
    }

    public void setRloPadraoVerbaRef(String rloPadraoVerbaRef) {
        this.rloPadraoVerbaRef = rloPadraoVerbaRef;
    }

    public String getRloMensagemErro() {
        return rloMensagemErro;
    }

    public void setRloMensagemErro(String rloMensagemErro) {
        this.rloMensagemErro = rloMensagemErro;
    }

    public Short getRloLimiteQuantidade() {
        return rloLimiteQuantidade;
    }

    public void setRloLimiteQuantidade(Short rloLimiteQuantidade) {
        this.rloLimiteQuantidade = rloLimiteQuantidade;
    }

    public Date getRloLimiteDataFimAde() {
        return rloLimiteDataFimAde;
    }

    public void setRloLimiteDataFimAde(Date rloLimiteDataFimAde) {
        this.rloLimiteDataFimAde = rloLimiteDataFimAde;
    }

    public Short getRloLimitePrazo() {
        return rloLimitePrazo;
    }

    public void setRloLimitePrazo(Short rloLimitePrazo) {
        this.rloLimitePrazo = rloLimitePrazo;
    }

    public BigDecimal getRloLimiteValorParcela() {
        return rloLimiteValorParcela;
    }

    public void setRloLimiteValorParcela(BigDecimal rloLimiteValorParcela) {
        this.rloLimiteValorParcela = rloLimiteValorParcela;
    }

    public BigDecimal getRloLimiteValorLiberado() {
        return rloLimiteValorLiberado;
    }

    public void setRloLimiteValorLiberado(BigDecimal rloLimiteValorLiberado) {
        this.rloLimiteValorLiberado = rloLimiteValorLiberado;
    }

    public BigDecimal getRloLimiteCapitalDevido() {
        return rloLimiteCapitalDevido;
    }

    public void setRloLimiteCapitalDevido(BigDecimal rloLimiteCapitalDevido) {
        this.rloLimiteCapitalDevido = rloLimiteCapitalDevido;
    }

    public String getEstCodigo() {
        return estCodigo;
    }

    public void setEstCodigo(String estCodigo) {
        this.estCodigo = estCodigo;
    }

    public String getOrgCodigo() {
        return orgCodigo;
    }

    public void setOrgCodigo(String orgCodigo) {
        this.orgCodigo = orgCodigo;
    }

    public String getSboCodigo() {
        return sboCodigo;
    }

    public void setSboCodigo(String sboCodigo) {
        this.sboCodigo = sboCodigo;
    }

    public String getUniCodigo() {
        return uniCodigo;
    }

    public void setUniCodigo(String uniCodigo) {
        this.uniCodigo = uniCodigo;
    }

    public String getSvcCodigo() {
        return svcCodigo;
    }

    public void setSvcCodigo(String svcCodigo) {
        this.svcCodigo = svcCodigo;
    }

    public String getNseCodigo() {
        return nseCodigo;
    }

    public void setNseCodigo(String nseCodigo) {
        this.nseCodigo = nseCodigo;
    }

    public String getNcaCodigo() {
        return ncaCodigo;
    }

    public void setNcaCodigo(String ncaCodigo) {
        this.ncaCodigo = ncaCodigo;
    }

    public String getCsaCodigo() {
        return csaCodigo;
    }

    public void setCsaCodigo(String csaCodigo) {
        this.csaCodigo = csaCodigo;
    }

    public String getCorCodigo() {
        return corCodigo;
    }

    public void setCorCodigo(String corCodigo) {
        this.corCodigo = corCodigo;
    }

    public String getCrsCodigo() {
        return crsCodigo;
    }

    public void setCrsCodigo(String crsCodigo) {
        this.crsCodigo = crsCodigo;
    }

    public String getCapCodigo() {
        return capCodigo;
    }

    public void setCapCodigo(String capCodigo) {
        this.capCodigo = capCodigo;
    }

    public String getPrsCodigo() {
        return prsCodigo;
    }

    public void setPrsCodigo(String prsCodigo) {
        this.prsCodigo = prsCodigo;
    }

    public String getPosCodigo() {
        return posCodigo;
    }

    public void setPosCodigo(String posCodigo) {
        this.posCodigo = posCodigo;
    }

    public String getSrsCodigo() {
        return srsCodigo;
    }

    public void setSrsCodigo(String srsCodigo) {
        this.srsCodigo = srsCodigo;
    }

    public String getTrsCodigo() {
        return trsCodigo;
    }

    public void setTrsCodigo(String trsCodigo) {
        this.trsCodigo = trsCodigo;
    }

    public String getVrsCodigo() {
        return vrsCodigo;
    }

    public void setVrsCodigo(String vrsCodigo) {
        this.vrsCodigo = vrsCodigo;
    }

    public String getFunCodigo() {
        return funCodigo;
    }

    public void setFunCodigo(String funCodigo) {
        this.funCodigo = funCodigo;
    }

    //--- GETTERS & SETTERS FOR RELATIONSHIPS
    public Estabelecimento getEstabelecimento() {
        return estabelecimento;
    }

    public void setEstabelecimento(Estabelecimento estabelecimento) {
        this.estabelecimento = estabelecimento;
        setEstCodigo(estabelecimento != null ? estabelecimento.getEstCodigo() : null);
    }

    public Orgao getOrgao() {
        return orgao;
    }

    public void setOrgao(Orgao orgao) {
        this.orgao = orgao;
        setOrgCodigo(orgao != null ? orgao.getOrgCodigo() : null);
    }

    public SubOrgao getSubOrgao() {
        return subOrgao;
    }

    public void setSubOrgao(SubOrgao subOrgao) {
        this.subOrgao = subOrgao;
        setSboCodigo(subOrgao != null ? subOrgao.getSboCodigo() : null);
    }

    public Unidade getUnidade() {
        return unidade;
    }

    public void setUnidade(Unidade unidade) {
        this.unidade = unidade;
        setUniCodigo(unidade != null ? unidade.getUniCodigo() : null);
    }

    public Servico getServico() {
        return servico;
    }

    public void setServico(Servico servico) {
        this.servico = servico;
        setSvcCodigo(servico != null ? servico.getSvcCodigo() : null);
    }

    public NaturezaServico getNaturezaServico() {
        return naturezaServico;
    }

    public void setNaturezaServico(NaturezaServico naturezaServico) {
        this.naturezaServico = naturezaServico;
        setNseCodigo(naturezaServico != null ? naturezaServico.getNseCodigo() : null);
    }

    public NaturezaConsignataria getNaturezaConsignataria() {
        return naturezaConsignataria;
    }

    public void setNaturezaConsignataria(NaturezaConsignataria naturezaConsignataria) {
        this.naturezaConsignataria = naturezaConsignataria;
        setNcaCodigo(naturezaConsignataria != null ? naturezaConsignataria.getNcaCodigo() : null);
    }

    public Consignataria getConsignataria() {
        return consignataria;
    }

    public void setConsignataria(Consignataria consignataria) {
        this.consignataria = consignataria;
        setCsaCodigo(consignataria != null ? consignataria.getCsaCodigo() : null);
    }

    public Correspondente getCorrespondente() {
        return correspondente;
    }

    public void setCorrespondente(Correspondente correspondente) {
        this.correspondente = correspondente;
        setCorCodigo(correspondente != null ? correspondente.getCorCodigo() : null);
    }

    public CargoRegistroServidor getCargoRegistroServidor() {
        return cargoRegistroServidor;
    }

    public void setCargoRegistroServidor(CargoRegistroServidor cargoRegistroServidor) {
        this.cargoRegistroServidor = cargoRegistroServidor;
        setCrsCodigo(cargoRegistroServidor != null ? cargoRegistroServidor.getCrsCodigo() : null);
    }

    public CapacidadeRegistroSer getCapacidadeRegistroSer() {
        return capacidadeRegistroSer;
    }

    public void setCapacidadeRegistroSer(CapacidadeRegistroSer capacidadeRegistroSer) {
        this.capacidadeRegistroSer = capacidadeRegistroSer;
        setCapCodigo(capacidadeRegistroSer != null ? capacidadeRegistroSer.getCapCodigo() : null);
    }

    public PadraoRegistroServidor getPadraoRegistroServidor() {
        return padraoRegistroServidor;
    }

    public void setPadraoRegistroServidor(PadraoRegistroServidor padraoRegistroServidor) {
        this.padraoRegistroServidor = padraoRegistroServidor;
        setPrsCodigo(padraoRegistroServidor != null ? padraoRegistroServidor.getPrsCodigo() : null);
    }

    public PostoRegistroServidor getPostoRegistroServidor() {
        return postoRegistroServidor;
    }

    public void setPostoRegistroServidor(PostoRegistroServidor postoRegistroServidor) {
        this.postoRegistroServidor = postoRegistroServidor;
        setPosCodigo(postoRegistroServidor != null ? postoRegistroServidor.getPosCodigo() : null);
    }

    public StatusRegistroServidor getStatusRegistroServidor() {
        return statusRegistroServidor;
    }

    public void setStatusRegistroServidor(StatusRegistroServidor statusRegistroServidor) {
        this.statusRegistroServidor = statusRegistroServidor;
        setSrsCodigo(statusRegistroServidor != null ? statusRegistroServidor.getSrsCodigo() : null);
    }

    public TipoRegistroServidor getTipoRegistroServidor() {
        return tipoRegistroServidor;
    }

    public void setTipoRegistroServidor(TipoRegistroServidor tipoRegistroServidor) {
        this.tipoRegistroServidor = tipoRegistroServidor;
        setTrsCodigo(tipoRegistroServidor != null ? tipoRegistroServidor.getTrsCodigo() : null);
    }

    public VinculoRegistroServidor getVinculoRegistroServidor() {
        return vinculoRegistroServidor;
    }

    public void setVinculoRegistroServidor(VinculoRegistroServidor vinculoRegistroServidor) {
        this.vinculoRegistroServidor = vinculoRegistroServidor;
        setVrsCodigo(vinculoRegistroServidor != null ? vinculoRegistroServidor.getVrsCodigo() : null);
    }

    public Funcao getFuncao() {
        return funcao;
    }

    public void setFuncao(Funcao funcao) {
        this.funcao = funcao;
        setFunCodigo(funcao != null ? funcao.getFunCodigo() : null);
    }

    public String getUsuCodigo() {
        return usuCodigo;
    }

    public void setUsuCodigo(String usuCodigo) {
        this.usuCodigo = usuCodigo;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

}

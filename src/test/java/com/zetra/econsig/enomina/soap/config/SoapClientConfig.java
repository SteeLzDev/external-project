package com.zetra.econsig.enomina.soap.config;

import static com.zetra.econsig.EConsigInitializer.getBaseURL;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import com.zetra.econsig.enomina.soap.client.AcompanharCompraContratoClient;
import com.zetra.econsig.enomina.soap.client.AlongarConsignacaoClient;
import com.zetra.econsig.enomina.soap.client.AlterarConsignacaoClient;
import com.zetra.econsig.enomina.soap.client.AutorizarReservaClient;
import com.zetra.econsig.enomina.soap.client.CancelarCompraClient;
import com.zetra.econsig.enomina.soap.client.CancelarConsignacaoClient;
import com.zetra.econsig.enomina.soap.client.CancelarRenegociacaoClient;
import com.zetra.econsig.enomina.soap.client.CancelarReservaClient;
import com.zetra.econsig.enomina.soap.client.ComprarContratoClient;
import com.zetra.econsig.enomina.soap.client.ConfirmarReservaClient;
import com.zetra.econsig.enomina.soap.client.ConfirmarSolicitacaoClient;
import com.zetra.econsig.enomina.soap.client.ConsultarConsignacaoClient;
import com.zetra.econsig.enomina.soap.client.ConsultarConsignacaoParaCompraClient;
import com.zetra.econsig.enomina.soap.client.ConsultarMargemClient;
import com.zetra.econsig.enomina.soap.client.ConsultarParametrosClient;
import com.zetra.econsig.enomina.soap.client.DetalharConsultaConsignacaoClient;
import com.zetra.econsig.enomina.soap.client.IncluirAnexoConsignacaoClient;
import com.zetra.econsig.enomina.soap.client.IncluirDadoConsignacaoClient;
import com.zetra.econsig.enomina.soap.client.InformarPagamentoSaldoDevedorClient;
import com.zetra.econsig.enomina.soap.client.InformarSaldoDevedorClient;
import com.zetra.econsig.enomina.soap.client.InserirSolicitacaoClient;
import com.zetra.econsig.enomina.soap.client.LiquidarCompraClient;
import com.zetra.econsig.enomina.soap.client.LiquidarConsignacaoClient;
import com.zetra.econsig.enomina.soap.client.ListarDadoConsignacaoClient;
import com.zetra.econsig.enomina.soap.client.ListarParcelasClient;
import com.zetra.econsig.enomina.soap.client.ListarSolicitacaoClient;
import com.zetra.econsig.enomina.soap.client.ReativarConsignacaoClient;
import com.zetra.econsig.enomina.soap.client.RejeitarPgSaldoDevedorClient;
import com.zetra.econsig.enomina.soap.client.RenegociarConsignacaoClient;
import com.zetra.econsig.enomina.soap.client.ReservarMargemClient;
import com.zetra.econsig.enomina.soap.client.RetirarContratoDaCompraClient;
import com.zetra.econsig.enomina.soap.client.SimularConsignacaoClient;
import com.zetra.econsig.enomina.soap.client.SolicitarRecalculoSaldoDevedorClient;
import com.zetra.econsig.enomina.soap.client.SuspenderConsignacaoClient;

@Configuration
public class SoapClientConfig {

    @Bean
    public Jaxb2Marshaller marshaller() {
        final Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        jaxb2Marshaller.setContextPaths(
                                        "com.zetra.econsig.soap",
                                        "com.zetra.econsig.soap.compra");

        return jaxb2Marshaller;
    }

    @Bean
    public InserirSolicitacaoClient inserirSolicitacaoClient(Jaxb2Marshaller jaxb2Marshaller) {
        final InserirSolicitacaoClient inserirSolicitacaoClient = new InserirSolicitacaoClient();
        inserirSolicitacaoClient.setDefaultUri(getOperacionalURL());
        inserirSolicitacaoClient.setMarshaller(jaxb2Marshaller);
        inserirSolicitacaoClient.setUnmarshaller(jaxb2Marshaller);

        return inserirSolicitacaoClient;
    }

    @Bean
    public ConsultarMargemClient consultarMargemClientClient(Jaxb2Marshaller jaxb2Marshaller) {
        final ConsultarMargemClient consultarMargemClientClient = new ConsultarMargemClient();
        consultarMargemClientClient.setDefaultUri(getOperacionalURL());
        consultarMargemClientClient.setMarshaller(jaxb2Marshaller);
        consultarMargemClientClient.setUnmarshaller(jaxb2Marshaller);

        return consultarMargemClientClient;
    }

    @Bean
    public ConfirmarSolicitacaoClient confirmarSolicitacaoClient(Jaxb2Marshaller jaxb2Marshaller) {
        final ConfirmarSolicitacaoClient confirmarSolicitacaoClient = new ConfirmarSolicitacaoClient();
        confirmarSolicitacaoClient.setDefaultUri(getOperacionalURL());
        confirmarSolicitacaoClient.setMarshaller(jaxb2Marshaller);
        confirmarSolicitacaoClient.setUnmarshaller(jaxb2Marshaller);

        return confirmarSolicitacaoClient;
    }

    @Bean
    public AutorizarReservaClient autorizarReservaClient(Jaxb2Marshaller jaxb2Marshaller) {
        final AutorizarReservaClient autorizarReservaClient = new AutorizarReservaClient();
        autorizarReservaClient.setDefaultUri(getOperacionalURL());
        autorizarReservaClient.setMarshaller(jaxb2Marshaller);
        autorizarReservaClient.setUnmarshaller(jaxb2Marshaller);

        return autorizarReservaClient;
    }

    @Bean
    public ConfirmarReservaClient confirmarReservaClient(Jaxb2Marshaller jaxb2Marshaller) {
        final ConfirmarReservaClient confirmarReservaClient = new ConfirmarReservaClient();
        confirmarReservaClient.setDefaultUri(getOperacionalURL());
        confirmarReservaClient.setMarshaller(jaxb2Marshaller);
        confirmarReservaClient.setUnmarshaller(jaxb2Marshaller);

        return confirmarReservaClient;
    }

    @Bean
    public CancelarReservaClient cancelarReservaClient(Jaxb2Marshaller jaxb2Marshaller) {
        final CancelarReservaClient cancelarReservaClient = new CancelarReservaClient();
        cancelarReservaClient.setDefaultUri(getOperacionalURL());
        cancelarReservaClient.setMarshaller(jaxb2Marshaller);
        cancelarReservaClient.setUnmarshaller(jaxb2Marshaller);

        return cancelarReservaClient;
    }

    @Bean
    public IncluirAnexoConsignacaoClient incluirAnexoConsignacaoClient(Jaxb2Marshaller jaxb2Marshaller) {
        final IncluirAnexoConsignacaoClient incluirAnexoConsignacaoClient = new IncluirAnexoConsignacaoClient();
        incluirAnexoConsignacaoClient.setDefaultUri(getOperacionalURL());
        incluirAnexoConsignacaoClient.setMarshaller(jaxb2Marshaller);
        incluirAnexoConsignacaoClient.setUnmarshaller(jaxb2Marshaller);

        return incluirAnexoConsignacaoClient;
    }

    @Bean
    public ReservarMargemClient reservarMargemClient(Jaxb2Marshaller jaxb2Marshaller) {
        final ReservarMargemClient ReservarMargemClient = new ReservarMargemClient();
        ReservarMargemClient.setDefaultUri(getOperacionalURL());
        ReservarMargemClient.setMarshaller(jaxb2Marshaller);
        ReservarMargemClient.setUnmarshaller(jaxb2Marshaller);

        return ReservarMargemClient;
    }

    @Bean
    public IncluirDadoConsignacaoClient incluirDadosConsignacaoClient(Jaxb2Marshaller jaxb2Marshaller) {
        final IncluirDadoConsignacaoClient incluirDadoConsignacaoClient = new IncluirDadoConsignacaoClient();
        incluirDadoConsignacaoClient.setDefaultUri(getOperacionalURL());
        incluirDadoConsignacaoClient.setMarshaller(jaxb2Marshaller);
        incluirDadoConsignacaoClient.setUnmarshaller(jaxb2Marshaller);

        return incluirDadoConsignacaoClient;
    }

    @Bean
    public ConsultarConsignacaoParaCompraClient consultarConsignacaoParaCompraClient(Jaxb2Marshaller jaxb2Marshaller) {
        final ConsultarConsignacaoParaCompraClient consultarConsignacaoParaCompraClient = new ConsultarConsignacaoParaCompraClient();
        consultarConsignacaoParaCompraClient.setDefaultUri(getCompraURL());
        consultarConsignacaoParaCompraClient.setMarshaller(jaxb2Marshaller);
        consultarConsignacaoParaCompraClient.setUnmarshaller(jaxb2Marshaller);

        return consultarConsignacaoParaCompraClient;
    }

    @Bean
    public SimularConsignacaoClient simularConsignacaoClient(Jaxb2Marshaller jaxb2Marshaller) {
        final SimularConsignacaoClient simularConsignacaoClient = new SimularConsignacaoClient();
        simularConsignacaoClient.setDefaultUri(getOperacionalURL());
        simularConsignacaoClient.setMarshaller(jaxb2Marshaller);
        simularConsignacaoClient.setUnmarshaller(jaxb2Marshaller);

        return simularConsignacaoClient;
    }

    @Bean
    public AlterarConsignacaoClient alterarConsignacaoClient(Jaxb2Marshaller jaxb2Marshaller) {
        final AlterarConsignacaoClient alterarConsignacaoClient = new AlterarConsignacaoClient();
        alterarConsignacaoClient.setDefaultUri(getOperacionalURL());
        alterarConsignacaoClient.setMarshaller(jaxb2Marshaller);
        alterarConsignacaoClient.setUnmarshaller(jaxb2Marshaller);

        return alterarConsignacaoClient;
    }

    @Bean
    public CancelarConsignacaoClient cancelarConsignacaoClient(Jaxb2Marshaller jaxb2Marshaller) {
        final CancelarConsignacaoClient cancelarConsignacaoClient = new CancelarConsignacaoClient();
        cancelarConsignacaoClient.setDefaultUri(getOperacionalURL());
        cancelarConsignacaoClient.setMarshaller(jaxb2Marshaller);
        cancelarConsignacaoClient.setUnmarshaller(jaxb2Marshaller);

        return cancelarConsignacaoClient;
    }

    @Bean
    public ConsultarConsignacaoClient consultarConsignacaoClient(Jaxb2Marshaller jaxb2Marshaller) {
        final ConsultarConsignacaoClient consultarConsignacaoClient = new ConsultarConsignacaoClient();
        consultarConsignacaoClient.setDefaultUri(getOperacionalURL());
        consultarConsignacaoClient.setMarshaller(jaxb2Marshaller);
        consultarConsignacaoClient.setUnmarshaller(jaxb2Marshaller);

        return consultarConsignacaoClient;
    }

    @Bean
    public ListarSolicitacaoClient listarSolicitacaoClient(Jaxb2Marshaller jaxb2Marshaller) {
        final ListarSolicitacaoClient listarSolicitacaoClient = new ListarSolicitacaoClient();
        listarSolicitacaoClient.setDefaultUri(getOperacionalURL());
        listarSolicitacaoClient.setMarshaller(jaxb2Marshaller);
        listarSolicitacaoClient.setUnmarshaller(jaxb2Marshaller);

        return listarSolicitacaoClient;
    }

    @Bean
    public LiquidarConsignacaoClient liquidarConsignacaoClient(Jaxb2Marshaller jaxb2Marshaller) {
        final LiquidarConsignacaoClient liquidarConsignacaoClient = new LiquidarConsignacaoClient();
        liquidarConsignacaoClient.setDefaultUri(getOperacionalURL());
        liquidarConsignacaoClient.setMarshaller(jaxb2Marshaller);
        liquidarConsignacaoClient.setUnmarshaller(jaxb2Marshaller);

        return liquidarConsignacaoClient;
    }

    @Bean
    public DetalharConsultaConsignacaoClient detalharConsultaConsignacaoClient(Jaxb2Marshaller jaxb2Marshaller) {
        final DetalharConsultaConsignacaoClient detalharConsultaConsignacaoClient = new DetalharConsultaConsignacaoClient();
        detalharConsultaConsignacaoClient.setDefaultUri(getOperacionalURL());
        detalharConsultaConsignacaoClient.setMarshaller(jaxb2Marshaller);
        detalharConsultaConsignacaoClient.setUnmarshaller(jaxb2Marshaller);

        return detalharConsultaConsignacaoClient;
    }

    @Bean
    public AlongarConsignacaoClient alongarConsignacaoClient(Jaxb2Marshaller jaxb2Marshaller) {
        final AlongarConsignacaoClient alongarConsignacaoClient = new AlongarConsignacaoClient();
        alongarConsignacaoClient.setDefaultUri(getOperacionalURL());
        alongarConsignacaoClient.setMarshaller(jaxb2Marshaller);
        alongarConsignacaoClient.setUnmarshaller(jaxb2Marshaller);

        return alongarConsignacaoClient;
    }

    @Bean
    public ConsultarParametrosClient consultarParametrosClient(Jaxb2Marshaller jaxb2Marshaller) {
        final ConsultarParametrosClient consultarParametrosClient = new ConsultarParametrosClient();
        consultarParametrosClient.setDefaultUri(getOperacionalURL());
        consultarParametrosClient.setMarshaller(jaxb2Marshaller);
        consultarParametrosClient.setUnmarshaller(jaxb2Marshaller);

        return consultarParametrosClient;
    }

    @Bean
    public ComprarContratoClient comprarContratoClient(Jaxb2Marshaller jaxb2Marshaller) {
        final ComprarContratoClient comprarContratoClient = new ComprarContratoClient();
        comprarContratoClient.setDefaultUri(getCompraURL());
        comprarContratoClient.setMarshaller(jaxb2Marshaller);
        comprarContratoClient.setUnmarshaller(jaxb2Marshaller);

        return comprarContratoClient;
    }

    @Bean
    public CancelarCompraClient cancelarCompraClient(Jaxb2Marshaller jaxb2Marshaller) {
        final CancelarCompraClient cancelarCompraClient = new CancelarCompraClient();
        cancelarCompraClient.setDefaultUri(getCompraURL());
        cancelarCompraClient.setMarshaller(jaxb2Marshaller);
        cancelarCompraClient.setUnmarshaller(jaxb2Marshaller);

        return cancelarCompraClient;
    }

    @Bean
    public InformarSaldoDevedorClient informarSaldoDevedorClient(Jaxb2Marshaller jaxb2Marshaller) {
        final InformarSaldoDevedorClient informarSaldoDevedorClient = new InformarSaldoDevedorClient();
        informarSaldoDevedorClient.setDefaultUri(getCompraURL());
        informarSaldoDevedorClient.setMarshaller(jaxb2Marshaller);
        informarSaldoDevedorClient.setUnmarshaller(jaxb2Marshaller);

        return informarSaldoDevedorClient;
    }

    @Bean
    public InformarPagamentoSaldoDevedorClient informarPagamentoSaldoDevedorClient(Jaxb2Marshaller jaxb2Marshaller) {
        final InformarPagamentoSaldoDevedorClient informarPagamentoSaldoDevedorClient = new InformarPagamentoSaldoDevedorClient();
        informarPagamentoSaldoDevedorClient.setDefaultUri(getCompraURL());
        informarPagamentoSaldoDevedorClient.setMarshaller(jaxb2Marshaller);
        informarPagamentoSaldoDevedorClient.setUnmarshaller(jaxb2Marshaller);

        return informarPagamentoSaldoDevedorClient;
    }

    @Bean
    public RejeitarPgSaldoDevedorClient rejeitarPgSaldoDevedorClient(Jaxb2Marshaller jaxb2Marshaller) {
        final RejeitarPgSaldoDevedorClient rejeitarPgSaldoDevedorClient = new RejeitarPgSaldoDevedorClient();
        rejeitarPgSaldoDevedorClient.setDefaultUri(getCompraURL());
        rejeitarPgSaldoDevedorClient.setMarshaller(jaxb2Marshaller);
        rejeitarPgSaldoDevedorClient.setUnmarshaller(jaxb2Marshaller);

        return rejeitarPgSaldoDevedorClient;
    }

    @Bean
    public SolicitarRecalculoSaldoDevedorClient solicitarRecalculoSaldoDevedorClient(Jaxb2Marshaller jaxb2Marshaller) {
        final SolicitarRecalculoSaldoDevedorClient solicitarRecalculoSaldoDevedorClient = new SolicitarRecalculoSaldoDevedorClient();
        solicitarRecalculoSaldoDevedorClient.setDefaultUri(getCompraURL());
        solicitarRecalculoSaldoDevedorClient.setMarshaller(jaxb2Marshaller);
        solicitarRecalculoSaldoDevedorClient.setUnmarshaller(jaxb2Marshaller);

        return solicitarRecalculoSaldoDevedorClient;
    }

    @Bean
    public LiquidarCompraClient liquidarCompraClient(Jaxb2Marshaller jaxb2Marshaller) {
        final LiquidarCompraClient liquidarCompraClient = new LiquidarCompraClient();
        liquidarCompraClient.setDefaultUri(getCompraURL());
        liquidarCompraClient.setMarshaller(jaxb2Marshaller);
        liquidarCompraClient.setUnmarshaller(jaxb2Marshaller);

        return liquidarCompraClient;
    }

    @Bean
    public RetirarContratoDaCompraClient retirarContratoDaCompraClient(Jaxb2Marshaller jaxb2Marshaller) {
        final RetirarContratoDaCompraClient retirarContratoDaCompraClient = new RetirarContratoDaCompraClient();
        retirarContratoDaCompraClient.setDefaultUri(getCompraURL());
        retirarContratoDaCompraClient.setMarshaller(jaxb2Marshaller);
        retirarContratoDaCompraClient.setUnmarshaller(jaxb2Marshaller);

        return retirarContratoDaCompraClient;
    }

    @Bean
    public AcompanharCompraContratoClient acompanharCompraContratoClient(Jaxb2Marshaller jaxb2Marshaller) {
        final AcompanharCompraContratoClient acompanharCompraContratoClient = new AcompanharCompraContratoClient();
        acompanharCompraContratoClient.setDefaultUri(getCompraURL());
        acompanharCompraContratoClient.setMarshaller(jaxb2Marshaller);
        acompanharCompraContratoClient.setUnmarshaller(jaxb2Marshaller);

        return acompanharCompraContratoClient;
    }

    @Bean
    public RenegociarConsignacaoClient renegociarConsignacaoClient(Jaxb2Marshaller jaxb2Marshaller) {
        final RenegociarConsignacaoClient renegociarConsignacaoClient = new RenegociarConsignacaoClient();
        renegociarConsignacaoClient.setDefaultUri(getOperacionalURL());
        renegociarConsignacaoClient.setMarshaller(jaxb2Marshaller);
        renegociarConsignacaoClient.setUnmarshaller(jaxb2Marshaller);

        return renegociarConsignacaoClient;
    }

    @Bean
    public ReativarConsignacaoClient reativarConsignacaoClient(Jaxb2Marshaller jaxb2Marshaller) {
        final ReativarConsignacaoClient reativarConsignacaoClient = new ReativarConsignacaoClient();
        reativarConsignacaoClient.setDefaultUri(getOperacionalURL());
        reativarConsignacaoClient.setMarshaller(jaxb2Marshaller);
        reativarConsignacaoClient.setUnmarshaller(jaxb2Marshaller);

        return reativarConsignacaoClient;
    }

    @Bean
    public CancelarRenegociacaoClient cancelarRenegociacaoClient(Jaxb2Marshaller jaxb2Marshaller) {
        final CancelarRenegociacaoClient cancelarRenegociacaoClient = new CancelarRenegociacaoClient();
        cancelarRenegociacaoClient.setDefaultUri(getOperacionalURL());
        cancelarRenegociacaoClient.setMarshaller(jaxb2Marshaller);
        cancelarRenegociacaoClient.setUnmarshaller(jaxb2Marshaller);

        return cancelarRenegociacaoClient;
    }

    @Bean
    public SuspenderConsignacaoClient suspenderConsignacaoClient(Jaxb2Marshaller jaxb2Marshaller) {
        final SuspenderConsignacaoClient suspenderConsignacaoClient = new SuspenderConsignacaoClient();
        suspenderConsignacaoClient.setDefaultUri(getOperacionalURL());
        suspenderConsignacaoClient.setMarshaller(jaxb2Marshaller);
        suspenderConsignacaoClient.setUnmarshaller(jaxb2Marshaller);

        return suspenderConsignacaoClient;
    }

    @Bean
    public ListarDadoConsignacaoClient listarDadoConsignacaoClient(Jaxb2Marshaller jaxb2Marshaller) {
        final ListarDadoConsignacaoClient listarDadoConsignacaoClient = new ListarDadoConsignacaoClient();
        listarDadoConsignacaoClient.setDefaultUri(getOperacionalURL());
        listarDadoConsignacaoClient.setMarshaller(jaxb2Marshaller);
        listarDadoConsignacaoClient.setUnmarshaller(jaxb2Marshaller);

        return listarDadoConsignacaoClient;
    }

    @Bean
    public ListarParcelasClient listarParcelasClient(Jaxb2Marshaller jaxb2Marshaller) {
        final ListarParcelasClient listarParcelasClient = new ListarParcelasClient();
        listarParcelasClient.setDefaultUri(getOperacionalURL("8"));
        listarParcelasClient.setMarshaller(jaxb2Marshaller);
        listarParcelasClient.setUnmarshaller(jaxb2Marshaller);

        return listarParcelasClient;
    }

    private static String getOperacionalURL() {
        return getBaseURL() + "/consig/services/HostaHostService-v7_0";
    }

    private static String getOperacionalURL(String versao) {
        return getBaseURL() + "/consig/services/HostaHostService-v" + versao + "_0";
    }

    private static String getCompraURL() {
        return getBaseURL() + "/consig/services/CompraService";
    }
}

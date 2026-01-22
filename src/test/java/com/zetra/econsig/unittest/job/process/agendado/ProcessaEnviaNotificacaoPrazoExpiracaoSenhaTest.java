package com.zetra.econsig.unittest.job.process.agendado;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.web.SSOToken;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.job.process.agendado.ProcessaEnviaNotificacaoPrazoExpiracaoSenha;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.service.usuario.UsuarioControllerBean;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webclient.sso.response.UserDetailResponse;

public class ProcessaEnviaNotificacaoPrazoExpiracaoSenhaTest {


    private ProcessaEnviaNotificacaoPrazoExpiracaoSenhaMock processo;

    @Before
    public void setup() {
        processo = new ProcessaEnviaNotificacaoPrazoExpiracaoSenhaMock("1", new AcessoSistema("1111111"));
    }

    @Test
    public void executaTest() throws ZetraException{
        processo.usuAutenticaSSO = "N";
        processo.dataExpiracao = LocalDate.now().plusDays(90);
        processo.executa();
        assertTrue(processo.chamouMetodoEnvioEmail);
    }

    @Test
    public void executaNotExpiredYet() throws ZetraException{
        processo.usuAutenticaSSO = "N";
        processo.dataExpiracao = LocalDate.now().plusDays(91);
        processo.executa();
        assertFalse(processo.chamouMetodoEnvioEmail);
    }

    @Test
    public void executaUsuAutenticaSSOequalsSTest() throws ZetraException{
        
        processo.usuAutenticaSSO = "S";
        processo.dataExpiracao = LocalDate.now().plusDays(90);
        processo.executa();

        assertTrue(processo.chamouMetodoEnvioEmail);
        assertEquals(processo.buscarIdentificadorInternoEconsig(null), processo.econsigMarcadoIdentInterno);
    }

    @Test
    public void executaUsuAutenticaSSOequalsSAutenticationFailTest() throws ZetraException{
        
        processo.usuAutenticaSSO = "S";
        processo.dataExpiracao = LocalDate.now().plusDays(90);
        processo.autenticarUsuarioSSO = false;
        processo.executa();

        assertFalse(processo.chamouMetodoEnvioEmail);
    }

    @Test
    public void executaUsuAutenticaSSOequalsSDiferentIdentInternoTest() throws ZetraException{
        
        processo.usuAutenticaSSO = "S";
        processo.dataExpiracao = LocalDate.now().plusDays(90);
        processo.emailIdentInternEconsig = "222222";
        processo.executa();

        assertFalse(processo.chamouMetodoEnvioEmail);
        
    }
    
    @Test
    public void executaSSONotExpiredYet() throws ZetraException{
        processo.usuAutenticaSSO = "S";
        processo.dataExpiracao = LocalDate.now().plusDays(91);
        processo.executa();
        assertFalse(processo.chamouMetodoEnvioEmail);
    }

    private class ProcessaEnviaNotificacaoPrazoExpiracaoSenhaMock extends ProcessaEnviaNotificacaoPrazoExpiracaoSenha {

        public String usuAutenticaSSO;
        public LocalDate dataExpiracao;
        public String emailIdentInternEconsig;

        public boolean chamouMetodoEnvioEmail;

        public String econsigMarcadoIdentInterno;

        public boolean autenticarUsuarioSSO = true;

        public ProcessaEnviaNotificacaoPrazoExpiracaoSenhaMock(String agdCodigo, AcessoSistema responsavel) {
            super(agdCodigo, responsavel);
        }

        @Override
        protected UsuarioController getUsuarioController() {
            return new UsuarioControllerBean() {
                @Override
                public List<TransferObject> listUsuariosAtivosComEmail(AcessoSistema responsavel)
                        throws UsuarioControllerException {
                    
                    CustomTransferObject transferObject = new CustomTransferObject();
                    transferObject.setAttribute(Columns.USU_AUTENTICA_SSO, usuAutenticaSSO);
                    transferObject.setAttribute(Columns.USU_DATA_EXP_SENHA, dataExpiracao);
                    transferObject.setAttribute(Columns.USU_NOME, "Nome do usuário ativo");
                    transferObject.setAttribute(Columns.USU_EMAIL, "usuarioativo@econsig.tech");

                    return List.of(transferObject);
                }
                
                @Override
                public void enviarNotificacaoPrazoExpiracaoSenha(String usuNome, String usuEmail,
                        Integer qtdeDiasExpiracaoSenha, AcessoSistema responsavel) {
                    chamouMetodoEnvioEmail = true;
                }
            };
        }

        @Override
        protected int buscarTpcDiasExpiracaoSenhaPorTipoUsuario(TransferObject usuAtivo, AcessoSistema responsavel) {
            return 90;
        }

        @Override
        protected SSOToken autenticarSSO(AcessoSistema responsavel) {
            if (autenticarUsuarioSSO) {
                return new SSOToken();
            }

            return null;
            
        }

        @Override
        protected UserDetailResponse buscarDadosUsuarioSSO(String email, SSOToken token) {

            if(token == null) {
                throw new RuntimeException ();
            }
            
            UserDetailResponse response = new UserDetailResponse();
            response.setPasswordExpirationDate(dataExpiracao);
            response.setUsername(email);
            response.setEmailIdentInternEconsig(emailIdentInternEconsig);

            return response;
        }


        @Override
        protected String buscarIdentificadorInternoEconsig(AcessoSistema responsavel) {
            return "11111";
        }

        @Override
        protected void marcarEsteEconsigParaEnviarEmail(UserDetailResponse userDetail,
                String identificadoInternoEconsig, SSOToken token) {

            if(token == null) {
                throw new RuntimeException ();
            }
            
            econsigMarcadoIdentInterno = identificadoInternoEconsig;
        }


        @Override
        protected boolean verificarSeRotinaHabilitada(AcessoSistema responsavel) {
            return true;
        }
    }
    
}

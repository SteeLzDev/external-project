-- DESENV-18893
-- @@delimiter=!

DECLARE
vClob VARCHAR(8000);

BEGIN

vClob := '<html><head><style type="text/css">body {width: 100%;font-family:Arial, Helvetica, sans-serif;}.button {width: 70%;height: 100%;border-radius: 10px;box-shadow: 0px 8px 15px rgba(0, 0, 0, 0.6) ' || chr(to_number('21', 'XX')) || 'important;-moz-box-shadow: 0px 8px 15px rgba(0, 0, 0, 0.6) ' || chr(to_number('21', 'XX')) || 'important;-webkit-box-shadow: 0px 8px 15px rgba(0, 0, 0, 0.6) ' || chr(to_number('21', 'XX')) || 'important;}.btn-primary, .btn-secondary {color: ' || chr(to_number('23', 'XX')) || 'fff;border: none;background-color: rgb(229, 231, 234);padding: 2.5% 4%;flex-direction: column;align-items: center;justify-content: center;text-decoration: none;font-weight: bold;font-size: 1.3rem;}.btn-secondary {color: ' || chr(to_number('23', 'XX')) || '7a1501;background-color: rgb(229, 231, 234);}.title-h1{font-size: 1.5rem;font-weight: 40%;color:' || chr(to_number('23', 'XX')) || '7a1501;font-family: Arial, Helvetica, sans-serif;margin:0;}.title-h2{font-size: 1.2rem;font-weight: 300;padding-left: 5%;color:' || chr(to_number('23', 'XX')) || '5e5e5e;font-family: Arial, Helvetica, sans-serif;margin-bottom:5vh;}.link-card{background:' || chr(to_number('23', 'XX')) || 'dfdfdf;}.link-card-header{background-color: ' || chr(to_number('23', 'XX')) || '82858D;color: ' || chr(to_number('23', 'XX')) || 'fff;font-weight: 500;text-align: center;}.link-card-button{width: 100%;height: 100%;font-size: 0.7rem ' || chr(to_number('21', 'XX')) || 'important;font-weight: 600 ' || chr(to_number('21', 'XX')) || 'important;color: ' || chr(to_number('23', 'XX')) || '585B64}.table-footer{background-color: ' || chr(to_number('23', 'XX')) || '7a1501;justify-content: center;color:' || chr(to_number('23', 'XX')) || 'fff;font-weight:70%;font-size: 1.3em;}.footer-box{flex-flow: row;align-items: center;}</style></head><body><table width="50%" border="0" cellspacing="0" cellpadding="0" align="center"><tr><td width="60%"align="center"><a target="_blank" href="<@url_sistema>"><img src="<@logoSistema>" width="30%" align="center" title="logo" alt="titulo.logo"></a></td></tr><tr><td width="60%"><h1 class="title-h1">Olá, <@serPrimeiroNome></h1></td></tr><tr><td width="60%"><h2 class="title-h2">Sua senha foi cadastrada com sucesso' || chr(to_number('21', 'XX')) || '</h2></td></tr></table><br><br><table class="table-footer" width="50%" border="0" cellspacing="0" cellpadding="0" align="center"><tr><td width="60%" align="center"><b>Obrigado' || chr(to_number('21', 'XX')) || '</b></td></tr><tr><td align="center"><img src="<@logoZetra>" width="13%" alt="Zetra"></td></tr><tr style="display:flex; flex-flow: row;"><td class="footer-box" width="50%" align="center">Baixe nosso app:</td><td class="footer-box" width="50%" align="center">Siga-nos nas redes:</td></tr><tr style="display:flex; flex-flow: row;"><td class="footer-box" width="50%" align="center"><a href="https://itunes.apple.com/br/app/econsig/id1209101894?l=en&mt=8"/><img src="<@logoAppleStore>" width="25%" alt="Apple store"></a><a href="https://play.google.com/store/apps/details?id=br.com.zetrasoft.econsig&hl=pt_BR"/><img src="<@logoGooglePlay>" width="25%" alt="Google play store"></a></td><td class="footer-box" width="50%" align="center"><a href="https://www.facebook.com/zetraBR/"><img src="<@logoFacebook>" width="10%" alt="Facebook"></a><a href="https://www.instagram.com/zetrabr/"><img src="<@logoInstagram>" width="10%" alt="Instagram"></a><a href="https://www.linkedin.com/company/zetrabr/"><img src="<@logoLinkedin>" width="10%" alt="Linkedin"></a></td></tr></table></body></html>';


INSERT INTO tb_modelo_email (MEM_CODIGO, MEM_TITULO, MEM_TEXTO)
values('emailCadastroSenhaServidor', '<@nome_sistema>: Cadastro de Senha', vClob);

END;
!

INSERT INTO tb_tipo_notificacao (TNO_CODIGO, TNO_DESCRICAO, TNO_ENVIO) VALUES('33', 'E-mail de Notificação de Cadastro de Senha de Servidor no fluxo de Cadastro de Senha', 'I')
!

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_VLR_DEFAULT, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES('888', 'Redireciona Servidor para recuperação de senha no cadastro de senha', 'SN', 'S', 'S', 'N', 'S', 'S', '3')
!

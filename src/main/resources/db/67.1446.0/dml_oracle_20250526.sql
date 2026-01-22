-- DESENV-23469
-- @@delimiter=!

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('988', 'Enviar senha por email ao se criar um novo usuário via sistema', 'SN', 'N', 'N', 'N', 'N', 'N', NULL)
!

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR)
VALUES ('988', '1', 'N')
!

DECLARE
vClob VARCHAR(8000);

BEGIN

vClob := '<html><head><style type="text/css">body{width:100%;font-family:Arial,Helvetica,sans-serif;}.button{width:70%;height:100%;border-radius:10px;box-shadow:0px 8px 15px rgba(0,0,0,0.6) ' || chr(to_number('21', 'XX')) || 'important;-moz-box-shadow:0px 8px 15px rgba(0,0,0,0.6) ' || chr(to_number('21', 'XX')) || 'important;-webkit-box-shadow:0px 8px 15px rgba(0,0,0,0.6) ' || chr(to_number('21', 'XX')) || 'important;}.btn-primary{color:' || chr(to_number('23', 'XX')) || 'fff;border:none;background-color:' || chr(to_number('23', 'XX')) || '1d4f91;padding:2.5% 4%;text-decoration:none;font-weight:bold;font-size:1.3rem;}.btn-secondary{color:' || chr(to_number('23', 'XX')) || 'fff;border:none;background-color:' || chr(to_number('23', 'XX')) || 'e80070;padding:2.5% 4%;text-decoration:none;font-weight:bold;font-size:1.3rem;} .title-h1{font-size:1.5rem;font-weight:bold;color:' || chr(to_number('23', 'XX')) || '1d4f91;font-family:Arial,Helvetica,sans-serif;margin:0;}</style></head><body><table width="50%" border="0" cellspacing="0" cellpadding="0" align="center"><tr><td align="center"><h1 class="title-h1">Olá, <@usuPrimeiroNome></h1></td></tr><tr><td align="center"><br><p>Seu usuário foi criado com sucesso. Sua senha é:</p><br><button class="button btn-primary"><@senha_inicial></button></td></tr><tr><td align="center"><br><p>Clique abaixo caso queria acessar o sistema</p><br><a href="<@link_acesso_sistema>" class="button btn-secondary">Acessar o Sistema</a></td></tr></table></body></html>';

INSERT INTO tb_modelo_email (MEM_CODIGO, MEM_TITULO, MEM_TEXTO) 
VALUES ('emailSenhaNovoUsuario', '<@nome_sistema>: Senha novo usuário', vClob);

END;
!

INSERT INTO tb_tipo_notificacao (TNO_CODIGO, TNO_DESCRICAO, TNO_ENVIO) VALUES ('49','E-mail de senha criada novo usuário','I')
!


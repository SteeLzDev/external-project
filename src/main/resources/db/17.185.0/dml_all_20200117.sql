-- DESENV-9211
DELETE FROM tb_ajuda WHERE ACR_CODIGO IN (SELECT ACR_CODIGO FROM tb_acesso_recurso WHERE ACR_RECURSO = '/margem/cancelar_consignacao.jsp');
DELETE FROM tb_acesso_usuario WHERE ACR_CODIGO IN (SELECT ACR_CODIGO FROM tb_acesso_recurso WHERE ACR_RECURSO = '/margem/cancelar_consignacao.jsp');
DELETE FROM tb_acesso_recurso WHERE ACR_RECURSO = '/margem/cancelar_consignacao.jsp';

UPDATE tb_acesso_recurso 
SET ACR_FIM_FLUXO = 'S' 
WHERE FUN_CODIGO = '59' AND ACR_RECURSO = '/v3/cancelarReserva' AND ACR_OPERACAO = 'cancelar';

UPDATE tb_acesso_recurso 
SET ACR_RECURSO = '/v3/cancelarMinhasReservas', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'efetivarAcao' 
WHERE ACR_RECURSO = '/margem/efetiva_acao_consignacao.jsp' AND FUN_CODIGO = '369';

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15484', '2', '369', '/v3/cancelarMinhasReservas', 'acao', 'cancelar', 1, 'S', 'S', null, 'N', '2');

-- DESENV-11198
UPDATE tb_acesso_recurso 
SET ACR_RECURSO = '/v3/configurarSistema', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'verNivelSeguranca' 
WHERE ACR_RECURSO = '/admin/nivel_seguranca.jsp';

INSERT tb_tipo_notificacao (TNO_CODIGO, TNO_DESCRICAO, TNO_ENVIO) 
VALUES (14, 'E-mail Recuperação de senha para o servidor', 'I');

-- DESENV-12087
INSERT INTO tb_modelo_email (MEM_CODIGO, MEM_TITULO, MEM_TEXTO) 
VALUES ('emailRecuperacaoSenhaServidor', '<@nome_sistema>: Recuperação de Senha', concat('<html><head><style type="text/css">body {width: 100%', 0x3b, 'font-family:Arial, Helvetica, sans-serif', 0x3b, '}.button {width: 70%', 0x3b, 'height: 100%', 0x3b, 'border-radius: 10px', 0x3b, 'box-shadow: 0px 8px 15px rgba(0, 0, 0, 0.6) !important', 0x3b, '-moz-box-shadow: 0px 8px 15px rgba(0, 0, 0, 0.6) !important', 0x3b, '-webkit-box-shadow: 0px 8px 15px rgba(0, 0, 0, 0.6) !important', 0x3b, '}.btn-primary, .btn-secondary {color: ', 0x23, 'fff', 0x3b, 'border: none', 0x3b, 'background-color: rgb(229, 231, 234)', 0x3b, 'padding: 2.5% 4%', 0x3b, 'flex-direction: column', 0x3b, 'align-items: center', 0x3b, 'justify-content: center', 0x3b, 'text-decoration: none', 0x3b, 'font-weight: bold', 0x3b, 'font-size: 1.3rem', 0x3b, '}.btn-secondary {color: ', 0x23, '7a1501', 0x3b, 'background-color: rgb(229, 231, 234)', 0x3b, '}.title-h1{font-size: 1.5rem', 0x3b, 'font-weight: 40% ', 0x3b, 'color:', 0x23, '7a1501 ', 0x3b, 'font-family: Arial, Helvetica, sans-serif ', 0x3b, 'margin:0 ', 0x3b, '}.title-h2{font-size: 1.2rem ', 0x3b, 'font-weight: 300 ', 0x3b, 'padding-left: 5% ', 0x3b, 'color:', 0x23, '5e5e5e ', 0x3b, 'font-family: Arial, Helvetica, sans-serif ', 0x3b, 'margin-bottom:5vh ', 0x3b, '}.link-card{background:', 0x23, 'dfdfdf ', 0x3b, '}.link-card-header{background-color: ', 0x23, '82858D ', 0x3b, 'color: ', 0x23, 'fff ', 0x3b, 'font-weight: 500 ', 0x3b, 'text-align: center ', 0x3b, '}.link-card-button{width: 100% ', 0x3b, 'height: 100% ', 0x3b, 'font-size: 0.7rem !important ', 0x3b, 'font-weight: 600 !important ', 0x3b, 'color: ', 0x23, '585B64}.table-footer{background-color: ', 0x23, '7a1501 ', 0x3b, 'justify-content: center ', 0x3b, 'color:', 0x23, 'fff ', 0x3b, 'font-weight:70% ', 0x3b, 'font-size: 1.3em ', 0x3b, '}.footer-box{flex-flow: row ', 0x3b, 'align-items: center ', 0x3b, '}</style></head><body><table width="50%" border="0" cellspacing="0" cellpadding="0" align="center"><tr><td width="60%"align="center"><a target="_blank" href="<@url_sistema>"><img src="<@logoSistema>" width="30%" align="center" title="logo" alt="titulo.logo"></a></td></tr><tr><td width="60%"><h1 class="title-h1">Olá, <@serPrimeiroNome></h1></td></tr><tr><td width="60%"><h2 class="title-h2">Você esqueceu sua senha?<br>Não tem problema, é muito fácil recuperá-la, <br>Clique no botão abaixo para definir uma senha nova</h2></td></tr><tr><td width="60%"><div align="center"><a href="<@linkRecuperacao>"><button class="button btn-secondary" alt="<@linkRecuperacao>">Quero recuperar a minha senha</button></a></div></td></tr></table> <br><br><table class="link-card" width="50%" border="0" cellspacing="0" cellpadding="0" align="center" bgcolor="', 0x23, '414141"><tr class="link-card-header"><td><p style="padding: 0px 5px 0px 5px ', 0x3b, ' font-size: 1.1em ', 0x3b, '">Caso você tenha algum problema para clicar no link, copie e cole o endereço abaixo no seu navegador.</p> </td></tr><tr class="link-card-body"><td style="padding: 10px ', 0x3b, '"><div class="link-card-body" align="center"><a href="<@linkRecuperacao>" style="color: ', 0x23, '585B64"><@linkRecuperacao></a></div></td></tr></table> <br><table width="45%" border="0" cellspacing="0" cellpadding="0" align="center"><tr><td width="60%"><p style="text-align: center ', 0x3b, ' color:', 0x23, '5e5e5e ', 0x3b, '">Desconsidere este e-mail caso você não tenha solicitado a alteração e siga acessando o portal com sua senha atual.</p><br></td></tr><tr><td width="60%"><div align="center"><a href="<@url_sistema>"><button class="button btn-secondary" alt="<@url_sistema>">Quero acessar o portal</button></a></div></td></tr></table> <br><table class="table-footer" width="50%" border="0" cellspacing="0" cellpadding="0" align="center"><tr ><td width="60%" align="center"><b>Obrigado!</b></td></tr><tr><td align="center"><img src="<@logoZetra>" width="13%" alt="Zetra"></td></tr><tr style="display:flex ', 0x3b, ' flex-flow: row ', 0x3b, '"><td class="footer-box" width="50%" align="center">Baixe nosso app:</td><td class="footer-box" width="50%" align="center">Siga-nos nas redes:</td></tr><tr style="display:flex ', 0x3b, ' flex-flow: row ', 0x3b, '"><td class="footer-box" width="50%" align="center"> <a href="https://itunes.apple.com/br/app/econsig/id1209101894?l=en&mt=8"/> <img src="<@logoAppleStore>" width="25%" alt="Apple store"></a> <a href="https://play.google.com/store/apps/details?id=br.com.zetrasoft.econsig&hl=pt_BR"/> <img src="<@logoGooglePlay>" width="25%" alt="Google play store"></a> </td><td class="footer-box" width="50%" align="center"><a href="https://www.facebook.com/zetraBR/"><img src="<@logoFacebook>" width="10%" alt="Facebook"></a><a href="https://www.instagram.com/zetrabr/"><img src="<@logoInstagram>" width="10%" alt="Instagram"></a><a href="https://www.linkedin.com/company/zetrabr/"><img src="<@logoLinkedin>" width="10%" alt="Linkedin"></a></td></tr> </table></body></html>'));


-- DESENV-12990 e DESENV-12991
-- PARA PRODUÇÃO TIRAR O SUFIXO '-hml' HOSTNAME DAS URLS
-- INSERT INTO tb_param_senha_externa (PSX_CHAVE, PSX_VALOR) VALUES ('metodo', 'OAUTH2');
-- INSERT INTO tb_param_senha_externa (PSX_CHAVE, PSX_VALOR) VALUES ('oauth2.uri.authentication.1.code', 'https://auth-cs-hml.identidadedigital.pr.gov.br/centralautenticacao/api/v1/authorize?response_type=code&scope=centralcidadao.v1.cidadao.grupos.cpf.get');
-- INSERT INTO tb_param_senha_externa (PSX_CHAVE, PSX_VALOR) VALUES ('oauth2.uri.authentication.2.token', 'https://auth-cs-hml.identidadedigital.pr.gov.br/centralautenticacao/api/v1/token?scope=centralcidadao.v1.cidadao.grupos.cpf.get');
-- INSERT INTO tb_param_senha_externa (PSX_CHAVE, PSX_VALOR) VALUES ('oauth2.uri.token.validation', 'https://cidadao-cs-hml.identidadedigital.pr.gov.br/centralcidadao/api/v1/cidadaos/grupos/cpf/');
-- INSERT INTO tb_param_senha_externa (PSX_CHAVE, PSX_VALOR) VALUES ('oauth2.client.id', 'e1e32e235eee1f970470a3a6658dfdd5');
-- INSERT INTO tb_param_senha_externa (PSX_CHAVE, PSX_VALOR) VALUES ('oauth2.client.key', '12345678');
-- INSERT INTO tb_param_senha_externa (PSX_CHAVE, PSX_VALOR) VALUES ('oauth2.param.cpf', 'cpf');
-- INSERT INTO tb_param_senha_externa (PSX_CHAVE, PSX_VALOR) VALUES ('oauth2.param.token', 'access_token');
-- INSERT INTO tb_param_senha_externa (PSX_CHAVE, PSX_VALOR) VALUES ('oauth2.param.code', 'code');
-- INSERT INTO tb_param_senha_externa (PSX_CHAVE, PSX_VALOR) VALUES ('oauth2.param.state', 'state');
-- INSERT INTO tb_param_senha_externa (PSX_CHAVE, PSX_VALOR) VALUES ('oauth2.response.cpf', 'document_number');
-- INSERT INTO tb_param_senha_externa (PSX_CHAVE, PSX_VALOR) VALUES ('oauth2.response.token', 'access_token');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15358', NULL, NULL, '/v3/autenticarOAuth2', NULL, NULL, 1, 'N', 'N', NULL, 'N', '0');

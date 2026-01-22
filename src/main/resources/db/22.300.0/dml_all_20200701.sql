-- DESENV-13961
INSERT INTO tb_tipo_notificacao (TNO_CODIGO, TNO_DESCRICAO, TNO_ENVIO) 
VALUES ('19', 'E-mail de envio de otp para o servidor', 'I');

-- Email original do econsig
INSERT INTO tb_modelo_email (MEM_CODIGO, MEM_TITULO, MEM_TEXTO)
VALUES ('enviarEmailOtpServidor', '<@tituloEmail>', '<@tituloEmail><br/><br/><br/><b>Código de Verificação: <@otp></b><br/><br/>');

-- Email que será usando para sistemas salary pay
-- INSERT INTO tb_modelo_email (MEM_CODIGO, MEM_TITULO, MEM_TEXTO)
-- VALUES ('enviarEmailOtpServidor', 'Seu código de verificação do Portal eConsig chegou', CONCAT('<html><head></head><body><table style="font-family: Arial, Helvetica, sans-serif', 0x3b, ' text-align: center" width="100%"><tbody><tr><td><img src="<@url_sistema>/img/email_otp_topo.png"/></td></tr><tr><td><br><br></td></tr><tr><td style="color: ', 0x23, '800000', 0x3b, ' font-size: 2em', 0x3b, '"><b>Olá, <@primeiroNome>!</b></td></tr><tr><td><br><br></td></tr><tr><td>Este email foi originado do Portal eConsig - Sistema Digital de Consignações.</td></tr><tr><td><br><br></td></tr><tr><td style="text-align: center', 0x3b, '">O seu código de verificação é:</td></tr><tr><td><br><br></td></tr><tr><td style="text-align: center', 0x3b, '"><b><@otp><b></td></tr><tr><td><br><br></td></tr><tr><td>Com o eConsig, você aproveita os melhores benefícios consignados de um jeito simples e inovador! <br> Tudo isso pelo computador ou aplicativo disponível gratuitamente nas lojas Google Play e Apple Store.</td></tr><tr><td><br><br></td></tr><tr><td style="text-align: center', 0x3b, '">Até breve.</td></tr><tr><td><br><br></td></tr><tr><td style="text-align: center', 0x3b, '">Abraços,<br>Equipe eConsig.</td></tr></tbody></table><br><br><table style="text-align: center', 0x3b, '" width="100%"><tbody><tr><td><img src="<@url_sistema>/img/email_otp_rodape.png"/></td></tr></tbody></table></body></html>'));

-- DESENV-15377
INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('810', 'Envia e-mail de expiração do cadastro das consignatárias para consignante ou órgãos', 'SN', 'N', 'N', 'N', 'N', 'N', NULL);

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR)
VALUES ('810', '1', 'N');

INSERT INTO tb_modelo_email (MEM_CODIGO, MEM_TITULO, MEM_TEXTO) 
VALUES ('enviarEmailExpiracaoCsa', '<@nome_sistema>: término de vigência de contrato em alguns dias.', '<b><@nome_sistema> - <@cse_nome> </b><br/><br/>A data de expiração informada no cadastro da consignatária está se aproximando.<br>Favor entrar em contato com o gestor do sistema para a regularização do cadastro.<br/>');

INSERT INTO tb_modelo_email (MEM_CODIGO, MEM_TITULO, MEM_TEXTO)
VALUES ('enviarEmailExpiracaoCsaTexto', '<br/>Expirará em <@dias> dias:', '<br/><br/><@csa_nome>');

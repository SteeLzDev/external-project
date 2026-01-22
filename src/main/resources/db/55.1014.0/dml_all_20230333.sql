-- DESENV-19365
INSERT INTO tb_modelo_email VALUES ('enviarEmailExpiracaoParaCsa','<@nome_sistema>: término de vigência de contrato em alguns dias. CSACSA','<b><@nome_sistema> - <@cse_nome> </b><br/><br/>A data de expiração informada no cadastro da consignatária está se aproximando.<br>Favor entrar em contato com o gestor do sistema para a regularização do cadastro.<br/>');

INSERT INTO tb_modelo_email VALUES ('enviarEmailExpiracaoParaCsaTexto','<br/>Expirará em CSA <@dias> dias:','<br/><br/><@csa_nome>');

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('915', 'Inserir anexo nos emails de credenciamento de consignatária', 'SN', 'N', 'N', 'N', 'N', 'N', NULL);

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR)
VALUES ('915', '1', 'N');


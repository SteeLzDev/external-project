-- DESENV-14147
INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, GPS_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA) 
VALUES ('787', NULL, 'Quantidade de respostas de não entendimento do chatbot para redirecionar o usuário ao chat suporte', 'INT', '3', 'N', 'N', 'N', 'N');

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR)
VALUES ('787', '1', '3');

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, GPS_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA) 
VALUES ('788', NULL, 'Janela de atendimento do chat suporte para redirecionamento do chatbot', 'ALFA', 'wday{Mon-Fri} hour{8am-5pm}', 'N', 'N', 'N', 'N');

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR)
VALUES ('788', '1', 'wday{Mon-Fri} hour{8am-5pm}');

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, GPS_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA)
VALUES ('789', NULL, 'URL para nova janela de chat de suporte', 'ALFA', NULL, 'N', 'N', 'N', 'N');

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR)
VALUES ('789', '1', CONCAT('https://static.zdassets.com/web_widget/latest/liveChat.html?v=2', cast(0x23 as char), 'key=zetrasoft.zendesk.com&locale=pt-br')); 

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, GPS_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA)
VALUES ('790', NULL, 'URL para widget do chat de suporte', 'ALFA', NULL, 'N', 'N', 'N', 'N');

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR)
VALUES ('790', '1', 'https://static.zdassets.com/ekr/snippet.js?key=a0255bd5-b0cb-48e9-bb9d-bf08adc7093d'); 

UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/abrirChatSuporte' WHERE ACR_CODIGO = '13051';

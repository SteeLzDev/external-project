-- DESENV-8933

update tb_tipo_param_sist_consignante set tpc_descricao = 'Envia e-mail de alerta às entidades relacionadas em operações sobre consignação' where tpc_codigo = '339';

-- inclusão de configurações que refletem os usuários atuais que disparam
-- envio de e-mail em caso de alteração do contrato
insert into tb_destinatario_email (FUN_CODIGO, PAP_CODIGO_OPERADOR, PAP_CODIGO_DESTINATARIO) values ('121', '1', '2');
insert into tb_destinatario_email (FUN_CODIGO, PAP_CODIGO_OPERADOR, PAP_CODIGO_DESTINATARIO) values ('121', '7', '2');
insert into tb_destinatario_email (FUN_CODIGO, PAP_CODIGO_OPERADOR, PAP_CODIGO_DESTINATARIO) values ('121', '3', '2');
insert into tb_destinatario_email (FUN_CODIGO, PAP_CODIGO_OPERADOR, PAP_CODIGO_DESTINATARIO) values ('88', '1', '2');
insert into tb_destinatario_email (FUN_CODIGO, PAP_CODIGO_OPERADOR, PAP_CODIGO_DESTINATARIO) values ('88', '7', '2');
insert into tb_destinatario_email (FUN_CODIGO, PAP_CODIGO_OPERADOR, PAP_CODIGO_DESTINATARIO) values ('88', '3', '2');
insert into tb_destinatario_email (FUN_CODIGO, PAP_CODIGO_OPERADOR, PAP_CODIGO_DESTINATARIO) values ('68', '1', '2');
insert into tb_destinatario_email (FUN_CODIGO, PAP_CODIGO_OPERADOR, PAP_CODIGO_DESTINATARIO) values ('68', '7', '2');
insert into tb_destinatario_email (FUN_CODIGO, PAP_CODIGO_OPERADOR, PAP_CODIGO_DESTINATARIO) values ('68', '3', '2');
insert into tb_destinatario_email (FUN_CODIGO, PAP_CODIGO_OPERADOR, PAP_CODIGO_DESTINATARIO) values ('29', '1', '2');
insert into tb_destinatario_email (FUN_CODIGO, PAP_CODIGO_OPERADOR, PAP_CODIGO_DESTINATARIO) values ('29', '7', '2');
insert into tb_destinatario_email (FUN_CODIGO, PAP_CODIGO_OPERADOR, PAP_CODIGO_DESTINATARIO) values ('29', '3', '2');
insert into tb_destinatario_email (FUN_CODIGO, PAP_CODIGO_OPERADOR, PAP_CODIGO_DESTINATARIO) values ('58', '1', '2');
insert into tb_destinatario_email (FUN_CODIGO, PAP_CODIGO_OPERADOR, PAP_CODIGO_DESTINATARIO) values ('58', '7', '2');
insert into tb_destinatario_email (FUN_CODIGO, PAP_CODIGO_OPERADOR, PAP_CODIGO_DESTINATARIO) values ('58', '3', '2');
insert into tb_destinatario_email (FUN_CODIGO, PAP_CODIGO_OPERADOR, PAP_CODIGO_DESTINATARIO) values ('27', '1', '2');
insert into tb_destinatario_email (FUN_CODIGO, PAP_CODIGO_OPERADOR, PAP_CODIGO_DESTINATARIO) values ('27', '7', '2');
insert into tb_destinatario_email (FUN_CODIGO, PAP_CODIGO_OPERADOR, PAP_CODIGO_DESTINATARIO) values ('27', '3', '2');
insert into tb_destinatario_email (FUN_CODIGO, PAP_CODIGO_OPERADOR, PAP_CODIGO_DESTINATARIO) values ('61', '1', '2');
insert into tb_destinatario_email (FUN_CODIGO, PAP_CODIGO_OPERADOR, PAP_CODIGO_DESTINATARIO) values ('61', '7', '2');
insert into tb_destinatario_email (FUN_CODIGO, PAP_CODIGO_OPERADOR, PAP_CODIGO_DESTINATARIO) values ('61', '3', '2');
insert into tb_destinatario_email (FUN_CODIGO, PAP_CODIGO_OPERADOR, PAP_CODIGO_DESTINATARIO) values ('60', '1', '2');
insert into tb_destinatario_email (FUN_CODIGO, PAP_CODIGO_OPERADOR, PAP_CODIGO_DESTINATARIO) values ('60', '7', '2');
insert into tb_destinatario_email (FUN_CODIGO, PAP_CODIGO_OPERADOR, PAP_CODIGO_DESTINATARIO) values ('60', '3', '2');
insert into tb_destinatario_email (FUN_CODIGO, PAP_CODIGO_OPERADOR, PAP_CODIGO_DESTINATARIO) values ('57', '1', '2');
insert into tb_destinatario_email (FUN_CODIGO, PAP_CODIGO_OPERADOR, PAP_CODIGO_DESTINATARIO) values ('57', '7', '2');
insert into tb_destinatario_email (FUN_CODIGO, PAP_CODIGO_OPERADOR, PAP_CODIGO_DESTINATARIO) values ('57', '3', '2');
insert into tb_destinatario_email (FUN_CODIGO, PAP_CODIGO_OPERADOR, PAP_CODIGO_DESTINATARIO) values ('30', '1', '2');
insert into tb_destinatario_email (FUN_CODIGO, PAP_CODIGO_OPERADOR, PAP_CODIGO_DESTINATARIO) values ('30', '7', '2');
insert into tb_destinatario_email (FUN_CODIGO, PAP_CODIGO_OPERADOR, PAP_CODIGO_DESTINATARIO) values ('30', '3', '2');
insert into tb_destinatario_email (FUN_CODIGO, PAP_CODIGO_OPERADOR, PAP_CODIGO_DESTINATARIO) values ('59', '1', '2');
insert into tb_destinatario_email (FUN_CODIGO, PAP_CODIGO_OPERADOR, PAP_CODIGO_DESTINATARIO) values ('59', '7', '2');
insert into tb_destinatario_email (FUN_CODIGO, PAP_CODIGO_OPERADOR, PAP_CODIGO_DESTINATARIO) values ('59', '3', '2');
insert into tb_destinatario_email (FUN_CODIGO, PAP_CODIGO_OPERADOR, PAP_CODIGO_DESTINATARIO) values ('28', '1', '2');
insert into tb_destinatario_email (FUN_CODIGO, PAP_CODIGO_OPERADOR, PAP_CODIGO_DESTINATARIO) values ('28', '7', '2');
insert into tb_destinatario_email (FUN_CODIGO, PAP_CODIGO_OPERADOR, PAP_CODIGO_DESTINATARIO) values ('28', '3', '2');
insert into tb_destinatario_email (FUN_CODIGO, PAP_CODIGO_OPERADOR, PAP_CODIGO_DESTINATARIO) values ('102', '1', '2');
insert into tb_destinatario_email (FUN_CODIGO, PAP_CODIGO_OPERADOR, PAP_CODIGO_DESTINATARIO) values ('102', '7', '2');
insert into tb_destinatario_email (FUN_CODIGO, PAP_CODIGO_OPERADOR, PAP_CODIGO_DESTINATARIO) values ('102', '3', '2');
insert into tb_destinatario_email (FUN_CODIGO, PAP_CODIGO_OPERADOR, PAP_CODIGO_DESTINATARIO) values ('264', '1', '2');
insert into tb_destinatario_email (FUN_CODIGO, PAP_CODIGO_OPERADOR, PAP_CODIGO_DESTINATARIO) values ('264', '7', '2');
insert into tb_destinatario_email (FUN_CODIGO, PAP_CODIGO_OPERADOR, PAP_CODIGO_DESTINATARIO) values ('264', '3', '2');
insert into tb_destinatario_email (FUN_CODIGO, PAP_CODIGO_OPERADOR, PAP_CODIGO_DESTINATARIO) values ('31', '1', '2');
insert into tb_destinatario_email (FUN_CODIGO, PAP_CODIGO_OPERADOR, PAP_CODIGO_DESTINATARIO) values ('31', '7', '2');
insert into tb_destinatario_email (FUN_CODIGO, PAP_CODIGO_OPERADOR, PAP_CODIGO_DESTINATARIO) values ('31', '3', '2');
insert into tb_destinatario_email (FUN_CODIGO, PAP_CODIGO_OPERADOR, PAP_CODIGO_DESTINATARIO) values ('81', '1', '2');
insert into tb_destinatario_email (FUN_CODIGO, PAP_CODIGO_OPERADOR, PAP_CODIGO_DESTINATARIO) values ('81', '7', '2');
insert into tb_destinatario_email (FUN_CODIGO, PAP_CODIGO_OPERADOR, PAP_CODIGO_DESTINATARIO) values ('81', '3', '2');

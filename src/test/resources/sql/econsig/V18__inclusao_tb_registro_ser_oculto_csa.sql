create table tb_registro_ser_oculto_csa
(
   RSE_CODIGO varchar(32) not null,
   CSA_CODIGO varchar(32) not null,
   primary key (RSE_CODIGO, CSA_CODIGO)
) ENGINE = InnoDB;

alter table tb_registro_ser_oculto_csa add constraint FK_RSE_ROC_1 foreign key (RSE_CODIGO)
      references tb_registro_servidor (RSE_CODIGO) on delete restrict on update restrict;

alter table tb_registro_ser_oculto_csa add constraint FK_CSA_ROC_1 foreign key (CSA_CODIGO)
      references tb_consignataria (CSA_CODIGO) on delete restrict on update restrict;
      
UPDATE tb_tipo_param_sist_consignante SET TPC_DESCRICAO = 'Quantidade de dias para notificar aos usuários CSA/COR, antes da data de expiração da senha.' WHERE TPC_CODIGO = '979';
UPDATE tb_tipo_param_sist_consignante SET TPC_DESCRICAO = 'Quantidade de dias para notificar aos usuários CSE/ORG, antes da data de expiração da senha.' WHERE TPC_CODIGO = '980';
UPDATE tb_tipo_param_sist_consignante SET TPC_DESCRICAO = 'Quantidade de dias para notificar aos usuários SER, antes da data de expiração da senha.' WHERE TPC_CODIGO = '981';

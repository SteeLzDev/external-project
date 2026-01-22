/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     27/09/2022 09:13:46                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_param_csa_registro_ser                             */
/*==============================================================*/
create table tb_param_csa_registro_ser
(
   CSA_CODIGO           varchar(32) not null,
   RSE_CODIGO           varchar(32) not null,
   TPA_CODIGO           varchar(32) not null,
   PRC_DATA_CADASTRO    datetime not null,
   PRC_VLR              varchar(255) not null,
   PRC_OBS              text,
   primary key (CSA_CODIGO, RSE_CODIGO, TPA_CODIGO)
) ENGINE=InnoDB;

alter table tb_param_csa_registro_ser add constraint FK_R_892 foreign key (CSA_CODIGO)
      references tb_consignataria (CSA_CODIGO) on delete restrict on update restrict;

alter table tb_param_csa_registro_ser add constraint FK_R_893 foreign key (RSE_CODIGO)
      references tb_registro_servidor (RSE_CODIGO) on delete restrict on update restrict;

alter table tb_param_csa_registro_ser add constraint FK_R_894 foreign key (TPA_CODIGO)
      references tb_tipo_param_consignataria (TPA_CODIGO) on delete restrict on update restrict;


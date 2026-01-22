/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     22/09/2020 09:37:12                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_param_svc_correspondente                           */
/*==============================================================*/
create table tb_param_svc_correspondente
(
   PSO_CODIGO           varchar(32) not null,
   TPS_CODIGO           varchar(32) not null,
   COR_CODIGO           varchar(32) not null,
   SVC_CODIGO           varchar(32) not null,
   PSO_DATA_INI_VIG     date,
   PSO_DATA_FIM_VIG     date,
   PSO_ATIVO            smallint,
   PSO_VLR              varchar(255),
   PSO_VLR_REF          varchar(255),
   primary key (PSO_CODIGO)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

alter table tb_param_svc_correspondente add constraint FK_R_812 foreign key (COR_CODIGO)
      references tb_correspondente (COR_CODIGO) on delete restrict on update restrict;

alter table tb_param_svc_correspondente add constraint FK_R_813 foreign key (TPS_CODIGO)
      references tb_tipo_param_svc (TPS_CODIGO) on delete restrict on update restrict;

alter table tb_param_svc_correspondente add constraint FK_R_814 foreign key (SVC_CODIGO)
      references tb_servico (SVC_CODIGO) on delete restrict on update restrict;


/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     28/05/2021 09:24:58                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_ocorrencia_coeficiente                             */
/*==============================================================*/
create table tb_ocorrencia_coeficiente
(
   OCF_CODIGO           varchar(32) not null,
   SVC_CODIGO           varchar(32) not null,
   CSA_CODIGO           varchar(32) not null,
   USU_CODIGO           varchar(32) not null,
   TOC_CODIGO           varchar(32) not null,
   OCF_DATA             datetime not null,
   OCF_DATA_INI_VIG     datetime,
   OCF_DATA_FIM_VIG     datetime,
   OCF_OBS              text not null,
   OCF_IP_ACESSO        varchar(45) not null,
   primary key (OCF_CODIGO)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

alter table tb_ocorrencia_coeficiente add constraint FK_R_834 foreign key (CSA_CODIGO)
      references tb_consignataria (CSA_CODIGO) on delete restrict on update restrict;

alter table tb_ocorrencia_coeficiente add constraint FK_R_835 foreign key (SVC_CODIGO)
      references tb_servico (SVC_CODIGO) on delete restrict on update restrict;

alter table tb_ocorrencia_coeficiente add constraint FK_R_836 foreign key (USU_CODIGO)
      references tb_usuario (USU_CODIGO) on delete restrict on update restrict;

alter table tb_ocorrencia_coeficiente add constraint FK_R_837 foreign key (TOC_CODIGO)
      references tb_tipo_ocorrencia (TOC_CODIGO) on delete restrict on update restrict;


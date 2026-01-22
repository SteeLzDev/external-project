/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     03/12/2020 12:05:15                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_ocorrencia_perfil                                  */
/*==============================================================*/
create table tb_ocorrencia_perfil
(
   OPR_CODIGO           varchar(32) not null,
   PER_CODIGO           varchar(32) not null,
   USU_CODIGO           varchar(32) not null,
   TOC_CODIGO           varchar(32) not null,
   TMO_CODIGO           varchar(32),
   OPR_DATA             datetime not null,
   OPR_OBS              text not null,
   OPR_IP_ACESSO        varchar(45),
   primary key (OPR_CODIGO)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

alter table tb_ocorrencia_perfil add constraint FK_R_822 foreign key (PER_CODIGO)
      references tb_perfil (PER_CODIGO) on delete restrict on update restrict;

alter table tb_ocorrencia_perfil add constraint FK_R_823 foreign key (USU_CODIGO)
      references tb_usuario (USU_CODIGO) on delete restrict on update restrict;

alter table tb_ocorrencia_perfil add constraint FK_R_824 foreign key (TOC_CODIGO)
      references tb_tipo_ocorrencia (TOC_CODIGO) on delete restrict on update restrict;

alter table tb_ocorrencia_perfil add constraint FK_R_825 foreign key (TMO_CODIGO)
      references tb_tipo_motivo_operacao (TMO_CODIGO) on delete restrict on update restrict;

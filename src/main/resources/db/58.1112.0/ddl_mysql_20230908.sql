/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     27/07/2023 15:25:01                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_informacao_csa_servidor                            */
/*==============================================================*/
create table tb_informacao_csa_servidor
(
   ICS_CODIGO           varchar(32) not null,
   SER_CODIGO           varchar(32) not null,
   CSA_CODIGO           varchar(32) not null,
   USU_CODIGO           varchar(32) not null,
   ICS_VALOR            text not null,
   ICS_DATA             datetime not null,
   ICS_IP_ACESSO        varchar(45),
   primary key (ICS_CODIGO)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

alter table tb_informacao_csa_servidor add constraint FK_R_928 foreign key (SER_CODIGO)
      references tb_servidor (SER_CODIGO) on delete restrict on update restrict;

alter table tb_informacao_csa_servidor add constraint FK_R_929 foreign key (CSA_CODIGO)
      references tb_consignataria (CSA_CODIGO) on delete restrict on update restrict;

alter table tb_informacao_csa_servidor add constraint FK_R_930 foreign key (USU_CODIGO)
      references tb_usuario (USU_CODIGO) on delete restrict on update restrict;


/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     07/03/2022 11:48:20                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_historico_login                                    */
/*==============================================================*/
create table tb_historico_login
(
   HLO_CODIGO           int not null auto_increment,
   USU_CODIGO           varchar(32) not null,
   HLO_DATA             datetime not null,
   HLO_CANAL            char(1) not null,
   primary key (HLO_CODIGO)
) ENGINE=InnoDB;

alter table tb_historico_login add constraint FK_R_859 foreign key (USU_CODIGO)
      references tb_usuario (USU_CODIGO) on delete restrict on update restrict;

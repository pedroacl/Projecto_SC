##############################
#   Trabalho realizado pelo  #
#			  Grupo 34  		  #
#									  #
# António Rodrigues, nº40853 #
# José Albuquerque, 	nº40251 #
# Pedro Luís, 			nº45588 #
##############################

############
# Servidor #
############

De modo a corrermos o servidor devemos executar a seguinte instrução na linha
de comandos:

java -Djava.security.manager -Djava.security.policy=policies/server.policy -jar
	server.jar <serverPort>


##########
# Client #
##########

Para corrermos o cliente devemos executar a seguinte instrução:

java -Djava.security.manager -Djava.security.policy=policies/client.policy	-jar
	client.jar myWhats <localUser> <serverAddress> [ ‐p <password> ]
	[ ‐m <contact> <message> | ‐f <contact> <file> | ‐r contact file |
	‐a <user> <group> | ‐d <user> <group> ]


Nota: Devemos garantir que a pasta de policies se encontra sempre localizada
junto a estes dois jars de modo a serem ativadas as devidas políticas de segurança.
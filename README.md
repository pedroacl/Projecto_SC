# Trabalho realizado pelo Grupo 34

- António Rodrigues nº40853
- José Albuquerque nº40251
- Pedro Luís nº45588

---

## Servidor

De modo a corrermos o servidor devemos executar a seguinte instrução na linha
de comandos:

java -Djava.security.manager -Djava.security.policy=policies/server.policy -jar
	server.jar <serverPort> <password>

## Client

Para corrermos o cliente devemos executar a seguinte instrução:

java -Djava.security.manager -Djava.security.policy=policies/client.policy	-jar
	client.jar myWhats <localUser> <serverAddress> [ ‐p <password> ]
	[ ‐m <contact> <message> | ‐f <contact> <file> | ‐r contact file |
	‐a <user> <group> | ‐d <user> <group> ]

**Nota:** Devemos garantir que a pasta de policies se encontra sempre localizada
junto a estes dois jars de modo a serem ativadas as devidas políticas de segurança.

---

## Keystores

De seguida documentamos os comandos utilizados para criar e configurar as keystores.

### Adicionar entry à keystore
/usr/lib/jvm/java-8-oracle/bin/keytool -keystore keystore.cliente -genkey -alias maria
(pass = seguranca)

### Gerar chave secreta (JCEKS)
/usr/lib/jvm/java-8-oracle/bin/keytool –genseckey maria –alias secKey -storetype storetype JCEKS

### Remover chave
/usr/lib/jvm/java-8-oracle/bin/keytool -delete -alias maria -keystore keystore.jks

---

### Gerar keystores dos utilizadores

#### Maria (pass: mariamaria)
/usr/lib/jvm/java-8-oracle/bin/keytool -genkey -keyalg RSA -alias maria -keystore keystore.maria -validity 360 -keysize 2048

#### Sara (pass: sarasara)
/usr/lib/jvm/java-8-oracle/bin/keytool -genkey -keyalg RSA -alias sara -keystore keystore.sara -validity 360 -keysize 2048

#### Pedro (pass: pedropedro)
/usr/lib/jvm/java-8-oracle/bin/keytool -genkey -keyalg RSA -alias pedro -keystore keystore.pedro -validity 360 -keysize 2048

---

### Exportar certificados dos utlizadores

#### Maria
/usr/lib/jvm/java-8-oracle/bin/keytool -exportcert -alias maria -keystore keystore.maria -file cert_maria.cer

#### Sara
/usr/lib/jvm/java-8-oracle/bin/keytool -exportcert -alias sara -keystore keystore.sara -file cert_sara.cer

#### Pedro
/usr/lib/jvm/java-8-oracle/bin/keytool -exportcert -alias pedro -keystore keystore.pedro -file cert_pedro.cer

---

### Importar certificados dos utilizadores

#### Maria
/usr/lib/jvm/java-8-oracle/bin/keytool -importcert -alias sara -keystore keystore.maria -file cert_sara.cer &&
/usr/lib/jvm/java-8-oracle/bin/keytool -importcert -alias pedro -keystore keystore.maria -file cert_pedro.cer

#### Sara
/usr/lib/jvm/java-8-oracle/bin/keytool -importcert -alias maria -keystore keystore.sara -file cert_maria.cer &&
/usr/lib/jvm/java-8-oracle/bin/keytool -importcert -alias pedro -keystore keystore.sara -file cert_pedro.cer

#### Pedro
/usr/lib/jvm/java-8-oracle/bin/keytool -importcert -alias maria -keystore keystore.pedro -file cert_maria.cer &&
/usr/lib/jvm/java-8-oracle/bin/keytool -importcert -alias sara -keystore keystore.pedro -file cert_sara.cer

---

## Gerar keystore e truststore para TLS

### Servidor (keystore)
/usr/lib/jvm/java-8-oracle/bin/keytool -genkeypair -alias server -keyalg RSA -keysize 2048 -keystore keystore.servidor

### Cliente (truststore)
/usr/lib/jvm/java-8-oracle/bin/keytool -genkeypair -alias maria -keyalg RSA -keysize 2048 -keystore truststore.cliente

---

## Exportar certificado do servidor

/usr/lib/jvm/java-8-oracle/bin/keytool -exportcert -alias server -keystore keystore.servidor -file server_cert.cer

## Importar certificado do servidor

/usr/lib/jvm/java-8-oracle/bin/keytool -importcert -alias server -keystore truststore.cliente -file server_cert.cer

## Print do certificado
/usr/lib/jvm/java-8-oracle/bin/keytool -printcert -file X509_certificate.cer

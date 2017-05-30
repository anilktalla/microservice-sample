Create the following Key in Consul:

service-discovery-routes

[
{
"host":"localhost",
"serviceName":"order-service"
}
]


Security:

Server:
Create a new key store:

keytool -genkey -alias mydomain -keyalg RSA -keystore keystore.jks -keysize 2048

 Self-Signed certificate for server:
1. Generate a private key for server 
openssl genrsa -out serverprivatekey.pem 2048
2.Create an openSSL self-signed certificate for the server using the private key
      openssl req -new -x509 -key serverprivatekey.pem -out servercert.pem -days 1095

3. Tomcat currently operates only on JKS format keystores. So generate a   keystore in JKS format from above certificate which involves creating a pkcs12 file
   openssl pkcs12 -export -out serverkeystore.pkcs12 -in servercert.pem -inkey serverprivatekey.pem    
     
      It asks for the export password, and it is recommended to provide a password.

4. Now convert serverkeystore.pkcs12 file to JKS format keystore using Java's keytool
 keytool -importkeystore -srckeystore serverkeystore.pkcs12 -srcstoretype PKCS12 -destkeystore keystore.jks -deststoretype JKS
       Keytool asks you for a new password(keystore password) for the JKS keystore twice, and it will also ask you for the password you set for the PKCS12 keystore created earlier.
             

-------------------------------------

Client:
Self-Signed certificate for client: 

1. Create a private key for client.
     openssl genrsa -out clientprivatekey.pem 2048


2. Create an openSSL self-signed certificate for the client using the private key
     openssl req -new -x509 -key clientprivatekey.pem -out clientcert.pem -days 365

     
     
     CURL
     
curl -E /Users/anilkumartalla/Documents/tmp/ssl-camel/client/clientcert.pem --key /Users/anilkumartalla/Documents/tmp/ssl-camel/client/clientprivatekey.pem https://localhost:8445/api/v1

invalid:
curl --insecure -E /Users/anilkumartalla/Documents/tmp/ssl-camel/invalidclient/clientcert.pem --key /Users/anilkumartalla/Documents/tmp/ssl-camel/invalidclient/clientprivatekey.pem https://localhost:8445/api/v1

Mac issues:
brew install curl --with-openssl
brew link curl --force
hash -r

https://github.com/boot2docker/boot2docker/issues/573#issuecomment-254436739
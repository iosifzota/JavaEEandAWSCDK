FROM payara/server-full

EXPOSE 8080
EXPOSE 4848

COPY mysql-connector-java-bin.jar /opt/payara/

RUN echo "AS_ADMIN_PASSWORD=admin" > /opt/payara/passFile

RUN echo 'add-library /opt/payara/mysql-connector-java-bin.jar' >> $POSTBOOT_COMMANDS

RUN echo 'create-jdbc-connection-pool --user admin --passwordfile /opt/payara/passFile --datasourceclassname com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource --restype javax.sql.ConnectionPoolDataSource --property user=admin:password=12345678:DatabaseName=testdb:ServerName=database-1.cqisgz3ko0a7.us-east-1.rds.amazonaws.com:port=3306:useSSL=false:allowPublicKeyRetrieval=true test-pool-005' >> $POSTBOOT_COMMANDS

RUN echo 'create-jdbc-resource --user admin --passwordfile /opt/payara/passFile --connectionpoolid test-pool-005 --enabled=true jdbc/myCliRes' >> $POSTBOOT_COMMANDS

COPY Lab4Ear001_with_RDS_db.ear $DEPLOY_DIR

ENV DEPLOY_PROPS=--contextroot=/
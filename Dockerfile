FROM payara/server-full

EXPOSE 8080

ADD --chown=payara:payara https://github.com/iosifzota/JavaEE/raw/master/Lab4Ear001_clean_nodb.ear $DEPLOY_DIR
RUN chmod 777 $DEPLOY_DIR/Lab4Ear001_clean_nodb.ear

ENV DEPLOY_PROPS=--contextroot=/
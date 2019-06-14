FROM hmcts/cnp-java-base:openjdk-8u191-jre-alpine3.9-2.0.1

COPY build/libs/bulk-scan-ccd-event-handler-sample-app.jar /opt/app/

HEALTHCHECK --interval=10s --timeout=10s --retries=10 CMD http_proxy="" wget -q --spider http://localhost:8484/health || exit 1

EXPOSE 8484
CMD [ "bulk-scan-ccd-event-handler-sample-app.jar" ]

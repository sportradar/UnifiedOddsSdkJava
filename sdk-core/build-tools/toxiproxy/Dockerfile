FROM ghcr.io/shopify/toxiproxy:2.7.0

COPY config.conf /app/config/proxies.json

EXPOSE 8093
ENTRYPOINT ["/toxiproxy"]
CMD ["-host=0.0.0.0", "-config=/app/config/proxies.json"]

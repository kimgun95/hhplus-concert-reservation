FROM redis:7.2.5-alpine3.20

EXPOSE 6379

CMD ["redis-server", "--maxmemory", "256mb", "--maxmemory-policy", "allkeys-lru"]
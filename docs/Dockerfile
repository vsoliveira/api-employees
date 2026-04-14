FROM nginx:1.27-alpine

COPY docs/nginx.conf /etc/nginx/conf.d/default.conf
COPY docs /usr/share/nginx/html

EXPOSE 80

HEALTHCHECK --interval=30s --timeout=5s --retries=3 CMD wget -qO- http://localhost/ >/dev/null || exit 1

# build stage
FROM node:lts-alpine as build-stage
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build


# production stage
FROM nginx:stable-alpine as production-stage
RUN apk update
RUN apk upgrade
RUN apk add bash
COPY --from=build-stage /app/dist /usr/share/nginx/html

COPY ./substituteEnv.sh /docker-entrypoint.d/substituteEnv.sh

# Execute the subsitution script and pass the path of the file to replace

#WORKDIR /usr/share/nginx/html/assets
#RUN grep -c 'localhost:8084' *.js
#RUN sed -i 's/localhost:8084/spring-boot-backend:8084/g' index.html
#RUN grep -c 'localhost:8084' index.html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]